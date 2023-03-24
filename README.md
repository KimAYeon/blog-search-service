# blog-search-service

# 환경 구성
  - Spring Boot 2.7.9
  - Java 11
  - H2 innoDB
  - Spring Data JPA
  - JUnit 5

# 패키지 구성
  - blog-search-service(root) : 프로젝트 환경 설정 (lombok, test 등)
    - api-module : API 구성을 위한 코드
    - core-module : 공통으로 사용되는 코드, 현재 DB 설정 및 JPA Entity, Repository 정의

# 외부 라이브러리 및 오픈소스 사용
  - modelmapper : 객체 간의 필드 매핑 기능으로 소스 코드 간결화
  - webflux : HTTP 클라이언트로 WebClient를 사용하여 Open API 호출
  - springdoc-openapi-ui : Swagger ui를 사용하여 REST API 명세를 시각화 및 문서화 (http://localhost:8081/api-docs/swagger-ui/index.html#/)
  - spring-boot-starter-validation : REST API 파라미터의 유효성 검사 및 Exception 처리
  - jacoco : 코드 커버리지 측정 및 보고서 생성 (http://localhost:63342/blog-search-service/blog-search-service.api-module/build/reports/jacoco/test/html/index.html)
  
# 실행 JAR 파일 경로
  - https://drive.google.com/file/d/1rAFgkxpBFh6byM_Z_SdY-CcqhfF1xQKg/view?usp=sharing
