package com.yiwo.friendscometogether.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Picasso;
import com.yatoooon.screenadaptation.ScreenAdapterTools;
import com.yiwo.friendscometogether.R;
import com.yiwo.friendscometogether.model.ArticleCommentListModel;

import java.util.List;

/**
 * Created by Administrator on 2018/7/19.
 */

public class MyCommentAdapter extends RecyclerView.Adapter<MyCommentAdapter.ViewHolder> {

    private Context context;
    private List<ArticleCommentListModel.ObjBean> data;
    private OnDelete onDelete;

    public void setOnDeleteListener(OnDelete onDelete){
        this.onDelete = onDelete;
    }

    public MyCommentAdapter(List<ArticleCommentListModel.ObjBean> data) {
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_my_comment, parent, false);
        ScreenAdapterTools.getInstance().loadView(view);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Glide.with(context).load(data.get(position).getUserpic()).apply(new RequestOptions().placeholder(R.mipmap.my_head).error(R.mipmap.my_head)).into(holder.ivAvatar);
        holder.tvNickname.setText(data.get(position).getUsername());
        holder.tvTitle.setText(data.get(position).getNewsTile());
        holder.tvContent.setText(data.get(position).getFctitle());
        holder.tvTime.setText(data.get(position).getFctime());
        if (data.get(position).getPic().size() > 0) {
            holder.recyclerView.setVisibility(View.VISIBLE);
            LinearLayoutManager manager = new LinearLayoutManager(context);
            manager.setOrientation(LinearLayoutManager.VERTICAL);
            holder.recyclerView.setLayoutManager(manager);
            MyCommentCommentAdapter adapter = new MyCommentCommentAdapter(data.get(position).getPic());
            holder.recyclerView.setAdapter(adapter);
        }else {
            holder.recyclerView.setVisibility(View.GONE);
        }
        holder.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDelete.onDelete(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivAvatar;
        private TextView tvNickname;
        private TextView tvTitle;
        private TextView tvContent;
        private TextView tvTime;
        private RecyclerView recyclerView;
        private TextView tvDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.activity_article_comment_rv_iv_avatar);
            tvNickname = itemView.findViewById(R.id.activity_article_comment_rv_tv_nickname);
            tvTitle = itemView.findViewById(R.id.activity_article_comment_rv_tv_title);
            tvContent = itemView.findViewById(R.id.activity_article_comment_rv_tv_content);
            tvTime = itemView.findViewById(R.id.activity_article_comment_rv_tv_time);
            recyclerView = itemView.findViewById(R.id.activity_article_comment_rv_rv);
            tvDelete = itemView.findViewById(R.id.activity_article_comment_rv_tv_delete);
        }
    }

    public interface OnDelete{
        void onDelete(int position);
    }

}
