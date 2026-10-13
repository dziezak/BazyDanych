package pl.kornel.rest;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.net.URI;

public class Server {
    public static void main(String[] args) throws Exception {
        URI base = URI.create("http://localhost:8081/");
        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(
                base, new ResourceConfig(ModuleResource.class));
        System.out.println("REST działa na " + base + " (Ctrl+C kończy)");
        Thread.currentThread().join();
    }
}