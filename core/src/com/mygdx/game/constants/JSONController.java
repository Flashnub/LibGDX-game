package com.mygdx.game.constants;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.mygdx.game.assets.EntityUIData;
import com.mygdx.game.model.actions.AbilitySettings;
import com.mygdx.game.model.actions.AttackSettings;
import com.mygdx.game.model.actions.WorldAttackSettings;
import com.mygdx.game.model.characters.CharacterProperties;
import com.mygdx.game.model.characters.EntityUIDataType;
import com.mygdx.game.model.characters.NPCProperties;
import com.mygdx.game.model.projectiles.ExplosionSettings;
import com.mygdx.game.model.projectiles.ProjectileSettings;
import com.mygdx.game.model.weapons.WeaponProperties;
import com.mygdx.game.model.worldObjects.Item;

public class JSONController {
    private static String jsonFilePath = "Json/";
    public static String globalFilePath = jsonFilePath + "_Global/"; 

	public static HashMap<String, AttackSettings> attacks = loadAttacksFromJSON() ;
    public static HashMap<String, AbilitySettings> abilities = loadAbilitiesFromJSON();
    public static HashMap<String, CharacterProperties> characterProperties = new HashMap<String, CharacterProperties>();
    public static HashMap<String, Item> items = loadItemsFromJSON();
    public static HashMap<String, ExplosionSettings> explosions = loadExplosionsFromJSON();
    public static HashMap<String, ProjectileSettings> projectiles = loadProjectilesFromJSON();
    public static HashMap<String, WorldAttackSettings> worldAttacks = loadWorldAttacksFromJSON();
    public static HashMap<String, EntityUIData> uiDatas = new HashMap<String, EntityUIData>();
    public static HashMap<String, NPCProperties> npcProperties = new HashMap<String, NPCProperties>();
    public static HashMap<String, WeaponProperties> weaponProperties = new HashMap <String, WeaponProperties>();
  
	 
	@SuppressWarnings("unchecked")
    private static HashMap<String, AttackSettings> loadAttacksFromJSON() {
        Json json = new Json();
	    HashMap <String, AttackSettings> tempAttacks = json.fromJson(HashMap.class, Gdx.files.internal(globalFilePath + "attacks.json"));
	    return tempAttacks;
    }
	
	@SuppressWarnings("unchecked")
    private static HashMap<String, AbilitySettings> loadAbilitiesFromJSON() {
        Json json = new Json();
	    HashMap <String, AbilitySettings> settings = json.fromJson(HashMap.class, Gdx.files.internal(globalFilePath + "abilities.json"));
	    return settings;
    }
	
	
	@SuppressWarnings("unchecked")
    private static HashMap<String, ProjectileSettings> loadProjectilesFromJSON() {
        Json json = new Json();
        HashMap<String, ProjectileSettings> settings = json.fromJson(HashMap.class, Gdx.files.internal(globalFilePath + "projectiles.json"));
        return settings;
    }
	
	@SuppressWarnings("unchecked")
    private static HashMap<String, WorldAttackSettings> loadWorldAttacksFromJSON() {
        Json json = new Json();
        HashMap<String, WorldAttackSettings> settings = json.fromJson(HashMap.class, Gdx.files.internal(globalFilePath + "worldAttacks.json"));
        return settings;
    }
    
	@SuppressWarnings("unchecked")
    private static HashMap<String, Item> loadItemsFromJSON() {
        Json json = new Json();
	    HashMap <String, Item> items = json.fromJson(HashMap.class, Gdx.files.internal(globalFilePath + "items.json"));
        return items;
    }
	
	@SuppressWarnings("unchecked")
    private static HashMap<String, ExplosionSettings> loadExplosionsFromJSON() {
        Json json = new Json();
	    HashMap <String, ExplosionSettings> explosions = json.fromJson(HashMap.class, Gdx.files.internal(globalFilePath + "explosions.json"));
        return explosions;
    }

    public static EntityUIData loadUIDataFromJSONForEntity(String name, EntityUIDataType dataType) {
        if (!uiDatas.containsKey(name)) {
            Json json = new Json();
            String filePath = "";
            switch (dataType) {
            case CHARACTER:
            	filePath = jsonFilePath + name + "/UIData.json";
            	break;
            case WORLDOBJECT:
            	filePath = jsonFilePath + "WorldObjects/" + name + "/UIData.json";
            	break;
            case PROJECTILE:
            	filePath = jsonFilePath + "Projectiles/" + name + "/UIData.json";
            	break;
            case EXPLOSION:
            	filePath = jsonFilePath + "Explosions/" + name + "/UIData.json";
            	break;
            }
        	EntityUIData uiData = json.fromJson(EntityUIData.class, Gdx.files.internal(filePath));
        	uiDatas.put(name, uiData);
        	return uiData;
        }
        else {
        	return uiDatas.get(name);
        }
    }
	
	public static CharacterProperties loadCharacterProperties(String characterName) {
		if (!characterProperties.containsKey(characterName)) {
	        Json json = new Json();
	        CharacterProperties properties = json.fromJson(CharacterProperties.class, Gdx.files.internal(jsonFilePath + characterName + "/properties.json"));
	        if (properties != null) {
				characterProperties.put(characterName, properties);		
	        }
		}
		CharacterProperties properties = characterProperties.get(characterName);
		if (properties != null) {
			properties = properties.cloneProperties();
		}
		return properties;
	}
	
	public static NPCProperties loadNPCProperties(String characterName) {
		if (!npcProperties.containsKey(characterName)) {
	        Json json = new Json();
			FileHandle fileHandle = Gdx.files.local(jsonFilePath + characterName + "/NPCProperties.json");
			if (fileHandle.exists()) {
		        NPCProperties properties = json.fromJson(NPCProperties.class, Gdx.files.internal(jsonFilePath + characterName + "/NPCProperties.json"));
		        if (properties != null) {
			        npcProperties.put(characterName, properties);		
		        }
			}

		}
		return npcProperties.get(characterName);
	}
	
	public static WeaponProperties loadWeaponProperties(String weaponName, String characterName) {
		if (!weaponProperties.containsKey(weaponName)) {
	        Json json = new Json();
			FileHandle fileHandle = Gdx.files.local(jsonFilePath + characterName + "/" + weaponName + ".json");
			if (fileHandle.exists()) {
				WeaponProperties properties = json.fromJson(WeaponProperties.class, Gdx.files.internal(jsonFilePath + characterName + "/" + weaponName + ".json"));
		        if (properties != null) {
		        	weaponProperties.put(weaponName, properties);		
		        }
			}

		}
		return weaponProperties.get(weaponName);
	}
	
}
