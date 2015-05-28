package com.cs130.beatmarkr.Dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.cs130.beatmarkr.BookmarksActivity;
import com.cs130.beatmarkr.R;

/**
 * Created by Justin on 5/6/15.
 */
public class RenameDialog extends GenericDialog {
    View view;
    int position;

    EditText bookmarkName;

    public RenameDialog(Activity a, int pos) {
        activity = a;
        position = pos;
    }

    public void setCustomView() {
        LayoutInflater inflater = activity.getLayoutInflater();
        view = inflater.inflate(R.layout.dialog_rename, null);

        // Get current bookmark name and display in input box
        bookmarkName = (EditText)view.findViewById(R.id.bookmarkName);
        String currName = ((BookmarksActivity)activity).getBmList().get(position).getDescription();
        bookmarkName.setText(currName);
        bookmarkName.setSelection(currName.length());
    }

    @Override
    void setContents() {
        builder.setTitle("Rename")
                .setView(view)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newDescription = bookmarkName.getText().toString();

                        if (newDescription.length() > 30) {
                            newDescription = newDescription.substring(0, 30);
                        }

                        ((BookmarksActivity)activity).getStorage().updateBookmarkEntry(
                                ((BookmarksActivity)activity).getBmList().get(position),
                                null,
                                newDescription);
                        ((BookmarksActivity)activity).getBmList().get(position).setDescription(newDescription);
                        ((BookmarksActivity)activity).update();
                    }
                })
                .setNegativeButton("Cancel", cancelButton);
    }
}
