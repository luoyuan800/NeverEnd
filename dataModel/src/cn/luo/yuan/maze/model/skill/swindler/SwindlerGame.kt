package cn.luo.yuan.maze.model.skill.swindler

import cn.luo.yuan.maze.model.Data
import cn.luo.yuan.maze.model.HarmAble
import cn.luo.yuan.maze.model.Hero
import cn.luo.yuan.maze.model.NameObject
import cn.luo.yuan.maze.model.skill.AtkSkill
import cn.luo.yuan.maze.model.skill.SkillParameter
import cn.luo.yuan.maze.model.skill.UpgradeAble
import cn.luo.yuan.maze.model.skill.result.HarmResult
import cn.luo.yuan.maze.model.skill.result.SkillResult
import cn.luo.yuan.maze.service.BattleServiceBase
import cn.luo.yuan.maze.utils.Random
import cn.luo.yuan.maze.utils.StringUtils

/**
 * Created by luoyuan on 2017/7/23.
 */
class SwindlerGame:AtkSkill(),UpgradeAble {
    private val model = SwindlerModel(this)
    private var level = 1L;
    override fun getName(): String {
        return "欺诈游戏 X $level"
    }

    override fun getDisplayName(): String {
        val builder = StringBuilder()
        builder.append("玩的就是心跳。<br>攻击时抛一次硬币，如果为正面，对敌人造成 $level 倍的攻击伤害。否则自己受到敌人攻击的 $level 倍伤害。")
        builder.append(StringUtils.formatPercentage(rate)).append("的概率释放。")
        builder.append("<br>已经使用：${StringUtils.formatNumber(useTime)}次")
        return builder.toString()
    }

    override fun canMount(parameter: SkillParameter?): Boolean {
        return model.canMount(parameter)
    }

    override fun upgrade(parameter: SkillParameter?): Boolean {
        level ++;
        if(rate + 0.5 < Data.RATE_MAX){
            rate +=0.5f;
        }
        return true;
    }

    override fun invoke(parameter: SkillParameter): SkillResult {
        val hr = HarmResult()
        val hero: HarmAble = parameter[SkillParameter.ATKER]
        val target :HarmAble = parameter[SkillParameter.TARGET]
        val random:Random = parameter[SkillParameter.RANDOM]
        var side = random.nextBoolean()
        val min: Long = parameter[SkillParameter.MINHARM]
        hr.addMessage((hero as NameObject).displayName + "开启欺诈游戏， 抛了一次硬币，结果为：" + if(side) "正" else "反")
        if(!side){
            if(model.isSkillEnable("Swindler", parameter[SkillParameter.CONTEXT])){
                side = random.nextBoolean()
                hr.addMessage(hero.displayName + "不要脸的又抛了一次硬币,结果为：" + if(side) "正" else "反")
            }
        }
        var harm = min
        if(side) {
            harm = BattleServiceBase.getHarm(hero, target, min, random, parameter[SkillParameter.MESSAGE])
            hr.isBack = false
        }else{
            harm = BattleServiceBase.getHarm(target, hero, min, random, parameter[SkillParameter.MESSAGE])
            hr.isBack = true
        }
        harm *= level
        hr.harm = harm;
        return hr
    }

    override fun getLevel(): Long {
        return level
    }

    override fun enable(parameter: SkillParameter?) {
        isEnable = true
    }

    override fun getSkillName(): String {
        return model.skillName
    }

    override fun canUpgrade(parameter: SkillParameter?): Boolean {
        return model.canUpgrade(parameter) && rate < Data.RATE_MAX/4
    }

    override fun canEnable(parameter: SkillParameter?): Boolean {
        return model.canEnable(parameter)
    }
}