import api from './api'
import type { CategoriaConsumo, CriarCategoriaPayload } from '../types/categoria'

export const categoriaService = {
  listar: () => api.get<CategoriaConsumo[]>('/categorias').then(r => r.data),
  buscar: (id: string) => api.get<CategoriaConsumo>(`/categorias/${id}`).then(r => r.data),
  criar: (payload: CriarCategoriaPayload) => api.post<CategoriaConsumo>('/categorias', payload).then(r => r.data),
  atualizar: (id: string, payload: CriarCategoriaPayload) =>
    api.put<CategoriaConsumo>(`/categorias/${id}`, payload).then(r => r.data),
  remover: (id: string) => api.delete(`/categorias/${id}`),
}
