package se.iths.mhb.http;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class HttpRequest {

    private final Http.Method method;
    private final String mapping;
    private final Map<String, String> headers;
    private final List<Parameter> parameters;


    private HttpRequest(Http.Method method, String mapping, Map<String, String> headers, List<Parameter> parameters) {
        this.method = method;
        this.mapping = mapping;
        this.headers = new TreeMap<>(headers);
        this.parameters = new LinkedList<>(parameters);
    }

    public Http.Method getMethod() {
        return method;
    }

    public String getMapping() {
        return mapping;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public List<Parameter> getParameters() {
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

        private Http.Method method = Http.Method.GET;
        private String mapping = "/";
        private Map<String, String> headers = new TreeMap<>();
        private List<Parameter> parameters = new LinkedList<>();

        private Builder() {
        }

        public Builder method(Http.Method method) {
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

        public Builder parameters(List<Parameter> parameters) {
            this.parameters.addAll(parameters);
            return this;
        }


        public HttpRequest build() {
            return new HttpRequest(this.method, this.mapping, this.headers, this.parameters);
        }
    }
}
