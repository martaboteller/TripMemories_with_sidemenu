package model;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {

    //Variables
    private static Retrofit retrofit = null;
    private static final String BASE_URL = "http://35.157.158.97/test/";
    //private static final String BASE_URL = "http://demo3840811.mockable.io/";
    //private static final String BASE_URL = "http://www.amock.io/api/";


    public static RestApiService getApiService() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(message -> Log.i("OkHttp", message));
        interceptor.level(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        if (retrofit == null) {

            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd")
                    .create();
            retrofit = new Retrofit
                    .Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

        }
        return retrofit.create(RestApiService.class);

    }
}
