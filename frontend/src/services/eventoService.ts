import api from './api'
import type { AdicionarCompraPayload, Compra, CriarEventoPayload, Evento, EventoParticipante, RateioItem, TransferenciaSugerida } from '../types/evento'

export const eventoService = {
  listar: () => api.get<Evento[]>('/eventos').then(r => r.data),
  buscar: (id: string) => api.get<Evento>(`/eventos/${id}`).then(r => r.data),
  criar: (payload: CriarEventoPayload) => api.post<Evento>('/eventos', payload).then(r => r.data),
  atualizar: (id: string, payload: CriarEventoPayload) =>
    api.put<Evento>(`/eventos/${id}`, payload).then(r => r.data),
  remover: (id: string) => api.delete(`/eventos/${id}`),

  listarParticipantes: (eventoId: string) =>
    api.get<EventoParticipante[]>(`/eventos/${eventoId}/participantes`).then(r => r.data),
  vincularParticipante: (eventoId: string, participanteId: string, menorDeIdade: boolean) =>
    api.post<EventoParticipante>(`/eventos/${eventoId}/participantes/${participanteId}`, { menorDeIdade }).then(r => r.data),
  desvincularParticipante: (eventoId: string, participanteId: string) =>
    api.delete(`/eventos/${eventoId}/participantes/${participanteId}`),

  adicionarExclusao: (eventoId: string, participanteId: string, categoriaId: string) =>
    api.post<EventoParticipante>(`/eventos/${eventoId}/participantes/${participanteId}/exclusoes/${categoriaId}`).then(r => r.data),
  removerExclusao: (eventoId: string, participanteId: string, categoriaId: string) =>
    api.delete(`/eventos/${eventoId}/participantes/${participanteId}/exclusoes/${categoriaId}`),

  listarCompras: (eventoId: string) =>
    api.get<Compra[]>(`/eventos/${eventoId}/compras`).then(r => r.data),
  adicionarCompra: (eventoId: string, payload: AdicionarCompraPayload) =>
    api.post<Compra>(`/eventos/${eventoId}/compras`, payload).then(r => r.data),
  removerCompra: (eventoId: string, compraId: string) =>
    api.delete(`/eventos/${eventoId}/compras/${compraId}`),

  calcularRateio: (eventoId: string) =>
    api.get<RateioItem[]>(`/eventos/${eventoId}/rateio`).then(r => r.data),
  simplificarDividas: (eventoId: string) =>
    api.get<TransferenciaSugerida[]>(`/eventos/${eventoId}/rateio/simplificado`).then(r => r.data),
}
