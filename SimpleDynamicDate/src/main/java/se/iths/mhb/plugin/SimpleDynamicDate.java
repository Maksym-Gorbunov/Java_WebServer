package se.iths.mhb.plugin;

import se.iths.mhb.http.*;

import java.io.IOException;
import java.util.Date;

@Adress("/date")
public class SimpleDynamicDate implements HttpService {

    @Override
    public String defaultMapping() {
        return "/date";
    }

    @Override
    public HttpResponse serve(HttpRequest httpRequest) throws IOException {
        String dynamicDateHtmlPage = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Title</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "Date: " + new Date().toString() +
                "\n" +
                "</body>\n" +
                "</html>";


        return HttpResponse.newBuilder()
                .statusCode(200)
                .setHeader("Content-type", "text/html")
                .mapping(defaultMapping())
                .body(dynamicDateHtmlPage.getBytes())
                .build();


    }

    @Override
    public String toString() {
        return "SimpleDynamicDate{}";
    }

    @RequestMethod
    public void getMetdo() {
        System.out.println("invoked default GET");
    }

    @RequestMethod("HEAD")
    public void headmetod() {
        System.out.println("invoked HEAD");
    }

    @RequestMethod("POST")
    public void postmetod() {
        System.out.println("invoked POST");
    }

}
