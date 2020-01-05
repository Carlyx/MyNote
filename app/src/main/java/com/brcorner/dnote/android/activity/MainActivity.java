package com.brcorner.dnote.android.activity;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract.CommonDataKinds.Note;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import android.view.ViewStub;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.brcorner.dnote.android.R;
import com.brcorner.dnote.android.adapter.NoteAdapter;
import com.brcorner.dnote.android.data.ConstantData;
import com.brcorner.dnote.android.data.DNoteDB;
import com.brcorner.dnote.android.listener.MyAnimationListener;
import com.brcorner.dnote.android.listener.MyHideAnimationListener;
import com.brcorner.dnote.android.model.NoteModel;
import com.brcorner.dnote.android.utils.CommonUtils;
//import com.brcorner.drag_sort_listview_lib.DragSortController;
//import com.brcorner.drag_sort_listview_lib.DragSortListView;


public class MainActivity extends Activity {

    //最开始显示的界面 分别为左上角登录图标 中间的标题题目 右上角的新建
    private ImageView info_image;
    private TextView title_text;
    private ImageView add_image;

    //为新建便签时显示的内容 分别为左上角的返回列表 右上角的完成
    private Button back_btn;
    private TextView complete_text;
    // 完成按钮为button_add

    // 新建的便签完成之后右上角的发送和删除
    private ImageView delete_image;
//    private ImageView send_image;

    // 标签懒加载 关于左上角登录按钮里面的内容
    private ViewStub viewstub_about;

    // 一些动画声明 具体注释在下面
    private Animation bottom_in_anim, bottom_out_anim, fade_in, fade_in_300, fade_out,
            left_in, left_out, right_in, right_out, zoom_translate,scale;

    //左上角点击登录之后的布局
    private FrameLayout about_fl;

    // 点击左上角登录按钮后背景会变暗 这个为变暗的背景
    private View about_bg;

    // 数据库声明
    private DNoteDB dNoteDB;

    // 发送时的布局
    private RelativeLayout send_rl;
    // 下面弹出删除窗口的布局
    private RelativeLayout delete_rl;

    // 拖动的布局
//    private DragSortListView dragSortListView;
//    private DragSortController mController;
    // 自建的非拖动布局
    private ListView my_list_view;

    // 显示note
    private NoteAdapter noteAdapter;

    // 空note时布局
    private RelativeLayout empty_note_view;
    // 左上角的登录
    private RelativeLayout layout_info;

    // 关于继承view的布局 空note列表和非空note列表
    private FrameLayout list_fl, edit_fl;

    // note输入部分
    private EditText edittext_note;
    // 选中或取消星标
    private CheckBox fav_checkBox;
    // 时间部分
    private TextView time_text;
    // 分享的表
//    private TableLayout layout_share;

    //关于搜索的部分 其中依次为搜索框 搜索框右边的删除文字的×
    private EditText edittext_search;
    private ImageView imageview_delete;

