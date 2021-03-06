package com.cscode.radytocook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cscode.radytocook.R;
import com.cscode.radytocook.model.Listdatum;
import com.cscode.radytocook.model.OrderDatum;
import com.cscode.radytocook.model.Orderdata;
import com.cscode.radytocook.model.Orderlist;
import com.cscode.radytocook.model.UserData;
import com.cscode.radytocook.retrofit.APIClient;
import com.cscode.radytocook.retrofit.GetResult;
import com.cscode.radytocook.utils.GetService;
import com.cscode.radytocook.utils.SessionManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

import static com.cscode.radytocook.retrofit.APIClient.Base_URL;
import static com.cscode.radytocook.utils.GetService.ISORDER;

public class MyordersActivity extends AppCompatActivity implements GetResult.MyListener {


    @BindView(R.id.recycle_address)
    RecyclerView recycleAddress;

    UserData userData;
    SessionManager sessionManager;
    @BindView(R.id.txt_itmecount)
    TextView txtItmecount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("My Order");

        userData = new UserData();
        sessionManager = new SessionManager(MyordersActivity.this);
        userData = sessionManager.getUserDetails("");

        LinearLayoutManager recyclerLayoutManager = new LinearLayoutManager(MyordersActivity.this);
        recycleAddress.setLayoutManager(recyclerLayoutManager);

        myOrder();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (ISORDER) {
            ISORDER = false;
            startActivity(new Intent(MyordersActivity.this, HomeActivity.class));
            finish();
        } else {
            finish();
        }
    }

    private void myOrder() {
        GetService.showPrograss(MyordersActivity.this);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uid", userData.getId());
            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().getOlist((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void callback(JsonObject result, String callNo) {

        GetService.close();
        if (callNo.equalsIgnoreCase("1")) {
            try {
                Gson gson = new Gson();
                Orderdata myOrder = gson.fromJson(result.toString(), Orderdata.class);
                if (myOrder.getOrderData().size() != 0) {
                    List<OrderDatum> datumList = new ArrayList<>();
                    for (int i = 0; i < myOrder.getOrderData().size(); i++) {
                        OrderDatum datum = myOrder.getOrderData().get(i);
                        datumList.add(datum);
                    }
                    txtItmecount.setText(datumList.size() + " orders");
                    MyOrderAdepter myOrderAdepter = new MyOrderAdepter(datumList);
                    recycleAddress.setAdapter(myOrderAdepter);
                } else {
                    txtItmecount.setText("0 orders");
                }

            } catch (Exception e) {
                e.printStackTrace();

            }

        }

    }

    public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

        private List<Orderlist> orderlists;

        public OrderAdapter(List<Orderlist> offersListIn) {
            orderlists = offersListIn;

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.order_item, parent, false);

            ViewHolder viewHolder =
                    new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Orderlist oderList = orderlists.get(position);
            holder.txtOrderid.setText("#" + oderList.getId());
            holder.txtStatus.setText("" + oderList.getStatus());
            holder.txtPrice.setText("" + oderList.getTotal());
            holder.txtDate.setText("" + oderList.getOdate());


        }

        @Override
        public int getItemCount() {
            return orderlists.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.txt_orderid)
            TextView txtOrderid;
            @BindView(R.id.txt_status)
            TextView txtStatus;
            @BindView(R.id.txt_price)
            TextView txtPrice;
            @BindView(R.id.txt_date)
            TextView txtDate;

            public ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);

            }
        }
    }

    public class MyOrderAdepter extends RecyclerView.Adapter<MyOrderAdepter.ViewHolder> {
        private List<OrderDatum> orderData;

        public MyOrderAdepter(List<OrderDatum> orderData) {
            this.orderData = orderData;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent,
                                             int viewType) {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.myorder_item, parent, false);

            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder,
                                     int position) {
            Log.e("position", "" + position);
            OrderDatum order = orderData.get(position);
            holder.txtOderid.setText("Order #00" + order.getOrderid());
            holder.txtPricetotla.setText(sessionManager.getStringData(SessionManager.CURRNCY) + " " + order.getTotalPrice());
            holder.txtDateandstatus.setText("" + order.getStatus() + " on " + order.getOdate());
            setJoinPlayrList(holder.lvlItem, order.getListdata());

            holder.lvlClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.lvlItem.getVisibility() == View.VISIBLE) {

                        holder.imgRight.setBackgroundResource(R.drawable.rights);
                        TranslateAnimation animate = new TranslateAnimation(
                                0,
                                0,
                                0,
                                holder.lvlItem.getHeight());
                        animate.setDuration(500);
                        animate.setFillAfter(true);
                        holder.lvlItem.startAnimation(animate);
                        holder.lvlItem.setVisibility(View.GONE);
                    } else {
                        holder.lvlItem.setVisibility(View.VISIBLE);
                        TranslateAnimation animate = new TranslateAnimation(
                                0,
                                0,
                                holder.lvlItem.getHeight(),
                                0);
                        animate.setDuration(500);
                        animate.setFillAfter(true);
                        holder.lvlItem.startAnimation(animate);

                        holder.imgRight.setBackgroundResource(R.drawable.ic_expand);

                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return orderData.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.txt_oderid)
            TextView txtOderid;
            @BindView(R.id.txt_pricetotla)
            TextView txtPricetotla;
            @BindView(R.id.txt_dateandstatus)
            TextView txtDateandstatus;
            @BindView(R.id.lvl_item)
            LinearLayout lvlItem;
            @BindView(R.id.lvl_click)
            LinearLayout lvlClick;
            @BindView(R.id.img_right)
            ImageView imgRight;

            public ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }
    }

    private void setJoinPlayrList(LinearLayout lnrView, List<Listdatum> listdata) {

        lnrView.removeAllViews();

        for (int i = 0; i < listdata.size(); i++) {
            Listdatum listdatum = listdata.get(i);
            LayoutInflater inflater = LayoutInflater.from(MyordersActivity.this);

            View view = inflater.inflate(R.layout.myordersub_item, null);
            ImageView imgView = view.findViewById(R.id.imageView);
            TextView txtTitle = view.findViewById(R.id.txt_title);
            TextView txtItems = view.findViewById(R.id.txt_items);
            TextView txtPrice = view.findViewById(R.id.txt_price);
            Glide.with(MyordersActivity.this).load(Base_URL + listdatum.getImage()).placeholder(R.drawable.slider).into(imgView);
            txtTitle.setText("" + listdatum.getTitle());
            txtItems.setText(" X " + listdatum.getQuantity() + " Items");
            txtPrice.setText(sessionManager.getStringData(SessionManager.CURRNCY) + " " + listdatum.getPrice() + "");

            lnrView.addView(view);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (ISORDER) {
                    ISORDER = false;
                    startActivity(new Intent(MyordersActivity.this, HomeActivity.class));
                    finish();
                } else {
                    finish();
                }
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
