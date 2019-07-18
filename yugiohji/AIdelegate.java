package yugiohji;

/**
 * This class requests the Computer to make a decision,
 * which delegates it to the AI of the corresponding CPU behavior.
 * That means, here the requests for CPU decisions
 * are just branching off to the several AI behaviors (one class for each).
 * 
 */

import static yugiohji.YuGiOhJi.Card;
import static yugiohji.YuGiOhJi.Game;
import static yugiohji.YuGiOhJi.HandCPU;

public class AIdelegate {
    
    // calls the AI of the CPU to make actions throughout the turn
    public static void cpuPlaysTurn(){
        if (!Game.isPlayersTurn) {
            Game.playerCurrentlyNegates=false;
            Game.CPUbehavior.forgetStrategies();
            cpuPlaysMainPhase1();
        }
    }
    
    // called after the computer has been interrupted by an effect,
    // that needed the player to choose a card, or that needed a player to discard a card
    public static void cpuContinuesPlayingTurn(){
        if (!Game.isPlayersTurn) {
            Game.playerCurrentlyNegates=false;
            if (Game.isMainPhase1()) {
                cpuPlaysMainPhase1();
            }
            else if (Game.isBattlePhase()) {
                cpuPlaysBattlePhase();
            }
            else if (Game.isMainPhase2()) {
                cpuPlaysMainPhase2();
            }
        }
    }
    
    // In case player can negate an effect at the end of the computer's main phase 1 or 2, the player gets asked first, if one wants to do so, before proceeding to next phase.
    // (One could in principle also allow that to happen at the end of the battle phase, but clicking buttons are coded differently there. So in order to avoid issues, leave that out.)
    public static void cpuAttemptsToEndPhase(){
        Game.deactivateCurrentCPUEffects(); // Just to be sure that possible effect negation of the player is counted as preventively (and also maybe to prevent possible bugs).
        Game.playerCurrentlyNegates=true;
        if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("go to next phase");}
        if (Game.isBattlePhase()){
            cpuProceedsToNextPhase();
        }
        else {
            PlayerInterrupts.askPlayerToNegateEffectsAtEndOfPhase();
        }
    }
    
    // in order not to repeat oneself, out-source here what happens, if it is clear that the CPU goes on to the next phase
    public static void cpuProceedsToNextPhase(){
        Game.playerCurrentlyNegates=false;
        if (Game.isMainPhase1()) {
            Game.proceedToNextPhase();
            cpuPlaysBattlePhase();
        }
        else if (Game.isBattlePhase()) {
            Game.proceedToNextPhase();
            cpuPlaysMainPhase2();
        }
        else if (Game.isMainPhase2()) {
            Game.proceedToNextPhase();
        }
    }
    
    // lets AI make decisions throughout the first main phase
    // (also in case one has to jump right into the phase, because of an effect negation interrupting the flow)
    public static void cpuPlaysMainPhase1(){
        Game.playerCurrentlyNegates=false;
        if (Game.isDefensiveCPU()){
            AIdefensive.cpuPlaysMainPhase1Defensively();
            // cpuAttemptsToEndPhase(); // already contained in method
        }
        else if (Game.isBalancedCPU()){
            AIbalanced.cpuPlaysMainPhase1Balanced();
            // cpuAttemptsToEndPhase(); // already contained in method
        }
        else if (Game.isAggressiveCPU()){
            // dummy code for now
            AIaggressive.cpuPlaysMainPhase1Aggressively();
            // cpuAttemptsToEndPhase(); // already contained in method
        }
    }
    
    // lets AI make decisions throughout the second main phase
    // (also in case one has to jump right into the phase, because of an effect negation interrupting the flow)
    public static void cpuPlaysMainPhase2(){
        Game.playerCurrentlyNegates=false;
        if (Game.isDefensiveCPU()){
            AIdefensive.cpuPlaysMainPhase2Defensively();
            // cpuAttemptsToEndPhase(); // already contained in method
        }
        else if (Game.isBalancedCPU()){
            AIbalanced.cpuPlaysMainPhase2Balanced();
            // cpuAttemptsToEndPhase(); // already contained in method
        }
        else if (Game.isAggressiveCPU()){
            AIaggressive.cpuPlaysMainPhase2Aggressively();
            // cpuAttemptsToEndPhase(); // already contained in method
        }
    }
    
    // -- everything about BATTLE PHASE of CPU --
    
    // lets AI make decisions throughout the battle phase
    // (also in case one has to jump right into the phase, because of an effect negation interrupting the flow)
    public static void cpuPlaysBattlePhase(){
        Game.playerCurrentlyNegates=false;
        if (Game.isVeryFirstTurn()) { // on the very first turn one can not attack anyway
            cpuAttemptsToEndPhase();
        }
        else {
            if (Game.isDefensiveCPU()){
                AIdefensive.cpuPlaysBattlePhaseDefensively();
                // cpuAttemptsToEndPhase(); // already contained in method
            }
            else if (Game.isBalancedCPU()){
                AIbalanced.cpuPlaysBattlePhaseBalanced();
                // cpuAttemptsToEndPhase(); // already contained in method
            }
            else if (Game.isAggressiveCPU()){
                AIaggressive.cpuPlaysBattlePhaseAggressively();
                // cpuAttemptsToEndPhase(); // already contained in method
            }
        }
    }
    
    // -- everything about CPU DISCARDING CARDS --
    
    // asks the computer to choose a card to discard at the end of the turn
    // (This is the same for all different CPU behaviors, so one method here suffices.)
    public static int cpuChooseCardToDiscard () {
        // try to discard an endboss first (because can summon themselves from GY)
        int cardNo = HandCPU.getPositionOfCardWithCardId(Card.EradicatorObstacle.cardId);
        if (cardNo!=0) {return cardNo;}
        else {cardNo = HandCPU.getPositionOfCardWithCardId(Card.GodKillingSpearLance.cardId);}
        if (cardNo!=0) {return cardNo;}
        else {cardNo = HandCPU.getPositionOfCardWithCardId(Card.MonsterStealerSteepLearningCurve.cardId);}
        if (cardNo!=0) {return cardNo;}
        else {cardNo = HandCPU.getPositionOfCardWithCardId(Card.BigBurnerSuicideCommando.cardId);}
        if (cardNo!=0) {return cardNo;} // if that didn't work, try to discard one of the most useless MOOKs
        else {cardNo = HandCPU.getPositionOfCardWithCardId(Card.FlakshipNapalm.cardId);}
        if (cardNo!=0) {return cardNo;}
        else {cardNo = HandCPU.getPositionOfCardWithCardId(Card.SlickRusherRecklessRusher.cardId);}
        if (cardNo!=0) {return cardNo;}
        else {cardNo = HandCPU.getPositionOfCardWithCardId(Card.ModeChangerExhaustedExecutioner.cardId);}
        if (cardNo!=0) {return cardNo;}
        else {cardNo = HandCPU.getPositionOfCardWithCardId(Card.AttackStopperBuggedUpgrade.cardId);}
        if (cardNo!=0) {return cardNo;}
        else { // if that didn't work, discard a random card
            return Deck.chooseRandomOption(HandCPU.numberOfCards);
        }
    }
    
    
    
}
