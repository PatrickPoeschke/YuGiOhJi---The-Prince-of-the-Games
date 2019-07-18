package yugiohji;

/**
 * This class requests the Computer to make decisions
 * according to the defensive AI behavior.
 * The computer will mostly set monsters.
 * Only after very strict criteria are fulfilled the computer,
 * the computer knows that it can safely attack,
 * it tries to overrun the player
 * by attacking with many monsters in one battle phase.
 * 
 */

import static yugiohji.YuGiOhJi.Card;
import static yugiohji.YuGiOhJi.Game;
import static yugiohji.YuGiOhJi.Mon;

public class AIdefensive {
    
    // for everything that the CPU tries, only do something, if the player is not still negating
    // that means, put the code of everything that the CPU tries behind code of the kind
    // if (Game.isAllowingCPUToPlay())) {}
    
    // rough plan:
    // 1st try to summon the MIDBOSSES and ENDBOSSES
    // 2nd look, if can win for game (if yes, try it)
    // 3rd try to search/summon/cheat-change MOOKS
    // 4th look, if can do a lot of damage (if yes, try it)
    // then do battle phase with usually standard behavior
    // then in main phase 2: if still normal summon, set something
    // try turning monsters into defence mode as well as possible
    // also try to repeat strategies from main phase 1
    
