package com.btree.user.domain.persistence;

import com.btree.user.domain.entity.LoginHistory;

public interface LoginHistoryGateway {
    LoginHistory create(LoginHistory loginHistory);
}
