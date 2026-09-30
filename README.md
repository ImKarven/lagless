# Lagless

Lagless is designed to improve gameplay experience for players with high latency on Minecraft servers.
<br />
Lagless modifies the behavior of sending some packets in the server in order to make the clients appear less annoyed when they have high ping.

Currently, Lagless can be installed as a mod for Fabric servers. Plugin versions for Paper and Sponge servers are planned!

> [!WARNING]
> Lagless is still in alpha/experimental state. It is nice if you use the mod/plugin and check out its feature. However, it is also important to note that Lagless can cause visual bugs or desynchronizations. Please report those bugs if you found any, your findings are greatly appreciated ❤️

## Supported Versions
Lagless only supports Minecraft version 26.3 and above. There will not be any back-ports to older versions.

## Building
Lagless uses Gradle as its build system.

You can get a working Fabric mod jar file by building the mod from source, following the steps below.

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
