package id.solvinap.dev.app;

import android.app.Application;
import android.support.multidex.MultiDex;

import id.solvinap.dev.realm.helper.Modules;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Erick Sumargo on 3/21/2017.
 */

public class SolvinAP extends Application {
    //    HELPER
    private RealmConfiguration config;

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);

        config = new RealmConfiguration.Builder(this)
                .name("solvinap.realm")
                .setModules(new Modules())
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
    }
}