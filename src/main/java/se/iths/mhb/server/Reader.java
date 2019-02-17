package se.iths.mhb.server;

import se.iths.mhb.http.HttpRequest;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public final class Reader {

    private final List<Consumer<HttpRequest>> readAllRequests;

    public Reader() {
        this.readAllRequests = Collections.unmodifiableList(new LinkedList<>());
    }

    public Reader(List<Consumer<HttpRequest>> readAllRequests) {
        this.readAllRequests = Collections.unmodifiableList(readAllRequests);
    }

    public Reader addConsumer(Consumer<HttpRequest> consumer) {
        LinkedList<Consumer<HttpRequest>> consumers = new LinkedList<>(readAllRequests);
        consumers.add(consumer);
        return new Reader(consumers);
    }

    public List<Consumer<HttpRequest>> getReadAllRequests() {
        return readAllRequests;
    }
}
