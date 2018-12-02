package com.vijaya.speechtotext;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private final int CHECK_CODE = 1;
    private static final int REQ_CODE_SPEECH_INPUT = 100;
    private TextView mVoiceInputTv;
    private ImageButton mSpeakBtn;

    public static final String PREF_FILE_NAME = "PrefFile";

    private boolean hello = false;
    private static final String NAME = "name";
    private String name="";

    private Speaker speaker;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences(PREF_FILE_NAME,0);

        mVoiceInputTv = (TextView) findViewById(R.id.voiceInput);
        mSpeakBtn = (ImageButton) findViewById(R.id.btnSpeak);
        mSpeakBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startVoiceInput();
            }
        });

        checkTTS();


    }

    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hello, How can I help you?");

        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {

        }
    }

    private void checkTTS() {
        Intent check = new Intent();
        check.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(check, CHECK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                        if (!hello){
                            hello=true;
                            if(result.get(0).toLowerCase().equals("hello") || result.get(0).toLowerCase().equals("hi")) {
                                speaker.speak("What is your name?");
                                mVoiceInputTv.append("What is your name?\n");
                            }
                        }else{
                            if (name.isEmpty()){
                                name = result.get(0);
                                editor = preferences.edit();
                                editor.putString(NAME,name).apply();
                                String response = "Hello " + preferences.getString(NAME,"name") + " what can I do for you?\n";
                                speaker.speak(response);
                                mVoiceInputTv.append(response);
                            }else {
                                switch(result.get(0).toLowerCase()) {
                                    case "i'm not feeling good what should i do":
                                        String response = "I can understand, please tell me your symptoms.\n";
                                        speaker.speak(response);
                                        mVoiceInputTv.append(response);
                                        break;
                                    case "thank you":
                                        response = "You're welcome " + preferences.getString(NAME,"name") + ", and thank you too.\n";
                                        speaker.speak(response);
                                        mVoiceInputTv.append(response);
                                        break;
                                    case "what medicine should i take":
                                        response = "You should take these and get a flu shot.\n";
                                        speaker.speak(response);
                                        mVoiceInputTv.append(response);
                                        break;
                                    case "what time is it":
                                        String time = getTime();
                                        response = "The current time is " + time +"\n";
                                        speaker.speak(response);
                                        mVoiceInputTv.append(response);
                                        break;
                                    case "ataxia dysphagia":
                                        response = "Oh dear, it sounds like you have rabies.\n";
                                        speaker.speak(response);
                                        speaker.speak("Engaging quarantine procedures");
                                        mVoiceInputTv.append(response);
                                        break;
                                    default:
                                        response = "Sorry, I didn't understand that.\n";
                                        speaker.speak(response);
                                        mVoiceInputTv.append(response);
                                        break;
                                }
                            }
                        }
                    mVoiceInputTv.append("You said: " + result.get(0) + "\n");
                }
                break;
            }
            case CHECK_CODE:{
                if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                    speaker = new Speaker(this);
                    speaker.allow(true);
                } else {
                    Intent install = new Intent();
                    install.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                    startActivity(install);
                }
                break;
            }

        }
    }

    private String getTime(){
        SimpleDateFormat sdfDate = new SimpleDateFormat("HH:mm");//dd/MM/yyyyDate
        Date now = new Date();
        String[] strDate = sdfDate.format(now).split(":");
        if(strDate[1].contains("00"))strDate[1] = "o'clock";
        return sdfDate.format(now);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        speaker.destroy();
    }
}