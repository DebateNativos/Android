package com.nativos.forumriu;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nativos.forumriu.models.DebateModel;
import com.nativos.forumriu.models.PlayerModel;
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

import static android.R.attr.id;
import static android.graphics.Color.LTGRAY;


public class DebaterActivity extends AppCompatActivity {

    NotificationCompat.Builder notification;
    private Button btnNotification;
    private int warning = 0;
    private ProgressBar progressBarStartSectionDebater, progressBarRunningSectionDebater,progressBarStartDebater,progressBarRunningDebater ;
    private Button buttonWarning;

    private TextView textViewShowTimeSectionDebater,textViewShowTimeDebater, tvSectionName, tvSectionMinutes;
    private CountDownTimer countDownTimerSection,countDownTimer;
    private long totalTimeCountInMillisecondsSection,totalTimeCountInMilliseconds;

    private PlayerModel playerModel = new PlayerModel();
    private SectionModel sectionModel;
    private DebateModel debateModel;
    private UserModel userModel;
    private ListView listViewSection;
    private PlayerModel currentPlayerModel;
    private SectionModel currentSectionModel = new SectionModel();
    private int minutesSection = 0;
    private int sectionNumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debater);
        debateModel = getIntent().getParcelableExtra("debateModel");
        userModel = getIntent().getParcelableExtra("userModel");
        getSupportActionBar().setTitle(debateModel.getName() + "  (Debatiente)");

        listViewSection = (ListView) findViewById(R.id.listViewSections);


        textViewShowTimeDebater = (TextView) findViewById(R.id.textView_timerview_timeDebater);
        progressBarStartDebater = (ProgressBar) findViewById(R.id.progressbar_timerviewStartDebater);
        progressBarRunningDebater = (ProgressBar) findViewById(R.id.progressbar1_timerviewRunningDebater);

        textViewShowTimeSectionDebater = (TextView) findViewById(R.id.textView_timerview_timeSectionDebater);
        progressBarStartSectionDebater = (ProgressBar) findViewById(R.id.progressbar_timerviewStartSectionDebater);
        progressBarRunningSectionDebater = (ProgressBar) findViewById(R.id.progressbar1_timerviewRunningSectionDebater);


        startRepeatingTaskCallWebService();


    }

    public class JsonTaskSectionDebater extends AsyncTask<String, String, List<SectionModel>> {
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

            tvSectionName.setText(" " + sectionModelList.get(position).getName());
            tvSectionMinutes.setText("  " + sectionModelList.get(position).getMinutes() + " minutos");
            boolean active = sectionModelList.get(position).getActiveSection();

            if (active) {
                tvSectionName.setTextColor(Color.WHITE);
                tvSectionMinutes.setTextColor(Color.WHITE);
                tvSectionName.setText(" " + sectionModelList.get(position).getName());
                tvSectionMinutes.setText("  " + sectionModelList.get(position).getMinutes() + " minutos ");
                convertView.setBackgroundResource(R.color.colorVerde);

                sectionNumber = sectionModelList.get(position).getSectionNumber();
                minutesSection = sectionModelList.get(position).getMinutes();

                if (sectionNumber != currentSectionModel.getSectionNumber()) {

                    currentSectionModel.setSectionNumber(sectionNumber);
                    setTimerSection(minutesSection);
                    startTimerSection();

                }

            } else {
                tvSectionName.setTextColor(Color.BLACK);
                tvSectionMinutes.setTextColor(Color.BLACK);
                convertView.setBackgroundColor(LTGRAY);
            }

            return convertView;
        }

    }


    private void setTimerSection(int time) {

        time = time * 60;

        totalTimeCountInMillisecondsSection = time * 1000;
        progressBarRunningSectionDebater.setMax(time * 1000);
    }

    private void startTimerSection() {
        countDownTimerSection = new CountDownTimer(totalTimeCountInMillisecondsSection, 1) {
            @Override
            public void onTick(long leftTimeInMilliseconds) {
                long seconds = leftTimeInMilliseconds / 1000;
                progressBarRunningSectionDebater.setProgress((int) (leftTimeInMilliseconds));

                textViewShowTimeSectionDebater.setText(String.format("%02d", seconds / 60)
                        + ":" + String.format("%02d", seconds % 60));

                progressBarStartSectionDebater.setVisibility(View.INVISIBLE);
                progressBarRunningSectionDebater.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish() {
                textViewShowTimeSectionDebater.setText("00:00");
                textViewShowTimeSectionDebater.setVisibility(View.VISIBLE);
                progressBarStartSectionDebater.setVisibility(View.VISIBLE);
                progressBarRunningSectionDebater.setVisibility(View.GONE);
            }
        }.start();
    }

    public void notifications(PlayerModel playerModel) {

        btnNotification = (Button) findViewById(R.id.buttonWarningDebater);
        String text = btnNotification.getText().toString().trim();
        String current = String.valueOf(playerModel.getWarnings());
        notification = new NotificationCompat.Builder(this);
        notification.setAutoCancel(true);

        if (text != current && text != "" || text.equals("3")) {

            NotificationClick();
        }

        btnNotification.setText(String.valueOf(playerModel.getWarnings()));
    }


    public class JsonTaskGetRoleDebater extends AsyncTask<String, String, List<PlayerModel>> {
        @Override
        protected List<PlayerModel> doInBackground(String... params) {
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
                List<PlayerModel> playerModelList = new ArrayList<>();

                for (int i = 0; i < parentArray.length(); i++) {
                    JSONObject finalObject = parentArray.getJSONObject(i);
                    playerModel = new PlayerModel();

                    playerModel.setRole(Integer.parseInt(finalObject.getString("role")));
                    playerModel.setDebate(Integer.parseInt(finalObject.getString("debate")));
                    playerModel.setWarnings(Integer.parseInt(finalObject.getString("warning")));

                    playerModelList.add(playerModel);
                }

                return playerModelList;

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
        protected void onPostExecute(List<PlayerModel> result) {
            super.onPostExecute(result);

            //PlayerModel pm = new PlayerModel();

            for (PlayerModel playerModel : result) {
                if (playerModel.getDebate() == debateModel.getId()) {
                    currentPlayerModel = playerModel;
                }
            }
            notifications(currentPlayerModel);

        }

    }


    public void NotificationClick() {
        notification.setSmallIcon(R.drawable.amarilla);
        notification.setTicker("Recibiste una amonestación");
        notification.setWhen(System.currentTimeMillis());
        notification.setContentTitle("Notificación de amonestación");
        notification.setContentText("Has sido amonestado por el moderador");

        warning = currentPlayerModel.getWarnings();

        notification.setVibrate(new long[]{1000, 1000});
        if (warning >= 3) {

            Toast.makeText(DebaterActivity.this, "Tercera amonestación, has sido expulsado del debate", Toast.LENGTH_LONG).show();
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            nm.notify(id, notification.build());
            goToSignIn();
        } else {
            Toast.makeText(DebaterActivity.this, "Has sido amonestado", Toast.LENGTH_SHORT).show();

//            Intent intent = new Intent(this, DebaterActivity.class);
//
//            Bundle mBundle = new Bundle();
//            mBundle.putParcelable("userModel", userModel);
//            mBundle.putParcelable("debateModel", debateModel);
//            intent.putExtras(mBundle);
//
//            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//            notification.setContentIntent(pendingIntent);
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            nm.notify(id, notification.build());
        }

    }

    public void goToSignIn() {
        stopRepeatingTaskCallWebService();
        Intent intent = new Intent(getBaseContext(), SignIn.class);
        startActivity(intent);

    }

    private final static int INTERVAL = 1000 * 1; //1 sec
    Handler mHandler = new Handler();

    Runnable mHandlerTask = new Runnable() {
        @Override
        public void run() {
            new JsonTaskSectionDebater().execute("http://debatesapp.azurewebsites.net/podiumwebapp/ws/debate/getsections?id=" + debateModel.getId());
            // Log.d("myTag", "This is my message");
            new JsonTaskGetRoleDebater().execute("http://debatesapp.azurewebsites.net/podiumwebapp/ws/debate/confirmeddebates?email=" + userModel.getEmail());

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
        //stopRepeatingTaskCallWebService();

        createExitDialog();

    }

    private void createExitDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
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

    private void setTimer(int time) {

        time = time * 60;

        totalTimeCountInMilliseconds = time * 1000;
        progressBarRunningDebater.setMax(time * 1000);
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(totalTimeCountInMilliseconds, 1) {
            @Override
            public void onTick(long leftTimeInMilliseconds) {
                long seconds = leftTimeInMilliseconds / 1000;
                progressBarRunningDebater.setProgress((int) (leftTimeInMilliseconds));

                textViewShowTimeDebater.setText(String.format("%02d", seconds / 60)
                        + ":" + String.format("%02d", seconds % 60));

                progressBarStartDebater.setVisibility(View.INVISIBLE);
                progressBarRunningDebater.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish() {
                textViewShowTimeDebater.setText("00:00");
                textViewShowTimeDebater.setVisibility(View.VISIBLE);
                progressBarStartDebater.setVisibility(View.VISIBLE);
                progressBarRunningDebater.setVisibility(View.GONE);
            }
        }.start();
    }

}
