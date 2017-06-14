package cn.luo.yuan.maze.model.effect;

import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.utils.Resource;
import cn.luo.yuan.maze.utils.Field;

/**
 * Created by luoyuan on 2017/3/18.
 */
public class AgiEffect extends cn.luo.yuan.maze.model.effect.original.AgiEffect {
    private static final long serialVersionUID = Field.SERVER_VERSION;
    private long agi;

    public long getAgi() {
        return agi;
    }

    public void setAgi(long agi) {
        this.agi = agi;
    }
    public String toString(){
        return Resource.getString(R.string.agi_effect) + agi;
    }


    @Override
    public void setValue(long value) {
        setAgi(value);
    }

    @Override
    public Long getValue() {
        return getAgi();
    }

    public cn.luo.yuan.maze.model.effect.original.AgiEffect covertToOriginal(){
        cn.luo.yuan.maze.model.effect.original.AgiEffect agiEffect = new cn.luo.yuan.maze.model.effect.original.AgiEffect();
        agiEffect.setAgi(getAgi());
        return agiEffect;
    }
}
