import { useState } from 'react'
import { CategoriaForm } from '../../components/categoria/CategoriaForm'
import { Button } from '../../components/common/Button'
import { Modal } from '../../components/common/Modal'
import { Table } from '../../components/common/Table'
import { useCategorias } from '../../hooks/useCategorias'
import { useAuth } from '../../contexts/AuthContext'
import type { CategoriaConsumo } from '../../types/categoria'

export function CategoriasPage() {
  const { categorias, loading, error, criar, atualizar, remover } = useCategorias()
  const { isAdmin } = useAuth()
  const [modal, setModal] = useState<'criar' | 'editar' | null>(null)
  const [selecionada, setSelecionada] = useState<CategoriaConsumo | null>(null)

  const abrirEditar = (c: CategoriaConsumo) => { setSelecionada(c); setModal('editar') }
  const fecharModal = () => { setModal(null); setSelecionada(null) }

  return (
    <div className="p-6">
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold text-gray-800">Categorias de Consumo</h1>
        {isAdmin && <Button onClick={() => setModal('criar')}>+ Nova categoria</Button>}
      </div>

      {error && <p className="text-red-600 mb-4">{error}</p>}

      {loading ? (
        <p className="text-gray-500">Carregando...</p>
      ) : (
        <Table
          keyExtractor={c => c.id}
          rows={categorias}
          columns={[
            { header: 'Nome', render: c => c.nome },
            { header: 'Descrição', render: c => c.descricao ?? '—' },
            ...(isAdmin ? [{
              header: 'Ações', render: (c: CategoriaConsumo) => (
                <div className="flex gap-2">
                  <Button variant="secondary" onClick={() => abrirEditar(c)}>Editar</Button>
                  <Button variant="danger" onClick={() => remover(c.id)}>Remover</Button>
                </div>
              )
            }] : []),
          ]}
        />
      )}

      <Modal title={modal === 'editar' ? 'Editar categoria' : 'Nova categoria'} open={modal !== null} onClose={fecharModal}>
        <CategoriaForm
          initial={selecionada ?? undefined}
          onSubmit={async payload => {
            if (modal === 'editar' && selecionada) await atualizar(selecionada.id, payload)
            else await criar(payload)
            fecharModal()
          }}
          onCancel={fecharModal}
        />
      </Modal>
    </div>
  )
}
