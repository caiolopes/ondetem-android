package com.icmc.ic.bixomaps.network;

import android.location.Location;
import android.os.Build;

import com.icmc.ic.bixomaps.models.MessageRequest;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.ResponseBody;
import rx.Observable;

/**
 * @author caiolopes
 */
public class Api {

    public Observable<ResponseBody> getRecommendations(Location location, String category) {
        PoiClient client = ServiceGenerator.createService(PoiClient.class);
        MessageRequest request = new MessageRequest();
        MessageRequest.Recommend recommend = new MessageRequest.Recommend();
        MessageRequest.User user = new MessageRequest.User();
        user.setId(Build.SERIAL);
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        user.setDate(sdfDate.format(now));
        user.setLat("" + location.getLatitude());
        user.setLong("" + location.getLongitude());
        MessageRequest.Place place = new MessageRequest.Place();
        place.setCategory(category);
        recommend.setPlace(place);
        recommend.setUser(user);
        request.setRecommend(recommend);
        Serializer serializer = new Persister();
        Writer writer = new StringWriter();
        try {
            serializer.write(request, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return client.getRecommendations(request);
    }
}
