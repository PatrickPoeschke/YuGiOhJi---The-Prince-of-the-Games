package yugiohji;

/**
 * Most about the battle phase is here.
 * This class contains all the methods for declaring attacks for player,
 * calculating battle damage, passive effects etc.
 * 
 * The computer uses its own methods for the attack declaration
 * (see file AIbattle).
 * However, all other methods are also used in general for both players,
 * since battle basically always involves both players anyway.
 * 
 */

import static yugiohji.SummonedMonster.getNthSummonedMonster;
import static yugiohji.YuGiOhJi.Mon;
import static yugiohji.YuGiOhJi.Game;

public class BattlePhase {
    
    // --- in first part of this file only method for attack declaration of the player --- (for the analog methods for the CPU see file AIbattle)
    
    // for saving the properties of already defeated monsters (for suicidal effects)
    public static SummonedMonster AttMonster = new SummonedMonster(false, 6);
    public static SummonedMonster GuardMonster = new SummonedMonster(false, 6);
    
    // declares an attack of a summoned monster, if one is allowed to do so
    public static void attemptDeclareAttack (SummonedMonster AttackingMonster){
        // if the monster exists at all, is in attack mode, and has not attacked at this turn (if it's battle phase has already been checked when calling this method)
        if (AttackingMonster.canStillAttackThisBattlePhase()) {
            if (Game.isVeryFirstTurn()) {
                YuGiOhJi.errorDialog("You can not declare an attack during first turn.", "Error.");
            }
            else {
                if (SummonedMonster.countOwnSummonedMonsters(false)==0) { // if CPU has no monsters, one can only attack directly
                    int intDialogResult = YuGiOhJi.multipleChoiceDialog("Attack opponent's life points directly with " + AttackingMonster.att + " attack points?", "", new String[]{"yes", "no"}, "no");
                    if (intDialogResult==0) {
                        Game.ActiveAttackingMonster = AttackingMonster;
                        updateDeclareAttackStatistic();
                        declareDirectAttack(AttackingMonster);
                    }
                }
                else {
                    String piercing = "";
                    if (AttackingMonster.hasPiercingDamageAbility()) {piercing = " (piercing)";}
                    int intDialogResult = YuGiOhJi.multipleChoiceDialog("Attack with " + AttackingMonster.att + " att" + piercing + "? Click on the monster you want to attack.", "", new String[]{"yes", "no"}, "no");
                    if (intDialogResult==0) {
                        Game.ActiveAttackingMonster = AttackingMonster;
                    }
                }
            }
        }
        
    }
    
    // finsihes the declaration of an attack, if one is allowed to do so
    public static void attemptFinishDeclareAttack (SummonedMonster SumMonster){
        if (SumMonster.isExisting && !SumMonster.isPlayersMonster && Game.ActiveAttackingMonster.sumMonsterNumber!=0) { // only do something, if the monster exists at all and one has already chosen the attacking monster
            Game.ActiveGuardingMonster=SumMonster;
            updateDeclareAttackStatistic();
            declareAttackOnMonster(Game.ActiveAttackingMonster, Game.ActiveGuardingMonster);
        }
    }
    
    // declares a direct attack (on opponents life points) by a monster of the player
    // and asks the opponent to intervene, if possible
    // (used by the player - player doesn't get ask to negate the attack: still add this?)
    public static void declareDirectAttack (SummonedMonster AttackingMonster) {
        if (AttackingMonster.isImmune()) { // the attacks of monsters being immune to all effects can not be negated (and for direct attacks one can not invoke the My Body As A Shield rule)
            continueDirectAttack(AttackingMonster);
        }
        else { // Theoretically, one should have the possibility to negate the on-field-effect of an attack negation. But since that will get too complicated, simply change rules.
            boolean isNegatingAttack = AIinterrupts.cpuIsUsingAttackNegate(AttackingMonster, true);
            if (!isNegatingAttack) {
                continueDirectAttack(AttackingMonster);
            }
        }
    }
    
