package com.stud10.codelocker;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class CredentialModal extends DialogFragment {
    private static String TAG = "CredentialModal";
    private EditText username, password;
    private TextView cancel, add;
    private String stringUsername, stringPassword;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.add_credentials_modal, container, false);
        cancel = view.findViewById(R.id.cancel);
        add = view.findViewById(R.id.add);
        username = view.findViewById(R.id.new_username);
        password = view.findViewById(R.id.new_password);

        cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Log.d(TAG, "onClick: closing dialog");
                getDialog().dismiss();
            }
        });

        add.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Log.d(TAG, "onClick: opening dialog");
                stringUsername = username.getText().toString();
                stringPassword = password.getText().toString();

                if(!stringPassword.equals("") && !stringUsername.equals("")){
                    ((MainActivity)getActivity()).newCredentialUsername.setText(stringUsername);
                    ((MainActivity)getActivity()).getNewCredentialPassword.setText(stringPassword);
                }
                getDialog().dismiss();
            }
        });

        return view;
    }
}
