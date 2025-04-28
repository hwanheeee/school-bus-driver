package com.example.driverr_bus;

/**
 * 사용자 계정 정보를 담는 모델 클래스
 */
public class UserAccount {

    private String idToken;    // Firebase Uid (고유 토큰 정보)
    private String emailId;    // 이메일 주소
    private String studentId;  // 학번 (※ password 대신 studentId로 명확히 변경)

    // 기본 생성자 (Firebase 데이터베이스에서 객체 생성 시 필요)
    public UserAccount() {}

    // idToken Getter/Setter
    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    // emailId Getter/Setter
    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    // studentId Getter/Setter
    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
}
