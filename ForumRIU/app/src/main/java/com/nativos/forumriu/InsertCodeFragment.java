package com.nativos.forumriu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

import static android.R.attr.button;
import static com.nativos.forumriu.R.id.activity_course_inserted;
import static com.nativos.forumriu.R.id.listViewDebate;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InsertCodeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InsertCodeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InsertCodeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private EditText et_code;
    Button btnAcceptCode;
    String email, courserCode;


    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public InsertCodeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InsertCodeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InsertCodeFragment newInstance(String param1, String param2) {
        InsertCodeFragment fragment = new InsertCodeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_insert_code, container, false);

        UserModel userModel = getActivity().getIntent().getParcelableExtra("userModel");
        email = userModel.getEmail();

        et_code = (EditText) rootView.findViewById(R.id.editTextInsertCode);


        btnAcceptCode = (Button) rootView.findViewById(R.id.ButtonInsertCode);

        btnAcceptCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                courserCode = et_code.getText().toString().trim();
                new JsonTask().execute("http://debatesapp.azurewebsites.net/podiumwebapp/ws/course/registerusercourse?email=" + email + "&coursecode=" + courserCode);

            }
        });


        return rootView;
    }

    public class JsonTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
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
                JSONObject parentObject = new JSONObject(finalJson);
                String status = parentObject.getString("status");

                return status;

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
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result.equals("@validRegistration")) {
                Toast.makeText(getActivity(), "Registrado con éxito ", Toast.LENGTH_LONG).show();
                goToCourseInserted();
            } else {

                Toast.makeText(getActivity(), "Error en el registro, intente de nuevo", Toast.LENGTH_LONG).show();
            }


        }

    }

    public void goToCourseInserted() {
        Intent intent = new Intent(getActivity().getBaseContext(), CourseInsertedActivity.class);
        intent.putExtra("CourseCode", courserCode);

        UserModel userModel = getActivity().getIntent().getParcelableExtra("userModel");

        Bundle mBundle = new Bundle();
        mBundle.putParcelable("userModel", userModel);
        intent.putExtras(mBundle);

        startActivity(intent);

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
