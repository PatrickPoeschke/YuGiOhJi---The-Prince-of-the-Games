package yugiohji;

/**
 * Everything about the computer interrupting the player during the player's turn.
 * This class contains some methods for hand trap interactions
 * and interruptive on field attack/effect negations
 * of the computer during its opponent's turn
 * as well as effect negation in last moment and preventively.
 * 
 * The player has corresponding methods for attack declaration
 * and attack/effect negations in the PlayerInterrupts class file.
 * 
 * Programmer's note:
 * Although this is not the biggest file in the game,
 * nor has it the most lines of code,
 * this (together with the PlayerInterrupts file)
 * is by far the trickiest and conceptually most complicated part of the game!
 * Thus it is also the most error-prone concept of the game.
 * If you code a game, better don't allow actions during the opponent's turn.
 * 
 */

import static yugiohji.YuGiOhJi.Card;
import static yugiohji.YuGiOhJi.Game;
import static yugiohji.YuGiOhJi.HandCPU;
import static yugiohji.YuGiOhJi.Mon;

public class AIinterrupts {
    
    // --- everything about CPU STOPPING EFFECTS ---
    
    // -- everything about the Banisher hand trap effect here --
    
    // returns true, if CPU decided to negate a special summoning effect from GY (CPU has to be able to so of course)
    public static boolean cpuIsUsingBanisherHandTrapNegate() {
        boolean isAbleToNegate = AIEffects.cpuCanUseBanisherHandTrapEffect();
        if (!isAbleToNegate) {
            return false;
        }
        else {
            return cpuMayUseBanisherHandTrapNegate();
        }
    }
    
    // asks the CPU to decide whether to use the hand trap, that can negate a special summoning from the GY in last moment
    // returns true, if the CPU reacts and negates via side effects of this method
    public static boolean cpuMayUseBanisherHandTrapNegate() {
        int rndPercentage = Deck.chooseRandomOption(100); // roll a 100 sided die
        int threshold=0;
        if (Game.isActEffReviveMook()) {
            threshold = Game.CPUbehavior.handTrapBanisherOnMookRevivalProb;
        }
        else if (Game.isActEffReviveMidboss()) {
            threshold = Game.CPUbehavior.handTrapBanisherOnMidbossRevivalProb;
        }
        else if (Game.isActSumEndbossGY()) {
            if (Game.actEffCardId==Card.NeutraliserSkillStealer.cardId) {
                threshold = 100; // if player revives Neutraliser from GY, definitely try to banish it
            }
            else {
                threshold = Game.CPUbehavior.handTrapBanisherOnEndbossRevivalProb;
            }
        }
        if (rndPercentage < threshold) { // threshold % chance to negate the summoning from GY
            AIEffects.cpuBanisherHandTrapEffectExecute(Game.actEffCardNo);
            return true;
        }
        return false;
    }
    
    // -- everything about Neutraliser's effect negation here --
    
    // returns true, if CPU decided to negate the effect (CPU has to be able to so of course)
    public static boolean cpuIsUsingEffectNegate() {
        // test what kind of intervention the CPU can use
        boolean isAbleToInterveneWithHandTrapNeutraliser = Hand.lookForEffectNegateOnHand(false);
        boolean isAbleToInterveneWithOnFieldNeutraliser = Hand.lookForEffectNegateOnField(false);
        // Discard Neutraliser, if possible, in order to prevent the player from using an effect on a valuable monster of the CPU, or an effect negation against any monster of the CPU.
        // (Whatever the player uses on one of the most important monsters of the CPU, it can not be good for the CPU.)
        if (isAbleToInterveneWithHandTrapNeutraliser && (isActiveNegationEffectOnCPUMonster() || cpuWouldGetAValuableMonsterTargettedByAnEffect())) {
            cpuDiscardsNeutraliser();
            return true;
        } // If not possible, use on field effect of Neutraliser, in order to prevent losing the effect of an own monster or getting an effect used against an important monster.
        else if (isAbleToInterveneWithOnFieldNeutraliser && (isActiveNegationEffectOnCPUMonster() || cpuWouldGetAValuableMonsterTargettedByAnEffect())) {
            return cpuUsesOnFieldEffectOfNeutraliserInLastMoment();
        }
        return false;
    }
        
