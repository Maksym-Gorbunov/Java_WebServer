package se.iths.mhb.server;

import se.iths.mhb.http.HttpRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * Thread-safe
 * Holder class for request consumers.
 */
public final class RequestConsumers {

    private List<Consumer<HttpRequest>> requestConsumers;

    public RequestConsumers() {
        this.requestConsumers = Collections.unmodifiableList(new ArrayList<>());
    }

    /**
     * Removes all currently stored consumers and sets new
     *
     * @param consumers new consumers
     */
    public synchronized void set(List<Consumer<HttpRequest>> consumers) {
        requestConsumers = Collections.unmodifiableList(new ArrayList<>(consumers));
    }

    /**
     * @return unmodifiable list of consumers
     */
    public final List<Consumer<HttpRequest>> getRequestConsumers() {
        return requestConsumers;
    }
}
