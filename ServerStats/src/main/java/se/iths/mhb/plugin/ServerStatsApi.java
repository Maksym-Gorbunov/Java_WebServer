package se.iths.mhb.plugin;

import se.iths.mhb.http.*;

@Address("/serverstats/api/v1/")
public class ServerStatsApi implements HttpService {

    private final StatsService statsService;

    public ServerStatsApi() {
        this.statsService = StatsService.statsService;
    }

    @RequestMethod
    public HttpResponse getStats(HttpRequest httpRequest) {

        String jsonString = "{ address: www, method: GET, counter: 1}";

        return HttpResponse.newBuilder()
                .statusCode(200)
                .setHeader("Content-type", "application/json")
                .mapping(httpRequest.getMapping())
                .body(jsonString.getBytes())
                .build();
    }
}
