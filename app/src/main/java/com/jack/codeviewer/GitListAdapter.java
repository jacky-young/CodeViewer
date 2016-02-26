package com.jack.codeviewer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jack.codeviewer.utils.DownloadState;

import java.util.List;

/**
 * Created by jack on 2/23/16.
 */
public class GitListAdapter extends BaseAdapter{

    private Context context;
    private LayoutInflater inflater;

    private List<DownloadState> gits;

    public GitListAdapter(Context context, List<DownloadState> gits) {
        this.context = context;
        this.gits = gits;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return gits.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        final ViewHolder holder;
        if (convertView == null) {
            v = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            holder = new ViewHolder();
            holder.text = (TextView) v.findViewById(android.R.id.text1);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        holder.text.setText(gits.get(position).getGitName());

        return v;
    }

    static class ViewHolder {
        TextView text;
    }
}
