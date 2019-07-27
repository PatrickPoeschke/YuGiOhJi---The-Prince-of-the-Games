package yugiohji;

/**
 * Since different YuGiOhJi-CPU-behaviors might partially use the same strategies, put them here.
 * A strategy is using whatever means are available to play in a specific style
 * trying to achieve a certain goal.
 * 
 * The computer might try to set a "trap monster" face down.
 * (These are the ones that have passive effects that trigger, when being attacked/destroyed)
 * 
 * Another strategy is to try to special summon monsters with high defence value,
 * and equipping them with Shield in order to increase their defence.
 * 
 * The computer might also just summon many monsters with high attack value and "overrun" you,
 * by attack all/most of your monsters,
 * or just summon monsters and use their effects to get rid of your monsters it can not overrun.
 * "Outsmart, if you can not outgun."
 * 
 * There are many more possible strategies.
 * 
 */

import static yugiohji.YuGiOhJi.Card;
import static yugiohji.YuGiOhJi.Game;
import static yugiohji.YuGiOhJi.GYDeckCPU;
import static yugiohji.YuGiOhJi.GYDeckPlayer;
import static yugiohji.YuGiOhJi.HandCPU;
import static yugiohji.YuGiOhJi.Mon;
import static yugiohji.AIsacrifice.cpuCanPayOneUselessCard;
import static yugiohji.AIsacrifice.cpuCanPayTwoUselessCards;
import static yugiohji.AIsacrifice.countPayableSemipointsWithHandAndGYAlone;
import static yugiohji.AIsacrifice.forgetAllSacrifices;
import static yugiohji.AIsacrifice.getNthSacrifice;
import static yugiohji.AIsacrifice.markGYCardAsSacrificeByCardNumber;
import static yugiohji.AIsacrifice.rememberSacrifice;
import static yugiohji.AIsacrifice.tryMarkingNEquipCardsOnOwnSideAsSacrifice;
import static yugiohji.AIsacrifice.tryPreparingAsSacrificeAnyCardExceptMonsters;
import static yugiohji.AIsacrifice.tryPreparingSacrificeWorthOneHalfCard;
import static yugiohji.AIsacrifice.tryPreparingSacrificesWorthOneCard;
import static yugiohji.AIsacrifice.tryPreparingSacrificesWorthTwoCards;

public class AIstrategies {
    
    // All strategies beginning with the word "try" are supposed to
    // return true, if they "succeeded" with their strategy, and return false, if they utterly failed.
    
    // -- general methods for dealing with arrays --
    
    // returns how often a given integer value appears in a given integer array
    public static int numberOfTimesOfValueInArray (int value, Integer[] integerArray) {
        int counter = 0;
        int maxLength = integerArray.length; // number of elements
        if (maxLength>0) {
            for (int index = 1; index <= maxLength; index++){
                if (integerArray[index-1]==value) {
                    counter++;
                }
            }
        }
        return counter;
    }
    
    // returns the (1st occuring) position of a given integer value in a given integer array
    // If no such value exists, returns zero. (positions are counted beginning with one)
    public static int getIndexOfValueInArray (int value, Integer[] integerArray) {
        return getNthIndexOfValueInArray(1, value, integerArray);
    }
    
    // returns the nth occuring position of a given integer value in a given integer array
    // If no such value exists, returns zero. (positions are counted beginning with one)
    public static int getNthIndexOfValueInArray (int n, int value, Integer[] integerArray) {
        int counter = 0;
        int maxLength = integerArray.length; // number of elements
        if (maxLength>0) {
            for (int index = 1; index <= maxLength; index++){
                if (integerArray[index-1]==value) {
                    counter++;
                    if (counter==n) {return index;}
                }
            }
        }
        return 0;
    }
    
    // returns an integer array containing the monster IDs of the summoned monsters of the CPU
    public static Integer[] getArrayOfCPUMonsterIds() {
        Integer[] monterIds = new Integer[5];
        for (int index = 1; index <= 5; index++){
            SummonedMonster SumMonster = SummonedMonster.getNthSummonedMonster(index, false);
            if (SumMonster.isExisting) {
                monterIds[index-1] = SumMonster.Monster.monsterId;
            }
            else {
                monterIds[index-1] = 0;
            }
        }
        return monterIds;
    }
    
    
    // --- mostly DEFENSIVE STRATEGIES ---
    
    // consists mostly of searching & setting MOOKs, summoning some MIDBOSSES in def and the Big Attack Stopper in att
    
    // Goal of this strategy: If its the very 1st turn (one is not allowed to attack yet!) and the CPU just starts with 3 or less cards
    // then special summon a MIDBOSS or at least try to set the MOOK with the highest def on hand.
    // The reason for the existence of this strategy is, that the usual strategies that are tried first in a turn,
    // might often result into the CPU doing nothing, when starting with too few cards. That would be fatal. That's why this method should prevent this.
    // returns true, if strategy worked
    public static boolean exceptionalTopDeckStrategy() {
        if (Game.isMainPhase() && Game.isAllowingCPUToPlay() && Game.isVeryFirstTurn() && HandCPU.numberOfCards <= 3) {
            if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try summoning/setting something");}
            int numberOfHandCards = HandCPU.numberOfCards;
            if (numberOfHandCards == 3 || numberOfHandCards == 2) {
                boolean isWorking = trySpecSumMidboss(true, Mon.Incorruptible.monsterId, false);
                if (isWorking) {
                    return true;
                }
                else if (Game.isMainPhase() && Game.isAllowingCPUToPlay()) {
                    return trySpecSumMidboss(true, Mon.BigBanisher.monsterId, false);
                }
            }
            return trySettingAnyMook(); // If nothing happened until here, well then tough luck for the CPU.
        }
        return false;
    }
    
    // Goal of this strategy: use search effect of Holy Lance to search out card God - Barrier
    // returns true, if strategy worked (If it worked, one can set barrier later on. This makes these strategies more consistent)
    public static boolean trySearchingBarrierWithHolyLance() {
        if (Game.isMainPhase() && Game.isAllowingCPUToPlay() && AIEffects.canBanishSearchHolyLanceActivate(Card.GodBarrier.cardId, false)) {
            AIEffects.cpuBanishSearchHolyLanceActivate(Card.GodBarrier.cardId); return true;
        }
        return false;
    }
    
    // Goal of this strategy: use search effect of Slick Rusher to search out hand trap Banisher
    // returns true, if strategy worked (If it worked, one can negate summonings from GY.)
    public static boolean trySearchingBanisherWithRusher() {
        if (Game.isMainPhase() && Game.isAllowingCPUToPlay() && AIEffects.canBanishSearchHolyLanceActivate(Card.BigBackBouncerBanisher.cardId, false)) {
            AIEffects.cpuBanishSearchHolyLanceActivate(Card.BigBackBouncerBanisher.cardId); return true;
        }
        return false;
    }
    
    // Goal of this strategy: searching out a card with a given card ID by whatever banish search effect is able to do so
    // returns true, if strategy worked
    public static boolean tryBanishSearchingCardWithCardId (int cardId) {
        if (Game.isMainPhase() && Game.isAllowingCPUToPlay()) {
            if (Card.GodBarrier.cardId==cardId || Card.DemonGodDemon.cardId==cardId) {
                if (AIEffects.canBanishSearchHolyLanceActivate(cardId, false)) {
                    AIEffects.cpuBanishSearchHolyLanceActivate(cardId); return true;
                }
                return false;
            }
            else if (Card.NecromancerBackBouncer.cardId==cardId || Card.BigBackBouncerBanisher.cardId==cardId || Card.BigBanisherBurner.cardId==cardId || Card.BigBurnerSuicideCommando.cardId==cardId) {
                if (AIEffects.canBanishSearchHolyLanceActivate(cardId, false)) {
                    AIEffects.cpuBanishSearchHolyLanceActivate(cardId); return true;
                }
                return false;
            }
        }
        return false;
    }
    
    // Goal of this strategy: set monsters that have passive suicide effects or can stall
    // returns true, if strategy worked
    public static boolean trySettingAStallOrTrapMonster() {
        if (Game.isMainPhase() && Game.isAllowingCPUToPlay() && AIEffects.cpuCanNormalSummonMook()) {
            int cardNoToBePlayed = cpuChooseMookCardForSetting();
            if (cardNoToBePlayed==0 || cardNoToBePlayed>10){
                return false;
            }
            else {
                AIEffects.cpuNormalSetsMookWithCardNo(cardNoToBePlayed);
                return true;
            }
        }
        return false;
    }
    
    // asks the computer to choose a MOOK card for setting it
    public static int cpuChooseMookCardForSetting () {
        // try to first best staller than mean "flip effect" monsters
        int cardNo = HandCPU.getPositionOfCardWithCardId(Card.GodBarrier.cardId); // try to stall with Barrier
        if (cardNo!=0) {return cardNo;} // if that didn't work, try to interrupt with suicidal MOOKS
        else {cardNo = HandCPU.getPositionOfCardWithCardId(Card.BigBurnerSuicideCommando.cardId);}
        if (cardNo!=0) {return cardNo;}
        else {cardNo = HandCPU.getPositionOfCardWithCardId(Card.NecromancerBackBouncer.cardId);}
        if (cardNo!=0) {return cardNo;}
        else {cardNo = HandCPU.getPositionOfCardWithCardId(Card.EradicatorObstacle.cardId);} // try to stall with Obstacle (bettter than nothing)
        if (cardNo!=0) {return cardNo;}
        else {cardNo = HandCPU.getPositionOfCardWithCardId(Card.DemonGodDemon.cardId);} // make use of psychological effect a set Demon (bettter than nothing, player might assume its a monster with a suicidal effect)
        if (cardNo!=0) {return cardNo;}
        else {cardNo = HandCPU.getPositionOfCardWithCardId(Card.BigBackBouncerBanisher.cardId);} // try Banisher last, because it is better to keep it on the hand
        return cardNo;
    }
    
    // Goal of this strategy: try at all cost to just set any MOOK monster, but try to find the best for that
    // returns true, if strategy worked
    public static boolean trySettingAnyMook() {
        if (Game.isMainPhase() && Game.isAllowingCPUToPlay() && AIEffects.cpuCanNormalSummonMook()) {
            // Try the Stall monsters first.
            int cardNo = HandCPU.getPositionOfCardWithCardId(Card.GodBarrier.cardId);
            if (cardNo==0) {cardNo = HandCPU.getPositionOfCardWithCardId(Card.EradicatorObstacle.cardId);}
            // Try the suicide effect monsters next.
            if (cardNo==0) {cardNo = HandCPU.getPositionOfCardWithCardId(Card.BigBurnerSuicideCommando.cardId);}
            if (cardNo==0) {cardNo = HandCPU.getPositionOfCardWithCardId(Card.NecromancerBackBouncer.cardId);}
            if (cardNo==0) {cardNo = HandCPU.getPositionOfCardWithCardId(Card.FlakshipNapalm.cardId);}
            if (cardNo==0) {cardNo = HandCPU.getPositionOfCardWithCardId(Card.BigBackBouncerBanisher.cardId);}
            // Then try the ones with still decent amount of def.
            if (cardNo==0) {cardNo = HandCPU.getPositionOfCardWithCardId(Card.GodKillingSpearLance.cardId);}
            if (cardNo==0) {cardNo = HandCPU.getPositionOfCardWithCardId(Card.MonsterStealerSteepLearningCurve.cardId);}
            if (cardNo==0) {cardNo = HandCPU.getPositionOfCardWithCardId(Card.CopyCatCardGrabber.cardId);}
            if (cardNo==0) {cardNo = HandCPU.getPositionOfCardWithCardId(Card.DiamondSwordShield.cardId);}
            // These are more valuable on the hand. That's why try them later.
            if (cardNo==0) {cardNo = HandCPU.getPositionOfCardWithCardId(Card.AttackStopperBuggedUpgrade.cardId);}
            if (cardNo==0) {cardNo = HandCPU.getPositionOfCardWithCardId(Card.BigAttackStopperSword.cardId);}
            // The last ones are the worst, but better than nothing.
            if (cardNo==0) {cardNo = HandCPU.getPositionOfCardWithCardId(Card.ModeChangerExhaustedExecutioner.cardId);}
            if (cardNo==0) {cardNo = HandCPU.getPositionOfCardWithCardId(Card.DemonGodDemon.cardId);}
            if (cardNo==0) {cardNo = HandCPU.getPositionOfCardWithCardId(Card.SlickRusherRecklessRusher.cardId);}
            if (cardNo==0) {
                AIEffects.cpuNormalSetsMookWithCardNo(cardNo);
                return true;
            }
        }
        return false;
    }
    
