@echo off
setlocal enabledelayedexpansion
set ROOT=%~dp0
set OUT=%ROOT%target
set SDK=%ROOT%sdklib\build
set CLASSES=%OUT%\classes
set SRC_ROOT=%ROOT%src\main\java

if exist "%SDK%" rmdir /s /q "%SDK%"
if exist "%CLASSES%" rmdir /s /q "%CLASSES%"
mkdir "%CLASSES%"

set "SDK_FILES="
for /r "%ROOT%sdklib\src" %%f in (*.java) do (
    set "SDK_FILES=!SDK_FILES! "%%~ff""
)
if not "!SDK_FILES!"=="" (
    javac -d "%SDK%" !SDK_FILES!
    if errorlevel 1 exit /b %errorlevel%
)

set "SRC_FILES="
for /r "%SRC_ROOT%" %%f in (*.java) do (
    set "SRC_FILES=!SRC_FILES! "%%~ff""
)
if "!SRC_FILES!"=="" (
    echo No Java source files found under %SRC_ROOT%
    exit /b 1
)

javac -cp "%SDK%" -d "%CLASSES%" !SRC_FILES!
if errorlevel 1 (
    rmdir /s /q "%SDK%"
    exit /b %errorlevel%
)

jar cf "%OUT%\demo-reverse-encoder-1.0.0.jar" -C "%CLASSES%" .
if errorlevel 1 (
    rmdir /s /q "%SDK%"
    exit /b %errorlevel%
)

rmdir /s /q "%SDK%"
echo Built: %OUT%\demo-reverse-encoder-1.0.0.jar
