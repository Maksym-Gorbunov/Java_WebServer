package se.iths.mhb.plugin;

import se.iths.mhb.http.*;

import java.util.HashMap;

@Address("/serverstats")
public class ServerStats implements HttpService {

    private final HashMap<String, Integer> pageHits = new HashMap<>();

    //todo add api which returns json

    @RequestMethod
    public HttpResponse getStatsHtmlPage(HttpRequest httpRequest) {
        StringBuilder stats = new StringBuilder();
        pageHits.forEach((s, integer) -> stats.append("<tr>\n<td>" + s + "</td>\n<td>" + integer + "</td>\n</tr>\n"));


        String page = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <link rel=\"stylesheet\" href=\"/w3.css\">" +
                "    <title>Server Stats</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<H3>Server Stats</H3>\n" +
                "\n" +

                "<table class=\"w3-table w3-border w3-striped w3-hoverable\">\n" +
                "<thead class=\"w3-flat-green-sea\">\n" +
                "<tr>\n" +
                "<th>Page</th>\n" +
                "<th>Hits</th>\n" +
                "</tr>\n" +
                "</thead>\n" +
                "<tbody>\n" +

                stats.toString() +

                "</tbody>\n" +
                "</table>\n" +


                "</body>\n" +
                "</html>";


        return HttpResponse.newBuilder()
                .statusCode(200)
                .setHeader("Content-type", "text/html")
                .mapping(httpRequest.getMapping())
                .body(page.getBytes())
                .build();
    }

    //todo Add store to database or store to file
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
