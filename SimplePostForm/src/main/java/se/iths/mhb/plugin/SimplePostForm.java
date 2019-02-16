package se.iths.mhb.plugin;


import se.iths.mhb.http.*;

import java.util.List;

@Address("/simplepostform")
public class SimplePostForm implements HttpService {


    @RequestMethod
    public HttpResponse getMethod(HttpRequest httpRequest) {
        String dynamicDateHtmlPage = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Title</title>\n" +
                "</head>\n" +
                "<body>\n" +
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
        List<Parameter> contentParameters = httpRequest.getContentParameters();
        var firstName = contentParameters.stream()
                .filter(parameter -> parameter.getKey().equals("FirstName"))
                .findFirst();
        var lastName = contentParameters.stream()
                .filter(parameter -> parameter.getKey().equals("LastName"))
                .findFirst();

        String dynamicDateHtmlPage = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Title</title>\n" +
                "</head>\n" +
                "<body>\n" +
                (firstName.isPresent() ? firstName.get().getValue() : "No FirstName found") + "\n" +
                (lastName.isPresent() ? lastName.get().getValue() : "No FirstName found") + "\n" +
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
        return "SimplePostForm{}";
    }
}
