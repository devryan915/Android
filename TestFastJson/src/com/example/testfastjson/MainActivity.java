package com.example.testfastjson;

import android.app.Activity;
import android.os.Bundle;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String jsonStr="{\"id\":0,\"name\":\"Teacher 0\",\"students\":[{\"age\":18,\"id\":0,\"name\":\"Student0\"},{\"age\":19,\"id\":1,\"name\":\"Student1\"}]}";
        Teacher teacher=JSON.parseObject(jsonStr,new TypeReference<Teacher>(){});
        System.out.println(teacher);
    }
}
