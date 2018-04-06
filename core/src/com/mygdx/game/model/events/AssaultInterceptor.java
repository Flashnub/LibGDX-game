package com.mygdx.game.model.events;

import com.mygdx.game.model.actions.Attack;
import com.mygdx.game.model.characters.EntityModel;
import com.mygdx.game.model.projectiles.Explosion;
import com.mygdx.game.model.projectiles.Projectile;

public interface AssaultInterceptor {
	public boolean didInterceptAttack(EntityModel target, Attack attack);
	public boolean didInterceptProjectile(EntityModel target, Projectile projectile);
	public boolean didInterceptExplosion(EntityModel target, Explosion explosion);
}
