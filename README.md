![AnturniaQuests](https://i.imgur.com/J5TQIJs.png)
**Overview:**
AnturniaQuests is a versatile quest plugin for Minecraft, allowing server administrators to create a personalized quest
system. With this plugin, players can engage in captivating quests and earn exciting rewards. Configuration is
straightforward, offering complete customization of quests and rewards.

Get the latest dev version here: https://jenkins.frinshy.me/job/AnturniaQuests/
**Wiki**: https://github.com/FrinshHD/AnturniaQuests/wiki

**Trailer:**  
[![](https://markdown-videos-api.jorgenkh.no/youtube/EeggEVhBkbY)](https://youtu.be/EeggEVhBkbY)

**Features:**

- **Custom Quests:** Effortlessly create your own quests through an intuitive configuration file.
- **Rewards:** Define unique rewards for completed quests, ranging from items to experience points and currencies.
- **Categories:** Organize quests into different categories for improved visibility.
- **Menu System:** Players can view their available quests in-game and track their progress.
- **Configuration-Friendly:** All aspects of the plugin are customizable via the configuration file to meet your
  server's needs.

**Installation:**

1. Place the JAR file in your Spigot server's "plugins" folder.
2. If you want to use the money feature please follow the instructions under "Special Note for Monetary Rewards".
3. Start or restart your server.
4. Adjust the configuration file as needed. You can find help on our
   wiki (https://github.com/FrinshHD/AnturniaQuests/wiki).

**Commands:**

- `/quests`: Open the ingame quest menu.
- `/quests reload`: Reload the plugin configurations
- `/quests help`: Get a list of all commands

**Permissions:**

- `quests.open`: permission to execute the /quests command
- `anturniaquests.admin.reload`: permission to execute the /quests reload command
- `quests.help`: permission to execute the /quests help command
- `anturniaquests.admin.updateNotify`: permission to get notified when a new update is avaliable

**Special Note for Monetary Rewards:**
If you intend to provide monetary rewards, ensure you have Vault and a compatible economy plugin installed on your
server. This allows seamless integration with in-game currency systems.

**Help and Support:**
Visit our [Discord server](https://discord.gg/89Dv8rqkpC) for assistance, feedback, and additional information.

**Feedback:**
We appreciate your feedback! Share your experiences, ideas, and suggestions on
the [Discord server](https://discord.gg/89Dv8rqkpC).  
![Discord Banner](https://discordapp.com/api/guilds/926392739904495626/widget.png?style=banner3)

**Images:**

![](https://i.imgur.com/x9SDfyR.png)
![](https://i.imgur.com/s7kweus.png)
![](https://i.imgur.com/gASsAKD.png)
![](https://i.imgur.com/U2UJC6p.png)
![](https://i.imgur.com/vLlW5TS.png)

**Configs:**

`config.yml`:

``` yml
database:
  type: sqlite #currently only sqlite is supported
```

`messages.properties`:

``` properties
quests.tip.useMultipleTimes=&6Tip: &7You can complete this quest multiple times!
quest.announce=&2%player% &7has completed the Quest &2%questName%
lore.alreadyCompleted=&a\u2714 &7You have already completed this quest &a\u2714
lore.requirements=&4Requirements:
lore.requirements.items.inInventory=&7 - &a%amountInInv%&7/%amount% &7%itemName%
lore.requirements.items.notInInventory=&7 - &c%amountInInv%&7/%amount% &7%itemName%
lore.rewards=&2Rewards:
lore.rewards.item=&7 - %amount%x %itemName%
lore.rewards.money=&7 - $%amount%
lore.rewards.commands=&7 - %name%
inventory.heading.color=&2
inventory.quest.color=&2
updateAvailable=&7There is a new update available for the &2AnturniaQuests&7 plugin, version &2%newVersion%&7 You are currently running version %currentVersion% Get the newest version here: &2https://www.spigotmc.org/resources/anturniaquests.113784/
```

`categories.yml`:

``` yml
combat:
  friendlyName: Combat
  description: You want to kill mobs? Here are the right quests for you!
  material: DIAMOND_SWORD
mining:
  friendlyName: Mining
  description: Just mine some stuff
  material: IRON_PICKAXE
farming:
  friendlyName: Farming
  description: You also like potatoes?
  material: GOLDEN_HOE
foraging:
  friendlyName: Foraging
  description: Everything can be made out of wood!
  material: JUNGLE_WOOD
```

`quests.yml`:

``` yml
combat1:
  friendlyName: Zombie Slayer
  description: The zombie apocalypse is upon us! Eliminate 20 zombies and collect
    their rotten flesh.
  category: combat
  oneTime: false
  requirements:
    ROTTEN_FLESH: 20
  rewards:
    items:
      - material: IRON_SWORD
        amount: 1
      - material: GOLDEN_APPLE
        amount: 2
    commands:
      - name: "1x Diamond Sword"
        command: "/give %player% diamond_sword"
    money: 2
combat2:
  friendlyName: Skeleton Archer
  description: Archery skills are no match for you! Defeat 30 skeletons and gather
    their bones for crafting purposes.
  category: combat
  requirements:
    BONE: 30
  rewards:
    items:
      - material: BOW
        amount: 1
      - material: ARROW
        amount: 32
    money: 60
combat3:
  friendlyName: Spider Exterminator
  description: Arachnophobia be gone! Annihilate 25 spiders and retrieve their eyes
    as trophies.
  category: combat
  announce: true
  requirements:
    SPIDER_EYE: 25
  rewards:
    items:
      - material: IRON_CHESTPLATE
        amount: 1
    money: 20
#--------------------------------------------------------------
farming1:
  friendlyName: Wheat Farmer
  description: Become the master of agriculture! Cultivate and harvest 128 wheat to
    fill your granary.
  category: farming
  requirements:
    WHEAT: 128
    IRON_HOE: 1
  rewards:
    items:
      - material: IRON_HOE
        amount: 1
      - material: BREAD
        amount: 64
    money: 60
farming2:
  friendlyName: Melon Enthusiast
  description: The thirst quencher! Gather a bountiful harvest of 64 melons for a
    refreshing feast.
  category: farming
  requirements:
    MELON_SLICE: 64
  rewards:
    items:
      - material: GOLDEN_AXE
        amount: 1
      - material: GLISTERING_MELON_SLICE
        amount: 16
    money: 60
farming3:
  friendlyName: Carrot Harvest
  description: Dig deep into the earth! Harvest 96 carrots to create a carrot paradise.
  category: farming
  requirements:
    CARROT: 96
    IRON_SHOVEL: 1
  rewards:
    items:
      - material: IRON_SHOVEL
        amount: 1
      - material: GOLDEN_CARROT
        amount: 16
    money: 60
#--------------------------------------------------------------
foraging1:
  friendlyName: Wood Collector
  description: Embrace nature! Gather 256 logs of any type and become a master lumberjack.
  category: foraging
  requirements:
    OAK_LOG: 128
    SPRUCE_LOG: 64
    BIRCH_LOG: 32
    JUNGLE_LOG: 32
    ACACIA_LOG: 16
    DARK_OAK_LOG: 16
  rewards:
    items:
      - material: DIAMOND_AXE
        amount: 1
      - material: OAK_PLANKS
        amount: 512
    money: 60
foraging2:
  friendlyName: Berry Picker
  description: The forest is a treasure trove of delicious berries! Collect 128 sweet
    berries for a tasty treat.
  category: foraging
  requirements:
    SWEET_BERRIES: 128
  rewards:
    items:
      - material: LEATHER_CHESTPLATE
        amount: 1
      - material: SWEET_BERRIES
        amount: 64
foraging3:
  friendlyName: Flower Power
  description: Embrace the beauty of nature! Collect 64 different flowers and create
    a stunning garden.
  category: foraging
  requirements:
    DANDELION: 4
    POPPY: 4
    BLUE_ORCHID: 4
    ALLIUM: 4
    AZURE_BLUET: 4
    RED_TULIP: 4
    ORANGE_TULIP: 4
    WHITE_TULIP: 4
    PINK_TULIP: 4
    OXEYE_DAISY: 4
    CORN_FLOWER: 4
    LILY_OF_THE_VALLEY: 4
    WITHER_ROSE: 4
    SUNFLOWER: 4
    LILAC: 4
    ROSE_BUSH: 4
    PEONY: 4
  rewards:
    items:
      - material: BONE_MEAL
        amount: 64
      - material: FLOWER_POT
        amount: 4
#--------------------------------------------------------------
mining1:
  friendlyName: Coal Miner
  description: Descend into the depths! Mine 128 blocks of coal ore to fuel your adventures.
  category: mining
  requirements:
    COAL: 128
  rewards:
    items:
      - material: TORCH
        amount: 128
      - material: COAL_BLOCK
        amount: 8
mining2:
  friendlyName: Iron Seeker
  description: Iron is the backbone of any thriving civilization. Mine 64 iron ore
    to bolster your resources.
  category: mining
  requirements:
    IRON_ORE: 64
  rewards:
    items:
      - material: IRON_PICKAXE
        amount: 1
      - material: IRON_INGOT
        amount: 64
mining3:
  friendlyName: Diamond Prospector
  description: Diamonds are a miner's best friend. Unearth 16 diamonds to add sparkle
    to your collection.
  category: mining
  requirements:
    DIAMOND: 16
  rewards:
    items:
      - material: DIAMOND_PICKAXE
        amount: 1
      - material: DIAMOND
        amount: 16
#--------------------------------------------------------------
```

**bStats:**
![Bstats](https://bstats.org/signatures/bukkit/AnturniaQuests.svg)
