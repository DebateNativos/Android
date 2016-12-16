package com.nativos.forumriu;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private ListView listViewDebate;
    private TextView tvDebateName;
    private ImageView imageIcon;
    private TextView tvDebateDate;
    private PlayerModel playerModel = new PlayerModel();
    private DebateModel debateModel;
    private UserModel userModel;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SectionModel sectionModel;
    private SectionModel currentSectionModel= new SectionModel();
    private List<SectionModel> currentSectionModelList = new ArrayList<>();


    public HomeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        listViewDebate = (ListView) rootView.findViewById(R.id.listViewDebate);

        new JsonTask().execute("http://debatesapp.azurewebsites.net/podiumwebapp/ws/debate/getdebates");
        userModel = getActivity().getIntent().getParcelableExtra("userModel");

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeDebates);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark);
        swipeRefreshLayout.setOnRefreshListener(this);

        listViewDebate.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setSelected(true);
                debateModel = (DebateModel) listViewDebate.getItemAtPosition(position);

                tvDebateDate = (TextView) rootView.findViewById(R.id.textViewDebateDate);

                if (debateModel.getActive() ) {
                    //&& currentDebate()
                    new JsonTaskSection().execute("http://debatesapp.azurewebsites.net/podiumwebapp/ws/debate/getsections?id=" + debateModel.getId());

                    if(!activeSection()) {
                        new JsonTaskGetRole().execute("http://debatesapp.azurewebsites.net/podiumwebapp/ws/debate/confirmeddebates?email=" + userModel.getEmail());
                    }
                    else{
                        createStayDialog();
                    }
                } else {

                    Intent intent = new Intent(getActivity().getBaseContext(), DebateNotActiveActivity.class);
                    Bundle mBundle = new Bundle();
                    mBundle.putParcelable("debateModel", debateModel);
                    intent.putExtras(mBundle);

                    startActivity(intent);

                }
            }
        });

        return rootView;

    }

    @Override
    public void onRefresh() {
        Toast.makeText(getActivity(), "Cargando...", Toast.LENGTH_SHORT).show();
        new JsonTask().execute("http://debatesapp.azurewebsites.net/podiumwebapp/ws/debate/getdebates");
    }

    public class JsonTaskGetRole extends AsyncTask<String, String, List<PlayerModel>> {
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
            PlayerModel pm = new PlayerModel();


            for (PlayerModel playerModel : result) {
                if (playerModel.getDebate() == debateModel.getId()) {
                    pm = playerModel;
                }
            }

            if(!activeSection()) {

            Intent intent;
            Bundle mBundle = new Bundle();
            mBundle.putParcelable("userModel", userModel);
            mBundle.putParcelable("debateModel", debateModel);
            mBundle.putParcelable("playerModel", pm);

            if (pm.getDebate() == debateModel.getId()) {

                if(pm.getWarnings()<3){
                switch (pm.getRole()) {
                    case 1:
                        intent = new Intent(getActivity().getBaseContext(), DebaterActivity.class);
                        intent.putExtras(mBundle);
                        //intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(intent);

                        break;

                    case 2:
                        intent = new Intent(getActivity().getBaseContext(), AdvisorActivity.class);
                        intent.putExtras(mBundle);
                       // intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(intent);
                        break;
                    case 3:
                        intent = new Intent(getActivity().getBaseContext(), ObserverActivity.class);
                        intent.putExtras(mBundle);
                        //intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(intent);
                        break;
                    case 4:
                        intent = new Intent(getActivity().getBaseContext(), PublicActivity.class);
                        intent.putExtras(mBundle);
                        //intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(intent);
                        break;
                    default:
                        intent = new Intent(getActivity().getBaseContext(), PublicActivity.class);
                        intent.putExtras(mBundle);
                       //intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(intent);
                        break;
                }
                }
                else{
                    Toast.makeText(getActivity(), "Te han expulsado del debate", Toast.LENGTH_SHORT).show();

                }

            } else {
                intent = new Intent(getActivity().getBaseContext(), PublicActivity.class);
                intent.putExtras(mBundle);
                //intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);

            }
            }
            else {

                createStayDialog();
            }

        }

    }


    public class JsonTask extends AsyncTask<String, String, List<DebateModel>> {
        @Override
        protected List<DebateModel> doInBackground(String... params) {
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

                List<DebateModel> debateModelList = new ArrayList<>();

                for (int i = 0; i < parentArray.length(); i++) {
                    JSONObject finalObject = parentArray.getJSONObject(i);
                    DebateModel debateModel = new DebateModel();
                    debateModel.setName(finalObject.getString("name"));
                    debateModel.setId(finalObject.getInt("idDebates"));
                    debateModel.setActive(Boolean.parseBoolean(finalObject.getString("isActive")));
                    debateModel.setDebateType(finalObject.getString("debateType"));
                    debateModel.setFirstcourse(finalObject.getString("course1"));
                    debateModel.setSecondCourse(finalObject.getString("course2"));

                    Calendar calendar = Calendar.getInstance();

                    calendar.setTimeInMillis(Long.parseLong(finalObject.getString("startingDate")));
                    calendar.add(Calendar.HOUR_OF_DAY, 6);//servidor tiene 6 horas adelantadas
                    Date date = calendar.getTime();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy  h:mm a");

                    debateModel.setDate(sdf.format(date));
                    ////debateModel.setDate(finalObject.getString("startingDate"));


                    Date now = new Date(System.currentTimeMillis() - (60 * 60000));

                    try {
                        Date dated = sdf.parse(debateModel.getDate());
                        int result = now.compareTo(dated);

                        if (result <= 0) {//mayores que hoy
                            debateModelList.add(debateModel);

                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    //debateModelList.add(debateModel);
                }


                Collections.sort(debateModelList, new Comparator<DebateModel>() {
                    public int compare(DebateModel m1, DebateModel m2) {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                        try {
                            Date date1 = sdf.parse(m1.getDate());
                            Date date2 = sdf.parse(m2.getDate());
                            return date1.compareTo(date2);

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        return m1.getDate().compareTo(m2.getDate());
                    }
                });


                return debateModelList;

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
        protected void onPostExecute(List<DebateModel> result) {
            super.onPostExecute(result);

            DebateAdapter adapter = new DebateAdapter(getActivity().getApplicationContext(), R.layout.row, result);

            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);

            }
            listViewDebate.setAdapter(adapter);

        }



    }

    public class DebateAdapter extends ArrayAdapter {

        public List<DebateModel> debateModelList;
        private int resource;
        private LayoutInflater inflater;

        public DebateAdapter(Context context, int resource, List<DebateModel> objects) {
            super(context, resource, objects);
            debateModelList = objects;
            this.resource = resource;
            inflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = inflater.inflate(resource, null);
            }

            tvDebateDate = (TextView) convertView.findViewById(R.id.textViewDebateDate);
            tvDebateName = (TextView) convertView.findViewById(R.id.textViewDebateName);

            tvDebateDate.setTextColor(Color.BLACK);
            tvDebateName.setTextColor(Color.BLACK);

            tvDebateName.setText(debateModelList.get(position).getName());
            tvDebateDate.setText("Fecha : " + debateModelList.get(position).getDate());


            imageIcon = (ImageView) convertView.findViewById(R.id.imageViewDebate_ic);

            return convertView;
        }
    }

    public class JsonTaskSection extends AsyncTask<String, String, List<SectionModel>> {
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

                currentSectionModelList = sectionModelList;
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

            for(SectionModel sm: result){
                if(sm.getActiveSection()){
                    currentSectionModel=sm;
                }
            }
        }

    }

    private boolean activeSection(){
        boolean active=false;
        for(SectionModel sm: currentSectionModelList){

            if(sm.getActiveSection()){
                active=true;
            }
        }

        return active;
    }


    public boolean currentDebate() {

        boolean status = false;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        Date now = new Date(System.currentTimeMillis()+ (60 * 60000));

        try {
            Date date = sdf.parse(debateModel.getDate());
            int result = now.compareTo(date);

            if (result >= 0) {//igual o menor que hoy
                status = true;

            } else {
                status = false;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return status;
    }

    private void createStayDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setMessage("No puede ingresar al debate. En este momento hay una sección del debate en progreso");
        alertDialog.setCancelable(false);
        alertDialog.setIcon(R.drawable.ic_logout_ic);
        alertDialog.setTitle("Intente más tarde");

        alertDialog.setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog.create().show();
    }

}
