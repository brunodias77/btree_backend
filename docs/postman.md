# Postman

Guia rapido para testar os endpoints publicados pelo `AuthController`.

## Variaveis

```text
base_url = http://localhost:8080
email_verification_token = cole-o-token-logado-pelo-fake-email-service
```

## Headers padrao

```text
Content-Type: application/json
Accept: application/json
```

## Endpoints publicos

Os endpoints abaixo nao exigem token JWT:

```text
GET  {{base_url}}/actuator/health
GET  {{base_url}}/v3/api-docs
GET  {{base_url}}/swagger-ui.html
POST {{base_url}}/api/v1/auth/register
POST {{base_url}}/api/v1/auth/verify-email
```

## Fluxo recomendado

1. Execute o health check.
2. Registre um usuario em `/api/v1/auth/register`.
3. Copie o token exibido no log da aplicacao pelo `FakeEmailService`.
4. Cole o token na variavel `email_verification_token`.
5. Verifique o e-mail em `/api/v1/auth/verify-email`.

O registro cria o usuario, garante a role `customer`, cria um token de verificacao com validade de 24 horas, publica eventos de dominio/integracao e envia o e-mail fake depois do commit da transacao. A criacao do carrinho acontece de forma assincrona via outbox.

## Formato padrao de resposta

Sucesso:

```json
{
  "success": true,
  "status": 200,
  "message": "Mensagem de sucesso",
  "data": {},
  "timestamp": "2026-05-26T00:00:00Z",
  "path": "/api/v1/exemplo"
}
```

Erro:

```json
{
  "success": false,
  "status": 400,
  "message": "Bad Request",
  "errors": [
    "mensagem do erro"
  ],
  "timestamp": "2026-05-26T00:00:00Z",
  "path": "/api/v1/exemplo"
}
```

Campos nulos sao omitidos no JSON.

## Health Check

```text
GET {{base_url}}/actuator/health
```

Auth: sem token.

Resposta esperada: `200 OK`

```json
{
  "status": "UP"
}
```

## Registrar Usuario

```text
POST {{base_url}}/api/v1/auth/register
```

Auth: sem token.

Body:

```json
{
  "username": "cliente_teste",
  "email": "cliente.teste@example.com",
  "password": "Senha@123"
}
```

Regras do body:

```text
username: obrigatorio, maximo 256 caracteres, apenas letras, numeros, _ e -
email: obrigatorio, email valido, maximo 256 caracteres
password: obrigatorio, minimo 8 caracteres, com maiuscula, minuscula, numero e caractere especial
```

Observacoes do fluxo:

```text
username e email sao normalizados com trim e lowercase antes de salvar
role customer e criada/atribuida automaticamente
token de verificacao de e-mail expira em 24 horas
o token bruto aparece apenas no log do FakeEmailService
o carrinho do usuario e criado de forma assincrona pelo outbox
```

Resposta esperada: `201 Created`

```json
{
  "success": true,
  "status": 201,
  "message": "Usuario registrado com sucesso",
  "data": {
    "id": "00000000-0000-0000-0000-000000000000",
    "username": "cliente_teste",
    "email": "cliente.teste@example.com",
    "emailVerified": false,
    "createdAt": "2026-05-26T00:00:00Z"
  },
  "timestamp": "2026-05-26T00:00:00Z",
  "path": "/api/v1/auth/register"
}
```

Erros comuns:

```text
400 Bad Request: campo obrigatorio ausente/invalido por Bean Validation
409 Conflict: username ou email ja esta em uso
422 Unprocessable Entity: regra de dominio falhou, por exemplo senha fraca ou username com caracteres invalidos
500 Internal Server Error: erro inesperado
```

Exemplo de conflito:

```json
{
  "success": false,
  "status": 409,
  "message": "Conflict",
  "errors": [
    "E-mail já está em uso"
  ],
  "timestamp": "2026-05-26T00:00:00Z",
  "path": "/api/v1/auth/register"
}
```

## Verificar E-mail

```text
POST {{base_url}}/api/v1/auth/verify-email
```

Auth: sem token.

Antes de chamar este endpoint, registre um usuario e copie o token exibido no console da aplicacao:

```text
[FAKE EMAIL] Verificacao de E-mail
TOKEN: valor-do-token
```

Body:

```json
{
  "token": "{{email_verification_token}}"
}
```

Regras do body:

```text
token: obrigatorio, nao pode ser vazio
```

Resposta esperada: `200 OK`

```json
{
  "success": true,
  "status": 200,
  "message": "E-mail verificado com sucesso",
  "timestamp": "2026-05-26T00:00:00Z",
  "path": "/api/v1/auth/verify-email"
}
```

Erros comuns:

```text
400 Bad Request: token ausente/vazio por Bean Validation ou token de tipo invalido
404 Not Found: token nao encontrado ou usuario do token nao encontrado
409 Conflict: token ja utilizado ou e-mail ja verificado
410 Gone: token expirado
422 Unprocessable Entity: regra de dominio falhou
500 Internal Server Error: erro inesperado
```

Exemplo de token nao encontrado:

```json
{
  "success": false,
  "status": 404,
  "message": "Not Found",
  "errors": [
    "Token inválido ou não encontrado"
  ],
  "timestamp": "2026-05-26T00:00:00Z",
  "path": "/api/v1/auth/verify-email"
}
```

## Swagger/OpenAPI

```text
GET {{base_url}}/swagger-ui.html
GET {{base_url}}/v3/api-docs
```

Auth: sem token.
