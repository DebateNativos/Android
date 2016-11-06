package com.nativos.forumriu;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import static android.R.attr.data;
import static android.R.attr.password;

public class SignUp extends Activity {


    private EditText et_email, et_password, et_confirmPassword, et_name,et_lastname,et_lastname2;
    Button btnSignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_up);

        et_email = (EditText) findViewById(R.id.editTextSignUpEmail);
        et_password = (EditText) findViewById(R.id.editTextSignUpPassword);
        et_confirmPassword = (EditText) findViewById(R.id.editTextSignUpConfirmPassword);
        et_name = (EditText) findViewById(R.id.editTextSignUpName);
        et_lastname = (EditText) findViewById(R.id.editTextSignUpLastName);
        et_lastname2 = (EditText) findViewById(R.id.editTextSignUpLastName2);

        btnSignUp= (Button) findViewById(R.id.buttonSignUp);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 if(validateData(et_name.getText().toString())){
                    et_name.setError("Campo requerido");
                    et_name.requestFocus();
                }
                else if(validateData(et_lastname.getText().toString())){
                    et_lastname.setError("Campo requerido");
                    et_lastname.requestFocus();
                }
                else if(validateData(et_lastname2.getText().toString())){
                    et_lastname2.setError("Campo requerido");
                    et_lastname2.requestFocus();
                }
                else if(!validateEmail(et_email.getText().toString())){
                    et_email.setError("Correo no válido");
                    et_email.requestFocus();
                }
                else if(validateData(et_password.getText().toString())){
                    et_password.setError("Campo requerido");
                    et_password.requestFocus();
                }
                else if(validateData(et_confirmPassword.getText().toString())){
                    et_confirmPassword.setError("Campo requerido");
                    et_confirmPassword.requestFocus();
                }
                 else if(!comparePassword(et_password.getText().toString(),et_confirmPassword.getText().toString())){
                     Toast.makeText(SignUp.this,"Contraseñas no coinciden",Toast.LENGTH_LONG).show();
                 }
                else{
                    Intent intent = new Intent(getBaseContext(),SignIn.class);
                    startActivity(intent);
                    Toast.makeText(SignUp.this,"Usuario registrado con éxito",Toast.LENGTH_LONG).show();
                }
            }
        });


        }

    private void registerUser() {
        final String email = et_email.getText().toString().trim();
        final String name = et_name.getText().toString().trim();
        final String password = et_password.getText().toString().trim();
        final String lastName = et_lastname.getText().toString().trim();
        final String lastName2 = et_lastname2.getText().toString().trim();

        final String registerURL = "";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, registerURL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

            }
        },
                new Response.ErrorListener() {


                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> params = new HashMap<String, String>();

                params.put("email",email);
                params.put("name",name);
                params.put("lastName",lastName);
                params.put("email",email);

                return params;

//                HashMap<String, String> hashMap = new HashMap<String, String>();
//                hashMap.put("email", et_email.getText().toString());
//                hashMap.put("password", et_password.getText().toString());
//                return hashMap;

            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public boolean validateData(String data){

        if(data.isEmpty()){
            return true;
        }
        else
            return false;
    }

    public boolean validateEmail(String email){

        if(email.contains("@")&& email.contains(".")){
            return true;
        }
        else
            return false;
    }

    public boolean comparePassword(String password,String confirmPassword){

        if(password.equals(confirmPassword)){
            return true;
        }
        else
            return false;
    }
}
