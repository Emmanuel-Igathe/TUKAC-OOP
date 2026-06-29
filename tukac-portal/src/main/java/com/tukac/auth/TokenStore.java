package com.tukac.auth;

import com.tukac.models.User;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenStore {
    private final Map<String, User> tokens = new ConcurrentHashMap<>();

    public String createToken(User user) {
        String token = UUID.randomUUID().toString();
        tokens.put(token, user);
        return token;
    }

    public User getUser(String token) {
        return (token != null) ? tokens.get(token) : null;
    }

    public void invalidate(String token) {
        if (token != null) tokens.remove(token);
    }
}
