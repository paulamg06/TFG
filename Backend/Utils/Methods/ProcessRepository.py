from flask import jsonify
import subprocess, os, tempfile, re, requests


class ProcessRepository:
    """Clase que se contiene los métodos relacionados con el manejo del repositorio de GitHub"""
    def __init__(self, github_repo: str):
        self.github_repo = github_repo
        self.GITHUB_URL_REGEX = r"^(https://github\.com/[A-Za-z0-9_-]+/[A-Za-z0-9_-]+\.git|git@github\.com:[A-Za-z0-9_-]+/[A-Za-z0-9_-]+\.git)$"
        self.repository_exists = self.VerifyGitHubUrlExists()

    def createTemporalDirectory(self):
        with tempfile.TemporaryDirectory() as temp_dir:
            repo_name = self.github_repo.split("/")[-1]

            cloned_repo_path = os.path.join(temp_dir, repo_name)

            try:
                subprocess.run(["git", "clone", self.github_repo, cloned_repo_path], check=True)
                return cloned_repo_path
            except subprocess.CalledProcessError:
                return jsonify({"error": "Failed to clone repository"}), 500
    

    def VerifyGitHubUrlExists(self):
        if not re.match(self.GITHUB_URL_REGEX, self.github_repo):
            return False
        
        response = requests.get(self.github_repo)
        if response.status_code != 200:
            return False
        
        return True