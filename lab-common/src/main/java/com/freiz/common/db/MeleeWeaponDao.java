package com.freiz.common.db;

import com.freiz.common.data.MeleeWeapon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class MeleeWeaponDao {
    private static final int ONE = 1;
    private MeleeWeaponDao() {
    }
    public static MeleeWeapon getMeleeWeaponById(int id) {
        Connection connection = Database.getInstance().getConnection();
        try (PreparedStatement ps = connection.prepareStatement("select * from meleeweapon where id = ?")) {
            ps.setInt(ONE, id);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return MeleeWeapon.valueOf(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Weapon was not found!");
    }

    public static int getMeleeWeaponIdByName(String name) {
        Connection connection = Database.getInstance().getConnection();
        try (PreparedStatement ps = connection.prepareStatement("select id from meleeweapon where name = ?")) {
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
