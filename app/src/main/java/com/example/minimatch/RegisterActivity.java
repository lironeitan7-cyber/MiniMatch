
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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends Activity {

    private FirebaseAuth mAuth;

    private final int orange = Color.rgb(235, 137, 65);
    private final int dark = Color.rgb(57, 43, 34);
    private final int background = Color.rgb(250, 246, 239);
    private final int muted = Color.rgb(135, 119, 106);
    private final int white = Color.WHITE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        getWindow().setStatusBarColor(background);
        getWindow().setNavigationBarColor(background);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR |
                        View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        );

        createScreen();
    }

    private void createScreen() {

        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);
        scrollView.setBackgroundColor(background);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setPadding(dp(25), dp(25), dp(25), dp(30));

        scrollView.addView(root);
        setContentView(scrollView);

        // Logo
        TextView logo = new TextView(this);
        logo.setText("🐾  DogMatch");
        logo.setTextSize(25);
        logo.setTextColor(dark);
        logo.setTypeface(null, Typeface.BOLD);
        logo.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams logoParams =
                new LinearLayout.LayoutParams(-1, dp(55));
        logoParams.setMargins(0, 0, 0, dp(22));
        root.addView(logo, logoParams);

        // Dog icon
        TextView dog = new TextView(this);
        dog.setText("🐶");
        dog.setTextSize(55);
        dog.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams dogParams =
                new LinearLayout.LayoutParams(-1, dp(85));
        dogParams.setMargins(0, 0, 0, dp(8));
        root.addView(dog, dogParams);

        // Title
        TextView title = new TextView(this);
        title.setText("מצטרפים ל-DogMatch");
        title.setTextSize(27);
        title.setTextColor(dark);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        // Subtitle
        TextView subtitle = new TextView(this);
        subtitle.setText(
                "פותחים חשבון ומוצאים חברים חדשים\n" +
                        "לחבר הכי טוב שלכם"
        );
        subtitle.setTextSize(15);
        subtitle.setTextColor(muted);
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setLineSpacing(dp(5), 1f);

        LinearLayout.LayoutParams subtitleParams =
                new LinearLayout.LayoutParams(-1, -2);
        subtitleParams.setMargins(0, dp(10), 0, dp(25));
        root.addView(subtitle, subtitleParams);

        // Form card
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(20), dp(22), dp(20), dp(22));
        card.setBackground(rounded(white, 22));
        card.setElevation(dp(5));

        TextView sectionTitle = new TextView(this);
        sectionTitle.setText("פרטי בעל החשבון");
        sectionTitle.setTextSize(20);
        sectionTitle.setTextColor(dark);
        sectionTitle.setTypeface(null, Typeface.BOLD);
        sectionTitle.setGravity(Gravity.RIGHT);
        card.addView(sectionTitle);

        EditText name = createField("השם שלך");
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

        // Register button
        Button register = new Button(this);
        register.setText("יצירת חשבון");
        register.setTextSize(17);
        register.setTypeface(null, Typeface.BOLD);
        register.setTextColor(white);
        register.setAllCaps(false);
        register.setBackground(rounded(orange, 17));

        LinearLayout.LayoutParams buttonParams =
                new LinearLayout.LayoutParams(-1, dp(56));
        buttonParams.setMargins(0, dp(25), 0, 0);
        card.addView(register, buttonParams);

        root.addView(card, new LinearLayout.LayoutParams(-1, -2));

        // Login link
        TextView login = new TextView(this);
        login.setText("כבר יש לך חשבון? התחברות");
        login.setTextSize(15);
        login.setTextColor(orange);
        login.setTypeface(null, Typeface.BOLD);
        login.setGravity(Gravity.CENTER);
        login.setPadding(0, dp(24), 0, dp(10));
        root.addView(login);

        login.setOnClickListener(v -> {
            startActivity(new Intent(
                    RegisterActivity.this,
                    LoginActivity.class
            ));
            finish();
        });

        // Registration logic
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

            mAuth.createUserWithEmailAndPassword(
                    userEmail, userPassword
            ).addOnCompleteListener(task -> {

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
                                    intent.putExtra("USER_NAME", userName);

                                    startActivity(intent);
                                    finish();

                                } else {

                                    Toast.makeText(this,
                                            "החשבון נוצר, אך עדכון השם נכשל",
                                            Toast.LENGTH_LONG).show();

                                    register.setEnabled(true);
                                    register.setText("יצירת חשבון");
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
                    register.setText("יצירת חשבון");
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
        field.setHintTextColor(muted);
        field.setPadding(dp(14), dp(10), dp(14), dp(10));
        field.setSingleLine(true);
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
