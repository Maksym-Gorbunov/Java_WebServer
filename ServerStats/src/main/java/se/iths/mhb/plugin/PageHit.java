package se.iths.mhb.plugin;

import se.iths.mhb.http.Http;

public final class PageHit implements Comparable<PageHit> {

    private final String address;
    private final Http.Method method;
    private final long counter;

    public PageHit(String address, Http.Method method, long counter) {
        this.address = address;
        this.method = method;
        this.counter = counter;
    }

    public final PageHit increment() {
        long inc = counter + 1;
        return new PageHit(this.getAddress(), this.getMethod(), inc);
    }

    public final String getAddress() {
        return address;
    }

    public final Http.Method getMethod() {
        return method;
    }

    public final long getCounter() {
        return counter;
    }

    @Override
    public String toString() {
        return "PageHit{" +
                "address='" + address + '\'' +
                ", method=" + method +
                ", counter=" + counter +
                '}';
    }

    @Override
    public final int compareTo(PageHit other) {
        return (int) (other.getCounter() - this.getCounter());
    }
}
