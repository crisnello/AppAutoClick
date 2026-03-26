# AutoClick em background

App em Python que roda em background (ícone na bandeja do sistema) e realiza cliques automáticos em intervalos configuráveis.

## Funcionalidades

- Inicia e para o autoclick pelo menu da bandeja.
- Intervalo pré-configurado pelo menu (1s, 5s, 10s, 30s).
- Salva uma posição fixa de clique (coordenadas atuais do mouse).
- Alterna para posição dinâmica (clica onde o mouse estiver no momento).

## Requisitos

- Python 3.10+
- Sistema com interface gráfica (Windows/Linux/macOS)

## Instalação

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

## Execução

```bash
python app_autoclick.py
```

Depois de executar:

1. Procure o ícone do app na bandeja do sistema.
2. Clique com botão direito no ícone.
3. Configure intervalo e posição de clique.
4. Clique em **Iniciar autoclick**.

## Configuração persistente

O app cria/atualiza um `config.json` automaticamente com as opções escolhidas.

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

O `pyautogui` está com fail-safe ativado: mover o mouse para o canto superior esquerdo pode interromper automações em caso de necessidade.
