/*
 *
 *  * Pixel Dungeon
 *  * Copyright (C) 2012-2015 Oleg Dolya
 *  *
 *  * Shattered Pixel Dungeon
 *  * Copyright (C) 2014-2019 Evan Debenham
 *  *
 *  * Yet Another Shattered Dungeon
 *  * Copyright (C) 2014-2020 Samuel Braithwaite
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 *
 */

package com.shatteredpixel.yasd.general.items.armor;

import com.shatteredpixel.yasd.general.Badges;
import com.shatteredpixel.yasd.general.Constants;
import com.shatteredpixel.yasd.general.Dungeon;
import com.shatteredpixel.yasd.general.MainGame;
import com.shatteredpixel.yasd.general.actors.Char;
import com.shatteredpixel.yasd.general.actors.buffs.Buff;
import com.shatteredpixel.yasd.general.actors.buffs.MagicImmune;
import com.shatteredpixel.yasd.general.actors.buffs.ShieldBuff;
import com.shatteredpixel.yasd.general.actors.hero.Hero;
import com.shatteredpixel.yasd.general.effects.Speck;
import com.shatteredpixel.yasd.general.items.BrokenSeal;
import com.shatteredpixel.yasd.general.items.Item;
import com.shatteredpixel.yasd.general.items.KindofMisc;
import com.shatteredpixel.yasd.general.items.armor.curses.AntiEntropy;
import com.shatteredpixel.yasd.general.items.armor.curses.Bulk;
import com.shatteredpixel.yasd.general.items.armor.curses.Corrosion;
import com.shatteredpixel.yasd.general.items.armor.curses.Displacement;
import com.shatteredpixel.yasd.general.items.armor.curses.Metabolism;
import com.shatteredpixel.yasd.general.items.armor.curses.Multiplicity;
import com.shatteredpixel.yasd.general.items.armor.curses.Overgrowth;
import com.shatteredpixel.yasd.general.items.armor.curses.Stench;
import com.shatteredpixel.yasd.general.items.armor.glyphs.Affection;
import com.shatteredpixel.yasd.general.items.armor.glyphs.AntiMagic;
import com.shatteredpixel.yasd.general.items.armor.glyphs.Brimstone;
import com.shatteredpixel.yasd.general.items.armor.glyphs.Camouflage;
import com.shatteredpixel.yasd.general.items.armor.glyphs.Entanglement;
import com.shatteredpixel.yasd.general.items.armor.glyphs.Flow;
import com.shatteredpixel.yasd.general.items.armor.glyphs.Obfuscation;
import com.shatteredpixel.yasd.general.items.armor.glyphs.Potential;
import com.shatteredpixel.yasd.general.items.armor.glyphs.Repulsion;
import com.shatteredpixel.yasd.general.items.armor.glyphs.Stone;
import com.shatteredpixel.yasd.general.items.armor.glyphs.Swiftness;
import com.shatteredpixel.yasd.general.items.armor.glyphs.Thorns;
import com.shatteredpixel.yasd.general.items.armor.glyphs.Viscosity;
import com.shatteredpixel.yasd.general.messages.Messages;
import com.shatteredpixel.yasd.general.sprites.HeroSprite;
import com.shatteredpixel.yasd.general.sprites.ItemSprite;
import com.shatteredpixel.yasd.general.sprites.ItemSpriteSheet;
import com.shatteredpixel.yasd.general.utils.GLog;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Armor extends KindofMisc {

	public float EVA = 1f;
	public float STE = 1f;
	public float speedFactor = 1f;
	public float magicalResist = 1f;
	public float physicalResist = 1f;
	public float regenFactor = 1f;

	protected static final String AC_DETACH       = "DETACH";
	
	public enum Augment {
		EVASION (2f , -1f),
		DEFENSE (-2f, 1f),
		NONE	(0f   ,  0f);
		
		private float evasionFactor;
		private float defenceFactor;
		
		Augment(float eva, float df){
			evasionFactor = eva;
			defenceFactor = df;
		}
		
		public int evasionFactor(int level){
			return Math.round((2 + level) * evasionFactor);
		}
		
		public int defenseFactor(int level){
			return Math.round((2 + level) * defenceFactor);
		}
	}

	@Override
	public boolean canDegrade() {
		return Constants.DEGRADATION;
	}

	protected String desc = null;

	public Augment augment = Augment.NONE;
	
	public Glyph glyph;
	public boolean curseInfusionBonus = false;
	
	private BrokenSeal seal;
	
	public int tier = 1;
	public int appearance = 1;
	
	private static final int USES_TO_ID = 10;
	private int usesLeftToID = USES_TO_ID;
	private float availableUsesToID = USES_TO_ID/2f;
	
	private static final String USES_LEFT_TO_ID = "uses_left_to_id";
	private static final String AVAILABLE_USES  = "available_uses";
	private static final String GLYPH			= "glyph";
	private static final String CURSE_INFUSION_BONUS = "curse_infusion_bonus";
	private static final String SEAL            = "seal";
	private static final String AUGMENT			= "augment";
	private static final String TIER = "tier";
	private static final String STEALTH = "stealth";
	private static final String EVASION = "evasion";
	private static final String SPEED = "speed";
	private static final String DR = "dr";
	private static final String MAGICAL_DR = "magic-dr";
	private static final String PHYSICAL_DR = "phys-dr";
	private static final String IMG = "image";
	private static final String NAME = "name";
	private static final String DESC = "desc";
	private static final String APPEARANCE = "appearance";
	private static final String REGEN_FACTOR = "regen-factor";

	@Override
	public void storeInBundle(  Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( USES_LEFT_TO_ID, usesLeftToID );
		bundle.put( AVAILABLE_USES, availableUsesToID );
		bundle.put( GLYPH, glyph );
		bundle.put( CURSE_INFUSION_BONUS, curseInfusionBonus );
		bundle.put( SEAL, seal);
		bundle.put( AUGMENT, augment);
		bundle.put( TIER, tier );
		bundle.put( STEALTH, STE );
		bundle.put( EVASION, EVA );
		bundle.put( SPEED, speedFactor );
		bundle.put( MAGICAL_DR, magicalResist);
		bundle.put( PHYSICAL_DR, physicalResist);
		bundle.put(NAME, name);
		bundle.put(IMG, image);
		bundle.put(DESC, desc);
		bundle.put(APPEARANCE, appearance);
		bundle.put(REGEN_FACTOR, regenFactor);
	}

	@Override
	public void restoreFromBundle(  Bundle bundle ) {
		super.restoreFromBundle(bundle);
		usesLeftToID = bundle.getInt( USES_LEFT_TO_ID );
		availableUsesToID = bundle.getInt( AVAILABLE_USES );
		inscribe((Glyph) bundle.get(GLYPH));
		curseInfusionBonus = bundle.getBoolean( CURSE_INFUSION_BONUS );
		seal = (BrokenSeal)bundle.get(SEAL);
		
		//pre-0.7.2 saves
		if (bundle.contains( "unfamiliarity" )){
			usesLeftToID = bundle.getInt( "unfamiliarity" );
			availableUsesToID = USES_TO_ID/2f;
		}
		
		augment = bundle.getEnum(AUGMENT, Augment.class);

		if (Dungeon.version >= MainGame.v0_2_0) {//Support older saves
			tier = bundle.getInt(TIER);
		}
		if (Dungeon.version > MainGame.v0_3_6) {
			STE = bundle.getFloat(STEALTH);
			EVA = bundle.getFloat(EVASION);
			speedFactor = bundle.getFloat(SPEED);
			magicalResist = bundle.getFloat(MAGICAL_DR);
			physicalResist = bundle.getFloat(PHYSICAL_DR);
			desc = bundle.getString(DESC);
			name = bundle.getString(NAME);
			image = bundle.getInt(IMG);
			appearance = bundle.getInt(APPEARANCE);
			regenFactor = bundle.getFloat(REGEN_FACTOR);
		} else {
			desc = super.desc();
			name = Messages.get(this, "name");
		}
		//Check the correct profile is still applied only if it's the main armour class not a subclass.
		if (getClass() == Armor.class) {
			matchProfile();
		}
	}

	private static float randomStat() {
		int num = Random.Int(5, 20);
		return num/10f;
	}

	private void resetStats() {
		EVA = 1f;
		STE = 1f;
		speedFactor = 1f;
		magicalResist = 1f;
		physicalResist = 1f;
		regenFactor = 1f;
	}

	@Override
	public String desc() {
		return desc;
	}

	public Armor initStats() {
		resetStats();
		if (Random.Int(3) == 0) {
			EVA = randomStat();
		}
		if (Random.Int(3) == 0) {
			STE = randomStat();
		}
		if (Random.Int(3) == 0) {
			speedFactor = randomStat();
		}
		if (Random.Int(3) == 0) {
			regenFactor = randomStat();
		}
		physicalResist = (Random.Float() + 1f)/2f;
		magicalResist = 2f - physicalResist;
		return matchProfile();
	}

	public float getDefenseFactor() {
		float DRfactor = 1f;
		DRfactor *= 1/EVA;
		DRfactor *= 1/STE;
		DRfactor *= 1/speedFactor;
		DRfactor *= 1/regenFactor;
		return DRfactor;
	}

	@Contract(" -> this")
	public Armor matchProfile() {
		//Weapons that are only very slightly different from the basic weapon get it's image and description.
		float closestMatch = 1.1f;
		ArmorProfile closestMatchProfile = ArmorProfile.NONE;
		//Shuffle list first in case two are tied for first place, to give all an equal chance. Randomness is fine as the image variable is stored in bundles, so it won't change for an individual weapon.
		ArrayList<ArmorProfile> profiles = new ArrayList<>(Arrays.asList(ArmorProfile.values()));
		Collections.shuffle(profiles);
		for (ArmorProfile profile : profiles) {
			float importance = profile.match(this);
			if (importance > closestMatch) {
				closestMatch = importance;
				closestMatchProfile = profile;
			}
		}
		closestMatchProfile.copy(this);
		return this;
	}

	@Override
	public void reset() {
		super.reset();
		usesLeftToID = USES_TO_ID;
		availableUsesToID = USES_TO_ID/2f;
		//armours can be kept in bones between runs, the seal cannot.
		seal = null;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (seal != null) actions.add(AC_DETACH);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {

		super.execute(hero, action);

		if (action.equals(AC_DETACH) && seal != null){
			BrokenSeal.WarriorShield sealBuff = hero.buff(BrokenSeal.WarriorShield.class);
			if (sealBuff != null) sealBuff.setArmor(null);

			GLog.i( Messages.get(Armor.class, "detach_seal") );
			hero.sprite.operate(hero.pos);
			if (!seal.collect()){
				Dungeon.level.drop(seal, hero.pos);
			}
			seal = null;
		}
	}


	@Override
	public void activate(Char ch) {
		super.activate(ch);
		if (seal != null) Buff.affect(ch, BrokenSeal.WarriorShield.class).setArmor(this);
	}

	public void affixSeal(BrokenSeal seal){
		this.seal = seal;
		if (isEquipped(Dungeon.hero)){
			Buff.affect(Dungeon.hero, BrokenSeal.WarriorShield.class).setArmor(this);
		}
	}


	public int appearance() {
		return appearance;
	}

	public BrokenSeal checkSeal(){
		return seal;
	}

	@Override
	protected float time2equip( Char hero ) {
		return 2 / hero.speed();
	}

	public int defense() {
		return defense(level());
	}

	public int defense(int lvl) {
		return Math.round(((tier * 4) + (tier * lvl * 2)) * getDefenseFactor());
	}

	public float defenseRegen() {
		return 0.2f*tier*regenFactor;
	}

	@Override
	public boolean doEquip(Hero hero) {
		boolean equipped = super.doEquip(hero);
		((HeroSprite) hero.sprite).updateArmor();
		return equipped;
	}

	@Override
	public boolean doUnequip(Char ch, boolean collect, boolean single) {
		boolean equipped = super.doUnequip(ch, collect, single);
		if (ch instanceof Hero) {
			((HeroSprite)ch.sprite).updateArmor();
		}
		return equipped;
	}

	@Override
	public int level() {
		int lvl = super.level();
		if (curseInfusionBonus) {
			lvl += Constants.CURSE_INFUSION_BONUS_AMT;
		}
		if (seal != null) {
			lvl += seal.level();
		}
		lvl = Math.min(upgradeLimit(), lvl);
		return lvl;
	}
	
	@Override
	public Item upgrade() {
		return upgrade( false );
	}
	
	public Item upgrade( boolean inscribe ) {

		if (inscribe && (glyph == null || glyph.curse())){
			inscribe( Glyph.random() );
		} else if (!inscribe && level() >= 4 && Random.Float(10) < Math.pow(2, level()-4)){
			inscribe(null);
		}
		
		cursed = false;

		if (seal != null && seal.level() == 0) {
			seal.upgrade();
			return this;
		}

		return super.upgrade();
	}
	
	public int proc( Char attacker, Char defender, int damage ) {

		if (glyph != null && defender.buff(MagicImmune.class) == null) {
			damage = glyph.proc( this, attacker, defender, damage );
		}

		if (!levelKnown && defender == Dungeon.hero && availableUsesToID >= 1) {
			availableUsesToID--;
			usesLeftToID--;
			if (usesLeftToID <= 0) {
				identify();
				GLog.p( Messages.get(Armor.class, "identify") );
				Badges.validateItemLevelAquired( this );
			}
		}

		return damage;
	}

	public int magicalProc( Char attacker, Char defender, int damage ) {

		if (glyph != null && defender.buff(MagicImmune.class) == null) {
			damage = glyph.magicalProc( this, attacker, defender, damage );
		}

		if (!levelKnown && defender == Dungeon.hero && availableUsesToID >= 1) {
			availableUsesToID--;
			usesLeftToID--;
			if (usesLeftToID <= 0) {
				identify();
				GLog.p( Messages.get(Armor.class, "identify") );
				Badges.validateItemLevelAquired( this );
			}
		}

		return damage;
	}
	
	@Override
	public void onHeroGainExp(float levelPercent, Hero hero) {
		if (!levelKnown && isEquipped(hero) && availableUsesToID <= USES_TO_ID/2f) {
			//gains enough uses to ID over 0.5 levels
			availableUsesToID = Math.min(USES_TO_ID/2f, availableUsesToID + levelPercent * USES_TO_ID);
		}
	}
	
	@Override
	public String name() {
		return getName() == null ? Glyph.getName(this, glyph, cursedKnown) : getName();
	}

	@Override
	public void curse() {
		super.curse();
		inscribe(Glyph.randomCurse());
	}

	@Override
	public void uncurse() {
		if (hasCurseGlyph()) {
			inscribe(null);
		}
	}

	@Override
	public String info() {
		String info = desc();
		
		if (levelKnown) {
			info += "\n\n" + Messages.get(Armor.class, "curr_absorb", tier, defense(), new DecimalFormat("#.##").format(defenseRegen()), STRReq());
			
			if (STRReq() > Dungeon.hero.STR()) {
				info += " " + Messages.get(Armor.class, "too_heavy");
			}
		} else {
			info += "\n\n" + Messages.get(Armor.class, "avg_absorb", tier, defense(0), new DecimalFormat("#.##").format(defenseRegen()), STRReq(0));

			if (STRReq(0) > Dungeon.hero.STR()) {
				info += " " + Messages.get(Armor.class, "probably_too_heavy");
			}
		}

		switch (augment) {
			case EVASION:
				info += "\n\n" + Messages.get(Armor.class, "evasion");
				break;
			case DEFENSE:
				info += "\n\n" + Messages.get(Armor.class, "defense");
				break;
			case NONE:
		}

		if (EVA != 1f || STE != 1f || speedFactor != 1f || magicalResist != 1f || physicalResist != 1f) {

			info += "\n";

			if (magicalResist > 1f) {
				info += "\n" + Messages.get(Armor.class, "magical_weak", Math.round((magicalResist-1f)*100));
			} else if (magicalResist < 1f) {
				info += "\n" + Messages.get(Armor.class, "magical_resist", Math.round((1f-magicalResist)*100));
			}

			if (physicalResist > 1f) {
				info += "\n" + Messages.get(Armor.class, "physical_weak", Math.round((physicalResist-1f)*100));
			} else if (physicalResist < 1f) {
				info += "\n" + Messages.get(Armor.class, "physical_resist", Math.round((1f-physicalResist)*100));
			}

			if (EVA > 1f) {
				info += "\n" + Messages.get(Armor.class, "eva_increase", Math.round((EVA-1f)*100));
			} else if (EVA < 1f) {
				info += "\n" + Messages.get(Armor.class, "eva_decrease", Math.round((1f-EVA)*100));
			}

			if (STE > 1f) {
				info += "\n" + Messages.get(Armor.class, "ste_increase", Math.round((STE-1f)*100));
			} else if (STE < 1f) {
				info += "\n" + Messages.get(Armor.class, "ste_decrease", Math.round((1f-STE)*100));
			}

			if (speedFactor > 1f) {
				info += "\n" + Messages.get(Armor.class, "speed_increase", Math.round((speedFactor-1f)*100));
			} else if (speedFactor < 1f) {
				info += "\n" + Messages.get(Armor.class, "speed_decrease", Math.round((1f-speedFactor)*100));
			}
		}
		
		if (glyph != null  && (cursedKnown || !glyph.curse())) {
			info += "\n\n" +  Messages.get(Armor.class, "inscribed", glyph.name());
			info += " " + glyph.desc();
		}
		
		if (cursed && isEquipped( Dungeon.hero )) {
			info += "\n\n" + Messages.get(Armor.class, "cursed_worn");
		} else if (cursedKnown && cursed) {
			info += "\n\n" + Messages.get(Armor.class, "cursed");
		} else if (seal != null) {
			info += "\n\n" + Messages.get(Armor.class, "seal_attached");
		} else if (!isIdentified() && cursedKnown){
			info += "\n\n" + Messages.get(Armor.class, "not_cursed");
		}
		
		return info;
	}

	@Override
	public Emitter emitter() {
		Emitter emitter = super.emitter();
		if (seal == null) return emitter;
		emitter.pos(ItemSpriteSheet.film.width(image)/2f + 2f, ItemSpriteSheet.film.height(image)/3f);
		emitter.fillTarget = false;
		emitter.pour(Speck.factory( Speck.RED_LIGHT ), 0.6f);
		return emitter;
	}

	@Override
	public Item random() {
		//+0: 75% (3/4)
		//+1: 20% (4/20)
		//+2: 5%  (1/20)
		int n = 0;
		if (Random.Int(4) == 0) {
			n++;
			if (Random.Int(5) == 0) {
				n++;
			}
		}
		level(n);
		
		//30% chance to be cursed
		//15% chance to be inscribed
		float effectRoll = Random.Float();
		if (effectRoll < 0.3f) {
			inscribe(Glyph.randomCurse());
			cursed = true;
		} else if (effectRoll >= 0.85f){
			inscribe();
		}

		initStats();

		return this;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int defaultSTRReq() {
		return Math.max(STRReq(level()),10);
	}

	public int STRReq() {
		if (isEquipped(Dungeon.hero)) {
			return Dungeon.hero.belongings.getArmorSTRReq();
		} else {
			return defaultSTRReq();
		}
	}


	public int STRReq(int lvl){
		lvl = Math.max(0, lvl);

		//strength req decreases at +1,+3,+6,+10,etc.
		return (7 + Math.round(tier * 3)) - lvl;
	}
	
	@Override
	public int price() {
		if (seal != null) return 0;

		int price = 20 * tier;
		if (hasGoodGlyph()) {
			price *= 1.5;
		}
		if (cursedKnown && (cursed || hasCurseGlyph())) {
			price /= 2;
		}
		if (levelKnown && level() > 0) {
			price *= (level() + 1);
		}
		if (price < 1) {
			price = 1;
		}
		return price;
	}

	public Armor inscribe( Glyph glyph ) {
		if (glyph == null || !glyph.curse()) curseInfusionBonus = false;
		this.glyph = glyph;
		updateQuickslot();
		return this;
	}

	public Armor inscribe() {

		Class<? extends Glyph> oldGlyphClass = glyph != null ? glyph.getClass() : null;
		Glyph gl = Glyph.random( oldGlyphClass );

		return inscribe( gl );
	}

	public boolean hasGlyph(Class<?extends Glyph> type, Char owner) {
		return glyph != null && glyph.getClass() == type && owner.buff(MagicImmune.class) == null;
	}

	//these are not used to process specific glyph effects, so magic immune doesn't affect them
	public boolean hasGoodGlyph(){
		return glyph != null && !glyph.curse();
	}

	public boolean hasCurseGlyph(){
		return glyph != null && glyph.curse();
	}
	
	@Override
	public ItemSprite.Glowing glowing() {
		return glyph != null && cursedKnown ? glyph.glowing() : null;
	}
	
	public static abstract class Glyph implements Bundlable {

		public static String getName(Armor armor, Glyph gly, boolean showGlyph) {
			String name = armor.name;
			if (gly != null && showGlyph) {
				name = gly.name(name);
			}
			return name;
		}
		
		private static final Class<?>[] common = new Class<?>[]{
				Obfuscation.class, Swiftness.class, Viscosity.class, Potential.class };
		
		private static final Class<?>[] uncommon = new Class<?>[]{
				Brimstone.class, Stone.class, Entanglement.class,
				Repulsion.class, Camouflage.class, Flow.class };
		
		private static final Class<?>[] rare = new Class<?>[]{
				Affection.class, AntiMagic.class, Thorns.class };
		
		private static final float[] typeChances = new float[]{
				50, //12.5% each
				40, //6.67% each
				10  //3.33% each
		};

		private static final Class<?>[] curses = new Class<?>[]{
				AntiEntropy.class, Corrosion.class, Displacement.class, Metabolism.class,
				Multiplicity.class, Stench.class, Overgrowth.class, Bulk.class
		};
		
		public abstract int proc( Armor armor, Char attacker, Char defender, int damage );

		public int magicalProc( Armor armor, Char attacker, Char defender, int damage ) {
			return damage;
		}
		
		public String name() {
			if (!curse())
				return name( Messages.get(this, "glyph") );
			else
				return name( Messages.get(Item.class, "curse"));
		}
		
		public String name( String armorName ) {
			return Messages.get(this, "name", armorName);
		}

		public String desc() {
			return Messages.get(this, "desc");
		}

		public boolean curse() {
			return false;
		}
		
		@Override
		public void restoreFromBundle( Bundle bundle ) {
		}

		@Override
		public void storeInBundle( Bundle bundle ) {
		}
		
		public abstract ItemSprite.Glowing glowing();

		@SuppressWarnings("unchecked")
		public static Glyph random( Class<? extends Glyph> ... toIgnore ) {
			switch(Random.chances(typeChances)){
				case 0: default:
					return randomCommon( toIgnore );
				case 1:
					return randomUncommon( toIgnore );
				case 2:
					return randomRare( toIgnore );
			}
		}
		
		@SuppressWarnings("unchecked")
		public static Glyph randomCommon( Class<? extends Glyph> ... toIgnore ){
			ArrayList<Class<?>> glyphs = new ArrayList<>(Arrays.asList(common));
			glyphs.removeAll(Arrays.asList(toIgnore));
			if (glyphs.isEmpty()) {
				return random();
			} else {
				return (Glyph) Reflection.newInstance(Random.element(glyphs));
			}
		}
		
		@SuppressWarnings("unchecked")
		public static Glyph randomUncommon( Class<? extends Glyph> ... toIgnore ){
			ArrayList<Class<?>> glyphs = new ArrayList<>(Arrays.asList(uncommon));
			glyphs.removeAll(Arrays.asList(toIgnore));
			if (glyphs.isEmpty()) {
				return random();
			} else {
				return (Glyph) Reflection.newInstance(Random.element(glyphs));
			}
		}
		
		@SuppressWarnings("unchecked")
		public static Glyph randomRare( Class<? extends Glyph> ... toIgnore ){
			ArrayList<Class<?>> glyphs = new ArrayList<>(Arrays.asList(rare));
			glyphs.removeAll(Arrays.asList(toIgnore));
			if (glyphs.isEmpty()) {
				return random();
			} else {
				return (Glyph) Reflection.newInstance(Random.element(glyphs));
			}
		}
		
		@SuppressWarnings("unchecked")
		public static Glyph randomCurse( Class<? extends Glyph> ... toIgnore ){
			ArrayList<Class<?>> glyphs = new ArrayList<>(Arrays.asList(curses));
			glyphs.removeAll(Arrays.asList(toIgnore));
			if (glyphs.isEmpty()) {
				return random();
			} else {
				return (Glyph) Reflection.newInstance(Random.element(glyphs));
			}
		}
		
	}
	public Armor setTier(int tier) {
		this.tier = tier;
		updateTier();
		return this;
	}

	public Armor upgradeTier(int tier) {
		this.tier += tier;
		updateTier();
		return this;
	}

	public Armor degradeTier(int tier) {
		this.tier -= tier;
		updateTier();
		return this;
	}

	public void updateTier() {

	}

	public static class Defense extends ShieldBuff {

		private float magicResist() {
			return target.magicalResist();
		}

		private float physicalResist() {
			return target.physicalResist();
		}

		@Override
		protected int shieldCap() {
			return target == null ? -1 : target.defense();
		}

		private float regenPerTurn() {
			return target.defenseRegen();
		}

		public void setToMax(Char ch) {
			setShield(ch.defense());
		}

		public static int curShield(Char ch) {
			Defense defense = ch.buff(Defense.class);
			if (defense != null) {
				return defense.shielding();
			} else {
				return 0;
			}
		}

		private float partialRegen = 0f;

		private static final String PARTIAL_REGEN = "partial_regen";

		@Override
		public boolean act() {
			//Round regen to a whole number and add it
			int roundedRegen = (int) regenPerTurn();
			if (roundedRegen > 0) {
				incShield(roundedRegen);
			}

			//If regen isn't a whole number, add the rest to a partial regen which builds up to +1 shield.
			partialRegen += regenPerTurn() - roundedRegen;
			if (partialRegen > 1f) {
				//Decrease instead of set to 0 as it may overflow above 1.
				partialRegen--;
				incShield();
			}

			spend(TICK);

			return true;
		}

		@Override
		public int absorbDamage(int dmg, @NotNull Char.DamageSrc src) {
			//Armour degrades when the shield absorbs damage
			if (target.hasBelongings() && target.belongings.getArmors().size() > 0) target.belongings.getArmors().get(0).use();

			if (src.ignores()) {
				return dmg;
			}

			if (src.getElement().isMagical() && shielding() > 0) {
				dmg *= magicResist();
			} else {
				dmg *= physicalResist();
			}
			return super.absorbDamage(dmg, src);
		}

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(PARTIAL_REGEN, partialRegen);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			partialRegen = bundle.getFloat(PARTIAL_REGEN);
		}

		//Don't detach at zero - this buff regenerates.
		@Override
		protected void onZeroShield() {}
	}
}
