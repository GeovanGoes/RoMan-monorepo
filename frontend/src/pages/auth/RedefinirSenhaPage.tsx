import { useState } from 'react'
import { Link, useNavigate, useSearchParams } from 'react-router-dom'
import { authService } from '../../services/authService'
import { Button } from '../../components/common/Button'
import { Input } from '../../components/common/Input'

export function RedefinirSenhaPage() {
  const [searchParams] = useSearchParams()
  const token = searchParams.get('token') ?? ''
  const navigate = useNavigate()

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
    if (!token) {
      setError('Token inválido.')
      return
    }
    setError(null)
    setLoading(true)
    try {
      await authService.redefinirSenha(token, novaSenha)
      navigate('/auth/login', { state: { message: 'Senha redefinida com sucesso. Faça login.' } })
    } catch (err: unknown) {
      const axiosErr = err as { response?: { data?: { detail?: string } } }
      const detail = axiosErr?.response?.data?.detail
      setError(detail ?? 'Token inválido ou expirado.')
    } finally {
      setLoading(false)
    }
  }

  if (!token) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="bg-white rounded-2xl shadow-md p-8 w-full max-w-sm text-center">
          <p className="text-red-600">Link inválido.</p>
          <Link to="/auth/login" className="text-sm text-indigo-600 hover:underline mt-2 block">
            Voltar ao login
          </Link>
        </div>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center">
      <div className="bg-white rounded-2xl shadow-md p-8 w-full max-w-sm">
        <h2 className="text-xl font-bold text-gray-800 mb-4">Nova senha</h2>

        <form onSubmit={handleSubmit} className="flex flex-col gap-4">
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
            label="Confirmar senha"
            type="password"
            value={confirmar}
            onChange={e => setConfirmar(e.target.value)}
            autoComplete="new-password"
            required
          />

          {error && <p className="text-sm text-red-600">{error}</p>}

          <Button type="submit" loading={loading}>Redefinir senha</Button>
        </form>
      </div>
    </div>
  )
}
