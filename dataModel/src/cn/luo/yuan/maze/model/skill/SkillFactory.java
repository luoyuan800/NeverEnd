package cn.luo.yuan.maze.model.skill;

import cn.luo.yuan.maze.model.skill.hero.HeroHit;
import cn.luo.yuan.maze.persistence.DataManagerInterface;

/**
 * Created by luoyuan on 2017/5/28.
 */
public class SkillFactory {
    public static Skill geSkillByName(String name, DataManagerInterface dm){
        Skill skill = dm.loadSkill(name);
        if(skill == null){
            switch (name){
                case "HeroHit":
                    skill = new HeroHit();
                    break;
            }
        }
        return skill;
    }
}
