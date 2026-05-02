export type PerfilUsuario = 'ADMIN' | 'USUARIO'

export interface LoginPayload {
  username: string
  senha: string
}

export interface LoginResponse {
  accessToken: string
  refreshToken: string
  perfil: PerfilUsuario
  senhaProvisoria: boolean
  nome: string
}

export interface RefreshResponse {
  accessToken: string
}

export interface AlterarSenhaPayload {
  senhaAtual: string
  novaSenha: string
}

export interface RecuperarSenhaPayload {
  email: string
}

export interface RedefinirSenhaPayload {
  token: string
  novaSenha: string
}

export interface UsuarioInfo {
  nome: string
  perfil: PerfilUsuario
  senhaProvisoria: boolean
}
