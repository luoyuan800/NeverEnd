package cn.luo.yuan.maze.model.task;

import cn.luo.yuan.maze.model.IDModel;
import cn.luo.yuan.maze.service.InfoControlInterface;
import cn.luo.yuan.maze.utils.StringUtils;

import java.util.Collections;
import java.util.List;

/**
 * Copyright @Luo
 * Created by Gavin Luo on 8/1/2017.
 */
public class MateTask extends Task {
    private long mate;
    @Override
    public void finished(InfoControlInterface context) {
        context.getHero().setMaterial(context.getHero().getMaterial() + mate);
    }

    @Override
    public String getAward() {
        return "可以获得锻造：" + StringUtils.formatNumber(mate);
    }

    @Override
    public List<IDModel> predecessorItems() {
        return Collections.emptyList();
    }


}
