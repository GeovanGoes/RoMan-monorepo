import { createContext, useCallback, useContext, useEffect, useRef, useState } from 'react'
import type { ReactNode } from 'react'
import type { LoginPayload, PerfilUsuario, UsuarioInfo } from '../types/auth'
import { authService } from '../services/authService'
import { tokenStore } from '../services/tokenStore'

interface AuthContextValue {
  usuario: UsuarioInfo | null
  isAuthenticated: boolean
  isAdmin: boolean
  loading: boolean
  login: (payload: LoginPayload) => Promise<{ senhaProvisoria: boolean }>
  logout: () => void
  markPasswordChanged: () => void
}

const AuthContext = createContext<AuthContextValue | null>(null)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [usuario, setUsuario] = useState<UsuarioInfo | null>(null)
  const [loading, setLoading] = useState(true)
  const refreshTokenRef = useRef<string | null>(localStorage.getItem('refreshToken'))

  const clearSession = useCallback(() => {
    tokenStore.clear()
    refreshTokenRef.current = null
    localStorage.removeItem('refreshToken')
    setUsuario(null)
  }, [])

  useEffect(() => {
    const storedRefresh = localStorage.getItem('refreshToken')
    if (!storedRefresh) {
      setLoading(false)
      return
    }
    authService.refresh(storedRefresh)
      .then(res => {
        tokenStore.set(res.accessToken)
        const payload = parseJwtPayload(res.accessToken)
        if (payload) {
          setUsuario({ nome: payload.nome, perfil: payload.perfil, senhaProvisoria: payload.senhaProvisoria })
        }
      })
      .catch(() => clearSession())
      .finally(() => setLoading(false))
  }, [clearSession])

  const login = useCallback(async (payload: LoginPayload) => {
    const res = await authService.login(payload)
    tokenStore.set(res.accessToken)
    refreshTokenRef.current = res.refreshToken
    localStorage.setItem('refreshToken', res.refreshToken)
    setUsuario({ nome: res.nome, perfil: res.perfil, senhaProvisoria: res.senhaProvisoria })
    return { senhaProvisoria: res.senhaProvisoria }
  }, [])

  const logout = useCallback(() => {
    const rt = refreshTokenRef.current
    clearSession()
    if (rt) authService.logout(rt).catch(() => {})
  }, [clearSession])

  const markPasswordChanged = useCallback(() => {
    setUsuario(prev => prev ? { ...prev, senhaProvisoria: false } : null)
  }, [])

  return (
    <AuthContext.Provider value={{
      usuario,
      isAuthenticated: usuario !== null,
      isAdmin: usuario?.perfil === 'ADMIN',
      loading,
      login,
      logout,
      markPasswordChanged,
    }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within AuthProvider')
  return ctx
}

function parseJwtPayload(token: string): { nome: string; perfil: PerfilUsuario; senhaProvisoria: boolean } | null {
  try {
    const base64 = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')
    const json = decodeURIComponent(
      atob(base64).split('').map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)).join('')
    )
    return JSON.parse(json)
  } catch {
    return null
  }
}
