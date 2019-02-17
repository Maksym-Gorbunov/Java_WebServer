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
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;

public class PluginHandler implements Runnable {

    private final Server server;
    private final String pluginDirectory;

    public PluginHandler(Server server, String pluginDirectory) {
        this.server = server;
        this.pluginDirectory = pluginDirectory;
    }

    private void load() {
        URLClassLoader ucl = createClassLoader();
        ServiceLoader<HttpService> loader = ServiceLoader.load(HttpService.class, ucl);
        for (HttpService httpService : loader) {
            //mapp address and requestmethods
            if (httpService.getClass().isAnnotationPresent(Address.class)) {
                String mapping = httpService.getClass().getAnnotation(Address.class).value();
                List<Method> collect = Arrays.stream(httpService.getClass().getDeclaredMethods())
                        .filter(m -> m.isAnnotationPresent(RequestMethod.class))
                        .collect(Collectors.toList());

                collect.forEach(method -> {
                    Http.Method requestMethod = method.getAnnotation(RequestMethod.class).value();
                    Function<HttpRequest, HttpResponse> responseFunction = httpRequest -> {
                        try {
                            return (HttpResponse) method.invoke(httpService, httpRequest);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                        return null;
                    };
                    server.setMapping(mapping, requestMethod, responseFunction);
                });
            }

            //mapp readrequest methods
            List<Method> methods = Arrays.stream(httpService.getClass().getDeclaredMethods())
                    .filter(m -> m.isAnnotationPresent(ReadRequest.class))
                    .collect(Collectors.toList());

            for (var method : methods) {
                Consumer<HttpRequest> requestConsumer = httpRequest -> {
                    try {
                        method.invoke(httpService, httpRequest);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                };
                server.setRequestReader(requestConsumer);
            }
        }

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
        System.out.println("Init Plugins");
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
