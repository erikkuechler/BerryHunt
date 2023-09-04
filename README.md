# BerryHunt
[![Resource](https://img.shields.io/badge/SpigotMC-Resource-orange.svg)](https://www.spigotmc.org/resources/berryhunt.112469/)  

BerryHunt is a new and unique minigame plugin for Minecraft. Players can collect berries and the player with the most berries wins. In a scoreboard all players can see the number of their Sweet berries and those of the others. Each player also has the opportunity to beat his own high score. But be careful: other players can steal your berries!

## Configuration
Settings can be changed in the **config.yml** file. For the game to work properly, the 4 coordinates pos1, pos2, lobby and spawn must be set.  

**density**  
Percentage of the area in which sweet berry bushes will be generated.  

**countdown**  
The countdown until the game starts in seconds. During this period new players can join.  

**duration**  
Duration of the game in seconds. During this time players can collect the berries.

**pos1 and pos2**  
The coordinates "pos1" and "pos2" are used to specify an area in the Minecraft world where Sweet berry bushes are automatically placed.

**lobby and spawn**  
Players are teleported to the "spawn" coordinate after joining and to the "lobby" coordinate after leaving the game.

## Commands and Permissions
Main command is /berryhunt. It is also possible to use /bh as an alias.

| Command                                                                              | Permission      |
|--------------------------------------------------------------------------------------|-----------------|
| **/berryhunt join**<br /> Join the BerryHunt game                                    | everyone        |
| **/berryhunt leave**<br /> Leave the BerryHunt game                                  | everyone        |
| **/berryhunt pos1**<br /> Sets position 1 for the BerryHunt area                     | berryhunt.admin |
| **/berryhunt pos2**<br /> Sets position 2 for the BerryHunt area                     | berryhunt.admin |
| **/berryhunt lobby**<br /> Sets lobby                                                | berryhunt.admin |
| **/berryhunt spawn**<br /> Sets spawn for the BerryHunt area                         | berryhunt.admin |
| **/berryhunt placebushes**<br /> Places sweetberry bushes in the designated area     | berryhunt.admin |
| **/berryhunt removebushes**<br /> Removes sweetberry bushes from the designated area | berryhunt.admin |
| **/berryhunt starting**<br /> Activation of start state by admin                     | berryhunt.admin |
