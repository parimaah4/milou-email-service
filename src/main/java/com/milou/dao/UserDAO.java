package com.milou.dao;

import com.milou.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAO {
    private Connection conn = DatabaseConnection.getConnection();

    public User getUserByEmail(String email) {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE email = ?");
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new User(rs.getInt("id"), rs.getString("name"), rs.getString("email"), rs.getString("password"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean createUser(String name, String email, String password) {
        try {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO users (name, email, password) VALUES (?, ?, ?)");
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, password);
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getUserEmailById(int id) {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT email FROM users WHERE id = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("email");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getUserIdByEmail(String email) {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT id FROM users WHERE email = ?");
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}