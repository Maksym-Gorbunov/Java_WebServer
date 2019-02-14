package se.iths.mhb.server;

import se.iths.mhb.http.HttpRequest;
import se.iths.mhb.http.HttpResponse;
import se.iths.mhb.http.HttpService;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.nio.file.StandardWatchEventKinds.*;
import static se.iths.mhb.server.Server.DEFAULT_FILE;

public class StaticFileService implements HttpService, Runnable {

    private final Server server;

    public StaticFileService(Server server) {
        this.server = server;
    }

    @Override
    public String defaultMapping() {
        return "/";
    }

    @Override
    public HttpResponse serve(HttpRequest httpRequest) throws IOException {
        String fileRequested = httpRequest.getMapping();
        if (httpRequest.getMapping().endsWith("/")) {
            fileRequested += DEFAULT_FILE;
        } else {
            fileRequested = fileRequested.replaceFirst("/", "");
        }
        System.out.println(fileRequested);
        return ClientHandler.response(200, fileRequested, httpRequest);

    }

    private void loadAllStaticFiles() {
        File[] files = Server.WEB_ROOT.listFiles(File::isFile);
        List<String> strings = Arrays.stream(files).map(file -> "/" + file.getName().toLowerCase()).collect(Collectors.toList());
        server.setMappings(strings, this);
    }

    @Override
    public void run() {
        loadAllStaticFiles();

        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();
            Path path = Server.WEB_ROOT.toPath();
            path.register(
                    watchService,
                    ENTRY_CREATE,
                    ENTRY_DELETE,
                    ENTRY_MODIFY);

            WatchKey key;
            while ((key = watchService.take()) != null) {
                loadAllStaticFiles();
                key.reset();
                //todo maybe change to one file at a time
                /*
                for (WatchEvent<?> event : key.pollEvents()) {
                        if (event.kind() == ENTRY_CREATE) {
                            Object context = event.context();
                            if (context instanceof Path) {
                                setMapping("/" + context.toString(), staticFileService);
                            }
                        } else if (event.kind() == ENTRY_DELETE) {

                        }
                        System.out.println("Event kind:" + event.kind() + ". File affected: " + event.context() + ".");
                    }*/

            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
