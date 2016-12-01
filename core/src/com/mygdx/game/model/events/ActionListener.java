package com.mygdx.game.model.events;

import com.mygdx.game.model.actions.Attack;
import com.mygdx.game.model.actions.nonhostile.DialogueAction;
import com.mygdx.game.model.projectiles.Projectile;

public interface ActionListener {
	public void processAttack(Attack attack);
	void deleteProjectile(Projectile projectile);
	void addProjectile(Projectile projectile);
	void processProjectile(Projectile projectile);
}
