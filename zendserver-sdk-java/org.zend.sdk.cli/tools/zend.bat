@echo off
rem Copyright (c) May 16, 2011 Zend Technologies Ltd. 
rem All rights reserved. This program and the accompanying materials 
rem are made available under the terms of the Eclipse Public License v1.0 
rem which accompanies this distribution, and is available at 
rem http://www.eclipse.org/legal/epl-v10.html  

rem Don't modify the caller's environment
setlocal

rem Check we have a valid Java.exe in the path.
set java_exe=
call %~dp0/../tools/find_java.bat
if not defined java_exe goto :EOF

rem Set registry Jar path based on current architecture (x86 or x86_64)
for /f %%a in ('%java_exe% -jar %~dp0\..\lib\archquery.jar') do set registry_path=%~dp0\..\lib\%%a

set jar_path=%~dp0/../lib/*

rem Finally exec the java program and end here.

if "%1" == "update" (goto :UPDATE) else (goto :MAIN)

:UPDATE
if "%2" == "" (call %java_exe% -Djava.library.path="%registry_path%" -classpath "%~dp0/../lib/org.zend.sdk.update.jar" org.zend.sdkcli.update.Update "%~dp0/../"
goto :EOF) else (goto :MAIN)

:MAIN
call %java_exe% -Djava.library.path="%registry_path%" -classpath "%jar_path%" org.zend.sdkcli.Main %*

:EOF