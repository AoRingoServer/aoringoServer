@echo off

REM 日付と時刻を取得
for /f "delims=" %%a in ('wmic OS Get localdatetime ^| find "."') do set datetime=%%a

REM 日付と時刻のフォーマットを変更
set year=%datetime:~0,4%
set month=%datetime:~4,2%
set day=%datetime:~6,2%
set hour=%datetime:~8,2%
set minute=%datetime:~10,2%
set second=%datetime:~12,2%

REM コミットメッセージを生成
set commit_message=%day%.%month%.%year%.%hour%

REM Gitコマンドを実行
git add .
git commit -m "%commit_message%"
git push
