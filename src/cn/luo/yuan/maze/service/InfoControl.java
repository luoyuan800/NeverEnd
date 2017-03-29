package cn.luo.yuan.maze.service;

import android.view.View;
import cn.luo.yuan.maze.display.view.RollTextView;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by luoyuan on 2017/3/28.
 */
public class InfoControl {
    private RollTextView textView;

    public InfoControl(RollTextView textView) {
        this.textView = textView;
    }

    public void addMessage(String msg){
        textView.addMessage(msg);
    }
}
