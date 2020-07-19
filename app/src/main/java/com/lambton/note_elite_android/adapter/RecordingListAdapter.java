package com.lambton.note_elite_android.adapter;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lambton.note_elite_android.R;
import com.lambton.note_elite_android.model.AudioFile;
import com.lambton.note_elite_android.note.RecordingListActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RecordingListAdapter extends RecyclerView.Adapter<RecordingListAdapter.RecordingListViewHolder> {

    private static final String TAG = "RecordingListAdapter";
    Context context;
    int layoutRes;
    List<AudioFile> audioFileList;
    List<AudioFile> audioFileListFull;
    MediaPlayer mediaPlayer;
    final int REQUEST_PERMISSION_CODE = 1000;

    final private static String RECORDED_FILE = "/audio.3gp";
//    FavoritePlacesRoomDb favoritePlacesRoomDb;


    public RecordingListAdapter(Context context, int layoutRes, List<AudioFile> audioFileList) {
        this.context = context;
        this.layoutRes = layoutRes;
        this.audioFileList = audioFileList;
    }


    public class RecordingListViewHolder extends RecyclerView.ViewHolder {
        TextView fileName;
        TextView date;

        public RecordingListViewHolder(@NonNull View itemView) {
            super(itemView);
            fileName = itemView.findViewById(R.id.tv_file_name);
            date = itemView.findViewById(R.id.tv_file_date);

        }
    }

    @NonNull
    @Override
    public RecordingListAdapter.RecordingListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutRes, null);

        RecordingListViewHolder recordingListViewHolder = new RecordingListAdapter.RecordingListViewHolder(view);
        return recordingListViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecordingListAdapter.RecordingListViewHolder holder, int position) {

        final AudioFile audioFile = audioFileList.get(position);
        holder.fileName.setText("File: "+audioFile.getFileName());
        holder.date.setText("Date: "+audioFile.getDate());
        holder.itemView.findViewById(R.id.btn_play_audio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(audioFile.getFilePath());
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaPlayer.start();
            }

        });

    }

    @Override
    public int getItemCount() {
        return audioFileList.size();
    }
}
