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
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"/>" +
                "    <link rel=\"stylesheet\" href=\"/w3.css\">" +
                "    <title>Date</title>\n" +
                "</head>\n" +
                "<body style=\"color:#141414; background-color:snow; max-width:750px\">\n" +
                "<div class=\"w3-container\">" +
                "<p>Date: " + new Date().toString() +
                "</p>\n" +
                "</div>" +
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
