package se.iths.mhb.server;

public class Main {

    public static void main(String[] args) {
        Server server = new Server(8085);
        server.start();
    }

}
