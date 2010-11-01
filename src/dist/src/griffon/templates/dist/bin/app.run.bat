@if "%DEBUG%" == "" @echo off

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

:begin
@rem Determine what directory it is in.
set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.\

set GRIFFON_APP_NAME="@app.name@"

"%DIRNAME%\startApp.bat" "%DIRNAME%"

@rem End local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" endlocal
