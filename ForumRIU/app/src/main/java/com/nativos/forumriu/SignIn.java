package com.nativos.forumriu;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

import static android.R.attr.button;
import static android.R.attr.id;
import static android.R.attr.name;
import static android.os.Build.VERSION_CODES.N;
import static com.nativos.forumriu.R.drawable.user;

public class SignIn extends Activity {

    EditText et_email = null;
    EditText et_password = null;
    Button btnSignIn;
    private TextView tvData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_in);


        et_email = (EditText) findViewById(R.id.emailText);
        et_password = (EditText) findViewById(R.id.passwordText);

        btnSignIn = (Button) findViewById(R.id.loginButton);
        tvData=(TextView)findViewById(R.id.jasonTextView);


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
                   // new JsonTask().execute("http://debatesapp.azurewebsites.net/podiumwebapp/ws/user/getAll");
                    // new JsonTask().execute("http://debatesapp.azurewebsites.net/podiumwebapp/ws/user/login?email=@gmail&password=123");
                   goToHome();
                }
            }
        });
    }

    public class JsonTask extends AsyncTask<String,String, String>{
        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url  =new URL(params[0]);
                connection =(HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();

                String line="";

                while ((line = reader.readLine())!=null){
                    buffer.append(line);
                }
                String finalJson= buffer.toString();
               // JSONArray parentArray = new JSONArray(finalJson);

               // JSONArray parentArray = parentObject.getJSONArray("user");

                JSONObject parentObject = new JSONObject(finalJson);

                UserModel userModel = new UserModel();
                userModel.setName(parentObject.getJSONObject("user").getString("name"));
                userModel.setLastname(parentObject.getJSONObject("user").getString("lastName"));



//                for (int i=0; i<parentArray.length();i++) {
//                    JSONObject finalObject = parentArray.getJSONObject(i);
//
//                    String name = finalObject.getString("name");
//                    String lastname = finalObject.getString("lastName");
//
//                    finalBufferedData.append(name+" "+lastname+"\n");

               // }


                return userModel.getName()+userModel.getLastname();

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
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            tvData.setText(result);

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
