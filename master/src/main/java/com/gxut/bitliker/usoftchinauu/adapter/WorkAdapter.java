package com.gxut.bitliker.usoftchinauu.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gxut.code.baseutil.util.Utils;
import com.gxut.bitliker.usoftchinauu.R;
import com.gxut.bitliker.usoftchinauu.model.Work;

import java.util.List;

/**
 * Created by Bitliker on 2017/6/26.
 */

public class WorkAdapter extends RecyclerView.Adapter<WorkAdapter.ViewHolder> {

    Context ct;
    List<Work> works;

    public WorkAdapter(Context ct, List<Work> works) {
        this.ct = ct;
        this.works = works;
    }

    public WorkAdapter(Context ct) {
        this.ct = ct;
    }


    public void setWorks(List<Work> works) {
        this.works = works;
        notifyDataSetChanged();
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ct).inflate(R.layout.item_work, parent, false);
        return new ViewHolder(view);
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        Work work = works.get(position);
        if (work != null) {
            holder.workTimeTv.setText(getTime("上班时间", work.getWorkTime()));
            holder.workSigninTv.setText(getTime("上班打卡", work.getWorkSignin()));
            holder.endTimeTv.setText(getTime("下班时间", work.getOffTime()));
            holder.endSigninTv.setText(getTime("下班打卡", work.getOffSignin()));
        }
    }

    private String getTime(String tag, String time) {
        String message = tag;
        if (!Utils.isEmpty(time)) message += "   " + time;
        return message;
    }

    public int getItemCount() {
        return Utils.getSize(works);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView workTimeTv, workSigninTv, endTimeTv, endSigninTv;

        public ViewHolder(View itemView) {
            super(itemView);
            workTimeTv = (TextView) itemView.findViewById(R.id.workTimeTv);
            workSigninTv = (TextView) itemView.findViewById(R.id.workSigninTv);
            endTimeTv = (TextView) itemView.findViewById(R.id.endTimeTv);
            endSigninTv = (TextView) itemView.findViewById(R.id.endSigninTv);
        }
    }
}
