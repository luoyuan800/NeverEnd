package cn.luo.yuan.maze.model.skill.elementalist

import cn.luo.yuan.maze.model.Data
import cn.luo.yuan.maze.model.HarmAble
import cn.luo.yuan.maze.model.NameObject
import cn.luo.yuan.maze.model.skill.DefSkill
import cn.luo.yuan.maze.model.skill.SkillParameter
import cn.luo.yuan.maze.model.skill.UpgradeAble
import cn.luo.yuan.maze.model.skill.result.SkillResult
import cn.luo.yuan.maze.model.skill.result.SkipThisTurn
import cn.luo.yuan.maze.service.BattleMessageInterface
import cn.luo.yuan.maze.service.BattleServiceBase
import cn.luo.yuan.maze.utils.Field
import cn.luo.yuan.maze.utils.Random
import cn.luo.yuan.maze.utils.StringUtils

/**
 * Created by luoyuan on 2017/7/20.
 */
class ElementDefend():DefSkill(),UpgradeAble {
    companion object {
        private const val serialVersionUID = Field.SERVER_VERSION
    }
    override fun upgrade(parameter: SkillParameter?): Boolean {
        if(rate + 0.5 < Data.RATE_MAX){
            rate += 0.5f
            level++
            return true
        }
        return false
    }

    override fun getLevel(): Long {
        return level
    }

    private val model = ElementModel(this)
    private var level = 1L
    override fun getName(): String {
        return "元素防御 X $level"
    }

    override fun getDisplayName(): String {
        val builder = StringBuilder()
        builder.append("用你的高冷藐视敌人！<br>抵消50%的伤害，当攻击的你的敌人的属性被你的五行属性克制的时候，免疫那个敌人的攻击。")
        builder.append(StringUtils.formatPercentage(rate)).append("概率释放<br>")
        return builder.toString()
    }

    override fun canMount(parameter: SkillParameter?): Boolean {
        return model.canMount(parameter)
    }

    override fun invoke(parameter: SkillParameter): SkillResult {
        val rs = SkipThisTurn()
        val atker:HarmAble = parameter[SkillParameter.ATKER]
        val defender:HarmAble = parameter.owner as HarmAble
        val minHarm:Long = parameter[SkillParameter.MINHARM]
        val messager:BattleMessageInterface = parameter[SkillParameter.MESSAGE]
        val random:Random = parameter[SkillParameter.RANDOM]
        var harm = BattleServiceBase.getHarm(atker, defender,minHarm, random,messager)
        if(defender.element.restriction(atker.element)){
            rs.messages.add((defender as NameObject).displayName + "免疫了" + (atker as NameObject).displayName + "的" + StringUtils.formatNumber(harm) + "点伤害")
        }else{
            rs.messages.add((defender as NameObject).displayName + "免疫了" + (atker as NameObject).displayName + "的" + StringUtils.formatNumber(harm/2) + "点伤害(50%)")
            defender.hp -= harm/2
        }
        return rs
    }

    override fun enable(parameter: SkillParameter?) {
        isEnable = true
    }

    override fun getSkillName(): String {
        return model.skillName
    }

    override fun canUpgrade(parameter: SkillParameter?): Boolean {
        return rate + 0.5 < Data.RATE_MAX/4 && model.canUpgrade(parameter)
    }
}