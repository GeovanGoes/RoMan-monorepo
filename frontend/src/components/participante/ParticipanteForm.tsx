import { useState, type FormEvent } from 'react'
import type { CriarParticipantePayload, Participante } from '../../types/participante'
import { Button } from '../common/Button'
import { Input } from '../common/Input'

interface ParticipanteFormProps {
  initial?: Participante
  onSubmit: (payload: CriarParticipantePayload) => Promise<void>
  onCancel: () => void
}

export function ParticipanteForm({ initial, onSubmit, onCancel }: ParticipanteFormProps) {
  const [nome, setNome] = useState(initial?.nome ?? '')
  const [username, setUsername] = useState(initial?.username ?? '')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    setLoading(true)
    setError(null)
    try {
      await onSubmit({ nome, username })
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : 'Erro ao salvar'
      setError(msg)
    } finally {
      setLoading(false)
    }
  }

  return (
    <form onSubmit={handleSubmit} className="flex flex-col gap-4">
      <Input id="nome" label="Nome" value={nome} onChange={e => setNome(e.target.value)} required />
      <Input id="username" label="Username" value={username} onChange={e => setUsername(e.target.value)} required />
      {error && <p className="text-sm text-red-600">{error}</p>}
      <div className="flex gap-2 justify-end">
        <Button type="button" variant="secondary" onClick={onCancel}>Cancelar</Button>
        <Button type="submit" loading={loading}>{initial ? 'Salvar' : 'Criar'}</Button>
      </div>
    </form>
  )
}
