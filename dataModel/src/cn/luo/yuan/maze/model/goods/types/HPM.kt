package cn.luo.yuan.maze.model.goods.types

import cn.luo.yuan.maze.listener.HarmListener
import cn.luo.yuan.maze.listener.LostListener
import cn.luo.yuan.maze.model.HarmAble
import cn.luo.yuan.maze.model.Hero
import cn.luo.yuan.maze.model.NameObject
import cn.luo.yuan.maze.model.goods.Goods
import cn.luo.yuan.maze.model.goods.GoodsProperties
import cn.luo.yuan.maze.service.InfoControlInterface
import cn.luo.yuan.maze.service.ListenerService
import cn.luo.yuan.maze.utils.Field
import cn.luo.yuan.maze.utils.Field.Companion.SERVER_VERSION
import cn.luo.yuan.maze.utils.StringUtils

/**
 * Created by gluo on 5/5/2017.
 */
class HPM() : Goods(){
    companion object {
        private const val serialVersionUID: Long = Field.SERVER_VERSION
    }

    override fun canLocalSell(): Boolean {
        return true
    }

    override fun load(properties: GoodsProperties) {
        ListenerService.registerListener(HPMListener)
    }

    override var price = 300000L
    override var desc = "生命值低于50%的时候会自动使用立即恢复30%的生命值。"

    override var name = "小红药";
    override fun use(properties: GoodsProperties): Boolean {
        if (super.use(properties)) {
            properties.hero.hp += (properties.hero.upperHp * 0.3f).toLong()
            return true
        } else {
            return false
        }
    }

    val HPMListener = object : HarmListener {
        override fun har(atker: HarmAble, harmer: HarmAble, context: InfoControlInterface) {
            if(harmer is Hero) {
                val p = GoodsProperties(harmer);
                if(use(p)){
                    context.addMessage(" ${harmer.displayName}使用 $name 恢复了30%的生命值！")
                }
            }
        }

        override fun getKey(): String? {
            return "HPM";
        }

    }
}