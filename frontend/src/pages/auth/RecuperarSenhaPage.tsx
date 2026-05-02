import { useState } from 'react'
import { Link } from 'react-router-dom'
import { authService } from '../../services/authService'
import { Button } from '../../components/common/Button'
import { Input } from '../../components/common/Input'

export function RecuperarSenhaPage() {
  const [email, setEmail] = useState('')
  const [loading, setLoading] = useState(false)
  const [enviado, setEnviado] = useState(false)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setLoading(true)
    try {
      await authService.recuperarSenha(email)
    } finally {
      setLoading(false)
      setEnviado(true)
    }
  }

  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center">
      <div className="bg-white rounded-2xl shadow-md p-8 w-full max-w-sm">
        <h2 className="text-xl font-bold text-gray-800 mb-2">Recuperar senha</h2>

        {enviado ? (
          <div className="flex flex-col gap-4">
            <p className="text-sm text-gray-600">
              Se houver uma conta com esse e-mail, você receberá um link de recuperação em breve.
            </p>
            <p className="text-sm text-gray-500">
              Caso não tenha e-mail cadastrado, solicite a um administrador que atualize seus dados cadastrais.
            </p>
            <Link to="/auth/login" className="text-sm text-indigo-600 hover:underline text-center">
              Voltar ao login
            </Link>
          </div>
        ) : (
          <form onSubmit={handleSubmit} className="flex flex-col gap-4">
            <p className="text-sm text-gray-500">Informe seu e-mail para receber o link de recuperação.</p>
            <Input
              id="email"
              label="E-mail"
              type="email"
              value={email}
              onChange={e => setEmail(e.target.value)}
              placeholder="seu@email.com"
              autoComplete="email"
              required
            />
            <Button type="submit" loading={loading}>Enviar link</Button>
            <Link to="/auth/login" className="text-sm text-indigo-600 hover:underline text-center">
              Voltar ao login
            </Link>
          </form>
        )}
      </div>
    </div>
  )
}
