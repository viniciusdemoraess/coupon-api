# Coupon API – Desafio Técnico (Spring Boot)

API REST para gerenciamento de cupons, desenvolvida em **Java 17 + Spring Boot 3**, 
seguindo as regras de negócio propostas no desafio técnico.

O foco da implementação foi garantir **qualidade de código**, **encapsulamento das regras de negócio no domínio**, **testabilidade e clareza arquitetural**,
 conforme esperado para o **nível Pleno**.

---

## 📌 Tecnologias Utilizadas

- Java 17
- Spring Boot 3
- Spring Web
- Spring Data JPA
- H2 (banco em memória)
- Swagger / OpenAPI
- JUnit 5
- Docker e Docker Compose
- Maven

---

## 📖 Regras de Negócio Implementadas

### ✅ Create (Criação de Cupom)

- Um cupom pode ser criado a qualquer momento
- Campos obrigatórios:
  - `code`
  - `description`
  - `discountValue`
  - `expirationDate`
- O código do cupom:
  - Deve conter **exatamente 6 caracteres alfanuméricos**
  - Caracteres especiais são aceitos na entrada, mas **removidos antes de salvar**
- Valor de desconto:
  - **Valor mínimo: 0.5 (50 centavos)**
  - **Sem valor máximo**
- A data de expiração:
  - **Não pode estar no passado**
- O cupom pode ser criado como:
  - publicado (`published = true`)
  - ou não publicado (`published = false`)

---

### ✅ Delete (Exclusão de Cupom)

- Um cupom pode ser deletado a qualquer momento
- O delete é **lógico (soft delete)**:
  - os dados não são removidos do banco
  - é registrado um `deletedAt`
- Não é possível deletar um cupom já deletado

---

## 🧠 Modelagem de Domínio

Todas as regras de negócio foram **encapsuladas no domínio**, evitando lógica espalhada em services ou controllers.

A entidade `Coupon` é responsável por:

- Normalizar e validar código
- Validar valor mínimo de desconto
- Validar data de expiração
- Controlar consumo (`consume`)
- Controlar exclusão lógica (`softDelete`)
- Determinar o status real do cupom (`getStatus`)

### Estados do Cupom

De acordo com a documentação da API, os status possíveis são:

- `ACTIVE`
- `INACTIVE`
- `DELETED`

Decisão de modelagem:
- Um cupom **expirado ou consumido** passa a ser `INACTIVE`
- Um cupom não publicado **pode estar ACTIVE**, mas não pode ser consumido
- `published` representa **visibilidade**, não validade

---

## 🔌 Endpoints Implementados

- POST /coupons – Criação de cupom
- GET /coupons – Listagem de cupons
- GET /coupons/{id} – Busca de cupom por ID
- PATCH /coupons/{id}/consume – Consumo de cupom
- DELETE /coupons/{id} – Exclusão lógica

---

## ⏱️ Uso de Clock (Testabilidade)

Foi utilizado `java.time.Clock` para:

- Evitar dependência direta de `now()`
- Tornar regras de tempo **determinísticas**
- Facilitar testes de expiração e consumo

Isso garante que os testes não dependam do horário real da máquina.

---

## 🧪 Testes

- Testes de domínio cobrindo regras críticas
- Testes de service cobrindo:
  - criação
  - duplicidade
  - consumo
  - consumo inválido
  - exclusão lógica
- Banco H2 em memória
- Cobertura superior a **80% das regras de negócio**

---

## 📄 Swagger / OpenAPI

A API está documentada via Swagger, com:

- Endpoints descritos
- Payloads de exemplo
- Datas com **offset de São Paulo (-03:00)**

Acesse após subir a aplicação:
`http://localhost:8080/swagger-ui.html`

---

## ⏰ Por que não foi implementado um Cron Job?

Embora seja possível criar um job para marcar cupons expirados, **não foi implementado um cron job propositalmente**, pelos seguintes motivos:

- A expiração é **validada no momento do uso**
- Evita inconsistência caso o job falhe ou atrase
- Não foi solicitado no desafio
- Mantém o domínio como **fonte da verdade**

👉 Um cron job pode ser adicionado futuramente para:
- otimizar consultas
- gerar relatórios
- emitir eventos de expiração

Mas **não é necessário para garantir a regra de negócio**.

---

## ▶️ Como Executar Localmente (Java + Maven)

### Pré-requisitos
- Java 17
- Maven

### Passos

```bash
mvn clean package
mvn spring-boot:run
```

A aplicação fica disponível para consumo em:

`http://localhost:8080`

Com seu swagger em:

`http://localhost:8080/swagger-ui.html`

## 🐳 Como Executar com Docker e Docker Compose

## Pré-requisitos

- Docker
- Docker Compose

## Build e execução

```bash
docker compose up --build
```

A aplicação ficará disponível em:

`http://localhost:8080`


Swagger:

`http://localhost:8080/swagger-ui.html`

## 🗄️ Banco de Dados (H2)

- Banco em memória (H2)
- Utilizado para desenvolvimento e testes
- Console H2 habilitado no ambiente local e container

`http://localhost:8080/h2-console`

## 📌 Considerações Finais

Este projeto foi desenvolvido com foco em:

- Clareza de regras de negócio
- Código limpo e testável
- Encapsulamento correto no domínio
- Decisões técnicas conscientes
- Atendimento completo aos requisitos do desafio para o nível Pleno

👤 Autor

Desenvolvido por Vinicius Oliveira
Desafio técnico – Backend Java / Spring Boot