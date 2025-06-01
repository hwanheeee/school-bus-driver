# 🚌 School Bus Reservation - 관리자용 앱

## 📱 프로젝트 소개
버스 기사 및 관리자들이 학생들의 예약 현황을 실시간으로 확인하고  
운행 스케줄을 관리할 수 있는 **스쿨버스 운행 관리 앱**입니다.

> **사용자용 앱(userr_bus)과 분리되어 관리자가 운행 상황을 효율적으로 관리할 수 있습니다.**

---

## 🛠️ 사용 기술

- **Android Studio** (Java)
- **Firebase Authentication** - 관리자 로그인 관리
- **Firebase Firestore** - 예약 데이터 실시간 모니터링
- **Firebase Realtime Database** - 실시간 예약자 알림 기능

---

## 🧩 주요 기능

- 🔑 **관리자 로그인 / 회원가입**: Firebase Authentication 기반
- 🚏 **노선 및 시간별 예약자 수 확인**
- 📋 **정류장별 승객 수 집계**: 장소별 탑승 인원 실시간 표시
- ⏰ **디지털 시계 및 현재 날짜 표시**
- 🧹 **운행 종료 버튼**: 운행 완료 후 데이터 초기화
- 🔔 **예약자 발생 시 알림 표시**: 운행 중 예약 발생 감지
- ⬅️ **로그아웃 및 뒤로 가기 버튼 지원**

---

## 📂 프로젝트 구조

/driverr_bus ├── login.java # 관리자 로그인 화면 ├── register.java # 관리자 회원가입 화면 ├── clock.java # 운행 및 예약 관리 메인 화면 ├── route_time.java # 노선과 시간 선택 화면 ├── UsesrAccount.java # 사용자 모델 클래스


---

## ✨ 기타 특징

- ✅ 예약 상황을 실시간으로 모니터링 가능
- ✅ 예약자 수 집계를 Firebase에서 가져와 즉시 반영
- ✅ 운행 종료 시 이전 운행 정보 초기화 가능
- ✅ 시간, 날짜 디스플레이 제공으로 운행 시간 관리에 용이

---

## 🔗 관련 링크

- [사용자용 앱 (userr_bus)](링크 추가 예정)
- [Firebase 공식 문서](https://firebase.google.com/docs)

---

### 🚌 운전자 앱 (Driver App)
![Driver App Screenshot](./screenshots/driverapp.bmp)


---
