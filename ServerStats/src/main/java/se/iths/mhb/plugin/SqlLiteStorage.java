package se.iths.mhb.plugin;

import se.iths.mhb.http.Http;
import se.iths.mhb.http.HttpRequest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class SqlLiteStorage implements Storage {

    private final String path = "jdbc:sqlite:DB/mhbserverstats.db";
    private Connection connection;

    public SqlLiteStorage() {
        try {
            File directory = new File("DB");
            if (!directory.exists()) {
                Files.createDirectory(directory.toPath());
            }
            connection = DriverManager.getConnection(path);

            String addressTable = "CREATE TABLE IF NOT EXISTS pagehit(" +
                    "address TEXT," +
                    "method TEXT," +
                    "hits integer," +
                    "UNIQUE(address, method) ON CONFLICT REPLACE);";

            Statement statement = connection.createStatement();
            statement.execute(addressTable);
            statement.close();

        } catch (SQLException | IOException e) {
            System.out.println("Couldn't load or start sqlite database");
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void addHit(HttpRequest httpRequest) {
        if (connection == null)
            return;
        try {
            String insertOrIncrement = "INSERT INTO pagehit(address,method,hits) " +
                    "VALUES('" + httpRequest.getMapping() + "','" + httpRequest.getMethod() + "','" + 1 + "') " +
                    "ON CONFLICT(address,method) " +
                    "DO UPDATE SET hits=hits +1;";
            System.out.println(insertOrIncrement);
            Statement stmt = connection.createStatement();
            stmt.execute(insertOrIncrement);
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized List<PageHit> getHits() {
        List<PageHit> hits = new LinkedList<>();
        if (connection == null)
            return hits;
        try {
            String selectment = "SELECT * FROM pagehit ORDER BY hits DESC";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(selectment);

            while (rs.next()) {
                hits.add(new PageHit(rs.getString("address"), Enum.valueOf(Http.Method.class, rs.getString("method")), rs.getInt("hits")));
            }
            rs.close();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hits;
    }
}
