package com.icmc.ic.bixomaps.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.TextInputEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RatingBar;

import com.icmc.ic.bixomaps.R;
import com.icmc.ic.bixomaps.fragments.PlaceFragment;

/**
 * Dialogs calls
 * Created by caiolopes on 5/17/16.
 */
public class Dialogs {
    public static void addPlace(Context context) {
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

    public static void review(final PlaceFragment context, float rating) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context.getContext());

        final View view = LayoutInflater.from(context.getContext()).inflate(R.layout.dialog_review, null);
        RatingBar ratingBar = (RatingBar) view.findViewById(R.id.dialog_rating);
        ratingBar.setRating(rating);

        builder.setTitle(R.string.dialog_review_title)
                .setView(view)
                .setPositiveButton(R.string.action_send, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        float rating = ((RatingBar) view.findViewById(R.id.dialog_rating))
                                .getRating();
                        String comment = ((TextInputEditText)view.findViewById(R.id.dialog_comment))
                                .getText().toString();
                        context.sendReview(comment, rating);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        context.setRatingBarListener();
                        dialog.cancel();
                    }
                }).show();
    }
}
