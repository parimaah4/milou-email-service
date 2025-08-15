package com.milou.service;

import com.milou.dao.UserDAO;
import com.milou.model.User;

public class UserService {
    private UserDAO userDAO = new UserDAO();

    public User login(String email, String password) {
        User user = userDAO.getUserByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public String signup(String name, String email, String password) {
        if (password.length() < 8) {
            return "Password must be at least 8 characters.";
        }
        if (userDAO.getUserByEmail(email) != null) {
            return "Email is already taken.";
        }
        boolean created = userDAO.createUser(name, email, password);
        return created ? "success" : "Error creating account.";
    }
}