package se.iths.mhb.plugin;

import se.iths.mhb.http.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Address("/serverstats")
public class ServerStats implements HttpService {

    private final Map<String, Map<Http.Method, PageHit>> pageHits = new HashMap<>();

    //todo add api which returns json

    //todo Fix: the javascript sorting is alphabetic atm

    @RequestMethod
    public HttpResponse getStatsHtmlPage(HttpRequest httpRequest) {
        StringBuilder stats = new StringBuilder();
        getPageHits().stream()
                .sorted()
                .forEach((pageHit) -> stats.append(
                        "       <tr class=\"item\">\n" +
                                "<td><a href=\"" + pageHit.getAddress() + "\">" + pageHit.getAddress() + "</a></td>\n" +
                                "<td>" + pageHit.getMethod() + "</td>\n" +
                                "<td>" + pageHit.getCounter() + "</td>\n" +
                                "</tr>\n"));


        String page = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"/>" +
                "    <link rel=\"stylesheet\" href=\"w3.css\">" +
                "    <link rel=\"stylesheet\" href=\"w3-colors-flat.css\">" +
                "<script src=\"w3.js\"></script>" +
                "    <title>Server Stats</title>\n" +
                "</head>\n" +
                "<body style=\"color:#141414; background-color:snow; max-width:750px\">\n" +
                "<div class=\"w3-container\">" +
                "<H3>Server Stats</H3>\n" +
                "<H4>Runtime Requests</H4>\n" +
                "\n" +

                "<table class=\"w3-table w3-border w3-striped w3-hoverable\" id=\"statstable\">\n" +
                "<thead class=\"w3-flat-green-sea\">\n" +
                "<tr>\n" +
                "<th onclick=\"w3.sortHTML('#statstable','.item', 'td:nth-child(1)')\" style=\"cursor:pointer\">Page</th>\n" +
                "<th onclick=\"w3.sortHTML('#statstable','.item', 'td:nth-child(2)')\" style=\"cursor:pointer\">Method</th>\n" +
                "<th onclick=\"w3.sortHTML('#statstable','.item', 'td:nth-child(3)')\" style=\"cursor:pointer\">Hits</th>\n" +
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
        addPageHit(httpRequest);
    }

    private synchronized List<PageHit> getPageHits() {
        return pageHits.values()
                .stream()
                .flatMap(methodPageHitMap -> methodPageHitMap.values().stream())
                .collect(Collectors.toList());
    }

    private synchronized void addPageHit(HttpRequest httpRequest) {
        String address = httpRequest.getMapping();
        Http.Method method = httpRequest.getMethod();
        var map = pageHits.get(address);
        if (map == null) {
            HashMap<Http.Method, PageHit> methodPageHitHashMap = new HashMap<>();
            methodPageHitHashMap.put(method, new PageHit(address, method, 1));
            pageHits.put(address, methodPageHitHashMap);
        } else {
            var pageHit = map.get(method);
            if (pageHit == null) {
                map.put(method, new PageHit(address, method, 1));
            } else {
                map.put(method, pageHit.increment());
            }
        }

    }

}
