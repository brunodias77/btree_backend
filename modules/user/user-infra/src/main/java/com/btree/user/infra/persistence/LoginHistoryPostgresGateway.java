package com.btree.user.infra.persistence;



import com.btree.user.domain.entity.LoginHistory;
import com.btree.user.domain.persistence.LoginHistoryGateway;
import com.btree.user.infra.model.LoginHistoryJpaEntity;
import com.btree.user.infra.repository.LoginHistoryJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class LoginHistoryPostgresGateway implements LoginHistoryGateway {

    private final LoginHistoryJpaRepository loginHistoryJpaRepository;

    public LoginHistoryPostgresGateway(final LoginHistoryJpaRepository loginHistoryJpaRepository) {
        this.loginHistoryJpaRepository = loginHistoryJpaRepository;
    }

    @Override
    public LoginHistory create(final LoginHistory loginHistory) {
        return loginHistoryJpaRepository
                .save(LoginHistoryJpaEntity.from(loginHistory))
                .toAggregate();
    }
}
