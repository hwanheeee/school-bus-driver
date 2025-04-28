package com.example.driverr_bus;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ClockActivity extends AppCompatActivity {

    private static final String TAG = "ClockActivity";

    private TextView regBoolTextView;
    private TextView digitalClock;
    private TextView currentDate;
    private DatabaseReference reservationsRef;
    private ValueEventListener valueEventListener;
    private Handler handler = new Handler();
    private boolean isListening = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);

        // 인텐트로부터 데이터 가져오기
        Intent intent = getIntent();
        String reqRoute = patchRoute(intent.getStringExtra("route"));
        String reqTime = intent.getStringExtra("time");

        // Firestore 및 Realtime Database 초기화
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        reservationsRef = FirebaseDatabase.getInstance().getReference().child("reservations");

        // 예약 인원 표시
        setupReservationListener(db, reqRoute, reqTime);

        // 시계, 날짜 초기화
        digitalClock = findViewById(R.id.digitalClock);
        currentDate = findViewById(R.id.currentDate);
        handler.post(updateClockRunnable);

        // 버튼 초기화
        initializeButtons();
    }

    // Intent로 받은 경로 이름 수정 (DB 저장된 형식 맞추기)
    private String patchRoute(String route) {
        if (route == null) return "";

        switch (route) {
            case "A2->안심역->사월역": return "학교->안심역->사월역";
            case "안심역->교내순환": return "안심역 출발";
            case "하양역->교내순환": return "하양역 출발";
            case "사월역->교내순환": return "사월역 출발";
            default: return route;
        }
    }

    // 예약 정보 리스너 설정
    private void setupReservationListener(FirebaseFirestore db, String reqRoute, String reqTime) {
        db.collection("Reservation")
                .whereEqualTo("route", reqRoute)
                .whereEqualTo("time", reqTime)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        List<String> places = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : value) {
                            if (doc.get("place") != null) {
                                places.add(doc.getString("place"));
                            }
                        }

                        regBoolTextView = findViewById(R.id.reg_bool);
                        regBoolTextView.setText(generateReservationSummary(reqRoute, places));
                    }
                });
    }

    // 장소별 인원수 요약 텍스트 생성
    private String generateReservationSummary(String route, List<String> places) {
        StringBuilder output = new StringBuilder();

        switch (route) {
            case "교내 순환":
            case "하양역 출발":
            case "안심역 출발":
            case "사월역 출발":
                output.append(generatePlaceLine(route, places));
                break;
            case "학교->안심역->사월역":
                output.append("• A2                      ").append(Collections.frequency(places, "A2(건너편)")).append(" 명\n\n\n")
                        .append("• 안심역(하차)       ").append(Collections.frequency(places, "안심역")).append(" 명\n\n\n")
                        .append("• 사월역(하차)       ").append(Collections.frequency(places, "사월역")).append(" 명\n");
                break;
            default:
                break;
        }

        return output.toString();
    }

    // 일반 경로용 인원수 요약 생성
    private String generatePlaceLine(String route, List<String> places) {
        String[] locations = {"정문", "B1", "C7", "C13", "D6", "A2(건너편)"};

        StringBuilder result = new StringBuilder();
        if (route.contains("하양역")) {
            result.append("• 하양역            ").append(Collections.frequency(places, "하양역 출발")).append(" 명\n\n");
        }
        if (route.contains("안심역")) {
            result.append("• 안심역            ").append(Collections.frequency(places, "안심역(3번출구)")).append(" 명\n\n");
        }
        if (route.contains("사월역")) {
            result.append("• 사월역            ").append(Collections.frequency(places, "사월역(3번출구)")).append(" 명\n\n");
        }
        for (String location : locations) {
            result.append("• ").append(location).append("               ").append(Collections.frequency(places, location)).append(" 명\n\n");
        }

        return result.toString();
    }

    // 버튼 초기화 메서드
    private void initializeButtons() {
        regBoolTextView = findViewById(R.id.reg_bool);

        Button toggleButton = findViewById(R.id.start_operation);
        toggleButton.setOnClickListener(view -> toggleListening());

        Button backButton = findViewById(R.id.btn_back);
        backButton.setOnClickListener(view -> {
            startActivity(new Intent(ClockActivity.this, route_time.class));
            finish();
        });

        Button logoutButton = findViewById(R.id.logout);
        logoutButton.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(ClockActivity.this, login.class));
            finish();
        });
    }

    // 매초 업데이트하는 Runnable
    private final Runnable updateClockRunnable = new Runnable() {
        @Override
        public void run() {
            updateClockAndDate();
            handler.postDelayed(this, 1000);
        }
    };

    // 시계와 날짜 업데이트
    private void updateClockAndDate() {
        Calendar currentTime = Calendar.getInstance();

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        digitalClock.setText(timeFormat.format(currentTime.getTime()));

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        currentDate.setText(dateFormat.format(currentTime.getTime()));
    }

    // 운행 시작/종료 토글
    private void toggleListening() {
        Button startOperationButton = findViewById(R.id.start_operation);

        if (!isListening) {
            regBoolTextView.setVisibility(View.VISIBLE);
            startDataListener();
            startOperationButton.setText("운행종료");
        } else {
            regBoolTextView.setVisibility(View.INVISIBLE);
            stopDataListener();
            startOperationButton.setText("운행시작");

            startActivity(new Intent(this, route_time.class));
            finish();
        }
    }

    // 예약 데이터 리스너 시작
    private void startDataListener() {
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                TextView subwayTextView = findViewById(R.id.subway);

                if (snapshot.exists()) {
                    String reservedRoute = snapshot.child("route").getValue(String.class);
                    subwayTextView.setText(reservedRoute);
                    regBoolTextView.setText("예약한 사람이 있습니다!");
                } else {
                    regBoolTextView.setText("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ClockActivity.this, "데이터를 읽어올 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        };

        reservationsRef.addValueEventListener(valueEventListener);
        isListening = true;
    }

    // 예약 데이터 리스너 중지
    private void stopDataListener() {
        if (valueEventListener != null) {
            reservationsRef.removeEventListener(valueEventListener);
            valueEventListener = null;
        }
        isListening = false;
    }
}
