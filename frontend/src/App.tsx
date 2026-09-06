import { useState } from 'react'
import { NavLink, useNavigate } from 'react-router-dom'
import { AppRoutes } from './routes/AppRoutes'
import { useAuth } from './contexts/AuthContext'

export function App() {
  const { isAuthenticated, isAdmin, usuario, logout } = useAuth()
  const navigate = useNavigate()
  const [menuOpen, setMenuOpen] = useState(false)

  const linkClass = ({ isActive }: { isActive: boolean }) =>
    `px-3 py-2 rounded-lg text-sm font-medium transition-colors ${isActive ? 'bg-indigo-100 text-indigo-700' : 'text-gray-600 hover:bg-gray-100'}`

  const handleLogout = () => {
    setMenuOpen(false)
    logout()
    navigate('/auth/login', { replace: true })
  }

  const navLinks = (
    <>
      <NavLink to="/participantes" className={linkClass} onClick={() => setMenuOpen(false)}>Participantes</NavLink>
      <NavLink to="/categorias" className={linkClass} onClick={() => setMenuOpen(false)}>Categorias</NavLink>
      <NavLink to="/eventos" className={linkClass} onClick={() => setMenuOpen(false)}>Eventos</NavLink>
    </>
  )

  const userSection = isAuthenticated ? (
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
    <NavLink to="/auth/login" className="text-sm text-indigo-600 font-medium hover:underline" onClick={() => setMenuOpen(false)}>
      Entrar
    </NavLink>
  )

  return (
    <div className="min-h-screen bg-gray-50">
      <nav className="bg-white border-b border-gray-200 px-4 sm:px-6 py-3 flex items-center gap-4">
        <span className="font-bold text-indigo-700 text-lg mr-4">RoMan</span>
        <div className="hidden md:flex items-center gap-4">
          {navLinks}
        </div>
        <div className="hidden md:flex ml-auto items-center gap-3">
          {userSection}
        </div>
        <button
          className="md:hidden ml-auto text-gray-600 hover:text-gray-900 text-2xl leading-none p-1"
          onClick={() => setMenuOpen(open => !open)}
          aria-label={menuOpen ? 'Fechar menu' : 'Abrir menu'}
          aria-expanded={menuOpen}
        >
          {menuOpen ? '✕' : '☰'}
        </button>
      </nav>
      {menuOpen && (
        <div className="md:hidden bg-white border-b border-gray-200 px-4 py-3 flex flex-col gap-1">
          {navLinks}
          <div className="flex items-center gap-3 mt-2 pt-2 border-t border-gray-100">
            {userSection}
          </div>
        </div>
      )}
      <main>
        <AppRoutes />
      </main>
    </div>
  )
}
