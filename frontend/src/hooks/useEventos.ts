import { useCallback, useEffect, useState } from 'react'
import type { CriarEventoPayload, Evento } from '../types/evento'
import { eventoService } from '../services/eventoService'

export function useEventos() {
  const [eventos, setEventos] = useState<Evento[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const carregar = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      setEventos(await eventoService.listar())
    } catch {
      setError('Erro ao carregar eventos')
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => { carregar() }, [carregar])

  const criar = async (payload: CriarEventoPayload) => {
    const novo = await eventoService.criar(payload)
    setEventos(prev => [...prev, novo])
    return novo
  }

  const atualizar = async (id: string, payload: CriarEventoPayload) => {
    const atualizado = await eventoService.atualizar(id, payload)
    setEventos(prev => prev.map(e => e.id === id ? atualizado : e))
    return atualizado
  }

  const remover = async (id: string) => {
    await eventoService.remover(id)
    setEventos(prev => prev.filter(e => e.id !== id))
  }

  return { eventos, loading, error, criar, atualizar, remover, carregar }
}
