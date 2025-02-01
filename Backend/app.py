from flask import Flask, request, jsonify
from flask_cors import CORS
from DTOs import *
from pydantic import ValidationError
import subprocess, os, re, requests 

app = Flask(__name__)
CORS(app)  # Permitir peticiones desde React

GITHUB_URL_REGEX = r"^(https://github\.com/[A-Za-z0-9_-]+/[A-Za-z0-9_-]+\.git|git@github\.com:[A-Za-z0-9_-]+/[A-Za-z0-9_-]+\.git)$"

CLONED_DIR = "./ClonedRepos/"

def verifyGitHubRepExists(url: str) -> bool:
    response = requests.get(url)
    return response.status_code == 200

@app.route("/api/analyzeCrypto", methods=["POST"])
def analyzeCrypto():
    data = request.json
    analyze_crypto_request = AnalyzeCryptoRequestDTO(**data)

    gitHub_repo = analyze_crypto_request.gitHub_repo

    if not re.match(GITHUB_URL_REGEX, gitHub_repo):
        return jsonify({"Error": "The URL is not from a GitHub repository"}), 400

    if not verifyGitHubRepExists(gitHub_repo):
        return jsonify({"Error": "The GitHub repository doesn't exist"}), 404
    
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
