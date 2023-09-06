package com.longluo.demo.okhttpthree;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.longluo.demo.R;
import com.longluo.demo.okhttpthree.bean.ENApplyInAppPurchaseResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Okhttp3Activity extends AppCompatActivity {

    OkHttpClient mOkHttpClient;
    Button btn_send_api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_okhttp3);
        initView();
    }

    private void initView(){
        btn_send_api = findViewById(R.id.btn_send_api);
        btn_send_api.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAsyncHttp();
            }
        });
    }

    private void getAsyncHttp() {
        mOkHttpClient = new OkHttpClient();
        Request.Builder requestBuilder = new Request.Builder().url("https://088bf283-f25f-40e6-bb56-dcc31619c72e.mock.pstmn.io/ticket");
        //可以省略，默认是GET请求
        requestBuilder.method("GET", null);
        Request request = requestBuilder.build();
        Call mCall = mOkHttpClient.newCall(request);
        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("wangshu", "onFailure");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Gson gson = new Gson();
                try {
                    // レスポンスJSONの内部JSON文字列を抽出する
                    JSONObject baseJson = new JSONObject(response.body().string());
                    Log.v("logStr:", baseJson.toString());
                    JSONObject innerJson = baseJson.getJSONObject("bkeapi");
                    String json = innerJson.toString(4);
                    String result = innerJson.getString("result");
                    ENApplyInAppPurchaseResponse bean;
                    if(result.equals("true")){
                         bean = (ENApplyInAppPurchaseResponse) gson.fromJson(json, ENApplyInAppPurchaseResponse.class);
                        Log.v("logStr: ","检证API成功");
                    }else if(result.equals("false")|| result.contains("BKE_0015") ){
                         bean = (ENApplyInAppPurchaseResponse) gson.fromJson(json, ENApplyInAppPurchaseResponse.class);
                        if (bean.getReceipt() != null) {
                            switch (bean.getReceipt().getErrorCode()) {
                                case 1:
                                    Log.v("logStr: ","已经使用了");
                                    break;
                                case 9:
                                    Log.v("logStr: ","无效ticket");
                                    break;
                                default:
                                    break;
                            }
                        }
                    }else if(result.equals("false") || (!result.equals("true") && !result.contains("BKE_0015"))){
                        Log.v("logStr: ","ticket grant error");
                    }


                } catch (JSONException e) {
                   Log.v("logStr:",e.getMessage());
                } catch (IOException e) {
                    Log.v("logStr:",e.getMessage());
                }
        }});
    }

}