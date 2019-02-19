package se.iths.mhb.plugin;

import se.iths.mhb.http.HttpRequest;

public class StatsService {

    static final StatsService statsService = new StatsService();

    private final Storage runtimeStorage;
    private final Storage sqlLiteStorage;

    public StatsService() {
        this.runtimeStorage = new RuntimeStorage();
        this.sqlLiteStorage = new SqlLiteStorage();
        System.out.println("StatsService started");
    }

    public void add(HttpRequest httpRequest) {
        runtimeStorage.addHit(httpRequest);
        sqlLiteStorage.addHit(httpRequest);
    }

    public Storage getRuntimeStorage() {
        return runtimeStorage;
    }

    public Storage getSqlLiteStorage() {
        return sqlLiteStorage;
    }
}
