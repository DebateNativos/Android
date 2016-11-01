package com.nativos.forumriu;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class InsertCodeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_insert_code);

        Bundle debateData= getIntent().getExtras();
        if(debateData==null){
            return;
        }

        String getName= debateData.getString("DebateName");
        final TextView finalText=(TextView)findViewById(R.id.textViewDebateName);
        finalText.setText(getName);
    }
}
