package se.iths.mhb.http;

public class HttpUtils {

    public static String getContentType(String fileRequested) {
        if (fileRequested.endsWith(".htm") || fileRequested.endsWith(".html"))
            return "text/html";


        if (fileRequested.endsWith(".css"))
            return "text/css";

        if (fileRequested.endsWith(".pdf"))
            return "application/pdf";
        if (fileRequested.endsWith(".js"))
            return "text/javascript";
        if (fileRequested.endsWith(".json"))
            return "application/json";
        if (fileRequested.endsWith(".ico"))
            return "image/vnd.microsoft.icon";
        else
            return "text/plain";

    }
}
