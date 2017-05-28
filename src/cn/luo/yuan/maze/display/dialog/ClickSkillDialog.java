package cn.luo.yuan.maze.display.dialog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.skill.click.*;
import cn.luo.yuan.maze.service.GameContext;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by luoyuan on 2016/7/9.
 */
public class ClickSkillDialog implements View.OnClickListener {
    private AlertDialog dialog;
    private View view;
    private int index = 0;
    private GameContext context;
    public ClickSkillDialog(GameContext context){
        dialog = new AlertDialog.Builder(context.getContext()).create();
        view = View.inflate(context.getContext(), R.layout.click_skill_list, null);
        dialog.setView(view);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "关闭", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    public void show(int index, boolean help){
        dialog.show();
        view.findViewById(R.id.set_baoza_button).setOnClickListener(this);
        view.findViewById(R.id.set_yiji_button).setOnClickListener(this);
        if(help){
            view.findViewById(R.id.set_baoza_button).setVisibility(View.GONE);
            view.findViewById(R.id.set_yiji_button).setVisibility(View.GONE);
        }else{
            this.index = index;
            Hero hero = context.getHero();
            if(hero!=null){
                Set<String> names = new HashSet<String>(3);
                ArrayList<ClickSkill> skills = new ArrayList<>(hero.getClickSkills());
                if(skills.size() > 0 && skills.get(0)!=null){
                    names.add(skills.get(0).getName());
                }
                if(skills.size() > 1 && skills.get(1)!=null){
                    names.add(skills.get(1).getName());
                }if(skills.size() > 2 && skills.get(2)!=null){
                    names.add(skills.get(2).getName());
                }
                if(names.contains("宠爆"))
                    view.findViewById(R.id.set_baoza_button).setVisibility(View.GONE);
                if(names.contains("一击"))
                    view.findViewById(R.id.set_yiji_button).setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.set_baoza_button:
                BaoZha baoZha = new BaoZha();
                setClickSkill(baoZha);
                break;
            case R.id.set_yiji_button:
                YiJi yiJi = new YiJi();
                setClickSkill(yiJi);
                break;
        }
    }

    public void setClickSkill(ClickSkill clickSkill){
        context.getHero().getClickSkills().add(clickSkill);
        dialog.dismiss();
    }
}
