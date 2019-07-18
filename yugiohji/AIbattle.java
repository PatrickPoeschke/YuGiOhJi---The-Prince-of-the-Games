package yugiohji;

/**
 * Everything about the computer analysing and performing the battle phase here.
 * This class provides all information for the computer
 * to perform well in the battle phase in YuGiOhJi.
 * It allows the computer to think ahead, what monsters it can defeat in battle
 * and how much battle damage it can deal, when attacking in a certain way.
 * Thus, this file is very crucial for the overall skill of the computer!
 * 
 */

import static yugiohji.YuGiOhJi.Card;
import static yugiohji.YuGiOhJi.Game;
import static yugiohji.SummonedMonster.countOwnSummonedMonsters;
import static yugiohji.SummonedMonster.getNthSummonedMonster;

public class AIbattle {
    
    // - two methods for the cpu declaring attacks (analog to the ones in file BattlePhase) -
    
    public static void cpuDeclareDirectAttack (SummonedMonster AttackingMonster) {
        if (AttackingMonster.canStillAttackThisBattlePhase()) {
            Game.ActiveAttackingMonster = AttackingMonster;
            boolean canNegateAttackByCards = Hand.lookForAttackNegate(true);
            boolean isNegatingAttack = false;
            String numberString = AIEffects.getNumberAsString(AttackingMonster.sumMonsterNumber);
            if (!AttackingMonster.isImmune() && canNegateAttackByCards) { // important: if the attacking monster is immune to all effects, its attack can not be negated (and for direct attacks one can not invoke the My Body As A Shield rule)
                int intDialogResult = YuGiOhJi.multipleChoiceDialog("Computer declares a direct attack with its " + numberString + " monster " + AttackingMonster.Monster.monsterName + " (with " + AttackingMonster.att + " attack points).", "Attack declaration", new String[]{"Negate attack!", "Let attack happen."}, "Let attack happen.");
                if (intDialogResult==0) {
                    isNegatingAttack = true;
                    PlayerInterrupts.playerNegatesAttack(AttackingMonster, true);
                }
            }
            else {
                YuGiOhJi.informationDialog("Computer attacks directly with its " + numberString + " monster " + AttackingMonster.Monster.monsterName + " (with " + AttackingMonster.att + " attack points).", "You take a direct hit.");
            }
            if (!isNegatingAttack) {
                boolean isContinuingGame = BattlePhase.isDealingBattleDamageAndContinuingGame(false, AttackingMonster.att, true);
                if (isContinuingGame) {
                    if (AttackingMonster.isTurningIntoDef()) {
                        AttackingMonster.passiveEffectExhaustedExecutioner();
                    }
                    BattlePhase.endAttack(false);
                }
            }
        }
    }
    
    public static void cpuDeclareAttackOnMonster (SummonedMonster AttackingMonster, SummonedMonster GuardingMonster) {
        if (AttackingMonster.canStillAttackThisBattlePhase() && GuardingMonster.isExisting) {
            Game.ActiveAttackingMonster = AttackingMonster;
            Game.ActiveGuardingMonster = GuardingMonster;
            boolean canNegateAttackByCards = Hand.lookForAttackNegate(true);
            boolean canNegateAttackByRule = (Game.isSwitchingOnBodyAsAShieldRule && AttackingMonster.att < Game.lifePointsPlayer);
            boolean isNegatingAttack = false;
            String numberStringCPU = AIEffects.getNumberAsString(AttackingMonster.sumMonsterNumber);
            String numberStringPlayer = AIEffects.getNumberAsString(GuardingMonster.sumMonsterNumber);
            String relevantGuardingString;
            if (GuardingMonster.isInAttackMode) {relevantGuardingString = " (" + GuardingMonster.att + " att.).";}
            else {relevantGuardingString = " (" + GuardingMonster.def + " def.).";}
            String piercing = "";
            if (AttackingMonster.hasPiercingDamageAbility()) {piercing = " piercing";}
            if ((canNegateAttackByCards || canNegateAttackByRule)) { // important: if the attacking monster is immune to all effects, its attack can not be negated
                int intDialogResult = YuGiOhJi.multipleChoiceDialog("Computer declares an attack with its " + numberStringCPU + " monster " + AttackingMonster.Monster.monsterName + " (" + AttackingMonster.att + " att." + piercing + ") to your " + numberStringPlayer + " monster " + GuardingMonster.Monster.monsterName + relevantGuardingString, "Attack declaration", new String[]{"Negate attack!", "Let attack happen."}, "Let attack happen.");
                if (intDialogResult==0) {
                    isNegatingAttack = true;
                    PlayerInterrupts.playerNegatesAttack(AttackingMonster, false);
                }
            }
            else {
                YuGiOhJi.informationDialog("Computer attacks your " + numberStringPlayer + " monster " + GuardingMonster.Monster.monsterName + relevantGuardingString + " with its " + numberStringCPU + " monster " + AttackingMonster.Monster.monsterName + " (" + AttackingMonster.att + " att." + piercing + ")", "Attack");
            }
            if (!isNegatingAttack) {
                BattlePhase.damageStep();
            }
        }
    }
    
