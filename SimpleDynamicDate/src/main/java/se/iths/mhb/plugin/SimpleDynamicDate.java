package se.iths.mhb.plugin;

import se.iths.mhb.http.HttpRequest;
import se.iths.mhb.http.HttpResponse;
import se.iths.mhb.http.HttpService;

import java.io.IOException;
import java.util.Date;

public class SimpleDynamicDate implements HttpService {

    @Override
    public String defaultMapping() {
        return "/date";
    }

    @Override
    public HttpResponse serve(HttpRequest httpRequest) throws IOException {
        String dynamicDateHtmlPage = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Title</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "Date: " + new Date().toString() +
                "\n" +
                "</body>\n" +
                "</html>";


        return HttpResponse.newBuilder()
                .statusCode(200)
                .setHeader("Content-type", "text/html")
                .mapping(defaultMapping())
                .body(dynamicDateHtmlPage.getBytes())
                .build();


    }

    @Override
    public String toString() {
        return "SimpleDynamicDate{}";
    }
}
