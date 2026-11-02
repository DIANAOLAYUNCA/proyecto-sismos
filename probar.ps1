# Arranca la aplicacion contra su MySQL. La clave se pide en pantalla y no se guarda en ningun archivo.
# Uso (PowerShell, desde esta carpeta):  .\probar.ps1
$puerto = Read-Host "Puerto de MySQL (Enter = 3306)"
if ([string]::IsNullOrWhiteSpace($puerto)) { $puerto = "3306" }

$base = Read-Host "Nombre de la base (Enter = cssp_sismos)"
if ([string]::IsNullOrWhiteSpace($base)) { $base = "cssp_sismos" }

$usuario = Read-Host "Usuario de MySQL (Enter = root)"
if ([string]::IsNullOrWhiteSpace($usuario)) { $usuario = "root" }

$secreto = Read-Host "Clave de MySQL" -AsSecureString

$env:DB_URL = "jdbc:mysql://localhost:$puerto/${base}?useSSL=false&serverTimezone=UTC"
$env:DB_USER = $usuario
$env:DB_PASSWORD = [System.Net.NetworkCredential]::new("", $secreto).Password
Remove-Variable secreto
$env:SERVER_PORT = "8081"

Write-Host "Abra http://localhost:8081/ cuando termine de arrancar."
.\mvnw.cmd spring-boot:run
