
package com.example.minimatch;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends Activity {

    private FirebaseAuth mAuth;

    private final int pink = Color.rgb(217, 79, 120);
    private final int dark = Color.rgb(63, 35, 43);
    private final int background = Color.rgb(255, 245, 241);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(dp(25), dp(30), dp(25), dp(30));
        root.setBackgroundColor(background);

        TextView heart = new TextView(this);
        heart.setText("♥");
        heart.setTextSize(55);
        heart.setTextColor(pink);
        heart.setGravity(Gravity.CENTER);
        root.addView(heart);

        TextView title = new TextView(this);
        title.setText("איזה כיף שחזרת!");
        title.setTextSize(29);
        title.setTextColor(dark);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("האהבה אולי מחכה לך כאן ❤️");
        subtitle.setTextSize(15);
        subtitle.setTextColor(Color.rgb(135, 105, 112));
        subtitle.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams subParams =
                new LinearLayout.LayoutParams(
                        -1, -2
                );
        subParams.setMargins(0, dp(8), 0, dp(28));
        root.addView(subtitle, subParams);

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(22), dp(25), dp(22), dp(25));
        card.setBackground(rounded(Color.WHITE, 24));
        card.setElevation(dp(5));

        TextView cardTitle = new TextView(this);
        cardTitle.setText("התחברות לחשבון");
        cardTitle.setTextSize(20);
        cardTitle.setTextColor(dark);
        cardTitle.setTypeface(null, Typeface.BOLD);
        cardTitle.setGravity(Gravity.RIGHT);
        card.addView(cardTitle);

        TextView emailLabel = createLabel("כתובת אימייל");
        card.addView(emailLabel);

        EditText email = createField("name@example.com");
        email.setInputType(33);
        card.addView(email);

        TextView passwordLabel = createLabel("סיסמה");
        card.addView(passwordLabel);

        EditText password = createField("הקלד את הסיסמה שלך");
        password.setInputType(129);
        card.addView(password);

        Button login = new Button(this);
        login.setText("התחברות");
        login.setTextSize(17);
        login.setTypeface(null, Typeface.BOLD);
        login.setTextColor(Color.WHITE);
        login.setAllCaps(false);
        login.setBackground(rounded(pink, 16));

        LinearLayout.LayoutParams buttonParams =
                new LinearLayout.LayoutParams(-1, dp(54));
        buttonParams.setMargins(0, dp(25), 0, 0);
        card.addView(login, buttonParams);

        root.addView(card, new LinearLayout.LayoutParams(-1, -2));

        TextView back = new TextView(this);
        back.setText("עדיין אין לך חשבון? הרשמה");
        back.setTextSize(15);
        back.setTextColor(pink);
        back.setGravity(Gravity.CENTER);
        back.setPadding(0, dp(25), 0, dp(10));
        root.addView(back);

        back.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });

        setContentView(root);

        login.setOnClickListener(v -> {

            String userEmail = email.getText().toString().trim();
            String userPassword = password.getText().toString();

            if (userEmail.isEmpty() || userPassword.isEmpty()) {
                Toast.makeText(this,
                        "יש למלא אימייל וסיסמה",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            login.setEnabled(false);
            login.setText("מתחברים...");

            mAuth.signInWithEmailAndPassword(userEmail, userPassword)
                    .addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {

                            Toast.makeText(this,
                                    "התחברת בהצלחה! ❤️",
                                    Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(
                                    LoginActivity.this,
                                    ProfileActivity.class
                            ));
                            finish();

                        } else {

                            login.setEnabled(true);
                            login.setText("התחברות");

                            Toast.makeText(this,
                                    "האימייל או הסיסמה אינם נכונים",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }

    private TextView createLabel(String text) {
        TextView label = new TextView(this);
        label.setText(text);
        label.setTextSize(14);
        label.setTextColor(dark);
        label.setGravity(Gravity.RIGHT);
        label.setTypeface(null, Typeface.BOLD);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, -2);
        params.setMargins(0, dp(20), 0, dp(5));
        label.setLayoutParams(params);

        return label;
    }

    private EditText createField(String hint) {
        EditText field = new EditText(this);
        field.setHint(hint);
        field.setTextSize(15);
        field.setTextColor(dark);
        field.setHintTextColor(Color.rgb(165, 145, 150));
        field.setSingleLine(true);
        field.setPadding(dp(15), 0, dp(15), 0);
        field.setBackground(rounded(
                Color.rgb(255, 248, 247), 13
        ));
        field.setLayoutParams(
                new LinearLayout.LayoutParams(-1, dp(52))
        );
        return field;
    }

    private GradientDrawable rounded(int color, int radius) {
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
