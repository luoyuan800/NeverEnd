package cn.luo.yuan.maze.client.display.dialog;

import android.content.DialogInterface;
import android.widget.EditText;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.client.service.NeverEnd;
import cn.luo.yuan.maze.client.service.ServerService;
import cn.luo.yuan.maze.client.utils.Resource;

/**
 * Copyright @Luo
 * Created by Gavin Luo on 8/16/2017.
 */
public class CdkeyDialog {
    private NeverEnd context;
    private ServerService server;
    public CdkeyDialog(NeverEnd context){
        this.context = context;
        server = new ServerService(context);
    }

    public void show(){
        final EditText editText = new EditText(context);
        editText.setHint("输入兑换码");
        SimplerDialogBuilder.build(editText, Resource.getString(R.string.conform), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                context.getExecutor().submit(new Runnable() {
                    @Override
                    public void run() {
                        final String rs = server.useCdkey(editText.getText().toString(), context);
                        context.showPopup(rs);
                    }
                });
                dialog.dismiss();
            }
        }, Resource.getString(R.string.close), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }, context);
    }

}
