package com.example.rmusic;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView list;
    ArrayList<File> allfiles;
    String[] items;

    public ArrayList<File> fetchsongs(File F)
    {
        ArrayList currentsongs=new ArrayList();
        File[] songs=F.listFiles();
        if(songs!=null)
        {
            for(File myfile:songs)
            {
                if(!myfile.isHidden()&&myfile.isDirectory())
                {
                    currentsongs.addAll(fetchsongs(myfile));
                }
                else{
                    if(myfile.getName().endsWith(".mp3"))
                    {
                        currentsongs.add(myfile);
                    }
                }
            }
        }
        return currentsongs;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list=findViewById(R.id.songslist);


        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        allfiles=fetchsongs(Environment.getExternalStorageDirectory());
                        items=new String[allfiles.size()];
                        for(int i=0;i<allfiles.size();i++)
                        {
                            items[i]=allfiles.get(i).getName().replace(".mp3","");
                        }
                        ArrayAdapter a=new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1,items);
                        list.setAdapter(a);
                    }
                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Toast.makeText(getApplicationContext(), "Go to settings and give permission", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                })
                .check();
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(getApplicationContext(),Playingsong.class);
                intent.putExtra("allsongs",allfiles);
                intent.putExtra("cursong",allfiles.get(i).getName());
                intent.putExtra("position",i);
                startActivity(intent);
            }
        });

    }
}