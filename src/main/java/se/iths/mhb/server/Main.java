package se.iths.mhb.server;

import javax.servlet.ServletException;

public class Main {

    public static void main(String[] args) {
        Server server = new Server(8085);
        server.start();

        MyServlet myServlet = new MyServlet(); // Construct servlet.
//        helloServlet.init(servletConfig); // Initialize servlet with config.
        try {
            myServlet.init(); // Initialize servlet without config.
        } catch (ServletException e) {
            e.printStackTrace();
        }
//        servlets.put("/hello", myServlet); // Add to servlet mapping.
    }

}
