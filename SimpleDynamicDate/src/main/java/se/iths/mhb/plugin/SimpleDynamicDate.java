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
                "    <title>Title</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "Date: " + new Date().toString() +
                "\n" +
                "<form method=\"post\">\n" +
                "    First Name: <input type=\"text\" name=\"FirstName\" value=\"Mickey\"><br>\n" +
                "    Last name: <input type=\"text\" name=\"LastName\" value=\"Mouse\"><br>\n" +
                "    <input type=\"submit\" value=\"Submit\">\n" +
                "</form>\n" +
                "</body>\n" +
                "</html>";


        return HttpResponse.newBuilder()
                .statusCode(200)
                .setHeader("Content-type", "text/html")
                .mapping(httpRequest.getMapping())
                .body(dynamicDateHtmlPage.getBytes())
                .build();


    }

    @RequestMethod(Http.Method.POST)
    public HttpResponse postMethod(HttpRequest httpRequest) {
        System.out.println("RUNNING POST METHOD");
        return getMethod(httpRequest);
    }

    @Override
    public String toString() {
        return "SimpleDynamicDate{}";
    }

}
