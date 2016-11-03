package com.mygdx.game.model.projectiles;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.mygdx.game.model.actions.AbilitySettings;
import com.mygdx.game.model.effects.Effect;
import com.badlogic.gdx.utils.JsonValue;

public class ProjectileSettings extends AbilitySettings{
	
	private Boolean explodeOnImpact;
	private Boolean bounces;
	private Boolean tracks; //shouldn't be used with smartAim/bounces/hasCollisionDetection.
	private Boolean useSmartAim;
	private Boolean hasCollisionDetection;
	private ArrayList <Effect> targetEffects;
	private String name;
	private Float speed;
	private Float gravity;
	private Vector2 size;
	private ArrayList<Vector2> possibleOrigins;
	private Float angleOfVelocity; //use this to fire projectile in a direction rather than at a target.
	private Float projectileDuration;

	
	public ProjectileSettings() {
		
	}

	public void write(Json json) {
		json.writeValue("explodeOnImpact", explodeOnImpact);
		json.writeValue("bounces", bounces);
		json.writeValue("tracks", tracks);
		json.writeValue("useSmartAim", useSmartAim);
		json.writeValue("hasCollisionDetection", hasCollisionDetection);
		json.writeValue("targetEffects", targetEffects);
		json.writeValue("name", name);
		json.writeValue("speed", speed);
		json.writeValue("gravity", gravity);
		json.writeValue("size", size);
		json.writeValue("possibleOrigins", possibleOrigins);
		json.writeValue("projectileDuration", projectileDuration);
		json.writeValue("angleOfVelocity", angleOfVelocity);
	}

	@SuppressWarnings("unchecked")
	public void read(Json json, JsonValue jsonData) {
		super.read(json, jsonData);
		explodeOnImpact = json.readValue("explodeOnImpact", Boolean.class, jsonData);
		bounces = json.readValue("bounces", Boolean.class, jsonData);
		tracks = json.readValue("tracks", Boolean.class, jsonData);
		hasCollisionDetection = json.readValue("hasCollisionDetection", Boolean.class, jsonData);
		useSmartAim = json.readValue("useSmartAim", Boolean.class, jsonData);
		targetEffects = json.readValue("targetEffects", ArrayList.class, jsonData);
		name = json.readValue("name", String.class, jsonData);
		speed = json.readValue("speed", Float.class, jsonData);
		gravity = json.readValue("gravity", Float.class, jsonData);
		angleOfVelocity = json.readValue("angleOfVelocity", Float.class, jsonData);
		size = json.readValue("size", Vector2.class, jsonData);
		ArrayList <Vector2> possibleOrigins = json.readValue("possibleOrigins", ArrayList.class, jsonData);
		if (possibleOrigins != null) {
			this.possibleOrigins = possibleOrigins;
		}
		else {
			this.possibleOrigins = new ArrayList<Vector2>();
			this.possibleOrigins.add(new Vector2(0, 0));
		}
		Float projectileDuration = json.readValue("projectileDuration", Float.class, jsonData);
		if (projectileDuration != null) {
			this.projectileDuration = projectileDuration;
		}
		else {
			this.projectileDuration = 10f;
		}
	}
	
	public Float getProjectileDuration() {
		return projectileDuration;
	}

	public boolean isExplodeOnImpact() {
		return explodeOnImpact;
	}

	public boolean isBounces() {
		return bounces;
	}

	public boolean isTracks() {
		return tracks;
	}

	public boolean isUseSmartAim() {
		return useSmartAim;
	}

	public boolean isHasCollisionDetection() {
		return hasCollisionDetection;
	}

	public ArrayList<Effect> getTargetEffects() {
		return targetEffects;
	}
	
	public String getName() {
		return name;
	}

	public Float getSpeed() {
		return speed;
	}

	public Float getGravity() {
		return gravity;
	}

	public Vector2 getSize() {
		return size;
	}

	public ArrayList<Vector2> getPossibleOrigins() {
		return possibleOrigins;
	}

	public Float getAngleOfVelocity() {
		return angleOfVelocity;
	}
	
}
