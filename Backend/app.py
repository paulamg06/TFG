from flask import Flask, request, jsonify
from flask_cors import CORS
from Utils.DTOs import *
from Utils.Methods import *
from pydantic import ValidationError
import subprocess, os, re

app = Flask(__name__)
CORS(app)  # Permitir peticiones desde React

CLONED_DIR = "./Backend/ClonedRepositories"


@app.route("/api/analyzeCrypto", methods=["POST"])
def analyzeCrypto():
    data = request.json
    analyze_crypto_request = AnalyzeCryptoRequestDTO(**data)

    gitHub_repo = analyze_crypto_request.gitHub_repo

    Validations(gitHub_repo)
    
    repo_name = gitHub_repo.split("/")[-1]
    cloned_repo_path = os.path.join(CLONED_DIR, repo_name)

    clone_command = ["git", "clone", gitHub_repo, cloned_repo_path]
    subprocess.run(clone_command)

    try:
        """# Semgrep
        semgrep_response = subprocess.run(["semgrep", "--config=auto", repo_name], capture_output=True, text=True)
        # Bandit
        bandit_response = subprocess.run(["bandit", "--r", repo_name], capture_output=True, text=True)

        response = AnalyzeCryptoResponseDTO(
            semgrep_response = semgrep_response.stdout,
            bandit_response = bandit_response.stdout
        )"""
        response = AnalyzeCryptoResponseDTO(
            semgrep_response = "Semgrep response",
            bandit_response = "Bandit response"
        )
        return jsonify(response.model_dump())

    except ValidationError as exception:
        return jsonify({"Error": "Invalid path format"}), 400

    except Exception as exception:
        return jsonify({"Error": str(exception)}), 500
    


if __name__ == "__main__":
    app.run(debug=True)
