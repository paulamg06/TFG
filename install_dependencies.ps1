$venvPath = ".\venv"
$venvActivate = ".\venv\Scripts\activate"

if (Test-Path $venvPath) {
    Write-Output "Reinstalando entorno virtual..."
    rmdir $venvPath
    python -m venv $venvPath
} 

Write-Output "Activando entorno virtual..."
. $venvActivate


Write-Output "Descargando dependencias..."
python.exe -m pip install --upgrade pip
pip install -r requirements.txt
