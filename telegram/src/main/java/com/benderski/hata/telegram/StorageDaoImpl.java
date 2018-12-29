package com.benderski.hata.telegram;

import com.benderski.hata.infrastructure.StorageDao;
import com.benderski.hata.subscription.SubscriptionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.telegram.abilitybots.api.db.DBContext;

import javax.annotation.PreDestroy;
import java.util.Map;

@Service
public class StorageDaoImpl implements StorageDao {

    private static final String SUBSCRIPTION = "SUB";

    @Autowired
    private DBContext dbContext;

    @Override
    public SubscriptionModel createOrRetrieveSubscription(@NonNull Integer userId) {
        Map<Integer, SubscriptionModel> map = dbContext.getMap(SUBSCRIPTION);
        final boolean hasId = map.containsKey(userId);
        if(hasId) {
            return map.get(userId);
        }
        SubscriptionModel model = new SubscriptionModel();
        map.put(userId, model);
        dbContext.commit();
        return model;
    }

    @Nullable
    @Override
    public SubscriptionModel getProfile(@NonNull Integer userId) {
        Map<Integer, SubscriptionModel> map = dbContext.getMap(SUBSCRIPTION);
        return map.get(userId);
    }

    @Override
    public void commit() {
        dbContext.commit();
    }

    @Override
    public SubscriptionModel updateProfile(Integer userId, SubscriptionModel model) {
        Map<Integer, SubscriptionModel> map = dbContext.getMap(SUBSCRIPTION);
        map.put(userId, model);
        //dbContext.commit();
        return map.get(userId);
    }

    @PreDestroy
    public void preDestroy() {
        commit();
    }

    public void setDbContext(DBContext dbContext) {
        this.dbContext = dbContext;
    }
}
