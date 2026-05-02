import { NavLink, useNavigate } from 'react-router-dom'
import { AppRoutes } from './routes/AppRoutes'
import { useAuth } from './contexts/AuthContext'

export function App() {
  const { isAuthenticated, isAdmin, usuario, logout } = useAuth()
  const navigate = useNavigate()

  const linkClass = ({ isActive }: { isActive: boolean }) =>
    `px-3 py-2 rounded-lg text-sm font-medium transition-colors ${isActive ? 'bg-indigo-100 text-indigo-700' : 'text-gray-600 hover:bg-gray-100'}`

  const handleLogout = () => {
    logout()
    navigate('/auth/login', { replace: true })
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <nav className="bg-white border-b border-gray-200 px-6 py-3 flex items-center gap-4">
        <span className="font-bold text-indigo-700 text-lg mr-4">RoMan</span>
        <NavLink to="/participantes" className={linkClass}>Participantes</NavLink>
        <NavLink to="/categorias" className={linkClass}>Categorias</NavLink>
        <NavLink to="/eventos" className={linkClass}>Eventos</NavLink>
        <div className="ml-auto flex items-center gap-3">
          {isAuthenticated ? (
            <>
              <span className="text-sm text-gray-600">
                {usuario?.nome}
                {isAdmin && (
                  <span className="ml-1 text-xs bg-indigo-100 text-indigo-700 px-1.5 py-0.5 rounded font-medium">
                    Admin
                  </span>
                )}
              </span>
              <button
                onClick={handleLogout}
                className="text-sm text-gray-500 hover:text-red-600 transition-colors"
              >
                Sair
              </button>
            </>
          ) : (
            <NavLink to="/auth/login" className="text-sm text-indigo-600 font-medium hover:underline">
              Entrar
            </NavLink>
          )}
        </div>
      </nav>
      <main>
        <AppRoutes />
      </main>
    </div>
  )
}
