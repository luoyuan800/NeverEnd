package cn.luo.yuan.maze.model.skill.race

import cn.luo.yuan.maze.model.HarmAble
import cn.luo.yuan.maze.model.NameObject
import cn.luo.yuan.maze.model.Parameter
import cn.luo.yuan.maze.model.Race
import cn.luo.yuan.maze.model.skill.Skill
import cn.luo.yuan.maze.model.skill.SkillModel
import cn.luo.yuan.maze.model.skill.SkillParameter
import cn.luo.yuan.maze.service.BattleMessage
import cn.luo.yuan.maze.utils.Random

/**
 * Created by luoyuan on 2017/9/4.
 */
class RaceSkillModel(private val s:Skill): SkillModel(s) {
    fun getDesc():String{
        return "当对战的敌人是 ${race().getName()} 时，攻击有50%的几率秒杀对方。"
    }

    private fun race(): Race {
        var race = Race.Nonsr;
        when (skill) {
            is Alayer -> race = Race.Orger
            is Decide -> race = Race.Elyosr
            is Exorcism -> race = Race.Ghosr
            is Masimm -> race = Race.Wizardsr
        }
        return race
    }

    fun perform(parameter: SkillParameter): Boolean{
        val m = parameter.get<HarmAble>(Parameter.TARGET)
        val r = parameter.get<Random>(Parameter.RANDOM);
        val a = parameter.get<HarmAble>(Parameter.ATKER)
        var msg = parameter.get<BattleMessage>(Parameter.MESSAGE);
        if(m!=null && m.race == race() && r!=null && r.nextBoolean()){
            m.hp -= m.currentHp;
            if(a is NameObject && m is NameObject) {
                msg.rowMessage("${a.displayName}秒杀了${m.displayName}")
            }
            return true;
        }
        msg.rowMessage("技能没有生效！")
        return false;
    }
}