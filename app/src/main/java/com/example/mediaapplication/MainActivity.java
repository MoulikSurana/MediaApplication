package com.example.mediaapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements ServiceConnection {
//    private Button play,pause;
    private SeekBar seekBar;
    private ImageView next,previous,pause;
    MediaPlayer mediaPlayer;
    Thread thread1;
    MusicService ms;
    MediaSessionCompat mediaSession;

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
        Intent intent =new Intent(this,MusicService.class);
        bindService(intent,this,BIND_AUTO_CREATE);
        mediaSession=new MediaSessionCompat(this,"Player Audio");

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
                 showNotification(R.drawable.play);
                }
                else {
                    pause.setImageResource(R.drawable.pause);
                    mediaPlayer.start();
                    showNotification(R.drawable.pause);
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent =new Intent(this,MusicService.class);
        bindService(intent,this,BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(this);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
MusicService.MyBinder binder=(MusicService.MyBinder)service;
ms=binder.getService();
        Log.e("Cnnected","music services");
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        ms=null;
        Log.e("Disconnected","music services");

    }
    public void showNotification(int playpauseBtn){
        Intent intent =new Intent(this,MusicService.class);
        PendingIntent contIntent=PendingIntent.getActivity(this,0,intent,0);

        Intent preIntent=new Intent(this,NotificatoinReciver.class).setAction("PREVIOUS");
        PendingIntent prependIntent =PendingIntent.getBroadcast(this,0,preIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent playIntent=new Intent(this,NotificatoinReciver.class).setAction("PLAY");
        PendingIntent playpendIntent =PendingIntent.getBroadcast(this,0,playIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent nextIntent=new Intent(this,NotificatoinReciver.class).setAction("NEXT");
        PendingIntent nextpendIntent =PendingIntent.getBroadcast(this,0,nextIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Bitmap picture= BitmapFactory.decodeResource(getResources(),R.drawable.cover_image);
        Notification notification=new NotificationCompat.Builder(this,"CHANNEL_1")
                .setSmallIcon(R.drawable.cover_image)
                .setLargeIcon(picture)
                .setContentTitle("Dark side")
                .addAction(R.drawable.previous,"Previous",prependIntent)
                .addAction(R.drawable.play,"Play",playpendIntent)
                .addAction(R.drawable.next,"Next",nextpendIntent)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSession.getSessionToken()))
                .setPriority(Notification.PRIORITY_LOW)
                .setContentIntent(contIntent)
                .setOnlyAlertOnce(true)
                .build();
        NotificationManager nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        nm.notify(0,notification);
    }
}