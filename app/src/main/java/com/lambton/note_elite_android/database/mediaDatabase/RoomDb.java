/*
package com.lambton.note_elite_android.database.mediaDatabase;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {MediaFile.class}, version = 2, exportSchema = false)
public abstract class RoomDb extends RoomDatabase {

    private static final String DB_NAME = "media_room_db";

    public abstract MediaDao mediaDao();

    private static volatile RoomDb INSTANCE;

    public static RoomDb getINSTANCE(Context context) {
        if(INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), RoomDb.class, DB_NAME)
                    .allowMainThreadQueries()
                    .build();
        }

        return INSTANCE;

    }


}
*/
