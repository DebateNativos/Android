package com.nativos.forumriu;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.nativos.forumriu.models.DebateModel;
import com.nativos.forumriu.models.UserModel;

import static android.R.attr.name;
import static android.R.attr.onClick;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private EditText  et_name,et_lastname,et_lastname2,et_Password,et_confirmPassword,et_phone, et_address;
    private Button btnUpdateProfile;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private OnFragmentInteractionListener mListener;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        // Inflate the layout for this fragment

        final View rootView =inflater.inflate(R.layout.fragment_profile, container, false);


        UserModel userModel = getActivity().getIntent().getParcelableExtra("userModel");

        et_name = (EditText) rootView.findViewById(R.id.editTextProfileName);
        et_name.setText(userModel.getName());

        et_lastname = (EditText) rootView.findViewById(R.id.editTextProfileLastName);
        et_lastname.setText(userModel.getLastname());

        et_lastname2 = (EditText) rootView.findViewById(R.id.editTextProfileLastName2);
        et_lastname2.setText(userModel.getLastname2());

        et_Password =(EditText) rootView.findViewById(R.id.editTextProfilePassword);
        et_confirmPassword =(EditText) rootView.findViewById(R.id.editTextProfileConfirmPassword);

        et_phone = (EditText) rootView.findViewById(R.id.editTextProfilePhone);
        et_phone.setText(userModel.getPhone());

        et_address = (EditText) rootView.findViewById(R.id.editTextProfileAddress);
        et_address.setText(userModel.getAddress());

        btnUpdateProfile=(Button) rootView.findViewById(R.id.StudentButton);

//        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (validatePassword(et_Password.getText().toString())) {
//                    et_Password.setError("Campo requerido");
//                    et_Password.requestFocus();
//                }
//
//                else if (validatePassword(et_confirmPassword.getText().toString())) {
//                    et_confirmPassword.setError("Campo requerido");
//                    et_confirmPassword.requestFocus();
//                }
//
//
//            }
//        });
        return rootView;

    }

    public boolean validatePassword(String password) {

        if (password.isEmpty()) {
            return true;
        } else
            return false;
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
