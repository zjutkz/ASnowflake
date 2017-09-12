package com.github.zjutkz;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.github.zjutkz.snowflake.SnowFlake;

import java.util.HashSet;
import java.util.Set;

import githubzjutkz.com.app.R;

/**
 * Created by kangzhe on 17/9/12.
 */

public class MainActivity extends AppCompatActivity{

    private static final String TAG = "MainActivity";

    private Set<Long> ids = new HashSet<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void generate(View view){
        for(int i = 0;i < 5000;i++){
            ids.add(SnowFlake.getInstance().nextId());
        }

        if(ids.size() < 5000){
            Log.e(TAG, "generate failed: %s" + ids.size());
        }else {
            Log.d(TAG, "generate success");
        }
    }
}
