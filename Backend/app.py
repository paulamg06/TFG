from flask import Flask, request, jsonify
from flask_cors import CORS
from Utils.DTOs import *
from Utils.Methods import *

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
    
    cloned_path = process_git.cloneRepository()

    try:
        run_tools = RunTools(cloned_path)

        # Semgrep"
        #semgrep_response = run_tools.ProcessSemgrep()

        # Bandit
        bandit_response = run_tools.ProcessBandit()

        # CBOM
        cbom_dict_response = run_tools.ProcessCBOM()
        

        response = AnalyzeCryptoResponseDTO(
            semgrep_response = "semgrep_response.stdout",
            bandit_response = bandit_response,
            cbom_response = cbom_dict_response
        )

        return jsonify(response.model_dump())

    except Exception as exception:
        return jsonify({"error": str(exception)}), 500
    


if __name__ == "__main__":
    app.run(debug=True)
