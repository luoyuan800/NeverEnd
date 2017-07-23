package cn.luo.yuan.maze.model.skill;

import cn.luo.yuan.maze.model.skill.elementalist.ElementBomb;
import cn.luo.yuan.maze.model.skill.elementalist.ElementDefend;
import cn.luo.yuan.maze.model.skill.elementalist.Elementalist;
import cn.luo.yuan.maze.model.skill.evil.EvilTalent;
import cn.luo.yuan.maze.model.skill.evil.Reinforce;
import cn.luo.yuan.maze.model.skill.evil.Stealth;
import cn.luo.yuan.maze.model.skill.hero.Dodge;
import cn.luo.yuan.maze.model.skill.hero.FightBack;
import cn.luo.yuan.maze.model.skill.hero.HeroHit;
import cn.luo.yuan.maze.model.skill.swindler.EatHarm;
import cn.luo.yuan.maze.model.skill.swindler.Swindler;
import cn.luo.yuan.maze.model.skill.swindler.SwindlerGame;
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
                case "EvilTalent":
                    skill = new EvilTalent();
                    break;
                case "FightBack":
                    skill = new FightBack();
                    break;
                case "Dodge":
                    skill = new Dodge();
                    break;
                case "Reinforce":
                    skill = new Reinforce();
                    break;
                case "Stealth":
                    skill = new Stealth();
                    break;
                case "Elementalist":
                    skill = new Elementalist();
                    break;
                case "ElementBomb":
                    skill = new ElementBomb();
                    break;
                case "ElementDefend":
                    skill = new ElementDefend();
                    break;
                case "Swindler":
                    skill = new Swindler();
                    break;
                case "SwindlerGame":
                    skill = new SwindlerGame();
                    break;
                case "EatHarm":
                    skill = new EatHarm();
                    break;
            }
        }
        return skill;
    }
}
