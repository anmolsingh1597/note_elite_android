package com.lambton.note_elite_android.actions;

import com.lambton.note_elite_android.model.Note;

public class NoteDeletedEvent{
	Note note;

	public NoteDeletedEvent(Note note){
		this.note = note;
	}

	public Note getNote(){
		return note;
	}
}
