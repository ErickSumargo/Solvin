package id.solvinap.dev.server.helper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import id.solvinap.dev.server.data.DataAuth;
import id.solvinap.dev.server.data.DataPackage;
import id.solvinap.dev.server.data.DataPrimary;
import id.solvinap.dev.server.data.DataSubject;
import id.solvinap.dev.view.activity.ActivityLogin;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Erick Sumargo on 2/14/2017.
 */

public class Session {
    //    HELPER
    private Context context;
    private Type type;

    private Intent intent;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    //    OBJECT
    private List<DataSubject> materialList;
    private List<DataPackage> packageList;

    public Session(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(Global.PREFERENCES_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static Session getInstance(Context context) {
        return new Session(context);
    }

    public void saveDataPrimary(DataPrimary dataPrimary) {
        saveCategoryList(dataPrimary.getMaterialList());
        savePackageList(dataPrimary.getPackageList());
    }

    public void saveCategoryList(List<DataSubject> materialList) {
        editor.putString(Global.PREFERENCES_CATEGORY, new Gson().toJson(materialList));
        editor.commit();
    }

    public void savePackageList(List<DataPackage> packageList) {
        editor.putString(Global.PREFERENCES_PACKAGE, new Gson().toJson(packageList));
        editor.commit();
    }

    public void saveLoginData(DataAuth dataAuth) {
        editor.putString(Global.ADMIN, new Gson().toJson(dataAuth));
        editor.putBoolean(Global.LOGGED_IN, true);
        editor.commit();
    }

    public void saveToken(String token) {
        editor.putString(Global.TOKEN, token);
        editor.commit();
    }

    public void saveNotificationCount(int count) {
        editor.putInt(Global.NOTIFICATION_COUNT, count);
        editor.commit();
    }

    public boolean loggedIn() {
        return sharedPreferences.getBoolean(Global.LOGGED_IN, false);
    }

    public String getToken() {
        return sharedPreferences.getString(Global.TOKEN, "");
    }

    public List<DataSubject> getCategoryList() {
        type = new TypeToken<List<DataSubject>>() {
        }.getType();
        materialList = new ArrayList<>();
        if (sharedPreferences.getString(Global.PREFERENCES_CATEGORY, null) != null) {
            materialList = new Gson().fromJson(sharedPreferences.getString(Global.PREFERENCES_CATEGORY, null), type);
        }
        return materialList;
    }

    public List<DataPackage> getPackageList() {
        type = new TypeToken<List<DataPackage>>() {
        }.getType();
        packageList = new ArrayList<>();
        if (sharedPreferences.getString(Global.PREFERENCES_PACKAGE, null) != null) {
            packageList = new Gson().fromJson(sharedPreferences.getString(Global.PREFERENCES_PACKAGE, null), type);
        }
        return packageList;
    }

    public int getNotificationCount() {
        return sharedPreferences.getInt(Global.NOTIFICATION_COUNT, 0);
    }

    public void clearLoginData() {
        materialList = getCategoryList();
        packageList = getPackageList();
        editor.clear();

        saveCategoryList(materialList);
        savePackageList(packageList);

        intent = new Intent(context, ActivityLogin.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
}