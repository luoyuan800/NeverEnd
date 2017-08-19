package cn.luo.yuan.maze.model.gift

import cn.luo.yuan.maze.model.effect.original.PetRateEffect
import cn.luo.yuan.maze.service.InfoControlInterface

/**
 * Created by luoyuan on 2017/8/16.
 */
class Pokemon:GiftHandler {
    override fun handler(control: InfoControlInterface?) {
        val config = control?.dataManager?.loadConfig();
        if(config!=null){
            config.isPetGift = true
        }
        val pre = PetRateEffect()
        pre.petRate = 5f
        pre.tag = this.javaClass.simpleName
        control?.hero?.effects?.add(pre)
    }

    override fun unHandler(control: InfoControlInterface?) {
        val config = control?.dataManager?.loadConfig();
        if(config!=null){
            config.isPetGift = false
        }
        control?.hero?.effects?.removeIf { it.tag == this.javaClass.simpleName }
    }
}