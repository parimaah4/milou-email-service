package com.milou.model;

import java.time.LocalDateTime;
import java.util.List;

public class Email {
    private int id;
    private String code;
    private int senderId;
    private String senderEmail;
    private String subject;
    private String body;
    private LocalDateTime date;
    private List<String> recipientEmails;

    public Email(int id, String code, int senderId, String senderEmail, String subject, String body, LocalDateTime date, List<String> recipientEmails) {
        this.id = id;
        this.code = code;
        this.senderId = senderId;
        this.senderEmail = senderEmail;
        this.subject = subject;
        this.body = body;
        this.date = date;
        this.recipientEmails = recipientEmails;
    }

    public int getId() { return id; }
    public String getCode() { return code; }
    public String getSenderEmail() { return senderEmail; }
    public String getSubject() { return subject; }
    public String getBody() { return body; }
    public LocalDateTime getDate() { return date; }
    public List<String> getRecipientEmails() { return recipientEmails; }
}