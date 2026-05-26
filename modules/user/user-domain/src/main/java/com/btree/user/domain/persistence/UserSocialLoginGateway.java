package com.btree.user.domain.persistence;

import com.btree.user.domain.entity.UserSocialLogin;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserSocialLoginGateway {
    UserSocialLogin create(UserSocialLogin userSocialLogin);
    Optional<UserSocialLogin> findByProviderAndProviderUserId(String provider, String providerUserId);
    List<UserSocialLogin> findByUserId(UUID userId);
    void deleteByProviderAndProviderUserId(String provider, String providerUserId);
}
