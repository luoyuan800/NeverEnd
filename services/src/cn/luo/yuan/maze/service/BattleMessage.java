package cn.luo.yuan.maze.service;

import cn.luo.yuan.maze.model.HarmAble;
import cn.luo.yuan.maze.model.NameObject;
import cn.luo.yuan.maze.model.Pet;
import cn.luo.yuan.maze.model.skill.AtkSkill;
import cn.luo.yuan.maze.model.skill.DefSkill;
import cn.luo.yuan.maze.model.skill.Skill;
import cn.luo.yuan.maze.model.skill.SkillAbleObject;

/**
 * Created by gluo on 5/19/2017.
 */
public interface BattleMessage {
    void battleTooLong();
    void win(NameObject winner, NameObject loser);
    void lost(NameObject loser, NameObject winner);
    void hit(NameObject hero);
    void harm(NameObject atker, NameObject defender, long harm);
    void petSuppress(NameObject pet, NameObject monster);
    void petDefend(Pet pet);
    void dodge(NameObject hero, NameObject monster);
    void parry(NameObject hero);

    void silent(SkillAbleObject atker, HarmAble target, Skill skill);

    void releaseSkill(HarmAble target, Skill skill);

    void rowMessage(String msg);

    void startBattle(String hero, String monster);
}