    // in order not to repeat oneself, out-source here what happens, if a direct attack does not get negated (for whatever reason)
    public static void continueDirectAttack (SummonedMonster AttackingMonster) {
        boolean isContinuingGame = isDealingBattleDamageAndContinuingGame(false, AttackingMonster.att, false);
        if (isContinuingGame) {
            if (AttackingMonster.isTurningIntoDef()) {
                AttackingMonster.passiveEffectExhaustedExecutioner();
            }
            endAttack(false); // swich off booleans remembering what monster are currently in battle
        }
    }
    
    // in order not to repeat oneself, outsource here the statistic that the player declared any attack
    public static void updateDeclareAttackStatistic() {
        Game.statisticsNumAttDeclarations++;
    }
    
    // declares an attack by one monster to an opponent's monster
    // and asks the opponent to intervene, if possible
    public static void declareAttackOnMonster (SummonedMonster AttackingMonster, SummonedMonster GuardingMonster) {
        boolean isNegatingAttack = AIinterrupts.cpuIsUsingAttackNegate(AttackingMonster, false);
        if (!isNegatingAttack) {
            damageStep();
        }
    }
    
    // --- everything below this are methods used by both players ---
    
    // lets the attack happen and considers all possible effects of involved monsters
    // Oh boy, at lot of effects can happen before, during or after the attack!
    // One only gets to the damage step, if the attack has finally not been negated (or redirected to opponent's body).
    // That means, no intervention by the CPU can happen here any more, except for negating effects.
    public static void damageStep () {
        // rough plan:
        // #1: flip defending monster, if face down -> see flipMonster(...);
        // #2: Ask CPU and player for possible effect negation! (For an attack negation it is too late!) -> see below in this method
        // #3: calculate damage, kill monsters, if needed
        // (consider also banishing and immunity, also copied passive effect of (Holy) Lance)
        // -> see makeDamageCalculation(...);
        // #4: decrease life points, if needed -> see isDealingBattleDamageAndContinuingGame(...);
        // #5: consider passive (also copied) trap monster effects -> see possibleSuicideEffect(...);
        // #6: display Win/Lose dialog and end match, if needed -> see Game.over(...);
        // #7: finally end the attack (consider the effects that happen afterwards), thus allowing the next attack -> endDamageStep(...);
        
        // about #1:
        if (Game.ActiveGuardingMonster.isFaceDown) {flipMonster(Game.ActiveGuardingMonster);}
        // about #2:
        if (Game.ActiveAttackingMonster.canBeNegated() || Game.ActiveGuardingMonster.canBeNegated()) {
            AIinterrupts.cpuIsUsingEffectNegateDuringBattlePhase(); // ask CPU to negate effects first
            // ask player to negate effect here
            boolean isCanceling = false;
            boolean hasEffectNegateHandTrap = Hand.lookForEffectNegateOnHand(true);
            boolean hasEffectNegateOnField = Hand.lookForEffectNegateOnField(true);
            if (hasEffectNegateHandTrap || hasEffectNegateOnField) {
                if (hasEffectNegateHandTrap && !hasEffectNegateOnField) {
                    isCanceling = PlayerInterrupts.playerMayNegateEffect(Game.ActiveAttackingMonster, Game.ActiveGuardingMonster, true);
                }
                else if (!hasEffectNegateHandTrap && hasEffectNegateOnField) {
                    isCanceling = PlayerInterrupts.playerMayNegateEffect(Game.ActiveAttackingMonster, Game.ActiveGuardingMonster, false);
                }
                else {
                    int intDialogResult = YuGiOhJi.multipleChoiceDialog("Do you want to negate effects on the field?", "You can negate passive effects.", new String[]{"yes, by discarding Neutraliser", "yes, by paying the cost worth 1 card", "no (default)"}, "no (default)");
                    if (intDialogResult==0) {
                        isCanceling = PlayerInterrupts.playerMayNegateEffect(Game.ActiveAttackingMonster, Game.ActiveGuardingMonster, true);
                    }
                    else if (intDialogResult==1) {
                        isCanceling = PlayerInterrupts.playerMayNegateEffect(Game.ActiveAttackingMonster, Game.ActiveGuardingMonster, false);
                    }
                }
            }
            if (!isCanceling) {
                makeDamageCalculation(Game.ActiveAttackingMonster, Game.ActiveGuardingMonster); // whole rest of battle
            }
        }
        else {
            makeDamageCalculation(Game.ActiveAttackingMonster, Game.ActiveGuardingMonster); // whole rest of battle
        }
        
    }
    
