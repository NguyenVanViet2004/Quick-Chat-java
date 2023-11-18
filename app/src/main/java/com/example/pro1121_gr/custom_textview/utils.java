package com.example.pro1121_gr.custom_textview;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

public class utils {
    private static Typeface alluraTypeFace;
    private static Typeface blackjackTypeFace;
    private static Typeface robotoBoldTypeFace;
    private static Typeface robotoItalicTypeFace;
    private static Typeface robotoLightTypeFace;

    public static Typeface getAlluraTypeFace(Context context) {
        if (alluraTypeFace == null) alluraTypeFace = Typeface.createFromAsset(context.getAssets(), "fonts/Allura-Regular.otf");
        return alluraTypeFace;
    }

    public static Typeface getBlackjackTypeFace(Context context) {
        if (blackjackTypeFace == null) blackjackTypeFace = Typeface.createFromAsset(context.getAssets(), "fonts/blackjack.otf");
        return blackjackTypeFace;
    }

    public static Typeface getRobotoBoldTypeFace(Context context) {
        if (robotoBoldTypeFace == null) robotoBoldTypeFace = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Bold.ttf");
        return robotoBoldTypeFace;
    }

    public static Typeface getRobotoItalicTypeFace(Context context) {
        if (robotoItalicTypeFace == null) robotoItalicTypeFace = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Italic.ttf");
        return robotoItalicTypeFace;
    }

    public static Typeface getRobotoLightTypeFace(Context context) {
        if (robotoLightTypeFace == null) robotoLightTypeFace = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf");
        return robotoLightTypeFace;
    }

    public static void setFontForTextView(TextView textView, Typeface typeface) {
        textView.setTypeface(typeface);
    }
}
