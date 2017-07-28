package cn.luo.yuan.maze.persistence;

import android.content.Context;
import cn.luo.yuan.maze.persistence.database.Sqlite;

/**
 * Copyright @Luo
 * Created by Gavin Luo on 7/28/2017.
 */
public class SaveFileManager {
    private Context context;
    public SaveFileManager(Context context){
        this.context = context;
    }

    public boolean isOlderSaveExisted(){
        for(String file : context.fileList()){
            if(file.contains("Hero@")){
                return true;
            }
        }
        return false;
    }

    public void clear(){
        context.deleteDatabase(Sqlite.DB_NAME);
        for(String file: context.fileList()){
            context.deleteFile(file);
        }
    }
}
