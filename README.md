# NBE4-5-1-Team01  
프로그래머스 백엔드 데브코스 4기 5회차 1차 1팀 프로젝트  

-칸반 보드 활용  
Jira에서 태스크를 관리  
담당자와 진행 상태(To Do, In Progress, Done)를 명확히 표시  

-GitHub Flow  
기본적으로 main 브랜치를 기준으로 진행  
기능 개발 시에는 커밋규칙/기능명 브랜치를 만들어 작업 후, Pull Request를 통해 Merge/Rebase  

<예시>  
최신 원격 저장소에서 pull 받아 브랜치에서 기능 개발 후  
git add. && git commit -m "코멘트"  
git pull origin main --rebase  
(충돌시 해결 후 git add. && git commit -m "코멘트")  
git push origin 브랜치명  
깃허브에서 Compare&Pull Request 생성  
description에 설명 추가  
create pull request 클릭  

*병합시에는 squash merge로 한다*  

각자 로컬에서 충분히 테스트(유닛테스트 및 통합테스트) 후 Merge/Rebase 진행  
Merge/Rebase 후에는 main 브랜치가 정상 구동되는지 꼭 확인  

-커밋 메시지 규칙  
enhancement: 새로운 기능 추가  
bug: 버그 수정  
refactor: 리팩토링  
test: 테스트 코드 추가/보완  
document: 문서 수정  

짧고 명확하게 작성
