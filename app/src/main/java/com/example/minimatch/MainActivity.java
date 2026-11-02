
package com.example.minimatch;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

    LinearLayout mainLayout;

    int backgroundColor = Color.rgb(255, 245, 241);
    int pinkColor = Color.rgb(217, 79, 120);
    int textColor = Color.rgb(63, 35, 43);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(backgroundColor);
        getWindow().setNavigationBarColor(backgroundColor);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR |
                        View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        );

        createScreen();
    }

    private void createScreen() {

        mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setGravity(Gravity.CENTER);
        mainLayout.setPadding(30, 20, 30, 20);
        mainLayout.setBackgroundColor(backgroundColor);

        setContentView(mainLayout);

        TextView heart = new TextView(this);
        heart.setText("♥");
        heart.setTextSize(48);
        heart.setTextColor(pinkColor);
        heart.setGravity(Gravity.CENTER);
        mainLayout.addView(heart);

        TextView title = new TextView(this);
        title.setText("אהבה קטנה");
        title.setTextSize(38);
        title.setTextColor(textColor);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        mainLayout.addView(title);

        TextView carrot = new TextView(this);
        carrot.setText("🥕");
        carrot.setTextSize(100);
        carrot.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams carrotParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        carrotParams.setMargins(0, 25, 0, 25);
        mainLayout.addView(carrot, carrotParams);

        TextView subtitle = new TextView(this);
        subtitle.setText("כי גם לגמדים מגיע למצוא אהבה");
        subtitle.setTextSize(17);
        subtitle.setTextColor(textColor);
        subtitle.setGravity(Gravity.CENTER);
        mainLayout.addView(subtitle);

        TextView description = new TextView(this);
        description.setText("מכירים. מתחברים. יוצאים לדייט.");
        description.setTextSize(14);
        description.setTextColor(Color.rgb(130, 105, 110));
        description.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams descriptionParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        descriptionParams.setMargins(0, 30, 0, 35);
        mainLayout.addView(description, descriptionParams);

        // כפתור התחברות
        TextView loginButton = createButton("יש לי חשבון – התחברות", true);
        mainLayout.addView(loginButton);

        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    LoginActivity.class
            );
            startActivity(intent);
        });

        // כפתור הרשמה
        TextView registerButton = createButton("משתמש חדש – הרשמה", false);

        LinearLayout.LayoutParams registerParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        60
                );

        registerParams.setMargins(0, 15, 0, 0);
        mainLayout.addView(registerButton, registerParams);

        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    RegisterActivity.class
            );
            startActivity(intent);
        });
    }

    private TextView createButton(String text, boolean filled) {

        TextView button = new TextView(this);
        button.setText(text);
        button.setTextSize(17);
        button.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        button.setGravity(Gravity.CENTER);
        button.setClickable(true);
        button.setFocusable(true);

        GradientDrawable background = new GradientDrawable();
        background.setCornerRadius(50);

        if (filled) {
            background.setColor(pinkColor);
            button.setTextColor(Color.WHITE);
        } else {
            background.setColor(backgroundColor);
            background.setStroke(3, pinkColor);
            button.setTextColor(pinkColor);
        }

        button.setBackground(background);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        60
                );

        button.setLayoutParams(params);

        return button;
    }
}
