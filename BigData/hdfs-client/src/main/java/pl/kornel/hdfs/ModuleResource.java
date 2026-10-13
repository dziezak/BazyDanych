package pl.kornel.rest;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Path("/modules")
public class ModuleResource {

    private record Module(int id, String name, int ects) {
        String toXml() {
            return "<module id=\"" + id + "\" name=\"" + name.replace("&", "&amp;")
                    .replace("\"", "&quot;").replace("<", "&lt;") + "\" ECTS=\"" + ects + "\"/>";
        }
    }

    // zamiast bazy danych + JPA
    private static final Map<Integer, Module> STORE = new ConcurrentHashMap<>();
    private static final AtomicInteger IDS = new AtomicInteger(900);

    private static Module parse(int id, InputStream is) throws Exception {
        var dbf = DocumentBuilderFactory.newInstance();
        dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true); // ochrona przed XXE
        Element el = dbf.newDocumentBuilder().parse(is).getDocumentElement();
        return new Module(id, el.getAttribute("name"), Integer.parseInt(el.getAttribute("ECTS")));
    }

    // POST /modules : tworzy zasób, id nadaje serwer
    @POST
    @Consumes(MediaType.APPLICATION_XML)
    public Response create(InputStream is, @Context UriInfo uriInfo) throws Exception {
        Module m = parse(IDS.incrementAndGet(), is);
        STORE.put(m.id(), m);
        URI location = uriInfo.getAbsolutePathBuilder().path(String.valueOf(m.id())).build();
        return Response.created(location).build();          // 201 + Location
    }

    // GET /modules : lista
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public String list() {
        var sb = new StringBuilder("<modules>");
        STORE.values().forEach(m -> sb.append(m.toXml()));
        return sb.append("</modules>").toString();
    }

    // GET /modules/{id} : negocjacja treści, XML albo tekst
    @GET @Path("{id: [0-9]+}")
    @Produces(MediaType.APPLICATION_XML)
    public String getXml(@PathParam("id") int id) {
        return find(id).toXml();
    }

    @GET @Path("{id: [0-9]+}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getText(@PathParam("id") int id) {
        Module m = find(id);
        return m.name() + " (" + m.ects() + " ECTS)";
    }

    // PUT /modules/{id} : tworzy lub nadpisuje, idempotentne
    @PUT @Path("{id: [0-9]+}")
    @Consumes(MediaType.APPLICATION_XML)
    public Response put(@PathParam("id") int id, InputStream is) throws Exception {
        boolean existed = STORE.containsKey(id);
        STORE.put(id, parse(id, is));
        return existed ? Response.noContent().build() : Response.status(201).build();
    }

    // DELETE /modules/{id}
    @DELETE @Path("{id: [0-9]+}")
    public Response delete(@PathParam("id") int id) {
        return STORE.remove(id) != null ? Response.noContent().build()
                                        : Response.status(404).build();
    }

    private static Module find(int id) {
        Module m = STORE.get(id);
        if (m == null) throw new NotFoundException();       // 404
        return m;
    }
}