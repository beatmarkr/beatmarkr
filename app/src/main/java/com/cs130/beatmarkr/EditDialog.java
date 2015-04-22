package com.cs130.beatmarkr;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

public class EditDialog extends DialogFragment {
    private String[] editList = {"Adjust Time", "Rename", "Delete"};
    private AlertDialog editDialog;
    private DialogInterface.OnClickListener cancelButton = new CancelButton();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Edit")
                .setItems(editList, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                adjustTimeDialog();
                                break;
                            case 1:
                                renameDialog();
                                break;
                            case 2:
                                deleteDialog();
                                break;
                            default:
                                break;
                        }
                    }
                })
                .setNegativeButton("Cancel", cancelButton);

        // Create the AlertDialog object and return it
        editDialog = builder.create();
        return editDialog;
    }

    private void adjustTimeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_adjust_time, null);

        configureNumberPicker(view);

        builder.setTitle("Adjust Time")
                .setView(view)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Store the new bookmark time
                    }
                })
               .setNegativeButton("Cancel", cancelButton);

        editDialog = builder.create();
        editDialog.show();
    }

    private void renameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_rename, null);

        // Get current bookmark name and display in input box
        EditText bookmarkName = (EditText)view.findViewById(R.id.bookmarkName);
        String currName = "current bookmark name";
        bookmarkName.setText(currName);
        bookmarkName.setSelection(currName.length());

        builder.setTitle("Rename")
                .setView(view)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Store the new bookmark name
                    }
                })
                .setNegativeButton("Cancel", cancelButton);

        editDialog = builder.create();
        editDialog.show();
    }

    private void deleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Delete")
                .setMessage("Are you sure?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Delete the bookmark
                    }
                })
                .setNegativeButton("No", cancelButton);

        editDialog = builder.create();
        editDialog.show();
    }

    private void configureNumberPicker(View v) {
        NumberPicker minutesNp = (NumberPicker)v.findViewById(R.id.minutes);
        NumberPicker secondsNp = (NumberPicker)v.findViewById(R.id.seconds);
        NumberPicker millisecondsNp = (NumberPicker)v.findViewById(R.id.milliseconds);

        int MAX_VALUE = 59;
        int MIN_VALUE = 0;

        minutesNp.setMaxValue(MAX_VALUE);
        minutesNp.setMinValue(MIN_VALUE);
        secondsNp.setMaxValue(MAX_VALUE);
        secondsNp.setMinValue(MIN_VALUE);
        millisecondsNp.setMaxValue(MAX_VALUE);
        millisecondsNp.setMinValue(MIN_VALUE);

        // Get current bookmark time and set it
        //minutesNp.setValue(0);
        //secondsNp.setValue(0);
        //millisecondsNp.setValue(0);
    }

    private class CancelButton implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialog, int id) {
            // do nothing
        }
    }
}
