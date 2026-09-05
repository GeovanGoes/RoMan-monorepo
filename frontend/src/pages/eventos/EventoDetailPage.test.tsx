import { render, screen, within } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { MemoryRouter, Routes, Route } from 'react-router-dom'
import { EventoDetailPage } from './EventoDetailPage'
import { eventoService } from '../../services/eventoService'
import { participanteService } from '../../services/participanteService'
import { categoriaService } from '../../services/categoriaService'
import { useAuth } from '../../contexts/AuthContext'

// jest.mock com factory: evita que o Jest precise carregar os módulos reais
// (que importam `api.ts`, com sintaxe `import.meta.env` do Vite não suportada pelo ts-jest).
jest.mock('../../services/eventoService', () => ({
  eventoService: {
    listar: jest.fn(),
    buscar: jest.fn(),
    criar: jest.fn(),
    atualizar: jest.fn(),
    remover: jest.fn(),
    listarParticipantes: jest.fn(),
    vincularParticipante: jest.fn(),
    desvincularParticipante: jest.fn(),
    adicionarExclusao: jest.fn(),
    removerExclusao: jest.fn(),
    listarCompras: jest.fn(),
    adicionarCompra: jest.fn(),
    removerCompra: jest.fn(),
    calcularRateio: jest.fn(),
    simplificarDividas: jest.fn(),
  },
}))
jest.mock('../../services/participanteService', () => ({
  participanteService: {
    listar: jest.fn(),
    buscar: jest.fn(),
    criar: jest.fn(),
    atualizar: jest.fn(),
    remover: jest.fn(),
  },
}))
jest.mock('../../services/categoriaService', () => ({
  categoriaService: {
    listar: jest.fn(),
    buscar: jest.fn(),
    criar: jest.fn(),
    atualizar: jest.fn(),
    remover: jest.fn(),
  },
}))
jest.mock('../../contexts/AuthContext', () => ({
  useAuth: jest.fn(),
}))

const eventoServiceMock = eventoService as jest.Mocked<typeof eventoService>
const participanteServiceMock = participanteService as jest.Mocked<typeof participanteService>
const categoriaServiceMock = categoriaService as jest.Mocked<typeof categoriaService>
const useAuthMock = useAuth as jest.Mock

const EVENTO_ID = '11111111-1111-1111-1111-111111111111'

function renderPage() {
  return render(
    <MemoryRouter initialEntries={[`/eventos/${EVENTO_ID}`]}>
      <Routes>
        <Route path="/eventos/:id" element={<EventoDetailPage />} />
      </Routes>
    </MemoryRouter>
  )
}

beforeEach(() => {
  jest.clearAllMocks()
  useAuthMock.mockReturnValue({ isAdmin: false })

  eventoServiceMock.buscar.mockResolvedValue({
    id: EVENTO_ID,
    nome: 'Churrasco',
    local: 'Praia',
    dataInicio: '2026-06-01',
    dataFim: '2026-06-02',
    createdAt: '2026-01-01',
    updatedAt: '2026-01-01',
  })
  eventoServiceMock.listarParticipantes.mockResolvedValue([])
  eventoServiceMock.listarCompras.mockResolvedValue([])
  participanteServiceMock.listar.mockResolvedValue([])
  categoriaServiceMock.listar.mockResolvedValue([])
})

describe('EventoDetailPage — Quem deve para quem', () => {
  it('exibe as transferências sugeridas após clicar em Calcular', async () => {
    eventoServiceMock.calcularRateio.mockResolvedValue([
      { usuarioId: 'a', nomeParticipante: 'Ana', totalDevido: 0, totalPago: 50, saldo: 50 },
      { usuarioId: 'b', nomeParticipante: 'Bruno', totalDevido: 50, totalPago: 0, saldo: -50 },
    ])
    eventoServiceMock.simplificarDividas.mockResolvedValue([
      { deId: 'b', nomeDe: 'Bruno', paraId: 'a', nomePara: 'Ana', valor: 50 },
    ])

    renderPage()
    const user = userEvent.setup()

    await screen.findByText('Churrasco')
    await user.click(screen.getByRole('button', { name: 'Calcular' }))

    const titulo = await screen.findByText('Quem deve para quem')
    const secaoTransferencias = titulo.closest('div') as HTMLElement
    expect(within(secaoTransferencias).getByText('Bruno')).toBeInTheDocument()
    expect(within(secaoTransferencias).getByText('Ana')).toBeInTheDocument()
    expect(within(secaoTransferencias).getByText('R$ 50.00')).toBeInTheDocument()
  })

  it('mostra mensagem de contas quitadas quando não há transferências', async () => {
    eventoServiceMock.calcularRateio.mockResolvedValue([])
    eventoServiceMock.simplificarDividas.mockResolvedValue([])

    renderPage()
    const user = userEvent.setup()

    await screen.findByText('Churrasco')
    await user.click(screen.getByRole('button', { name: 'Calcular' }))

    expect(await screen.findByText('Nenhuma transferência necessária — contas já quitadas.')).toBeInTheDocument()
  })
})
