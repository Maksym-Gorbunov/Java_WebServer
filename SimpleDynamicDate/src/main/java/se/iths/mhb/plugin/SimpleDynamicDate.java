package se.iths.mhb.plugin;

import se.iths.mhb.http.*;

import java.util.Date;

@Address("/date")
public class SimpleDynamicDate implements HttpService {

    @RequestMethod
    public HttpResponse getMethod(HttpRequest httpRequest) {
        String dynamicDateHtmlPage = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Date</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "Date: " + new Date().toString() +
                "\n" +
                "</body>\n" +
                "</html>";


        return HttpResponse.newBuilder()
                .statusCode(200)
                .setHeader("Content-type", "text/html")
                .mapping(httpRequest.getMapping())
                .body(dynamicDateHtmlPage.getBytes())
                .build();


    }


    @Override
    public String toString() {
        return "SimpleDynamicDate{}";
    }

}
