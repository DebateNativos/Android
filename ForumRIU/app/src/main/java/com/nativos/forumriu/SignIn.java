package com.nativos.forumriu;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;

import com.nativos.forumriu.models.UserModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;



public class SignIn extends Activity {

    EditText et_email = null;
    EditText et_password = null;
    Button btnSignIn;
    UserModel userModel = new UserModel();
    private static String URL="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_in);

        login();
    }

    public void login(){
        et_email = (EditText) findViewById(R.id.emailText);
        et_password = (EditText) findViewById(R.id.passwordText);
        btnSignIn = (Button) findViewById(R.id.StudentButton);

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

                JSONObject parentObject = new JSONObject(finalJson);
                userModel.setStatus(parentObject.getString("status"));

                if(userModel.getStatus().equals("@validLogin")) {
                    JSONObject finalObject = parentObject.getJSONObject("user");

                    userModel.setName(finalObject.getString("name"));
                    userModel.setLastname(finalObject.getString("lastName"));
                    userModel.setLastname2(finalObject.getString("lastName2"));
                    userModel.setEmail(finalObject.getString("email"));
                }
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

        Bundle mBundle = new Bundle();
        mBundle.putParcelable("userModel", userModel);
        intent.putExtras(mBundle);

        startActivity(intent);
    }

    public void onBackPressed() {
        //do nothing
    }

}
