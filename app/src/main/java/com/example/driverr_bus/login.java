package com.example.driverr_bus;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseRef;

    private EditText emailEditText, passwordEditText;
    private Button loginButton, registerButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeFirebase();
        initializeViews();
        setButtonListeners();
    }

    // Firebase 인증 및 데이터베이스 초기화
    private void initializeFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference();
    }

    // 레이아웃 요소 초기화
    private void initializeViews() {
        emailEditText = findViewById(R.id.et_email);
        passwordEditText = findViewById(R.id.et_pwd);
        loginButton = findViewById(R.id.btn_login);
        registerButton = findViewById(R.id.btn_register);
    }

    // 버튼 클릭 리스너 설정
    private void setButtonListeners() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToRegister();
            }
        });
    }

    // 로그인 시도 메서드
    private void attemptLogin() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (!validateInput(email, password)) {
            return; // 입력값이 잘못된 경우 중단
        }

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            saveUserEmail(email);
                            Toast.makeText(getApplicationContext(), "로그인에 성공하셨습니다.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, route_time.class));
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "로그인에 실패하였습니다. 정확한 이메일과 비밀번호를 작성해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // 이메일 저장 (SharedPreferences 사용)
    private void saveUserEmail(String email) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyApp", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userEmail", email);
        editor.apply();
    }

    // 입력 유효성 검사
    private boolean validateInput(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!email.contains("@")) {
            Toast.makeText(getApplicationContext(), "이메일 형식이 아닙니다.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    // 회원가입 화면으로 이동
    private void navigateToRegister() {
        Intent intent = new Intent(LoginActivity.this, register.class);
        startActivity(intent);
    }
}
