package com.example.autodo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

public class AutodoListActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    boolean nightMode;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autodo_list);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_activity);

        sharedPreferences = getSharedPreferences("THEME_MODE", Context.MODE_PRIVATE);
        nightMode = sharedPreferences.getBoolean("night", false);

        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

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
    public void ClickCalendar(View view) {
        MainActivity.redirectActivity(this, CalendarActivity.class);
    }
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