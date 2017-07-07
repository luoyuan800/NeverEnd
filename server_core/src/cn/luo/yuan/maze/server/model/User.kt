package cn.luo.yuan.maze.server.model

import cn.luo.yuan.maze.utils.EncodeLong
import cn.luo.yuan.maze.utils.Field
import cn.luo.yuan.maze.utils.StringUtils

import java.io.Serializable

/**
 * Created by gluo on 7/4/2017.
 */
class User : Serializable {
    companion object {
        private const val serialVersionUID: Long = Field.SERVER_VERSION
    }
    private val pass = EncodeLong(111)

    fun getPass():Long{
            return pass.value
        }

    fun setPass(pass:Int){
        this.pass.value = pass as Long
    }
    var name: String? = null
    var login: Boolean = false
    var sing = StringUtils.EMPTY_STRING
    var battleInterval = 5
}
