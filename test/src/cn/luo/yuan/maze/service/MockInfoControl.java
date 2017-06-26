package cn.luo.yuan.maze.service;

import android.content.Context;
import cn.luo.yuan.maze.client.service.GameContext;
import cn.luo.yuan.maze.utils.Random;

/**
 * Created by gluo on 5/16/2017.
 */
public class MockInfoControl extends GameContext {
    private Random random = new Random(System.currentTimeMillis());

    public MockInfoControl(Context context) {
        super(context);
    }

    public MockInfoControl(){
        super(null);
    }

    public Random getRandom(){
        return random;
    }
}
