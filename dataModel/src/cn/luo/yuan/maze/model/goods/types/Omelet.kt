package cn.luo.yuan.maze.model.goods.types

import cn.luo.yuan.maze.model.goods.GoodsProperties
import cn.luo.yuan.maze.model.goods.UsableGoods
import cn.luo.yuan.maze.service.InfoControlInterface
import cn.luo.yuan.maze.utils.Field

/**
 *
 * Created by luoyuan on 2017/7/16.
 */
class Omelet() : UsableGoods() {
    companion object {
        private const val serialVersionUID: Long = Field.SERVER_VERSION
    }
    override fun perform(properties: GoodsProperties): Boolean {
        val context = properties["context"] as InfoControlInterface
        val hero = properties.hero
        if (count > 0) {
            val index = context.random.nextInt(6)
            var msg = ""
            when (index) {
                0 -> {
                    hero.hp = (hero.maxHp * 0.6).toLong()
                    msg = "使用煎蛋恢复了60%的生命值。"
                }
                1 -> {
                    hero.hp = hero.upperHp
                    hero.pets.forEach {
                        it.hp = it.maxHp
                    }
                    msg = "使用煎蛋恢复了全部的生命值并且复活了所有宠物。"
                }
                2 -> {
                    msg = "使用煎蛋恢复了复活了所有宠物。"
                    hero.pets.forEach {
                        it.hp = it.maxHp
                    }
                }
                3 -> {
                    msg = "食用煎蛋肚子疼，导致生命值减少50%。"
                    hero.hp -= (hero.maxHp * 0.5).toLong()
                }
                4 -> {
                    msg = "食用了一个黑乎乎的煎蛋，导致生命值变为1。"
                    hero.setHp(1L)
                }
                5 -> {
                    msg = "使用煎蛋后掉进了一个洞"
                    if (context.random.nextInt(100) > 3) {
                        msg += ",不知道怎么跑到最高层了"
                        context.maze.level = context.maze.maxLevel
                    } else {
                        val level = context.random.nextLong(context.maze.maxLevel - 1000)
                        msg += ",不知道怎么着就跑到" + level + "层了"
                        context.maze.level = level
                    }
                }
            }
            context.showPopup(msg)
            return true
        }
        return false
    }

    override var desc: String = "传说中的煎蛋。吃下去之后发生随机事件。"

    override var name: String = "煎蛋"
    override var price: Long = 1000L
}