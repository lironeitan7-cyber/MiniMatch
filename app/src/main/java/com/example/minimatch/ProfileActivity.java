
package com.example.minimatch;

import android.app.Activity;
import android.os.Bundle;
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

public class ProfileActivity extends Activity {

    private EditText name, age, height, city, about, hobbies;

    private final int pink = Color.rgb(217, 79, 120);
    private final int dark = Color.rgb(63, 35, 43);
    private final int background = Color.rgb(255, 245, 241);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        title.setText("בואו נכיר אותך!");
        title.setTextSize(28);
        title.setTextColor(dark);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("כמה פרטים קטנים כדי למצוא את החיבור שלך ❤️");
        subtitle.setTextSize(14);
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
        sectionTitle.setText("קצת עליי");
        sectionTitle.setTextSize(20);
        sectionTitle.setTextColor(dark);
        sectionTitle.setTypeface(null, Typeface.BOLD);
        sectionTitle.setGravity(Gravity.RIGHT);
        card.addView(sectionTitle);

        name = createField("שם מלא");

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String displayName = FirebaseAuth.getInstance()
                    .getCurrentUser().getDisplayName();

            if (displayName != null) {
                name.setText(displayName);
            }
        }

        addField(card, "איך קוראים לך?", name);

        age = createField("גיל");
        age.setInputType(2);

        String savedAge = getIntent().getStringExtra("USER_AGE");
        if (savedAge != null) {
            age.setText(savedAge);
        }

        addField(card, "בן/בת כמה את/ה?", age);

        height = createField("גובה בס״מ");
        height.setInputType(2);
        addField(card, "מה הגובה שלך? (עד 150 ס״מ)", height);

        city = createField("עיר מגורים");
        addField(card, "מאיפה את/ה?", city);

        about = createField("ספר/י קצת על עצמך...");
        about.setSingleLine(false);
        about.setMinLines(3);
        about.setGravity(Gravity.TOP | Gravity.RIGHT);
        addField(card, "כמה מילים עליי", about);

        hobbies = createField("מה את/ה אוהב/ת לעשות?");
        hobbies.setSingleLine(false);
        hobbies.setMinLines(2);
        hobbies.setGravity(Gravity.TOP | Gravity.RIGHT);
        addField(card, "תחביבים ותחומי עניין", hobbies);

        Button saveButton = new Button(this);
        saveButton.setText("שמירת הפרופיל ❤️");
        saveButton.setTextSize(17);
        saveButton.setTypeface(null, Typeface.BOLD);
        saveButton.setTextColor(Color.WHITE);
        saveButton.setAllCaps(false);
        saveButton.setBackground(rounded(pink, 16));

        LinearLayout.LayoutParams buttonParams =
                new LinearLayout.LayoutParams(-1, dp(55));
        buttonParams.setMargins(0, dp(25), 0, 0);
        card.addView(saveButton, buttonParams);

        root.addView(card, new LinearLayout.LayoutParams(-1, -2));

        TextView footer = new TextView(this);
        footer.setText("אהבה קטנה – מתחילים להכיר 💗");
        footer.setTextSize(13);
        footer.setTextColor(Color.rgb(150, 120, 125));
        footer.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams footerParams =
                new LinearLayout.LayoutParams(-1, -2);
        footerParams.setMargins(0, dp(20), 0, 0);
        root.addView(footer, footerParams);

        setContentView(scrollView);

        saveButton.setOnClickListener(v -> {

            String userName = name.getText().toString().trim();
            String userAge = age.getText().toString().trim();
            String userHeight = height.getText().toString().trim();
            String userCity = city.getText().toString().trim();
            String userAbout = about.getText().toString().trim();
            String userHobbies = hobbies.getText().toString().trim();

            if (userName.isEmpty() || userAge.isEmpty()
                    || userHeight.isEmpty() || userCity.isEmpty()
                    || userAbout.isEmpty() || userHobbies.isEmpty()) {

                Toast.makeText(this,
                        "יש למלא את כל הפרטים",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            int ageNumber;
            int heightNumber;

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
                        "יש להזין גיל תקין",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                heightNumber = Integer.parseInt(userHeight);
            } catch (NumberFormatException e) {
                Toast.makeText(this,
                        "יש להזין גובה במספרים בלבד",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (heightNumber < 1 || heightNumber > 150) {
                Toast.makeText(this,
                        "הגובה חייב להיות בין 1 ל-150 ס״מ",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this,
                    "הפרטים הוזנו בהצלחה! ❤️",
                    Toast.LENGTH_LONG).show();
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
