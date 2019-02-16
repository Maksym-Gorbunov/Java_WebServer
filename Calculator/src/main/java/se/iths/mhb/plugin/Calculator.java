package se.iths.mhb.plugin;

import se.iths.mhb.http.HttpRequest;
import se.iths.mhb.http.HttpResponse;
import se.iths.mhb.http.HttpService;
import se.iths.mhb.server.ClientHandler;

import java.io.IOException;


// Simple calculator with url arguments input
// ex. http://localhost:8085/calculator?a=5&b=1&c=add

public class Calculator implements HttpService {
    private int a = 0;
    private int b = 0;
    private String operator = "";
    private String[] operators = new String[]{"add", "sub", "mul", "div"};

    @Override
    public String defaultMapping() {
        return "/calculator";
    }

    @Override
    public HttpResponse serve(HttpRequest httpRequest) throws IOException {
        String data = "wrong calculator arguments in url";
        if(setParameters()){
            switch (operator){
                case "add":
                    data = a + " + " + b + " = " + (a + b);
                    break;
                case "sub":
                    data = a + " - " + b + " = " + (a - b);
                    break;
                case "mul":
                    data = a + " * " + b + " = " + (a * b);
                    break;
                case "div":
                    data = a + " / " + b + " = " + (a / b);
                    break;
                default: break;
            }
        }

        String dynamicDateHtmlPage = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Title</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<div>"+
                data +
                "</div>\n"+
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
        return "Calculator{}";
    }

    private boolean setParameters(){
        if((ClientHandler.parameterList.size() == 3) &&
                (ClientHandler.parameterList.get(0).getValue().matches("[0-9]{1,}")) &&
                (ClientHandler.parameterList.get(1).getValue().matches("[0-9]{1,}"))){
            for(String s : operators){
                if(s.equals(ClientHandler.parameterList.get(2).getValue())){
                    a = Integer.parseInt(ClientHandler.parameterList.get(0).getValue());
                    b = Integer.parseInt(ClientHandler.parameterList.get(1).getValue());
                    operator = ClientHandler.parameterList.get(2).getValue();
                    return true;
                }
            }
        }
        return false;
    }
}
