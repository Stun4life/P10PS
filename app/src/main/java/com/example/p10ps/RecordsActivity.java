package com.example.p10ps;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class RecordsActivity extends AppCompatActivity {

    Button btnRefresh, btnFavorites;

    ListView lvCheckRecords;

    ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);

        btnRefresh = findViewById(R.id.refreshRecords);
        btnFavorites = findViewById(R.id.viewFavorites);
        lvCheckRecords = findViewById(R.id.recordsListView);

        String folderLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Folder";
        File targetFile = new File(folderLocation, "location.txt");

        if (targetFile.exists() == true) {
            String data = "";
            try {
                FileReader reader = new FileReader(targetFile);
                BufferedReader br = new BufferedReader(reader);

                String line = br.readLine();
                while(line != null){
                    data += line + "\n";
                    line = br.readLine();
                }
                br.close();
                reader.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                Toast.makeText(RecordsActivity.this, "Failed to read", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            Log.d("Content", data);
        }


        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

}