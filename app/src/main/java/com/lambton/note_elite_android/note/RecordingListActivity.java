package com.lambton.note_elite_android.note;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.lambton.note_elite_android.R;
import com.lambton.note_elite_android.adapter.RecordingListAdapter;
import com.lambton.note_elite_android.database.AudioDao;
import com.lambton.note_elite_android.model.AudioFile;

import java.util.ArrayList;
import java.util.List;

public class RecordingListActivity extends AppCompatActivity {

//    private FavoritePlacesRoomDb favoritePlacesRoomDb;
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
}