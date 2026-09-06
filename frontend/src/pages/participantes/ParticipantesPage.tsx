import { useState } from 'react'
import { ParticipanteForm } from '../../components/participante/ParticipanteForm'
import { Button } from '../../components/common/Button'
import { Modal } from '../../components/common/Modal'
import { Table } from '../../components/common/Table'
import { useParticipantes } from '../../hooks/useParticipantes'
import { useAuth } from '../../contexts/AuthContext'
import type { Participante } from '../../types/participante'

export function ParticipantesPage() {
  const { participantes, loading, error, criar, atualizar, remover } = useParticipantes()
  const { isAdmin } = useAuth()
  const [modal, setModal] = useState<'criar' | 'editar' | null>(null)
  const [selecionado, setSelecionado] = useState<Participante | null>(null)

  const abrirEditar = (p: Participante) => { setSelecionado(p); setModal('editar') }
  const fecharModal = () => { setModal(null); setSelecionado(null) }

  return (
    <div className="p-4 sm:p-6">
      <div className="flex flex-wrap items-center justify-between gap-3 mb-6">
        <h1 className="text-2xl font-bold text-gray-800">Participantes</h1>
        {isAdmin && <Button onClick={() => setModal('criar')}>+ Novo participante</Button>}
      </div>

      {error && <p className="text-red-600 mb-4">{error}</p>}

      {loading ? (
        <p className="text-gray-500">Carregando...</p>
      ) : (
        <Table
          keyExtractor={p => p.id}
          rows={participantes}
          columns={[
            { header: 'Nome', render: p => p.nome },
            { header: 'Username', render: p => <code className="text-xs bg-gray-100 px-1 rounded">@{p.username}</code> },
            { header: 'Cadastrado em', render: p => new Date(p.createdAt).toLocaleDateString('pt-BR') },
            ...(isAdmin ? [{
              header: 'Ações', render: (p: Participante) => (
                <div className="flex gap-2">
                  <Button variant="secondary" onClick={() => abrirEditar(p)}>Editar</Button>
                  <Button variant="danger" onClick={() => remover(p.id)}>Remover</Button>
                </div>
              )
            }] : []),
          ]}
        />
      )}

      <Modal title={modal === 'editar' ? 'Editar participante' : 'Novo participante'} open={modal !== null} onClose={fecharModal}>
        <ParticipanteForm
          initial={selecionado ?? undefined}
          onSubmit={async payload => {
            if (modal === 'editar' && selecionado) await atualizar(selecionado.id, payload)
            else await criar(payload)
            fecharModal()
          }}
          onCancel={fecharModal}
        />
      </Modal>
    </div>
  )
}
