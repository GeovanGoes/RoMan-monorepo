import { Route, Routes, Navigate } from 'react-router-dom'
import { ParticipantesPage } from '../pages/participantes/ParticipantesPage'
import { CategoriasPage } from '../pages/categorias/CategoriasPage'
import { EventosPage } from '../pages/eventos/EventosPage'
import { EventoDetailPage } from '../pages/eventos/EventoDetailPage'

export function AppRoutes() {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/participantes" replace />} />
      <Route path="/participantes" element={<ParticipantesPage />} />
      <Route path="/categorias" element={<CategoriasPage />} />
      <Route path="/eventos" element={<EventosPage />} />
      <Route path="/eventos/:id" element={<EventoDetailPage />} />
    </Routes>
  )
}
