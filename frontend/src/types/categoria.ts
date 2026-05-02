export interface CategoriaConsumo {
  id: string
  nome: string
  descricao: string | null
  createdAt: string
  updatedAt: string
}

export interface CriarCategoriaPayload {
  nome: string
  descricao?: string
}
