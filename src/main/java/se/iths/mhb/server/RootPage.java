package se.iths.mhb.server;

import se.iths.mhb.http.HttpRequest;
import se.iths.mhb.http.HttpResponse;

public class RootPage {

    private final Server server;

    public RootPage(Server server) {
        this.server = server;
    }

    HttpResponse showAllMappingsPage(HttpRequest httpRequest) {

        StringBuilder pages = new StringBuilder();
        server.getMappings()
                .getServiceMap()
                .keySet()
                .forEach((page) -> pages.append(
                        // "<li>" +
                        "<a href=\"" + page + "\" class=\"w3-bar-item w3-border w3-button\">" + page + "</a>\n"
                        //                 "</li>\n"
                ));


        String page = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"/>" +
                "    <link rel=\"stylesheet\" href=\"w3.css\">" +
                "    <link rel=\"stylesheet\" href=\"w3-colors-flat.css\">" +
                "<script src=\"w3.js\"></script>" +
                "    <title>Web Server</title>\n" +
                "</head>\n" +
                "<body style=\"color:#141414; background-color:snow; max-width:750px\">\n" +
                "<div class=\"w3-container\">" +
                "<H3>All pages</H3>\n" +
                "\n" +
                "<div class=\"w3-bar-block w3-light-grey\">" +
                //"<ul class=\"w3-ul w3-border w3-hoverable\">\n" +

                pages.toString() +

                "</div>\n" +
                "</div>" +

                "</body>\n" +
                "</html>";


        return HttpResponse.newBuilder()
                .statusCode(200)
                .setHeader("Content-type", "text/html")
                .mapping(httpRequest.getMapping())
                .body(page.getBytes())
                .build();
    }

}
