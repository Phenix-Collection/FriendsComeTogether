package com.yiwo.friendscometogether.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.vise.xsnow.http.ViseHttp;
import com.vise.xsnow.http.callback.ACallback;
import com.yatoooon.screenadaptation.ScreenAdapterTools;
import com.yiwo.friendscometogether.R;
import com.yiwo.friendscometogether.adapter.FragmentAllOrderAdapter;
import com.yiwo.friendscometogether.adapter.FragmentToPayAdapter;
import com.yiwo.friendscometogether.base.OrderBaseFragment;
import com.yiwo.friendscometogether.custom.EditContentDialog;
import com.yiwo.friendscometogether.custom.WeiboDialogUtils;
import com.yiwo.friendscometogether.model.AllOrderFragmentModel;
import com.yiwo.friendscometogether.model.FocusOnToFriendTogetherModel;
import com.yiwo.friendscometogether.model.OrderToPayModel;
import com.yiwo.friendscometogether.model.PayFragmentModel;
import com.yiwo.friendscometogether.model.UserFocusModel;
import com.yiwo.friendscometogether.model.UserReleaseModel;
import com.yiwo.friendscometogether.network.NetConfig;
import com.yiwo.friendscometogether.network.UMConfig;
import com.yiwo.friendscometogether.pages.ApplyActivity;
import com.yiwo.friendscometogether.pages.CreateFriendRememberActivity;
import com.yiwo.friendscometogether.pages.CreateIntercalationActivity;
import com.yiwo.friendscometogether.pages.MyFocusActivity;
import com.yiwo.friendscometogether.sp.SpImp;
import com.yiwo.friendscometogether.utils.StringUtils;
import com.yiwo.friendscometogether.utils.TokenUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

import static com.umeng.socialize.utils.DeviceConfig.context;

/**
 * Created by Administrator on 2018/7/18.
 */

public class AllOrderFragment extends OrderBaseFragment {

    @BindView(R.id.fragment_order_rv)
    RecyclerView recyclerView;
    @BindView(R.id.fragment_all_order_refreshLayout)
    RefreshLayout refreshLayout;

    private FragmentAllOrderAdapter adapter;
    private List<AllOrderFragmentModel.ObjBean> mList;

    private SpImp spImp;
    private String uid = "";

    private IWXAPI api;

    private int page = 1;

    private PopupWindow popupWindow;

    private static final int SDK_PAY_FLAG = 1;

