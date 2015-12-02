@echo off & setlocal enabledelayedexpansion

set TITLE=Pusher Console
set MAIN_CLASS=cn.brent.pusher.ServerRunner
set JAVA_OPTS=-Xms64m -Xmx512m -XX:MaxNewSize=128m -XX:MaxPermSize=64m

set CURRENT_DIR=%cd%
cd ..
set DEPLOY_HOME=%cd%
cd %CURRENT_DIR%

if "%JAVA_HOME%" == "" goto noJavaHome

echo Using DEPLOY_HOME:    %DEPLOY_HOME%
echo Using JAVA_HOME:      %JAVA_HOME%

set _RUNJAVA="%JAVA_HOME%\bin\java"

if "%TITLE%" == "" set TITLE=Console
set _EXECJAVA= %_RUNJAVA%

set LIB_JARS=""

cd ..\lib
for %%i in (*) do set LIB_JARS=!LIB_JARS!;%DEPLOY_HOME%\lib\%%i
cd ..

%_EXECJAVA% %JAVA_OPTS% -classpath %DEPLOY_HOME%\conf;%LIB_JARS% %MAIN_CLASS%
goto end

:noJavaHome
echo The JAVA_HOME environment variable is not defined correctly
echo This environment variable is needed to run this program
goto end

:end