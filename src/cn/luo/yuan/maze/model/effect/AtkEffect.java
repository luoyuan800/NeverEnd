package cn.luo.yuan.maze.model.effect;

import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.utils.EncodeLong;
import cn.luo.yuan.maze.client.utils.Resource;
import cn.luo.yuan.maze.utils.Field;

/**
 * Created by luoyuan on 2017/3/18.
 */
public class AtkEffect extends cn.luo.yuan.maze.model.effect.original.AtkEffect {
    private static final long serialVersionUID = Field.SERVER_VERSION;
    private EncodeLong atk = new EncodeLong(0);

    public long getAtk() {
        return atk.getValue();
    }

    public void setAtk(long atk) {
        this.atk.setValue(atk);
    }

    public String toString(){
        return Resource.getString(R.string.atk_effect) + atk;
    }

    @Override
    public void setValue(long value) {
        setAtk(value);
    }

    @Override
    public Long getValue() {
        return getAtk();
    }
    public cn.luo.yuan.maze.model.effect.original.AtkEffect covertToOriginal(){
        cn.luo.yuan.maze.model.effect.original.AtkEffect atkEffect = new cn.luo.yuan.maze.model.effect.original.AtkEffect();
        atkEffect.setAtk(getAtk());
        return atkEffect;
    }
    public Effect clone(){
        return (Effect) super.clone();
    }
}
