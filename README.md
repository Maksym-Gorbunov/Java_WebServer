# WebServer
A simple web server with support for plugins.

```
mvn package
java -jar Server-1.0.jar
```

Implementations of HttpService will be parsed. Use
@Address and @RequestMethod to map methods.
Methods with @ReadRequest will read all HttpRequests to the server.

Sqlite3 is required for the persistent storage for StatsServer.

Simple json api is available at /serverstats/api/v1/pagehits{?method=GET|POST...}


