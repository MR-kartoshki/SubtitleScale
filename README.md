# SubtitleScale
SubtitleScale is a client-side Fabric mod that lets you resize the vanilla subtitles overlay.

## What it does
- Scales the vanilla subtitles overlay as one grouped UI element
- Adds in-game config controls (Mod Menu) for subtitle scale and X/Y offsets
- Adds profile presets: `Default`, `Cinematic`, `Accessibility Large`
- Adds quick scale preset cycling with `F3 +` and `F3 -`
- Supports Minecraft `1.21.9`, `1.21.10`, and `1.21.11`

## Installation
1. Install Fabric Loader for the Minecraft version you intend to use
2. Install dependencies: Fabric API, Cloth Config, Mod Menu
3. Put `subtitlescale-<mod_version>.jar` in your `mods` folder
4. Launch Minecraft

## Configuration
Use Mod Menu for the config screen.

Config file:
- `%APPDATA%/.minecraft/config/subtitlescale/config.json`

Main options:
- `scale` (`0.5` to `2.0`, default `1.25`)
- `offsetX` (`-400` to `400`, default `0`)
- `offsetY` (`-400` to `400`, default `0`)
- `profile` (`DEFAULT`, `CINEMATIC`, `ACCESSIBILITY_LARGE`)

## License
MIT. See `LICENSE`.

## Credits
- MR-Kartoshki (GitHub)
- freddy._.fazbear (Discord)
