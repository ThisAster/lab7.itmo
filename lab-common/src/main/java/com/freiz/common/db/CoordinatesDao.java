package com.freiz.common.db;

import com.freiz.common.data.Coordinates;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class CoordinatesDao {
    private static final int ONE = 1;
    private static final int TWO = 2;
    private static final int THREE = 3;

    private CoordinatesDao() {
    }
    public static Coordinates getCoordinatesById(int id) {
        Database db = Database.getInstance();
        Connection connection = db.getConnection();
        PreparedStatement ps = null;
        Coordinates coordinates = new Coordinates();
        try {
            ps = connection.prepareStatement("select * from coordinates where id = ?");
            ps.setInt(ONE, id);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                coordinates.setX(rs.getDouble("x"));
                coordinates.setY(rs.getFloat("y"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return coordinates;
    }

    public static long createCoordinates(Coordinates coordinates) {
        Database db = Database.getInstance();
        Connection connection = db.getConnection();
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement("INSERT INTO coordinates (x, y) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setDouble(ONE, coordinates.getX());
            ps.setFloat(TWO, coordinates.getY());
            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(ONE);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static void updateCoordinates(Coordinates coordinates, int coordinatesId) {
        Database db = Database.getInstance();
        Connection connection = db.getConnection();
        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement("UPDATE coordinates set x = ?, y = ? WHERE id = ?");
            ps.setDouble(ONE, coordinates.getX());
            ps.setFloat(TWO, coordinates.getY());
            ps.setInt(THREE, coordinatesId);

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removeCoordinates(int coordinatesId) {
        Database db = Database.getInstance();
        Connection connection = db.getConnection();
        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement("DELETE FROM coordinates WHERE id = ?");
            ps.setInt(ONE, coordinatesId);

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int getCoordinatesIdBySpaceMarineId(long orgId) {
        Database db = Database.getInstance();
        Connection connection = db.getConnection();

        int id = -1;
        try (PreparedStatement ps = connection.prepareStatement("select id from coordinates where id = ?")) {
            connection.prepareStatement("select id from coordinates where id = ?");
            ps.setLong(ONE, orgId);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                id = rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return id;
    }
}
