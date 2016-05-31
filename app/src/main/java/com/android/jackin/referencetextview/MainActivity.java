package com.android.jackin.referencetextview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.android.jackin.library.ReferenceTextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ReferenceTextView dynamicSetContent = (ReferenceTextView) findViewById(R.id.dynamic_set_content);
        dynamicSetContent.setReferenceContent("@DynamicAddUser");
    }
}
