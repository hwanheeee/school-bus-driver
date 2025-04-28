package com.example.driverr_bus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;            // Firebase 인증 객체
    private DatabaseReference databaseRef;         // Firebase Realtime Database 참조
    private EditText emailEditText, passwordEditText;  // 입력 필드
    private Button registerButton;                 // 회원가입 버튼

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initializeFirebase();
        initializeViews();
        setButtonListeners();
    }

    // Firebase 인증 및 데이터베이스 객체 초기화
    private void initializeFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference("driverr_bus");
    }

    // 레이아웃 요소 초기화
    private void initializeViews() {
        emailEditText = findViewById(R.id.et_email);
        passwordEditText = findViewById(R.id.et_pwd);
        registerButton = findViewById(R.id.btn_register);
    }

    // 버튼 클릭 리스너 설정
    private void setButtonListeners() {
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });
    }

    // 회원가입 시도 메서드
    private void attemptRegister() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (!validateInput(email, password)) {
            return; // 입력값이 잘못된 경우 중단
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            saveUserToDatabase(password);
                            Toast.makeText(RegisterActivity.this, "회원가입에 성공하셨습니다.", Toast.LENGTH_SHORT).show();
                            navigateToLogin();
                        } else {
                            Toast.makeText(RegisterActivity.this, "회원가입에 실패하셨습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // 입력 유효성 검사
    private boolean validateInput(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
            emailEditText.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
            passwordEditText.requestFocus();
            return false;
        }

        if (!email.contains("@")) {
            Toast.makeText(this, "이메일 형식이 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
            emailEditText.requestFocus();
            return false;
        }

        return true;
    }

    // 사용자 정보를 데이터베이스에 저장
    private void saveUserToDatabase(String password) {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) return;

        // 사용자 계정 정보 객체 생성
        UsesrAccount account = new UsesrAccount();
        account.setIdToken(firebaseUser.getUid());
        account.setEmailId(firebaseUser.getEmail());
        account.setPassword(password);

        // 데이터베이스에 저장
        databaseRef.child("UsesrAccount").child(firebaseUser.getUid()).setValue(account);
    }

    // 로그인 화면으로 이동
    private void navigateToLogin() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
