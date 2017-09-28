package cn.luo.yuan.maze.model.goods.types

import cn.luo.yuan.maze.model.Parameter
import cn.luo.yuan.maze.model.goods.GoodsProperties
import cn.luo.yuan.maze.model.goods.UsableGoods
import cn.luo.yuan.maze.service.InfoControlInterface

/**
 * Copyright @Luo
 * Created by Gavin Luo on 9/28/2017.
 */
class Invincible : UsableGoods() {
    override fun perform(properties: GoodsProperties): Boolean {
        val context = properties[Parameter.CONTEXT] as InfoControlInterface
        if(!context.isWeakling){
            context.startInvincible(30 * 1000, 30 * 1000)
            return true
        }
        return false
    }

    override var desc: String = "磕药！进入３０秒的无敌状态，但是之后会进入３０秒的虚弱状态：攻击、防御、生命均减少５０％．虚弱状态下无法进入无敌"
    override var name: String = "无敌药水"

    override var price: Long = 100000000L

    override fun canLocalSell(): Boolean {
        return false
    }

}