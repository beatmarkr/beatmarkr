package com.cs130.beatmarkr.Dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

/**
 * Created by Justin on 5/6/15.
 */
public abstract class GenericDialog {
    Activity activity;
    AlertDialog.Builder builder;
    public DialogInterface.OnClickListener cancelButton = new CancelButton();

    public final void createDialog() {
        getBuilder();
        setCustomView();
        configureDialog();
        showDialog();
    }

    abstract void configureDialog();

    final void getBuilder() {
        builder = new AlertDialog.Builder(activity);
    }

    void setCustomView() {}

    final void showDialog() {
        AlertDialog editDialog = builder.create();
        editDialog.show();
    }

    public class CancelButton implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialog, int id) {
            // do nothing
        }
    }
}
