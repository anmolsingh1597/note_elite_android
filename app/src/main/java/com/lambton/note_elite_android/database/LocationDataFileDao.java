package com.lambton.note_elite_android.database;

import com.lambton.note_elite_android.model.LocationDataFile;
//import com.lambton.note_elite_android.model.Location_Table;
import com.lambton.note_elite_android.model.LocationDataFile_Table;
import com.lambton.note_elite_android.model.Note;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

public class LocationDataFileDao {

    public static List<LocationDataFile> getLatestLocation(int noteId){
        return SQLite.select()
                .from(LocationDataFile.class)
                .where(LocationDataFile_Table.NoteId.is(noteId))
                .orderBy(LocationDataFile_Table.date, false)
                .queryList();
    }

    public static LocationDataFile getLocation(int noteId){
        return SQLite.select()
                .from(LocationDataFile.class)
                .where(LocationDataFile_Table.NoteId.is(noteId))
                .querySingle();
    }

    public static void deleteLocation(Note note){
        SQLite.delete().from(LocationDataFile.class).where(LocationDataFile_Table.NoteId.is(note.getId())).execute();
    }
}
