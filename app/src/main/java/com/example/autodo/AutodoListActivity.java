package com.example.autodo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AutodoListActivity extends AppCompatActivity {

    private TextView textViewProfile;
    private ImageView imageViewProfile;
    DrawerLayout drawerLayout;
    boolean nightMode;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;
    private List<TaskData> dataList;
    GoogleSignInOptions googleSignInOptions;
    static GoogleSignInClient googleSignInClient;
    private DatabaseReference databaseReference;
    ValueEventListener eventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autodo_list);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_activity);
        textViewProfile = (TextView) findViewById(R.id.textView6);
        imageViewProfile = (ImageView) findViewById(R.id.imageView5);
        recyclerView = (RecyclerView) findViewById(R.id.autodoListRecycler);
        dataList = new ArrayList<>();

        sharedPreferences = getSharedPreferences("THEME_MODE", Context.MODE_PRIVATE);
        nightMode = sharedPreferences.getBoolean("night", false);

        recyclerView.setLayoutManager(new LinearLayoutManager(AutodoListActivity.this));

        adapter = new RecyclerAdapter(dataList, AutodoListActivity.this);
        recyclerView.setAdapter(adapter);

        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        if (account != null) {
            populateData(account);
        }
        databaseReference = FirebaseDatabase.getInstance().getReference(account.getId());
        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();
                for (DataSnapshot itemSnap: snapshot.getChildren()) {
                    TaskData data = itemSnap.getValue(TaskData.class);
                    data.setKey(itemSnap.getKey());
                    dataList.add(data);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void populateData(GoogleSignInAccount account) {
        textViewProfile.setText(account.getDisplayName());
        Picasso.get().load(account.getPhotoUrl()).placeholder(R.drawable.logo_wouttext).into(imageViewProfile);
    }
    public void ClickProfile(View view) {
        MainActivity.redirectActivity(this, ProfileActivity.class);
    }
    public void changeTheme(View view) {
        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            editor = sharedPreferences.edit();
            editor.putBoolean("night", false);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            editor = sharedPreferences.edit();
            editor.putBoolean("night", true);
        }
        editor.apply();
    }
    public void AddTask(View view) {
        MainActivity.redirectActivity(this, AddTaskActivity.class);
    }
    public void ClickMenu(View view) {
        MainActivity.openDrawer(drawerLayout);
    }
    public void ClickHome(View view) {
        MainActivity.redirectActivity(this, MainActivity.class);
    }
    public void ClickCalendar(View view) { MainActivity.redirectActivity(this, CalendarActivity.class);}
    public void ClickTask(View view) {
        recreate();
    }
    public void ClickLogOut(View view) {
        MainActivity.logOut(this);
    }
    @Override
    protected void onPause() {
        super.onPause();
        MainActivity.closeDrawer(drawerLayout);
    }
}