import { NavLink } from 'react-router-dom'
import { AppRoutes } from './routes/AppRoutes'

export function App() {
  const linkClass = ({ isActive }: { isActive: boolean }) =>
    `px-3 py-2 rounded-lg text-sm font-medium transition-colors ${isActive ? 'bg-indigo-100 text-indigo-700' : 'text-gray-600 hover:bg-gray-100'}`

  return (
    <div className="min-h-screen bg-gray-50">
      <nav className="bg-white border-b border-gray-200 px-6 py-3 flex items-center gap-4">
        <span className="font-bold text-indigo-700 text-lg mr-4">RoMan</span>
        <NavLink to="/participantes" className={linkClass}>Participantes</NavLink>
        <NavLink to="/categorias" className={linkClass}>Categorias</NavLink>
        <NavLink to="/eventos" className={linkClass}>Eventos</NavLink>
      </nav>
      <main>
        <AppRoutes />
      </main>
    </div>
  )
}
