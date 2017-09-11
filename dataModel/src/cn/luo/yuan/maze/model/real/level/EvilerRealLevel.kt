package cn.luo.yuan.maze.model.real.level

/**
 * Created by luoyuan on 2017/9/7.
 */
enum class EvilerRealLevel(val point: Int, val level: Int, val display: String) {
    Bronze(5, 10, "凝丹"), Silver(8, 10, "中青"), Gold(15, 10, "梦结"), Platinum(20, 10, "七灵"), Diamond(30, 10, "万象"), Adamas(40, 10, "万劫"), Stars(40, 10, "妖尊");

    companion object {
        fun getLevel(p: Long): String {
            var point = p
            if (point < Bronze.level * Bronze.point) {
                return buildLevelString(Bronze, point)
            }
            point -= Bronze.level * Bronze.point
            if (point < Silver.point * Silver.level) {
                return buildLevelString(Silver, point)
            }
            point -= Bronze.level * Bronze.point + Silver.point * Silver.level
            if (point < Gold.point * Gold.level) {
                return buildLevelString(Gold, point)
            }
            point -= Bronze.level * Bronze.point + Silver.point * Silver.level + Gold.point * Gold.level
            if (point < Platinum.point * Platinum.level) {
                return buildLevelString(Platinum, point)
            }
            point -= Bronze.level * Bronze.point + Silver.point * Silver.level + Gold.point * Gold.level + Platinum.point * Platinum.level
            if (point < Diamond.point * Diamond.level) {
                return buildLevelString(Diamond, point)
            }
            point -= Bronze.level * Bronze.point + Silver.point * Silver.level + Gold.point * Gold.level + Platinum.point * Platinum.level + Diamond.point * Diamond.level
            if (point < Adamas.point * Adamas.level) {
                return buildLevelString(Adamas, point)
            }
            point -= Bronze.level * Bronze.point + Silver.point * Silver.level + Gold.point * Gold.level +
                    Platinum.point * Platinum.level + Diamond.point * Diamond.level + Adamas.point * Adamas.level
            return buildLevelString(Stars, point)
        }

        private fun buildLevelString(level: EvilerRealLevel, point: Long) = "${level.display} + ${point / level.point}"

        fun getCurrentLevel(p: Long): EvilerRealLevel {
            var point = p
            if (point < Bronze.level * Bronze.point) {
                return Bronze
            }
            point -= Bronze.level * Bronze.point
            if (point < Silver.point * Silver.level) {
                return Silver
            }
            point -= Bronze.level * Bronze.point + Silver.point * Silver.level
            if (point < Gold.point * Gold.level) {
                return Gold
            }
            point -= Bronze.level * Bronze.point + Silver.point * Silver.level + Gold.point * Gold.level
            if (point < Platinum.point * Platinum.level) {
                return Platinum
            }
            point -= Bronze.level * Bronze.point + Silver.point * Silver.level + Gold.point * Gold.level + Platinum.point * Platinum.level
            if (point < Diamond.point * Diamond.level) {
                return Diamond
            }
            point -= Bronze.level * Bronze.point + Silver.point * Silver.level + Gold.point * Gold.level + Platinum.point * Platinum.level + Diamond.point * Diamond.level
            if (point < Adamas.point * Adamas.level) {
                return Adamas
            }
            point -= Bronze.level * Bronze.point + Silver.point * Silver.level + Gold.point * Gold.level +
                    Platinum.point * Platinum.level + Diamond.point * Diamond.level + Adamas.point * Adamas.level
            return Stars
        }

        fun isLevelUp(cp: Long, pp:Long):Boolean{
            return getCurrentLevel(cp)!= getCurrentLevel(pp)
        }
    }

}