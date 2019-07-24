package yugiohji;

/**
 * This class creates YuGiOhJi-monsters.
 * They basically have all kinds of attributes
 * which become relevant, when they have been summoned on the field,
 * i.e. when they become summoned Monsters.
 * (Ses the summonedMonster class)
 * 
 * The summoned monsters earn a few more temporal attributes.
 * (like in what mode they are, if face down or existing at all, or if their effects are negated)
 * 
 * Here are most effects of the monsters the player can use.
 * Some passive effects are in the SummonedMonster class.
 * Some interactions during battle and the opponent's turn
 * can be found in the SummonedMonster class and AIdelegate class.
 * 
 */

import static yugiohji.YuGiOhJi.Card;
import static yugiohji.YuGiOhJi.Game;
import static yugiohji.YuGiOhJi.HandPlayer;
import static yugiohji.YuGiOhJi.Mon;
import static yugiohji.AIinterrupts.cpuIsUsingEffectNegate;
        
public class YMonster {
    
    public static int effectiveMonsterId;
    
    // When Source Code is completely written:
    // check: do we really need all these properties?
    
    // instance variables (different for every monster)
    public int monsterId; // ID of the monster for identifying everything about it
    public String monsterName; // name of the monster (just for readability) use ID for everything
    
    public int stars; // an integer from 1 to 4
    public int att; // the attack value that is printed on the card
    public int def; // the defense value that is printed on the card
    
    public boolean hasPassEffects; // if true, than has at least one of the following passive effects
    public boolean hasSuicidalEffects; // if true, does something, when destroyed
    
    public boolean isIndestructibleByBattle; // if true, then survives the battle
    public boolean isIndestructibleByEffect; // if true, then can not be chosen for destruction effect
    public boolean isImmuneWhileWinning; // if true, then can not be chosen by an effect while killing an monster
    public boolean isImmuneToAllEffects; // if true, then can never be chosen by an effect
    public boolean isPiercing; // if true, then has piercing attack
    public boolean isGettingStronger; // if true, then gets stronger after every victory
    public boolean isGettingIntoDef; // if true, turns into defense mode after attack
    public boolean isGettingItselfToHand; // if true, then egts to hand at the end of the turn
    public boolean isGettingAnotherToHand; // if true, then can get a card of the field to the hand when destroyed in battle
    public boolean isBurning; // if true, inflicts burn damage when destroyed
    public boolean isDestroying; // if true, destroys other card when destroyed in battle
    public boolean isBanishingWhenDestroyed; // if true, banishes both cards when destroyed in battle
    public boolean isBanishingWhenDestroying; // if true, banishes when destroying others
    public boolean isCheatChanging; // if true, has the sacrifices Demons to cheat change effect
    
    // // public boolean isNotEquippable; // if true, can not be equipped (redundant, already cntained in isImmuneToAllEffects)
    public boolean hasEquipEffect; // if true, can equip other monsters
    
    public boolean hasOptionalEffectOnField; // if true, can use an effect when summoned (look up effect via ID)
    public boolean hasAttackNegateOnField; // if true, one can negate an attack while on field
    public boolean hasEffectNegateOnField; // if true, one can negate an effect while on field
    
    public int specialNumber; // an integer for monster specific effects (equals the attack boost for Sword and Steep Learning Curve, the def. boost for Shield, and the burn damage for Napalm) [for other monsters is just zero and irrelevant]
    
    public String copyableEffect; // card text of the copyable part of the effects
    
    public String cardPathAtt; // path to the image for attack mode as string
    public String cardPathDef; // path to the image for defence mode as string
    
    // constructor 
    public YMonster (int monsterId, String monsterName, int stars, int att, int def, boolean hasPassEffects, boolean hasSuicidalEffects, boolean isIndestructibleByBattle, boolean isIndestructibleByEffect, boolean isImmuneWhileWinning, boolean isImmuneToAllEffects, boolean isPiercing, boolean isGettingStronger, boolean isGettingIntoDef, boolean isGettingItselfToHand, boolean isGettingAnotherToHand, boolean isBurning, boolean isDestroying, boolean isBanishingWhenDestroyed, boolean isBanishingWhenDestroying, boolean isCheatChanging, boolean hasEquipEffect, boolean hasOptionalEffectOnField, boolean hasAttackNegateOnField, boolean hasEffectNegateOnField, int specialNumber, String copyableEffect, String cardPathAtt, String cardPathDef)
    {
        // set all the entered values
        this.monsterId = monsterId;
        this.monsterName = monsterName;
        
        this.stars = stars;
        this.att = att;
        this.def = def;
    
        this.hasPassEffects = hasPassEffects;
        this.hasSuicidalEffects = hasSuicidalEffects;
        
        this.isIndestructibleByBattle = isIndestructibleByBattle;
        this.isIndestructibleByEffect = isIndestructibleByEffect;
        this.isImmuneWhileWinning = isImmuneWhileWinning;
        this.isImmuneToAllEffects = isImmuneToAllEffects;
        this.isPiercing = isPiercing;
        this.isGettingStronger = isGettingStronger;
        this.isGettingIntoDef = isGettingIntoDef;
        this.isGettingItselfToHand = isGettingItselfToHand;
        this.isGettingAnotherToHand = isGettingAnotherToHand;
        this.isBurning = isBurning;
        this.isDestroying = isDestroying;
        this.isBanishingWhenDestroyed = isBanishingWhenDestroyed;
        this.isBanishingWhenDestroying = isBanishingWhenDestroying;
        this.isCheatChanging = isCheatChanging;
        
        // // this.isNotEquippable = isNotEquippable; (redundant, already cntained in isImmuneToAllEffects)
        this.hasEquipEffect = hasEquipEffect;
        
        this.hasOptionalEffectOnField = hasOptionalEffectOnField;
        this.hasAttackNegateOnField = hasAttackNegateOnField;
        this.hasEffectNegateOnField = hasEffectNegateOnField;
        
        this.specialNumber = specialNumber;
        
        this.copyableEffect = copyableEffect;
                
        this.cardPathAtt = cardPathAtt;
        this.cardPathDef = cardPathDef;
        
    }
    
    // I consider two monsters equal, if they have the same monster ID.
    // (for more readable code, because card names used, and more efficiency, because no string comparison)
    public boolean equals (YMonster Monster) {
        return (monsterId==Monster.monsterId);
    }
    
    // returns the attack boost of Steep Learning Curve
    // (source code looks best, if called on the correct monster, however it actually doesn't matter at all)
    public int SteepLearningCurveAttBoost() {
        return Mon.SteepLearningCurve.specialNumber;
    }
    
    // returns the attack boost of Sword
    // (source code looks best, if called on the correct monster, however it actually doesn't matter at all)
    public int SwordAttBoost() {
        return Mon.Sword.specialNumber;
    }
    
    // returns the defence boost of Shield
    // (source code looks best, if called on the correct monster, however it actually doesn't matter at all)
    public int ShieldDefBoost() {
        return Mon.Shield.specialNumber;
    }
    
    // returns the defence boost of Shield
    // (source code looks best, if called on the correct monster, however it actually doesn't matter at all)
    public int NapalmBurnDamage() {
        return Mon.Napalm.specialNumber;
    }
    
    // returns true, if a given monster has a passive effect that can remove other cards
    // here actually only the original monsters are considered (not a copy if the corresponding effect)
    public boolean hasPassiveRemovalEffect() {
        return (equals(Mon.Banisher) || equals(Mon.BackBouncer) || equals(Mon.SuicideCommando));
    }
    
    // returns true, if a given monster has an optional effect that can remove other cards
    // here actually only the original monsters are considered (not a copy if the corresponding effect)
    public boolean hasOptionalRemovalEffect() {
        return (equals(Mon.Eradicator) || equals(Mon.BigBackBouncer) || equals(Mon.MonsterStealer));
    }
    
    // --- methods for optional effects of all monsters on field here (including equip effects) ---
    // for effects on hand see class YCard
    // hand trap effects mostly reactions in other methods
    // passive effects are included in battle phase (see class SummonedMonster)
    
    // used for displaying the name of the monster whose effects one has copied (also useful in other situations)
    public static YMonster getMonsterById (int monsterId) {
        switch (monsterId) {
            case 0: return Mon.NoMonster;
            case 1: return Mon.Obstacle;
            case 2: return Mon.Barrier;
            case 3: return Mon.Napalm;
            case 4: return Mon.ExhaustedExecutioner;
            case 5: return Mon.RecklessRusher;
            case 6: return Mon.SteepLearningCurve;
            case 7: return Mon.Shield;
            case 8: return Mon.Sword;
            case 9: return Mon.Lance;
            case 10: return Mon.BuggedUpgrade;
            case 11: return Mon.SuicideCommando;
            case 12: return Mon.BackBouncer;
            case 13: return Mon.Banisher;
            case 14: return Mon.CardGrabber;
            case 15: return Mon.CopyCat;
            case 16: return Mon.SkillStealer;
            case 17: return Mon.Flakship;
            case 18: return Mon.HolyLance;
            case 19: return Mon.ModeChanger;
            case 20: return Mon.Necromancer;
            case 21: return Mon.Burner;
            case 22: return Mon.BigBackBouncer;
            case 23: return Mon.BigBanisher;
            case 24: return Mon.DiamondSword;
            case 25: return Mon.SlickRusher;
            case 26: return Mon.Incorruptible;
            case 27: return Mon.AttackStopper;
            case 28: return Mon.MonsterStealer;
            case 29: return Mon.BigBurner;
            case 30: return Mon.Eradicator;
            case 31: return Mon.BigAttackStopper;
            case 32: return Mon.Neutraliser;
            case 33: return Mon.GodKillingSpear;
            case 34: return Mon.God;
            case 35: return Mon.Demon;
            case 36: return Mon.DemonGod;
            default: YuGiOhJi.debugDialog("Error: out of bounds in getMonsterById(...); attempted Id: " + monsterId); return Mon.NoMonster;
        }
    }
    
    // -- OPTIONAL EFFECTS in general --
    
    // calls the whatever the optional effect of the summoned monster is one wants to use
    public static void activateOptionalEffectOnField (SummonedMonster SumMonster) { // not needded to check if monster is existing, because buttons only do something if they are visible
        effectiveMonsterId = SumMonster.effectiveMonsterId();
        if (Mon.God.monsterId==effectiveMonsterId || Mon.Obstacle.monsterId==effectiveMonsterId) {
            if (SumMonster.canStillUseOncePerTurnEffect) {effectShowACardLookAtCardActivate(SumMonster);}
            else{oncePerTurnErrorDialog();}      
        }
        else if (Mon.MonsterStealer.monsterId==effectiveMonsterId) {
            if (SumMonster.canStillUseOncePerTurnEffect) {effectMonsterStealerActivate(SumMonster);}
            else{oncePerTurnErrorDialog();}      
        }
        else if (Mon.Burner.monsterId==effectiveMonsterId) {
            if (SumMonster.canStillUseOncePerTurnEffect) {effectBurnerActivate(SumMonster);}
            else{oncePerTurnErrorDialog();}      
        }
        else {
            if (Game.isSwitchingOnOncePerTurnRule && !SumMonster.canStillUseOncePerTurnEffect) {
                oncePerTurnErrorDialog();
            }
            else{
                if (Mon.BigBurner.monsterId==effectiveMonsterId) {effectBigBurnerActivate(SumMonster);}
                else if (Mon.CardGrabber.monsterId==effectiveMonsterId) {effectCardGrabber(SumMonster);}
                else if (Mon.SkillStealer.monsterId==effectiveMonsterId) {effectSkillStealerActivate(SumMonster);}
                else if (Mon.CopyCat.monsterId==effectiveMonsterId) {effectCopyCatActivate(SumMonster);}
                else if (Mon.ModeChanger.monsterId==effectiveMonsterId) {effectModeChangerActivate(SumMonster);}
                else if (Mon.Necromancer.monsterId==effectiveMonsterId) {effectNecromancer(SumMonster);}
                else if (Mon.BigBackBouncer.monsterId==effectiveMonsterId) {effectBigBackBouncerActivate(SumMonster);}
                else if (Mon.BigBanisher.monsterId==effectiveMonsterId) {effectBigBanisherActivate(SumMonster);}
                else if (Mon.Eradicator.monsterId==effectiveMonsterId) {effectEradicatorActivate(SumMonster);}
                else if (Mon.Neutraliser.monsterId==effectiveMonsterId) {effectNeutraliserOptionalActivate(SumMonster);}
                else if (Mon.Demon.monsterId==effectiveMonsterId) {onFieldEffectDemon(SumMonster);}
                else if (getMonsterById(effectiveMonsterId).hasEquipEffect) { // this has to come after Demon, because Demon has more abilities and thus has to be checked first
                    equipEffectFromMonsterActivate(SumMonster);
                }
            }
        }
        
    }
    
    // displays the dialog that a given monster can only use its effects once per turn (in order to repeat oneself less, out-source dialog here)
    public static void oncePerTurnErrorDialog() {
        YuGiOhJi.errorDialog("You can use the effect of this monster only once per turn.", "Error.");
    }
    
