package com.icmc.ic.bixomaps.network;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

/**
 * ServiceGenerator is our API/HTTP client generator.
 * It defines one method to create a basic REST adapter for a given class/interface
 * @author caiolopes
 */
public class ServiceGenerator {
    public static final String API_BASE_URL = "http://143.107.183.246:8080";

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
    /*private static Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
            .create();*/

    /*private static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson));*/

    private static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(SimpleXmlConverterFactory.create());

    public static <T> T createService(Class<T> serviceClass) {
        Retrofit retrofit = builder.client(httpClient.build()).build();
        return retrofit.create(serviceClass);
    }
}
