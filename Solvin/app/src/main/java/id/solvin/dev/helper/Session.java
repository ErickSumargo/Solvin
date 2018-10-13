package id.solvin.dev.helper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import id.solvin.dev.model.basic.Auth;
import id.solvin.dev.model.basic.Bank;
import id.solvin.dev.model.basic.Mentor;
import id.solvin.dev.model.basic.MobileNetwork;
import id.solvin.dev.model.basic.Paket;
import id.solvin.dev.model.basic.Payment;
import id.solvin.dev.model.basic.Student;
import id.solvin.dev.model.basic.Subject;
import id.solvin.dev.model.basic.Version;
import id.solvin.dev.view.activities.ActivitySignIn;
import id.solvin.dev.view.activities.ActivityWelcome;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by edinofri on 01/12/2016.
 */

public class Session {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    private final int PRIVATE_MODE = 0;

    private final String PREF_NAME = "solvin.auth";
    private final String KEY_LOGIN_TYPE = "solvin.auth.type";
    private final String KEY_AUTH_IS_SIGNIN = "solvin.auth.issignin";
    private final String KEY_AUTH_DATA = "solvin.auth.data";
    private final String KEY_IS_SKIP_INTRO = "solvin.intro";
    private final String KEY_BANK = "solvin.bank";
    private final String KEY_MOBILE_NETWORK = "solvin.mobile_network";
    private final String KEY_PACKAGE = "solvin.package";
    private final String KEY_MATERIAL = "solvin.material";
    private final String KEY_VERSION = "solvin.version";

    private final String KEY_CATEGORIES = "solvin.categories";
    private final String KEY_COUNT_NOTIFICATION = "solvin.notification.count";
    private final String KEY_PAYMENT = "solvin.notification.payment";

    public static final String KEY_SETTING_GET_NOTIFICATION = "solvin.settings.notification";
    public static final String KEY_SETTING_SOUND = "solvin.settings.sound";
    public static final String KEY_SETTING_VIBRATIONN = "solvin.settings.vibration";

    private final String KEY_TOKEN = "solvin.token";

