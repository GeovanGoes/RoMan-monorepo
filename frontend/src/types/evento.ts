export interface Evento {
  id: string
  nome: string
  local: string
  dataInicio: string
  dataFim: string
  createdAt: string
  updatedAt: string
}

export interface EventoParticipante {
  id: string
  eventoId: string
  participanteId: string
  menorDeIdade: boolean
  categoriasExcluidas: string[]
  createdAt: string
}

export interface Compra {
  id: string
  descricao: string
  valor: number
  eventoId: string
  categoriaId: string
  pagadoresIds: string[]
  createdAt: string
  updatedAt: string
}

export interface CriarEventoPayload {
  nome: string
  local: string
  dataInicio: string
  dataFim: string
}

export interface AdicionarCompraPayload {
  descricao: string
  valor: number
  categoriaId: string
  pagadoresIds: string[]
}

export interface RateioItem {
  participanteId: string
  nomeParticipante: string
  totalDevido: number
  totalPago: number
  saldo: number
}
