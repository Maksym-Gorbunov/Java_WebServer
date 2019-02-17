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
    private final String content;
    private final List<Parameter> contentParameters;

//    private void test() {
//        System.out.println("---->" + mapping);
//
//    }

//    String addQueryStringToUrlString(String url, final Map<Object, Object> parameters) throws UnsupportedEncodingException {
//        if (parameters == null) {
//            return url;
//        }
//
//        for (Map.Entry<Object, Object> parameter : parameters.entrySet()) {
//
//            final String encodedKey = URLEncoder.encode(parameter.getKey().toString(), "UTF-8");
//            final String encodedValue = URLEncoder.encode(parameter.getValue().toString(), "UTF-8");
//
//            if (!url.contains("?")) {
//                url += "?" + encodedKey + "=" + encodedValue;
//            } else {
//                url += "&" + encodedKey + "=" + encodedValue;
//            }
//        }
//
//        return url;
//    }

    private HttpRequest(Http.Method method, String mapping, Map<String, String> headers,
                        List<Parameter> parameters, String content, List<Parameter> contentParameters) {
        this.method = method;
        this.mapping = mapping;
        this.headers = new TreeMap<>(headers);
        this.parameters = new LinkedList<>(parameters);
        this.content = content;
        this.contentParameters = new LinkedList<>(contentParameters);
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

    public static class Builder {

        private Http.Method method = Http.Method.GET;
        private String mapping = "/";
        private Map<String, String> headers = new TreeMap<>();
        private List<Parameter> parameters = new LinkedList<>();
        private String content = "";
        private List<Parameter> contentParameters = new LinkedList<>();

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
            if (parameters != null)
                this.parameters.addAll(parameters);
            return this;
        }

        public Builder content(String content) {
            if (content != null)
                this.content = content;
            return this;
        }

        public Builder contentParameters(List<Parameter> contentParameters) {
            if (contentParameters != null)
                this.contentParameters.addAll(contentParameters);
            return this;
        }


        public HttpRequest build() {
            return new HttpRequest(this.method, this.mapping, this.headers, this.parameters, this.content, this.contentParameters);
        }
    }
}
