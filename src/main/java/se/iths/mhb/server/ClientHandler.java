package se.iths.mhb.server;

import se.iths.mhb.http.HttpRequest;
import se.iths.mhb.http.HttpResponse;
import se.iths.mhb.http.HttpService;
import se.iths.mhb.http.Method;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Map;
import java.util.StringTokenizer;

import static se.iths.mhb.server.Server.FILE_NOT_FOUND;
import static se.iths.mhb.server.Server.METHOD_NOT_SUPPORTED;

public class ClientHandler implements Runnable {


    private final Socket connect;
    private final Map<String, HttpService> plugins;

    public ClientHandler(Socket connect, Map<String, HttpService> plugins) {
        this.connect = connect;
        this.plugins = plugins;
    }

    @Override
    public void run() {

        try (var in = new BufferedReader(new InputStreamReader(connect.getInputStream()))) {
            HttpRequest httpRequest = parseInput(in);
            System.out.println(httpRequest.toString());
            HttpResponse httpResponse = null;
            try {
                if (httpRequest.getMethod() == Method.GET) {
                    HttpService httpService = plugins.get(httpRequest.getMapping());
                    if (httpService != null)
                        httpResponse = httpService.serve(httpRequest);
                } else {
                    httpResponse = errorResponse(501, httpRequest);
                }
            } catch (FileNotFoundException e) {
                System.out.println("Error with file not found");
                httpResponse = errorResponse(404, httpRequest);
            }

            try (var out = new PrintWriter(connect.getOutputStream()); var dataOut = new BufferedOutputStream(connect.getOutputStream());) {
                System.out.println(httpResponse.toString());
                send(out, dataOut, httpResponse);
            } catch (IOException e) {
                System.out.println("Error with send() out dataOut");
                e.printStackTrace();
            }

        } catch (IOException e) {
            System.out.println("Error with in");
            e.printStackTrace();
        } finally {
            try {
                connect.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Connection closed.\n");
        }

    }

    private HttpRequest parseInput(BufferedReader in) throws IOException {

        String input = in.readLine();
        //todo Ändra så att alla raderna läses in och parsas
        //todo headers osv
        System.out.println("input [ " + input + " ]");

        StringTokenizer parse = new StringTokenizer(input);
        String method = parse.nextToken().toUpperCase(); // we get the HTTP method of the client

        String address = parse.nextToken().toLowerCase();
        StringTokenizer addressTokeniser = new StringTokenizer(address, "?");
        String mapping = addressTokeniser.nextToken();

        if (addressTokeniser.hasMoreTokens()) {
            String parameters = addressTokeniser.nextToken();

        }
        return HttpRequest.newBuilder()
                .method(Enum.valueOf(Method.class, method))
                .mapping(mapping)
                .build();

    }

    private void send(PrintWriter out, BufferedOutputStream dataOut, HttpResponse httpResponse) throws IOException {
        out.println(httpResponse.getStatusLine());

        httpResponse.getHeaders().forEach((key, value) -> out.println(key + ": " + value));
        out.println();
        out.flush();

        dataOut.write(httpResponse.getBody(), 0, httpResponse.getBody().length);
        dataOut.flush();
    }

    public static String readFile(File file) throws IOException {
        return Files.readString(file.toPath());
    }

    public static byte[] readFileData(File file, int fileLength) throws IOException {
        byte[] fileData = new byte[fileLength];

        try (var fileIn = new FileInputStream(file)) {
            fileIn.read(fileData);
        }
        return fileData;
    }

    // return supported MIME Types
    public static String getContentType(String fileRequested) {
        if (fileRequested.endsWith(".htm") || fileRequested.endsWith(".html"))
            return "text/html";


        if (fileRequested.endsWith(".css"))
            return "text/css";

        if (fileRequested.endsWith(".pdf"))
            return "application/pdf";
        if (fileRequested.endsWith(".js"))
            return "text/javascript";
        if (fileRequested.endsWith(".json"))
            return "application/json";
        if (fileRequested.endsWith(".ico"))
            return "image/vnd.microsoft.icon";
        else
            return "text/plain";

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

}