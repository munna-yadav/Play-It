package com.example.playit;

import static java.lang.Thread.*;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import java.util.ArrayList;

public class Playsong extends AppCompatActivity {

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();

    }
    TextView songName,showTime,currentTime;
    SeekBar seekBar;
    Thread updateSeek;
    ImageView previous,play,next;
    LottieAnimationView lottieAnimationView;
    ArrayList songs;
    MediaPlayer mediaPlayer;
    String textContent;
    int songPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playsong);
        songName = findViewById(R.id.songName);
        showTime = findViewById(R.id.showTime);
        currentTime =  findViewById(R.id.currentTime);
        play = findViewById(R.id.play);
        previous = findViewById(R.id.previous);
        next = findViewById(R.id.next);
        seekBar = findViewById(R.id.seekBar);
        lottieAnimationView = findViewById(R.id.lottieAnimationView);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        assert bundle != null;
        songs = (ArrayList) bundle.getParcelableArrayList("songList");
        textContent = intent.getStringExtra("currentSong");
        songName.setText(textContent);
        songName.setSelected(true);
        songPosition = intent.getIntExtra("position", 0);
        Uri uri = Uri.parse(songs.get(songPosition).toString());
        mediaPlayer = MediaPlayer.create(Playsong.this, uri);
        mediaPlayer.start();


        int minute=0,second=0;
        int mili = mediaPlayer.getDuration();
        minute=mili/60000;
        second = mili% (1000 * 60) / 1000;
        showTime.setText(minute+":"+second);

        seekBar.setMax(mediaPlayer.getDuration());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());


            }
        });
        updateSeek = new Thread(){
            @Override
            public void run() {
                int currentPosition = 0;
                try{
                    while(currentPosition<mediaPlayer.getDuration()){
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition);
                        int mili = seekBar.getProgress();
                        int minute = mili/60000;
                        int second = mili%(1000*60)/1000;
                        String formattedSecond = String.format("%02d", second);
                        currentTime.setText(minute+":"+formattedSecond);
                        sleep(800);
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

        };
        updateSeek.start();


        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                    lottieAnimationView.playAnimation();
                    play.setImageResource(R.drawable.pause);
                } else {
                    mediaPlayer.pause();
                    play.setImageResource(R.drawable.play);
                    lottieAnimationView.pauseAnimation();

                }

            }
        });
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if (songPosition != 0) {
                    songPosition = songPosition - 1;
                } else {
                    songPosition = songs.size() - 1;
                }
                Uri uri = Uri.parse(songs.get(songPosition).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                mediaPlayer.start();
                play.setImageResource(R.drawable.pause);
                seekBar.setMax(mediaPlayer.getDuration());
                textContent = songs.get(songPosition).toString();
                songName.setText(textContent);
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if (songPosition != songs.size() - 1) {
                    songPosition = songPosition + 1;
                } else {
                    songPosition = 0;
                }
                Uri uri = Uri.parse(songs.get(songPosition).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                mediaPlayer.start();
                play.setImageResource(R.drawable.pause);
                seekBar.setMax(mediaPlayer.getDuration());
                textContent = songs.get(songPosition).toString();
                songName.setText(textContent);

            }
        });
//        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                if (songPosition != 0) {
//                    songPosition = songPosition + 1;
//                } else {
//                    songPosition = 0;
//                }
//                Uri uri = Uri.parse(songs.get(songPosition).toString());
//                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
////                textContent = songs.get(songPosition).toString();
////                songName.setText(textContent);
//                try {
//                    Thread.sleep(5000);
//                } catch (InterruptedException e) {
//
//                }
//                mediaPlayer.start();
//            }
//        });
    }

}
