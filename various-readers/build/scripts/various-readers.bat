@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  various-readers startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Add default JVM options here. You can also use JAVA_OPTS and VARIOUS_READERS_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto init

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto init

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:init
@rem Get command-line arguments, handling Windows variants

if not "%OS%" == "Windows_NT" goto win9xME_args

:win9xME_args
@rem Slurp the command line arguments.
set CMD_LINE_ARGS=
set _SKIP=2

:win9xME_args_slurp
if "x%~1" == "x" goto execute

set CMD_LINE_ARGS=%*

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\various-readers-0.1.0.jar;%APP_HOME%\lib\pravega-keycloak-credentials-0.5.0-2302.7592022-0.11.7-001.29d9682-shadow.jar;%APP_HOME%\lib\common-0.1.0.jar;%APP_HOME%\lib\logback-classic-1.2.3.jar;%APP_HOME%\lib\pravega-client-0.5.0-2302.7592022.jar;%APP_HOME%\lib\pravega-shared-protocol-0.5.0-2302.7592022.jar;%APP_HOME%\lib\pravega-shared-controller-api-0.5.0-2302.7592022.jar;%APP_HOME%\lib\pravega-common-0.5.0-2302.7592022.jar;%APP_HOME%\lib\pravega-shared-authplugin-0.5.0-2302.7592022.jar;%APP_HOME%\lib\slf4j-api-1.7.25.jar;%APP_HOME%\lib\logstash-logback-encoder-4.11.jar;%APP_HOME%\lib\logback-core-1.2.3.jar;%APP_HOME%\lib\jackson-databind-2.9.1.jar;%APP_HOME%\lib\netty-transport-native-epoll-4.1.30.Final.jar;%APP_HOME%\lib\grpc-protobuf-1.17.1.jar;%APP_HOME%\lib\grpc-netty-1.17.1.jar;%APP_HOME%\lib\grpc-stub-1.17.1.jar;%APP_HOME%\lib\grpc-auth-1.17.1.jar;%APP_HOME%\lib\grpc-protobuf-lite-1.17.1.jar;%APP_HOME%\lib\grpc-core-1.17.1.jar;%APP_HOME%\lib\guava-27.0.1-jre.jar;%APP_HOME%\lib\jackson-annotations-2.9.0.jar;%APP_HOME%\lib\jackson-core-2.9.1.jar;%APP_HOME%\lib\commons-io-2.6.jar;%APP_HOME%\lib\lombok-1.18.4.jar;%APP_HOME%\lib\netty-codec-http2-4.1.30.Final.jar;%APP_HOME%\lib\netty-handler-4.1.30.Final.jar;%APP_HOME%\lib\netty-transport-native-unix-common-4.1.30.Final.jar;%APP_HOME%\lib\netty-handler-proxy-4.1.30.Final.jar;%APP_HOME%\lib\netty-codec-http-4.1.30.Final.jar;%APP_HOME%\lib\netty-codec-socks-4.1.30.Final.jar;%APP_HOME%\lib\netty-codec-4.1.30.Final.jar;%APP_HOME%\lib\netty-transport-4.1.30.Final.jar;%APP_HOME%\lib\commons-lang3-3.7.jar;%APP_HOME%\lib\netty-all-4.1.30.Final.jar;%APP_HOME%\lib\netty-tcnative-boringssl-static-2.0.17.Final.jar;%APP_HOME%\lib\netty-buffer-4.1.30.Final.jar;%APP_HOME%\lib\netty-resolver-4.1.30.Final.jar;%APP_HOME%\lib\netty-common-4.1.30.Final.jar;%APP_HOME%\lib\failureaccess-1.0.1.jar;%APP_HOME%\lib\listenablefuture-9999.0-empty-to-avoid-conflict-with-guava.jar;%APP_HOME%\lib\jsr305-3.0.2.jar;%APP_HOME%\lib\checker-qual-2.5.2.jar;%APP_HOME%\lib\error_prone_annotations-2.2.0.jar;%APP_HOME%\lib\j2objc-annotations-1.1.jar;%APP_HOME%\lib\animal-sniffer-annotations-1.17.jar;%APP_HOME%\lib\protobuf-java-3.5.1.jar;%APP_HOME%\lib\proto-google-common-protos-1.0.0.jar;%APP_HOME%\lib\google-auth-library-credentials-0.9.0.jar;%APP_HOME%\lib\grpc-context-1.17.1.jar;%APP_HOME%\lib\gson-2.7.jar;%APP_HOME%\lib\opencensus-contrib-grpc-metrics-0.17.0.jar;%APP_HOME%\lib\opencensus-api-0.17.0.jar

@rem Execute various-readers
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %VARIOUS_READERS_OPTS%  -classpath "%CLASSPATH%" com.dellemc.oe.readers.JSONReader %CMD_LINE_ARGS%

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable VARIOUS_READERS_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%VARIOUS_READERS_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
