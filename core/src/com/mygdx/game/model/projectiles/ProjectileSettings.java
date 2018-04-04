package com.mygdx.game.model.projectiles;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.mygdx.game.assets.HitSparkUtils;
import com.mygdx.game.model.actions.AbilitySettings;
import com.mygdx.game.model.characters.EntityModel;
import com.mygdx.game.model.effects.EffectSettings;
import com.mygdx.game.model.globalEffects.WorldEffectSettings;
import com.mygdx.game.model.hitSpark.HitSparkData;
import com.badlogic.gdx.utils.JsonValue;

public class ProjectileSettings extends AbilitySettings{
	
	private Boolean disappearOnImpact;
	private String explosionName;
	private Array <WorldEffectSettings> worldEffectSettings;
	private Boolean bounces;
	private Boolean tracks; //shouldn't be used with smartAim/bounces/hasCollisionDetection.
	private Boolean useSmartAim;
	private Boolean hasCollisionDetection;
	private Array <EffectSettings> targetEffects;
	private String name;
	private Float activeSpeed;
	private Float windupSpeed;
	private Float cooldownSpeed;
	private Float gravity;
	private Vector2 size;
	private Vector2 origin;
	private Float angleOfVelocity; //use this to fire projectile in a direction rather than at a target.
	private boolean inheritAngleFromSource;
	private Float collisionWidthCoefficient;
	private Float collisionHeightCoefficient;
	private Float hitRate;
	private Boolean shouldRotate;
	private Array<Rectangle> hurtBoxProperties;
	Float windupTime;
	Float cooldownTime;
	protected Float duration;
	private HitSparkData hitSparkData;

	public ProjectileSettings deepCopy() {
		ProjectileSettings copy = new ProjectileSettings();
		copy.setFieldsWithAbilitySettings(super.deepCopy());
		copy.windupTime = this.windupTime;
		copy.duration = this.duration;
		copy.cooldownTime = this.cooldownTime;
		copy.hitRate = this.hitRate;
		copy.disappearOnImpact = this.disappearOnImpact;
		copy.bounces = this.bounces;
		copy.tracks = this.tracks;
		copy.useSmartAim = this.useSmartAim;
		copy.hasCollisionDetection = this.hasCollisionDetection;
		copy.name = this.name;
		copy.activeSpeed = this.activeSpeed;
		copy.windupSpeed = this.windupSpeed;
		copy.cooldownSpeed = this.cooldownSpeed;
		copy.gravity = this.gravity;
		copy.size = this.size;
		copy.origin = this.origin;
		copy.angleOfVelocity = this.angleOfVelocity;
		copy.explosionName = this.explosionName;
		copy.collisionWidthCoefficient = this.collisionWidthCoefficient;
		copy.collisionHeightCoefficient = this.collisionHeightCoefficient;
		copy.shouldRotate = this.shouldRotate;
		copy.inheritAngleFromSource = this.inheritAngleFromSource;
		copy.hitSparkData = this.hitSparkData;
		
		Array <WorldEffectSettings> newWorldEffects = new Array <WorldEffectSettings>();
		for (WorldEffectSettings wSettings : this.worldEffectSettings) {
			newWorldEffects.add(wSettings.deepCopy());
		}
		copy.worldEffectSettings = newWorldEffects;
		
		Array <EffectSettings> newTargetEffects = new Array <EffectSettings>();
		for(EffectSettings eSettings : targetEffects) {
			newTargetEffects.add(eSettings.deepCopy());
		}
		copy.targetEffects = newTargetEffects;
		
		return copy;
	}
	
	public ProjectileSettings() {
		
	}

	public void write(Json json) {
		super.write(json);
		json.writeValue("disappearOnImpact", disappearOnImpact);
		json.writeValue("bounces", bounces);
		json.writeValue("tracks", tracks);
		json.writeValue("hitRate", hitRate);
		json.writeValue("useSmartAim", useSmartAim);
		json.writeValue("hasCollisionDetection", hasCollisionDetection);
		json.writeValue("targetEffects", targetEffects);
		json.writeValue("name", name);
		json.writeValue("speed", activeSpeed);
		json.writeValue("gravity", gravity);
		json.writeValue("size", size);
		json.writeValue("possibleOrigins", origin);
		json.writeValue("targetEffects", targetEffects);
		json.writeValue("angleOfVelocity", angleOfVelocity);
		json.writeValue("collisionWidthCoefficient", collisionWidthCoefficient);
		json.writeValue("collisionHeightCoefficient", collisionHeightCoefficient);
		json.writeValue("worldEffectSettings", worldEffectSettings);
	}

