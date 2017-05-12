package cn.luo.yuan.maze.model.goods.types

import cn.luo.yuan.maze.listener.LostListener
import cn.luo.yuan.maze.model.HarmAble
import cn.luo.yuan.maze.model.Hero
import cn.luo.yuan.maze.model.goods.Goods
import cn.luo.yuan.maze.model.goods.GoodsProperties
import cn.luo.yuan.maze.service.ListenerService

/**
 * Created by gluo on 5/5/2017.
 */
class Medallion : Goods {

    override fun load(properties: GoodsProperties) {
        ListenerService.registerListener(MedallionListener)
    }
    private val serialVersionUID = 1L
    override var price = 100000L
    override var desc = ""
    override var count = 0
    set(count) {
        this.count = count;
        if(this.count > 0){

        }
    }
    override var name = "";
    override fun use(properties: GoodsProperties): Boolean {
        if(super.use(properties)){
            properties.hero.hp = properties.hero.maxHp
            for(pet in properties.hero.pets){
                pet.hp = pet.maxHP
            }
            return true
        }else {
            return false
        }
    }

    val MedallionListener = object : LostListener {
        override fun lost(loser: Hero?, winner: HarmAble?) {
            var p = GoodsProperties(hero = loser!!);
            use(p)
        }

        override fun getKey(): String? {
            return "Medallion";
        }

    }
}