package cn.luo.yuan.maze.utils

/**
 * Created by gluo on 6/8/2017.
 */
class Field {
    //Use static file should define companion object
    companion object {
        const val VERIFY_RESULT: String = "verify_result"
        const val TASK_ID: String = "task_id"
        const val FILE_NAME: String = "fileName"
        const val LEVEL: String = "level"
        const val SIGN_FIELD: String = "sign"
        const val LIMIT_STRING: String = "limit"
        const val EXPECT_TYPE: String = "expectType"
        const val INDEX: String = "index"
        const val COUNT: String = "count"
        const val SERVER_VERSION: Long = 506L
        const val SERVER_VERSION_FIELD: String = "server_version"
        const val STATE_SUCCESS = "1"
        const val STATE_FAILED = "21"
        const val STATE_ACKNOWLEDGE = "11"
        const val OWNER_ID_FIELD = "owner_id"
        const val VERSION_FIELD = "version"
        const val RESPONSE_TYPE = "type"
        const val RESPONSE_CODE = "code"
        const val LOCAL_TASK_VERSION = "local_task_version"
        const val RESPONSE_RESULT_SUCCESS = "success"
        const val RESPONSE_RESULT_FAILED = "failed"
        const val RESPONSE_RESULT_OK= RESPONSE_RESULT_SUCCESS
        const val ITEM_ID_FIELD = "ex_id"
        const val RESPONSE_OBJECT_TYPE = "object"
        const val RESPONSE_STRING_TYPE = "string"
        const val RESPONSE_NONE_TYPE = "none"
        const val SERVER_URL = "http://DSGHFQ6Q52.prod.quest.corp:8888/Web_Server/app/"
        const val PET_TYPE = 1;
        const val ACCESSORY_TYPE = 2;
        const val GOODS_TYPE = 3;
        const val WAREHOUSE_EXPIRE_TIME = 7*24*60*60*1000L
        const val WAREHOUSE_ID_FIELD = "warehouse_id"
        const val WAREHOUSE_TYPE_FIELD = "warehouse_type"
        const val NOT_YOUR_ITEM = "22"
        const val NECKLACE_TYPE = "项链"
        const val RING_TYPE = "戒指"
        const val HAT_TYPE = "帽子"
        const val SWORD_TYPE = "武器"
        const val ARMOR_TYPR = "防具"



    }
}