package com.brcorner.dnote.android.model;

public class NoteModel {
	private int noteId;
	private String noteTime;
	private String noteContent;
	private boolean isFav;
	private boolean isUp;
	private boolean isDelete;
	private int isCompletely;

	public boolean isFav() {
		return isFav;
	}
	public boolean isDelete(){
		return isDelete;
	}
	public int isCompletely(){return isCompletely;}
	public void setFav(boolean isFav) {
		this.isFav = isFav;
	}
	public void setDelete(boolean isDelete){
		this.isDelete = isDelete;
	}
	public void setIsCompletely(int isCompletely){
		this.isCompletely += 1;
	}
	public int getNoteId() {
		return noteId;
	}
	public void setNoteId(int noteId) {
		this.noteId = noteId;
	}
	public String getNoteTime() {
		return noteTime;
	}
	public void setNoteTime(String noteTime) {
		this.noteTime = noteTime;
	}
	public String getNoteContent() {
		return noteContent;
	}
	public void setNoteContent(String noteContent) {
		this.noteContent = noteContent;
	}

	public boolean isUp() {
		return isUp;
	}

	public void setIsUp(boolean isUp) {
		this.isUp = isUp;
	}
}
