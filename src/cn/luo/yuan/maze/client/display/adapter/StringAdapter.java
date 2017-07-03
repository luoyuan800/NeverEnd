package cn.luo.yuan.maze.client.display.adapter;

import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.model.NameObject;

import java.util.List;

/**
 * Created by gluo on 5/12/2016.
 */
public class StringAdapter <T> extends BaseAdapter {
    protected List<T> data;
    protected View.OnClickListener listener;
    private  View.OnLongClickListener longClickListener;
    public StringAdapter(List<T> data) {
        this.data = data;
    }

    public void addAll(List<T> list){
        data.addAll(list);
        notifyDataSetChanged();
    }

    public List<T> getData(){
        return data;
    }
    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;
    }

    public void setData(List<T> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public T getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = new TextView(parent.getContext());
            ((TextView)convertView).setTextSize(18);
            if(listener!=null) {
                convertView.setOnClickListener(listener);
            }
            if(longClickListener!=null){
                convertView.setOnLongClickListener(longClickListener);
            }
        }
        T item = getItem(position);
        ((TextView)convertView).setText(Html.fromHtml(item instanceof NameObject ? ((NameObject) item).getDisplayName() : item.toString()));
        convertView.setTag(R.string.adapter, this);
        convertView.setTag(R.string.item, item);
        convertView.setTag(R.string.position, position);
        return convertView;
    }

    public void setOnLongClickListener(View.OnLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }
}
