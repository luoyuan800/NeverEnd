package cn.luo.yuan.maze.model.goods.types

import cn.luo.yuan.maze.model.Parameter
import cn.luo.yuan.maze.model.goods.GoodsProperties
import cn.luo.yuan.maze.model.goods.UsableGoods
import cn.luo.yuan.maze.service.InfoControlInterface
import cn.luo.yuan.maze.utils.StringUtils

/**
 * Created by luoyuan on 2017/9/4.
 */
class Scalpel: UsableGoods() {
    override fun perform(properties: GoodsProperties): Boolean {
        val context : InfoControlInterface? = properties[Parameter.CONTEXT] as InfoControlInterface
        if(context!=null){
            val pet = context.random.randomItem(properties.hero.pets.toList())
            if(pet!=null) {
                pet.sex = if(pet.sex == 0) 1 else 0
                context.dataManager.savePet(pet)
                return true;
            }
        }
        return false
    }

    override var desc: String = "使用后随机选择一个<b>出战中</b>的宠物转换性别！无法选择未孵化的宠物。"
    override var name: String = "手术刀"
    override var price: Long = 700000
}