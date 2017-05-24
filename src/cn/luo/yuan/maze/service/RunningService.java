package cn.luo.yuan.maze.service;

import android.util.Log;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.exception.MonsterToPetException;
import cn.luo.yuan.maze.listener.LostListener;
import cn.luo.yuan.maze.listener.PetCatchListener;
import cn.luo.yuan.maze.listener.WinListener;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.Maze;
import cn.luo.yuan.maze.model.Monster;
import cn.luo.yuan.maze.model.Pet;
import cn.luo.yuan.maze.model.gift.Gift;
import cn.luo.yuan.maze.persistence.DataManager;
import cn.luo.yuan.maze.utils.LogHelper;
import cn.luo.yuan.maze.utils.Random;
import cn.luo.yuan.maze.utils.Resource;
import cn.luo.yuan.maze.utils.StringUtils;

import static cn.luo.yuan.maze.service.ListenerService.lostListeners;
import static cn.luo.yuan.maze.service.ListenerService.petCatchListeners;
import static cn.luo.yuan.maze.service.ListenerService.winListeners;


/**
 * Created by luoyuan on 2017/3/28.
 */
public class RunningService implements Runnable {
    private Hero hero;
    private GameContext gameContext;
    private Maze maze;
    private boolean running;
    private boolean pause;
    private long fps;
    private Random random;
    private long startTime;
    private DataManager dataManager;
    private Monster monster;

    public RunningService(Hero hero, Maze maze, GameContext gameContext, DataManager dataManager, long fps) {
        this.hero = hero;
        this.gameContext = gameContext;
        this.maze = maze;
        running = true;
        this.fps = fps;
        this.dataManager = dataManager;
        random = gameContext.getRandom();
    }

    public void close() {
        this.running = false;
    }

    public boolean getPause() {
        return pause;
    }

    public boolean pause() {
        this.pause = !this.pause;
        return pause;
    }

