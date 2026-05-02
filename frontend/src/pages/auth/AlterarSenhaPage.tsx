import { useState } from 'react'
import { useNavigate, useLocation } from 'react-router-dom'
import { authService } from '../../services/authService'
import { useAuth } from '../../contexts/AuthContext'
import { Button } from '../../components/common/Button'
import { Input } from '../../components/common/Input'

export function AlterarSenhaPage() {
  const navigate = useNavigate()
  const location = useLocation()
  const obrigatorio = (location.state as { obrigatorio?: boolean })?.obrigatorio ?? false
  const { markPasswordChanged } = useAuth()

  const [senhaAtual, setSenhaAtual] = useState('')
  const [novaSenha, setNovaSenha] = useState('')
  const [confirmar, setConfirmar] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (novaSenha !== confirmar) {
      setError('As senhas não coincidem.')
      return
    }
    if (novaSenha.length < 8 || !/\d/.test(novaSenha)) {
      setError('A senha deve ter pelo menos 8 caracteres e ao menos um número.')
      return
    }
    setError(null)
    setLoading(true)
    try {
      await authService.alterarSenha({ senhaAtual, novaSenha })
      markPasswordChanged()
      navigate('/', { replace: true })
    } catch (err: unknown) {
      const axiosErr = err as { response?: { data?: { detail?: string } } }
      const detail = axiosErr?.response?.data?.detail
      setError(detail ?? 'Erro ao alterar senha.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center">
      <div className="bg-white rounded-2xl shadow-md p-8 w-full max-w-sm">
        <h2 className="text-xl font-bold text-gray-800 mb-2">Alterar senha</h2>

        {obrigatorio && (
          <div className="bg-amber-50 border border-amber-200 rounded-lg p-3 mb-4 text-sm text-amber-800">
            Você deve alterar sua senha antes de continuar.
          </div>
        )}

        <form onSubmit={handleSubmit} className="flex flex-col gap-4">
          <Input
            id="senhaAtual"
            label="Senha atual"
            type="password"
            value={senhaAtual}
            onChange={e => setSenhaAtual(e.target.value)}
            autoComplete="current-password"
            required
          />
          <Input
            id="novaSenha"
            label="Nova senha"
            type="password"
            value={novaSenha}
            onChange={e => setNovaSenha(e.target.value)}
            autoComplete="new-password"
            required
          />
          <Input
            id="confirmar"
            label="Confirmar nova senha"
            type="password"
            value={confirmar}
            onChange={e => setConfirmar(e.target.value)}
            autoComplete="new-password"
            required
          />

          {error && <p className="text-sm text-red-600">{error}</p>}

          <Button type="submit" loading={loading}>Alterar senha</Button>
        </form>
      </div>
    </div>
  )
}
