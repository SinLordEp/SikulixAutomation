@echo off
setlocal

set APP_NAME=AQSAutomationFlow
set MAIN_CLASS=executable.ATLauncher
set MAIN_JAR=SikulixAutomation-1.1.jar
set ICON= src\main\resources\AqsAutomationFlow.ico

if exist dist rmdir /s /q dist
if exist jpackage-temp rmdir /s /q jpackage-temp
mkdir jpackage-temp
copy build\libs\%MAIN_JAR% jpackage-temp\

jpackage ^
  --type app-image ^
  --input jpackage-temp ^
  --dest dist ^
  --name %APP_NAME% ^
  --main-jar %MAIN_JAR% ^
  --main-class %MAIN_CLASS% ^
  --java-options "-Dfile.encoding=UTF-8" ^
  --icon %ICON%

endlocal
