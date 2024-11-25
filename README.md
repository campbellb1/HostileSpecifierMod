Certainly! Here’s a simple README file for your mod that explains the configuration and how to set it up:

---

# Hostile Mod - README

## Introduction

The **Hostile Mod** allows you to modify the behavior of entities in Minecraft, making them hostile based on certain conditions such as factions and specific target entities. With this mod, you can create custom groups of hostile entities, specify which entities they should target, and define exceptions (whitelist entities).

This README will guide you on how to configure the mod's behavior using the generated configuration file.

---

## Configuration File

The configuration for **Hostile Mod** is stored in a **JSON file** located in your Minecraft `config` folder. By default, the file is named `hostilemod_config.json`.

### Default Structure

Here’s an example of the configuration file structure:

```json
{
  "default": {
    "hostileEntities": [
      "toroquest:toroquest_sentry"
    ],
    "hostileFactions": [],
    "targetEntities": [
      "millenaire:*"
    ],
    "whitelistEntities": []
  },
  "test": {
    "hostileEntities": [],
    "hostileFactions": [
      "brigand"
    ],
    "targetEntities": [
      "minecraft:player",
      "minecraft:villager",
      "millenaire:*"
    ],
    "whitelistEntities": [
      "minecraft:villager"
    ]
  }
}
```

### Explanation of Configuration

- **`hostileEntities`**:  
  A list of entity registry names (or IDs) that should be made hostile. Example: `"minecraft:cow"`, `"toroquest:toroquest_sentry"`.  
  **Tip**: Leave this list empty if you want to use the faction system instead of specific entities.

- **`hostileFactions`**:  
  A list of factions (e.g., `"brigand"`, `"bandit"`) that should be made hostile. Entities from these factions will be hostile to the entities defined in the `targetEntities` list.  
  **Tip**: Use this for faction-based hostility, instead of defining each hostile entity individually.

- **`targetEntities`**:  
  A list of entities or namespaces that the hostile entities will target. For example:
    - `"minecraft:player"` – hostile entities will target players.
    - `"minecraft:villager"` – hostile entities will target villagers.
    - `"millenaire:*"` – hostile entities will target all entities from the Millénaire mod.

- **`whitelistEntities`**:  
  A list of entities that should **not** be attacked by hostile entities. For example, if you don’t want villagers to be attacked, add `"minecraft:villager"` to this list.

### Example Configuration Breakdown

#### Default Group

```json
"default": {
  "hostileEntities": [
    "toroquest:toroquest_sentry"
  ],
  "hostileFactions": [],
  "targetEntities": [
    "millenaire:*"
  ],
  "whitelistEntities": []
}
```

- This setup makes **Toroquest Sentries** hostile to **all entities from the Millénaire mod**, but it does not whitelist any entities.

#### Test Group

```json
"test": {
  "hostileEntities": [],
  "hostileFactions": [
    "brigand"
  ],
  "targetEntities": [
    "minecraft:player",
    "minecraft:villager",
    "millenaire:*"
  ],
  "whitelistEntities": [
    "minecraft:villager"
  ]
}
```

- This setup makes **all entities in the "brigand" faction** hostile to **players**, **villagers**, and **Millénaire mod entities**, but **villagers** are whitelisted and will not be attacked.

---

## Adding More Groups

You can add as many groups as you want to the configuration file. Each group can have its own set of hostile entities, factions, target entities, and whitelist entities.

### Example of Adding a New Group

```json
"myGroup": {
  "hostileEntities": [
    "minecraft:zombie",
    "minecraft:skeleton"
  ],
  "hostileFactions": [
    "bandit"
  ],
  "targetEntities": [
    "minecraft:player"
  ],
  "whitelistEntities": [
    "minecraft:villager"
  ]
}
```

In this example, both **Zombies** and **Skeletons** are hostile to **players**, and all **bandit faction** entities will also target players. However, **villagers** are whitelisted and will not be attacked.

---

## Troubleshooting

- **Entities Not Being Made Hostile**:  
  Ensure that the entities you want to be hostile are correctly listed in the `hostileEntities` array or are part of a valid faction in the `hostileFactions` array.

- **Target Entities Not Being Attacked**:  
  Make sure that the entities you're targeting (e.g., players, villagers) are correctly listed in the `targetEntities` array.

- **Whitelist Not Working**:  
  Double-check that the entities you want to exclude from being attacked are correctly added to the `whitelistEntities` array.

---

## Conclusion

With the **Hostile Mod**, you can easily customize which entities become hostile, define what they target, and exclude specific entities from being attacked. By editing the configuration file, you can fine-tune the behavior of hostile entities to suit your world.

If you have any questions or issues, feel free to reach out via the mod’s support channels or check for updates.

---

### License

This mod is licensed under the [Insert License Here] License.  
Please check the `LICENSE` file for more details.

---

This README provides a basic overview of how to configure the mod using the JSON configuration file, and it explains each part of the configuration clearly for users.