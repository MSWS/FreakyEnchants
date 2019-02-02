# FreakyEnchantments
'<img alt="undefined" src="https://img.shields.io/spiget/download-size/64154.svg?colorB=aa0000&label=File%20Size&style=flat-square">
Features:
  * Over 35+ custom enchantments
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