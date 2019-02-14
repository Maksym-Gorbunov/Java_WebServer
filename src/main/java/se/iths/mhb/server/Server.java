package se.iths.mhb.server;

import se.iths.mhb.http.HttpService;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;


public class Server {

    static final File WEB_ROOT = new File("WEB-ROOT/static");
    static final String DEFAULT_FILE = "index.html";
    static final String FILE_NOT_FOUND = "404.html";
    static final String METHOD_NOT_SUPPORTED = "501.html";


    private final int port;
    private Mappings mappings;

    public Server(int port) {
        this.port = port;
        this.mappings = new Mappings();
    }

    public void start() {
        //todo read some config file for port and if specify plugin to specific mapping
        startStaticFileListener();
        startPluginListener();
        try {
            ServerSocket serverConnect = new ServerSocket(port);
            System.out.println("Server started.\nListening for connections on port : " + port + " ...\n");

            while (true) {
                System.out.println("Connection opened. (" + new Date() + ")");
                CompletableFuture.runAsync(new ClientHandler(serverConnect.accept(), mappings.getServiceMap()));
            }
        } catch (IOException e) {
            System.err.println("Server Connection error : " + e.getMessage());
        }

    }

    public synchronized void setMappings(List<String> mappingList, HttpService httpService) {
        HashMap<String, HttpService> hashMap = new HashMap<>();
        mappingList.forEach(mapping -> hashMap.put(mapping, httpService));
        mappings = mappings.addServices(hashMap);
    }

    public synchronized void setMapping(String mapping, HttpService httpService) {
        mappings = mappings.addService(mapping, httpService);
    }

    public synchronized void setDefaultMapping(HttpService httpService) {
        mappings = mappings.addService(httpService.defaultMapping(), httpService);
    }

    private void startPluginListener() {
        PluginHandler pluginHandler = new PluginHandler(this, "Plugins");
        Thread thread = new Thread(pluginHandler);
        thread.setDaemon(true);
        thread.start();
    }

    private void startStaticFileListener() {
        StaticFileService staticFileService = new StaticFileService(this);
        Thread thread = new Thread(staticFileService);
        thread.setDaemon(true);
        thread.start();
    }

}
