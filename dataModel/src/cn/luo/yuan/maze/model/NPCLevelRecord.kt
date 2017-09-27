package cn.luo.yuan.maze.model

import cn.luo.yuan.maze.model.real.action.AtkAction
import cn.luo.yuan.maze.model.real.action.AtkSkillAction
import cn.luo.yuan.maze.model.real.action.DefSkillAction
import cn.luo.yuan.maze.model.real.action.RealTimeAction
import cn.luo.yuan.maze.model.skill.AtkSkill
import cn.luo.yuan.maze.model.skill.DefSkill
import cn.luo.yuan.maze.utils.Field
import cn.luo.yuan.utils.Random
import java.util.*

/**
 * Copyright @Luo
 * Created by Gavin Luo on 9/14/2017.
 */
class NPCLevelRecord(hero:Hero):LevelRecord(hero) {

    companion object {
        private const val serialVersionUID: Long = Field.SERVER_VERSION
    }
    var sex = 0
    val message = mutableListOf<String>()
        get(){
            if(field == null){
                field = mutableListOf<String>()
            }
            return field
        }

    val tips = mutableMapOf("win1" to "渣渣！")
    var level= 300
    fun randomAction(random: Random, actionPoint:Int):RealTimeAction{
        var index = 5 + skills.size + actionPoint/100
        index = random.nextInt(index)
        if(index >= 5){
            val skill = random.randomItem(skills)
            if(skill is AtkSkill){
                return AtkSkillAction(UUID.randomUUID().toString(), id, skill)
            } else if(skill is DefSkill){
                return DefSkillAction(UUID.randomUUID().toString(), id, skill)
            }
        }
        return AtkAction(UUID.randomUUID().toString(), id)
    }
}