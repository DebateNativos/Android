package com.nativos.forumriu;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nativos.forumriu.models.DebateModel;
import com.nativos.forumriu.models.UserModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.nativos.forumriu.R.drawable.user;
import static com.nativos.forumriu.R.id.listViewDebate;


public class SignIn extends Activity {

    EditText et_email = null;
    EditText et_password = null;
    Button btnSignIn;

    private RequestQueue requestQueue;
    private static String URL="";
    private StringRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_in);


        et_email = (EditText) findViewById(R.id.emailText);
        et_password = (EditText) findViewById(R.id.passwordText);

        btnSignIn = (Button) findViewById(R.id.loginButton);
        requestQueue = Volley.newRequestQueue(this);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateEmail(et_email.getText().toString())) {
                    et_email.setError("Correo no válido");
                    et_email.requestFocus();
                } else if (validatePassword(et_password.getText().toString())) {
                    et_password.setError("Campo requerido");
                    et_password.requestFocus();
                } else {
//                    URL="http://debatesapp.azurewebsites.net/podiumwebapp/ws/user/login?email="+et_email.getText().toString().trim()+"&password="+et_password.getText().toString().trim();
//                    request = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//                            try {
//                                JSONObject jsonObject = new JSONObject(response);
//                                    String status = jsonObject.getString("status");
//                                    if(status.equals("@validLogin")){
//                                        goToHome();
//                                    }
//                                    else{
//                                        Toast.makeText(getApplicationContext(),"Error"+jsonObject.get("status"), Toast.LENGTH_LONG).show();
//                                    }
//
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }, new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            error.printStackTrace();
//                            Log.d(TAG, "Error fghjk: " + error.getMessage());
//                        }
//                    }){
//                        @Override
//                        protected Map<String, String> getParams() throws AuthFailureError {
//                            HashMap<String, String> hashMap= new HashMap<String, String>();
//                            hashMap.put("email",et_email.getText().toString());
//                            hashMap.put("password",et_password.getText().toString());
//                            return hashMap;
//                        }
//                    };
//
//                    requestQueue.add(request);


                   // new JsonTask().execute("http://debatesapp.azurewebsites.net/podiumwebapp/ws/user/getAll");
                   // new JsonTask().execute("http://debatesapp.azurewebsites.net/podiumwebapp/ws/user/login?email=@gmail.com&password=123");


                    URL="http://debatesapp.azurewebsites.net/podiumwebapp/ws/user/login?email="+et_email.getText().toString().trim()+"&password="+et_password.getText().toString().trim();
                    new JsonTask().execute(URL);
                }
            }
        });
    }


    public class JsonTask extends AsyncTask<String,String, UserModel> {
        @Override
        protected UserModel doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                java.net.URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();

                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                String finalJson = buffer.toString();

                UserModel userModel = new UserModel();
                JSONObject finalObject = new JSONObject(finalJson);
                userModel.setStatus(finalObject.getString("status"));


                //String r=finalObject.getString("status");

                return userModel;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if(connection !=null){
                    connection.disconnect();}
                try {
                    if(reader !=null){
                        reader.close();}
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(UserModel result) {
            super.onPostExecute(result);

            String status = result.getStatus();

            if(status.equals("@validLogin")){
                goToHome();
            }
            else{
                Toast.makeText(SignIn.this,"Correo o contraseña incorrecta ", Toast.LENGTH_LONG).show();
            }

//            HomeFragment.DebateAdapter adapter = new HomeFragment.DebateAdapter(getActivity().getApplicationContext(), R.layout.row, result);
//            listViewDebate.setAdapter(adapter);
        }

    }


    public boolean validatePassword(String password) {

        if (password.isEmpty()) {
            return true;
        } else
            return false;
    }

    public boolean validateEmail(String email) {

        if (email.contains("@") && email.contains(".")) {
            return true;
        } else
            return false;
    }

    public void goToSignUp(View view) {
        Intent intent = new Intent(getBaseContext(), SignUp.class);
        startActivity(intent);
    }

    public void goToForgotPassword(View view) {
        Intent intent = new Intent(getBaseContext(), forgotPassword.class);
        startActivity(intent);
    }

    public void goToHome() {
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        String email = et_email.getText().toString();
        intent.putExtra("UserEmail", email);
        startActivity(intent);
    }

    public void onBackPressed() {
        //do nothing
    }

}