    // CPU discards the card Big Attack Stopper - Sword in order to negate an attack (analog to a similar method of player in this class)
    public static void cpuDiscardsNeutraliser() {
        YMonster.revealHandCard(Card.NeutraliserSkillStealer.cardId, false); // player using an on hand effect (hand traps and searches) should reveal it first (for dramatic effect and stuff)  
        YuGiOhJi.informationDialog("Computer discards " + Card.NeutraliserSkillStealer.cardName + " and negates the effect.", "Effect negation");
        Hand.discardCard(HandCPU.getPositionOfCardWithCardId(Card.NeutraliserSkillStealer.cardId), false);
        cpuNegatesCard();
    }
    
    // CPU wants to use the on field effect of Neutraliser in the last possible moment
    // returns true, if worked (if it actually paid the costs)
    public static boolean cpuUsesOnFieldEffectOfNeutraliserInLastMoment() {
        int posNeutraliser = AIEffects.getPosOfCPUMonsterWithWorkingEffectByMonsterId(Mon.Neutraliser.monsterId);
        if (posNeutraliser!=0) {
            SummonedMonster NegatingMonster = SummonedMonster.getNthSummonedMonster(posNeutraliser, false);
            if (Game.isSwitchingOnOncePerTurnRule) {
                NegatingMonster.canStillUseOncePerTurnEffect=false;
            }
            AIsacrifice.forgetAllSacrifices();
            boolean hasFoundSacrifice = AIsacrifice.tryPreparingSacrificesWorthOneCard(new AIsacrifice(), false);
            boolean isWillingToPayTheCost = AIsacrifice.worthInSemipointsOfAllCurrentSacrifices()==2;
            if (hasFoundSacrifice && isWillingToPayTheCost) {
                AIEffects.cpuPaysCost(AIsacrifice.getNthSacrifice(1), AIsacrifice.getNthSacrifice(2));
                if (!CardOptions.thereAreNegatableCards()) { // in case one tributed all valid targets
                    YuGiOhJi.informationDialog("no more negatable cards", "Nothing happens.");
                }
                else {
                    YuGiOhJi.informationDialog("Computer uses its " + AIEffects.getNumberAsString(NegatingMonster.sumMonsterNumber) + " monster, " + NegatingMonster.Monster.monsterName + ", to negate the effect.", "Negation");
                    cpuNegatesCard();
                    return true;
                }
            }
        }
        return false;
    }
    
    // out-source here the part, where the card of the player gets negated
    public static void cpuNegatesCard() {
        if (Game.actEffId!=0) {
            if (Game.ActEffMonSource.sumMonsterNumber!=0) {
                cpuNegatesMonster(Game.ActEffMonSource);
            }
            else if (Game.ActEffStack.stackNumber!=0) {
                String pronoun;
                if (Game.ActEffStack.isBelongingToPlayer) {pronoun = "your ";}
                else {pronoun = "its ";}
                YuGiOhJi.informationDialog("Computer negates the equip card " + Game.ActEffStack.getNthCardOfStack(Game.actEffCardNo).lowMonster.monsterName + ", in the equip stack of " + pronoun + AIEffects.getNumberAsString(Game.ActEffStack.stackNumber) + " monster.", "Negation");
                YMonster.negateEquipCard(Game.ActEffStack, Game.actEffCardNo, false, false);
            }
        }
    }
    
    // return true, if the player wants to use an effect on a valuable monster of the computer
    public static boolean cpuWouldGetAValuableMonsterTargettedByAnEffect() {
        Game.CPUbehavior.reevaluateStrategies();
        if (Game.ActEffMonTarget.sumMonsterNumber!=0) {
            return Game.ActEffMonTarget.isPlayersMonster;
        }
        return false;
    }
    
    // returns true, if the player uses an negation effect on any monster of the CPU, that can be negated
    // (Consider equipping with Bugged Upgrade only if it is in the last mmoment (i.e. from monster or from equip stack.)
    public static boolean isActiveNegationEffectOnCPUMonster() {
        if (Game.isActEffOptNeutraliser() && !Game.isAboutPlayerActEff) {
            return true; // using optional on field effect of Neutraliser on a monster of the computer
        }
        else if (Game.isActEffEquipFromMonster()) { // equipping a monster of the computer with Bugged Upgrade from monster
            if (Game.ActEffMonSource.Monster.equals(Mon.BuggedUpgrade) && Game.ActEffMonTarget.isPlayersMonster) {
                return true;
            }
        }
        else if (Game.isActEffEquipFromStack()) { // equipping a monster of the computer with Bugged Upgrade from stack
            YCard ConsCard = Game.ActEffStack.getNthCardOfStack(Game.actEffCardNo);
            if (ConsCard.lowMonster.equals(Mon.BuggedUpgrade) && Game.ActEffMonTarget.isPlayersMonster) {
                return true;
            }
        }
        return false;
    }
    
