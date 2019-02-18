package se.iths.mhb.http;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HttpRequestTest {

    private String address = "index.html";

    @Test
    void getMethod() {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .mapping(address)
                .build();

        assertEquals(Http.Method.GET, httpRequest.getMethod());

        httpRequest = HttpRequest.newBuilder()
                .method(Http.Method.POST)
                .mapping(address)
                .build();

        assertEquals(Http.Method.POST, httpRequest.getMethod());
        assertEquals(address, httpRequest.getMapping());

    }


    @Test
    void getHeaders() {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .mapping(address)
                .build();


        assertNotNull(httpRequest.getHeaders());
        assertTrue(httpRequest.getHeaders().isEmpty());

        assertThrows(UnsupportedOperationException.class, () -> httpRequest.getHeaders().put("key", "value"));

        HttpRequest httpRequest2 = HttpRequest.newBuilder()
                .mapping(address)
                .setHeader("Content-type", "text/html")
                .build();

        assertEquals(1, httpRequest2.getHeaders().size());
        assertEquals("text/html", httpRequest2.getHeaders().get("Content-type"));
    }

    @Test
    void getParameters() {
    }

    @Test
    void getContent() {
    }

    @Test
    void getContentParameters() {
    }

    @Test
    void newBuilder() {
    }
}