    // displays the dialog that a given monster is immune against the attempted effect (in order to repeat oneself less, out-source dialog here)
    public static void immuneErrorDialog() {
        YuGiOhJi.errorDialog("This monster is not affected by this effect.", "Error. No valid target.");
    }
    
    // displays the dialog that a given monster is immune against the attempted effect (in order to repeat oneself less, out-source dialog here)
    public static void alreadyNegatedErrorDialog() {
        YuGiOhJi.errorDialog("You can not negate an already negated card.", "Error. No valid target.");
    }
    
    // acts with an optional effect on a monster (for similar method for equip cards or GY cards as target, see right below)
    // (has already been checked if selected monster can be targeted when calling this method)
    public static void executeOptionalEffectOnField (SummonedMonster SumMonster) {
        if (Game.isActEffRevealCard()) { // for simplicity it must be allowed that even Incorruptible can be revealed
            if (SumMonster.isFaceDown) {effectShowACardLookAtCardExecute(SumMonster);}
        }
        else if (Game.isActEffBigBurner()) {
            if (SumMonster.isBasicallySameMonster(Game.ActEffMonSource)) {
                 YuGiOhJi.errorDialog("You have to choose a monster other than itself.", "Error. No valid target.");
            }
            else if (!SumMonster.isPlayersMonster) {
                YuGiOhJi.errorDialog("You have to tribute one of your own summoned monsters.", "Error. No valid target.");
            }
            else {
                effectBigBurnerExecute(SumMonster);
            }
        }
        else if (Game.isActEffSkillStealer()) {
            if (SumMonster.isFaceDown) {
                YuGiOhJi.errorDialog("You have to choose a face up card.", "Error. No valid target.");
            }
            else if (SumMonster.Monster.hasEquipEffect && !SumMonster.Monster.isEquipMonsterAndHasCopyableEffect()){
                YuGiOhJi.errorDialog("Out of the equip cards one can only copy those with passive effects.", "Error. No valid target.");
            }
            else if (!SumMonster.Monster.hasPassEffects && !SumMonster.Monster.hasOptionalEffectOnField){
                YuGiOhJi.errorDialog("This monster has no effect you can copy.", "Error. No valid target.");
            }
            else {
                Game.ActEffMonTarget = SumMonster; // important info for helping CPU deciding if wants to negate
                effectSkillStealerExecute(SumMonster.Monster);
            }
        }
        else if (Game.isActEffModeChanger()) {
            if (SumMonster.isImmune()) {
                immuneErrorDialog();
            }
            else {
                effectModeChangerExecute(SumMonster);
            }
        }
        else if (Game.isActEffBigBackBouncer()) {
            if (SumMonster.isImmune()) {
                immuneErrorDialog();
            }
            else {
                effectBigBackBouncerExecuteOnMonster(SumMonster);
            }
        }
        else if (Game.isActEffMonsterStealer()) {
            if (SumMonster.isImmune()) {
                immuneErrorDialog();
            }
            else if (SumMonster.isPlayersMonster) {
                YuGiOhJi.errorDialog("You can only steal cards of the opponent.", "Error. No valid target.");
            }
            else {
                effectMonsterStealerExecuteOnMonster(SumMonster);
            }
        }
        else if (Game.isActEffEradicator()) {
            if (SumMonster.isPlayersMonster) {
                YuGiOhJi.errorDialog("You can only destroy cards of the opponent.", "Error. No valid target.");
            }
            else if (!SumMonster.canBeDestroyedByEffect()) {
                if (SumMonster.hasToRevealDiamondSword()) { // here CPU has to reveal that it is Diamond Sword, in order to prove, that the monster is not destroyed
                    revealDiamondSword(SumMonster); // To make rules the same for player and computer, I make it such, that instead of a destruction you get a reveal and the effect is ended,
                } // because the computer can not decide on another target. I'm to lazy to program that.
                Game.deactivateCurrentEffects();
            }
            else {
                effectEradicatorExecuteOnMonster(SumMonster);
            }
        }
        else if (Game.isActEffOptNeutraliser()) {
            if (!SumMonster.canBeNegated()) {
                alreadyNegatedErrorDialog();
            }
            else {
                effectNeutraliserOptionalExecuteOnMonster(SumMonster);
            }
        }
        else if (Game.isActEffEquipFromMonster()) {
            if (SumMonster.isBasicallySameMonster(Game.ActEffMonSource)) {
                YuGiOhJi.errorDialog("No monster can equip itself. Choose another monster.", "Error. No valid target.");
            }
            else if (!SumMonster.isEquippable()) {
                YuGiOhJi.errorDialog("This monster can not be equipped.", "Error. No valid target.");
            }
            else if (Game.ActEffMonSource.Monster.equals(Mon.BuggedUpgrade) && !SumMonster.canBeNegated()) {
                alreadyNegatedErrorDialog();
            }
            else {
                equipEffectFromMonsterExecute(SumMonster);
            }
        }
        else if (Game.isActEffEquipFromStack()) {
            if (!SumMonster.isEquippable()) {
                YuGiOhJi.errorDialog("This monster can not be equipped.", "Error. No valid target.");
            }
            else if (Game.ActEffStack.stackIsBelongingToMonster(SumMonster)) {
                YuGiOhJi.errorDialog("It is already equipping this monster. Choose another one.", "Error. No valid target.");
            }
            else if (Game.ActEffStack.getNthCardOfStack(Game.actEffCardNo).lowMonster.equals(Mon.BuggedUpgrade) && !SumMonster.canBeNegated()) {
                alreadyNegatedErrorDialog();
            }
            else {
                equipEffectFromStackExecute(SumMonster);
            }
        }
        else if (Game.isActEffEquipFromHand()) {
            if (!SumMonster.isEquippable()) {
                YuGiOhJi.errorDialog("This monster can not be equipped.", "Error. No valid target.");
            }
            else if (HandPlayer.getNthCardOfHand(Game.actEffCardNo).lowMonster.equals(Mon.BuggedUpgrade) && !SumMonster.canBeNegated()) {
                alreadyNegatedErrorDialog();
            }
            else {
                equipEffectFromHandExecute(SumMonster);
            }
        }
        // for executing tribute summoning
        else if (Game.isActTribSumMidboss()) {
            if (SumMonster.isPlayersMonster) {
                tributeSummonMidbossExecute(SumMonster);
            }
            else {
                YuGiOhJi.errorDialog("You have to tribute one of your own summoned monsters for tribute summoning.", "Error. No valid target.");
            }
        }
        else if (Game.isActTribSetMidboss()) {
            if (SumMonster.isPlayersMonster) {
                tributeSetMidbossExecute(SumMonster);
            }
            else {
                YuGiOhJi.errorDialog("You have to tribute one of your own summoned monsters for tribute setting.", "Error. No valid target.");
            }
        }
        else if (Game.isActTribSumEndboss()) {
            if (!SumMonster.isPlayersMonster) {
                YuGiOhJi.errorDialog("You have to tribute your own summoned monsters for tribute summoning.", "Error. No valid target.");
            }
            else {
                if (SumMonster.sumMonsterNumber==Game.tributeNo1) {
                YuGiOhJi.errorDialog("You already have chosen this monster as a tribute. Choose more monsters.", "");
                }
                else {
                    tributeSummonEndbossExecute(SumMonster);
                }
            }
        }
        else if (Game.isActTribSetEndboss()) {
            if (!SumMonster.isPlayersMonster) {
                YuGiOhJi.errorDialog("You have to tribute your own summoned monsters for tribute summoning.", "Error. No valid target.");
            }
            else {
                if (SumMonster.sumMonsterNumber==Game.tributeNo1) {
                YuGiOhJi.errorDialog("You already have chosen this monster as a tribute. Choose more monsters.", "");
                }
                else {
                    tributeSetEndbossExecute(SumMonster);
                }
            }
        }
        else if (Game.isActTribSumGod()) {
            if (!SumMonster.isPlayersMonster) {
                YuGiOhJi.errorDialog("You have to tribute your own summoned monsters for tribute summoning.", "Error. No valid target.");
            }
            else {
                if (SumMonster.sumMonsterNumber==Game.tributeNo1 || SumMonster.sumMonsterNumber==Game.tributeNo2) {
                YuGiOhJi.errorDialog("You already have chosen this monster as a tribute. Choose more monsters.", "");
                }
                else {
                    tributeSummonGodExecute(SumMonster);
                }
            }
        }
        else if (Game.isActTribSetGod()) {
            if (!SumMonster.isPlayersMonster) {
                YuGiOhJi.errorDialog("You have to tribute your own summoned monsters for tribute setting.", "Error. No valid target.");
            }
            else {
                if (SumMonster.sumMonsterNumber==Game.tributeNo1 || SumMonster.sumMonsterNumber==Game.tributeNo2) {
                YuGiOhJi.errorDialog("You already have chosen this monster as a tribute. Choose more monsters.", "");
                }
                else {
                    tributeSetGodExecute(SumMonster);
                }
            }
        }
        else if (Game.isActEffHandTrapNeutraliser()) {
            if (SumMonster.isImmune()) {
                immuneErrorDialog();
            }
            else if (SumMonster.isNotAbleToUseItsEffects) {
                alreadyNegatedErrorDialog();
            }
            else {
                handTrapEffectNegateExecuteOnMonster(SumMonster);
            }
        }
    }
    
    // acts with an optional effect on an equip card
    public static void executeOptionalEffectToEquipCard (EStack Stack, int n) {
        if (Game.isActEffSkillStealer()) {
            if (Stack.isCopyableEquipCard(n)){
                Game.ActEffStack = Stack; // important info for helping CPU deciding if wants to negate
                Game.actEffCardNo = n;
                YMonster EquipMonster = Stack.getNthCardOfStack(n).lowMonster;
                effectSkillStealerExecute(EquipMonster);
            }
            else {
                YuGiOhJi.errorDialog("Out of the equip cards one can only copy face up ones with passive effects.", "Error. No valid target.");
            }
        }
        if (Game.isActEffBigBackBouncer()) {
            effectBigBackBouncerExecuteOnEquipCard(Stack, n);
        }
        if (Game.isActEffMonsterStealer()) {
            if (Stack.getControllerOfNthEquipCard(n)) {
                YuGiOhJi.errorDialog("You can only steal cards of the opponent.", "Error. No valid target.");
            }
            else {
                effectMonsterStealerExecuteOnEquipCard(Stack, n);
            }
        }
        if (Game.isActEffEradicator()) {
            if (Stack.getControllerOfNthEquipCard(n)) {
                YuGiOhJi.errorDialog("You can only destroy cards of the opponent.", "Error. No valid target.");
            }
            else {
                effectEradicatorExecuteOnEquipCard(Stack, n);
            }
        }
        if (Game.isActEffOptNeutraliser()) { // only on non-negated card
            if (Stack.getNegationStatusOfNthEquipCard(n)) {
                alreadyNegatedErrorDialog();
            }
            else {
                effectNeutraliserOptionalExecuteOnEquipCard(Stack, n, true);
            }
        }
        if (Game.isActEffHandTrapNeutraliser()) { // only on non negated card
            if (Stack.getNegationStatusOfNthEquipCard(n)) {
                alreadyNegatedErrorDialog();
            }
            else {
                handTrapEffectNegateExecuteOnEquipCard(Stack, n, true);
            }
        }
        
    }
    
    // acts with an optional effect on a card in a graveyard
    public static void executeOptionalEffectToGYCard (boolean isPlayersGY, int n) {
        if (Game.isActEffReviveMook()) {
            if (!isPlayersGY) { // forbid reanimation fromopponents GY for now (alowing it would require rewriting the (special) summoning methods)
                YuGiOhJi.errorDialog("You can only revive monsters from your own graveyard.", "Error. No valid target.");
            }
            else {
                effectNecromancer1Execute(isPlayersGY, n);
            }
        }
        if (Game.isActEffReviveMidboss()) {
            if (!isPlayersGY) { // forbid reanimation fromopponents GY for now (alowing it would require rewriting the (special) summoning methods)
                YuGiOhJi.errorDialog("You can only revive monsters from your own graveyard.", "Error. No valid target.");
            }
            else {
                effectNecromancer2Execute(isPlayersGY, n);
            }
        }
        if (Game.isActEffBigBackBouncer()) {
            effectBigBackBouncerExecuteOnGYCard(isPlayersGY, n);
        }
        if (Game.isActEffBigBanisher()) {
            if (isPlayersGY) {
                YuGiOhJi.errorDialog("You can only banish monsters from your opponent's graveyard.", "Error. No valid target.");
            }
            else {
                effectBigBanisherExecute(n);
            }
        }
        
    }
    
    // -- EFFECTS on the FIELD --
    
