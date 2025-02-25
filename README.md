#  NBE4-5-1-Team01
- **`팀 명` :** 커피한잔할까요
- **`프로젝트 명` :** Grids&Circles
- **`프로젝트 기간` :** 2025.02.19 - 2025.02.26
- **`한줄 소개` :** Spring을 이용해 커피 메뉴 데이터를 관리하는 4가지 로직 CRUD(Create, Read, Update, Delete)를 구현하는 프로젝트
- **`컨벤션` :** <a href="https://github.com/prgrms-be-devcourse/NBE4-5-1-Team01/wiki/%EA%B9%83-%EC%BB%A8%EB%B0%B4%EC%85%98" target="_blank">🔖 Wiki</a>

--- 

##  Team

| 이태경<br>(팀장) | 소진영<br> | 이은준<br> | 김아성<br> | 김진명<br> | 
| :---: | :---: | :---: | :---:| :---: |
|<a href="https://github.com/dlfjsld1">GitHub</a>|<a href="https://github.com/Jinyoung0718">GitHub</a>|<a href="https://github.com/linedj">GitHub</a>|<a href="https://github.com/asungkim">GitHub</a>|<a href="https://github.com/jin214930">GitHub</a>|

---

## 🚀 **설치 가이드 (Installation Guide)**
### 📌 **1. 프로젝트 클론**
* 먼저 프로젝트를 로컬 환경으로 가져옵니다.
```shell
git clone https://github.com/prgrms-be-devcourse/NBE4-5-1-Team01.git [프로젝트 명]
```
### 📌 2. Backend (Spring Boot) 설정

#### ✅ 2-1. Java & Gradle 환경 설정
- Java 23 설치 확인
- Gradle 설치 확인

#### ✅ 2-2. IntelliJ 설정
- IntelliJ에서 backend 폴더를 엽니다.
    - File > Project Structure > SDK에서 Java 23 설정
    - File > Settings > Build, Execution, Deployment > Build Tools > Gradle 설정

#### ✅ 2-3. Docker 및 MySql 설정
- Docker에서 MySql 컨테이너 실행 및 beanstore 자동 생성
```shell
cd ~ # 운영환경에서는 `cd /`

# 설정파일 만들기
mkdir -p dockerProjects/mysql-1/volumes/etc/mysql/conf.d
mkdir -p dockerProjects/mysql-1/volumes/init

# MySQL 설정파일 생성
chmod 644 dockerProjects/mysql-1/volumes/etc/mysql/conf.d/my.cnf 2>/dev/null
echo "[mysqld]
# general_log = ON
# general_log_file = /etc/mysql/conf.d/general.log" > dockerProjects/mysql-1/volumes/etc/mysql/conf.d/my.cnf
chmod 444 dockerProjects/mysql-1/volumes/etc/mysql/conf.d/my.cnf

# 초기 실행 스크립트 작성 (beanstore DB 자동 생성)
echo "CREATE DATABASE IF NOT EXISTS beanstore;" > dockerProjects/mysql-1/volumes/init/init.sql

# MySQL 컨테이너 실행 (beanstore DB 자동 생성 포함)
docker run \
    --name mysql-1 \
    -p 3306:3306 \
    -v /${PWD}/dockerProjects/mysql-1/volumes/var/lib/mysql:/var/lib/mysql \
    -v /${PWD}/dockerProjects/mysql-1/volumes/etc/mysql/conf.d:/etc/mysql/conf.d \
    -v /${PWD}/dockerProjects/mysql-1/volumes/init:/docker-entrypoint-initdb.d \
    -e TZ=Asia/Seoul \
    -e MYSQL_ROOT_PASSWORD=lldj123414 \
    -d \
    mysql:8.4.1
```

#### ✅ 2-4. Spring Boot 실행
-  인텔리제이에서 BeanstoreApplication 파일 실행


### 📌 3. Front End (Next.js) 설정
#### 1. Visual Studio Code 로 frontend 폴더 실행
#### 2. npm install 
```shell
npm install
```
#### 3. Next.js 실행
```shell
npm run dev
```


---

## Stack
### <span> ⚙️ **Tools** </span>
| Github | Figma | Notion |
| :---: | :---: |:---:|
| <img alt="github logo" src="https://techstack-generator.vercel.app/github-icon.svg" width="65" height="65"> | [![My Skills](https://skillicons.dev/icons?i=figma)](https://skillicons.dev) |<img alt="Notion logo" src="https://www.notion.so/cdn-cgi/image/format=auto,width=640,quality=100/front-static/shared/icons/notion-app-icon-3d.png" height="65" width="65">|

<br />

### <span> 🖥 **Front-end** </span>
| Html | CSS | JavaScript | NextJS | React | TailWind |
| :---: | :---: | :---: | :---: | :---: | :---: |
| <div>[![My Skills](https://skillicons.dev/icons?i=html)](https://skillicons.dev) </div> | <div>[![My Skills](https://skillicons.dev/icons?i=css)](https://skillicons.dev) </div> | <div>[![My Skills](https://skillicons.dev/icons?i=javascript)](https://skillicons.dev) </div> | <div>[![My Skills](https://skillicons.dev/icons?i=nextjs)](https://skillicons.dev) </div> | <div>[![My Skills](https://skillicons.dev/icons?i=react)](https://skillicons.dev) </div> | [![My Skills](https://skillicons.dev/icons?i=tailwind)](https://skillicons.dev)

<br />

### <span>🔒 **Back-end** </span>
| Java | mySQL | Spring | Spring<br>Boot | JWT|Spring<br/>Security | RestAPI |
| :---: | :---: | :---: | :---: | :---: | :---: | :---: |
| <div style="display: flex; align-items: flex-start;"><img src="https://techstack-generator.vercel.app/java-icon.svg" alt="icon" width="65" height="65" /></div> | <div style="display: flex; align-items: flex-start;"><img src="https://techstack-generator.vercel.app/mysql-icon.svg" alt="icon" width="65" height="65" /></div> | <img alt="spring logo" src="https://www.vectorlogo.zone/logos/springio/springio-icon.svg" height="50" width="50" > | <img alt="spring-boot logo" src="https://t1.daumcdn.net/cfile/tistory/27034D4F58E660F616" width="65" height="65" > | <img alt="spring-boot logo" src="https://play-lh.googleusercontent.com/3C-hB-KWoyWzZjUnRsXUPu-bqB3HUHARMLjUe9OmPoHa6dQdtJNW30VrvwQ1m7Pln3A" width="65" height="65" >| <img alt="spring-boot logo" src="https://blog.kakaocdn.net/dn/dIQDQP/btqZ09ESd8T/0ibqtotW52OaJS8HznXDQK/img.png" width="65" height="65" > |  <div style="display: flex; align-items: flex-start;"><img src="https://techstack-generator.vercel.app/restapi-icon.svg" alt="icon" width="65" height="65" /></div> | 

<br />

## 🔖 Project Specification

<ul>
<li><a href="https://www.notion.so/Team-011663550b7b55809b84d3d2c39423fe42">Team Notion Page</a></li>
<li><a href="https://www.figma.com/design/FGVx6IgumxGuYKq3ZlAPeb/DEV_1st_TEAM1?node-id=0-1&t=jHC9UDmAD8KZaQ50-1">기획서 - Figma</a></li>
<li><a href="https://www.notion.so/19f3550b7b558088b067c017e4b7d7e1">기획서 - 노션</a></li>
</ul>

<br />
