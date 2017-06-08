package cn.luo.yuan.maze.utils

/**
 * Created by gluo on 6/8/2017.
 */
class Field {
    //Use static file should define companion object
    companion object {
        const val SERVER_VERSION: Long = 1L
        const val STATE_SUCCESS = "1"
        const val STATE_FAILED = "21"
        const val OWNER_ID_FIELD = "owner_id"
        const val VERSION_FIELD = "version"
        const val RESPONSE_TYPE = "type"
        const val RESPONSE_CODE = "code"
        const val RESPONSE_RESULT_SUCCESS = "success"
        const val RESPONSE_RESULT_OK= "OK"
        const val EXCHANGE_ID_FIELD = "ex_id"
        const val RESPONSE_OBJECT_TYPE = "object"
        const val RESPONSE_STRING_TYPE = "string"
        const val RESPONSE_NONE_TYPE = "none"


    }
}