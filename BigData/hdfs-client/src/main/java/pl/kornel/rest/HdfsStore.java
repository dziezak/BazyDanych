package pl.kornel.rest;

import java.net.URI;
import java.net.http.*;

public class HdfsStore {
    private static final String BASE = "http://localhost:9870/webhdfs/v1/dane/modules";
    private static final HttpClient HTTP = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NEVER).build();

    public static void write(int id, String xml) throws Exception {
        // krok 1: NameNode wskazuje DataNode
        String url = BASE + "/" + id + ".xml?op=CREATE&overwrite=true&user.name=root";
        HttpResponse<String> r1 = HTTP.send(
                HttpRequest.newBuilder(URI.create(url))
                        .PUT(HttpRequest.BodyPublishers.noBody()).build(),
                HttpResponse.BodyHandlers.ofString());
        if (r1.statusCode() != 307)
            throw new RuntimeException("CREATE krok 1: " + r1.statusCode() + " " + r1.body());

        // krok 2: dane idą pod adres z Location
        String location = r1.headers().firstValue("Location").orElseThrow();
        HttpResponse<String> r2 = HTTP.send(
                HttpRequest.newBuilder(URI.create(location))
                        .PUT(HttpRequest.BodyPublishers.ofString(xml)).build(),
                HttpResponse.BodyHandlers.ofString());
        if (r2.statusCode() != 201)
            throw new RuntimeException("CREATE krok 2: " + r2.statusCode() + " " + r2.body());
    }

    public static void delete(int id) throws Exception {
        String url = BASE + "/" + id + ".xml?op=DELETE&user.name=root";
        HTTP.send(HttpRequest.newBuilder(URI.create(url)).DELETE().build(),
                HttpResponse.BodyHandlers.ofString());
    }
}