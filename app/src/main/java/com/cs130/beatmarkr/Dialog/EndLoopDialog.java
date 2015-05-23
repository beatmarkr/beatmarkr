package com.cs130.beatmarkr.Dialog;

import android.app.Activity;
import android.content.DialogInterface;

import com.cs130.beatmarkr.Bookmark;
import com.cs130.beatmarkr.BookmarksActivity;
import com.cs130.beatmarkr.SetLoopAdapter;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Justin on 5/22/15.
 */
public class EndLoopDialog extends GenericDialog {
    SetLoopAdapter setLoopAdt;
    BookmarksActivity bmActivity;
    Bookmark bmStart;
    ArrayList<Bookmark> bmEndList;

    public EndLoopDialog(Activity a, Bookmark bm) {
        activity = a;
        bmActivity = (BookmarksActivity)activity;
        bmStart = bm;
    }

    public void setCustomView() {
        bmEndList = (ArrayList<Bookmark>)bmActivity.getBmList().clone();

        Iterator i = bmEndList.iterator();

        while (i.hasNext()) {
            Bookmark bm = (Bookmark)i.next();

            if (bm.getSeekTime() < bmStart.getSeekTime()) {
                i.remove();
            }
        }

        setLoopAdt = new SetLoopAdapter(activity, bmEndList);
    }

    @Override
    void setContents() {
        builder.setTitle("Set as end of loop")
                .setAdapter(setLoopAdt, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        bmActivity.setBmLoopEnd(bmEndList.get(which));
                    }
                });
    }

    public void configureDialog() {
        editDialog.setCancelable(false);
    }
}
