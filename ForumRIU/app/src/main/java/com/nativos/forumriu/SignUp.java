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
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.R.attr.name;


public class SignUp extends Activity {

    private EditText et_email, et_password, et_confirmPassword, et_name, et_lastname, et_lastname2, et_phone, et_address;
    private Button btnSignUp;
    private static String URL = "";
    private String name, lastname, lastname2, address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_up);

        signUp();
    }


    public void signUp() {
        et_email = (EditText) findViewById(R.id.editTextSignUpEmail);
        et_password = (EditText) findViewById(R.id.editTextSignUpPassword);
        et_confirmPassword = (EditText) findViewById(R.id.editTextSignUpConfirmPassword);
        et_name = (EditText) findViewById(R.id.editTextSignUpName);
        et_lastname = (EditText) findViewById(R.id.editTextSignUpLastName);
        et_lastname2 = (EditText) findViewById(R.id.editTextSignUpLastName2);
        et_phone = (EditText) findViewById(R.id.editTextSignUpPhone);
        et_address = (EditText) findViewById(R.id.editTextSignUpAddress);

        name = et_name.getText().toString().trim();
        lastname = et_lastname.getText().toString().trim();
        lastname2 = et_lastname2.getText().toString().trim();
        address = et_address.getText().toString().trim();

        btnSignUp = (Button) findViewById(R.id.buttonSignUp);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name = et_name.getText().toString().trim();
                lastname = et_lastname.getText().toString().trim();
                lastname2 = et_lastname2.getText().toString().trim();
                address = et_address.getText().toString().trim();

                if (validateData(name)) {
                    et_name.setError("Campo requerido");
                    et_name.requestFocus();
                } else if (validateData(lastname)) {
                    et_lastname.setError("Campo requerido");
                    et_lastname.requestFocus();
                } else if (validateData(lastname2)) {
                    et_lastname2.setError("Campo requerido");
                    et_lastname2.requestFocus();
                } else if (!validateEmail(et_email.getText().toString())) {
                    et_email.setError("Correo no válido");
                    et_email.requestFocus();
                } else if (validateData(et_password.getText().toString())) {
                    et_password.setError("Campo requerido");
                    et_password.requestFocus();
                } else if (validateData(et_confirmPassword.getText().toString())) {
                    et_confirmPassword.setError("Campo requerido");
                    et_confirmPassword.requestFocus();
                } else if (!comparePassword(et_password.getText().toString(), et_confirmPassword.getText().toString())) {
                    Toast.makeText(SignUp.this, "Contraseñas no coinciden", Toast.LENGTH_LONG).show();
                } else {
                    name = name.replace(" ", "%20");
                    lastname = lastname.replace(" ", "%20");
                    lastname2 = lastname2.replace(" ", "%20");
                    address = address.replace(" ", "%20");

                    Toast.makeText(SignUp.this, " Registrando... ", Toast.LENGTH_SHORT).show();
                    URL = "http://debatesapp.azurewebsites.net/podiumwebapp/ws/user/registeruser?name=" + name + "&lastname=" + lastname + "&lastname2=" + lastname2 + "&email=" + et_email.getText().toString().trim() + "&password=" + et_password.getText().toString().trim() + "&phone=" + et_phone.getText().toString().trim() + "&address=" + address + "&idUniversity=123";

                    new JsonTask().execute(URL);

                }
            }
        });


    }

    public class JsonTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
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
                String status = buffer.toString();

                return status;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            String status = result;

            if (status != null && status.equals("@validRegistration")) {
                Intent intent = new Intent(getBaseContext(), SignIn.class);
                startActivity(intent);
                Toast.makeText(SignUp.this, "Usuario registrado con éxito", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(SignUp.this, "Error en registro ", Toast.LENGTH_LONG).show();
            }
        }
    }


    public boolean validateData(String data) {

        if (data.isEmpty()) {
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

    public boolean comparePassword(String password, String confirmPassword) {

        if (password.equals(confirmPassword)) {
            return true;
        } else
            return false;
    }


}
