@echo off

REM 現在の日時を取得
for /f "delims=" %%a in ('wmic OS Get localdatetime ^| find "."') do set datetime=%%a

REM 年月日と時刻のフォーマットを定義
set "year=%datetime:~0,4%"
set "month=%datetime:~4,2%"
set "day=%datetime:~6,2%"
set "hour=%datetime:~8,2%"
set "minute=%datetime:~10,2%"

REM コミットメッセージを生成
set "commit_message=Click Push %year%-%month%-%day% at %hour%:%minute%"

REM git add, commit, push を実行
git add .
git commit -m "%commit_message%"
if errorlevel 1 (
    echo Error
) else (
    git push
    if errorlevel 1 (
        echo Error
    ) else (
        echo Success
    )
)