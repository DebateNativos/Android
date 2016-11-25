package com.nativos.forumriu;


import android.content.Context;
import android.content.Intent;
import android.graphics.Movie;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nativos.forumriu.models.DebateModel;
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

import static android.R.attr.name;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private ListView listViewDebate;
    TextView tvDebateName;
    ImageView imageIcon;
    TextView tvDebateDate;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       final View rootView =inflater.inflate(R.layout.fragment_home, container, false);
        listViewDebate=(ListView) rootView.findViewById(R.id.listViewDebate);

        new JsonTask().execute("http://debatesapp.azurewebsites.net/podiumwebapp/ws/debate/getactualdebates");


        listViewDebate.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DebateModel debateModel ;
                debateModel= (DebateModel) listViewDebate.getItemAtPosition(position);

              //  String name = String.valueOf(listViewDebate.getItemAtPosition(position));

                tvDebateDate= (TextView) rootView.findViewById(R.id.textViewDebateDate);

                if(debateModel.getActive()){

                    Intent intent = new Intent(getActivity().getBaseContext(), InsertCodeActivity.class);
                        Bundle mBundle = new Bundle();
                        mBundle.putParcelable("debateModel", debateModel);
                        intent.putExtras(mBundle);

                        startActivity(intent);
                }

                else{
                    Intent intent = new Intent(getActivity().getBaseContext(), DebateNotActiveActivity.class);
                        Bundle mBundle = new Bundle();
                        mBundle.putParcelable("debateModel", debateModel);
                        intent.putExtras(mBundle);

                        startActivity(intent);
                }


//                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
//                Date now = new Date(System.currentTimeMillis());
//
//                try {
//                    Date date= sdf.parse(debateModel.getDate());
//                    int result = now.compareTo(date);
//
//                    if (result==0){//iguales que hoy
//                        Intent intent = new Intent(getActivity().getBaseContext(), InsertCodeActivity.class);
//                        Bundle mBundle = new Bundle();
//                        mBundle.putParcelable("debateModel", debateModel);
//                        intent.putExtras(mBundle);
//
//                        startActivity(intent);
//
//                    }
//                    else{
//                        Intent intent = new Intent(getActivity().getBaseContext(), DebateNotActiveActivity.class);
//                        Bundle mBundle = new Bundle();
//                        mBundle.putParcelable("debateModel", debateModel);
//                        intent.putExtras(mBundle);
//
//                        startActivity(intent);
//
//                    }
//
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }



            }
        });

        return rootView;

    }

    public class JsonTask extends AsyncTask<String,String, List<DebateModel>> {
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

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(Long.parseLong(finalObject.getString("startingDate")));
                    Date date = calendar.getTime();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

                    debateModel.setDate(sdf.format(date));
                    //debateModel.setDate(finalObject.getString("startingDate"));

                    Date now = new Date(System.currentTimeMillis());
                    try {
                        Date dated = sdf.parse(debateModel.getDate());
                        int result = now.compareTo(dated);

                        if (result<=0){//mayores que hoy
                            debateModelList.add(debateModel);

                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
                        Collections.sort(debateModelList, new Comparator<DebateModel>() {
                            public int compare(DebateModel m1, DebateModel m2) {
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                                try {
                                    Date date1= sdf.parse(m1.getDate());
                                    Date date2=sdf.parse(m2.getDate());
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
        protected void onPostExecute(List<DebateModel> result) {
            super.onPostExecute(result);

            DebateAdapter adapter = new DebateAdapter(getActivity().getApplicationContext(), R.layout.row, result);
            listViewDebate.setAdapter(adapter);

        }

}
    public class DebateAdapter extends ArrayAdapter{

        public  List<DebateModel> debateModelList;
        private int resource;
        private LayoutInflater inflater;

        public DebateAdapter(Context context, int resource, List<DebateModel> objects) {
            super(context, resource, objects);
            debateModelList = objects;
            this.resource=resource;
            inflater= (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView == null){
                convertView = inflater.inflate(resource, null);
            }

            tvDebateDate= (TextView) convertView.findViewById(R.id.textViewDebateDate);
            tvDebateName= (TextView) convertView.findViewById(R.id.textViewDebateName);

            tvDebateName.setText(debateModelList.get(position).getName());
            tvDebateDate.setText("Fecha del debate: "+debateModelList.get(position).getDate());

            imageIcon=(ImageView) convertView.findViewById(R.id.imageViewDebate_ic);

            return convertView;
        }
    }

    public void goToInsertCode() {
        Intent intent = new Intent(getActivity().getBaseContext(), InsertCodeActivity.class);
        startActivity(intent);
    }

}