    // Effect ID = 1
    // Effect: "show a card, look at a card" (effects of 1st & last monster #1 & #34 )
    public static void effectShowACardLookAtCardActivate (SummonedMonster SumMonster) {
        // "cost": has to have one card in hand or a facedown monster
        // also opponent has to have a face down monster
        int intDialogResult = YuGiOhJi.multipleChoiceDialog("Reveal a card as cost for effect?", "Are you sure?", new String[]{"yes", "no"}, "no");
        if (intDialogResult==0) {
            Game.actEffId=1;
            Game.ActEffMonSource=SumMonster;
            SumMonster.canStillUseOncePerTurnEffect=false;
            YChooseCardWindow.payCost(2, "Reveal a card or a facedown monster.");
        }
    }
    public static void effectShowACardLookAtCardExecute (SummonedMonster SumMonster) {
        Game.ActEffMonTarget=SumMonster;
        boolean isCanceling = cpuIsUsingEffectNegate();
        if (!isCanceling) {revealFaceDownMonster(SumMonster.sumMonsterNumber, SumMonster.isPlayersMonster);}
        Game.deactivateCurrentEffects();
    }
    
    // shows nth summoned monster (a face down card) of a given player
    public static void revealFaceDownMonster (int monsterNumber, boolean isPlayersMonster) {
        SummonedMonster SumMonster = SummonedMonster.getNthSummonedMonster(monsterNumber,isPlayersMonster);
        if (isPlayersMonster) {
            YuGiOhJi.setMonsterButtonIcon(SumMonster.Monster.cardPathDef, monsterNumber, isPlayersMonster, false, true);
        }
        else { // monsters of the CPU appear upside down
            if (SumMonster.isLowerMonster()) {
                YuGiOhJi.setMonsterButtonIcon(SumMonster.Card.upMonster.cardPathDef, monsterNumber, isPlayersMonster, false, true);
            }
            else {
                YuGiOhJi.setMonsterButtonIcon(SumMonster.Card.lowMonster.cardPathDef, monsterNumber, isPlayersMonster, false, true);
            }
        }
        YuGiOhJi.informationDialog("revealing monster " + SumMonster.Monster.monsterName, "Showing card");
        SumMonster.isKnown=true;
        SumMonster.updateAttDefDisplay(); // update Att/Def display permanently
        YuGiOhJi.setMonsterButtonIcon("/images/YuGiOhJiFacedown.png", monsterNumber, isPlayersMonster, false, true);
    }
    
    // reveals unknown Diamond Sword in order to prove that it is not destroyed by Eradicator
    public static void revealDiamondSword (SummonedMonster SumMonster) {
        if (SumMonster.hasToRevealDiamondSword()) {
            revealFaceDownMonster(SumMonster.sumMonsterNumber, SumMonster.isPlayersMonster);
        }
    }
    
    // shows card with given card ID on the hand of a given player
    public static void revealHandCard (int handCardId, boolean isPlayersHand) {
        Hand FullHand = Hand.getHand(isPlayersHand);
        int cardNo = FullHand.getPositionOfCardWithCardId(handCardId);
        revealNthHandCard(cardNo, isPlayersHand);
    }
    
    // shows nth card on the hand of a given player
    public static void revealNthHandCard (int cardNumberOnHand, boolean isPlayersHand) {
        YCard Card = Hand.getHand(isPlayersHand).getNthCardOfHand(cardNumberOnHand);
        if (isPlayersHand) {
            YuGiOhJi.setCardButtonIcon("/images/YuGiOhJiFacedown.png", cardNumberOnHand, isPlayersHand, true);
            YuGiOhJi.informationDialog("you reveal card " + Card.cardName, "Showing card");
            YuGiOhJi.setCardButtonIcon(Card.upMonster.cardPathAtt, cardNumberOnHand, isPlayersHand, true);
        }
        else {
            YuGiOhJi.setCardButtonIcon(Card.upMonster.cardPathAtt, cardNumberOnHand, isPlayersHand, true);
            YuGiOhJi.informationDialog("computer has revealed the card " + Card.cardName, "Showing card");
            YuGiOhJi.setCardButtonIcon("/images/YuGiOhJiFacedown.png", cardNumberOnHand, isPlayersHand, true);
        }
    }
    
    // Effect ID = 2
    public static void effectBurnerActivate (SummonedMonster SumMonster) {
        // cost: 1/2 MOOK or 1 MOOK (i.e. any card containing a MOOK as a monster)
        int intDialogResult = YuGiOhJi.multipleChoiceDialog("Pay the cost (1 MOOK card) for effect?", "Are you sure?", new String[]{"yes", "no"}, "no");
        if (intDialogResult==0) {
            Game.actEffId=2;
            Game.ActEffMonSource=SumMonster;
            SumMonster.canStillUseOncePerTurnEffect=false;
            YChooseCardWindow.payCost(1, "Pay the cost for the effect of Burner. (1 MOOK)");
        }
    }
    public static void effectBurnerExecute (YCard Card) { // this method can only be used by the player
        // possible negation has already happened in YChooseCardWindow class
        dealBurnDamage(Card.extractMookBurnDamage(), false);
        Game.deactivateCurrentEffects();
    }
    
    // deals a given amount of burn damage to a given player and returns true, if the game goes on
    public static boolean dealBurnDamage (int burnDamage, boolean isHittingPlayer) { // in order not to repeat oneself, simply deal battle damage
        return BattlePhase.isDealingBattleDamageAndContinuingGame(true, burnDamage, isHittingPlayer);
    }
    
    // Effect: "burn" (effects of Napalm, Burner, & Big Burner (monsters #3, #21, #29))
    // use method dealBurnDamage(...)
    
    // Effect ID = 3
    public static void effectBigBurnerActivate (SummonedMonster SumMonster) {
        // cost: one own summoned monster (always useable, because can tribute itself)
        int intDialogResult = YuGiOhJi.multipleChoiceDialog("Pay the cost (1 monster) for effect?", "Are you sure?", new String[]{"yes", "no"}, "no");
        if (intDialogResult==0) {
            Game.actEffId=3;
            Game.ActEffMonSource=SumMonster;
            if (Game.isSwitchingOnOncePerTurnRule) {
                SumMonster.canStillUseOncePerTurnEffect=false;
            }
            YuGiOhJi.informationDialog("Click on one of your summoned monsters you want to tribute.", "");
        }
    }
    public static void effectBigBurnerExecute (SummonedMonster SumMonster) {
        Game.ActEffMonTarget=SumMonster;
        boolean isCanceling = cpuIsUsingEffectNegate();
        if (!isCanceling) {
            dealBurnDamage(SumMonster.att, false); // This order is important, because if the monster was first killed and then the damage dealt, it would be zero!
            SumMonster.killMonster();
        }
        Game.deactivateCurrentEffects();
    }
    
    // leave out effects of Reckless Rusher, Steep Learning Curve (happen somewhere else)
    // for the 4 equip effects look at the lower end of this class   
    // leave out effects of trap monsters SuicideCommando, BackBouncer, Banisher (these passive effects happen somewhere else)
    
    // Effect: "Card Grabber" (monster #14)
    public static void effectCardGrabber (SummonedMonster SumMonster) {
        // branch if one can use both effects
        // or one just gets passed to the one of the two effects one can use
        int payableSemipoints = CardOptions.countPayableCosts(true);
        if (payableSemipoints>=4) {
            int intDialogResult = YuGiOhJi.multipleChoiceDialog("Which effect do you want to use?", "Choose effect", new String[]{"get card from deck", "send card to GY"}, "");
            if (intDialogResult==0) {
                effectCardGrabber1Activate(SumMonster);
            }
            else if (intDialogResult==1) {
                effectCardGrabber2Activate(SumMonster);
            }
        }
        else if (payableSemipoints>=2 && payableSemipoints<4) {
            effectCardGrabber2Activate(SumMonster);
        }
    }
    
    // Effect ID = 4 (1st effect of Card Grabber: the search effect)
    public static void effectCardGrabber1Activate (SummonedMonster SumMonster) {
        // cost: two cards
        // if one has 10 cards on hand, one has to pay costs by discarding at least one card (still add that restriction at part where one selects cards as cost)
        // also one has to have at least one card in the deck
        int intDialogResult = YuGiOhJi.multipleChoiceDialog("Pay the cost (worth 2 cards) for effect?", "Are you sure?", new String[]{"yes", "no"}, "no");
        if (intDialogResult==0) {
            Game.actEffId=4;
            Game.ActEffMonSource=SumMonster;
            if (Game.isSwitchingOnOncePerTurnRule) {
                SumMonster.canStillUseOncePerTurnEffect=false;
            }
            YChooseCardWindow.payCost(4, "Pay the cost for the 1st effect of Card Grabber. (worth 2 cards)");
        }
    }
    public static void effectCardGrabber1Execute (int cardNumberInDeck, boolean isBelongingToPlayer) {
        // possible negation has already happened in SearchWindow class
        // (one does tribute at least one card from the hand rather than discarding between paying costs and searching)
        YuGiOhJi.informationDialog("adding card " + Deck.getDeck(isBelongingToPlayer).getNthCardOfDeck(cardNumberInDeck).cardName + " to hand", "");
        Deck.addNthCardFromDeckToHand(cardNumberInDeck, isBelongingToPlayer);
        // revealNthHandCard(YHand.numberOfCardsOnHand(isBelongingToPlayer), isBelongingToPlayer); // reveal card in order to proove that one was allowed to search that card (no, not needed, since one can search any card)
        Game.deactivateCurrentEffects();
    }
    
    // Effect ID = 5 (2nd effect of Card Grabber: the "Foolish Burial" effect)
    public static void effectCardGrabber2Activate (SummonedMonster SumMonster) {
        // cost: one card
        // also one has to have at least one card in the deck
        int intDialogResult = YuGiOhJi.multipleChoiceDialog("Pay the cost (worth 1 card) for effect?", "Are you sure?", new String[]{"yes", "no"}, "no");
        if (intDialogResult==0) {
            Game.actEffId=5;
            Game.ActEffMonSource=SumMonster;
            if (Game.isSwitchingOnOncePerTurnRule) {
                SumMonster.canStillUseOncePerTurnEffect=false;
            }
            YChooseCardWindow.payCost(2, "Pay the cost for the 2nd effect of Card Grabber. (worth 1 card)");
        }
    }
    public static void effectCardGrabber2Execute (int cardNumberInDeck, boolean isBelongingToPlayer) {
        // possible negation has already happened in SearchWindow class
        YuGiOhJi.informationDialog("burying card " + Deck.getDeck(isBelongingToPlayer).getNthCardOfDeck(cardNumberInDeck).cardName + ".", ""); // hm, maybe one could also call it "milling card"
        Deck.addNthCardFromDeckToGY(cardNumberInDeck, isBelongingToPlayer);
        Game.deactivateCurrentEffects();
    }
    
    // about Skill Stealer / Copy Cat: for simplicity don't allow to copy equip effects (unless comming up with a good idea how to code that properly)
    // exception: one should still be able to copy the piercing effect of "Lance"
    
    // Effect ID = 6
    public static void effectSkillStealerActivate (SummonedMonster SumMonster) {
        // "cost": has to have one card in hand or a facedown monster
        // also there has to be a face up non-immune effect monster on the field or at least "Lance" (also as an equip card)
        int intDialogResult = YuGiOhJi.multipleChoiceDialog("Reveal a card as cost for effect?", "Are you sure?", new String[]{"yes", "no"}, "no");
        if (intDialogResult==0) {
            Game.actEffId=6;
            Game.ActEffMonSource=SumMonster;
            YChooseCardWindow.payCost(2, "Reveal a card on the hand or a facedown monster.");
        }
    }
    public static void effectSkillStealerExecute (YMonster Monster) { // exclude face down monsters, Shield, Sword, Bugged Upgrade, FLAKship, Attack Stopper, Big Attack Stopper, God Killing Spear, and Demon God (already taken care of by allowing only passive and optional effects, because both attack stoppers count as having no optional effects on field)
        copyMonsterEffect(Monster);
        Game.deactivateCurrentEffects();
    }
    
    // lets the already selected summoned monster remember that it uses the effects of a given monster
    public static void copyMonsterEffect (YMonster Monster) {
        boolean isCanceling = cpuIsUsingEffectNegate();
        if (!isCanceling) {
            YuGiOhJi.informationDialog("copied effect of " + Monster.monsterName, "");
            Game.ActEffMonSource.isUsingEffectsOfOtherMonster=true;
            Game.ActEffMonSource.otherEffectMonsterID=Monster.monsterId;
        }
    }
    
    // returns true, if a monster has abilities one can copy (excluding the equip monsters Shield, Sword and Bugged Upgrade) [in case of Lance or Demon, one can only copy the non-equipping effects]
    public boolean hasStealableEffect() {
        return ( ((hasPassEffects || hasOptionalEffectOnField) && !hasEquipEffect) || isEquipMonsterAndHasCopyableEffect() );
    }
    
    // returns true, if a monster has an equip effect, but also a copyable effect 
    public boolean isEquipMonsterAndHasCopyableEffect() {
        return (hasEquipEffect && hasPassEffects);
    }
    
