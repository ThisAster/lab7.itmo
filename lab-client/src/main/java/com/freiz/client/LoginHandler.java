package com.freiz.client;

import com.freiz.common.data.User;

public final class LoginHandler {
    private static User user;
    private LoginHandler() {
    }

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        LoginHandler.user = user;
    }
}
