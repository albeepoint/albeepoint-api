# albeepoint-api

`albeepoint-api` 는 별도 저장소/프로젝트로 운용되는 Spring Boot API 서버 예제입니다.

## 기준 버전
- JDK 21
- Spring Boot 3.4.5
- Maven
- MyBatis Spring Boot Starter 4.0.0

## 전제 조건
이 프로젝트는 아래 라이브러리를 Maven 저장소에서 가져옵니다.

- `com.albee:albeepoint-mapper:0.0.1-SNAPSHOT`

즉, 먼저 `albeepoint-mapper` 를 로컬 저장소 또는 사내 Maven 저장소(Nexus / Artifactory / GitLab Package Registry 등)에 올려야 합니다.

### 로컬 개발 시
`albeepoint-mapper` 프로젝트에서 먼저 실행:

```bash
mvn clean install
```

그 다음 이 프로젝트에서:

```bash
mvn clean package
mvn spring-boot:run
```

## 주요 설정
- MapperScan: `com.albee.albeepoint.mapper`
- mapper XML classpath: `classpath*:com/albee/albeepoint/mapper/**/*.xml`

## 실행 확인
```bash
curl http://localhost:8080/api/health
```

## 회사/팀 분리형 운영 방식
- `albeepoint-mapper` : 별도 저장소에서 빌드/배포하는 라이브러리
- `albeepoint-api` : `pom.xml` dependency 로 mapper 가져와서 사용하는 실행 서버

## 사내 저장소 사용 예시
필요하면 `pom.xml` 에 repository 를 추가해서 사내 저장소에서 mapper 를 가져오도록 바꿀 수 있습니다.

```xml
<repositories>
    <repository>
        <id>company-releases</id>
        <url>https://your-company-repo.example.com/maven/releases</url>
    </repository>
    <repository>
        <id>company-snapshots</id>
        <url>https://your-company-repo.example.com/maven/snapshots</url>
    </repository>
</repositories>
```
