from pydantic import BaseModel

class AnalyzeCryptoRequestDTO(BaseModel):
    """Data Transfer Object de la entrada de la API analyzeCrypto"""
    github_repo: str
