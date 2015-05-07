package com.cs130.beatmarkr.Dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.cs130.beatmarkr.R;

/**
 * Created by Justin on 5/6/15.
 */
public class RenameDialog extends GenericDialog {
    View view;

    public RenameDialog(Activity a) {
        activity = a;
    }

    public void setCustomView() {
        LayoutInflater inflater = activity.getLayoutInflater();
        view = inflater.inflate(R.layout.dialog_rename, null);

        // Get current bookmark name and display in input box
        EditText bookmarkName = (EditText)view.findViewById(R.id.bookmarkName);
        String currName = "current bookmark name";
        bookmarkName.setText(currName);
        bookmarkName.setSelection(currName.length());
    }

    @Override
    void configureDialog() {
        builder.setTitle("Rename")
                .setView(view)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Store the new bookmark name
                    }
                })
                .setNegativeButton("Cancel", cancelButton);
    }
}
