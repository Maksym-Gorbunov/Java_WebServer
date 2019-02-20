package se.iths.mhb.plugin;


import se.iths.mhb.http.*;

import java.io.*;
import java.util.List;


public class Calculator implements HttpService {
    private String path = System.getProperty("user.dir") + File.separator + "Calculator" + File.separator
            + "Web" + File.separator;
    private String htmlPage = "";

    public Calculator(){
        String def_index = readFile(path + "default_index.html");
        String styles = "<style>" + readFile(path + "styles.css") + "</style>";
        htmlPage = def_index + styles + "</body></html>";
    }

    @Address("/calculator")
    @RequestMethod
    public HttpResponse getMethod(HttpRequest httpRequest) {
        setHtmlPage();
        return HttpResponse.newBuilder()
                .statusCode(200)
                .setHeader("Content-type", "text/html")
                .mapping(httpRequest.getMapping())
                .body(htmlPage.getBytes())
                .build();
    }

    @RequestMethod(Http.Method.POST)
    public HttpResponse postMethod(HttpRequest httpRequest) {
        List<Parameter> contentParameters = httpRequest.getContentParameters();
        var firstName = contentParameters.stream()
                .filter(parameter -> parameter.getKey().equals("FirstName"))
                .findFirst();
        var lastName = contentParameters.stream()
                .filter(parameter -> parameter.getKey().equals("LastName"))
                .findFirst();

        String dynamicDateHtmlPage = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Post Form</title>\n" +
                "</head>\n" +
                "<body>\n" +
                (firstName.isPresent() ? firstName.get().getValue() : "No FirstName found") + "\n" +
                (lastName.isPresent() ? lastName.get().getValue() : "No FirstName found") + "\n" +
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
    public void setHtmlPage(){
        String index = readFile(path + "index.html");
        String styles = "<style>" + readFile(path + "styles.css") + "</style>";
        String script = "<script>" + readFile(path + "script.js") + "</script>";
        htmlPage = index + styles + script + "</body></html>";
    }

    private String readFile(String path){
        StringBuilder contentBuilder = new StringBuilder();
        try {
            BufferedReader in = new BufferedReader(new FileReader(path));
            String str;
            while ((str = in.readLine()) != null) {
                contentBuilder.append(str);
            }
            in.close();
            return contentBuilder.toString();
        } catch (IOException e) {}
        return "";
    }
}
