import { useState, type FormEvent } from 'react'
import type { CategoriaConsumo, CriarCategoriaPayload } from '../../types/categoria'
import { Button } from '../common/Button'
import { Input } from '../common/Input'

interface CategoriaFormProps {
  initial?: CategoriaConsumo
  onSubmit: (payload: CriarCategoriaPayload) => Promise<void>
  onCancel: () => void
}

export function CategoriaForm({ initial, onSubmit, onCancel }: CategoriaFormProps) {
  const [nome, setNome] = useState(initial?.nome ?? '')
  const [descricao, setDescricao] = useState(initial?.descricao ?? '')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    setLoading(true)
    setError(null)
    try {
      await onSubmit({ nome, descricao: descricao || undefined })
    } catch {
      setError('Erro ao salvar categoria')
    } finally {
      setLoading(false)
    }
  }

  return (
    <form onSubmit={handleSubmit} className="flex flex-col gap-4">
      <Input id="nome" label="Nome da categoria" value={nome} onChange={e => setNome(e.target.value)} required />
      <Input id="descricao" label="Descrição (opcional)" value={descricao} onChange={e => setDescricao(e.target.value)} />
      {error && <p className="text-sm text-red-600">{error}</p>}
      <div className="flex gap-2 justify-end">
        <Button type="button" variant="secondary" onClick={onCancel}>Cancelar</Button>
        <Button type="submit" loading={loading}>{initial ? 'Salvar' : 'Criar'}</Button>
      </div>
    </form>
  )
}
