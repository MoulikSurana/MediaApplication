package com.example.mediaapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
//    private Button play,pause;
    private SeekBar seekBar;
    private ImageView next,previous,pause;
    MediaPlayer mediaPlayer;
    Thread thread1;


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        thread1.interrupt();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        play=findViewById(R.id.play);
//        pause=findViewById(R.id.pause);
        pause=findViewById(R.id.pause);
        next=findViewById(R.id.next);
        previous=findViewById(R.id.previous);

        seekBar=findViewById(R.id.seekBar);
        mediaPlayer=MediaPlayer.create(this,R.raw.darkside);
        mediaPlayer.start();
        seekBar.setMax(mediaPlayer.getDuration());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//              if(b){  mediaPlayer.seekTo(i);}
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());

            }
        });

        thread1=new Thread(){
            @Override
            public void run() {
                int cp=0;
                try {
                    while (cp< mediaPlayer.getDuration()){
                        cp=mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(cp);
                        sleep(700);

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        thread1.start();
//        mediaPlayer=new MediaPlayer();
//        try {
//            mediaPlayer.setDataSource("https://drive.google.com/drive/u/0/my-drive");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mediaPlayer) {
//                Toast.makeText(MainActivity.this, "Ready to play", Toast.LENGTH_SHORT).show();
//                mediaPlayer.start();
//
//            }
//        });
//        mediaPlayer.prepareAsync();


        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying())
                {pause.setImageResource(R.drawable.play);
                 mediaPlayer.pause();
                }
                else {
                    pause.setImageResource(R.drawable.pause);
                    mediaPlayer.start();
                }
            }
        });

    }
}