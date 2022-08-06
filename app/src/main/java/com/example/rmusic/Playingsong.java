package com.example.rmusic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class Playingsong extends AppCompatActivity {
    TextView songname;
    ImageView play;
    ImageView next;
    ImageView previous;
    ArrayList<File> songs;
    int position;
    SeekBar seekbar;
    MediaPlayer mediaplayer;
    Thread updateseek;


    @Override
    protected void onDestroy(){
        super.onDestroy();
        mediaplayer.stop();
        mediaplayer.release();
        updateseek.interrupt();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playingsong);
        songname=findViewById(R.id.songname);
        play=findViewById(R.id.play);
        next=findViewById(R.id.next);
        previous=findViewById(R.id.previous);
        seekbar=findViewById(R.id.seekBar);
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        songs=(ArrayList)bundle.getParcelableArrayList("allsongs");
        String currentlyrunningsong=intent.getStringExtra("cursong");
        songname.setText(currentlyrunningsong.replace(".mp3",""));
        position=intent.getIntExtra("position",0);
        Uri uri=Uri.parse(songs.get(position).toString());
        mediaplayer=MediaPlayer.create(this,uri);
        mediaplayer.start();

        seekbar.setMax(mediaplayer.getDuration());


        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaplayer.seekTo(seekbar.getProgress());
            }
        });
        updateseek=new Thread(){
            @Override
            public void run()
            {
                int currentpos=0;
                try{
                    while(currentpos<mediaplayer.getDuration())
                    {
                        currentpos=mediaplayer.getCurrentPosition();
                        seekbar.setProgress(currentpos);
                        try{
                            Thread.currentThread().sleep(950);}
                        catch (Exception e)
                        {
                            Log.d("mytag",""+e);
                        }
                    }
                }
                catch (Exception e)
                {
                    Log.d("mytag",""+e);
                }
            }
        };
        updateseek.start();

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaplayer.isPlaying())
                {
                    mediaplayer.pause();
                    play.setImageResource(R.drawable.playimg);
                }
                else{
                    play.setImageResource(R.drawable.pauseimg);
                    mediaplayer.start();
                }
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //new song fresh progress bar
                seekbar.setProgress(0);
                if(mediaplayer.isPlaying()==false)
                {
                    play.setImageResource(R.drawable.pauseimg);
                }
                position=position-1;
                Uri uri;
                if(position>=0){
                uri=Uri.parse(songs.get(position).toString());
                }
                else {
                    position=songs.size()-1;
                    uri=Uri.parse(songs.get(position).toString());
                }
                songname.setText(songs.get(position).getName().replace(".mp3",""));
                mediaplayer.stop();
                mediaplayer.release();
                mediaplayer=MediaPlayer.create(getApplicationContext(),uri);
                seekbar.setMax(mediaplayer.getDuration());
                mediaplayer.start();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //new song fresh progress bar
                seekbar.setProgress(0);

                if(mediaplayer.isPlaying()==false)
                {
                    play.setImageResource(R.drawable.pauseimg);
                }
                position++;
                Uri uri;
                if(position>=songs.size()){
                    position=0;
                    uri=Uri.parse(songs.get(position).toString());
                }
                else {
                    uri=Uri.parse(songs.get(position).toString());
                }
                songname.setText(songs.get(position).getName().replace(".mp3",""));
                mediaplayer.stop();
                mediaplayer.release();
                mediaplayer=MediaPlayer.create(getApplicationContext(),uri);
                seekbar.setMax(mediaplayer.getDuration());
                mediaplayer.start();
            }
            });
    }
}