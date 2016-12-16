package com.nativos.forumriu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nativos.forumriu.models.CommentModel;
import com.nativos.forumriu.models.CourseModel;
import com.nativos.forumriu.models.DebateModel;
import com.nativos.forumriu.models.PlayerModel;
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

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;


public class CommentsActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private ListView listViewComment;
    private TextView tv_user, tv_comment, tv_remainComment;
    private ImageView imageUser;
    private EditText et_comment;
    private ImageButton buttonSendComment;
    private DebateModel debateModel;
    private UserModel userModel;
    private CourseModel courseModel;
    private CourseModel currentCourseModel = new CourseModel();
    private CommentModel commentModel;
    private PlayerModel currentPlayerModel;
    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        debateModel = getIntent().getParcelableExtra("debateModel");
        currentPlayerModel = getIntent().getParcelableExtra("playerModel");
        getSupportActionBar().setTitle(" Comentarios Asesor-Debatiente");
        setContentView(R.layout.activity_comments);

        listViewComment = (ListView) findViewById(R.id.listViewComments);
        userModel = getIntent().getParcelableExtra("userModel");

        new JsonTaskGetCourse().execute("http://debatesapp.azurewebsites.net/podiumwebapp/ws/course/getusercourses?email=" + userModel.getEmail());
        new JsonTaskGetComments().execute("http://debatesapp.azurewebsites.net/podiumwebapp/ws/comment/getcomments?course=" + currentCourseModel.getCourseCode() + "&debate=" + debateModel.getId());

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeComment);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark);
        swipeRefreshLayout.setOnRefreshListener(this);

        startRepeatingTaskCallWebService();
        et_comment = (EditText) findViewById(R.id.CommentEditText);


        buttonSendComment = (ImageButton) findViewById(R.id.sendCommentButton);
        buttonSendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userComment = et_comment.getText().toString().trim();
                SendComment(userComment);

            }
        });
    }

    @Override
    public void onRefresh() {
        Toast.makeText(this, "Cargando...", Toast.LENGTH_SHORT).show();
        new JsonTaskGetComments().execute("http://debatesapp.azurewebsites.net/podiumwebapp/ws/comment/getcomments?course=" + currentCourseModel.getCourseCode() + "&debate=" + debateModel.getId());
    }


    public void SendComment(String comment) {

        if (comment.contains("  ")) {
            Toast.makeText(CommentsActivity.this, "No se permite más de dos espacios entre palabras", Toast.LENGTH_SHORT).show();
        } else if (comment.isEmpty()) {
            Toast.makeText(CommentsActivity.this, "Favor digitar un comentario", Toast.LENGTH_SHORT).show();
        } else {

            comment = comment.replace(" ", "%20");
            new JsonTaskSendComment().execute("http://debatesapp.azurewebsites.net/podiumwebapp/ws/comment/pushcomment?course=" + currentCourseModel.getCourseCode() + "&debate=" + debateModel.getId() + "&email=" + userModel.getEmail() + "&text=" + comment);
            buttonSendComment.setEnabled(false);
            Toast.makeText(CommentsActivity.this, "Enviando...", Toast.LENGTH_LONG).show();

        }

    }


    public class JsonTaskGetCourse extends AsyncTask<String, String, List<CourseModel>> {
        @Override
        protected List<CourseModel> doInBackground(String... params) {
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


                List<CourseModel> courseModelList = new ArrayList<>();

                for (int i = 0; i < parentArray.length(); i++) {
                    JSONObject finalObject = parentArray.getJSONObject(i);

                    JSONObject finalObjectCourse = finalObject.getJSONObject("course");
                    courseModel = new CourseModel();

                    courseModel.setCourseId(Integer.parseInt(finalObjectCourse.getString("idCourse")));
                    courseModel.setCourseName(finalObjectCourse.getString("name"));
                    courseModel.setCourseCode(finalObjectCourse.getString("courseCode"));

                    courseModelList.add(courseModel);
                }

                return courseModelList;

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
        protected void onPostExecute(List<CourseModel> result) {
            super.onPostExecute(result);


            for (CourseModel courseModel : result) {
                if (courseModel.getCourseCode().equals(currentPlayerModel.getTeam()) && (debateModel.getFirstcourse().equals(currentPlayerModel.getTeam()) || debateModel.getSecondCourse().equals(currentPlayerModel.getTeam()))) {
                    currentCourseModel = courseModel;
                }
            }


        }

    }


    public class JsonTaskGetComments extends AsyncTask<String, String, List<CommentModel>> {
        @Override
        protected List<CommentModel> doInBackground(String... params) {
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


                List<CommentModel> commentModelList = new ArrayList<>();

                for (int i = 0; i < parentArray.length(); i++) {
                    JSONObject finalObject = parentArray.getJSONObject(i);

                    JSONObject finalObjectUser = finalObject.getJSONObject("user");
                    commentModel = new CommentModel();

                    commentModel.setIdUser(Integer.parseInt(finalObjectUser.getString("idUsers")));
                    commentModel.setName(finalObjectUser.getString("name"));
                    commentModel.setLastname(finalObjectUser.getString("lastName"));
                    commentModel.setEmail(finalObjectUser.getString("email"));
                    commentModel.setComment(finalObject.getString("text"));

                    commentModelList.add(commentModel);
                }

                return commentModelList;

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
        protected void onPostExecute(List<CommentModel> result) {
            super.onPostExecute(result);


            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);

            }
            CommentAdapter adapter = new CommentAdapter(getApplicationContext(), R.layout.row_comment, result);
            listViewComment.setAdapter(adapter);

        }
    }


    public class CommentAdapter extends ArrayAdapter {

        public List<CommentModel> commentModelList;
        private int resource;
        private LayoutInflater inflater;

        public CommentAdapter(Context context, int resource, List<CommentModel> objects) {
            super(context, resource, objects);
            commentModelList = objects;
            this.resource = resource;
            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = inflater.inflate(resource, null);
            }

            tv_user = (TextView) convertView.findViewById(R.id.textViewUserNameComment);
            tv_comment = (TextView) convertView.findViewById(R.id.textViewCommentText);

            tv_user.setText(commentModelList.get(position).getName() + " " + commentModelList.get(position).getLastname());
            tv_comment.setText(commentModelList.get(position).getComment());

            imageUser = (ImageView) convertView.findViewById(R.id.imageViewUserComment);

            return convertView;
        }
    }


    public class JsonTaskSendComment extends AsyncTask<String, String, String> {
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

            if (status != null && status.equals("@sent")) {
                et_comment.getText().clear();

            } else {

                Toast.makeText(CommentsActivity.this, "No se pudo enviar el comentario", Toast.LENGTH_SHORT).show();
            }

            buttonSendComment.setEnabled(true);
        }
    }


    private final static int INTERVAL = 1000 * 2; //2 sec
    Handler mHandler = new Handler();

    Runnable mHandlerTask = new Runnable() {
        @Override
        public void run() {
            //Log.d("myTag", "This is my messageComments");
            //  new JsonTaskGetComments().execute("http://debatesapp.azurewebsites.net/podiumwebapp/ws/comment/getcomments?course=FLUF0&debate=1");
            new JsonTaskGetComments().execute("http://debatesapp.azurewebsites.net/podiumwebapp/ws/comment/getcomments?course=" + currentCourseModel.getCourseCode() + "&debate=" + debateModel.getId());
            mHandler.postDelayed(mHandlerTask, INTERVAL);
        }
    };

    void startRepeatingTaskCallWebService() {
        mHandlerTask.run();
    }

    void stopRepeatingTaskCallWebService() {
        mHandler.removeCallbacks(mHandlerTask);
    }


    public void onBackPressed() {
        stopRepeatingTaskCallWebService();

        super.onBackPressed();

    }

}
