@if "%DEBUG%" == "" @echo off

setlocal enabledelayedexpansion

set DIRNAME=%~s1
shift

set CLASS=%~1
shift

@rem Determine the command interpreter to execute the "CD" later
set COMMAND_COM="cmd.exe"
if exist "%SystemRoot%\system32\cmd.exe" set COMMAND_COM="%SystemRoot%\system32\cmd.exe"
if exist "%SystemRoot%\command.com" set COMMAND_COM="%SystemRoot%\command.com"

@rem Use explicit find.exe to prevent cygwin and others find.exe from being used
set FIND_EXE="find.exe"
if exist "%SystemRoot%\system32\find.exe" set FIND_EXE="%SystemRoot%\system32\find.exe"
if exist "%SystemRoot%\command\find.exe" set FIND_EXE="%SystemRoot%\command\find.exe"

:check_JAVA_HOME
@rem Make sure we have a valid JAVA_HOME
if not "%JAVA_HOME%" == "" goto have_JAVA_HOME
for %%P in (%PATH%) do if exist %%P\..\bin\java.exe set JAVA_HOME=%%P\..
if not "%JAVA_HOME%" == "" goto valid_JAVA_HOME
java -version 2>NUL
if not ERRORLEVEL 1 goto default_JAVA_EXE

echo.
echo ERROR: Environment variable JAVA_HOME has not been set.
echo Attempting to find JAVA_HOME from PATH also failed.
goto common_error

:have_JAVA_HOME
@rem Remove trailing slash from JAVA_HOME if found
if "%JAVA_HOME:~-1%"=="\" SET JAVA_HOME=%JAVA_HOME:~0,-1%

@rem Validate JAVA_HOME
%COMMAND_COM% /C DIR "%JAVA_HOME%" 2>&1 | %FIND_EXE% /I /C "%JAVA_HOME%" >nul
if not errorlevel 1 goto valid_JAVA_HOME_DIR

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%

:common_error
echo Please set the JAVA_HOME variable in your environment
echo to match the location of your Java installation.
goto end

:default_JAVA_EXE
set JAVA_EXE=java.exe

:valid_JAVA_HOME_DIR
set JAVA_EXE=%JAVA_HOME%\bin\java.exe
if exist "%JAVA_EXE%" goto valid_JAVA_HOME

echo.
echo ERROR: No java.exe found at: %JAVA_EXE%
goto common_error

:valid_JAVA_HOME
if exist "%JAVA_HOME%\lib\tools.jar" set TOOLS_JAR=%JAVA_HOME%\lib\tools.jar

@rem classpath handling
set _SKIP=2
set CP=
if "x%~1" == "x-cp" set CP=%~2
if "x%~1" == "x-classpath" set CP=%~2
if "x%~1" == "x--classpath" set CP=%~2
if "x" == "x%CP%" goto init
set _SKIP=4
shift
shift

:init
@rem Get command-line arguments, handling Windowz variants
if not "%OS%" == "Windows_NT" goto win9xME_args
if "%eval[2+2]" == "4" goto 4NT_args

:win9xME_args
@rem Slurp the command line arguments.
set CMD_LINE_ARGS=

:win9xME_args_slurp
if "x%~1" == "x" goto execute

rem horrible roll your own arg processing inspired by jruby equivalent

rem escape minus (-d), quotes (-q), star (-s).
set _ARGS=%*
if not defined _ARGS goto execute
set _ARGS=%_ARGS:-=-d%
set _ARGS=%_ARGS:"=-q%
rem Windowz will try to match * with files so we escape it here
rem but it is also a meta char for env var string substitution
rem so it can't be first char here, hack just for common cases.
rem If in doubt use a space or bracket before * if using -e.
set _ARGS=%_ARGS: *= -s%
set _ARGS=%_ARGS:)*=)-s%
set _ARGS=%_ARGS:0*=0-s%
set _ARGS=%_ARGS:1*=1-s%
set _ARGS=%_ARGS:2*=2-s%
set _ARGS=%_ARGS:3*=3-s%
set _ARGS=%_ARGS:4*=4-s%
set _ARGS=%_ARGS:5*=5-s%
set _ARGS=%_ARGS:6*=6-s%
set _ARGS=%_ARGS:7*=7-s%
set _ARGS=%_ARGS:8*=8-s%
set _ARGS=%_ARGS:9*=9-s%
rem prequote all args for 'for' statement
set _ARGS="%_ARGS%"

:win9xME_args_loop
rem split args by spaces into first and rest
for /f "tokens=1,*" %%i in (%_ARGS%) do call :get_arg "%%i" "%%j"
goto process_arg

:get_arg
rem remove quotes around first arg
for %%i in (%1) do set _ARG=%%~i
rem set the remaining args
set _ARGS=%2
rem return
goto :EOF

:process_arg
if "%_ARG%" == "" goto execute

if "x4" == "x%_SKIP%" goto skip_4
if "x3" == "x%_SKIP%" goto skip_3
if "x2" == "x%_SKIP%" goto skip_2
if "x1" == "x%_SKIP%" goto skip_1

rem now unescape -q, -s, -d
set _ARG=%_ARG:-q="%
set _ARG=%_ARG:-s=*%
set _ARG=%_ARG:-d=-%

set CMD_LINE_ARGS=%CMD_LINE_ARGS% %_ARG%
set _ARG=
goto win9xME_args_loop

:skip_4
set _ARG=
set _SKIP=3
goto win9xME_args_loop

:skip_3
set _ARG=
set _SKIP=2
goto win9xME_args_loop

:skip_2
set _ARG=
set _SKIP=1
goto win9xME_args_loop

:skip_1
set _ARG=
set _SKIP=0
goto win9xME_args_loop

:4NT_args
@rem Get arguments from the 4NT Shell from JP Software
set CMD_LINE_ARGS=%$

:execute
@rem Setup the command line
set LIBDIR="%DIRNAME%..\lib"
set CLASSPATH=
for %%i in (%LIBDIR%\*.jar) do set CLASSPATH=!CLASSPATH!;%%i

@rem Setting a classpath using the -cp or -classpath option means not to use
@rem the global classpath. Groovy behaves then the same as the java
@rem interpreter
if "x" == "x%CP%" goto empty_cp
:non_empty_cp
set CP=%CP%;.
goto after_cp
:empty_cp
set CP=.
if "x" == "x%CLASSPATH%" goto after_cp
set CP=%CLASSPATH%;%CP%
:after_cp

set JAVA_OPTS="@app.java.opts@"
if "%JAVA_OPTS%" == "" set JAVA_OPTS="-Xmx128m"
set JAVA_OPTS=%JAVA_OPTS% -Dprogram.name="%PROGNAME%" -Dgriffon.start.dir=%DIRNAME%..\

@rem Execute App
"%JAVA_EXE%" %JAVA_OPTS% -classpath %CP% @app.main.class@ %CMD_LINE_ARGS%

:end
@rem End local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" endlocal

@rem Optional pause the batch file
if "%GROOVY_BATCH_PAUSE%" == "on" pause
