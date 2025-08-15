package com.milou.dao;

import com.milou.model.Email;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EmailDAO {
    private Connection conn = DatabaseConnection.getConnection();

    public String createEmail(int senderId, String subject, String body, List<Integer> recipientIds) {
        try {
            String code = com.milou.util.CodeGenerator.generateCode();
            while (codeExists(code)) {
                code = com.milou.util.CodeGenerator.generateCode();
            }

            PreparedStatement ps = conn.prepareStatement("INSERT INTO emails (code, sender_id, subject, body, date) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, code);
            ps.setInt(2, senderId);
            ps.setString(3, subject);
            ps.setString(4, body);
            ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            int emailId = -1;
            if (rs.next()) {
                emailId = rs.getInt(1);
            }

            for (int recipientId : recipientIds) {
                PreparedStatement psRec = conn.prepareStatement("INSERT INTO email_recipients (email_id, recipient_id) VALUES (?, ?)");
                psRec.setInt(1, emailId);
                psRec.setInt(2, recipientId);
                psRec.executeUpdate();
            }

            return code;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean codeExists(String code) {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM emails WHERE code = ?");
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    public List<Email> getUnreadEmails(int userId) {
        return getEmails("WHERE er.recipient_id = ? AND er.is_read = FALSE", userId);
    }

    public List<Email> getAllReceivedEmails(int userId) {
        return getEmails("WHERE er.recipient_id = ? ", userId);
    }

    public List<Email> getSentEmails(int userId) {
        return getEmails("WHERE e.sender_id = ? ", userId, true);
    }

    private List<Email> getEmails(String whereClause, int userId) {
        return getEmails(whereClause, userId, false);
    }

    private List<Email> getEmails(String whereClause, int userId, boolean isSent) {
        List<Email> emails = new ArrayList<>();
        try {
            String sql = "SELECT e.id, e.code, e.sender_id, u.email as sender_email, e.subject, e.body, e.date " +
                    "FROM emails e " +
                    "JOIN users u ON e.sender_id = u.id " +
                    (isSent ? "" : "JOIN email_recipients er ON e.id = er.email_id ") +
                    whereClause +
                    " ORDER BY e.date DESC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int emailId = rs.getInt("id");
                List<String> recipients = getRecipients(emailId);
                emails.add(new Email(
                        emailId,
                        rs.getString("code"),
                        rs.getInt("sender_id"),
                        rs.getString("sender_email"),
                        rs.getString("subject"),
                        rs.getString("body"),
                        rs.getTimestamp("date").toLocalDateTime(),
                        recipients
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return emails;
    }

    private List<String> getRecipients(int emailId) {
        List<String> recipients = new ArrayList<>();
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT u.email FROM email_recipients er JOIN users u ON er.recipient_id = u.id WHERE er.email_id = ?");
            ps.setInt(1, emailId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                recipients.add(rs.getString("email"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return recipients;
    }

    public Email getEmailByCode(String code, int userId) {
        try {
            String sql = "SELECT e.id, e.code, e.sender_id, u.email as sender_email, e.subject, e.body, e.date " +
                    "FROM emails e " +
                    "JOIN users u ON e.sender_id = u.id " +
                    "WHERE e.code = ? AND (e.sender_id = ? OR EXISTS (SELECT 1 FROM email_recipients er WHERE er.email_id = e.id AND er.recipient_id = ?))";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, code);
            ps.setInt(2, userId);
            ps.setInt(3, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int emailId = rs.getInt("id");
                List<String> recipients = getRecipients(emailId);
                return new Email(
                        emailId,
                        rs.getString("code"),
                        rs.getInt("sender_id"),
                        rs.getString("sender_email"),
                        rs.getString("subject"),
                        rs.getString("body"),
                        rs.getTimestamp("date").toLocalDateTime(),
                        recipients
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void markAsRead(int emailId, int userId) {
        try {
            PreparedStatement ps = conn.prepareStatement("UPDATE email_recipients SET is_read = TRUE WHERE email_id = ? AND recipient_id = ?");
            ps.setInt(1, emailId);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getEmailIdByCode(String code) {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT id FROM emails WHERE code = ?");
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public List<Integer> getRecipientsIds(int emailId) {
        List<Integer> ids = new ArrayList<>();
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT recipient_id FROM email_recipients WHERE email_id = ?");
            ps.setInt(1, emailId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ids.add(rs.getInt("recipient_id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ids;
    }

    public int getSenderIdByEmailId(int emailId) {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT sender_id FROM emails WHERE id = ?");
            ps.setInt(1, emailId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("sender_id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}
