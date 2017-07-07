package cn.luo.yuan.maze.client.display.activity;

import android.app.Activity;
import org.jetbrains.annotations.NotNull;

/**
 * Created by gluo on 7/7/2017.
 */
public class BaseActivity extends Activity {
    public interface PermissionRequestListener{
        void result(int requestCode, String[] permissions, int[] grantResults);
    }

    public PermissionRequestListener listener;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        if(listener!=null){
            listener.result(requestCode,permissions,grantResults);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
