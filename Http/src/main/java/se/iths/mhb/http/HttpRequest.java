package se.iths.mhb.http;

import java.util.*;

/**
 * Represents a Http request
 * Immutable
 * Use HttpRequest.newBuilder()to create new instances.
 */
public class HttpRequest {

    private final Http.Method method;
    private final String mapping;
    private final Map<String, String> headers;
    private final List<Parameter> parameters;
    private final String content;
    private final List<Parameter> contentParameters;

    private HttpRequest(Http.Method method, String mapping, Map<String, String> headers,
                        List<Parameter> parameters, String content, List<Parameter> contentParameters) {
        Objects.requireNonNull(method);
        Objects.requireNonNull(mapping);
        this.method = method;
        this.mapping = mapping;
        this.headers = (headers != null) ? Collections.unmodifiableMap(headers) : Collections.emptyMap();
        this.parameters = (parameters != null) ? Collections.unmodifiableList(parameters) : Collections.emptyList();
        this.content = (content != null) ? content : "";
        this.contentParameters = (contentParameters != null) ? Collections.unmodifiableList(contentParameters) : Collections.emptyList();
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

    public String getContent() {
        return content;
    }

    public List<Parameter> getContentParameters() {
        return contentParameters;
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "method=" + method +
                ", mapping='" + mapping + '\'' +
                ", headers=" + headers +
                ", parameters=" + parameters +
                ", content='" + content + '\'' +
                ", contentParameters=" + contentParameters +
                '}';
    }

    public static HttpRequest.Builder newBuilder() {
        return new Builder();
    }

    /**
     * Builder for se.iths.mhb.http request
     * Not Thread-safe, use sequential.
     */
    public static class Builder {

        private Http.Method method = Http.Method.GET;
        private String mapping;
        private Map<String, String> headers;
        private List<Parameter> parameters;
        private String content;
        private List<Parameter> contentParameters;

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
            if (this.headers == null)
                this.headers = new HashMap<>();
            this.headers.put(key, value);
            return this;
        }

        public Builder parameters(List<Parameter> parameters) {
            if (this.parameters == null)
                this.parameters = new LinkedList<>();
            this.parameters.addAll(parameters);
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder contentParameters(List<Parameter> contentParameters) {
            if (this.contentParameters == null)
                this.contentParameters = new LinkedList<>();
            this.contentParameters.addAll(contentParameters);
            return this;
        }


        public HttpRequest build() {
            return new HttpRequest(this.method, this.mapping, this.headers, this.parameters, this.content, this.contentParameters);
        }
    }
}
