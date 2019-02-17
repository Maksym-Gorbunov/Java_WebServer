package se.iths.mhb.plugin;

import se.iths.mhb.http.*;

import java.io.IOException;

@Address("/calculator")
public class Calculator implements HttpService {

    @RequestMethod
    public HttpResponse serve(HttpRequest httpRequest) throws IOException {
        String dynamicDateHtmlPage = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Title</title>\n" +
                "</head>\n" +
                "<body>\n" +

                "CALCULATOR: \n" +


                "</body>\n" +
                "</html>";


        return HttpResponse.newBuilder()
                .statusCode(200)
                .setHeader("Content-type", "text/html")
                .mapping(httpRequest.getMapping())
                .body(dynamicDateHtmlPage.getBytes())
                .build();


    }

    @Override
    public String toString() {
        return "Calculator{}";
    }
}
