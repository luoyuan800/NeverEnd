package cn.luo.yuan.maze.client.service;

import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.model.HarmAble;
import cn.luo.yuan.maze.model.NameObject;
import cn.luo.yuan.maze.model.Pet;
import cn.luo.yuan.maze.model.skill.Skill;
import cn.luo.yuan.maze.model.skill.SkillAbleObject;
import cn.luo.yuan.maze.service.BattleMessage;
import cn.luo.yuan.maze.client.utils.Resource;
import cn.luo.yuan.maze.utils.StringUtils;

/**
 * Created by gluo on 5/19/2017.
 */
public class BattleMessageImp implements BattleMessage {
    private NeverEnd control;
    public BattleMessageImp(NeverEnd control){
        this.control = control;
    }
    public void battleTooLong(){
        control.addMessage(Resource.getString(R.string.battle_to_long));

    }

    @Override
    public void win(NameObject winner, NameObject loser) {
        control.addMessage(String.format(Resource.getString(R.string.win_msg), winner.getDisplayName(), loser.getDisplayName()));
    }

    @Override
    public void lost(NameObject loser, NameObject winner) {
        control.addMessage(String.format(Resource.getString(R.string.lost_msg), loser.getDisplayName(), winner.getDisplayName()));
    }

    @Override
    public void hit(NameObject hero) {
        control.addMessage(String.format(Resource.getString(R.string.hit_happen), hero.getDisplayName()));
    }

    @Override
    public void harm(NameObject atker, NameObject defender, long harm) {
        control.addMessage(String.format(Resource.getString(R.string.atk_harm_color_msg), atker.getDisplayName(), defender.getDisplayName(), StringUtils.formatNumber(harm, false)));

    }

    @Override
    public void petSuppress(NameObject pet, NameObject monster) {
        control.addMessage(String.format(Resource.getString(R.string.pet_index_suppression), pet.getDisplayName(), monster.getDisplayName()));
    }

    @Override
    public void petDefend(Pet pet) {
        control.addMessage(String.format(Resource.getString(R.string.pet_defend), pet.getDisplayName()));
    }

    @Override
    public void dodge(NameObject hero, NameObject monster) {
        control.addMessage(String.format(Resource.getString(R.string.doge_happen), hero.getDisplayName(), monster.getDisplayName()));
    }

    public void parry(NameObject hero){
        control.addMessage(String.format(Resource.getString(R.string.parry_happen), hero.getDisplayName()));

    }

    @Override
    public void silent(SkillAbleObject atker, HarmAble target, Skill skill) {
        control.addMessage(String.format(Resource.getString(R.string.silent_skill),
                (atker instanceof NameObject ? ((NameObject) atker).getDisplayName() : ""),
                skill.getName(),
                (target instanceof NameObject ? ((NameObject) target).getDisplayName() : "")));
    }

    @Override
    public void releaseSkill(HarmAble target, Skill skill) {
        control.addMessage(String.format(Resource.getString(R.string.release_skill), target instanceof NameObject ? ((NameObject) target).getDisplayName() : "", skill.getName()));
    }

    @Override
    public void rowMessage(String msg) {
        control.addMessage(msg);
    }

    @Override
    public void startBattle(String hero, String monster) {

    }

    @Override
    public void myTurn(String displayName) {
        control.addMessage(String.format(Resource.getString(R.string.my_turn), displayName));
    }
}
