package com.btree.user.domain.persistence;

import com.btree.user.domain.value_object.SocialUserProfile;

import java.util.Optional;

public interface SocialProviderGateway {
    Optional<SocialUserProfile> validateTokenAndGetProfile(String provider, String token);
}