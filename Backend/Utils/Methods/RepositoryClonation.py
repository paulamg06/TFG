import subprocess, os, tempfile

class RepositoryClonation:
    """Clase que se encarga de clonar el respositorio de GitHub en un directorio temporal"""
    def __init__(self, github_repo: str):
        self.github_repo = github_repo
        self.createTemporalDirectory()


    def createTemporalDirectory(self):
        with tempfile.TemporaryDirectory() as temp_dir:
            repo_name = self.github_repo.split("/")[-1]

            cloned_repo_path = os.path.join(temp_dir, repo_name)

            clone_command = ["git", "clone", self.github_repo, cloned_repo_path]
            subprocess.run(clone_command)