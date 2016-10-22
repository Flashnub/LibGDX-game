package com.mygdx.game.assets;

//import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;


//	public enum CharacterUIData implements EntityUIData{
//		Calder {
//			@Override
//			public Array<AnimationData> getAnimations() {
//				Array<AnimationData> animations = new Array<AnimationData>();
//				
//				animations.add(new AnimationData(CharacterState.Idle.getState(), 1, Durations.FPS20.toDuration()));
//				animations.add(new AnimationData(CharacterState.Walk.getState(), 7, Durations.FPS15.toDuration(), PlayMode.LOOP));
//				animations.add(new AnimationData(CharacterState.Backwalk.getState(), 11, Durations.FPS13.toDuration(), PlayMode.LOOP));
//				animations.add(new AnimationData(PlayerState.Block.getState(), 4, Durations.FPS20.toDuration()));
//
//				return animations;
//			}
//		},
//		BasicEnemy {
//			@Override
//			public Array<AnimationData> getAnimations() {
//				Array<AnimationData> animations = new Array<AnimationData>();
//				
//				animations.add(new AnimationData(CharacterState.Idle.getState(), 1, Durations.FPS20.toDuration()));
//				
//				return animations;
//			}
//		};
//		
//		public Array<AnimationData> getAnimations() {
//			Array<AnimationData> animations =  new Array<AnimationData>();
//			return animations;
//		}
//		
//		public TextureAtlas getMasterAtlas() {
//			return new TextureAtlas(Gdx.files.internal("Sprites/" + this.toString() + "/textures.atlas"));
//		}
//	}
//public class CharacterUIData extends EntityUIData {
//
//	@Override
//	public void write(Json json) {
//		// TODO Auto-generated method stub
//		json.writeValue("animations", animations);
//		json.writeValue("masterAtlasPath", masterAtlasPath);
//	}
//
//	@Override
//	@SuppressWarnings("unchecked")
//	public void read(Json json, JsonValue jsonData) {
//		// TODO Auto-generated method stub
//		animations = json.readValue("animations", ArrayList.class, jsonData);
//		masterAtlasPath = json.readValue("masterAtlasPath", String.class, jsonData);
//	}
//	
//}
	