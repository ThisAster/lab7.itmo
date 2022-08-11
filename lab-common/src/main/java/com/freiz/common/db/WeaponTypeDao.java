package com.freiz.common.db;

import com.freiz.common.data.Weapon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class WeaponTypeDao {
    private static final int ONE = 1;
    private WeaponTypeDao() {
    }
    public static Weapon getWeaponTypeById(int id) {
        Connection connection = Database.getInstance().getConnection();
        try (PreparedStatement ps = connection.prepareStatement("select * from weapontype where id = ?")) {
            ps.setInt(ONE, id);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Weapon.valueOf(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Weapon was not found!");
    }

    public static int getWeaponTypeIdByName(String name) {
        Connection connection = Database.getInstance().getConnection();
        try (PreparedStatement ps = connection.prepareStatement("select id from weapontype where name = ?")) {
            ps.setString(ONE, name);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("View was not found!");
    }
}
