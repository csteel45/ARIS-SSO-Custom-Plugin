@echo off

REM ################################## MUST SET THESE VARIABLES ###############
set ARIS_PATH=C:\SoftwareAG\ARIS9.8
REM The runnables usually end in _m, _s, or _l. Change the ABS and UMC runnables to use the right extension.
REM You can check by starting the ARIS Cloud Controller and typing list at the ACC+ prompt. Example: ACC+ localhost> list
set EXT=l
set USER=Clous
set PASS=g3h31m
set PORT=9001 
REM ###########################################################################

set ORIG_PATH=%PATH%
set PATH=%ARIS_PATH%\server\acc;%PATH%

set ABS=abs_%EXT%
set UMC=umcadmin_%EXT%
set COP=copernicus_%EXT%

set "CURRENT_DIR=%CD%"
set SSO_JAR_NAME=y-umc-auth-provider-custom-${project.version}.jar
set SSO_JAR=%CURRENT_DIR%\%SSO_JAR_NAME%
set SSO_PACK=y-umc-auth-provider-custom-${project.version}.jar.pack.gz
set ACTIVE_FLAG=+JAVA-Dcom.aris.umc.sso.plugins.active

set ABS_PATH=%ARIS_PATH%\server\bin\work\work_abs_%EXT%
set UMC_PATH=%ARIS_PATH%\server\bin\work\work_umcadmin_%EXT%

set PROPS_FILE=sso-plugin.properties

set ABS_CFG_DIR=%ABS_PATH%\base\webapps\abs\downloadClient\config
set ABS_LIB_DIR=%ABS_PATH%\base\webapps\abs\downloadClient\lib
set ABS_PROP_DIR=%ABS_PATH%\base\conf
set UMC_PROP_DIR=%UMC_PATH%\base\conf
set ABS_WEBINF=%ABS_PATH%\base\webapps\abs\WEB-INF\lib
set UMC_WEBINF=%UMC_PATH%\base\webapps\umc\WEB-INF\lib

