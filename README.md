# RoMan — Rolê Manager

> Aplicação web para gestão de rolês (eventos e viagens em grupo) com divisão justa de custos por categoria de consumo.

Participantes que não consomem determinadas categorias (ex.: bebidas alcoólicas) são excluídos do rateio dessas despesas. Menores de idade podem ser incluídos no evento sem gerar débitos.

---

## Índice

- [Funcionalidades](#funcionalidades)
- [Stack Tecnológica](#stack-tecnológica)
- [Arquitetura](#arquitetura)
- [Pré-requisitos](#pré-requisitos)
- [Executando com Docker](#executando-com-docker)
- [Executando localmente](#executando-localmente)
- [Variáveis de Ambiente](#variáveis-de-ambiente)
- [Testes](#testes)
- [API REST](#api-rest)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Gitflow](#gitflow)

---

## Funcionalidades

- **Participantes** — cadastro com soft delete; unicidade de username
- **Categorias de consumo** — ex.: alimentação, bebidas, hospedagem
- **Eventos** — criação com local e datas; hard delete em cascata
- **Vínculo evento-participante** — flag de menor de idade e lista de categorias que o participante não consome
- **Compras** — valor, categoria e lista de quem adiantou o pagamento (pagadores)
- **Rateio** — cálculo automático de quanto cada um deve pagar ou receber de ressarcimento, considerando exclusões de categoria e pagamentos adiantados

---

## Stack Tecnológica

### Back-end

| Tecnologia | Versão | Finalidade |
|---|---|---|
| Java | 21 (LTS) | Linguagem principal |
| Spring Boot | 3.3.x | Framework de aplicação |
| PostgreSQL | 16 | Banco de dados relacional |
| Spring Data JPA + Hibernate | — | ORM |
| Flyway | — | Migrations de banco de dados |
| Maven | 3.9.x | Build e dependências |
| JUnit 5 + Mockito | — | Testes unitários |
| Testcontainers | — | Testes de integração com PostgreSQL real |
| Lombok | — | Redução de boilerplate |

### Front-end

| Tecnologia | Versão | Finalidade |
|---|---|---|
| React | 18 (LTS) | Framework de UI |
| TypeScript | 5.x | Tipagem estática |
| Tailwind CSS | 3.x | Estilização utilitária |
| React Router | v6 | Roteamento client-side |
| Vite | — | Build tool e dev server |
| Axios | — | Cliente HTTP |
| Jest + React Testing Library | — | Testes de componentes |

### Infraestrutura

| Tecnologia | Finalidade |
|---|---|
| Docker | Containerização |
| Docker Compose | Orquestração local |
| Nginx | Serving do frontend em produção |

---

## Arquitetura

### Back-end — Clean Architecture

As dependências apontam sempre para o centro (domínio), isolando as regras de negócio de frameworks e banco de dados.

```
interfaces/      →   application/   →   domain/
infrastructure/  →   application/   →   domain/
```

| Camada | Pacote | Responsabilidade |
|---|---|---|
| **Domain** | `com.roman.domain` | Entidades, exceções de domínio, interfaces de repositório (ports) — zero frameworks |
| **Application** | `com.roman.application` | Casos de uso (um por operação) |
| **Infrastructure** | `com.roman.infrastructure` | Implementações JPA, adapters, configurações Spring |
| **Interfaces** | `com.roman.interfaces` | Controllers REST, DTOs, mappers |

### Front-end — Arquitetura Componentizada

Lógica de estado e side-effects encapsulados em custom hooks; componentes permanecem declarativos.

```
pages/       → composição de componentes e chamada de hooks
hooks/       → lógica de estado e chamadas de serviço
services/    → comunicação com a API REST (Axios)
components/  → componentes reutilizáveis de UI
types/       → definições TypeScript
```

---

## Pré-requisitos

- [Docker](https://www.docker.com/) 20+ e Docker Compose V2
- (Opcional, para dev local) Java 21 e Node.js 20+

---

## Executando com Docker

```bash
# 1. Copie e ajuste as variáveis de ambiente
cp .env.example .env

# 2. Suba todos os serviços
docker-compose up --build
```

| Serviço | URL |
|---|---|
| Frontend | http://localhost:3000 |
| Backend (API) | http://localhost:8080/api/v1 |
| PostgreSQL | `localhost:5432` (apenas se a porta estiver exposta no compose) |

Para parar:

```bash
docker-compose down
```

Para destruir os dados do banco:

```bash
docker-compose down -v
```

---

## Executando localmente

### Back-end

```bash
cd backend

# Suba apenas o banco via Docker
docker-compose up postgres -d

# Execute a aplicação
mvn spring-boot:run
```

A API estará disponível em `http://localhost:8080`.

### Front-end

```bash
cd frontend
npm install
npm run dev
```

O app estará disponível em `http://localhost:5173`.

> Certifique-se de que `VITE_API_URL` aponta para o back-end (padrão: `http://localhost:8080/api/v1`).

---

## Variáveis de Ambiente

Copie `.env.example` para `.env` e ajuste os valores:

```env
# Banco de dados
DB_HOST=localhost
DB_PORT=5432
DB_NAME=roman
DB_USER=roman
DB_PASSWORD=roman

# Back-end
SERVER_PORT=8080

# Front-end
VITE_API_URL=http://localhost:8080/api/v1
```

---

## Testes

### Back-end

```bash
cd backend

# Todos os testes (unitários + integração com Testcontainers)
mvn test

# Apenas testes unitários (sem Docker)
mvn test -Dgroups="unit"
```

Cobertura esperada:
- **domain + application**: 100% por testes unitários (JUnit 5 + Mockito)
- **infrastructure / controllers**: testes de integração com Testcontainers (requer Docker)

### Front-end

```bash
cd frontend
npm test          # execução única
npm run test:watch  # modo watch
```

---

## API REST

Base URL: `/api/v1`

### Participantes

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/participantes` | Criar participante |
| `GET` | `/participantes` | Listar participantes ativos |
| `GET` | `/participantes/{id}` | Buscar por ID |
| `PUT` | `/participantes/{id}` | Atualizar |
| `DELETE` | `/participantes/{id}` | Remover (soft delete) |

### Categorias de Consumo

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/categorias` | Criar categoria |
| `GET` | `/categorias` | Listar categorias |
| `GET` | `/categorias/{id}` | Buscar por ID |
| `PUT` | `/categorias/{id}` | Atualizar |
| `DELETE` | `/categorias/{id}` | Remover |

### Eventos

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/eventos` | Criar evento |
| `GET` | `/eventos` | Listar eventos |
| `GET` | `/eventos/{id}` | Buscar por ID |
| `PUT` | `/eventos/{id}` | Atualizar |
| `DELETE` | `/eventos/{id}` | Remover (hard delete) |

### Participantes no Evento

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/eventos/{id}/participantes` | Listar participantes do evento |
| `POST` | `/eventos/{id}/participantes/{participanteId}` | Vincular (body: `{ menorDeIdade: bool }`) |
| `DELETE` | `/eventos/{id}/participantes/{participanteId}` | Desvincular |

### Exclusões de Categoria

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/eventos/{id}/participantes/{participanteId}/exclusoes/{categoriaId}` | Adicionar exclusão |
| `DELETE` | `/eventos/{id}/participantes/{participanteId}/exclusoes/{categoriaId}` | Remover exclusão |

### Compras

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/eventos/{id}/compras` | Adicionar compra (body: `{ descricao, valor, categoriaId, pagadoresIds[] }`) |
| `GET` | `/eventos/{id}/compras` | Listar compras do evento |
| `DELETE` | `/eventos/{id}/compras/{compraId}` | Remover compra |

### Rateio

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/eventos/{id}/rateio` | Calcular quanto cada participante deve pagar ou receber |

**Exemplo de resposta do rateio:**

```json
[
  {
    "participanteId": "...",
    "nomeParticipante": "Ana",
    "totalDevido": 50.00,
    "totalPago": 80.00,
    "saldo": 30.00
  },
  {
    "participanteId": "...",
    "nomeParticipante": "Bruno",
    "totalDevido": 50.00,
    "totalPago": 20.00,
    "saldo": -30.00
  }
]
```

`saldo > 0` → participante deve receber ressarcimento  
`saldo < 0` → participante deve pagar a diferença  
`saldo = 0` → quitado

### Respostas de Erro

| Status | Situação |
|---|---|
| `400` | Dados inválidos (Bean Validation) |
| `404` | Entidade não encontrada |
| `409` | Conflito (username duplicado, participante já vinculado) |

---

## Estrutura do Projeto

```
RoMan-monorepo/
├── backend/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/roman/
│       │   ├── domain/           # Entidades, ports, exceções
│       │   ├── application/      # Casos de uso
│       │   ├── infrastructure/   # JPA, adapters, config Spring
│       │   └── interfaces/       # Controllers REST, DTOs, mappers
│       └── main/resources/
│           ├── application.yml
│           ├── application-docker.yml
│           └── db/migration/     # Flyway migrations
├── frontend/
│   ├── Dockerfile
│   ├── nginx.conf
│   ├── src/
│   │   ├── components/           # Componentes de UI reutilizáveis
│   │   ├── pages/                # Páginas por rota
│   │   ├── hooks/                # Custom hooks (estado + side-effects)
│   │   ├── services/             # Comunicação com a API (Axios)
│   │   └── types/                # Tipos TypeScript
│   └── package.json
├── docker-compose.yml
├── .env.example
└── CLAUDE.md
```

---

## Gitflow

Este repositório segue o [Gitflow Workflow](https://nvie.com/posts/a-successful-git-branching-model/).

| Branch | Origem | Propósito |
|---|---|---|
| `main` | — | Código em produção; recebe merges de `release/*` e `hotfix/*` |
| `develop` | `main` | Branch de integração; base para novas features |
| `feature/*` | `develop` | Uma branch por funcionalidade nova |
| `release/*` | `develop` | Preparação de versão (ajustes finais, bump de versão) |
| `hotfix/*` | `main` | Correções urgentes em produção |

### Fluxo de uma feature

```bash
git checkout develop
git checkout -b feature/nome-da-feature

# ... desenvolvimento e commits ...

git checkout develop
git merge --no-ff feature/nome-da-feature
git branch -d feature/nome-da-feature
git push origin develop
```

### Fluxo de release

```bash
git checkout develop
git checkout -b release/1.0.0

# ... ajustes e bump de versão ...

git checkout main
git merge --no-ff release/1.0.0
git tag -a v1.0.0 -m "Release 1.0.0"

git checkout develop
git merge --no-ff release/1.0.0
git branch -d release/1.0.0
```

### Fluxo de hotfix

```bash
git checkout main
git checkout -b hotfix/descricao-do-bug

# ... correção ...

git checkout main
git merge --no-ff hotfix/descricao-do-bug
git tag -a v1.0.1 -m "Hotfix 1.0.1"

git checkout develop
git merge --no-ff hotfix/descricao-do-bug
git branch -d hotfix/descricao-do-bug
```
