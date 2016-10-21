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

public class forgotPassword extends Activity {

    private EditText et_email;

    Button btnSendMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_forgot_password);

        et_email = (EditText) findViewById(R.id.forgotPasswordEmailText);
        btnSendMail= (Button) findViewById(R.id.forgotPasswordButton);

        btnSendMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validateEmail(et_email.getText().toString())){
                    et_email.setError("Correo no válido");
                    et_email.requestFocus();
                }
                else{
                    Intent intent = new Intent(getBaseContext(),SignIn.class);
                    startActivity(intent);
                    Toast.makeText(forgotPassword.this,"Correo enviado",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public boolean validateEmail(String email){

        if(email.contains("@")&& email.contains(".")){
            return true;
        }
        else
            return false;
    }
}
