# Evento Já (Android)

App Android nativo (Java) que consome a API REST de eventos feita em Spring Boot
(repositório `api-tra-mobile`).

## Como rodar

Precisa do JDK 17 (Temurin 17). O Gradle 8.11 deste projeto não roda com o JDK 25
que vem no Android Studio novo; no Android Studio, em *Settings > Build Tools > Gradle >
Gradle JDK*, escolha o JDK 17.

1. Suba a API (ver README do `api-tra-mobile`). Ela fica em `http://localhost:8080`.
2. Abra esta pasta no Android Studio e rode o app no emulador,
   ou pelo PowerShell: `.\rodar-app.ps1` (compila, abre o emulador, instala e abre o app).

O app abre na **tela de login**. Usuários:

| Usuário   | Senha     | O que pode fazer                         |
|-----------|-----------|------------------------------------------|
| `admin`   | `admin`   | ver, cadastrar, editar e excluir eventos |
| `usuario` | `usuario` | só ver os eventos                        |

O login fica salvo; para trocar de usuário use **Sair** no menu da lista de eventos.

No emulador o endereço da API é `http://10.0.2.2:8080/` (já é o padrão).

Pelo terminal:

```
.\gradlew.bat assembleDebug
.\gradlew.bat testDebugUnitTest
```

### Opções de build

| Propriedade  | Padrão                    | Para que serve                                   |
|--------------|---------------------------|--------------------------------------------------|
| `apiBaseUrl` | `http://10.0.2.2:8080/`   | Endereço da API (use o IP do PC num celular real) |
| `usarMock`   | `false`                   | `true` usa dados de exemplo, sem precisar da API |

Exemplo: `.\gradlew.bat assembleDebug -PusarMock=true`
Também dá para colocar no `gradle.properties` (ex.: `usarMock=true`).

## Organização do código

```
br.com.eventoja
├── data
│   ├── model        Evento, Usuario, LoginRequest, ErroApi, ValidadorEvento
│   ├── remote       EventoApi (endpoints do Retrofit), RetrofitClient,
│   │                TokenInterceptor, TratadorDeErros
│   ├── local        SessaoUsuario (guarda o usuário e o token do login)
│   └── repository   EventoRepository / UsuarioRepository (interfaces),
│                    versões Remote (API) e Mock (dados de exemplo),
│                    Repositorios (escolhe qual usar)
└── viewmodel        EventoViewModel, LoginViewModel, Resultado
```

Fluxo: **Activity → ViewModel → Repository → Retrofit → API**.

- As telas só conversam com os ViewModels.
- Os ViewModels devolvem um `Resultado` com o status `CARREGANDO`, `SUCESSO` ou `ERRO`.
- Em caso de erro, `resultado.getMensagem()` já vem pronta para mostrar ao usuário
  (sem conexão, tempo esgotado, ou a mensagem que a API mandou, ex.: "Titulo obrigatorio").
- O token do login é guardado e enviado sozinho no header `token`.
  Se a API responder 401, o usuário é deslogado.

## Como usar nas telas

### Lista de eventos

```java
EventoViewModel viewModel = new ViewModelProvider(this).get(EventoViewModel.class);

viewModel.getEventos().observe(this, resultado -> {
    if (resultado.isCarregando()) {
        // mostrar ProgressBar
    } else if (resultado.isSucesso()) {
        adapter.setEventos(resultado.getDados());
    } else {
        Toast.makeText(this, resultado.getMensagem(), Toast.LENGTH_LONG).show();
    }
});

viewModel.carregarEventos();
```

### Criar ou editar

```java
viewModel.getResultadoSalvar().observe(this, resultado -> {
    if (resultado.isSucesso()) {
        finish();
    } else if (resultado.isErro()) {
        Toast.makeText(this, resultado.getMensagem(), Toast.LENGTH_LONG).show();
    }
});

// Evento sem id = cria. Evento com id = atualiza.
viewModel.salvarEvento(evento);
```

Os campos são validados antes de ir para a API (título, local e datas obrigatórios,
datas no formato `dd/MM/yyyy HH:mm` e fim depois do início).

### Excluir

```java
viewModel.getResultadoExcluir().observe(this, resultado -> { ... });
viewModel.excluirEvento(evento.getId()); // a lista é recarregada sozinha
```

### Login

Sem login não dá para ver nada: o app abre na `LoginActivity`, e a `MainActivity`
volta para o login se a sessão acabar. Criar, editar e excluir só funcionam com um
usuário admin logado (`admin` / `admin`). O usuário `usuario` / `usuario` só consegue
ver os eventos.

```java
LoginViewModel loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
loginViewModel.getResultadoLogin().observe(this, resultado -> { ... });
loginViewModel.entrar(login, senha);
```

Use `viewModel.podeEditar()` para mostrar ou esconder os botões de criar, editar e excluir.

### Passar um evento para outra tela

`Evento` implementa `Serializable`:

```java
intent.putExtra("evento", evento);
Evento evento = (Evento) getIntent().getSerializableExtra("evento");
```
