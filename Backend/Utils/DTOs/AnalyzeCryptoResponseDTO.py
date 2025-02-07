from pydantic import BaseModel
import subprocess

class AnalyzeCryptoResponseDTO(BaseModel):
    semgrep_response: str
    bandit_response: str
    cbom_response: dict