# 🟧 Spring Security + JWT 백엔드 템플릿

---

## 🟧 목차
1. [파일 구조](#파일-구조)
2. [기술 스택](#기술-스택)
3. [API](#api)
4. [인증 - 인가](#인증---인가)
5. [로깅 설정 (Logback)](#로깅-설정-logback)
6. [예외 처리 구조](#예외-처리-구조)
7. [EC2 자동 배포 스크립트](#ec2-자동-배포-스크립트-deploysh)
8. [Swagger 연동](#swagger-연동)

---
## 🟧 파일 구조

```markdown
src
└── main
    └── java
        └── lcy.jwt
            ├── application                  # 서비스 계층
            ├── config                       # 글로벌 설정
            ├── domain                       # 도메인 모델
            ├── dto                          # 데이터 전송 객체
            ├── exception                    # 커스텀 예외, 공통 예외 처리
            ├── infra                        # 외부 시스템 구현체 (Repository 구현체)
            ├── security                     
            │   ├── AuthUser.java                    # 인증된 사용자 정보를 담는 객체
            │   ├── CustomAccessDeniedHandler.java   # 인가 실패(403) 핸들러
            │   ├── CustomAuthEntryPoint.java        # 인증 실패(401) 핸들러
            │   ├── JwtAuthenticationFilter.java     # JWT 토큰 검증 및 인증 처리 필터
            │   ├── JwtAuthenticationToken.java      # JWT 인증 토큰 객체
            │   ├── JwtUtil.java                     # JWT 생성 및 검증 유틸
            │   └── SecurityConfig.java              # Spring Security 설정 클래스
            ├── ui.api                       # API 엔드포인트 (Controller)
            ├── utils                        # 공통 유틸 클래스
            └── JwtApplication.java
```

---

## 🟧 기술 스택
| 분야 | 내용                           |
|-----|------------------------------|
| 언어 | Java 17                      |
| 프레임워크 | Spring Boot 3.x              |
| 인증/인가 | Spring Security + JWT        |
| 문서화 | Swagger (springdoc-openapi)  |
| 로깅 | Logback (logback-spring.xml) |
| 빌드 | Gradle 8.x                   |
| 배포 | AWS EC2 + Bash 스크립트          |
| 테스트 | JUnit                        |

---

## 🟧 API

### Auth API
| 메서드 | 경로 | 설명 |
|--------|------|------|
| POST | /api/auth/register | 사용자 회원가입 |
| POST | /api/auth/register/admin | 관리자 회원가입 (secret code 필요) |
| POST | /api/auth/login | 로그인 후 JWT 토큰 발급 |
### ADMIN API
| 메서드 | 경로 | 설명 |
|--------|------|------|
| PATCH | /api/admin/users/{userId}/roles | 사용자에게 관리자 권한 부여 |

---
## 🟧 인증 - 인가

- JWT 기반 무상태 Stateless 인증
- SecurityContextHolder를 통해 현재 사용자 정보 저장 및 전달
- @Secured("ROLE_ADMIN") 으로 관리자 API 보호
- 모든 요청은 JwtAuthenticationFilter를 통해 JWT 검증

### JWT 검증 로직 (`JwtAuthenticationFilter`)

- OncePerRequestFilter 구현
- URI가 화이트리스트에 포함되어 있으면 필터를 건너뜀 (`shouldNotFilter()`)
- JWT 추출 후 사용자 ID와 Role 기반으로 AuthUser 객체 생성
- JwtAuthenticationToken 생성 후 SecurityContextHolder 에 저장
```java
String header = request.getHeader("Authorization");
if (header.startsWith("Bearer ")) {
  String token = jwtUtil.substringToken(header);
  Claims claims = jwtUtil.extractClaims(token);
  AuthUser authUser = AuthUser.of(userId, userRole);
  SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(authUser));
}
```

### SecurityConfig 주요 설정
- 세션을 생성하지 않는 Stateless 전략 사용
- 필터 체인 커스터마이징, formLogin, httpBasic, logout 등은 모두 disable
- 화이트리스트 URL은 permitAll(), 관리자 전용은 hasAuthority("ROLE_ADMIN")

---


## 🟧 로깅 설정 (Logback)

### 환경별 출력 분리
```xml
<springProfile name="local">
  <root level="INFO">
    <appender-ref ref="CONSOLE"/>
  </root>
</springProfile>

<springProfile name="deploy">
  <root level="INFO">
    <appender-ref ref="CONSOLE"/>
    <appender-ref ref="FILE"/>
  </root>
</springProfile>
```

- 로그 파일 경로: /home/ubuntu/app/app.log
- 콘솔 + 파일 동시 기록은 deploy 프로필에서만 적용

---

## 🟧 예외 처리 구조

### 에러 응답 포맷 (공통)
```json
{
  "status": 401,
  "error": "UnAuthorized",
  "code": "au401",
  "message": "패스워드가 일치하지 않습니다."
}
```

### 커스텀 예외 구조
- BaseException → CustomException
- ErrorCode enum 으로 도메인별 코드 정리
- GlobalExceptionHandler에서 일괄 처리

---

## 🟧 EC2 자동 배포 스크립트 (deploy.sh)

### 주요 흐름
1. 보안 그룹 생성 및 포트(8080, 22) 오픈
2. EC2 인스턴스 생성 (Temurin JDK 17, Gradle 설치 포함)
3. GitHub에서 소스 클론 및 gradle build -x test
4. 백그라운드에서 JAR 실행 (nohup)

### 실행 결과 예시
```
EC2(i-xxxxxxxx) 준비 완료 http://ec2-xx-xxx-xxx-xxx.compute.amazonaws.com:8080
```

---

## 🟧 Swagger 연동
- @Operation, @ApiResponses 등으로 문서화
- JWT 토큰 입력 후 테스트 가능
- 예외 응답 Swagger 상에서 스키마로 명시

---

