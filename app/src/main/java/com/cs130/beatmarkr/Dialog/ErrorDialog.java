package com.cs130.beatmarkr.Dialog;

import android.app.Activity;
import android.content.DialogInterface;

import com.cs130.beatmarkr.BookmarksActivity;

/**
 * Created by Justin on 5/25/15.
 */
public class ErrorDialog extends GenericDialog {
    String error_msg = "";

    public ErrorDialog(Activity a, String e) {
        activity = a;
        error_msg = e;
    }

    @Override
    void setContents() {
        builder.setMessage(error_msg)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                });
    }
}
