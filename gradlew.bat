@rem
@rem Copyright © 2015-2021 the original authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem

@if "%DEBUG%" == "" @echo off
@(#)SB Justine

if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%

if exist "%JAVA_HOME%\bin\java.exe" set JAVACMD=%JAVA_HOME%\bin\java.exe

if "%JAVACMD%" == "" set JAVACMD=java

"%JAVACMD%" -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
    goto fail
)

set DEFAULT_JVM_OPTS="-Xmx64m" "-Xms64m"

"%JAVACMD%" %DEFAULT_JVM_OPTS% -jar "%APP_HOME%\gradle\wrapper\gradle-wrapper.jar" %*

if errorlevel 1 goto fail

:fail
exit /b 1
