package cn.luo.yuan.maze.model.skill.result;

import java.util.Collections;
import java.util.List;

/**
 * Created by luoyuan on 2017/7/19.
 */
public class DonothingResult implements SkillResult {
    @Override
    public List<String> getMessages() {
        return Collections.emptyList();
    }
}
