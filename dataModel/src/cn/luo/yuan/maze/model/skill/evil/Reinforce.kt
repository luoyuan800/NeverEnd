package cn.luo.yuan.maze.model.skill.evil

import cn.luo.yuan.maze.model.Data
import cn.luo.yuan.maze.model.Hero
import cn.luo.yuan.maze.model.effect.original.HPPercentEffect
import cn.luo.yuan.maze.model.skill.AtkSkill
import cn.luo.yuan.maze.model.skill.SkillParameter
import cn.luo.yuan.maze.model.skill.UpgradeAble
import cn.luo.yuan.maze.model.skill.result.SkillResult
import cn.luo.yuan.maze.model.skill.result.SkipThisTurn
import cn.luo.yuan.maze.service.InfoControlInterface
import cn.luo.yuan.maze.utils.Field
import cn.luo.yuan.maze.utils.StringUtils
import java.util.concurrent.TimeUnit

/**
 * Created by luoyuan on 2017/7/19.
 */
class Reinforce() : AtkSkill(), UpgradeAble {
    fun setLevel(level: Long) {
        this.level = level
    }

    companion object {
        private const val serialVersionUID = Field.SERVER_VERSION
    }
    private var level = 1L
    private var percen = 5.0f
    private var turn = 3 * 60 * 1000L
    private val model = EvilModel(this)

    override fun getName(): String {
        return "强化 X " + level
    }

    override fun getDisplayName(): String {
        val builder = StringBuilder()
        builder.append(StringUtils.formatPercentage(rate)).append("的概率释放，自身生命值上限增加").append(StringUtils.formatPercentage(percen)).append("。强化效果结束时，HP会重置。")
        return builder.toString()
    }

    override fun canMount(parameter: SkillParameter?): Boolean {
        return model.canMount(parameter)
    }

    override fun upgrade(parameter: SkillParameter?): Boolean {
        rate += 1
        level++
        percen += level
        turn += 60000
        return true
    }

    override fun invoke(parameter: SkillParameter?): SkillResult {
        val hero = parameter!!.owner
        val rs = SkipThisTurn()
        if (hero is Hero) {
            val hppe = HPPercentEffect()
            hppe.tag = this.id
            hppe.setPercent(percen)
            hppe.isEnable = true
            hero.effects.add(hppe)
            val context: InfoControlInterface = parameter[SkillParameter.CONTEXT]
            context.executor.schedule({ hero.effects.remove(hppe) }, turn, TimeUnit.MILLISECONDS)
            rs.isSkip = true
            rs.messages.add("生命值提高了" + StringUtils.formatPercentage(percen) + "，持续时间" + (turn / 60000) + "分钟")
        }
        return rs
    }

    override fun getLevel(): Long {
        return level
    }

    override fun enable(parameter: SkillParameter?) {
        if (model.canEnable(parameter)) {
            isEnable = true
        }
    }

    override fun getSkillName(): String {
        return model.skillName
    }

    override fun canUpgrade(parameter: SkillParameter?): Boolean {
        return isEnable() && model.isUpgradePointEnough(parameter) && rate < Data.RATE_MAX/2
    }

    override fun canEnable(parameter: SkillParameter?): Boolean {
        return model.canEnable(parameter)
    }
}