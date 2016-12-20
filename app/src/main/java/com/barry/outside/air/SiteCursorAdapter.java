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

public class SiteCursorAdapter extends AbstractRecyclerCursorAdapter {

    Context context;
    int mSiteInfos;

    public SiteCursorAdapter(Context context, Cursor c) {
        super(context, c);
        this.context = context;
    }

    public void setInfoType(int infoType) {
        mSiteInfos = infoType;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, Cursor cursor) {
        SiteInfo info = new SiteInfo(cursor);
        SiteViewHolder siteViewHolder = (SiteViewHolder) viewHolder;

        siteViewHolder.tvName.setText(info.getName());
        siteViewHolder.tvCountry.setText(info.getCountry());
        showInfoType(siteViewHolder, info);
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

    private void showInfoType(SiteViewHolder siteViewHolder, SiteInfo info) {
        if (mSiteInfos == 0) {
            siteViewHolder.tvPM25.setText(info.getPm25() + "");
            siteViewHolder.tvPM25.setTextColor(ColorUtils.getPM25Color(context, info.getPm25()));
        } else if (mSiteInfos == 1) {
            siteViewHolder.tvPM25.setText(info.getPm10() + "");
            siteViewHolder.tvPM25.setTextColor(ColorUtils.getPM10(context, info.getPm10()));
        } else if (mSiteInfos == 2) {
            siteViewHolder.tvPM25.setText(info.getPSI() + "");
            siteViewHolder.tvPM25.setTextColor(ColorUtils.getPSIColor(context, info.getPSI()));
        } else if (mSiteInfos == 3) {
            siteViewHolder.tvPM25.setText(info.getO3() + "");
            siteViewHolder.tvPM25.setTextColor(ColorUtils.getO3(context, info.getO3()));
        } else {
            siteViewHolder.tvPM25.setText(info.getCO() + "");
            siteViewHolder.tvPM25.setTextColor(ColorUtils.getCO(context, info.getCO()));
        }
    }
}
