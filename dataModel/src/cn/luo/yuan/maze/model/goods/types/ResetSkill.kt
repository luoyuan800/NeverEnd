package cn.luo.yuan.maze.model.goods.types

import cn.luo.yuan.maze.model.Data
import cn.luo.yuan.maze.model.goods.Goods
import cn.luo.yuan.maze.model.goods.GoodsProperties
import cn.luo.yuan.maze.model.goods.UsableGoods
import cn.luo.yuan.maze.model.skill.PropertySkill
import cn.luo.yuan.maze.model.skill.SkillParameter
import cn.luo.yuan.maze.model.skill.UpgradeAble
import cn.luo.yuan.maze.service.InfoControlInterface
import cn.luo.yuan.maze.utils.Field

/**
 * Created by gluo on 7/5/2017.
 */
class ResetSkill : UsableGoods() {
    companion object {
        private const val serialVersionUID: Long = Field.SERVER_VERSION
    }

    override var desc: String = "使用后可以重置技能点。每个激活的技能你可以获得 技能等级 * " + Data.SKILL_ENABLE_COST + " 的能力点返还。"

    override var name: String = "易筋经"

    override var price: Long = 100000L


    override fun perform(properties: GoodsProperties): Boolean {
        if(getCount() > 0){
            val context = properties[SkillParameter.CONTEXT] as InfoControlInterface
            val sp = SkillParameter(properties.hero)
            sp.set(SkillParameter.CONTEXT, context)
            val totalPoint = context.resetSkill(sp)
            properties.hero.point += totalPoint
            return true
        }
        return false
    }

}