
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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MatchesActivity extends Activity {

    private final int background = Color.rgb(250, 246, 239);
    private final int orange = Color.rgb(235, 137, 65);
    private final int dark = Color.rgb(57, 43, 34);
    private final int muted = Color.rgb(135, 119, 106);
    private final int white = Color.WHITE;

    private FirebaseFirestore db;
    private String currentUid;
    private LinearLayout matchesContainer;

    private int dp(float value) {
        return (int) (value * getResources()
                .getDisplayMetrics().density);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        currentUid = user.getUid();
        db = FirebaseFirestore.getInstance();

        getWindow().setStatusBarColor(background);
        getWindow().setNavigationBarColor(background);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR |
                        View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        );

        createScreen();
        loadMatches();
    }

    private void createScreen() {

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(background);
        root.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(20), dp(18), dp(20), dp(8));
        content.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        TextView title = text("ההתאמות שלך ❤️", 24, true);

        LinearLayout.LayoutParams titleParams =
                new LinearLayout.LayoutParams(-1, -2);
        titleParams.setMargins(0, 0, 0, dp(4));
        content.addView(title, titleParams);

        TextView subtitle = text(
                "החברים החדשים שלכם מחכים להכיר 🐶",
                14, false
        );
        subtitle.setTextColor(muted);

        LinearLayout.LayoutParams subtitleParams =
                new LinearLayout.LayoutParams(-1, -2);
        subtitleParams.setMargins(0, 0, 0, dp(15));
        content.addView(subtitle, subtitleParams);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setClipToPadding(false);

        matchesContainer = new LinearLayout(this);
        matchesContainer.setOrientation(LinearLayout.VERTICAL);
        matchesContainer.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        scroll.addView(matchesContainer);

        content.addView(scroll, new LinearLayout.LayoutParams(
                -1, 0, 1
        ));

        root.addView(content, new LinearLayout.LayoutParams(
                -1, 0, 1
        ));

        root.addView(createBottomNavigation());

        setContentView(root);
    }

    private LinearLayout createBottomNavigation() {

        LinearLayout navigation = new LinearLayout(this);
        navigation.setOrientation(LinearLayout.HORIZONTAL);
        navigation.setGravity(Gravity.CENTER);
        navigation.setBackground(roundTop(white, 22));
        navigation.setElevation(dp(12));
        navigation.setPadding(dp(5), dp(9), dp(5), dp(7));
        navigation.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        addNavItem(navigation, "🏠", "ראשי", false, () -> {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        addNavItem(navigation, "❤️", "התאמות", true, () -> {
            // כבר נמצאים במסך ההתאמות
        });

        addNavItem(navigation, "💬", "צ'אטים", false, () -> {
            Toast.makeText(this,
                    "מסך הצ'אטים יגיע בקרוב 🐶",
                    Toast.LENGTH_SHORT).show();
        });

        addNavItem(navigation, "👤", "פרופיל", false, () -> {
            Toast.makeText(this,
                    "מסך הפרופיל יגיע בקרוב 🐾",
                    Toast.LENGTH_SHORT).show();
        });

        return navigation;
    }

    private void addNavItem(
            LinearLayout navigation,
            String icon,
            String label,
            boolean selected,
            Runnable action) {

        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.VERTICAL);
        item.setGravity(Gravity.CENTER);
        item.setPadding(dp(2), dp(3), dp(2), dp(3));
        item.setClickable(true);
        item.setFocusable(true);

        TextView iconText = new TextView(this);
        iconText.setText(icon);
        iconText.setTextSize(21);
        iconText.setGravity(Gravity.CENTER);

        item.addView(iconText);

        TextView labelText = new TextView(this);
        labelText.setText(label);
        labelText.setTextSize(11);
        labelText.setGravity(Gravity.CENTER);
        labelText.setTextColor(selected ? orange : muted);

        if (selected) {
            labelText.setTypeface(null, Typeface.BOLD);
        }

        LinearLayout.LayoutParams labelParams =
                new LinearLayout.LayoutParams(-1, -2);
        labelParams.setMargins(0, dp(3), 0, 0);

        item.addView(labelText, labelParams);

        item.setOnClickListener(v -> action.run());

        navigation.addView(item, new LinearLayout.LayoutParams(
                0, -2, 1
        ));
    }

    private void loadMatches() {

        matchesContainer.removeAllViews();

        TextView loading = text(
                "טוענים את ההתאמות שלך... 🐾", 16, false
        );
        loading.setGravity(Gravity.CENTER);
        matchesContainer.addView(loading);

        db.collection("matches")
                .whereArrayContains("users", currentUid)
                .get()
                .addOnSuccessListener(snapshot -> {

                    matchesContainer.removeAllViews();

                    if (snapshot.isEmpty()) {
                        showEmpty();
                        return;
                    }

                    List<String> dogIds = new ArrayList<>();

                    for (QueryDocumentSnapshot match : snapshot) {

                        List<String> users =
                                (List<String>) match.get("users");

                        if (users == null || users.size() < 2) {
                            continue;
                        }

                        String otherUid = users.get(0).equals(currentUid)
                                ? users.get(1)
                                : users.get(0);

                        if (!dogIds.contains(otherUid)) {
                            dogIds.add(otherUid);
                        }
                    }

                    if (dogIds.isEmpty()) {
                        showEmpty();
                        return;
                    }

                    for (String dogId : dogIds) {
                        loadDog(dogId);
                    }
                })
                .addOnFailureListener(error -> {

                    matchesContainer.removeAllViews();

                    Toast.makeText(this,
                            "שגיאה בטעינת ההתאמות: " +
                                    error.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    private void loadDog(String dogId) {

        db.collection("users")
                .document(dogId)
                .get()
                .addOnSuccessListener(document -> {

                    if (!document.exists()) {
                        return;
                    }

                    String name = document.getString("dogName");
                    String breed = document.getString("breed");
                    String city = document.getString("city");
                    Long age = document.getLong("dogAge");

                    if (name == null || name.trim().isEmpty()) {
                        name = "כלב חדש";
                    }

                    addDogCard(name, breed, city, age);
                });
    }

    private void addDogCard(
            String name,
            String breed,
            String city,
            Long age) {

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackground(round(white, 22));
        card.setElevation(dp(4));
        card.setClipToOutline(true);

        ImageView image = new ImageView(this);
        image.setImageResource(android.R.drawable.ic_menu_camera);
        image.setScaleType(ImageView.ScaleType.CENTER);
        image.setBackground(round(
                Color.rgb(245, 225, 207), 22
        ));

        card.addView(image, new LinearLayout.LayoutParams(
                -1, dp(125)
        ));

        LinearLayout details = new LinearLayout(this);
        details.setOrientation(LinearLayout.VERTICAL);
        details.setPadding(dp(12), dp(7), dp(12), dp(12));
        details.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        TextView matchTag = text("♥ יש לכם התאמה", 11, true);
        matchTag.setTextColor(orange);
        details.addView(matchTag);

        String dogTitle = name;
        if (age != null) {
            dogTitle += ", " + age;
        }

        TextView nameText = text(dogTitle, 18, true);
        details.addView(nameText);

        TextView breedText = text(
                breed == null ? "גזע לא צוין" : breed,
                13, false
        );
        breedText.setTextColor(muted);
        details.addView(breedText);

        TextView cityText = text(
                "📍 " + (city == null ? "לא צוין" : city),
                12, false
        );
        cityText.setTextColor(muted);
        details.addView(cityText);

        card.addView(details);

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(0, -2, 1);
        cardParams.setMargins(dp(5), dp(5), dp(5), dp(13));

        card.setOnClickListener(v -> {
            Toast.makeText(this,
                    "בקרוב תוכלו להתחיל שיחה עם " + name + " 🐶",
                    Toast.LENGTH_SHORT).show();
        });

        addCardToGrid(card, cardParams);
    }

    private void addCardToGrid(
            View card,
            LinearLayout.LayoutParams cardParams) {

        int count = matchesContainer.getChildCount();

        LinearLayout row;

        if (count == 0) {
            row = createRow();
            matchesContainer.addView(row);
        } else {
            row = (LinearLayout) matchesContainer.getChildAt(count - 1);

            if (row.getChildCount() >= 2) {
                row = createRow();
                matchesContainer.addView(row);
            }
        }

        row.addView(card, cardParams);
    }

    private LinearLayout createRow() {

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        return row;
    }

    private void showEmpty() {

        LinearLayout emptyCard = new LinearLayout(this);
        emptyCard.setOrientation(LinearLayout.VERTICAL);
        emptyCard.setGravity(Gravity.CENTER);
        emptyCard.setPadding(dp(25), dp(35), dp(25), dp(35));
        emptyCard.setBackground(round(white, 24));
        emptyCard.setElevation(dp(3));

        TextView icon = text("🐶", 55, false);
        icon.setGravity(Gravity.CENTER);
        emptyCard.addView(icon);

        TextView title = text("עדיין אין התאמות", 21, true);
        title.setGravity(Gravity.CENTER);
        emptyCard.addView(title);

        TextView message = text(
                "המשיכו להכיר כלבים חדשים.\n" +
                        "כשתהיה אהבה הדדית, היא תופיע כאן ❤️",
                14, false
        );
        message.setTextColor(muted);
        message.setGravity(Gravity.CENTER);

        emptyCard.addView(message);

        matchesContainer.addView(emptyCard);
    }

    private TextView text(
            String value,
            int size,
            boolean bold) {

        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(dark);
        view.setTextDirection(View.TEXT_DIRECTION_FIRST_STRONG);
        view.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        view.setGravity(Gravity.RIGHT);

        if (bold) {
            view.setTypeface(null, Typeface.BOLD);
        }

        return view;
    }

    private GradientDrawable round(int color, int radius) {

        GradientDrawable shape = new GradientDrawable();
        shape.setColor(color);
        shape.setCornerRadius(dp(radius));

        return shape;
    }

    private GradientDrawable roundTop(int color, int radius) {

        GradientDrawable shape = new GradientDrawable();
        shape.setColor(color);

        float r = dp(radius);
        shape.setCornerRadii(new float[]{
                r, r, r, r,
                0, 0, 0, 0
        });

        return shape;
    }
}
