package cn.luo.yuan.maze.model.skill.click;

/**
 * Created by luoyuan on 2017/5/28.
 */
public class ClickSkillFactory {
    public static ClickSkill buildSkill(String name) {
        if (name == null) {
            return null;
        } else if ("宠爆".equalsIgnoreCase(name)) {
            return new BaoZha();
        } else if ("一击".equalsIgnoreCase(name)) {
            return new YiJi();
        }  else if ("点金".equalsIgnoreCase(name)) {
            return new Material();
        } else {
            return null;
        }
    }
}
