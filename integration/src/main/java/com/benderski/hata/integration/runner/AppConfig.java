package com.benderski.hata.integration.runner;

import com.benderski.hata.infrastructure.Apartment;
import com.benderski.hata.telegram.dialog.DialogFlow;
import com.benderski.hata.telegram.dialog.SubscriptionDialogFactory;
import io.reactivex.subjects.ReplaySubject;
import io.reactivex.subjects.Subject;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.telegram.abilitybots.api.db.DBContext;
import org.telegram.abilitybots.api.db.MapDBContext;


@Configuration
public class AppConfig {

    @Bean(name = "apartmentStorageObservable")
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public Subject<Apartment> createPublishSubject() {
        return ReplaySubject.createWithSize(20);
    }

    @Bean(name = "dbContext")
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public DBContext createDbContext() {
        DB db = DBMaker
                .tempFileDB()
                .fileMmapEnableIfSupported()
                .closeOnJvmShutdown()
                .transactionEnable()
                .make();

        return new MapDBContext(db);
    }

    @Bean(name = "createSubscriptionDialog")
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public DialogFlow createSubscriptionDialog() {
        return SubscriptionDialogFactory.createSubscriptionDialog();
    }
}
