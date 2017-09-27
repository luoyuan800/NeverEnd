package cn.luo.yuan.maze.model.dlc

import cn.luo.yuan.`object`.IDModel
import java.io.Serializable

/**
 * Created by luoyuan on 2017/8/9.
 */
interface DLC: IDModel, Serializable {
    var title: String

    var desc: String

    var debrisCost: Int

    override fun getId():String{
        return title
    }

    override fun setId(id:String){
        title = id;
    }

    fun clone():DLC

}
