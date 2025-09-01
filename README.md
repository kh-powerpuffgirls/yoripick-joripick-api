# 🚀 파워퍼프걸즈 개발 가이드

## 1. 초기 설정

1. **프런트엔드 워크스페이스 클론**  
   - Sourcetree에서 프런트엔드 워크스페이스를 클론
   - `VSCode`에서 해당 폴더 열고 터미널 경로 확인 후 node_modules 설치
     ```bash
     npm i
     ```

2. **백엔드 워크스페이스 클론 및 Import**  
   - `STS4` 실행 → `yoripick-joripick-api` 워크스페이스 클론  
   - `Import > Git > Projects from Git (with smart import) > Clone URI > 깃허브 주소 붙여넣기 > 디렉토리 경로 설정 > Finish`

3. **Maven 프로젝트 변환**  
   - 프로젝트 우클릭 → `Configure > Convert to Maven Project`

4. **환경 설정 파일 추가**  
   - `src/main/resources` 폴더에 전달받은 `application.properties` 파일 추가

5. **Sourcetree 연동**  
   - Sourcetree → `Add` → 로컬 디렉토리 경로 탐색 후 추가

6. **DB 계정 생성**  
   - `SQL Developer` 실행 → **시스템 관리자 계정** 로그인  
   - 전달받은 **계정 생성 스크립트** 실행

## 2. 프로젝트 실행 순서

1. **DB 접속**  
   - 계정 생성 후, 새로 생성된 계정으로 `localhost:1521` 접속

2. **SQL 스크립트 실행**  
   - 최신 SQL 스크립트 실행

3. **백엔드 실행**  
   - `STS4`에서 `yoripick-joripick-api` 프로젝트 실행

4. **프런트엔드 실행**  
   - `VSCode` 실행 후 `ypjp-workspace/yoripick-joripick` 폴더 열기  
   - 터미널에서 해당 폴더 경로 확인 후 실행:
     ```bash
     npm run dev
     ```

👉 **필요한 파일은 Slack에서 확인 가능합니다:**  
[📂 Slack 파일 공유 바로가기](https://app.slack.com/client/T08N3QD6PC5/C09AU3H2JJX)

## 3. 주의사항

- 작업 전 반드시 **현재 브랜치 확인 (Sourcetree)**
- 커밋 후에는 **항상 메인 브랜치 pull/merge** → 충돌 여부 확인 → 문제 없을 시 push
- 브랜치는 **이슈별로 GitHub에서 원격 브랜치 생성** 후, Sourcetree에서 해당 원격 브랜치를 기준으로 **로컬 브랜치 생성**
