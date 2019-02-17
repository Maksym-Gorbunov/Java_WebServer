package se.iths.mhb.http;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class Http {

    public enum Method {GET, HEAD, POST, PUT, DELETE}

    public static String getContentType(String fileSuffix) {
        switch (fileSuffix) {
            case ".htm":
            case ".html":
                return "text/html";
            case ".css":
                return "text/css";
            case ".pdf":
                return "application/pdf";
            case ".js":
                return "text/javascript";
            case ".json":
                return "application/json";
            case ".ico":
                return "image/vnd.microsoft.icon";
            default:
                return "text/plain";
        }
    }

    public static List<Parameter> parseParameters(String parameters) {
        Objects.requireNonNull(parameters);
        return Arrays.stream(parameters.split("&"))
                .map(String::trim)
                .filter(pair -> pair.contains("="))
                .map(Parameter::new)
                .collect(Collectors.toList());
    }
}
