package pl.kornel.hdfs;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class CreateFolderClient {
    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:9870/webhdfs/v1/dane/java/1900?op=MKDIRS&user.name=root"))
                .header("Accept", "application/json")
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response.statusCode());
        System.out.println(response.body());
    }
}