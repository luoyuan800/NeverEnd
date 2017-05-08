package cn.luo.yuan.maze.model.goods

/**
 * Created by gluo on 5/5/2017.
 */
class Medallion : Goods {
    override var desc = ""
    override var count = 0
    override var name = "";
    override fun use(properties: GoodsProperties): Boolean {
        if(super.use(properties)){
            properties.hero?.hp = properties.hero.maxHp
            for(pet in properties.hero?.pets){
                pet.hp = pet.maxHP
            }
            return true
        }else {
            return false
        }
    }
}