from flask import jsonify
import requests, re

class Validations:
    """Clase que contiene las validaciones necesarias"""
    def __init__(self, url):
        self.url = url
        self.GITHUB_URL_REGEX = r"^(https://github\.com/[A-Za-z0-9_-]+/[A-Za-z0-9_-]+\.git|git@github\.com:[A-Za-z0-9_-]+/[A-Za-z0-9_-]+\.git)$"
        self.verifyGitHubUrl()

    def verifyGitHubUrl(self):
        if not re.match(self.GITHUB_URL_REGEX, self.url):
            return jsonify({"Error": "The URL is not from a GitHub repository"}), 400
        
        if not self._verifyGitHubRepExists():
            return jsonify({"Error": "The GitHub repository doesn't exist"}), 404
        
  
    def _verifyGitHubRepExists(self) -> bool:
        response = requests.get(self.url)
        return response.status_code == 200