from pydantic import BaseModel

class AnalyzeCryptoResponseDTO(BaseModel):
    semgrep_response: str
    bandit_response: dict
    cbom_response: dict
