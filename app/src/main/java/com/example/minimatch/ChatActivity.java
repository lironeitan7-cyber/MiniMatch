
package com.example.minimatch;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends Activity {

    private final int background = Color.rgb(250, 246, 239);
    private final int orange = Color.rgb(235, 137, 65);
    private final int dark = Color.rgb(57, 43, 34);
    private final int muted = Color.rgb(135, 119, 106);
    private final int white = Color.WHITE;

    private FirebaseFirestore db;
    private String currentUid;
    private String otherUid;
    private String otherDogName;
    private String chatId;

    private LinearLayout messagesContainer;
    private ScrollView messagesScroll;
    private EditText messageInput;

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
        otherUid = getIntent().getStringExtra("OTHER_UID");
        otherDogName = getIntent().getStringExtra("DOG_NAME");

        if (otherUid == null || otherUid.trim().isEmpty()) {
            Toast.makeText(this, "לא נמצא משתמש לשיחה", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (otherDogName == null || otherDogName.trim().isEmpty()) {
            otherDogName = "החבר החדש";
        }

        chatId = currentUid.compareTo(otherUid) < 0
                ? currentUid + "_" + otherUid
                : otherUid + "_" + currentUid;

        db = FirebaseFirestore.getInstance();

        getWindow().setStatusBarColor(background);
        getWindow().setNavigationBarColor(background);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR |
                        View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        );

        createScreen();
        listenForMessages();
    }

    private void createScreen() {

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(background);
        root.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(18), dp(15), dp(18), dp(15));
        header.setBackgroundColor(white);
        header.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        TextView back = text("‹", 34, true);
        back.setGravity(Gravity.CENTER);
        back.setTextColor(orange);
        back.setOnClickListener(v -> finish());

        header.addView(back, new LinearLayout.LayoutParams(
                dp(40), dp(45)
        ));

        LinearLayout nameBox = new LinearLayout(this);
        nameBox.setOrientation(LinearLayout.VERTICAL);
        nameBox.setPadding(dp(8), 0, 0, 0);

        TextView name = text(otherDogName, 19, true);
        nameBox.addView(name);

        TextView subtitle = text("ההתאמה החדשה שלכם ❤️", 12, false);
        subtitle.setTextColor(muted);
        nameBox.addView(subtitle);

        header.addView(nameBox);

        root.addView(header);

        messagesScroll = new ScrollView(this);
        messagesScroll.setFillViewport(true);
        messagesScroll.setClipToPadding(false);
        messagesScroll.setPadding(dp(12), dp(12), dp(12), dp(12));

        messagesContainer = new LinearLayout(this);
        messagesContainer.setOrientation(LinearLayout.VERTICAL);
        messagesContainer.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        messagesScroll.addView(messagesContainer);

        root.addView(messagesScroll, new LinearLayout.LayoutParams(
                -1, 0, 1
        ));

        LinearLayout inputArea = new LinearLayout(this);
        inputArea.setOrientation(LinearLayout.HORIZONTAL);
        inputArea.setGravity(Gravity.CENTER_VERTICAL);
        inputArea.setPadding(dp(10), dp(10), dp(10), dp(10));
        inputArea.setBackgroundColor(white);
        inputArea.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        messageInput = new EditText(this);
        messageInput.setHint("כתבו הודעה...");
        messageInput.setTextSize(15);
        messageInput.setTextColor(dark);
        messageInput.setHintTextColor(muted);
        messageInput.setSingleLine(true);
        messageInput.setPadding(dp(15), dp(10), dp(15), dp(10));
        messageInput.setBackground(round(
                Color.rgb(250, 246, 239), 25
        ));
        messageInput.setTextDirection(View.TEXT_DIRECTION_FIRST_STRONG);
        messageInput.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        inputArea.addView(messageInput, new LinearLayout.LayoutParams(
                0, dp(48), 1
        ));

        TextView send = text("➤", 22, true);
        send.setTextColor(white);
        send.setGravity(Gravity.CENTER);
        send.setBackground(round(orange, 30));

        LinearLayout.LayoutParams sendParams =
                new LinearLayout.LayoutParams(dp(48), dp(48));
        sendParams.setMargins(dp(8), 0, 0, 0);

        inputArea.addView(send, sendParams);
        send.setOnClickListener(v -> sendMessage());

        root.addView(inputArea);

        setContentView(root);
    }

    private void listenForMessages() {

        db.collection("chats")
                .document(chatId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshot, error) -> {

                    if (error != null) {
                        Toast.makeText(this,
                                "שגיאה בטעינת ההודעות",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (snapshot == null) {
                        return;
                    }

                    messagesContainer.removeAllViews();

                    for (DocumentSnapshot document : snapshot.getDocuments()) {

                        String sender = document.getString("senderId");
                        String message = document.getString("message");

                        if (message == null || sender == null) {
                            continue;
                        }

                        addMessage(message, sender.equals(currentUid));
                    }

                    messagesScroll.post(() ->
                            messagesScroll.fullScroll(View.FOCUS_DOWN));
                });
    }

    private void sendMessage() {

        String message = messageInput.getText().toString().trim();

        if (message.isEmpty()) {
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("senderId", currentUid);
        data.put("receiverId", otherUid);
        data.put("message", message);
        data.put("timestamp",
                com.google.firebase.firestore.FieldValue.serverTimestamp());

        db.collection("chats")
                .document(chatId)
                .collection("messages")
                .add(data)
                .addOnSuccessListener(documentReference -> {

                    messageInput.setText("");

                    InputMethodManager keyboard =
                            (InputMethodManager) getSystemService(
                                    Context.INPUT_METHOD_SERVICE);

                    if (keyboard != null) {
                        keyboard.hideSoftInputFromWindow(
                                messageInput.getWindowToken(), 0);
                    }
                })
                .addOnFailureListener(error ->
                        Toast.makeText(this,
                                "שליחת ההודעה נכשלה",
                                Toast.LENGTH_SHORT).show());
    }

    private void addMessage(String message, boolean mine) {

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(mine ? Gravity.LEFT : Gravity.RIGHT);
        row.setPadding(0, dp(4), 0, dp(4));

        TextView bubble = text(message, 15, false);
        bubble.setTextColor(mine ? white : dark);
        bubble.setPadding(dp(15), dp(10), dp(15), dp(10));
        bubble.setMaxWidth(dp(270));
        bubble.setBackground(round(
                mine ? orange : white, 18
        ));

        row.addView(bubble);

        messagesContainer.addView(row, new LinearLayout.LayoutParams(
                -1, -2
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
}
