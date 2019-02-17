package se.iths.mhb.server;

import se.iths.mhb.http.HttpRequest;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public final class RequestConsumers {

    private final List<Consumer<HttpRequest>> requestConsumers;

    public RequestConsumers() {
        this.requestConsumers = Collections.unmodifiableList(new LinkedList<>());
    }

    public RequestConsumers(List<Consumer<HttpRequest>> requestConsumers) {
        this.requestConsumers = Collections.unmodifiableList(requestConsumers);
    }

    public RequestConsumers add(Consumer<HttpRequest> consumer) {
        LinkedList<Consumer<HttpRequest>> consumers = new LinkedList<>(requestConsumers);
        consumers.add(consumer);
        return new RequestConsumers(consumers);
    }

    public List<Consumer<HttpRequest>> getRequestConsumers() {
        return requestConsumers;
    }
}
