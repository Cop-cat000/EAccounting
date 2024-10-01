package utils.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DBConnector {
    private static Statement stmt;
    private DBConnector() {}
    
    public static void connect(String DBURL, String DBUSERNAME, String DBPASSWORD) {
        Connection connection;
        try {
            connection = DriverManager.getConnection(DBURL, DBUSERNAME, DBPASSWORD);
            stmt = connection.createStatement();
        } catch(Exception e) {
            System.out.println("DB connection unsuccessful.");
            System.out.println(e);
            throw new RuntimeException();
        }
    }

    public static Statement getStatement() {
        return stmt;
    }
}
