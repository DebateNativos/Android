package com.nativos.forumriu;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.nativos.forumriu.models.DebateModel;
import com.nativos.forumriu.models.UserModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static com.nativos.forumriu.R.drawable.user;

public class QuestionActivity extends AppCompatActivity {
    private EditText et_question;
    private ImageButton buttonSendQuestion;
    private DebateModel debateModel;
    private UserModel userModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Formular pregunta");
        setContentView(R.layout.activity_question);

        debateModel = getIntent().getParcelableExtra("debateModel");
        userModel = getIntent().getParcelableExtra("userModel");

        et_question = (EditText) findViewById(R.id.QuestionEditText);
        buttonSendQuestion = (ImageButton) findViewById(R.id.sendQuestionButton);


        buttonSendQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String questionText = et_question.getText().toString().trim();
                SendQuestion(questionText);
            }
        });

    }
    public void SendQuestion(String question) {

            if (question.contains("  ")) {
                Toast.makeText(QuestionActivity.this, "No se permite más de dos espacios entre palabras", Toast.LENGTH_SHORT).show();
            } else if (question.isEmpty()) {
                Toast.makeText(QuestionActivity.this, "Favor digitar una pregunta", Toast.LENGTH_SHORT).show();
            } else {

                question = question.replace(" ", "%20");
                new JsonTaskSendQuestion().execute("http://debatesapp.azurewebsites.net/podiumwebapp/ws/question/pushquestion?debate="+debateModel.getId()+"&email="+userModel.getEmail()+"&text="+question);
                buttonSendQuestion.setEnabled(false);
                Toast.makeText(QuestionActivity.this, "Enviando...", Toast.LENGTH_LONG).show();

            }

    }

    public class JsonTaskSendQuestion extends AsyncTask<String, String, String> {
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

            if (status != null && status.equals("@questionSent")) {
                et_question.getText().clear();
                Toast.makeText(QuestionActivity.this, "Pregunta enviada", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(QuestionActivity.this, "No se pudo enviar la pregunta", Toast.LENGTH_SHORT).show();
            }

            buttonSendQuestion.setEnabled(true);
        }
    }

}
