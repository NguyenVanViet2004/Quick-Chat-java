package com.example.pro1121_gr.custom_textview;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

public class BlackjackTextview extends AppCompatTextView {
    public BlackjackTextview(@NonNull Context context) {
        super(context);
        setFontsTextView();
    }

    public BlackjackTextview(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setFontsTextView();
    }

    public BlackjackTextview(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFontsTextView();
    }

    private void setFontsTextView(){
        Typeface typeface = Utils.getBlackjackTypeFace(getContext());
        setTypeface(typeface);
    }
}
