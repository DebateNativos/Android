package com.nativos.forumriu;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.nativos.forumriu.models.CourseModel;
import com.nativos.forumriu.models.UserModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.R.id.list;
import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

public class CourseInsertedActivity extends Activity {

    private TextView tv_courseName, tv_courseSchedule, tv_courseTeacher, tv_courseClassroom;
    private Button btnBackToHome;
    private CourseModel courseModel = new CourseModel();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_course_inserted);

        new JsonTask().execute("http://debatesapp.azurewebsites.net/podiumwebapp/ws/course/getcoursebycode?code="+getCourseCode());

        btnBackToHome= (Button) findViewById(R.id.buttonGotoHome);

        btnBackToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                goToHome();
            }
        });
    }

    public class JsonTask extends AsyncTask<String,String, CourseModel> {
        @Override
        protected CourseModel doInBackground(String... params) {
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
                String finalJson = buffer.toString();

                JSONObject parentObject = new JSONObject(finalJson);

                JSONObject finalObject =  parentObject.getJSONObject("course");

                courseModel.setCourseName(finalObject.getString("name"));
                courseModel.setCourseId(finalObject.getInt("idCourse"));
                courseModel.setCourseSchedule(finalObject.getString("schedule"));
                courseModel.setCourseTeacher(finalObject.getString("professor"));
                courseModel.setCourseClassroom(finalObject.getString("classroom"));

                return courseModel;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if(connection !=null){
                    connection.disconnect();}
                try {
                    if(reader !=null){
                        reader.close();}
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(CourseModel result) {
            super.onPostExecute(result);

            tv_courseName= (TextView) findViewById(R.id.textViewCourseName);
            tv_courseSchedule= (TextView) findViewById(R.id.textViewCourseSchedule);
            tv_courseClassroom= (TextView) findViewById(R.id.textViewCourseClassroom);
            tv_courseTeacher= (TextView) findViewById(R.id.textViewCourseTeacher);

            tv_courseName.setText("Curso: "+"\n"+result.getCourseName());
            tv_courseSchedule.setText("Horario: "+"\n"+result.getCourseSchedule());
            tv_courseClassroom.setText("Clase: "+"\n"+result.getCourseClassroom());
            tv_courseTeacher.setText("Profesor: "+"\n"+result.getCourseTeacher());

        }
    }

    public void goToHome() {
        Intent intent = new Intent(getBaseContext(), MainActivity.class);

        UserModel userModel = getIntent().getParcelableExtra("userModel");
        Bundle mBundle = new Bundle();
        mBundle.putParcelable("userModel", userModel);
        intent.putExtras(mBundle);

        startActivity(intent);
    }

    public String getCourseCode() {

        Intent intent = getIntent();
        String code = intent.getStringExtra("CourseCode");
        return code;
    }
    public void onBackPressed() {
        //do nothing
    }

}
