package se.iths.mhb.server;

import se.iths.mhb.http.Http;
import se.iths.mhb.http.HttpRequest;
import se.iths.mhb.http.HttpResponse;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;


public class Server {

    static final File WEB_ROOT = new File("WEB-ROOT/static");
    static final String DEFAULT_FILE = "index.html";
    static final String FILE_NOT_FOUND = "404.html";
    static final String METHOD_NOT_SUPPORTED = "501.html";


    private final int port;
    private Mappings mappings;
    private Reader reader;

    public Server(int port) {
        this.port = port;
        this.mappings = new Mappings();
        this.reader = new Reader();
    }

    public void start() {
        //todo read some config file for port and if specify plugin to specific mapping
        startStaticFileListener();
        startPluginListener();
        try {
            ServerSocket serverConnect = new ServerSocket(port);
            System.out.println("Server started.\nListening for connections on port : " + port + " ...\n");

            while (true) {
                CompletableFuture.runAsync(new ClientHandler(serverConnect.accept(), mappings.getServiceMap(), reader.getReadAllRequests()));
            }
        } catch (IOException e) {
            System.err.println("Server Connection error : " + e.getMessage());
        }

    }

    public synchronized void setMappings(List<String> mappingList, Function<HttpRequest, HttpResponse> responseFunction) {
        HashMap<String, Map<Http.Method, Function<HttpRequest, HttpResponse>>> hashMap = new HashMap<>();
        mappingList.forEach(string -> {
            HashMap<Http.Method, Function<HttpRequest, HttpResponse>> methodMap = new HashMap();
            methodMap.put(Http.Method.GET, responseFunction);
            methodMap.put(Http.Method.HEAD, responseFunction);
            hashMap.put(string, methodMap);
        });

        mappings = mappings.addServices(hashMap);
    }

    public synchronized void setMapping(String mapping, Http.Method method, Function<HttpRequest, HttpResponse> responseFunction) {
        mappings = mappings.addService(mapping, method, responseFunction);
    }

    public synchronized void setRequestReader(Consumer<HttpRequest> consumer) {
        reader = reader.addConsumer(consumer);
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
