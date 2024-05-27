
**Configuration example**
```
fintechmarket:
  mock: true
  branchKey: alfa_es
  baseUrl: https://decision.testing.fintech-market.com/api/v1/${fintechmarket.branchKey}
  client:
    grantType: client_credentials
    clientId: {client_id}
    clientSecret: {client_secret}
    accessTokenUri: https://entrance.fintech-market.com/oauth/token
```
