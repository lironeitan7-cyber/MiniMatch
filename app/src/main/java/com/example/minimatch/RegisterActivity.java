
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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends Activity {

    private FirebaseAuth mAuth;

    private final int pink = Color.rgb(217, 79, 120);
    private final int dark = Color.rgb(63, 35, 43);
    private final int background = Color.rgb(255, 245, 241);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        getWindow().setStatusBarColor(background);
        getWindow().setNavigationBarColor(background);

        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setPadding(dp(24), dp(25), dp(24), dp(30));
        root.setBackgroundColor(background);

        scrollView.addView(root);

        TextView heart = new TextView(this);
        heart.setText("♥");
        heart.setTextSize(48);
        heart.setTextColor(pink);
        heart.setGravity(Gravity.CENTER);
        root.addView(heart);

        TextView title = new TextView(this);
        title.setText("יוצרים חשבון!");
        title.setTextSize(28);
        title.setTextColor(dark);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("אולי כאן מתחיל סיפור יפה ❤️");
        subtitle.setTextSize(15);
        subtitle.setTextColor(Color.rgb(135, 105, 112));
        subtitle.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams subtitleParams =
                new LinearLayout.LayoutParams(-1, -2);
        subtitleParams.setMargins(0, dp(8), 0, dp(22));
        root.addView(subtitle, subtitleParams);

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(20), dp(22), dp(20), dp(22));
        card.setBackground(rounded(Color.WHITE, 22));
        card.setElevation(dp(5));

        TextView sectionTitle = new TextView(this);
        sectionTitle.setText("פרטים ליצירת החשבון");
        sectionTitle.setTextSize(20);
        sectionTitle.setTextColor(dark);
        sectionTitle.setTypeface(null, Typeface.BOLD);
        sectionTitle.setGravity(Gravity.RIGHT);
        card.addView(sectionTitle);

        EditText name = createField("שם מלא");
        addField(card, "איך קוראים לך?", name);

        EditText age = createField("גיל");
        age.setInputType(2);
        addField(card, "בן/בת כמה את/ה?", age);

        EditText email = createField("name@example.com");
        email.setInputType(33);
        addField(card, "כתובת אימייל", email);

        EditText password = createField("לפחות 6 תווים");
        password.setInputType(129);
        addField(card, "סיסמה", password);

        Button register = new Button(this);
        register.setText("יצירת חשבון ❤️");
        register.setTextSize(17);
        register.setTypeface(null, Typeface.BOLD);
        register.setTextColor(Color.WHITE);
        register.setAllCaps(false);
        register.setBackground(rounded(pink, 16));

        LinearLayout.LayoutParams buttonParams =
                new LinearLayout.LayoutParams(-1, dp(55));
        buttonParams.setMargins(0, dp(25), 0, 0);
        card.addView(register, buttonParams);

        root.addView(card, new LinearLayout.LayoutParams(-1, -2));

        TextView footer = new TextView(this);
        footer.setText("כבר יש לך חשבון? התחברות");
        footer.setTextSize(15);
        footer.setTextColor(pink);
        footer.setGravity(Gravity.CENTER);
        footer.setPadding(0, dp(22), 0, dp(10));
        root.addView(footer);

        footer.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        setContentView(scrollView);

        register.setOnClickListener(v -> {

            String userName = name.getText().toString().trim();
            String userAge = age.getText().toString().trim();
            String userEmail = email.getText().toString().trim();
            String userPassword = password.getText().toString();

            if (userName.isEmpty() || userAge.isEmpty()
                    || userEmail.isEmpty() || userPassword.isEmpty()) {

                Toast.makeText(this,
                        "יש למלא את כל הפרטים",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (userPassword.length() < 6) {
                Toast.makeText(this,
                        "הסיסמה חייבת להכיל לפחות 6 תווים",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            int ageNumber;

            try {
                ageNumber = Integer.parseInt(userAge);
            } catch (NumberFormatException e) {
                Toast.makeText(this,
                        "יש להזין גיל תקין",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (ageNumber < 18 || ageNumber > 120) {
                Toast.makeText(this,
                        "ההרשמה מיועדת לבני 18 ומעלה",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            register.setEnabled(false);
            register.setText("יוצרים חשבון...");

            mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                    .addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {

                            UserProfileChangeRequest profileUpdates =
                                    new UserProfileChangeRequest.Builder()
                                            .setDisplayName(userName)
                                            .build();

                            mAuth.getCurrentUser()
                                    .updateProfile(profileUpdates)
                                    .addOnCompleteListener(profileTask -> {

                                        if (profileTask.isSuccessful()) {

                                            Intent intent = new Intent(
                                                    RegisterActivity.this,
                                                    ProfileActivity.class
                                            );

                                            intent.putExtra("USER_AGE", userAge);

                                            startActivity(intent);
                                            finish();

                                        } else {

                                            Toast.makeText(this,
                                                    "החשבון נוצר, אך עדכון השם נכשל",
                                                    Toast.LENGTH_LONG).show();

                                            register.setEnabled(true);
                                            register.setText("יצירת חשבון ❤️");
                                        }
                                    });

                        } else {

                            String error = task.getException() != null
                                    ? task.getException().getMessage()
                                    : "אירעה שגיאה לא ידועה";

                            Toast.makeText(this,
                                    "שגיאה בהרשמה: " + error,
                                    Toast.LENGTH_LONG).show();

                            register.setEnabled(true);
                            register.setText("יצירת חשבון ❤️");
                        }
                    });
        });
    }

    private void addField(LinearLayout card, String labelText,
                          EditText field) {

        TextView label = new TextView(this);
        label.setText(labelText);
        label.setTextSize(14);
        label.setTextColor(dark);
        label.setTypeface(null, Typeface.BOLD);
        label.setGravity(Gravity.RIGHT);

        LinearLayout.LayoutParams labelParams =
                new LinearLayout.LayoutParams(-1, -2);
        labelParams.setMargins(0, dp(18), 0, dp(6));
        card.addView(label, labelParams);

        card.addView(field);
    }

    private EditText createField(String hint) {

        EditText field = new EditText(this);
        field.setHint(hint);
        field.setTextSize(15);
        field.setTextColor(dark);
        field.setHintTextColor(Color.rgb(165, 145, 150));
        field.setPadding(dp(14), dp(10), dp(14), dp(10));
        field.setSingleLine(true);
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
