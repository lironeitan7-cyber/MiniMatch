
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
import android.widget.ScrollView;

public class MainActivity extends Activity {

    private LinearLayout mainLayout;

    private final int backgroundColor = Color.rgb(250, 246, 239);
    private final int orangeColor = Color.rgb(235, 137, 65);
    private final int darkColor = Color.rgb(57, 43, 34);
    private final int mutedColor = Color.rgb(135, 119, 106);
    private final int whiteColor = Color.WHITE;

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

        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);
        scrollView.setBackgroundColor(backgroundColor);

        mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setGravity(Gravity.CENTER);
        mainLayout.setPadding(dp(28), dp(30), dp(28), dp(30));

        scrollView.addView(mainLayout);
        setContentView(scrollView);

        // Logo
        TextView logo = new TextView(this);
        logo.setText("🐾  DogMatch");
        logo.setTextSize(29);
        logo.setTextColor(darkColor);
        logo.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        logo.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams logoParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        logoParams.setMargins(0, 0, 0, dp(35));
        mainLayout.addView(logo, logoParams);

        // Dog illustration area
        LinearLayout dogContainer = new LinearLayout(this);
        dogContainer.setGravity(Gravity.CENTER);
        dogContainer.setBackground(createRounded(
                Color.rgb(255, 232, 207), 150
        ));

        TextView dog = new TextView(this);
        dog.setText("🐕");
        dog.setTextSize(105);
        dog.setGravity(Gravity.CENTER);

        dogContainer.addView(dog);

        LinearLayout.LayoutParams dogParams =
                new LinearLayout.LayoutParams(dp(220), dp(220));

        dogParams.setMargins(0, 0, 0, dp(30));
        mainLayout.addView(dogContainer, dogParams);

        // Main title
        TextView title = new TextView(this);
        title.setText("החבר הבא של הכלב שלך");
        title.setTextSize(27);
        title.setTextColor(darkColor);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);

        mainLayout.addView(title);

        // Subtitle
        TextView subtitle = new TextView(this);
        subtitle.setText(
                "מכירים כלבים חדשים, יוצרים חברויות\n" +
                        "ויוצאים יחד להרפתקאות"
        );
        subtitle.setTextSize(16);
        subtitle.setTextColor(mutedColor);
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setLineSpacing(dp(5), 1f);

        LinearLayout.LayoutParams subtitleParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        subtitleParams.setMargins(0, dp(12), 0, dp(38));
        mainLayout.addView(subtitle, subtitleParams);

        // Login button
        TextView loginButton = createButton(
                "התחברות",
                true
        );

        mainLayout.addView(loginButton);

        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    LoginActivity.class
            );
            startActivity(intent);
        });

        // Register button
        TextView registerButton = createButton(
                "יצירת חשבון חדש",
                false
        );

        LinearLayout.LayoutParams registerParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(58)
                );

        registerParams.setMargins(0, dp(13), 0, 0);
        mainLayout.addView(registerButton, registerParams);

        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    RegisterActivity.class
            );
            startActivity(intent);
        });

        // Bottom caption
        TextView footer = new TextView(this);
        footer.setText("A better friend for every dog.");
        footer.setTextSize(12);
        footer.setTextColor(mutedColor);
        footer.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams footerParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        footerParams.setMargins(0, dp(30), 0, 0);
        mainLayout.addView(footer, footerParams);
    }

    private TextView createButton(String text, boolean filled) {

        TextView button = new TextView(this);

        button.setText(text);
        button.setTextSize(17);
        button.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        button.setGravity(Gravity.CENTER);
        button.setAllCaps(false);

        if (filled) {
            button.setTextColor(whiteColor);
            button.setBackground(createRounded(orangeColor, 18));
        } else {
            button.setTextColor(darkColor);
            GradientDrawable outline =
                    createRounded(backgroundColor, 18);

            outline.setStroke(dp(2), orangeColor);
            button.setBackground(outline);
        }

        button.setElevation(dp(2));
        button.setClickable(true);
        button.setFocusable(true);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(58)
                );

        button.setLayoutParams(params);

        return button;
    }

    private GradientDrawable createRounded(int color, int radius) {

        GradientDrawable shape = new GradientDrawable();
        shape.setColor(color);
        shape.setCornerRadius(dp(radius));

        return shape;
    }

    private int dp(int value) {
        return (int) (value * getResources()
                .getDisplayMetrics().density);
    }
}
