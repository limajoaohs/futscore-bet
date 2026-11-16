# FutScore Bet

Casa de apostas esportivas (futebol) com **créditos virtuais**, construída sobre o backend do
[futscore-backend](https://github.com/limajoaohs/futscore-backend) (Spring Boot). O mesmo app serve a API REST e o site.

> Projeto de demonstração/portfólio. Não movimenta dinheiro real. Operar apostas com dinheiro real no Brasil
> exige autorização da SPA/Ministério da Fazenda (Lei 14.790/2023), KYC, verificação de idade e meios de pagamento homologados.

## O que tem

- **Mercado 1X2** (Casa / Empate / Fora) com odds decimais e margem da casa (~6%).
- **Carteira**: todo novo usuário ganha C$ 1.000 em créditos virtuais.
- **Bilhete**: aposta simples, valor de C$ 1 a C$ 10.000, retorno potencial calculado na hora; a odd fica travada no momento da aposta.
- **Liquidação automática** a cada minuto: paga `valor × odd` nas vencedoras, devolve o valor em partidas canceladas.
- **Painel admin**: registrar o placar final de uma partida encerra o mercado e liquida as apostas.
- **Partidas reais** via Football-Data.org (se houver token) ou **rodada demo** gerada na inicialização.
- Tudo do projeto original continua disponível: grupos de bolão, convites, palpites, ranking, regras de pontuação.

## Rodando

Requer JDK 17+.

```bash
./gradlew bootRun          # Windows: gradlew.bat bootRun
```

Abra http://localhost:8085. Admin padrão: `admin@futscore.bet` / código `123456`.

| Variável | Padrão | Para quê |
|---|---|---|
| `PORT` | `8085` | porta HTTP |
| `FOOTBALL_API_TOKEN` | vazio | token da football-data.org; vazio desliga a sincronização |
| `DEMO_SEED` | `true` | cria partidas de exemplo quando o banco está vazio |

Banco H2 em memória: os dados somem ao reiniciar.

## API de apostas

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/usuarios` | cadastro (`nome, email, username, endereco, codigo`) |
| `POST` | `/usuarios/login` | `{email, codigo}` → usuário com `saldo` |
| `GET` | `/partidas` | partidas com mercado aberto e odds |
| `POST` | `/apostas?usuarioId&codigoAcesso` | `{partidaId, palpite: CASA\|EMPATE\|FORA, valor}` |
| `GET` | `/apostas?usuarioId&codigoAcesso` | histórico de apostas |
| `POST` | `/partidas/{id}/resultado?adminId&codigoAcesso` | `{golsMandante, golsVisitante}` (admin) |

Swagger: http://localhost:8085/swagger-ui/index.html

## Testes

```bash
./gradlew test
```

## Limitações conhecidas

- Autenticação herdada do projeto original: código numérico de 6 dígitos enviado como parâmetro, sem hash nem sessão/JWT.
- Odds derivadas de forma determinística do nome dos times — trocar por um feed de odds real.
- Apenas apostas simples (sem múltiplas/cash-out).

## Créditos

Backend original desenvolvido na disciplina de Projeto de Software — Ciência da Computação, UFCG.
