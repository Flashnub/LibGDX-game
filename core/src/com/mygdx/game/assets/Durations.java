package com.mygdx.game.assets;

public enum Durations {
	FPS24 {
		@Override
		public float toDuration() {
			return 0.0412f;
		}
	},
		
	FPS20 {
		@Override
		public float toDuration() {
			return 0.05f;
		}
	},
	
	FPS15 {
		@Override
		public float toDuration() {
			return 0.06667f;
		}
	},
	
	FPS13 {
		@Override
		public float toDuration() {
			return 0.07692f;
		}
	};
		
	public float toDuration() {
		return 0.05f;
	}
}

