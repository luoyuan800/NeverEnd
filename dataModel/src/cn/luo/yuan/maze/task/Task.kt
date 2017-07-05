package cn.luo.yuan.maze.task

import cn.luo.yuan.maze.model.Accessory
import cn.luo.yuan.maze.model.IDModel
import cn.luo.yuan.maze.model.Pet
import cn.luo.yuan.maze.model.goods.Goods
import cn.luo.yuan.maze.service.InfoControlInterface
import cn.luo.yuan.maze.utils.Field
import java.io.Serializable
import java.util.*

/**
 * Created by gluo on 6/12/2017.
 */
class Task(var name: String, var desc: String) : IDModel, Serializable {
    private var delete: Boolean = false
    override fun isDelete(): Boolean {
        return delete
    }

    override fun markDelete() {
        delete = true
    }

    companion object {
        private const val serialVersionUID: Long = Field.SERVER_VERSION
    }

    val preconditionTaskIds = mutableSetOf<String>()
    val preconditionNames = mutableMapOf<String, Int>()
    var startTime = 0L
    var finished = false
    var finishedTime = 0L
    var start = false;
    var point = 0
    var material = 0
    val goodes = mutableListOf<Goods>()
    val accessories = mutableListOf<Accessory>()
    val pets = mutableListOf<Pet>()

    override fun getId(): String {
        return taskId
    }

    override fun setId(id: String?) {
        //not support.
    }

    val taskId = UUID.randomUUID().toString()

    fun canStart(context: InfoControlInterface): Boolean {
        preconditionTaskIds
                .map { context.taskManager.findTaskById(it) }
                .filter { it == null || !it.finished }
                .forEach { return false }

        for ((key, value) in preconditionNames) {
            when (value) {
                Field.PET_TYPE -> {
                    if (context.dataManager.findPetByType(key).isEmpty()) {
                        return false
                    }
                }
                Field.GOODS_TYPE -> {
                    val goods = context.dataManager.loadGoods(key)
                    if (goods == null || goods.count <= 0) {
                        return false
                    }
                }
                Field.ACCESSORY_TYPE -> {
                    context.dataManager.findAccessoryByName(key) ?: return false
                }
            }
        }
        return true;
    }

    fun start() {
        start = true
        startTime = System.currentTimeMillis()
    }

    fun finished(context: InfoControlInterface) {
        finished = true
        finishedTime = System.currentTimeMillis()
        if (point > 0) {
            context.hero.point = context.hero.point + point
        }
        if (material > 0) {
            context.hero.material += material
        }
        if (goodes.isNotEmpty()) {
            for (goods in goodes) {
                context.dataManager.add(goods)
            }
        }
        for (accessory in accessories) {
            context.dataManager.saveAccessory(accessory)
        }
        for (pet in pets) {
            context.dataManager.savePet(pet)
        }
    }
}

