# DWM AutoArmor — Minecraft 1.8.9 Forge

Standalone Forge 1.8.9 mod project with:

- `G` — toggle AutoArmor
- `H` — open settings GUI
- Armor scoring
- Durability threshold
- Optional enchantment scoring
- Optional inventory open/close behavior
- Pause while moving / using an item

## Build

This project targets Forge `1.8.9-11.15.1.2318`.

1. Install Java 8.
2. Put this repository in a clean directory.
3. Run:

```text
./gradlew setupDecompWorkspace
./gradlew build
```

On Windows:

```text
gradlew.bat setupDecompWorkspace
gradlew.bat build
```

The finished JAR will be in:

```text
build/libs/AutoArmor-1.8.9-1.0.0.jar
```

Copy that JAR to the Minecraft 1.8.9 Forge `mods` folder.

## Important

This is an independent Forge implementation of the AutoArmor behavior found in the supplied JAR; it does not include the original client's proprietary classes.
