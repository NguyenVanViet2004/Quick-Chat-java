package com.example.pro1121_gr.custom_textview;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

public class RobotoLightTextView extends AppCompatTextView {
    public RobotoLightTextView(@NonNull Context context) {
        super(context);
        setFontsTextView();
    }

    public RobotoLightTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setFontsTextView();
    }

    public RobotoLightTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFontsTextView();
    }
    private void setFontsTextView(){
        Typeface typeface = Utils.getRobotoLightTypeFace(getContext());
        setTypeface(typeface);
    }
}
