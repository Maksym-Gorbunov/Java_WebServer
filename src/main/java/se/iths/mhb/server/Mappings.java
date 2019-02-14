package se.iths.mhb.server;

import se.iths.mhb.http.HttpService;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class Mappings {
    private final Map<String, HttpService> ServiceMap;

    public Mappings() {
        this.ServiceMap = Collections.unmodifiableMap(new HashMap<>());
    }

    public Mappings(Map<String, HttpService> ServiceMap) {
        this.ServiceMap = Collections.unmodifiableMap(ServiceMap);
    }

    public Mappings addServices(Map<String, HttpService> serviceMap) {
        HashMap<String, HttpService> hashMap = new HashMap<>(this.ServiceMap);
        hashMap.putAll(serviceMap);
        return new Mappings(hashMap);
    }

    public final Mappings addService(String mapping, HttpService httpService) {
        HashMap<String, HttpService> hashMap = new HashMap<>(this.ServiceMap);
        hashMap.put(mapping, httpService);
        return new Mappings(hashMap);
    }

    public final Map<String, HttpService> getServiceMap() {
        return ServiceMap;
    }
}
