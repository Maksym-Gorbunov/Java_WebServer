package se.iths.mhb.server;

import se.iths.mhb.http.Http;
import se.iths.mhb.http.HttpRequest;
import se.iths.mhb.http.HttpResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.nio.file.StandardWatchEventKinds.*;
import static se.iths.mhb.server.Server.*;

/**
 * The Runnable of this class will load new files from
 * a directory, even in runtime.
 * File name will be mapped as it's address.
 * Also some public static methods to serve files
 */
public class StaticFileService implements Runnable {

    private final Server server;

    public StaticFileService(Server server) {
        this.server = server;
    }

    public HttpResponse serve(HttpRequest httpRequest) {
        String fileRequested = httpRequest.getMapping();
        if (httpRequest.getMapping().endsWith("/")) {
            fileRequested += DEFAULT_FILE;
        } else {
            fileRequested = fileRequested.replaceFirst("/", "");
        }
        return response(200, fileRequested, httpRequest);
    }


    /**
     * Create http response for a file.
     *
     * @param code          status code
     * @param fileRequested file
     * @param httpRequest   http request
     * @return HttpResponse
     */
    public static HttpResponse response(int code, String fileRequested, HttpRequest httpRequest) {
        File file = new File(Server.WEB_ROOT, fileRequested);
        int fileLength = (int) file.length();
        String content = Http.getContentType(fileRequested.substring(fileRequested.lastIndexOf(".")));

        try {
            byte[] body = readFileData(file, fileLength);
            return HttpResponse.newBuilder()
                    .statusCode(code)
                    .setHeader("Content-type", content)
                    .setHeader("Content-length", "" + fileLength)
                    .mapping(httpRequest.getMapping())
                    .body(body)
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
            return errorResponse(404, httpRequest);
        }


    }

    /**
     * Create error http response.
     * Supporeted codes: 404, 501, 500.
     * @param code status code
     * @param httpRequest http request
     * @return HttpResponse
     */
    public static HttpResponse errorResponse(int code, HttpRequest httpRequest) {
        String fileRequested = "";
        switch (code) {
            case 404:
                fileRequested = FILE_NOT_FOUND;
                break;
            case 501:
                fileRequested = METHOD_NOT_SUPPORTED;
                break;
            case 500:
                fileRequested = "500.html";
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

    private void loadAllStaticFiles() {
        File[] files = Server.WEB_ROOT.listFiles(File::isFile);
        List<String> strings = Arrays.stream(files).map(file -> "/" + file.getName().toLowerCase()).collect(Collectors.toList());
        server.getAddressMapper().set(strings, this::serve);
        System.out.println("Mapped " + strings.size() + " files to addresses");
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
                                set("/" + context.toString(), staticFileService);
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
