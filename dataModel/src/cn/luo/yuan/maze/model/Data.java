package cn.luo.yuan.maze.model;

import cn.luo.yuan.maze.model.skill.AtkSkill;
import cn.luo.yuan.maze.model.skill.DefSkill;
import cn.luo.yuan.maze.model.skill.Skill;
import cn.luo.yuan.maze.model.skill.UpgradeAble;
import cn.luo.yuan.maze.utils.Random;

/**
 * 定义一些基本的游戏数据，这些数据只是根据经验和个人喜欢设计的，所以特意抽取出来放到一起方便修改
 * Created by luoyuan on 2017/3/28.
 */
public class Data {
    public static final long INTIMACY_REDUCE = 10;//宠物死亡衰减的亲密度
    public static  int MAX_SELL_COUNT = 5;//商店最大的商品数量
    public static final String BLUE_COLOR = "#0000FF";//蓝色（阶位）1
    public static final String RED_COLOR = "#FF0000";//红色（阶位）2
    public static final String DEFAULT_QUALITY_COLOR = "#556B2F";//绿色（默认阶位）0
    public static  int ACCESSORY_FLUSE_LIMIT = 15;//装备升级的限制级数（从这个开始会有失败的情况发生）
    public static  int PET_UPGRADE_LIMIT = 5;//宠物升级的限制级数，作用同上
    public static final String ORANGE_COLOR = "#FFA500";//橙色色（阶位）3
    public static final String DARKGOLD_COLOR = "#B8860B";//暗金色（阶位）4
    public static  float RATE_MAX = 70f;//最高的概率，超过会强制设置为这个值
    public static  int DARKGOLD_RATE_REDUCE = 10;//橙色阶位的二次限制
    public static  String DISABLE_COLOR = "#C0C0C0";
    public static  String ENABLE_COLOR = "#33038C";
    public static  int MAX_PET_COUNT = 100;//最多背包宠物个数
    public static  long FUSE_COST = 20000; //装备升级消耗的锻造
    public static  long BASE_PET_COUNT = 3;
    public static float MONSTER_MEET_RATE = 99f;//遇怪的概率
    public static float PORTAL_RATE = 0.05f;//传送门的概率
    public static int LEVEL_BASE_POINT_REDUCE = 550;//爬楼奖励的点数基数分母（random（level/reduce））
    public static long REFRESH_SPEED = 500;//刷新间隔
    public static long MONSTER_ATK_RISE_PRE_LEVEL = 80;//怪物每层的攻击成长
    public static long MONSTER_HP_RISE_PRE_LEVEL = 200;//怪物每层的生命成长
    public static long MONSTER_DEF_RISE_PRE_LEVEL = 50;//怪物每层的防御成长
    public static float DODGE_AGI_RATE = 0.0001f;//敏捷转换成闪避率加成
    public static float DODGE_STR_RATE = 0.0001f;//力量转换成闪避率衰减
    public static float PARRY_STR_RATE = 0.001f;//力量转化格挡率加成
    public static float PARRY_AGI_RATE = 0.0001f;//敏捷转化格挡率衰减
    public static float HIT_AGI_RATE = 0.001f;//敏捷转化重击率衰减
    public static float HIT_STR_RATE = 0.002f;//力量转化重击率加成
    public static int SKILL_ENABLE_COST = 500;//激活技能消耗的能力点
    public static  long RESTOREPERIOD = 10000;//30 * 60 * 1000;//死亡后复活的时间（Server）
    public static  long RESTORE_LIMIT = 5;//死亡复活次数（Server）
    public static  long REINCARNATE_COST = 500000;//转生消耗的锻造
    public static  long REINCARNATE_LEVEL = 100;//转生需要的层数
    public static  long GROW_INCRESE = 2;//每次转生增加的成长基数
    public static  float PET_RATE_REDUCE = 1.2f; //宠物捕获率修正系数，越大捕获率越低
    public static  float EGG_RATE_REDUCE = 1.0f; //宠物生蛋率修正系数，越大捕获率越低
    public static  long MATERIAL_LIMIT = 3000000;//如果携带超过这个数量的锻造，就增加商店的价格和怪物的攻击
    public static  int WAREHOUSE_DEBRIS = 2;//仓库需要消耗的碎片数量
    public static  int UPLOAD_SAVE_DEBRIS = 20;//备份需要消耗的碎片数量
    public static  long RECYCLE_MATERIAL = 2000;//回收装备可以获得的锻造

    /**
     * 计算击败一个怪物可以获得的锻造点
     *
     * @param hp    怪物的最高血量
     * @param atk   怪物的攻击
     * @param level 当前层数
     * @return 返回可以获得的基础锻造数量
     */
    public static long getMonsterMaterial(long hp, long atk, long level, Random random) {
        long m1 = random.nextLong(hp + 1) / 281 + 2;
        long m2 = random.nextLong(atk + 1) / 811 + 4;
        long material = random.nextLong((m1 + m2) / 1832 + 1) + 10 + random.nextLong(level / 100 + 1);
        if (material > 1000) {
            material = 100 + random.nextInt(900);
        }
        return material;
    }

    public static float getColorReduce(String color) {
        float colorReduce = 0.1f;
        switch (color) {
            case Data.BLUE_COLOR:
                colorReduce = 0.3f;
                break;
            case Data.RED_COLOR:
                colorReduce = 0.5f;
                break;
            case Data.ORANGE_COLOR:
                colorReduce = 0.65f;
                break;
            case Data.DARKGOLD_COLOR:
                colorReduce = 0.8f;
                break;
        }
        return colorReduce;
    }

    public static int getSkillActionPoint(Skill skill){
        Float rate  = 0F;
        if(skill instanceof AtkSkill){
            rate = 100 - ((AtkSkill) skill).getRate();
        }else if(skill instanceof DefSkill){
            rate = 100 -((DefSkill) skill).getRate();
        }
        Integer level = 1;
        if(skill instanceof UpgradeAble){
            level = (int)((UpgradeAble) skill).getLevel();
        }
        return rate.intValue() * level;
    }
}
