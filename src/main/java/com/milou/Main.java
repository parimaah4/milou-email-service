package com.milou;

import com.milou.model.User;
import com.milou.service.UserService;
import com.milou.service.EmailService;
import com.milou.util.LoggerConfig;

import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static UserService userService = new UserService();
    private static EmailService emailService = new EmailService();
    private static User currentUser;

    public static void main(String[] args) {
        LoggerConfig.setupLogger();
        while (true) {
            System.out.print("[L]ogin, [S]ign up: ");
            String choice = scanner.nextLine().trim().toLowerCase();
            if (choice.equals("l") || choice.equals("login")) {
                login();
            } else if (choice.equals("s") || choice.equals("sign up")) {
                signup();
            } else {
                System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void login() {
        System.out.print("Email: ");
        String email = normalizeEmail(scanner.nextLine().trim());
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        User user = userService.login(email, password);
        if (user != null) {
            currentUser = user;
            System.out.println("Welcome back, " + user.getName() + "!");
            showUnreadEmails();
            userMenu();
        } else {
            System.out.println("Invalid email or password.");
        }
    }

    private static void signup() {
        System.out.print("Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Email: ");
        String email = normalizeEmail(scanner.nextLine().trim());
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        String result = userService.signup(name, email, password);
        if (result.equals("success")) {
            System.out.println("Your new account is created. Go ahead and login!");
        } else {
            System.out.println(result);
            signup();
        }
    }

    private static void userMenu() {
        while (true) {
            System.out.print("[S]end, [V]iew, [R]eply, [F]orward: ");
            String choice = scanner.nextLine().trim().toLowerCase();
            switch (choice) {
                case "s": case "send":
                    sendEmail();
                    break;
                case "v": case "view":
                    viewEmails();
                    break;
                case "r": case "reply":
                    replyEmail();
                    break;
                case "f": case "forward":
                    forwardEmail();
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void sendEmail() {
        System.out.print("Recipient(s): ");
        String recipientsStr = scanner.nextLine().trim();
        System.out.print("Subject: ");
        String subject = scanner.nextLine().trim();
        System.out.print("Body: ");
        String body = scanner.nextLine().trim();

        try {
            String code = emailService.sendEmail(currentUser.getId(), recipientsStr, subject, body);
            System.out.println("Successfully sent your email.");
            System.out.println("Code: " + code);
        } catch (Exception e) {
            System.out.println("Error sending email: " + e.getMessage());
        }
    }

    private static void viewEmails() {
        System.out.print("[A]ll emails, [U]nread emails, [S]ent emails, Read by [C]ode: ");
        String choice = scanner.nextLine().trim().toLowerCase();
        switch (choice) {
            case "a": case "all":
                showAllEmails();
                break;
            case "u": case "unread":
                showUnreadEmails();
                break;
            case "s": case "sent":
                showSentEmails();
                break;
            case "c": case "code":
                readByCode();
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private static void showUnreadEmails() {
        List<com.milou.model.Email> unread = emailService.getUnreadEmails(currentUser.getId());
        System.out.println("Unread Emails:");
        System.out.println(unread.size() + " unread emails:");
        for (com.milou.model.Email email : unread) {
            System.out.println("+ " + email.getSenderEmail() + " - " + email.getSubject() + " (" + email.getCode() + ")");
        }
    }

    private static void showAllEmails() {
        List<com.milou.model.Email> all = emailService.getAllReceivedEmails(currentUser.getId());
        System.out.println("All Emails:");
        for (com.milou.model.Email email : all) {
            System.out.println("+ " + email.getSenderEmail() + " - " + email.getSubject() + " (" + email.getCode() + ")");
        }
    }

    private static void showSentEmails() {
        List<com.milou.model.Email> sent = emailService.getSentEmails(currentUser.getId());
        System.out.println("Sent Emails:");
        for (com.milou.model.Email email : sent) {
            System.out.println("+ " + String.join(", ", email.getRecipientEmails()) + " - " + email.getSubject() + " (" + email.getCode() + ")");
        }
    }

    private static void readByCode() {
        System.out.print("Code: ");
        String code = scanner.nextLine().trim();
        com.milou.model.Email email = emailService.getEmailByCode(code, currentUser.getId());
        if (email != null) {
            System.out.println("Code: " + email.getCode());
            System.out.println("Recipient(s): " + String.join(", ", email.getRecipientEmails()));
            System.out.println("Subject: " + email.getSubject());
            System.out.println("Date: " + email.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            System.out.println();
            System.out.println(email.getBody());
            emailService.markAsRead(email.getId(), currentUser.getId());
        } else {
            System.out.println("You cannot read this email.");
        }
    }

    private static void replyEmail() {
        System.out.print("Code: ");
        String code = scanner.nextLine().trim();
        System.out.print("Body: ");
        String body = scanner.nextLine().trim();

        try {
            String newCode = emailService.replyEmail(code, currentUser.getId(), body);
            System.out.println("Successfully sent your reply to email " + code + ".");
            System.out.println("Code: " + newCode);
        } catch (Exception e) {
            System.out.println("Error replying: " + e.getMessage());
        }
    }

    private static void forwardEmail() {
        System.out.print("Code: ");
        String code = scanner.nextLine().trim();
        System.out.print("Recipient(s): ");
        String recipientsStr = scanner.nextLine().trim();

        try {
            String newCode = emailService.forwardEmail(code, currentUser.getId(), recipientsStr);
            System.out.println("Successfully forwarded your email.");
            System.out.println("Code: " + newCode);
        } catch (Exception e) {
            System.out.println("Error forwarding: " + e.getMessage());
        }
    }

    public static String normalizeEmail(String email) {
        if (!email.endsWith("@milou.com")) {
            email += "@milou.com";
        }
        return email;
    }
}