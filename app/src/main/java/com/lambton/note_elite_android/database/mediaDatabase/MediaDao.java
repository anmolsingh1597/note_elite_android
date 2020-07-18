package com.lambton.note_elite_android.database.mediaDatabase;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MediaDao {

    @Insert
    void insertAudioMedia(MediaFile mediaFile);

    @Query("DELETE FROM audioMedia")
    void deleteAllAudioMedias();

    @Query("DELETE FROM audioMedia WHERE id = :id AND fileName = :fileName")
    int deleteAudioMedia(int id, String fileName);

    @Query("UPDATE audioMedia SET fileName = :fileName, filePath = :filePath, date = :date WHERE id = :id")
    int updateAudioMedia(int id, String fileName, String filePath, double date);

    @Query("SELECT * FROM audioMedia ORDER BY fileName")
    List<MediaFile> getAllAudioMedia();

}
