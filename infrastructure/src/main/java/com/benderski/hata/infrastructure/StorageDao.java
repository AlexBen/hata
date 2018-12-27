package com.benderski.hata.infrastructure;

import com.benderski.hata.subscription.SubscriptionModel;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public interface StorageDao {
    SubscriptionModel createOrRetrieveSubscription(@NonNull Integer userId);
    @Nullable SubscriptionModel getProfile(@NonNull Integer userId);
}
