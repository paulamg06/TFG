from flask import Flask, request, jsonify
from flask_cors import CORS
from Utils.DTOs import *
from Utils.Methods import *
from pydantic import ValidationError
import subprocess, os, chardet

app = Flask(__name__)
CORS(app)  # Permitir peticiones desde React


@app.route("/api/analyzeCrypto", methods=["POST"])
def analyzeCrypto():
    data = request.json

    if not data.get("github_repo"):
        return jsonify({"error": "github_repo es obligatorio"}), 400

    analyze_crypto_request = AnalyzeCryptoRequestDTO(**data)

    github_repo = analyze_crypto_request.github_repo

    process_git = ProcessRepository(github_repo)
    
    if not process_git.repository_exists:
        return jsonify({"error": "Invalid URL"}), 404
    
    cloned_path = process_git.createTemporalDirectory()

    try:
        path = os.path.join(os.getcwd(), "venv", "Scripts")
        # Semgrep"
        #semgrep_path = os.path.join(path, "semgrep.exe")
        #semgrep_response = subprocess.run([semgrep_path, "--config=auto", cloned_path], capture_output=True, text=True)

        # Bandit
        bandit_path = os.path.join(path, "bandit.exe")
        bandit_response = subprocess.run([bandit_path, "--r", cloned_path], capture_output=True, text=True)

        # CBOM
        cbom_path = os.path.join(path, "cyclonedx-py.exe")
        generated_cbom_path = f"{cloned_path}/bom.json"
        cbom_response = subprocess.run([cbom_path, "--output-format", "json", "output-file", generated_cbom_path, "--input-file", cloned_path], 
                                     capture_output=True, text=True)
        cbom_dict_response = {}
        if os.path.exists(cbom_path):
            with open(cbom_path, "rb") as file:
                cbom_dict_response = file.read()

        encoding_detected = chardet.detect(cbom_dict_response)['encoding']

        response = AnalyzeCryptoResponseDTO(
            #semgrep_response = semgrep_response.stdout,
            semgrep_response = "semgrep_response.stdout",
            bandit_response = "bandit_response.stdout",
            cbom_response = cbom_dict_response
        )

        return jsonify(response.model_dump())

    except Exception as exception:
        return jsonify({"error": str(exception)}), 500
    


if __name__ == "__main__":
    app.run(debug=True)
