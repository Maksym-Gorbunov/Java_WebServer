package se.iths.mhb.server;

import se.iths.mhb.http.Http;
import se.iths.mhb.http.HttpRequest;
import se.iths.mhb.http.HttpResponse;
import se.iths.mhb.http.HttpService;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Map;
import java.util.StringTokenizer;

import static se.iths.mhb.server.StaticFileService.errorResponse;

public class ClientHandler implements Runnable {


    private final Socket connect;
    private final Map<String, HttpService> serviceMap;

    public ClientHandler(Socket connect, Map<String, HttpService> serviceMap) {
        this.connect = connect;
        this.serviceMap = serviceMap;
    }

    @Override
    public void run() {

        try (var in = new BufferedReader(new InputStreamReader(connect.getInputStream()))) {
            HttpRequest httpRequest = parseInput(in);
            System.out.println(httpRequest.toString());
            HttpResponse httpResponse = null;
            try {
                //if (httpRequest.getMethod() == Method.GET) {
                HttpService httpService = serviceMap.get(httpRequest.getMapping());
                httpResponse = (httpService == null) ? errorResponse(404, httpRequest) : httpService.serve(httpRequest);
                //} else {
                //    httpResponse = errorResponse(501, httpRequest);
                //}
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
        LinkedList<String> headers = new LinkedList<>();
        String headerLine = null;
        while ((headerLine = in.readLine()).length() != 0) {
            headers.addLast(headerLine);
            //System.out.println(headerLine);
        }

        StringBuilder payload = new StringBuilder();
        while (in.ready()) {
            payload.append((char) in.read());
        }
        //System.out.println("Payload data is: "+payload.toString());

        String input = headers.getFirst();

        //todo Ändra så att alla raderna läses in och parsas
        //todo headers osv

        StringTokenizer parse = new StringTokenizer(input);
        String method = parse.nextToken().toUpperCase(); // we get the HTTP method of the client

        String address = parse.nextToken().toLowerCase();
        StringTokenizer addressTokeniser = new StringTokenizer(address, "?");
        String mapping = addressTokeniser.nextToken();

        if (addressTokeniser.hasMoreTokens()) {
            String parameters = addressTokeniser.nextToken();

        }
        return HttpRequest.newBuilder()
                .method(Enum.valueOf(Http.Method.class, method))
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


}