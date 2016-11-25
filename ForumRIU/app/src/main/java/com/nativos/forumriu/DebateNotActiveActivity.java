package com.nativos.forumriu;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.nativos.forumriu.models.DebateModel;

public class DebateNotActiveActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_debate_not_active);


        getDebateName();
    }


    public void getDebateName() {

        DebateModel debateModel = getIntent().getParcelableExtra("debateModel");

        String getName = debateModel.getName();
        final TextView finalText = (TextView) findViewById(R.id.textViewDebateNameNotActive);
        finalText.setText(getName);
    }
}