    // --- everything about the computer analysing the current battle situation (i.e. the situation on the field before/between/after attacks) ---
    
    public int numberOfAttacksLeft; // the number of times the CPU can still attack this battle phase (will decrease, when updating after each attack)
    public int maxAttCPU;
    public int maxRelValueCPU;
    public int maxKnownAttPlayer;
    public int maxKnownRelValuePlayer;

    // constructor for empty AIbattle objects used at the beginning of the game
    public AIbattle() {
        this.numberOfAttacksLeft=0;
        this.maxAttCPU=0;
        this.maxRelValueCPU=0;
        this.maxKnownAttPlayer=0;
        this.maxKnownRelValuePlayer=0;
    }
    
    // constructor: collects needed data for current battle phase (for convenience)
    // but more importantly, analyses the whole battle situation in the first place
    // (the results of this analysis is stored in the summoned monsters themselves)
    public AIbattle (boolean isUsingOrinaryChange, boolean isAlsoConsideringPotentialChangeByPlayer) {
        forgetPreviousBattle();
        this.maxAttCPU = sortCPUMonstersAccordingToStrength(isUsingOrinaryChange);
        if (isAlsoConsideringPotentialChangeByPlayer) {
            sortPlayerMonstersAccordingToStrengthAndThreatLv(isUsingOrinaryChange, maxAttCPU);
            this.maxKnownAttPlayer = maxKnownAttValuePlayer(isUsingOrinaryChange);
        }
        else {
            sortPlayerMonstersAccordingToStrengthAndThreatLv(false, maxAttCPU);
            this.maxKnownAttPlayer = maxKnownAttValuePlayer(false);
        }
        // threat level CPU monsters (used to decide how valuable the own monsters are to the computer)
        for (int index = 1; index <= 5; index++){
            SummonedMonster SumMonster = getNthSummonedMonster(index, false);
            SumMonster.AIinfoThreatLv = SumMonster.threatLevel(maxKnownAttPlayer);
        }
        if (Game.isBattlePhase()) {
            this.numberOfAttacksLeft = SummonedMonster.countNumberOfAttacksLeftThisBattlePhase(false);
        }
        else {
            this.numberOfAttacksLeft = SummonedMonster.countPotentialNumberOfLeftAttacksThisTurn(false);
        }
        this.maxRelValueCPU = maxRelevantValue(false);
        this.maxKnownRelValuePlayer = maxRelevantValue(true);
    }
    
    // erases the memory about the battle situation before the last attack
    public static void forgetPreviousBattle() {
        SummonedMonster.resetAllAIinfo();
    }
    
    // evaluates what monster controlled by the computer has the highest (potential) relevant attack value and returns that value
    // and sorts the monsters accordingly by saving (if tied, position decides) their rank as one of their properties
    public static int sortCPUMonstersAccordingToStrength (boolean isUsingOrinaryChange) {
        SummonedMonster.resetAllAIinfo();
        SummonedMonster SumMonster;
        int maxAttCPU = 0;
        int currentAttCPU;
        for (int index = 1; index <= 5; index++){ // determine all strength values first
            SumMonster = getNthSummonedMonster(index, false);
            currentAttCPU = SumMonster.getLargestPossibleAttValue(isUsingOrinaryChange);
            SumMonster.AIinfoPotentialStrength = currentAttCPU;
            if (currentAttCPU > maxAttCPU) {maxAttCPU = currentAttCPU;}
        }
        for (int index = 1; index <= 5; index++){ // presort according to pure strength here
            SumMonster = getNthSummonedMonster(index, false);
            SumMonster.presortAccordingToPotentialStrength(); // piercing strategy taken into account here in method (piercing ones are ranked better)
        }
        // threat levels of CPU monsters don't matter and can stay unchanged
        for (int index = 1; index <= 5; index++){ // sort according to monster position here (they always already know their position)
            SumMonster = getNthSummonedMonster(index, false);
            SumMonster.finallySortAccordingToPosition(false);
        }
        return maxAttCPU;
    }
    
