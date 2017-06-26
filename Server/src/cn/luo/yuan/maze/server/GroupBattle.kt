package cn.luo.yuan.maze.server

import cn.luo.yuan.maze.model.Hero
import cn.luo.yuan.maze.server.model.Group
import cn.luo.yuan.maze.server.model.Messager
import cn.luo.yuan.maze.server.persistence.GroupTable
import cn.luo.yuan.maze.server.persistence.HeroTable
import cn.luo.yuan.maze.service.BattleService
import cn.luo.yuan.maze.utils.Random

/**
 * Created by gluo on 6/20/2017.
 */
/*class GroupBattle(private val g1: Group, private val g2: Group?, private val hero: Hero?, private val heroTable: HeroTable, private val groupTable: GroupTable, private val msger: Messager, private val rs:Running) {
    private val random
            = Random(System.currentTimeMillis())

    fun battle(): Boolean {
        if (hero != null) {
            return battleWithHero()
        } else {
            return battleWithGroup()
        }
    }

    private fun battleWithHero(): Boolean {
        msger.heroChallengeGroup(hero!!.displayName, groupName())
        for(id in g1.heroIds){
            val gh = heroTable.getHero(id);
            if(gh.currentHp > 0){
                msger.groupBattler(gh.displayName)
                val bs = BattleService(hero,gh,random,rs);
                if(!bs.battle(g1.level)){
                    msger.win(gh, hero)
                    msger.lost(hero, gh)
                    break;
                }else{
                    msger.win(hero, gh)
                    msger.lost(gh, hero)
                }
            }
        }
        if(hero.currentHp > 0){
            msger.heroWinGroup(hero.displayName, groupName())
            return false;
        }else{
            msger.groupWinHero(groupName(), hero.displayName)
            return true;
        }
    }

    private fun groupName() = heroTable.getHero(g1.heroIds.elementAt(0)).displayName + "的队伍"

    private fun battleWithGroup(): Boolean {
        msger.groupBattle(heroTable.getHero(g1.heroIds.elementAt(0)).displayName + "的队伍", heroTable.getHero(g2!!.heroIds.elementAt(0)).displayName + "的队伍")
        return false
    }
}
*/