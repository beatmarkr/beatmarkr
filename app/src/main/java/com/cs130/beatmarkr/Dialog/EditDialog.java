package com.cs130.beatmarkr.Dialog;

import android.app.Activity;
import android.content.DialogInterface;

/**
 * Created by Justin on 5/6/15.
 */
public class EditDialog extends GenericDialog {
    private String[] editList = {"Adjust Time", "Rename", "Delete"};
    int position;

    public EditDialog(Activity a, int pos) {
        activity = a;
        position = pos;
    }

    @Override
    void setContents() {
        builder.setTitle("Edit")
                .setItems(editList, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                AdjustTimeDialog adjustTime = new AdjustTimeDialog(activity, position);
                                adjustTime.createDialog();
                                break;
                            case 1:
                                RenameDialog rename = new RenameDialog(activity, position);
                                rename.createDialog();
                                break;
                            case 2:
                                DeleteDialog delete = new DeleteDialog(activity, position);
                                delete.createDialog();
                                break;
                            default:
                                break;
                        }
                    }
                })
                .setNegativeButton("Cancel", cancelButton);
    }
}
