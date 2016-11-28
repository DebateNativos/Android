package com.nativos.forumriu;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nativos.forumriu.models.DebateModel;
import com.nativos.forumriu.models.PlayerModel;
import com.nativos.forumriu.models.SectionModel;


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

import static android.R.attr.id;
import static android.media.CamcorderProfile.get;


public class DebaterActivity extends AppCompatActivity implements View.OnClickListener{

    NotificationCompat.Builder notification;
    Button btnNotification ;
    private int warning= 0;
    ProgressBar mProgressBar, mProgressBar1;
    private Button buttonStartTime, buttonStopTime, buttonWarning;

    private TextView textViewShowTime, tvSectionName,tvSectionMinutes;
    private CountDownTimer countDownTimer;
    private long totalTimeCountInMilliseconds;
    PlayerModel playerModel;
    SectionModel sectionModel ;
    private ListView listViewSection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debater);
        DebateModel debateModel = getIntent().getParcelableExtra("debateModel");
        getSupportActionBar().setTitle(debateModel.getName());
        playerModel = getIntent().getParcelableExtra("playerModel");
        listViewSection = (ListView) findViewById(R.id.listViewSections);
        notifications();

        buttonStartTime = (Button) findViewById(R.id.button_timerview_start);
        buttonStopTime = (Button) findViewById(R.id.button_timerview_stop);

        textViewShowTime = (TextView)
                findViewById(R.id.textView_timerview_time);


        mProgressBar = (ProgressBar) findViewById(R.id.progressbar_timerview);
        mProgressBar1 = (ProgressBar) findViewById(R.id.progressbar1_timerview);

        buttonStartTime.setOnClickListener(this);
        buttonStopTime.setOnClickListener(this);

        new JsonTask().execute("http://debatesapp.azurewebsites.net/podiumwebapp/ws/debate/getsections?id="+debateModel.getId());


        buttonWarning= (Button) findViewById(R.id.buttonWarningDebater);
        buttonWarning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationClick(v);
            }
        });
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

                    sectionModel= new SectionModel();
                    sectionModel.setMinutes(Integer.parseInt(finalObject.getString("minutesPerUser")));
                    sectionModel.setActiveSection(Boolean.parseBoolean(finalObject.getString("activeSection")));


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

            //tvSectionName.setText(sectionModelList.get(position).getName());
            tvSectionName.setText("Sección ");
            tvSectionMinutes.setText("  Tiempo : " + sectionModelList.get(position).getMinutes()+" minutos");
            boolean active = sectionModelList.get(position).getActiveSection();
                    if(active){
                        convertView.setBackgroundColor(Color.GREEN);
                        setTimer(sectionModelList.get(position).getMinutes());

                    }
            else {
                        convertView.setBackgroundColor(Color.TRANSPARENT);
                    }
            return convertView;
        }

    }


    public void notifications(){

        btnNotification=(Button) findViewById(R.id.buttonWarningDebater);
        btnNotification.setText(String.valueOf(playerModel.getWarnings()));
        notification = new NotificationCompat.Builder(this);
        notification.setAutoCancel(true);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.button_timerview_start) {


            buttonStartTime.setVisibility(View.INVISIBLE);
            buttonStopTime.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.INVISIBLE);

            startTimer();
            mProgressBar1.setVisibility(View.VISIBLE);


        } else if (v.getId() == R.id.button_timerview_stop) {
            countDownTimer.cancel();
            countDownTimer.onFinish();
            mProgressBar1.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);

            buttonStartTime.setVisibility(View.VISIBLE);
            buttonStopTime.setVisibility(View.INVISIBLE);
        }

    }

    private void setTimer(int time){

         time = time*60;

        totalTimeCountInMilliseconds =  time * 1000;
        mProgressBar1.setMax( time * 1000);
    }
    private void startTimer() {
        countDownTimer = new CountDownTimer(totalTimeCountInMilliseconds, 1) {
            @Override
            public void onTick(long leftTimeInMilliseconds) {
                long seconds = leftTimeInMilliseconds / 1000;
                mProgressBar1.setProgress((int) (leftTimeInMilliseconds));

                textViewShowTime.setText(String.format("%02d", seconds / 60)
                        + ":" + String.format("%02d", seconds % 60));
            }

            @Override
            public void onFinish() {
                textViewShowTime.setText("00:00");
                textViewShowTime.setVisibility(View.VISIBLE);
                buttonStartTime.setVisibility(View.VISIBLE);
                buttonStopTime.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.VISIBLE);
                mProgressBar1.setVisibility(View.GONE);

            }
        }.start();
    }

    public String getAllWarning(int num){

        Bundle WarningData= getIntent().getExtras();
        if(WarningData==null){
            return "0";
        }

        String getWarnings= WarningData.getString("Warnings");

        return getWarnings;
    }


    public void NotificationClick(View view){
        notification.setSmallIcon(R.drawable.amarilla);
        notification.setTicker("Recibiste una amonestación");
        notification.setWhen(System.currentTimeMillis());
        notification.setContentTitle("Notificación de amonestación");
        notification.setContentText("Has sido amonestado por el moderador");

        String currentWarning= btnNotification.getText().toString();
        warning= Integer.parseInt(currentWarning);
        warning++;
        notification.setVibrate(new long[]{ 1000, 1000});
        if(warning>=3) {
            btnNotification.setText((String.valueOf(warning)));
            Toast.makeText(DebaterActivity.this, "Tercera amonestación, has sido expulsado del debate", Toast.LENGTH_LONG).show();
            NotificationManager nm=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            nm.notify(id,notification.build());
            goToSignIn();
        }
        else{
        Toast.makeText(DebaterActivity.this, "Has sido amonestado", Toast.LENGTH_SHORT).show();
        btnNotification.setText((String.valueOf(warning)));

        Intent intent = new Intent(this,DebaterActivity.class);
            String warningText = btnNotification.getText().toString();
            intent.putExtra("Warnings", warningText);
        PendingIntent pendingIntent=  PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pendingIntent);
        NotificationManager nm=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(id,notification.build());
    }

    }

    public void goToSignIn() {
        Intent intent = new Intent(getBaseContext(), SignIn.class);
        startActivity(intent);
        
    }
}
