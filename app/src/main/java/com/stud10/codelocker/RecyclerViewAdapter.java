package com.stud10.codelocker;

import android.content.Context;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{
    private static final String TAG = "RecyclerViewAdapter";
    private ArrayList<String> appList = new ArrayList<>();
    private ArrayList<String> usernameList = new ArrayList<>();
    private ArrayList<String> pwdList = new ArrayList<>();
    private Context context;

    public RecyclerViewAdapter(ArrayList<String> appList, ArrayList<String> usernameList, ArrayList<String> pwdList, Context context) {
        this.appList = appList;
        this.usernameList = usernameList;
        this.pwdList = pwdList;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView appName;
        TextView username;
        RelativeLayout platform_row;
        public ViewHolder(View itemView){
            super(itemView);
            platform_row = itemView.findViewById(R.id.platform_row);
            appName = itemView.findViewById(R.id.appname);
            username = itemView.findViewById(R.id.username);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.platform_row, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        holder.appName.setText(appList.get(position));
        holder.username.setText(usernameList.get(position));
        holder.platform_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Clicked on: " + appList.get(position));

                Toast.makeText(context, pwdList.get(position), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return appList.size();
    }

}