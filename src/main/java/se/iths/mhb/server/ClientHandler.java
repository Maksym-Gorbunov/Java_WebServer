package se.iths.mhb.server;

import se.iths.mhb.http.Http;
import se.iths.mhb.http.HttpRequest;
import se.iths.mhb.http.HttpResponse;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

import static se.iths.mhb.server.StaticFileService.errorResponse;


/**
 * Instance of this class is created in a new thread for each request to the server.
 * It will parse and create A HttpRequest object.
 * Handle the request and create a response.
 * Send the response.
 */
public class ClientHandler implements Runnable {


    private final Socket connect;
    private final Map<String, Map<Http.Method, Function<HttpRequest, HttpResponse>>> serviceMap;
    private final List<Consumer<HttpRequest>> requestConsumers;

    public ClientHandler(Socket connect, Map<String, Map<Http.Method,
            Function<HttpRequest, HttpResponse>>> serviceMap,
                         List<Consumer<HttpRequest>> requestConsumers) {
        this.connect = connect;
        this.serviceMap = serviceMap;
        this.requestConsumers = requestConsumers;
    }

    @Override
    public void run() {
        System.out.println("Connection opened. (" + new Date() + ")");
        try (var in = new BufferedReader(new InputStreamReader(connect.getInputStream()))) {
            HttpRequest httpRequest = parseInput(in);
            System.out.println(httpRequest.toString());

            consumeRequest(httpRequest);
            HttpResponse httpResponse = handleRequest(httpRequest);

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
        HttpRequest.Builder builder = HttpRequest.newBuilder();

        LinkedList<String> headers = new LinkedList<>();
        String headerLine = null;
        while ((headerLine = in.readLine()).length() != 0) {
            headers.addLast(headerLine);
            System.out.println("[Header] " + headerLine);
        }

        StringBuilder payload = new StringBuilder();
        while (in.ready()) {
            payload.append((char) in.read());
        }

        if (payload.toString().length() > 0) {
            String content = payload.toString();
            System.out.println("[Payload] " + content);
            builder = builder.content(content);
            builder = builder.contentParameters(Http.parseParameters(content));
        }

        String input = headers.getFirst();
        StringTokenizer parse = new StringTokenizer(input);
        builder = builder.method(Enum.valueOf(Http.Method.class, parse.nextToken().toUpperCase()));

        String address = parse.nextToken().toLowerCase();
        StringTokenizer addressTokeniser = new StringTokenizer(address, "?");
        builder = builder.mapping(addressTokeniser.nextToken());

        if (addressTokeniser.hasMoreTokens()) {
            builder = builder.parameters(Http.parseParameters(addressTokeniser.nextToken()));
        }

        return builder.build();
    }

//    // Create parameters
//    public static List<Parameter> splitQuery(String address) throws UnsupportedEncodingException, MalformedURLException {
//        URL url = new URL("http://localhost/" + address);
//        List<Parameter> params = new LinkedList<>();
//        String query = url.getQuery();
//        if (query != null) {
//            String[] pairs = query.split("&");
//            for (String pair : pairs) {
//                int idx = pair.indexOf("=");
//                params.add(new Parameter(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8")));
//            }
//        }
//        return params;
//    }

    /*
    private String addQueryStringToUrlString(String url, List<Parameter> parameters) throws UnsupportedEncodingException {
        System.out.println("************************"+url);
        if (parameters == null) {
            return url;
        }
        for (Parameter parameter : parameters) {
            final String encodedKey = URLEncoder.encode(parameter.getKey().toString(), "UTF-8");
            final String encodedValue = URLEncoder.encode(parameter.getValue().toString(), "UTF-8");
            if (!url.contains("?")) {
                url += "?" + encodedKey + "=" + encodedValue;
            } else {
                url += "&" + encodedKey + "=" + encodedValue;
            }
            parameters.add(new Parameter(encodedKey, encodedValue));
            System.out.println("****************************");
            System.out.println(encodedKey+": "+encodedValue);
            System.out.println("****************************");

        }

        return url;
    }
*/

    private void consumeRequest(HttpRequest httpRequest) {
        requestConsumers.forEach(consumer -> consumer.accept(httpRequest));
    }

    private HttpResponse handleRequest(HttpRequest httpRequest) {
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

        //FIXME This ok? or hack?
        if (httpResponse.getMethod() != Http.Method.HEAD) {
            dataOut.write(httpResponse.getBody(), 0, httpResponse.getBody().length);
            dataOut.flush();
        }
    }

}