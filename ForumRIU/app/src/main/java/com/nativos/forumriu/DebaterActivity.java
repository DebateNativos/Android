package com.nativos.forumriu;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.annotation.StringRes;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nativos.forumriu.models.UserModel;

import static android.R.attr.id;
import static android.R.string.no;
import static com.nativos.forumriu.R.drawable.user;

public class DebaterActivity extends AppCompatActivity implements View.OnClickListener{

    NotificationCompat.Builder notification;
    Button btnNotification ;
    private int warning= 0;
    ProgressBar mProgressBar, mProgressBar1;
    private Button buttonStartTime, buttonStopTime;
    private EditText edtTimerValue;
    private TextView textViewShowTime;
    private CountDownTimer countDownTimer;
    private long totalTimeCountInMilliseconds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debater);

        notifications();

        buttonStartTime = (Button) findViewById(R.id.button_timerview_start);
        buttonStopTime = (Button) findViewById(R.id.button_timerview_stop);

        textViewShowTime = (TextView)
                findViewById(R.id.textView_timerview_time);
        edtTimerValue = (EditText) findViewById(R.id.textview_timerview_back);

        mProgressBar = (ProgressBar) findViewById(R.id.progressbar_timerview);
        mProgressBar1 = (ProgressBar) findViewById(R.id.progressbar1_timerview);

        buttonStartTime.setOnClickListener(this);
        buttonStopTime.setOnClickListener(this);

    }

    public void notifications(){

        btnNotification=(Button) findViewById(R.id.buttonWarning);
        btnNotification.setText(getAllWarning(warning));
        notification = new NotificationCompat.Builder(this);
        notification.setAutoCancel(true);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.button_timerview_start) {

            setTimer();

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
            edtTimerValue.setVisibility(View.VISIBLE);
            buttonStartTime.setVisibility(View.VISIBLE);
            buttonStopTime.setVisibility(View.INVISIBLE);
        }

    }

    private void setTimer(){
        int time = 0;
        if (!edtTimerValue.getText().toString().equals("")) {
            time = Integer.parseInt(edtTimerValue.getText().toString());
        } else
            Toast.makeText(DebaterActivity.this, "Digite los segundos...",
                    Toast.LENGTH_LONG).show();
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
        warning=Integer.parseInt(currentWarning);
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
