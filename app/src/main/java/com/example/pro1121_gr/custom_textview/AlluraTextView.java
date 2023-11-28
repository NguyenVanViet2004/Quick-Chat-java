package com.example.pro1121_gr.custom_textview;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

public class AlluraTextView extends AppCompatTextView {
    public AlluraTextView(@NonNull Context context) {
        super(context);
        setFontsTextView();
    }

    public AlluraTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setFontsTextView();
    }

    public AlluraTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFontsTextView();
    }

    private void setFontsTextView(){
        Typeface typeface = Utils.getAlluraTypeFace(getContext());
        setTypeface(typeface);
    }
}
