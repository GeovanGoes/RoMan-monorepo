import api from './api'
import type { CriarParticipantePayload, Participante } from '../types/participante'

export const participanteService = {
  listar: () => api.get<Participante[]>('/participantes').then(r => r.data),
  buscar: (id: string) => api.get<Participante>(`/participantes/${id}`).then(r => r.data),
  criar: (payload: CriarParticipantePayload) => api.post<Participante>('/participantes', payload).then(r => r.data),
  atualizar: (id: string, payload: CriarParticipantePayload) =>
    api.put<Participante>(`/participantes/${id}`, payload).then(r => r.data),
  remover: (id: string) => api.delete(`/participantes/${id}`),
}
