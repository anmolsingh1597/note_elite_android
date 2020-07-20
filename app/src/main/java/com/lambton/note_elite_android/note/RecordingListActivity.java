package com.lambton.note_elite_android.note;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
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

import java.util.ArrayList;
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

        }
        return super.onOptionsItemSelected(item);
    }
}