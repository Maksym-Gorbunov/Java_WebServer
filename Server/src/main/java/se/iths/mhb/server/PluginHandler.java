package se.iths.mhb.server;

import se.iths.mhb.http.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;

/**
 * This class Loads all .jar plugins from a directory ("Plugins")
 * It will reload if new plugin is dropped in the directory.
 * Plugins have to implement HttpService for now and can use the
 * annotations address, requestmethod and readrequest for now.
 */
public class PluginHandler implements Runnable {

    private final Server server;
    private final String pluginDirectory;

    public PluginHandler(Server server, String pluginDirectory) {
        this.server = server;
        this.pluginDirectory = pluginDirectory;
    }

    private void load() {
        int counter = 0;
        List<Consumer<HttpRequest>> requestConsumers = new ArrayList<>();
        URLClassLoader ucl = createClassLoader();
        ServiceLoader<HttpService> loader = ServiceLoader.load(HttpService.class, ucl);
        for (HttpService httpService : loader) {
            //mapp address and requestmethods
            if (httpService.getClass().isAnnotationPresent(Address.class)) {
                String mapping = httpService.getClass().getAnnotation(Address.class).value();
                List<Method> methods = Arrays.stream(httpService.getClass().getDeclaredMethods())
                        .filter(m -> m.isAnnotationPresent(RequestMethod.class))
                        .collect(Collectors.toList());


                for (var method : methods) {
                    Http.Method requestMethod = method.getAnnotation(RequestMethod.class).value();
                    Function<HttpRequest, HttpResponse> responseFunction = httpRequest -> {
                        try {
                            return (HttpResponse) method.invoke(httpService, httpRequest);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                        return StaticFileService.errorResponse(500, httpRequest);
                    };
                    server.getAddressMapper().set(mapping, requestMethod, responseFunction);
                    counter++;
                }
            }

            //load readrequest consumers
            List<Method> methods = Arrays.stream(httpService.getClass().getDeclaredMethods())
                    .filter(m -> m.isAnnotationPresent(ReadRequest.class))
                    .collect(Collectors.toList());

            for (var method : methods) {
                Consumer<HttpRequest> requestConsumer = httpRequest -> {
                    try {
                        method.invoke(httpService, httpRequest);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                };
                //server.getRequestConsumers().add(requestConsumer);
                requestConsumers.add(requestConsumer);

            }
        }
        server.getRequestConsumers().set(requestConsumers);
        System.out.println("Mapped " + counter + " addresses from plugins");
        System.out.println("Loaded " + server.getRequestConsumers().getRequestConsumers().size() + " request consumers from plugins");
    }

    private URLClassLoader createClassLoader() {
        File[] files = new File(pluginDirectory).listFiles(file -> file.getPath().toLowerCase().endsWith(".jar"));
        Objects.requireNonNull(files);
        URL[] urls = new URL[files.length];
        for (int i = 0; i < files.length; i++) {
            try {
                urls[i] = files[i].toURI().toURL();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return new URLClassLoader(urls);
    }

    @Override
    public void run() {
        load();
        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();
            Path path = Path.of(pluginDirectory);
            path.register(
                    watchService,
                    ENTRY_CREATE,
                    ENTRY_DELETE);

            WatchKey key;
            while ((key = watchService.take()) != null) {
                System.out.println("Reloading Plugins");
                load();
                for (WatchEvent<?> event : key.pollEvents()) {

                }
                key.reset();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}
