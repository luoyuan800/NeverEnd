package cn.luo.yuan.maze.model.effect;

import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.utils.EncodeLong;
import cn.luo.yuan.maze.utils.Field;
import cn.luo.yuan.maze.client.utils.Resource;

/**
 * Created by luoyuan on 2017/3/18.
 */
public class HpEffect extends cn.luo.yuan.maze.model.effect.original.HpEffect {
    private static final long serialVersionUID = Field.SERVER_VERSION;
    private EncodeLong hp = new EncodeLong(0);

    public long getHp() {
        return hp.getValue();
    }

    public void setHp(long hp) {
        this.hp.setValue(hp);
    }

    public String toString() {
        return Resource.getString(R.string.hp_effect) + hp;
    }

    @Override
    public Long getValue() {
        return getHp();
    }

    @Override
    public void setValue(long value) {
        setHp(value);
    }

    public cn.luo.yuan.maze.model.effect.original.HpEffect covertToOriginal() {
        cn.luo.yuan.maze.model.effect.original.HpEffect effect = new cn.luo.yuan.maze.model.effect.original.HpEffect();
        effect.setHp(getHp());
        return effect;
    }
    public Effect clone(){
        return (Effect) super.clone();
    }
}
