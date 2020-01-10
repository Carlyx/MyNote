package com.brcorner.dnote.android.data;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.ListView;

import com.brcorner.dnote.android.model.NoteModel;


public class DNoteDB {

	//数据库的名字
	public static final String DB_NAME = "dnote2";

	public static final int VERSION = 1;

	private static DNoteDB dnoteDB;

	private SQLiteDatabase db;

	private DNoteDB(Context context)
	{
		DNoteOpenHelper dbHelper = new DNoteOpenHelper(context, DB_NAME, null, VERSION);
		Log.d("yes","in there");
		db = dbHelper.getWritableDatabase();
	}

	public synchronized static DNoteDB getInstance(Context context)
	{
		Log.d("数据库","aaa");
		if(dnoteDB == null)
		{
			Log.d("数据库","空");
			dnoteDB = new DNoteDB(context);
		}
		return dnoteDB;
	}

	public int saveNote(NoteModel noteModel)
	{

		if(noteModel != null)
		{
			Log.d("DnoteDB","51");
			ContentValues values = new ContentValues();
			values.put(DNoteOpenHelper.NOTE_TIME, noteModel.getNoteTime());
			values.put(DNoteOpenHelper.NOTE_CONTENT, noteModel.getNoteContent());
			if(noteModel.isFav())
			{
				values.put(DNoteOpenHelper.NOTE_ISFAV, 1);
			}
			else
			{
				values.put(DNoteOpenHelper.NOTE_ISFAV, 0);
			}
			if(noteModel.isDelete())
			{
				values.put(DNoteOpenHelper.NOTE_ISDELETE,1);
			}
			else
			{
				values.put(DNoteOpenHelper.NOTE_ISDELETE,0);
			}
			return (int) db.insert(DNoteOpenHelper.TAB_NAME, null, values);
		}
		return 0;
	}

	public void updateNote(NoteModel noteModel)
	{
		if(noteModel.isFav())
		{
			db.execSQL("update " + DNoteOpenHelper.TAB_NAME + " set note_content = ? and note_isfav = 1 where id = ?",
					new Object[]{noteModel.getNoteContent(),String.valueOf(noteModel.getNoteId())});
		}
		else
		{
			db.execSQL("update " + DNoteOpenHelper.TAB_NAME + " set note_content = ? and note_isfav = 0 where id = ?",
					new Object[]{noteModel.getNoteContent(),String.valueOf(noteModel.getNoteId())});
		}
	}

	public void changeNoteRanking(int fromIndex,int toIndex)
	{
		db.execSQL("update " + DNoteOpenHelper.TAB_NAME + " set id = 0 where id = ?",
				new Object[]{String.valueOf(fromIndex)});
		db.execSQL("update " + DNoteOpenHelper.TAB_NAME + " set id = id - 1 where ? > id > ?",
				new Object[]{String.valueOf(toIndex),String.valueOf(fromIndex)});
		db.execSQL("update " + DNoteOpenHelper.TAB_NAME + " set id = ? where id = 0",
				new Object[]{String.valueOf(toIndex)});
	}

	public void deleteNote(int noteId)
	{
		db.execSQL("delete from " + DNoteOpenHelper.TAB_NAME + " where id = ?", new Object[]{String.valueOf(noteId)});
	}

	public void recycle(int noteId)
	{
		Cursor cursor = null;
		Log.d("5","5");
		cursor = db.rawQuery("select * from Dnote", null);
		Log.d("6","6");
		if (cursor.moveToFirst()){
			do{
				int p = cursor.getInt(cursor.getColumnIndex("id"));
				if(p == noteId){
					int a = cursor.getInt(cursor.getColumnIndex(DNoteOpenHelper.NOTE_ISDELETE));
					if(a == 1){
						deleteNote(noteId);
					}
					else
					{
						db.execSQL("update " + DNoteOpenHelper.TAB_NAME + " set " + DNoteOpenHelper.NOTE_ISDELETE + " = 1 where id = ?", new Object[]{String.valueOf(noteId)});
					}
				}
			}while(cursor.moveToNext());
		}

	}

