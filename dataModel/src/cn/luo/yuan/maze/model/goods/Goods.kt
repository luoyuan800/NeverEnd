package cn.luo.yuan.maze.model.goods

import cn.luo.yuan.maze.model.IDModel
import cn.luo.yuan.maze.model.OwnedAble
import cn.luo.yuan.maze.utils.Field
import cn.luo.yuan.maze.utils.StringUtils
import java.io.Serializable

/**
 * Created by gluo on 5/5/2017.
 */
abstract class Goods : Serializable, IDModel, OwnedAble, Cloneable {
    companion object {
        private const val serialVersionUID: Long = Field.SERVER_VERSION
    }
    private var delete: Boolean = false
    var lock = false;
    var index = 0
    var medId: String = "";
    var count: Int = 0
    abstract var desc: String
    abstract var name: String
    abstract var price: Long

    override fun getId(): String {
        return medId
    }

    override fun setId(id: String?) {
        this.medId = id!!;
    }
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



    open fun use(properties: GoodsProperties): Boolean {
        if (count > 0) {
            count--;
            return true
        } else {
            return false;
        }
    }

    open fun load(properties: GoodsProperties){
        //Do nothing
    }

    open fun canLocalSell(): Boolean {
        return false
    }

    override fun clone(): Any {
        return super.clone()
    }

    override fun toString(): String {
        return name + " * " + count
    }
}