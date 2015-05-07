package com.cs130.beatmarkr.Dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import com.cs130.beatmarkr.R;

/**
 * Created by Justin on 5/6/15.
 */
public class AdjustTimeDialog extends GenericDialog {
    View view;

    public AdjustTimeDialog(Activity a) {
        activity = a;
    }

    public void setCustomView() {
        LayoutInflater inflater = activity.getLayoutInflater();
        view = inflater.inflate(R.layout.dialog_adjust_time, null);

        NumberPicker minutesNp = (NumberPicker)view.findViewById(R.id.minutes);
        NumberPicker secondsNp = (NumberPicker)view.findViewById(R.id.seconds);
        NumberPicker millisecondsNp = (NumberPicker)view.findViewById(R.id.milliseconds);

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

    @Override
    void configureDialog() {
        builder.setTitle("Adjust Time")
                .setView(view)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Store the new bookmark time
                    }
                })
                .setNegativeButton("Cancel", cancelButton);
    }
}