    // evaluates what monster controlled by the player has the highest (potential) relevant attack value and highest threat level
    // and sorts the monsters accordingly by saving their rank (and finally just position) as one of their properties
    public static void sortPlayerMonstersAccordingToStrengthAndThreatLv (boolean isUsingOrinaryChange, int maxAttCPU) {
        // assuming resetAllAIinfo(); has already been called by or before sortCPUMonstersAccordingToStrength(...) right before this method
        SummonedMonster SumMonster;
        for (int index = 1; index <= 5; index++){ // determine all strength values first
            SumMonster = getNthSummonedMonster(index, true);
            SumMonster.AIinfoPotentialStrength = SumMonster.getLargestPossibleAttValue(isUsingOrinaryChange);
        }
        for (int index = 1; index <= 5; index++){ // presort according to pure strength here
            SumMonster = getNthSummonedMonster(index, true);
            SumMonster.presortAccordingToPotentialStrength();
        }
        // now consider also threat level
        for (int index = 1; index <= 5; index++){ // determine all threat levels next
            SumMonster = getNthSummonedMonster(index, true);
            SumMonster.AIinfoThreatLv = SumMonster.threatLevel(maxAttCPU);
        }
        for (int index = 1; index <= 5; index++){ // further presort according to strength and threat level here
            SumMonster = getNthSummonedMonster(index, true);
            SumMonster.furtherPresortAccordingToThreatLv();
        }
        for (int index = 1; index <= 5; index++){ // sort according to monster position here (they always already know their position)
            SumMonster = getNthSummonedMonster(index, true);
            SumMonster.finallySortAccordingToPosition(true);
        }
    }
    
    // returns the max. current attack value of all monsters of the player known to the CPU
    // (one can also include considering cheat change for all known monsters in def)
    public static int maxKnownAttValuePlayer (boolean isAlsoConsideringCheatChange) {
        int maxAttValue = 0;
        int possibleValue;
        for (int index = 1; index <= 5; index++){
            SummonedMonster SumMonster = getNthSummonedMonster(index, true);
            if (SumMonster.isExistingAndKnown()) {
                if (isAlsoConsideringCheatChange) {
                    possibleValue = SumMonster.getLargestAttValueByOrdinaryChange();
                }
                else {
                    possibleValue = SumMonster.att;
                }
                if (possibleValue > maxAttValue) {
                    maxAttValue = possibleValue;
                }
            }
        }
        return maxAttValue;
    }
    
    // returns the maximum of the current relevant values (att/def depending on mode) of all monsters of the CPU
    public static int maxRelevantValue (boolean isConsideringPlayerMonsters) {
        int maxValue = 0;
        int possibleValue;
        for (int index = 1; index <= 5; index++){
            SummonedMonster SumMonster = getNthSummonedMonster(index, isConsideringPlayerMonsters);
            if (!isConsideringPlayerMonsters || (isConsideringPlayerMonsters && SumMonster.isExistingAndKnown())) {
                possibleValue = SumMonster.relevantValue();
                if (possibleValue>maxValue) {
                    maxValue = possibleValue;
                }
            }
        }
        return maxValue;
    }
    
    // --- about simulating dealable battle damage and the like ---
    
    // the following monster and methods are used to fully simulate the mode changes of the considered monster of the CPU with all its properties without changing the actual monster
    public static SummonedMonster CopyOfAttMonster = new SummonedMonster(false, 6);
    
    public static void simulatedAllowedModeChange (SummonedMonster CopyOfAttackingMonster, SummonedMonster OriginalAttackingMonster) {
        CopyOfAttackingMonster.isInAttackMode = !CopyOfAttackingMonster.isInAttackMode;
        simulatedBoostDueToEquipping(CopyOfAttackingMonster, OriginalAttackingMonster);
    }
    
    public static void simulatedCheatModeChange (SummonedMonster CopyOfAttackingMonster, SummonedMonster OriginalAttackingMonster) {
        CopyOfAttackingMonster.att = CopyOfAttackingMonster.otherMonsterAtt();
        CopyOfAttackingMonster.def = CopyOfAttackingMonster.otherMonsterDef();
        if (CopyOfAttackingMonster.isLowerMonster()) {
            CopyOfAttackingMonster.Monster = CopyOfAttackingMonster.Card.upMonster;
        }
        else {
            CopyOfAttackingMonster.Monster = CopyOfAttackingMonster.Card.lowMonster;
        }
        simulatedAllowedModeChange(CopyOfAttackingMonster, OriginalAttackingMonster);
    }
    
