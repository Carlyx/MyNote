package com.brcorner.dnote.android.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.brcorner.dnote.android.R;

public class NotesListActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText editText_acco, editText_pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_list);
        Button button = (Button) findViewById(R.id.login_button);
        button.setOnClickListener(this);
        editText_acco = (EditText) findViewById(R.id.login_account_edit);
        editText_pass = (EditText) findViewById(R.id.login_password_edit);
    }
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.login_button:
                Intent intent = new Intent(NotesListActivity.this, MainActivity.class);
                startActivity(intent);
                String account = editText_acco.getText().toString();
                String password = editText_pass.getText().toString();
                if(account.equals("hls")){
                    if (password.equals("swe")){
//                        Intent intent = new Intent(NotesListActivity.this, MainActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(NotesListActivity.this, "密码错误",
                                Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(NotesListActivity.this, "账号错误",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
}
