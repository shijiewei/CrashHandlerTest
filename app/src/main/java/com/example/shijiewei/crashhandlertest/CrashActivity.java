package com.example.shijiewei.crashhandlertest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CrashActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mButton;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash);
        initView();
    }

    private void initView() {
        mButton = (Button) findViewById(R.id.button1);
        mTextView = (TextView) findViewById(R.id.path);
        mTextView.setText(CrashHandler.PATH + CrashHandler.FILE_NAME + CrashHandler.FILE_NAME_SUFFIX);
        mButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == mButton) {
            // 在这里模拟异常抛出情况，人为地抛出一个运行时异常
            throw new RuntimeException("自定义异常：这是自己抛出的异常");
        }
    }
}
