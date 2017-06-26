package cn.luo.yuan.maze.model.effect;

import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.utils.EncodeLong;
import cn.luo.yuan.maze.utils.Field;
import cn.luo.yuan.maze.client.utils.Resource;

/**
 * Created by luoyuan on 2017/3/18.
 */
public class StrEffect extends cn.luo.yuan.maze.model.effect.original.StrEffect {
    private static final long serialVersionUID = Field.SERVER_VERSION;
    private EncodeLong str = new EncodeLong(0);

    public long getStr() {
        return str.getValue();
    }

    public void setStr(long str) {
        this.str.setValue(str);
    }

    public String toString() {
        return Resource.getString(R.string.str_effect) + str;
    }

    @Override
    public Long getValue() {
        return getStr();
    }

    @Override
    public void setValue(long value) {
        setStr(value);
    }

    public cn.luo.yuan.maze.model.effect.original.StrEffect covertToOriginal() {
        cn.luo.yuan.maze.model.effect.original.StrEffect effect = new cn.luo.yuan.maze.model.effect.original.StrEffect();
        effect.setStr(getStr());
        return effect;
    }

}
