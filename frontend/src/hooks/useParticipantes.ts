import { useCallback, useEffect, useState } from 'react'
import type { CriarParticipantePayload, Participante } from '../types/participante'
import { participanteService } from '../services/participanteService'

export function useParticipantes() {
  const [participantes, setParticipantes] = useState<Participante[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const carregar = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      setParticipantes(await participanteService.listar())
    } catch {
      setError('Erro ao carregar participantes')
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => { carregar() }, [carregar])

  const criar = async (payload: CriarParticipantePayload) => {
    const novo = await participanteService.criar(payload)
    setParticipantes(prev => [...prev, novo])
    return novo
  }

  const atualizar = async (id: string, payload: CriarParticipantePayload) => {
    const atualizado = await participanteService.atualizar(id, payload)
    setParticipantes(prev => prev.map(p => p.id === id ? atualizado : p))
    return atualizado
  }

  const remover = async (id: string) => {
    await participanteService.remover(id)
    setParticipantes(prev => prev.filter(p => p.id !== id))
  }

  return { participantes, loading, error, criar, atualizar, remover, carregar }
}
