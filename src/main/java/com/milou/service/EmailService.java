package com.milou.service;

import com.milou.dao.EmailDAO;
import com.milou.dao.UserDAO;
import com.milou.model.Email;

import java.util.ArrayList;
import java.util.List;

public class EmailService {
    private EmailDAO emailDAO = new EmailDAO();
    private UserDAO userDAO = new UserDAO();

    public String sendEmail(int senderId, String recipientsStr, String subject, String body) {
        List<Integer> recipientIds = parseRecipients(recipientsStr);
        if (recipientIds.isEmpty()) {
            throw new IllegalArgumentException("Invalid recipients.");
        }
        if (subject.length() > 255) {
            throw new IllegalArgumentException("Subject too long.");
        }
        return emailDAO.createEmail(senderId, subject, body, recipientIds);
    }

    private List<Integer> parseRecipients(String recipientsStr) {
        List<Integer> ids = new ArrayList<>();
        String[] emails = recipientsStr.split(",");
        for (String email : emails) {
            email = com.milou.Main.normalizeEmail(email.trim());
            int id = userDAO.getUserIdByEmail(email);
            if (id != -1) {
                ids.add(id);
            }
        }
        return ids;
    }

    public List<Email> getUnreadEmails(int userId) {
        return emailDAO.getUnreadEmails(userId);
    }

    public List<Email> getAllReceivedEmails(int userId) {
        return emailDAO.getAllReceivedEmails(userId);
    }

    public List<Email> getSentEmails(int userId) {
        return emailDAO.getSentEmails(userId);
    }

    public Email getEmailByCode(String code, int userId) {
        return emailDAO.getEmailByCode(code, userId);
    }

    public void markAsRead(int emailId, int userId) {
        emailDAO.markAsRead(emailId, userId);
    }

    public String replyEmail(String code, int senderId, String body) {
        int emailId = emailDAO.getEmailIdByCode(code);
        if (emailId == -1) {
            throw new IllegalArgumentException("Invalid code.");
        }
        Email original = getEmailByCode(code, senderId);
        if (original == null) {
            throw new IllegalArgumentException("You cannot reply to this email.");
        }

        List<Integer> replyRecipients = new ArrayList<>();
        int originalSenderId = emailDAO.getSenderIdByEmailId(emailId);
        replyRecipients.add(originalSenderId);
        List<Integer> originalRecipients = emailDAO.getRecipientsIds(emailId);
        for (int recId : originalRecipients) {
            if (recId != senderId) {
                replyRecipients.add(recId);
            }
        }

        String newSubject = "[Re] " + original.getSubject();
        return emailDAO.createEmail(senderId, newSubject, body, replyRecipients);
    }

    public String forwardEmail(String code, int senderId, String recipientsStr) {
        int emailId = emailDAO.getEmailIdByCode(code);
        if (emailId == -1) {
            throw new IllegalArgumentException("Invalid code.");
        }
        Email original = getEmailByCode(code, senderId);
        if (original == null) {
            throw new IllegalArgumentException("You cannot forward this email.");
        }

        List<Integer> forwardRecipients = parseRecipients(recipientsStr);
        if (forwardRecipients.isEmpty()) {
            throw new IllegalArgumentException("Invalid recipients.");
        }

        String newSubject = "[Fw] " + original.getSubject();
        return emailDAO.createEmail(senderId, newSubject, original.getBody(), forwardRecipients);
    }
}
