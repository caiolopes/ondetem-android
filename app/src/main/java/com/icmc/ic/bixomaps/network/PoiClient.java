package com.icmc.ic.bixomaps.network;

import com.icmc.ic.bixomaps.models.EventRequest;
import com.icmc.ic.bixomaps.models.MessageRequest;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Interface for the PoI API
 * @author Caio Lopes
 */
public interface PoiClient {
    @POST("POIbroker/Adapt")
    Observable<ResponseBody> getRecommendations(@Body MessageRequest messageRequest);

    @POST("POIbroker/RegisterActions")
    Observable<Response<ResponseBody>> sendEvent(@Body EventRequest eventRequest);
}