    // - effect negation during battle here -
    
    // returns true, if CPU decided to negate the effect of a monster involved in battle (CPU has to be able to so of course)
    public static boolean cpuIsUsingEffectNegateDuringBattlePhase() {
        if (Hand.lookForEffectNegate(false)) {
            boolean hasWorked = cpuWouldDieFromAttackAndCanPreventThatViaEffectNegation();
            if (hasWorked) {
                return true;
            }
            else {
                return cpuWouldLoseValuableMonsterFromAttackWithSuicidalEffect();
            }
        }
        return false;
    }
    
    // looks, if the CPU would die from an attack and can prevent that from happening by negating the effects of one of the involved monsters
    // also finally tries to pull off the needed negation
    // returns true, if worked
    public static boolean cpuWouldDieFromAttackAndCanPreventThatViaEffectNegation() { // effect negation at beginning of attack
        // if would die, switch on 100% recklessness for the negation alone (reconsider recklessness later)
        Game.isSwitchingOnAdditionalRecklessness=true;
        boolean hasWorked=false;
        if (Game.ActiveAttackingMonster.isExisting) {
            if (Game.ActiveAttackingMonster.isPlayersMonster && Game.ActiveGuardingMonster.isExisting && cpuWouldDieFromAttack() && Game.ActiveAttackingMonster.canBeNegated()) {
                SummonedMonster PlayerMonster = Game.ActiveAttackingMonster;
                SummonedMonster MonsterCPU = Game.ActiveGuardingMonster;
                if (PlayerMonster.hasPiercingDamageAbility() && !MonsterCPU.isInAttackMode) {
                    EStack Stack = EStack.getNthStack(PlayerMonster.sumMonsterNumber, PlayerMonster.isPlayersMonster);
                    if (!Stack.lookForLanceInStack(true)) { // if is not equipped with a non-negated Lance
                        hasWorked = tryToNegateAMonsterSomeWay(PlayerMonster);
                    }
                }
                else if (Hand.lookForEffectNegateOnField(false)) {
                    // One can prevent death by tributing away the attacked monster, thus indirectly ending the attack. (For this cheap trick alone it was a good decision after all to allow negation of non-effect monsters.)
                    if (PlayerMonster.canBeNegated()) { // negate attacking monster (player monster) here (by tributing the guarding monster to Neutraliser)
                        hasWorked = cpuUsesOnFieldEffectOfNeutraliserDuringBattlePhase(PlayerMonster, AIsacrifice.markMonsterAsSacrifice(MonsterCPU));
                    }
                }
            }
            else if (Game.ActiveGuardingMonster.isExisting && Game.ActiveGuardingMonster.isPlayersMonster) {
                SummonedMonster MonsterCPU = Game.ActiveAttackingMonster;
                SummonedMonster PlayerMonster = Game.ActiveGuardingMonster;
                if (PlayerMonster.Monster.equals(Mon.Napalm) && AIbattle.isAbleToDefeatInSimulation(MonsterCPU, PlayerMonster, false, false) && Mon.Napalm.NapalmBurnDamage() >= Game.lifePointsCPU) {
                    hasWorked = tryToNegateAMonsterSomeWay(PlayerMonster);
                }
            }
        }
        Game.reconsiderRecklessness();
        return hasWorked;
    }
    