    // puts and attacked monster into face up defence mode, if it was face down before
    // (This method can also be used by the CPU.)
    public static void flipMonster (SummonedMonster GuardingMonster) {
        boolean wasUnkownBefore = GuardingMonster.isExistingButUnknown();
        GuardingMonster.isFaceDown=false; // For simplicity, we actually fully flip the monster here and consider the rules as if one is attacking a face up monster. Thus it is as if one can never truely attack a still face down monster. (This basically only means, that Incorruptible becomes immune for this attack already.)
        GuardingMonster.isKnown=true;
        if (GuardingMonster.isPlayersMonster) { // keep in mind that monsters that are flipped while being attacked are always in defence mode before and after the flipping
            YuGiOhJi.setMonsterButtonIcon(GuardingMonster.Monster.cardPathDef, GuardingMonster.sumMonsterNumber, GuardingMonster.isPlayersMonster, false, true);
        }
        else { // monsters of the CPU appear upside down
            if (GuardingMonster.isLowerMonster()) {
                YuGiOhJi.setMonsterButtonIcon(GuardingMonster.Card.upMonster.cardPathDef, GuardingMonster.sumMonsterNumber, GuardingMonster.isPlayersMonster, false, true);
            }
            else {
                YuGiOhJi.setMonsterButtonIcon(GuardingMonster.Card.lowMonster.cardPathDef, GuardingMonster.sumMonsterNumber, GuardingMonster.isPlayersMonster, false, true);
            }
        }
        GuardingMonster.updateAttDefDisplay();
        GuardingMonster.isModeChangeableThisTurn=false;
        if (wasUnkownBefore && GuardingMonster.isPlayersMonster) {
            Game.CPUbehavior.reevaluateStrategies();
        }
    }
    
