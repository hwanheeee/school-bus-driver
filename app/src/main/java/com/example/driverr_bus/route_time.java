package com.example.driverr_bus;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RouteTimeActivity extends AppCompatActivity {

    private static final String TAG = "RouteTimeActivity";

    private Spinner firstSpinner, secondSpinner;
    private Button reservationButton, backButton;
    private FirebaseFirestore db;

    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_time);

        initializeFirebase();
        initializeViews();
        setupFirstSpinner();
        setupButtons();
        setupBackPressHandler();
    }

    // Firestore 인스턴스 초기화
    private void initializeFirebase() {
        db = FirebaseFirestore.getInstance();
    }

    // 레이아웃 요소 초기화
    private void initializeViews() {
        firstSpinner = findViewById(R.id.firstSpinner);
        secondSpinner = findViewById(R.id.secondSpinner);
        reservationButton = findViewById(R.id.reservation);
        backButton = findViewById(R.id.btn_back);
    }

    // 첫 번째 스피너 설정
    private void setupFirstSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.first_spinner_items, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        firstSpinner.setAdapter(adapter);

        firstSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                updateSecondSpinner(firstSpinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // 아무것도 선택되지 않았을 때 처리 필요 없음
            }
        });
    }

    // 첫 번째 스피너 선택에 따라 두 번째 스피너 항목 업데이트
    private void updateSecondSpinner(String selectedItem) {
        int arrayResourceId;
        switch (selectedItem) {
            case "교내 순환":
                arrayResourceId = R.array.second_spinner_items_option1;
                break;
            case "하양역->교내순환":
                arrayResourceId = R.array.second_spinner_items_option2;
                break;
            case "안심역->교내순환":
                arrayResourceId = R.array.second_spinner_items_option3;
                break;
            case "사월역->교내순환":
                arrayResourceId = R.array.second_spinner_items_option4;
                break;
            case "A2->안심역->사월역":
                arrayResourceId = R.array.second_spinner_items_option5;
                break;
            default:
                secondSpinner.setAdapter(null);
                return;
        }

        ArrayAdapter<CharSequence> secondAdapter = ArrayAdapter.createFromResource(this,
                arrayResourceId, android.R.layout.simple_spinner_item);
        secondAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        secondSpinner.setAdapter(secondAdapter);
    }

    // 예약, 뒤로가기 버튼 설정
    private void setupButtons() {
        reservationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleReservation();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToLogin();
            }
        });
    }

    // 예약 버튼 클릭 처리
    private void handleReservation() {
        String route = firstSpinner.getSelectedItem() != null ? firstSpinner.getSelectedItem().toString() : "";
        String time = secondSpinner.getSelectedItem() != null ? secondSpinner.getSelectedItem().toString() : "";

        if (route.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "노선과 시간을 모두 선택해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        addReservationToFirestore(route, time);

        Toast.makeText(this, "선택이 완료되었습니다.", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, ClockActivity.class);
        intent.putExtra("route", route);
        intent.putExtra("time", time);
        startActivity(intent);
    }

    // 로그인 화면으로 이동
    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    // 뒤로 가기 버튼 두 번 누르면 앱 종료
    private void setupBackPressHandler() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (doubleBackToExitPressedOnce) {
                    finishAffinity(); // 앱 종료
                } else {
                    doubleBackToExitPressedOnce = true;
                    Toast.makeText(RouteTimeActivity.this, "뒤로가기 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();

                    new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
                }
            }
        });
    }

    // Firestore에 예약 데이터 추가
    private void addReservationToFirestore(String route, String time) {
        Map<String, Object> reservation = new HashMap<>();
        reservation.put("route", route);
        reservation.put("time", time);

        db.collection("bus_reservation")
                .add(reservation)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "예약 추가 성공: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "예약 추가 실패", e);
                    }
                });
    }
}
