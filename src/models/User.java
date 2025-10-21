package models;

import db.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class User {
    private int id;
    private String name;
    private String username;
    private String passwordHash;
    private String role; // 'student' or 'organizer'

    public User(int id, String name, String username, String passwordHash, String role) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public User(String name, String username, String password, String role) {
        this.name = name;
        this.username = username;
        this.passwordHash = hashPassword(password);
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    private String hashPassword(String password) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hashed = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashed) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing algorithm not found.");
        }
    }

    public boolean verifyPassword(String password) {
        return hashPassword(password).equals(this.passwordHash);
    }

    public boolean save() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String checkSql = "SELECT id FROM users WHERE username = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, this.username);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                return false; // Username already exists
            }

            String sql = "INSERT INTO users (name, username, password, role) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setString(1, this.name);
            stmt.setString(2, this.username);
            stmt.setString(3, this.passwordHash);
            stmt.setString(4, this.role);
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

    public static User login(String username, String password, String role) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM users WHERE username = ? AND role = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, role);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String storedHash = rs.getString("password");
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("username"),
                        storedHash,
                        rs.getString("role")
                );
                if (user.verifyPassword(password)) {
                    return user;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean deleteProfile() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "DELETE FROM users WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, this.id);
            int affected = stmt.executeUpdate();
            return affected == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}