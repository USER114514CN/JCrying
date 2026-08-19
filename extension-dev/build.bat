@echo off
setlocal enabledelayedexpansion
set ROOT=%~dp0
set OUT=%ROOT%target
set SDK=%ROOT%sdklib\build
set CLASSES=%OUT%\classes
set SRC_ROOT=%ROOT%src\main\java
r
if exist "%SDK%" rmdir /s /q "%SDK%"
if exist "%CLASSES%" rmdir /s /q "%CLASSES%"
mkdir "%CLASSES%"

rem temporary sources list files
set SDK_SOURCES=%OUT%\sdk-sources.txt
set SRC_SOURCES=%OUT%\sources.txt
nrem build SDK (if any)
if exist "%SDK_SOURCES%" del /q "%SDK_SOURCES%"
for /r "%ROOT%sdklib\src" %%f in (*.java) do (
    echo %%~ff >> "%SDK_SOURCES%"
)
if exist "%SDK_SOURCES%" (
    javac -d "%SDK%" @"%SDK_SOURCES%"
    if errorlevel 1 exit /b %errorlevel%
)
nrem build main sources
if exist "%SRC_SOURCES%" del /q "%SRC_SOURCES%"
for /r "%SRC_ROOT%" %%f in (*.java) do (
    echo %%~ff >> "%SRC_SOURCES%"
)
if not exist "%SRC_SOURCES%" (
    echo No Java source files found under %SRC_ROOT%
    if exist "%SDK%" rmdir /s /q "%SDK%"
    exit /b 1
)

javac -cp "%SDK%" -d "%CLASSES%" @"%SRC_SOURCES%"
if errorlevel 1 (
    if exist "%SDK%" rmdir /s /q "%SDK%"
    del /q "%SDK_SOURCES%" >nul 2>&1
    del /q "%SRC_SOURCES%" >nul 2>&1
    exit /b %errorlevel%
)
nrem package jar with fixed name packcore.jar
jar cf "%OUT%\packcore.jar" -C "%CLASSES%" .
if errorlevel 1 (
    if exist "%SDK%" rmdir /s /q "%SDK%"
    del /q "%SDK_SOURCES%" >nul 2>&1
    del /q "%SRC_SOURCES%" >nul 2>&1
    exit /b %errorlevel%
)

rmdir /s /q "%SDK%"
del /q "%SDK_SOURCES%" >nul 2>&1
del /q "%SRC_SOURCES%" >nul 2>&1
echo Built: %OUT%\packcore.jar
