package com.lambton.note_elite_android.model;

import com.lambton.note_elite_android.database.AppDatabase;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(database = AppDatabase.class, allFields = true)
public class AudioFile extends BaseModel {

    @PrimaryKey
    private String fileName;
    private int NoteId;
    private String filePath;
    private String date;
    public AudioFile() {
    }

   /* public AudioFile(String fileName, int noteId, String filePath, String date) {
        this.fileName = fileName;
        NoteId = noteId;
        this.filePath = filePath;
        this.date = date;
    }*/

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getNoteId() {
        return NoteId;
    }

    public void setNoteId(int noteId) {
        NoteId = noteId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "AudioFile{" +
                "fileName='" + fileName + '\'' +
                ", NoteId=" + NoteId +
                ", filePath='" + filePath + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
