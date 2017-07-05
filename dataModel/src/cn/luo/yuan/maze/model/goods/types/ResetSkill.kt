package cn.luo.yuan.maze.model.goods.types

import cn.luo.yuan.maze.model.Data
import cn.luo.yuan.maze.model.goods.Goods
import cn.luo.yuan.maze.model.goods.GoodsProperties
import cn.luo.yuan.maze.model.skill.PropertySkill
import cn.luo.yuan.maze.model.skill.SkillParameter
import cn.luo.yuan.maze.model.skill.UpgradeAble
import cn.luo.yuan.maze.service.InfoControlInterface

/**
 * Created by gluo on 7/5/2017.
 */
class ResetSkill : Goods() {
    override var desc: String = "使用后可以重置技能点。每个激活的技能你可以获得 技能等级 * " + Data.SKILL_ENABLE_COST + " 的能力点返还。"

    override var name: String = "易筋经"

    override var price: Long = 100000L


    override fun use(properties: GoodsProperties): Boolean {
        if(count > 0){
            val context = properties["context"] as InfoControlInterface
            var totalPoint = 0L;
            val sp = SkillParameter(properties.hero)
            for( skill in context.dataManager.loadAllSkill()){
                if(skill.isEnable){
                    if(skill is UpgradeAble){
                        totalPoint += skill.level * Data.SKILL_ENABLE_COST
                    }else{
                        totalPoint += Data.SKILL_ENABLE_COST
                    }
                }
                if(skill is PropertySkill){
                    skill.disable(sp)
                }else {
                    skill.disable()
                }
            }
        }
        return super.use(properties)
    }

}