    // looks, if the CPU would lose an important monster, because of a negatable effect of a monster involved in battle,
    // because opponent's monster is Banisher, Back Bouncer or Suicide Commando
    // also finally tries to pull off the needed negation
    // returns true, if worked
    public static boolean cpuWouldLoseValuableMonsterFromAttackWithSuicidalEffect() {
        Game.CPUbehavior.reevaluateStrategies();
        if (Game.ActiveAttackingMonster.isExisting) {
            if (Game.ActiveAttackingMonster.isPlayersMonster && Game.ActiveGuardingMonster.isExisting) { // here it's the attacking monster with the suicidal removal effect
                SummonedMonster PlayerMonster = Game.ActiveAttackingMonster;
                SummonedMonster MonsterCPU = Game.ActiveGuardingMonster;
                if (PlayerMonster.hasUseableSuicideGetRidOfEffect() && MonsterCPU.AIinfoThreatLv==3 && AIbattle.isAbleToDefeatInSimulation(PlayerMonster, MonsterCPU, false, false)) {
                    return tryToNegateAMonsterSomeWay(PlayerMonster);
                }
            }
            else if (Game.ActiveGuardingMonster.isExisting && Game.ActiveGuardingMonster.isPlayersMonster) { // here it's the guarding monster with the suicidal removal effect
                SummonedMonster MonsterCPU = Game.ActiveAttackingMonster;
                SummonedMonster PlayerMonster = Game.ActiveGuardingMonster;
                if (PlayerMonster.hasUseableSuicideGetRidOfEffect() && MonsterCPU.AIinfoThreatLv==3 && AIbattle.isAbleToDefeatInSimulation(MonsterCPU, PlayerMonster, false, false)) {
                    return tryToNegateAMonsterSomeWay(PlayerMonster);
                }
            }
        }
        return false;
    }
    
    // in order not to repeat oneself, out-source here the part, where the computer wants to negate a monster involved in battle
    // returns true, if worked
    public static boolean tryToNegateAMonsterSomeWay (SummonedMonster MonsterToBeNegated) {
        boolean isAbleToInterveneWithHandTrapNeutraliser = Hand.lookForEffectNegateOnHand(false);
        boolean isAbleToInterveneWithOnFieldNeutraliser = Hand.lookForEffectNegateOnField(false);
        if (isAbleToInterveneWithHandTrapNeutraliser) { // always try to discard Neutraliser first (Is that a good decision?)
            cpuDiscardsNeutraliserDuringBattlePhase(MonsterToBeNegated);
            return true;
        }
        else if (isAbleToInterveneWithOnFieldNeutraliser) { // then try the on field effect
            return cpuUsesOnFieldEffectOfNeutraliserDuringBattlePhase(MonsterToBeNegated, new AIsacrifice());
        }
        return false;
    }
    
    // CPU discards the card Big Attack Stopper - Sword in order to negate an attack (analog to a similar method of player in this class)
    public static void cpuDiscardsNeutraliserDuringBattlePhase (SummonedMonster MonsterToBeNegated) {
        YMonster.revealHandCard(Card.NeutraliserSkillStealer.cardId, false); // player using an on hand effect (hand traps and searches) should reveal it first (for dramatic effect and stuff)  
        YuGiOhJi.informationDialog("Computer discards " + Card.NeutraliserSkillStealer.cardName + " and negates an effect.", "Effect negation");
        Hand.discardCard(HandCPU.getPositionOfCardWithCardId(Card.NeutraliserSkillStealer.cardId), false);
        cpuNegatesMonster(MonsterToBeNegated);
    }
    
    // CPU wants to use the on field effect of Neutraliser in the last possible moment
    // returns true, if worked (if it actually paid the costs)
    public static boolean cpuUsesOnFieldEffectOfNeutraliserDuringBattlePhase (SummonedMonster MonsterToBeNegated, AIsacrifice SacrificeToBeUsedIfValid) {
        int posNeutraliser = AIEffects.getPosOfCPUMonsterWithWorkingEffectByMonsterId(Mon.Neutraliser.monsterId);
        if (posNeutraliser!=0) {
            SummonedMonster NegatingMonster = SummonedMonster.getNthSummonedMonster(posNeutraliser, false);
            if (Game.isSwitchingOnOncePerTurnRule) {
                NegatingMonster.canStillUseOncePerTurnEffect=false;
            }
            AIsacrifice.forgetAllSacrifices();
            boolean hasFoundSacrifice;
            if (SacrificeToBeUsedIfValid.isValidSacrifice()) {
                AIsacrifice.rememberSacrifice(SacrificeToBeUsedIfValid);
                hasFoundSacrifice = true;
            }
            else {
                hasFoundSacrifice = AIsacrifice.tryPreparingSacrificesWorthOneCard(new AIsacrifice(), false);
            }
            boolean isWillingToPayTheCost = AIsacrifice.worthInSemipointsOfAllCurrentSacrifices()==2;
            if (hasFoundSacrifice && isWillingToPayTheCost) {
                AIEffects.cpuPaysCost(AIsacrifice.getNthSacrifice(1), AIsacrifice.getNthSacrifice(2));
                if (!CardOptions.thereAreNegatableCards()) { // in case one tributed all valid targets
                    YuGiOhJi.informationDialog("no more negatable cards", "Nothing happens.");
                }
                else {
                    YuGiOhJi.informationDialog("Computer uses its " + AIEffects.getNumberAsString(NegatingMonster.sumMonsterNumber) + " monster, " + NegatingMonster.Monster.monsterName + ", to negate the effect.", "Negation");
                    cpuNegatesMonster(MonsterToBeNegated);
                    return true;
                }
            }
        }
        return false;
    }
    
