package com.example.finalapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.finalapplication.R;
import com.example.finalapplication.utils.InternetDetecter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.android.volley.Request.*;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText login_email, login_password;
    CheckBox show_hide_password;
    TextView forgot_password, createAccount;
    Button loginBtn;
    ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login_email = findViewById(R.id.login_email);
        login_password = findViewById(R.id.login_password);
        show_hide_password = findViewById(R.id.show_hide_password);
        forgot_password = findViewById(R.id.forgot_password);
        createAccount = findViewById(R.id.createAccount);
        loginBtn = findViewById(R.id.loginBtn);
        pb = findViewById(R.id.pb);

        forgot_password.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
        createAccount.setOnClickListener(this);
        show_hide_password.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    login_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    login_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.loginBtn:
                checkValidation();
                break;

            case R.id.forgot_password:
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
                break;

            case R.id.createAccount:
                Intent intent1 = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent1);
                break;
        }
    }

    private void checkValidation() {
        String mobile = login_email.getText().toString();
        String pass = login_password.getText().toString();

        if (mobile.isEmpty() || pass.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Field can't be empty", Toast.LENGTH_SHORT).show();
        }  else if (login_password.length() <= 6) {
            Toast.makeText(LoginActivity.this, "Password legth is too short", Toast.LENGTH_SHORT).show();
        } else {
            if (InternetDetecter.isInternet(LoginActivity.this)){
                userLogin(mobile, pass);
                /*validateUserInfo(EmailId, Password);*/
            }else {
                Toast.makeText(LoginActivity.this,"Please check the internet",Toast.LENGTH_SHORT).show();
            }

        }
    }

    private boolean validateEmail(String emailId) {
        Pattern pattern;
        Matcher matcher;
        String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(emailId);
        return matcher.matches();
    }

    private void validateUserInfo(String user_name, String user_pass) {
        String myPackageName = getPackageName();
        SharedPreferences sp = getSharedPreferences(myPackageName + "user_login_info", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("userName", user_name);
        editor.putString("userPass", user_pass);

        if (editor.commit()) {
            Toast.makeText(this, "Login successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();

        } else {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    private void userLogin(final String email, final String pass) {
        loginBtn.setEnabled(false);
        pb.setVisibility(View.VISIBLE);
        String URL = "http://consoledude.com/telepho_shop/ciapi/api/Api/userlogin";
        StringRequest stringRequest = new StringRequest(Method.POST, URL, new Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int success = jsonObject.getInt("status");
                    Log.d("checkresponse","excepfastion == "+response);

                    if (success == 1) {
                        pb.setVisibility(View.GONE);
                        loginBtn.setEnabled(true);

                        JSONObject obj = jsonObject.getJSONObject("data");
                        String phone = obj.getString("phone");
                        String password = obj.getString("password");
                        validateUserInfo(phone,password);

                    } else if(success == 0) {
                        Toast.makeText(LoginActivity.this, "Entered invalid user info", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("checkresponse","exception == "+response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("mobileno", email);
                params.put("password", pass);
                return params;
            }
        };

        Volley.newRequestQueue(LoginActivity.this).add(stringRequest);
    }
}