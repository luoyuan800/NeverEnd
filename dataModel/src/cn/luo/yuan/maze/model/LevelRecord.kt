package cn.luo.yuan.maze.model

import cn.luo.yuan.maze.model.skill.Skill
import cn.luo.yuan.maze.utils.Field
import java.io.Serializable

/**
 * Copyright @Luo
 * Created by Gavin Luo on 9/11/2017.
 */
class LevelRecord(val id:String):Serializable {
    companion object {
        private const val serialVersionUID: Long = Field.SERVER_VERSION
    }
    var point:Long = 0
        set(value){
            priorPoint = field
            field = value
        }
    var priorPoint:Long = 0
    var win = 0L
    var lost = 0L
    var hero:Hero? = null
    val pets = mutableListOf<Pet>()
    val accessories = mutableListOf<Accessory>()
    val skills = mutableListOf<Skill>()
    var head:String = ""
}