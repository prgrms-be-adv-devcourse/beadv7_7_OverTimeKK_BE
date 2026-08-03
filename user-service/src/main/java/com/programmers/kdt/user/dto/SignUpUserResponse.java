package com.programmers.kdt.user.dto;

import com.programmers.kdt.user.entity.User;
import com.programmers.kdt.user.entity.UserStatus;

public record SignUpUserResponse(
        Long userId,
        String email,
        String username,
        UserStatus status
) {
    public static SignUpUserResponse from(User user) {
        return new SignUpUserResponse(user.getUserId(), user.getEmail(), user.getUsername(), user.getStatus());
    }
}
