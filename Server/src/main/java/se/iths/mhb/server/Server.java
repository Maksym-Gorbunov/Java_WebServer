package se.iths.mhb.server;

import se.iths.mhb.http.Http;
import se.iths.mhb.http.HttpRequest;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;

/**
 * Main thread which listens for new connections.
 * Also has the address mappings to all static files and plugins.
 * The mappings and set should be thread-safe
 */
public class Server {

    static final File WEB_ROOT = new File("WEB-ROOT/static");
    static final String DEFAULT_FILE = "index.html";
    static final String FILE_NOT_FOUND = "404.html";
    static final String METHOD_NOT_SUPPORTED = "501.html";


    private final int port;
    private final AddressMapper addressMapper;
    private RequestConsumers requestConsumers;

    public Server(int port) {
        this.port = port;
        this.addressMapper = new AddressMapper();
        this.requestConsumers = new RequestConsumers();
    }

    public void start() {
        RootPage rootPage = new RootPage(this);
        addressMapper.set("/", Http.Method.GET, rootPage::showAllMappingsPage);
        addressMapper.set("/", Http.Method.HEAD, rootPage::showAllMappingsPage);
        startStaticFileListener();
        startPluginListener();
        try {
            ServerSocket serverConnect = new ServerSocket(port);
            System.out.println("Server started.\nListening for connections on port : " + port + " ...\n");

            while (true) {
                Socket socket = serverConnect.accept();
                Thread thread = new Thread(new ClientHandler(socket, addressMapper.getMappings(), requestConsumers.getRequestConsumers()));
                thread.start();
            }
        } catch (IOException e) {
            System.err.println("Server Connection error : " + e.getMessage());
        }

    }

    public AddressMapper getAddressMapper() {
        return addressMapper;
    }

//    public synchronized void set(List<String> mappingList, Function<HttpRequest, HttpResponse> responseFunction) {
//        HashMap<String, Map<Http.Method, Function<HttpRequest, HttpResponse>>> hashMap = new HashMap<>();
//        mappingList.forEach(string -> {
//            HashMap<Http.Method, Function<HttpRequest, HttpResponse>> methodMap = new HashMap();
//            methodMap.put(Http.Method.GET, responseFunction);
//            methodMap.put(Http.Method.HEAD, responseFunction);
//            hashMap.put(string, methodMap);
//        });
//
//        mappings = mappings.addServices(hashMap);
//    }

//    public synchronized void set(String mapping, Http.Method method, Function<HttpRequest, HttpResponse> responseFunction) {
//        mappings = mappings.addService(mapping, method, responseFunction);
//    }

    public synchronized void addRequestConsumer(Consumer<HttpRequest> consumer) {
        requestConsumers = requestConsumers.add(consumer);
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
