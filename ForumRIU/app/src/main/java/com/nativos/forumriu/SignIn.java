package com.nativos.forumriu;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class SignIn extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_in);
    }


    public void goToSignUp(View view){
        Intent intent = new Intent(getBaseContext(),SignUp.class);
        startActivity(intent);
    }

    public void goToForgotPassword(View view){
        Intent intent = new Intent(getBaseContext(),forgotPassword.class);
        startActivity(intent);
    }

    public void goToHome(View view){
        Intent intent = new Intent(getBaseContext(),MainActivity.class);
        startActivity(intent);
    }
}
