# SubtitleScale
SubtitleScale is a client-side Fabric mod that lets you resize the vanilla subtitles overlay.

## What it does
- Scales the vanilla subtitles overlay as one grouped UI element
- Adds an in-game config slider (in Mod Menu) for subtitle scale (`0.5x` to `2.0x`)
- Works without Mod Menu installed (config button only appears when Mod Menu is present)
- Supports Minecraft `1.21.9`, `1.21.10`, and `1.21.11`

## Installation
1. Install Fabric Loader for the Minecraft version you intend to use
2. Install dependencies: Fabric API, Cloth Config, Mod Menu
3. Put `subtitlescale-<version>.jar` in your `mods` folder
4. Launch Minecraft

## Configuration
Use Mod Menu for the config screen.

Config file:
- `%APPDATA%/.minecraft/config/subtitlescale/config.json`

Main option:
- `scale` (`0.5` to `2.0`, default `1.25`)

## Telemetry
SubtitleScale sends minimal anonymous usage telemetry.

- No personal data is collected.
- Disable anytime in Mod Menu (`Telemetry -> Enable telemetry`) or by setting `enabled: false` in `config/subtitlescale/telemetry.json`.


## License
MIT. See `LICENSE`.

## Credits
- MR-Kartoshki (GitHub)
- freddy._.fazbear (Discord)