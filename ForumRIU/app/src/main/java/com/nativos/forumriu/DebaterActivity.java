package com.nativos.forumriu;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.annotation.StringRes;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import static android.R.attr.id;

public class DebaterActivity extends AppCompatActivity {
    NotificationCompat.Builder notification;
    Button btnNotification ;
    private int warning= 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debater);

        btnNotification=(Button) findViewById(R.id.buttonWarning);

        btnNotification.setText(getAllWarning(warning));

        notification = new NotificationCompat.Builder(this);

        notification.setAutoCancel(true);
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
