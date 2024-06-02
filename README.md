# Minecraft-Launcher-For-Modded-Server
This is a custom launcher for a modded minecraft server i used to play on with some friends <br>
Almost the entirety of the code is from the project Javafx-launcher available here : https://github.com/Support-Launcher/javafx-launcher <br>
I also highly recommend the youtube serie from Bricklou which explains the code : https://www.youtube.com/playlist?list=PL4Iry42iWxQQo-xMzk2BRO1YOoHgG-slU <br>
It also heavily relies on FlowUpdater : https://github.com/FlowArg/FlowUpdater <br>
Big Shout-out to Bricklou and FlowArg for their work which helped me to have some fun with my friends on our minecraft server 🙌 <br>
Here are my modifications from the source code : <br>
<ul>
  <li>New design</li>
  <li>Removed the Mojang Connection since it's deprecated</li>
  <li>Changed Game and Forge Version to 1.20.1</li>
  <li>Added possibility to download the curse forge mods list from a FTP server (not secure tho, require to put the credentials in the code but it's fine if you don't plan to distribute the launcher publicly)</li>
  <li>Also download custom mods that aren't on Forge (must be added as arguments in the ModFileDeleter!)</li>
  <li>Added DownloadMod in the utils section which download : Curse Forge Mods List, version.txt, and custom mods </li>
  <li>Possibility to update the launcher if version in version.txt > current version of the user's launcher</li>
  <li>DownloadUpdate is called if the user want to download newer version and a cmd script in Main Class will replace the launcher by the newer version once downloaded</li>
</ul><br>

I use my Django WebApp hosted on PythonAnywhere to store the files and download them. The code is available here : https://github.com/NicolasDortu/Django-Minecraft-Server-WebApp <br>

## Connection page :
![image](https://github.com/NicolasDortu/Minecraft-Launcher-For-Modded-Server/assets/126513916/14fbd130-4894-409d-88d4-0d30dff906e3)


## Home page :
![image](https://github.com/NicolasDortu/Minecraft-Launcher-For-Modded-Server/assets/126513916/fc6bec10-41a5-41db-a54b-09fdec5c0b96)
