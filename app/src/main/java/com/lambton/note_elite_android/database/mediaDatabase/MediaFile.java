package com.lambton.note_elite_android.database.mediaDatabase;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "audioMedia")
public class MediaFile {

    @NonNull
    @ColumnInfo(name = "id")
    private int id;

    @NonNull
    @ColumnInfo(name = "fileName")
    private String fileName;

    @NonNull
    @ColumnInfo(name = "filePath")
    private String filePath;

    @NonNull
    @ColumnInfo(name = "date")
    private String date;

    public MediaFile(int id, @NonNull String fileName, @NonNull String filePath, @NonNull String date) {
        this.id = id;
        this.fileName = fileName;
        this.filePath = filePath;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getFileName() {
        return fileName;
    }

    public void setFileName(@NonNull String fileName) {
        this.fileName = fileName;
    }

    @NonNull
    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(@NonNull String filePath) {
        this.filePath = filePath;
    }

    @NonNull
    public String getDate() {
        return date;
    }

    public void setDate(@NonNull String date) {
        this.date = date;
    }
}