    // 保存的值 分别为单个note和多个note组成的list
    private NoteModel noteModel;
    private List<NoteModel> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        // 布局设置
        this.initView();
        // 加载页面
        listPage();
    }

    private void initView() {
        // 数据库的建立
        dNoteDB = DNoteDB.getInstance(this);
        //左上角的登录界面
        info_image = (ImageView) findViewById(R.id.info_image);
        //标题栏的名字
        title_text = (TextView) findViewById(R.id.title_text);
        //右上角写便签view
        add_image = (ImageView) findViewById(R.id.add_image);
        // 左上角返回列表的按钮
        back_btn = (Button) findViewById(R.id.back_btn);
        // 写完便签右上角完成的文字
        complete_text = (TextView) findViewById(R.id.complete_text);
        // 删除按钮的红色背景
        delete_image = (ImageView) findViewById(R.id.delete_image);
        // 发送信息的按钮
//        send_image = (ImageView) findViewById(R.id.send_image);
        // 关于左上角登录按钮的一些懒标记
        viewstub_about = (ViewStub) findViewById(R.id.viewstub_about);
        // 让一些懒标记显示
        viewstub_about.inflate();
        //左上角点击登录之后的布局
        about_fl = (FrameLayout) findViewById(R.id.about_fl);

        // 左上角登录按钮弹出窗口上面的相关布局
//        layout_share = (TableLayout) findViewById(R.id.layout_share);
        layout_info = (RelativeLayout) findViewById(R.id.layout_info);

        // 点击左上角登录按钮后背景会变暗 这个为变暗的背景
        about_bg = findViewById(R.id.about_bg);

        // 发送的相关布局
        send_rl = (RelativeLayout) View.inflate(this, R.layout.send_dialog,
                null);
        // 点击右上角删除按钮弹出下面窗口的布局
        delete_rl = (RelativeLayout) View.inflate(this, R.layout.delete_dialog,
                null);
        // 没有任何note时空便签
        empty_note_view = (RelativeLayout) findViewById(R.id.empty_note_view);
        // 空note列表
        list_fl = (FrameLayout) findViewById(R.id.list_fl);
        // 非空note列表
        edit_fl = (FrameLayout) findViewById(R.id.edit_fl);
        // note编辑输入部分
        edittext_note = (EditText) findViewById(R.id.edittext_note);
        // 监听note输入部分的焦点变化， 如是否进入 退出等
        edittext_note.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            // v 发生变化的视图    hasFocus:用来判断视图是否获得了焦点
            public void onFocusChange(View v, boolean hasFocus) {
                Log.d("MainActivity.java","181");
                // 只有当view本身以及它的所有祖先们都是visible时，isShown()才返回TRUE
                if (edittext_note.isShown()) {
                    if (hasFocus) {
                        //获得焦点后 进入新建的页面
                        noteEditState();
                    } else {
                        noteFinishState();
                    }
                }

            }
        });
        // 星标
        fav_checkBox = (CheckBox) findViewById(R.id.fav_checkBox);
        // 时间显示
        time_text = (TextView) findViewById(R.id.time_text);
        // 点击星标的事件
        fav_checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                Log.d("MainActivity.java","205");
                noteModel.setFav(isChecked);
            }
        });

        // 动态布局
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        this.addContentView(send_rl, params);
        this.addContentView(delete_rl, params);

        // 使用AnimationUtils载入动画
        // 表示点击右上角删除后 顶部弹框上升的动画
        bottom_in_anim = AnimationUtils.loadAnimation(this, R.anim.bottom_in);
        // 点击底部弹框的删除按钮后 顶部弹框下降的动画
        bottom_out_anim = AnimationUtils.loadAnimation(this, R.anim.bottom_out);
        // 表示点击右上角删除后 背景变暗的动画
        fade_in = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        // 表示点击弹框的删除后 背景变亮的动画
        fade_out = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        // 点击底部弹框删除按钮后 恢复主界面的动画
        left_in = AnimationUtils.loadAnimation(this, R.anim.left_in);
//        left_out = AnimationUtils.loadAnimation(this, R.anim.left_out);
        // 点击返回列表返回页面的动画
        right_out = AnimationUtils.loadAnimation(this, R.anim.right_out);
        // 进入编辑页面的动画
        right_in = AnimationUtils.loadAnimation(this, R.anim.right_in);
        // 分享的动画
        fade_in_300 = AnimationUtils.loadAnimation(this, R.anim.fade_in_100);
//        zoom_translate = AnimationUtils.loadAnimation(this,
//                R.anim.zoom_translate);
        // 进入编辑页面编辑框放缩的页面
        scale = AnimationUtils.loadAnimation(this,R.anim.scale);
        // 拖动功能
//        dragSortListView = (DragSortListView) findViewById(R.id.listview_notes);
        // 自建非拖动功能
        my_list_view = (ListView) findViewById(R.id.listview_my);

        // 以下几行为判断显示空页面还是有note页面  同时获取适配器的数据源 也为从数据库获取all notes
        dataList = dNoteDB.loadNotes();
        if (dataList != null && dataList.size() > 0) {
            empty_note_view.setVisibility(View.INVISIBLE);
        } else {
            empty_note_view.setVisibility(View.VISIBLE);
        }

        //创建数组适配器，作为数据源和列表控件联系的桥梁
        //第一个参数：上下文环境
        //第二个参数：当前列表项加载的布局文件
        //第三个参数：数据源
        noteAdapter = new NoteAdapter(this, R.layout.list_notes_item, dataList);