    // out-source here the part, where a monster gets negated
    public static void cpuNegatesMonster (SummonedMonster MonsterToBeNegated) {
        if (MonsterToBeNegated.isExisting) {
            String pronoun;
            if (MonsterToBeNegated.isPlayersMonster) {pronoun = "your ";}
            else {pronoun = "its ";}
            YuGiOhJi.informationDialog("Computer negates " + pronoun + AIEffects.getNumberAsString(MonsterToBeNegated.sumMonsterNumber) + " monster, " + MonsterToBeNegated.Monster.monsterName + ".", "Negation");
            MonsterToBeNegated.isNotAbleToUseItsEffects=true;
        }
    }
    
    // -- everything about CPU STOPPING ATTACKS --
    
    // There are 4 ways to stop an attack:
    // 1: by the on field effect of Attack Stopper
    // 2: by the on field effect of Big Attack Stopper
    // 3: by the hand trap effect of Big Attack Stopper
    // 4: by using "My Body As A Shield" rule, if the rule is active
    // This method asks the CPU to decide to intervene.
    // It returns true, if it negates the attack in some way.
    public static boolean cpuIsUsingAttackNegate (SummonedMonster AttackingMonster, boolean isDirectAttack) {
        // test what kind of intervention the CPU can use
        boolean isAbleToInterveneWithHandTrapAttackStop = Hand.lookForAttackNegateOnHand(false);
        boolean isAbleToInterveneWithAttackStopper = AIEffects.cpuCanUseOnFieldEffectAttackStopper();
        boolean isAbleToInterveneWithBigAttackStopper = AIEffects.cpuCanUseOnFieldEffectBigAttackStopper();
        boolean isAbleToInterveneWithOwnBody = isAbleToInterveneWithOwnBody(AttackingMonster, isDirectAttack);
        // The attacks of monsters being immune to all effects (i.e. being effectively Incorruptible) can not be negated.
        // However, one can still "negate" the attack by using the own body as a shield and redirect the attack.
        if (AttackingMonster.isImmune()) { // take away all negation options except for My Body As A Shield
            isAbleToInterveneWithHandTrapAttackStop = false;
            isAbleToInterveneWithAttackStopper = false;
            isAbleToInterveneWithBigAttackStopper = false;
        }
        // Discard Big Attack Stopper, if possible, in order to prevent defeat or losing all monsters.
        if (isAbleToInterveneWithHandTrapAttackStop && (cpuWouldDieOrLoseAllMonstersInBattlePhase() || cpuWouldLoseValuableMonsterFromAttack())) {
            cpuDiscardsBigAttackStopper();
            return true;
        } // If not possible, use on field effect of Attack Stopper, in order to prevent defeat or losing an important monster.
        if (isAbleToInterveneWithAttackStopper && (cpuWouldDieFromAttack() || cpuWouldLoseValuableMonsterFromAttack())) {
            int posAttackStopper = AIEffects.getPosOfCPUMonsterWithWorkingEffectByMonsterId(Mon.AttackStopper.monsterId);
            if (posAttackStopper!=0) {
                SummonedMonster NegatingMonster = SummonedMonster.getNthSummonedMonster(posAttackStopper, false);
                AIsacrifice.forgetAllSacrifices();
                boolean hasFoundSacrifice = AIsacrifice.tryMarkingNEquipCardsOnOwnSideAsSacrifice(1)==1;
                boolean isWillingToPayTheCost = (AIsacrifice.worthInSemipointsOfAllCurrentSacrifices()==1);
                if (hasFoundSacrifice && isWillingToPayTheCost && AIEffects.cpuCanUseOnFieldEffectAttackStopper(NegatingMonster)) {
                    AIEffects.cpuOnFieldEffectAttackStopper(NegatingMonster, AIsacrifice.getNthSacrifice(1));
                    return true;
                }
            }
        } // same with Big Attack Stopper
        if (isAbleToInterveneWithBigAttackStopper && (cpuWouldDieFromAttack() || cpuWouldLoseValuableMonsterFromAttack())) {
            int posBigAttackStopper = AIEffects.getPosOfCPUMonsterWithWorkingEffectByMonsterId(Mon.BigAttackStopper.monsterId);
            if (posBigAttackStopper!=0) {
                SummonedMonster NegatingMonster = SummonedMonster.getNthSummonedMonster(posBigAttackStopper, false);
                AIsacrifice.forgetAllSacrifices();
                boolean hasFoundSacrifice = AIsacrifice.tryPreparingAsSacrificeAnyCardExceptMonsters(new AIsacrifice());
                boolean isWillingToPayTheCost = (AIsacrifice.worthInSemipointsOfAllCurrentSacrifices()==1 || AIsacrifice.worthInSemipointsOfAllCurrentSacrifices()==2);
                if (hasFoundSacrifice && isWillingToPayTheCost && AIEffects.cpuCanUseOnFieldEffectBigAttackStopper(NegatingMonster)) {
                    AIEffects.cpuOnFieldEffectBigAttackStopper(NegatingMonster, AIsacrifice.getNthSacrifice(1));
                    return true;
                }
            }
        } // If everything else fails, try to trade life points for protecting a valuable monster.
        if (isAbleToInterveneWithOwnBody && cpuWouldLoseValuableMonsterFromAttack()) {
            cpuUsesBodyAsShield(Game.ActiveAttackingMonster);
            return true;
        }
        return false;
    }
        
