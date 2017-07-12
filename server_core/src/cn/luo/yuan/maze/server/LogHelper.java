package cn.luo.yuan.maze.server;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Created by gluo on 12/6/2016.
 */
public class LogHelper {
    public static final Logger logger = Logger.getLogger("NeverEndServer");
    public static void init(String root){
        try {
            File file = new File(root + "/logs");
            if(!file.exists() || !file.isDirectory()){
                file.mkdirs();
            }
            FileHandler handler = new FileHandler(root + "/logs/log.maze", 40240000, 30, true);
            handler.setFormatter(new SimpleFormatter());
            logger.addHandler(handler);
        } catch (IOException e) {
            LogHelper.error(e);
        }
    }

    public static void info(String msg){
        logger.log(Level.INFO, msg);
    }
    public static void error(Exception e, String msg){
        logger.log(Level.SEVERE, msg, e);
    }
    public static void error(Exception e){
        e.printStackTrace();
        logger.log(Level.SEVERE, "", e );
        if(e instanceof InterruptedException){
            Thread.currentThread().interrupt();
        }
    }
}
