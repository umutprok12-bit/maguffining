Red Guns - Minecraft 1.12.2 Forge

Setup: copy gradlew, gradlew.bat and the gradle/ folder from your force cube project
into this folder (Gradle 4.9 + JDK 8). Then:
  gradlew setupDecompWorkspace   (once)
  gradlew build                  -> build/libs/redguns-1.0.jar
  gradlew runClient              -> test in game

Hand/GUI positions: src/main/java/com/redguns/GunBakedModel.java (buildTransforms).
Change a number, rebuild, test. Format: t(rotX, rotY, rotZ, moveX, moveY, moveZ, scale).
