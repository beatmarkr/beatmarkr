package com.cs130.beatmarkr.Dialog;

import android.app.Activity;
import android.content.DialogInterface;

import com.cs130.beatmarkr.Bookmark;
import com.cs130.beatmarkr.BookmarksActivity;
import com.cs130.beatmarkr.SetLoopAdapter;

/**
 * Created by Justin on 5/22/15.
 */
public class StartLoopDialog extends GenericDialog {
    SetLoopAdapter setLoopAdt;
    BookmarksActivity bmActivity;

    public StartLoopDialog(Activity a) {
        activity = a;
        bmActivity = (BookmarksActivity)activity;
    }

    public void setCustomView() {
        Bookmark bmStart = bmActivity.getBmLoopStart();
        setLoopAdt = new SetLoopAdapter(activity, bmActivity.getBmList(), bmStart);
    }

    @Override
    void setContents() {
        builder.setTitle("Set as start of loop")
                .setAdapter(setLoopAdt, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Bookmark bmStart = bmActivity.getBmList().get(which);
                        bmActivity.setBmLoopStart(bmStart);

                        EndLoopDialog endLoop = new EndLoopDialog(activity, bmStart);
                        endLoop.createDialog();
                    }
                });
    }

    public void configureDialog() {
        editDialog.setCancelable(false);
    }
}
