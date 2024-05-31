chcp 65001
pushd %~dp0
call mvn clean install
call docker-compose up -d --build
pause