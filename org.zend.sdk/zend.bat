@echo off
rem Copyright (c) May 16, 2011 Zend Technologies Ltd. 
rem All rights reserved. This program and the accompanying materials 
rem are made available under the terms of the Eclipse Public License v1.0 
rem which accompanies this distribution, and is available at 
rem http://www.eclipse.org/legal/epl-v10.html  

rem Don't modify the caller's environment
setlocal

rem Get current directory
set current_dir=%cd%

cd /d %~dp0

set jar_path=lib/commons-cli-1.2.jar

rem Finally exec the java program and end here.
call java.exe -classpath "%jar_path%;bin" org.zend.sdk.cli.ZendSDKMain %* -currDir %current_dir%

:EOF
