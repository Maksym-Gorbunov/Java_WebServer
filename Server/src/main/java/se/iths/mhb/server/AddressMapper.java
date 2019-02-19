package se.iths.mhb.server;

import se.iths.mhb.http.Http;
import se.iths.mhb.http.HttpRequest;
import se.iths.mhb.http.HttpResponse;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Thread-safe. The set methods will overwrite current mapping.
 * With the all the address mappings and Http request methods to correct Function.
 */
public final class AddressMapper {
    private Map<String, Map<Http.Method, Function<HttpRequest, HttpResponse>>> mappings;

    public AddressMapper() {
        this.mappings = Collections.unmodifiableMap(new HashMap<>());
    }

    /**
     * Sets each address from the list to responseFunction with GET and HEAD requests.
     *
     * @param mappingList      addresses
     * @param responseFunction response function
     */
    public final synchronized void set(List<String> mappingList, Function<HttpRequest, HttpResponse> responseFunction) {
        HashMap<String, Map<Http.Method, Function<HttpRequest, HttpResponse>>> hashMap = new HashMap<>(this.mappings);
        mappingList.forEach(string -> {
            HashMap<Http.Method, Function<HttpRequest, HttpResponse>> methodMap = new HashMap();
            methodMap.put(Http.Method.GET, responseFunction);
            methodMap.put(Http.Method.HEAD, responseFunction);
            hashMap.put(string, methodMap);
        });

        this.mappings = Collections.unmodifiableMap(hashMap);
    }

    /**
     * Set address and Http.Method to a response function.
     *
     * @param address          address
     * @param method           Http request method
     * @param responseFunction response function
     */
    public final synchronized void set(String address, Http.Method method, Function<HttpRequest, HttpResponse> responseFunction) {
        HashMap<String, Map<Http.Method, Function<HttpRequest, HttpResponse>>> hashMap = new HashMap<>(this.mappings);
        if (hashMap.containsKey(address)) {
            hashMap.get(address).put(method, responseFunction);
        } else {
            HashMap<Http.Method, Function<HttpRequest, HttpResponse>> methodMap = new HashMap<>();
            methodMap.put(method, responseFunction);
            hashMap.put(address, methodMap);
        }
        this.mappings = Collections.unmodifiableMap(hashMap);
    }

    public final Map<String, Map<Http.Method, Function<HttpRequest, HttpResponse>>> getMappings() {
        return mappings;
    }
}
