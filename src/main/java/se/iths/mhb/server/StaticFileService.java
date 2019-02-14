package se.iths.mhb.server;

import se.iths.mhb.http.HttpRequest;
import se.iths.mhb.http.HttpResponse;
import se.iths.mhb.http.HttpService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.nio.file.StandardWatchEventKinds.*;
import static se.iths.mhb.http.HttpUtils.getContentType;
import static se.iths.mhb.server.Server.*;

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
        return response(200, fileRequested, httpRequest);

    }


    public static HttpResponse response(int code, String fileRequested, HttpRequest httpRequest) throws IOException {
        File file = new File(Server.WEB_ROOT, fileRequested);
        int fileLength = (int) file.length();
        String content = getContentType(fileRequested);
        byte[] body = readFileData(file, fileLength);

        return HttpResponse.newBuilder()
                .statusCode(code)
                .setHeader("Content-type", content)
                .setHeader("Content-length", "" + fileLength)
                .mapping(httpRequest.getMapping())
                .body(body)
                .build();

    }

    public static HttpResponse errorResponse(int code, HttpRequest httpRequest) throws IOException {
        String fileRequested = "";
        switch (code) {
            case 404:
                fileRequested = FILE_NOT_FOUND;
                break;
            case 501:
                fileRequested = METHOD_NOT_SUPPORTED;
                break;
        }
        return response(code, fileRequested, httpRequest);
    }

    public static byte[] readFileData(File file, int fileLength) throws IOException {
        byte[] fileData = new byte[fileLength];

        try (var fileIn = new FileInputStream(file)) {
            fileIn.read(fileData);
        }
        return fileData;
    }

//    public static String readFile(File file) throws IOException {
//        return Files.readString(file.toPath());
//    }

    private void loadAllStaticFiles() {
        File[] files = Server.WEB_ROOT.listFiles(File::isFile);
        List<String> strings = Arrays.stream(files).map(file -> "/" + file.getName().toLowerCase()).collect(Collectors.toList());
        server.setMappings(strings, this);
    }

    @Override
    public void run() {
        System.out.println("Init Static files");
        server.setMapping("/", this);
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
                System.out.println("Reloading static files");
                loadAllStaticFiles();
                for (WatchEvent<?> event : key.pollEvents()) {

                }
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
