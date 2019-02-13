package se.iths.mhb.server;

import se.iths.mhb.http.HttpService;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class Plugins {
    private final Map<String, HttpService> plugins;

    public Plugins() {
        this.plugins = Collections.unmodifiableMap(new HashMap<>());
    }

    public Plugins(Map<String, HttpService> plugins) {
        this.plugins = Collections.unmodifiableMap(plugins);
    }

    public Plugins addPlugins(Map<String, HttpService> plugins) {
        HashMap<String, HttpService> hashMap = new HashMap<>(this.plugins);
        hashMap.putAll(plugins);
        return new Plugins(hashMap);
    }

    public final Plugins addPlugin(String mapping, HttpService httpService) {
        HashMap<String, HttpService> hashMap = new HashMap<>(this.plugins);
        hashMap.put(mapping, httpService);
        return new Plugins(hashMap);
    }

    public final Map<String, HttpService> getPlugins() {
        return plugins;
    }
}
