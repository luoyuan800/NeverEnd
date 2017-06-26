package cn.luo.yuan.maze.model.goods.types

import cn.luo.yuan.maze.listener.LostListener
import cn.luo.yuan.maze.model.HarmAble
import cn.luo.yuan.maze.model.Hero
import cn.luo.yuan.maze.model.goods.Goods
import cn.luo.yuan.maze.model.goods.GoodsProperties
import cn.luo.yuan.maze.service.ListenerService
import cn.luo.yuan.maze.utils.Field.Companion.SERVER_VERSION
import cn.luo.yuan.maze.utils.StringUtils

/**
 * Created by gluo on 5/5/2017.
 */
class Medallion() : Goods {
    private var delete: Boolean = false
    override fun isDelete(): Boolean {
        return delete
    }

    override fun markDelete() {
        delete = true
    }
    override fun setHeroIndex(index: Int) {
        this.index = index
    }

    override fun getHeroIndex(): Int {
        return index
    }

    var index = 0
    override fun setKeeperName(name: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setOwnerName(name: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setKeeperId(id: String) {
       keeper = id
    }

    override fun setOwnerId(id: String) {
        keeper = id
    }

    var keeper = StringUtils.EMPTY_STRING;
    override fun getOwnerId(): String {
        return keeper
    }

    override fun getKeeperId(): String {
        return keeper
    }

    companion object {
        private const val serialVersionUID: Long = SERVER_VERSION
    }

    var medId: String = "";
    override fun getId(): String {
        return medId
    }

    override fun setId(id: String?) {
        this.medId = id!!;
    }

    override fun load(properties: GoodsProperties) {
        ListenerService.registerListener(MedallionListener)
    }

    override var price = 100000L
    override var desc = ""
    override var count = 0
        set(count) {
            this.count = count;
            if (this.count > 0) {

            }
        }
    override var name = "";
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
            var p = GoodsProperties(hero = loser!!);
            use(p)
        }

        override fun getKey(): String? {
            return "Medallion";
        }

    }
}