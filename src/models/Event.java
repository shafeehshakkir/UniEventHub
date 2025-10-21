package models;

import db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Event {
    private int id;
    private int organizerId;
    private String title;
    private String description;
    private Date eventDate;

    public Event(int id, int organizerId, String title, String description, Date eventDate) {
        this.id = id;
        this.organizerId = organizerId;
        this.title = title;
        this.description = description;
        this.eventDate = eventDate;
    }

    public Event(int organizerId, String title, String description, Date eventDate) {
        this.organizerId = organizerId;
        this.title = title;
        this.description = description;
        this.eventDate = eventDate;
    }

    public int getId() {
        return id;
    }

    public int getOrganizerId() {
        return organizerId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public boolean save() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO events (organizer_id, title, description, event_date) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, this.organizerId);
            stmt.setString(2, this.title);
            stmt.setString(3, this.description);
            stmt.setDate(4, this.eventDate);
            int affected = stmt.executeUpdate();
            if (affected == 1) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    this.id = keys.getInt(1);
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<Event> getEventsByOrganizer(int organizerId) {
        List<Event> events = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM events WHERE organizer_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, organizerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                events.add(new Event(
                        rs.getInt("id"),
                        rs.getInt("organizer_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getDate("event_date")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    public static List<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM events ORDER BY event_date ASC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                events.add(new Event(
                        rs.getInt("id"),
                        rs.getInt("organizer_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getDate("event_date")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    public static boolean deleteEvent(int eventId, int organizerId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "DELETE FROM events WHERE id = ? AND organizer_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, eventId);
            stmt.setInt(2, organizerId);
            int affected = stmt.executeUpdate();
            return affected == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean registerStudentToEvent(int studentId, int eventId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO registrations (student_id, event_id) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, studentId);
            stmt.setInt(2, eventId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            // Could be duplicate registration - ignore or handle accordingly
            if (e.getErrorCode() == 1062) { // Duplicate entry
                return false;
            }
            e.printStackTrace();
        }
        return false;
    }

    public static List<Event> getEventsRegisteredByStudent(int studentId) {
        List<Event> events = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT e.* FROM events e " +
                    "JOIN registrations r ON e.id = r.event_id " +
                    "WHERE r.student_id = ? ORDER BY e.event_date ASC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                events.add(new Event(
                        rs.getInt("id"),
                        rs.getInt("organizer_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getDate("event_date")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }
}