    // most of the whole attack with all possible side effects happening here
    // (If all its sub-methods are written correctly, this method can also be used by the CPU)
    public static void makeDamageCalculation (SummonedMonster AttackingMonster, SummonedMonster GuardingMonster) {
        AttackingMonster.canStillAttackThisTurn = false; // Just in case this might be missed somewhere, erase its one attack per turn, because now there is no turning back.
        AttMonster.rememberPropertiesOfMonster(AttackingMonster);
        GuardMonster.rememberPropertiesOfMonster(GuardingMonster);
        // get the 2 relevant values for the attack calculation
        int relevantAttValue = AttackingMonster.att;
        int releventGuardingValue = GuardingMonster.relevantValue();
        // get some relevant properties of the involved monsters
        boolean attackingMonsterIndestructible = AttackingMonster.isIndestructibleByBattle();
        boolean attackingMonsterImmuneWhileWinning = AttackingMonster.isImmuneWhileWinning();
        boolean attackingMonsterImmune = AttackingMonster.isImmune();
        boolean attackingMonsterBanishes = AttackingMonster.isBanishingOpposingMonster(GuardingMonster);
        boolean attackingMonsterBanishesBoth = AttackingMonster.isBanishingBothMonsters(GuardingMonster);
        
        boolean guardingMonsterIndestructible = GuardingMonster.isIndestructibleByBattle();
        boolean guardingMonsterImmuneWhileWinning = GuardingMonster.isImmuneWhileWinning();
        boolean guardingMonsterImmune = GuardingMonster.isImmune();
        boolean guardingMonsterBanishes = GuardingMonster.isBanishingOpposingMonster(AttackingMonster);
        boolean guardingMonsterBanishesBoth = GuardingMonster.isBanishingBothMonsters(AttackingMonster);
        // let monsters clash here
        if (relevantAttValue == releventGuardingValue) {
            if (relevantAttValue==0) {
                YuGiOhJi.informationDialog("Nothing happens, because of zero attack.", "");
                endAttack(false);
            }
            else {
                if (GuardingMonster.isInAttackMode) { // usually both monsters kill each other
                    if (attackingMonsterBanishesBoth) {
                        banishingDialogBanisher(AttackingMonster);
                        getNthSummonedMonster(AttackingMonster.sumMonsterNumber, AttackingMonster.isPlayersMonster).banishMonster();
                        if (!guardingMonsterImmuneWhileWinning && !guardingMonsterImmune) {
                            banishingDialogBanisher(GuardingMonster);
                            getNthSummonedMonster(GuardingMonster.sumMonsterNumber, GuardingMonster.isPlayersMonster).banishMonster();
                        }
                        else {
                            getNthSummonedMonster(GuardingMonster.sumMonsterNumber, GuardingMonster.isPlayersMonster).killMonster();
                        }
                    }
                    else if (guardingMonsterBanishesBoth) {
                        banishingDialogBanisher(GuardingMonster);
                        getNthSummonedMonster(GuardingMonster.sumMonsterNumber, GuardingMonster.isPlayersMonster).banishMonster();
                        if (!attackingMonsterImmuneWhileWinning && !attackingMonsterImmune) {
                            banishingDialogBanisher(AttackingMonster);
                            getNthSummonedMonster(AttackingMonster.sumMonsterNumber, AttackingMonster.isPlayersMonster).banishMonster();
                        }
                        else {
                            getNthSummonedMonster(AttackingMonster.sumMonsterNumber, AttackingMonster.isPlayersMonster).killMonster();
                        }
                    }
                    else {
                        if (!guardingMonsterIndestructible) {
                            if (attackingMonsterBanishes && !guardingMonsterImmuneWhileWinning && !guardingMonsterImmune) {
                                banishingDialogBigBanisher(GuardingMonster);
                                getNthSummonedMonster(GuardingMonster.sumMonsterNumber, GuardingMonster.isPlayersMonster).banishMonster();
                            }
                            else {
                                getNthSummonedMonster(GuardingMonster.sumMonsterNumber, GuardingMonster.isPlayersMonster).killMonster();
                            }
                        }
                        if (!attackingMonsterIndestructible) {
                            if (guardingMonsterBanishes && !attackingMonsterImmuneWhileWinning && !attackingMonsterImmune) {
                                banishingDialogBigBanisher(AttackingMonster);
                                getNthSummonedMonster(AttackingMonster.sumMonsterNumber, AttackingMonster.isPlayersMonster).banishMonster();
                            }
                            else {
                                getNthSummonedMonster(AttackingMonster.sumMonsterNumber, AttackingMonster.isPlayersMonster).killMonster();
                            }
                        }
                    }
                    if (!guardingMonsterIndestructible && attackingMonsterIndestructible) {
                        possibleSuicideEffect(false, false);
                    }
                    else if (guardingMonsterIndestructible && !attackingMonsterIndestructible) {
                        possibleSuicideEffect(true, false); // suicide effect of attacking monster
                    }
                    else if (!guardingMonsterIndestructible && !attackingMonsterIndestructible) {
                        possibleSuicideEffect(false, true); // double kill here
                    }
                    else { // if both are indestructible, none of them has a suicide effect
                        endDamageStep(false); // here both survive
                    }
                } // else nothing happens
            }
        }
        else if (relevantAttValue > releventGuardingValue) { // here the attacking monster usually kills the guarding one
            // look if attacking monster is either piercing by itself or copies Lance or Holy Lance
            boolean isEffectivelyPiercing = (AttackingMonster.hasPiercingDamageAbility() && !guardingMonsterImmune);
            boolean isContinuingGame = true;
            if (GuardingMonster.isInAttackMode || isEffectivelyPiercing) {
                isContinuingGame = isDealingBattleDamageAndContinuingGame(false, relevantAttValue-releventGuardingValue, GuardingMonster.isPlayersMonster);
            } // else no damage
            if (isContinuingGame) {
                if (guardingMonsterBanishesBoth) { // definitely banishes itself
                    banishingDialogBanisher(GuardingMonster);
                    getNthSummonedMonster(GuardingMonster.sumMonsterNumber, GuardingMonster.isPlayersMonster).banishMonster();
                    if (!attackingMonsterImmuneWhileWinning && !attackingMonsterImmune) { // banishes opponent only, if it not immune against it
                        banishingDialogBanisher(AttackingMonster);
                        getNthSummonedMonster(AttackingMonster.sumMonsterNumber, AttackingMonster.isPlayersMonster).banishMonster();
                    }
                }
                else if (!guardingMonsterIndestructible) {
                    if (attackingMonsterBanishes && !guardingMonsterImmune) {
                        banishingDialogBigBanisher(GuardingMonster);
                        getNthSummonedMonster(GuardingMonster.sumMonsterNumber, GuardingMonster.isPlayersMonster).banishMonster();
                    }
                    else {
                        getNthSummonedMonster(GuardingMonster.sumMonsterNumber, GuardingMonster.isPlayersMonster).killMonster();
                    }
                    possibleSuicideEffect(false, false);
                }
                else {
                    endDamageStep(false);
                }
            }
        }
        else { // here: relevantAttValue < releventGuardingValue
            // rare case in which attacker gets the damage
            boolean isContinuingGame = isDealingBattleDamageAndContinuingGame(false, releventGuardingValue-relevantAttValue, AttackingMonster.isPlayersMonster);
            if (isContinuingGame) {
                if (GuardingMonster.isInAttackMode) {
                    if (attackingMonsterBanishesBoth) {
                        banishingDialogBanisher(AttackingMonster);
                        getNthSummonedMonster(AttackingMonster.sumMonsterNumber, AttackingMonster.isPlayersMonster).banishMonster();
                        if (!guardingMonsterImmuneWhileWinning && !guardingMonsterImmune) {
                            banishingDialogBanisher(GuardingMonster);
                            getNthSummonedMonster(GuardingMonster.sumMonsterNumber, GuardingMonster.isPlayersMonster).banishMonster();
                        }
                    }
                    else if (!attackingMonsterIndestructible) {
                        if (guardingMonsterBanishes && !attackingMonsterImmune) {
                            banishingDialogBigBanisher(AttackingMonster);
                            getNthSummonedMonster(AttackingMonster.sumMonsterNumber, AttackingMonster.isPlayersMonster).banishMonster();
                        }
                        else {
                            getNthSummonedMonster(AttackingMonster.sumMonsterNumber, AttackingMonster.isPlayersMonster).killMonster();
                        }
                    }
                    possibleSuicideEffect(true, false);
                }
                else {
                    endDamageStep(false);
                }
            }
        }
        
    }
    
