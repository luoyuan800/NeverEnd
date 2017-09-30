package cn.luo.yuan.maze.model.goods.types

import cn.luo.yuan.maze.model.Parameter
import cn.luo.yuan.maze.model.goods.GoodsProperties
import cn.luo.yuan.maze.model.goods.UsableGoods
import cn.luo.yuan.maze.service.InfoControlInterface
import cn.luo.yuan.maze.utils.Field

/**
 * Copyright @Luo
 * Created by Gavin Luo on 9/28/2017.
 */
class Invincible : UsableGoods() {
    companion object {
        private const val serialVersionUID: Long = Field.SERVER_VERSION
    }

    override fun perform(properties: GoodsProperties): Boolean {
        val context = properties[Parameter.CONTEXT] as InfoControlInterface
        if(!context.isWeakling && !context.isInvincible){
            context.startInvincible(30 * 1000, 60 * 1000)
            return true
        }
        if(context.isWeakling) {
            context.showPopup("虚弱状态下无法使用")
        }else if(context.isInvincible){
            context.showPopup("无敌状态下无法使用")
        }
        return false
    }

    override var desc: String = "生命诚可贵，磕药要谨慎！进入３０秒的无敌状态，但是之后会进入6０秒的虚弱状态：攻击、防御、生命均减少５０％．虚弱/无敌状态下无法再次进入无敌"
    override var name: String = "无敌药水"

    override var price: Long = 100000000L

    override fun canLocalSell(): Boolean {
        return false
    }

}