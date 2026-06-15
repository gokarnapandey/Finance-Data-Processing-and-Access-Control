<h1 align="center">🤖 Finance Chatbot</h1>

<p align="center">
  <b>A role-based, tool-calling AI assistant for the Finance Record Management API.</b><br/>
  Built with <b>Spring AI</b> + <b>Ollama</b> (local LLM) — talk to your financial data in plain English,
  with every action gated by the caller's role.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-orange?logo=openjdk&logoColor=white" alt="Java 21"/>
  <img src="https://img.shields.io/badge/Spring%20Boot-3.5.13-6DB33F?logo=springboot&logoColor=white" alt="Spring Boot"/>
  <img src="https://img.shields.io/badge/Spring%20AI-1.1.7-6DB33F?logo=spring&logoColor=white" alt="Spring AI"/>
  <img src="https://img.shields.io/badge/Ollama-local%20LLM-black?logo=ollama&logoColor=white" alt="Ollama"/>
  <img src="https://img.shields.io/badge/Build-Maven-C71A36?logo=apachemaven&logoColor=white" alt="Maven"/>
</p>

---

## 📖 Overview

**Finance Chatbot** is a standalone Spring Boot service that puts a natural-language chatbot in front of the
[Finance-Data-Processing-and-Access-Control](https://github.com/gokarnapandey/Finance-Data-Processing-and-Access-Control)
API. It uses **Spring AI tool calling**: each capability (read the dashboard, filter records, create a record,
manage users…) is a Java **tool** the model can invoke. The set of tools handed to the model is **filtered by
the caller's role** (`VIEWER`, `ANALYST`, `ADMIN`), and every tool call forwards the user's JWT downstream so
the Finance API re-enforces permissions — defense in depth, so even a prompt-injected model can't exceed the
user's real rights. Conversations are **multi-turn** via Spring AI chat memory.

> The chatbot is a **separate application** from the Finance API. The Finance API remains the source of truth
> and is not modified.

---

## 🏗️ Architecture

```text
Client ──login──►  Finance API   POST /api/v1/login            (existing, :8080)
       ◄── JWT ──
        │
        │  POST /api/v1/chat  { conversationId?, message }   Authorization: Bearer <JWT>
        ▼
  finance-chatbot (:8081)
     JwtAuthFilter   → validate JWT (shared secret) → role + raw token (request scope)
     ChatController  → ChatService
          ChatClient.prompt()
            .system(role-aware prompt)
            .advisors(MessageChatMemoryAdvisor)     // per-user/session conversation thread
            .tools(tools allowed for this role)     // VIEWER / ANALYST / ADMIN
            .user(message).call()
                  │  model decides to call a tool
                  ▼
          FinanceApiClient ──Bearer <same JWT>──►  Finance API /api/v1/...
                                                    (RBAC enforced again here)

  Ollama (local, :11434) serves a tool-capable model (e.g. qwen2.5)
```

---

## ✨ Features

- 🧠 **Natural-language interface** to financial records, dashboards, and user administration.
- 🔧 **Tool calling** — the LLM invokes typed Java tools; no hand-written intent parsing.
- 🔐 **Role-based access** — tools are exposed per role, and re-checked downstream by the Finance API.
- 💬 **Multi-turn memory** — follow-up questions ("…and last month?") keep context per conversation.
- 🏠 **Runs fully locally** — Ollama means no API keys and no data leaving your machine.
- 🚦 **Graceful errors** — downstream 403/404/400 responses are explained in plain language.

---

## 🧰 Tech Stack

<table>
  <tr><th>Concern</th><th>Choice</th></tr>
  <tr><td>Language / Runtime</td><td>Java 21</td></tr>
  <tr><td>Framework</td><td>Spring Boot 3.5.13</td></tr>
  <tr><td>AI</td><td>Spring AI 1.1.7 (<code>spring-ai-starter-model-ollama</code>)</td></tr>
  <tr><td>LLM</td><td>Ollama — local, tool-capable model (qwen3:4b / qwen2.5 / llama3.1)</td></tr>
  <tr><td>Security</td><td>Spring Security + JWT (jjwt 0.11.5)</td></tr>
  <tr><td>Build</td><td>Maven</td></tr>
</table>

---

## ✅ Prerequisites

1. **Java 21** and **Maven**.
2. **[Ollama](https://ollama.com)** installed and running, with a tool-capable model pulled:
   ```bash
   ollama pull qwen3:4b      # default; qwen2.5 or llama3.1:8b also work
   ```
3. The **Finance Record Management API** running on `http://localhost:8080`.
4. The **same `JWT_SECRET`** on both apps (the defaults already match for local demos).

---

## 🚀 Getting Started

```bash
# 1. Start Ollama and make sure the model is available
ollama pull qwen3:4b

# 2. Start the Finance API (in its own project)
mvn spring-boot:run          # serves on :8080

# 3. Start the chatbot (this project)
mvn spring-boot:run          # serves on :8081
```

---

## ⚙️ Configuration

All settings have sensible defaults and can be overridden with environment variables.

<table>
  <tr><th>Variable</th><th>Default</th><th>Description</th></tr>
  <tr><td><code>OLLAMA_BASE_URL</code></td><td><code>http://localhost:11434</code></td><td>Ollama server URL</td></tr>
  <tr><td><code>OLLAMA_MODEL</code></td><td><code>qwen3:4b</code></td><td>Tool-capable model name</td></tr>
  <tr><td><code>FINANCE_API_BASE_URL</code></td><td><code>http://localhost:8080</code></td><td>Downstream Finance API</td></tr>
  <tr><td><code>JWT_SECRET</code></td><td><i>(shared default)</i></td><td><b>Must equal the Finance API's <code>JWT_SECRET</code></b></td></tr>
  <tr><td><code>server.port</code></td><td><code>8081</code></td><td>Chatbot port</td></tr>
</table>

---

## 🌐 API Endpoints

<table>
  <tr><th>Method</th><th>Path</th><th>Auth</th><th>Description</th></tr>
  <tr><td><code>POST</code></td><td><code>/api/v1/auth/login</code></td><td>Public</td><td>Proxy to the Finance API login; returns a JWT</td></tr>
  <tr><td><code>POST</code></td><td><code>/api/v1/chat</code></td><td>Bearer JWT</td><td>Send a message; returns the assistant's reply</td></tr>
  <tr><td><code>GET</code></td><td><code>/actuator/health</code></td><td>Public</td><td>Health check</td></tr>
</table>

---

## 🛡️ Role → Tool Access

The role is read from the JWT. Each role inherits the tools of the ones above it.

<table>
  <tr><th>Role</th><th>Tools exposed to the model</th></tr>
  <tr>
    <td><b>VIEWER</b></td>
    <td><code>getFinancialSummary</code>, <code>getCategoryTotals</code>, <code>getMonthlyTrends</code>, <code>getRecentActivities</code></td>
  </tr>
  <tr>
    <td><b>ANALYST</b></td>
    <td><i>VIEWER tools</i> + <code>listRecords</code>, <code>getRecordById</code>, <code>filterRecords</code></td>
  </tr>
  <tr>
    <td><b>ADMIN</b></td>
    <td><i>ANALYST tools</i> + <code>createRecord</code>, <code>updateRecord</code>, <code>deleteRecord</code>, <code>createUser</code>, <code>listUsers</code>, <code>getUserById</code>, <code>updateUser</code>, <code>updateUserStatus</code>, <code>deleteUser</code>, <code>listDeletedUsers</code></td>
  </tr>
</table>

---

## 💡 Usage Examples

<b>1. Log in to get a token</b> (default admin from the Finance API):

```bash
curl -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"system@admin.com","password":"Admin@12345"}'
# -> { "email": "...", "status": "SUCCESS", "jwtToken": "eyJ..." }
```

<b>2. Chat</b> (pass the token as a Bearer header):

```bash
curl -X POST http://localhost:8081/api/v1/chat \
  -H "Authorization: Bearer <JWT>" \
  -H "Content-Type: application/json" \
  -d '{"message":"What is my total income versus expense?"}'
# -> { "conversationId": "system@admin.com", "reply": "Your total income is ..." }
```

<b>3. Multi-turn</b> (reuse the same <code>conversationId</code> for follow-ups):

```bash
curl -X POST http://localhost:8081/api/v1/chat \
  -H "Authorization: Bearer <JWT>" \
  -H "Content-Type: application/json" \
  -d '{"conversationId":"demo-1","message":"Add an EXPENSE of 1200 for RENT"}'

curl -X POST http://localhost:8081/api/v1/chat \
  -H "Authorization: Bearer <JWT>" \
  -H "Content-Type: application/json" \
  -d '{"conversationId":"demo-1","message":"and now show me my last 5 transactions"}'
```

> A **VIEWER** asking "delete record X" is told their role doesn't permit it — the write tools are never
> even offered to them, and the Finance API would reject the call anyway.

---

## 🔍 How It Works

- **Security** — `JwtAuthFilter` validates the bearer token with the **same HMAC secret** the Finance API
  uses, reads the `authorities` claim into the Spring Security context, and stashes the raw token in a
  request-scoped holder.
- **Tool selection** — `ToolRegistry` returns only the tool objects allowed for the caller's role; they are
  registered on the `ChatClient` per request via `.tools(...)`.
- **Tool execution** — each `@Tool` method calls `FinanceApiClient`, which forwards the user's JWT to the
  Finance API, so RBAC is enforced a second time.
- **Memory** — `MessageChatMemoryAdvisor` over an in-memory window keeps each conversation's history,
  keyed by `conversationId` (defaults to the user's identity).

---

<details>
<summary>📂 Project Structure</summary>

```text
finance-chatbot/
├── pom.xml
├── src/main/resources/application.yaml
└── src/main/java/com/zorvyn/assignment/financechatbot/
    ├── FinanceChatbotApplication.java
    ├── config/        # SecurityConfig, JwtAuthFilter, RestClientConfig, ChatClientConfig
    ├── security/      # JwtService, CurrentToken (request scope), Role
    ├── client/        # FinanceApiClient (RestClient, forwards the JWT)
    ├── tools/         # DashboardTools, RecordReadTools, RecordWriteTools, UserAdminTools, ToolRegistry
    ├── service/       # ChatService (role-gated, memory-backed chat)
    ├── controller/    # ChatController, AuthProxyController
    ├── dto/           # ChatRequest, ChatResponse, LoginRequest
    └── exception/     # GlobalExceptionHandler
```

</details>

---

## 📝 Notes & Limitations

- Chat memory is **in-memory** and resets on restart (matching the Finance API's non-persistent H2 design).
- Tool-calling reliability depends on the Ollama model; **qwen3:4b** is the default (small, tool-capable),
  while **qwen2.5** or **llama3.1:8b** are good alternatives for more reliable tool selection.
- The chatbot trusts the Finance API as the authority on permissions; it adds role-gating for good UX and
  as a first line of defense.
