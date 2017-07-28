package cn.luo.yuan.maze.persistence;

import android.content.Context;
import android.util.Log;
import cn.luo.yuan.maze.persistence.database.Sqlite;

import java.io.File;

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
        boolean clear = false;
        File folder  = context.getFilesDir();
        for(File file : folder.listFiles()){
            if(!file.isFile() && file.getName().contains("@")){
                Log.i("maze", "delete " + file.getName());
                file.delete();
                clear = true;
            }
        }
        if(clear) {
            Log.i("maze", "Delete DB");
            context.deleteDatabase(Sqlite.DB_NAME);
        }
    }
}
