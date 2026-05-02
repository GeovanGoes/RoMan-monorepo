import api from './api'
import type { LoginPayload, LoginResponse, RefreshResponse, AlterarSenhaPayload } from '../types/auth'

export const authService = {
  login: (payload: LoginPayload) =>
    api.post<LoginResponse>('/auth/login', payload).then(r => r.data),

  refresh: (refreshToken: string) =>
    api.post<RefreshResponse>('/auth/refresh', { refreshToken }).then(r => r.data),

  logout: (refreshToken: string) =>
    api.post('/auth/logout', { refreshToken }).then(() => {}),

  recuperarSenha: (email: string) =>
    api.post('/auth/recuperar-senha', { email }).then(() => {}),

  redefinirSenha: (token: string, novaSenha: string) =>
    api.post('/auth/redefinir-senha', { token, novaSenha }).then(() => {}),

  alterarSenha: (payload: AlterarSenhaPayload) =>
    api.post('/auth/alterar-senha', payload).then(() => {}),
}
