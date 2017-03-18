package cn.luo.yuan.maze.utils;

import android.content.Context;

/**
 * Created by luoyuan on 2017/3/18.
 */
public class Resource {
    private static Context context;
    public static void init(Context context){
        Resource.context = context;
    }
    public static String getString(int id){
        return context.getResources().getString(id);
    }
}
