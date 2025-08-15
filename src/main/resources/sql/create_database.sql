DROP DATABASE IF EXISTS milou;
CREATE DATABASE milou CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE milou;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE emails (
    id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(6) UNIQUE NOT NULL,
    sender_id INT NOT NULL,
    subject VARCHAR(255) NOT NULL,
    body TEXT NOT NULL,
    date DATETIME NOT NULL,
    FOREIGN KEY (sender_id) REFERENCES users(id)
);

CREATE TABLE email_recipients (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email_id INT NOT NULL,
    recipient_id INT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (email_id) REFERENCES emails(id),
    FOREIGN KEY (recipient_id) REFERENCES users(id)
);

INSERT INTO users (name, email, password) VALUES ('bahar', 'bahar@milou.com', 'bahar123');
INSERT INTO users (name, email, password) VALUES ('sara', 'sara@milou.com', 'sara1234');