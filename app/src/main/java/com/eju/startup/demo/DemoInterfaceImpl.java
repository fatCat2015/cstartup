package com.eju.startup.demo;

import android.util.Log;

import org.reflections.Reflections;

import java.util.Set;

public class DemoInterfaceImpl implements DemoInterface{

    public void test(){
        Reflections reflections=new Reflections("com.eju");
        Set<Class<? extends DemoInterface>> lists = reflections.getSubTypesOf(DemoInterface.class);
        Log.i("sck220", "test: "+lists.size());
    }
}
