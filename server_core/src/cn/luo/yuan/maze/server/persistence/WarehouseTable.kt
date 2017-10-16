package cn.luo.yuan.maze.server.persistence

import cn.luo.yuan.maze.model.Accessory
import cn.luo.yuan.maze.model.OwnedAble
import cn.luo.yuan.maze.model.Pet
import cn.luo.yuan.maze.model.goods.Goods
import cn.luo.yuan.serialize.FileObjectTable
import cn.luo.yuan.maze.utils.Field
import java.io.File

/**
 * Created by gluo on 6/15/2017.
 */
class WarehouseTable(root:File):Runnable{
    override fun run() {
        //petWH.removeExpire(Field.WAREHOUSE_EXPIRE_TIME)
        //accessoryWH.removeExpire(Field.WAREHOUSE_EXPIRE_TIME)
        //goodsWH.removeExpire(Field.WAREHOUSE_EXPIRE_TIME)
    }

    val warehouseRoot = File(root, "warehouse")
    val petWH = FileObjectTable<Pet>(Pet::class.java, warehouseRoot)
    val accessoryWH = FileObjectTable<Accessory>(Accessory::class.java, warehouseRoot)
    val goodsWH = FileObjectTable<Goods>(Goods::class.java, warehouseRoot)

    fun store(obj:Any){
        when(obj){
            is Pet -> petWH.save(obj)
            is Accessory -> accessoryWH.save(obj)
            is Goods -> goodsWH.save(obj)
        }
    }

    fun delete(obj:Any?){
        when(obj){
            is Pet -> petWH.delete(obj.id)
            is Accessory -> accessoryWH.delete(obj.id)
            is Goods -> goodsWH.delete(obj.id)
        }
    }

    fun retrieve(id:String, type:Int): OwnedAble? {
        when(type){
            Field.PET_TYPE -> return petWH.loadObject(id)
            Field.ACCESSORY_TYPE  -> return accessoryWH.loadObject(id)
            Field.GOODS_TYPE -> return goodsWH.loadObject(id)
        }
        return null
    }

    fun retrieveAll(keeperId:String):List<OwnedAble>{
        val res = mutableListOf<OwnedAble>();
        res.addAll(petWH.loadAll().filter { it is OwnedAble && it.keeperId == keeperId})
        res.addAll(accessoryWH.loadAll().filter { it is OwnedAble && it.keeperId == keeperId})
        res.addAll(goodsWH.loadAll().filter { it is OwnedAble && it.keeperId == keeperId})

        return res;
    }

    fun retrieveAll(keeperId:String, type:Int):List<OwnedAble>{
        val res = mutableListOf<OwnedAble>();
        when(type){
            Field.PET_TYPE -> res.addAll(petWH.loadAll().filter { it is OwnedAble && it.keeperId == keeperId })
            Field.ACCESSORY_TYPE -> res.addAll(accessoryWH.loadAll().filter { it is OwnedAble && it.keeperId == keeperId})
            Field.GOODS_TYPE -> res.addAll(goodsWH.loadAll().filter { it is OwnedAble && it.keeperId == keeperId})
        }
        return res;
    }

    private fun filter(keeperId: String, obj: Pet, res: MutableList<OwnedAble>) {
        if (obj is OwnedAble) {
            if (obj.keeperId == keeperId) {
                res.add(obj);
            }
        }
    }


}

