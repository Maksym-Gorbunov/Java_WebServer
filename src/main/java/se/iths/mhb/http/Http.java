package se.iths.mhb.http;

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
}
