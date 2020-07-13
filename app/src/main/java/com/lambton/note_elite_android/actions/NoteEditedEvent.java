package com.lambton.note_elite_android.actions;

import com.lambton.note_elite_android.database.NotesDAO;
import com.lambton.note_elite_android.model.Note;


public class NoteEditedEvent{
	int noteId;

	public NoteEditedEvent(int noteId){
		this.noteId = noteId;
	}

	public int getNoteId(){
		return noteId;
	}
	
	public Note getNote(){
		return NotesDAO.getNote(noteId);
	}
}
