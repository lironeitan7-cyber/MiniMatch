
package com.example.minimatch;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends Activity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private final int orange = Color.rgb(235, 137, 65);
    private final int dark = Color.rgb(57, 43, 34);
    private final int background = Color.rgb(250, 246, 239);
    private final int muted = Color.rgb(135, 119, 106);
    private final int white = Color.WHITE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        getWindow().setStatusBarColor(background);
        getWindow().setNavigationBarColor(background);
        getWindow().getDecorView().setSystemUiVisibility(
                android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR |
                        android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        );

        createScreen();
    }

    private void createScreen() {

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(dp(25), dp(30), dp(25), dp(30));
        root.setBackgroundColor(background);

        TextView logo = new TextView(this);
        logo.setText("🐾  DogMatch");
        logo.setTextSize(25);
        logo.setTextColor(dark);
        logo.setTypeface(null, Typeface.BOLD);
        logo.setGravity(Gravity.CENTER);
        root.addView(logo);

        TextView dogIcon = new TextView(this);
        dogIcon.setText("🐶");
        dogIcon.setTextSize(65);
        dogIcon.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams iconParams =
                new LinearLayout.LayoutParams(-1, dp(105));
        iconParams.setMargins(0, dp(15), 0, dp(5));
        root.addView(dogIcon, iconParams);

        TextView title = new TextView(this);
        title.setText("איזה כיף שחזרת!");
        title.setTextSize(28);
        title.setTextColor(dark);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("החבר הבא של הכלב שלך מחכה כאן");
        subtitle.setTextSize(14);
        subtitle.setTextColor(muted);
        subtitle.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams subParams =
                new LinearLayout.LayoutParams(-1, -2);
        subParams.setMargins(0, dp(8), 0, dp(27));
        root.addView(subtitle, subParams);

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(22), dp(25), dp(22), dp(25));
        card.setBackground(rounded(white, 24));
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
        login.setTextColor(white);
        login.setAllCaps(false);
        login.setBackground(rounded(orange, 16));

        LinearLayout.LayoutParams buttonParams =
                new LinearLayout.LayoutParams(-1, dp(55));
        buttonParams.setMargins(0, dp(25), 0, 0);
        card.addView(login, buttonParams);

        root.addView(card, new LinearLayout.LayoutParams(-1, -2));

        TextView register = new TextView(this);
        register.setText("עדיין אין לך חשבון? הרשמה");
        register.setTextSize(15);
        register.setTextColor(orange);
        register.setTypeface(null, Typeface.BOLD);
        register.setGravity(Gravity.CENTER);
        register.setPadding(0, dp(25), 0, dp(10));
        root.addView(register);

        TextView footer = new TextView(this);
        footer.setText("A better friend for every dog. 🐾");
        footer.setTextSize(12);
        footer.setTextColor(muted);
        footer.setGravity(Gravity.CENTER);
        root.addView(footer);

        setContentView(root);

        register.setOnClickListener(v -> {
            startActivity(new Intent(
                    LoginActivity.this,
                    RegisterActivity.class
            ));
        });

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

            mAuth.signInWithEmailAndPassword(
                    userEmail, userPassword
            ).addOnCompleteListener(task -> {

                if (task.isSuccessful()) {

                    if (mAuth.getCurrentUser() == null) {
                        login.setEnabled(true);
                        login.setText("התחברות");
                        return;
                    }

                    String uid = mAuth.getCurrentUser().getUid();

                    checkUserProfile(uid, login);

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

    private void checkUserProfile(String uid, Button login) {

        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(document -> {

                    Intent intent;

                    if (document.exists()) {
                        intent = new Intent(
                                LoginActivity.this,
                                HomeActivity.class
                        );
                    } else {
                        intent = new Intent(
                                LoginActivity.this,
                                ProfileActivity.class
                        );
                    }

                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(error -> {

                    login.setEnabled(true);
                    login.setText("התחברות");

                    Toast.makeText(this,
                            "לא הצלחנו לבדוק את הפרופיל. נסה שוב.",
                            Toast.LENGTH_LONG).show();
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
        field.setHintTextColor(muted);
        field.setSingleLine(true);
        field.setPadding(dp(15), 0, dp(15), 0);
        field.setBackground(rounded(
                Color.rgb(250, 247, 242), 13
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
