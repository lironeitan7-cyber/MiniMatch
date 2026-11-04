
package com.example.minimatch;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Spinner;
import android.widget.ArrayAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends Activity {

    private EditText dogName, dogAge, breed, city, about;
    private Spinner sizeSpinner, energySpinner, personalitySpinner;
    private Button saveButton;

    private FirebaseFirestore db;

    private final int orange = Color.rgb(235, 137, 65);
    private final int dark = Color.rgb(57, 43, 34);
    private final int background = Color.rgb(250, 246, 239);
    private final int muted = Color.rgb(135, 119, 106);
    private final int white = Color.WHITE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();

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
        scrollView.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        root.setPadding(dp(24), dp(25), dp(24), dp(30));

        scrollView.addView(root);
        setContentView(scrollView);

        TextView logo = new TextView(this);
        logo.setText("🐾  DogMatch");
        logo.setTextSize(25);
        logo.setTextColor(dark);
        logo.setTypeface(null, Typeface.BOLD);
        logo.setGravity(Gravity.CENTER);
        root.addView(logo);

        TextView dogIcon = new TextView(this);
        dogIcon.setText("🐶");
        dogIcon.setTextSize(55);
        dogIcon.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams iconParams =
                new LinearLayout.LayoutParams(-1, dp(85));
        iconParams.setMargins(0, dp(15), 0, dp(5));
        root.addView(dogIcon, iconParams);

        TextView title = new TextView(this);
        title.setText("בואו נכיר את הכלב שלך!");
        title.setTextSize(26);
        title.setTextColor(dark);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText(
                "כמה פרטים שיעזרו לנו למצוא לו חברים חדשים"
        );
        subtitle.setTextSize(14);
        subtitle.setTextColor(muted);
        subtitle.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams subtitleParams =
                new LinearLayout.LayoutParams(-1, -2);
        subtitleParams.setMargins(0, dp(9), 0, dp(23));
        root.addView(subtitle, subtitleParams);

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        card.setPadding(dp(20), dp(22), dp(20), dp(22));
        card.setBackground(rounded(white, 22));
        card.setElevation(dp(5));

        TextView sectionTitle = new TextView(this);
        sectionTitle.setText("הפרופיל של הכלב 🐕");
        sectionTitle.setTextSize(20);
        sectionTitle.setTextColor(dark);
        sectionTitle.setTypeface(null, Typeface.BOLD);
        sectionTitle.setGravity(Gravity.RIGHT);
        card.addView(sectionTitle);

        dogName = createField("למשל: מקס");
        addField(card, "איך קוראים לכלב?", dogName);

        dogAge = createField("גיל בשנים");
        dogAge.setInputType(
                android.text.InputType.TYPE_CLASS_NUMBER
        );
        addField(card, "בן כמה הכלב?", dogAge);

        breed = createField("למשל: גולדן רטריבר");
        addField(card, "מה הגזע שלו?", breed);

        city = createField("עיר מגורים");
        addField(card, "איפה אתם גרים?", city);

        addSpinnerField(card, "מה הגודל של הכלב?",
                new String[]{"בחר גודל", "קטן", "בינוני", "גדול"},
                0);

        addSpinnerField(card, "מה רמת האנרגיה שלו?",
                new String[]{"בחר רמת אנרגיה", "רגועה", "בינונית", "גבוהה"},
                1);

        addSpinnerField(card, "איך האופי שלו?",
                new String[]{"בחר אופי", "חברותי", "שובב", "רגוע", "ביישן", "אנרגטי"},
                2);

        about = createField("ספרו לנו קצת על הכלב...");
        about.setSingleLine(false);
        about.setMinLines(3);
        about.setGravity(Gravity.RIGHT | Gravity.TOP);
        about.setImeOptions(EditorInfo.IME_ACTION_DONE);
        addField(card, "קצת עליו", about);

        saveButton = new Button(this);
        saveButton.setText("שמירת פרופיל הכלב 🐾");
        saveButton.setTextSize(16);
        saveButton.setTypeface(null, Typeface.BOLD);
        saveButton.setTextColor(white);
        saveButton.setAllCaps(false);
        saveButton.setBackground(rounded(orange, 17));

        LinearLayout.LayoutParams buttonParams =
                new LinearLayout.LayoutParams(-1, dp(56));
        buttonParams.setMargins(0, dp(25), 0, 0);
        card.addView(saveButton, buttonParams);

        root.addView(card, new LinearLayout.LayoutParams(-1, -2));

        TextView footer = new TextView(this);
        footer.setText("A better friend for every dog. 🐾");
        footer.setTextSize(12);
        footer.setTextColor(muted);
        footer.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams footerParams =
                new LinearLayout.LayoutParams(-1, dp(45));
        footerParams.setMargins(0, dp(15), 0, 0);
        root.addView(footer, footerParams);

        saveButton.setOnClickListener(v -> saveProfile());
    }

    private void addSpinnerField(LinearLayout card, String label,
                                 String[] options, int spinnerNumber) {

        TextView text = new TextView(this);
        text.setText(label);
        text.setTextSize(14);
        text.setTextColor(dark);
        text.setTypeface(null, Typeface.BOLD);
        text.setGravity(Gravity.RIGHT);

        LinearLayout.LayoutParams labelParams =
                new LinearLayout.LayoutParams(-1, -2);
        labelParams.setMargins(0, dp(18), 0, dp(6));
        card.addView(text, labelParams);

        Spinner spinner = new Spinner(this);
        spinner.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                options
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinner.setAdapter(adapter);
        spinner.setBackground(rounded(
                Color.rgb(250, 247, 242), 13
        ));

        card.addView(spinner,
                new LinearLayout.LayoutParams(-1, dp(52)));

        if (spinnerNumber == 0) {
            sizeSpinner = spinner;
        } else if (spinnerNumber == 1) {
            energySpinner = spinner;
        } else {
            personalitySpinner = spinner;
        }
    }

    private void addField(LinearLayout card, String labelText,
                          EditText field) {

        TextView label = new TextView(this);
        label.setText(labelText);
        label.setTextSize(14);
        label.setTextColor(dark);
        label.setTypeface(null, Typeface.BOLD);
        label.setGravity(Gravity.RIGHT);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, -2);
        params.setMargins(0, dp(18), 0, dp(6));

        card.addView(label, params);
        card.addView(field);
    }

    private EditText createField(String hint) {

        EditText field = new EditText(this);

        field.setHint(hint);
        field.setTextSize(15);
        field.setTextColor(dark);
        field.setHintTextColor(muted);
        field.setPadding(dp(14), dp(10), dp(14), dp(10));

        field.setBackground(rounded(
                Color.rgb(250, 247, 242), 13
        ));

        // תמיכה בכיווניות עברית ואנגלית
        field.setTextDirection(View.TEXT_DIRECTION_FIRST_STRONG);
        field.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        field.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        field.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);

        // לא מגבילים את שפת ההקלדה
        field.setInputType(
                android.text.InputType.TYPE_CLASS_TEXT |
                        android.text.InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
        );

        field.setImeOptions(EditorInfo.IME_ACTION_NEXT);

        field.setLayoutParams(
                new LinearLayout.LayoutParams(-1, dp(52))
        );

        return field;
    }

    private void saveProfile() {

        String name = dogName.getText().toString().trim();
        String ageText = dogAge.getText().toString().trim();
        String dogBreed = breed.getText().toString().trim();
        String dogCity = city.getText().toString().trim();
        String dogAbout = about.getText().toString().trim();

        if (name.isEmpty() || ageText.isEmpty() ||
                dogBreed.isEmpty() || dogCity.isEmpty() ||
                dogAbout.isEmpty()) {

            Toast.makeText(this,
                    "יש למלא את כל הפרטים",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (sizeSpinner.getSelectedItemPosition() == 0 ||
                energySpinner.getSelectedItemPosition() == 0 ||
                personalitySpinner.getSelectedItemPosition() == 0) {

            Toast.makeText(this,
                    "יש לבחור גודל, אנרגיה ואופי",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        int age;

        try {
            age = Integer.parseInt(ageText);
        } catch (NumberFormatException e) {
            Toast.makeText(this,
                    "יש להזין גיל תקין",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (age < 0 || age > 30) {
            Toast.makeText(this,
                    "יש להזין גיל בין 0 ל-30",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user =
                FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            Toast.makeText(this,
                    "צריך להתחבר מחדש לחשבון",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> profile = new HashMap<>();

        profile.put("dogName", name);
        profile.put("dogAge", age);
        profile.put("breed", dogBreed);
        profile.put("city", dogCity);
        profile.put("size", sizeSpinner.getSelectedItem().toString());
        profile.put("energy", energySpinner.getSelectedItem().toString());
        profile.put("personality", personalitySpinner.getSelectedItem().toString());
        profile.put("about", dogAbout);
        profile.put("email", user.getEmail());
        profile.put("ownerName", user.getDisplayName());

        saveButton.setEnabled(false);
        saveButton.setText("שומרים את הפרופיל...");

        db.collection("users")
                .document(user.getUid())
                .set(profile)
                .addOnSuccessListener(unused -> {

                    Toast.makeText(this,
                            "הפרופיל נשמר בהצלחה! 🐾",
                            Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(
                            ProfileActivity.this,
                            HomeActivity.class
                    );

                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {

                    saveButton.setEnabled(true);
                    saveButton.setText("שמירת פרופיל הכלב 🐾");

                    Toast.makeText(this,
                            "שמירת הפרופיל נכשלה: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
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
