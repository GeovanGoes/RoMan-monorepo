import { useState } from 'react'
import { useNavigate, useLocation, Link } from 'react-router-dom'
import { useAuth } from '../../contexts/AuthContext'
import { Button } from '../../components/common/Button'
import { Input } from '../../components/common/Input'

export function LoginPage() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const from = (location.state as { from?: { pathname: string } })?.from?.pathname ?? '/'

  const [username, setUsername] = useState('')
  const [senha, setSenha] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError(null)
    setLoading(true)
    try {
      const { senhaProvisoria } = await login({ username, senha })
      if (senhaProvisoria) {
        navigate('/auth/alterar-senha', { replace: true, state: { obrigatorio: true } })
      } else {
        navigate(from, { replace: true })
      }
    } catch {
      setError('Usuário ou senha inválidos.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center">
      <div className="bg-white rounded-2xl shadow-md p-8 w-full max-w-sm">
        <h1 className="text-2xl font-bold text-indigo-700 mb-1">RoMan</h1>
        <p className="text-gray-500 text-sm mb-6">Rolê Manager — faça login para continuar</p>

        <form onSubmit={handleSubmit} className="flex flex-col gap-4">
          <Input
            id="username"
            label="Username"
            value={username}
            onChange={e => setUsername(e.target.value)}
            placeholder="seu.username"
            autoComplete="username"
            required
          />
          <Input
            id="senha"
            label="Senha"
            type="password"
            value={senha}
            onChange={e => setSenha(e.target.value)}
            placeholder="••••••••"
            autoComplete="current-password"
            required
          />

          {error && <p className="text-sm text-red-600">{error}</p>}

          <Button type="submit" loading={loading}>Entrar</Button>
        </form>

        <div className="mt-4 text-center">
          <Link to="/auth/recuperar-senha" className="text-sm text-indigo-600 hover:underline">
            Esqueci minha senha
          </Link>
        </div>
      </div>
    </div>
  )
}
