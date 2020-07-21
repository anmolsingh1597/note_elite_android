package com.lambton.note_elite_android.note;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.VolumeShaper;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lambton.note_elite_android.R;
import com.lambton.note_elite_android.database.AudioDao;
import com.lambton.note_elite_android.model.AudioFile;
import com.lambton.note_elite_android.model.Note;
import com.raizlabs.android.dbflow.sql.language.Operator;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class RecordingActivity extends AppCompatActivity {

    static int noteId;
    AudioFile audioFile;
    Button btnPlay, btnStop, btnRecord, btnStopRecord, btnSave;
    String pathSave = "";
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;
    AudioManager audioManager;

    final int REQUEST_PERMISSION_CODE = 1000;

    private static String RECORDED_FILE;
    String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);

       /* if(noteId != 0){
            List<AudioFile> audioFileList = new ArrayList<>();
            audioFileList = AudioDao.getLatestAudioFiles(noteId);
            if(!audioFileList.equals(null)){
            pathSave = audioFileList.get(0).getFilePath();
            }
        }*/

        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        // set the volume of played media to maximum.
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
//       RecordingActivityIntentBuilder.inject(getIntent(), this);
        btnPlay = findViewById(R.id.btn_play);
        btnRecord = findViewById(R.id.btn_record);
        btnStop = findViewById(R.id.btn_stop);
        btnStopRecord = findViewById(R.id.btn_stop_record);
        btnSave = findViewById(R.id.btn_save);

        btnRecord.setVisibility(View.VISIBLE);
        btnStop.setVisibility(View.GONE);
        btnPlay.setVisibility(View.GONE);
        btnStopRecord.setVisibility(View.GONE);
        btnSave.setVisibility(View.GONE);



        // check the permission
        if (!checkPermissionDevice())
            requestPermission();




        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermissionDevice()) {
//                    pathSave = Environment.getExternalStorageDirectory().getAbsolutePath()
//                            + "/recording.3gp";
//                    pathSave = getExternalFilesDir(null).getAbsolutePath()
//                            + RECORDED_FILE;
                    Calendar cal = Calendar.getInstance();

                    RECORDED_FILE =  "/"+String.valueOf(cal.getTimeInMillis())+noteId+"audio.3gp";
                    pathSave = getExternalCacheDir().getAbsolutePath()
                            + RECORDED_FILE;

                    Log.d("path", "onClick: " + pathSave);

                    setUpMediaRecorder();

                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    } catch (IllegalStateException ise) {
                        // make something ...
                        ise.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }



                    btnPlay.setEnabled(false);
                    btnStop.setEnabled(false);
                    btnStopRecord.setEnabled(true);
                    btnRecord.setVisibility(View.GONE);
                    btnStop.setVisibility(View.GONE);
                    btnPlay.setVisibility(View.GONE);
                    btnStopRecord.setVisibility(View.VISIBLE);
                    btnSave.setVisibility(View.GONE);

                } else
                    requestPermission();
            }
        });

        btnStopRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaRecorder.stop();
                btnStopRecord.setEnabled(false);
                btnPlay.setEnabled(true);
                btnStop.setEnabled(true);
                btnRecord.setEnabled(true);
                btnRecord.setVisibility(View.VISIBLE);
                btnStop.setVisibility(View.GONE);
                btnPlay.setVisibility(View.VISIBLE);
                btnStopRecord.setVisibility(View.GONE);
                btnSave.setVisibility(View.VISIBLE);
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnStopRecord.setEnabled(false);
                btnStop.setEnabled(true);
                btnRecord.setEnabled(false);
                btnPlay.setVisibility(View.GONE);
                btnStop.setVisibility(View.VISIBLE);

                btnRecord.setVisibility(View.GONE);
                btnPlay.setVisibility(View.GONE);
                btnStopRecord.setVisibility(View.GONE);
                btnSave.setVisibility(View.VISIBLE);

                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(pathSave);
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        btnStop.setVisibility(View.GONE);
                        btnPlay.setVisibility(View.VISIBLE);
                        btnRecord.setEnabled(true);
                    }
                });

                mediaPlayer.start();

            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnStopRecord.setEnabled(false);
                btnPlay.setEnabled(true);
                btnStop.setEnabled(false);
                btnRecord.setEnabled(true);
                btnPlay.setVisibility(View.VISIBLE);

                btnRecord.setVisibility(View.VISIBLE);
                btnStop.setVisibility(View.GONE);
                btnStopRecord.setVisibility(View.GONE);
                btnSave.setVisibility(View.VISIBLE);

                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    setUpMediaRecorder();
                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RecordingActivity.this);
                LayoutInflater layoutInflater = LayoutInflater.from(RecordingActivity.this);
                View view = layoutInflater.inflate(R.layout.alert_dialog_save, null);
                builder.setView(view);
                final AlertDialog alertDialog = builder.create();
                alertDialog.show();

                final EditText etFileName = view.findViewById(R.id.et_file_name);
                view.findViewById(R.id.btn_save_file).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String fileName = etFileName.getText().toString().trim();
                        if (fileName.isEmpty()) {
                            etFileName.setError("Name field cannot be empty");
                            etFileName.requestFocus();
                            return;
                        }
                        bind(fileName);
                        alertDialog.dismiss();
                        startActivity(new Intent(RecordingActivity.this,RecordingListActivity.class));
                        finish();
                    }
                });
            }
        });


    }

    private void bind(String fileName) {

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        String date = simpleDateFormat.format(cal.getTime());

        audioFile = new AudioFile();

        audioFile.setFileName(fileName);
        audioFile.setDate(date);
        audioFile.setNoteId(noteId);
        audioFile.setFilePath(pathSave);

        audioFile.save();

    }

    private void setUpMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(pathSave);
