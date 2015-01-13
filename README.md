DiscoSheep
==========

A plugin for [Bukkit](http://bukkit.org/)/Spigot, the Minecraft server plugin API. This is a from-scratch remake of the fabulous [DiscoSheep plugin](http://forums.bukkit.org/threads/inactive-fun-discosheep-v0-3-uninvited-guesssts-permissions-and-source-code-522.7106/) which spawns a dance party of sheep at your whim. 

###A Note on UUIDs###
DiscoSheep doesn't store anything after a party, and disco-party-on-join is handled by a permissions plugin. Thus, we don't need to worry about the migration to UUIDs.

###A Note on Performance###
DiscoSheep can be a fairly resource intensive plugin, but only when it is running. The events are unregistered when there are no parties. To prevent server slowdowns, set sensible limits in your config.yml, though the defaults are already fairly conservative.

###[Documentation](https://github.com/Gibstick/DiscoSheep/wiki)###

###Versions###
Tested up to CraftBukkit build #3092 (MC: 1.7.9); can be built with Java 7.
Also works as-is with Spigot for 1.8.

###[~~BukkitDev~~](http://dev.bukkit.org/bukkit-plugins/superdiscosheep/)###
No longer updated.

###Releases###
See BukkitDev, or https://github.com/Gibstick/DiscoSheep/releases

###License###
See LICENSE.md
