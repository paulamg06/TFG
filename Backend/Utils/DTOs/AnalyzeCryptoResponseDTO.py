from pydantic import BaseModel

class AnalyzeCryptoResponseDTO(BaseModel):
    cbom_response: dict
