package spring.controller;

import spring.AutoWired;
import spring.service.UserService;

public class UserController {
    @AutoWired
    UserService userService;

    public UserService getUserService() {
        return userService;
    }
}
