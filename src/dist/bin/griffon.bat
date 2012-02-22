@if "%DEBUG%" == "" @echo off

@rem 
@rem $Revision: 2770 $ $Date: 2005-08-29 10:49:42 +0000 (Mon, 29 Aug 2005) $
@rem 

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

:begin
@rem Determine what directory it is in.
set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.\

set STARTER_CLASSPATH=%GRIFFON_HOME%\lib\groovy-all-@groovy.version@.jar;%GRIFFON_HOME%\dist\griffon-cli-@griffon.version@.jar;%GRIFFON_HOME%\dist\griffon-rt-@griffon.version@.jar

set STARTER_CONF=%GRIFFON_HOME%\conf\groovy-starter.conf

CALL "%DIRNAME%\startGriffon.bat" "%DIRNAME%" org.codehaus.griffon.cli.GriffonScriptRunner %*
