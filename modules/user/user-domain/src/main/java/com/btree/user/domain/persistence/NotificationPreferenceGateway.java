package com.btree.user.domain.persistence;

import com.btree.user.domain.entity.NotificationPreference;

public interface NotificationPreferenceGateway {
    NotificationPreference create(NotificationPreference preference);
}