    // CPU discards the card Big Attack Stopper - Sword in order to negate an attack (analog to a similar method of player in this class)
    public static void cpuDiscardsBigAttackStopper() {
        YMonster.revealHandCard(Card.BigAttackStopperSword.cardId, false); // player using an on hand effect (hand traps and searches) should reveal it first (for dramatic effect and stuff)  
        YuGiOhJi.informationDialog("Computer discards " + Card.BigAttackStopperSword.cardName + " and ends battle phase.", "");
        Hand.discardCard(HandCPU.getPositionOfCardWithCardId(Card.BigAttackStopperSword.cardId), false);
        BattlePhase.cancelAttackAndEndBattlePhase(false);
    }
    
    public static boolean isAbleToInterveneWithOwnBody(SummonedMonster AttackingMonster, boolean isDirectAttack) {
        if (!Game.isSwitchingOnBodyAsAShieldRule) {return false;}
        if (isDirectAttack) {return false;}
        return (AttackingMonster.att < Game.lifePointsCPU);
    }
    
    // called, when the computer uses the "My Body As A Shield"-rule
    public static void cpuUsesBodyAsShield (SummonedMonster AttackingMonster) {
        YuGiOhJi.informationDialog("Computer invokes the \"My Body As A Shield\" rule.", "");
        boolean isContinuingGame = BattlePhase.isDealingBattleDamageAndContinuingGame(false, AttackingMonster.att, false);
        if (isContinuingGame) { // The My Body As A Shield rule is not really an attack negation. It just redirects the attack into a direct attack. That mean the Exhausted Executioner still turns into defence mode after that.
            if (AttackingMonster.isTurningIntoDef()) {
                AttackingMonster.passiveEffectExhaustedExecutioner();
            }
            BattlePhase.endAttack(false); // swich off booleans remembering what monster are currently in battle
        }
    }
    
