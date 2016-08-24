package com.icmc.ic.bixomaps.views;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.design.widget.TextInputEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RatingBar;

import com.icmc.ic.bixomaps.R;
import com.icmc.ic.bixomaps.fragments.PlaceFragment;

/**
 * Dialogs calls
 * @author Caio Lopes
 */
public class Dialogs {
    public static void review(final PlaceFragment context, float rating) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context.getContext());

        final View dialogView = LayoutInflater.from(context.getContext()).inflate(R.layout.dialog_review, null);
        RatingBar ratingBar = (RatingBar) dialogView.findViewById(R.id.dialog_rating);
        ratingBar.setRating(rating);

        final AlertDialog dialog = builder.setTitle(R.string.dialog_review_title)
                .setView(dialogView)
                .setPositiveButton(R.string.action_send, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        context.setRatingBarListener();
                        dialog.cancel();
                    }
                }).create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float rating = ((RatingBar) dialogView.findViewById(R.id.dialog_rating))
                        .getRating();
                String comment = ((TextInputEditText)dialogView.findViewById(R.id.dialog_comment))
                        .getText().toString();
                if (comment.length() > 0) {
                    context.sendReview(comment, rating);
                    dialog.dismiss();
                } else {
                    ((TextInputEditText)dialogView
                            .findViewById(R.id.dialog_comment))
                            .setError(context.getString(R.string.empty_review));
                }
            }
        });
    }
}
