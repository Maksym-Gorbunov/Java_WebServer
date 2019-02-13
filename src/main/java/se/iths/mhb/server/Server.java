package se.iths.mhb.server;

import se.iths.mhb.http.HttpService;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class Server {

    static final File WEB_ROOT = new File("WEB-ROOT/static");
    static final String DEFAULT_FILE = "index.html";
    static final String FILE_NOT_FOUND = "404.html";
    static final String METHOD_NOT_SUPPORTED = "501.html";


    private final int port;
    private Plugins plugins;
    private final StaticFileService staticFileService;

    public Server(int port) {
        this.port = port;
        this.plugins = new Plugins();
        this.staticFileService = new StaticFileService(this);
    }

    public void start() {
        setMapping("/", staticFileService);
        startPluginListener();
        ServerSocket serverConnect = null;
        try {
            serverConnect = new ServerSocket(port);
            System.out.println("Server started.\nListening for connections on port : " + port + " ...\n");

            while (true) {
                System.out.println("Connection opened. (" + new Date() + ")");
                CompletableFuture.runAsync(new ClientHandler(serverConnect.accept(), plugins.getPlugins()));
            }
        } catch (IOException e) {
            System.err.println("Server Connection error : " + e.getMessage());
        }

    }

    public synchronized void setMappings(List<String> mappings, HttpService httpService) {
        HashMap<String, HttpService> hashMap = new HashMap<>();
        mappings.forEach(mapping -> hashMap.put(mapping, httpService));
        plugins = plugins.addPlugins(hashMap);
    }

    public synchronized void setMapping(String mapping, HttpService httpService) {
        plugins = plugins.addPlugin(mapping, httpService);
    }

    private void loadConfig() {

    }

    private void startPluginListener() {
        CompletableFuture.runAsync(() -> {
            while (true) {
                File[] files = Server.WEB_ROOT.listFiles(File::isFile);
                List<String> strings = Arrays.stream(files).map(file -> "/" + file.getName().toLowerCase()).collect(Collectors.toList());
                setMappings(strings, staticFileService);

                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

        });
    }


}
