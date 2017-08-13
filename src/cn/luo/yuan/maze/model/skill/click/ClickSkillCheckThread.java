package cn.luo.yuan.maze.model.skill.click;

import android.util.Log;
import cn.luo.yuan.maze.client.display.dialog.SimplerDialogBuilder;
import cn.luo.yuan.maze.client.service.NeverEnd;
import cn.luo.yuan.maze.service.InfoControlInterface;

/**
 * Created by luoyuan on 2017/8/13.
 */
public class ClickSkillCheckThread implements Runnable {
    private ClickSkill clickSkill;
    private long lastClick;
    private InfoControlInterface context;
    private long speed;
    public ClickSkillCheckThread(ClickSkill clickSkill, InfoControlInterface context, long speed){
        this.clickSkill = clickSkill;
        lastClick = clickSkill.getClick();
        this.context = context;
    }
    @Override
    public void run() {
        long click = clickSkill.getClick() - this.lastClick;
        Log.d("maze", "Click check speed: " + click);
        if(click > speed){
            context.postTaskInUIThread(new Runnable() {
                @Override
                public void run() {
                    SimplerDialogBuilder.buildClickWarnDialog(((NeverEnd)context).getContext(), context.getRandom());
                }
            });
        }
        this.lastClick = clickSkill.getClick();
    }
}
