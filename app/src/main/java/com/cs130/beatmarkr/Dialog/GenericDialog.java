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
    AlertDialog editDialog;
    public DialogInterface.OnClickListener cancelButton = new CancelButton();

    public final void createDialog() {
        getBuilder();
        setCustomView();
        setContents();
        showDialog();
        configureDialog();
    }

    final void getBuilder() {
        builder = new AlertDialog.Builder(activity);
    }

    void setCustomView() {}

    abstract void setContents();

    final void showDialog() {
        editDialog = builder.create();
        editDialog.show();
    }

    void configureDialog() {}

    public class CancelButton implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialog, int id) {
            // do nothing
        }
    }
}
