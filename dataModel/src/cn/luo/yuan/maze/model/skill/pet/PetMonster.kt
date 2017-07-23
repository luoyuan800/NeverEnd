package cn.luo.yuan.maze.model.skill.pet

import cn.luo.yuan.maze.model.Data
import cn.luo.yuan.maze.model.Hero
import cn.luo.yuan.maze.model.skill.PropertySkill
import cn.luo.yuan.maze.model.skill.SkillParameter
import cn.luo.yuan.maze.model.skill.UpgradeAble

/**
 * Created by luoyuan on 2017/7/23.
 */
class PetMonster:PropertySkill(), UpgradeAble {
    private var level = 0L
    var petCount = 3
    override fun upgrade(parameter: SkillParameter): Boolean {
        if(petCount < Data.MAX_PET_COUNT/5){
            petCount ++
            val hero = parameter.owner
            if(hero is Hero){
                hero.petCount ++
            }
            level ++
            return true
        }
        return false
    }

    override fun getLevel(): Long {
        return level
    }

    override fun getName(): String {
        return "宠物大师 X $level"
    }

    override fun getDisplayName(): String {
        return "可携带出战宠物上限 + $petCount"
    }

    override fun enable(parameter: SkillParameter) {
        isEnable = true
        val hero = parameter.owner
        if(hero is Hero){
            hero.petCount += petCount
        }
    }

    override fun disable(parameter: SkillParameter) {
        isEnable = false
        val hero = parameter.owner
        if(hero is Hero){
            hero.petCount -= petCount
        }
    }

    override fun getSkillName(): String {
        return model.skillName
    }

    private val model = PetModel(this)
}