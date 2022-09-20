package com.freiz.common.db;

import com.freiz.common.data.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class UserDao {
    private UserDao() {
    }
    public static User getUserById(int id) {
        //интерфейс Connection
        User user = new User();
        Connection connection = Database.getInstance().getConnection();
        try (PreparedStatement ps = connection.prepareStatement("select * from \"user\" where id = ?")) {
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                user.setName(rs.getString("name"));
                user.setPassword(rs.getString("password"));
                user.setId(rs.getInt("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public static String createUser(User user) {

        if (getUserId(user) != -1) {
            return "User with this login already exists!";
        }

        Connection connection = Database.getInstance().getConnection();

        try (PreparedStatement ps = connection.prepareStatement("INSERT INTO \"user\" (name, password) VALUES (?, ?)")) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getPassword());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "User created successfully";
    }

    public static int getUserId(User user) {
        Connection connection = Database.getInstance().getConnection();
        try (PreparedStatement ps = connection.prepareStatement("SELECT id FROM \"user\" WHERE name = ?")) {
            ps.setString(1, user.getName());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static int getUserId(String username) {
        Connection connection = Database.getInstance().getConnection();
        try (PreparedStatement ps = connection.prepareStatement("select id from \"user\" where name = ?")) {
            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return 0;
    }

    public static User getUser(User user) {
        User userFromDb = new User();
        Connection connection = Database.getInstance().getConnection();
        try (PreparedStatement ps = connection.prepareStatement("select * from \"user\" where name = ?")) {
            ps.setString(1, user.getName());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                userFromDb.setName(rs.getString("name"));
                userFromDb.setPassword(rs.getString("password"));
                userFromDb.setId(rs.getInt("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userFromDb;
    }
}
