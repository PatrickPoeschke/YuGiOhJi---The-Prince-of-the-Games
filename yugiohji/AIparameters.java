package yugiohji;

/**
 * This class contains all the fine tuning parameters
 * for the different computer behaviors.
 * The idea is, that one can simply ask a given AI
 * e.g. what decks it wants to use or
 * how likely it will negate a specific effect.
 * 
 * That way one has all the properties
 * that the AIs differ in (apart from the different strategies)
 * together in one object - each for every AI behavior. 
 * 
 */

import static yugiohji.YuGiOhJi.Game;
import static yugiohji.YuGiOhJi.Mon;

public class AIparameters {
    
    public int numberOfDecks; // the number of different decks  used randomly by the CPU
    public Integer[] standardDecks; // numbers of the standard decks used randomly by the CPU
    
    public int handTrapBanisherOnMookRevivalProb; // probability (in %) that CPU hand traps the player with Banisher, when wanting to revive a MOOK
    public int handTrapBanisherOnMidbossRevivalProb; // probability (in %) that CPU hand traps the player with Banisher, when wanting to revive a MIDBOSS
    public int handTrapBanisherOnEndbossRevivalProb; // probability (in %) that CPU hand traps the player with Banisher, when wanting to revive a ENDBOSS
    
    public int estimatedMookDefence; // the estimated def. value of any MOOK (ranges from 0 to 2000, most have no more than 1500)
    public int estimatedMidbossDefence; // the estimated def. value of any MIDBOSS (ranges from 1000 to 3000, most have no more than 2000)
    public int estimatedEndbossDefence; // the estimated def. value of any ENDBOSS (ranges from 0 to 2000, most are closer to the 200 though)
    public int estimatedGodDefence; // the estimated def. value of any GOD (there are 2: one with 4000 def & one with 2000 def)
    
    public int estimatedMinAttack; // the expected minimum attack value the opponent can summon to the field the next turn
    
    public int aLotOfDamage; // the different CPUs idea of what "a lot of damage is"
    
    // the strategy parameters serve as something like a short time memory
    // if a specific non-standard strategy (attack pattern) could work, then the corresponding variable is switched on
    public boolean isTryingAttackForGameStrategy; // if switched on, the computer will attack more recklessly, not careing, if the own monsters survive (After all, the computer thinks, it can win the game right now)
    public boolean isTryingPiercingStrategy; // if switched on, the computer tries to win by piercing your monsters, probably, because you have a face up Barrier
    public boolean isTryingBurnStrategy; // if switched on, the computer tries to win by using the effects of Burner and Big Burner in main phase 2
    public boolean isTryingRemovalByEffectStrategy; // if switched on, the computer (can not defeat monsters by sheer attack power and) tries to remove the most threatening monsters by effect (most likely in main phase 1)
    public boolean isTryingSafetyFirstStrategy; // if switched on, the computer will try to attack unknown face down monsters with immune monsters (Incorruptible or Slick Rusher) first, instead of risking to lose a valuable monster
    
    // one constructor for all CPU behaviors, such that it can be generated on new game and only if changed?
    public AIparameters (int cpuBehaviorAsInt) {
        
        if (cpuBehaviorAsInt==1) { // defensive CPU behavior
            
            this.numberOfDecks = 3;
            this.standardDecks = new Integer[numberOfDecks+1];
            this.standardDecks[0] = 1; // this is just a placeholder and is never chosen
            this.standardDecks[1] = 1;
            this.standardDecks[2] = 8;
            this.standardDecks[3] = 10;
            
            // the defensive CPU is very carefull and always want to banishe revived monsters, no matter how strong they are
            // also this AI probably has still hand traps on the hand and not already used them up, so this is very likely to happen
            this.handTrapBanisherOnMookRevivalProb = 100;
            this.handTrapBanisherOnMidbossRevivalProb = 100;
            this.handTrapBanisherOnEndbossRevivalProb = 100;
            
            this.estimatedMookDefence = 1500; // reasonable standard value
            this.estimatedMidbossDefence = 2000; // reasonable standard value
            this.estimatedEndbossDefence = 2000; // reasonable standard value
            this.estimatedGodDefence = 4000; // the defence CPU always expects the stronger GOD
            
            this.estimatedMinAttack = 2000;
            
            this.aLotOfDamage = 3000;
            
            this.isTryingAttackForGameStrategy = false;
            this.isTryingPiercingStrategy = false;
            this.isTryingBurnStrategy = false;
            this.isTryingRemovalByEffectStrategy = false;
            
        }
        else if (cpuBehaviorAsInt==2) { // balanced CPU behavior
            
            this.numberOfDecks = 2;
            this.standardDecks = new Integer[numberOfDecks+1];
            this.standardDecks[0] = 4; // this is just a placeholder and is never chosen
            this.standardDecks[1] = 4;
            this.standardDecks[2] = 6;
            
            // the balanced CPU has relatively likely a hand trap on the hand
            // and since with standard settings both players have only few cards at beginning,
            // it makes sense that this CPU wants to negate relatively likely
            this.handTrapBanisherOnMookRevivalProb = 50;
            this.handTrapBanisherOnMidbossRevivalProb = 75;
            this.handTrapBanisherOnEndbossRevivalProb = 100;
            
            this.estimatedMookDefence = 1500; // reasonable standard value
            this.estimatedMidbossDefence = 2000; // reasonable standard value
            this.estimatedEndbossDefence = 2000; // reasonable standard value
            this.estimatedGodDefence = 3000; // the balanced CPU is willing to take 1000 reflecting damage into account
            
            this.estimatedMinAttack = 2000;
            
            this.aLotOfDamage = 1500;
            
            this.isTryingAttackForGameStrategy = false;
            this.isTryingPiercingStrategy = false;
            this.isTryingBurnStrategy = false;
            this.isTryingRemovalByEffectStrategy = false;
            
        }
        else if (cpuBehaviorAsInt==3) { // aggressive CPU behavior
            
            this.numberOfDecks = 3;
            this.standardDecks = new Integer[numberOfDecks+1];
            this.standardDecks[0] = 1; // this is just a placeholder and is never chosen
            this.standardDecks[1] = 1;
            this.standardDecks[2] = 4;
            this.standardDecks[3] = 5;
            
            // the aggressive CPU probably has used up most hand traps anyway and ideally only want to use them, if really needed
            this.handTrapBanisherOnMookRevivalProb = 0;
            this.handTrapBanisherOnMidbossRevivalProb = 25;
            this.handTrapBanisherOnEndbossRevivalProb = 100;
            
            this.estimatedMookDefence = 1000; // risky CPU expects many weaker MOOKS
            this.estimatedMidbossDefence = 1000; // risky CPU expects many weaker MIDBOSSES
            this.estimatedEndbossDefence = 1500; // risky CPU expects one of few weak ENDBOSSES
            this.estimatedGodDefence = 2000; // the risky CPU also expects the weaker GOD
            
            this.estimatedMinAttack = 1500;
            
            this.aLotOfDamage = 500;
            
            this.isTryingAttackForGameStrategy = false;
            this.isTryingPiercingStrategy = false;
            this.isTryingBurnStrategy = false;
            this.isTryingRemovalByEffectStrategy = false;
            
        }
    }
    
