package com.barry.outside.air;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.barry.outside.base.AbstractRecyclerCursorAdapter;
import com.barry.outside.utils.ColorUtils;
import com.barry.outside.R;

/**
 * Created by Mac on 15/12/4.
 */
public class SiteCursorAdapter extends AbstractRecyclerCursorAdapter {

    Context context;

    public SiteCursorAdapter(Context context, Cursor c) {
        super(context, c);
        this.context = context;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, Cursor cursor) {
        SiteInfo info = new SiteInfo(cursor);
        SiteViewHolder siteViewHolder = (SiteViewHolder) viewHolder;

        siteViewHolder.tvName.setText(info.getName());
        siteViewHolder.tvCountry.setText(info.getCountry());
        siteViewHolder.tvPM25.setText(info.getPm25() + "");
        siteViewHolder.tvPM25.setTextColor(ColorUtils.getColor(context, info.getPm25()));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = m_inflater.inflate(R.layout.listitem_site_details, parent, false);
        SiteViewHolder viewHolder = new SiteViewHolder(v);
        return viewHolder;
    }

    public class SiteViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        TextView tvCountry;
        TextView tvPM25;

        public SiteViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_site);
            tvCountry = (TextView) itemView.findViewById(R.id.tv_conuntry);
            tvPM25 = (TextView) itemView.findViewById(R.id.tv_pm25);
        }
    }
}
