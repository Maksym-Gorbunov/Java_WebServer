package se.iths.mhb.server;

import se.iths.mhb.http.HttpRequest;
import se.iths.mhb.http.HttpResponse;
import se.iths.mhb.http.HttpService;

import java.io.IOException;

import static se.iths.mhb.server.ClientHandler.DEFAULT_FILE;

public class StaticFileService implements HttpService {

    private final Server server;

    public StaticFileService(Server server) {
        this.server = server;
        System.out.println(server);
    }

    @Override
    public HttpResponse serve(HttpRequest httpRequest) throws IOException {
        String fileRequested = httpRequest.getMapping();
        if (httpRequest.getMapping().endsWith("/")) {
            fileRequested += DEFAULT_FILE;
        }

        return ClientHandler.response(200, fileRequested, httpRequest);

    }
}
