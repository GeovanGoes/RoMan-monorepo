import { Navigate, Route, Routes } from 'react-router-dom'
import { ParticipantesPage } from '../pages/participantes/ParticipantesPage'
import { CategoriasPage } from '../pages/categorias/CategoriasPage'
import { EventosPage } from '../pages/eventos/EventosPage'
import { EventoDetailPage } from '../pages/eventos/EventoDetailPage'
import { LoginPage } from '../pages/auth/LoginPage'
import { AlterarSenhaPage } from '../pages/auth/AlterarSenhaPage'
import { RecuperarSenhaPage } from '../pages/auth/RecuperarSenhaPage'
import { RedefinirSenhaPage } from '../pages/auth/RedefinirSenhaPage'
import { ProtectedRoute } from '../components/auth/ProtectedRoute'

export function AppRoutes() {
  return (
    <Routes>
      {/* Public auth routes */}
      <Route path="/auth/login" element={<LoginPage />} />
      <Route path="/auth/recuperar-senha" element={<RecuperarSenhaPage />} />
      <Route path="/auth/redefinir-senha" element={<RedefinirSenhaPage />} />

      {/* Protected: requires login for password change */}
      <Route element={<ProtectedRoute />}>
        <Route path="/auth/alterar-senha" element={<AlterarSenhaPage />} />
      </Route>

      {/* Public browsing routes */}
      <Route path="/" element={<Navigate to="/participantes" replace />} />
      <Route path="/participantes" element={<ParticipantesPage />} />
      <Route path="/categorias" element={<CategoriasPage />} />
      <Route path="/eventos" element={<EventosPage />} />
      <Route path="/eventos/:id" element={<EventoDetailPage />} />
    </Routes>
  )
}
