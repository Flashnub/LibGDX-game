package com.mygdx.game.constants;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.mygdx.game.assets.EntityUIData;
import com.mygdx.game.model.actions.AbilitySettings;
import com.mygdx.game.model.actions.AttackSettings;
import com.mygdx.game.model.actions.ProjectileAttackSettings;
import com.mygdx.game.model.characters.CharacterProperties;
import com.mygdx.game.model.characters.EntityUIDataType;
import com.mygdx.game.model.items.Item;
import com.mygdx.game.model.projectiles.ProjectileSettings;

public class JSONController {
    private static String jsonFilePath = "Json/";
    public static String globalFilePath = jsonFilePath + "_Global/"; 

	public static HashMap<String, AttackSettings> attacks = loadAttacksFromJSON() ;
    public static HashMap<String, ProjectileSettings> projectiles = loadProjectilesFromJSON();
    public static HashMap<String, AbilitySettings> abilities = loadAbilitiesFromJSON();
    public static HashMap<String, CharacterProperties> characterProperties = new HashMap<String, CharacterProperties>();
    public static HashMap<String, Item> items = loadItemsFromJSON();
    public static HashMap<String, EntityUIData> uiDatas = new HashMap<String, EntityUIData>();
    public static HashMap<String, ProjectileAttackSettings> projectileAttacks = loadProjectileAttacksFromJSON();
  
	 
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
    private static HashMap<String, ProjectileAttackSettings> loadProjectileAttacksFromJSON() {
        Json json = new Json();
        HashMap<String, ProjectileAttackSettings> settings = json.fromJson(HashMap.class, Gdx.files.internal(globalFilePath + "projectileAttacks.json"));
        return settings;
    }
    
	@SuppressWarnings("unchecked")
    private static HashMap<String, Item> loadItemsFromJSON() {
        Json json = new Json();
	    HashMap <String, Item> items = json.fromJson(HashMap.class, Gdx.files.internal(globalFilePath + "items.json"));
        return items;
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
            	filePath = jsonFilePath + "WorldObjects/" + name + "UIData.json";
            	break;
            case PROJECTILE:
            	filePath = jsonFilePath + "Projectiles/" + name + "UIData.json";
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
			characterProperties.put(characterName, json.fromJson(CharacterProperties.class, Gdx.files.internal(jsonFilePath + characterName + "/properties.json")));		
		}
		return characterProperties.get(characterName).cloneProperties();
	}
}
