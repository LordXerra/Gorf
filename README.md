# GORF

A recreation of the classic 1981 Midway arcade game Gorf, built with Java and LibGDX.

## Features

- All 5 original missions: Astro Battles, Laser Attack, Galaxians, Space Warp, and Flag Ship
- Authentic scoring system matching the original arcade
- Rank progression system: Space Cadet through Space Avenger
- Quark Laser mechanic (one shot at a time, re-fire cancels previous)
- Dynamic particle explosions with flying debris
- Parallax scrolling star background
- Colorful attract mode with title screen, high scores, help, and credits
- Top 10 high score table with 3-letter initial entry
- Full PS4/PS5 controller support (D-pad, left stick, Cross button, Options)
- Keyboard controls (WASD movement, Space to fire, Enter to start)
- F1 fullscreen/windowed toggle (setting persists between sessions)
- Procedurally generated retro sound effects

## Requirements

- Java 21 (Temurin JDK recommended)
- Gradle 8.12 (included via wrapper)

## Building

```bash
./gradlew :desktop:build
```

## Running

```bash
./gradlew :desktop:run
```

## Controls

| Action | Keyboard | Controller |
|--------|----------|------------|
| Move | WASD | D-pad / Left Stick |
| Fire | Space | Cross (X) |
| Start | Enter | Options |
| Fullscreen | F1 | F1 |

## Credits

- Coding by Tony Brice
- Technical support by Aaron Thorne
- Additional controls and game logic support from Claude
- Testing by Triona Melhuish, AJ Brice and Nailesh Sheth
- Original sprites by Midway, ripped by WilliamStef
