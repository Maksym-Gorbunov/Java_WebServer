package se.iths.mhb.server;

import se.iths.mhb.http.Http;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Main thread which listens for new connections.
 * Also has the address mappings to all static files and plugins.
 */
public class Server {

    static final File WEB_ROOT = new File("WEB-ROOT/static");
    static final String DEFAULT_FILE = "index.html";
    static final String FILE_NOT_FOUND = "404.html";
    static final String METHOD_NOT_SUPPORTED = "501.html";

    private final int port;
    private final AddressMapper addressMapper;
    private final RequestConsumers requestConsumers;

    public Server(int port) {
        this.port = port;
        this.addressMapper = new AddressMapper();
        this.requestConsumers = new RequestConsumers();
    }

    public void start() {
        RootPage rootPage = new RootPage(this);
        addressMapper.set("/", Http.Method.GET, rootPage::showAllMappingsPage);
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

    public RequestConsumers getRequestConsumers() {
        return requestConsumers;
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
