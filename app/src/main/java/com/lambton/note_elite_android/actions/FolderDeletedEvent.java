package com.lambton.note_elite_android.actions;

import com.lambton.note_elite_android.model.Folder;

public class FolderDeletedEvent{
	Folder folder;

	public FolderDeletedEvent(Folder folder){
		this.folder = folder;
	}

	public Folder getFolder(){
		return folder;
	}
}
