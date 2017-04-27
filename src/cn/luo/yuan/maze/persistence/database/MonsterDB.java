package cn.luo.yuan.maze.persistence.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.model.Race;

/**
 * Created by luoyuan on 2017/4/22.
 */
public class MonsterDB {
    private static String buildSql(int id, String type, long atk, long hp, long def,float pet_rate,float meet_rate,float egg_rate,float silent, String des,String img, int sex, float hit, int race, long min_level){
        return String.format("replace into monster (" +
                "id,   type, atk,   hp, def, pet_rate, meet_rate, egg_rate, silent, des, image, sex, hit, race, min_level) values(" +
                "%s, '%s',  %s,    %s,'%s',   %s,       %s,        %s,       %s,  '%s', '%s',  %s,  %s,  %s,    %s)",
                id, type, atk, hp, def, pet_rate,meet_rate,egg_rate,silent,des,img,sex,hit,race,min_level);
    }
    public static void initMonsterDB(SQLiteDatabase database, Context context){
        String sql = "replace into monster (" +
                "id,   type, atk,   hp, def, pet_rate, meet_rate, egg_rate, silent, des, image, sex, hit, race, min_level) values(" +
                "%s, '%s',  %s,    %s,'%s',   %s,       %s,        %s,       %s,  '%s', '%s',  %s,  %s,  %s,    %s)";
        database.execSQL(buildSql(1, "蟑螂", 10, 80, 120,300, 310,100,0,"zl_desc","zl", -1,1, Race.Nonsr.ordinal(), 1));
//        database.execSQL(String.format(sql, 2, "大蚯蚓", 50, 90,23 ,300, 300, 0,context.getString(R.string.qy_desc),"qy",-1,1, Race.Nonsr.ordinal(), 1));
//        database.execSQL(String.format(sql, 3, "异壳虫", 15, 50,100, 260, 300, 0,context.getString(R.string.pc_desc),"pc", -1,5, Race.Orger.ordinal(), 5));
//        database.execSQL(String.format(sql, 4, "巨型飞蛾", 35, 25, 16,300, 150, 0,context.getString(R.string.feie_desc),"feie", -1,3, Race.Wizardsr.ordinal(), 5));
//        database.execSQL(String.format(sql, 5, "猪", 35, 95, 114,260, 300, 0,context.getString(R.string.zz_desc), "zz", -1,3, Race.Elyosr.ordinal(), 15));
//        database.execSQL(String.format(sql, 6, "老鼠", 56, 115, 12,300, "硝石", "", 308, 0,"老鼠是一种啮齿动物，体形有大有小。当然这个世界的老鼠大小可是和普通人一样大，所以被老鼠打败了也不要觉得丢人。",R.drawable.laoshu));
//        database.execSQL(String.format(sql, 7, "嗜血蚁", 99, 200, 112,250, "蛇骨", "嗜血蚁是一种昆虫。属节肢动物门，昆虫纲，膜翅目，蚁科。它们随时都处于嗜血的状态，只要被看到就会疯狂的进行攻击。", 150, 0,"",R.drawable.mayi));
//        database.execSQL(String.format(sql, 8, "嗜血蚁", 200, 100, 99,250, "硝石", "", 150, 0,"嗜血蚁是一种昆虫。属节肢动物门，昆虫纲，膜翅目，蚁科。它们随时都处于嗜血的状态，只要被看到就会疯狂的攻击。",R.drawable.mayi_red));
//        database.execSQL(String.format(sql, 9, "老虎", 120, 120, 120,200, "虎皮", "", 200, 0,"大型猫科动物，肚子饿的时候会假装亲近人类，肚子不饿的时候不具备攻击力。据说会捕获人类去当它的铲屎官。",R.drawable.laohu));
//        database.execSQL(String.format(sql, 10, "老虎", 120, 120,121, 200, "虎筋", "", 200, 0,"大型猫科动物，肚子饿的时候会假装亲近人类，肚子不饿的时候不具备攻击力。据说会捕获人类去当它的铲屎官。",R.drawable.laohu_red));
//        database.execSQL(String.format(sql, 11, "蛟", 205, 200, 70,150, "白云石", "", 300, 0,"龙的亚种，身躯脆肉，但是攻击力不俗。",R.drawable.jiao));
//        database.execSQL(String.format(sql, 12, "变异蝎", 40, 50, 125,300, "鼠皮", "", 200, 0,"变异的蝎子，似乎是身躯变大，并且毒性会导致敌人产生幻觉。经常被一些无良的商人抓来制作毒品卖给爬楼的勇者。",R.drawable.xiezi));
//        database.execSQL(String.format(sql, 13, "变异蝎", 40, 52,130, 300, "硝石", "", 200, 0,"变异的蝎子，似乎是身躯变大，并且毒性会导致敌人产生幻觉。经常被一些无良的商人抓来制作毒品卖给爬楼的勇者。",R.drawable.xiezi_red));
//        database.execSQL(String.format(sql, 14, "食人鸟", 60, 80, 200,200, "食人鸟毛", "", 250, 0,"一种可爱又小巧的鸟类，红色的嘴巴表示它们具有强烈的主动攻击性。",R.drawable.srn));
//        database.execSQL(String.format(sql, 15, "丑蝙蝠", 150, 20, 123,253, "鼠骨", "", 222, 0,"黑暗中生活的动物，蝙蝠本来就很丑了，更何况丑蝙蝠。",R.drawable.bianfu));
//        database.execSQL(String.format(sql, 16, "丑蝙蝠", 250, 200, 24,253, "铁矿石", "", 222, 0,"黑暗中生活的动物，蝙蝠本来就很丑了，更何况丑蝙蝠。",R.drawable.bianfu_red));
//        database.execSQL(String.format(sql, 17, "蛇", 200, 28, 200,200, "蛇筋", "", 240, 0,"滑溜溜的。",R.drawable.se));
//        database.execSQL(String.format(sql, 18, "蛇", 200, 28, 200,200, "蛟筋", "", 240, 0,"滑溜溜的。",R.drawable.se_lv));
//        database.execSQL(String.format(sql, 19, "猴", 210, 210, 20,300, "蚁须", "", 280, 0,"喜欢躲在阴暗处丢石头。似乎有一点点的智慧，据说是从外面世界逃进迷宫的人类？",R.drawable.hou));
//        database.execSQL(String.format(sql, 20, "野牛", 150, 250, 25,290, "牛骨", "", 270, 0,"据说勤劳老实木讷的人会变成一头牛。",R.drawable.niu));
//        database.execSQL(String.format(sql, 21, "龟", 10, 200, 250,250, "龟壳", "", 101, 0,"因为长寿，它们可能是唯一知道迷宫存在秘密的种族，可惜它们不会交流。",R.drawable.wugui));
//        database.execSQL(String.format(sql, 22, "三头蛇", 200, 10,201, 240, "硝石", "", 260, 0,"据说这些怪物是人为制造出来的，当然并不是简单的把三条蛇的脑袋缝在一起。",R.drawable.santoushe));
//        database.execSQL(String.format(sql, 23, "刺猬", 100, 150, 102,230, "黑石", "", 255, 0,"这个浑身都是刺的家伙，出乎意料的温柔。",R.drawable.ciwei));
//        database.execSQL(String.format(sql, 24, "狼", 150, 230, 120,225, "硝石", "", 250, 0,"据说迷宫外面世界的狼都是群居的，但是为什么迷宫里面的狼都是独立特行的呢？",R.drawable.lan));
//        database.execSQL(String.format(sql, 25, "精灵", 250, 40,180, 201, "精铁", "", 109, 0,"美貌与身材兼具的生物，但是它们可是很凶残的。",R.drawable.jingling));
//        database.execSQL(String.format(sql, 26, "精灵", 25, 40, 154,201, "硝石", "", 109, 0,"美貌与身材兼具的生物，但是它们可是很凶残的。",R.drawable.jingling_lv));
//        database.execSQL(String.format(sql, 27, "僵尸", 30, 90, 89,200, "硝石", "", 200, 0,"并不是所有的僵尸都是腐烂的尸体，这些僵尸具有可爱的外表，并且攻击力也很低。似乎是一种纯粹卖萌的怪物。",R.drawable.jiangshi));
//        database.execSQL(String.format(sql, 28, "僵尸", 30, 45, 125,200, "龙筋", "", 200, 0,"并不是所有的僵尸都是腐烂的尸体，这些僵尸具有可爱的外表，并且攻击力也很低。似乎是一种纯粹卖萌的怪物。",R.drawable.jiangshi_red));
//        database.execSQL(String.format(sql, 29, "怨魂", 230, 45, 5,20, "龙筋", "", 200, 0,"据说是那些被离去玩家抛弃的勇者们默默死亡后变成的幽灵。它们在不断寻找那些抛弃它们的人。",R.drawable.allip));
//        database.execSQL(String.format(sql, 30, "龙", 50, 100, 200,213, "玄石", "", 143, 0,"西方神话传说中的一种生物，不知道为什么会出现在迷宫中。",R.drawable.long_pet));
//        database.execSQL(String.format(sql, 31, "龙", 50, 100, 200,213, "龙筋", "", 143, 0,"西方神话传说中的一种生物，不知道为什么会出现在迷宫中。",R.drawable.long_pet_red));
//        database.execSQL(String.format(sql, 32, "骷髅", 120, 200, 10,254, "硝石", "", 101, 0,"迷宫中的死神，据说被迷宫制造者赋予了管理迷宫生物生死的权利，但是在迷宫制造者消失之后，它们就变成了暴虐的怪物。",R.drawable.kulou));
//        database.execSQL(String.format(sql, 33, "箭鹰", 220, 100, 80,54, "硝石", "", 101, 10,"终其一生都在飞翔的怪物，奇怪的是这个迷宫竟然高得足够它们飞行。",R.drawable.arrowhake));
//        database.execSQL(String.format(sql, 34, "熊", 120, 30, 220,245, "硝石", "", 278, 1,"喜欢睡觉的一种怪物，皮非常非常的厚。",R.drawable.xion));
//        database.execSQL(String.format(sql, 35, "朱厌", 165, 60, 60,101, "冷杉木", "", 121, 2,"朱厌是古代汉族神话传说中的凶兽,身形像猿猴,白头红脚。据说在迷宫世界中被创造出来最初的目的是用作为守护者存在的。",R.drawable.zhuyan));
//        database.execSQL(String.format(sql, 36, "陆吾", 100, 180, 67,100, "硝石", "", 109, 3,"陆吾即肩吾，古代神话传说中的昆仑山神名，人面虎身虎爪而九尾。喜欢养各种奇怪的怪兽，是个老实可爱的培育师。",R.drawable.luwu));
//        database.execSQL(String.format(sql, 37, "山魁", 200, 150, 56,10, "凤凰木", "", 120, 4,"山魈其实就是凶狠的大猴子，丑得让人看见就想哭。据说是堕落的勇者失去理智之后的形成的",R.drawable.shankui));
//        database.execSQL(String.format(sql, 38, "底栖魔鱼", 99, 99,155, 100, "硝石", "", 50, 20,"一种长相丑陋的的鱼形两栖生物，只是因为出现的比较晚所以看起来好像很厉害。",R.drawable.aboleth));
//        database.execSQL(String.format(sql, 39, "鹦哥兽", 199, 109,55, 100, "白杏木", "", 90, 210,"一种巨大但是无法飞行的鸟，喜欢折磨敌人。",R.drawable.achaierai));
//        database.execSQL(String.format(sql, 40, "九尾狐", 100, 230, 32,39, "白杏木", "", 99, 30,"九尾狐，中华古代汉族神话传说中的生物--青丘之山，有兽焉，其状如狐而九尾。在迷宫创造者消失后才出现的生物。",R.drawable.jiuweihu));
//        database.execSQL(String.format(sql, 41, "九尾狐", 100, 130, 199,19, "硝石", "", 99, 30,"九尾狐，中华古代汉族神话传说中的生物--青丘之山，有兽焉，其状如狐而九尾。在迷宫创造者消失后才出现的生物。",R.drawable.jiuweihu_red));
//        database.execSQL(String.format(sql, 42, "猼訑", 110, 160, 190,48, "硝石", "", 89, 7,"猼訑（bódàn）是古代汉族神话传说中一种样子像羊的怪兽。它有九条尾和四只耳朵，眼睛长在背上。据说是由迷宫创造者设立作为图腾的生物。",R.drawable.fudi));
//        database.execSQL(String.format(sql, 43, "狰", 140, 65,199, 38, "硝石", "", 77, 9,"狰是古代汉族传说中的奇兽，章峨之山有兽焉，其状如赤豹，五尾一角，其音如击石，其名曰狰。",R.drawable.zheng));
//        database.execSQL(String.format(sql, 44, "朱獳", 160, 170, 123,23, "龙须木", "", 68, 12,"朱獳（zhūrú ）是汉族神话中的兽类，样子很像狐狸，背部长有鱼的鳍。",R.drawable.zhuru));
//        database.execSQL(String.format(sql, 45, "掘地虫", 100, 80,120, 120, "硝石", "", 15, 10,"喜欢挖洞，各种不小心踩到洞掉回底层的勇者们对它是无比憎恨。",R.drawable.ankheg));
//        database.execSQL(String.format(sql, 46, "哈士奇", 120, 188,110, 5, "龙须", "", 10, 60,"哈士奇，我就是逗逼，别管我。",R.drawable.hsq));
//        database.execSQL(String.format(sql, 47, "石头人", 24, 189,200, 25, "白云石", "", 10, 10,"全身都是石头的家伙，身上还有细小的树苗，据说当树苗长大的时候就会吸收石头人的能量知道最后长成树精，然后石头人就会因为没有能量而散架。",R.drawable.shitour));
//        database.execSQL(String.format(sql, 48, "蟑螂", 240, 188, 15,25, "蚁须", "", 110, 30,"不要以为蟑螂就是个弱逼，这种奇葩的蟑螂肯定可以推倒你。",R.drawable.zhanglang_1));
//        database.execSQL(String.format(sql, 49, "蟑螂", 255, 98,24, 15, "蚁须", "", 110, 30,"不要以为蟑螂就是个弱逼，这种奇葩的蟑螂肯定可以推倒你。",R.drawable.zhanglang_2));
//        database.execSQL(String.format(sql, 50, "蟑螂", 134, 108,186, 13, "蚁须", "", 210, 60,"不要以为蟑螂就是个弱逼，这种奇葩的蟑螂等着你推倒。",R.drawable.zhanglang_3));
//        database.execSQL(String.format(sql, 51, "蟑螂", 35, 118,255, 12, "蚁须", "", 110, 10,"这只奇葩的蟑螂以为在胸口纹上一个强字就可以装B了。",R.drawable.zhanglang_4));
//        database.execSQL(String.format(sql, 52, "蟑螂", 135, 91, 255,10, "银矿石", "", 10, 10,"这只奇葩的蟑螂以为在胸口纹上一个强字就可以装B了。但他确实可以装B。",R.drawable.zhanglang_6));
//        database.execSQL(String.format(sql, 53, "蟑螂", 45, 118,234, 8, "蚁须", "", 110, 10,"牛仔蟑螂。",R.drawable.zhanglang_5));
//        database.execSQL(String.format(sql, 54, "蟑螂", 45, 118,220, 5, "蚁须", "", 10, 90,"传说中的蟑螂妹子？遇见它你都不好意思放技能了。",R.drawable.zhanglang_7));
//        database.execSQL(String.format(sql, 55, "树精", 150, 81,211, 5, "黑石", "", 3, 50,"爱好是维护和平，但是他们爱护的对象只有迷宫中的非人生物，所以遇见他们还是小心为是。你看它这么多条腿，速度肯定不会比你慢。",R.drawable.shujing));
//        database.execSQL(String.format(sql, 56, "夔牛", 30, 118, 235,60, "牛皮", "", 83, 10,"夔状如牛，一足，苍灰色，出入水必有风雨，能发出雷鸣之声，并伴以日月般的光芒。",R.drawable.kuiniu));
//        database.execSQL(String.format(sql, 57, "贝克风精", 30, 18, 35,10, "白云石", "", 3, 100,"似乎是气体化的形态，会随风改变外形。。",R.drawable.belker));
//        database.execSQL(String.format(sql, 58, "白虎", 166, 119,112, 10, "虎皮", "", 103, 20,"白虎，在中国传统文化中是道教西方七宿星君四象之一，根据五行学说，它是代表西方的灵兽，因西方属金，色白，故称白虎，代表的季节是秋季。",R.drawable.baihu));
//        database.execSQL(String.format(sql, 59, "玄武", 160, 195, 179,5, "龟壳", "", 2, 30,"玄武，北方之神，龟蛇合体。北方七神之宿，实始于斗，镇北方，主风雨。",R.drawable.xuanwu));
//        database.execSQL(String.format(sql, 60, "白泽", 86, 219, 201,15, "黑石", "", 13, 40,"白泽是中国古代汉族神话传说中昆仑山上著名的神兽。它浑身雪白，有翼，能说人话，通万物之情，很少出没，除非当世有圣人治理天下，才奉书而至，常与麒麟或凤凰等，视同为德行高的统治者治世的象征。 是可使人逢凶化吉的吉祥之兽。",R.drawable.baize));
//        database.execSQL(String.format(sql, 61, "鲛人", 76, 119,252, 15, "白云石", "", 93, 40,"鲛人，鱼尾人身，谓人鱼之灵异者。中国古代典籍中记载的鲛人即是西方神话中的人鱼，他们生产的鲛绡，入水不湿，他们哭泣的时候，眼泪会化为珍珠。鲛人的油，一旦燃烧将万年不熄。",R.drawable.jiaoren));
//        database.execSQL(String.format(sql, 62, "山膏", 117, 95, 209,10, "白杏木", "", 1, 40,"苦山，有兽焉，名曰山膏，其状如逐，赤若丹火，善骂。",R.drawable.shanhuan));
//        database.execSQL(String.format(sql, 63, "蜚", 79, 195,254, 35, "玄石", "", 5, 40,"有兽焉，其状如牛而白首，一目而蛇尾，其名曰蜚，行水则竭，行草则死，见则天下大疫。",R.drawable.fei));
//        database.execSQL(String.format(sql, 64, "九凤", 176, 119,197, 25, "黑石", "", 13, 40,"大荒之中，有山名曰北极柜。海水北注焉。有神九首，人面鸟身，句曰九凤。",R.drawable.jiufeng));
//        database.execSQL(String.format(sql, 65, "帝江", 117, 195, 100,10, "黑石", "", 1, 40,"又西三百五十里曰天山，多金玉，有青雄黄，英水出焉，而西南流注于汤谷。有神鸟，其状如黄囊，赤如丹火，六足四翼，浑敦 无面目，是识歌舞，实惟帝江也。欺负善良，喜欢残暴的人。",R.drawable.dijiang));
//        database.execSQL(String.format(sql, 66, "朱厌", 116, 95,200, 10, "钢石", "", 1, 40,"又西四百里，曰小次之山，其上多白玉，其下多赤铜。有兽焉，其状如猿，而白首赤足，名曰朱厌，见则大兵。（朱厌兽进化！朱厌妹子！）",R.drawable.zhuyan_1));
//        database.execSQL(String.format(sql, 67, "灭蒙鸟", 177, 95,231, 10, "红檀木", "", 1, 40,"灭蒙鸟在结匈国的北部，鸟的羽毛是青色的，尾巴是红色的。灭蒙鸟就是孟鸟。秦的先人，是帝颛顼的孙女，名叫修。女修在织补时，有一只玄鸟生了个卵，女修吃了下去，生了个儿子取名大业。大业娶少典的女儿少华。少华又生了大费，大费生了两个孩子，一个叫大廉，便是鸟俗氏；另一个叫若木，便是费氏。大廉的玄孙孟戏、仲衍，都是身子像鸟，但会说人的语言；因此他们是灭蒙鸟的国民，但是他们没有留下子嗣。",R.drawable.miemengniao));
//        database.execSQL(String.format(sql, 68, "天使", 76, 219,123, 15, "玄石", "", 25, 60,"代表圣洁、良善，正直，保护平民不被恶魔侵扰。",R.drawable.angle_0));
//        database.execSQL(String.format(sql, 69, "黑天使", 86, 175,253, 10, "黑石", "", 40, 70,"被人性中最最邪恶的一面污染的天使就会进化为黑天使。",R.drawable.angle_1));
//        database.execSQL(String.format(sql,70, "黑天使", 41, 105,210,10, "黑石", "", 40, 70,"被人性中最最邪恶的一面污染的天使就会进化为黑天使。",R.drawable.angle_2));
//        database.execSQL(String.format(sql, 71, "天使喵", 51, 105,255, 5, "萤石", "", 4, 65,"它的眼神为何如此忧伤，或许只是因为你不再爱它。",R.drawable.angle_miao));
//        database.execSQL(String.format(sql, 72, "初音未来", 189, 99,225, 1, "凤凰木", "", 1, 70,"仅供参考。",R.drawable.chuyin));
//        database.execSQL(String.format(sql, 81, "九尾狐", 89, 199,225, 10, "凤凰木", "", 10, 70,"九尾狐，中华古代汉族神话传说中的生物--青丘之山，有兽焉，其状如狐而九尾。这种形态的九尾狐拥有极高的智商，已然脱离妖的范畴。",R.drawable.jiuwei_kynei));
//        database.execSQL(String.format(sql, 82, "囚牛", 199, 199,25, 1, "黑石", "", 10, 70,"囚牛是古代汉族神话传说中龙生的第一个儿子。平生爱好音乐，它常常蹲在琴头上欣赏弹拨弦拉的音乐，因此琴头上便刻上它的雕像。",R.drawable.qiuniu));
//        database.execSQL(String.format(sql, 83, "爢珡", 99, 99,255, 1, "玄石", "", 10, 70,"爢珡(bā xià)。是中国古代汉族神话传说是龙生的九子之一。形似鱼非鱼，好水，又名避水兽。",R.drawable.gongwu));
//        database.execSQL(String.format(sql, 84, "穷奇", 190, 100, 83,88, "白杏木", "穷奇，汉族神话传说中的古代四凶之一，外貌像老虎又像牛，长有一双翅膀和刺猬的毛发，代表至邪之物。据说这种生物并不是创造出来的", 100, 5,"",R.drawable.qiongqi));
//        database.execSQL(String.format(sql, 85, "穷奇", 190, 100, 77,68, "紫熏木", "穷奇，汉族神话传说中的古代四凶之一，外貌像老虎又像牛，长有一双翅膀和刺猬的毛发，代表至邪之物。据说这种生物并不是创造出来的", 100, 5,"",R.drawable.qiongqi_red));
//        database.execSQL(String.format(sql, 86, "凤凰", 140, 160, 176,9, "凤凰毛", "", 2, 0,"传说中的生物，据说食人鸟捕食了人类后变异而成。",R.drawable.fengh));
//        database.execSQL(String.format(sql, 87, "梼杌", 200, 180,80, 15, "青檀木", "", 15, 40,"梼杌(táowù)是上古时期华夏神话中的四凶之一。长得很像老虎，毛长，人面、虎足、猪口牙，尾长。传说中的神死亡后其精神分化形成的",R.drawable.taowu));
//        database.execSQL(String.format(sql, 88, "作者（伪）", 210, 210,1, 0, "硝石", "", 0, 62,"游荡在这个迷宫的一种意志的化身。没有实体，所以无法捕捉。据说是因为作者在构思这个游戏的时候因为睡眠不足产生的一种怨念。",R.drawable.wenhao));
//        database.execSQL(String.format(sql, 89, "睚眦", 210, 110,81, 9, "青檀木", "", 10, 12,"睚眦是古代中国神话传说中龙的第二个儿子，总是嘴衔宝剑，怒目而视，刻镂于刀环、剑柄吞口，以增加自身的强大威力。",R.drawable.yazi));
//        database.execSQL(String.format(sql, 90, "辟邪", 130, 124,85, 3, "青檀木", "", 2, 12,"似鹿而长尾，有两角，也叫做貔貅。有镇宅辟邪的灵性，相传此灵物物嘴大无肛，能够招财纳福，极具灵力。",R.drawable.pixie));
//        database.execSQL(String.format(sql, 91, "苍龙", 60, 117,224, 1, "龙骨", "", 3, 50,"苍龙又称青龙，是古代汉族神话传说中的灵兽。属于汉族传统文化中是四象之一，身似长蛇、麒麟首、鲤鱼尾、面有长须、犄角似鹿、有五爪、相貌威武。二十八宿中东方七宿,即角、亢、氐、房、心、尾、箕这七宿的形状又极似龙形，合称苍龙。",R.drawable.changlong));
//        database.execSQL(String.format(sql,
//                73, "鱼妇", 66, 130, 200,20, "玄石", "",
//                10, 20,"有鱼偏枯，名曰鱼妇。颛顼死即复苏。风道北来，天及大水泉，蛇乃化为鱼，是为鱼妇。",R.drawable.yufu));
//        database.execSQL(String.format(sql,
//                74, "鸾鸟", 168, 243,50, 50, "玄石", "",
//                3, 50,"女床之山，有鸟焉，其状如翟而五采文，名曰鸾鸟，见则天下安宁。",R.drawable.luanniao));
//        database.execSQL(String.format(sql,
//                75, "月儿の萌萌", 255, 1, 255,1, "玄石", "",
//                1, 100,"仅供参考。",R.drawable.yueer));
    }
}
