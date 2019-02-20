package se.iths.mhb.http;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Util class for common Http cases.
 */
public final class Http {

    public enum Method {GET, HEAD, POST, PUT, DELETE}

    public static String getContentType(String fileSuffix) {
        switch (fileSuffix) {
            case ".htm":
            case ".html":
                return "text/html";
            case ".ico":
                return "image/vnd.microsoft.icon";
            case ".css":
                return "text/css";
            case ".js":
                return "text/javascript";
            case ".png":
                return "image/png";
            case ".jpg":
                return "image/jpeg";
            case ".pdf":
                return "application/pdf";
            case ".json":
                return "application/json";
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