	// 从数据库中搜索相应的文本
	public List<NoteModel> searchNotesByStr(String str)
	{
		List<NoteModel> list = new ArrayList<NoteModel>();
		Cursor cursor = null;
		if(str != null && str.length() > 0)
		{
			Log.d("数据库中","搜索");
			cursor = db.rawQuery("select * from Dnote where note_content like '%"+ str +"%'",null);
		}
		else
		{
			cursor = db.query(DNoteOpenHelper.TAB_NAME, null, null, null, null, null, null);
		}
		// moveToFirst() 为指向结果查询的第一个结果 同时判断是否查询为空 不断把每一个note放入list中
		if(cursor.moveToFirst())
		{
			do{
				NoteModel noteModel = new NoteModel();
				noteModel.setNoteId(cursor.getInt(cursor.getColumnIndex("id")));
				noteModel.setNoteContent(cursor.getString(cursor.getColumnIndex(DNoteOpenHelper.NOTE_CONTENT)));
				noteModel.setNoteTime(cursor.getString(cursor.getColumnIndex(DNoteOpenHelper.NOTE_TIME)));
				int b =  cursor.getInt(cursor.getColumnIndex(DNoteOpenHelper.NOTE_ISFAV));
				if(b == 1)
				{
					noteModel.setFav(true);
				}
				else
				{
					noteModel.setFav(false);
				}
				int c = cursor.getInt(cursor.getColumnIndex(DNoteOpenHelper.NOTE_ISDELETE));
				if(c == 0)
				{
					list.add(noteModel);
				}
			}while(cursor.moveToNext());
		}
		return list;
	}
	// 加载回收站里面的数据
	public List<NoteModel> loadRecycle()
	{
		Log.d("DnoteDB.java","加载all数据");
		List<NoteModel> list = new ArrayList<NoteModel>();
//		Cursor cursor = db.rawQuery("select * from Dnote order by note_rankId",null);
		Cursor cursor = db.query(DNoteOpenHelper.TAB_NAME, null, null, null, null, null, null);
		if(cursor.moveToFirst())
		{
			do{
				NoteModel noteModel = new NoteModel();
				noteModel.setNoteId(cursor.getInt(cursor.getColumnIndex("id")));
				noteModel.setNoteContent(cursor.getString(cursor.getColumnIndex(DNoteOpenHelper.NOTE_CONTENT)));
				noteModel.setNoteTime(cursor.getString(cursor.getColumnIndex(DNoteOpenHelper.NOTE_TIME)));
				int b =  cursor.getInt(cursor.getColumnIndex(DNoteOpenHelper.NOTE_ISFAV));
				if(b == 1)
				{
					noteModel.setFav(true);
				}
				else
				{
					noteModel.setFav(false);
				}
				int c = cursor.getInt(cursor.getColumnIndex(DNoteOpenHelper.NOTE_ISDELETE));
				if(c == 0){
					noteModel.setDelete(true);
				}
				else
				{
					noteModel.setDelete(false);
					list.add(noteModel);
				}
			}while(cursor.moveToNext());
		}
		return list;
	}
	// 加载数据库里面的all notes
	public List<NoteModel> loadNotes()
	{
		Log.d("DnoteDB.java","加载all数据");
		List<NoteModel> list = new ArrayList<NoteModel>();
//		Cursor cursor = db.rawQuery("select * from Dnote order by note_rankId",null);
		Cursor cursor = db.query(DNoteOpenHelper.TAB_NAME, null, null, null, null, null, null);
		if(cursor.moveToFirst())
		{
			do{
				NoteModel noteModel = new NoteModel();
				noteModel.setNoteId(cursor.getInt(cursor.getColumnIndex("id")));
				noteModel.setNoteContent(cursor.getString(cursor.getColumnIndex(DNoteOpenHelper.NOTE_CONTENT)));
				noteModel.setNoteTime(cursor.getString(cursor.getColumnIndex(DNoteOpenHelper.NOTE_TIME)));
				int b =  cursor.getInt(cursor.getColumnIndex(DNoteOpenHelper.NOTE_ISFAV));
				if(b == 1)
				{
					noteModel.setFav(true);
				}
				else
				{
					noteModel.setFav(false);
				}
				int c = cursor.getInt(cursor.getColumnIndex(DNoteOpenHelper.NOTE_ISDELETE));
				if(c == 1){
					noteModel.setDelete(true);
				}
				else
				{
					noteModel.setDelete(false);
					list.add(noteModel);
				}
			}while(cursor.moveToNext());
		}
		return list;
	}

	public List<NoteModel> loadNotes1(){
		Cursor cursor = null;
		List<NoteModel> list = new ArrayList<NoteModel>();
		Log.d("5","5");
		cursor = db.rawQuery("select * from Dnote order by " + DNoteOpenHelper.NOTE_TIME, null);
		Log.d("6","6");
		if(cursor.moveToFirst())
		{
			do{
				NoteModel noteModel = new NoteModel();
				noteModel.setNoteId(cursor.getInt(cursor.getColumnIndex("id")));
				noteModel.setNoteContent(cursor.getString(cursor.getColumnIndex(DNoteOpenHelper.NOTE_CONTENT)));
				noteModel.setNoteTime(cursor.getString(cursor.getColumnIndex(DNoteOpenHelper.NOTE_TIME)));
				int b =  cursor.getInt(cursor.getColumnIndex(DNoteOpenHelper.NOTE_ISFAV));
				if(b == 1)
				{
					noteModel.setFav(true);
				}
				else
				{
					noteModel.setFav(false);
				}
				int c = cursor.getInt(cursor.getColumnIndex(DNoteOpenHelper.NOTE_ISDELETE));
				if(c == 1){
					noteModel.setDelete(true);
				}
				else
				{
					noteModel.setDelete(false);
					list.add(noteModel);
				}
			}while(cursor.moveToNext());
		}
		return list;
	}

	public List<NoteModel> loadNotes2(){
		Cursor cursor = null;
		List<NoteModel> list = new ArrayList<NoteModel>();
		Log.d("5","5");
		cursor = db.rawQuery("select * from Dnote order by " + DNoteOpenHelper.NOTE_TIME +" desc", null);
		Log.d("6","6");
		if(cursor.moveToFirst())
		{
			do{
				NoteModel noteModel = new NoteModel();
				noteModel.setNoteId(cursor.getInt(cursor.getColumnIndex("id")));
				noteModel.setNoteContent(cursor.getString(cursor.getColumnIndex(DNoteOpenHelper.NOTE_CONTENT)));
				noteModel.setNoteTime(cursor.getString(cursor.getColumnIndex(DNoteOpenHelper.NOTE_TIME)));
				int b =  cursor.getInt(cursor.getColumnIndex(DNoteOpenHelper.NOTE_ISFAV));
				if(b == 1)
				{
					noteModel.setFav(true);
				}
				else
				{
					noteModel.setFav(false);
				}
				int c = cursor.getInt(cursor.getColumnIndex(DNoteOpenHelper.NOTE_ISDELETE));
				if(c == 1){
					noteModel.setDelete(true);
				}
				else
				{
					noteModel.setDelete(false);
					list.add(noteModel);
				}
			}while(cursor.moveToNext());
		}
		return list;
	}
}
