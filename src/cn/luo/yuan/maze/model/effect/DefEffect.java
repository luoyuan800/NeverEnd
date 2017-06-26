package cn.luo.yuan.maze.model.effect;

import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.utils.EncodeLong;
import cn.luo.yuan.maze.client.utils.Resource;
import cn.luo.yuan.maze.utils.Field;

/**
 * Created by luoyuan on 2017/3/18.
 */
public class DefEffect extends cn.luo.yuan.maze.model.effect.original.DefEffect {
    private static final long serialVersionUID = Field.SERVER_VERSION;
    private EncodeLong def = new EncodeLong(0);

    public long getDef() {
        return def.getValue();
    }

    public void setDef(long def) {
        this.def.setValue(def);
    }

    public String toString(){
        return Resource.getString(R.string.def_effect) + def;
    }

    @Override
    public void setValue(long value) {
        setDef(value);
    }

    @Override
    public Long getValue() {
        return getDef();
    }

    public cn.luo.yuan.maze.model.effect.original.DefEffect covertToOriginal(){
        cn.luo.yuan.maze.model.effect.original.DefEffect effect = new cn.luo.yuan.maze.model.effect.original.DefEffect();
        effect.setDef(getDef());
        return effect;
    }
    public Effect clone(){
        return (Effect) super.clone();
    }
}
