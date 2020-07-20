package com.lambton.note_elite_android.note;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.lambton.note_elite_android.R;
import com.lambton.note_elite_android.adapter.RecordingListAdapter;
import com.lambton.note_elite_android.database.AudioDao;
import com.lambton.note_elite_android.model.AudioFile;
import com.lambton.note_elite_android.utils.ViewUtils;

import java.io.File;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecordingListActivity extends AppCompatActivity {

//    private FavoritePlacesRoomDb favoritePlacesRoomDb;
@BindView(R.id.toolbar_recordingList) Toolbar mToolbar;
    RecyclerView recordingListRV;
    List<AudioFile> audioFileList;
    RecordingListAdapter recordingListAdapter;
    public static int noteId;
    public static final int AUDIO_REQUEST_CODE = 12432;
    AudioFile audioFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording_list);
        initials();
    }

    private void initials() {
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(ViewUtils.tintDrawable(R.drawable.ic_arrow_back_white_24dp, R.color.tintColorByAman));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onBackPressed();
            }
        });
        recordingListRV = findViewById(R.id.recording_list_rv);
        recordingListRV.setLayoutManager(new LinearLayoutManager(this));
        audioFileList = new ArrayList<>();
//        favoritePlacesRoomDb = FavoritePlacesRoomDb.getINSTANCE(this);
        loadRecordingList();
    }

    private void loadRecordingList() {
        audioFileList = AudioDao.getLatestAudioFiles(noteId);
        recordingListAdapter = new RecordingListAdapter(this,R.layout.recording_list, audioFileList);
        recordingListRV.setAdapter(recordingListAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.recording_list_menu, menu);
        ViewUtils.tintMenu(menu, R.id.btn_speak_now, R.color.tintColorByAman);
        ViewUtils.tintMenu(menu, R.id.btn_select_audio_files, R.color.tintColorByAman);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.btn_speak_now){
            RecordingActivity.noteId = noteId;
            Intent intent = new Intent(RecordingListActivity.this,RecordingActivity.class);
            startActivity(intent);
            finish();

        }if(id == R.id.btn_select_audio_files){
            Intent intent_upload = new Intent();
            intent_upload.setType("audio/*");
            intent_upload.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent_upload,AUDIO_REQUEST_CODE);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){

        if(requestCode == AUDIO_REQUEST_CODE){

            if(resultCode == RESULT_OK){

                //the selected audio.
                Uri uri = data.getData();
                bind(uri.getPath(), uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void bind(String pathSave, Uri uri) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        String date = simpleDateFormat.format(cal.getTime());

        audioFile = new AudioFile();

        audioFile.setFileName(date);
        audioFile.setDate(date);
        audioFile.setNoteId(noteId);
        audioFile.setFilePath(pathSave);

        audioFile.save();

        loadRecordingList();
    }
}