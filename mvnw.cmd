@echo off
setlocal

set MAVEN_VERSION=3.9.9
set MAVEN_HOME=%USERPROFILE%\.mvn\apache-maven-%MAVEN_VERSION%
set MAVEN_ZIP=%TEMP%\apache-maven-%MAVEN_VERSION%-bin.zip
set MAVEN_URL=https://archive.apache.org/dist/maven/maven-3/%MAVEN_VERSION%/binaries/apache-maven-%MAVEN_VERSION%-bin.zip

if not exist "%MAVEN_HOME%\bin\mvn.cmd" (
    echo ============================================
    echo  Downloading Apache Maven %MAVEN_VERSION%...
    echo ============================================
    if not exist "%USERPROFILE%\.mvn" mkdir "%USERPROFILE%\.mvn"
    powershell -Command "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; try { Invoke-WebRequest -Uri '%MAVEN_URL%' -OutFile '%MAVEN_ZIP%' -ErrorAction Stop } catch { Write-Host 'Download failed' }"
    if not exist "%MAVEN_ZIP%" (
        echo ERROR: Failed to download Maven. Please install Maven manually.
        exit /b 1
    )
    powershell -Command "Expand-Archive -Path '%MAVEN_ZIP%' -DestinationPath '%USERPROFILE%\.mvn' -Force"
    del "%MAVEN_ZIP%"
    echo Maven %MAVEN_VERSION% installed successfully!
)

"%MAVEN_HOME%\bin\mvn.cmd" %*
endlocal