    @Override
    public View initView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_all_order, null);
        ScreenAdapterTools.getInstance().loadView(view);

        ButterKnife.bind(this, view);
        api = WXAPIFactory.createWXAPI(getContext(), null);
        spImp = new SpImp(getContext());
        uid = spImp.getUID();
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
                        .addParam("type", "0")
                        .request(new ACallback<String>() {
                            @Override
                            public void onSuccess(String data) {
                                try {
                                    JSONObject jsonObject = new JSONObject(data);
                                    if (jsonObject.getInt("code") == 200) {
                                        Gson gson = new Gson();
                                        AllOrderFragmentModel model = gson.fromJson(data, AllOrderFragmentModel.class);
                                        page = 2;
                                        mList.clear();
                                        mList.addAll(model.getObj());
                                        adapter.notifyDataSetChanged();
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
                        .addParam("type", "0")
                        .request(new ACallback<String>() {
                            @Override
                            public void onSuccess(String data) {
                                try {
                                    JSONObject jsonObject = new JSONObject(data);
                                    if (jsonObject.getInt("code") == 200) {
                                        Gson gson = new Gson();
                                        AllOrderFragmentModel model = gson.fromJson(data, AllOrderFragmentModel.class);
                                        page = page + 1;
                                        mList.addAll(model.getObj());
                                        adapter.notifyDataSetChanged();
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
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        ViseHttp.POST(NetConfig.myOrderListUrl)
                .addParam("app_key", TokenUtils.getToken(NetConfig.BaseUrl + NetConfig.myOrderListUrl))
                .addParam("page", "1")
                .addParam("userID", uid)
                .addParam("type", "0")
                .request(new ACallback<String>() {
                    @Override
                    public void onSuccess(String data) {
                        try {
                            JSONObject jsonObject = new JSONObject(data);
                            if (jsonObject.getInt("code") == 200) {
                                Gson gson = new Gson();
                                AllOrderFragmentModel model = gson.fromJson(data, AllOrderFragmentModel.class);
                                mList = model.getObj();
                                adapter = new FragmentAllOrderAdapter(mList, getActivity());
                                recyclerView.setAdapter(adapter);
                                page = 2;
                                adapter.setOnPayListener(new FragmentAllOrderAdapter.OnPayListener() {
                                    @Override
                                    public void onPay(int position) {
                                        showCompletePopupwindow(mList.get(position).getOID());
                                    }
                                });
                                adapter.setOnCancelListener(new FragmentAllOrderAdapter.OnCancelListener() {
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
                                adapter.setOnDeleteListener(new FragmentAllOrderAdapter.OnDeleteListener() {
                                    @Override
                                    public void onDelete(final int position) {
                                        AlertDialog.Builder normalDialog = new AlertDialog.Builder(getContext());
                                        normalDialog.setIcon(R.mipmap.ic_launcher);
                                        normalDialog.setTitle("提示");
                                        normalDialog.setMessage("是否删除活动");
                                        normalDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                ViseHttp.POST(NetConfig.deleteOrderTripUrl)
                                                        .addParam("app_key", TokenUtils.getToken(NetConfig.BaseUrl + NetConfig.deleteOrderTripUrl))
                                                        .addParam("userID",uid)
                                                        .addParam("order_id", mList.get(position).getOID())
                                                        .request(new ACallback<String>() {
                                                            @Override
                                                            public void onSuccess(String data) {
                                                                try {
                                                                    JSONObject jsonObject1 = new JSONObject(data);
                                                                    if (jsonObject1.getInt("code") == 200) {
                                                                        Toast.makeText(getContext(), "删除活动成功", Toast.LENGTH_SHORT).show();
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
                                        normalDialog.setNegativeButton("关闭", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }
                                        });
                                        // 显示
                                        normalDialog.show();
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

    public void wxPay(OrderToPayModel.ObjBean model) {
        api.registerApp(UMConfig.WECHAT_APPID);
        PayReq req = new PayReq();
        req.appId = model.getAppId();
        req.partnerId = model.getPartnerId();
        req.prepayId = model.getPrepayId();
        req.nonceStr = model.getNonceStr();
        req.timeStamp = model.getTimestamp() + "";
        req.packageValue = model.getPackageX();
        req.sign = model.getSign();
        req.extData = "app data";
        api.sendReq(req);
    }

    public void aliPay(String info) {
        final String orderInfo = info;   // 订单信息

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(getActivity());
                Map<String, String> result = alipay.payV2(orderInfo,true);
                Log.e("123123", result.toString());
                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };
        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG:
                    Map<String, String> result = (Map<String, String>) msg.obj;
                    if(result.get("resultStatus").equals("9000")){
                        Toast.makeText(getContext(), "支付成功", Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                    }
                    break;
            }
        }

    };

    private void showCompletePopupwindow(final String OId) {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.popupwindow_paytype, null);
        ScreenAdapterTools.getInstance().loadView(view);

        TextView tvWx = view.findViewById(R.id.popupwindow_complete_tv_release);
//        TextView tvSave = view.findViewById(R.id.popupwindow_complete_tv_save);
        TextView tvAli = view.findViewById(R.id.popupwindow_complete_tv_next);
        TextView tvCancel = view.findViewById(R.id.popupwindow_complete_tv_cancel);

        popupWindow = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);
        // 设置点击窗口外边窗口消失
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        // 设置popWindow的显示和消失动画
        popupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);
        WindowManager.LayoutParams params = getActivity().getWindow().getAttributes();
        params.alpha = 0.5f;
        getActivity().getWindow().setAttributes(params);
        popupWindow.update();

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            // 在dismiss中恢复透明度
            public void onDismiss() {
                WindowManager.LayoutParams params = getActivity().getWindow().getAttributes();
                params.alpha = 1f;
                getActivity().getWindow().setAttributes(params);
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });

        tvWx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                ViseHttp.POST(NetConfig.orderToPayUrl)
                        .addParam("app_key", TokenUtils.getToken(NetConfig.BaseUrl + NetConfig.orderToPayUrl))
                        .addParam("order_id", OId)
                        .addParam("type", "0")
                        .request(new ACallback<String>() {
                            @Override
                            public void onSuccess(String data) {
                                Log.e("222", data);
                                try {
                                    JSONObject pay = new JSONObject(data);
                                    if (pay.getInt("code") == 200) {
                                        Gson gson1 = new Gson();
                                        OrderToPayModel model1 = gson1.fromJson(data, OrderToPayModel.class);
                                        wxPay(model1.getObj());
                                        getActivity().finish();
                                    }else {
                                        Toast.makeText(getContext(),pay.getString("message"),Toast.LENGTH_SHORT).show();
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

        tvAli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                ViseHttp.POST(NetConfig.orderToPayUrl)
                        .addParam("app_key", TokenUtils.getToken(NetConfig.BaseUrl + NetConfig.orderToPayUrl))
                        .addParam("order_id", OId)
                        .addParam("type", "1")
                        .request(new ACallback<String>() {
                            @Override
                            public void onSuccess(String data) {
                                Log.e("222", data);
                                try {
                                    JSONObject pay = new JSONObject(data);
                                    if (pay.getInt("code") == 300) {
                                        aliPay(pay.optString("obj"));
                                    }else {
                                        Toast.makeText(getContext(),pay.getString("message"),Toast.LENGTH_SHORT).show();
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

    }

}
