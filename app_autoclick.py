import json
import os
import threading
import time
from dataclasses import dataclass, asdict
from typing import Optional, Tuple

import pyautogui
import pystray
from PIL import Image, ImageDraw
from pystray import MenuItem as item

CONFIG_FILE = "config.json"


@dataclass
class AutoClickConfig:
    interval_seconds: float = 10.0
    click_x: Optional[int] = None
    click_y: Optional[int] = None
    button: str = "left"


class AutoClickApp:
    def __init__(self) -> None:
        self.config = self._load_config()
        self._running = False
        self._worker: Optional[threading.Thread] = None
        self._stop_event = threading.Event()
        pyautogui.FAILSAFE = True

    def _load_config(self) -> AutoClickConfig:
        if os.path.exists(CONFIG_FILE):
            with open(CONFIG_FILE, "r", encoding="utf-8") as f:
                data = json.load(f)
            return AutoClickConfig(**data)
        cfg = AutoClickConfig()
        self._save_config(cfg)
        return cfg

    def _save_config(self, cfg: AutoClickConfig) -> None:
        with open(CONFIG_FILE, "w", encoding="utf-8") as f:
            json.dump(asdict(cfg), f, indent=2, ensure_ascii=False)

    def _click_loop(self) -> None:
        while not self._stop_event.is_set():
            pos = self._get_click_position()
            pyautogui.click(x=pos[0], y=pos[1], button=self.config.button)
            self._stop_event.wait(self.config.interval_seconds)

    def _get_click_position(self) -> Tuple[int, int]:
        if self.config.click_x is not None and self.config.click_y is not None:
            return self.config.click_x, self.config.click_y
        current = pyautogui.position()
        return current.x, current.y

    def start(self, icon: pystray.Icon) -> None:
        if self._running:
            return
        self._stop_event.clear()
        self._worker = threading.Thread(target=self._click_loop, daemon=True)
        self._worker.start()
        self._running = True
        icon.title = f"AutoClick ativo ({self.config.interval_seconds}s)"
        self._refresh_menu(icon)

    def stop(self, icon: pystray.Icon) -> None:
        if not self._running:
            return
        self._stop_event.set()
        if self._worker:
            self._worker.join(timeout=1)
        self._running = False
        icon.title = "AutoClick parado"
        self._refresh_menu(icon)

    def set_interval(self, icon: pystray.Icon, value: float) -> None:
        was_running = self._running
        if was_running:
            self.stop(icon)
        self.config.interval_seconds = value
        self._save_config(self.config)
        if was_running:
            self.start(icon)

    def use_current_cursor(self, icon: pystray.Icon) -> None:
        pos = pyautogui.position()
        self.config.click_x = pos.x
        self.config.click_y = pos.y
        self._save_config(self.config)
        icon.title = f"Posição salva: ({pos.x}, {pos.y})"

    def clear_fixed_position(self, icon: pystray.Icon) -> None:
        self.config.click_x = None
        self.config.click_y = None
        self._save_config(self.config)
        icon.title = "Usando posição atual do mouse"

    def _refresh_menu(self, icon: pystray.Icon) -> None:
        icon.menu = self._build_menu(icon)
        icon.update_menu()

    def quit_app(self, icon: pystray.Icon) -> None:
        self.stop(icon)
        icon.stop()

    def _build_menu(self, icon: pystray.Icon) -> pystray.Menu:
        return pystray.Menu(
            item(
                "Iniciar autoclick",
                lambda: self.start(icon),
                enabled=lambda _: not self._running,
            ),
            item(
                "Parar autoclick",
                lambda: self.stop(icon),
                enabled=lambda _: self._running,
            ),
            pystray.Menu.SEPARATOR,
            item("Intervalo: 1s", lambda: self.set_interval(icon, 1)),
            item("Intervalo: 5s", lambda: self.set_interval(icon, 5)),
            item("Intervalo: 10s", lambda: self.set_interval(icon, 10)),
            item("Intervalo: 30s", lambda: self.set_interval(icon, 30)),
            pystray.Menu.SEPARATOR,
            item("Salvar posição atual", lambda: self.use_current_cursor(icon)),
            item("Usar posição dinâmica", lambda: self.clear_fixed_position(icon)),
            pystray.Menu.SEPARATOR,
            item("Sair", lambda: self.quit_app(icon)),
        )


def create_tray_icon() -> Image.Image:
    image = Image.new("RGB", (64, 64), color=(30, 30, 30))
    draw = ImageDraw.Draw(image)
    draw.ellipse((10, 10, 54, 54), fill=(0, 180, 255), outline=(255, 255, 255), width=2)
    draw.rectangle((30, 18, 34, 46), fill=(255, 255, 255))
    draw.rectangle((18, 30, 46, 34), fill=(255, 255, 255))
    return image


def main() -> None:
    app = AutoClickApp()
    icon = pystray.Icon("AutoClick", create_tray_icon(), "AutoClick parado")
    icon.menu = app._build_menu(icon)
    icon.run()


if __name__ == "__main__":
    main()