    // Goal of this strategy: try at all cost to just summon any MIDBOSS (if less than 2 monsters at the end of main phase 1), but try to find the best for that
    // returns true, if strategy worked
    // Always try to set a MOOK first! (This is really just the last resort.)
    public static boolean exceptionalStrategyTrySummonAnyMidboss() {
        if (Game.isMainPhase() && Game.isAllowingCPUToPlay() && SummonedMonster.countOwnSummonedMonsters(false) < 3) { // this exceptional strategy is only supposed to be used, if the CPU has less than 3 monsters, so that the CPU does at least something
            Game.CPUbehavior.reevaluateStrategies(); // needed to know, if the monsters are supposed to be summoned in attack or defence mode
            int maxAttPlayer = Game.BattleSituation.maxKnownAttPlayer;
            int maxRelValueCPU = Game.BattleSituation.maxRelValueCPU;
            if (maxAttPlayer <= 2000) { // try to summon a strong MIDBOSS in attack mode
                int cardNo = HandCPU.getPositionOfCardWithCardId(Card.IncorruptibleHolyLance.cardId);
                if (cardNo==0) {cardNo = HandCPU.getPositionOfCardWithCardId(Card.BigBanisherBurner.cardId);}
                if (cardNo==0) {cardNo = HandCPU.getPositionOfCardWithCardId(Card.FlakshipNapalm.cardId);}
                if (cardNo==0) {cardNo = HandCPU.getPositionOfCardWithCardId(Card.ModeChangerExhaustedExecutioner.cardId);}
                if (cardNo==0) {cardNo = HandCPU.getPositionOfCardWithCardId(Card.NecromancerBackBouncer.cardId);}
                if (cardNo==0) {cardNo = HandCPU.getPositionOfCardWithCardId(Card.SlickRusherRecklessRusher.cardId);}
                if (cardNo==0) {cardNo = HandCPU.getPositionOfCardWithCardId(Card.DiamondSwordShield.cardId);}
                if (cardNo==0) {cardNo = HandCPU.getPositionOfCardWithCardId(Card.AttackStopperBuggedUpgrade.cardId);}
                if (cardNo==0) {cardNo = HandCPU.getPositionOfCardWithCardId(Card.CopyCatCardGrabber.cardId);}
                if (cardNo==0) {cardNo = HandCPU.getPositionOfCardWithCardId(Card.BigBackBouncerBanisher.cardId);}
                if (cardNo!=0) {
                    return trySpecSumMidboss(cardNo, false);
                }
            }
            else if (maxAttPlayer > 2000 && maxAttPlayer <= 2500) { // try to summon a strong MIDBOSS in attack mode, or a MIDBOSS in defence, if it has to be
                int cardNo = HandCPU.getPositionOfCardWithCardId(Card.IncorruptibleHolyLance.cardId);
                if (cardNo==0) {cardNo = HandCPU.getPositionOfCardWithCardId(Card.BigBanisherBurner.cardId);}
                if (cardNo==0) {cardNo = HandCPU.getPositionOfCardWithCardId(Card.FlakshipNapalm.cardId);}
                if (cardNo==0) {cardNo = HandCPU.getPositionOfCardWithCardId(Card.ModeChangerExhaustedExecutioner.cardId);}
                if (cardNo==0) {cardNo = HandCPU.getPositionOfCardWithCardId(Card.NecromancerBackBouncer.cardId);}
                if (cardNo==0) {cardNo = HandCPU.getPositionOfCardWithCardId(Card.SlickRusherRecklessRusher.cardId);}
                if (cardNo!=0) {
                    return trySpecSumMidboss(cardNo, false);
                }
                if (maxAttPlayer > maxRelValueCPU && Game.isMainPhase() && Game.isAllowingCPUToPlay()) { // only summon a MIDBOSS in def, if it really has to be
                    if (cardNo==0) {cardNo = HandCPU.getPositionOfCardWithCardId(Card.AttackStopperBuggedUpgrade.cardId);}
                }
                if (cardNo!=0 && Game.isMainPhase() && Game.isAllowingCPUToPlay()) {
                    return trySpecSumMidbossInDef(cardNo, false);
                }
                if (cardNo==0) {cardNo = HandCPU.getPositionOfCardWithCardId(Card.BigBackBouncerBanisher.cardId);}
                if (cardNo!=0 && Game.isMainPhase() && Game.isAllowingCPUToPlay()) {
                    return trySpecSumMidboss(cardNo, false);
                }
                if (maxAttPlayer > maxRelValueCPU && Game.isMainPhase() && Game.isAllowingCPUToPlay()) { // only summon a MIDBOSS in def, if it really has to be
                    if (cardNo==0) {cardNo = HandCPU.getPositionOfCardWithCardId(Card.CopyCatCardGrabber.cardId);}
                }
                if (cardNo!=0 && Game.isMainPhase() && Game.isAllowingCPUToPlay()) {
                    return trySpecSumMidbossInDef(cardNo, false);
                }
            }
            else { // if player has more than 2500 attack, then try to summon Attack Stopper in def
                if (maxAttPlayer > maxRelValueCPU) { // only do something here, if really needed (but this is extremely likely to happen anyway)
                    int cardNo = HandCPU.getPositionOfCardWithCardId(Card.AttackStopperBuggedUpgrade.cardId);
                    if (cardNo!=0) {
                        return trySpecSumMidbossInDef(cardNo, false);
                    } // Stopp here. Everything else wouldn't make much sense. In such a case it's better just to set a MOOK. That's why trying to set a MOOK should be tried before this strategy.
                }
            }
        }
        return false;
    }
    
