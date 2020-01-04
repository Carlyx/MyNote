package com.brcorner.dnote.android.utils;

import android.util.Log;

import com.brcorner.dnote.android.activity.MainActivity;
import com.brcorner.dnote.android.data.ConstantData;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Stack;


public class CommonUtils {
	
	public static Stack stack = new Stack();
	// 点击左上角登录界面后弹出内容 背景变暗 点击背景则doFinish 返回
	public static void doFinish(MainActivity mainActivity)
	{
		Log.d("CommonUtils","doFinish");
		if(stack.isEmpty())
		{
			mainActivity.finish();
		}
		else
		{
			switch ((Integer)stack.peek()) {
			case ConstantData.DELETE_DIALOG:
				mainActivity.hideDeleteDialog(null);
				break;
			case ConstantData.INFOR_DIALOG:
				mainActivity.hideInfoDialog(null);
				break;
			case ConstantData.SEND_DIALOG:
				mainActivity.hideSendDialog(null);
				break;
			case ConstantData.EDIT_STATE:
				mainActivity.backToList(null);
				break;
			default:
				break;
			}
		}
		
	}
	
	public static String getDate()
	{
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date);
		
	}
}
