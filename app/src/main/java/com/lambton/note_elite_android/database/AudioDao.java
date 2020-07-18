package com.lambton.note_elite_android.database;

import com.lambton.note_elite_android.model.AudioFile;
import com.lambton.note_elite_android.model.AudioFile_Table;
import com.lambton.note_elite_android.model.Note;
import com.lambton.note_elite_android.model.Note_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

public class AudioDao {


    public static List<AudioFile> getLatestAudioFiles(int noteId){
            return SQLite.select()
                    .from(AudioFile.class)
                    .where(AudioFile_Table.NoteId.is(noteId))
                    .orderBy(AudioFile_Table.date, false)
                    .queryList();
    }

    public static AudioFile getAudioFile(int noteId, String fileName){
        return SQLite.select()
                .from(AudioFile.class)
                .where(AudioFile_Table.NoteId.is(noteId), (AudioFile_Table.fileName.is(fileName)))
                .querySingle();
    }

    public static void deleteAudioFile(Note note){
        SQLite.delete().from(AudioFile.class).where(AudioFile_Table.NoteId.is(note.getId())).execute();
    }
}
