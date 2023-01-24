package com.example.autodo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.TaskViewHolder>{
    private Context context;
    private List<TaskData> taskData;

    public RecyclerAdapter(List<TaskData> taskData) {
        this.taskData = taskData;
    }
    public RecyclerAdapter(List<TaskData> taskData, Context context) {
        this.taskData = taskData;
        this.context = context;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item_card, parent, false);
        return new TaskViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context.getApplicationContext());
        holder.checkBox.setText(taskData.get(position).getName());
        holder.checkBox.setChecked(taskData.get(position).isStatus());
        String key = taskData.get(holder.getAdapterPosition()).getKey();

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(account.getId());
                databaseReference.child(key).removeValue();
                databaseReference.child(key).removeValue();

            }
        });

    }
    @Override
    public int getItemCount() {
        return taskData.size();
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {
        private CheckBox checkBox;
        private ImageButton deleteButton;
        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkBox2);
            deleteButton = (ImageButton) itemView.findViewById(R.id.imageButton2);
        }
    }
}