    // displays the dialog that a given monster is banished due to the effects of Banisher (in order to repeat oneself less, out-source dialog here)
    public static void banishingDialogBanisher (SummonedMonster BanishedMonster) {
        YuGiOhJi.informationDialog("Banishing " + BanishedMonster.Monster.monsterName + " due to the effect of " + YuGiOhJi.Banisher.monsterName, "Passive effect");
    }
    
    // displays the dialog that a given monster is banished due to the effects of Big Banisher (in order to repeat oneself less, out-source dialog here)
    public static void banishingDialogBigBanisher (SummonedMonster BanishedMonster) {
        YuGiOhJi.informationDialog("Banishing " + BanishedMonster.Monster.monsterName + " due to the effect of " + YuGiOhJi.BigBanisher.monsterName, "Passive effect");
    }
    
    // deals a given amount of battle damage to a given player and returns true, if the game goes on
    public static boolean isDealingBattleDamageAndContinuingGame (boolean isBurnDamage, int battleDamage, boolean isHittingPlayer) {
        String damageKind = " battle";
        if (isBurnDamage) {damageKind = " burn";}
        if (isHittingPlayer) {
            YuGiOhJi.informationDialog("You take " + battleDamage + damageKind + " damage.", "");
            if (battleDamage >= Game.lifePointsPlayer) {
                Game.lifePointsPlayer=0;
                YuGiOhJi.informationDialog("The opponent reduced your life points to zero.", "YOU LOSE!");
                Game.over(false);
                return false; // GAME OVER
            }
            else {Game.lifePointsPlayer = Game.lifePointsPlayer-battleDamage;
                YuGiOhJi.updateDisplayedLP();
                return true; // game continues
            }
        }
        else {
            YuGiOhJi.informationDialog("Computer takes " + battleDamage + damageKind + " damage.", "");
            if (battleDamage >= Game.lifePointsCPU) {
                Game.lifePointsCPU=0;
                YuGiOhJi.informationDialog("You reduced the Opponent's life points to zero.", "YOU WIN!");
                // display some hidden achievements here, if unlocked
                if (battleDamage>=Game.initialLP) {YuGiOhJi.informationDialog("Hidden achievement found: \"One Shot\". You have won by one single attack or burn effect.", ""); Game.unlockTrophy(Game.trophyOneUnlocked);}
                // end of listing of achievements
                Game.over(true);
                return false; // GAME OVER
            }
            else {Game.lifePointsCPU = Game.lifePointsCPU-battleDamage;
                YuGiOhJi.updateDisplayedLP();
                return true; // game continues
            }
        }
    }
    
