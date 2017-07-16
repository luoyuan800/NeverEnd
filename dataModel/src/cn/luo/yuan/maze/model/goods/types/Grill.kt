package cn.luo.yuan.maze.model.goods.types

import cn.luo.yuan.maze.model.goods.GoodsProperties
import cn.luo.yuan.maze.model.goods.UsableGoods
import cn.luo.yuan.maze.service.InfoControlInterface
import cn.luo.yuan.maze.utils.Field

/**
 *
 * Created by luoyuan on 2017/7/16.
 */
class Grill() : UsableGoods() {
    companion object {
        private const val serialVersionUID: Long = Field.SERVER_VERSION
    }
    override fun perform(properties: GoodsProperties): Boolean {
        if (count > 0) {
                val context = properties["context"] as InfoControlInterface
                var desc = "没有任何作用！"
            val random = context.random
            val hero = properties.hero
            val index = random.nextInt(10)
                var value : Long
                when (index) {
                    0 -> {
                        value = random.nextLong(hero.str / 50000) + 1
                        if (value > 100) {
                            value = random.nextLong(100)
                        }
                        desc = "吃到了带血丝的烤肉，力量增加了$value。但是要小心寄生虫感染哦！"
                        hero.str += value
                    }
                    1 -> {
                        value = random.nextLong(hero.agi / 20000) + 1
                        desc = "吃到了烤焦的烤肉，黑暗属性的烤肉导致你的体内发生了变异，敏捷增加了$value。记得多备纸巾随时准备上厕所！"
                        if (value > 100) {
                            value = random.nextLong(100)
                        }
                        hero.agi += value
                    }
                }
               context.showPopup(desc)
            return true
            }
        return false
    }

    override var desc: String = "一团黑乎乎的烤肉，不知道是什么味道。"
    override var name: String = "烤肉"
    override var price = 1000L
}