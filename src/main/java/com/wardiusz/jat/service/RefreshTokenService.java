package com.wardiusz.jat.service;

import com.wardiusz.jat.entity.RefreshToken;

public interface RefreshTokenService {

    RefreshToken createRefreshToken(String username);

    RefreshToken verifyRefreshToken(String token);

    void deleteByToken(String token);

    void deleteByUser(String username);

}
