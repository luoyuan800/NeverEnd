package cn.luo.yuan.maze.server.persistence

import cn.luo.yuan.maze.model.Accessory
import cn.luo.yuan.maze.model.OwnedAble
import cn.luo.yuan.maze.model.Pet
import cn.luo.yuan.maze.model.goods.Goods
import cn.luo.yuan.maze.server.persistence.serialize.ObjectTable
import cn.luo.yuan.maze.utils.Field
import java.io.File

/**
 * Created by gluo on 6/15/2017.
 */
class WarehouseTable(root:File):Runnable{
    override fun run() {
        petWH.removeExpire(Field.WAREHOUSE_EXPIRE_TIME)
        accessoryWH.removeExpire(Field.WAREHOUSE_EXPIRE_TIME)
        goodsWH.removeExpire(Field.WAREHOUSE_EXPIRE_TIME)
    }

    val warehouseRoot = File(root, "warehouse")
    val petWH = ObjectTable<Pet>(Pet::class.java, warehouseRoot)
    val accessoryWH = ObjectTable<Accessory>(Accessory::class.java, warehouseRoot)
    val goodsWH = ObjectTable<Goods>(Goods::class.java, warehouseRoot)

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

    fun retrieve(id:String, type:Int): Any? {
        when(type){
            Field.PET_TYPE -> petWH.loadObject(id)
            Field.ACCESSORY_TYPE  -> accessoryWH.loadObject(id)
            Field.GOODS_TYPE -> goodsWH.loadObject(id)
        }
        return null
    }

    fun retrieveAll(keeperId:String):List<OwnedAble>{
        val res = mutableListOf<OwnedAble>();
        for(obj in petWH.loadAll()){
            filter(keeperId, obj, res)
        }
        res.addAll(accessoryWH.loadAll().filter { it is OwnedAble && it.keeperId == keeperId})
        res.addAll(goodsWH.loadAll().filter { it is OwnedAble && it.keeperId == keeperId})

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

