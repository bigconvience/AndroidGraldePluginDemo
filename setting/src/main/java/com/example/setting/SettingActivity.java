package com.example.setting;

import android.os.Bundle;

public class SettingActivity extends AbsSettingActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        printName();
    }

    @Override
    protected void printName() {
        System.out.println("printSettingActivity");
    }
}
