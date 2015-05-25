package com.cs130.beatmarkr.Dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.cs130.beatmarkr.Bookmark;
import com.cs130.beatmarkr.BookmarksActivity;
import com.cs130.beatmarkr.R;

/**
 * Created by Justin on 5/19/15.
 */
public class NameDialog extends GenericDialog {
    View view;
    int position;

    EditText bookmarkName;

    public NameDialog(Activity a, int pos) {
        activity = a;
        position = pos;
    }

    public void setCustomView() {
        LayoutInflater inflater = activity.getLayoutInflater();
        view = inflater.inflate(R.layout.dialog_rename, null);
        bookmarkName = (EditText)view.findViewById(R.id.bookmarkName);
    }

    @Override
    void setContents() {
        builder.setTitle("Name")
                .setView(view)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newDescription = bookmarkName.getText().toString();
                        Bookmark bm = ((BookmarksActivity)activity).getBmList().get(position);
                        bm.setDescription(newDescription);
                        ((BookmarksActivity)activity).getStorage().addBookmarkEntry(bm);
                        ((BookmarksActivity)activity).update();

                        // Scroll the list to the bookmark's new position
                        for (Bookmark b : ((BookmarksActivity)activity).getBmList()) {
                            if (b.equals(bm)) {
                                position = ((BookmarksActivity)activity).getBmList().indexOf(b);
                                break;
                            }
                        }

                        ((BookmarksActivity)activity).getBmListView().setSelection(position);

                        // Continue playing music
                        ((BookmarksActivity)activity).play(activity.findViewById(android.R.id.content));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        ((BookmarksActivity)activity).getBmList().remove(position);
                        ((BookmarksActivity)activity).play(activity.findViewById(android.R.id.content));
                    }
                });
    }

    public void configureDialog() {
        editDialog.setCancelable(false);
    }
}
