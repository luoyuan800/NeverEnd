package cn.luo.yuan.maze.model.goods

import cn.luo.yuan.maze.model.IDModel
import cn.luo.yuan.maze.model.OwnedAble
import cn.luo.yuan.maze.model.Parameter
import cn.luo.yuan.maze.utils.EncodeInteger
import cn.luo.yuan.maze.utils.EncodeLong
import cn.luo.yuan.maze.utils.Field
import cn.luo.yuan.maze.utils.StringUtils
import java.io.Serializable

/**
 * Created by gluo on 5/5/2017.
 */
abstract class Goods : Serializable, IDModel, OwnedAble,Cloneable {
    companion object {
        private const val serialVersionUID: Long = Field.SERVER_VERSION
    }
    private var delete: Boolean = false
    var lock = false;
    var index = 0
    var medId: String = "";
    private var count: EncodeInteger = EncodeInteger(0)
    abstract var desc: String
    abstract var name: String
    abstract var price: Long

    fun getCount():Int{
        return count.value
    }

    fun setCount(count:Int){
        this.count.value = count
    }

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
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setOwnerName(name: String?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setKeeperId(id: String?) {
        if(id !=null) {
            keeper = id
        }
    }

    override fun setOwnerId(id: String) {
        keeper = id
    }

    var keeper:String = StringUtils.EMPTY_STRING;
    override fun getOwnerId(): String {
        return keeper
    }

    override fun getKeeperId(): String {
        return keeper
    }



    open fun use(properties: GoodsProperties): Boolean {
        var count = 1
        if(this is BatchUseGoods){
            count = properties[Parameter.COUNT] as Int
        }
        if (!lock && getCount() >= count) {
            setCount(getCount() - count)
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

    override fun clone(): Goods {
        val clone = super.clone() as Goods
        clone.count = EncodeInteger(getCount().toLong())
        return clone
    }

    override fun toString(): String {
        return name + " * " + count
    }
}