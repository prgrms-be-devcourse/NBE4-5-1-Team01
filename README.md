백엔드 데브코스 4기 5회차 1차 1팀 프로젝트

프로젝트 기간

2/19 ~ 2/26 | 2/27 발표

매일 5시 30분 프로젝트 회의 진행

회의 때 진행한 이슈 보고 및 도움 여부, 다음 이슈 지정

✅ 칸반 보드 활용

태스크에서 자신이 맡은 이슈를 생성

담당자와 진행 상태 (To Do, In Progress, Done)를 명확히 표시

🔄 GitHub Flow

기본적으로 main 브랜치를 기준으로 진행

기능 개발 시에는 프로젝트의 이슈를 통해 커밋규칙/기능명 브랜치를 만들어 작업 후, Pull Request를 통해 Merge/Rebase

<예시>

git pull origin main
# 브랜치에서 기능 개발 후

git add . && git commit -m "코멘트"
git pull origin main --rebase

# 충돌 발생 시 해결 후

git add . && git commit -m "코멘트"
git push origin 브랜치명

GitHub에서 Compare & Pull Request 생성

description에 설명 추가 후 Create Pull Request 클릭

병합 시에는 squash merge로 진행

각자 로컬에서 충분히 테스트(유닛 테스트 및 통합 테스트) 후 Merge/Rebase 진행

Merge/Rebase 후 main 브랜치 정상 구동 여부 반드시 확인

📌 커밋 메시지 규칙

타입

설명

enhancement

새로운 기능 추가

bug

버그 수정

refactor

리팩토링

test

테스트 코드 추가/보완

document

문서 수정

✍️ 짧고 명확하게 작성

