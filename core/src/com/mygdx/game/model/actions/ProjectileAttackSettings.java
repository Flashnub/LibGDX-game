package com.mygdx.game.model.actions;

import java.util.ArrayList;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.mygdx.game.constants.JSONController;
import com.mygdx.game.wrappers.StringWrapper;
import com.mygdx.game.model.projectiles.ProjectileSettings;

public class ProjectileAttackSettings implements Serializable{
	String abilitySettingKey;
	ArrayList <StringWrapper> projectileSettingKeys;
	AbilitySettings abilitySettings;
	ArrayList <ProjectileSettings> projectileSettings;
	
	public ProjectileAttackSettings() {
		
	}
	
	@Override
	public void write(Json json) {
		json.writeValue("abilitySettingKey", abilitySettingKey);
		json.writeValue("projectileSettingKeys", projectileSettingKeys);		
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void read(Json json, JsonValue jsonData) {
		abilitySettingKey = json.readValue("abilitySettingKey", String.class, jsonData);
		projectileSettingKeys = json.readValue("projectileSettingKeys", ArrayList.class, jsonData);
		abilitySettings = JSONController.abilities.get(abilitySettingKey);
		projectileSettings = new ArrayList <ProjectileSettings>();
		for (StringWrapper key : projectileSettingKeys) {
			projectileSettings.add(JSONController.projectiles.get(key.value));
		}
	}

	public AbilitySettings getAbilitySettings() {
		return abilitySettings;
	}

	public ArrayList<ProjectileSettings> getProjectileSettings() {
		return projectileSettings;
	}
	
	
}
