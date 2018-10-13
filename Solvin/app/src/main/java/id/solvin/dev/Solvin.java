package id.solvin.dev;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.facebook.stetho.Stetho;

import id.solvin.dev.realm.helper.Modules;
import io.realm.Realm;
import io.realm.RealmConfiguration;

import static com.uphyca.stetho_realm.RealmInspectorModulesProvider.builder;

/**
 * Created by edinofri on 26/02/2017.
 */

public class Solvin extends Application {
    //    HELPER
    private RealmConfiguration config;

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);

        config = new RealmConfiguration.Builder(this)
                .name("solvin.realm")
                .setModules(new Modules())
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);

        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(builder(this).build())
                        .build());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }
}