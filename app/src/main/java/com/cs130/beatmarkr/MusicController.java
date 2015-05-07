package com.cs130.beatmarkr;

/**
 * Created by Ashley on 5/6/15.
 */
import android.content.Context;
import android.util.AttributeSet;
import android.widget.MediaController;

public class MusicController extends MediaController {
    public MusicController(Context c){
        super(c);
    }

    // Override in order to stop the controller from hiding after 3 seconds
    public void hide(){}
}
