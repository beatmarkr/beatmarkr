package com.cs130.beatmarkr.Dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import com.cs130.beatmarkr.BookmarksActivity;
import com.cs130.beatmarkr.R;

import java.util.concurrent.TimeUnit;

/**
 * Created by Justin on 5/6/15.
 */
public class AdjustTimeDialog extends GenericDialog {
    View view;
    int position;

    NumberPicker minutesNp;
    NumberPicker secondsNp;
    NumberPicker millisecondsNp;

    public AdjustTimeDialog(Activity a, int pos) {
        activity = a;
        position = pos;
    }

    public void setCustomView() {
        LayoutInflater inflater = activity.getLayoutInflater();
        view = inflater.inflate(R.layout.dialog_adjust_time, null);

        minutesNp = (NumberPicker)view.findViewById(R.id.minutes);
        secondsNp = (NumberPicker)view.findViewById(R.id.seconds);
        millisecondsNp = (NumberPicker)view.findViewById(R.id.milliseconds);

        // Scroll the milliseconds in intervals of 50
        String[] ms_values = new String[20];
        for(int i = 0; i < ms_values.length; i++){
            ms_values[i] = Integer.toString(i*50);
        }

        // Set the minimum and maximum values of the number picker
        long finalTime = ((BookmarksActivity)activity).getFinalTime();

        int MIN_VALUE = 0;
        int MINUTE_MAX_VALUE = (int)TimeUnit.MILLISECONDS.toMinutes(finalTime);
        int SEC_MAX_VALUE = 59;

        minutesNp.setMinValue(MIN_VALUE);
        minutesNp.setMaxValue(MINUTE_MAX_VALUE);

        secondsNp.setMinValue(MIN_VALUE);
        secondsNp.setMaxValue(SEC_MAX_VALUE);

        millisecondsNp.setMinValue(MIN_VALUE);
        millisecondsNp.setMaxValue(ms_values.length - 1);
        millisecondsNp.setDisplayedValues(ms_values);

        // Display the current bookmark time in the number picker
        long currTime = ((BookmarksActivity)activity).getBmList().get(position).getSeekTime();

        int min = (int)TimeUnit.MILLISECONDS.toMinutes(currTime);
        int sec = (int)(TimeUnit.MILLISECONDS.toSeconds(currTime) - TimeUnit.MINUTES.toSeconds(min));
        int msec = (int)(currTime - TimeUnit.MINUTES.toMillis(min) - TimeUnit.SECONDS.toMillis(sec));

        minutesNp.setValue(min);
        secondsNp.setValue(sec);
        millisecondsNp.setValue(msec);
    }

    @Override
    void setContents() {
        builder.setTitle("Adjust Time")
                .setView(view)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        long newTime = TimeUnit.MINUTES.toMillis(minutesNp.getValue());
                        newTime += TimeUnit.SECONDS.toMillis(secondsNp.getValue());
                        newTime += millisecondsNp.getValue();

                        ((BookmarksActivity)activity).getBmList().get(position).setSeekTime(newTime);
                        ((BookmarksActivity)activity).update();
                    }
                })
                .setNegativeButton("Cancel", cancelButton);
    }
}
