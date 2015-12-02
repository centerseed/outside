package com.barry.outside;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Mac on 15/11/20.
 */
public class SiteAdapter extends RecyclerView.Adapter<SiteAdapter.viewHolder> {

    Context context;
    ArrayList<SiteInfo> siteInfos;

    public SiteAdapter(Context c, ArrayList<SiteInfo> infos) {
        context = c;
        siteInfos = infos;
    }

    @Override
    public viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater in = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = in.inflate(R.layout.listitem_site, parent, false);
        viewHolder vh = new viewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(viewHolder holder, int position) {
        SiteInfo info = siteInfos.get(position);
        holder.tvName.setText(info.name);
        holder.tvPM25.setText(info.pm25 + "");
        holder.tvPM25.setTextColor(ColorUtils.getColor(context, info.pm25));
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return siteInfos.size();
    }

    class viewHolder extends RecyclerView.ViewHolder{

        public TextView tvName;
        public TextView tvPM25;

        public viewHolder(View v) {
            super(v);
            tvName = (TextView) v.findViewById(R.id.tv_name);
            tvPM25 = (TextView) v.findViewById(R.id.tv_pm25);
        }
    }
}