    public static void simulatedBoostDueToEquipping (SummonedMonster CopyOfAttackingMonster, SummonedMonster OriginalAttackingMonster) {
        EStack Stack = EStack.getNthStack(OriginalAttackingMonster.sumMonsterNumber, OriginalAttackingMonster.isPlayersMonster);
        if (Stack.numberOfCards >= 1) {
            for (int index = Stack.numberOfCards; index >= 1; index--){
                EquipCard ECard = Stack.getNthEquipCardOfStack(index);
                if (!ECard.isNegated) {
                    YCard ConsCard = ECard.Card;
                    if (ConsCard.equals(Card.BigAttackStopperSword)) {
                        CopyOfAttackingMonster.att = CopyOfAttackingMonster.att + Card.BigAttackStopperSword.lowMonster.SwordAttBoost();
                    }
                    else if (ConsCard.equals(Card.DiamondSwordShield)) {
                        CopyOfAttackingMonster.def = CopyOfAttackingMonster.def + Card.DiamondSwordShield.lowMonster.ShieldDefBoost();
                    }
                    else if (ConsCard.equals(Card.AttackStopperBuggedUpgrade)) {
                        CopyOfAttackingMonster.isNotAbleToUseItsEffects = true;
                    }
                }
            }
        }
    }
    
    public static void simulateMaximizationOfAtt (SummonedMonster CopyOfAttackingMonster, SummonedMonster OriginalAttackingMonster, boolean isUsingOrinaryChange) {
        if (isUsingOrinaryChange && !CopyOfAttackingMonster.isInAttackMode && CopyOfAttackingMonster.isModeChangeableThisTurn) {
            if (Game.isSwitchingOnCheatModeChangingRule && CopyOfAttackingMonster.otherMonsterAtt() > CopyOfAttackingMonster.att) {
                simulatedCheatModeChange(CopyOfAttackingMonster, OriginalAttackingMonster);
            }
            else {
                simulatedAllowedModeChange(CopyOfAttackingMonster, OriginalAttackingMonster);
            }
        }
    }
    
    // returns the number of battle damage one can deal with a given monster, when attacking a certain monster
    public static int simulateAmountOfDealableBattleDamage (SummonedMonster AttackingMonster, SummonedMonster GuardingMonster, boolean isUsingOrinaryChange) {
        CopyOfAttMonster = new SummonedMonster(false, 6);
        CopyOfAttMonster.rememberPropertiesOfMonster(AttackingMonster);
        simulateMaximizationOfAtt(CopyOfAttMonster, AttackingMonster, isUsingOrinaryChange);
        return CopyOfAttMonster.simulatingBattleDamageFromAttackingMonsterForSure(GuardingMonster);
    }
    
    // returns if a given monster could defeat another monster in battle
    public static boolean isAbleToDefeatInSimulation (SummonedMonster AttackingMonster, SummonedMonster GuardingMonster, boolean isUsingOrinaryChange, boolean isNeededToSurvive) {
        CopyOfAttMonster = new SummonedMonster(false, 6);
        CopyOfAttMonster.rememberPropertiesOfMonster(AttackingMonster);
        simulateMaximizationOfAtt(CopyOfAttMonster, AttackingMonster, isUsingOrinaryChange);
        return CopyOfAttMonster.canDefeatForSure(GuardingMonster, isNeededToSurvive);
    }
    
    // returns battle damage the CPU can deal by attacking with its monsters without changing anything before
    public static int dealableBattleDamageByAttackingWithoutChanges (boolean isNeededToSurvive) {
        return dealableBattleDamageByAttacking(false, isNeededToSurvive);
    }
    
    // returns how many monsters a given player has on the field with attack value > 0
    // It is used instead of looking what monsters can still attack this battle phse, because one could still mode change the monsters with zero att.
    public static int countCPUMonstersWithNonZeroAttack() {
        int numberOfMonsters=0;
        for (int index = 1; index <= 5; index++){
            SummonedMonster SumMonster = getNthSummonedMonster(index, false);
            if (SumMonster.isExistingAndKnown() && SumMonster.att>0) {numberOfMonsters++;}
        }
        return numberOfMonsters;
    }
    
