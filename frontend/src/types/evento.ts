export interface Evento {
  id: string
  nome: string
  local: string
  dataInicio: string
  dataFim: string
  createdAt: string
  updatedAt: string
}

export interface CategoriaExcluida {
  id: string
  nome: string
}

export interface EventoParticipante {
  id: string
  eventoId: string
  usuarioId: string
  nomeUsuario: string
  usernameUsuario: string
  menorDeIdade: boolean
  categoriasExcluidas: CategoriaExcluida[]
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
