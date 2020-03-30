package com.example.finalapplication.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.finalapplication.utils.Const;
import com.example.finalapplication.utils.ImageSlider;
import com.example.finalapplication.utils.MyApplication;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.finalapplication.R;
import com.smarteist.autoimageslider.SliderPager;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductDetailsActivity extends AppCompatActivity {

    SliderView imageSlider;
    ImageSlider imageSliderAdapter;
    ProgressBar productActivityPB;
    List<String> imageProductList;
    Bundle bundle;

    TextView title_TV;
    TextView brand_TV;
    TextView p_id_TV;
    TextView selling_price_TV;
    TextView pricing_TV;
    TextView colour_TV;
    TextView size_TV;
    TextView description_TV;
    TextView in_stock_TV;
    Button ATB_button;

    ImageView product_details_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        productActivityPB = findViewById(R.id.productActivityPB);
        setSupportActionBar(toolbar);
        imageSlider = findViewById(R.id.imageSlider);

        title_TV = findViewById(R.id.title_TV);
        brand_TV = findViewById(R.id.brand_TV);
        p_id_TV = findViewById(R.id.p_id_TV);
        selling_price_TV = findViewById(R.id.selling_price_TV);
        pricing_TV = findViewById(R.id.pricing_TV);
        colour_TV = findViewById(R.id.colour_TV);
        size_TV = findViewById(R.id.size_TV);
        description_TV = findViewById(R.id.description_TV);
        in_stock_TV = findViewById(R.id.in_stock_TV);
        ATB_button = findViewById(R.id.ATB_button);

        imageProductList = new ArrayList<>();
        bundle = getIntent().getExtras();
        if(bundle !=null)
        {
            String getProductId = bundle.getString("productId");
            showImageSlider(getProductId);
        }

        product_details_back = findViewById(R.id.product_details_back);
        product_details_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductDetailsActivity.this,HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void showImageSlider(final String productId)
    {
        productActivityPB.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Const.Url.GET_PRODUCT_DETAILS_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            int success = jsonObject.getInt("status");
                            productActivityPB.setVisibility(View.GONE);
                            if(success == 1)
                            {
                                /////Images///////////
                                JSONObject obj = jsonObject.getJSONObject("data");
                                String productImg = obj.getString("images");
                                String imgList[] = productImg.split(",");
                                String imgPath = jsonObject.getString("image-url");
                                for(String getImg : imgList)
                                {
                                    imageProductList.add(imgPath+getImg);
                                }

                                imageSliderAdapter = new ImageSlider(ProductDetailsActivity.this,imageProductList);
                                imageSlider.setSliderAdapter(imageSliderAdapter);
                                imageSliderAdapter.notifyDataSetChanged();


                                String id = obj.getString("id");
                                String title = obj.getString("title");
                                String description = obj.getString("description");
                                String long_desc = obj.getString("long_desc");
                                String pricing = obj.getString("pricing");
                                String selling_price = obj.getString("selling_price");
                                String in_stock = obj.getString("in_stock");
                                String size = obj.getString("size");
                                String colour = obj.getString("colour");
                                String brand = obj.getString("brand");
                                String p_id = obj.getString("p_id");

                               String getDes = Html.fromHtml(description).toString();

                                title_TV.setText(title);
                                brand_TV.setText(brand);
                                p_id_TV.setText(p_id);
                                selling_price_TV.setText(selling_price);
                                title_TV.setText(title);
                                title_TV.setText(title);
                                pricing_TV.setText(pricing);
                                pricing_TV.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                                colour_TV.setText(colour);
                                size_TV.setText(size);
                                description_TV.setText(getDes);
                                in_stock_TV.setText(in_stock);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("showrersposneessss",error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("product_id", productId);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }
}
