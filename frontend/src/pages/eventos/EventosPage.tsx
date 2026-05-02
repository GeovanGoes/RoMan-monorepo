import { useState } from 'react'
import { Link } from 'react-router-dom'
import { EventoForm } from '../../components/evento/EventoForm'
import { Button } from '../../components/common/Button'
import { Modal } from '../../components/common/Modal'
import { Table } from '../../components/common/Table'
import { useEventos } from '../../hooks/useEventos'
import type { Evento } from '../../types/evento'

export function EventosPage() {
  const { eventos, loading, error, criar, atualizar, remover } = useEventos()
  const [modal, setModal] = useState<'criar' | 'editar' | null>(null)
  const [selecionado, setSelecionado] = useState<Evento | null>(null)

  const abrirEditar = (e: Evento) => { setSelecionado(e); setModal('editar') }
  const fecharModal = () => { setModal(null); setSelecionado(null) }

  return (
    <div className="p-6">
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold text-gray-800">Eventos</h1>
        <Button onClick={() => setModal('criar')}>+ Novo evento</Button>
      </div>

      {error && <p className="text-red-600 mb-4">{error}</p>}

      {loading ? (
        <p className="text-gray-500">Carregando...</p>
      ) : (
        <Table
          keyExtractor={e => e.id}
          rows={eventos}
          columns={[
            { header: 'Nome', render: e => <Link to={`/eventos/${e.id}`} className="text-indigo-600 hover:underline">{e.nome}</Link> },
            { header: 'Local', render: e => e.local },
            { header: 'Início', render: e => new Date(e.dataInicio).toLocaleDateString('pt-BR') },
            { header: 'Fim', render: e => new Date(e.dataFim).toLocaleDateString('pt-BR') },
            {
              header: 'Ações', render: e => (
                <div className="flex gap-2">
                  <Button variant="secondary" onClick={() => abrirEditar(e)}>Editar</Button>
                  <Button variant="danger" onClick={() => remover(e.id)}>Remover</Button>
                </div>
              )
            },
          ]}
        />
      )}

      <Modal title={modal === 'editar' ? 'Editar evento' : 'Novo evento'} open={modal !== null} onClose={fecharModal}>
        <EventoForm
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