    // Effect ID = 7
    public static void effectCopyCatActivate (SummonedMonster SumMonster) {
        // cost: one card on hand
        int intDialogResult = YuGiOhJi.multipleChoiceDialog("Discard a card as cost for effect?", "Are you sure?", new String[]{"yes", "no"}, "no");
        if (intDialogResult==0) {
            Game.actEffId=7;
            Game.ActEffMonSource=SumMonster;
            YChooseCardWindow.payCost(2, "Discard a card.");
        }
    }
    public static void effectCopyCatExecute (YCard Card) { // exclude Shield, Sword, Bugged Upgrade, (explicitly by card ID); rest: FLAKship, Attack Stopper, Big Attack Stopper, God Killing Spear, and Demon God (taken care of by looking for passive and optional effects, because both attack stoppers count as having no optional effects on field)
        // possible negation has already happened in YChooseCardWindow class
        boolean isCopyableUpperMonsterEffect = Card.upMonster.hasStealableEffect();
        boolean isCopyableLowerMonsterEffect = Card.lowMonster.hasStealableEffect();
        if (isCopyableUpperMonsterEffect && isCopyableLowerMonsterEffect) {
            int intDialogResult = YuGiOhJi.multipleChoiceDialog("The effects of which monster shall be used?", "Choose monster", new String[]{Card.upMonster.monsterName, Card.lowMonster.monsterName}, Card.upMonster.monsterName);
            if (intDialogResult!=1) {
                Game.actEffCardId=Card.cardId;
                Game.isActAboutUpperMon = true;
                copyMonsterEffect(Card.upMonster);
            }
            else {
                Game.actEffCardId=Card.cardId;
                Game.isActAboutUpperMon = false;
                copyMonsterEffect(Card.lowMonster);
            }
        }
        else if (isCopyableUpperMonsterEffect && !isCopyableLowerMonsterEffect) {
            YuGiOhJi.informationDialog("You can only copy the effect of the upper monster.", "");
            Game.actEffCardId=Card.cardId;
            Game.isActAboutUpperMon = true;
            copyMonsterEffect(Card.upMonster);
        }
        else if (!isCopyableUpperMonsterEffect && isCopyableLowerMonsterEffect) {
            YuGiOhJi.informationDialog("You can only copy the effect of the lower monster.", "");
            Game.actEffCardId=Card.cardId;
            Game.isActAboutUpperMon = false;
            copyMonsterEffect(Card.lowMonster);
        }
        else { // This only happens when discarding Attack Stopper - Bugged Upgrade or Big Attack Stopper - Sword.
            YuGiOhJi.errorDialog("You can neither copy the effect of the upper monster nor the lower monster.", "No effect copied");
        } // update: This case shouldn't occur any more, since when attempting to use the effect and when paying the cost, this is checked. But just to be sure still keep it.
        Game.deactivateCurrentEffects();
    }
    
    // FLAKship, Holy Lance have no effect respectively only an effect that is considered during battle phase (see class SummonedMonster)
    
    // Effect ID = 8
    public static void effectModeChangerActivate (SummonedMonster SumMonster) {
        // cost: one card (apart from that always useable, even on itself)
        int intDialogResult = YuGiOhJi.multipleChoiceDialog("Pay the cost (any card) for effect?", "Are you sure?", new String[]{"yes", "no"}, "no");
        if (intDialogResult==0) {
            Game.actEffId=8;
            Game.ActEffMonSource=SumMonster;
            if (Game.isSwitchingOnOncePerTurnRule) {
                SumMonster.canStillUseOncePerTurnEffect=false;
            }
            YChooseCardWindow.payCost(2, "Pay the cost for the effect of Mode Changer. (any card)");
        }
    }
    public static void effectModeChangerExecute (SummonedMonster SumMonster) {
        Game.ActEffMonTarget=SumMonster;
        boolean isCanceling = cpuIsUsingEffectNegate();
        if (!isCanceling) {SumMonster.changeMode(true);}
        Game.deactivateCurrentEffects();
    }
    
    // Effect: "Necromancer" (monster #20)
    public static void effectNecromancer (SummonedMonster SumMonster) {
        // branch (binary dialog) if one can use both effects
        // or one just gets passed to the one of the two effects one can use
        // (apart from that one can always pay enough for the second effect, since Necromancer can tribute itself;
        // just for 1st effect one has to have an equip card or another card in GY)
        boolean hasMook = Deck.getGY(true).hasMook();
        boolean hasMidboss = Deck.getGY(true).hasMidboss();
        int numberOfEquipCards = EStack.countOwnEquipMonsters(true, false);
        int numberOfGYCards = Deck.numberOfCardsInGY(true);
        boolean isAbleToUse1stEffect = (hasMook && (numberOfEquipCards>=1 || numberOfGYCards>=2));
        boolean isAbleToUse2ndEffect = (hasMidboss);
        if (isAbleToUse1stEffect && isAbleToUse2ndEffect) {
        int intDialogResult = YuGiOhJi.multipleChoiceDialog("Which effect do you want to use?", "Choose effect", new String[]{"summon MOOK", "summon MIDBOSS"}, "");
            if (intDialogResult==0) {
                effectNecromancer1Activate(SumMonster);
            }
            else if (intDialogResult==1) {
                effectNecromancer2Activate(SumMonster);
            }
        }
        else if (isAbleToUse1stEffect && !isAbleToUse2ndEffect) {
            effectNecromancer1Activate(SumMonster);
        }
        else if (!isAbleToUse1stEffect && isAbleToUse2ndEffect) {
            effectNecromancer2Activate(SumMonster);
        }
    }
    
    // rewrite the 2 Necromancer effects such that one selects the monster to be revived first, then pay costs
    
    // Effect ID = 9
    public static void effectNecromancer1Activate (SummonedMonster SumMonster) {
        // cost: 1/2 card (one has to be able to pay at least 1 card other than the mook one wants to revive)
        // also one has to have one MOOK in own GY (so any card except Neutraliser/Incorruptable/BigBanisher)
        // also one has to have one free monster card zone
        int intDialogResult = YuGiOhJi.multipleChoiceDialog("Revive a MOOK for the cost of one equip card or GY card?", "Are you sure?", new String[]{"yes", "no"}, "no");
        if (intDialogResult==0) {
            Game.actEffId=9;
            Game.ActEffMonSource=SumMonster;
            if (Game.isSwitchingOnOncePerTurnRule) {
                SumMonster.canStillUseOncePerTurnEffect=false;
            }
            YuGiOhJi.informationDialog("Click on the MOOK you want to revive.", "Choose target");
            SearchWindow.searchDeck(Deck.getGY(true), false, true, false, false, false);
        }
    }
    public static void effectNecromancer1Execute (boolean isPlayersGY, int cardNumberInGY) {
        // here one just pays the cost
        Game.actEffCardNo = cardNumberInGY;
        Game.actEffCardId = Deck.getGY(isPlayersGY).getNthCardOfDeck(Game.actEffCardNo).cardId;
        YChooseCardWindow.payCost(1, "Pay the cost for the 1st effect of Necromancer. (worth 1/2 card)");
    }
    public static void effectReviveMook (boolean isPlayersGY) {
        // revive the MOOK, if the CPU doesn't interfer
        boolean isCancelingViaBanisher = AIinterrupts.cpuIsUsingBanisherHandTrapNegate();
        boolean isCanceling = false;
        if (!isCancelingViaBanisher) { // only ask to negate Necromancer, if has not negated summoning itself
            isCanceling = cpuIsUsingEffectNegate();
        }
        if (!isCanceling && !isCancelingViaBanisher) {
            int cardNumberInGY = Deck.getGY(isPlayersGY).getPositionOfCardWithCardIdInDeck(Game.actEffCardId);
            YCard.specialSummonMonster(Deck.getGY(isPlayersGY).getNthCardOfDeck(cardNumberInGY), false, true);
        }
        Game.deactivateCurrentEffects();
    }
    
    // Effect ID = 10
    public static void effectNecromancer2Activate (SummonedMonster SumMonster) {
        // cost: 1 card other than the MIDBOSS in GY one wants to revive
        // also one has to have one MIDBOSS in own GY [still add that restriction when choosing midboss]
        // also one has to have one free monster card zone
        int intDialogResult = YuGiOhJi.multipleChoiceDialog("Revive a MIDBOSS for the cost of 1 card?", "Are you sure?", new String[]{"yes", "no"}, "no");
        if (intDialogResult==0) {
            Game.actEffId=10;
            Game.ActEffMonSource=SumMonster;
            if (Game.isSwitchingOnOncePerTurnRule) {
                SumMonster.canStillUseOncePerTurnEffect=false;
            }
            YuGiOhJi.informationDialog("Click on the MIDBOSS you want to revive.", "Choose target");
            SearchWindow.searchDeck(Deck.getGY(true), false, false, true, false, false);
        }
    }
    public static void effectNecromancer2Execute (boolean isPlayersGY, int cardNumberInGY) {
        // here one just pays the cost
        Game.actEffCardNo = cardNumberInGY;
        Game.actEffCardId = Deck.getGY(isPlayersGY).getNthCardOfDeck(Game.actEffCardNo).cardId;
        YChooseCardWindow.payCost(2, "Pay the cost for the 2nd effect of Necromancer. (worth 1 card)");
    }
    public static void effectReviveMidboss (boolean isPlayersGY) {
        boolean isCancelingViaBanisher = AIinterrupts.cpuIsUsingBanisherHandTrapNegate();
        boolean isCanceling = false;
        if (!isCancelingViaBanisher) { // Here sometimes, unfortunately for the CPU and contrary to the player, the CPU has to decide whether to negate the summoning, without knowing which monster of the card will be revived: C'est la vie.
            isCanceling = cpuIsUsingEffectNegate();
        }
        if (!isCanceling && !isCancelingViaBanisher) { // only ask to negate Necromancer, if has not negated summoning itself
            // if one chooses Big Banisher - Burner, or Incorruptible - Holy Lance ask which one shall be revived (binary dialog)
            int cardNumberInGY = Deck.getGY(isPlayersGY).getPositionOfCardWithCardIdInDeck(Game.actEffCardId);
            YCard Card = Deck.getGY(isPlayersGY).getNthCardOfDeck(cardNumberInGY);
            if (Card.upMonster.stars==2 && Card.lowMonster.stars==2){
                int intDialogResult = YuGiOhJi.multipleChoiceDialog("Which monster do you want to revive?", "Choose monster", new String[]{Card.upMonster.monsterName, Card.lowMonster.monsterName}, Card.upMonster.monsterName);
                if (intDialogResult!=1) {
                    YCard.specialSummonMonster(Card, true, true);
                }
                else {
                    YCard.specialSummonMonster(Card, false, true);
                }
            }
            else if (Card.upMonster.stars==2 && Card.lowMonster.stars!=2) {
                YCard.specialSummonMonster(Card, true, true);
            }
            else if (Card.upMonster.stars!=2 && Card.lowMonster.stars==2) {
                YCard.specialSummonMonster(Card, false, true);
            }
        }
        Game.deactivateCurrentEffects();
    }
    
    // Effect ID = 11
    // (for the similar, weaker effect of Back Bouncer, see effect #28 far below)
    public static void effectBigBackBouncerActivate (SummonedMonster SumMonster) {
        // cost: 1 card
        // one can not have 10 cards on the hand, or one has to discard a card first
        // (apart from that always useable, because one needs a card in GY or somewhere on the field, but can also bounce itself back, even when tributed itself)
        int intDialogResult = YuGiOhJi.multipleChoiceDialog("Pay the cost (worth 1 card) for effect?", "Are you sure?", new String[]{"yes", "no"}, "no");
        if (intDialogResult==0) {
            Game.actEffId=11;
            Game.ActEffMonSource=SumMonster;
            if (Game.isSwitchingOnOncePerTurnRule) {
                SumMonster.canStillUseOncePerTurnEffect=false;
            }
            YChooseCardWindow.payCost(2, "Pay the cost for the effect of Big Back Bouncer. (worth 1 card)");
        }
    }
    public static void effectBigBackBouncerExecuteOnMonster (SummonedMonster SumMonster) {
        Game.ActEffMonTarget=SumMonster;
        boolean isCanceling = cpuIsUsingEffectNegate();
        if (!isCanceling) {
            if (Hand.getHand(SumMonster.isOriginallyPlayersMonster).numberOfCards==10) {
                YCard.discardCardOfFullHand(SumMonster.isOriginallyPlayersMonster);
            }
            else {
                // YuGiOhJi.informationDialog("adding card " + SumMonster.Card.cardName + " to hand", "Returning card"); // dialog already in called method
                YCard.returnCardBackToHand(SumMonster); // whom the monster originally belonged to is already checked in that method
                Game.deactivateCurrentEffects();
            }
        }
        else {
            Game.deactivateCurrentEffects();
        }
    }
    public static void effectBigBackBouncerExecuteOnEquipCard (EStack Stack, int cardNumberInStack) {
        Game.ActEffStack=Stack;
        Game.actEffCardNo=cardNumberInStack;
        boolean isCanceling = cpuIsUsingEffectNegate();
        if (!isCanceling) {
            boolean isPlayersCard = Stack.getOriginalOwnerOfNthEquipCard(cardNumberInStack);
            if (Hand.getHand(isPlayersCard).numberOfCards==10) {
                YCard.discardCardOfFullHand(isPlayersCard);
            }
            else {
                // YuGiOhJi.informationDialog("adding card " + Stack.getNthCardOfStack(cardNumberInStack).cardName + " to hand", "Returning card"); // dialog already in called method
                YCard.returnEquipCardBackToHand(Stack, cardNumberInStack); // whom the equip card originally belonged to is already checked in that method
                Game.deactivateCurrentEffects();
            }
        }
        else {
            Game.deactivateCurrentEffects();
        }
    }
    public static void effectBigBackBouncerExecuteOnGYCard (boolean isPlayersGY, int cardNumberInGY) {
        Game.isAboutPlayerActEff=isPlayersGY;
        Game.actEffCardNo=cardNumberInGY;
        boolean isCanceling = cpuIsUsingEffectNegate();
        if (!isCanceling) {
            if (Hand.getHand(Game.isAboutPlayerActEff).numberOfCards==10) {
                YCard.discardCardOfFullHand(Game.isAboutPlayerActEff);
            }
            else {
                // YuGiOhJi.informationDialog("adding card " + Deck.getGY(Game.isAboutPlayerActEff).getNthCardOfDeck(cardNumberInGY).cardName + " to hand", ""); // dialog already in called method
                Deck.addNthCardFromGYToHand(cardNumberInGY, isPlayersGY);
                Game.deactivateCurrentEffects();
            }
        }
        else {
            Game.deactivateCurrentEffects();
        }
    }
    
