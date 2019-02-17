package se.iths.mhb.plugin;

import se.iths.mhb.http.*;

@Address("/serverstats")
public class ServerStats implements HttpService {

    @RequestMethod
    public HttpResponse getStatsHtmlPage(HttpRequest httpRequest) {
        String dynamicDateHtmlPage = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Server Stats</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<H3>Server Stats</H3>\n" +
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

    @ReadRequest
    public void collectStats(HttpRequest httpRequest) {

    }

}
