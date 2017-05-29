package cn.luo.yuan.maze.model.skill.result;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luoyuan on 2017/5/29.
 */
public abstract class HasMessageResult implements SkillResult {
    private List<String> messages = new ArrayList<>();

    public List<String> getMessages() {
        return messages;
    }

    public void addMessage(String msg){
        this.messages.add(msg);
    }
}
