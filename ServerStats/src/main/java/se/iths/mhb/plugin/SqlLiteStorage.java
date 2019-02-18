package se.iths.mhb.plugin;

import se.iths.mhb.http.Http;
import se.iths.mhb.http.HttpRequest;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class SqlLiteStorage implements Storage {
    String path = "jdbc:sqlite:mhbserverstats.db";
    Connection sqliteConnection;


    public SqlLiteStorage() {
        try {
            sqliteConnection = DriverManager.getConnection(path);

            Statement stmt1 = sqliteConnection.createStatement();
            stmt1.execute("PRAGMA foreign_keys = ON");
            stmt1.close();


            String addressTable = "CREATE TABLE IF NOT EXISTS Address(" +
                    "ID integer PRIMARY KEY," +
                    "Address TEXT);";


            Statement stmt2 = sqliteConnection.createStatement();
            stmt2.execute(addressTable);
            stmt2.close();

            String methodTable = "CREATE TABLE IF NOT EXISTS Method(" +
                    "Method TEXT UNIQUE," +
                    "Counter integer," +
                    " FOREIGN KEY(addressid) REFERENCES Address(ID);";

            Statement stmt3 = sqliteConnection.createStatement();
            stmt3.execute(methodTable);
            stmt3.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void addHit(HttpRequest httpRequest) {
        try {
            System.out.println("SQL ADD 1");
            //   Connection sqliteConnection = DriverManager.getConnection(path);

            //Lägg till en kund i tabellen Customers
            String sql_insert_customer = "INSERT INTO Address(Address)" +
                    " VALUES('" + httpRequest.getMapping() + "');";

            Statement stmt = sqliteConnection.createStatement();
            stmt.execute(sql_insert_customer);
            stmt.close();
            //     sqliteConnection.close();
            System.out.println("SQL ADD 2");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized List<PageHit> getHits() {
        List<PageHit> hits = new LinkedList<>();

        try {
            System.out.println("SQL READ 1");
//            String sql = "select * from people";
//            PreparedStatement statement = connection.prepareStatement(sql);
//
//            ResultSet result = statement.executeQuery();

            //  Connection sqliteConnection = DriverManager.getConnection(path);

            //Hämta alla kunder med matchande namn
            String sql_select_customer = "SELECT Counter, Method, Address FROM Method NATURAL JOIN Address";

            Statement stmt = sqliteConnection.createStatement();

            ResultSet rs = stmt.executeQuery(sql_select_customer);


            while (rs.next()) {
                hits.add(new PageHit(rs.getString("Address"), Enum.valueOf(Http.Method.class, rs.getString("Method")), rs.getInt("Counter")));
            }
            rs.close();
            stmt.close();
            //    sqliteConnection.close();
            System.out.println("SQL READ 2");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hits;
    }
}
