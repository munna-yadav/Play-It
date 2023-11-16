package com.example.playit;


import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    LinearLayout linearLayout;
    SearchView searchView;
    ImageView imageView;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView =findViewById(R.id.listView);
        linearLayout = findViewById(R.id.linearLayout);
        imageView= findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);
        Dexter.withContext(MainActivity.this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                ArrayList<File> mySongs = FetchSongs(Environment.getExternalStorageDirectory());
                String [] items = new String[mySongs.size()];
                for(int i =0; i<mySongs.size();i++){
                    items[i] = mySongs.get(i).getName().replace(".mp3","");
                    ArrayAdapter<String > ad = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1,items);
                    listView.setAdapter(ad);



                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(MainActivity.this,Playsong.class);
                            String currentSong = listView.getItemAtPosition(position).toString();
                            intent.putExtra("songList",mySongs);
                            intent.putExtra("currentSong",currentSong);
                            intent.putExtra("position",position);
                            startActivity(intent);
                        }
                    });

                }
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Toast.makeText(MainActivity.this, "permission needed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }





    public ArrayList<File> FetchSongs(File file){
        ArrayList arrayList = new ArrayList();
        File [] songs = file.listFiles();
        if (songs !=null){
            for(File myfile:songs){
                if(myfile.isDirectory() && !myfile.isHidden()){
                    arrayList.addAll(FetchSongs(myfile));
                }
                else {
                    if(myfile.getName().endsWith(".mp3") && !myfile.getName().startsWith(".")){
                        arrayList.addAll(Collections.singleton(myfile));
                    }
                }
            }
        }
        return arrayList;
    }
}