package id.solvin.dev.helper;

import android.content.Context;
import android.util.Log;

import id.solvin.dev.model.basic.Auth;
import id.solvin.dev.model.interfaces.IApiConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by edinofri on 01/12/2016.
 */

public class ConnectionAPI {
    private static ConnectionAPI instance;
    private static IApiConfig api;
    private static Retrofit retrofit;

    public static ConnectionAPI getInstance(Context context) {
        instance = new ConnectionAPI(context);
        return instance;
    }

    public IApiConfig getApi() {
        return api;
    }

    private ConnectionAPI(final Context context) {
        final String token = Session.with(context).getSessionToken();
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Interceptor.Chain chain) throws IOException {
                Request original = chain.request();
                Auth auth = null;
                if (Session.with(context).isSignIn()) {
                    auth = Session.with(context).getAuth();
                }
                String auth_type = auth == null ? "guest" : auth.getAuth_type();
                String _token = String.format("%s %s %s",
                        "Solvin", auth_type,
                        token.isEmpty() ? "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6LTEsIm5hbWUiOiJHdWVzdCJ9.YPBvng-UfdW9xipEBsVJB4ZEvzLAnc2hbh_6fDgkzVg" : token);
                Log.d("Token_Authorization", _token);
                Request.Builder requestBuilder = original.newBuilder()
                        .header("Authorization", _token);
                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });

        Gson gson = new GsonBuilder().setLenient()
                .create();

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient.addInterceptor(logging);
        retrofit = new Retrofit.Builder()
                .baseUrl(ConfigApp.get().API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
//                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())                 RxJava
                .client(httpClient.build())
                .build();
        api = retrofit.create(IApiConfig.class);
    }
}