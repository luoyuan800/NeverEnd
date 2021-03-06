package cn.luo.yuan.maze.model.real.level

/**
 * Created by luoyuan on 2017/9/7.
 */
enum class OrgerRealLevel(val point: Int, val level: Int, val display: String) {
    Bronze(5, 10, "开魔"), Silver(8, 10, "筑基"), Gold(15, 10, "魔丹"), Platinum(20, 10, "魔婴"), Diamond(30, 10, "化魔"), Adamas(40, 10, "玄魔"), Stars(40, 10, "真魔");
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

        private fun buildLevelString(level: OrgerRealLevel, point: Long) = "${level.display} + ${point / level.point}"

        fun getCurrentLevel(p: Long): OrgerRealLevel {
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