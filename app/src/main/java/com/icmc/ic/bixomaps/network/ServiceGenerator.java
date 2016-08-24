package com.icmc.ic.bixomaps.network;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.stream.Format;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

/**
 * ServiceGenerator is our API/HTTP client generator.
 * It defines one method to create a basic REST adapter for a given class/interface
 * @author Caio Lopes
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

    private static Serializer serializer = new Persister(new Format(
            "<?xml version=\"1.0\" encoding= \"UTF-8\" ?>"));

    private static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(SimpleXmlConverterFactory.create(serializer));

    public static <T> T createService(Class<T> serviceClass) {
        Retrofit retrofit = builder.client(httpClient.build()).build();
        return retrofit.create(serviceClass);
    }
}
