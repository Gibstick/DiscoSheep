DiscoSheep
==========

A plugin for [Bukkit](http://bukkit.org/), the Minecraft server API. This is a from-scratch remake of the fabulous [DiscoSheep plugin](http://forums.bukkit.org/threads/inactive-fun-discosheep-v0-3-uninvited-guesssts-permissions-and-source-code-522.7106/) which spawns a dance party of sheep at your whim. 

###DiscoFix###
An attempt to fix up DiscoSheep. No new big features. A short (non-binding) todo list:
* fix NPE for guests
* refactor command handling (use sk89q's framework) 
* fix wifi, bluetooth, GPS (wipe dalvik x333)
* you tell me

##A note on UUIDs##
DiscoSheep doesn't store anything after a party and getPlayer(name), and disco-party-on-join is handled by a permissions plugin. Thus, we don't need to worry about the migration to UUIDs.

###Versions###
Tested up to CraftBukkit build 2918 (RB for 1.6.4 R2.0); can be built with Java 7.

###[BukkitDev](http://dev.bukkit.org/bukkit-plugins/superdiscosheep/)###

###Releases###
See BukkitDev, or https://github.com/Gibstick/DiscoSheep/releases

###License###
Copyright (c) 2013 "Gibstick", "RangerMauve"

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
