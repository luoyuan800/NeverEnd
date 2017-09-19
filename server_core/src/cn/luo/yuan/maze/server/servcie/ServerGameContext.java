package cn.luo.yuan.maze.server.servcie;

import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.Maze;
import cn.luo.yuan.maze.model.skill.SkillParameter;
import cn.luo.yuan.maze.persistence.DataManagerInterface;
import cn.luo.yuan.maze.service.InfoControlInterface;
import cn.luo.yuan.maze.service.PetMonsterHelperInterface;
import cn.luo.yuan.maze.utils.Random;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by luoyuan on 2017/9/19.
 */
public class ServerGameContext implements InfoControlInterface {
    private ServerDataManager dataManager;
    private Hero hero;
    private Random random;
    private Maze maze;
    private ScheduledExecutorService executor;

    public ServerGameContext(Hero hero, ServerDataManager dataManager, Maze maze) {
        this.hero = hero;
        this.maze = maze;
        this.dataManager = dataManager;
        this.random = new Random(System.currentTimeMillis());
    }

    @Override
    public Hero getHero() {
        return hero;
    }

    @Override
    public Random getRandom() {
        return random;
    }

    @Override
    public Maze getMaze() {
        return maze;
    }

    @Override
    public void addMessage(String msg) {

    }

    @Override
    public void stopGame() {

    }

    @Override
    public DataManagerInterface getDataManager() {
        return dataManager;
    }

    @Override
    public PetMonsterHelperInterface getPetMonsterHelper() {
        return null;
    }

    @Override
    public ScheduledExecutorService getExecutor() {
        return executor;
    }

    public void setExecutor(ScheduledExecutorService executor) {
        this.executor = executor;
    }

    @Override
    public long resetSkill(@NotNull SkillParameter sp) {
        return 0;
    }

    @Override
    public void showPopup(String msg) {

    }

    @Override
    public void postTaskInUIThread(Runnable task) {

    }

    @Override
    public void refreshHead() {

    }

    @Override
    public void showEmptyAccessoryDialog() {

    }

    @Override
    public void showInputPopup(InputListener listener, String hint) {

    }
}
