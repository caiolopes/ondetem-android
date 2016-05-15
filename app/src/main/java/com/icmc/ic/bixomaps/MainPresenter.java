package com.icmc.ic.bixomaps;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;

import com.icmc.ic.bixomaps.models.MessageRequest;
import com.icmc.ic.bixomaps.network.PoiClient;
import com.icmc.ic.bixomaps.network.ServiceGenerator;

import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.ResponseBody;
import rx.Observable;

/**
 * @author caiolopes
 */
public class MainPresenter {

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
        return client.getRecommendations(request);
    }

    public static void addDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_add_place, null);

        builder.setTitle(R.string.add_dialog_title)
                .setView(view)
                .setPositiveButton(R.string.action_add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
    }
}
