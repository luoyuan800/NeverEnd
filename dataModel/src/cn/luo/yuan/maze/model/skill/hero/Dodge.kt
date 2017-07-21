package cn.luo.yuan.maze.model.skill.hero

import cn.luo.yuan.maze.model.Data
import cn.luo.yuan.maze.model.Hero
import cn.luo.yuan.maze.model.skill.DefSkill
import cn.luo.yuan.maze.model.skill.SkillParameter
import cn.luo.yuan.maze.model.skill.UpgradeAble
import cn.luo.yuan.maze.model.skill.result.SkillResult
import cn.luo.yuan.maze.model.skill.result.SkipThisTurn
import cn.luo.yuan.maze.utils.StringUtils

/**
 * Created by luoyuan on 2017/7/16.
 */
class Dodge():DefSkill(), UpgradeAble {
    private var level = 1L
    private val model = HeroModel(this)
    override fun upgrade(parameter: SkillParameter?): Boolean {
        if(rate + 0.3 > Data.RATE_MAX/10){
            return false
        }
        val hero = parameter!!.owner
        if (hero is Hero) {
            rate += 0.3f
            level++
            return true
        }
        return false
    }

    override fun getLevel(): Long {
        return level
    }

    override fun getName(): String {
        return "闪避 X $level"
    }

    override fun getDisplayName(): String {
        val builder = StringBuilder()
        builder.append("防御技能<br>")
        builder.append(StringUtils.formatPercentage(rate)).append("概率闪避攻击")
        return builder.toString()
    }

    override fun canMount(parameter: SkillParameter?): Boolean {
        return model.canMount(parameter)
    }

    override fun invoke(parameter: SkillParameter?): SkillResult {
        return SkipThisTurn()
    }

    override fun enable(parameter: SkillParameter?) {
        isEnable = true
    }
    override fun canEnable(parameter: SkillParameter): Boolean {
        return model.canEnable(parameter) ;
    }

    override fun getSkillName(): String {
        return model.skillName
    }

    override fun canUpgrade(parameter: SkillParameter): Boolean {
        return isEnable && parameter.owner is Hero && rate < Data.RATE_MAX && isUpgradePointEnough(parameter)
    }
}