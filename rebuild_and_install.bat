@echo off
setlocal enabledelayedexpansion

REM ========================================
REM Initialize device priorities
REM ========================================
set "DEVICE_PRIORITIES[0]=2B16166D4AA2EB"
set "DEVICE_PRIORITIES[1]=24191FDF600130"
set "DEVICE_PRIORITIES[2]=dc678e7f"
set "DEVICE_PRIORITIES[3]=127.0.0.1:10114"
set "DEVICE_COUNT=4"

REM ========================================
REM Connect and select device
REM ========================================
call :SelectDevice
if "!SELECTED_DEVICE!"=="" (
    set "ADB_CMD=adb"
    echo Using default device
) else (
    set "ADB_CMD=adb -s !SELECTED_DEVICE!"
    echo Using selected device: !SELECTED_DEVICE!
)

echo ========================================
echo Rebuilding and Installing Hook Module
echo ========================================

echo.
echo [1/5] Stopping target app...
!ADB_CMD! shell am force-stop com.ss.android.ugc.aweme
if !errorlevel! equ 0 (
    echo Successfully stopped com.ss.android.ugc.aweme
) else (
    echo App not running or stop failed, continuing...
)

echo.
echo [2/5] Uninstalling old APK...
!ADB_CMD! uninstall com.hook 2>nul
if !errorlevel! equ 0 (
    echo Successfully uninstalled
) else (
    echo App not installed or uninstall failed, continuing...
)

echo.
echo [3/4] Building APK...
call gradlew.bat hook:assembleDebug
set BUILD_ERROR=!errorlevel!
if !BUILD_ERROR! neq 0 (
    echo Error: Build failed!
    pause
    exit /b 1
)
echo Build completed successfully

echo.
echo [4/4] Installing APK...
!ADB_CMD! install -r hook\build\outputs\apk\debug\hook-debug.apk
set INSTALL_ERROR=!errorlevel!
if !INSTALL_ERROR! neq 0 (
    echo Error: Installation failed!
    pause
    exit /b 1
)

echo.
echo ========================================
echo Successfully rebuilt and installed!
echo ========================================
pause
exit /b 0

REM ========================================
REM Function: SelectDevice
REM Try to connect and select device by priority
REM Returns: SELECTED_DEVICE variable
REM ========================================
:SelectDevice
set "SELECTED_DEVICE="

REM Try to connect to network devices first
echo Attempting to connect to network devices...
for /L %%i in (0,1,2) do (
    set "DEVICE=!DEVICE_PRIORITIES[%%i]!"
    echo !DEVICE! | findstr /C:":" >nul
    if !errorlevel! equ 0 (
        echo Trying to connect to !DEVICE!...
        adb connect !DEVICE! >nul 2>&1
    )
)

echo.
echo Checking available devices...
adb devices >nul 2>&1

REM Check each priority device
for /L %%i in (0,1,!DEVICE_COUNT!) do (
    if %%i lss !DEVICE_COUNT! (
        set "DEVICE=!DEVICE_PRIORITIES[%%i]!"

        REM Check if device is online
        for /f "tokens=2" %%d in ('adb devices ^| findstr /C:"!DEVICE!"') do (
            if "%%d"=="device" (
                set "SELECTED_DEVICE=!DEVICE!"
                goto :DeviceFound
            )
        )
    )
)

:DeviceFound
if "!SELECTED_DEVICE!"=="" (
    echo No priority devices found, using default device
) else (
    echo Found priority device: !SELECTED_DEVICE!
)
exit /b 0