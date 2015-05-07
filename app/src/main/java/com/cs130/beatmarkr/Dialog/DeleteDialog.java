package com.cs130.beatmarkr.Dialog;

import android.app.Activity;
import android.content.DialogInterface;

/**
 * Created by Justin on 5/6/15.
 */
public class DeleteDialog extends GenericDialog {

    public DeleteDialog(Activity a) {
        activity = a;
    }

    @Override
    void configureDialog() {
        builder.setTitle("Delete")
                .setMessage("Are you sure?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Delete the bookmark
                    }
                })
                .setNegativeButton("No", cancelButton);
    }
}
