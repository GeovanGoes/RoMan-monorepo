import { useCallback, useEffect, useState } from 'react'
import type { CategoriaConsumo, CriarCategoriaPayload } from '../types/categoria'
import { categoriaService } from '../services/categoriaService'

export function useCategorias() {
  const [categorias, setCategorias] = useState<CategoriaConsumo[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const carregar = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      setCategorias(await categoriaService.listar())
    } catch {
      setError('Erro ao carregar categorias')
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => { carregar() }, [carregar])

  const criar = async (payload: CriarCategoriaPayload) => {
    const nova = await categoriaService.criar(payload)
    setCategorias(prev => [...prev, nova])
    return nova
  }

  const atualizar = async (id: string, payload: CriarCategoriaPayload) => {
    const atualizada = await categoriaService.atualizar(id, payload)
    setCategorias(prev => prev.map(c => c.id === id ? atualizada : c))
    return atualizada
  }

  const remover = async (id: string) => {
    await categoriaService.remover(id)
    setCategorias(prev => prev.filter(c => c.id !== id))
  }

  return { categorias, loading, error, criar, atualizar, remover, carregar }
}
