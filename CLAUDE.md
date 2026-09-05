# RoMan — Rolê Manager

Aplicação web para gestão de rolês (eventos e viagens em grupo) com divisão justa de custos por categoria de consumo. Participantes que não consomem determinadas categorias (ex.: bebidas alcoólicas) são excluídos do rateio dessas despesas. Menores de idade podem ser incluídos no evento sem gerar débitos.

---

## Stack Tecnológica

### Back-end
| Tecnologia | Versão | Finalidade |
|------------|--------|------------|
| Java | 25 (LTS) | Linguagem principal |
| Spring Boot | 4.1.x | Framework de aplicação |
| PostgreSQL | 16 | Banco de dados relacional |
| Spring Data JPA + Hibernate | — | ORM e acesso a dados |
| Maven | 3.9.x | Gerenciamento de dependências e build |
| JUnit 5 + Mockito | — | Testes unitários |
| Testcontainers | — | Testes de integração com PostgreSQL real |

### Front-end
| Tecnologia | Versão | Finalidade |
|------------|--------|------------|
| React | 18 (LTS) | Framework de UI |
| TypeScript | 5.x | Tipagem estática |
| Tailwind CSS | 3.x | Estilização utilitária |
| React Router | v6 | Roteamento client-side |
| Vite | — | Build tool e dev server |
| Jest + React Testing Library | — | Testes de componentes e hooks |

### Infraestrutura
| Tecnologia | Finalidade |
|------------|------------|
| Docker | Containerização dos serviços |
| Docker Compose | Orquestração local e de produção |
| Nginx | Serving do frontend em produção |

---

## Estrutura do Monorepo

```
RoMan-monorepo/
├── backend/
│   ├── src/
│   │   ├── main/java/com/roman/
│   │   │   ├── domain/           # Entidades, value objects, ports (interfaces de repositório)
│   │   │   ├── application/      # Casos de uso, interfaces input/output
│   │   │   ├── infrastructure/   # Implementações JPA, adapters, configurações Spring
│   │   │   └── interfaces/       # Controllers REST, DTOs, mappers
│   │   └── resources/
│   │       └── application.yml
│   └── pom.xml
├── frontend/
│   ├── src/
│   │   ├── components/           # Componentes reutilizáveis de UI
│   │   ├── pages/                # Componentes de página (uma pasta por rota)
│   │   ├── hooks/                # Custom React hooks
│   │   ├── services/             # Comunicação com a API REST
│   │   ├── types/                # Definições de tipos TypeScript
│   │   └── utils/                # Funções utilitárias puras
│   ├── index.html
│   ├── vite.config.ts
│   └── package.json
├── docker-compose.yml
├── .env.example
└── CLAUDE.md
```

---

## Arquitetura

### Back-end — Clean Architecture

A arquitetura é organizada em camadas concêntricas onde as dependências sempre apontam para o centro (domínio). Isso permite substituir o Spring Boot ou o banco de dados sem alterar as regras de negócio.

```
interfaces/   →   application/   →   domain/
infrastructure/   →   application/   →   domain/
```

| Camada | Pacote | Responsabilidade | Dependências |
|--------|--------|------------------|--------------|
| **Domain** | `com.roman.domain` | Entidades, value objects, interfaces de repositório (ports), exceções de domínio | Nenhuma (zero frameworks) |
| **Application** | `com.roman.application` | Casos de uso (um por operação), ports de entrada/saída | Somente `domain` |
| **Infrastructure** | `com.roman.infrastructure` | Implementações JPA, mapeamentos de entidade, configurações Spring | `domain` + `application` |
| **Interfaces** | `com.roman.interfaces` | Controllers REST, DTOs de request/response, mappers de DTO ↔ domínio | Somente `application` |

**Entidades de domínio previstas:** `Role`, `Participante`, `Custo`, `CategoriaConsumo`, `Pagamento`

### Front-end — Arquitetura Componentizada

Componentes são classificados por responsabilidade. Lógica de estado e side-effects vivem em custom hooks, mantendo os componentes declarativos.

---

## Variáveis de Ambiente

Copie `.env.example` para `.env` e ajuste os valores antes de executar.

```env
# Banco de dados
DB_HOST=localhost
DB_PORT=5432
DB_NAME=roman
DB_USER=roman
DB_PASSWORD=roman

# Back-end
SERVER_PORT=8080
APP_ENV=development

# Front-end
VITE_API_URL=http://localhost:8080/api
```

---

## Design Patterns

| Pattern | Onde | Descrição |
|---------|------|-----------|
| **Repository Pattern** | `domain` (port) + `infrastructure` (impl) | Isola o acesso a dados do domínio |
| **Use Case Pattern** | `application` | Uma classe por operação de negócio |
| **DTO + Mapper Pattern** | `interfaces` | Separa contratos da API das entidades de domínio |
| **Dependency Injection** | Todo o backend via Spring | Inversão de controle entre camadas |
| **Component Composition** | Frontend | Componentes pequenos compostos em telas |
| **Custom Hook Pattern** | Frontend | Encapsula lógica de estado e efeitos colaterais |

---

## Metodologia de Desenvolvimento

**TDD (Test-Driven Development)** é obrigatório. O ciclo é: Red → Green → Refactor.

- `domain` e `application`: 100% de cobertura por testes unitários.
- `infrastructure`: testes de integração com Testcontainers (banco PostgreSQL real).
- Frontend: componentes e hooks cobertos com React Testing Library.
- Nenhuma implementação deve ser entregue sem testes correspondentes.
