@echo off

call setenv.bat

call acc -h localhost -u %USER% -pwd %PASS% -p %PORT% stop %ABS%
call acc -h localhost -u %USER% -pwd %PASS% -p %PORT% stop %UMC%

del %ABS_WEBINF%\%SSO_JAR_NAME%
del %UMC_WEBINF%\%SSO_JAR_NAME%

REM Restore original arisloader.cfg config file.
copy /Y %ABS_CFG_DIR%\arisloader.orig %ABS_CFG_DIR%\arisloader.cfg

REM Restore client.zip
copy /Y %ABS_LIB_DIR%\client.orig %ABS_LIB_DIR%\client.zip

REM Delete extraneous properties file
del %ABS_PROP_DIR%/sso-plugin.properties
del %UMC_PROP_DIR%/sso-plugin.properties

call acc -h localhost -u %USER% -pwd %PASS% -p %PORT% start %ABS%
call acc -h localhost -u %USER% -pwd %PASS% -p %PORT% start %UMC%

call acc -h localhost -u %USER% -pwd %PASS% -p %PORT% reconfigure %ABS% %ACTIVE_FLAG%=false
call acc -h localhost -u %USER% -pwd %PASS% -p %PORT% reconfigure %UMC% %ACTIVE_FLAG%=false
REM call acc -h localhost -u %USER% -pwd %PASS% -p %PORT% reconfigure %COP% %ACTIVE_FLAG%=false

set PATH=%ORIG_PATH%

echo SSO Uninstall complete.

:exit
exit /B
