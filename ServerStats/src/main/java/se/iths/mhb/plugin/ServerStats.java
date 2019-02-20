package se.iths.mhb.plugin;

import se.iths.mhb.http.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class ServerStats implements HttpService {

    private final StatsService statsService;

    public ServerStats() {
        this.statsService = new StatsService();
    }

    //FIXME: the javascript sorting is alphabetic atm

    @Address("/serverstats")
    @RequestMethod
    public HttpResponse getStatsHtmlPage(HttpRequest httpRequest) {
        StringBuilder stats = new StringBuilder();
        statsService.getRuntimeStorage().getHits().stream()
                .sorted()
                .forEach((pageHit) -> stats.append(
                        "<tr class=\"item\">\n" +
                                "<td><a href=\"" + pageHit.getAddress() + "\">" + pageHit.getAddress() + "</a></td>\n" +
                                "<td>" + pageHit.getMethod() + "</td>\n" +
                                "<td>" + pageHit.getCounter() + "</td>\n" +
                                "</tr>\n"));

        StringBuilder sqlStats = new StringBuilder();
        statsService.getSqlLiteStorage().getHits()
                .forEach((pageHit) -> sqlStats.append(
                        "<tr class=\"item\">\n" +
                                "<td><a href=\"" + pageHit.getAddress() + "\">" + pageHit.getAddress() + "</a></td>\n" +
                                "<td>" + pageHit.getMethod() + "</td>\n" +
                                "<td>" + pageHit.getCounter() + "</td>\n" +
                                "</tr>\n"));


        String page = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"/>" +
                "    <link rel=\"stylesheet\" href=\"/w3.css\">" +
                "    <link rel=\"stylesheet\" href=\"/w3-colors-flat.css\">" +
                "    <script src=\"/w3.js\"></script>" +
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
                "\n" +
                "<H4>Lifetime Requests</H4>\n" +
                "\n" +

                "<table class=\"w3-table w3-border w3-striped w3-hoverable\" id=\"statstablesql\">\n" +
                "<thead class=\"w3-flat-green-sea\">\n" +
                "<tr>\n" +
                "<th onclick=\"w3.sortHTML('#statstablesql','.item', 'td:nth-child(1)')\" style=\"cursor:pointer\">Page</th>\n" +
                "<th onclick=\"w3.sortHTML('#statstablesql','.item', 'td:nth-child(2)')\" style=\"cursor:pointer\">Method</th>\n" +
                "<th onclick=\"w3.sortHTML('#statstablesql','.item', 'td:nth-child(3)')\" style=\"cursor:pointer\">Hits</th>\n" +
                "</tr >\n" +
                "</thead>\n" +
                "<tbody>\n" +

                sqlStats.toString() +

                "</tbody>\n" +
                "</table>\n" +
                "\n" +
                "\n" +

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

    @ReadRequest
    public void collectStats(HttpRequest httpRequest) {
        statsService.add(httpRequest);

    }

    @Address("/serverstats/api/v1/pagehits")
    @RequestMethod
    public HttpResponse getStats(HttpRequest httpRequest) {

        List<Parameter> parameters = httpRequest.getParameters();
        Optional<Parameter> method = parameters.stream().filter(p -> p.getKey().equals("method")).findAny();

        List<PageHit> hits = statsService.getSqlLiteStorage().getHits();

        String jsonString = "[ " + hits.stream()
                .filter(p -> {
                    if (method.isPresent())
                        return p.getMethod().toString().equalsIgnoreCase(method.get().getValue());
                    return true;
                })
                .map(p -> "\n   {\n      \"address\": \"" + p.getAddress() + "\",\n      \"method\": \"" + p.getMethod() + "\",\n      \"counter\": \"" + p.getCounter() + "\"\n   }"

                ).collect(Collectors.joining(", ")) + " \n]";


        return HttpResponse.newBuilder()
                .statusCode(200)
                .setHeader("Content-type", "application/json")
                .mapping(httpRequest.getMapping())
                .body(jsonString.getBytes())
                .build();
    }

}
