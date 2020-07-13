package com.lambton.note_elite_android.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import com.lambton.note_elite_android.R;
import com.lambton.note_elite_android.database.AppDatabase;
import com.lambton.note_elite_android.database.FoldersDAO;
import com.lambton.note_elite_android.model.Folder;
import com.lambton.note_elite_android.model.FolderNoteRelation;
import com.lambton.note_elite_android.model.Note;
import com.lambton.note_elite_android.model.Note_Table;


public class DebugActivity extends AppCompatActivity{
	@Override protected void onCreate(@Nullable Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_debug);
		ButterKnife.bind(this);
	}

	@OnClick(R.id.assign_to_folders) void assignToFolders(){
		Note note = SQLite.select().from(Note.class).orderBy(Note_Table.createdAt, false).querySingle();
		List<Folder> folders = FoldersDAO.getLatestFolders();
		for (Folder folder : folders){
			FolderNoteRelation fnr = new FolderNoteRelation();
			fnr.setFolder(folder);
			fnr.setNote(note);
			fnr.save();
		}
	}

	@OnClick(R.id.create_5_notes) void create5Notes(){
		AppDatabase.Utils.createSomeNotes(5);
	}
}
