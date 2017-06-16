package cn.luo.yuan.maze.model.goods.types

import cn.luo.yuan.maze.model.goods.Goods
import cn.luo.yuan.maze.model.goods.GoodsProperties

/**
 * Created by gluo on 5/9/2017.
 */
class EmptyGoods : Goods {
    override fun setKeeperId(id: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setOwnerId(id: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setOwnerName(name: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setKeeperName(name: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getOwnerId(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getKeeperId(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getId(): String {
        return ""
    }

    override fun setId(id: String?) {
        //Donothing
    }

    override var price: Long
        get() = throw UnsupportedOperationException()
        set(value) {
        }
    override var count: Int
        get() = throw UnsupportedOperationException()
        set(value) {
        }
    override var desc: String
        get() = throw UnsupportedOperationException()
        set(value) {
        }
    override var name: String
        get() = throw UnsupportedOperationException()
        set(value) {
        }

    override fun load(properties: GoodsProperties) {
        throw UnsupportedOperationException()
    }
}