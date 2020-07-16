package com.lambton.note_elite_android.tasks;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.google.android.gms.maps.model.LatLng;
import com.lambton.note_elite_android.actions.NoteEditedEvent;
import com.lambton.note_elite_android.database.NotesDAO;
import com.lambton.note_elite_android.model.Note;

import org.greenrobot.eventbus.EventBus;

import java.util.Locale;

public class SaveLocationTask extends Job {

    private static final String TAG = "SaveLocationTask";
    private final int noteId;
    private final LatLng notesLocation;

    public SaveLocationTask(LatLng notesLocation, int noteId){
        super(new Params(1));
        this.notesLocation = notesLocation;
        this.noteId = noteId;

    }
    @Override
    public void onAdded() {
        Log.e(TAG, "onAdded() called with: " + "");
    }

    @Override
    public void onRun() throws Throwable {
        Log.e(TAG, "onRun() called with: " + "");
        Note note = NotesDAO.getNote(noteId);
        note.setBody(note.getBody() + "Location: "+ String.format(Locale.CANADA, "%.4f", (notesLocation.latitude)) + "," + String.format(Locale.CANADA, "%.4f", (notesLocation.longitude)));
        note.save();
        EventBus.getDefault().post(new NoteEditedEvent(note.getId()));
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {

    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return null;
    }
}