    @Override
    public void run() {
        PetMonsterHelper monsterHelper = gameContext.getPetMonsterHelper();
        startTime = System.currentTimeMillis();
        if (running) {
            try {
                if (pause) {
                    return;
                }
                maze.setStep(maze.getStep() + 1);
                if (random.nextLong(10000) > 9985 || random.nextLong(maze.getStep()) > 10 + random.nextLong(22) || random.nextLong(maze.getStreaking() + 1) > 50 + maze.getLevel()) {
                    maze.setLevel(maze.getLevel() + 1);
                    Log.i("maze", "End to next level");
                    long point = 1;
                    long add = random.nextLong(maze.getLevel() / 1000);
                    if (add < 10) {
                        point += add;
                    }
                    if (maze.getMaxLevel() < 500) {
                        point *= 4;
                    }
                    if (maze.getLevel() < maze.getMaxLevel()) {
                        point /= 2;
                    }
                    String msg;
                    if (point > 0 && (maze.getLevel() > maze.getMaxLevel() || random.nextBoolean())) {
                        msg = String.format(gameContext.getContext().getString(R.string.move_to_next_level),
                                hero.getDisplayName(), StringUtils.formatNumber(maze.getLevel()), StringUtils.formatNumber(point));
                    } else {
                        point = 1;
                        msg = String.format(gameContext.getContext().getString(R.string.move_to_next_level),
                                hero.getDisplayName(), StringUtils.formatNumber(maze.getLevel()), StringUtils.formatNumber(point));
                    }
                    mazeLevelCalculate();
                    hero.setPoint(hero.getPoint() + point);
                    gameContext.addMessage(msg);
                    if ((System.currentTimeMillis() - startTime) % 1000 == 5 * 60) {//每隔五分钟自动存储一次
                        gameContext.save();
                    }
                    maze.setStep(0);
                } else {
                    Log.i("maze", "Try to find monster battle");
                    Monster monster = monsterHelper.randomMonster(maze.getLevel());
                    if (monster != null) {
                        Log.i("maze", "battle with " + monster.getDisplayName());
                        BattleService battleService = new BattleService(hero,monster, gameContext.getRandom());
                        BattleMessage battleMessage = new BattleMessageImp(gameContext);
                        battleService.setBattleMessage(battleMessage);
                        if (battleService.battle(gameContext.getMaze().getLevel())) {
                            Log.i("maze", "Battle win " + monster.getDisplayName());
                            maze.setStreaking(maze.getStreaking() + 1);
                            hero.setMaterial(hero.getMaterial() + monster.getMaterial());
                            gameContext.addMessage(String.format(gameContext.getContext().getString(R.string.add_mate), StringUtils.formatNumber(monster.getMaterial())));
                            Pet pet = tryCatch(monster, dataManager.getPetCount());
                            if (pet != null) {
                                gameContext.addMessage(String.format(Resource.getString(R.string.pet_catch), pet.getDisplayName()));
                                dataManager.savePet(pet);
                                for (PetCatchListener listener : petCatchListeners.values()) {
                                    listener.catchPet(pet);
                                }
                            }
                            for (WinListener listener : winListeners.values()) {
                                listener.win(hero, monster);
                            }
                        } else {
                            Log.i("maze", "Battle failed with " + monster.getDisplayName());
                            maze.setStreaking(0);
                            for (LostListener lostListener : lostListeners.values()) {
                                lostListener.lost(hero, monster);
                            }
                            if (hero.getHp() <= 0) {
                                gameContext.addMessage(String.format(gameContext.getContext().getString(R.string.lost), hero.getDisplayName()));
                                hero.setHp(hero.getMaxHp());
                                for (Pet pet : hero.getPets()) {
                                    pet.setHp(pet.getMaxHp());
                                }
                                maze.setLevel(1);
                            }
                            Log.i("maze", "Battle failed restore");
                        }
                    }else{
                        Log.i("maze", "not monster");
                        if (random.nextLong(hero.getAgi()) + random.nextInt(2000)> 1985 + random.nextLong(hero.getStr())) {
                            long mate = random.nextLong(gameContext.getMaze().getLevel() * 30 + 1) + random.nextLong((hero.getAgi() - hero.getStr()) / 10000 + 10) + 58;
                            if (mate > 1000) {
                                mate = 800 + random.nextLong(mate / 10000 + 100);
                            }
                            if (hero.getMaterial() > 30000) {
                                mate /= 30;
                            }
                            if (hero.getMaterial() > 12000) {
                                mate /= 60;
                            }
                            if (hero.getMaterial() > 30000) {
                                mate /= 100;
                            }
                            if (mate < 38) {
                                mate = 38;
                            }

                            gameContext.addMessage(hero.getDisplayName() + "找到了一个宝箱， 获得了<font color=\"#FF8C00\">" + mate + "</font>锻造点数");
                            hero.setMaterial(hero.getMaterial() + mate);
                        } else if (hero.getHp() < hero.getUpperHp() && random.nextLong(1000) > 989) {
                            long hel = random.nextLong(hero.getUpperHp() / 5 + 1) + random.nextLong(hero.getStr() / 300);
                            if (hel > hero.getUpperHp() / 2) {
                                hel = random.nextLong(hero.getUpperHp() / 2 + 1) + 1;
                            }
                            gameContext.addMessage(hero.getDisplayName() + "休息了一会，恢复了<font color=\"#556B2F\">" + hel + "</font>点HP");
                            hero.setHp(hero.getHp() + hel);
                        } else if (random.nextLong(hero.getAgi()) + random.nextInt(2000)> 1989 + random.nextLong(hero.getStr())) {
                                gameContext.getMaze().setStep(0);
                                long levJ = random.nextLong(gameContext.getMaze().getMaxLevel() + 100 + hero.getReincarnate()) + 1;
                                gameContext.addMessage( hero.getDisplayName() + "踩到了传送门，被传送到了迷宫第" + levJ + "层");
                                gameContext.getMaze().setLevel(levJ);
                                mazeLevelCalculate();
                        } else {
                            switch (random.nextInt(6)) {
                                case 0:
                                    gameContext.addMessage(hero.getDisplayName() + "思考了一下人生...");
                                    if (hero.getReincarnate() > 0) {
                                        long point = hero.getReincarnate();
                                        if (point > 10) {
                                            point = 10 + random.nextInt(10);
                                        }

                                        gameContext.addMessage(hero.getDisplayName() + "获得了" + point + "点能力点");
                                        hero.setPoint(hero.getPoint() + point);
                                    }
                                    break;
                                case 1:
                                    gameContext.addMessage(hero.getDisplayName() + "犹豫了一下不知道做什么好。");
                                    /*if (Gift.valueOf(hero.getGift()) == Gift.ChildrenKing) {
                                        gameContext.addMessage(hero.getDisplayName() + "因为" + Gift.ChildrenKing.getName() + "天赋，你虽然会因为无知犯错，但是大人们宽容你的错误。增加100点能力点");
                                        hero.setPoint(hero.getPoint() + 100);
                                    }*/
                                    break;
                                case 2:
                                    gameContext.addMessage(hero.getDisplayName() + "感觉到肚子饿了！");
                                    /*if (hero.getGift() == Gift.ChildrenKing) {
                                        addMessage(context, hero.getFormatName() + "因为" + Gift.ChildrenKing.getName() + "天赋，可以在肚子饿的时候哭闹一下，就会有人上前服侍。增加 20 点力量。");
                                        hero.addStrength(20);
                                    }*/
                                    break;
                                case 3:
                                    if (hero.getPets().size() > 0) {
                                        /*addMessage(context, hero.getFormatName() + "和宠物们玩耍了一下会。");
                                        for (Pet pet : hero.getPets()) {
                                            if (random.nextBoolean()) {
                                                pet.click(5);
                                            }
                                        }*/
                                    } else {
                                        gameContext.addMessage(hero.getDisplayName() + "想要养宠物");
                                    }
                                    break;
                                case 4:
                                    gameContext.addMessage(hero.getDisplayName() + "正在发呆...");
                                    /*if (hero.getGift() == Gift.ChildrenKing) {
                                        addMessage(context, hero.getFormatName() + "因为" + Gift.ChildrenKing.getName() + "天赋，发呆的时候咬咬手指头就可以恢复20%的生命值。");
                                        hero.addHp((long) (hero.getUpperHp() * 0.3));
                                    }*/
                                    break;
                                case 5:
                                    /*GoodsType mirrori = GoodsType.Mirror;
                                    if (mirrori.getCount() > 0 && !mirrori.isLock() && random.nextLong(mirrori.getCount()) < 3) {
                                        addMessage(context, hero.getFormatName() + "拿出镜子照了一下，觉得自己很帅帅哒/亮亮哒！");
                                        addMessage(context, hero.getFormatName() + "敏捷加  " + mirrori.getCount());
                                        hero.addAgility(mirrori.getCount());
                                        if (context.getBirthDay()) {
                                            addMessage(context, "生日还一个孤零零的照镜子，能力点增加 " + mirrori.getCount());
                                            hero.addPoint(mirrori.getCount());
                                        }
                                        mirrori.use();*/
                                   /* } else {
                                        addMessage(context, hero.getFormatName() + "正在发呆...");
                                    }*/
                                    break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                LogHelper.logException(e, false, "Error while running game thread.");
            }
        }
    }

    private void mazeLevelCalculate() {
        if (maze.getMaxLevel() < maze.getLevel()) {
            maze.setMaxLevel(maze.getLevel());
        }
    }

    public Monster getMonster() {
        return monster;
    }

    private Pet tryCatch(Monster monster, int petCount) {
        try {
            if (gameContext.getPetMonsterHelper().isCatchAble(monster, hero, random, petCount)) {
                return gameContext.getPetMonsterHelper().monsterToPet(monster, hero);
            } else {
                return null;
            }
        } catch (MonsterToPetException e) {

        }
        return null;
    }
}
