package cn.luo.yuan.maze.model.skill.swindler

import cn.luo.yuan.maze.model.HarmAble
import cn.luo.yuan.maze.model.NameObject
import cn.luo.yuan.maze.model.skill.DefSkill
import cn.luo.yuan.maze.model.skill.SkillParameter
import cn.luo.yuan.maze.model.skill.result.HarmResult
import cn.luo.yuan.maze.model.skill.result.SkillResult
import cn.luo.yuan.maze.service.BattleServiceBase
import cn.luo.yuan.maze.service.InfoControlInterface
import cn.luo.yuan.utils.Random
import cn.luo.yuan.maze.utils.StringUtils

/**
 * Created by luoyuan on 2017/7/23.
 */
class EatHarm:DefSkill() {
    private val model=SwindlerModel(this)
    override fun getName(): String {
        return "虚无吞噬"
    }

    override fun getDisplayName(): String {
        val builder = StringBuilder()
        builder.append("无法使用能力点激活，每前进100层迷宫随机激活或者重置。<br>").append("防御技能。敌方攻击的时候")
        builder.append(StringUtils.formatPercentage(rate)).append("的概率释放，抵消敌人的攻击。并且<br>抛一次硬币，正面就攻击敌人一次。欺诈师技能可以对这个技能生效。")
        builder.append("<br>已经使用：${StringUtils.formatNumber(useTime)}次")
        return builder.toString()
    }

    override fun canMount(parameter: SkillParameter?): Boolean {
        return model.canMount(parameter)
    }

    override fun invoke(parameter: SkillParameter): SkillResult {
        val hr = HarmResult()
        val atk: HarmAble = parameter[SkillParameter.ATKER]
        val defender : HarmAble = parameter[SkillParameter.DEFENDER]
        val random: Random = parameter[SkillParameter.RANDOM]
        var side = random.nextBoolean()
        val min: Long = parameter[SkillParameter.MINHARM]
        hr.addMessage((defender as NameObject).displayName + " 抛了一次硬币，结果为：" + if(side) "正" else "反")
        if(!side){
            val context: InfoControlInterface = parameter[SkillParameter.CONTEXT]
            if(model.isSkillEnable("Swindler", context)){
                side = random.nextBoolean()
                hr.addMessage(defender.displayName + "不要脸的又抛了一次硬币,结果为：" + if(side) "正" else "反")
            }
        }
        if(side){
            hr.isBack = true
            hr.harm = BattleServiceBase.getHarm(defender,atk,min,random,parameter[SkillParameter.MESSAGE])
        }
        return hr
    }

    override fun enable(parameter: SkillParameter) {
        val random: Random = parameter[SkillParameter.RANDOM]
        if(model.canEnable(parameter) && random.nextBoolean()) {
            isEnable = true
        }
    }

    override fun getSkillName(): String {
        return model.skillName
    }

    override fun canEnable(parameter: SkillParameter?): Boolean {
        return false
    }
}