    // Effect ID = 12
    public static void effectBigBanisherActivate (SummonedMonster SumMonster) {
        // cost: any card
        // the opponent has to have a card in GY
        // (apart from tat always usable, because can also tribute itself)
        int intDialogResult = YuGiOhJi.multipleChoiceDialog("Pay the cost (any card) for effect?", "Are you sure?", new String[]{"yes", "no"}, "no");
        if (intDialogResult==0) {
            Game.actEffId=12;
            Game.ActEffMonSource=SumMonster;
            if (Game.isSwitchingOnOncePerTurnRule) {
                SumMonster.canStillUseOncePerTurnEffect=false;
            }
            YChooseCardWindow.payCost(2, "Pay the cost for the effect of Big Banisher. (any card)");
        }
    }
    public static void effectBigBanisherExecute (int cardNumberInGY) {
        Game.actEffCardNo = cardNumberInGY;
        boolean isCanceling = cpuIsUsingEffectNegate();
        if (!isCanceling) {
            YuGiOhJi.informationDialog("banishing card " + Deck.getGY(false).getNthCardOfDeck(cardNumberInGY).cardName, "");
            Deck.banishNthCardFromGY(cardNumberInGY, false);
        }
        Game.deactivateCurrentEffects();
    }
    
    // leave out effects of DiamondSword, SlickRusher, Incorruptible, Attack Stopper (happen somewhere else)
    
    // Effect ID = 13
    public static void effectMonsterStealerActivate (SummonedMonster SumMonster) {
        // cost: 2 cards
        // also opponent has to have a monster or equip card (equip card would then be summoned to field)
        // also one has to have at least one monster card zone free!
        int intDialogResult = YuGiOhJi.multipleChoiceDialog("Pay the cost (worth 2 cards) for effect?", "Are you sure?", new String[]{"yes", "no"}, "no");
        if (intDialogResult==0) {
            Game.actEffId=13;
            Game.ActEffMonSource=SumMonster;
            if (Game.isSwitchingOnOncePerTurnRule) {
                SumMonster.canStillUseOncePerTurnEffect=false;
            }
            YChooseCardWindow.payCost(4, "Pay the cost for the effect of Monster Stealer. (worth 2 cards)");
        }
    }
    public static void effectMonsterStealerExecuteOnMonster (SummonedMonster SumMonster) { // don't allow "stealing" of own monsters (already checked when calling this method)
        Game.ActEffMonTarget=SumMonster;
        boolean isCanceling = cpuIsUsingEffectNegate();
        if (!isCanceling) {
            SummonedMonster.stealMonster(SumMonster);
            Game.ActEffMonSource.canStillAttackThisTurn=false; // forbid user of the effect to attack this turn
        }
        Game.deactivateCurrentEffects();
    }
    public static void effectMonsterStealerExecuteOnEquipCard (EStack Stack, int cardNumberInStack) { // don't allow "stealing" of own monsters (already checked when calling this method)
        Game.ActEffStack = Stack;
        Game.actEffCardNo = cardNumberInStack;
        boolean isCanceling = cpuIsUsingEffectNegate();
        if (!isCanceling) {
            // ask if the equip card monster shall be summoned in face up attack or face up defence mode to field
            YCard Card = Stack.getNthCardOfStack(cardNumberInStack);
            int intDialogResult = YuGiOhJi.multipleChoiceDialog("Which mode shall the stolen monster be summoned in?", "Summoning " + Card.lowMonster.monsterName, new String[]{"face up attack mode", "face up defence mode"}, "face up attack mode");
            if (intDialogResult==0) {
                SummonedMonster.stealEquipMonster(Stack, cardNumberInStack, true);
            }
            else {
                SummonedMonster.stealEquipMonster(Stack, cardNumberInStack, false);
            }
            // forbid user of the effect to attack this turn with Monster Stealer
            Game.ActEffMonSource.canStillAttackThisTurn=false;
        }
        Game.deactivateCurrentEffects();
    }
    
    // Effect ID = 14
    public static void effectEradicatorActivate (SummonedMonster SumMonster) {
        // cost: 1 card
        // opponent needs to have a destructable monster, or any equip card
        int intDialogResult = YuGiOhJi.multipleChoiceDialog("Pay the cost (worth 1 card) for effect?", "Are you sure?", new String[]{"yes", "no"}, "no");
        if (intDialogResult==0) {
            Game.actEffId=14;
            Game.ActEffMonSource=SumMonster;
            if (Game.isSwitchingOnOncePerTurnRule) {
                SumMonster.canStillUseOncePerTurnEffect=false;
            }
            YChooseCardWindow.payCost(2, "Pay the cost for the effect of Eradicator. (worth 1 card)");
        }
    }
    public static void effectEradicatorExecuteOnMonster (SummonedMonster SumMonster) {
        Game.ActEffMonTarget=SumMonster;
        boolean isCanceling = cpuIsUsingEffectNegate();
        if (!isCanceling) {
            SumMonster.killMonster();
            if (getMonsterById(SumMonster.effectiveMonsterId()).isBurning) { // in case Eradicator killed Napalm, inflict burn damage to the owner of Eradicator (not the original owner, also not the owner of the destroyed monster)
                dealBurnDamage(Mon.Napalm.NapalmBurnDamage(), Game.ActEffMonSource.isPlayersMonster);
            }
        }
        Game.deactivateCurrentEffects();
    }
    public static void effectEradicatorExecuteOnEquipCard (EStack Stack, int cardNumberInStack) {
        Game.ActEffStack = Stack;
        Game.actEffCardNo = cardNumberInStack;
        boolean isCanceling = cpuIsUsingEffectNegate();
        if (!isCanceling) {
            Stack.sendNthEquipCardToGY(cardNumberInStack);
        }
        Game.deactivateCurrentEffects();
    }
    
    // Effect ID = 15  (This is the optional effect on field during player's own turn.)
    // (for the hand trap effect during player's own turn, see effect ID 27)
    // (for the optional effect on field during opponent's turn, see effect ID 29)
    // in order not to repeat oneself, also use this at the end of the phase in the opponent's turn (and after the CPU used an (equip) effect)
    public static void effectNeutraliserOptionalActivate (SummonedMonster SumMonster) {
        if (!Game.isPlayersTurn && !Game.isBattlePhase()) {Game.playerCurrentlyNegates=true;}
        // cost: 1 card
        // (if there is only Neutraliser on the field, one has to be able to pay something else in order to have a still valid target afterwards)
        int intDialogResult = YuGiOhJi.multipleChoiceDialog("Pay the cost (worth 1 card) for effect?", "Are you sure?", new String[]{"yes", "no"}, "no");
        if (intDialogResult==0) {
            Game.actEffId=15;
            Game.ActEffMonSource=SumMonster;
            if (Game.isSwitchingOnOncePerTurnRule) {
                SumMonster.canStillUseOncePerTurnEffect=false;
            }
            YChooseCardWindow.payCost(2, "Pay the cost for the effect of Neutraliser. (worth 1 card)");
        }
    }
    public static void effectNeutraliserOptionalExecuteOnMonster (SummonedMonster SumMonster) {
        // when calling this method already checked that one can only negate card that still can use their effects
        if (Game.isPlayersTurn) {
            negateCardPreventively(SumMonster);
        }
        else {
            if (Game.isActCPUEff) { // used after CPU paid costs after activating an effect
                SumMonster.isNotAbleToUseItsEffects=true;
                negatedInLastMoment();
                Game.deactivateCurrentEffects();
                Game.deactivateCurrentCPUEffects(); // cpu erases memory about current CPU sacrifices
                AIdelegate.cpuContinuesPlayingTurn();
            }
            else { // since this effect is used at the end of the phase during opponent's turn, counts as negated preventively
                negateCardPreventively(SumMonster);
                AIdelegate.cpuProceedsToNextPhase(); // for simplicity, no matter if the CPU negates the negation, the phase still keeps ending afterwards
            }
        }
    }
    public static void effectNeutraliserOptionalExecuteOnEquipCard (EStack Stack, int cardNumberInStack, boolean isNegatingPreventively) { // when calling this method already checked that one can only negate card that still can use their effects
        Game.ActEffStack = Stack;
        Game.actEffCardNo = cardNumberInStack;
        if (Game.isPlayersTurn) { // if the player negates an equip card during the own turn, it has to be preventive (because one can not equip during opponent's turn)
            negateCardPreventively(Stack, cardNumberInStack);
        }
        else {
            if (Game.isActCPUEff) { // used after CPU paid costs after activating an effect
                if (isNegatingPreventively) {
                    negateCardPreventively(Stack, cardNumberInStack);
                }
                else {
                    negateEquipCard(Stack, cardNumberInStack, true, isNegatingPreventively);
                    AIdelegate.cpuContinuesPlayingTurn();
                }
            }
            else { // since this effect is used at the end of the phase during opponent's turn, counts as negated preventively
                negateCardPreventively(Stack, cardNumberInStack);
                AIdelegate.cpuProceedsToNextPhase(); // for simplicity, no matter if the CPU negates the negation, the phase still keeps ending afterwards
            }
        }
    }
    
    // in order to repeat oneself less, out-source here the preventive negation of a summoned monster
    public static void negateCardPreventively (SummonedMonster SumMonster) {
        Game.isAboutPlayerActEff = SumMonster.isPlayersMonster; // important for the computer to know whom the target belongs
        boolean isCanceling = cpuIsUsingEffectNegate();
        if (!isCanceling) {
            SumMonster.isNotAbleToUseItsEffects=true;
            if (!SumMonster.isPlayersMonster) {
                negatedPreventively();
            }
        }
        Game.deactivateCurrentEffects();
    }
    // in order to repeat oneself less, out-source here the preventive negation of an equip card
    public static void negateCardPreventively (EStack Stack, int cardNumberInStack) {
        Game.isAboutPlayerActEff = Stack.getControllerOfNthEquipCard(cardNumberInStack); // important for the computer to know whom the target belongs
        boolean isCanceling = cpuIsUsingEffectNegate();
        if (!isCanceling) {
            negateEquipCard(Stack, cardNumberInStack, true, true);
        }
        Game.deactivateCurrentEffects();
    }
    
    // in order not to repeat oneself, out-source here, what happens, if one negated preventively (just for statistics/achievements)
    public static void negatedPreventively() {
        Game.statisticsNumPreventiveEffectNegates++;
        if (Game.statisticsNumPreventiveEffectNegates==10) {
            YuGiOhJi.informationDialog("Hidden achievement found: \"Better Save than Sorry\". You preventively negated the effects of 10 cards of the opponent in one game.", ""); Game.unlockTrophy(Game.trophySaveUnlocked);
        }
    }
    
    // in order not to repeat oneself, out-source here, what happens, if one negated in last moment (just for statistics/achievements)
    public static void negatedInLastMoment() {
        Game.statisticsNumLastMomentEffectNegates++;
        if (Game.statisticsNumLastMomentEffectNegates==5) {
            YuGiOhJi.informationDialog("Hidden achievement found: \"Not so fast!\". You negated the effects of 5 cards of the opponent in the last possible moment in one game.", ""); Game.unlockTrophy(Game.trophyNoSoFastUnlocked);
        }
    }
    
    // -- EQUIP EFFECTS (optional effects on field or from hand) --
    // about equip monsters: if equip monsters get their effects negated, turn them facedown
    
