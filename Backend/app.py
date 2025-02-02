from flask import Flask, request, jsonify
from flask_cors import CORS
from Utils.DTOs import *
from Utils.Methods import *
from pydantic import ValidationError
import subprocess

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

    if not process_git.validateGitHubRegex():
        return jsonify({"error": "Invalid GitHub URL"}), 400
    
    if not process_git.VerifyGitHubUrlExists():
        return jsonify({"error": "Repository doesn't exist or is private"}), 404
    
    process_git.createTemporalDirectory()

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
        return jsonify({"error": "Invalid path format"}), 400

    except Exception as exception:
        return jsonify({"error": str(exception)}), 500
    


if __name__ == "__main__":
    app.run(debug=True)