    // deals a given amount of burn damage to both players at the same time and returns true, if the game goes on
    // fun fact: This is the only way in the game to result in a draw. (can only happen when 2 Napalm kill each other)
    public static boolean isDealingBurnDamageToBothPlayersAndContinuingGame (int burnDamage) {
        boolean isDefeaingPlayer = (burnDamage >= Game.lifePointsPlayer);
        boolean isDefeatingCPU = (burnDamage >= Game.lifePointsCPU);
        if (isDefeaingPlayer && isDefeatingCPU) {
            Game.lifePointsPlayer=0;
            Game.lifePointsCPU=0;
            YuGiOhJi.informationDialog("Both players take " + burnDamage + " burn damage.", "");
            YuGiOhJi.informationDialog("The life points of both players have been reduced to zero.", "DRAW!");
            // display some hidden achievements here, if unlocked
            YuGiOhJi.informationDialog("Hidden achievement found: \"Worthy Adversary\". You let a game result in a draw.", ""); Game.unlockTrophy(Game.trophyAdversaryUnlocked);
            // end of listing of achievements
            Game.over();
            return false; // GAME OVER
        }
        else if (isDefeaingPlayer && !isDefeatingCPU) {
            isDealingBattleDamageAndContinuingGame(true, burnDamage, false); // GAME OVER already happens inside the method
            return false; // GAME OVER
        }
        else if (!isDefeaingPlayer && isDefeatingCPU) {
            isDealingBattleDamageAndContinuingGame(true, burnDamage, true); // GAME OVER already happens inside the method
            return false; // GAME OVER
        }
        else {
            YuGiOhJi.informationDialog("Both players take " + burnDamage + " burn damage.", "");
            Game.lifePointsPlayer = Game.lifePointsPlayer-burnDamage;
            Game.lifePointsCPU = Game.lifePointsCPU-burnDamage;
            YuGiOhJi.updateDisplayedLP();
            return true; // game continues
        }
    }
    
