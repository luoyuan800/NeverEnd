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
import cn.luo.yuan.maze.model.skill.pet.Foster;
import cn.luo.yuan.maze.model.skill.pet.Mass;
import cn.luo.yuan.maze.model.skill.pet.PetMaster;
import cn.luo.yuan.maze.model.skill.pet.Trainer;
import cn.luo.yuan.maze.model.skill.pet.Zoarium;
import cn.luo.yuan.maze.model.skill.race.Alayer;
import cn.luo.yuan.maze.model.skill.race.Chaos;
import cn.luo.yuan.maze.model.skill.race.Decide;
import cn.luo.yuan.maze.model.skill.race.Exorcism;
import cn.luo.yuan.maze.model.skill.race.Masimm;
import cn.luo.yuan.maze.model.skill.race.Painkiller;
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
                case "Zoarium":
                    skill = new Zoarium();
                    break;
                case "Mass":
                    skill = new Mass();
                    break;
                case "Trainer":
                    skill = new Trainer();
                    break;
                case "PetMaster":
                    skill = new PetMaster();
                    break;
                case "Foster":
                    skill = new Foster();
                    break;
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
                case "Alayer":
                    skill = new Alayer();
                    break;
                case "Chaos":
                    skill = new Chaos();
                    break;
                case "Decide":
                    skill = new Decide();
                    break;
                case "Exorcism":
                    skill = new Exorcism();
                    break;
                case "Masimm":
                    skill = new Masimm();
                    break;
                case "Painkiller":
                    skill = new Painkiller();
                    break;
            }
        }
        return skill;
    }
}
