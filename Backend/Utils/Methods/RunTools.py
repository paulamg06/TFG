from flask import jsonify
import os, subprocess, json

class RunTools():
    """Clase que contiene los métodos para ejecutar las herramientas de análisis de código"""
    def __init__(self, cloned_path: str):
        self.path = os.path.join(os.getcwd(), "venv", "Scripts")
        self.cloned_path = cloned_path


    def ProcessSemgrep(self) -> str:
        semgrep_path = os.path.join(self.path, "semgrep.exe")
        semgrep_response = subprocess.run([semgrep_path, "--config=auto", self.cloned_path], capture_output=True, text=True)

        return semgrep_response.stdout
    

    def ProcessBandit(self) -> dict:
        bandit_path = os.path.join(self.path, "bandit.exe")
        bandit_result = subprocess.run([bandit_path, "--r", self.cloned_path], capture_output=True, text=True)

        bandit_response = bandit_result.stdout.splitlines()
        issues = []

        for line in bandit_response:
            if line.startswith("["):
                severity, description = line.split(" ", 1)
                issues.append({"severity": severity.strip("[]"), "description": description})

        return {"issues": issues}
    

    def ProcessCBOM(self) -> dict:
        cbom_path = os.path.join(self.path, "cyclonedx-py.exe")
        generated_cbom_path = os.path.join(self.cloned_path, "bom.json")

        cbom_response = subprocess.run([cbom_path, "--output-format", "json", "--output-file", generated_cbom_path, "--input-file", self.cloned_path], 
                                     capture_output=True, text=True)
        
        if os.path.exists(generated_cbom_path) and os.path.getsize(generated_cbom_path) > 0:
            with open(generated_cbom_path, "r", encoding="utf-8") as file:
                try:
                    return json.load(file)

                except json.JSONDecodeError:
                    return {"error": "Archivo bom.json corrupto o vacío"}
                except Exception as exception:
                    return {"error": str(exception)}

        return {"error": "CBOM no generó dependencias o el archivo está vacío"}