    // Effect ID = 16
    public static void equipEffectFromMonsterActivate (SummonedMonster SumMonster) {
        // no costs
        // but there has to be a summoned monster (other than itself!) on the field
        // ignore card copying equip effects (only copied Lance has only passive effect (considered only during damage calculation))
        // don't allow to equip a negated monster with Bugged Upgrade
        int intDialogResult = YuGiOhJi.multipleChoiceDialog("After confirming, click on the monster you want to equip.", "Equip a monster with this card?", new String[]{"yes", "no"}, "no");
        if (intDialogResult==0) {
            Game.actEffId=16;
            Game.ActEffMonSource=SumMonster;
            Game.isAboutPlayerActEff=SumMonster.isOriginallyPlayersMonster;
            if (Game.isSwitchingOnOncePerTurnRule) {
                SumMonster.canStillUseOncePerTurnEffect=false;
            }
        }
    }
    public static void equipEffectFromMonsterExecute (SummonedMonster SumMonster) {
        Game.ActEffMonTarget=SumMonster;
        boolean isCanceling = cpuIsUsingEffectNegate();
        if (!isCanceling) {
            EStack.equipMonster(SumMonster, Game.ActEffMonSource.Card, true, Game.isAboutPlayerActEff);
            SummonedMonster.getNthSummonedMonster(Game.ActEffMonSource.sumMonsterNumber, Game.ActEffMonSource.isPlayersMonster).deleteMonster();
        }
        Game.deactivateCurrentEffects();
    }
    
    // Effect ID = 17
    public static void equipEffectFromStackActivate (EStack Stack, int cardNumberInStack) {
        // no costs
        // but there has to be a summoned monster on the field other than the one it is already equipping
        int intDialogResult = YuGiOhJi.multipleChoiceDialog("After confirming, click on the monster you want to equip.", "Equip a monster with this card?", new String[]{"yes", "no"}, "no");
        if (intDialogResult==0) {
            Game.actEffId=17;
            Game.ActEffStack=Stack;
            Game.actEffCardNo=cardNumberInStack;
            Game.isAboutPlayerActEff=Stack.getOriginalOwnerOfNthEquipCard(cardNumberInStack);
        }
    }
    public static void equipEffectFromStackExecute (SummonedMonster SumMonster) {
        Game.ActEffMonTarget=SumMonster;
        boolean isCanceling = cpuIsUsingEffectNegate();
        if (!isCanceling) {
            EStack.equipMonster(SumMonster, Game.ActEffStack.getNthCardOfStack(Game.actEffCardNo), true, Game.isAboutPlayerActEff);
            EStack.getNthStack(Game.ActEffStack.stackNumber, Game.ActEffStack.isBelongingToPlayer).deleteNthCardInEquipStackAndRearrange(Game.actEffCardNo);
        }
        Game.deactivateCurrentEffects();
    }
    
    // Effect ID = 18
    public static void equipEffectFromHandActivate (int n) {
        // no costs
        // but there has to be a summoned monster on the field
        int intDialogResult = YuGiOhJi.multipleChoiceDialog("After confirming, click on the monster you want to equip.", "Equip a monster with this card?", new String[]{"yes", "no"}, "no");
        if (intDialogResult==0) {
            Game.actEffId=18;
            Game.actEffCardNo=n;
        }
    }
    public static void equipEffectFromHandExecute (SummonedMonster SumMonster) {
        // effects from hand can not be negated
        EStack.equipMonster(SumMonster, HandPlayer.getNthCardOfHand(Game.actEffCardNo), true, true);
        HandPlayer.deleteNthCardOnHandAndRearrange(Game.actEffCardNo, true);
        Game.deactivateCurrentEffects();
    }
    
    // -- SPECIAL SUMMONING EFFECTS --
    
    // add code here for executing tribute summoning / special summoning
    // Note that an ENDBOSS special summoning ITSELF can not be negated, since it is not on the field
    // (similarly effects/summonings from hand can not be negated).
    // However, Necromancer (or a monster copying its effect) reviving a monster can be negated,
    // since that happens from the field.
    
    // special summon a monster from hand or GY
    public static void specialSummon (YCard Card, int cardNumber, int cardId, boolean isConcerningUpperMonster, boolean isInGY) {
        // branching here if ENDBOSS is special summoning itself from GY or just a special summoning from hand
        if (isInGY) {
            specialSummonFromGY(cardNumber, cardId, true); // extend here, if adding cards with endbosses also as lower monsters
        }
        else {
            specialSummonFromHand(Card, cardNumber, cardId, isConcerningUpperMonster);
        }
    }
    
    // Effect ID = 19 special summon from hand
    public static void specialSummonFromHand (YCard Card, int cardNumberOnHand, int cardId, boolean isConcerningUpperMonster) {
        int stars;
        if (isConcerningUpperMonster) {
            stars = Card.upMonster.stars;
        }
        else {
            stars = Card.lowMonster.stars;
        }
        if (stars == 2) {
            int intDialogResult = YuGiOhJi.multipleChoiceDialog("Pay the cost (worth 1 card) for special summoning?", "Are you sure?", new String[]{"yes", "no"}, "no");
            if (intDialogResult==0) {
                Game.actEffId=19;
                Game.actEffCardNo=cardNumberOnHand;
                Game.actEffCardId=cardId;
                Game.isActAboutUpperMon=isConcerningUpperMonster;
                YChooseCardWindow.payCost(2, "Pay the cost for special summoning. (worth 1 card)");
            }
        }
        else if (stars == 3) {
            int intDialogResult = YuGiOhJi.multipleChoiceDialog("Pay the cost (worth 2 cards) for special summoning?", "Are you sure?", new String[]{"yes", "no"}, "no");
            if (intDialogResult==0) {
                Game.actEffId=19;
                Game.actEffCardNo=cardNumberOnHand;
                Game.actEffCardId=cardId;
                Game.isActAboutUpperMon=isConcerningUpperMonster;
                YChooseCardWindow.payCost(4, "Pay the cost for special summoning. (worth 2 cards)");
            }
        }
        // another case should not happen, because already checked before calling this method
    }
    
    // Effect ID = 20 special summon itself from GY
    // (for reviving from GY using Necromancer see effects 9 and 10)
    public static void specialSummonFromGY (int cardNumberInGY, int cardId, boolean isConcerningUpperMonster) {
        int intDialogResult = YuGiOhJi.multipleChoiceDialog("Pay the cost (worth 2 cards) for special summoning?", "Are you sure?", new String[]{"yes", "no"}, "no");
        if (intDialogResult==0) {
            Game.actEffId=20;
            Game.actEffCardNo=cardNumberInGY;
            Game.actEffCardId=cardId;
            Game.isActAboutUpperMon=isConcerningUpperMonster; // extend here, if adding cards with endbosses also as lower monsters
            YChooseCardWindow.payCost(4, "Pay the cost for special summoning. (worth 2 cards)"); // possible negate happens in YChooseCardWindow clss file
        }
    }
    
    // -- TRIBUTE SUMMONING EFFECTS --
    // normal summoning of higher level monsters
    // (needs tributing a number of own summoned monsters equal to one less than the number of stars of the monster)
    
    // Effect ID = 21
    public static void tributeSummonMidbossActivate (int cardNumber, boolean isConcerningUpperMonster){
        Game.actEffId=21;
        Game.actEffCardNo=cardNumber;
        Game.isActAboutUpperMon=isConcerningUpperMonster;
        int intDialogResult = YuGiOhJi.multipleChoiceDialog("Click on one of your summoned monsters you want to tribute.", "Tribute summon?", new String[]{"yes", "no"}, "no");
        if (intDialogResult!=0) {
            Game.deactivateCurrentEffects();
        }
    }
    public static void tributeSummonMidbossExecute (SummonedMonster SumMonster){ // hand effects like tribute summoning can not be negated
        SumMonster.killMonster();
        SummonedMonster TargetMonster = SummonedMonster.determineFreeMonsterZone(true);
        YCard.setCardToNthSumMonster(HandPlayer.getNthCardOfHand(Game.actEffCardNo), Game.isActAboutUpperMon, false, true, TargetMonster.sumMonsterNumber, false, true);
        Game.hasStillNormalSummonPlayer=false;
        Game.deactivateCurrentEffects();
    }
    
    // Effect ID = 22
    public static void tributeSetMidbossActivate (int cardNumber, boolean isConcerningUpperMonster){
        Game.actEffId=22;
        Game.actEffCardNo=cardNumber;
        Game.isActAboutUpperMon=isConcerningUpperMonster;
        int intDialogResult = YuGiOhJi.multipleChoiceDialog("Click on one of your summoned monsters you want to tribute.", "Tribute set?", new String[]{"yes", "no"}, "no");
        if (intDialogResult!=0) {
            Game.deactivateCurrentEffects();
        }
    }
    public static void tributeSetMidbossExecute (SummonedMonster SumMonster){ // hand effects like tribute summoning can not be negated
        SumMonster.killMonster();
        SummonedMonster TargetMonster = SummonedMonster.determineFreeMonsterZone(true);
        YCard.setCardToNthSumMonster(HandPlayer.getNthCardOfHand(Game.actEffCardNo), Game.isActAboutUpperMon, true, false, TargetMonster.sumMonsterNumber, false, true);
        Game.hasStillNormalSummonPlayer=false;
        Game.deactivateCurrentEffects();
    }
    
    // returns one if the argument is larger than one, else returns zero
    // (useful for counting number of monsters selected as tributes)
    public static int sign (int number) {
        if (number>0) {return 1;} else {return 0;}
    }
    
    // Effect ID = 23
    public static void tributeSummonEndbossActivate (int cardNumber, boolean isConcerningUpperMonster){
        Game.actEffId=23;
        Game.actEffCardNo=cardNumber;
        Game.isActAboutUpperMon=isConcerningUpperMonster;
        int intDialogResult = YuGiOhJi.multipleChoiceDialog("Click on 2 of your summoned monsters you want to tribute.", "Tribute summon?", new String[]{"yes", "no"}, "no");
        if (intDialogResult!=0) {
            Game.deactivateCurrentEffects();
        }
    }
    public static void tributeSummonEndbossExecute (SummonedMonster SumMonster){
        // if not one tribute already selected, just select one more tribute (else summon)
        int numberOfSelectedTributes = sign(Game.tributeNo1)+sign(Game.tributeNo2);
        if (numberOfSelectedTributes==0) {
            Game.tributeNo1=SumMonster.sumMonsterNumber;
        }
        else if (numberOfSelectedTributes==1) {
            Game.tributeNo2=SumMonster.sumMonsterNumber;
            // tribute both monsters
            SummonedMonster.getNthSummonedMonster(Game.tributeNo1, true).killMonster();
            SummonedMonster.getNthSummonedMonster(Game.tributeNo2, true).killMonster();
            SummonedMonster TargetMonster = SummonedMonster.determineFreeMonsterZone(true);
            YCard.setCardToNthSumMonster(HandPlayer.getNthCardOfHand(Game.actEffCardNo), Game.isActAboutUpperMon, false, true, TargetMonster.sumMonsterNumber, false, true);
            Game.hasStillNormalSummonPlayer=false;
            // hand effects like tribute summoning can not be negated
            Game.deactivateCurrentEffects();
        }
        
    }
    
    // Effect ID = 24
    public static void tributeSetEndbossActivate (int cardNumber, boolean isConcerningUpperMonster){
        Game.actEffId=24;
        Game.actEffCardNo=cardNumber;
        Game.isActAboutUpperMon=isConcerningUpperMonster;
        int intDialogResult = YuGiOhJi.multipleChoiceDialog("Click on 2 of your summoned monsters you want to tribute.", "Tribute set?", new String[]{"yes", "no"}, "no");
        if (intDialogResult!=0) {
            Game.deactivateCurrentEffects();
        }
    }
    public static void tributeSetEndbossExecute (SummonedMonster SumMonster){
        // if not one tribute already selected, just select one more tribute (else set)
        int numberOfSelectedTributes = sign(Game.tributeNo1)+sign(Game.tributeNo2);
        if (numberOfSelectedTributes==0) {
            Game.tributeNo1=SumMonster.sumMonsterNumber;
        }
        else if (numberOfSelectedTributes==1) {
            Game.tributeNo2=SumMonster.sumMonsterNumber;
            // tribute both monsters
            SummonedMonster.getNthSummonedMonster(Game.tributeNo1, true).killMonster();
            SummonedMonster.getNthSummonedMonster(Game.tributeNo2, true).killMonster();
            SummonedMonster TargetMonster = SummonedMonster.determineFreeMonsterZone(true);
            YCard.setCardToNthSumMonster(HandPlayer.getNthCardOfHand(Game.actEffCardNo), Game.isActAboutUpperMon, true, false, TargetMonster.sumMonsterNumber, false, true);
            Game.hasStillNormalSummonPlayer=false;
            // hand effects like tribute summoning can not be negated
            Game.deactivateCurrentEffects();
        }
        
    }
    
