package com.yiwo.friendscometogether.newadapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.yatoooon.screenadaptation.ScreenAdapterTools;
import com.yiwo.friendscometogether.R;
import com.yiwo.friendscometogether.activecard.CardAdapter;
import com.yiwo.friendscometogether.custom.LookPasswordDialog;
import com.yiwo.friendscometogether.model.FriendsTogethermodel;
import com.yiwo.friendscometogether.pages.DetailsOfFriendTogetherActivity;

import java.util.List;

/**
 * Created by ljc on 2019/3/19.
 */

public class SwipeFIingViewAdapter extends BaseAdapter{

    private Context context;
    public List<FriendsTogethermodel.ObjBean> data;
    private int currentPositon = 0;
    public SwipeFIingViewAdapter(Context context,List<FriendsTogethermodel.ObjBean> data) {
        this.context = context;
        this.data = data;
    }
    @Override
    public int getCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return data.get(position).hashCode();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false);
            ViewHolder holder = new ViewHolder(convertView);
            convertView.setTag(holder);
            ScreenAdapterTools.getInstance().loadView(convertView);
        }
        currentPositon = position;
        ViewHolder holder = (ViewHolder) convertView.getTag();
        Glide.with(context).load(data.get(position).getUpicurl()).apply(new RequestOptions().placeholder(R.mipmap.my_head).error(R.mipmap.my_head)).into(holder.ivAvatar);
        holder.tvNickname.setText(data.get(position).getUsername());
        holder.tvTitle.setText(data.get(position).getPftitle());
        Glide.with(context).load(data.get(position).getPfpic()).apply(new RequestOptions().placeholder(R.mipmap.zanwutupian).error(R.mipmap.zanwutupian)).into(holder.ivTitle);
        holder.tvFabuTime.setText(data.get(position).getPftime());
        holder.tvStartTime.setText("开始时间: "+data.get(position).getPfgotime());
        holder.tvRenjun.setText("人均消费: RMB"+data.get(position).getPfspend()+"/人");
        holder.tvBaoming.setText("报名人数: "+data.get(position).getHave_num()+"人");
        holder.tvShengyu.setText("剩余名额: "+data.get(position).getSurplus()+"人");
        holder.tvAddress.setText(data.get(position).getPfaddress());
        holder.rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent();
                if (TextUtils.isEmpty(data.get(position).getPfpwd())) {
                    intent.setClass(context, DetailsOfFriendTogetherActivity.class);
                    intent.putExtra("pfID", data.get(position).getPfID());
                    context.startActivity(intent);
                } else {
                    LookPasswordDialog lookPasswordDialog = new LookPasswordDialog(context, new LookPasswordDialog.SetPasswordListener() {
                        @Override
                        public boolean setActivityText(String s) {
                            if (s.equals(data.get(position).getPfpwd())) {
                                intent.setClass(context, DetailsOfFriendTogetherActivity.class);
                                intent.putExtra("pfID", data.get(position).getPfID());
                                context.startActivity(intent);
                                return true;
                            }else {
                                Toast.makeText(context,"密码错误",Toast.LENGTH_SHORT).show();
                                return false;
                            }
                        }
                    });
                    lookPasswordDialog.show();
                }
            }
        });
        return convertView;
    }
    public class ViewHolder{

        private ImageView ivAvatar;
        private TextView tvNickname;
        private TextView tvFabuTime;
        private TextView tvTitle;
        private ImageView ivTitle;
        private TextView tvStartTime;
        private TextView tvRenjun;
        private TextView tvBaoming;
        private TextView tvShengyu;
        private TextView tvAddress;
        private LinearLayout ll;
        private RelativeLayout rl;
        public ViewHolder(View itemView) {
            ivAvatar = itemView.findViewById(R.id.iv_avatar);
            tvNickname = itemView.findViewById(R.id.tv_nickname);
            tvFabuTime = itemView.findViewById(R.id.tv_fabu_time);
            tvTitle = itemView.findViewById(R.id.tv_title);
            ivTitle = itemView.findViewById(R.id.iv_title);
            tvStartTime = itemView.findViewById(R.id.tv_start_time);
            tvRenjun = itemView.findViewById(R.id.tv_renjun);
            tvBaoming = itemView.findViewById(R.id.tv_baoming);
            tvShengyu = itemView.findViewById(R.id.tv_shengyu);
            tvAddress = itemView.findViewById(R.id.tv_address);
            ll = itemView.findViewById(R.id.ll);
            rl = itemView.findViewById(R.id.click_layout);
        }
    }
}
