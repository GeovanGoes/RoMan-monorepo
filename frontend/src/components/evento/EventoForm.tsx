import { useState, type FormEvent } from 'react'
import type { CriarEventoPayload, Evento } from '../../types/evento'
import { Button } from '../common/Button'
import { Input } from '../common/Input'

interface EventoFormProps {
  initial?: Evento
  onSubmit: (payload: CriarEventoPayload) => Promise<void>
  onCancel: () => void
}

export function EventoForm({ initial, onSubmit, onCancel }: EventoFormProps) {
  const [nome, setNome] = useState(initial?.nome ?? '')
  const [local, setLocal] = useState(initial?.local ?? '')
  const [dataInicio, setDataInicio] = useState(initial?.dataInicio ?? '')
  const [dataFim, setDataFim] = useState(initial?.dataFim ?? '')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    setLoading(true)
    setError(null)
    try {
      await onSubmit({ nome, local, dataInicio, dataFim })
    } catch {
      setError('Erro ao salvar evento')
    } finally {
      setLoading(false)
    }
  }

  return (
    <form onSubmit={handleSubmit} className="flex flex-col gap-4">
      <Input id="nome" label="Nome do evento" value={nome} onChange={e => setNome(e.target.value)} required />
      <Input id="local" label="Local" value={local} onChange={e => setLocal(e.target.value)} required />
      <Input id="dataInicio" label="Data de início" type="date" value={dataInicio} onChange={e => setDataInicio(e.target.value)} required />
      <Input id="dataFim" label="Data de fim" type="date" value={dataFim} onChange={e => setDataFim(e.target.value)} required />
      {error && <p className="text-sm text-red-600">{error}</p>}
      <div className="flex gap-2 justify-end">
        <Button type="button" variant="secondary" onClick={onCancel}>Cancelar</Button>
        <Button type="submit" loading={loading}>{initial ? 'Salvar' : 'Criar'}</Button>
      </div>
    </form>
  )
}
