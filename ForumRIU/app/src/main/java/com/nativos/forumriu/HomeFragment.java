package com.nativos.forumriu;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.name;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private ListView listViewDebate;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView =inflater.inflate(R.layout.fragment_home, container, false);
        listViewDebate=(ListView) rootView.findViewById(R.id.listViewDebate);

        new JsonTask().execute("http://debatesapp.azurewebsites.net/podiumwebapp/ws/debate/activedebates");
        // Inflate the layout for this fragment

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

                // JSONArray parentArray = parentObject.getJSONArray("user");

                //JSONObject parentObject = new JSONObject(finalJson);
//                UserModel userModel = new UserModel();
//                userModel.setName(parentObject.getJSONObject("user").getString("name"));
//                userModel.setLastname(parentObject.getJSONObject("user").getString("lastName"));

                List<DebateModel> debateModelList = new ArrayList<>();

                for (int i = 0; i < parentArray.length(); i++) {
                    JSONObject finalObject = parentArray.getJSONObject(i);
                    DebateModel debateModel = new DebateModel();
                    debateModel.setName(finalObject.getString("name"));

                    debateModelList.add(debateModel);
                }


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

            TextView tvDebateName;

            tvDebateName= (TextView) convertView.findViewById(R.id.textViewDebateName);

            tvDebateName.setText(debateModelList.get(position).getName());
            return convertView;
        }
    }

}
