import { useCallback, useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import { Button } from '../../components/common/Button'
import { Table } from '../../components/common/Table'
import { Modal } from '../../components/common/Modal'
import { eventoService } from '../../services/eventoService'
import { participanteService } from '../../services/participanteService'
import { categoriaService } from '../../services/categoriaService'
import { useAuth } from '../../contexts/AuthContext'
import type { Evento, EventoParticipante, CategoriaExcluida, Compra, AdicionarCompraPayload, RateioItem, TransferenciaSugerida } from '../../types/evento'
import type { Participante } from '../../types/participante'
import type { CategoriaConsumo } from '../../types/categoria'

interface VincularForm {
  participanteId: string
  menorDeIdade: boolean
  exclusoes: string[]
}

interface GerenciarForm {
  ep: EventoParticipante
  exclusoes: string[]
}

export function EventoDetailPage() {
  const { id } = useParams<{ id: string }>()
  const { isAdmin } = useAuth()
  const [evento, setEvento] = useState<Evento | null>(null)
  const [participantesEvento, setParticipantesEvento] = useState<EventoParticipante[]>([])
  const [compras, setCompras] = useState<Compra[]>([])
  const [todosParticipantes, setTodosParticipantes] = useState<Participante[]>([])
  const [categorias, setCategorias] = useState<CategoriaConsumo[]>([])
  const [loading, setLoading] = useState(true)

  const [modalVincular, setModalVincular] = useState(false)
  const [vincularForm, setVincularForm] = useState<VincularForm>({ participanteId: '', menorDeIdade: false, exclusoes: [] })
  const [vincularLoading, setVincularLoading] = useState(false)
  const [vincularError, setVincularError] = useState<string | null>(null)

  const [gerenciar, setGerenciar] = useState<GerenciarForm | null>(null)
  const [gerenciarLoading, setGerenciarLoading] = useState(false)
  const [gerenciarError, setGerenciarError] = useState<string | null>(null)

  const [modalCompra, setModalCompra] = useState(false)
  const [compraForm, setCompraForm] = useState<AdicionarCompraPayload>({ descricao: '', valor: 0, categoriaId: '', pagadoresIds: [] })

  const [rateio, setRateio] = useState<RateioItem[] | null>(null)
  const [transferencias, setTransferencias] = useState<TransferenciaSugerida[] | null>(null)
  const [rateioLoading, setRateioLoading] = useState(false)

  const carregar = useCallback(async () => {
    if (!id) return
    setLoading(true)
    try {
      const [ev, eps, cs, ps, cats] = await Promise.all([
        eventoService.buscar(id),
        eventoService.listarParticipantes(id),
        eventoService.listarCompras(id),
        participanteService.listar(),
        categoriaService.listar(),
      ])
      setEvento(ev); setParticipantesEvento(eps); setCompras(cs)
      setTodosParticipantes(ps); setCategorias(cats)
    } finally {
      setLoading(false)
    }
  }, [id])

  useEffect(() => { carregar() }, [carregar])

  if (!id) return null
  if (loading) return <p className="p-4 sm:p-6 text-gray-500">Carregando...</p>
  if (!evento) return <p className="p-4 sm:p-6 text-red-600">Evento não encontrado.</p>

  const participantesVinculadosIds = new Set(participantesEvento.map(ep => ep.usuarioId))
  const disponiveis = todosParticipantes.filter(p => !participantesVinculadosIds.has(p.id))
  const nomeParticipante = (pid: string) => todosParticipantes.find(p => p.id === pid)?.nome ?? pid
  const nomeCategoria = (cid: string) => categorias.find(c => c.id === cid)?.nome ?? cid

  const abrirVincular = () => {
    setVincularForm({ participanteId: disponiveis[0]?.id ?? '', menorDeIdade: false, exclusoes: [] })
    setVincularError(null)
    setModalVincular(true)
  }

  const confirmarVincular = async () => {
    if (!vincularForm.participanteId) return
    setVincularLoading(true)
    setVincularError(null)
    try {
      const ep = await eventoService.vincularParticipante(id, vincularForm.participanteId, vincularForm.menorDeIdade)
      await Promise.all(
        vincularForm.exclusoes.map(catId => eventoService.adicionarExclusao(id, vincularForm.participanteId, catId))
      )
      const participante = todosParticipantes.find(p => p.id === vincularForm.participanteId)
      const catObjects: CategoriaExcluida[] = vincularForm.exclusoes.map(catId => ({
        id: catId,
        nome: categorias.find(c => c.id === catId)?.nome ?? '',
      }))
      const epEnriquecido: EventoParticipante = {
        ...ep,
        nomeUsuario: participante?.nome ?? '',
        usernameUsuario: participante?.username ?? '',
        categoriasExcluidas: catObjects,
      }
      setParticipantesEvento(prev => [...prev, epEnriquecido])
      setRateio(null)
      setTransferencias(null)
      setModalVincular(false)
    } catch {
      setVincularError('Erro ao vincular participante')
    } finally {
      setVincularLoading(false)
    }
  }

  const toggleExclusaoVincular = (catId: string) => {
    setVincularForm(f => ({
      ...f,
      exclusoes: f.exclusoes.includes(catId) ? f.exclusoes.filter(c => c !== catId) : [...f.exclusoes, catId],
    }))
  }

  const desvincular = async (usuarioId: string) => {
    await eventoService.desvincularParticipante(id, usuarioId)
    setParticipantesEvento(prev => prev.filter(ep => ep.usuarioId !== usuarioId))
    setRateio(null)
    setTransferencias(null)
  }

  const abrirGerenciar = (ep: EventoParticipante) => {
    setGerenciar({ ep, exclusoes: ep.categoriasExcluidas.map(c => c.id) })
    setGerenciarError(null)
  }

  const confirmarGerenciar = async () => {
    if (!gerenciar) return
    setGerenciarLoading(true)
    setGerenciarError(null)
    const { ep, exclusoes } = gerenciar
    const anterioresIds = ep.categoriasExcluidas.map(c => c.id)
    const adicionar = exclusoes.filter(c => !anterioresIds.includes(c))
    const remover = anterioresIds.filter(c => !exclusoes.includes(c))
    try {
      await Promise.all([
        ...adicionar.map(c => eventoService.adicionarExclusao(id, ep.usuarioId, c)),
        ...remover.map(c => eventoService.removerExclusao(id, ep.usuarioId, c)),
      ])
      const novasCats: CategoriaExcluida[] = exclusoes.map(cId => ({
        id: cId,
        nome: categorias.find(c => c.id === cId)?.nome ?? '',
      }))
      setParticipantesEvento(prev =>
        prev.map(p => p.id === ep.id ? { ...p, categoriasExcluidas: novasCats } : p)
      )
      setRateio(null)
      setTransferencias(null)
      setGerenciar(null)
    } catch {
      setGerenciarError('Erro ao salvar exclusões')
    } finally {
      setGerenciarLoading(false)
    }
  }

  const toggleExclusaoGerenciar = (catId: string) => {
    setGerenciar(g => {
      if (!g) return g
      return {
        ...g,
        exclusoes: g.exclusoes.includes(catId) ? g.exclusoes.filter(c => c !== catId) : [...g.exclusoes, catId],
      }
    })
  }

  const adicionarCompra = async () => {
    const compra = await eventoService.adicionarCompra(id, compraForm)
    setCompras(prev => [...prev, compra])
    setModalCompra(false)
    setCompraForm({ descricao: '', valor: 0, categoriaId: '', pagadoresIds: [] })
    setRateio(null)
    setTransferencias(null)
  }

  const removerCompra = async (compraId: string) => {
    await eventoService.removerCompra(id, compraId)
    setCompras(prev => prev.filter(c => c.id !== compraId))
    setRateio(null)
    setTransferencias(null)
  }

  const calcularRateio = async () => {
    setRateioLoading(true)
    try {
      const [resultadoRateio, resultadoTransferencias] = await Promise.all([
        eventoService.calcularRateio(id),
        eventoService.simplificarDividas(id),
      ])
      setRateio(resultadoRateio)
      setTransferencias(resultadoTransferencias)
    } finally {
      setRateioLoading(false)
    }
  }

  return (
    <div className="p-4 sm:p-6 space-y-8">
      <div>
        <Link to="/eventos" className="text-sm text-indigo-600 hover:underline">&larr; Voltar</Link>
        <h1 className="text-2xl font-bold text-gray-800 mt-2">{evento.nome}</h1>
        <p className="text-gray-500 text-sm">{evento.local} &bull; {new Date(evento.dataInicio).toLocaleDateString('pt-BR')} – {new Date(evento.dataFim).toLocaleDateString('pt-BR')}</p>
      </div>

      {/* Participantes */}
      <section>
        <div className="flex flex-wrap items-center justify-between gap-3 mb-3">
          <h2 className="text-lg font-semibold text-gray-700">Participantes</h2>
          {isAdmin && disponiveis.length > 0 && (
            <Button onClick={abrirVincular}>+ Vincular participante</Button>
          )}
        </div>
        <Table
          keyExtractor={ep => ep.id}
          rows={participantesEvento}
          emptyMessage="Nenhum participante vinculado."
          columns={[
            { header: 'Nome', render: ep => ep.nomeUsuario },
            { header: 'Menor de idade', render: ep => ep.menorDeIdade ? 'Sim' : 'Não' },
            { header: 'Não consome', render: ep => ep.categoriasExcluidas.map(c => c.nome).join(', ') || '—' },
            ...(isAdmin ? [{
              header: 'Ações', render: (ep: EventoParticipante) => (
                <div className="flex gap-2">
                  <Button variant="secondary" onClick={() => abrirGerenciar(ep)}>Gerenciar</Button>
                  <Button variant="danger" onClick={() => desvincular(ep.usuarioId)}>Desvincular</Button>
                </div>
              )
            }] : []),
          ]}
        />
      </section>

      {/* Compras */}
      <section>
        <div className="flex flex-wrap items-center justify-between gap-3 mb-3">
          <h2 className="text-lg font-semibold text-gray-700">Compras</h2>
          {isAdmin && <Button onClick={() => setModalCompra(true)}>+ Adicionar compra</Button>}
        </div>
        <Table
          keyExtractor={c => c.id}
          rows={compras}
          emptyMessage="Nenhuma compra registrada."
          columns={[
            { header: 'Descrição', render: c => c.descricao },
            { header: 'Categoria', render: c => nomeCategoria(c.categoriaId) },
            { header: 'Valor', render: c => `R$ ${c.valor.toFixed(2)}` },
            { header: 'Pago por', render: c => c.pagadoresIds.map(nomeParticipante).join(', ') },
            ...(isAdmin ? [{
              header: 'Ações', render: (c: Compra) => (
                <Button variant="danger" onClick={() => removerCompra(c.id)}>Remover</Button>
              )
            }] : []),
          ]}
        />
      </section>

      {/* Rateio */}
      <section>
        <div className="flex flex-wrap items-center justify-between gap-3 mb-3">
          <h2 className="text-lg font-semibold text-gray-700">Rateio</h2>
          <Button onClick={calcularRateio} loading={rateioLoading}>Calcular</Button>
        </div>
        {rateio === null ? (
          <p className="text-sm text-gray-400">Clique em "Calcular" para ver o rateio atualizado.</p>
        ) : rateio.length === 0 ? (
          <p className="text-sm text-gray-400">Nenhum participante vinculado.</p>
        ) : (
          <Table
            keyExtractor={r => r.usuarioId}
            rows={rateio}
            emptyMessage=""
            columns={[
              { header: 'Participante', render: r => r.nomeParticipante },
              { header: 'Total devido', render: r => `R$ ${r.totalDevido.toFixed(2)}` },
              { header: 'Total pago', render: r => `R$ ${r.totalPago.toFixed(2)}` },
              {
                header: 'Situação', render: r => {
                  if (r.saldo > 0.005) return <span className="text-green-600 font-medium">A receber R$ {r.saldo.toFixed(2)}</span>
                  if (r.saldo < -0.005) return <span className="text-red-600 font-medium">Deve pagar R$ {Math.abs(r.saldo).toFixed(2)}</span>
                  return <span className="text-gray-400">Quitado</span>
                }
              },
            ]}
          />
        )}

        {transferencias !== null && (
          <div className="mt-6">
            <h3 className="text-md font-semibold text-gray-700 mb-3">Quem deve para quem</h3>
            {transferencias.length === 0 ? (
              <p className="text-sm text-gray-400">Nenhuma transferência necessária — contas já quitadas.</p>
            ) : (
              <Table
                keyExtractor={t => `${t.deId}-${t.paraId}`}
                rows={transferencias}
                emptyMessage=""
                columns={[
                  { header: 'De', render: t => t.nomeDe },
                  { header: 'Para', render: t => t.nomePara },
                  { header: 'Valor', render: t => `R$ ${t.valor.toFixed(2)}` },
                ]}
              />
            )}
          </div>
        )}
      </section>

      {/* Modal: Vincular participante */}
      <Modal title="Vincular participante" open={modalVincular} onClose={() => setModalVincular(false)}>
        <div className="flex flex-col gap-4">
          <div className="flex flex-col gap-1">
            <label className="text-sm font-medium text-gray-700">Participante</label>
            <select
              className="border rounded-lg px-3 py-2 text-sm w-full min-w-0"
              value={vincularForm.participanteId}
              onChange={e => setVincularForm(f => ({ ...f, participanteId: e.target.value }))}
            >
              {disponiveis.map(p => <option key={p.id} value={p.id}>{p.nome} (@{p.username})</option>)}
            </select>
          </div>
          <label className="flex items-center gap-2 text-sm font-medium text-gray-700">
            <input
              type="checkbox"
              checked={vincularForm.menorDeIdade}
              onChange={e => setVincularForm(f => ({ ...f, menorDeIdade: e.target.checked }))}
            />
            Menor de idade
          </label>
          {categorias.length > 0 && (
            <div className="flex flex-col gap-1">
              <span className="text-sm font-medium text-gray-700">Não consome</span>
              {categorias.map(cat => (
                <label key={cat.id} className="flex items-center gap-2 text-sm text-gray-600">
                  <input
                    type="checkbox"
                    checked={vincularForm.exclusoes.includes(cat.id)}
                    onChange={() => toggleExclusaoVincular(cat.id)}
                  />
                  {cat.nome}
                </label>
              ))}
            </div>
          )}
          {vincularError && <p className="text-sm text-red-600">{vincularError}</p>}
          <div className="flex gap-2 justify-end">
            <Button variant="secondary" onClick={() => setModalVincular(false)}>Cancelar</Button>
            <Button loading={vincularLoading} onClick={confirmarVincular}>Vincular</Button>
          </div>
        </div>
      </Modal>

      {/* Modal: Gerenciar exclusões */}
      <Modal
        title={`Gerenciar — ${gerenciar ? gerenciar.ep.nomeUsuario : ''}`}
        open={gerenciar !== null}
        onClose={() => setGerenciar(null)}
      >
        {gerenciar && (
          <div className="flex flex-col gap-4">
            {categorias.length > 0 ? (
              <div className="flex flex-col gap-1">
                <span className="text-sm font-medium text-gray-700">Não consome</span>
                {categorias.map(cat => (
                  <label key={cat.id} className="flex items-center gap-2 text-sm text-gray-600">
                    <input
                      type="checkbox"
                      checked={gerenciar.exclusoes.includes(cat.id)}
                      onChange={() => toggleExclusaoGerenciar(cat.id)}
                    />
                    {cat.nome}
                  </label>
                ))}
              </div>
            ) : (
              <p className="text-sm text-gray-500">Nenhuma categoria cadastrada.</p>
            )}
            {gerenciarError && <p className="text-sm text-red-600">{gerenciarError}</p>}
            <div className="flex gap-2 justify-end">
              <Button variant="secondary" onClick={() => setGerenciar(null)}>Cancelar</Button>
              <Button loading={gerenciarLoading} onClick={confirmarGerenciar}>Salvar</Button>
            </div>
          </div>
        )}
      </Modal>

      {/* Modal: Adicionar compra */}
      <Modal title="Adicionar compra" open={modalCompra} onClose={() => setModalCompra(false)}>
        <div className="flex flex-col gap-4">
          <div className="flex flex-col gap-1">
            <label className="text-sm font-medium text-gray-700">Descrição</label>
            <input className="border rounded-lg px-3 py-2 text-sm" value={compraForm.descricao} onChange={e => setCompraForm(f => ({ ...f, descricao: e.target.value }))} />
          </div>
          <div className="flex flex-col gap-1">
            <label className="text-sm font-medium text-gray-700">Valor (R$)</label>
            <input type="number" step="0.01" className="border rounded-lg px-3 py-2 text-sm" value={compraForm.valor} onChange={e => setCompraForm(f => ({ ...f, valor: parseFloat(e.target.value) }))} />
          </div>
          <div className="flex flex-col gap-1">
            <label className="text-sm font-medium text-gray-700">Categoria</label>
            <select className="border rounded-lg px-3 py-2 text-sm w-full min-w-0" value={compraForm.categoriaId} onChange={e => setCompraForm(f => ({ ...f, categoriaId: e.target.value }))}>
              <option value="">Selecione...</option>
              {categorias.map(c => <option key={c.id} value={c.id}>{c.nome}</option>)}
            </select>
          </div>
          <div className="flex flex-col gap-1">
            <label className="text-sm font-medium text-gray-700">Pagadores</label>
            {participantesEvento.map(ep => (
              <label key={ep.id} className="flex items-center gap-2 text-sm">
                <input type="checkbox" checked={compraForm.pagadoresIds.includes(ep.usuarioId)}
                  onChange={e => setCompraForm(f => ({
                    ...f,
                    pagadoresIds: e.target.checked
                      ? [...f.pagadoresIds, ep.usuarioId]
                      : f.pagadoresIds.filter(pid => pid !== ep.usuarioId)
                  }))} />
                {ep.nomeUsuario}
              </label>
            ))}
          </div>
          <div className="flex gap-2 justify-end">
            <Button variant="secondary" onClick={() => setModalCompra(false)}>Cancelar</Button>
            <Button onClick={adicionarCompra}>Adicionar</Button>
          </div>
        </div>
      </Modal>
    </div>
  )
}
