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

        // Set the minimum and maximum values of the number picker
        long finalTime = ((BookmarksActivity)activity).getFinalTime();

        int MIN_VALUE = 0;

        int MINUTE_MAX_VALUE = (int)TimeUnit.MILLISECONDS.toMinutes(finalTime);
        int SEC_MAX_VALUE = 59;
        int MS_MAX_VALUE = 999;

        minutesNp.setMinValue(MIN_VALUE);
        minutesNp.setMaxValue(MINUTE_MAX_VALUE);

        secondsNp.setMinValue(MIN_VALUE);
        secondsNp.setMaxValue(SEC_MAX_VALUE);

        millisecondsNp.setMinValue(MIN_VALUE);
        millisecondsNp.setMaxValue(MS_MAX_VALUE);

        // Display the current bookmark time in the number picker
        long currTime = ((BookmarksActivity)activity).getBmList().get(position).getSeekTime();

        int min = (int)TimeUnit.MILLISECONDS.toMinutes(currTime);
        int sec = (int)(TimeUnit.MILLISECONDS.toSeconds(currTime) - TimeUnit.MINUTES.toSeconds(min));
        int msec = (int)(currTime - TimeUnit.MINUTES.toMillis(min) - TimeUnit.SECONDS.toMillis(sec));

        minutesNp.setValue(min);
        secondsNp.setValue(sec);
        millisecondsNp.setValue(msec);

        // Scroll faster when holding down
        millisecondsNp.setOnLongPressUpdateInterval(50);
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

                        long finalTime = ((BookmarksActivity)activity).getFinalTime();
                        
                        // The max time is the song duration
                        if (newTime > finalTime) {
                            newTime = finalTime;
                        }

                        ((BookmarksActivity)activity).getBmList().get(position).setSeekTime(newTime);
                        ((BookmarksActivity)activity).update();
                    }
                })
                .setNegativeButton("Cancel", cancelButton);
    }
}
