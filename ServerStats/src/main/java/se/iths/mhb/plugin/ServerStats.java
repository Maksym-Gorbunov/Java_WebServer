package se.iths.mhb.plugin;

import se.iths.mhb.http.*;

import java.util.HashMap;

@Address("/serverstats")
public class ServerStats implements HttpService {

    private final HashMap<String, Integer> pageHits = new HashMap<>();

    @RequestMethod
    public HttpResponse getStatsHtmlPage(HttpRequest httpRequest) {
        StringBuilder stats = new StringBuilder("<ul>");
        pageHits.forEach((s, integer) -> stats.append("<li>" + s + " " + integer + "</li>\n"));
        stats.append("</ul>\n");

        String page = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Server Stats</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<H3>Server Stats</H3>\n" +
                "\n" +
                stats.toString() +
                "</body>\n" +
                "</html>";


        return HttpResponse.newBuilder()
                .statusCode(200)
                .setHeader("Content-type", "text/html")
                .mapping(httpRequest.getMapping())
                .body(page.getBytes())
                .build();
    }

    @ReadRequest
    public void collectStats(HttpRequest httpRequest) {
        addPageHit(httpRequest.getMapping());
    }

    private synchronized void addPageHit(String address) {
        Integer hits = pageHits.get(address);
        if (hits == null) {
            pageHits.put(address, 1);
        } else {
            pageHits.put(address, ++hits);
        }
    }

}
