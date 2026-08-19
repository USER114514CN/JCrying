@echo off
setlocal enabledelayedexpansion
set ROOT=%~dp0
set OUT=%ROOT%target
set SDK=%ROOT%sdklib\build
set CLASSES=%OUT%\classes
set SRC_ROOT=%ROOT%src\main\java

::清理旧输出
if exist "%SDK%" rmdir /s /q "%SDK%"
if exist "%CLASSES%" rmdir /s /q "%CLASSES%"
mkdir "%CLASSES%"

::===== 编译 sdklib：写入 sources‑sdk.txt 源文件列表 =====
set SDK_LIST=%ROOT%sources-sdk.txt
del /q "%SDK_LIST%" 2>nul
for /r "%ROOT%sdklib\src" %%f in (*.java) do (
    echo %%~ff >> "%SDK_LIST%"
)
if exist "%SDK_LIST%" (
    javac -d "%SDK%" @"%SDK_LIST%"
    if errorlevel 1 (
        del /q "%SDK_LIST%" 2>nul
        exit /b %errorlevel%
    )
)

::===== 收集主项目源码写入 sources‑main.txt =====
set MAIN_LIST=%ROOT%sources-main.txt
del /q "%MAIN_LIST%" 2>nul
for /r "%SRC_ROOT%" %%f in (*.java) do (
    echo %%~ff >> "%MAIN_LIST%"
)
if not exist "%MAIN_LIST%" (
    echo No Java source files found under %SRC_ROOT%
    exit /b 1
)
for %%s in ("%MAIN_LIST%") do if %%~zss equ 0 (
    echo No Java source files found under %SRC_ROOT%
    del /q "%MAIN_LIST%" 2>nul
    exit /b 1
)

javac -cp "%SDK%" -d "%CLASSES%" @"%MAIN_LIST%"
if errorlevel 1 (
    rmdir /s /q "%SDK%"
    del /q "%SDK_LIST%" "%MAIN_LIST%" 2>nul
    exit /b %errorlevel%
)

::打包输出 packcore.jar
jar cf "%OUT%\packcore.jar" -C "%CLASSES%" .
if errorlevel 1 (
    rmdir /s /q "%SDK%"
    del /q "%SDK_LIST%" "%MAIN_LIST%" 2>nul
    exit /b %errorlevel%
)

::清理临时编译产物、临时列表文件
rmdir /s /q "%SDK%"
del /q "%SDK_LIST%" "%MAIN_LIST%" 2>nul

echo Built: %OUT%\packcore.jar
endlocal
