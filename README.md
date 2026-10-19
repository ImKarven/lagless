# Lagless

Lagless is designed to improve gameplay experience for players with high latency on Minecraft servers.
<br />
Lagless modifies the behavior of sending some packets in the server in order to make the clients appear less annoyed when they have high ping.

Currently, Lagless can be installed as a mod for Fabric servers. Support for Paper server is in development, but it is not a plugin. See [LaglessPurpur](https://github.com/ImKarven/LaglessPurpur) for more information.

> [!WARNING]
> Lagless is still in alpha/experimental state. It is nice if you use the mod/plugin and check out its feature. However, it is also important to note that Lagless can cause visual bugs or desynchronizations. Please report those bugs if you found any, your findings are greatly appreciated ❤️

## Showcase

https://github.com/user-attachments/assets/8de83c34-ce29-4088-aa49-b971c0ec6d6a

## Supported Versions
Lagless is designed to upstream with the latest version of the game. This means that when a new Minecraft version releases, older versions will get deprecated. This makes development much easier to deal with. Lagless still leaves a margin of time for supporting the outdated version when a new Minecraft version releases, this gives time for everyone to update to the new Minecraft version before Lagless completely ends its support for the outdated version.

## Building
Lagless uses Gradle as its build system.

You can get a working Fabric mod jar file by building the mod from source, following the steps below.

> [!NOTE]
> Outdated. Needs specific steps for both Paper and Fabric

### Requirements
- Java 25
- Git

### Steps
1. Clone this repository:
```sh
git clone https://github.com/ImKarven/lagless.git
cd lagless/
```

2. Build the Fabric mod

For Linux/MacOS:
```sh
./gradlew build
```

For Windows:
```batch
gradlew.bat build
```

3. Obtain the jar file

The jar file should be built, located in `build/libs` directory.
