package cn.luo.yuan.maze.model.gift

import cn.luo.yuan.maze.model.effect.original.SkillRateEffect
import cn.luo.yuan.maze.service.InfoControlInterface

/**
 * Created by luoyuan on 2017/9/15.
 */
class SkillMaster:GiftHandler {
    override fun handler(control: InfoControlInterface?) {
        val pre = SkillRateEffect()
        pre.skillRate = 10f
        pre.tag = this.javaClass.simpleName
        control?.hero?.effects?.add(pre)
    }

    override fun unHandler(control: InfoControlInterface?) {
        val e = control?.hero?.effects?.find { it.tag == this.javaClass.simpleName }
        if(e!=null){
            control.hero?.effects?.remove(e)
        }
    }
}