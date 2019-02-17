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
        pageHits.forEach((s, integer) -> stats.append(
                "       <tr class=\"item\">\n" +
                        "<td>" + s + "</td>\n" +
                        "<td>" + integer + "</td>\n" +
                        "</tr>\n"));


        String page = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"/>" +
                "    <link rel=\"stylesheet\" href=\"/w3.css\">" +
                "    <link rel=\"stylesheet\" href=\"w3-colors-flat.css\">" +
                "<script src=\"w3.js\"></script>" +
                "    <title>Server Stats</title>\n" +
                "</head>\n" +
                "<body style=\"color:#141414; background-color:snow; max-width:750px\">\n" +
                "<div class=\"w3-container\">" +
                "<H3>Server Stats</H3>\n" +
                "\n" +

                "<table class=\"w3-table w3-border w3-striped w3-hoverable\" id=\"statstable\">\n" +
                "<thead class=\"w3-flat-green-sea\">\n" +
                "<tr>\n" +
                "<th onclick=\"w3.sortHTML('#statstable','.item', 'td:nth-child(1)')\" style=\"cursor:pointer\">Page</th>\n" +
                "<th onclick=\"w3.sortHTML('#statstable','.item', 'td:nth-child(2)')\" style=\"cursor:pointer\">Hits</th>\n" +
                "</tr >\n" +
                "</thead>\n" +
                "<tbody>\n" +

                stats.toString() +

                "</tbody>\n" +
                "</table>\n" +
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
