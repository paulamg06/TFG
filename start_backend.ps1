$venvPath = ".\venv\Scripts\Activate.ps1"

if (Test-Path $venvPath) {
    Write-Output "Activando entorno virtual..."
    . $venvPath
} else {
    Write-Output "No se encontró el entorno virtual. ¿Lo creaste?"
    Write-Output "Asegúrate de que la carpeta 'venv' está en el mismo directorio."
}

Write-Output "Descargando dependencias..."
pip install -r requirements.txt

Write-Output "Iniciando backend..."
python .\backend\app.py