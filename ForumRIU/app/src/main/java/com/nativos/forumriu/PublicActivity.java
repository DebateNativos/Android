package com.nativos.forumriu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.nativos.forumriu.models.DebateModel;

public class PublicActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DebateModel debateModel = getIntent().getParcelableExtra("debateModel");
        getSupportActionBar().setTitle(debateModel.getName());
        setContentView(R.layout.activity_public);
    }

    public void goToSendQuestion(View view) {
        Intent intent = new Intent(getBaseContext(), QuestionActivity.class);
        startActivity(intent);
    }
}