    public static void cpuPlaysMainPhase1Defensively() {
        if (Game.isAllowingCPUToPlay()) {
            AIstrategies.exceptionalTopDeckStrategy();
        }
        if (Game.isAllowingCPUToPlay()) {
            if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try neutralising Neutraliser");}
            AIstrategies.tryNeutralisingNeutraliser();
        }
        if (Game.isAllowingCPUToPlay()) {
            if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try searching out Barrier");}
            AIstrategies.trySearchingBarrierWithHolyLance();
        }
        if (Game.isAllowingCPUToPlay()) {
            if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try searching out Banisher");}
            AIstrategies.trySearchingBanisherWithRusher();
        }
        if (Game.isAllowingCPUToPlay() && SummonedMonster.countOwnSummonedMonsters(false)>=3) {
            if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try tribute summoning a GOD");}
            AIstrategies.tryTributeSummoningAGod();
        }
        if (Game.isAllowingCPUToPlay()) {
            if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try uncovering a card");}
            AIstrategies.tryUncoverFaceDownCard();
        }
        if (Game.isAllowingCPUToPlay()) {
            if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try setting stall/trap monster");}
            AIstrategies.trySettingAStallOrTrapMonster();
        }
        Game.CPUbehavior.reevaluateStrategies();
        if (Game.isAllowingCPUToPlay() && Game.CPUbehavior.isTryingRemovalByEffectStrategy) {
            if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try using optional removal effect");}
            AIstrategies.tryUsingRemovalEffects(); // try removal 5 times, just to be sure
            AIstrategies.tryUsingRemovalEffects();
            AIstrategies.tryUsingRemovalEffects();
            AIstrategies.tryUsingRemovalEffects();
            AIstrategies.tryUsingRemovalEffects();
        }
        // non-removal strategies
        Game.CPUbehavior.switchRemovalStrategyOff();
        if (Game.isAllowingCPUToPlay() && SummonedMonster.countOwnFaceDownMonsters(true, true)>=1) {
            if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try to mode change unknown face down monsters");}
            AIstrategies.tryModeChangeUnknownMonstersByModeChanger();
        }
        if (Game.isAllowingCPUToPlay()) {
            if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try spec. sum. MIDBOSS");}
            AIstrategies.trySpecSumMidboss(false, Mon.AttackStopper.monsterId, false); // Attack Stopper in def. mode
        }
        if (Game.isAllowingCPUToPlay()) {
            if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try equipping with Shield from hand");}
            AIstrategies.tryEquipWithShieldFromHand();
        }
        if (Game.isAllowingCPUToPlay()) {
            if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try equipping with Shield from equip stack");}
            AIstrategies.tryEquipWithShieldFromStack();
        }
        if (Game.isAllowingCPUToPlay()) {
            if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try spec. sum. ENDBOSS from GY");}
            AIstrategies.trySpecSumEndbossFromGY(true, Mon.BigAttackStopper.monsterId); // Big Attack Stopper in att. mode
        }
        if (Game.isAllowingCPUToPlay()) {
            if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try spec. sum. MIDBOSS");}
            AIstrategies.trySpecSumMidboss(false, Mon.Flakship.monsterId, false); // FLAKship in def. mode
        }
        if (Game.isAllowingCPUToPlay()) {
            if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try spec. sum. ENDBOSS");}
            AIstrategies.trySpecSumEndbossFromHand(true, Mon.BigAttackStopper.monsterId); // Big Attack Stopper in att. mode
        }
        Game.CPUbehavior.reevaluateStrategies();
        if (Game.isAllowingCPUToPlay() && Game.isSwitchingOnCheatModeChangingRule) {
            if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try cheat change Barrier");}
            AIstrategies.tryCheatChangeGodBarrier();
        }
        if (Game.isAllowingCPUToPlay() && Game.isSwitchingOnCheatModeChangingRule) {
            if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try cheat change Obstacle");}
            AIstrategies.tryCheatChangeEradicatorObstacle();
        }
        if (Game.isAllowingCPUToPlay() && Game.isSwitchingOnCheatModeChangingRule) {
            if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try cheat change Demon");}
            AIstrategies.tryCheatChangeDemonGodDemon();
        }
        if (Game.isAllowingCPUToPlay() && Game.isSwitchingOnCheatModeChangingRule && Game.CPUbehavior.isTryingRemovalByEffectStrategy) {
            Game.CPUbehavior.reevaluateStrategies(); // look for possible removal, because maybe just cheat changed some powerful monsters with removal effects
            if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try using optional removal effect");}
            AIstrategies.tryUsingRemovalEffects(); // try removal 5 times, just to be sure
            AIstrategies.tryUsingRemovalEffects();
            AIstrategies.tryUsingRemovalEffects();
            AIstrategies.tryUsingRemovalEffects();
            AIstrategies.tryUsingRemovalEffects();
        }
        if (Game.isAllowingCPUToPlay() && Game.CPUbehavior.isTryingRemovalByEffectStrategy) {
            if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try weakening monster by changing mode");}
            AIstrategies.tryModeChangeUnknownMonstersByModeChanger();
        }
        if (!Game.isVeryFirstTurn()) { // In the very 1st turn one can not attack anyway. Thus don't bother analysing battle situation.
            if (Game.isAllowingCPUToPlay()) {
                if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("check, if can deal a lot of damage with strongest monster alone after changing modes");}
                int simulatedDamage = AIbattle.dealableBattleDamageWithStrongestMonster(true);
                // if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("simulated total battle damage: " + simulatedDamage);} // There is this slight issue, that non-aggressive CPUs want to attack safely and Incorruptible is not considered such a strong monster. That's why the CPU wants to attack with others first and this the simulated battle damage is not the correct one. Thus better just hide it.
                if (simulatedDamage >= Game.CPUbehavior.aLotOfDamage) {
                    if (simulatedDamage >= Game.lifePointsPlayer) {Game.CPUbehavior.isTryingAttackForGameStrategy=true;}
                    if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try changing modes, if needed");}
                    AIbattle.ordinaryChangeToAtt();
                    AIdelegate.cpuAttemptsToEndPhase();
                }
            }
            if (Game.isAllowingCPUToPlay()) {
                if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("check, if can defeat a monster without mode changes");}
                int simulatedDamage = AIbattle.dealableBattleDamageByAttackingWithoutChanges(true);
                // if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("simulated total battle damage: " + simulatedDamage);}
                if (AIbattle.canDefeatOneMonsterWithoutChanges(true)) {
                    if (simulatedDamage >= Game.lifePointsPlayer) {Game.CPUbehavior.isTryingAttackForGameStrategy=true;}
                    AIdelegate.cpuAttemptsToEndPhase();
                }
            }
            if (Game.isAllowingCPUToPlay()) {
                if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("check, if can defeat a monster after changing modes");}
                int simulatedDamage = AIbattle.dealableBattleDamageByAttackingWithOrdinaryModeChanges(true);
                // if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("simulated total battle damage: " + simulatedDamage);}
                if (AIbattle.canDefeatOneMonsterWithChanges(true)) {
                    if (simulatedDamage >= Game.lifePointsPlayer) {Game.CPUbehavior.isTryingAttackForGameStrategy=true;}
                    if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try changing modes, if needed");}
                    AIbattle.ordinaryChangeToAtt();
                    AIdelegate.cpuAttemptsToEndPhase();
                }
            }
        }
        if (Game.isAllowingCPUToPlay()) { // Although this might look like this is sometimes not progressing, it's ok.
            AIdelegate.cpuAttemptsToEndPhase(); // The CPU is asked to continue playing the phase until it finally goes uninterrupted to this point.
        }
        
    }
    
    
    // Important note about the battle phase:
    // One has to check if it is still the battle phase before each action,
    // because any player could have discarded Big Attack Stopper, which immediately ends the battle phase!
    public static void cpuPlaysBattlePhaseDefensively() {
        if (Game.isBattlePhase() && Game.isAllowingCPUToPlay()) {
            boolean isTryingToAttackForGame = Game.CPUbehavior.isTryingAttackForGameStrategy;
            if (Game.isSwitchingOnStrategyDialogs && isTryingToAttackForGame) {YuGiOhJi.debugDialog("try to attack for game");}
            AIbattle.attackStrategy(!isTryingToAttackForGame, false);
        }
        if (Game.isBattlePhase() && Game.isAllowingCPUToPlay()) { // Although this might look like this is sometimes not progressing, it's ok.
            AIdelegate.cpuAttemptsToEndPhase(); // The CPU is asked to continue playing the phase until it finally goes uninterrupted to this point.
        }
    }
    
    
    // During the 2nd main phase the computer can not prepare for attack.
    // Thus it can only try to set a monster, if it has not done so for some weird reason.
    // (This case shouldn't actually occour, but just to be safe use this phase.)
    // Btw, since setting monsters with suicidal effects is somewhat remotely similar to setting backrow in Yu-Gi-Oh!,
    // and in Yu-Gi-Oh! this is what is usually done directly before ending the turn,
    // it makes a bit sense to do the same here.
    public static void cpuPlaysMainPhase2Defensively() {
        if (Game.isAllowingCPUToPlay()) {
            AIstrategies.tryUncoverFaceDownCard();
        }
        if (Game.isAllowingCPUToPlay()) {
            if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try neutralising " + Mon.Neutraliser.monsterName);}
            AIstrategies.tryNeutralisingNeutraliser();
        }
        if (Game.isAllowingCPUToPlay()) {
            if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try searching out " + Mon.Barrier.monsterName);}
            AIstrategies.trySearchingBarrierWithHolyLance();
        }
        if (Game.isAllowingCPUToPlay()) {
            if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try searching out " + Mon.Banisher.monsterName);}
            AIstrategies.trySearchingBanisherWithRusher();
        }
        if (Game.hasStillNormalSummonCPU) {
            if (Game.isAllowingCPUToPlay()) {
                if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try setting stall/trap monster");}
                AIstrategies.trySettingAStallOrTrapMonster();
            }
            if (Game.isAllowingCPUToPlay()) {
                if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try summoning any MIDBOSS");}
                AIstrategies.exceptionalStrategyTrySummonAnyMidboss();
            }
        }
        if (Game.isAllowingCPUToPlay() && SummonedMonster.countOwnSummonedMonsters(false)<=1) {
            if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try summoning any MIDBOSS");}
            AIstrategies.exceptionalStrategyTrySummonAnyMidboss();
        }
        if (Game.isAllowingCPUToPlay()) {
            if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try spec. sum. ENDBOSS");}
            AIstrategies.trySpecSumEndbossFromHand(true, Mon.BigAttackStopper.monsterId); // Big Attack Stopper in att. mode
        }
        if (Game.isAllowingCPUToPlay()) {
            if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try turning weak monsters into def");}
            int threshold = (int) Math.max(2000, AIbattle.maxKnownAttValuePlayer(Game.isSwitchingOnCheatModeChangingRule));
            AIstrategies.tryTurningWeakMonstersIntoDef(threshold);
        }
        if (Game.isAllowingCPUToPlay() && Game.isSwitchingOnCheatModeChangingRule) {
            if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try cheat change " + Mon.Barrier.monsterName);}
            AIstrategies.tryCheatChangeGodBarrier();
        }
        if (Game.isAllowingCPUToPlay() && Game.isSwitchingOnCheatModeChangingRule) {
            if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try cheat change " + Mon.Obstacle.monsterName);}
            AIstrategies.tryCheatChangeEradicatorObstacle();
        }
        if (Game.isAllowingCPUToPlay() && Game.isSwitchingOnCheatModeChangingRule) {
            if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try cheat change " + Mon.Demon.monsterName);}
            AIstrategies.tryCheatChangeDemonGodDemon();
        }
        if (Game.isAllowingCPUToPlay() && Game.isSwitchingOnCheatModeChangingRule) {
            if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try cheat change " + Mon.Lance.monsterName + " from def. mode");}
            AIstrategies.tryCheatChangeGodKillingSpearLance();
        }
        if (Game.isAllowingCPUToPlay() && Game.isSwitchingOnCheatModeChangingRule && Game.CPUbehavior.isTryingRemovalByEffectStrategy) {
            Game.CPUbehavior.reevaluateStrategies(); // look for possible removal, because maybe just cheat changed some powerful monsters with removal effects
            if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try using optional removal effect");}
            AIstrategies.tryUsingRemovalEffects(); // try removal 5 times, just to be sure
            AIstrategies.tryUsingRemovalEffects();
            AIstrategies.tryUsingRemovalEffects();
            AIstrategies.tryUsingRemovalEffects();
            AIstrategies.tryUsingRemovalEffects();
        }
        if (Game.isAllowingCPUToPlay()) { // Although this might look like this is sometimes not progressing, it's ok.
            AIdelegate.cpuAttemptsToEndPhase(); // The CPU is asked to continue playing the phase until it finally goes uninterrupted to this point.
        }
        
    }
    
    // returns true, if a summoned monster is considered expendable by the defensive CPU behavior (thus can be used to pay for effects)
    public static boolean isExpendableCardOnField (SummonedMonster SumMonster) {
        if (Game.isSwitchingOnAdditionalRecklessness) {
            return true;
        }
        return (SumMonster.AIinfoThreatLv < 3);
    }
    
    // returns true, if a card with a given card ID is expendable when on hand of the defensive CPU behavior (thus can be used to pay for effects)
    public static boolean isExpendableCardOnHand (YCard ConsCard) {
        if (Game.isSwitchingOnAdditionalRecklessness) {
            return true;
        }
        boolean isUsingRemovalStrategy = Game.CPUbehavior.isTryingRemovalByEffectStrategy;
        boolean isUsingStandardStrategy = !isUsingRemovalStrategy;
        if (isUsingStandardStrategy) {
            return (ConsCard.equals(Card.GodKillingSpearLance) || ConsCard.equals(Card.MonsterStealerSteepLearningCurve) || ConsCard.equals(Card.CopyCatCardGrabber) || ConsCard.equals(Card.BigAttackStopperSword) || ConsCard.equals(Card.ModeChangerExhaustedExecutioner) || ConsCard.equals(Card.IncorruptibleHolyLance) || ConsCard.equals(Card.BigBanisherBurner) || ConsCard.equals(Card.DemonGodDemon));
        }
        else if (isUsingRemovalStrategy) {
            return (ConsCard.equals(Card.GodBarrier) || ConsCard.equals(Card.EradicatorObstacle) || ConsCard.equals(Card.GodKillingSpearLance) || ConsCard.equals(Card.BigAttackStopperSword) || ConsCard.equals(Card.DiamondSwordShield) || ConsCard.equals(Card.NecromancerBackBouncer) || ConsCard.equals(Card.BigBanisherBurner) || ConsCard.equals(Card.BigBurnerSuicideCommando) || ConsCard.equals(Card.FlakshipNapalm) || ConsCard.equals(Card.DemonGodDemon));
        }
        return false; // shouldn't happen
    }
    
    // returns true, if a card with a given card ID is expendable when in GY of the defensive CPU behavior (thus can be used to pay for effects)
    public static boolean isExpendableCardInGY (YCard ConsCard) {
        if (Game.isSwitchingOnAdditionalRecklessness) {
            return true;
        }
        boolean isUsingRemovalStrategy = Game.CPUbehavior.isTryingRemovalByEffectStrategy;
        boolean isUsingStandardStrategy = !isUsingRemovalStrategy;
        if (isUsingStandardStrategy) {
            return (ConsCard.equals(Card.MonsterStealerSteepLearningCurve) || ConsCard.equals(Card.CopyCatCardGrabber) || ConsCard.equals(Card.ModeChangerExhaustedExecutioner) || ConsCard.equals(Card.SlickRusherRecklessRusher) || ConsCard.equals(Card.IncorruptibleHolyLance) || ConsCard.equals(Card.BigBanisherBurner) || ConsCard.equals(Card.DemonGodDemon));
        }
        else if (isUsingRemovalStrategy) {
            return (!ConsCard.equals(Card.EradicatorObstacle) && !ConsCard.equals(Card.NeutraliserSkillStealer) && !ConsCard.equals(Card.BigAttackStopperSword));
        }
        return false; // shouldn't happen
        
    }
    
    
}
