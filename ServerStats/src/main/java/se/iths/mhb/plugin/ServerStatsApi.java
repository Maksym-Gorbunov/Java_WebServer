package se.iths.mhb.plugin;

import se.iths.mhb.http.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Address("/serverstats/api/v1/pagehits")
public class ServerStatsApi implements HttpService {

    private final StatsService statsService;

    public ServerStatsApi() {
        this.statsService = StatsService.statsService;
    }

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
                .map(p -> "{ \"address\": \"" + p.getAddress() + "\", \"method\": \"" + p.getMethod() + "\", \"counter\": \"" + p.getCounter() + "\" }"

                ).collect(Collectors.joining(", ")) + " ]";


        return HttpResponse.newBuilder()
                .statusCode(200)
                .setHeader("Content-type", "application/json")
                .mapping(httpRequest.getMapping())
                .body(jsonString.getBytes())
                .build();
    }
}
