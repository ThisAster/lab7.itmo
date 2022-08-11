package com.freiz.common.db;

import com.freiz.common.data.SpaceMarine;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.freiz.common.db.ChapterDao.createChapter;
import static com.freiz.common.db.ChapterDao.getChapterById;
import static com.freiz.common.db.ChapterDao.getChapterIdBySpaceMarineId;
import static com.freiz.common.db.ChapterDao.removeChapter;
import static com.freiz.common.db.ChapterDao.updateChapter;
import static com.freiz.common.db.CoordinatesDao.createCoordinates;
import static com.freiz.common.db.CoordinatesDao.getCoordinatesById;
import static com.freiz.common.db.CoordinatesDao.getCoordinatesIdBySpaceMarineId;
import static com.freiz.common.db.CoordinatesDao.removeCoordinates;
import static com.freiz.common.db.CoordinatesDao.updateCoordinates;
import static com.freiz.common.db.MeleeWeaponDao.getMeleeWeaponById;
import static com.freiz.common.db.MeleeWeaponDao.getMeleeWeaponIdByName;
import static com.freiz.common.db.UserDao.getUserById;
import static com.freiz.common.db.WeaponTypeDao.getWeaponTypeById;
import static com.freiz.common.db.WeaponTypeDao.getWeaponTypeIdByName;

public final class SpaceMarineDao {

    private static final int ONE = 1;
    private static final int TWO = 2;
    private static final int THREE = 3;
    private static final int FOUR = 4;
    private static final int FIVE = 5;
    private static final int SIX = 6;
    private static final int SEVEN = 7;
    private static final int EIGHT = 8;
    private static final int NINE = 9;

    private SpaceMarineDao() {
    }
    public static List<SpaceMarine> getAllSpaceMarinesFromDB() {
        List<SpaceMarine> spaceMarines = new ArrayList<>();
        Connection connection = Database.getInstance().getConnection();
        try (PreparedStatement ps = connection.prepareStatement("select * from spacemarine")) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                SpaceMarine spaceMarine = new SpaceMarine();
                spaceMarine.setId(rs.getLong("id"));
                spaceMarine.setName(rs.getString("name"));
                spaceMarine.setCoordinates(getCoordinatesById(rs.getInt("coordinates_id")));
                spaceMarine.setCreationDate(ZonedDateTime.ofInstant(rs.getTimestamp("creationDate").toInstant(), ZoneId.of("UTC")));
                spaceMarine.setHealth(rs.getFloat("health"));
                spaceMarine.setHeartCount(rs.getInt("heartcount"));
                spaceMarine.setChapter(getChapterById(rs.getInt("chapter_id")));
                spaceMarine.setMeleeWeapon(getMeleeWeaponById(rs.getInt("melee_weapon_id")));
                spaceMarine.setWeaponType(getWeaponTypeById(rs.getInt("weapon_type_id")));
                spaceMarine.setUser(getUserById(rs.getInt("user_id")));
                spaceMarines.add(spaceMarine);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return spaceMarines;
    }

    public static SpaceMarine getSpaceMarineById(long id) {
        SpaceMarine spaceMarine = null;
        Connection connection = Database.getInstance().getConnection();
        try (PreparedStatement ps = connection.prepareStatement("select * from spacemarine where id = ?")) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                spaceMarine = new SpaceMarine();
                spaceMarine.setId(rs.getLong("id"));
                spaceMarine.setName(rs.getString("name"));
                spaceMarine.setCreationDate(ZonedDateTime.ofInstant(rs.getTimestamp("creationDate").toInstant(), ZoneId.of("UTC")));
                spaceMarine.setHealth(rs.getFloat("health"));
                spaceMarine.setHeartCount(rs.getInt("heartcount"));
                spaceMarine.setMeleeWeapon(getMeleeWeaponById(rs.getInt("melee_weapon_id")));
                spaceMarine.setWeaponType(getWeaponTypeById(rs.getInt("weapon_type_id")));
                spaceMarine.setChapter(getChapterById(rs.getInt("chapter_id")));
                spaceMarine.setUser(getUserById(rs.getInt("user_id")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return spaceMarine;
    }

    public static void createSpaceMarine(SpaceMarine spaceMarine, int userId) {
        Connection connection = Database.getInstance().getConnection();
        try (PreparedStatement ps = connection.prepareStatement("INSERT INTO spacemarine "
                + "(name, coordinates_id, \"creationDate\", health, heartcount, weapon_type_id,"
                + " melee_weapon_id, chapter_id, user_id)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            ps.setString(ONE, spaceMarine.getName());
            ps.setLong(TWO, createCoordinates(spaceMarine.getCoordinates()));
            ps.setDate(THREE, new Date(System.currentTimeMillis()));
            ps.setFloat(FOUR, spaceMarine.getHealth());
            ps.setInt(FIVE, spaceMarine.getHeartCount());
            ps.setInt(SIX, getWeaponTypeIdByName(spaceMarine.getWeaponType().name()));
            ps.setInt(SEVEN, getMeleeWeaponIdByName(spaceMarine.getMeleeWeapon().name()));
            ps.setInt(EIGHT, createChapter(spaceMarine.getChapter()));
            ps.setInt(NINE, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void update(SpaceMarine spaceMarine, long spaceMarineId) {
        Connection connection = Database.getInstance().getConnection();
        try (PreparedStatement ps = connection.prepareStatement("UPDATE spacemarine set name = ?,"
                + "health = ?, heartcount = ?, weapon_type_id = ?, melee_weapon_id = ?"
                + "WHERE id = ?")) {
            ps.setString(ONE, spaceMarine.getName());
            ps.setFloat(TWO, spaceMarine.getHealth());
            ps.setInt(THREE, spaceMarine.getHeartCount());
            ps.setInt(FOUR, getWeaponTypeIdByName(spaceMarine.getWeaponType().name()));
            ps.setInt(FIVE, getMeleeWeaponIdByName(spaceMarine.getMeleeWeapon().name()));
            ps.setLong(SIX, spaceMarineId);

            updateCoordinates(spaceMarine.getCoordinates(), getCoordinatesIdBySpaceMarineId(spaceMarine.getId()));
            updateChapter(spaceMarine.getChapter(), getChapterIdBySpaceMarineId(spaceMarine.getId()));

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removeById(long id) {
        Connection connection = Database.getInstance().getConnection();
        try (PreparedStatement ps = connection.prepareStatement("DELETE FROM spacemarine WHERE id = ?")) {
            ps.setLong(ONE, id);

            removeCoordinates(getCoordinatesIdBySpaceMarineId(id));
            removeChapter(getChapterIdBySpaceMarineId(id));

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
