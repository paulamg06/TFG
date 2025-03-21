from flask import jsonify
import subprocess, os, re, requests, json


class ProcessRepository:
    """Clase que se contiene los métodos relacionados con el manejo del repositorio de GitHub"""
    def __init__(self, github_repo: str):
        self.github_repo = github_repo
        self.repository_exists = self.verify_github_url_exists()
        self.path = self.path = os.path.join(os.getcwd(), "venv", "Scripts")
        self.cloned_path = ""
        self.cbom_path = os.path.join(os.getcwd(), "Backend", "Outputs", "CBOM",)
        self.generated_cbom_path = os.path.join(self.cbom_path, "cbom.json")

    def verify_github_url_exists(self):
        """Método que verifica si la URL introducida tiene el formato correcto y si existe"""
        if not re.match(r"^(https://github\.com/[A-Za-z0-9_-]+/[A-Za-z0-9_-]+\.git|git@github\.com:[A-Za-z0-9_-]+/[A-Za-z0-9_-]+\.git)$", self.github_repo):
            return False
        
        response = requests.get(self.github_repo)
        if response.status_code != 200:
            return False
        
        return True
    

    def clone_repository(self):
        """Método que clona el repositorio en la carpeta .\Backend\Repositories para poder procesarlo"""
        repo_name = self.github_repo.split("/")[-1].replace(".git", "")
        repo_path = os.path.join(os.getcwd(), "Backend", "Outputs", "Repositories")
        os.makedirs(os.path.dirname(repo_path), exist_ok=True)

        self.cloned_path = os.path.join(repo_path, repo_name)

        if not os.path.exists(self.cloned_path):
            try:
                subprocess.run(["git", "clone", self.github_repo, self.cloned_path], check=True)

            except subprocess.CalledProcessError:
                return jsonify({"error": "Failed to clone repository"}), 500
    

    def process_cbom(self) -> dict:
        """Método que se encarga de analizar el repositorio con CBOM y generar el archivo para poder estudiarlo"""
        cbom_path = os.path.join(self.path, "cyclonedx-py.exe")
        requirements_path = os.path.join(self.cloned_path, "requirements.txt")

        os.makedirs(os.path.dirname(self.generated_cbom_path), exist_ok=True)

        with open(self.generated_cbom_path, 'w') as output_file:
            subprocess.run([cbom_path, "requirements", requirements_path], stdout=output_file, text=True)
        
        if os.path.exists(self.generated_cbom_path) and os.path.getsize(self.generated_cbom_path) > 0:
            with open(self.generated_cbom_path, "r", encoding="utf-8") as file:
                try:
                    return json.load(file)

                except json.JSONDecodeError:
                    return {"error": "Archivo bom.json corrupto o vacío"}
                except Exception as exception:
                    return {"error": str(exception)}

        return {"error": "CBOM no generó dependencias o el archivo está vacío"}
    

    def filtered_libraries(self):
        """Método que filtra los componentes de tipo "library" de un fichero cbom generado"""
        with open(self.generated_cbom_path, 'r') as file:
            cbom_data = json.load(file)

        # Filtrado de componentes "library"
        library_components = [
        {"name": component.get("name"), "version": component.get("version")}
        for component in cbom_data.get("components", [])
        if component.get("type") == "library"
        ]

        output_path = os.path.join(self.cbom_path, "filtered_libraries.json")

        with open(output_path, 'w') as output_file:
            json.dump(library_components, output_file, indent=4)
