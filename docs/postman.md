# Postman

## Variaveis

```text
base_url = http://localhost:8080
```

## Headers padrao

```text
Content-Type: application/json
Accept: application/json
```

## Health Check

```text
GET {{base_url}}/actuator/health
```

Auth: sem token.

Resposta esperada:

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

Resposta esperada: `201 Created`

```json
{
  "success": true,
  "status": 201,
  "message": "User registered successfully",
  "data": {
    "id": "uuid-do-usuario",
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
400 Bad Request: JSON invalido ou campo obrigatorio ausente/invalido por Bean Validation
422 Unprocessable Content: username/email ja existe ou regra de dominio falhou
```

Observacao:

```text
Apos o registro, o carrinho do usuario e criado de forma assincrona pelo outbox. Nao existe endpoint HTTP do cart ainda.
```
