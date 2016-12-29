package com.mygdx.game.model.events;

import com.mygdx.game.model.actions.Attack;
import com.mygdx.game.model.globalEffects.WorldEffect;
import com.mygdx.game.model.projectiles.Explosion;
import com.mygdx.game.model.projectiles.Projectile;

public interface ActionListener {
	public void processAttack(Attack attack);
	void deleteProjectile(Projectile projectile);
	void processProjectile(Projectile projectile);
	void processExplosion(Explosion explosion);
	void deleteExplosion(Explosion explosion);
	void spawnCharacter(String name, String type, float originX, float originY);
	void addWorldEffect(WorldEffect worldEffect);
	void addProjectile(Projectile projectile);
	void addExplosion(Explosion explosion);
}
