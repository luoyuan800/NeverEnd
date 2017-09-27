package cn.luo.yuan.maze.model.skill.hero

import cn.luo.yuan.maze.model.Data
import cn.luo.yuan.maze.model.HarmAble
import cn.luo.yuan.maze.model.Hero
import cn.luo.yuan.maze.model.NameObject
import cn.luo.yuan.maze.model.skill.DefSkill
import cn.luo.yuan.maze.model.skill.SkillParameter
import cn.luo.yuan.maze.model.skill.UpgradeAble
import cn.luo.yuan.maze.model.skill.result.HarmResult
import cn.luo.yuan.maze.model.skill.result.SkillResult
import cn.luo.yuan.maze.service.BattleMessageInterface
import cn.luo.yuan.maze.service.BattleServiceBase
import cn.luo.yuan.maze.utils.Field
import cn.luo.yuan.utils.Random
import cn.luo.yuan.maze.utils.StringUtils


/**
 * Created by luoyuan on 2017/7/16.
 */
class FightBack() : DefSkill(), UpgradeAble {
    companion object {
        private const val serialVersionUID = Field.SERVER_VERSION
    }
    private var percent = 10f
    private var level = 1L
    private val model = HeroModel(this)
    override fun upgrade(parameter: SkillParameter?): Boolean {
        if (percent + 1 > Data.RATE_MAX/3) {
            return false
        }
        val hero = parameter!!.owner
        if (hero is Hero) {
            level++
            percent++
            if(rate < Data.RATE_MAX/5) {
                rate += 0.5f
            }
            return true
        }

        return false
    }

    override fun getLevel(): Long {
        return level
    }

    override fun getName(): String {
        return "反弹 X$level"
    }

    override fun getDisplayName(): String {
        val builder = StringBuilder()
        builder.append("防御技能<br>")
        builder.append(StringUtils.formatPercentage(rate)).append("概率反弹").append(StringUtils.formatPercentage(percent)).append("的攻击伤害")
        builder.append("<br>已经使用：${StringUtils.formatNumber(useTime)}次")
        return builder.toString()
    }

    override fun canMount(parameter: SkillParameter?): Boolean {
        return model.canMount(parameter)
    }

    override fun canUpgrade(parameter: SkillParameter): Boolean {
        return isEnable && parameter.owner is Hero && percent < Data.RATE_MAX && model.isUpgradePointEnough(parameter)
    }

    override fun invoke(parameter: SkillParameter): SkillResult {
        val atker: HarmAble = parameter.get(SkillParameter.ATKER)
        val defender: HarmAble = parameter.owner as HarmAble
        val minHarm: Long = parameter.get(SkillParameter.MINHARM)
        val random: Random = parameter.get(SkillParameter.RANDOM)
        val battleMessage = parameter.get<BattleMessageInterface>(SkillParameter.MESSAGE)
        val harm = BattleServiceBase.getHarm(atker, defender, minHarm, random, battleMessage)
        defender.hp -= harm
        if (defender is NameObject && atker is NameObject) {
            battleMessage.harm(atker, defender, harm)
        }

        val rHarm: Long = Math.round(harm * (percent / 100f)).toLong()
        val result = HarmResult()
        result.isBack = true
        result.harm = rHarm
        return result
    }

    override fun enable(parameter: SkillParameter?) {
        isEnable = true
    }

    override fun canEnable(parameter: SkillParameter): Boolean {
        return model.canEnable(parameter);
    }


    override fun getSkillName(): String {
        return model.skillName
    }
}