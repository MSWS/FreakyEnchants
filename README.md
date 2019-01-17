# CustomEnchants
Features:
  * Over 25+ custom enchantments
  * NPC Support
  * Enchantments work with regular entities
  * Extremely customizable enchantments, messages, prices, and GUIs
  * Full Vault economy support, or optional custom economy system (tokens)
  * PlaceholderAPI support
  
PlaceholderAPI:
%ce_[name]%

balance - Returns the player's balance in decimal form (%ce_balance%)
balance_round - Returns the player's balance in integer form, rounded normally
balance_floor - Returns the player's balance in integer form, rounded down
balance_ceil - Returns the player's balance in integer form, rounded up

cost_[enchantment]_[level] - Returns the cost of the enchantment (level should be an integer) (%ce_cost_excavation_1%)

allow_[enchantment] - Returns whether or not an enchantment is allowed where the player is (%ce_allow_excavation%)
allowworld_[world]_enchantment] - Returns whether or not a specific enchantment is allowed in the specified world (%ce_allowworld_world_excavation%)

enchants - Returns a list of all enchantments separated by ,'s (%ce_enchants%)