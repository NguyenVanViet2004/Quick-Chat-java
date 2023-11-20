package com.example.pro1121_gr.function;

import android.app.Activity;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.widget.Toast;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class VoiceRecordingUtil {
    private static final int REQUEST_CODE_SPEECH_INPUT = 1;

    public static void startVoiceRecognitionActivity(Activity activity) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Nói gì đó...");

        try {
            activity.startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (Exception e) {
            Toasty.warning(activity,"Thiết bị của bạn không hỗ trợ chức năng nhận diện giọng nói.", Toasty.LENGTH_LONG, true).show();
        }
    }

    // Đoạn mã xử lý kết quả sau khi người dùng đã nói xong
    public static void processVoiceInput(int requestCode, int resultCode, Intent data, final VoiceInputListener listener) {
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == Activity.RESULT_OK && null != data) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (result != null && result.size() > 0) {
                    String voiceText = result.get(0);
                    if (listener != null) {
                        listener.onVoiceInput(voiceText);
                    }
                }
            }
        }
    }

    public interface VoiceInputListener {
        void onVoiceInput(String text);
    }
}
