# KillStreaksSystem
Spigot killstreaks plugin with rewards and placeholderapi support
## Commands
  * /killstreak [player] - Shows your or other player kill streak information 
  * /killstreak restore <player> - Restores last player kill streak before death
  * /killstreak set <player> <amount> - Sets player kill streak to given amount
## Placeholders
* %KillstreakSystem_topcks_1% - Placeholder for player with highest current kill streaks, number can be from 1 to 10
* %KillstreakSystem_tobcks_1% - Placeholder for player with all time highest kill streak, number can go from 1 to 10
  
  This will replace for their InGame name, you can append \_number to the end of placeholder to display number of kill streak instead (%KillstreakSystem_topcks_1_number%)
## Requirements
  * [PlaceholderAPI](https://github.com/PlaceholderAPI/PlaceholderAPI) for handling placeholders 
  * [Vault](https://www.spigotmc.org/resources/vault.34315/) for economy
