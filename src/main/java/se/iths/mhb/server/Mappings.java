package se.iths.mhb.server;

import se.iths.mhb.http.Http;
import se.iths.mhb.http.HttpRequest;
import se.iths.mhb.http.HttpResponse;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class Mappings {
    private final Map<String, Map<Http.Method, Function<HttpRequest, HttpResponse>>> ServiceMap;

    public Mappings() {
        this.ServiceMap = Collections.unmodifiableMap(new HashMap<>());
    }

    public Mappings(Map<String, Map<Http.Method, Function<HttpRequest, HttpResponse>>> ServiceMap) {
        this.ServiceMap = Collections.unmodifiableMap(ServiceMap);
    }

    public Mappings addServices(Map<String, Map<Http.Method, Function<HttpRequest, HttpResponse>>> serviceMap) {
        HashMap<String, Map<Http.Method, Function<HttpRequest, HttpResponse>>> hashMap = new HashMap<>(this.ServiceMap);
        hashMap.putAll(serviceMap);
        return new Mappings(hashMap);
    }

    public final Mappings addService(String address, Http.Method method, Function<HttpRequest, HttpResponse> responseFunction) {
        HashMap<String, Map<Http.Method, Function<HttpRequest, HttpResponse>>> hashMap = new HashMap<>(this.ServiceMap);
        if (hashMap.containsKey(address)) {
            hashMap.get(address).put(method, responseFunction);
        } else {
            HashMap<Http.Method, Function<HttpRequest, HttpResponse>> methodMap = new HashMap<>();
            methodMap.put(method, responseFunction);
            hashMap.put(address, methodMap);
        }
        return new Mappings(hashMap);
    }

    public final Map<String, Map<Http.Method, Function<HttpRequest, HttpResponse>>> getServiceMap() {
        return ServiceMap;
    }
}
