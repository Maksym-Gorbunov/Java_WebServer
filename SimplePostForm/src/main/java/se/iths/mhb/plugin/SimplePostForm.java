package se.iths.mhb.plugin;


import se.iths.mhb.http.*;

import java.util.List;


public class SimplePostForm implements HttpService {


    @Address("/simplepostform")
    @RequestMethod
    public HttpResponse getMethod(HttpRequest httpRequest) {
        String dynamicDateHtmlPage = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"/>" +
                "    <link rel=\"stylesheet\" href=\"/w3.css\">" +
                "    <link rel=\"stylesheet\" href=\"/w3-colors-flat.css\">" +
                "    <title>Title</title>\n" +
                "</head>\n" +
                "<body style=\"color:#141414; background-color:snow; max-width:750px\">\n" +
                "<div class=\"w3-container\">" +
                "<form class=\"w3-container\" method=\"post\">\n" +
                "    First Name: <input class=\"w3-input w3-border\" type=\"text\" name=\"FirstName\" value=\"Mickey\"><br>\n" +
                "    Last name: <input class=\"w3-input w3-border\" type=\"text\" name=\"LastName\" value=\"Mouse\"><br>\n" +
                "    <input class=\"w3-button w3-flat-belize-hole\" type=\"submit\" value=\"Submit\">\n" +
                "</form>\n" +
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

    @Address("/simplepostform")
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
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"/>" +
                "    <link rel=\"stylesheet\" href=\"w3.css\">" +
                "    <title>Post Form</title>\n" +
                "</head>\n" +
                "<body style=\"color:#141414; background-color:snow; max-width:750px\">\n" +
                "<div class=\"w3-container\">" +
                "<p>" +
                (firstName.isPresent() ? firstName.get().getValue() : "No FirstName found") + "\n" +
                (lastName.isPresent() ? lastName.get().getValue() : "No FirstName found") + "\n" +
                "</p>" +
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
        return "SimplePostForm{}";
    }
}
