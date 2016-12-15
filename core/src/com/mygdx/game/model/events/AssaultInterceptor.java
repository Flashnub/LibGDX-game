package com.mygdx.game.model.events;

import com.mygdx.game.model.actions.Attack;
import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.projectiles.Projectile;

public interface AssaultInterceptor {
	public boolean didInterceptAttack(CharacterModel target, Attack attack);
	public boolean didInterceptProjectile(CharacterModel target, Projectile projectile);
}
