package cn.luo.yuan.maze.server.persistence

import cn.luo.yuan.maze.model.Accessory
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

    fun retrieve(id:String, type:Int): Any? {
        when(type){
            Field.PET_TYPE -> petWH.loadObject(id)
            Field.ACCESSORY_TYPE  -> accessoryWH.loadObject(id)
            Field.GOODS_TYPE -> goodsWH.loadObject(id)
        }
        return null
    }


}

