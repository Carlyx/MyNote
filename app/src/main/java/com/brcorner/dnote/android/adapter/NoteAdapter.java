package com.brcorner.dnote.android.adapter;

import java.util.List;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.brcorner.dnote.android.R;
import com.brcorner.dnote.android.data.ConstantData;
import com.brcorner.dnote.android.model.NoteModel;
import com.brcorner.drag_sort_listview_lib.DragSortListView;
import android.util.Log;
// 适配器（adapter）在android中是数据和视图（View）之间的一个桥梁，通过适配器以便于数据在view视图上显示。本文在model文件夹里面继承了ArrayAdapter来实现。
public class NoteAdapter extends ArrayAdapter<NoteModel> implements DragSortListView.DropListener{
	
	
	private int resourceId;

	private List<NoteModel> objects;

	public NoteAdapter(Context context, int resource,
			List<NoteModel> objects) {
		super(context, resource, objects);
		this.objects = objects;
		resourceId = resource;
	}

	public void setUp()
	{
		for (NoteModel object : objects) {
			object.setIsUp(true);
		}
		// 数据改变时 自动滑到顶部
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return super.getCount();
	}

	// 进入主页面时显示已有的note
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.d("init","show");
		NoteModel noteModel = getItem(position);
		View view;
		ViewHolder viewHolder;
		if(convertView == null)
		{
			view = LayoutInflater.from(getContext()).inflate(resourceId, null);
			viewHolder = new ViewHolder();
			viewHolder.ago_text = (TextView) view.findViewById(R.id.ago_text);
			viewHolder.time_text = (TextView) view.findViewById(R.id.time_text);
			viewHolder.summary_text = (TextView) view.findViewById(R.id.summary_text);
			viewHolder.fav_image = (ImageView) view.findViewById(R.id.fav_image);
//			viewHolder.clip_image = (ImageView) view.findViewById(R.id.clip_image);
			// setTag记录一些view的信息, 用于节约资源
			view.setTag(viewHolder);
		}
		else
		{
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}
//		viewHolder.ago_text.setText("一天前");
		viewHolder.time_text.setText(noteModel.getNoteTime());
		Log.d("show",noteModel.getNoteContent());
		if(noteModel.getNoteContent().length() > ConstantData.TITLE_LENGTH)
		{
			viewHolder.summary_text.setText(noteModel.getNoteContent().substring(0, ConstantData.TITLE_LENGTH));
		}
		else
		{
			viewHolder.summary_text.setText(noteModel.getNoteContent());
		}
		if(noteModel.isFav())
		{
			viewHolder.fav_image.setVisibility(View.VISIBLE);
		}
		else
		{
			viewHolder.fav_image.setVisibility(View.INVISIBLE);
		}

//		if(noteModel.isUp())
//		{
//			viewHolder.clip_image.setImageResource(R.mipmap.clip_up);
//		}
//		else
//		{
//			viewHolder.clip_image.setImageResource(R.mipmap.clip_normal);
//		}
		return view;
	}

	@Override
	public void drop(int from, int to) {
		if (from != to) {
			NoteModel item = this.getItem(from);
			this.remove(item);
			this.insert(item, to);
		}
	}

	class ViewHolder{
		TextView ago_text;
		TextView time_text;
		ImageView fav_image;
		TextView summary_text;
//		ImageView clip_image;
	}
}