    // Effect ID = 25
    public static void tributeSummonGodActivate (int cardNumber, boolean isConcerningUpperMonster){
        Game.actEffId=25;
        Game.actEffCardNo=cardNumber;
        Game.isActAboutUpperMon=isConcerningUpperMonster;
        int intDialogResult = YuGiOhJi.multipleChoiceDialog("Click on 3 of your summoned monsters you want to tribute.", "Tribute summon?", new String[]{"yes", "no"}, "no");
        if (intDialogResult!=0) {
            Game.deactivateCurrentEffects();
        }
    }
    public static void tributeSummonGodExecute (SummonedMonster SumMonster){
        // if not 2 tributes already selected, just select one more tribute (else summon)
        int numberOfSelectedTributes = sign(Game.tributeNo1)+sign(Game.tributeNo2)+sign(Game.tributeNo3);
        if (numberOfSelectedTributes==0) {
            Game.tributeNo1=SumMonster.sumMonsterNumber;
        }
        if (numberOfSelectedTributes==1) {
            Game.tributeNo2=SumMonster.sumMonsterNumber;
        }
        else if (numberOfSelectedTributes==2) {
            Game.tributeNo3=SumMonster.sumMonsterNumber;
            // tribute all 3 monsters
            SummonedMonster.getNthSummonedMonster(Game.tributeNo1, true).killMonster();
            SummonedMonster.getNthSummonedMonster(Game.tributeNo2, true).killMonster();
            SummonedMonster.getNthSummonedMonster(Game.tributeNo3, true).killMonster();
            SummonedMonster TargetMonster = SummonedMonster.determineFreeMonsterZone(true);
            YCard.setCardToNthSumMonster(HandPlayer.getNthCardOfHand(Game.actEffCardNo), Game.isActAboutUpperMon, false, true, TargetMonster.sumMonsterNumber, false, true);
            Game.hasStillNormalSummonPlayer=false;
            // hand effects like tribute summoning can not be negated
            Game.deactivateCurrentEffects();
        }
        
    }
    
    // Effect ID = 26
    public static void tributeSetGodActivate (int cardNumber, boolean isConcerningUpperMonster){
        Game.actEffId=26;
        Game.actEffCardNo=cardNumber;
        Game.isActAboutUpperMon=isConcerningUpperMonster;
        int intDialogResult = YuGiOhJi.multipleChoiceDialog("Click on 3 of your summoned monsters you want to tribute.", "Tribute set?", new String[]{"yes", "no"}, "no");
        if (intDialogResult!=0) {
            Game.deactivateCurrentEffects();
        }
    }
    public static void tributeSetGodExecute (SummonedMonster SumMonster){
        // if not 2 tributes already selected, just select one more tribute (else set)
        int numberOfSelectedTributes = sign(Game.tributeNo1)+sign(Game.tributeNo2)+sign(Game.tributeNo3);
        if (numberOfSelectedTributes==0) {
            Game.tributeNo1=SumMonster.sumMonsterNumber;
        }
        if (numberOfSelectedTributes==1) {
            Game.tributeNo2=SumMonster.sumMonsterNumber;
        }
        else if (numberOfSelectedTributes==2) {
            Game.tributeNo3=SumMonster.sumMonsterNumber;
            // tribute all 3 monsters
            SummonedMonster.getNthSummonedMonster(Game.tributeNo1, true).killMonster();
            SummonedMonster.getNthSummonedMonster(Game.tributeNo2, true).killMonster();
            SummonedMonster.getNthSummonedMonster(Game.tributeNo3, true).killMonster();
            SummonedMonster TargetMonster = SummonedMonster.determineFreeMonsterZone(true);
            YCard.setCardToNthSumMonster(HandPlayer.getNthCardOfHand(Game.actEffCardNo), Game.isActAboutUpperMon, true, false, TargetMonster.sumMonsterNumber, false, true);
            Game.hasStillNormalSummonPlayer=false;
            // hand effects like tribute summoning can not be negated
            Game.deactivateCurrentEffects();
        }
    }
    
    // I forgot about the hand trap negate during one's own turn. That's why it is so far down below here.
    
    // Effect ID = 27 (This is the effect negate from the hand during player's own turn.)
    // (for the optional effect on field during player's own turn, see effect ID 15)
    // (for the optional effect on field during opponent's turn, see effect ID 29)
    // in order not to repeat oneself, also use this at the end of the phase in the opponent's turn (and after the CPU used an (equip) effect)
    public static void handTrapEffectNegateActivate() {
        if (!Game.isPlayersTurn && !Game.isBattlePhase()) {Game.playerCurrentlyNegates=true;}
        // Don't show a dialog here, because when called during opponent's turn, one shall not be allowed to opt-out, because the game wouldn't continue.
        Game.actEffId=27;
        //revealHandCard(NeutraliserSkillStealer.cardId, true); // player using an on hand effect (hand traps and searches) should reveal it first (for dramatic effect and stuff)
        Hand.discardCard(HandPlayer.getPositionOfCardWithCardId(Card.NeutraliserSkillStealer.cardId), true);
        YuGiOhJi.informationDialog("Click on the card you want to negate.", "discarding " + YuGiOhJi.NeutraliserSkillStealer.cardName);
    }
    public static void handTrapEffectNegateExecuteOnMonster (SummonedMonster SumMonster) {
        SumMonster.isNotAbleToUseItsEffects=true;
        Game.deactivateCurrentEffects();
        if (!Game.isPlayersTurn) { // since this effect can only be used during player's turn or at the end of the phase during opponent's turn
            if (Game.isActCPUEff) { // used after CPU paid costs after activating an effect
                negatedInLastMoment();
                Game.deactivateCurrentCPUEffects(); // cpu erases memory about current CPU sacrifices
                AIdelegate.cpuContinuesPlayingTurn();
            }
            else { // since this effect is used at the end of the phase during opponent's turn
                negatedPreventively(); // hand traps can not be negated, but for the statistics progress the counter here
                AIdelegate.cpuProceedsToNextPhase();
            }
        }
        negatedPreventively(); // hand traps can not be negated, but for the statistics progress the counter here
    }
    public static void handTrapEffectNegateExecuteOnEquipCard (EStack Stack, int cardNumberInStack, boolean isNegatingPreventively) {
        negateEquipCard(Stack, cardNumberInStack, true, isNegatingPreventively);
        if (!Game.isPlayersTurn) {
            if (Game.isActCPUEff) { // used after CPU paid costs after activating an effect
                Game.deactivateCurrentCPUEffects(); // cpu erases memory about current CPU sacrifices
                AIdelegate.cpuContinuesPlayingTurn();
            }
            else { // since this effect is used at the end of the phase during opponent's turn
                AIdelegate.cpuProceedsToNextPhase();
            }
        }
    }
    
    // in order not to repeat oneself, out-source the part about negating an equip card here
    public static void negateEquipCard (EStack Stack, int cardNumberInStack, boolean isUsedByPlayer, boolean isNegatingPreventively) {
        Stack.downgradeUnequippedMonster(cardNumberInStack);
        Stack.setNegationStatusOfNthEquipCard(cardNumberInStack, true);
        if (cardNumberInStack==Stack.numberOfCards) {
            YuGiOhJi.setEquipButtonIcon("/images/YuGiOhJiFacedown.png", Stack.stackNumber, Stack.isBelongingToPlayer, true);
        }
        if (isUsedByPlayer) { // for statistics
            if (isNegatingPreventively) {negatedPreventively();}
            else {negatedInLastMoment();}
            Game.deactivateCurrentCPUEffects();
        }
        Game.deactivateCurrentEffects();
    }
    
    // -- PASSIVE EFFECTS --
    
    // Effect ID = 28
    // (for the similar effect of Big Back Bouncer, see effect #11)
    // similar source code than for Big Back Bouncer (just left out the part of the method that can also target cards in GY or equip cards)
    // This method is used by the CPU as well.
    public static void effectBackBouncerActivate (SummonedMonster SumMonster) {
        // no cost: activates, when destroyed in battle
        // the opponent has to have a card that is not immune while defeating a monster, immune to effects in general or copying any of these effects
        Game.actEffId=28;
        Game.ActEffMonTarget=SumMonster;
        // opponent already missed the chance to cancel this effect
        boolean isOriginallyPlayersMonster = SumMonster.isOriginallyPlayersMonster;
        if (Hand.getHand(isOriginallyPlayersMonster).numberOfCards==10) {
            YuGiOhJi.informationDialog("attempting to add card " + SumMonster.Card.cardName + " to hand, due to the effect of " + Mon.BackBouncer.monsterName, "Passive effect");
            YCard.discardCardOfFullHand(isOriginallyPlayersMonster);
        }
        else {
            effectBackBouncerExecute();
        }
    }
    public static void effectBackBouncerExecute() {
        YuGiOhJi.informationDialog("Returning the card " + Game.ActEffMonTarget.Card.cardName + " to hand, due to the effect of " + Mon.BackBouncer.monsterName, "Passive effect");
        YCard.returnCardBackToHand(Game.ActEffMonTarget);
        Game.deactivateCurrentEffects();
        Game.deactivateCurrentCPUEffects(); // not needed, since passive effect called from battle phase, but can not hurt here
        BattlePhase.endAttack(false);
    }
    
    // other passive effects handled in Battle Phase part in SummonedMonster class file
    
    // -- EFFECTS DURING OPPONENT'S TURN --
    
    // - effect negates -
    
    // Effect ID = 29
    // (for the optional effect on field during player's own turn, see effect ID 15)
    // (for the hand trap effect during player's own turn, see effect ID 27)
    // (for the hand trap effect of Neutraliser during opponent's turn, see method playerDiscardsNeutraliser in BattlePhase class file)
    // (for the analog effect of this on equip cards, see effect ID 30)
    public static void onFieldEffectNeutraliserDuringOpponentsTurnOrBattleActivate (SummonedMonster SumMonster) {
        if (!Game.isPlayersTurn && !Game.isBattlePhase()) {Game.playerCurrentlyNegates=true;}
        // since this is an on field effect, one should in principle be able to negate it, but I leave that out for simplicity
        Game.actEffId=29;
        Game.ActEffMonTarget=SumMonster;
        // The CPU doesn't need to know, what the source of the effect negation is, because it is always a reaction and can thus not be negated.
        YChooseCardWindow.payCost(2, "Pay the cost for negating effects. (worth 1 card)");
    }
    public static void onFieldEffectNeutraliserDuringOpponentsTurnOrBattleExecute (boolean isTributingAFightingMonster) {
        SummonedMonster.getNthSummonedMonster(Game.ActEffMonTarget.sumMonsterNumber, Game.ActEffMonTarget.isPlayersMonster).isNotAbleToUseItsEffects=true;
        negatedInLastMoment();
        Game.deactivateCurrentEffects();
        Game.deactivateCurrentCPUEffects(); // really needed? (yes, for forgetting current CPU sacrifices)
        if (Game.isBattlePhase()) { // here the code is written such that it doesn't matter whose turn it is
            if (isTributingAFightingMonster) { // here the attack gets cancelled, because one of the two fighting monsters doesn't exist any more (if the attacking monster still exists, it can still attack this turn) [This makes stuff complicated, but I want to allow that, because it makes sense.]
                BattlePhase.endAttack(Game.ActiveAttackingMonster.isExisting);
            }
            else {
                BattlePhase.makeDamageCalculation(Game.ActiveAttackingMonster, Game.ActiveGuardingMonster); // continue attack
            }
        }
        else {
            AIdelegate.cpuContinuesPlayingTurn();
        } // This method is simply not called during the player's main phases. Thus, one does not have to check for these cases
    }
    
    // Effect ID = 30
    // analog effect of number 29 applied on equip cards
    // This is not quite the analog effect, since when it comes to equip cards, the player can sometimes negate in the last moment (when it is an effect from an equip stack) and sometimes just preventively (after the equip card arrived in the stack). Sometimes one is asked both things after another.
    public static void onFieldEffectNeutraliserDuringOpponentsTurnOnEquipCardActivate (EStack Stack, int cardNumberInStack, boolean isNegatingPreventively) {
        Game.playerCurrentlyNegates=true;
        Game.actEffId=30;
        Game.ActEffStack=Stack;
        Game.actEffCardNo=cardNumberInStack;
        Game.isNegatingPreventively=isNegatingPreventively;
        YChooseCardWindow.payCost(2, "Pay the cost for negating effects. (worth 1 card)");
    }
    public static void onFieldEffectNeutraliserDuringOpponentsTurnOnEquipCardExecute() {
        if (Game.isNegatingPreventively) {
            boolean isCanceling = cpuIsUsingEffectNegate();
            if (!isCanceling) {
                negateEquipCard(Game.ActEffStack, Game.actEffCardNo, true, Game.isNegatingPreventively);
                Game.deactivateCurrentCPUEffects(); // really needed? (yes, for forgetting current CPU sacrifices)
                AIdelegate.cpuContinuesPlayingTurn(); // since CPU can only use equip card in own main phase (and this method is only called then), simply let CPU go on
            }
        }
        else {
            negateEquipCard(Game.ActEffStack, Game.actEffCardNo, true, Game.isNegatingPreventively);
            Game.deactivateCurrentCPUEffects(); // really needed? (yes, for forgetting current CPU sacrifices)
            AIdelegate.cpuContinuesPlayingTurn(); // since CPU can only use equip card in own main phase (and this method is only called then), simply let CPU go on
        }
    }
    
