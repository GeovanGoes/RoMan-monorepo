import { Navigate, Outlet, useLocation } from 'react-router-dom'
import { useAuth } from '../../contexts/AuthContext'

interface Props {
  requireAdmin?: boolean
}

export function ProtectedRoute({ requireAdmin = false }: Props) {
  const { isAuthenticated, isAdmin, loading } = useAuth()
  const location = useLocation()

  if (loading) return <div className="p-6 text-gray-500">Carregando...</div>
  if (!isAuthenticated) return <Navigate to="/auth/login" state={{ from: location }} replace />
  if (requireAdmin && !isAdmin) return <Navigate to="/" replace />

  return <Outlet />
}
