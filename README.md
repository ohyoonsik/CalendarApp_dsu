# Calendar App - Java + Android Studio

Java와 Android Studio를 사용하여 개발한 완전한 기능의 캘린더 모바일 애플리케이션입니다.

## 🎯 주요 기능

- **월별 캘린더 뷰**: 7x6 그리드로 월의 모든 날짜 표시
- **월 네비게이션**: 이전/다음 월 이동
- **일정 추가/수정/삭제**: 완전한 CRUD 기능
- **날짜별 조회**: 특정 날짜의 모든 일정 표시
- **로컬 데이터베이스**: Room을 사용한 영속성
- **날짜/시간 선택**: DatePickerDialog, TimePickerDialog
- **Material Design UI**: 현대적이고 직관적인 인터페이스

## 📁 프로젝트 구조

```
CalendarApp_Organized/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/calendarapp/
│   │   │   ├── Event.java
│   │   │   ├── EventDao.java
│   │   │   ├── EventDatabase.java
│   │   │   ├── EventRepository.java
│   │   │   ├── DateUtils.java
│   │   │   ├── CalendarUtils.java
│   │   │   ├── EventAdapter.java
│   │   │   ├── CalendarDayAdapter.java
│   │   │   ├── MainActivity.java
│   │   │   ├── CalendarActivity.java
│   │   │   └── EventDetailActivity.java
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   │   ├── activity_main.xml
│   │   │   │   ├── activity_calendar.xml
│   │   │   │   ├── activity_event_detail.xml
│   │   │   │   ├── item_event.xml
│   │   │   │   └── item_calendar_day.xml
│   │   │   └── values/
│   │   │       ├── colors.xml
│   │   │       ├── strings.xml
│   │   │       └── styles.xml
│   │   └── AndroidManifest.xml
│   ├── build.gradle
│   └── proguard-rules.pro
├── build.gradle
├── settings.gradle
├── gradle.properties
└── README.md
```

## 🚀 시작하기

### 1단계: Android Studio에서 프로젝트 열기
- File > Open
- CalendarApp_Organized 폴더 선택
- Open 클릭

### 2단계: Gradle 동기화
- File > Sync Now
- 모든 의존성 다운로드 완료 대기

### 3단계: 에뮬레이터 생성
- Tools > AVD Manager
- Create Virtual Device 클릭
- Android 12 이상 선택

### 4단계: 앱 실행
- Run > Run 'app'
- 에뮬레이터 선택
- OK 클릭

## 🛠️ 기술 스택

| 항목 | 기술 |
|------|------|
| **언어** | Java 11+ |
| **IDE** | Android Studio |
| **API 레벨** | 28+ (권장: 34) |
| **데이터베이스** | Room (SQLite) |
| **UI 프레임워크** | Android XML Layout |
| **아키텍처** | MVVM + Repository |

## 📚 주요 클래스 설명

### 데이터 모델
- **Event.java**: 일정 데이터 모델 (Room Entity)

### 데이터베이스
- **EventDatabase.java**: Room 데이터베이스 정의
- **EventDao.java**: 데이터 접근 객체 (CRUD 쿼리)
- **EventRepository.java**: 데이터 리포지토리 (비동기 작업)

### 유틸리티
- **DateUtils.java**: 날짜 포맷팅, 계산 등
- **CalendarUtils.java**: 캘린더 날짜 생성, 월 정보 등

### UI 어댑터
- **EventAdapter.java**: RecyclerView 어댑터 (일정 목록)
- **CalendarDayAdapter.java**: GridView 어댑터 (캘린더 날짜)

### 액티비티
- **MainActivity.java**: 메인 화면
- **CalendarActivity.java**: 캘린더 메인 화면
- **EventDetailActivity.java**: 일정 상세 화면

## 💡 사용 방법

### 메인 화면
- "캘린더 시작하기" 버튼을 클릭하여 캘린더 앱 시작

### 캘린더 화면
- **월 네비게이션**: 좌우 화살표 버튼으로 이전/다음 월 이동
- **일정 추가**: "+ 추가" 버튼 클릭하여 새 일정 추가

### 일정 추가
1. "+ 추가" 버튼 클릭
2. 다음 정보 입력:
   - **제목**: 일정의 제목 (필수)
   - **설명**: 일정의 상세 설명 (선택)
   - **위치**: 일정의 장소 (선택)
   - **시작 시간**: 날짜 및 시간 선택
   - **종료 시간**: 날짜 및 시간 선택
3. "저장" 버튼 클릭

## ⚙️ 빌드 및 배포

### 디버그 빌드
```bash
./gradlew assembleDebug
```

### 릴리스 빌드
```bash
./gradlew assembleRelease
```

## 🐛 문제 해결

### Gradle 동기화 실패
```
File > Invalidate Caches > Invalidate and Restart
```

### 에뮬레이터 시작 실패
- Tools > AVD Manager에서 에뮬레이터 우클릭
- Wipe Data 클릭
- 다시 시작

---

**즐거운 개발되세요!** 🎉
