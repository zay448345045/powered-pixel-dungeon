/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2019 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.yasd.general.actors.mobs;

import com.shatteredpixel.yasd.general.Constants;
import com.shatteredpixel.yasd.general.Dungeon;
import com.shatteredpixel.yasd.general.actors.Char;
import com.shatteredpixel.yasd.general.actors.hero.Belongings;
import com.shatteredpixel.yasd.general.effects.MagicMissile;
import com.shatteredpixel.yasd.general.items.Ankh;
import com.shatteredpixel.yasd.general.items.Generator;
import com.shatteredpixel.yasd.general.items.KindofMisc;
import com.shatteredpixel.yasd.general.items.armor.Armor;
import com.shatteredpixel.yasd.general.items.rings.Ring;
import com.shatteredpixel.yasd.general.items.stones.StoneOfRepair;
import com.shatteredpixel.yasd.general.items.wands.Wand;
import com.shatteredpixel.yasd.general.items.wands.WandOfWarding;
import com.shatteredpixel.yasd.general.items.weapon.Weapon.Enchantment;
import com.shatteredpixel.yasd.general.items.weapon.enchantments.Grim;
import com.shatteredpixel.yasd.general.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.yasd.general.journal.Notes;
import com.shatteredpixel.yasd.general.mechanics.Ballistica;
import com.shatteredpixel.yasd.general.sprites.StatueSprite;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Statue extends Mob implements Callback {
	
	{
		spriteClass = StatueSprite.class;

		EXP = 0;
		state = PASSIVE;
		
		properties.add(Property.INORGANIC);

		STR = Integer.MAX_VALUE;

		loot = new  StoneOfRepair();

		lootChance = 1f;//Guaranteed in Animated Statues
	}

	int ankhs = Math.max(1,Dungeon.depth/Constants.CHAPTER_LENGTH);//1 Ankh per chapter
	
	public Statue() {
		super();
		belongings = new  Belongings(this);

		for (int i = 0; i < belongings.miscs.length; i++) {
			belongings.miscs[i] = newItem();
			belongings.miscs[i].activate(this);
		}

		upgradeItems();
		
		HP = HT = 15 + Dungeon.depth * 5;
		defenseSkill = 4 + Dungeon.depth;
		attackSkill  = 10 + Dungeon.depth;
	}

	@Override
	public boolean canAttack(Char enemy) {
		if (Dungeon.level.adjacent( pos, enemy.pos )) {
			return super.canAttack( enemy );
		} else if (wandToAttack(enemy) != null) {
			return new  Ballistica( pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
		} else {
			return false;
		}
	}

	public KindofMisc newItem() {
		boolean con = false;
		KindofMisc item;
		do {
			int type = Random.Int(4);
			switch (type) {
				default:
					item = ((MeleeWeapon) Generator.random(Generator.Category.WEAPON));
					if (((MeleeWeapon) item).hasCurseEnchant()) {
						((MeleeWeapon) item).enchant(Enchantment.random());
					}
					if (belongings.getWeapons().size() < 3) {
						con = true;
					}
					break;
				case 1:
					item = ((KindofMisc) Generator.random(Generator.Category.RING));
					if (belongings.getEquippedItemsOFType(Ring.class).size() < 3) {
						con = true;
					}
					break;
				case 2:
					item = ((Armor) Generator.random(Generator.Category.ARMOR));
					if (((Armor) item).hasCurseGlyph()) {
						((Armor) item).inscribe(Armor.Glyph.random());
					}
					if (belongings.getEquippedItemsOFType(Armor.class).size() < 3) {
						con = true;
					}
					break;
				case 3:
					item = ((KindofMisc) Generator.random(Generator.Category.WAND));
					if (belongings.getEquippedItemsOFType(Wand.class).size() < 3) {
						con = true;
					}
					break;
			}
		} while (!con);

		item.level(0);
		item.cursed = false;
		item.identify();
		return item;
	}

	public void upgradeItems() {
		int sous = (Dungeon.depth/Constants.CHAPTER_LENGTH)*Constants.SOU_PER_CHAPTER;//(Dungeon.depth/5 [chapter]) * 3 [3 SoU per chapter]
		KindofMisc Item;
		if (belongings.miscs.length > 0) {
			do {
				do {
					Item = Random.element(belongings.miscs);
				} while (Item == null || !Item.isUpgradable());//If the item is not upgradeable (An artifact or +3) chose another. Also, if it is null (nothing equipped in that slot)
				Item.upgrade();
				sous--;
			} while (sous > 0);
		}
	}

	public void wandZap() {
		if (enemy != null) {
			Wand wand = wandToAttack(enemy);
			wand.activate(this);
			if (wand instanceof WandOfWarding) {//Wand of Warding cannot zap directly
				int closest = findClosest(this, enemy, this.pos);

				if (closest == -1){
					sprite.centerEmitter().burst(MagicMissile.WardParticle.FACTORY, 8);
					return; //do not spawn guardian or detach buff
				} else {
					wand.zap(closest);
				}
			} else {
				wand.zap(enemy.pos);
			}

		}
		spend(1f);
		next();
	}

	protected Wand wandToAttack(Char enemy ) {
		if (enemy != null ) {
			ArrayList<KindofMisc> Wands = belongings.getEquippedItemsOFType(Wand.class);
			ArrayList<Wand> UsableWands = new ArrayList<>();
			for (int i = 0; i < Wands.size(); i++) {
				Wand Wand = ((Wand) Wands.get(i));
				if (Wand.curCharges > 0) {
					UsableWands.add(Wand);
				}
			}
			if (UsableWands.size() > 0) {
				return Random.element(UsableWands);
			}
		}
		return null;

	}
	
	@Override
	protected boolean act() {
		if (Dungeon.level.heroFOV[pos]) {
			Notes.add( Notes.Landmark.STATUE );
		}
		return super.act();
	}
	
	@Override
	public void damage( int dmg, Object src ) {

		if (state == PASSIVE) {
			state = HUNTING;
			return;
		}
		if (dmg > HP & ankhs > 0) {
			Ankh.revive(this, null);
			ankhs--;
		}
		super.damage( dmg, src );
	}

	protected void zap(Char enemy) {
		if (enemy != null ) {
			Wand WandToZap = wandToAttack(enemy);
			if (WandToZap != null) {
				WandToZap.zap(enemy.pos);
			}
		}
	}

	protected boolean doAttack( Char enemy ) {
		if (Dungeon.level.adjacent( pos, enemy.pos )) {
			return super.doAttack( enemy );
		} else if (belongings.getEquippedItemsOFType(Wand.class).size() > 0) {
			return doMagicAttack( enemy );
		} else {
			return false;
		}
	}

	@Override
	public void beckon( int cell ) {
		// Do nothing
	}

	public void dropGear() {
		for (int i=0; i < belongings.miscs.length; i++) {
			if (belongings.miscs[i] != null) {
				Dungeon.level.drop(belongings.miscs[i].identify(), pos).sprite.drop();
			}
		}
	}

	@Override
	public void die( Object cause ) {
		dropGear();
		super.die( cause );
	}
	
	@Override
	public void destroy() {
		Notes.remove( Notes.Landmark.STATUE );
		super.destroy();
	}
	
	@Override
	public boolean reset() {
		state = PASSIVE;
		return true;
	}

	@Override
	public String description() {
		String description = super.description() + "_";
		for (int i=0; i < belongings.miscs.length; i++) {
			if (belongings.miscs[i] != null) {
				description += (belongings.miscs[i].name()) + "_ \n\n_";
			}
		}
		return description + "_";
	}
	
	{
		resistances.add(Grim.class);
	}

	@Override
	public void call() {
		next();
	}
}