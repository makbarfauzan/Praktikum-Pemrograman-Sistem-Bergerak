package id.ac.unp.ft.informatika.ppsb.baseadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ItemAdapter extends BaseAdapter {
    public ItemAdapter(Context context, ArrayList<Item> listItem) {
        this.context = context;
        this.listItem = listItem;
    }

    Context context;
    ArrayList<Item> listItem;

    @Override
    public int getCount() {
        return listItem.size();
    }

    @Override
    public Object getItem(int i) {
        return listItem.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_list, viewGroup, false);
        }
        Item item = (Item) getItem(i);
        TextView itemtext = view.findViewById(R.id.itemtext);
        TextView desctext = view.findViewById(R.id.desctext);
        itemtext.setText(item.itemno);
        desctext.setText(item.description);

        return view;
    }
}
