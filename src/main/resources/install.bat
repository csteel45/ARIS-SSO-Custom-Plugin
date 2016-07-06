@echo off
call setenv.bat
@echo on

call acc -h localhost -u %USER% -pwd %PASS% -p %PORT% stop %ABS%
call acc -h localhost -u %USER% -pwd %PASS% -p %PORT% stop %UMC%

call acc -h localhost -u %USER% -pwd %PASS% enhance %ABS% with webappsClasspath local file "%SSO_JAR%"
call acc -h localhost -u %USER% -pwd %PASS% enhance %UMC% with webappsClasspath local file "%SSO_JAR%"

call acc -h localhost -u %USER% -pwd %PASS% -p %PORT% start %ABS%
call acc -h localhost -u %USER% -pwd %PASS% -p %PORT% start %UMC%

call acc -h localhost -u %USER% -pwd %PASS% -p %PORT% reconfigure %ABS% %ACTIVE_FLAG%=true
call acc -h localhost -u %USER% -pwd %PASS% -p %PORT% reconfigure %UMC% %ACTIVE_FLAG%=true
REM call acc -h localhost -u %USER% -pwd %PASS% -p %PORT% reconfigure %COP% %ACTIVE_FLAG%=true

REM Now update the download client

REM Backup original arisloader.cfg config file. If prompted during installation to overwrite, select No.
copy /-Y %ABS_CFG_DIR%\arisloader.cfg %ABS_CFG_DIR%\arisloader.orig

REM Copy updated config file to config directory and overwrite existing file.
copy /Y/V arisloader.cfg %ABS_CFG_DIR%
copy /Y/V sso-plugin.properties %ABS_PROP_DIR%
copy /Y/V sso-plugin.properties %UMC_PROP_DIR%

REM Backup client.zip
copy /-Y %ABS_LIB_DIR%\client.zip %ABS_LIB_DIR%\client.orig

REM Update client.zip with SSO pack file
call jar uvfM %ABS_LIB_DIR%\client.zip %SSO_PACK%
REM call java -jar %SSO_JAR_NAME% %ABS_LIB_DIR%/client.zip %SSO_PACK%

set PATH=%ORIG_PATH%
set ORIG_PATH=

echo SSO Installation complete.

:exit
exit /B
