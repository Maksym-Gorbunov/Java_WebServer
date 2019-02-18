package se.iths.mhb.http;

import java.util.Objects;

/**
 * A Key value class to be used for parameters
 */
public class Parameter {

    private final String key;
    private final String value;

    public Parameter(String key, String value) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        this.key = key;
        this.value = value;
    }

    public Parameter(String pair) {
        Objects.requireNonNull(pair);
        if (!pair.contains("="))
            throw new IllegalArgumentException("Not valid pair, must contain =");
        int idx = pair.indexOf("=");
        this.key = pair.substring(0, idx);
        this.value = pair.substring(idx + 1);
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Parameter{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
