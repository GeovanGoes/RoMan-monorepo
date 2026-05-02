export interface Participante {
  id: string
  nome: string
  username: string
  createdAt: string
  updatedAt: string
}

export interface CriarParticipantePayload {
  nome: string
  username: string
}
