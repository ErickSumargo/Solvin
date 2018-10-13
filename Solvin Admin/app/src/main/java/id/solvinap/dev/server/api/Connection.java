package id.solvinap.dev.server.api;

import android.content.Context;

import id.solvinap.dev.server.helper.Global;
import id.solvinap.dev.server.helper.Session;
import id.solvinap.dev.server.interfaces.IAPIRequest;

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
 * Created by Erick Sumargo on 2/6/2017.
 */

public class Connection {
    private OkHttpClient.Builder okHttpClientBuilder;
    private HttpLoggingInterceptor httpLoggingInterceptor;
    private IAPIRequest iAPIRequest;
    private Retrofit retrofit;
    private Gson gson;

    private Request original, request;
    private Request.Builder requestBuilder;

    public Connection(final Context context) {
        final String token = Session.getInstance(context).getToken();
        final String header = String.format("%s %s %s", "SolvinAP", "admin",
                token.isEmpty() ? "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6LTEsIm5hbWUiOiJHdWVzdCJ9.YPBvng-UfdW9xipEBsVJB4ZEvzLAnc2hbh_6fDgkzVg" : token);

        okHttpClientBuilder = new OkHttpClient.Builder();
        okHttpClientBuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Interceptor.Chain chain) throws IOException {
                original = chain.request();
                requestBuilder = original.newBuilder().header("Authorization", header);
                request = requestBuilder.build();

                return chain.proceed(request);
            }
        });

        httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        okHttpClientBuilder.addInterceptor(httpLoggingInterceptor);

        gson = new GsonBuilder().setLenient().create();

        retrofit = new Retrofit.Builder()
                .baseUrl(Global.SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClientBuilder.build())
                .build();
        iAPIRequest = retrofit.create(IAPIRequest.class);
    }

    public static Connection getInstance(Context context) {
        return new Connection(context);
    }

    public IAPIRequest getiAPIRequest() {
        return iAPIRequest;
    }
}