	@SuppressWarnings("unchecked")
	public void read(Json json, JsonValue jsonData) {
		super.read(json, jsonData);
		disappearOnImpact = json.readValue("disappearOnImpact", Boolean.class, jsonData);
		bounces = json.readValue("bounces", Boolean.class, jsonData);
		tracks = json.readValue("tracks", Boolean.class, jsonData);
		hasCollisionDetection = json.readValue("hasCollisionDetection", Boolean.class, jsonData);
		useSmartAim = json.readValue("useSmartAim", Boolean.class, jsonData);
		targetEffects = json.readValue("targetEffects", Array.class, jsonData);
		name = json.readValue("name", String.class, jsonData);
		activeSpeed = json.readValue("activeSpeed", Float.class, jsonData);
		angleOfVelocity = json.readValue("angleOfVelocity", Float.class, jsonData);
		size = json.readValue("size", Vector2.class, jsonData);
		
		Float gravity = json.readValue("gravity", Float.class, jsonData);
		if (gravity != null) {
			this.gravity = gravity;
		}
		else {
			this.gravity = 0f;
		}
		
		Float windupSpeed = json.readValue("windupSpeed", Float.class, jsonData);
		if (windupSpeed != null) {
			this.windupSpeed = windupSpeed;
		}
		else {
			this.windupSpeed = activeSpeed;
		}
		
		Float cooldownSpeed = json.readValue("cooldownSpeed", Float.class, jsonData);
		if (cooldownSpeed != null) {
			this.cooldownSpeed = cooldownSpeed;
		}
		else {
			this.cooldownSpeed = activeSpeed;
		}
		
		Float widthCoefficient = json.readValue("collisionWidthCoefficient", Float.class, jsonData);
		if (widthCoefficient != null) {
			this.collisionWidthCoefficient = widthCoefficient;
		}
		else {
			this.collisionWidthCoefficient = 1f;
		}
		
		Float heightCoefficient = json.readValue("collisionHeightCoefficient", Float.class, jsonData);
		if (heightCoefficient != null) {
			this.collisionHeightCoefficient = heightCoefficient;
		}
		else {
			this.collisionHeightCoefficient = 1f;
		}
		
		Float hitRate = json.readValue("hitRate", Float.class, jsonData);
		if (hitRate != null) {
			this.hitRate = hitRate;
		}
		else {
			this.hitRate = 5f;
		}
		
		Array <WorldEffectSettings> worldEffectSettings = json.readValue("worldEffectSettings", Array.class, jsonData);
		if (worldEffectSettings != null) {
			this.worldEffectSettings = worldEffectSettings;
		}
		else {
			this.worldEffectSettings = new Array <WorldEffectSettings>();
		}
		
		Boolean shouldRotate = json.readValue("shouldRotate", Boolean.class, jsonData);
		if (shouldRotate != null) {
			this.shouldRotate = shouldRotate;
		}
		else {
			this.shouldRotate = true;
		}
		
		Float duration = json.readValue("duration", Float.class, jsonData);
		if (this.activeTillDisruption()) {
			this.duration = Float.MAX_VALUE;
		}
		else if (duration != null) {
			this.duration = duration;
		}
		else {
			this.duration = 0.5f;
		}
		Float windupTime = json.readValue("windUpTime", Float.class, jsonData);
		if (this.windupTillDisruption()) {
			this.windupTime = Float.MAX_VALUE;
		}
		else if (windupTime != null) {
			this.windupTime = windupTime;
		}
		else {
			this.windupTime = 0f;
		}
		Float cooldownTime = json.readValue("cooldownTime", Float.class, jsonData);
		if (cooldownTime != null) {
			this.cooldownTime = cooldownTime;
		}
		else {
			this.cooldownTime = 0f;
		}
		
		Boolean inheritAngleFromSource = json.readValue("inheritAngleFromSource", Boolean.class, jsonData);
		if (inheritAngleFromSource != null) {
			this.inheritAngleFromSource = inheritAngleFromSource;
		}
		else {
			this.inheritAngleFromSource = false;
		}
		
		this.hitSparkData = json.readValue("hitSparkData", HitSparkData.class, jsonData);
		if (this.hitSparkData == null) {
			this.hitSparkData = HitSparkUtils.defaultData();
		}
	}
	
	
	
	public Vector2 getOrigin() {
		return origin;
	}

	public void setOrigin(Vector2 possibleOrigin) {
		this.origin = possibleOrigin;
	}

	public void disableGravity() {
		this.gravity = 0f;
	}
	
	public boolean isDisappearOnImpact() {
		return disappearOnImpact;
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

	public Array<EffectSettings> getTargetEffects() {
		return targetEffects;
	}
	
	public String getName() {
		return name;
	}

	public Float getSpeed(String state) {
		if (state.equals(EntityModel.windupState)) {
			return this.windupSpeed;
		}
		else if (state.equals(EntityModel.activeState)) {
			return this.activeSpeed;
		}
		else if (state.equals(EntityModel.cooldownState)) {
			return this.cooldownSpeed;
		}
		return activeSpeed;
	}
	
	public boolean inheritAngleFromSource() {
		return inheritAngleFromSource;
	}

	public Float getGravity() {
		return gravity;
	}

	public Vector2 getSize() {
		return size;
	}

	public Float getAngleOfVelocity() {
		return angleOfVelocity;
	}

	public String getExplosionName() {
		return explosionName;
	}

	public Float getWidthCoefficient() {
		return collisionWidthCoefficient;
	}

	public Float getHeightCoefficient() {
		return collisionHeightCoefficient;
	}

	public Array<WorldEffectSettings> getWorldEffectSettings() {
		return worldEffectSettings;
	}

	public Boolean getShouldRotate() {
		return shouldRotate;
	}
	
	public Float getDuration() {
		return duration;
	}

	public Float getWindUpTime() {
		return windupTime;
	}

	public Float getCooldownTime() {
		return cooldownTime;
	}

	public Float getWindUpPlusDuration() {
		return this.windupTime + this.duration;
	}

	public Float getTotalTime() {
		return this.windupTime + this.duration + this.cooldownTime;
	}

	public HitSparkData getHitSparkData() {
		return this.hitSparkData;
	}
}
