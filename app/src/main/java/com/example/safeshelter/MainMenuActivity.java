package com.example.safeshelter;

import android.annotation.SuppressLint;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.miniapps.maze.MazeActivity;
import com.miniapps.quiz.MainQuizActivity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


public class MainMenuActivity extends AppCompatActivity {
    public ImageView settings_image, youtube_kids_image, quiz_app_image, maze_app_image;
    private boolean isYoutubeKidsSelected, isQuizSelected, isMazeSelected, isTicTacToeSelected;

    boolean currentFocus;
    boolean isPaused;
    Handler collapseNotificationHandler;

    //Criação de todos os botões do MainMenu
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        removeTitleBar();
        setContentView(R.layout.activity_main_menu);

        setTitle("SafeShelter");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Obtem as variaveis boolean para saber que apps estao bloqueadas a partir da AppFilterActivity
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        isYoutubeKidsSelected = prefs.getBoolean("YoutubeKids", false);
        isQuizSelected = prefs.getBoolean("Quiz", false);
        isMazeSelected = prefs.getBoolean("Maze", false);
        isTicTacToeSelected = prefs.getBoolean("TicTacToe", false);

        settings_image = (ImageView) findViewById(R.id.Settings_Icon);
        settings_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainMenuActivity.this, InsertParentalCode.class);
                startActivity(intent);
            }
        });

        youtube_kids_image = (ImageView) findViewById(R.id.imageYoutubeKids);

        youtube_kids_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.imageYoutubeKids && isYoutubeKidsSelected) {
                    Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.google.android.apps.youtube.kids");
                    startActivity(launchIntent);
                } else {
                    Toast.makeText(MainMenuActivity.this, "Acesso não permitido", Toast.LENGTH_SHORT).show();
                }
            }
        });

        quiz_app_image = (ImageView) findViewById(R.id.quizApp);
        quiz_app_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isQuizSelected){
                    Intent intent = new Intent(MainMenuActivity.this, MainQuizActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainMenuActivity.this, "Acesso não permitido", Toast.LENGTH_SHORT).show();
                }
            }
        });

        maze_app_image = (ImageView) findViewById(R.id.mazeApp);
        maze_app_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isMazeSelected){
                    Intent intent = new Intent(MainMenuActivity.this, MazeActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainMenuActivity.this, "Acesso não permitido", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    /*Bloquear a barra de notificações*/

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        currentFocus = hasFocus;

        if (!hasFocus) {
            collapseNow();
        }
    }

    public void collapseNow() {
        if (collapseNotificationHandler == null) {
            collapseNotificationHandler = new Handler();
        }

        if (!currentFocus && !isPaused) {
            collapseNotificationHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    @SuppressLint("WrongConstant") Object statusBarService = getSystemService("statusbar");
                    Class<?> statusBarManager = null;

                    try {
                        statusBarManager = Class.forName("android.app.StatusBarManager");
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    Method collapseStatusBar = null;

                    try {
                        if (Build.VERSION.SDK_INT > 16) {
                            collapseStatusBar = statusBarManager .getMethod("collapsePanels");
                        } else {
                            collapseStatusBar = statusBarManager .getMethod("collapse");
                        }
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }

                    collapseStatusBar.setAccessible(true);

                    try {
                        collapseStatusBar.invoke(statusBarService);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }

                    if (!currentFocus && !isPaused) {
                        collapseNotificationHandler.postDelayed(this, 100L);
                    }

                }
            }, 1L);
        }
    }

    //Remover Title Bar
    protected void removeTitleBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();
        //Remove notification bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    //Bloquear Back Button
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Do nothing or catch the keys you want to block
        return false;
    }
}