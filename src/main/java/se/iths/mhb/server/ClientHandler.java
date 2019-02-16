package se.iths.mhb.server;

import se.iths.mhb.http.Http;
import se.iths.mhb.http.HttpRequest;
import se.iths.mhb.http.HttpResponse;
import se.iths.mhb.http.Parameter;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.function.Function;

import static se.iths.mhb.server.StaticFileService.errorResponse;

public class ClientHandler implements Runnable {


    private final Socket connect;
    private final Map<String, Map<Http.Method, Function<HttpRequest, HttpResponse>>> serviceMap;

    public ClientHandler(Socket connect, Map<String, Map<Http.Method, Function<HttpRequest, HttpResponse>>> serviceMap) {
        this.connect = connect;
        this.serviceMap = serviceMap;
    }

    @Override
    public void run() {

        try (var in = new BufferedReader(new InputStreamReader(connect.getInputStream()))) {
            HttpRequest httpRequest = parseInput(in);
            System.out.println(httpRequest.toString());
            HttpResponse httpResponse = doRequest(httpRequest);

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


        StringTokenizer parse = new StringTokenizer(input);
        String method = parse.nextToken().toUpperCase(); // we get the HTTP method of the client

        String address = parse.nextToken().toLowerCase();
        StringTokenizer addressTokeniser = new StringTokenizer(address, "?");
        String mapping = addressTokeniser.nextToken();

        List<Parameter> parameterList = new LinkedList<>();
        if (addressTokeniser.hasMoreTokens()) {
            String parameters = addressTokeniser.nextToken();
            // System.out.println(parameters);
            String[] split = parameters.split("&");
            Arrays.stream(split).forEach(s -> {
                if (s.contains("=")) {
                    String[] split1 = s.split("=");
                    parameterList.add(new Parameter(split1[0], split1[1]));
                }
            });

        }
        return HttpRequest.newBuilder()
                .method(Enum.valueOf(Http.Method.class, method))
                .mapping(mapping)
                .parameters(parameterList)
                .build();

    }

    private HttpResponse doRequest(HttpRequest httpRequest) {
        var methods = serviceMap.get(httpRequest.getMapping());
        if (methods == null)
            return errorResponse(404, httpRequest);

        var function = methods.get(httpRequest.getMethod());
        if (function == null)
            return errorResponse(501, httpRequest);

        return function.apply(httpRequest);

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