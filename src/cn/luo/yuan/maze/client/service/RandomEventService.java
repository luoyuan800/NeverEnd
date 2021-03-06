package cn.luo.yuan.maze.client.service;

import cn.luo.yuan.maze.model.*;
import cn.luo.yuan.serialize.ObjectTable;
import cn.luo.yuan.maze.service.InfoControlInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gluo on 5/25/2017.
 */
public class RandomEventService {
    private InfoControlInterface gameControl;
    public RandomEventService(InfoControlInterface gameControl){
        this.gameControl = gameControl;
    }
    public void random(){
        if (gameControl.getRandom().nextLong(gameControl.getHero().getAgi()) + gameControl.getRandom().nextInt(2000)> 1985 + gameControl.getRandom().nextLong(gameControl.getHero().getStr())) {
            long mate = gameControl.getRandom().nextLong(gameControl.getMaze().getLevel() * 30 + 1) + gameControl.getRandom().nextLong((gameControl.getHero().getAgi() - gameControl.getHero().getStr()) / 10000 + 10) + 58;
            if (mate > 1000) {
                mate = 800 + gameControl.getRandom().nextLong(mate / 10000 + 100);
            }
            if (gameControl.getHero().getMaterial() > 30000) {
                mate /= 30;
            }
            if (gameControl.getHero().getMaterial() > 12000) {
                mate /= 60;
            }
            if (gameControl.getHero().getMaterial() > 30000) {
                mate /= 100;
            }
            if (mate < 38) {
                mate = 18 + gameControl.getRandom().nextInt(20);
            }

            gameControl.addMessage(gameControl.getHero().getDisplayName() + "找到了一个宝箱， 获得了<font color=\"#FF8C00\">" + mate + "</font>锻造点数");
            gameControl.getHero().setMaterial(gameControl.getHero().getMaterial() + mate);
        } else if (gameControl.getHero().getHp() < gameControl.getHero().getUpperHp() && gameControl.getRandom().nextLong(1000) > 989) {
            long hel = gameControl.getRandom().nextLong(gameControl.getHero().getUpperHp() / 5 + 1) + gameControl.getRandom().nextLong(gameControl.getHero().getStr() / 300);
            if (hel > gameControl.getHero().getUpperHp() / 2) {
                hel = gameControl.getRandom().nextLong(gameControl.getHero().getUpperHp() / 2 + 1) + 1;
            }
            gameControl.addMessage(gameControl.getHero().getDisplayName() + "休息了一会，恢复了<font color=\"#556B2F\">" + hel + "</font>点HP");
            gameControl.getHero().setHp(gameControl.getHero().getHp() + hel);
        } else if (Data.PORTAL_RATE + gameControl.getRandom().nextLong(gameControl.getHero().getAgi()) >  gameControl.getRandom().nextFloat(100f)+ gameControl.getRandom().nextLong(gameControl.getHero().getStr())) {
            gameControl.getMaze().setStep(0);
            long levJ = gameControl.getRandom().nextLong(gameControl.getMaze().getMaxLevel() + 10 + gameControl.getHero().getReincarnate()) + 1;
            gameControl.addMessage( gameControl.getHero().getDisplayName() + "踩到了传送门，被传送到了迷宫第" + levJ + "层");
            gameControl.getMaze().setLevel(levJ);
        } else {
            switch (gameControl.getRandom().nextInt(7)) {
                case 0:
                    gameControl.addMessage(gameControl.getHero().getDisplayName() + "思考了一下人生...");
                    if (gameControl.getHero().getReincarnate() > 0) {
                        long point = gameControl.getHero().getReincarnate();
                        if (point > 10) {
                            point = 10 + gameControl.getRandom().nextInt(10);
                        }

                        gameControl.addMessage(gameControl.getHero().getDisplayName() + "获得了" + point + "点能力点");
                        gameControl.getHero().setPoint(gameControl.getHero().getPoint() + point);
                    }
                    break;
                case 1:
                    gameControl.addMessage(gameControl.getHero().getDisplayName() + "犹豫了一下不知道做什么好。");
                                    /*if (Gift.valueOf(gameControl.getHero().getGift()) == Gift.ChildrenKing) {
                                        gameControl.addMessage(gameControl.getHero().getDisplayName() + "因为" + Gift.ChildrenKing.getName() + "天赋，你虽然会因为无知犯错，但是大人们宽容你的错误。增加100点能力点");
                                        gameControl.getHero().setPoint(gameControl.getHero().getPoint() + 100);
                                    }*/
                    break;
                case 2:
                    gameControl.addMessage(gameControl.getHero().getDisplayName() + "感觉到肚子饿了！");
                                    /*if (gameControl.getHero().getGift() == Gift.ChildrenKing) {
                                        addMessage(context, gameControl.getHero().getFormatName() + "因为" + Gift.ChildrenKing.getName() + "天赋，可以在肚子饿的时候哭闹一下，就会有人上前服侍。增加 20 点力量。");
                                        gameControl.getHero().addStrength(20);
                                    }*/
                    break;
                case 3:
                    if (gameControl.getHero().getPets().size() > 0) {
                        gameControl.addMessage(gameControl.getHero().getDisplayName() + "和宠物们玩耍了一下会。");
                        for (Pet pet : gameControl.getHero().getPets()) {
                            if (gameControl.getRandom().nextBoolean()) {
                                pet.setIntimacy(pet.getIntimacy() + (pet.getAtk()/2 > gameControl.getHero().getUpperAtk() && pet.getIntimacy() > 18 ? -7 : 5));
                            }
                        }
                    } else {
                        gameControl.addMessage(gameControl.getHero().getDisplayName() + "想要养宠物");
                    }
                    break;
                case 4:
                    gameControl.addMessage(gameControl.getHero().getDisplayName() + "正在发呆...");
                                    /*if (gameControl.getHero().getGift() == Gift.ChildrenKing) {
                                        addMessage(context, gameControl.getHero().getFormatName() + "因为" + Gift.ChildrenKing.getName() + "天赋，发呆的时候咬咬手指头就可以恢复20%的生命值。");
                                        gameControl.getHero().addHp((long) (gameControl.getHero().getUpperHp() * 0.3));
                                    }*/
                    break;
                case 5:
                    gameControl.getExecutor().submit(new Runnable() {
                        @Override
                        public void run() {
                            ArrayList<Pet> pets = new ArrayList<>(gameControl.getHero().getPets());
                            for(Pet p1 : pets){
                                for(Pet p2 : pets){
                                    Egg egg = gameControl.getPetMonsterHelper().buildEgg(p1, p2, gameControl);
                                    if(egg!=null){
                                        gameControl.addMessage(p1.getDisplayName() + "和" + p2.getDisplayName() + "生了一个蛋");
                                        gameControl.getDataManager().savePet(egg);
                                        break;
                                    }
                                }
                            }
                        }
                    });
                case 7:
                    ObjectTable<NPCLevelRecord> table = gameControl.getDataManager().getNPCTable();
                    List<String> ids = table.loadIds();
                    if(ids.size() > 0){
                        NPCLevelRecord record = table.loadObject(gameControl.getRandom().randomItem(ids));
                        if(record!=null && record.getHero()!=null &&  gameControl instanceof NeverEnd){
                            if(Math.abs(gameControl.getMaze().getMaxLevel() - record.getLevel()) < 100) {
                                record.getHero().setElement(gameControl.getRandom().randomItem(Element.values()));
                                ((NeverEnd) gameControl).getViewHandler().showNPCIcon(record);
                            }
                        }
                    }

                    break;
            }
        }
    }

}
