# AutoClick em background

App em Python que roda em background (ícone na bandeja do sistema) e realiza cliques automáticos em intervalos configuráveis.

## Funcionalidades (desktop)

- Inicia e para o autoclick pelo menu da bandeja.
- Intervalo pré-configurado pelo menu (1s, 5s, 10s, 30s).
- Salva uma posição fixa de clique (coordenadas atuais do mouse).
- Alterna para posição dinâmica (clica onde o mouse estiver no momento).

## Requisitos (desktop)

- Python 3.10+
- Sistema com interface gráfica (Windows/Linux/macOS)

## Instalação (desktop)

```bash
python -m venv .venv
source .venv/bin/activate  # Linux/macOS
pip install -r requirements.txt
```

No Windows PowerShell:

```powershell
python -m venv .venv
.\.venv\Scripts\Activate.ps1
pip install -r requirements.txt
```

## Execução (desktop)

```bash
python app_autoclick.py
```

Depois de executar:

1. Procure o ícone do app na bandeja do sistema.
2. Clique com botão direito no ícone.
3. Configure intervalo e posição de clique.
4. Clique em **Iniciar autoclick**.

## Versão Android

Foi adicionada uma versão Android nativa em `android/` (Kotlin + Android Studio), com:

- Liga/desliga do autoclick.
- Intervalos de 1s, 5s, 10s e 30s.
- Ponto de clique fixo (salvo por toque) ou modo dinâmico.
- Serviço de acessibilidade para executar gestos de toque automáticos.

### Como abrir no Android Studio

1. Abra o Android Studio.
2. Selecione **Open** e escolha a pasta `android/`.
3. Aguarde sync do Gradle.
4. Rode em um dispositivo Android (API 26+).

### Como usar no Android

1. Abra o app **AutoClick Android**.
2. Toque em **Abrir acessibilidade** e habilite o serviço do app.
3. Escolha o intervalo.
4. (Opcional) Toque em **Salvar próximo toque...** e escolha um ponto fixo.
5. Ative **Ativar autoclick**.

> Observação: por restrições do Android, o autoclick depende de acessibilidade ativa e pode se comportar de forma diferente entre fabricantes/versões.

## Configuração persistente (desktop)

O app desktop cria/atualiza um `config.json` automaticamente com as opções escolhidas.

Exemplo:

```json
{
  "interval_seconds": 10.0,
  "click_x": null,
  "click_y": null,
  "button": "left"
}
```

## Segurança

No desktop, o `pyautogui` está com fail-safe ativado: mover o mouse para o canto superior esquerdo pode interromper automações em caso de necessidade.