    // returns true, if CPU is able to defeat at least one monster of the opponent
    public static boolean canDefeatOneMonsterWithoutChanges (boolean isNeededToSurvive) {
        Game.BattleSituation = new AIbattle(false, false);
        int monstersCPU = countCPUMonstersWithNonZeroAttack();
        int monstersPlayer = countOwnSummonedMonsters(true);
        if (monstersCPU==0 || monstersPlayer==0) {
            return false;
        }
        else {
            for (int index = 1; index <= monstersCPU; index++){
                SummonedMonster MonsterCPU = SummonedMonster.getNthStrongestMonster(index, false);
                SummonedMonster PlayerMonster = SummonedMonster.getNthStrongestMonster(index, true);
                if (MonsterCPU.canDefeatForSure(PlayerMonster, isNeededToSurvive)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    // returns true, if CPU is able to defeat at least one monster of the opponent
    public static boolean canDefeatOneMonsterWithChanges (boolean isNeededToSurvive) {
        Game.BattleSituation = new AIbattle(true, false);
        int monstersCPU = countCPUMonstersWithNonZeroAttack();
        int monstersPlayer = countOwnSummonedMonsters(true);
        if (monstersCPU==0 || monstersPlayer==0) {
            return false;
        }
        else {
            for (int index = 1; index <= monstersCPU; index++){
                SummonedMonster MonsterCPU = SummonedMonster.getNthStrongestMonster(index, false);
                for (int n = 1; n <= 5; n++){ // get the nth strongest monster of the player and simulate the battle damage, if it exists, can bedefeated, and has not been defeated yet
                    SummonedMonster PlayerMonster = SummonedMonster.getNthStrongestMonster(n, true);
                    if (PlayerMonster.isExisting) {
                        if (isAbleToDefeatInSimulation(MonsterCPU, PlayerMonster, true, isNeededToSurvive)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    // returns battle damage the CPU can deal by attacking with its strongest monster without changing anything before
    public static int dealableBattleDamageWithStrongestMonster (boolean isUsingOrinaryChange) {
        if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("simulating possible battle damage");}
        Game.BattleSituation = new AIbattle(isUsingOrinaryChange, false);
        SummonedMonster StrongestMonster = SummonedMonster.getNthStrongestMonster(1, false);
        int battleDamage = 0;
        if (StrongestMonster.isExisting) {
            int monstersPlayer = countOwnSummonedMonsters(true);
            if (monstersPlayer>0) {
                for (int index = 1; index <= monstersPlayer; index++){
                    SummonedMonster PlayerMonster = SummonedMonster.getNthStrongestMonster(index, true);
                    if (PlayerMonster.isExistingAndKnown()) {
                        int newBattleDamage = simulateAmountOfDealableBattleDamage(StrongestMonster, PlayerMonster, isUsingOrinaryChange);
                        if (newBattleDamage > battleDamage) {
                            battleDamage = newBattleDamage;
                        }
                    }
                }
            }
            else {
                return simulateAmountOfDealableBattleDamage(StrongestMonster, new SummonedMonster(true, 6), isUsingOrinaryChange);
            }
        }
        return battleDamage;
    }
    
    // in order not to repeat oneself, reuse this source code for the methods dealableBattleDamageByAttackingWithoutChanges and dealableBattleDamageByAttackingWithOrdinaryModeChanges
    public static int dealableBattleDamageByAttacking (boolean isUsingOrinaryChange, boolean isNeededToSurvive) {
        if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("simulating possible battle damage");}
        Game.BattleSituation = new AIbattle(isUsingOrinaryChange, false);
        int battleDamage = 0;
        int monstersCPU = countOwnSummonedMonsters(false);
        int monstersPlayer = countOwnSummonedMonsters(true);
        boolean[] playerMonsterDefeated = new boolean[6];
        playerMonsterDefeated[0]=false; // I have one more element, just so that the array is not off by one any more
        for (int index = 1; index <= 5; index++){
            SummonedMonster PlayerMonster = SummonedMonster.getNthSummonedMonster(index, true);
            playerMonsterDefeated[index] = !PlayerMonster.isExisting;
        }
        boolean hasNoInvincibleMonstersPlayer = SummonedMonster.hasNoInvincibleMonsterKnownToOpponentForSure(true);
        if (monstersCPU==0) {
            return 0;
        }
        else {
            for (int index = 1; index <= 5; index++){
                SummonedMonster MonsterCPU = SummonedMonster.getNthStrongestMonster(index, false);
                boolean hasDefeatedAll = (playerMonsterDefeated[1] && playerMonsterDefeated[2] && playerMonsterDefeated[3] && playerMonsterDefeated[4] && playerMonsterDefeated[5]);
                if (index>monstersPlayer && hasNoInvincibleMonstersPlayer && hasDefeatedAll) {
                    battleDamage = battleDamage + simulateAmountOfDealableBattleDamage(MonsterCPU, new SummonedMonster(true, 6), isUsingOrinaryChange); // this simulates direct attacks (only possible if player has no invincible monsters for sure, that means also no unknown monsters)
                }
                else {
                    for (int n = 1; n <= 5; n++){ // get the nth strongest monster of the player and simulate the battle damage, if it exists, can be defeated, and has not been defeated yet
                        SummonedMonster PlayerMonster = SummonedMonster.getNthStrongestMonster(n, true);
                        if (PlayerMonster.isExisting && !playerMonsterDefeated[PlayerMonster.sumMonsterNumber]) {
                            if (isAbleToDefeatInSimulation(MonsterCPU, PlayerMonster, isUsingOrinaryChange, isNeededToSurvive)) {
                                battleDamage = battleDamage + simulateAmountOfDealableBattleDamage(MonsterCPU, PlayerMonster, isUsingOrinaryChange);
                                playerMonsterDefeated[PlayerMonster.sumMonsterNumber] = true;
                            }
                        }
                    }
                }
            }
        }
        return battleDamage;
    }
    
    // returns battle damage the CPU can deal by attacking with its monsters by using only free mode changes (including cheat changes, if switched on)
    public static int dealableBattleDamageByAttackingWithOrdinaryModeChanges (boolean isNeededToSurvive) {
        return dealableBattleDamageByAttacking(true, isNeededToSurvive);
    }
    
    // -- about changing modes as preparation for attack strategy --
    
    // makes ordinary changes into attack mode (This can not be negated!), such that afterwards all monsters have attack of at least the highest relevant value of the monsters of the player (that means, doesn't change weaker ones)
    public static void ordinaryChangeToAtt() {
        // determine weakest existing monster of player
        int numberOfMonstersPlayer = countOwnSummonedMonsters(true);
        if (numberOfMonstersPlayer>0) {
            Game.BattleSituation = new AIbattle(true, false);
            SummonedMonster SumMonster = SummonedMonster.getNthStrongestMonster(numberOfMonstersPlayer, true);
            int weakestValue;
            if (SumMonster.isExistingButUnknown()) { // if it has to be, then CPU has to estimate values here
                weakestValue = SumMonster.estimatedDefOfUnknownMonster();
            }
            else {
                weakestValue = SumMonster.relevantValue();
            }
            boolean weakestInDef = !SumMonster.isInAttackMode; // if true, than the CPU has to have even more attack than that value (gets false, if at least one with that value is in attack mode and not invincible)
            for (int index = numberOfMonstersPlayer; index >= 1; index--){
                SumMonster = SummonedMonster.getNthStrongestMonster(index, true);
                if (SumMonster.isExistingAndKnown() && SumMonster.relevantValue()==weakestValue) {
                    weakestInDef = (!SumMonster.isInAttackMode && !SumMonster.isIndestructibleByBattle());
                }
                else {
                    break;
                }
            }
            if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try changing monsters into attack mode");}
            // then go through all monsters of CPU and change those that have at least the lowest value of the opponent
            int numberOfMonstersCPU = countOwnSummonedMonsters(false);
            if (numberOfMonstersCPU>0) {
                for (int index = 1; index <= numberOfMonstersCPU; index++){
                    SumMonster = SummonedMonster.getNthStrongestMonster(index, false);
                    if (!SumMonster.isInAttackMode) {
                        int criticalValue;
                        if (weakestInDef) {
                            criticalValue = weakestValue+1;
                        }
                        else {
                            criticalValue = weakestValue;
                        }
                        AIstrategies.tryTurningMonsterIntoAtt(SumMonster, criticalValue);
                    }
                }
            }
        }

    }
    
    // --- ATTACK STRATEGIES ---
    
    // Goal of this strategy: attack as much deafeatable monsters of the player as possible by attack the strongest it can defeat with its strongest monster
    // and so on until all monsters of the CPU that could defeat a monster or even attack directly have attacked.
    // Before that it tries the piercing strategy, if there is at least one monster indestructible by battle.
    // If one needed the attack pattern to be in accordance with the burn strategy, then the monsters crucial for that strategy will attack safely. They are needed to survive.
    // This is the only strategy for attacking, since piercing and burn strategies are already considered within.
    public static void attackStrategy (boolean isNeededToSurvive, boolean inAccordanceWithBurnStrategy) {
        for (int index = 1; index <= 5; index++){
            if (Game.isBattlePhase() && Game.isAllowingCPUToPlay() && !Game.isPlayersTurn) {
                Game.CPUbehavior.reevaluateStrategies();
                if (Game.BattleSituation.numberOfAttacksLeft==0) {
                    break;
                }
                else {
                    SummonedMonster MonsterCPU = SummonedMonster.getNthStrongestMonster(index, false);
                    if (MonsterCPU.isExisting && MonsterCPU.canStillAttackThisBattlePhase()) {
                        if (SummonedMonster.countOwnSummonedMonsters(true)==0 && Game.isBattlePhase() && Game.isAllowingCPUToPlay()) { // if player has no monsters left
                            AIbattle.cpuDeclareDirectAttack(MonsterCPU);
                        }
                        else {
                            SummonedMonster InvincibleMonster = SummonedMonster.getWeakestMonsterIndestructibleByBattle(true, false);
                            if (InvincibleMonster.isExisting && InvincibleMonster.sumMonsterNumber!=0) {
                                Game.CPUbehavior.switchToPierceStrategy();
                            }
                            else {
                                Game.CPUbehavior.switchPierceStrategyOff();
                            }
                            attackMonsterStrategy(isNeededToSurvive, inAccordanceWithBurnStrategy);
                        }
                    }
                }
            }
        }
    }
    public static void attackMonsterStrategy (boolean isNeededToSurvive, boolean inAccordanceWithBurnStrategy) {
        for (int index = 1; index <= 5; index++){
            Game.CPUbehavior.reevaluateStrategies();
            if (Game.isBattlePhase() && Game.isAllowingCPUToPlay() && !Game.isPlayersTurn) {
                Game.CPUbehavior.reevaluateStrategies(); // This whole repetition of the code is unfortunately needed, because if piercing strategy is active, needs to reconsider attack priorities.
                if (Game.BattleSituation.numberOfAttacksLeft==0) {
                    break;
                }
                else {
                    SummonedMonster MonsterCPU = SummonedMonster.getNthStrongestMonster(index, false);
                    if (MonsterCPU.isExisting && MonsterCPU.canStillAttackThisBattlePhase()) {
                        if (SummonedMonster.countOwnSummonedMonsters(true)==0 && Game.isBattlePhase() && Game.isAllowingCPUToPlay()) {
                            AIbattle.cpuDeclareDirectAttack(MonsterCPU);
                        }
                        else {
                            if (inAccordanceWithBurnStrategy) {
                                isNeededToSurvive = MonsterCPU.isStillNeededForBurnStrategy();
                            }
                            attackMonsterIdeallyWithPiercingDamage(MonsterCPU, isNeededToSurvive);
                        }
                    }
                }
            }
        }
    }
    public static void attackMonsterIdeallyWithPiercingDamage (SummonedMonster MonsterCPU, boolean isNeededToSurvive) {
        boolean isOKToAttack;
        boolean canUsePiercingAttack;
        // these two need to be initialised, or else the compiler complains
        SummonedMonster InvincibleMonster = SummonedMonster.getWeakestMonsterIndestructibleByBattle(true, false);
        SummonedMonster InvincibleMonsterInAtt = SummonedMonster.getWeakestMonsterIndestructibleByBattle(true, true);
        Game.CPUbehavior.reevaluateStrategies();
        if (Game.CPUbehavior.isTryingPiercingStrategy) { // analyse, if one can use piercing damage at all
            if (MonsterCPU.hasPiercingDamageAbility()) {
                InvincibleMonster = SummonedMonster.getWeakestMonsterIndestructibleByBattle(true, false);
                canUsePiercingAttack = (InvincibleMonster.isExisting && InvincibleMonster.sumMonsterNumber!=0);
            }
            else { // This is not really piercing here though. Here one tries to attack over an invincible monster in attack mode. But the idea of putting battle damage over destroying monsters is the same!
                InvincibleMonsterInAtt = SummonedMonster.getWeakestMonsterIndestructibleByBattle(true, true);
                canUsePiercingAttack = (InvincibleMonsterInAtt.isExisting && InvincibleMonsterInAtt.sumMonsterNumber!=0);
            }
        }
        else {
            canUsePiercingAttack = false;
        }
        // attack safely first, if needed and possible
        Game.CPUbehavior.isTryingSafetyFirstStrategy = (SummonedMonster.countOwnFaceDownMonsters(true, true)>=1 && SummonedMonster.canAttackSafely(false));
        if (Game.CPUbehavior.isTryingSafetyFirstStrategy) {
            int posOfMonsterCPU = SummonedMonster.getMonsterThatCanStillAttackSafely(false);
            int posOfMonsterPlayer = SummonedMonster.getUnknownFaceDownMonster(true);
            SummonedMonster PlayerMonster;
            if (posOfMonsterCPU!=0 && posOfMonsterPlayer!=0) {
                MonsterCPU = SummonedMonster.getNthSummonedMonster(posOfMonsterCPU, false);
                PlayerMonster = SummonedMonster.getNthSummonedMonster(posOfMonsterPlayer, true);
                isOKToAttack = (Game.isBattlePhase() && Game.isAllowingCPUToPlay()); // Here one knows not much for sure. Thus estimate.
                if (MonsterCPU.canLikelyDefeat(PlayerMonster) && isOKToAttack) {
                    AIbattle.cpuDeclareAttackOnMonster(MonsterCPU, PlayerMonster);
                }
            }
        }
        // attack with piercing damage, if possible
        if (canUsePiercingAttack) {
            if (MonsterCPU.hasPiercingDamageAbility()) {
                isOKToAttack = (Game.isBattlePhase() && Game.isAllowingCPUToPlay() && (!isNeededToSurvive || (isNeededToSurvive && MonsterCPU.canDefeatForSure(InvincibleMonster, isNeededToSurvive))));
                if (MonsterCPU.simulatingBattleDamageFromAttackingMonster(InvincibleMonster, isNeededToSurvive) > 0 && isOKToAttack) {
                    AIbattle.cpuDeclareAttackOnMonster(MonsterCPU, InvincibleMonster);
                }
            }
            else {
                isOKToAttack = (Game.isBattlePhase() && Game.isAllowingCPUToPlay() && (!isNeededToSurvive || (isNeededToSurvive && MonsterCPU.canDefeatForSure(InvincibleMonsterInAtt, isNeededToSurvive))));
                if (MonsterCPU.simulatingBattleDamageFromAttackingMonster(InvincibleMonsterInAtt, isNeededToSurvive) > 0 && isOKToAttack) {
                    AIbattle.cpuDeclareAttackOnMonster(MonsterCPU, InvincibleMonsterInAtt);
                }
            }
        } // usual attacks here
        if (MonsterCPU.canStillAttackThisBattlePhase()) { // if hasn't attacked meanwhile
            finallyAttackMonsterTryingToDefeatOrDealDamage(MonsterCPU, isNeededToSurvive);
        }
    }
    public static void finallyAttackMonsterTryingToDefeatOrDealDamage (SummonedMonster MonsterCPU, boolean isNeededToSurvive) {
        Game.CPUbehavior.reevaluateStrategies();
        boolean isOKToAttack;
        int posOfStongestMonster = MonsterCPU.getAsTargetStrongestMonsterItCanDefeatForSure(isNeededToSurvive);
        if (posOfStongestMonster!=0 && MonsterCPU.canStillAttackThisBattlePhase()) {
            SummonedMonster PlayerMonster = SummonedMonster.getNthSummonedMonster(posOfStongestMonster, true);
                isOKToAttack = (Game.isBattlePhase() && Game.isAllowingCPUToPlay() && (!isNeededToSurvive || (isNeededToSurvive && MonsterCPU.canDefeatForSure(PlayerMonster, isNeededToSurvive))));
            if (MonsterCPU.simulatingBattleDamageFromAttackingMonster(PlayerMonster, isNeededToSurvive) >= 0 && isOKToAttack) {
                AIbattle.cpuDeclareAttackOnMonster(MonsterCPU, PlayerMonster);
            }
        } // if there is no monster one can defeat or do damage over them for sure, try the ones for that is likely (at least if one doesn't demand it shall survive for sure)
        else if (!isNeededToSurvive) {
            posOfStongestMonster = MonsterCPU.getAsTargetStrongestMonsterItCanLikelyDefeat();
            if (posOfStongestMonster!=0 && MonsterCPU.canStillAttackThisBattlePhase()) {
                SummonedMonster PlayerMonster = SummonedMonster.getNthSummonedMonster(posOfStongestMonster, true);
                isOKToAttack = (Game.isBattlePhase() && Game.isAllowingCPUToPlay() && (!isNeededToSurvive || (isNeededToSurvive && MonsterCPU.canDefeatForSure(PlayerMonster, isNeededToSurvive))));
                if (MonsterCPU.simulatingBattleDamageFromAttackingMonster(PlayerMonster, isNeededToSurvive) >= 0 && isOKToAttack) {
                    AIbattle.cpuDeclareAttackOnMonster(MonsterCPU, PlayerMonster);
                }
            }
        }
    }
    
    
    
}