    // simulates battle damage about to happen and retuns true, if the computer would lose the game in this whole battle phase
    // or at least all monsters (if it had any before the attack), if this battle phase is not ended prematurely
    public static boolean cpuWouldDieOrLoseAllMonstersInBattlePhase() {
        Game.BattleSituation = new AIbattle(false, false);
        int battleDamage = 0;
        int monstersCPU = SummonedMonster.countOwnSummonedMonsters(false);
        int monstersPlayer = SummonedMonster.countOwnSummonedMonsters(true);
        boolean[] MonsterCPUDefeated = new boolean[6];
        MonsterCPUDefeated[0]=false;
        for (int index = 1; index <= 5; index++){
            SummonedMonster MonsterCPU = SummonedMonster.getNthSummonedMonster(index, true);
            MonsterCPUDefeated[index] = !MonsterCPU.isExisting;
        }
        if (Game.ActiveAttackingMonster.isPlayersMonster) { // only analyse further, if it is the player who is attacking
            for (int index = 1; index <= 5; index++){
                SummonedMonster PlayerMonster = SummonedMonster.getNthStrongestMonster(index, false);
                boolean hasDefeatedAll = (MonsterCPUDefeated[1] && MonsterCPUDefeated[2] && MonsterCPUDefeated[3] && MonsterCPUDefeated[4] && MonsterCPUDefeated[5]);
                if (index>monstersPlayer && hasDefeatedAll) {
                    battleDamage = battleDamage + AIbattle.simulateAmountOfDealableBattleDamage(PlayerMonster, new SummonedMonster(true, 6), false); // this simulates direct attacks (only possible if player has no invincible monsters for sure, that means also no unknown monsters)
                }
                else {
                    for (int n = 1; n <= 5; n++){ // get the nth strongest monster of the player and simulate the battle damage, if it exists, can bedefeated, and has not been defeated yet
                        SummonedMonster MonsterCPU = SummonedMonster.getNthStrongestMonster(n, true);
                        if (PlayerMonster.isExisting && AIbattle.isAbleToDefeatInSimulation(PlayerMonster, MonsterCPU, false, false) && !MonsterCPUDefeated[PlayerMonster.sumMonsterNumber]) {
                            battleDamage = battleDamage + AIbattle.simulateAmountOfDealableBattleDamage(PlayerMonster, MonsterCPU, false);
                            if (!MonsterCPU.isIndestructibleByBattle()) {
                                MonsterCPUDefeated[MonsterCPU.sumMonsterNumber] = true;
                            }
                        }
                    }
                }
            }
        }
        boolean hasDefeatedAll = (MonsterCPUDefeated[1] && MonsterCPUDefeated[2] && MonsterCPUDefeated[3] && MonsterCPUDefeated[4] && MonsterCPUDefeated[5]);
        return ((monstersCPU>=1 && hasDefeatedAll) || battleDamage >= Game.lifePointsCPU);
    }
    
    // simulates battle damage about to happen and retuns true, if the computer would lose the game, if the attack won't get negated
    public static boolean cpuWouldDieFromAttack() {
        if (Game.ActiveAttackingMonster.isPlayersMonster) {
            if (Game.ActiveGuardingMonster.isExisting) {
                return (simulatingBattleDamageFromAttackingMonster(Game.ActiveAttackingMonster, Game.ActiveGuardingMonster) >= Game.lifePointsCPU);
            }
            else { // this is a direct attack
                return (Game.ActiveAttackingMonster.att >= Game.lifePointsCPU);
            }
        }
        return false;
    }
    
    // simulates battle and retuns true, if the computer would lose a very good monster (threat lv 3), if the attack won't get negated
    public static boolean cpuWouldLoseValuableMonsterFromAttack() {
        Game.CPUbehavior.reevaluateStrategies();
        if (Game.ActiveAttackingMonster.isPlayersMonster && Game.ActiveGuardingMonster.isExisting) {
            SummonedMonster PlayerMonster = Game.ActiveAttackingMonster;
            SummonedMonster MonsterCPU = Game.ActiveGuardingMonster;
            boolean isValuable = MonsterCPU.AIinfoThreatLv==3;
            boolean wouldBeDefeated = (AIbattle.isAbleToDefeatInSimulation(PlayerMonster, MonsterCPU, false, false));
            return (wouldBeDefeated && isValuable);
        }
        return false;
    }
    
    // returns the battle damage that a monster would deal (or receive!) from attacking another monster
    // (assuming neither attack nor effects get negated)
    // in case one attacks a stronger monster, the returned value is negative (i.e. reflected battle damage)
    // This version of the method (from SummonedMonster class file) is only used, when a monster of the CPU is attacked and thus is allowed to use all information about both monsters.
    public static int simulatingBattleDamageFromAttackingMonster (SummonedMonster PlayerMonster, SummonedMonster GuardingMonster) {
        if (GuardingMonster.isInAttackMode || (PlayerMonster.hasPiercingDamageAbility() && !GuardingMonster.isImmune()) ) {
            return (PlayerMonster.att-GuardingMonster.relevantValue());
        }
        else {
            return 0;
        }
    }
    
    
}