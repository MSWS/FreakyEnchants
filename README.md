# FreakyEnchantments
![File Size](https://img.shields.io/spiget/download-size/64154.svg?colorB=aa0000&label=File%20Size&style=flat-square) ![Code Size](https://img.shields.io/github/languages/code-size/msws/FreakyEnchants.svg?label=Code%20Size&style=flat-square) ![Downloads](https://img.shields.io/spiget/downloads/64154.svg?colorB=33ff33&label=Downloads&style=flat-square) ![Ratings](https://img.shields.io/spiget/rating/64154.svg?colorB=008800&label=Ratings&style=flat-square) ![Activity](https://img.shields.io/github/commit-activity/y/msws/FreakyEnchants.svg?label=Activity&colorB=FFA500&style=popout-square) ![License](https://img.shields.io/github/license/msws/FreakyEnchants.svg?label=License&style=popout-square) ![Versions](https://img.shields.io/spiget/tested-versions/64154.svg?colorB=800080&label=Versions&style=popout-square)

Features:
  * Over 40+ custom enchantments
  * NPC Support
  * Enchantments work with regular entities
  * Extremely customizable enchantments, messages, prices, and GUIs
  * Full Vault economy support, or optional custom economy system (tokens)
  * PlaceholderAPI support
  
PlaceholderAPI:
%fe_[name]%

balance - Returns the player's balance in decimal form (%fe_balance%)
balance_round - Returns the player's balance in integer form, rounded normally
balance_floor - Returns the player's balance in integer form, rounded down
balance_ceil - Returns the player's balance in integer form, rounded up

cost_[enchantment]_[level] - Returns the cost of the enchantment (level should be an integer) (%fe_cost_excavation_1%)

allow_[enchantment] - Returns whether or not an enchantment is allowed where the player is (%fe_allow_excavation%)
allowworld_[world]_enchantment] - Returns whether or not a specific enchantment is allowed in the specified world (%fe_allowworld_world_excavation%)

enchants - Returns a list of all enchantments separated by ,'s (%fe_enchants%)
currency - Returns the currency type set in the config.yml
