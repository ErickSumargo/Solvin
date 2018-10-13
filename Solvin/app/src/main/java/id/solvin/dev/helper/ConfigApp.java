package id.solvin.dev.helper;

/**
 * Created by edinofri on 01/12/2016.
 */

public class ConfigApp {
    public  final String KEY_DATA_INTENT_EDIT_PERTANYAAN = "key.data.intent.pertanyaan";

//    public final String API_BASE_URL = "http://192.168.10.70/labs/re-solvin/public/api/";
//    public final String API_ASSETS_URL = "http://192.168.10.70/labs/re-solvin/public/images/";

    public final String API_BASE_URL = "http://192.168.100.7:81/api/";
    public final String API_ASSETS_URL = "http://192.168.100.7:81/images/";

//    public final String API_BASE_URL = "http://dev.solvin.id/api/";
//    public final String API_ASSETS_URL = "http://dev.solvin.id/images/";

    public final String KEY_DATA_INTENT = "key.data.intent";

    public final int MENTOR = 1;
    public final int STUDENT = 0;
    public static ConfigApp get(){
        return new ConfigApp();
    }
}