//        mController = buildController(dragSortListView);
//        dragSortListView.setFloatViewManager(mController);
//        dragSortListView.setOnTouchListener(mController);
//        // 控制能否拖动
//        dragSortListView.setDragEnabled(false);


        //搜索框 依次为搜索框和右边的×号
        LinearLayout search = (LinearLayout) View.inflate(this,
                R.layout.search, null);
        edittext_search = (EditText) search.findViewById(R.id.edittext_search);
        imageview_delete = (ImageView) search.findViewById(R.id.imageview_delete);

        // 点击搜索框的右边小×  搜索框清空
        imageview_delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                edittext_search.setText("");
            }
        });

        // 搜索框逻辑 文本监听
        edittext_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("查找内容","ing");
                String searchStr = s.toString();
                if (s != null && s.length() > 0) {
                    Log.d("test","1");
                    imageview_delete.setVisibility(View.VISIBLE);
//                    imageview_delete.setVisibility(View.INVISIBLE);
                } else {
                    Log.d("test","2");
                    imageview_delete.setVisibility(View.GONE);
                }
                List<NoteModel> newList = dNoteDB.searchNotesByStr(searchStr);
                dataList.clear();
                dataList.addAll(newList);
                // 通过一个外部的方法控制如果适配器的内容改变时需要强制调用getView来刷新每个Item的内容。
                if (noteAdapter != null) {
                    noteAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });
        my_list_view.addHeaderView(search);
        my_list_view.setAdapter(noteAdapter);
        // 单note点击事件
        my_list_view
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    // arg0：是指父View  arg1就是你点击的那个Item的View arg2是position，position是你适配器里面的position
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int arg2, long arg3) {
                        Log.d("MainActivity.java","322");
                        noteModel = dataList.get(arg2 - 1);
                        showFinish();
                        noteFinishState();
                    }
                });

//        dragSortListView.addHeaderView(search);
//        dragSortListView.setAdapter(noteAdapter);
//        // 点击进某一便签的编辑页面则触发
//        dragSortListView
//                .setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> arg0, View arg1,
//                                            int arg2, long arg3) {
//                        Log.d("MainActivity.java","322");
//                        noteModel = dataList.get(arg2 - 1);
//                        showFinish();
//                        noteFinishState();
//                    }
//                });
    }

//    public void setUp()
//    {
//        for (NoteModel model : dataList) {
//            model.setIsUp(true);
//        }
//    }

//    public DragSortController buildController(DragSortListView dslv) {
//        DragSortController controller = new DragSortController(dslv);
//        controller.setDragHandleId(R.id.drag_handle);
//        controller.setClickRemoveId(R.id.click_remove);
//        controller.setRemoveEnabled(true);
//        controller.setSortEnabled(true);
//        controller.setDragInitMode(DragSortController.ON_LONG_PRESS);
//        controller.setRemoveMode(DragSortController.CLICK_REMOVE);
//
//        return controller;
//    }

    public void listPage() {
        //左上角的登录部分
        info_image.setVisibility(View.VISIBLE);
        //最上面标题栏中央的字
        title_text.setVisibility(View.VISIBLE);
        //右上角写内容的按钮
        add_image.setVisibility(View.VISIBLE);

//        info_image.setVisibility(View.INVISIBLE);
//        title_text.setVisibility(View.INVISIBLE);
//        add_image.setVisibility(View.INVISIBLE);

        back_btn.setVisibility(View.INVISIBLE);
        complete_text.setVisibility(View.INVISIBLE);
        delete_image.setVisibility(View.INVISIBLE);
//        send_image.setVisibility(View.INVISIBLE);
//        back_btn.setVisibility(View.VISIBLE);
//        complete_text.setVisibility(View.VISIBLE);
//        delete_image.setVisibility(View.VISIBLE);
//        send_image.setVisibility(View.VISIBLE);

    }

    // 进入开始界面 左上角的登录
    public void showInfoDialog(View view) {
        Log.d("进入","info");
        viewstub_about.setVisibility(View.VISIBLE);
//        bottom_in_anim.setAnimationListener(new MyAnimationListener(about_fl));
//        about_fl.startAnimation(bottom_in_anim);
        about_fl.setVisibility(View.VISIBLE);

//        fade_in.setAnimationListener(new MyAnimationListener(about_bg));
//        about_bg.startAnimation(fade_in);
        about_bg.setVisibility(View.VISIBLE);
//        about_bg.setVisibility(View.INVISIBLE);
        about_bg.setClickable(true);

        CommonUtils.stack.push(ConstantData.INFOR_DIALOG);
    }
    // 左上角登录弹框后点×后退出弹框
    public void hideInfoDialog(View view) {
        Log.d("退出","info");
        bottom_out_anim.setAnimationListener(new MyHideAnimationListener(new View[]{about_fl}));
        about_fl.startAnimation(bottom_out_anim);
        about_bg.setClickable(false);
        fade_out.setAnimationListener(new MyHideAnimationListener(new View[]{viewstub_about, about_bg}));
        about_bg.startAnimation(fade_out);
//        layout_share.setVisibility(View.GONE);
        layout_info.setVisibility(View.VISIBLE);
        CommonUtils.stack.pop();
    }

