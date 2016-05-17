package com.icmc.ic.bixomaps.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;

import com.icmc.ic.bixomaps.R;

/**
 * Dialogs calls
 * Created by caiolopes on 5/17/16.
 */
public class Dialogs {
    public static void add(Context context) {
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
