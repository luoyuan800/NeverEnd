package cn.luo.yuan.maze.model

import cn.luo.yuan.maze.model.skill.EmptySkill
import cn.luo.yuan.maze.model.skill.Skill
import cn.luo.yuan.maze.service.AccessoryHelper
import cn.luo.yuan.maze.utils.Field
import java.io.Serializable
import java.util.concurrent.locks.ReentrantReadWriteLock

/**
 * Copyright @Luo
 * Created by Gavin Luo on 9/11/2017.
 */
open class LevelRecord(private var id:String):IDModel, Serializable, Cloneable {
    override fun getId(): String {
        return id
    }

    override fun isDelete(): Boolean {
        return delete
    }

    override fun markDelete() {
        this.delete = true
    }

    override fun setId(id: String) {
        this.id = id
    }
    constructor(hero: Hero):this(hero.id){
        this.hero = hero
        this.skills.addAll(hero.skills.toList().filter { it !is EmptySkill })
        this.accessories.addAll(hero.accessories)
        this.pets.addAll(hero.pets)
    }
    companion object {
        private const val serialVersionUID: Long = Field.SERVER_VERSION
    }
    private var delete = false
    val lock = id
    var waitTrun = 0
    var point:Long = 0
        set(value){
            priorPoint = field
            if(value < 0){
                field = 0
            }else {
                field = value
            }
        }
    var priorPoint:Long = 0
    var win = 0L
    var lost = 0L
    var hero:Hero? = null
        get() {
            if(field!=null){
                if(field!!.pets.isEmpty()){
                    field!!.pets.addAll(pets)
                }
                if(field!!.accessories.isEmpty()){
                    for(acc in accessories){
                        AccessoryHelper.mountAccessory(acc, field, false, null)
                    }
                    AccessoryHelper.judgeElementEnable(field, isElementer)
                }
            }
            return field
        }
    val pets = mutableListOf<Pet>()
    val accessories = mutableListOf<Accessory>()
    val skills = mutableListOf<Skill>()
    var head:String = ""
    var isElementer = false

    override fun clone(): LevelRecord {
        return super.clone() as LevelRecord
    }

}