    // check and execute a trap effect of a monster that triggers when it gets destroyed
    // if you land here, at least one monster has been destroyed in battle
    // in case of double kill: both monsters actually get destroyed (i.e. none of them is invincible)
    public static void possibleSuicideEffect (boolean isInterchangingMonsters, boolean isDoubleKill) {
        SummonedMonster DyingMonster;
        SummonedMonster OpposingMonster;
        if (isInterchangingMonsters) {
            DyingMonster = AttMonster;
            OpposingMonster = GuardMonster;
        }
        else {
            DyingMonster = GuardMonster;
            OpposingMonster = AttMonster;
        }
        int effectiveMonsterId = DyingMonster.relevantEffectiveSuicidalMonsterId();
        int effectiveMonsterIdOpponent = OpposingMonster.relevantEffectiveSuicidalMonsterId();
        boolean isContinuingGame = true;
        if (YMonster.getMonsterById(effectiveMonsterId).isBurning) { // effect of Napalm
            if (isDoubleKill) {
                if (YMonster.getMonsterById(effectiveMonsterIdOpponent).isBurning) {
                    isContinuingGame = isDealingBurnDamageToBothPlayersAndContinuingGame(Mon.Napalm.NapalmBurnDamage());
                }
                else {
                    isContinuingGame = YMonster.dealBurnDamage(Mon.Napalm.NapalmBurnDamage(), OpposingMonster.isPlayersMonster);
                }
                if (isContinuingGame) {endDamageStep(false);}
            } // other suicide effects don't apply, since they don't have a valid target any more
            else {
                isContinuingGame = YMonster.dealBurnDamage(Mon.Napalm.NapalmBurnDamage(), OpposingMonster.isPlayersMonster);
                if (isContinuingGame) {endDamageStep(true);}
            }
        }
        else if (YMonster.getMonsterById(effectiveMonsterId).isDestroying || YMonster.getMonsterById(effectiveMonsterId).isGettingAnotherToHand) {
            boolean opposingMonsterIndestructible = OpposingMonster.isIndestructibleByEffect();
            boolean opposingMonsterImmuneWhileWinning = OpposingMonster.isImmuneWhileWinning();
            boolean opposingMonsterImmune = OpposingMonster.isImmune();
            boolean opposingMonsterStillExisting = getNthSummonedMonster(OpposingMonster.sumMonsterNumber, OpposingMonster.isPlayersMonster).isExisting;
            if (YMonster.getMonsterById(effectiveMonsterId).isDestroying) { // effect of Suicide Commando
                if (opposingMonsterStillExisting && !opposingMonsterIndestructible && !opposingMonsterImmuneWhileWinning && !opposingMonsterImmune) {
                    YuGiOhJi.informationDialog(OpposingMonster.Monster.monsterName + " is killed due to the effect of " + YuGiOhJi.SuicideCommando.monsterName + ".", "Passive effect");
                    getNthSummonedMonster(OpposingMonster.sumMonsterNumber, OpposingMonster.isPlayersMonster).killMonster();
                    if (YMonster.getMonsterById(effectiveMonsterIdOpponent).isBurning) { // this part here already contains the case of double kill
                        isContinuingGame = YMonster.dealBurnDamage(Mon.Napalm.NapalmBurnDamage(), DyingMonster.isPlayersMonster);
                    } // other suicide effects don't apply, since they don't have a valid target any more
                    if (isContinuingGame) {
                        endAttack(false);
                    }
                }
                else {
                    if (isDoubleKill) {endAttack(false);}
                    else {endDamageStep(true);}
                }
            }
            else if (YMonster.getMonsterById(effectiveMonsterId).isGettingAnotherToHand) { // effect of Back Bouncer (check that one last)
                // only do something, if Back Bouncer got destroyed and opposing monster is still there and not immune
                // (that means nothing happens here in case of double kill)
                if (opposingMonsterStillExisting && !opposingMonsterImmuneWhileWinning && !opposingMonsterImmune && !isDoubleKill) { // at max. one monster gets bounced back
                    YMonster.effectBackBouncerActivate(getNthSummonedMonster(OpposingMonster.sumMonsterNumber, OpposingMonster.isPlayersMonster));
                    // still add endAttack(false);? No. The typical ending of the attack happens at the end of YMonster.effectBackBouncerExecute().
                }
                else {
                    if (isDoubleKill) {endAttack(false);}
                    else {endDamageStep(true);}
                }
            }
        }
        else {
            if (isDoubleKill) {endAttack(false);}
            else {endDamageStep(true);}
        }
        
    }
    