//    public void share(View view) {
//        fade_in_300.setAnimationListener(new MyAnimationListener(layout_share));
//        layout_share.startAnimation(fade_in_300);
//        layout_share.setVisibility(View.VISIBLE);
//        layout_info.setVisibility(View.GONE);
//    }
    // 登出的事件
    public void login_out(View view){
        Intent intent = new Intent(MainActivity.this, NotesListActivity.class);
        startActivity(intent);
    }

    // 进入某一便签的编辑页面时触发
    public void showFinish() {
        Log.d("MainActivity.java","showFinish()");
        showEditAnim();
        initFinishData();
    }
    // 进入编辑页面的动画
    public void showEditAnim() {
        Log.d("MainActivity.java","showEditAnim");
        edittext_note.setVisibility(View.VISIBLE);
//        left_out.setAnimationListener(new MyAnimationListener(list_fl));
//        list_fl.startAnimation(left_out);
        list_fl.setVisibility(View.INVISIBLE);

        edit_fl.setScaleX(0.8f);
        edit_fl.setScaleY(0.8f);
        scale.setAnimationListener(new MyAnimationListener(edit_fl));
        right_in.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            // 当进入编辑页面的动画结束时  缩放编辑页面框的大小
            @Override
            public void onAnimationEnd(Animation animation) {
                edit_fl.startAnimation(scale);
                edit_fl.setScaleX(1.0f);
                edit_fl.setScaleY(1.0f);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        edit_fl.startAnimation(right_in);


        edit_fl.setVisibility(View.VISIBLE);
        CommonUtils.stack.push(ConstantData.EDIT_STATE);
    }

    // 跳转编辑页面, 在进入编辑页面时初始化
    public void showEdit(View view) {
        Log.d("编辑","ing");
        showEditAnim();
        edittext_note.setFocusable(true);
        edittext_note.requestFocus();

        noteModel = new NoteModel();
        noteModel.setFav(false);
        noteModel.setNoteTime(CommonUtils.getDate());

        initFinishData();
    }
    // 每当进入某一个note后用于显示信息， 如note建立的内容 时间 星标
    public void initFinishData() {
        Log.d("MainActivity.java", "initFinishData");
        time_text.setText(noteModel.getNoteTime());
        fav_checkBox.setChecked(noteModel.isFav());
        edittext_note.setText(noteModel.getNoteContent());
    }

    // 点击左上角返回按钮后返回列表
    public void backToList(View view) {
        Log.d("MainActivity.java","backToList");
        listPage();
        edittext_note.setVisibility(View.GONE);
        right_out.setAnimationListener(new MyAnimationListener(edit_fl));
        edit_fl.startAnimation(right_out);
        edit_fl.setVisibility(View.INVISIBLE);

        left_in.setAnimationListener(new MyAnimationListener(list_fl));
        list_fl.startAnimation(left_in);
        list_fl.setVisibility(View.VISIBLE);
        CommonUtils.stack.pop();
        dataList.clear();

        List<NoteModel> newList = dNoteDB.loadNotes();
        dataList.addAll(newList);
        if (noteAdapter != null) {
            noteAdapter.notifyDataSetChanged();
        }

        if (dataList != null && dataList.size() > 0) {
            empty_note_view.setVisibility(View.INVISIBLE);
        } else {
            empty_note_view.setVisibility(View.VISIBLE);
        }
    }

    // 显示删除列表
    public void showDeleteDialog(View view) {
        Log.d("MainActivity.java","showDeleteDialog");
//        bottom_in_anim.setAnimationListener(new MyAnimationListener(delete_rl));
//        delete_rl.startAnimation(bottom_in_anim);
        delete_rl.setVisibility(View.VISIBLE);

        fade_in.setAnimationListener(new MyAnimationListener(about_bg));
        about_bg.startAnimation(fade_in);
        about_bg.setVisibility(View.VISIBLE);
        about_bg.setClickable(true);
        CommonUtils.stack.push(ConstantData.DELETE_DIALOG);
    }

    // 隐藏删除弹窗
    public void hideDeleteDialog(View view) {
        Log.d("MainActivity.java","hideDeleteDialog");
        about_bg.setClickable(false);
        bottom_out_anim
                .setAnimationListener(new MyAnimationListener(delete_rl));
        delete_rl.startAnimation(bottom_out_anim);
        delete_rl.setVisibility(View.GONE);

        fade_out.setAnimationListener(new MyAnimationListener(about_bg));
        about_bg.startAnimation(fade_out);
        about_bg.setVisibility(View.GONE);
        CommonUtils.stack.pop();
    }

    // 显示发送弹窗
//    public void showSendDialog(View view) {
//        bottom_in_anim.setAnimationListener(new MyAnimationListener(send_rl));
//        send_rl.startAnimation(bottom_in_anim);
//        send_rl.setVisibility(View.VISIBLE);
//        about_bg.setClickable(false);
//        fade_in.setAnimationListener(new MyAnimationListener(about_bg));
//        about_bg.startAnimation(fade_in);
//        about_bg.setVisibility(View.VISIBLE);
//        CommonUtils.stack.push(ConstantData.SEND_DIALOG);
//    }

    // 当点击完右上角的删除按钮后 隐藏传送按钮
    // AnimationListener为动画的监听器
//    public void hideSendDialog(View view) {
//        //背景不可点击
//        about_bg.setClickable(false);
//        bottom_out_anim.setAnimationListener(new MyAnimationListener(send_rl));
//        send_rl.startAnimation(bottom_out_anim);
//        send_rl.setVisibility(View.GONE);
//
//        fade_out.setAnimationListener(new MyAnimationListener(about_bg));
//        about_bg.startAnimation(fade_out);
//        about_bg.setVisibility(View.GONE);
//        CommonUtils.stack.pop();
//    }

    // 点击删除按钮
    public void doDelete(View view) {
        Log.d("点击","删除");
        // 点击下面弹出的删除按钮后应该隐藏该弹窗
        this.hideDeleteDialog(null);
        dNoteDB.deleteNote(noteModel.getNoteId());
        backToList(null);
//        delete_image.setImageResource(R.anim.del_btn_anim);
//        animationDrawable = (AnimationDrawable) delete_image.getDrawable();
//        animationDrawable.start();
//        int duration = 0;
//        for (int i = 0; i < animationDrawable.getNumberOfFrames(); i++) {
//            duration += animationDrawable.getDuration(i);
//        }
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            public void run() {
//                edit_fl.startAnimation(zoom_translate);
//                zoom_translate.setAnimationListener(new AnimationListener() {
//
//                    @Override
//                    public void onAnimationStart(Animation animation) {
//                    }
//
//                    @Override
//                    public void onAnimationRepeat(Animation animation) {
//                    }
//
//                    @Override
//                    public void onAnimationEnd(Animation animation) {
//                        delete_image
//                                .setImageResource(R.drawable.batch_delete_back);
//                        try {
//                            Thread.sleep(1);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//
//
//
//
//                        delete_image.clearAnimation();
//                    }
//                });
//            }
//        }, duration);
    }
    // 当进入已编辑的note时（或是点击完成后） 显示界面
    private void noteFinishState() {
        Log.d("MainActivity.java","noteFinishState()");
        back_btn.setVisibility(View.VISIBLE);
        delete_image.setVisibility(View.VISIBLE);

//        send_image.setVisibility(View.INVISIBLE);
        info_image.setVisibility(View.INVISIBLE);
        title_text.setVisibility(View.INVISIBLE);
        add_image.setVisibility(View.INVISIBLE);
        complete_text.setVisibility(View.INVISIBLE);
    }
    //当点击右上角新建之后
    private void noteEditState() {
        Log.d("右上角","新建");
        back_btn.setVisibility(View.VISIBLE);
        complete_text.setVisibility(View.VISIBLE);

        info_image.setVisibility(View.INVISIBLE);
        title_text.setVisibility(View.INVISIBLE);
        add_image.setVisibility(View.INVISIBLE);
        delete_image.setVisibility(View.INVISIBLE);
//        send_image.setVisibility(View.INVISIBLE);
    }

    // 点击右上角完成之后
    public void doFinish(View view) {
        // 修改头部
        Log.d("完成","done");
        String noteContent = edittext_note.getText().toString();
        Log.d("内容是",noteContent);
        if (noteContent != null && noteContent.length() > 0) {
            //失去焦点 退出键盘
            edittext_note.clearFocus();

            noteModel.setNoteContent(noteContent);
            int noteId = dNoteDB.saveNote(noteModel);
            noteModel.setNoteId(noteId);
        } else {
            backToList(null);
        }
    }

    // 左上角登出后弹出框 此时点击背景 触发事件 回到主页面
    public void doClickBg(View view) {
        Log.d("MainActivity.java","doClickBg");
        CommonUtils.doFinish(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.left_top, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.recycle_bin:
                break;
            case R.id.login_out:
                break;
        }
        return true;
    }

    //    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        Log.d("sad","adsad");
//        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//            CommonUtils.doFinish(this);
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

}
