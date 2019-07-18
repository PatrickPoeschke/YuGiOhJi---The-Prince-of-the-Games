package yugiohji;

/**
 * This class requests the Computer to make decisions
 * according to the aggressive AI behavior.
 * The computer will mostly summon strong monsters.
 * Very likely they will also accidently have strong
 * removal effects.
 * Also it will attack almost as risky and reckless as possible,
 * as long as there is still a small chance of success.
 * This opponent is willing to take a lot of reflectied battle damage
 * and getting its monsters destroyed by sucidal effects of
 * unknown face down monsters of the opponent.
 * 
 */

import static yugiohji.YuGiOhJi.Card;
import static yugiohji.YuGiOhJi.Game;
import static yugiohji.YuGiOhJi.Mon;

public class AIaggressive {
    
    public static void cpuPlaysMainPhase1Aggressively() {
        if (Game.isAllowingCPUToPlay()) {
            AIstrategies.exceptionalTopDeckStrategy();
        }
        Game.CPUbehavior.reevaluateStrategies();
        if (Game.isAllowingCPUToPlay() && Game.CPUbehavior.isTryingPiercingStrategy) {
            if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try preparing pierce to death strategy");}
            AIstrategies.tryPreparingPierceToDeathStrategy();
        }
        // About this strategy: This AI always wants to go 2nd, because wants to attack 1st and is way better than, when the player wins the coin flip and forces the AI to begin, because this strategy contains overpowering the opponent.
        // After all, the CPU doesn't like to lose 4 God Killing Spears to one Mode Changer and a few MOOKs. This AI should be more aggressive when going 2nd.
        // However, sometimes the opponent is just unlucky, and has not the right cards on the hand for this very core of this whole AI behavior.
        if (Game.isAllowingCPUToPlay()) { 
            if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try summoning many strong monsters");}
            AIstrategies.trySummoningManyStrongestMonsters();
        }
        Game.CPUbehavior.reevaluateStrategies();
        if (Game.isAllowingCPUToPlay() && Game.CPUbehavior.isTryingBurnStrategy) {
            if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try making preparations for burn to death strategy");}
            AIstrategies.tryPreparingBurnToDeathStrategy();
        }
        if (Game.isAllowingCPUToPlay()) { // it makes sense to make use of all the countless effects that might be on the field (except burn effects), since one just tried to summon the best monsters
            if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try using effects already on the field");}
            AIstrategies.tryUsingEffectsAlreadyOnTheField();
        }
        if (Game.isAllowingCPUToPlay() && SummonedMonster.countOwnFaceDownMonsters(true, true)>=1) {
            if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try to mode change unknown face down monsters");}
            AIstrategies.tryModeChangeUnknownMonstersByModeChanger();
        }
        if (Game.isAllowingCPUToPlay()) {
            if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try summon strong MOOK");}
            AIstrategies.trySummonStrongMook();
        }
        if (Game.isAllowingCPUToPlay()) {
            if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try summoning any MIDBOSS");}
            AIstrategies.exceptionalStrategyTrySummonAnyMidboss();
        }
        if (Game.isAllowingCPUToPlay() && Game.CPUbehavior.isTryingRemovalByEffectStrategy) { // Interestingly, weakening by mode changes fits best the aggressive CPU, since one needs many strong monsters to make use of this effect.
            if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try weakening monster by changing mode");}
            AIstrategies.tryModeChangeUnknownMonstersByModeChanger();
        }
        if (Game.isAllowingCPUToPlay()) { // This strategy mainly exists to make sure, that God Killing Spear (with lots of att. and no def.) is no easy prey.
            if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try turning strong monsters into attack mode");}
            AIstrategies.tryTurningStrongMonstersIntoAtt();
        }
        if (!Game.isVeryFirstTurn()) { // In the very 1st turn one can not attack anyway. Thus don't bother analysing battle situation.
            if (Game.isAllowingCPUToPlay()) {
                if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("check, if can deal a lot of damage after changing modes");}
                int simulatedDamage = AIbattle.dealableBattleDamageByAttackingWithOrdinaryModeChanges(false);
                // if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("simulated total battle damage: " + simulatedDamage);} hide simulated battle damage for consistency (i.e. because it's also hidden for other CPU behaviors)
                if (simulatedDamage >= Game.CPUbehavior.aLotOfDamage) {
                    if (simulatedDamage >= Game.lifePointsPlayer) {Game.CPUbehavior.isTryingAttackForGameStrategy=true;}
                    if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try changing modes, if needed");}
                    AIbattle.ordinaryChangeToAtt();
                    AIdelegate.cpuAttemptsToEndPhase();
                }
            }
            if (Game.isAllowingCPUToPlay()) {
                if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("check, if can defeat a monster after changing modes");}
                int simulatedDamage = AIbattle.dealableBattleDamageByAttackingWithOrdinaryModeChanges(false);
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
    public static void cpuPlaysBattlePhaseAggressively() {
        Game.CPUbehavior.reevaluateStrategies(); // piercing strategy already incorporated in all other attack patterns
        if (Game.isBattlePhase() && Game.isAllowingCPUToPlay()) {
            AIbattle.attackStrategy(false, false); // The aggressive CPU always attacks as if it was for game.
        }
        if (Game.isBattlePhase() && Game.isAllowingCPUToPlay() && Game.CPUbehavior.isTryingBurnStrategy && SummonedMonster.countPotentialNumberOfLeftAttacksThisTurn(false)>=1) { // The condition about the number of left attacks is just there to prevent the dialog from happening, if CPU can not attack any more.
            if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try attacking safely enough for burn strategy");}
            AIbattle.attackStrategy(false, true); // attack with both Burners safely, with rest recklessly
        }
        if (Game.isBattlePhase() && Game.isAllowingCPUToPlay()) { // Although this might look like this is sometimes not progressing, it's ok.
            AIdelegate.cpuAttemptsToEndPhase(); // The CPU is asked to continue playing the phase until it finally goes uninterrupted to this point.
        }
    }
    
    
    public static void cpuPlaysMainPhase2Aggressively() {
        Game.CPUbehavior.reevaluateStrategies();
        if (Game.isAllowingCPUToPlay() && Game.CPUbehavior.isTryingBurnStrategy) {
            if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try burn to death strategy");}
            AIstrategies.tryBurnToDeatStrategyInMainPhase2();
        }
        if (Game.isAllowingCPUToPlay()) { // Although this might look like this is sometimes not progressing, it's ok.
            AIdelegate.cpuAttemptsToEndPhase(); // The CPU is asked to continue playing the phase until it finally goes uninterrupted to this point.
        }
    }
    
    // returns true, if a summoned monster is considered expendable by the aggressive CPU behavior (thus can be used to pay for effects)
    public static boolean isExpendableCardOnField (SummonedMonster SumMonster) {
        if (Game.isSwitchingOnAdditionalRecklessness) {
            return (SumMonster.att < Game.BattleSituation.maxAttCPU);
        }
        if (Game.CPUbehavior.isTryingBurnStrategy) {
            return (SumMonster.AIinfoThreatLv < 3 && (SumMonster.isNotAbleToUseItsEffects || (!SumMonster.Monster.equals(Mon.Burner) && !SumMonster.Monster.equals(Mon.BigBurner) && !SumMonster.Monster.equals(Mon.Necromancer))) );
        }
        return (SumMonster.AIinfoThreatLv < 3);
    }
    
    // returns true, if a card with a given card ID is expendable when on hand of the aggressive CPU behavior (thus can be used to pay for effects)
    public static boolean isExpendableCardOnHand (YCard ConsCard) {
        if (Game.isSwitchingOnAdditionalRecklessness) {
            return true;
        }
        boolean isUsingPiercingStrategy = Game.CPUbehavior.isTryingPiercingStrategy;
        boolean isUsingBurnStrategy = Game.CPUbehavior.isTryingBurnStrategy;
        boolean isUsingStandardStrategy = (!isUsingPiercingStrategy && !isUsingBurnStrategy);
        if (isUsingStandardStrategy) {
            return (ConsCard.equals(Card.GodBarrier) || ConsCard.equals(Card.EradicatorObstacle) || ConsCard.equals(Card.GodKillingSpearLance) || ConsCard.equals(Card.MonsterStealerSteepLearningCurve) || ConsCard.equals(Card.NeutraliserSkillStealer) || ConsCard.equals(Card.CopyCatCardGrabber) || ConsCard.equals(Card.AttackStopperBuggedUpgrade) || ConsCard.equals(Card.BigAttackStopperSword) || ConsCard.equals(Card.DiamondSwordShield) || ConsCard.equals(Card.NecromancerBackBouncer) || ConsCard.equals(Card.BigBurnerSuicideCommando) || ConsCard.equals(Card.DemonGodDemon));
        }
        else if (isUsingPiercingStrategy) {
            return (ConsCard.equals(Card.GodBarrier) || ConsCard.equals(Card.EradicatorObstacle) || ConsCard.equals(Card.MonsterStealerSteepLearningCurve) || ConsCard.equals(Card.NeutraliserSkillStealer) || ConsCard.equals(Card.CopyCatCardGrabber) || ConsCard.equals(Card.AttackStopperBuggedUpgrade) || ConsCard.equals(Card.BigAttackStopperSword) || ConsCard.equals(Card.DiamondSwordShield) || ConsCard.equals(Card.BigBurnerSuicideCommando) || ConsCard.equals(Card.DemonGodDemon));
        }
        else if (isUsingBurnStrategy) {
            return (ConsCard.equals(Card.GodBarrier) || ConsCard.equals(Card.EradicatorObstacle) || ConsCard.equals(Card.GodKillingSpearLance) || ConsCard.equals(Card.MonsterStealerSteepLearningCurve) || ConsCard.equals(Card.NeutraliserSkillStealer) || ConsCard.equals(Card.CopyCatCardGrabber) || ConsCard.equals(Card.AttackStopperBuggedUpgrade) || ConsCard.equals(Card.BigAttackStopperSword) || ConsCard.equals(Card.DiamondSwordShield) || ConsCard.equals(Card.BigBurnerSuicideCommando) || ConsCard.equals(Card.DemonGodDemon));
        }
        return false; // shouldn't happen
    }
    
    // returns true, if a card with a given card ID is expendable when in GY of the aggressive CPU behavior (thus can be used to pay for effects)
    public static boolean isExpendableCardInGY (YCard ConsCard) {
        if (Game.isSwitchingOnAdditionalRecklessness) {
            return true;
        }
        boolean isUsingPiercingStrategy = Game.CPUbehavior.isTryingPiercingStrategy;
        boolean isUsingBurnStrategy = Game.CPUbehavior.isTryingBurnStrategy;
        boolean isUsingStandardStrategy = (!isUsingPiercingStrategy && !isUsingBurnStrategy);
        if (isUsingStandardStrategy) {
            return (ConsCard.equals(Card.GodBarrier) || ConsCard.equals(Card.MonsterStealerSteepLearningCurve) || ConsCard.equals(Card.CopyCatCardGrabber) || ConsCard.equals(Card.AttackStopperBuggedUpgrade) || ConsCard.equals(Card.DiamondSwordShield) || ConsCard.equals(Card.ModeChangerExhaustedExecutioner) || ConsCard.equals(Card.SlickRusherRecklessRusher) || ConsCard.equals(Card.IncorruptibleHolyLance) || ConsCard.equals(Card.NecromancerBackBouncer) || ConsCard.equals(Card.BigBackBouncerBanisher) || ConsCard.equals(Card.BigBanisherBurner) || ConsCard.equals(Card.FlakshipNapalm) || ConsCard.equals(Card.DemonGodDemon));
        }
        else if (isUsingPiercingStrategy) {
            return (ConsCard.equals(Card.GodBarrier) || ConsCard.equals(Card.MonsterStealerSteepLearningCurve) || ConsCard.equals(Card.CopyCatCardGrabber) || ConsCard.equals(Card.AttackStopperBuggedUpgrade) || ConsCard.equals(Card.DiamondSwordShield) || ConsCard.equals(Card.ModeChangerExhaustedExecutioner) || ConsCard.equals(Card.SlickRusherRecklessRusher) || ConsCard.equals(Card.NecromancerBackBouncer) || ConsCard.equals(Card.BigBackBouncerBanisher) || ConsCard.equals(Card.BigBanisherBurner) || ConsCard.equals(Card.FlakshipNapalm) || ConsCard.equals(Card.DemonGodDemon));
        }
        else if (isUsingBurnStrategy) {
            return (ConsCard.equals(Card.GodBarrier) || ConsCard.equals(Card.MonsterStealerSteepLearningCurve) || ConsCard.equals(Card.CopyCatCardGrabber) || ConsCard.equals(Card.AttackStopperBuggedUpgrade) || ConsCard.equals(Card.DiamondSwordShield) || ConsCard.equals(Card.IncorruptibleHolyLance) || ConsCard.equals(Card.NecromancerBackBouncer) || ConsCard.equals(Card.BigBackBouncerBanisher) || ConsCard.equals(Card.FlakshipNapalm) || ConsCard.equals(Card.DemonGodDemon));
        }
        return false; // shouldn't happen
    }
    
    
    
    
    
    
}
