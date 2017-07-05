package cn.luo.yuan.maze.model.goods.types

import cn.luo.yuan.maze.listener.LostListener
import cn.luo.yuan.maze.model.HarmAble
import cn.luo.yuan.maze.model.Hero
import cn.luo.yuan.maze.model.NameObject
import cn.luo.yuan.maze.model.goods.Goods
import cn.luo.yuan.maze.model.goods.GoodsProperties
import cn.luo.yuan.maze.service.ListenerService
import cn.luo.yuan.maze.utils.Field.Companion.SERVER_VERSION
import cn.luo.yuan.maze.utils.StringUtils

/**
 * Created by gluo on 5/5/2017.
 */
class Medallion() : Goods(){

    override fun load(properties: GoodsProperties) {
        ListenerService.registerListener(MedallionListener)
    }

    override var price = 100000L
    override var desc = "拥有这个物品可以在被击败时不会掉到第一层，你和你宠物原地半血复活。"

    override var name = "免死金牌";
    override fun use(properties: GoodsProperties): Boolean {
        if (super.use(properties)) {
            properties.hero.hp = properties.hero.maxHp
            for (pet in properties.hero.pets) {
                pet.hp = pet.maxHp
            }
            return true
        } else {
            return false
        }
    }

    val MedallionListener = object : LostListener {
        override fun lost(loser: Hero?, winner: HarmAble?) {
            val p = GoodsProperties(hero = loser!!);
            use(p)
        }

        override fun getKey(): String? {
            return "Medallion";
        }

    }
}