package cn.luo.yuan.maze.model;

import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.client.utils.Resource;
import cn.luo.yuan.maze.utils.StringUtils;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by luoyuan on 2017/3/19.
 */
public class HeroIndex extends cn.luo.yuan.maze.model.index.HeroIndex {

    public String toString(){
        DateFormat dateInstance = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.MEDIUM);
        return "<b>" + getName() + "</b>(" + getElement() + ")"+
                "<br>" + Resource.getString(R.string.level) + StringUtils.formatNumber(getLevel()) + "/" + StringUtils.formatNumber(getMaxLevel())
                + "<br>" + Resource.getString(R.string.created) + StringUtils.formatData(getCreated())
                + "<br>" + Resource.getString(R.string.last_update) + StringUtils.formatData(getLastUpdated());

    }

}
