package cn.luo.yuan.maze.display.view;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.model.Pet;
import cn.luo.yuan.maze.service.PetMonsterLoder;

/**
 * Created by luoyuan on 2016/9/15.
 */
public class PetTextView extends LinearLayout {
    public PetTextView(Context context, Pet pet) {
        super(context);
        View view = View.inflate(context, R.layout.pet_list_view, null);
        ((TextView) view.findViewById(R.id.pet_name)).setText(Html.fromHtml(pet.getDisplayNameWithLevel()));
        ((ImageView) view.findViewById(R.id.pet_image)).setImageDrawable(PetMonsterLoder.loadMonsterImage(pet.getIndex()));
        view.findViewById(R.id.pet_tag).setVisibility(GONE);
        this.addView(view);
    }

    public void changePet(Pet pet) {
        ((TextView) findViewById(R.id.pet_name)).setText(Html.fromHtml(pet.getDisplayNameWithLevel()));
        ((ImageView) findViewById(R.id.pet_image)).setImageDrawable(PetMonsterLoder.loadMonsterImage(pet.getIndex()));
    }

}
