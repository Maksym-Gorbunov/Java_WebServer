package se.iths.mhb.plugin;

import se.iths.mhb.http.Http;
import se.iths.mhb.http.HttpRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RuntimeStorage implements Storage {

    private final Map<String, Map<Http.Method, PageHit>> pageHits = new HashMap<>();

    @Override
    public synchronized void addHit(HttpRequest httpRequest) {
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

    @Override
    public synchronized List<PageHit> getHits() {
        return pageHits.values()
                .stream()
                .flatMap(methodPageHitMap -> methodPageHitMap.values().stream())
                .collect(Collectors.toList());
    }
}
