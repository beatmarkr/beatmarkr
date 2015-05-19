package com.cs130.beatmarkr.Dialog;

import android.app.Activity;
import android.content.DialogInterface;

import com.cs130.beatmarkr.BookmarksActivity;

/**
 * Created by Justin on 5/6/15.
 */
public class DeleteDialog extends GenericDialog {
    int position;

    public DeleteDialog(Activity a, int pos) {
        activity = a;
        position = pos;
    }

    @Override
    void setContents() {
        builder.setTitle("Delete")
                .setMessage("Are you sure?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((BookmarksActivity)activity).getBmList().remove(position);
                        ((BookmarksActivity)activity).update();
                    }
                })
                .setNegativeButton("No", cancelButton);
    }
}
