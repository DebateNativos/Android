package com.nativos.forumriu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class PublicActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_public);
    }

    public void goToSendQuestion(View view) {
        Intent intent = new Intent(getBaseContext(), QuestionActivity.class);
        startActivity(intent);
    }
}
