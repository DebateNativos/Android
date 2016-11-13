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
import android.widget.TextView;
import android.widget.Toast;

import static android.R.id.edit;

public class InsertCodeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_insert_code);

        getDebateName();
        validateCode();
    }

    public void validateCode() {

        Button btnInsertCode = (Button) findViewById(R.id.StudentButton);

        btnInsertCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = (EditText) findViewById(R.id.editTextDebateCode);
                String debateCode = editText.getText().toString().trim();
                if (debateCode.equals("debatiente")) {
                    goToDebaterActivity();
                } else
                    Toast.makeText(InsertCodeActivity.this, "Código Incorrecto ", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getDebateName() {

        Bundle debateData = getIntent().getExtras();
        if (debateData == null) {
            return;
        }

        String getName = debateData.getString("DebateName");
        final TextView finalText = (TextView) findViewById(R.id.textViewDebateName);
        finalText.setText(getName);
    }

    public void goToDebaterActivity() {
        Intent intent = new Intent(getBaseContext(), DebaterActivity.class);
        startActivity(intent);
    }

    public void goToPublicActivity(View view) {
        Intent intent = new Intent(getBaseContext(), PublicActivity.class);
        startActivity(intent);
    }
}
