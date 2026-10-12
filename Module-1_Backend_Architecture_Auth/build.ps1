# ====================================================================
# CampusVoice Backend - Build & Package Script
# Compiles Java sources, runs unit tests, and packages campusvoice.war
# ====================================================================

$ErrorActionPreference = "Stop"

Write-Host ">>> [1/4] Preparing directories..." -ForegroundColor Cyan
if (-not (Test-Path "target\classes")) { New-Item -ItemType Directory -Path "target\classes" -Force | Out-Null }
if (-not (Test-Path "target\war\WEB-INF\classes")) { New-Item -ItemType Directory -Path "target\war\WEB-INF\classes" -Force | Out-Null }
if (-not (Test-Path "target\war\WEB-INF\lib")) { New-Item -ItemType Directory -Path "target\war\WEB-INF\lib" -Force | Out-Null }

$javac = if (Get-Command javac -ErrorAction SilentlyContinue) { (Get-Command javac).Source } else { "javac" }
$java = if (Get-Command java -ErrorAction SilentlyContinue) { (Get-Command java).Source } else { "java" }
$jar = if (Get-Command jar -ErrorAction SilentlyContinue) { (Get-Command jar).Source } elseif (Test-Path "C:\Program Files\Java\jdk-26.0.2\bin\jar.exe") { "C:\Program Files\Java\jdk-26.0.2\bin\jar.exe" } else { "jar" }

Write-Host ">>> [2/4] Compiling Java backend sources..." -ForegroundColor Cyan
$sources = (Get-ChildItem -Path "src\main\java" -Recurse -Filter "*.java").FullName
& $javac -cp "lib\*" -d "target\classes" $sources
if ($LASTEXITCODE -ne 0) {
    Write-Host "Compilation failed!" -ForegroundColor Red
    exit 1
}

# Copy classpath resources
Copy-Item -Path "src\main\resources\*" -Destination "target\classes\" -Recurse -Force

Write-Host ">>> [3/4] Running Verification Engine & Security Tests..." -ForegroundColor Cyan
& $javac -cp "lib\*;target\classes" -d "target\classes" "src\test\java\campusvoice\AlgorithmTest.java"
& $java -cp "lib\*;target\classes;src\main\resources" campusvoice.AlgorithmTest
if ($LASTEXITCODE -ne 0) {
    Write-Host "Unit tests failed!" -ForegroundColor Red
    exit 1
}

Write-Host ">>> [4/4] Assembling Tomcat Web Application Archive (WAR)..." -ForegroundColor Cyan
# Copy webapp files
Copy-Item -Path "src\main\webapp\*" -Destination "target\war\" -Recurse -Force
# Copy compiled classes
Copy-Item -Path "target\classes\*" -Destination "target\war\WEB-INF\classes\" -Recurse -Force
# Copy runtime libraries to WEB-INF/lib
Copy-Item -Path "lib\mysql-connector-j-8.3.0.jar" -Destination "target\war\WEB-INF\lib\" -Force
Copy-Item -Path "lib\gson-2.10.1.jar" -Destination "target\war\WEB-INF\lib\" -Force

# Build WAR archive
& $jar -cvf "target\campusvoice.war" -C "target\war" . | Out-Null

Write-Host "`n========================================================" -ForegroundColor Green
Write-Host "  BUILD SUCCESSFUL! " -ForegroundColor Green
Write-Host "  WAR Artifact: target\campusvoice.war" -ForegroundColor Green
Write-Host "  Ready to deploy to Apache Tomcat webapps/ directory." -ForegroundColor Green
Write-Host "========================================================`n" -ForegroundColor Green
