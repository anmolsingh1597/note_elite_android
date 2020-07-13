package com.lambton.note_elite_android.actions;

import com.lambton.note_elite_android.model.Note;
import com.lambton.note_elite_android.database.NotesDAO;

public class NoteFoldersUpdatedEvent{

	int noteId;

	public NoteFoldersUpdatedEvent(int noteId){
		this.noteId = noteId;
	}

	public int getNoteId(){
		return noteId;
	}

	public Note getNote(){
		return NotesDAO.getNote(noteId);
	}
}
