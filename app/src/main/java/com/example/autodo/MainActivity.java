package com.example.autodo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView textViewProfile;
    private ImageView imageViewProfile;
    DrawerLayout drawerLayout;
    boolean nightMode;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    GoogleSignInOptions googleSignInOptions;
    static GoogleSignInClient googleSignInClient;
    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;
    private List<TaskData> dataList;
    private DatabaseReference databaseReference;
    ValueEventListener eventListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        textViewProfile = (TextView) findViewById(R.id.textView6);
        imageViewProfile = (ImageView) findViewById(R.id.imageView5);
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        dataList = new ArrayList<>();

        sharedPreferences = getSharedPreferences("THEME_MODE", Context.MODE_PRIVATE);
        nightMode = sharedPreferences.getBoolean("night", false);

        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        adapter = new RecyclerAdapter(dataList, MainActivity.this);
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
    public static void closeDrawer(DrawerLayout drawerLayout) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }
    public void ClickProfile(View view) {
        redirectActivity(this, ProfileActivity.class);
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
        redirectActivity(this, AddTaskActivity.class);
    }
    public void ClickMenu(View view) {
        openDrawer(drawerLayout);
    }
    public static void openDrawer(@NonNull DrawerLayout drawerLayout) {drawerLayout.openDrawer(GravityCompat.START);}
    public void ClickHome(View view) {
        recreate();
    }
    public void ClickCalendar(View view) {
        redirectActivity(this, CalendarActivity.class);
    }
    public void ClickTask(View view) {
        redirectActivity(this, AutodoListActivity.class);
    }
    public void ClickLogOut(View view) {
        logOut(this);
    }
    public static void logOut(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Sign out ?");
        builder.setMessage("Are you sure you want to Sign out ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(activity.getApplicationContext(), LoginActivity.class);
                        activity.startActivity(intent);
                        activity.finish();
                    }
                });
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
    public static void redirectActivity(Activity activity,Class Class) {
        Intent intent = new Intent(activity, Class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }
    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
    }
}