    // - attack negates -
    
    // Effect ID = 31
    public static void onFieldEffectAttackStopperActivate (SummonedMonster SumMonster) {
        Game.playerCurrentlyNegates=true;
        // since this is an on field effect, one should in principle be able to negate it, but I leave that out for simplicity (so an attack has to count as an effect)
        Game.actEffId=31;
        Game.ActEffMonTarget=SumMonster;
        YChooseCardWindow.payCost(1, "Pay the cost for negating an attack. (1 equip card)");
    }
    
    public static void negateAttack (SummonedMonster AttackingMonster) {
        // Exhausted Executioner only turns into def, if it actually attacked, not if the attack got negated (That's why it is not happening here.)
        YuGiOhJi.informationDialog("negating attack", "");
        updateAttackNegateStatistic(); // just for statistics/achievements
        Game.deactivateCurrentEffects();
        BattlePhase.endAttack(false);
    }
    
    // in order not to repeat oneself, out-source here, what happens, if one negated and attack (just for statistics/achievements)
    // (by the way, redirecting an attack to the own life points does not count into this statistic)
    public static void updateAttackNegateStatistic() {
        Game.statisticsNumAttackNegates++;
        if (Game.statisticsNumAttackNegates==5) {
            YuGiOhJi.informationDialog("Hidden achievement found: \"Pub Bouncer\". You negated 5 attacks in one game.", ""); Game.unlockTrophy(Game.trophyBouncerUnlocked);
        }
    }
    
    // Effect ID = 32
    public static void onFieldEffectBigAttackStopperActivate (SummonedMonster SumMonster) {
        Game.playerCurrentlyNegates=true;
        // since this is an on field effect, one should in principle be able to negate it, but I leave that out for simplicity (so an attack has to count as an effect)
        Game.actEffId=32;
        Game.ActEffMonTarget=SumMonster;
        YChooseCardWindow.payCost(2, "Pay the cost for negating an attack. (any card)");
    }
    // uses same execution method as the "small" Attack Stopper, see negateAttack(...)
    
    // btw, for the hand trap effect of the Big Attack Stopper, see AIdelegate.playerDiscardsBigAttackStopper();
    
    
    // -- effects of Demon (since I came up with card Demon God - Demon last, its effects are here at the end) -- 
    
    // Keep in mind that only summoned monsters (no equip cards) can remember, if they already used their effect.
    // Thus, Demon can equip from hand and from stack as often as one wants. It uses the usual equipping effects for that.
    
    // (for the equip effects of Demon, see effects 16-18)
    public static void onFieldEffectDemon (SummonedMonster SumMonster) {
        boolean canUseEquipEffect = SumMonster.canUseEquipEffectFromMonster();
        boolean canUseCheatChangeEffect = SumMonster.canUseCheatChangeEffectOfDemon();
        if (canUseEquipEffect && canUseCheatChangeEffect) {
            int intDialogResult = YuGiOhJi.multipleChoiceDialog("Which effect do you want to use?", "Choose effect", new String[]{"equip a monster", "cheat change this card"}, "");
            if (intDialogResult==0) {
                equipEffectFromMonsterActivate(SumMonster);
                //equipEffectDemonFromFieldActivate(SumMonster);
            }
            else if (intDialogResult==1) {
                cheatChangeEffectDemon(SumMonster);
            }
        }
        else if (canUseEquipEffect && !canUseCheatChangeEffect) {
            equipEffectFromMonsterActivate(SumMonster);
            //equipEffectDemonFromFieldActivate(SumMonster);
        }
        else if (!canUseEquipEffect && canUseCheatChangeEffect) {
            cheatChangeEffectDemon(SumMonster);
        }
    }
    
    // Effect ID = 33
    // has become redundant and has been deleted (effect #16 used instead)
    
    // Effect ID = 34
    public static void specialSummonDemonFromStack (EStack Stack, int cardNumberInStack) {
        Game.actEffId=34;
        Game.ActEffStack=Stack;
        Game.actEffCardNo=cardNumberInStack;
        boolean isCanceling = cpuIsUsingEffectNegate();
        if (!isCanceling) {
            boolean isInAttMode = false;
            int intDialogResult = YuGiOhJi.multipleChoiceDialog("Which mode shall the monster be summoned in?", "Special summon this monster?", new String[]{"face up attack mode", "face up defence mode"}, "");
            if (intDialogResult==0) {
                isInAttMode = true;
            }
            else if (intDialogResult==1) {
                isInAttMode = false;
            }
            if (intDialogResult!=-1) {
                boolean isBelongingToPlayer = true;
                int zoneNumber = SummonedMonster.determineFreeMonsterZone(isBelongingToPlayer).sumMonsterNumber;
                // since method setCardToNthSumMonster needs either summoning from hand or GY, can not use it here, thus do it manually
                YCard Card = Stack.getNthCardOfStack(cardNumberInStack);
                SummonedMonster.getNthSummonedMonster(zoneNumber, isBelongingToPlayer).updateMonsterPropertiesWhenSummoning(Card, false, false, isInAttMode);
                SummonedMonster.getNthSummonedMonster(zoneNumber, isBelongingToPlayer).canStillUseOncePerTurnEffect=false;
                if (isInAttMode) {
                    YuGiOhJi.setMonsterButtonIcon(Card.lowMonster.cardPathAtt, zoneNumber, isBelongingToPlayer, isInAttMode, true);
                }
                else {
                    YuGiOhJi.setMonsterButtonIcon(Card.lowMonster.cardPathDef, zoneNumber, isBelongingToPlayer, isInAttMode, true);
                }
                SummonedMonster.getNthSummonedMonster(zoneNumber, isBelongingToPlayer).updateAttDefDisplay();
                Stack.deleteNthCardInEquipStackAndRearrange(cardNumberInStack);
                YuGiOhJi.rescaleEverything();
            }
        }
        Game.deactivateCurrentEffects();
    }
    
    // Effect ID = 35
    public static void cheatChangeEffectDemon (SummonedMonster SumMonster) {
        int intDialogResult = YuGiOhJi.multipleChoiceDialog("Tribute all other Demons you control to cheat mode change this card?", "Are you sure?", new String[]{"yes", "no"}, "no");
        if (intDialogResult==0) {
            Game.actEffId=35;
            Game.ActEffMonSource=SumMonster;
            for (int index = 1; index <= 5; index++){
                SummonedMonster PotentialSacrifice=SummonedMonster.getNthSummonedMonster(index, true);
                if (!SumMonster.isBasicallySameMonster(PotentialSacrifice) && PotentialSacrifice.Monster.equals(Mon.Demon)) { // Only real Demons (no copies) count as possible sacrifices for this effect. Fun fact: The word "tribute" imlies that only summoned monsters (no equipping monsters) are considered. Maybe mention that in the rules?
                    YuGiOhJi.informationDialog("tributing " + PotentialSacrifice.Monster.monsterName, "");
                    PotentialSacrifice.killMonster();
                }
            }
            boolean isCanceling = cpuIsUsingEffectNegate();
            if (!isCanceling) {
                YuGiOhJi.informationDialog("applying cheat mode change", "");
                SumMonster.cheatChangeMode(true);
                if (SumMonster.Monster.equals(Mon.DemonGod) && !Game.trophyFinePrintUnlocked) {YuGiOhJi.informationDialog("Hidden achievement found: \"Master of Fine Print\". You summoned Demon God by effect.", ""); Game.unlockTrophy(Game.trophyFinePrintUnlocked);} // unlock achievement only when used like intended (i.e. used to cheat change Demon, not copied the effect)
            }
            Game.deactivateCurrentEffects();
        }
    }
    
    // I came up with the following effects very late. That's why they are down here.
    
    // Effect ID = 36
    // searching any GOD monster by banishing Holy Lance from hand
    public static void banishSearchHolyLanceActivate (int cardNumberOnHand) {
        // since hand effect, can not be negated
        int intDialogResult = YuGiOhJi.multipleChoiceDialog("Banish this card to search for a GOD card?", "Are you sure?", new String[]{"yes", "no"}, "no");
        if (intDialogResult==0) {
            Game.actEffId=36;
            Hand.banishHandCard(cardNumberOnHand, true);
            SearchWindow.searchDeck(Deck.getDeck(true), false, false, false, true, false);
        }
    }
    
    // in order not to repeat oneself, out-source what happens during a banish search here
    // here the hand card has already been banished and a card with given card ID is being searched from the deck of a given player
    public static void banishSearchExecute (int cardId, boolean isBelongingToPlayer) {
        int cardNumberInDeck = Deck.getDeck(isBelongingToPlayer).getPositionOfCardWithCardIdInDeck(cardId);
        YuGiOhJi.informationDialog("adding card " + Deck.getDeck(isBelongingToPlayer).getNthCardOfDeck(cardNumberInDeck).cardName + " to hand", "Search effect");
        Deck.addNthCardFromDeckToHand(cardNumberInDeck, isBelongingToPlayer);
        revealNthHandCard(Hand.numberOfCardsOnHand(isBelongingToPlayer), isBelongingToPlayer); // reveal card in order to proove that one was allowed to search that card
        Game.deactivateCurrentEffects(); // the analog for the CPU is not needed, because banishing a hand card is not done using AIsacrifice
    }
    
    // Effect ID = 37
    // searching any "Banish", "Bounce" or "Burn" card by banishing Slick Rusher from hand
    public static void banishSearchSlickRusherActivate (int cardNumberOnHand) {
        // since hand effect, can not be negated
        int intDialogResult = YuGiOhJi.multipleChoiceDialog("Banish this card to search for a card with \"Banish\", \"Bounce\" or \"Burn\" in the name?", "Are you sure?", new String[]{"yes", "no"}, "no");
        if (intDialogResult==0) {
            Game.actEffId=37;
            Hand.banishHandCard(cardNumberOnHand, true);
            SearchWindow.searchDeck(Deck.getDeck(true), false, false, false, false, true);
        }
    }
    
    // Effect ID = 38
    // can also be used by CPU
    public static void banisherHandTrapEffectExecute (int cardNumberInGY, boolean isUsedByPlayer) {
        if (Hand.hasCardWithCardIdOnHand(isUsedByPlayer, Card.BigBackBouncerBanisher.cardId)) {
            revealHandCard(Card.BigBackBouncerBanisher.cardId, isUsedByPlayer); // player using an on hand effect (hand traps and searches) should reveal it first (for dramatic effect and stuff)
            int cardNumberOnHand = Hand.getHand(isUsedByPlayer).getPositionOfCardWithCardId(Card.BigBackBouncerBanisher.cardId);
            YCard HandCard = Hand.getHand(isUsedByPlayer).getNthCardOfHand(cardNumberOnHand);
            String message;
            String messageTitle;
            if (isUsedByPlayer) {
                messageTitle = "You";
                message = "discard " + HandCard.cardName + ".";
            }
            else {
                messageTitle = "Computer";
                message = "discards " + HandCard.cardName + ".";
            }
            YuGiOhJi.informationDialog(message, messageTitle); // "you discard/computer discards Big Back Bouncer Banisher"
            Hand.discardCard(cardNumberOnHand, isUsedByPlayer);
            YCard GYcard = Deck.getGY(!isUsedByPlayer).getNthCardOfDeck(cardNumberInGY);
            YuGiOhJi.informationDialog("banishing card " + GYcard.cardName + " from GY, thus negating special summoning from GY", "Effect negation");
            Deck.banishNthCardFromGY(cardNumberInGY, !isUsedByPlayer);
            if (isUsedByPlayer) {
                Game.deactivateCurrentCPUEffects(); // cpu erases memory about current CPU sacrifices
            } // deactivating effects of player not needed here, because already happens at end of all possible effects (in case CPU is using this)
        }
    }
    // If one extended this game by adding different combination of monsters on cards, one would have to add the choice of what card to discard in general, because Banisher might be combined with different monsters on different cards on the hand.
    
    // in order not to repeat oneself, out-source here the testing, if the player can use the negate and showing the dialog
    public static boolean askPlayerToNegateSpecSumFromGY (int cardNumberInGY) {
        if (Hand.hasCardWithCardIdOnHand(true, Card.BigBackBouncerBanisher.cardId)) {
            int intDialogResult = YuGiOhJi.multipleChoiceDialog("Do you want to negate the special summoning of from the graveyard by discarding Banisher?", "You can negate a special summoning.", new String[]{"yes", "no"}, "no");
            if (intDialogResult==0) {
                banisherHandTrapEffectExecute(cardNumberInGY, true);
                negatedInLastMoment();
                return true;
            }
        }
        return false;
    }
    
    // -- end of effects --
    
    // btw: Most analog effects of these optional monster effects are found in the AIEffects class file. Some of the ones here are reused though.
    
    // btw: Attack declaration is not an effect and handled in the Battle Phase class file.
}
