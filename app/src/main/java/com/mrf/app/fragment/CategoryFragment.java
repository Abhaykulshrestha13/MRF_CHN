package com.mrf.app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mrf.app.R;
import com.mrf.app.adepter.CategoryAdapter;
import com.mrf.app.model.CatAll;
import com.mrf.app.model.Catlist;
import com.mrf.app.model.UserData;
import com.mrf.app.retrofit.APIClient;
import com.mrf.app.retrofit.GetResult;
import com.mrf.app.utils.GetService;
import com.mrf.app.utils.SessionManager;
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


public class CategoryFragment extends Fragment implements GetResult.MyListener {
    @BindView(R.id.rey_category)
    RecyclerView reyCategory;
    List<Catlist> myListData = new ArrayList<>();
    SessionManager sessionManager;
    UserData userData;

    public CategoryFragment() {
    }

    // TODO: Rename and change types and number of parameters
    public static CategoryFragment newInstance(String param1, String param2) {
        CategoryFragment fragment = new CategoryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        ButterKnife.bind(this, view);
        sessionManager = new SessionManager(getActivity());
        userData = sessionManager.getUserDetails("");
        reyCategory.setHasFixedSize(true);
        reyCategory.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        getCategory();
        return view;
    }

    private void getCategory() {
        GetService.showPrograss(getActivity());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uid", userData.getId());
            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().getCategoryAll((JsonObject) jsonParser.parse(jsonObject.toString()));
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
            Gson gson = new Gson();
            CatAll catAll = gson.fromJson(result.toString(), CatAll.class);
            myListData = catAll.getCplist();
            CategoryAdapter adapter1 = new CategoryAdapter(myListData, getActivity());
            reyCategory.setAdapter(adapter1);
        }
    }
}
