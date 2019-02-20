package se.iths.mhb.plugin;

import se.iths.mhb.http.HttpRequest;

import java.util.List;

public interface Storage {
    void addHit(HttpRequest httpRequest);

    List<PageHit> getHits();
}
