package com.lambton.note_elite_android.actions;

import com.lambton.note_elite_android.model.Folder;

public class FolderCreatedEvent{
	private Folder folder;

	public FolderCreatedEvent(Folder folder){
		this.folder = folder;
	}

	public Folder getFolder(){
		return folder;
	}
}
