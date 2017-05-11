package cn.luo.yuan.maze.display.adapter;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.model.Pet;
import cn.luo.yuan.maze.utils.Resource;
import cn.luo.yuan.maze.utils.StringUtils;

import java.util.List;

/**
 * Created by luoyuan on 2017/5/11.
 */
public class PetAdapter extends BaseAdapter {
    private List<Pet> pets;
    private Context context;
    public PetAdapter(List<Pet> pets){
        this.pets = pets;
    }
    @Override
    public int getCount() {
        return pets != null ? pets.size() : 0;
    }

    @Override
    public Pet getItem(int position) {
        if (position >= getCount()) position = 0;
        return pets.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.pet_list_view, null);
            convertView.setTag(position);
        }
        if(!convertView.getTag().equals(position)){
            Pet pet = getItem(position);
            ((TextView)convertView.findViewById(R.id.pet_name)).setText(Html.fromHtml(pet.getDisplayName()));
            ((ImageView)convertView.findViewById(R.id.pet_image)).setImageDrawable(Resource.loadMonsterImage(pet.getIndex()));
            ((TextView)convertView.findViewById(R.id.pet_level)).setText(StringUtils.formatStar(pet.getLevel()));
            ((TextView)convertView.findViewById(R.id.pet_tag)).setText(pet.getTag());
        }

        return convertView;
    }
}
