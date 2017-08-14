package cn.luo.yuan.maze.utils;

import cn.luo.yuan.maze.model.effect.Effect;

import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by gluo on 9/8/2015.
 * Use for handle string input type
 */
public class StringUtils {
    public static final String EMPTY_STRING = "";

    public static String formatPercentage(float value){
        return DecimalFormatRound(value,2) + "%";
    }

    public static boolean isCivil(String word) {
        return !(word.contains("你妈的") || word.contains("日你") || word.contains("你妈") ||
                word.contains("SB") || word.contains("sb") || word.contains("我日") ||
                word.toLowerCase().contains("fuck") || word.contains("傻逼") || word.contains("粪") || word.contains("屎") || word.contains("屁"));
    }

    public static String formatNumber(long num, boolean exquisite) {
        Double value;
        if (num > 100000000) {
            value = num / 100000000d;
            return String.format(Locale.CHINA,"%.1f", value) + "亿";
        }
        if (num > 10000000) {
            value = num / 10000000d;
            return String.format(Locale.CHINA,"%.1f", value) + "千万";
        }
        if(!exquisite) {
            if (num > 10000) {
                value = num / 10000d;
                return String.format(Locale.CHINA, "%.1f", value) + "万";
            }
        }
        return num + "";
    }

    public static String formatNumber(Number number){
        if(number instanceof Long || number instanceof Integer){
            return formatNumber(number.longValue(), false);
        }
        if(number instanceof Float){
            return DecimalFormatRound(number.floatValue(), 2);
        }
        return number.toString();
    }

    public static String toHexString(String s) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            int ch = (int) s.charAt(i);
            String s4 = Integer.toHexString(ch);
            str.append(s4);
        }
        return "0x" + str;//0x表示十六进制
    }

    //转换十六进制编码为字符串
    public static String toStringHex(String s) {
        if ("0x".equals(s.substring(0, 2))) {
            s = s.substring(2);
        }
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(
                        i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            s = new String(baKeyword, "utf-16");//UTF-16le:Not
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }

    public static boolean isNotEmpty(String countStr) {
        return countStr != null && !countStr.trim().isEmpty() && !"null".equalsIgnoreCase(countStr);
    }

    public static String[] split(String str, String regularExpression) {
        if (isNotEmpty(str)) {
            return str.split(regularExpression);
        } else {
            return new String[]{""};
        }
    }

    public static Long toLong(String number) {
        try {
            number = number.replaceFirst("~", "-");
            return Long.parseLong(number);
        } catch (Exception e) {
            try {
                return Double.valueOf(number).longValue();
            } catch (Exception e1) {
                return 1L;
            }
        }
    }

    public static Float toFloat(String number) {
        try {
            return Float.parseFloat(number);
        } catch (Exception e) {
            try {
                return Double.valueOf(number).floatValue();
            } catch (Exception e1) {
                return 0.1f;
            }
        }
    }

    public static void main(String... args) {
        System.out.print(toStringHex("0x6c81739f"));
    }

    public static Integer toInt(String type) {
        try {
            return Integer.parseInt(type);
        } catch (Exception e) {
            try {
                return Double.valueOf(type).intValue();
            } catch (Exception exp) {
                return 1;
            }
        }
    }


    /**
     * 使用DecimalFormat，保留小数点后n位数
     *
     * @param value 需要进行四舍五入的数字
     * @param scale 精度（保留小数点后几位）
     * @return 数字文本
     */
    public static String DecimalFormatRound(float value, int scale) {
        StringBuilder sb = new StringBuilder("0");
        if (scale > 0) {
            sb.append(".");
            while (scale > 0) {
                sb.append("0");
                scale--;
            }
        }
        DecimalFormat df = new DecimalFormat(sb.toString());
        df.setRoundingMode(RoundingMode.HALF_UP);
        return df.format(value);

    }

    public static String formatStar(long level) {
        return "★" + level;
    }

    public static String formatEffectsAsHtml(List<Effect> effects){
        StringBuilder builder = new StringBuilder();
        for (Effect effect : effects) {
            builder.append("<br>");
            if(effect.isElementControl()){
                builder.append("<font color=\"").
                        append(effect.isEnable() ? "#B8860B": "#A19EA0").append("\">").
                        append(effect.toString()).append("</font>");
            }else{
                builder.append("<font color=\"blue\">").
                        append(effect.toString()).append("</font>");
            }
        }

        return builder.toString();
    }

    public static String formatSex(int sex){
        return (sex == 0 ? "♂" : sex == 1 ? "♀" : "?");
    }

    public static boolean isEmpty(String object) {
        return !isNotEmpty(object);
    }

    private static DateFormat dateInstance;
    public static String getCurrentDate() {
        if(dateInstance == null){
            dateInstance = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.MEDIUM);
        }
        return dateInstance.format(new Date(System.currentTimeMillis()));
    }

    public static String formatData(long data){
        if(dateInstance == null){
            dateInstance = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.MEDIUM);
        }
        return dateInstance.format(new Date(data));
    }

    private static DateFormat timeInstance;
    public static String getCurrentTime() {
        if(timeInstance == null){
            timeInstance = DateFormat.getTimeInstance(DateFormat.MEDIUM);
        }
        return timeInstance.format(new Date(System.currentTimeMillis()));
    }


    public static Shengxiao getYear(Integer year) {
        if (year < 1900) {
            return Shengxiao.Unknown;
        }
        Integer start = 1900;
        String[] years = new String[]{
                "鼠", "牛", "虎", "兔",
                "龙", "蛇", "马", "羊",
                "猴", "鸡", "狗", "猪"
        };
        return Shengxiao.valueOf(years[(year - start) % years.length]);
    }

    public static enum Shengxiao {
        鼠(139),
        牛(140),
        虎(141),
        兔(142),
        龙(143),
        蛇(144),
        马(145),
        羊(146),
        猴(147),
        鸡(148),
        狗(149),
        猪(150),
        Unknown(0);
        int index;

        Shengxiao(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }
    }

    public static String formatIntimacyString(long intimacy) {
        String source = "<font color=\"#6A5ACD\">";
        if (intimacy < 5) {
            source += "它似乎很讨厌你。";
        } else if (intimacy < 20) {
            source += "它好像不愿搭理你。";
        } else if (intimacy <50) {
            source += "它在偷偷看你。";
        } else if (intimacy < 90) {
            source += "它有一点傲娇。";
        } else if (intimacy < 120) {
            source += "它开始喜欢你了。";
        } else if (intimacy < 170) {
            source += "它在亲近你。";
        } else if (intimacy < 210) {
            source += "它不愿意离开你。";
        } else if (intimacy > 240) {
            source += "它黏在你身上甩不掉。";
        } else {
            source += "默默的看着你...";
        }
        source += "</font>";
        return source;
    }
}
