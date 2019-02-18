package se.iths.mhb.http;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/**
 * Represents a Http response.
 * Immutable.
 * Use Httpresponse.newBuilder() to create new response
 */
public class HttpResponse {


    private final Http.Method method;
    private final String statusLine;
    private final String mapping;
    private final Map<String, String> headers;
    private final Map<String, String> parameters;
    private final byte[] body;

    private HttpResponse(Http.Method method, String statusLine, String mapping, Map<String, String> headers, Map<String, String> parameters, byte[] body) {
        this.method = method;
        this.statusLine = statusLine;
        this.mapping = mapping;
        this.headers = new TreeMap<>(headers);
        this.parameters = new TreeMap<>(parameters);
        this.body = body;
    }

    public Http.Method getMethod() {
        return method;
    }

    public String getStatusLine() {
        return statusLine;
    }

    public String getMapping() {
        return mapping;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public byte[] getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "HttpResponse{" +
                "method=" + method +
                ", statusLine='" + statusLine + '\'' +
                ", mapping='" + mapping + '\'' +
                ", headers=" + headers +
                ", parameters=" + parameters +
                ", body.length=" + body.length +
                '}';
    }

    public static HttpResponse.Builder newBuilder() {
        return new HttpResponse.Builder();
    }

    /**
     * Builder for http response
     */
    public static class Builder {

        private Http.Method method = Http.Method.GET;
        private String statusLine = "Http/1.1";
        private String mapping = "/";
        private Map<String, String> headers = new TreeMap<>();
        private Map<String, String> parameters = new TreeMap<>();
        private byte[] body = new byte[0];

        private Builder() {
            headers.put("Server", "MHB Web Server");
            headers.put("Date", new Date().toString());
        }

        public HttpResponse.Builder method(Http.Method method) {
            this.method = method;
            return this;
        }

        public HttpResponse.Builder statusCode(int statusCode) {
            switch (statusCode) {
                case 404:
                    this.statusLine += " 404 Not Found";
                    break;
                case 501:
                    this.statusLine += " 501 Not Implemented";
                    break;
                case 500:
                    this.statusLine += " 500 Internal Server Error";
                    break;
            }
            return this;
        }

        public HttpResponse.Builder mapping(String mapping) {
            this.mapping = mapping;
            return this;
        }

        public HttpResponse.Builder setHeader(String key, String value) {
            this.headers.put(key, value);
            return this;
        }

        public HttpResponse.Builder parameters(String parameters) {
            //todo
            //todo parse parameters into Map paramters
            return this;
        }

        public HttpResponse.Builder body(byte[] body) {
            this.body = body;
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(this.method, this.statusLine, this.mapping, this.headers, this.parameters, this.body);
        }
    }
}