    // at the beginning of a new turn of the computer,
    // the computer has to forget all strategies that might have worked in the previous turn
    public void forgetStrategies() {
        isTryingAttackForGameStrategy = false;
        isTryingPiercingStrategy = false;
        isTryingBurnStrategy = false;
        isTryingRemovalByEffectStrategy = false;
    }
    
    // switches on the piercing strategy
    public void switchToPierceStrategy() {
        isTryingPiercingStrategy = true;
    }
    
    // switches off the piercing strategy
    public void switchPierceStrategyOff() {
        isTryingPiercingStrategy = false;
    }
    
    // switches on the piercing strategy
    public void switchToBurnStrategy() {
        isTryingBurnStrategy = true;
    }
    
    // switches on the piercing strategy
    public void switchBurnStrategyOff() {
        isTryingBurnStrategy = false;
    }
    
    // switches on the piercing strategy
    public void switchToRemovalStrategy() {
        isTryingRemovalByEffectStrategy = true;
    }
    
    // switches on the piercing strategy
    public void switchRemovalStrategyOff() {
        isTryingRemovalByEffectStrategy = false;
    }
    
    // switches on the safety first strategy
    public void switchToSafetyFirstStrategy() {
        isTryingSafetyFirstStrategy = true;
    }
    
    // switches on the safety first strategy
    public void switchSafetyFirstStrategyOff() {
        isTryingSafetyFirstStrategy = false;
    }
    
    // updates the strategies preferred in the current situation
    public void reevaluateStrategies() {
        Game.BattleSituation = new AIbattle(false, false);
        SummonedMonster InvincibleMonster = SummonedMonster.getWeakestMonsterIndestructibleByBattle(true, false);
        switchPierceStrategyOff();
        if ((InvincibleMonster.isExisting && InvincibleMonster.sumMonsterNumber!=0)) {
            switchToPierceStrategy(); // if player has invincible monster, try to pierce through it
        }
        switchBurnStrategyOff();
        if (AIEffects.hasWorkingMonsterEffect(false, Mon.BigBurner.monsterId) || AIEffects.hasWorkingMonsterEffect(false, Mon.Burner.monsterId)) {
            switchToBurnStrategy(); // if computer has burn monsters, try burn strategy
        }
        if (Game.CPUbehavior.isTryingPiercingStrategy) {
            int noOfPiercingCPUMonsters=0;
            for (int index = 1; index <= 5; index++){
                SummonedMonster SumMonster = SummonedMonster.getNthSummonedMonster(index, false);
                if (SumMonster.hasPiercingDamageAbility()) {
                    noOfPiercingCPUMonsters++;
                }
            }
            if (noOfPiercingCPUMonsters==0) {
                switchToBurnStrategy(); // meanwhile (this method has been called again later), if preparation of piercing strategy didn't work, try burn
            }
        }
        int maxValuePlayer = Game.BattleSituation.maxKnownRelValuePlayer;
        int maxAttCPU = Game.BattleSituation.maxAttCPU;
        switchRemovalStrategyOff();
        if (SummonedMonster.isHavingThreatLv3Monster(true) || (maxValuePlayer > 0 && maxValuePlayer > maxAttCPU && maxValuePlayer > estimatedMinAttack)) {
            switchToRemovalStrategy(); // If you can not outgun your opponent, try to outsmart him.
        }
        isTryingSafetyFirstStrategy = (SummonedMonster.countOwnFaceDownMonsters(true, true)>=1 && SummonedMonster.canAttackSafely(false));
        
    }
    
    
}