    // end attack and apply effects that happen after the full attack and trap monster effects have happend (i.e. passive effects of Exhausted Executioner, Steep Learning Curve etc.)
    // also take away the free mode change per turn for monsters that attacked and still exist
    // (This method and everything it calls can also be used by the CPU.)
    public static void endDamageStep (boolean hasDefeatedOpponent) {
        // Exhausted Executioner effect (turn into defence mode)
        if (Game.ActiveAttackingMonster.isTurningIntoDef()) {
            Game.ActiveAttackingMonster.passiveEffectExhaustedExecutioner();
        }
        // delete free mode change per turn
        Game.ActiveAttackingMonster.isModeChangeableThisTurn=false;
        // gain attack due to fighting experience
        if (hasDefeatedOpponent && Game.ActiveAttackingMonster.isGainingExperience()) {
            // only count number of defeated monster while the Steep Learning Curve effect is active
            getNthSummonedMonster(Game.ActiveAttackingMonster.sumMonsterNumber, Game.ActiveAttackingMonster.isPlayersMonster).numberOfDefeatedMonsters++;
            Game.ActiveAttackingMonster.att = Game.ActiveAttackingMonster.att + Mon.SteepLearningCurve.SteepLearningCurveAttBoost();
            Game.ActiveAttackingMonster.updateAttDefDisplay();
        }
        endAttack(false); // swich off booleans remembering what monsters are currently in battle
    }
    
    // called, if the battle phase is endet by the discarding effect of Big Attack Stopper
    public static void cancelAttackAndEndBattlePhase (boolean isUsedByPlayer) {
        if (isUsedByPlayer) {
            playerEffectivelyEndsBattlePhase();
        }
        else {
            cpuEffectivelyEndsBattlePhase();
        }
    }
    
    // If the computer discards Big Attack Stopper, it happens exactly what is written on the card.
    public static void cpuEffectivelyEndsBattlePhase() {
        endAttack(false);
        Game.proceedToNextPhase();
    }
    
    // Out-source here what happens, when the player discards Big Attack Stopper:
    // The idea is, that it is a lot safer to just take the residual attacks the monsters possess away,
    // so that the computer will have to end the battle phase itself.
    // This is not exactly what is written on the card, but it effectively achieves this without causing major bugs.
    public static void playerEffectivelyEndsBattlePhase() {
        for (int index = 1; index <= 5; index++){
            SummonedMonster MonsterCPU = SummonedMonster.getNthSummonedMonster(index, false);
            if (MonsterCPU.isExisting) {
                MonsterCPU.canStillAttackThisTurn = false;
            }
        }
        endAttack(false);
    }
    
    // called, if attack has been negated or is simply finished
    // use the option that the monster can still attack, if its target got tributed before attack could finally happen
    public static void endAttack (boolean isStillAbleToAttack) {
        if (Game.ActiveAttackingMonster.isExisting) {
            SummonedMonster SumMonster = getNthSummonedMonster(Game.ActiveAttackingMonster.sumMonsterNumber, Game.ActiveAttackingMonster.isPlayersMonster);
            SumMonster.canStillAttackThisTurn=isStillAbleToAttack;
            if (!isStillAbleToAttack && SumMonster.Monster.equals(Mon.MonsterStealer)) {
                SumMonster.canStillUseOncePerTurnEffect=false; // if Monster Stealer attacked, it can not use its effect for the rest of the turn, due to its own effect
            }
            Game.ActiveAttackingMonster = new SummonedMonster(true, 0);
            Game.ActiveGuardingMonster = new SummonedMonster(false, 0);
        }
        if (!Game.isPlayersTurn) {
            AIdelegate.cpuContinuesPlayingTurn();
        }
    }
    
    
}
