param(
    [string]$Version
)

$jdkCandidates = @(
    @{ Version = '8'; Path = 'C:\Program Files\Java\jre-1.8' },
    @{ Version = '17'; Path = 'C:\Program Files\Microsoft\jdk-17.0.20.101-hotspot' },
    @{ Version = '21'; Path = 'C:\Program Files\Microsoft\jdk-21.0.12.101-hotspot' },
    @{ Version = '26'; Path = 'C:\Program Files\Java\jdk-26.0.2.1' }
) | Where-Object { Test-Path (Join-Path $_.Path 'bin\java.exe') }

if (-not $jdkCandidates) {
    throw 'No se encontraron instalaciones de Java compatibles.'
}

if (-not $Version) {
    Write-Host 'Versiones de Java disponibles:'
    for ($index = 0; $index -lt $jdkCandidates.Count; $index++) {
        Write-Host "$($index + 1). Java $($jdkCandidates[$index].Version) - $($jdkCandidates[$index].Path)"
    }

    $selection = Read-Host 'Elige el numero de version'
    if ($selection -notmatch '^\d+$' -or [int]$selection -lt 1 -or [int]$selection -gt $jdkCandidates.Count) {
        throw 'Seleccion no valida.'
    }

    $Version = $jdkCandidates[[int]$selection - 1].Version
}

$selected = $jdkCandidates | Where-Object Version -eq $Version | Select-Object -First 1
if (-not $selected) {
    throw "Java $Version no esta instalado o no fue detectado."
}

$env:JAVA_HOME = $selected.Path
$javaBin = Join-Path $selected.Path 'bin'
$pathEntries = $env:Path -split ';' | Where-Object { $_ -and $_ -notmatch '\\Java\\|\\Microsoft\\jdk-' }
$env:Path = "$javaBin;$($pathEntries -join ';')"

Write-Host "JAVA_HOME=$env:JAVA_HOME"
java -version
Write-Host ''
Write-Host 'La version queda activa en esta ventana de PowerShell.'
Write-Host 'Para Maven: .\mvnw.cmd test'
