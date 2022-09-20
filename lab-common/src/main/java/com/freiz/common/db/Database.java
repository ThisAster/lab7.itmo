package com.freiz.common.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

public final class Database {
    private static final String DB_URL = "jdbc:postgresql://127.0.0.1:5432/studs";
    private static final String USER = "s338879";
    private static final String PASS = "7emn2ZsjgX2BaLq4";

    private static Database database;
    private final Connection connection;
    private final Logger logger = Logger.getLogger("log");

    private Database() {
        this.connection = connectToDB();
    }

    public static Database getInstance() {
        if (database == null) {
            database = new Database();
        }
        return database;
    }

    private Connection connectToDB() {
        try {
            Class.forName("org.postgresql.Driver"); //загрузка драйвера, передаём имя драйвера классу Class
            Connection connect = DriverManager.getConnection(DB_URL, USER, PASS);
            logger.info("connected to database");
            return connect;
            //вызываем метод getConnection у класса DriverManager, которому передаём параметры соединения с БД
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Problems with creating connection!");
    }

    public Connection getConnection() {
        if (connection == null) {
            throw new RuntimeException("No connection to database!");
        }
        return connection;
    }
}
