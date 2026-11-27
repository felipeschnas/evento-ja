# Compila o app, instala no emulador (ou celular) conectado e abre a tela de login.
# Uso: .\rodar-app.ps1            (abre o emulador se nenhum estiver ligado)
#      .\rodar-app.ps1 -SoCompilar (só gera o APK e roda os testes)
param([switch]$SoCompilar)

$ErrorActionPreference = 'Stop'

if (-not $env:JAVA_HOME -or -not (Test-Path "$env:JAVA_HOME\bin\java.exe")) {
    $env:JAVA_HOME = (Get-ChildItem 'C:\Program Files\Eclipse Adoptium' -Directory -Filter 'jdk-17*' | Select-Object -First 1).FullName
}
$sdk = if ($env:ANDROID_HOME) { $env:ANDROID_HOME } else { "$env:LOCALAPPDATA\Android\Sdk" }
$adb = "$sdk\platform-tools\adb.exe"
$emulador = "$sdk\emulator\emulator.exe"

Set-Location $PSScriptRoot
.\gradlew.bat assembleDebug testDebugUnitTest
if ($LASTEXITCODE -ne 0) { throw 'Falha no build' }
if ($SoCompilar) { return }

# Daqui para baixo o adb escreve avisos no stderr enquanto o emulador liga; não é erro
$ErrorActionPreference = 'Continue'

$dispositivos = & $adb devices | Select-String "`tdevice$"
if (-not $dispositivos) {
    $avd = (& $emulador -list-avds | Select-Object -First 1)
    Write-Host "Abrindo o emulador $avd..."
    Start-Process $emulador -ArgumentList "-avd", $avd
}

Write-Host 'Esperando o emulador terminar de ligar...'
do {
    Start-Sleep -Seconds 3
    $ligado = cmd /c "`"$adb`" shell getprop sys.boot_completed 2>nul"
} while ("$ligado".Trim() -ne '1')

& $adb install -r app\build\outputs\apk\debug\app-debug.apk
& $adb shell am start -n br.com.eventoja/.LoginActivity
