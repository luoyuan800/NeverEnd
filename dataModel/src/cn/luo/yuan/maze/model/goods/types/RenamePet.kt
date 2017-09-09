package cn.luo.yuan.maze.model.goods.types

import cn.luo.yuan.maze.model.Parameter
import cn.luo.yuan.maze.model.goods.GoodsProperties
import cn.luo.yuan.maze.model.goods.UsableGoods
import cn.luo.yuan.maze.service.InfoControlInterface
import cn.luo.yuan.maze.utils.StringUtils

/**
 * Created by luoyuan on 2017/9/4.
 */
class RenamePet : UsableGoods() {
    override fun perform(properties: GoodsProperties): Boolean {
        val context :InfoControlInterface? = properties[Parameter.CONTEXT] as InfoControlInterface
        if(context!=null){
            val pet = context.random.randomItem(properties.hero.pets.toList())
            if(pet!=null) {
                context.showInputPopup(InfoControlInterface.InputListener { input, context ->
                    run {
                        if (StringUtils.isNotEmpty(input)) {
                            pet.myFirstName = input
                            context.dataManager.savePet(pet)
                        }
                    }
                }, pet.displayName)
                return true;
            }
        }
        return false
    }

    override var desc: String = "使用后随机选择一只<b>出战中</b>的宠物自定义前缀名。比如，一个宠物名字为‘普通的小蟑螂’，那么输入‘我’可以把宠物改名成‘我的小蟑螂’。"
    override var name: String = "宠物前缀"
    override var price: Long = 5000000
    override fun canLocalSell(): Boolean {
        return false
    }
}