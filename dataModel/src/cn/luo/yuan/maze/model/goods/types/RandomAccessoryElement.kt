package cn.luo.yuan.maze.model.goods.types

import cn.luo.yuan.maze.model.Accessory
import cn.luo.yuan.maze.model.Element
import cn.luo.yuan.maze.model.Parameter
import cn.luo.yuan.maze.model.goods.GoodsProperties
import cn.luo.yuan.maze.model.goods.UsableGoods
import cn.luo.yuan.maze.service.InfoControlInterface

/**
 * Created by luoyuan on 2017/9/4.
 */
class RandomAccessoryElement: UsableGoods() {
    override fun perform(properties: GoodsProperties): Boolean {
        val context:InfoControlInterface? = properties[Parameter.CONTEXT] as InfoControlInterface
        val hero = properties.hero
        if(context!=null){
            try {

            val acc:Accessory = context.random.randomItem(hero.accessories.toList())!!
            acc.element = context.random.randomItem(Element.values())
            context.dataManager.saveAccessory(acc)
            return true
            }catch (e :Exception){
                return false
            }
        }
        return false
    }

    override var desc: String = "随机选择一件<br>身上</b>的装备，随机改变该装备的五行属性"
    override var name: String = "五行珠"
    override var price: Long = 900000
    override fun canLocalSell(): Boolean {
        return false
    }
}