package cn.luo.yuan.maze.model.skill.race

import cn.luo.yuan.maze.model.skill.AtkSkill
import cn.luo.yuan.maze.model.skill.SkillParameter
import cn.luo.yuan.maze.model.skill.result.DoNoThingResult
import cn.luo.yuan.maze.model.skill.result.SkillResult
import cn.luo.yuan.maze.model.skill.result.SkipThisTurn

/**
 * Created by luoyuan on 2017/9/4.
 */
class Painkiller: AtkSkill() {
    private val model:RaceSkillModel = RaceSkillModel(this)

    override fun getName(): String {
        return "斩妖"
    }

    override fun getDisplayName(): String {
        return model.getDesc()
    }

    override fun canMount(parameter: SkillParameter?): Boolean {
        return isEnable
    }

    override fun invoke(parameter: SkillParameter): SkillResult {
        if(model.perform(parameter)){
            return SkipThisTurn()
        }else{
            return DoNoThingResult()
        }
    }

    override fun enable(parameter: SkillParameter?) {
        isEnable = true
    }

    override fun canEnable(parameter: SkillParameter?): Boolean {
        return model.isEnablePointEnough(parameter)
    }
    override fun getSkillName(): String {
        return model.skillName
    }
}