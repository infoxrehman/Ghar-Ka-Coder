package com.lixtanetwork.gharkacoder.geminiai;

import android.content.Context;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

public class GradientTextView extends AppCompatTextView {

    public GradientTextView(Context context) {
        super(context);
        init();
    }

    public GradientTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GradientTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            if (getWidth() > 0) {
                applyGradient();
            }
        });
    }

    private void applyGradient() {
        int[] colors = {
                0xFF439DDF,  // #439DDF
                0xFF4F87ED,  // #4F87ED
                0xFF9476C5,  // #9476C5
                0xFFBC688E,  // #BC688E
                0xFFD6645D   // #D6645D
        };

        float[] positions = {
                0f, 0.25f, 0.5f, 0.75f, 1f
        };

        LinearGradient linearGradient = new LinearGradient(
                0, 0, getWidth(), 0,
                colors, positions,
                Shader.TileMode.CLAMP
        );

        getPaint().setShader(linearGradient);
        invalidate();
    }
}
