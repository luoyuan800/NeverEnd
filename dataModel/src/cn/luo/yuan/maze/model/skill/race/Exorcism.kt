package cn.luo.yuan.maze.model.skill.race

import cn.luo.yuan.maze.model.skill.AtkSkill
import cn.luo.yuan.maze.model.skill.SkillParameter
import cn.luo.yuan.maze.model.skill.SpecialSkill
import cn.luo.yuan.maze.model.skill.result.DoNoThingResult
import cn.luo.yuan.maze.model.skill.result.SkillResult
import cn.luo.yuan.maze.model.skill.result.SkipThisTurn
import cn.luo.yuan.maze.utils.Field

/**
 * Created by luoyuan on 2017/9/4.
 */
class Exorcism: AtkSkill(), SpecialSkill {
    companion object {
        private const val serialVersionUID: Long = Field.SERVER_VERSION
    }
    private val model:RaceSkillModel = RaceSkillModel(this)
    init {
        rate = 3f
    }
    override fun getName(): String {
        return "驱鬼"
    }

    override fun getDisplayName(): String {
        return model.getDesc()
    }

    override fun canMount(parameter: SkillParameter?): Boolean {
        return isEnable
    }

    override fun invoke(parameter: SkillParameter): SkillResult {
        return model.perform(parameter)
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