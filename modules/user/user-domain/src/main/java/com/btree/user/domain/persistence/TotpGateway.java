package com.btree.user.domain.persistence;

public interface TotpGateway {
    String generateSecret();
    String getUriForImage(String secret, String accountName, String issuer);
    boolean isValidCode(String secret, String code);
}
