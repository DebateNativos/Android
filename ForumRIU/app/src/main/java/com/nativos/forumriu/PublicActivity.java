package com.nativos.forumriu;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nativos.forumriu.models.DebateModel;
import com.nativos.forumriu.models.SectionModel;
import com.nativos.forumriu.models.UserModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PublicActivity extends AppCompatActivity {

    private DebateModel debateModel;
    private SectionModel sectionModel;
    private UserModel userModel;
    private ListView listViewSection;
    private TextView  tvSectionName, tvSectionMinutes,textViewShowTime;
    private ImageButton imgButtonQuestions;

    private SectionModel currentSectionModel = new SectionModel();
    private ProgressBar progressBarStartPublic, progressBarRunningPublic;
    private CountDownTimer countDownTimer;
    private long totalTimeCountInMilliseconds;
    private int minutes =0;
    private int sectionNumber =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        debateModel = getIntent().getParcelableExtra("debateModel");
        userModel = getIntent().getParcelableExtra("userModel");
        getSupportActionBar().setTitle(debateModel.getName()+"  (Público)");
        setContentView(R.layout.activity_public);
        listViewSection = (ListView) findViewById(R.id.listViewSectionsPublic);

        textViewShowTime = (TextView)findViewById(R.id.textView_timerview_timePublic);

        progressBarStartPublic = (ProgressBar) findViewById(R.id.progressbar_timerviewStartPublic);
        progressBarRunningPublic = (ProgressBar) findViewById(R.id.progressbar1_timerviewRunningPublic);

        startRepeatingTaskCallWebService();

    }

    public class JsonTask extends AsyncTask<String, String, List<SectionModel>> {
        @Override
        protected List<SectionModel> doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();

                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                String finalJson = buffer.toString();
                JSONArray parentArray = new JSONArray(finalJson);

                List<SectionModel> sectionModelList = new ArrayList<>();

                for (int i = 0; i < parentArray.length(); i++) {
                    JSONObject finalObject = parentArray.getJSONObject(i);

                    sectionModel = new SectionModel();
                    sectionModel.setMinutes(Integer.parseInt(finalObject.getString("minutesPerUser")));
                    sectionModel.setActiveSection(Boolean.parseBoolean(finalObject.getString("activeSection")));
                    sectionModel.setName(finalObject.getString("name"));
                    sectionModel.setSectionNumber(Integer.parseInt(finalObject.getString("sectionNUmber")));

                    sectionModelList.add(sectionModel);
                }


                return sectionModelList;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
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
        protected void onPostExecute(List<SectionModel> result) {
            super.onPostExecute(result);

            SectionAdapter adapter = new SectionAdapter(getApplicationContext(), R.layout.row_section, result);
            listViewSection.setAdapter(adapter);
        }

    }

    public class SectionAdapter extends ArrayAdapter {

        public List<SectionModel> sectionModelList;
        private int resource;
        private LayoutInflater inflater;

        public SectionAdapter(Context context, int resource, List<SectionModel> objects) {
            super(context, resource, objects);
            sectionModelList = objects;
            this.resource = resource;
            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = inflater.inflate(resource, null);
            }

            tvSectionName = (TextView) convertView.findViewById(R.id.textViewSectionName);
            tvSectionMinutes = (TextView) convertView.findViewById(R.id.textViewSectionMinutes);

            tvSectionName.setText(" " +sectionModelList.get(position).getName());
            tvSectionMinutes.setText("  "+sectionModelList.get(position).getMinutes() + " minutos");
            boolean active = sectionModelList.get(position).getActiveSection();
            if (active) {

                tvSectionName.setTextColor(Color.WHITE);
                tvSectionMinutes.setTextColor(Color.WHITE);
                tvSectionName.setText(" " +sectionModelList.get(position).getName());
                tvSectionMinutes.setText("  "+sectionModelList.get(position).getMinutes() + " minutos ");
                convertView.setBackgroundResource(R.color.colorVerde);

                imgButtonQuestions = (ImageButton) findViewById(R.id.imageButtonQuestionsPublic);
                if(sectionModelList.get(position).getSectionNumber()>3){

                    imgButtonQuestions.setVisibility(View.INVISIBLE);
                }
                else{
                    imgButtonQuestions.setVisibility(View.VISIBLE);
                }


                sectionNumber = sectionModelList.get(position).getSectionNumber();
                minutes = sectionModelList.get(position).getMinutes();

                if (sectionNumber != currentSectionModel.getSectionNumber()) {

                    currentSectionModel.setSectionNumber(sectionNumber);
                    setTimer(minutes);
                    startTimer();

                }


            } else {
                tvSectionName.setTextColor(Color.BLACK);
                tvSectionMinutes.setTextColor(Color.BLACK);
                convertView.setBackgroundColor(Color.LTGRAY);
            }



            return convertView;
        }

    }

    private void setTimer(int time) {

        time = time * 60;

        totalTimeCountInMilliseconds = time * 1000;
        progressBarRunningPublic.setMax(time * 1000);
    }

    public void startTimer() {
        countDownTimer = new CountDownTimer(totalTimeCountInMilliseconds, 1) {
            @Override
            public void onTick(long leftTimeInMilliseconds) {
                long seconds = leftTimeInMilliseconds / 1000;
                progressBarRunningPublic.setProgress((int) (leftTimeInMilliseconds));

                textViewShowTime.setText(String.format("%02d", seconds / 60)
                        + ":" + String.format("%02d", seconds % 60));

                progressBarStartPublic.setVisibility(View.INVISIBLE);
                progressBarRunningPublic.setVisibility(View.VISIBLE);

            }

            @Override
            public void onFinish() {
                textViewShowTime.setText("00:00");
                textViewShowTime.setVisibility(View.VISIBLE);
                progressBarStartPublic.setVisibility(View.VISIBLE);
                progressBarRunningPublic.setVisibility(View.GONE);
                return;

            }
        }.start();
    }




    private final static int INTERVAL = 1000 * 1; //1 sec
    Handler mHandler = new Handler();

    Runnable mHandlerTask = new Runnable() {
        @Override
        public void run() {
            new JsonTask().execute("http://debatesapp.azurewebsites.net/podiumwebapp/ws/debate/getsections?id=" + debateModel.getId());
           // Log.d("myTag", "This is my message");

            mHandler.postDelayed(mHandlerTask, INTERVAL);
        }
    };

    void startRepeatingTaskCallWebService() {
        mHandlerTask.run();
    }

    void stopRepeatingTaskCallWebService() {
        mHandler.removeCallbacks(mHandlerTask);
    }

    @Override
    public void onBackPressed() {
        //stopRepeatingTaskGetSections();

        createExitDialog();

    }

    public void goToSendQuestion(View view) {
        Intent intent = new Intent(getBaseContext(), QuestionActivity.class);
        startActivity(intent);
    }

    private void createExitDialog(){

        AlertDialog.Builder alertDialog= new AlertDialog.Builder(this);
        alertDialog.setMessage("¿Está seguro que desea salir del debate?");
        alertDialog.setCancelable(false);
        alertDialog.setIcon(R.drawable.ic_logout_ic);
        alertDialog.setTitle("Salir del debate");

        alertDialog.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                userModel = getIntent().getParcelableExtra("userModel");
                Bundle mBundle = new Bundle();
                mBundle.putParcelable("userModel", userModel);
                intent.putExtras(mBundle);

                startActivity(intent);
            }

        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog.create().show();
}

}
