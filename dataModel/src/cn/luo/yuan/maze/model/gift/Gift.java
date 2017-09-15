package cn.luo.yuan.maze.model.gift;

import cn.luo.yuan.maze.service.InfoControlInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright 2015 luoyuan.
 * ALL RIGHTS RESERVED
 * Created by luoyuan on 12/25/15.
 */
public enum Gift{
    HeroHeart("勇敢的心", "一根筋的热血埋藏在你的心中，这个天赋会提升攻击成长", 0, HeroHeart.class),
    DarkHeard("暗黑之心", "天生的黑暗心灵，连恒河水都洗不干净。这个天赋会提升防御成长", 0, DarkHeard.class),
    Warrior("战士", "勇敢向前绝对不会害怕，放弃技能注重身体属性成长！你最多只能使用三个技能，但是hp、攻击、防御成长翻倍。", 1, Warrior.class),
    Searcher("守财奴", "就算死，也要守护自己的财产！囤积锻造点数也不会招引强力的怪物。", 1, cn.luo.yuan.maze.model.gift.Searcher.class),
    Long("龙裔", "声称自己是龙的传人，但是却因为脸黑没有人相信你。碰到名字含有‘龙’的敌人，你可以触发你的王八之气秒杀他们。代价是会变得脸黑。", 4, cn.luo.yuan.maze.model.gift.Long.class),
    Element("元素之心", "据说从一出生的时候就体内就被植入了奇怪的东西从而可以操纵元素能量。拥有这个天赋使得你自身的属性也可以激活装备的隐藏属性了！。", 3, cn.luo.yuan.maze.model.gift.Element.class),
    Pokemon("神奇宝贝", "Pokemon Monster的忠实玩家，以至于产生了这个奇怪的天赋。通常情况下，宠物数量越多，宠物的的捕获率就会越低，但这个天赋可以使得你不再受此影响。同时还会额外增加宠物捕获率%5。", 3, cn.luo.yuan.maze.model.gift.Pokemon.class),
    SkillMaster("技能大师", "天生脸皮厚，所以你可以拥有比别人更高的技能释放概率，所有主动技能的释放概率提升10%。", 4, cn.luo.yuan.maze.model.gift.SkillMaster.class),
    RandomMaster("冒险之心", "喜欢变幻莫测的未知事物，喜欢探索各个隐秘的角落。增加随机事件、随机人物出现的概率。", 2, null),
    ChildrenKing("孩子王", "永远保持童真，任何事物在你眼中都是新奇的，世界只是你手中的玩具（五十转后才可以选择）。", 50, null),
    Daddy("奶爸", "超级奶爸，不分昼夜的换尿布是你的天赋。每当因为受到伤害生命值变成负数的时候即刻增加10%的生命，如果可以抵消负数值，" +
            "你就可以避免挂掉。。", 25, null),
    Epicure("美食家", "拥有这个天赋你就可以制作煎蛋了……。", 2, cn.luo.yuan.maze.model.gift.Epicure.class);

    private String name;
    private String desc;
    private int recount;
    private Class<? extends GiftHandler> handlerType;

    private Gift(String name, String desc, int recount, Class<? extends GiftHandler> clazz) {
        this.name = name;
        this.desc = desc;
        this.recount = recount;
        this.handlerType = clazz;
    }

    public static Gift getByName(String name) {
        for (Gift gift : values()) {
            if (gift.name.equals(name)) {
                return gift;
            }
        }
        return null;
    }

    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public int getRecount() {
        return recount;
    }

    public void handler(InfoControlInterface control) throws IllegalAccessException, InstantiationException {
            GiftHandler handler = handlerType.newInstance();
            handler.handler(control);

    }

    public void unHandler(InfoControlInterface control) throws IllegalAccessException, InstantiationException {

            GiftHandler handler = handlerType.newInstance();
            handler.unHandler(control);
    }

    public static List<Gift> values(int recount){
        List<Gift> gifts = new ArrayList<>();
        for(Gift gift : values()){
            if(gift.recount <= recount){
                gifts.add(gift);
            }
        }
        return gifts;
    }

}
