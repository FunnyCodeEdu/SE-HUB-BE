package com.se.hub.modules.user.service;

import com.se.hub.modules.user.dto.response.UserInfoResponse;

public interface TokenService {
    UserInfoResponse getCurrentUserInfo();
}

