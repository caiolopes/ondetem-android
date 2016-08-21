package com.icmc.ic.bixomaps.network;

import android.location.Location;
import android.os.Build;

import com.icmc.ic.bixomaps.models.EventRequest;
import com.icmc.ic.bixomaps.models.MessageRequest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.Observable;

/**
 * @author caiolopes
 */
public class Api {
    PoiClient mClient = ServiceGenerator.createService(PoiClient.class);

    public Observable<ResponseBody> getRecommendations(Location location, String category) {
        MessageRequest request = new MessageRequest();
        MessageRequest.Recommend recommend = new MessageRequest.Recommend();
        MessageRequest.User user = new MessageRequest.User();
        user.setId(Build.SERIAL);
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date now = new Date();
        user.setDate(sdfDate.format(now));
        user.setLat("" + location.getLatitude());
        user.setLong("" + location.getLongitude());
        MessageRequest.Place place = new MessageRequest.Place();
        place.setCategory(category);
        recommend.setPlace(place);
        recommend.setUser(user);
        request.setRecommend(recommend);
        return mClient.getRecommendations(request);
    }

    public Observable<Response<ResponseBody>> sendEvent(String placeId, String placeCategory,
                                                        String eventType, Float rating,
                                                        String comment,
                                                        Double latitude,
                                                        Double longitude) {
        EventRequest.Notify notify = new EventRequest.Notify();
        EventRequest.Event event = new EventRequest.Event();
        event.setType(eventType);
        notify.setEvent(event);
        EventRequest.User user = new EventRequest.User();
        user.setId(Build.SERIAL);
        Date now = new Date();
        user.setDate(String.valueOf(now.getTime()/1000L));
        user.setLat(String.valueOf(latitude));
        user.setLng(String.valueOf(longitude));
        notify.setUser(user);
        EventRequest.Place place = new EventRequest.Place();
        place.setCategory(placeCategory);
        place.setId(placeId);
        notify.setPlace(place);
        if (comment != null && rating != null) {
            EventRequest.Review review = new EventRequest.Review();
            review.setText(comment);
            // TODO: option for english
            review.setLanguage("pt");
            review.setRating(String.valueOf(rating));
            place.setReview(review);
        }
        EventRequest request = new EventRequest();
        request.setNotify(notify);
        return mClient.sendEvent(request);
    }
}
