package yugiohji;

/**
 * This class requests the Computer to make decisions
 * according to the balanced AI behavior.
 * The computer will mostly summon monsters with good removal effects,
 * but also still keep some cards on the hand.
 * Either to use them as hand traps
 * or as costs for on field interruptions
 * or as costs for effects/summonings in the next turn.
 * Also this AI will attack, even if it doesn't have many monsters,
 * if the attacks might achieve something.
 * 
 */

import static yugiohji.YuGiOhJi.Card;
import static yugiohji.YuGiOhJi.Game;
import static yugiohji.YuGiOhJi.Mon;

public class AIbalanced {
    
    public static void cpuPlaysMainPhase1Balanced() {
        if (Game.isAllowingCPUToPlay()) {
            AIstrategies.exceptionalTopDeckStrategy();
        }
        Game.CPUbehavior.reevaluateStrategies();
        if (Game.isAllowingCPUToPlay()) {
            if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try neutralising " + Mon.Neutraliser.monsterName);}
            AIstrategies.tryNeutralisingNeutraliser();
        }
        Game.CPUbehavior.reevaluateStrategies();
        if (Game.isAllowingCPUToPlay() && Game.CPUbehavior.isTryingRemovalByEffectStrategy) {
            if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try using optional removal effect");}
            AIstrategies.tryUsingRemovalEffects(); // try removal 5 times, just to be sure (This is the very core of this whole AI behavior)
            AIstrategies.tryUsingRemovalEffects();
            AIstrategies.tryUsingRemovalEffects();
            AIstrategies.tryUsingRemovalEffects();
            AIstrategies.tryUsingRemovalEffects();
        }
        // add summoning of 2 strong monsters here
        if (Game.isAllowingCPUToPlay() && !Game.CPUbehavior.isTryingRemovalByEffectStrategy) {
            if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try overpowering opponent");}
            int maxRelValuePlayer;
            if (SummonedMonster.countOwnSummonedMonsters(true)==0) {
                maxRelValuePlayer=2000; // if player has no monsters yet, try to summon some that can overpower those with 2000 att
            }
            else {
                maxRelValuePlayer = Game.BattleSituation.maxKnownRelValuePlayer;
            }
            AIstrategies.tryOverpoweringOpponent(maxRelValuePlayer);
        }
        if (Game.isAllowingCPUToPlay() && SummonedMonster.countOwnFaceDownMonsters(true, true)>=1) {
            if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try to mode change unknown face down monsters");}
            AIstrategies.tryModeChangeUnknownMonstersByModeChanger();
        }
        // we have only resources left to just play around, if the player doesn't have any real threat right now
        if (Game.isAllowingCPUToPlay() && !Game.CPUbehavior.isTryingRemovalByEffectStrategy) {
            if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try using effect already on the field");}
            AIstrategies.tryUsingEffectsAlreadyOnTheField();
        }
        if (Game.isAllowingCPUToPlay() && !Game.CPUbehavior.isTryingRemovalByEffectStrategy && Game.hasStillNormalSummonCPU && SummonedMonster.countOwnSummonedMonsters(false) < 3) {
            if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try setting stall/trap monster");}
            AIstrategies.trySettingAStallOrTrapMonster();
        }
        if (Game.isAllowingCPUToPlay() && !Game.CPUbehavior.isTryingRemovalByEffectStrategy) {
            if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try summoning any MIDBOSS");}
            AIstrategies.exceptionalStrategyTrySummonAnyMidboss();
        }
        if (Game.isAllowingCPUToPlay() && Game.CPUbehavior.isTryingRemovalByEffectStrategy) {
            if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try weakening monster by changing mode");}
            AIstrategies.tryModeChangeUnknownMonstersByModeChanger();
        }
        if (!Game.isVeryFirstTurn()) { // In the very 1st turn one can not attack anyway. Thus don't bother analysing battle situation.
            if (Game.isAllowingCPUToPlay()) {
                if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("check, if can deal a lot of damage after changing modes");}
                int simulatedDamage = AIbattle.dealableBattleDamageByAttackingWithOrdinaryModeChanges(true);
                // if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("simulated total battle damage: " + simulatedDamage);} // There is this slight issue, that non-aggressive CPUs want to attack safely and Incorruptible is not considered such a strong monster. That's why the CPU wants to attack with others first and this the simulated battle damage is not the correct one. Thus better just hide it.
                if (simulatedDamage >= Game.CPUbehavior.aLotOfDamage) {
                    if (simulatedDamage >= Game.lifePointsPlayer) {Game.CPUbehavior.isTryingAttackForGameStrategy=true;}
                    if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try changing modes, if needed");}
                    AIbattle.ordinaryChangeToAtt();
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
    public static void cpuPlaysBattlePhaseBalanced() {
        if (Game.isBattlePhase() && Game.isAllowingCPUToPlay()) {
            boolean isTryingToAttackForGame = Game.CPUbehavior.isTryingAttackForGameStrategy;
            if (Game.isSwitchingOnStrategyDialogs && isTryingToAttackForGame) {YuGiOhJi.debugDialog("try to attack for game");}
            AIbattle.attackStrategy(!isTryingToAttackForGame, false);
        }
        if (Game.isBattlePhase() && Game.isAllowingCPUToPlay()) { // Although this might look like this is sometimes not progressing, it's ok.
            AIdelegate.cpuAttemptsToEndPhase(); // The CPU is asked to continue playing the phase until it finally goes uninterrupted to this point.
        }
    }
    
    
    public static void cpuPlaysMainPhase2Balanced() {
        if (Game.isAllowingCPUToPlay()) {
            AIstrategies.tryUncoverFaceDownCard();
        }
        if (Game.isAllowingCPUToPlay()) {
            if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try neutralising " + Mon.Neutraliser.monsterName);}
            AIstrategies.tryNeutralisingNeutraliser();
        }
        if (Game.isAllowingCPUToPlay()) {
            if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try searching out " + Mon.Banisher.monsterName);}
            AIstrategies.trySearchingBanisherWithRusher();
        }
        if (Game.isAllowingCPUToPlay() && Game.hasStillNormalSummonCPU && SummonedMonster.countOwnSummonedMonsters(false) < 3) {
            if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try setting stall/trap monster");}
            AIstrategies.trySettingAStallOrTrapMonster();
        }
        if (Game.isAllowingCPUToPlay()) {
            if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try turning weak monsters into def");}
            int threshold = (int) Math.max(2000, AIbattle.maxKnownAttValuePlayer(Game.isSwitchingOnCheatModeChangingRule));
            AIstrategies.tryTurningWeakMonstersIntoDef(threshold);
        }
        if (Game.isAllowingCPUToPlay() && SummonedMonster.countOwnSummonedMonsters(false)==0) {
            if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try summoning any MIDBOSS");}
            AIstrategies.exceptionalStrategyTrySummonAnyMidboss();
        }
        if (Game.isAllowingCPUToPlay()) { // Although this might look like this is sometimes not progressing, it's ok.
            AIdelegate.cpuAttemptsToEndPhase(); // The CPU is asked to continue playing the phase until it finally goes uninterrupted to this point.
        }
    }
    
    // returns true, if a summoned monster is considered expendable by the balanced CPU behavior (thus can be used to pay for effects)
    public static boolean isExpendableCardOnField (SummonedMonster SumMonster) {
        if (Game.isSwitchingOnAdditionalRecklessness) {
            return (SumMonster.att < Game.BattleSituation.maxAttCPU);
        }
        YMonster Monster = YMonster.getMonsterById(SumMonster.effectiveMonsterId());
        if (Game.CPUbehavior.isTryingRemovalByEffectStrategy) {
            return (SumMonster.AIinfoThreatLv < 3 && (SumMonster.isNotAbleToUseItsEffects || (!Monster.hasStealableEffect() && !Monster.equals(Mon.ModeChanger))) );
        }
        return (SumMonster.AIinfoThreatLv < 3);
    }
    
    // returns true, if a card with a given card ID is expendable when on hand of the balanced CPU behavior (thus can be used to pay for effects)
    public static boolean isExpendableCardOnHand (YCard ConsCard) {
        if (Game.isSwitchingOnAdditionalRecklessness) {
            return true;
        }
        boolean isUsingRemovalStrategy = Game.CPUbehavior.isTryingRemovalByEffectStrategy;
        boolean isUsingStandardStrategy = !isUsingRemovalStrategy;
        if (isUsingStandardStrategy) {
            return (ConsCard.equals(Card.GodBarrier) || ConsCard.equals(Card.EradicatorObstacle) || ConsCard.equals(Card.GodKillingSpearLance) || ConsCard.equals(Card.MonsterStealerSteepLearningCurve) || ConsCard.equals(Card.CopyCatCardGrabber) || ConsCard.equals(Card.BigAttackStopperSword) || ConsCard.equals(Card.DiamondSwordShield) || ConsCard.equals(Card.NecromancerBackBouncer) || ConsCard.equals(Card.BigBanisherBurner) || ConsCard.equals(Card.BigBurnerSuicideCommando) || ConsCard.equals(Card.FlakshipNapalm) || ConsCard.equals(Card.DemonGodDemon));
        }
        else if (isUsingRemovalStrategy) {
            return (ConsCard.equals(Card.GodBarrier) || ConsCard.equals(Card.EradicatorObstacle) || ConsCard.equals(Card.GodKillingSpearLance) || ConsCard.equals(Card.BigAttackStopperSword) || ConsCard.equals(Card.DiamondSwordShield) || ConsCard.equals(Card.NecromancerBackBouncer) || ConsCard.equals(Card.BigBanisherBurner) || ConsCard.equals(Card.BigBurnerSuicideCommando) || ConsCard.equals(Card.FlakshipNapalm) || ConsCard.equals(Card.DemonGodDemon));
        }
        return false; // shouldn't happen
    }
    
    // returns true, if a card with a given card ID is expendable when in GY of the balanced CPU behavior (thus can be used to pay for effects)
    public static boolean isExpendableCardInGY (YCard ConsCard) {
        if (Game.isSwitchingOnAdditionalRecklessness) {
            return true;
        }
        boolean isUsingRemovalStrategy = Game.CPUbehavior.isTryingRemovalByEffectStrategy;
        boolean isUsingStandardStrategy = !isUsingRemovalStrategy;
        if (isUsingStandardStrategy) {
            return (!ConsCard.equals(Card.EradicatorObstacle) && !ConsCard.equals(Card.NeutraliserSkillStealer) && !ConsCard.equals(Card.BigAttackStopperSword));
        }
        else if (isUsingRemovalStrategy) {
            return (!ConsCard.equals(Card.EradicatorObstacle) && !ConsCard.equals(Card.NeutraliserSkillStealer) && !ConsCard.equals(Card.BigAttackStopperSword));
        }
        return false; // shouldn't happen
    }
    
    
}
