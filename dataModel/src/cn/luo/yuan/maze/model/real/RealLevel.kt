package cn.luo.yuan.maze.model.real

/**
 * Created by luoyuan on 2017/9/7.
 */
enum class RealLevel(val point: Int, val level: Int, val display: String) {
    Bronze(5, 10, "青铜"), Silver(8, 10, "白银"), Gold(15, 10, "黄金"), Diamond(20, Int.MAX_VALUE, "钻石");

    companion object {
        fun getLevel(p: Long): String {
            var point = p;
            if (point <= Bronze.point * Bronze.level) {
                return buildLevelString(Bronze, point)
            }
            if (point <= Silver.point * Silver.level) {
                point -= Bronze.point * Bronze.level;
                return buildLevelString(Silver, point)
            }
            if (point <= Gold.point * Gold.level) {
                point -= (Bronze.point * Bronze.level + Silver.point * Silver.level)
                return buildLevelString(Gold, point)
            }
            point -= (Bronze.point * Bronze.level + Silver.point * Silver.level + Gold.point * Gold.level)
            return buildLevelString(Diamond, point)
        }

        private fun buildLevelString(level: RealLevel, point: Long) = "${level.display} + ${point / level.point}"
    }

}