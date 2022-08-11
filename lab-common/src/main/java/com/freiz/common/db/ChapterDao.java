package com.freiz.common.db;

import com.freiz.common.data.Chapter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class ChapterDao {
    private static final int ONE = 1;
    private static final int TWO = 2;
    private static final int THREE = 3;
    private static final int FOUR = 4;
    private static final int FIVE = 5;
    private ChapterDao() {
    }
    public static Chapter getChapterById(int id) {
        Database db = Database.getInstance();
        Connection connection = db.getConnection();
        Chapter chapter = new Chapter();
        try (PreparedStatement ps = connection.prepareStatement("select * from chapter where id = ?")) {
            ps.setInt(ONE, id);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                chapter.setMarinesCount(rs.getInt("marinescount"));
                chapter.setParentLegion(rs.getString("parentLegion"));
                chapter.setWorld(rs.getString("world"));
                chapter.setName(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return chapter;
    }

    public static int createChapter(Chapter chapter) {
        Database db = Database.getInstance();
        Connection connection = db.getConnection();
        try (PreparedStatement ps = connection.prepareStatement("INSERT INTO chapter (name, parentlegion, marinescount, world)"
                + " VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(ONE, chapter.getName());
            ps.setString(TWO, chapter.getParentLegion());
            ps.setInt(THREE, chapter.getMarinesCount());
            ps.setString(FOUR, chapter.getWorld());
            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(ONE);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static void updateChapter(Chapter chapter, int chapterId) {
        Database db = Database.getInstance();
        Connection connection = db.getConnection();

        try (PreparedStatement ps = connection.prepareStatement("UPDATE chapter set name = ?,"
                + " parentlegion = ?, marinescount = ?, world = ? WHERE id = ?")) {
            ps.setString(ONE, chapter.getName());
            ps.setString(TWO, chapter.getParentLegion());
            ps.setInt(THREE, chapter.getMarinesCount());
            ps.setString(FOUR, chapter.getWorld());
            ps.setInt(FIVE, chapterId);

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removeChapter(int chapterId) {
        Database db = Database.getInstance();
        Connection connection = db.getConnection();

        try (PreparedStatement ps = connection.prepareStatement("DELETE FROM chapter WHERE id = ?")) {
            ps.setInt(ONE, chapterId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int getChapterIdBySpaceMarineId(long spaceMarineId) {
        Database db = Database.getInstance();
        Connection connection = db.getConnection();
        int id = -1;
        try (PreparedStatement ps = connection.prepareStatement("select chapter_id from spacemarine where id = ?")) {
            ps.setLong(ONE, spaceMarineId);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                id = rs.getInt("chapter_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return id;
    }

}