//        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);

    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        }, REQUEST_PERMISSION_CODE);
    }

    private boolean checkPermissionDevice() {
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result == PackageManager.PERMISSION_GRANTED &&
                record_audio_result == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        startActivity(new Intent(RecordingActivity.this,RecordingListActivity.class));
        finish();
    }
}


/*

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.transition.TransitionManager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import com.lambton.note_elite_android.R;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class RecordingActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int RECORD_AUDIO_REQUEST_CODE = 101;

    private Toolbar toolbar;
    private Chronometer chronometer;
    private ImageView imageViewRecord, imageViewPlay, imageViewStop;
    private SeekBar seekBar;
    private LinearLayout linearLayoutRecorder, linearLayoutPlay;
    private MediaRecorder mRecorder;
    private MediaPlayer mPlayer;
    private String fileName = null;
    private int lastProgress = 0;
    private Handler mHandler = new Handler();
    private boolean isPlaying = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);

        getPermissionToRecordAudio();
        initViews();

    }

    private void initViews() {

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Voice Recorder");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.black));
        setSupportActionBar(toolbar);

        linearLayoutRecorder = (LinearLayout) findViewById(R.id.linearLayoutRecorder);
        chronometer = (Chronometer) findViewById(R.id.chronometerTimer);
        chronometer.setBase(SystemClock.elapsedRealtime());
        imageViewRecord = (ImageView) findViewById(R.id.imageViewRecord);
        imageViewStop = (ImageView) findViewById(R.id.imageViewStop);
        imageViewPlay = (ImageView) findViewById(R.id.imageViewPlay);
        linearLayoutPlay = (LinearLayout) findViewById(R.id.linearLayoutPlay);
        seekBar = (SeekBar) findViewById(R.id.seekBar);

        imageViewRecord.setOnClickListener(this);
        imageViewStop.setOnClickListener(this);
        imageViewPlay.setOnClickListener(this);

    }


    public void getPermissionToRecordAudio() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    RECORD_AUDIO_REQUEST_CODE);

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == RECORD_AUDIO_REQUEST_CODE) {
            if (grantResults.length == 3 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED) {

                //Toast.makeText(this, "Record Audio permission granted", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, "You must give permissions to use this app. App is exiting.", Toast.LENGTH_SHORT).show();
                finishAffinity();
            }
        }

    }

    private void prepareforRecording() {
        TransitionManager.beginDelayedTransition(linearLayoutRecorder);
        imageViewRecord.setVisibility(View.GONE);
        imageViewStop.setVisibility(View.VISIBLE);
        linearLayoutPlay.setVisibility(View.GONE);
    }


    private void startRecording() {
        //we use the MediaRecorder class to recordingbtn
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        */
/**
 * In the lines below, we create a directory VoiceRecorderSimplifiedCoding/Audios in the phone storage
 * and the audios are being stored in the Audios folder  once the audio is complete, timer is stopped here moving the track as per the seekBar's position
 **//*

        File root = android.os.Environment.getExternalStorageDirectory();
        File file = new File(root.getAbsolutePath() + "/VoiceRecorderSimplifiedCoding/Audios");
        if (!file.exists()) {
            file.mkdirs();
        }

        fileName =  root.getAbsolutePath() + "/VoiceRecorder/Audios/" +
                String.valueOf(System.currentTimeMillis() + ".mp3");
        Log.d("filename",fileName);
        mRecorder.setOutputFile(fileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
            mRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int lastProgress = 0;
        seekBar.setProgress(0);
        stopPlaying();
        //starting the chronometer
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
    }


    private void stopPlaying() {
        try{
            mPlayer.release();
        }catch (Exception e){
            e.printStackTrace();
        }
        mPlayer = null;
        //showing the play button
        imageViewPlay.setImageResource(R.drawable.play);
        chronometer.stop();
    }

    private void prepareforStop() {
        TransitionManager.beginDelayedTransition(linearLayoutRecorder);
        imageViewRecord.setVisibility(View.VISIBLE);
        imageViewStop.setVisibility(View.GONE);
        linearLayoutPlay.setVisibility(View.VISIBLE);
    }

    private void stopRecording() {

        try{
            mRecorder.stop();
            mRecorder.release();
        }catch (Exception e){
            e.printStackTrace();
        }
        mRecorder = null;
        //starting the chronometer
        chronometer.stop();
        chronometer.setBase(SystemClock.elapsedRealtime());
        //showing the play button
        Toast.makeText(this, "Recording saved successfully.", Toast.LENGTH_SHORT).show();
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
//fileName is global string. it contains the Uri to the recently recorded audio.
            mPlayer.setDataSource(fileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e("LOG_TAG", "prepare() failed");
        }
        //making the imageview pause button
        imageViewPlay.setImageResource(R.drawable.pause);

        seekBar.setProgress(lastProgress);
        mPlayer.seekTo(lastProgress);
        seekBar.setMax(mPlayer.getDuration());
        seekUpdation();
        chronometer.start();

        */
/** once the audio is complete, timer is stopped here**//*

        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                imageViewPlay.setImageResource(R.drawable.play);
                isPlaying = false;
                chronometer.stop();
            }
        });

        */
/** moving the track as per the seekBar's position**//*

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if( mPlayer!=null && fromUser ){
                    //here the track's progress is being changed as per the progress bar
                    mPlayer.seekTo(progress);
                    //timer is being updated as per the progress of the seekbar
                    chronometer.setBase(SystemClock.elapsedRealtime() - mPlayer.getCurrentPosition());
                    lastProgress = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            seekUpdation();
        }
    };

    private void seekUpdation() {
        if(mPlayer != null){
            int mCurrentPosition = mPlayer.getCurrentPosition() ;
            seekBar.setProgress(mCurrentPosition);
            lastProgress = mCurrentPosition;
        }
        mHandler.postDelayed(runnable, 100);
    }

    @Override
    public void onClick(View v) {

        if( v == imageViewRecord ){
            prepareforRecording();
            startRecording();
        }else if( v == imageViewStop ){
            prepareforStop();
            stopRecording();
        }

    }
}*/
