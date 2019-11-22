package com.stud10.codelocker;

import android.content.Context;
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
    private EditText appname, username, password;
    private TextView cancel, add;
    private String stringAppname, stringUsername, stringPassword;

    public interface OnInputListener{
        void sendInput(String input, int idx);
    }

    public OnInputListener oil;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.add_credentials_modal, container, false);
        cancel = view.findViewById(R.id.cancel);
        add = view.findViewById(R.id.add);
        appname = view.findViewById(R.id.new_appname);
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

                stringAppname = appname.getText().toString();
                stringUsername = username.getText().toString();
                stringPassword = password.getText().toString();

//                if(!stringPassword.equals("") && !stringUsername.equals("")){
//                    ((MainActivity)getActivity()).newCredentialAppname.setText(stringAppname);
//                    ((MainActivity)getActivity()).newCredentialUsername.setText(stringUsername);
//                    ((MainActivity)getActivity()).newCredentialPassword.setText(stringPassword);
//                }

                oil.sendInput(stringAppname, 0);
                oil.sendInput(stringUsername, 1);
                oil.sendInput(stringPassword, 2);
                oil.sendInput("Add Credential to DB", 3);
                getDialog().dismiss();
            }
        });

        //update main page
        oil.sendInput("Refresh Contents", 4);

        return view;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);

        try{
            oil = (OnInputListener) getActivity();
        }catch(ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage());
        }
    }
}
