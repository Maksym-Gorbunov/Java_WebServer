package se.iths.mhb.http;

import java.util.Map;
import java.util.TreeMap;

public class HttpRequest {

    private final Method method;
    private final String mapping;
    private final Map<String, String> headers;
    private final Map<String, String> parameters;


    private HttpRequest(Method method, String mapping, Map<String, String> headers, Map<String, String> parameters) {
        this.method = method;
        this.mapping = mapping;
        this.headers = new TreeMap<>(headers);
        this.parameters = new TreeMap<>(parameters);
    }

    public Method getMethod() {
        return method;
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

    @Override
    public String toString() {
        return "HttpRequest{" +
                "method=" + method +
                ", mapping='" + mapping + '\'' +
                ", headers=" + headers +
                ", parameters=" + parameters +
                '}';
    }

    public static HttpRequest.Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {

        private Method method = Method.GET;
        private String mapping = "/";
        private Map<String, String> headers = new TreeMap<>();
        private Map<String, String> parameters = new TreeMap<>();

        private Builder() {
        }

        public Builder method(Method method) {
            this.method = method;
            return this;
        }

        public Builder mapping(String mapping) {
            this.mapping = mapping;
            return this;
        }

        public Builder setHeader(String key, String value) {
            this.headers.put(key, value);
            return this;
        }

        public Builder parameters(String parameters) {
            //todo
            //todo parse parameters into Map paramters
            return this;
        }


        public HttpRequest build() {
            return new HttpRequest(this.method, this.mapping, this.headers, this.parameters);
        }
    }
}
