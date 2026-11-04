
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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ChatsActivity extends Activity {

    private final int background = Color.rgb(250, 246, 239);
    private final int orange = Color.rgb(235, 137, 65);
    private final int dark = Color.rgb(57, 43, 34);
    private final int muted = Color.rgb(135, 119, 106);
    private final int white = Color.WHITE;

    private FirebaseFirestore db;
    private String currentUid;
    private LinearLayout chatsContainer;

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
        loadChats();
    }

    private void createScreen() {

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(background);
        root.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(20), dp(20), dp(20), dp(8));
        content.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        TextView title = text("הצ'אטים שלי 💬", 25, true);
        content.addView(title);

        TextView subtitle = text(
                "כאן תוכלו לדבר עם ההתאמות שלכם 🐶",
                14, false
        );
        subtitle.setTextColor(muted);

        LinearLayout.LayoutParams subtitleParams =
                new LinearLayout.LayoutParams(-1, -2);
        subtitleParams.setMargins(0, dp(5), 0, dp(20));
        content.addView(subtitle, subtitleParams);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);

        chatsContainer = new LinearLayout(this);
        chatsContainer.setOrientation(LinearLayout.VERTICAL);
        chatsContainer.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        scroll.addView(chatsContainer);

        content.addView(scroll, new LinearLayout.LayoutParams(
                -1, 0, 1
        ));

        root.addView(content, new LinearLayout.LayoutParams(
                -1, 0, 1
        ));

        root.addView(createBottomNavigation());

        setContentView(root);
    }

    private void loadChats() {

        chatsContainer.removeAllViews();

        TextView loading = text("טוענים את השיחות... 🐾", 16, false);
        loading.setGravity(Gravity.CENTER);
        chatsContainer.addView(loading);

        db.collection("matches")
                .whereArrayContains("users", currentUid)
                .get()
                .addOnSuccessListener(snapshot -> {

                    chatsContainer.removeAllViews();

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
                    chatsContainer.removeAllViews();

                    Toast.makeText(this,
                            "לא הצלחנו לטעון את השיחות",
                            Toast.LENGTH_SHORT).show();
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

                    if (name == null || name.trim().isEmpty()) {
                        name = "כלב חדש";
                    }

                    addChatCard(dogId, name, breed);
                });
    }

    private void addChatCard(
            String dogId,
            String dogName,
            String breed) {

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(dp(15), dp(14), dp(15), dp(14));
        card.setBackground(round(white, 20));
        card.setElevation(dp(3));
        card.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        card.setClickable(true);
        card.setFocusable(true);

        TextView dogIcon = text("🐶", 35, false);
        dogIcon.setGravity(Gravity.CENTER);

        card.addView(dogIcon, new LinearLayout.LayoutParams(
                dp(55), dp(55)
        ));

        LinearLayout details = new LinearLayout(this);
        details.setOrientation(LinearLayout.VERTICAL);
        details.setPadding(dp(12), 0, 0, 0);

        TextView name = text(dogName, 17, true);
        details.addView(name);

        TextView description = text(
                breed == null || breed.trim().isEmpty()
                        ? "ההתאמה שלכם ❤️"
                        : breed + " · ההתאמה שלכם ❤️",
                13, false
        );
        description.setTextColor(muted);
        details.addView(description);

        card.addView(details, new LinearLayout.LayoutParams(
                0, -2, 1
        ));

        TextView arrow = text("‹", 28, true);
        arrow.setTextColor(orange);
        card.addView(arrow);

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(-1, -2);
        cardParams.setMargins(0, 0, 0, dp(12));

        chatsContainer.addView(card, cardParams);

        card.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra("OTHER_UID", dogId);
            intent.putExtra("DOG_NAME", dogName);
            startActivity(intent);
        });
    }

    private void showEmpty() {

        LinearLayout empty = new LinearLayout(this);
        empty.setOrientation(LinearLayout.VERTICAL);
        empty.setGravity(Gravity.CENTER);
        empty.setPadding(dp(25), dp(35), dp(25), dp(35));
        empty.setBackground(round(white, 24));
        empty.setElevation(dp(3));

        TextView icon = text("💬", 48, false);
        icon.setGravity(Gravity.CENTER);
        empty.addView(icon);

        TextView title = text("עדיין אין לכם שיחות", 20, true);
        title.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams titleParams =
                new LinearLayout.LayoutParams(-1, -2);
        titleParams.setMargins(0, dp(10), 0, dp(5));
        empty.addView(title, titleParams);

        TextView message = text(
                "כשתהיה לכם התאמה הדדית,\n" +
                        "תוכלו להתחיל לדבר כאן ❤️",
                14, false
        );
        message.setTextColor(muted);
        message.setGravity(Gravity.CENTER);
        empty.addView(message);

        chatsContainer.addView(empty);
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

        addNavItem(navigation, "❤️", "התאמות", false, () -> {
            startActivity(new Intent(this, MatchesActivity.class));
            finish();
        });

        addNavItem(navigation, "💬", "צ'אטים", true, () -> {
            // כבר נמצאים במסך הצ'אטים
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

        TextView labelText = text(label, 11, selected);
        labelText.setGravity(Gravity.CENTER);
        labelText.setTextColor(selected ? orange : muted);

        LinearLayout.LayoutParams labelParams =
                new LinearLayout.LayoutParams(-1, -2);
        labelParams.setMargins(0, dp(3), 0, 0);
        item.addView(labelText, labelParams);

        item.setOnClickListener(v -> action.run());

        navigation.addView(item, new LinearLayout.LayoutParams(
                0, -2, 1
        ));
    }

    private TextView text(String value, int size, boolean bold) {

        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(dark);
        view.setTextDirection(View.TEXT_DIRECTION_FIRST_STRONG);
        view.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

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