    // Goal of this strategy: cheat change a weak MOOK into the other monster:
    // whatever it is, it will be better!
    // (no matter which mode, but ideally from def. to att. mode; the latter condition can be forced by entering true as the 2nd argument)
    // returns true, if strategy worked
    // (The way it is written, it would work on any monster (not just mooks),
    // but it is not reccomended for anything else.)
    public static boolean tryCheatChangeMook (int monsterIdOfMook, boolean isOnlyConsideringMonstersInDef) {
        boolean isWorking = false;
        if (Game.isMainPhase() && Game.isAllowingCPUToPlay() && Game.isSwitchingOnCheatModeChangingRule) {
            for (int index = 1; index <= 5 ; index++){
                SummonedMonster SumMonster = SummonedMonster.getNthSummonedMonster(index, false);
                if (SumMonster.isExisting && SumMonster.Monster.monsterId==monsterIdOfMook) {
                    if (!isOnlyConsideringMonstersInDef || (isOnlyConsideringMonstersInDef && !SumMonster.isInAttackMode)) {
                        if (SumMonster.isModeChangeableThisTurn) {
                            AIEffects.cpuCheatChangeModeOfNthMonster(index);
                            return true;
                        }
                        else {
                            if (!AIEffects.cpuCanUseEffectModeChanger() && Game.isMainPhase() && Game.isAllowingCPUToPlay()) {
                                trySpecSumMidboss(true, Mon.ModeChanger.monsterId, false);
                            }
                            if (AIEffects.cpuCanUseEffectModeChanger() && Game.isMainPhase() && Game.isAllowingCPUToPlay()) {
                                return tryModeChangerToCheatChangeMook(index);
                            }
                        }
                    }
                }
            }
        }
        return isWorking;
    }
    // Goal of this strategy: use Mode Changer a given MOOK to cheat change it
    // (only try using own equip cards on own side, non-hand trap hand cards, or other MOOKS as sacrifice)
    // returns true, if strategy worked
    // (The way it is written, it would work on any monster (not just mooks),
    // but it is not reccomended for anything else.)
    public static boolean tryModeChangerToCheatChangeMook (int monsterNoOfMook) { // This strategy assumes Mode Changer to be already summoned! (Will only happen, if CPU summoned it.)
        if (Game.isMainPhase() && Game.isAllowingCPUToPlay() && Game.isSwitchingOnCheatModeChangingRule) {
            int posModeChanger = AIEffects.getPosOfCPUMonsterWithWorkingEffectByMonsterId(Mon.ModeChanger.monsterId);
            if (posModeChanger!=0) {
                SummonedMonster SumModeChanger = SummonedMonster.getNthSummonedMonster(posModeChanger, false);
                SummonedMonster SumMook = SummonedMonster.getNthSummonedMonster(monsterNoOfMook, false);
                forgetAllSacrifices();
                boolean hasDecidedOnSacrifice = tryMarkingNEquipCardsOnOwnSideAsSacrifice(1)==1;
                if (hasDecidedOnSacrifice) {
                    AIEffects.cpuEffectModeChanger(SumModeChanger, SumMook, true, getNthSacrifice(1));
                    return true;
                }
                else {
                    hasDecidedOnSacrifice = AIsacrifice.tryMarkingNonHandTrapHandCardAsSacrifice();
                    if (hasDecidedOnSacrifice) {
                        AIEffects.cpuEffectModeChanger(SumModeChanger, SumMook, true, getNthSacrifice(1));
                        return true;
                    }
                    else {
                        hasDecidedOnSacrifice = AIsacrifice.tryMarkingMonsterExceptNthAsSacrifice(monsterNoOfMook);
                        if (hasDecidedOnSacrifice) { // Here the Mode Changer might sacrifice itself, if it has to be, so it should better be worth it (like turning Barrier into God(!) or at least Obstacle into Eradicator).
                            AIEffects.cpuEffectModeChanger(SumModeChanger, SumMook, true, getNthSacrifice(1));
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    // Goal of this strategy: cheat change Barrier into God (no matter which mode, ideally from def. to att.)
    // returns true, if strategy worked
    public static boolean tryCheatChangeGodBarrier() {
        return tryCheatChangeMook(Mon.Barrier.monsterId, false);
    }
    
    // Goal of this strategy: cheat change Obstacle into Eradicator (no matter which mode, ideally from def. to att.)
    // returns true, if strategy worked
    public static boolean tryCheatChangeEradicatorObstacle() {
        return tryCheatChangeMook(Mon.Obstacle.monsterId, false);
    }
    
    // Goal of this strategy: cheat change Demon into Demon God (no matter which mode, ideally from def. to att.)
    // returns true, if strategy worked
    public static boolean tryCheatChangeDemonGodDemon() {
        return tryCheatChangeMook(Mon.Demon.monsterId, false);
    }
    
    // Goal of this strategy: cheat change Lance into GodKillingSpear (because God Killing Spear has zero def, only from def. to att. mode!)
    // returns true, if strategy worked
    public static boolean tryCheatChangeGodKillingSpearLance() {
        return tryCheatChangeMook(Mon.Lance.monsterId, true);
    }
    
    // Goal of this strategy: if the computer has enough monsters (at least 3) and the player can not get over 4000 attack,
    // tribute the 3 weakest summoned monsters to tribute summon a GOD (i.e. God or Demon God)
    // returns true, if strategy worked
    public static boolean tryTributeSummoningAGod() {
        Game.CPUbehavior.reevaluateStrategies();
        int consCardID;
        if (Game.isMainPhase() && Game.isAllowingCPUToPlay() && SummonedMonster.countOwnSummonedMonsters(false)>=3 && Game.BattleSituation.maxKnownRelValuePlayer < 4000) {
            consCardID = Card.GodBarrier.cardId;
            int cardNoOnHand = HandCPU.getPositionOfCardWithCardId(consCardID);
            if (cardNoOnHand==0) {
                consCardID = Card.DemonGodDemon.cardId;
                cardNoOnHand = HandCPU.getPositionOfCardWithCardId(consCardID);
            }
            if (cardNoOnHand!=0) { // has a God on hand
                boolean hasWorked = AIsacrifice.tryPreparingAsSacrificesNWeakMonsters(3, new AIsacrifice());
                if (hasWorked) {
                    AIEffects.cpuTributeSummonsGod(consCardID, AIsacrifice.getNthSacrifice(1), AIsacrifice.getNthSacrifice(2), AIsacrifice.getNthSacrifice(3));
                    return true;
                }
            }
        }
        return false;
    }
    
    // Goal of this strategy: special summon MIDBOSS (e.g. Attack Stopper in def. mode)
    // returns true, if strategy worked
    public static boolean trySpecSumMidboss (boolean isInAttackMode, int monsterId, boolean isNotUsingHandCards) {
        return trySpecSumMidboss(isInAttackMode, YCard.getCardIdByMonsterId(monsterId), YCard.monsterIdBelongsToUpperMonster(monsterId), isNotUsingHandCards);
    }
    public static boolean trySpecSumMidboss (int cardNoOnHand, boolean isNotUsingHandCards) {
        return trySpecSumMidboss(true, HandCPU.getNthCardOfHand(cardNoOnHand).cardId, true, isNotUsingHandCards);
    }
    public static boolean trySpecSumMidbossInDef (int handNo, boolean isNotUsingHandCards) {
        return trySpecSumMidboss(false, HandCPU.getNthCardOfHand(handNo).cardId, true, isNotUsingHandCards);
    }
    public static boolean trySpecSumMidboss (boolean isInAttackMode, int cardId, boolean isConcerningUpperMonster, boolean isNotUsingHandCards) {
        if (Game.isMainPhase() && Game.isAllowingCPUToPlay() && cpuCanPayOneUselessCard()) {
            SummonedMonster TargetMonster = SummonedMonster.determineFreeMonsterZone(false);
            if (TargetMonster.sumMonsterNumber>0 && HandCPU.getPositionOfCardWithCardId(cardId)!=0) {
                AIsacrifice HandCardNotToBeSacrificed = AIsacrifice.markHandCardAsSacrificeByCardId(cardId);
                boolean hasWorked = tryPreparingSacrificesWorthOneCard(HandCardNotToBeSacrificed, isNotUsingHandCards);
                if (hasWorked) {
                    AIEffects.cpuSpecialSummonsMidbossFromHand(isInAttackMode, cardId, isConcerningUpperMonster, getNthSacrifice(1), getNthSacrifice(2));
                    return true;
                }
            }
        }
        return false;
    }
    
    // Goal of this strategy: try to equip (from hand) a monster with a shield, ideally an Attack Stopper in def. mode
    // returns true, if strategy worked
    public static boolean tryEquipWithShieldFromHand() {
        int cardIdShield = Card.DiamondSwordShield.cardId;
        if (Game.isMainPhase() && Game.isAllowingCPUToPlay() && SummonedMonster.countOwnSummonedMonsters(false)>=1) {
            int cardNo = HandCPU.getPositionOfCardWithCardId(cardIdShield);
            if (cardNo!=0) {
                Integer[] monterIds = getArrayOfCPUMonsterIds();
                int pos = getIndexOfValueInArray(Mon.AttackStopper.monsterId, monterIds); // look for Attack Stopper first
                SummonedMonster SumMonster;
                if (pos!=0) {
                    SumMonster = SummonedMonster.getNthSummonedMonster(pos, false);
                    if (AIEffects.cpuCanUseEquipEffectFromHand(SumMonster, cardIdShield)) { // should always be true here, but just to be safe
                        AIEffects.cpuEquipEffectFromHand(SumMonster, cardIdShield);
                        return true;
                    }
                }
                pos = getIndexOfValueInArray(Mon.Obstacle.monsterId, monterIds); // try Obstacle in def. next
                if (pos!=0) {
                    SumMonster = SummonedMonster.getNthSummonedMonster(pos, false);
                    if (!SumMonster.isInAttackMode && AIEffects.cpuCanUseEquipEffectFromHand(SumMonster, cardIdShield)) {
                        AIEffects.cpuEquipEffectFromHand(SumMonster, cardIdShield);
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    // Goal of this strategy: try to equip (from backrow) a monster with a shield, ideally an Attack Stopper in def. mode
    // returns true, if strategy worked
    public static boolean tryEquipWithShieldFromStack() {
        int cardIdShield = Card.DiamondSwordShield.cardId;
        if (Game.isMainPhase() && Game.isAllowingCPUToPlay() && SummonedMonster.countOwnSummonedMonsters(false)>=1 && AIEffects.cpuCanUseEquipEffectFromStack()) {
            Integer[] monterIds = getArrayOfCPUMonsterIds();
            int numberOfAttStoppers = numberOfTimesOfValueInArray(Mon.AttackStopper.monsterId, monterIds);
            if (numberOfAttStoppers>0) {
                for (int n = 1; n <= numberOfAttStoppers; n++){ // only equip Attack Stopper (it it doesn't exist, leave equip card where it is)
                    int posOfAttStopper = getNthIndexOfValueInArray(n, Mon.AttackStopper.monsterId, monterIds);
                    SummonedMonster NthAttStopper = SummonedMonster.getNthSummonedMonster(posOfAttStopper, false);
                    for (int index = 1; index <= 5; index++){
                        SummonedMonster SumMonster =SummonedMonster.getNthSummonedMonster(index, false);
                        EStack Stack = EStack.getNthStack(index, false);
                        if (SumMonster.isExisting && Stack.numberOfCards>=1) {
                            if (AIEffects.cpuCanUseEquipEffectFromStack(Stack, cardIdShield, NthAttStopper) && !SumMonster.Monster.equals(Mon.AttackStopper)) { // only equip, if Shield is not already equipping an Attack Stopper
                                AIEffects.cpuEquipEffectFromStack(Stack, cardIdShield, NthAttStopper);
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    
    // Goal of this strategy: spec. summon ENDBOSS (e.g. Big Attack Stopper) from GY (since one has discarded already)
    // returns true, if strategy worked
    public static boolean trySpecSumEndbossFromGY (boolean isInAttackMode, int monsterId) {
        if (Game.isMainPhase() && Game.isAllowingCPUToPlay() && cpuCanPayTwoUselessCards() && AIEffects.cpuCanSpecialSummonEndbossFromGY()) {
            int cardId = YCard.getCardIdByMonsterId(monsterId);
            SummonedMonster TargetMonster = SummonedMonster.determineFreeMonsterZone(false);
            if (TargetMonster.sumMonsterNumber>0 && GYDeckCPU.getPositionOfCardWithCardIdInDeck(cardId)!=0) {
                AIsacrifice GYCardNotToBeSacrificed = AIsacrifice.markGYCardAsSacrificeByCardId(cardId);
                boolean hasWorked = tryPreparingSacrificesWorthTwoCards(GYCardNotToBeSacrificed);
                if (hasWorked && isNotTributingImportantMonster()) {
                    AIEffects.cpuSpecialSummonsFromGY(isInAttackMode, cardId, getNthSacrifice(1), getNthSacrifice(2), getNthSacrifice(3), getNthSacrifice(4));
                    return true;
                }
            }
        }
        return false;
    }
    
    // returns true, if none of the marked sacrifices are summoned monsters of threat level 3 or have at least 3 stars
    // This method prevents tributing important monsters. Very useful when summoning ENDBOSSES, because one doesn't want to tribute the same monster + another one for that!
    public static boolean isNotTributingImportantMonster() {
        Game.CPUbehavior.reevaluateStrategies();
        for (int index = 1; index <= 4; index++){
            AIsacrifice ConsideredSacrifice = AIsacrifice.getNthSacrifice(index);
            if (ConsideredSacrifice.sacrificeIsValidMonster()) {
                SummonedMonster SumMonster = ConsideredSacrifice.extractSummonedMonsterFromValidSacrifice();
                if (SumMonster.Monster.stars >= 3 || SumMonster.AIinfoThreatLv >= 3) {
                    return false;
                }
            }
        }
        return true;
    }
    
    // Goal of this strategy: special summon ENDDBOSS (e.g. Big Attack Stopper in att. mode) from hand (since in the first few turns it is too unlikely that e.g. Big Attack Stopper is already in GY)
    // returns true, if strategy worked
    public static boolean trySpecSumEndbossFromHand (boolean isInAttackMode, int monsterId) {
        if (Game.isMainPhase() && Game.isAllowingCPUToPlay() && cpuCanPayTwoUselessCards()) {
            int cardId = YCard.getCardIdByMonsterId(monsterId);
            SummonedMonster TargetMonster = SummonedMonster.determineFreeMonsterZone(false);
            if (TargetMonster.sumMonsterNumber>0 && HandCPU.getPositionOfCardWithCardId(cardId)!=0) {
                AIsacrifice HandCardNotToBeSacrificed = AIsacrifice.markHandCardAsSacrificeByCardId(cardId);
                boolean hasWorked = tryPreparingSacrificesWorthTwoCards(HandCardNotToBeSacrificed);
                if (hasWorked && isNotTributingImportantMonster()) {
                    AIEffects.cpuSpecialSummonsEndbossFromHand(isInAttackMode, cardId, true, getNthSacrifice(1), getNthSacrifice(2));
                    return true;
                }
            }
        }
        return false;
    }
    
    // Goal of this strategy: reveal 1st card on hand in order to look at an unknown monster of the player
    // (this effect automatically uses the last Obstacle/God and the last unkown monsters on the field)
    // returns true, if strategy worked
    public static boolean tryUncoverFaceDownCard() {
        if (Game.isMainPhase() && Game.isAllowingCPUToPlay() && SummonedMonster.countOwnUnknownMonsters(true)>=1 && HandCPU.numberOfCards>=1) {
            if (!Game.isPlayersTurn && Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try uncovering a card");}
            int posOfEffectMonster=0;
            int posOfTarget=0;
            for (int index = 1; index <= 5; index++){
                SummonedMonster SumMonster = SummonedMonster.getNthSummonedMonster(index, false);
                if (AIEffects.cpuCanUseEffectShowACardLookAtCardOnlyByRevealingHandCard(SumMonster)) {
                    posOfEffectMonster = SumMonster.sumMonsterNumber;
                }
                SumMonster = SummonedMonster.getNthSummonedMonster(index, true);
                if (SumMonster.isExistingButUnknown()) {
                    posOfTarget = SumMonster.sumMonsterNumber;
                }
            }
            if (posOfEffectMonster!=0 && posOfTarget!=0) {
                SummonedMonster EffectMonster = SummonedMonster.getNthSummonedMonster(posOfEffectMonster, false);
                SummonedMonster UnkownFaceDownMonster = SummonedMonster.getNthSummonedMonster(posOfTarget, true);
                if (AIEffects.cpuCanUseEffectShowACardLookAtCard(EffectMonster, UnkownFaceDownMonster)) {
                    AIEffects.cpuEffectShowACardLookAtCard(EffectMonster, UnkownFaceDownMonster, 1);
                    return true;
                }
            }
        }
        return false;
    }
    
    // Goal of this strategy: in main phase 2 try to turn all monsters such that they have the higher relevant value
    // or are at least in defence mode (if the relevant value is less than 2000 or so)
    // returns true, if strategy worked
    public static boolean tryTurningWeakMonstersIntoDef() {
        return tryTurningWeakMonstersIntoDef(Game.CPUbehavior.estimatedMinAttack);
    }
    public static boolean tryTurningWeakMonstersIntoDef (int threshold) {
        if (Game.isMainPhase() && Game.isAllowingCPUToPlay()) {
            int numOfTurnedMonsters=0;
            for (int index = 1; index <= 5; index++){
                if (AIEffects.cpuCanChangeModeOfNthMonster(index)) {
                    SummonedMonster SumMonster = SummonedMonster.getNthSummonedMonster(index, false);
                    if (tryTurningWeakMonsterIntoDef(SumMonster, threshold)) {numOfTurnedMonsters++;}
                }
            }
            return (numOfTurnedMonsters>0);
        }
        return false;
    }
    public static boolean tryTurningWeakMonsterIntoDef (SummonedMonster SumMonster, int threshold) {
        if (Game.isMainPhase() && Game.isAllowingCPUToPlay() && SumMonster.isModeChangeable()) {
            if (SumMonster.isInAttackMode) {
                return tryTurningMonsterIntoDef(SumMonster, threshold);
            }
            else { // is already in def
                if (SumMonster.isExistingAndKnown()) { // only try to get it into attack mode, if the monster is already known to the player and if the attack value is at least the threashold
                    return tryTurningMonsterIntoAtt(SumMonster, threshold);
                }
            }
        }
        return false;
    }
    
    // try increasing relevant value by turning monster into defence mode, or better protect life points when values are less than the threashold
    // returns true, if strategy worked
    public static boolean tryTurningMonsterIntoDef (SummonedMonster SumMonster, int threshold) {
        if (Game.isMainPhase() && Game.isAllowingCPUToPlay() && SumMonster.isModeChangeable() && SumMonster.isInAttackMode) {
            int currentRelevantValue = SumMonster.relevantValue();
            int largestDef = SumMonster.getLargestDefValueByOrdinaryChange();
            int otherMonsterDef = SumMonster.otherMonsterDef();
            if (currentRelevantValue < threshold || largestDef>currentRelevantValue) { // Here the 1st condition is different from the attack version. Since here one wants to protect life points, one prefers turning into def, even if that decreases the relevant value.
                AIEffects.cpuChangeModeOfNthMonster(SumMonster.sumMonsterNumber, (Game.isSwitchingOnCheatModeChangingRule && otherMonsterDef==largestDef && otherMonsterDef>SumMonster.def)); // last condition for prefering allowed change over cheat change
                return true;
            }
        }
        return false;
    }
    
    // Goal of this strategy: in main phase 1 (right before the simulation of possible battle damage)
    // try to turn all monsters with more attack than defence into attack mode (if the attack value is at least 2000 or so)
    // returns true, if strategy worked
    public static boolean tryTurningStrongMonstersIntoAtt() {
        return tryTurningStrongMonstersIntoAtt(Game.CPUbehavior.estimatedMinAttack);
    }
    public static boolean tryTurningStrongMonstersIntoAtt (int threshold) {
        if (Game.isMainPhase() && Game.isAllowingCPUToPlay()) {
            int numOfTurnedMonsters=0;
            for (int index = 1; index <= 5; index++){
                if (AIEffects.cpuCanChangeModeOfNthMonster(index)) {
                    SummonedMonster SumMonster = SummonedMonster.getNthSummonedMonster(index, false);
                    if (tryTurningMonsterIntoAtt(SumMonster, threshold)) {numOfTurnedMonsters++;}
                }
            }
            return (numOfTurnedMonsters>0);
        }
        return false;
    }
    
    // try increasing relevant value by turning monster into attack mode, if the attack value is at least the threashold and larger than the defence value
    // returns true, if strategy worked
    public static boolean tryTurningMonsterIntoAtt (SummonedMonster SumMonster, int threshold) {
        if (Game.isMainPhase() && Game.isAllowingCPUToPlay() && SumMonster.isModeChangeable() && !SumMonster.isInAttackMode) {
            int largestAtt = SumMonster.getLargestAttValueByOrdinaryChange();
            int otherMonsterAtt = SumMonster.otherMonsterAtt();
            if (largestAtt > threshold && largestAtt > SumMonster.att) {
                AIEffects.cpuChangeModeOfNthMonster(SumMonster.sumMonsterNumber, (Game.isSwitchingOnCheatModeChangingRule && otherMonsterAtt==largestAtt && otherMonsterAtt > SumMonster.att)); // last condition for prefering allowed change over cheat change
                return true;
            }
        }
        return false;
    }
    
    // if everything else fails, try the removal strategies (from balanced CPU)
    
    
    // --- mostly BALANCED STRATEGIES ---
    
    // consists of basic attacks and trying to get rid of monster with highest threat level by using Eradicator, Big Back Bouncer, Monster Stealer or Mode Changer or using their effects with Copy Cat or Skill Stealer
    // (rest in this strategy is reusing either defencive, aggrsssive or reactive (negates) strategies)
    // there is also a proactive negating strategy
    
    // BEST STRATEGY EVER!
    // Goal of this strategy: If the player has a summoned non-negated Neutraliser on the field and the computer has Neutraliser (or Bugged Upgrade) on the hand, discard/equip it to neutralise Neutraliser
    // returns true, if strategy worked
    public static boolean tryNeutralisingNeutraliser() {
        if (Game.isMainPhase() && Game.isAllowingCPUToPlay()) {
            for (int index = 1; index <= 5; index++){
                SummonedMonster SumMonster = SummonedMonster.getNthSummonedMonster(index, true);
                if (SumMonster.Monster.equals(Mon.Neutraliser) && !SumMonster.isNotAbleToUseItsEffects) {
                    boolean hasWorked = tryDiscardingNeutraliserOnEffectmonster(SumMonster);
                    if (hasWorked) {
                        return true;
                    }
                    else if (Game.isMainPhase() && Game.isAllowingCPUToPlay()) {
                        return tryEquippingEffectmonsterWithBuggedUpgrade(SumMonster);
                    }
                }
            }
        }
        return false;
    }
    // Goal of this strategy: try to discard Neutraliser in order to negate the effects of a given effect monster (best used on Neutraliser) for good
    // returns true, if strategy worked
    public static boolean tryDiscardingNeutraliserOnEffectmonster (SummonedMonster EffectMonster) {
        if (AIEffects.cpuCanUseHandTrapEffectNegateOnMonster(EffectMonster) && Game.isMainPhase() && Game.isAllowingCPUToPlay()) {
            AIEffects.cpuHandTrapEffectNegateOnMonster(EffectMonster);
            return true;
        }
        return false;
    }
    // Goal of this strategy: try to discard Neutraliser in order to negate the effects of a given effect monster (best used on Neutraliser) for good
    // returns true, if strategy worked
    public static boolean tryEquippingEffectmonsterWithBuggedUpgrade (SummonedMonster EffectMonster) {
        if (Game.isMainPhase() && Game.isAllowingCPUToPlay()) {
            int cardIdBuggedUpgrade = Card.AttackStopperBuggedUpgrade.cardId;
            if (AIEffects.cpuCanUseEquipEffectFromHand(EffectMonster, cardIdBuggedUpgrade)) { // first try equipping from hand
                AIEffects.cpuEquipEffectFromHand(EffectMonster, cardIdBuggedUpgrade);
                return true;
            }
            // then try equipping from stack
            EStack Stack = new EStack(false, 0);
            int cardNoInStack = 0;
            for (int index = 1; index <= 5; index++){
                if (SummonedMonster.getNthSummonedMonster(index, false).isExisting) {
                    Stack = EStack.getNthStack(index, false);
                    cardNoInStack = Stack.getPositionOfEquipCardByCardID(cardIdBuggedUpgrade, false, false);
                    if (cardNoInStack!=0) {break;}
                }
                if (SummonedMonster.getNthSummonedMonster(index, true).isExisting) {
                    Stack = EStack.getNthStack(index, true);
                    cardNoInStack = Stack.getPositionOfEquipCardByCardID(cardIdBuggedUpgrade, false, false);
                    if (cardNoInStack!=0) {break;}
                }
            }
            if (cardNoInStack!=0 && Game.isMainPhase() && Game.isAllowingCPUToPlay()) {
                if (AIEffects.cpuCanUseEquipEffectFromStack(Stack, cardIdBuggedUpgrade, EffectMonster)) {
                    AIEffects.cpuEquipEffectFromStack(Stack, cardIdBuggedUpgrade, EffectMonster);
                    return true;
                }
            }
        }
        return false;
    }
    
    // Goal of this strategy: if the monster with the highest threat level has threat level 3, try getting rid of it by destroying it with Eradicator
    // returns true, if strategy worked
    public static boolean tryDestroyingGreatestThreatByEradicator() {
        Game.CPUbehavior.reevaluateStrategies();
        int monsterIdEradicator = Mon.Eradicator.monsterId;
        if (Game.isMainPhase() && Game.isAllowingCPUToPlay() && SummonedMonster.isHavingThreatLv3Monster(true)) {
            if (!AIEffects.hasWorkingMonsterEffect(false, monsterIdEradicator)) { // if the CPU doesn't have working Eradicator on the field yet, try to summon it
                // 1st try to summon it from hand, then from GY
                int payableSemipoints = countPayableSemipointsWithHandAndGYAlone();
                if (payableSemipoints >= 6) { // one needs 6 semipoints (i.e. a worth of 3 cards) to pull this off: 2 cards for summoning Eradicator, one card for its effect
                    boolean isWorking = trySpecSumEndbossFromHand(true, monsterIdEradicator); // can not be negated
                    if (!isWorking && Game.isMainPhase() && Game.isAllowingCPUToPlay()) {
                        trySpecSumEndbossFromGY(true, monsterIdEradicator); // this can be negated though
                    }
                }
            }
            return tryDestroyingGreatestThreatByEradicatorAlreadyOnField(); // maybe meanwhile CPU has a working Eradicator
        }
        return false;
    }
    public static boolean tryDestroyingGreatestThreatByEradicatorAlreadyOnField() { // like above, just that here it is already (assumed to be) on field
        Game.CPUbehavior.reevaluateStrategies();
        int monsterIdEradicator = Mon.Eradicator.monsterId;
        if (Game.isMainPhase() && Game.isAllowingCPUToPlay() && SummonedMonster.isHavingThreatLv3Monster(true) && AIEffects.hasWorkingMonsterEffect(false, monsterIdEradicator)) {
            int posEradicator = AIEffects.getPosOfCPUMonsterWithWorkingEffectByMonsterId(monsterIdEradicator);
            int payableSemipoints = countPayableSemipointsWithHandAndGYAlone();
            if (posEradicator!=0 && payableSemipoints >= 2) {
                SummonedMonster EffectMonster = SummonedMonster.getNthSummonedMonster(posEradicator, false);
                SummonedMonster StrongestMonster = SummonedMonster.getNthStrongestMonster(1, true);
                boolean hasWorked = tryPreparingSacrificesWorthOneCard(new AIsacrifice(), false);
                if (hasWorked) {
                    if (StrongestMonster.canBeDestroyedByEffect() && !StrongestMonster.isFaceDown) {
                        if (AIEffects.cpuCanUseEffectEradicatorOnMonster(EffectMonster, StrongestMonster)) {
                            AIEffects.cpuEffectEradicatorOnMonster(EffectMonster, StrongestMonster, getNthSacrifice(1), getNthSacrifice(2));
                            return true;
                        }
                    }
                    else { // if can not get rid of strongest one, try second strongest one
                        SummonedMonster AlmostStrongestMonster = SummonedMonster.getNthStrongestMonster(2, true);
                        if (AIEffects.cpuCanUseEffectEradicatorOnMonster(EffectMonster, AlmostStrongestMonster) && AlmostStrongestMonster.isExisting) {
                            AIEffects.cpuEffectEradicatorOnMonster(EffectMonster, AlmostStrongestMonster, getNthSacrifice(1), getNthSacrifice(2));
                            return true;
                        }
                    }   
                }
            }
        }
        return false;
    }
    
    // Goal of this strategy: if the monster with the highest threat level has threat level 3, try getting rid of it by returning it to the hand with Big Back Bouncer
    // returns true, if strategy worked
    public static boolean tryRemovingGreatestThreatByBigBackBouncer() {
        Game.CPUbehavior.reevaluateStrategies();
        int monsterIdBigBackBouncer = Mon.BigBackBouncer.monsterId;
        if (Game.isMainPhase() && Game.isAllowingCPUToPlay() && SummonedMonster.isHavingThreatLv3Monster(true)) {
            if (!AIEffects.hasWorkingMonsterEffect(false, monsterIdBigBackBouncer)) { // if the CPU doesn't have working Big Back Bouncer on the field yet, try to summon it
                int payableSemipoints = countPayableSemipointsWithHandAndGYAlone();
                if (payableSemipoints >= 4) { // one needs 4 semipoints (i.e. a worth of 2 cards) to pull this off: 1 cards for summoning Big Back Bouncer, one card for its effect
                    trySpecSumMidboss(true, monsterIdBigBackBouncer, false); // can not be negated
                }
            }
            return tryRemovingGreatestThreatByBigBackBouncerAlreadyOnField(); // maybe meanwhile CPU has a working Big Back Bouncer
        }
        return false;
    }
    public static boolean tryRemovingGreatestThreatByBigBackBouncerAlreadyOnField() { // like above, just that here it is already (assumed to be) on field
        Game.CPUbehavior.reevaluateStrategies();
        int monsterIdBigBackBouncer = Mon.BigBackBouncer.monsterId;
        if (Game.isMainPhase() && Game.isAllowingCPUToPlay() && SummonedMonster.isHavingThreatLv3Monster(true) && AIEffects.hasWorkingMonsterEffect(false, monsterIdBigBackBouncer)) {
            int posBigBackBouncer = AIEffects.getPosOfCPUMonsterWithWorkingEffectByMonsterId(monsterIdBigBackBouncer);
            int payableSemipoints = countPayableSemipointsWithHandAndGYAlone();
            if (posBigBackBouncer!=0 && payableSemipoints >= 2) {
                SummonedMonster EffectMonster = SummonedMonster.getNthSummonedMonster(posBigBackBouncer, false);
                SummonedMonster StrongestMonster = SummonedMonster.getNthStrongestMonster(1, true);
                boolean hasWorked = tryPreparingSacrificesWorthOneCard(new AIsacrifice(), false);
                if (hasWorked) {
                    if (!StrongestMonster.isImmune()) {
                        if (AIEffects.cpuCanUseEffectBigBackBouncerOnMonster(EffectMonster, StrongestMonster)) {
                            AIEffects.cpuEffectBigBackBouncerOnMonster(EffectMonster, StrongestMonster, getNthSacrifice(1), getNthSacrifice(2));
                            return true;
                        }
                    }
                    else { // if can not get rid of strongest one, try second strongest one
                        SummonedMonster AlmostStrongestMonster = SummonedMonster.getNthStrongestMonster(2, true);
                        if (AIEffects.cpuCanUseEffectBigBackBouncerOnMonster(EffectMonster, AlmostStrongestMonster) && AlmostStrongestMonster.isExisting) {
                            AIEffects.cpuEffectBigBackBouncerOnMonster(EffectMonster, AlmostStrongestMonster, getNthSacrifice(1), getNthSacrifice(2));
                            return true;
                        }
                    }   
                }
            }
        }
        return false;
    }
    
    // Goal of this strategy: if the monster with the highest threat level has threat level 3, try getting rid of it by stealing it with Monster Stealer
    // returns true, if strategy worked
    public static boolean tryRemovingGreatestThreatByMonsterStealer() {
        Game.CPUbehavior.reevaluateStrategies();
        int monsterIdMonsterStealer = Mon.MonsterStealer.monsterId;
        if (Game.isMainPhase() && Game.isAllowingCPUToPlay() && SummonedMonster.isHavingThreatLv3Monster(true)) {
            if (!AIEffects.hasWorkingMonsterEffect(false, monsterIdMonsterStealer)) { // if the CPU doesn't have working Monster Stealer on the field yet, try to summon it
                // 1st try to summon it from hand, then from GY
                int payableSemipoints = countPayableSemipointsWithHandAndGYAlone();
                if (payableSemipoints >= 8) { // one needs 8 semipoints (i.e. a worth of 4 cards) to pull this off: half of the costs for summoning Monster Stealer, the other half for its effect
                    boolean isWorking = trySpecSumEndbossFromHand(true, monsterIdMonsterStealer); // can not be negated
                    if (!isWorking) {
                        trySpecSumEndbossFromGY(true, monsterIdMonsterStealer); // this can be negated though
                    }
                }
            }
            return tryRemovingGreatestThreatByMonsterStealerAlreadyOnField(); // maybe meanwhile CPU has a working Monster Stealer
        }
        return false;
    }
    public static boolean tryRemovingGreatestThreatByMonsterStealerAlreadyOnField() { // like above, just that here it is already (assumed to be) on field
        Game.CPUbehavior.reevaluateStrategies();
        int monsterIdMonsterStealer = Mon.MonsterStealer.monsterId;
        if (Game.isMainPhase() && Game.isAllowingCPUToPlay() && SummonedMonster.isHavingThreatLv3Monster(true) && AIEffects.hasWorkingMonsterEffect(false, monsterIdMonsterStealer)) {
            int posMonsterStealer = AIEffects.getPosOfCPUMonsterWithWorkingEffectByMonsterId(monsterIdMonsterStealer);
            int payableSemipoints = countPayableSemipointsWithHandAndGYAlone();
            if (posMonsterStealer!=0 && payableSemipoints >= 4) {
                SummonedMonster EffectMonster = SummonedMonster.getNthSummonedMonster(posMonsterStealer, false);
                SummonedMonster StrongestMonster = SummonedMonster.getNthStrongestMonster(1, true);
                boolean hasWorked = tryPreparingSacrificesWorthTwoCards(new AIsacrifice());
                if (hasWorked) {
                    if (!StrongestMonster.isImmune()) {
                        if (AIEffects.cpuCanUseEffectMonsterStealerOnMonster(EffectMonster, StrongestMonster)) {
                            AIEffects.cpuEffectMonsterStealerOnMonster(EffectMonster, StrongestMonster, getNthSacrifice(1), getNthSacrifice(2), getNthSacrifice(3), getNthSacrifice(4));
                            return true;
                        }
                    }
                    else { // if can not get rid of strongest one, try second strongest one
                        SummonedMonster AlmostStrongestMonster = SummonedMonster.getNthStrongestMonster(2, true);
                        if (AIEffects.cpuCanUseEffectMonsterStealerOnMonster(EffectMonster, AlmostStrongestMonster) && AlmostStrongestMonster.isExisting) {
                            AIEffects.cpuEffectMonsterStealerOnMonster(EffectMonster, AlmostStrongestMonster, getNthSacrifice(1), getNthSacrifice(2), getNthSacrifice(3), getNthSacrifice(4));
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    // Maybe also try stealing equip card? Or is that too much cost for too few payoff?
    // In the moment, I don't see the need yet. What very specific situation would that be really good in? 
    // The player always can see the whole picture. However, the computer only ever sees a tiny fraction of the whole situation.
    // That's why this effect is probably not well suited for the computer. At least not until it has become significantly more intelligent.
    
    // Goal of this strategy: if the monster with the highest threat level has threat level 3, try getting weakening it by changing its mode (usually into defence mode) with Mode Changer
    // returns true, if strategy worked
    public static boolean tryWeakeningGreatestThreatByModeChanger() {
        Game.CPUbehavior.reevaluateStrategies();
        int noOfAttacks = SummonedMonster.countNumberOfAttacksLeftThisBattlePhase(false);
        int noOfWeakMonsters=0;
        int monsterIdModeChanger = Mon.ModeChanger.monsterId;
        if (Game.isMainPhase() && Game.isAllowingCPUToPlay() && SummonedMonster.isHavingThreatLv3Monster(true)) {
            boolean isAbleToWeaken=false;
            for (int index = 1; index <= SummonedMonster.countOwnSummonedMonsters(true); index++){
                SummonedMonster SumMonster = SummonedMonster.getNthStrongestMonster(index, true);
                if (SumMonster.canBeWeakenedByEffectChange()) {
                    if (SumMonster.AIinfoThreatLv==3) {
                        isAbleToWeaken=true;
                    }
                }
                else {
                    noOfWeakMonsters++;
                }
            }
            if (isAbleToWeaken && noOfAttacks >= noOfWeakMonsters) {
                if (!AIEffects.hasWorkingMonsterEffect(false, monsterIdModeChanger)) { // if the CPU doesn't have working Mode Changer on the field yet, try to summon it
                    int payableSemipoints = countPayableSemipointsWithHandAndGYAlone();
                    if (payableSemipoints >= 3) { // one needs 3 semipoints (i.e. a worth of 1.5 cards) to pull this off: 1 cards for summoning Mode Changer, any card (worth at least 0.5 cards) for its effect
                        trySpecSumMidboss(true, monsterIdModeChanger, false); // can not be negated
                    }
                }
                return tryWeakeningGreatestThreatByModeChangerAlreadyOnField(); // maybe meanwhile CPU has a working Mode Changer
            }
        }
        return false;
    }
    public static boolean tryWeakeningGreatestThreatByModeChangerAlreadyOnField() { // like above, just that here it is already (assumed to be) on field
        Game.CPUbehavior.reevaluateStrategies();
        int monsterIdModeChanger = Mon.ModeChanger.monsterId;
        int maxAttCPU = Game.BattleSituation.maxAttCPU;
        SummonedMonster NthStrongestMonster = SummonedMonster.getNthStrongestMonster(1, true);
        if (Game.isMainPhase() && Game.isAllowingCPUToPlay() && SummonedMonster.isHavingThreatLv3Monster(true) && AIEffects.hasWorkingMonsterEffect(false, monsterIdModeChanger)) {
            boolean isUsefulEffect=false;
            for (int index = 1; index <= SummonedMonster.countOwnSummonedMonsters(true); index++){
                NthStrongestMonster = SummonedMonster.getNthStrongestMonster(index, true);
                if (NthStrongestMonster.AIinfoThreatLv==3 && NthStrongestMonster.canBeWeakenedByEffectChange()) {
                    isUsefulEffect=true;
                    break;
                }
            }
            if (isUsefulEffect && NthStrongestMonster.getLowestRelevantValueByEffectChange() < maxAttCPU) { // just to be safe, assume a more conservative attack pattern latter on, in case one is turning a monster into attack mode and one does not have a higher attack (only the same)
                int posModeChanger = AIEffects.getPosOfCPUMonsterWithWorkingEffectByMonsterId(monsterIdModeChanger);
                SummonedMonster EffectMonster = SummonedMonster.getNthSummonedMonster(posModeChanger, false);
                // these need to be initialised, otherwise compiler complains
                NthStrongestMonster = SummonedMonster.getNthStrongestMonster(1, true);
                int lowestValue = NthStrongestMonster.getLowestRelevantValueByEffectChange();
                // but need these two here, because otherwise not known later on
                boolean hasWorked = tryPreparingAsSacrificeAnyCardExceptMonsters(new AIsacrifice());
                if (posModeChanger!=0 && hasWorked) {
                    for (int index = 1; index <= SummonedMonster.countOwnSummonedMonsters(true); index++){
                        NthStrongestMonster = SummonedMonster.getNthStrongestMonster(index, true);
                        lowestValue = NthStrongestMonster.getLowestRelevantValueByEffectChange();
                        boolean isKnown = NthStrongestMonster.isExistingAndKnown();
                        if (isKnown && NthStrongestMonster.AIinfoThreatLv==3 && NthStrongestMonster.canBeWeakenedByEffectChange()) {
                            break;
                        }
                    }
                    if (AIEffects.cpuCanUseEffectModeChanger(EffectMonster, NthStrongestMonster)) {
                        boolean isUsingCheatChange;
                        if (Game.isSwitchingOnCheatModeChangingRule) {
                            if (NthStrongestMonster.isInAttackMode) {
                                isUsingCheatChange = (NthStrongestMonster.otherMonsterDef()==lowestValue);
                            }
                            else {
                                isUsingCheatChange = (NthStrongestMonster.otherMonsterAtt()==lowestValue);
                            }
                        }
                        else {
                           isUsingCheatChange = false;
                        }
                        AIEffects.cpuEffectModeChanger(EffectMonster, NthStrongestMonster, isUsingCheatChange, getNthSacrifice(1));
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    // Goal of this strategy: try to use the effect of Mode Changer on as many unknown monsters of the opponent as possible
    // summon Mode Changer, if it has to be
    public static boolean tryModeChangeUnknownMonstersByModeChanger() {
        Game.CPUbehavior.reevaluateStrategies();
        int monsterIdModeChanger = Mon.ModeChanger.monsterId;
        if (Game.isMainPhase() && Game.isAllowingCPUToPlay() && SummonedMonster.countOwnFaceDownMonsters(true, true)>=1) {
            if (!AIEffects.hasWorkingMonsterEffect(false, monsterIdModeChanger)) { // if the CPU doesn't have working Mode Changer on the field yet, try to summon it
                int payableSemipoints = countPayableSemipointsWithHandAndGYAlone();
                if (payableSemipoints >= 3) { // one needs 3 semipoints (i.e. a worth of 1.5 cards) to pull this off: 1 cards for summoning Mode Changer, any card (worth at least 0.5 cards) for its effect
                    trySpecSumMidboss(true, monsterIdModeChanger, false); // can not be negated
                }
            }
            return tryModeChangeUnknownMonstersByModeChangerAlreadyOnField(); // maybe meanwhile CPU has a working Mode Changer
        }
        return false;
    }
    public static boolean tryModeChangeUnknownMonstersByModeChangerAlreadyOnField() {
        int monsterIdModeChanger = Mon.ModeChanger.monsterId;
        boolean isUsingCheatChange = false; // Since one is changing unknown face down monsters of the opponent, it is highly recommended NOT to use the cheat change!
        if (Game.isMainPhase() && Game.isAllowingCPUToPlay() && SummonedMonster.countOwnFaceDownMonsters(true, true)>=1 && AIEffects.hasWorkingMonsterEffect(false, monsterIdModeChanger)) {
            int posModeChanger = AIEffects.getPosOfCPUMonsterWithWorkingEffectByMonsterId(monsterIdModeChanger);
            if (posModeChanger!=0) {
                SummonedMonster EffectMonster = SummonedMonster.getNthSummonedMonster(posModeChanger, false);
                for (int index = 1; index <= 5; index++){
                    SummonedMonster PlayerMonster = SummonedMonster.getNthSummonedMonster(index, true);
                    if (PlayerMonster.isExistingButUnknown()) {
                        boolean hasWorked = tryPreparingAsSacrificeAnyCardExceptMonsters(new AIsacrifice());
                        if (hasWorked && AIEffects.cpuCanUseEffectModeChanger(EffectMonster, PlayerMonster) && Game.isMainPhase() && Game.isAllowingCPUToPlay()) {
                            AIEffects.cpuEffectModeChanger(EffectMonster, PlayerMonster, isUsingCheatChange, getNthSacrifice(1));
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    // Goal of this strategy: if the monster with the highest threat level has threat level 3, try getting rid of it by copying (by discarding) an Eradicator, a Big Back Bouncer or a Monster Stealer
    // returns true, if strategy worked
    public static boolean tryRemovingGreatestThreatByCopyCat() {
        Game.CPUbehavior.reevaluateStrategies();
        int monsterIdCopyCat = Mon.CopyCat.monsterId;
        if (Game.isMainPhase() && Game.isAllowingCPUToPlay() && SummonedMonster.isHavingThreatLv3Monster(true) && HandCPU.hasOptionalRemovalEffectOnHand()) {
            if (!AIEffects.hasWorkingMonsterEffect(false, monsterIdCopyCat) && HandCPU.numberOfCards>=2) {
                trySpecSumMidboss(true, monsterIdCopyCat, true); // can not be negated (just to be safe, try to gather sacrifices without hand cards)
            }
            return tryRemovingGreatestThreatByCopyCatAlreadyOnField(); // maybe meanwhile CPU has a working Copy Cat
        }
        return false;
    }
    public static boolean tryRemovingGreatestThreatByCopyCatAlreadyOnField() { // like above, just that here it is already (assumed to be) on field
        Game.CPUbehavior.reevaluateStrategies();
        int monsterIdCopyCat = Mon.CopyCat.monsterId;
        if (Game.isMainPhase() && Game.isAllowingCPUToPlay() && SummonedMonster.isHavingThreatLv3Monster(true) && AIEffects.hasWorkingMonsterEffect(false, monsterIdCopyCat) && HandCPU.numberOfCards>=1) {
            int posSkillStealer = AIEffects.getPosOfCPUMonsterWithWorkingEffectByMonsterId(monsterIdCopyCat);
            SummonedMonster EffectMonster = SummonedMonster.getNthSummonedMonster(posSkillStealer, false);
            for (int index = 1; index <= SummonedMonster.countOwnSummonedMonsters(true); index++){
                SummonedMonster NthStrongestMonster = SummonedMonster.getNthStrongestMonster(index, true);
                int cardNo = HandCPU.getPositionOfCardWithCardId(Card.EradicatorObstacle.cardId);
                if (cardNo==0) {cardNo = HandCPU.getPositionOfCardWithCardId(Card.BigBackBouncerBanisher.cardId);}
                if (cardNo==0) {cardNo = HandCPU.getPositionOfCardWithCardId(Card.MonsterStealerSteepLearningCurve.cardId);}
                if (cardNo!=0 && !NthStrongestMonster.isImmune()) {
                    int monsterId = HandCPU.getNthCardOfHand(cardNo).upMonster.monsterId;
                    if (AIEffects.cpuCanUseEffectCopyCat(EffectMonster, monsterId) && Game.isMainPhase() && Game.isAllowingCPUToPlay()) {
                        AIEffects.cpuEffectCopyCat(EffectMonster, monsterId);
                        return tryUsingRemovalEffectsAlreadyOnTheField();
                    }
                }
            }
        }
        return false;
    }
    
    // Goal of this strategy: if the monster with the highest threat level has threat level 3, try getting rid of it by copying own Or opponents greatest threats (like Eradicator, Big Back Bouncer, Monster Stealer, Neutraliser, Mode Changer) [likely able to copy effect of opponent]
    // One first tries using Copy Cat and then Skill Stealer, because this CPU behavior wants to keep Neutraliser on the hand.
    // returns true, if strategy worked
    public static boolean tryRemovingGreatestThreatBySkillStealer() {
        Game.CPUbehavior.reevaluateStrategies();
        int monsterIdSkillStealer = Mon.SkillStealer.monsterId;
        if (Game.isMainPhase() && Game.isAllowingCPUToPlay() && SummonedMonster.isHavingThreatLv3Monster(true) && SummonedMonster.hasOptionalCopyableRemovalEffect()) {
            if (!AIEffects.hasWorkingMonsterEffect(false, monsterIdSkillStealer) && HandCPU.numberOfCards>=2) { // if the CPU doesn't have working Skill Stealer on the field yet, try to summon it
                trySpecSumMidboss(true, monsterIdSkillStealer, true); // can not be negated (just to be safe, try to gather sacrifices without hand cards)
            }
            return tryRemovingGreatestThreatBySkillStealerAlreadyOnField(); // maybe meanwhile CPU has a working Skill Stealer
        }
        return false;
    }
    public static boolean tryRemovingGreatestThreatBySkillStealerAlreadyOnField() { // like above, just that here it is already (assumed to be) on field
        Game.CPUbehavior.reevaluateStrategies();
        int monsterIdSkillStealer = Mon.SkillStealer.monsterId;
        if (Game.isMainPhase() && Game.isAllowingCPUToPlay() && SummonedMonster.isHavingThreatLv3Monster(true) && AIEffects.hasWorkingMonsterEffect(false, monsterIdSkillStealer) && HandCPU.numberOfCards>=1) {
            int posSkillStealer = AIEffects.getPosOfCPUMonsterWithWorkingEffectByMonsterId(monsterIdSkillStealer);
            SummonedMonster EffectMonster = SummonedMonster.getNthSummonedMonster(posSkillStealer, false);
            for (int index = 1; index <= SummonedMonster.countOwnSummonedMonsters(true); index++){
                SummonedMonster NthStrongestMonster = SummonedMonster.getNthStrongestMonster(index, true);
                if (NthStrongestMonster.Monster.hasOptionalRemovalEffect() && AIEffects.cpuCanUseEffectSkillStealerByRevealingMonster(EffectMonster, NthStrongestMonster) && Game.isAllowingCPUToPlay()) {
                    AIEffects.cpuEffectSkillStealerByRevealingHandCard(EffectMonster, NthStrongestMonster, 1);
                    return tryUsingRemovalEffectsAlreadyOnTheField();
                }
            }
            for (int index = 1; index <= SummonedMonster.countOwnSummonedMonsters(false); index++){ // also try to copy own effects (they might have been negated, but can still be copied)
                SummonedMonster NthStrongestMonster = SummonedMonster.getNthStrongestMonster(index, false);
                if (NthStrongestMonster.Monster.hasOptionalRemovalEffect() && AIEffects.cpuCanUseEffectSkillStealerByRevealingMonster(EffectMonster, NthStrongestMonster) && Game.isAllowingCPUToPlay()) {
                    AIEffects.cpuEffectSkillStealerByRevealingHandCard(EffectMonster, NthStrongestMonster, 1);
                    return tryUsingRemovalEffectsAlreadyOnTheField();
                }
            }
        }
        return false;
    }
    
    // Goal of this strategy: If the opponent has a strong ENDBOSS in the GY, try to banish it using Big Banisher in order to prevent Revival.
    // Don't bother try to summon Big Banisher though. Just use it, if you happen to have the effect on the field.
    // returns true, if strategy worked
    public static boolean tryBanishingStrongMonsterFromGYAlreadyOnField() {
        int monsterIdBigBanisher = Mon.BigBanisher.monsterId;
        boolean hasWorked = tryPreparingAsSacrificeAnyCardExceptMonsters(new AIsacrifice());
        if (Game.isMainPhase() && Game.isAllowingCPUToPlay() && AIEffects.hasWorkingMonsterEffect(false, monsterIdBigBanisher) && hasWorked) {
            int posBigBanisher = AIEffects.getPosOfCPUMonsterWithWorkingEffectByMonsterId(monsterIdBigBanisher);
            SummonedMonster EffectMonster = SummonedMonster.getNthSummonedMonster(posBigBanisher, false);
            int cardId = Card.NeutraliserSkillStealer.cardId;
            int cardNo = GYDeckPlayer.getPositionOfCardWithCardIdInDeck(cardId);
            if (cardNo!=0) {return tryBanishingGYCard(EffectMonster, cardNo);}
            cardId = Card.EradicatorObstacle.cardId;
            cardNo = GYDeckPlayer.getPositionOfCardWithCardIdInDeck(cardId);
            if (cardNo!=0) {return tryBanishingGYCard(EffectMonster, cardNo);}
            cardId = Card.GodKillingSpearLance.cardId;
            cardNo = GYDeckPlayer.getPositionOfCardWithCardIdInDeck(cardId);
            if (cardNo!=0) {return tryBanishingGYCard(EffectMonster, cardNo);}
            cardId = Card.BigAttackStopperSword.cardId;
            cardNo = GYDeckPlayer.getPositionOfCardWithCardIdInDeck(cardId);
            if (cardNo!=0) {return tryBanishingGYCard(EffectMonster, cardNo);}
            cardId = Card.MonsterStealerSteepLearningCurve.cardId;
            cardNo = GYDeckPlayer.getPositionOfCardWithCardIdInDeck(cardId);
            if (cardNo!=0) {return tryBanishingGYCard(EffectMonster, cardNo);}
            cardId = Card.BigBurnerSuicideCommando.cardId;
            cardNo = GYDeckPlayer.getPositionOfCardWithCardIdInDeck(cardId);
            if (cardNo!=0) {return tryBanishingGYCard(EffectMonster, cardNo);}
        }
        return false;
    }
    
    // tries to banish a card in the opponents GY by using a given Summoned Monster (Big Banisher or copying its effect) 
    public static boolean tryBanishingGYCard (SummonedMonster EffectMonster, int cardNumberInGY) {
        if (Game.isMainPhase() && Game.isAllowingCPUToPlay()) {
            boolean hasWorked = tryPreparingAsSacrificeAnyCardExceptMonsters(new AIsacrifice());
            if (AIEffects.cpuCanUseEffectBigBanisher(EffectMonster, cardNumberInGY) && hasWorked) {
                AIEffects.cpuEffectBigBanisher(EffectMonster, cardNumberInGY, getNthSacrifice(1));
                return true;
            }
        }
        return false;
    }
    
    // btw, doesn't need a method for trying to get rid of mean effects with Neutraliser, because already does that as a reaction (which can then not be negated, that's why it's better anyway)
    
    // if everything else fails, use aggressive (overrunning) or defensive strategies (setting trap monsters)
    
    
    // --- mostly AGGRESSIVE STRATEGIES ---
    // is mostly just using basic attack strategies (like pierce to death etc.) or 2 burn strategies (Burner once, Big Burner several times in main phase 2)
    
    // If everything else fails, use removal methods from the balanced strategies.
    // After all, the strongest monsters, are the ENDBOSSES. They likely have a good removal effect.
    
    // Goal of this strategy: try to use any kind of optional removal effect to get rid of strongest monsters of the play
    // only call, if player controls a threat lv 3 monster
    public static boolean tryUsingRemovalEffects() {
        if (Game.isMainPhase() && Game.isAllowingCPUToPlay()) {
            boolean hasWorked = tryDestroyingGreatestThreatByEradicator();
            if (!hasWorked) {hasWorked = tryRemovingGreatestThreatByBigBackBouncer();}
            if (!hasWorked) {hasWorked = tryRemovingGreatestThreatByMonsterStealer();}
            if (!hasWorked) {hasWorked = tryRemovingGreatestThreatBySkillStealer();}
            if (!hasWorked) {hasWorked = tryRemovingGreatestThreatByCopyCat();}
            if (!hasWorked) {hasWorked = tryWeakeningGreatestThreatByModeChanger();}
            if (!hasWorked) {return tryBanishingStrongMonsterFromGYAlreadyOnField();}
        }
        return false;
    }
    
    // Goal of this strategy: since the balanced and aggressive CPU always want to have stong ENDBOSSES and MIDBOSSES on the field,
    // why not try to use the effects of Eradicator, Big Back Bouncer, Mode Changer (Big Banisher on Neutraliser in GY)
    // returns true, if strategy worked
    public static boolean tryUsingEffectsAlreadyOnTheField() {
        if (Game.isMainPhase() && Game.isAllowingCPUToPlay()) {
            boolean hasWorked = tryDestroyingGreatestThreatByEradicatorAlreadyOnField();
            if (!hasWorked) {hasWorked = tryRemovingGreatestThreatByBigBackBouncerAlreadyOnField();}
            if (!hasWorked) {hasWorked = tryWeakeningGreatestThreatByModeChangerAlreadyOnField();}
            if (!hasWorked) {hasWorked = tryRemovingGreatestThreatByMonsterStealerAlreadyOnField();}
            if (!hasWorked) {hasWorked = tryRemovingGreatestThreatBySkillStealerAlreadyOnField();}
            if (!hasWorked) {hasWorked = tryRemovingGreatestThreatByCopyCatAlreadyOnField();}
            if (!hasWorked) {hasWorked = tryModeChangeUnknownMonstersByModeChangerAlreadyOnField();}
            if (!hasWorked) {return tryBanishingStrongMonsterFromGYAlreadyOnField();}
        }
        return false;
    }
    public static boolean tryUsingRemovalEffectsAlreadyOnTheField() { // like above just excluding the copy effects and the Big Bansiher effect (also being called after copying an effect)
        if (Game.isMainPhase() && Game.isAllowingCPUToPlay()) {
            boolean hasWorked = tryDestroyingGreatestThreatByEradicatorAlreadyOnField();
            if (!hasWorked) {hasWorked = tryRemovingGreatestThreatByBigBackBouncerAlreadyOnField();}
            if (!hasWorked) {hasWorked = tryWeakeningGreatestThreatByModeChangerAlreadyOnField();}
            if (!hasWorked) {return tryRemovingGreatestThreatByMonsterStealerAlreadyOnField();}
        }
        return false;
    }
    
    // Goal of this strategy: try to summon 2 monsters with at least the same given amount of attack as possible (either given by player or estimated)
    // try a MIDBOSS first, then an ENDBOSS (ideally the one one has discarded for summoning the MIDBOSS)
    // returns true, if strategy worked
    public static boolean tryOverpoweringOpponent (int maxKnownRelValuePlayer) {
        boolean hasWorked=false;
        if (Game.isMainPhase() && Game.isAllowingCPUToPlay()) {
            YMonster Monster = HandCPU.getStrongestMonsterOnHand(maxKnownRelValuePlayer);
            if (Monster.monsterId!=0) {
                if (Monster.stars==2) { // if strongest monster on hand is MIDBOSS, summon it right away
                    boolean isUpperMonster = YCard.monsterIdBelongsToUpperMonster(Monster.monsterId);
                    hasWorked = trySpecSumMidboss(true, YCard.getCardIdByMonsterId(Monster.monsterId), isUpperMonster, false);
                }
                else if (Monster.stars==3) { // if strongest monster on hand is an ENDBOSS, try to use it as cost for strongest MIDBOSS
                    AIsacrifice HandCardAsSacrifice = AIsacrifice.markHandCardAsSacrificeByCardId(YCard.getCardIdByMonsterId(Monster.monsterId));
                    int cardId = HandCPU.cardIdOf2ndStrongestMonsterOnHandBeingAMidboss(HandCardAsSacrifice);
                    if (cardId==0) { // if there is no 2nd strongest, simply try to summon the strongest
                        boolean isUpperMonster = YCard.monsterIdBelongsToUpperMonster(Monster.monsterId);
                        hasWorked = trySpecSumMidboss(true, YCard.getCardIdByMonsterId(Monster.monsterId), isUpperMonster, false);
                    }
                    else {
                        YCard ConsCard = HandCPU.getCardOnHandByCardID(cardId);
                        boolean isUpperMonster = false;
                        if (ConsCard.upMonster.stars==2) {
                            isUpperMonster = true;
                        }
                        if (ConsCard.lowMonster.stars==2) {
                            if (isUpperMonster) {
                                if (ConsCard.lowMonster.att > ConsCard.upMonster.att) {
                                    isUpperMonster = true;
                                }
                            }
                            else {
                                isUpperMonster = false;
                            }
                        } // try to summon the MIDBOSS (hopefully for free)
                        SummonedMonster TargetMonster = SummonedMonster.determineFreeMonsterZone(false);
                        if (TargetMonster.sumMonsterNumber>0 && HandCPU.getPositionOfCardWithCardId(cardId)!=0) {
                            forgetAllSacrifices();
                            rememberSacrifice(HandCardAsSacrifice);
                            AIEffects.cpuSpecialSummonsMidbossFromHand(true, cardId, isUpperMonster, getNthSacrifice(1));
                            hasWorked = true;
                        }
                        if (hasWorked && Game.isMainPhase() && Game.isAllowingCPUToPlay()) { // try to summon the ENDBOSS (if this worked, then the MIDBOSS was summoned for free)
                            hasWorked = trySpecSumEndbossFromGY(true, Monster.monsterId);
                        }
                    }
                }
            }
        }
        return hasWorked;
    }
    
    // Goal of this strategy: if the opponent has no strong monster (all 2500 or less attack, no threat level 3), try summoning 5 monsters with at least 2000 attack
    // (or as much as possible) in main phase 1 as a preparation for overrunning in battle phase
    public static boolean trySummoningManyStrongestMonsters() {
        Game.CPUbehavior.reevaluateStrategies();
        if (Game.isMainPhase() && Game.isAllowingCPUToPlay()) {
            int maxRelValuePlayer = Game.BattleSituation.maxKnownRelValuePlayer;
            for (int index = 1; index <= 5; index++){
                trySummonStrongMonsters(maxRelValuePlayer);
            }
        }
        return false;
    }
    
    // in order not to repeat oneself, out-source here a loop for summoning the strongest monsters from hand and GY
    public static void trySummonStrongMonsters (int maxRelValuePlayer) {
        if (Game.isMainPhase() && Game.isAllowingCPUToPlay()) {
            tryOverpoweringOpponent(maxRelValuePlayer);
            for (int index = 1; index <= 5; index++){
                if (Game.isMainPhase() && Game.isAllowingCPUToPlay()) {
                    boolean hasWorked = trySpecSumStrongestEndbossInGY();
                    if (!hasWorked) { // if summoning of ENDBOSS from GY didn't work, try to summon one from hand
                        hasWorked = trySpecSumStrongestEndbossFromHand();
                    }
                    if (!hasWorked) {break;}
                }
            }
        }
    }
    
    // Goal of this strategy: find the ENDBOSS with the highest attack in the graveyard and try to summon it in attack mode
    public static boolean trySpecSumStrongestEndbossInGY() {
        if (Game.isMainPhase() && Game.isAllowingCPUToPlay()) {
            int cardNoInGY = GYDeckCPU.getCardNoOfStrongestEndbossInGY();
            if (cardNoInGY!=0) {
                YCard ConsCard = GYDeckCPU.getNthCardOfDeck(cardNoInGY) ;
                return trySpecSumEndbossFromGY(true, ConsCard.upMonster.monsterId);
            }
        }
        return false;
    }
    
    // Goal of this strategy: find the ENDBOSS with the highest attack on the hand and try to summon it in attack mode
    public static boolean trySpecSumStrongestEndbossFromHand() {
        if (Game.isMainPhase() && Game.isAllowingCPUToPlay()) {
            int cardNoOnHand = HandCPU.getCardNoOfStrongestEndbossOnHand();
            if (cardNoOnHand!=0) {
                YCard ConsCard = HandCPU.getNthCardOfHand(cardNoOnHand);
                return trySpecSumEndbossFromHand (true, ConsCard.upMonster.monsterId);
            }
        }
        return false;
    }
    
    // Goal of this strategy: try normal summon one of the two strongest MOOKs (in face up att)
    // returns true, if strategy worked
    public static boolean trySummonStrongMook() {
        if (AIEffects.cpuCanNormalSummonMook() && Game.isMainPhase() && Game.isAllowingCPUToPlay()) {
            int cardNo = HandCPU.getPositionOfCardWithCardId(Card.SlickRusherRecklessRusher.cardId);
            if (cardNo==0) {cardNo = HandCPU.getPositionOfCardWithCardId(Card.ModeChangerExhaustedExecutioner.cardId);}
            if (cardNo!=0) {
                AIEffects.cpuNormalSummonsMookWithCardNo(cardNo);
                return true;
            }
        }
        return false;
    }
    
    // -- PIERCING STRATEGIES --
    
    // Goal of this strategy: if the opponent has an invincible monster, try preparing piercing the opponent to death, prepare that strategy here (summon Holy lance and equip strongest monsters with Lance, hell even copy pierce effect, if it has to be)
    // This preparation only happens, if the CPU notices the invincible monster in main phase 1.
    public static boolean tryPreparingPierceToDeathStrategy() {
        Game.CPUbehavior.reevaluateStrategies();
        if (Game.isMainPhase() && Game.isAllowingCPUToPlay() && Game.CPUbehavior.isTryingPiercingStrategy) {
            tryEquipStrongestWithLanceFromHand();
        }
        if (Game.isMainPhase() && Game.isAllowingCPUToPlay() && Game.CPUbehavior.isTryingPiercingStrategy) {
            tryEquipStrongestWithLanceFromStack();
        }
        if (Game.isMainPhase() && Game.isAllowingCPUToPlay() && Game.CPUbehavior.isTryingPiercingStrategy) {
            tryEquipStrongestWithLanceFromMonster();
        }
        if (Game.isMainPhase() && Game.isAllowingCPUToPlay() && Game.CPUbehavior.isTryingPiercingStrategy) {
            trySpecSumMidboss(true, Mon.HolyLance.monsterId, false);
        }
        return false;
    }
    
    // Goal of this strategy: if opponent has invincible monster in def, try to equip the strongest monster with Lance (in order to pierce through it later)
    // returns true, if strategy worked
    public static boolean tryEquipStrongestWithLanceFromHand() {
        int noSumMonstersCPU = SummonedMonster.countOwnSummonedMonsters(false);
        int cardIdLance = Card.GodKillingSpearLance.cardId;
        Game.CPUbehavior.reevaluateStrategies();
        if (Game.isMainPhase() && Game.isAllowingCPUToPlay() && Game.CPUbehavior.isTryingPiercingStrategy && noSumMonstersCPU>=1) {
            SummonedMonster InvincibleMonster = SummonedMonster.getWeakestMonsterIndestructibleByBattle(true, false);
            int cardNo = HandCPU.getPositionOfCardWithCardId(cardIdLance);
            if (cardNo!=0 && InvincibleMonster.isExisting && InvincibleMonster.sumMonsterNumber!=0) {
                for (int index = 1; index <= noSumMonstersCPU; index++){ // try all monsters of the CPU from strongest to weakest (as long as they are still strong enough)
                    SummonedMonster MonsterCPU = SummonedMonster.getNthStrongestMonster(index, false);
                    if (!MonsterCPU.hasPiercingDamageAbility() && MonsterCPU.att > InvincibleMonster.def) {
                        if (AIEffects.cpuCanUseEquipEffectFromHand(MonsterCPU, cardIdLance)) {
                            AIEffects.cpuEquipEffectFromHand(MonsterCPU, cardIdLance);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    // Goal of this strategy: if opponent has invincible monster in def, try to equip the strongest monster with Lance (in order to pierce through it later)
    // Since one looks like always, that the monster doesn't have piercing damage yet, one doesn't have to check, if not already equipped with a non-negated Lance.
    // returns true, if strategy worked
    public static boolean tryEquipStrongestWithLanceFromStack() {
        int noSumMonstersCPU = SummonedMonster.countOwnSummonedMonsters(false);
        int cardIdLance = Card.GodKillingSpearLance.cardId;
        Game.CPUbehavior.reevaluateStrategies();
        if (Game.isMainPhase() && Game.isAllowingCPUToPlay() && Game.CPUbehavior.isTryingPiercingStrategy && noSumMonstersCPU>=1 && AIEffects.cpuCanUseEquipEffectFromStack()) {
            SummonedMonster InvincibleMonster = SummonedMonster.getWeakestMonsterIndestructibleByBattle(true, false);
            for (int index = 1; index <= noSumMonstersCPU; index++){ // try all monsters of the CPU from strongest to weakest (as long as they are still strong enough)
                SummonedMonster MonsterCPU = SummonedMonster.getNthStrongestMonster(index, false);
                if (!MonsterCPU.hasPiercingDamageAbility() && MonsterCPU.isEquippable() && MonsterCPU.att > InvincibleMonster.def) {
                    for (int n = 1; n <= 5; n++){
                        SummonedMonster SumMonster = SummonedMonster.getNthSummonedMonster(n, false);
                        EStack Stack = EStack.getNthStack(n, false);
                        if (SumMonster.isExisting && Stack.numberOfCards>=1) {
                            if (AIEffects.cpuCanUseEquipEffectFromStack(Stack, cardIdLance, MonsterCPU) && SumMonster.att<MonsterCPU.att) {
                                AIEffects.cpuEquipEffectFromStack(Stack, cardIdLance, MonsterCPU);
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    
    // Goal of this strategy: if opponent has invincible monster in def, try to equip the strongest monster with Lance (in order to pierce through it later)
    // Since one looks like always, that the monster doesn't have piercing damage yet, one doesn't have to check, if not already equipped with a non-negated Lance.
    // also check that the to be equipped monster has more attack than Lance itself (otherwise it wouldn't make much sense)
    // returns true, if strategy worked
    public static boolean tryEquipStrongestWithLanceFromMonster() {
        int noSumMonstersCPU = SummonedMonster.countOwnSummonedMonsters(false);
        Game.CPUbehavior.reevaluateStrategies();
        if (Game.isMainPhase() && Game.isAllowingCPUToPlay() && Game.CPUbehavior.isTryingPiercingStrategy && noSumMonstersCPU>=2 && AIEffects.cpuCanUseEquipEffectFromMonster()) {
            SummonedMonster InvincibleMonster = SummonedMonster.getWeakestMonsterIndestructibleByBattle(true, false);
            for (int n = 1; n <= 5; n++){
                SummonedMonster EquipMonster = SummonedMonster.getNthSummonedMonster(n, false);
                if (EquipMonster.Monster.equals(Mon.Lance) && AIEffects.cpuCanUseEquipEffectFromMonster(EquipMonster)) {
                    for (int index = 1; index <= noSumMonstersCPU; index++){ // try all monsters of the CPU from strongest to weakest (as long as they are still strong enough)
                        SummonedMonster MonsterCPU = SummonedMonster.getNthStrongestMonster(index, false);
                        if (!MonsterCPU.hasPiercingDamageAbility() && MonsterCPU.isEquippable() && MonsterCPU.att > InvincibleMonster.def && MonsterCPU.att > EquipMonster.att) {
                            if (AIEffects.cpuCanUseEquipEffectFromMonster(EquipMonster, MonsterCPU)) {
                                AIEffects.cpuEquipEffectFromMonster(EquipMonster, MonsterCPU);
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    
    // -- finally BURN STRATEGIES --
    //  (a potentially effective finisher for aggressive AI)
    
    // Goal of this strategy: if one could win by burning the opponent to death, prepare that strategy here
    // (i.e. summon Burner and Big Burner, and maybe even Necromancer, if possible)
    // returns true, if strategy worked
    public static boolean tryPreparingBurnToDeathStrategy() {
        boolean hasSummonedAtLeastOneBurningMonster=false;
        Game.CPUbehavior.reevaluateStrategies();
        if (Game.isMainPhase() && Game.isAllowingCPUToPlay() && Game.CPUbehavior.isTryingBurnStrategy) {
            // try summon Burner, if still needed
            if (!AIEffects.hasWorkingMonsterEffect(false, Mon.Burner.monsterId)) {
                hasSummonedAtLeastOneBurningMonster = trySpecSumMidboss(true, Mon.Burner.monsterId, false);
            }
            // try summon Big Burner, if still needed
            if (!AIEffects.hasWorkingMonsterEffect(false, Mon.BigBurner.monsterId)) {
                boolean hasWorked = trySpecSumEndbossFromHand(true, Mon.BigBurner.monsterId);
                if (hasWorked) {hasSummonedAtLeastOneBurningMonster=true;}
                hasWorked = trySpecSumEndbossFromGY(true, Mon.BigBurner.monsterId);
                if (hasWorked) {hasSummonedAtLeastOneBurningMonster=true;}
            }
        }
        if (Game.isMainPhase() && Game.isAllowingCPUToPlay() && (Game.CPUbehavior.isTryingBurnStrategy ||  AIEffects.hasWorkingMonsterEffect(false, Mon.BigBurner.monsterId) || AIEffects.hasWorkingMonsterEffect(false, Mon.Burner.monsterId))) {
            // try summon Necromancer, if useful
            trySpecSumMidboss(true, Mon.Necromancer.monsterId, false);
        }
        return hasSummonedAtLeastOneBurningMonster;
    }
    
    // Goal of this strategy: try to summon a strong MOOK from GY using Necromancer (can then beused to attack and maybe as a sacrifice to Big Burner or Burner)
    // Only try to use MOOKs with more than 500 att, beginning with most attack, and only if CPU has a working Burner on the field, and only if the player has 2000 or less life points or the once-per-turn-rule is switched off
    // (Try this move 5 times in a row. With standard settings this should be enough to finish the opponent. When plying with more life, the player has a higher chance to survive this.)
    // returns true, if strategy worked
    public static boolean trySummoningStrongMookUsingNecromancer() {
        Game.CPUbehavior.reevaluateStrategies();
        if (Game.isMainPhase() && Game.isAllowingCPUToPlay() && Game.CPUbehavior.isTryingBurnStrategy && AIEffects.cpuCanUseEffectNecromancerRevivesMook()) {
            if (AIEffects.hasWorkingMonsterEffect(false, Mon.BigBurner.monsterId) && (Game.lifePointsPlayer <= 2000 || !Game.isSwitchingOnOncePerTurnRule)) {// don't ask for the ability of using Big Burner already, because its tribute is still missing
                int cardNoInGY = GYDeckCPU.getCardNoOfStrongestMookInGY();
                if (cardNoInGY!=0) {
                    YCard ConsCard = GYDeckCPU.getNthCardOfDeck(cardNoInGY);
                    int mookMonsterId = ConsCard.lowMonster.monsterId;
                    AIsacrifice GYCardAsSacrifice = markGYCardAsSacrificeByCardNumber(cardNoInGY);
                    boolean hasWorked = tryPreparingSacrificeWorthOneHalfCard(GYCardAsSacrifice);
                    if (hasWorked) {
                        int posNecromancer = AIEffects.getPosOfCPUMonsterWithWorkingEffectByMonsterId(Mon.Necromancer.monsterId);
                        SummonedMonster EffectMonster = SummonedMonster.getNthSummonedMonster(posNecromancer, false);
                        if (AIEffects.cpuCanUseEffectNecromancerRevivesMook(EffectMonster, mookMonsterId)) {
                            AIEffects.cpuEffectNecromancerRevivesMook(EffectMonster, mookMonsterId, false, AIsacrifice.getNthSacrifice(1)); // just to be sure, summon MOOKs always in def
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    // Goal of this strategy: try to summon a strong MIDBOSS from GY using Necromancer (can then beused to attack and maybe as a sacrifice to Big Burner)
    // (Try this move 5 times in a row. With standard settings this should be enough to finish the opponent. When plying with more life, the player has a higher chance to survive this.)
    // returns true, if strategy worked
    public static boolean trySummoningStrongMidbossUsingNecromancer() {
        Game.CPUbehavior.reevaluateStrategies();
        int monsterIdBigBurner = Mon.BigBurner.monsterId;
        if (Game.isMainPhase() && Game.isAllowingCPUToPlay() && Game.CPUbehavior.isTryingBurnStrategy && AIEffects.cpuCanUseEffectNecromancerRevivesMidboss()) {
            if (AIEffects.hasWorkingMonsterEffect(false, monsterIdBigBurner) && (Game.lifePointsPlayer <= 2500 || !Game.isSwitchingOnOncePerTurnRule)) {// don't ask for the ability of using Big Burner already, because its tribute is still missing
                int posNecromancer = AIEffects.getPosOfCPUMonsterWithWorkingEffectByMonsterId(Mon.Necromancer.monsterId);
                SummonedMonster NecroMonster = SummonedMonster.getNthSummonedMonster(posNecromancer, false);
                int cardNoInGY = GYDeckCPU.getCardNoOfStrongestMidbossInGY();
                if (cardNoInGY!=0) {
                    YCard ConsCard = GYDeckCPU.getNthCardOfDeck(cardNoInGY);
                    int midbossMonsterId;
                    if (ConsCard.equals(Card.NeutraliserSkillStealer)) {
                        midbossMonsterId = ConsCard.lowMonster.monsterId;
                    }
                    else {
                        midbossMonsterId = ConsCard.upMonster.monsterId;
                    }
                    AIsacrifice GYCardAsSacrifice = markGYCardAsSacrificeByCardNumber(cardNoInGY);
                    boolean hasWorked = tryPreparingSacrificesWorthOneCard(GYCardAsSacrifice, false);
                    if (hasWorked) {
                        if (AIEffects.cpuCanUseEffectNecromancerRevivesMidboss(NecroMonster, midbossMonsterId)) {
                            AIEffects.cpuEffectNecromancerRevivesMidboss(NecroMonster, midbossMonsterId, true, AIsacrifice.getNthSacrifice(1), AIsacrifice.getNthSacrifice(2)); // always summon MIDBOSS in attac mode
                            // Here the strategy basically already worked. However, still try to tribute Necromancer itself. That's why don't return anything here yet.
                        }
                    }
                }
                // if everything breaks down and the player has 2500 or less life points, here Big Burner could also just tribute Necromancer
                if (Game.isMainPhase() && Game.isAllowingCPUToPlay() && AIEffects.hasWorkingMonsterEffect(false, Mon.BigBurner.monsterId) && (Game.lifePointsPlayer <= 2500)) {
                    forgetAllSacrifices();
                    AIsacrifice.markMonsterAsSacrifice(NecroMonster);
                    int posBigBurner = AIEffects.getPosOfCPUMonsterWithWorkingEffectByMonsterId(monsterIdBigBurner);
                    SummonedMonster EffectMonster = SummonedMonster.getNthSummonedMonster(posBigBurner, false);
                    if (AIEffects.cpuCanUseEffectBigBurner(EffectMonster, AIsacrifice.getNthSacrifice(1))) {
                        AIEffects.cpuEffectBigBurner(EffectMonster, AIsacrifice.getNthSacrifice(1)); // This would be an awesome finisher, if that ever goes through!
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    // Goal of this strategy: Try to win by using all kinds of burn damage strategies instead of attack in main phase two,
    // after one has attacked safely with the monsters needed for this strategy.
    // returns true, if strategy worked
    public static boolean tryBurnToDeatStrategyInMainPhase2() {
        // rough plan:
        // burn everything with Big Burner except Necromancer & Burner (can not burn itself anyway)
        // try to revive as much as possible with Necromancer and burn it again using Big Burner
        // if it would win the game, even burn Necromancer away 
        // burn best MOOK in GY by Burner
        // finally burn away Burner using Big Burner
        boolean hasWorkedBurn;
        boolean hasWorkedAllInAll=false;
        if (Game.isMainPhase() && Game.isAllowingCPUToPlay() && Game.CPUbehavior.isTryingBurnStrategy) {
            // burn
            hasWorkedBurn = tryBurnToDeathByBigBurnerInMainPhase2();
            if (hasWorkedBurn) {hasWorkedAllInAll=true;}
            // summon MOOKS
            for (int index = 1; index <= 3; index++){
                trySummoningStrongMookUsingNecromancer();
            }
            // burn
            hasWorkedBurn = tryBurnToDeathByBigBurnerInMainPhase2();
            if (hasWorkedBurn) {hasWorkedAllInAll=true;}
            // summon MIDBOSSES
            for (int index = 1; index <= 3; index++){
                trySummoningStrongMidbossUsingNecromancer();
            }
            // burn
            hasWorkedBurn = tryBurnToDeathByBigBurnerInMainPhase2();
            if (hasWorkedBurn) {hasWorkedAllInAll=true;}
        }
        // grand final
        hasWorkedBurn = tryBurnToDeathByBurnerInMainPhase2();
        if (hasWorkedBurn) {hasWorkedAllInAll=true;}
        hasWorkedBurn = finallyTryToTributeBurnerToBigBurnerInMainPhase2();
        if (hasWorkedBurn) {hasWorkedAllInAll=true;}
        return hasWorkedAllInAll;
    }
    
    // Goal of this strategy: after having attacked with all powerful monsters, check if can win by burning them all away using Big Burner in main phase 2
    // If possible, burn aways the monsters that probably wouldn't survive the next turn anyway.
    // returns true, if strategy worked
    public static boolean tryBurnToDeathByBigBurnerInMainPhase2() {
        int monsterIdBigBurner = Mon.BigBurner.monsterId;
        int noOfBurnedMonsters=0;
        if (Game.isMainPhase() && Game.isAllowingCPUToPlay() && Game.CPUbehavior.isTryingBurnStrategy && AIEffects.cpuCanUseEffectBigBurner()) {
            int posBigBurner = AIEffects.getPosOfCPUMonsterWithWorkingEffectByMonsterId(monsterIdBigBurner);
            if (posBigBurner!=0) {
                // simulate maximum amount of burn damage
                SummonedMonster EffectMonster = SummonedMonster.getNthSummonedMonster(posBigBurner, false);
                int sumOfOtherAttValues=0;
                for (int index = 1; index <= 5; index++){
                    SummonedMonster MonsterCPU = SummonedMonster.getNthSummonedMonster(index, false);
                    if (index != posBigBurner) {
                        sumOfOtherAttValues = sumOfOtherAttValues + MonsterCPU.att;
                    }
                }
                if (sumOfOtherAttValues >= Game.lifePointsPlayer) { // if the sum of the attack values of all ohter monsters suffice, try it
                    // Try to burn from weakest to strongest monster skipping every Burner for now: They will be burned later.
                    for (int index = 5; index >= 1; index--){
                        SummonedMonster MonsterCPU = SummonedMonster.getNthStrongestMonster(index, false);
                        if (MonsterCPU.isExisting && !MonsterCPU.isBasicallySameMonster(EffectMonster) && !MonsterCPU.Monster.equals(Mon.Burner)) {
                            if (Game.isMainPhase() && Game.isAllowingCPUToPlay() && AIEffects.hasWorkingMonsterEffect(false, Mon.BigBurner.monsterId) && (MonsterCPU.att > 0)) { // only worth burning, if non-zero attack
                                forgetAllSacrifices();
                                AIsacrifice.markMonsterAsSacrifice(MonsterCPU);
                                if (AIEffects.cpuCanUseEffectBigBurner(EffectMonster, AIsacrifice.getNthSacrifice(1))) {
                                    AIEffects.cpuEffectBigBurner(EffectMonster, AIsacrifice.getNthSacrifice(1));
                                    noOfBurnedMonsters++;
                                }
                            }
                        }
                    }
                }
            }
        }
        return (noOfBurnedMonsters>=1);
    }
    
    // Goal of this strategy: If it would win the computer the game, tribute Burner to Big Burner.
    // This would probably the best finisher the CPU can do, after verything else resolved.
    // returns true, if strategy worked
    public static boolean finallyTryToTributeBurnerToBigBurnerInMainPhase2() {
        Game.CPUbehavior.reevaluateStrategies();
        int posBurner=0;
        int monsterIdBigBurner = Mon.BigBurner.monsterId;
        if (Game.isMainPhase() && Game.isAllowingCPUToPlay() && Game.CPUbehavior.isTryingBurnStrategy && AIEffects.cpuCanUseEffectBigBurner()) {
            for (int index = 1; index <= 5; index++){
                SummonedMonster SumMonster = SummonedMonster.getNthSummonedMonster(index, false);
                if (SumMonster.isExisting && SumMonster.Monster.equals(Mon.Burner)) {
                    posBurner = SumMonster.sumMonsterNumber;
                }
            }
        }
        if (posBurner!=0) {
            SummonedMonster SumMonster = SummonedMonster.getNthSummonedMonster(posBurner, false);
            if (AIEffects.hasWorkingMonsterEffect(false, Mon.BigBurner.monsterId) && (Game.lifePointsPlayer <= SumMonster.att)) {
                forgetAllSacrifices();
                AIsacrifice.markMonsterAsSacrifice(SummonedMonster.getNthSummonedMonster(posBurner, false));
                int posBigBurner = AIEffects.getPosOfCPUMonsterWithWorkingEffectByMonsterId(monsterIdBigBurner);
                SummonedMonster EffectMonster = SummonedMonster.getNthSummonedMonster(posBigBurner, false);
                if (AIEffects.cpuCanUseEffectBigBurner(EffectMonster, AIsacrifice.getNthSacrifice(1))) {
                    AIEffects.cpuEffectBigBurner(EffectMonster, AIsacrifice.getNthSacrifice(1)); // This would be an awesome finisher, if that ever goes through!
                    return true;
                }
            }
        }
        return false;
    }
    
    // Goal of this strategy: after having almost burned opponent to death with Big Burner (so that oppnent ideally has 2000 LP or less)
    // try recycling the burned monsters and burn away a 2000 attack MOOK from GY the end of main phase 2
    // returns true, if strategy worked
    public static boolean tryBurnToDeathByBurnerInMainPhase2() {
        int simulatedBurnDamage=0;
        AIsacrifice CardToBeBurnedAsSacrifice = new AIsacrifice();
        if (Game.isMainPhase() && Game.isAllowingCPUToPlay() && Game.CPUbehavior.isTryingBurnStrategy && AIEffects.cpuCanUseEffectBurner()) {
            CardToBeBurnedAsSacrifice = AIsacrifice.prepareSacrificeForBurner();
            if (CardToBeBurnedAsSacrifice.isValidSacrificeAndContainsMook()) {
                simulatedBurnDamage = CardToBeBurnedAsSacrifice.extractCardFromValidSacrifice().lowMonster.att;
            }
        }
        int monsterIdBurner = Mon.Burner.monsterId;
        int posSkillStealer = AIEffects.getPosOfCPUMonsterWithWorkingEffectByMonsterId(monsterIdBurner);
        if (posSkillStealer!=0) {
            SummonedMonster EffectMonster = SummonedMonster.getNthSummonedMonster(posSkillStealer, false);
            if (simulatedBurnDamage>0 && AIEffects.cpuCanUseEffectBurner(EffectMonster, CardToBeBurnedAsSacrifice)) {
                AIEffects.cpuEffectBurner(EffectMonster, CardToBeBurnedAsSacrifice);
                return true;
            }
        }
        return false;
    }
    
    
    
}
