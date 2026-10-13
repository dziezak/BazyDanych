package pl.kornel.rest;

import java.net.URI;
import java.net.http.*;

public class PostModuleClient {
    public static void main(String[] args) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/modules"))
                .header("Content-Type", "application/xml")
                .POST(HttpRequest.BodyPublishers.ofString("<module name=\"Data bases\" ECTS=\"4\"></module>"))
                .build();

        HttpResponse<String> response =
                HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 201)
            throw new RuntimeException("Failed to call. Error: " + response.statusCode());

        System.out.println("The location of new resource is: "
                + response.headers().firstValue("Location").orElse("(brak)"));
    }
}