    private Session(Context c) {
        this.context = c;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public static Session with(Context c) {
        return new Session(c);
    }

    public void createSessionLogin(Auth auth) {
        if (auth.getAuth_type().equals(Auth.AUTH_TYPE_STUDENT)) {
            editor.putString(KEY_AUTH_DATA, new Gson().toJson(Student.parseToStudent(auth)));
        } else {
            editor.putString(KEY_AUTH_DATA, new Gson().toJson(Mentor.parseToMentor(auth)));
        }
        editor.putBoolean(KEY_AUTH_IS_SIGNIN, true);
        editor.putString(KEY_LOGIN_TYPE, auth.getAuth_type());
        editor.putBoolean(KEY_IS_SKIP_INTRO, true);
        editor.commit();
    }

    public void createSessionToken(String token) {
        Log.d("Session", token);
        editor.putString(KEY_TOKEN, token);
        editor.commit();
    }

    public void createSessionBanks(List<Bank> bankList) {
        if (bankList != null) {
            editor.putString(KEY_BANK, new Gson().toJson(bankList));
            editor.commit();
        }
    }

    public void createSessionMobileNetworks(List<MobileNetwork> mobileNetworkList) {
        if (mobileNetworkList != null) {
            editor.putString(KEY_MOBILE_NETWORK, new Gson().toJson(mobileNetworkList));
            editor.commit();
        }
    }

    public void createSessionPackages(List<Paket> packageList) {
        if (packageList != null) {
            editor.putString(KEY_PACKAGE, new Gson().toJson(packageList));
            editor.commit();
        }
    }

    public void createSessionMaterial(List<Subject> subjectList) {
        if (subjectList != null) {
            editor.putString(KEY_MATERIAL, new Gson().toJson(subjectList));
            editor.commit();
        }
    }

    public void createSessionVersion(Version version) {
        editor.putString(KEY_VERSION, new Gson().toJson(version));
        editor.commit();
    }

    public void setSettings(boolean notification, boolean sound, boolean vibration) {
        editor.putBoolean(KEY_SETTING_GET_NOTIFICATION, notification);
        editor.putBoolean(KEY_SETTING_SOUND, sound);
        editor.putBoolean(KEY_SETTING_VIBRATIONN, vibration);
        editor.commit();
    }

    public void saveCountNotification(int count) {
        editor.putInt(KEY_COUNT_NOTIFICATION, count);
        editor.commit();
    }

    public void checkSignIn() {
        if (isSkipIntro()) {
            if (!isSignIn()) {
                redirectToSignIn();
            }
        } else {
            redirectToIntro();
        }
    }

    public void clearLoginSession() {
        List<Subject> tmpMaterial = getMaterials();
        List<Bank> tmpBank = getBanks();
        List<MobileNetwork> tmpMobileNetworks = getMobileNetworks();
        List<Paket> tmpPackage = getPackage();
        editor.clear();

        createSessionMaterial(tmpMaterial);
        createSessionBanks(tmpBank);
        createSessionMobileNetworks(tmpMobileNetworks);
        createSessionPackages(tmpPackage);

        editor.putBoolean(KEY_IS_SKIP_INTRO, true);
        editor.commit();
        Intent i = new Intent(context, ActivitySignIn.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(i);
    }

    public String getSessionToken() {
        return pref.getString(KEY_TOKEN, "");
    }

    public Payment getPayment() {
        return new Gson().fromJson(pref.getString(KEY_PAYMENT, ""), Payment.class);
    }

    public List<Bank> getBanks() {
        Type myListType = new TypeToken<List<Bank>>() {
        }.getType();
        List<Bank> myList = new ArrayList<>();
        if (pref.getString(KEY_BANK, null) != null) {
            myList = new Gson().fromJson(pref.getString(KEY_BANK, null), myListType);
        }
        return myList;
    }

    public List<MobileNetwork> getMobileNetworks() {
        Type myListType = new TypeToken<List<MobileNetwork>>() {
        }.getType();
        List<MobileNetwork> myList = new ArrayList<>();
        if (pref.getString(KEY_MOBILE_NETWORK, null) != null) {
            myList = new Gson().fromJson(pref.getString(KEY_MOBILE_NETWORK, null), myListType);
        }
        return myList;
    }

    public List<Paket> getPackage() {
        Type myListType = new TypeToken<List<Paket>>() {
        }.getType();
        List<Paket> myList = new ArrayList<>();
        if (pref.getString(KEY_PACKAGE, null) != null) {
            myList = new Gson().fromJson(pref.getString(KEY_PACKAGE, null), myListType);
        }
        return myList;
    }

    public int getCountNotification() {
        return pref.getInt(KEY_COUNT_NOTIFICATION, 0);
    }

    public int getLoginType() {
        return pref.getString(KEY_LOGIN_TYPE, null).equals(Auth.AUTH_TYPE_STUDENT) ? 0 : 1;
    }

    public boolean getSetting(String keySetting) {
        return pref.getBoolean(keySetting, true);
    }

    public boolean isSignIn() {
        return pref.getBoolean(KEY_AUTH_IS_SIGNIN, false);
    }

    public boolean isSkipIntro() {
        return pref.getBoolean(KEY_IS_SKIP_INTRO, false);
    }

    public Auth getAuth() {
        if (getLoginType() == ConfigApp.get().STUDENT) {
            return new Gson().fromJson(pref.getString(KEY_AUTH_DATA, null), Student.class);
        }
        return new Gson().fromJson(pref.getString(KEY_AUTH_DATA, null), Mentor.class);
    }

    public List<Subject> getMaterials() {
        Type myListType = new TypeToken<List<Subject>>() {
        }.getType();
        List<Subject> myList = new ArrayList<>();
        if (pref.getString(KEY_MATERIAL, null) != null) {
            myList = new Gson().fromJson(pref.getString(KEY_MATERIAL, null), myListType);
        }
        return myList;
    }

    private void redirectToSignIn() {
        Intent i = new Intent(context, ActivitySignIn.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(i);
    }

    private void redirectToIntro() {
        Intent i = new Intent(context, ActivityWelcome.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(i);
    }

    public Version getVersion() {
        Version version = new Gson().fromJson(pref.getString(KEY_VERSION, null), Version.class);
        return version == null ? new Version() : version;
    }
}