package com.yiwo.friendscometogether.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.vise.xsnow.http.ViseHttp;
import com.vise.xsnow.http.callback.ACallback;
import com.yatoooon.screenadaptation.ScreenAdapterTools;
import com.yiwo.friendscometogether.R;
import com.yiwo.friendscometogether.adapter.FragmentToTripAdapter;
import com.yiwo.friendscometogether.base.OrderBaseFragment;
import com.yiwo.friendscometogether.custom.EditContentDialog;
import com.yiwo.friendscometogether.model.TripFragmentModel;
import com.yiwo.friendscometogether.network.NetConfig;
import com.yiwo.friendscometogether.sp.SpImp;
import com.yiwo.friendscometogether.utils.TokenUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.umeng.socialize.utils.DeviceConfig.context;

/**
 * Created by Administrator on 2018/7/18.
 */

public class ToTripFragment extends OrderBaseFragment {

    @BindView(R.id.fragment_to_trip_rv)
    RecyclerView recyclerView;
    @BindView(R.id.fragment_to_trip_refreshLayout)
    RefreshLayout refreshLayout;

    private FragmentToTripAdapter adapter;
    private List<TripFragmentModel.ObjBean> mList;

    private SpImp spImp;
    private String uid = "";

    private int page = 1;

    @Override
    public View initView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_to_trip, null);
        ScreenAdapterTools.getInstance().loadView(view);

        ButterKnife.bind(this, view);
        spImp = new SpImp(getContext());

        return view;
    }

    @Override
    public void initData() {
        initData1();
    }

    private void initData1() {

        refreshLayout.setRefreshHeader(new ClassicsHeader(getContext()));
        refreshLayout.setRefreshFooter(new ClassicsFooter(getContext()));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(final RefreshLayout refreshlayout) {
                ViseHttp.POST(NetConfig.myOrderListUrl)
                        .addParam("app_key", TokenUtils.getToken(NetConfig.BaseUrl + NetConfig.myOrderListUrl))
                        .addParam("page", "1")
                        .addParam("userID", uid)
                        .addParam("type", "2")
                        .request(new ACallback<String>() {
                            @Override
                            public void onSuccess(String data) {
                                try {
                                    JSONObject jsonObject = new JSONObject(data);
                                    if (jsonObject.getInt("code") == 200) {
                                        Gson gson = new Gson();
                                        TripFragmentModel model = gson.fromJson(data, TripFragmentModel.class);
                                        mList.clear();
                                        mList.addAll(model.getObj());
                                        adapter.notifyDataSetChanged();
                                        page = 2;
                                        refreshlayout.finishRefresh(1000);
                                    }
                                    refreshlayout.finishRefresh(1000);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFail(int errCode, String errMsg) {
                                refreshlayout.finishRefresh(1000);
                            }
                        });
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(final RefreshLayout refreshlayout) {
                ViseHttp.POST(NetConfig.myOrderListUrl)
                        .addParam("app_key", TokenUtils.getToken(NetConfig.BaseUrl + NetConfig.myOrderListUrl))
                        .addParam("page", page + "")
                        .addParam("userID", uid)
                        .addParam("type", "2")
                        .request(new ACallback<String>() {
                            @Override
                            public void onSuccess(String data) {
                                try {
                                    JSONObject jsonObject = new JSONObject(data);
                                    if (jsonObject.getInt("code") == 200) {
                                        Gson gson = new Gson();
                                        TripFragmentModel model = gson.fromJson(data, TripFragmentModel.class);
                                        mList.addAll(model.getObj());
                                        adapter.notifyDataSetChanged();
                                        page = page + 1;
                                        refreshlayout.finishLoadMore(1000);
                                    }
                                    refreshlayout.finishLoadMore(1000);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFail(int errCode, String errMsg) {
                                refreshlayout.finishLoadMore(1000);
                            }
                        });
            }
        });

        uid = spImp.getUID();

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        ViseHttp.POST(NetConfig.myOrderListUrl)
                .addParam("app_key", TokenUtils.getToken(NetConfig.BaseUrl + NetConfig.myOrderListUrl))
                .addParam("page", "1")
                .addParam("userID", uid)
                .addParam("type", "2")
                .request(new ACallback<String>() {
                    @Override
                    public void onSuccess(String data) {
                        try {
                            JSONObject jsonObject = new JSONObject(data);
                            if (jsonObject.getInt("code") == 200) {
                                Gson gson = new Gson();
                                TripFragmentModel model = gson.fromJson(data, TripFragmentModel.class);
                                mList = model.getObj();
                                adapter = new FragmentToTripAdapter(mList, getActivity());
                                recyclerView.setAdapter(adapter);
                                page = 2;
                                adapter.setOnCancelListener(new FragmentToTripAdapter.OnCancelListener() {
                                    @Override
                                    public void onCancel(final int position) {
                                        if (mList.get(position).getAllow_refund().equals("1")){
                                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                            builder.setMessage("此订单不允许退款,无法取消活动！")
                                                    .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {

                                                        }
                                                    }).show();
//                                            builder.setMessage("此订单不可退款，是否继续取消活动？")
//                                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                                                        @Override
//                                                        public void onClick(DialogInterface dialog, int which) {
//                                                            AlertDialog.Builder normalDialog = new AlertDialog.Builder(getContext());
//                                                            normalDialog.setIcon(R.mipmap.ic_launcher);
//                                                            normalDialog.setTitle("提示");
//                                                            normalDialog.setMessage("是否取消活动");
//                                                            normalDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                                                                @Override
//                                                                public void onClick(DialogInterface dialogInterface, int i) {
//                                                                    ViseHttp.POST(NetConfig.cancelOrderTripUrl)
//                                                                            .addParam("app_key", TokenUtils.getToken(NetConfig.BaseUrl + NetConfig.cancelOrderTripUrl))
//                                                                            .addParam("order_id", mList.get(position).getOID())
//                                                                            .request(new ACallback<String>() {
//                                                                                @Override
//                                                                                public void onSuccess(String data) {
//                                                                                    try {
//                                                                                        JSONObject jsonObject1 = new JSONObject(data);
//                                                                                        if (jsonObject1.getInt("code") == 200) {
//                                                                                            Toast.makeText(getContext(), "取消活动成功", Toast.LENGTH_SHORT).show();
//                                                                                            mList.remove(position);
//                                                                                            adapter.notifyDataSetChanged();
//                                                                                        }
//                                                                                    } catch (JSONException e) {
//                                                                                        e.printStackTrace();
//                                                                                    }
//                                                                                }
//
//                                                                                @Override
//                                                                                public void onFail(int errCode, String errMsg) {
//
//                                                                                }
//                                                                            });
//                                                                }
//                                                            });
//                                                            normalDialog.setNegativeButton("关闭", new DialogInterface.OnClickListener() {
//                                                                @Override
//                                                                public void onClick(DialogInterface dialogInterface, int i) {
//                                                                    dialogInterface.dismiss();
//                                                                }
//                                                            });
//                                                            // 显示
//                                                            normalDialog.show();
//                                                        }
//                                                    })
//                                                    .setNegativeButton("关闭", new DialogInterface.OnClickListener() {
//                                                        @Override
//                                                        public void onClick(DialogInterface dialog, int which) {
//
//                                                        }
//                                                    }).show();
                                        }else {
                                            EditContentDialog dialog = new EditContentDialog(getContext(), new EditContentDialog.OnReturnListener() {
                                                @Override
                                                public void onReturn(final String content) {
                                                    ViseHttp.POST(NetConfig.cancelOrderTripUrl)
                                                            .addParam("app_key", TokenUtils.getToken(NetConfig.BaseUrl + NetConfig.cancelOrderTripUrl))
                                                            .addParam("order_id", mList.get(position).getOID())
                                                            .addParam("info",content)
                                                            .request(new ACallback<String>() {
                                                                @Override
                                                                public void onSuccess(String data) {
                                                                    try {
                                                                        JSONObject jsonObject1 = new JSONObject(data);
                                                                        if (jsonObject1.getInt("code") == 200) {
                                                                            Toast.makeText(getContext(), "取消活动成功", Toast.LENGTH_SHORT).show();
                                                                            mList.remove(position);
                                                                            adapter.notifyDataSetChanged();
                                                                        }
                                                                    } catch (JSONException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }

                                                                @Override
                                                                public void onFail(int errCode, String errMsg) {

                                                                }
                                                            });
                                                }
                                            });
                                            dialog.show();

                                        }
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFail(int errCode, String errMsg) {

                    }
                });

    }
}
