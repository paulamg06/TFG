from pydantic import BaseModel

class AnalyzeCryptoRequestDTO(BaseModel):
    """Data Transfer Object de la entrada de la API analyzeCrypto"""
    gitHub_repo: str
