package yugiohji;

/**
 * This class creates summoned YuGiOhJi-monsters.
 * It basically takes a monster from a card and summons it
 * by making a button look accordingly.
 * The summoned monsters earn a few more temporal attributes,
 * like in what mode they are, if face down or existing at all, or if their effects are negated,
 * but also the memory about how often they used an effect in the current turn.
 * That is the reason why we need another class for that. 
 * 
 */

import static yugiohji.YuGiOhJi.DeckPlayer;
import static yugiohji.YuGiOhJi.Game;
import static yugiohji.YuGiOhJi.GYDeckPlayer;
import static yugiohji.YuGiOhJi.HandPlayer;
import static yugiohji.YuGiOhJi.Mon;
import static yugiohji.YuGiOhJi.SummonedMonster1CPU;
import static yugiohji.YuGiOhJi.SummonedMonster2CPU;
import static yugiohji.YuGiOhJi.SummonedMonster3CPU;
import static yugiohji.YuGiOhJi.SummonedMonster4CPU;
import static yugiohji.YuGiOhJi.SummonedMonster5CPU;
import static yugiohji.YuGiOhJi.SummonedMonster1Player;
import static yugiohji.YuGiOhJi.SummonedMonster2Player;
import static yugiohji.YuGiOhJi.SummonedMonster3Player;
import static yugiohji.YuGiOhJi.SummonedMonster4Player;
import static yugiohji.YuGiOhJi.SummonedMonster5Player;

public class SummonedMonster {
    
    // instance variables (different for every monster)
    public boolean isPlayersMonster; // if true than belongs to the player, i.e. in lower half of screen (if false, belongs to CPU)
    public int sumMonsterNumber; // a number from 1 to 5 counting from leftmost to rightmost summoned monster
    
    public int att; // attack value (given by monster while summoning + possible boost by equipping)
    public int def; // defence value
    
    public boolean isFaceDown; // if true than attack mode has to be false, because face down always means defense mode
    public boolean isInAttackMode; // if true than is able to attack
    public boolean isModeChangeableThisTurn; // if true than the player can switch between attack & defense mode in this turn (usually false, gets true at end of turn it has been summoned)
    public boolean canStillAttackThisTurn; // if true this monster can still attack in the same battle phase
    public boolean canStillUseOncePerTurnEffect; // gets false for the rest of the turn, if it used up a once per turn effect
    
    public boolean isExisting; // true if there is a monster at this position
    public boolean isNotAbleToUseItsEffects; // true if its effects are negated (permanently)
    
    public boolean isUsingEffectsOfOtherMonster; // if true, look into next interger, which effect it actually has
    public int otherEffectMonsterID; // the ID of the monster whose effect is being copied

    public int numberOfDefeatedMonsters; // no. of monsters defeated by this one (maybe needed for effect of one monster)
    
    public YMonster Monster; // the YMonster as an object (in order to get its effects more easily)
    public YCard Card; // the Ycard of the monster as an object (in order to get its effects more easily)
    public boolean isOriginallyPlayersMonster; // important for giving back cards into right graveyards (differs from isPlayersMonster after has been stolen)
    
    public boolean isKnown; // true if it has been revealed due to a monster effect (so that one can see the big version of the card even if it is face down)
    
    // the following properties are there for the computer to decide which one to attack first
    public int AIinfoPotentialStrength; // temporarily saves largest possible attack values (depends on considered mode (changes))
    public int AIinfoThreatLv; // temporarily saves current threat level towards CPU (depends on max. attack of CPU)
    public int AIinfoNthStrongestMonster; // saves N in Nth strongest monster (1, ..., 5)
    
    // check, if this constructor is actually used
    // constructor
    public SummonedMonster (boolean isPlayersMonster, int sumMonsterNumber, int att, int def, boolean isFaceDown, boolean isInAttackMode, boolean isModeChangeableThisTurn, boolean canStillAttackThisTurn, boolean canStillUseOncePerTurnEffect, boolean isExisting, boolean isNotAbleToUseItsEffects, boolean isUsingEffectsOfOtherMonster, int otherEffectMonsterID, int numberOfDefeatedMonsters, YMonster Monster, YCard Card, boolean isOriginallyPlayersMonster, boolean isKnown)
    {
        // set all the entered values
        this.isPlayersMonster = isPlayersMonster;
        this.sumMonsterNumber = sumMonsterNumber;
        
        this.att = att;
        this.def = def;
        
        this.isFaceDown = isFaceDown;
        this.isInAttackMode = isInAttackMode;
        this.isModeChangeableThisTurn = isModeChangeableThisTurn;
        this.canStillAttackThisTurn = canStillAttackThisTurn;
        this.canStillUseOncePerTurnEffect = canStillUseOncePerTurnEffect;
        
        this.isExisting = isExisting;
        this.isNotAbleToUseItsEffects = isNotAbleToUseItsEffects;
        
        this.isUsingEffectsOfOtherMonster = isUsingEffectsOfOtherMonster;
        this.otherEffectMonsterID = otherEffectMonsterID;
        
        this.numberOfDefeatedMonsters = numberOfDefeatedMonsters;
        
        this.Monster = Monster;
        this.Card = Card;
        this.isOriginallyPlayersMonster = isOriginallyPlayersMonster;

        this.isKnown = isKnown;
        
        this.AIinfoPotentialStrength=0;
        this.AIinfoThreatLv=0;
        this.AIinfoNthStrongestMonster=6; 
        
    }
    
    // standard constructor for empty monster card zones 
    public SummonedMonster (boolean isPlayersMonster, int sumMonsterNumber) {
        // set all the entered values
        this.isPlayersMonster = isPlayersMonster;
        this.sumMonsterNumber = sumMonsterNumber;
        
        this.att = 0;
        this.def = 0;
        
        this.isFaceDown = false;
        this.isInAttackMode = true;
        this.isModeChangeableThisTurn = false;
        this.canStillAttackThisTurn = false;
        this.canStillUseOncePerTurnEffect = false;
        
        this.isExisting = false;
        this.isNotAbleToUseItsEffects = false;
        
        this.isUsingEffectsOfOtherMonster = false;
        this.otherEffectMonsterID = 0;
        
        this.numberOfDefeatedMonsters = 0;
        
        this.Monster = YuGiOhJi.NoMonster;
        this.Card = YuGiOhJi.NoCard;
        this.isOriginallyPlayersMonster = isPlayersMonster; // changes only after has been stolen
        
        this.isKnown = false;
        
        this.AIinfoPotentialStrength=0;
        this.AIinfoThreatLv=0;
        this.AIinfoNthStrongestMonster=6; 
        
        
    }
    
    // basic setter and getter methods
    
    // returns the nth summoned monster of a given player
    public static SummonedMonster getNthSummonedMonster (int n, boolean isBelongingToPlayer) {
        if (isBelongingToPlayer) {
            switch (n) {
                case 1: return SummonedMonster1Player;
                case 2: return SummonedMonster2Player;
                case 3: return SummonedMonster3Player;
                case 4: return SummonedMonster4Player;
                case 5: return SummonedMonster5Player;
                default: YuGiOhJi.debugDialog("Error: out of bounds in getNthSummonedMonster(...); attempted N: " + n); return null;
            }
        }
        else {
            switch (n) {
                case 1: return SummonedMonster1CPU;
                case 2: return SummonedMonster2CPU;
                case 3: return SummonedMonster3CPU;
                case 4: return SummonedMonster4CPU;
                case 5: return SummonedMonster5CPU;
                default: YuGiOhJi.debugDialog("Error: out of bounds in getNthSummonedMonster(...); attempted N: " + n); return null;
            }
        }
    }
    
    // returns true, if a monster has same position and owner as another monster (so it is basically the same monster, even if some properties might have changed meanwhile)
    public boolean isBasicallySameMonster (SummonedMonster SumMonster) {
        return (isPlayersMonster==SumMonster.isPlayersMonster && sumMonsterNumber==SumMonster.sumMonsterNumber);
    }
    
    // determines the 1st "summoned monster" from left that is not existing
    public static SummonedMonster determineFreeMonsterZone (boolean isBelongingToPlayer) {
        for (int index = 1; index <= 5 ; index++){
            if (!getNthSummonedMonster(index, isBelongingToPlayer).isExisting) {
                return getNthSummonedMonster(index, isBelongingToPlayer);
            }
        }
        return (new SummonedMonster(true, 0)); // avoid retuning null, just to be safe
    }
    
    // returns true, if a given player has a free monster card zone (important for many summoning conditions)
    public static boolean hasFreeMonsterZone (boolean isConcerningPlayer) {
        return (countOwnSummonedMonsters(isConcerningPlayer) < 5);
    }
    
    // --- about often used combinations of properties of summoned monsters (many used for AI battle strategies) ---
    
    // counts the number of attacks left in this turn
    public static int countPotentialNumberOfLeftAttacksThisTurn (boolean isConcerningPlayer) {
        int numberOfAttacksLeft = 0;
        for (int index = 1; index <= 5; index++){
            SummonedMonster SumMonster = getNthSummonedMonster(index, isConcerningPlayer);
            if (SumMonster.isExisting && SumMonster.canStillAttackThisTurn) {numberOfAttacksLeft++;}
        }
        return numberOfAttacksLeft;
    }
    
    // returns true, if the monster can still attack, if not mode changed
    public boolean canStillAttackThisBattlePhase() {
        return (isExisting && isInAttackMode && canStillAttackThisTurn);
    }
    
    // counts the number of attacks left in this battle phase of monsters in attack mode of a given player
    public static int countNumberOfAttacksLeftThisBattlePhase (boolean isConcerningPlayer) {
        int numberOfAttacksLeft = 0;
        for (int index = 1; index <= 5; index++){
            SummonedMonster SumMonster = getNthSummonedMonster(index, isConcerningPlayer);
            if (SumMonster.canStillAttackThisBattlePhase()) {numberOfAttacksLeft++;}
        }
        return numberOfAttacksLeft;
    }
    
    // return true, if a given monster is the lower monster on its monster card
    public boolean isLowerMonster() {
        return (Monster.monsterId==Card.lowMonster.monsterId);
    }
    
    // returns, if a given monster is unkown
    public boolean isExistingAndKnown() {
        return (isExisting && (!isFaceDown || isKnown));
    }
    
    // returns, if a given monster is unkown
    public boolean isExistingButUnknown() {
        return (isExisting && isFaceDown && !isKnown);
    }
    
    // returns, if a given monster is unkown
    public boolean isKnownToPlayer() {
        return (isPlayersMonster || isExistingAndKnown());
    }
    
    // counts the number of face down monsters of a given player that the opponent doesn't know yet
    public static int countOwnUnknownMonsters (boolean isConcerningPlayer) {
        int counter=0;
        for (int index = 1; index <= 5; index++){
            SummonedMonster SumMonster = getNthSummonedMonster(index, isConcerningPlayer);
            if (SumMonster.isExistingButUnknown()) {counter++;}
            
        }
        return counter;
    }
    
    // returns an interger telling how threatening a monster of the player is to the CPU
    // lv3: has working powerful opt. get-rid-of-effect, or more attack than the max. attack of CPU, or 3000 attack or more
    // lv2: has working relatively threatening effect
    // lv1: has at least as much defence than max. attack of CPU
    // lv0: the rest (posing no immediate threat)
    public int threatLevel (int maxAttCPU) {
        if (!isExisting || isExistingButUnknown()) {
            return 0;
        }
        else {
            if ( (att>2000 && att>maxAttCPU) || att>=3000) {
                return 3;
            }
            else {
                if (!isNotAbleToUseItsEffects) {
                    YMonster Monst = YMonster.getMonsterById(effectiveMonsterId());
                    if ( Monst.equals(Mon.Neutraliser) || Monst.equals(Mon.BigAttackStopper) || Monst.equals(Mon.AttackStopper) || Monst.equals(Mon.BigBackBouncer) || Monst.equals(Mon.Eradicator) || Monst.equals(Mon.BigBurner) || Monst.equals(Mon.Burner) || Monst.equals(Mon.MonsterStealer) || Monst.equals(Mon.ModeChanger) || Monst.equals(Mon.BuggedUpgrade) ) {
                        return 3; // monsters: Neutraliser, BigAttackStopper, AttackStopper, BigBackBouncer, Eradicator, BigBurner, Burner, MonsterStealer, ModeChanger, BuggedUpgrade
                    }
                    else if ( Monst.equals(Mon.Shield) || Monst.equals(Mon.Sword) || Monst.equals(Mon.Lance) || Monst.equals(Mon.CardGrabber) || Monst.equals(Mon.CopyCat) || Monst.equals(Mon.SkillStealer) || Monst.equals(Mon.Incorruptible) || Monst.equals(Mon.HolyLance) || Monst.equals(Mon.Necromancer) || Monst.equals(Mon.BigBanisher) || Monst.equals(Mon.Napalm) || Monst.equals(Mon.Demon) ) {
                        return 2; // monsters: Shield, Sword, Lance, CardGrabber, CopyCat, SkillStealer, Incorruptible, HolyLance, Necromancer, BigBanisher, Napalm, Demon
                    }
                }
                if (def>=maxAttCPU) {
                    return 1;
                }
                else {
                    return 0;
                }
            }
        }
    }
    
    // copies all properties of a given monster
    // (properties of defeated monsters have to be stored, because are still needed for suicidal effects happening later)
    public void rememberPropertiesOfMonster (SummonedMonster OldMonster) {
        isPlayersMonster = OldMonster.isPlayersMonster;
        sumMonsterNumber = OldMonster.sumMonsterNumber;
        att = OldMonster.att;
        def = OldMonster.def;
        isFaceDown = OldMonster.isFaceDown;
        isInAttackMode = OldMonster.isInAttackMode;
        isModeChangeableThisTurn = OldMonster.isModeChangeableThisTurn;
        canStillAttackThisTurn = OldMonster.canStillAttackThisTurn;
        canStillUseOncePerTurnEffect = OldMonster.canStillUseOncePerTurnEffect;
        isExisting = true; // since originally used to remember properties of defeated monsters, set here true, so that all properties can be evaluated properly (of course, the defeated monster does not exist any more, but all checked properties are only evaluated positively, if a monster exists)
        isNotAbleToUseItsEffects = OldMonster.isNotAbleToUseItsEffects;
        isUsingEffectsOfOtherMonster = OldMonster.isUsingEffectsOfOtherMonster;
        otherEffectMonsterID = OldMonster.otherEffectMonsterID;
        numberOfDefeatedMonsters = OldMonster.numberOfDefeatedMonsters;
        Monster = OldMonster.Monster;
        Card = OldMonster.Card;
        isOriginallyPlayersMonster = OldMonster.isOriginallyPlayersMonster;
        isKnown = OldMonster.isKnown;
    }
    
    // returns attack value, if a given monster is in attack mode
    // returns defence value, if in defence mode
    // returns zero, if not existing
    public int relevantValue() {
        if (!isExisting) {
            return 0;
        }
        else {
            if (isInAttackMode) {
                return att;
            }
            else {
                return def;
            }
        }
    }
    
    // returns the defence value (or the def. value of the other monster on the card, whichever is lower with cheat change rule), if in attack mode
    // returns the attack value (or the att. value of the other monster on the card, whichever is lower with cheat change rule), if in defence mode
    // returns zero, if not existing
    public int getLowestRelevantValueByEffectChange() {
        if (isPlayersMonster && isExistingButUnknown()) {
            return 0; // This method is only used by the computer. Thus it is not allowed to estimate the strength of unkown monsters of the player.
        }
        if (!isExisting) {
            return 0;
        }
        else {
            if (isInAttackMode) {
                int otherMonsterDef = otherMonsterDef();
                if (Game.isSwitchingOnCheatModeChangingRule && otherMonsterDef < def) {
                    return otherMonsterDef;
                }
                else {
                    return def;
                }
            }
            else {
                int otherMonsterAtt = otherMonsterAtt();
                if (Game.isSwitchingOnCheatModeChangingRule && otherMonsterAtt < att) {
                    return otherMonsterAtt;
                }
                else {
                    return att;
                }
            }
        }
    }
    
    // returns true, if the relevant value can be decreased by an mode change
    public boolean canBeWeakenedByEffectChange() {
        return (getLowestRelevantValueByEffectChange() < relevantValue());
    }
    
    // returns the battle damage that a monster would deal (or receive!) from attacking another monster
    // (assuming neither attack nor effects get negated)
    // in case one attacks a stronger monster, the returned value is negative (i.e. reflected battle damage)
    public int estimatingBattleDamageFromAttackingMonster (SummonedMonster GuardingMonster, boolean isNeededToSurvive) {
        int guardingValue;
        if (GuardingMonster.isExistingButUnknown()) {
            guardingValue = GuardingMonster.estimatedDefOfUnknownMonster(); // estimate the def value, if it has to be
        }
        else {
            guardingValue = GuardingMonster.relevantValue();
        } // this also includes direct attacks, in case guarding monster is not existing
        if (!GuardingMonster.isExisting || GuardingMonster.isInAttackMode || (hasPiercingDamageAbility() && !GuardingMonster.isImmune()) ) { // it's ok to ask for immunity, because it can only be immune, if face up any way
            if (isNeededToSurvive && GuardingMonster.isInAttackMode && att <= guardingValue) {
                return 0;
            }
            else {
                return (att-guardingValue);
            }
        }
        else {
            return 0;
        }
    }
    // like above (returning the expected battle damage), but in case of unknown monster returns zero instead of estimating it 
    public int simulatingBattleDamageFromAttackingMonsterForSure (SummonedMonster GuardingMonster) {
        if (GuardingMonster.isExistingButUnknown()) {
            return 0;
        }
        else {
            return estimatingBattleDamageFromAttackingMonster(GuardingMonster, true);
        }
    }
    // simulates battle damage: If the monster needs to survive simulates exactly, if not, only estimates.
    public int simulatingBattleDamageFromAttackingMonster (SummonedMonster GuardingMonster, boolean isNeededToSurvive) {
        if (isNeededToSurvive) {
            return simulatingBattleDamageFromAttackingMonsterForSure(GuardingMonster);
        }
        else {
            return estimatingBattleDamageFromAttackingMonster(GuardingMonster, false);
        }
    }
    
    // returns true, if the monster (before the dot) can defeat a given monster (in the argument) in battle
    // optionally one can check for targets the monster can survive
    public boolean canDefeatForSure (SummonedMonster GuardingMonster, boolean isNeededToSurvive) {
        if (GuardingMonster.isExistingButUnknown() || GuardingMonster.isIndestructibleByBattle() || att==0 || !canStillAttackThisBattlePhase()) {
            return false; // It's ok to check for indestructibility, because it is either unknown and one can not defeat it for sure, or it is known to be invincible and then one can not defeat it either for sure.
        }
        else if (GuardingMonster.hasUseableSuicideGetRidOfEffect() && !isImmune() && !isImmuneWhileWinning() && isNeededToSurvive) {
            return false; // Here one could maybe defeat it. But if one demands the attacking monster to survive, then this will turn false here.
        }
        else { // this also includes being able to "defeat" a non-existing monster
            if (GuardingMonster.isInAttackMode) {
                if (isNeededToSurvive) {
                    return (att > GuardingMonster.att);
                }
                else {
                    return (att >= GuardingMonster.att);
                }
            }
            else {
                return (att > GuardingMonster.def);
            }
        }
    }
    
    // returns true, if the monster (before the dot) can likely defeat a given unknown monster (in the argument) in battle
    // (def values of unkown monsters are estimated depending on the CPU behavior)
    public boolean canLikelyDefeat (SummonedMonster GuardingMonster) {
        if ((GuardingMonster.isIndestructibleByBattle() && GuardingMonster.isExistingAndKnown()) || att==0 || !canStillAttackThisBattlePhase()) {
            return false;
        }
        else { // this also includes being able to "defeat" a non-existing monster of a player
            if (GuardingMonster.isInAttackMode) {
                return (att >= GuardingMonster.att);
            }
            else {
                return (att > GuardingMonster.estimatedDefOfUnknownMonster());
            }
        }
    }
    
    // estimates and returns the defence value of an unknown monster, taking into account the known number of stars
    // (if MOOK, MIDBOSS, etc.) and also considering its equipped Shields
    // In case the monster is known, simply returns the defence value
    public int estimatedDefOfUnknownMonster() {
        if (isExistingAndKnown()) {
            return def;
        }
        else {
            int estimatedDef;
            if (Monster.stars==1) {
                estimatedDef = Game.CPUbehavior.estimatedMookDefence;
            }
            else if (Monster.stars==2) {
                estimatedDef = Game.CPUbehavior.estimatedMidbossDefence;
            }
            else if (Monster.stars==3) {
                estimatedDef = Game.CPUbehavior.estimatedEndbossDefence;
            }
            else if (Monster.stars==4) {
                estimatedDef = Game.CPUbehavior.estimatedGodDefence;
            }
            else {
                estimatedDef = 0;
            }
            return estimatedDef + defBoostByShields();
        }
    }
    
    // returns the number of defence points of a monster increased by all the shields it is equipped with
    public int defBoostByShields() {
        int defBoost=0;
        boolean isPlayerMonster = isPlayersMonster;
        int position = sumMonsterNumber;
        EStack Stack = EStack.getNthStack(position, isPlayerMonster);
        if (Stack.numberOfCards>0) {
            for (int index = Stack.numberOfCards; index >= 1; index--){
                if (Stack.getNthCardOfStack(index).lowMonster.equals(Mon.Shield)  && !Stack.getNegationStatusOfNthEquipCard(index)) {
                    defBoost = defBoost + Mon.Shield.ShieldDefBoost();
                }
            }
        }
        return defBoost;
    }
    
    // returns the largest possible attack value a given monster can have depending on usage of changes 
    public int getLargestPossibleAttValue (boolean isUsingOrinaryChange) {
        if (isUsingOrinaryChange) {
            return getLargestAttValueByOrdinaryChange();
        }
        else {
            return getLargestAttValueWithoutChange();
        }
    }
    
    // returns the attack value of a monster, if in attack mode (zero, if in def mode)
    public int getLargestAttValueWithoutChange() {
        if (isPlayersMonster && isExistingButUnknown()) {
            return 0; // This method is only used by the computer. Thus it is not allowed to estimate the strength of unkown monsters of the player.
        }
        if (isInAttackMode) {
            return att;
        }
        else {
            return 0;
        }
    }
    
    // returns the attack value of the other monster printed on the same card
    // (also consider att boost by Swords and by Steep Learning Curve)
    public int otherMonsterAtt() {
        int currentAtt;
        if (isLowerMonster()) {
            currentAtt=Card.upMonster.att;
        }
        else { // here also consider possible attack boost by Steep Learning Curve
            currentAtt=Card.lowMonster.att;
            if (Card.equals(YuGiOhJi.MonsterStealerSteepLearningCurve) && numberOfDefeatedMonsters>0) {
                currentAtt = currentAtt + numberOfDefeatedMonsters*Mon.SteepLearningCurve.SteepLearningCurveAttBoost();
            }
        }
        // consider possible power-ups by being equipped with Sword(s)
        EStack Stack = EStack.getNthStack(sumMonsterNumber, isPlayersMonster);
        if (Stack.numberOfCards>0) {
            for (int index = Stack.numberOfCards; index >= 1; index--){
                if (!Stack.getNegationStatusOfNthEquipCard(index) && Stack.getNthCardOfStack(index).lowMonster.equals(Mon.Sword)) {
                    currentAtt = currentAtt + Mon.Sword.SwordAttBoost();
                }
            }
        }
        return currentAtt;
    }
    
    // returns the defence value of the other monster printed on the same card
    // (also consider def boost by Shields)
    public int otherMonsterDef() {
        int currentDef;
        if (isLowerMonster()) {
            currentDef=Card.upMonster.def;
        }
        else {
            currentDef=Card.lowMonster.def;
        }
        // consider possible power-ups by being equipped with Shield(s)
        EStack Stack = EStack.getNthStack(sumMonsterNumber, isPlayersMonster);
        if (Stack.numberOfCards>0) {
            for (int index = Stack.numberOfCards; index >= 1; index--){
                if (!Stack.getNegationStatusOfNthEquipCard(index) && Stack.getNthCardOfStack(index).lowMonster.equals(Mon.Shield)) {
                    currentDef = currentDef + Mon.Shield.ShieldDefBoost();
                }
            }
        }
        return currentDef;
    }
    
    // returns the attack value, if in attack mode or can be ordinarily changed into attack mode
    // (returns zero, if can not be changed)
    public int getLargestAttValueByOrdinaryChange() {
        if (isPlayersMonster && isExistingButUnknown()) {
            return 0; // This method is only used by the computer. Thus it is not allowed to estimate the strength of unkown monsters of the player.
        }
        if (isInAttackMode) {
            return att;
        }
        else {
            if (isModeChangeableThisTurn) {
                int otherMonsterAtt = otherMonsterAtt();
                if (Game.isSwitchingOnCheatModeChangingRule && otherMonsterAtt > att) {
                    return otherMonsterAtt;
                }
                else {
                    return att;
                }
            }
            else {
                return 0;
            }
        }
    }
    
    // returns the defence value, if in defence mode or can be ordinarily changed into defence mode
    // (returns zero, if can not be changed)
    public int getLargestDefValueByOrdinaryChange() {
        if (isPlayersMonster && isExistingButUnknown()) {
            return 0; // This method is only used by the computer. Thus it is not allowed to estimate the strength of unkown monsters of the player.
        }
        if (!isInAttackMode) {
            return def;
        }
        else {
            if (isModeChangeableThisTurn) {
                int otherMonsterDef = otherMonsterDef();
                if (Game.isSwitchingOnCheatModeChangingRule && otherMonsterDef > def) {
                    return otherMonsterDef;
                }
                else {
                    return def;
                }
            }
            else {
                return 0;
            }
        }
    }
    
    // prepares a sorting of monsters according to potential strength by just counting how many monsters have a higher strength
    // This sorting is not complete, if there are monsters with the same strength.
    // Therefore one still has to consider (the threat level for monsters of the player and) the summoned monster position using other methods
    public void presortAccordingToPotentialStrength() {
        int numberOfStrongerMonsters = 0;
        SummonedMonster SumMonster;
        for (int index = 1; index <= 5; index++){
            SumMonster = getNthSummonedMonster(index, isPlayersMonster); // only consider monsters on same side
            if (isWeakerThan(SumMonster)) {
                numberOfStrongerMonsters++;
            }
        }
        AIinfoNthStrongestMonster = 1 + numberOfStrongerMonsters; // If there are monsters with same strength, this rank is not final!
    }
    
    // returns true, if a monster on the same side is weaker than the monster in the argument:
    // don't compare to itself & other is stronger (and in case of piercing strategy is same or more piercing)
    public boolean isWeakerThan (SummonedMonster SumMonster) {
        if (Game.CPUbehavior.isTryingPiercingStrategy) {
            boolean firstPiercing = hasPiercingDamageAbility();
            boolean secondPiercing = SumMonster.hasPiercingDamageAbility();
            boolean bothSamePiercing = firstPiercing==secondPiercing;
            boolean firstLessPiercing = (!firstPiercing && secondPiercing);
            boolean isSameOrLessPiercing = (firstLessPiercing || bothSamePiercing);
            return (!isBasicallySameMonster(SumMonster) && SumMonster.AIinfoPotentialStrength > AIinfoPotentialStrength && isSameOrLessPiercing);
        }
        else {
            return (!isBasicallySameMonster(SumMonster) && SumMonster.AIinfoPotentialStrength > AIinfoPotentialStrength);
        }
    }
    
    // prepares a sorting of monsters according to threat level by just counting how many monsters have a higher threat level
    // This sorting is not complete, if there are monsters with the same threat level.
    // Therefore one still has to consider the summoned monster position using other methods
    public void furtherPresortAccordingToThreatLv() {
        int numberOfBiggerThreats = 0;
        SummonedMonster SumMonster;
        for (int index = 1; index <= 5; index++){
            SumMonster = getNthSummonedMonster(index, isPlayersMonster); // only consider monsters on same side
            if (isSmallerThreatThan(SumMonster)) {
                numberOfBiggerThreats++;
            }
        }
        AIinfoNthStrongestMonster = AIinfoNthStrongestMonster + numberOfBiggerThreats; // If there are monsters with same strength & threat level, this rank is not final!
    }
    
    // returns true, if a monster on the same side has the same strength and a smaller threat level than the monster in the argument:
    // don't compare to itself & have same strength & other is bigger threat
    public boolean isSmallerThreatThan (SummonedMonster SumMonster) {
        return (!isBasicallySameMonster(SumMonster) && SumMonster.AIinfoPotentialStrength==AIinfoPotentialStrength && SumMonster.AIinfoThreatLv > AIinfoThreatLv);
    }
    
    // sorts the monsters (already presorted according to strength (& threat lv)) according to summoned monster position
    // This last criterion exists just so that there is a definitive order of all monsters.
    public void finallySortAccordingToPosition (boolean isConsideringThreatLv) {
        int numberOfMoreLeftMonster = 0; // number of monsters with smaller battle position
        SummonedMonster SumMonster;
        for (int index = 1; index <= 5; index++){
            SumMonster = getNthSummonedMonster(index, isPlayersMonster); // only consider monsters on same side
            if (isFurtherRightThan(SumMonster, isConsideringThreatLv)) {
                numberOfMoreLeftMonster++;
            }
        }
        AIinfoNthStrongestMonster = AIinfoNthStrongestMonster + numberOfMoreLeftMonster; // This sorting should be final, since no two summoned monsters are supposed to have the same position.
    }
    
    // returns true, if a monster on the same side has the same strength (and same threat level) and a smaller position number than the monster in the arguments:
    // don't compare to itself & have same strength (& have same threat level) & other has smaller summoned monster number
    public boolean isFurtherRightThan (SummonedMonster SumMonster, boolean isConsideringThreatLv) {
        if (isConsideringThreatLv) {
            return (!isBasicallySameMonster(SumMonster) && SumMonster.AIinfoPotentialStrength==AIinfoPotentialStrength && SumMonster.AIinfoThreatLv==AIinfoThreatLv && SumMonster.sumMonsterNumber < sumMonsterNumber);
        }
        else {
            return (!isBasicallySameMonster(SumMonster) && SumMonster.AIinfoPotentialStrength==AIinfoPotentialStrength && SumMonster.sumMonsterNumber < sumMonsterNumber);
        }
    }
    
    // returns the nth strongest monster of a given player
    // returns a non-existing monster (with position zero), if there is no such monster
    public static SummonedMonster getNthStrongestMonster (int n, boolean isConcerningPlayer) {
        for (int index = 1; index <= 5; index++){
            SummonedMonster SumMonster = getNthSummonedMonster(index, isConcerningPlayer);
            if (SumMonster.AIinfoNthStrongestMonster==n) {
                return SumMonster;
            }
        }
        return (new SummonedMonster(isConcerningPlayer, 0));
    }
    
    // returns true, if a given player has amonster with threat lv 3 (assumes that an AIbattle object has been called before, so that this info is up to date!)
    public static boolean isHavingThreatLv3Monster (boolean isConcerningPlayer) {
        return getNthStrongestMonster(1, isConcerningPlayer).AIinfoThreatLv==3; // it's enough to check the "strongest" monster
    }
    
    // returns the position (as integer) of the strongest monster of the opponent it can defeat it battle (and survive doing that)
    // returns zero, if there is no such monster
    public int getAsTargetStrongestMonsterItCanDefeatForSure (boolean isSurviving) {
        for (int n = 1; n <= countOwnSummonedMonsters(!isPlayersMonster); n++){
            for (int index = 1; index <= 5; index++){
                SummonedMonster SumMonster = getNthSummonedMonster(index, !isPlayersMonster);
                if (SumMonster.AIinfoNthStrongestMonster==n && SumMonster.isExisting && canDefeatForSure(SumMonster, isSurviving)) {
                    return SumMonster.sumMonsterNumber;
                }
            }
        }
        return 0;
    }
    
    // returns the position (as integer) of the strongest monster of the opponent it can probably defeat it battle (estimating strength of face down monsters)
    // returns zero, if there is no such monster
    public int getAsTargetStrongestMonsterItCanLikelyDefeat() {
        for (int n = 1; n <= countOwnSummonedMonsters(!isPlayersMonster); n++){
            for (int index = 1; index <= 5; index++){
                SummonedMonster SumMonster = getNthSummonedMonster(index, !isPlayersMonster);
                if (SumMonster.AIinfoNthStrongestMonster==n && SumMonster.isExisting && canLikelyDefeat(SumMonster)) {
                    return SumMonster.sumMonsterNumber;
                }
            }
        }
        return 0;
    }
   
    // sets the AIinfo properties to standard values
    public static void resetAllAIinfo() {
        for (int index = 1; index <= 5; index++){
            SummonedMonster SumMonster = getNthSummonedMonster(index, true); // erase AI info of all player monsters
            SumMonster.AIinfoPotentialStrength=0;
            SumMonster.AIinfoThreatLv=0;
            SumMonster.AIinfoNthStrongestMonster=6;
            SumMonster = getNthSummonedMonster(index, false); // same for all monsters of computer
            SumMonster.AIinfoPotentialStrength=0;
            SumMonster.AIinfoThreatLv=0;
            SumMonster.AIinfoNthStrongestMonster=6;
        }
    }
    
    // returns true, if a summoned monster is still needed for a burn strategy and thus has to attack carefully
    public boolean isStillNeededForBurnStrategy() {
        if (Game.CPUbehavior.isTryingBurnStrategy) {
            return (Monster.equals(Mon.Burner) || Monster.equals(Mon.BigBurner) || Monster.equals(Mon.Necromancer));
        }
        return false;
    }
    
    
    // -- methods concerning the things cards can do on the field (change between att/def mode, calling of monster effects etc.) --
    
    // --- about playing cards on field ---
    
    // plays a Monster card on the field, if one is allowed to do so
    public static void attemptPlayMonster (SummonedMonster SumMonster){
        boolean isChangeable = SumMonster.isModeChangeable();
        boolean isAbleToUseEffect = SumMonster.checkMonsterEffectOnField();
        int intDialogResult=-1;
        if (isChangeable && isAbleToUseEffect) {
            intDialogResult = YuGiOhJi.multipleChoiceDialog("What do you want to do?", SumMonster.Monster.monsterName, new String[]{"change its mode", "use its effect"}, "");
        }
        else if (isChangeable && !isAbleToUseEffect) {
            intDialogResult = YuGiOhJi.multipleChoiceDialog("What do you want to do?", SumMonster.Monster.monsterName, new String[]{"change its mode"}, "");
        }
        else if (!isChangeable && isAbleToUseEffect) {
            intDialogResult = YuGiOhJi.multipleChoiceDialog("What do you want to do?", SumMonster.Monster.monsterName, new String[]{"use its effect"}, "");
            if (intDialogResult==0) {intDialogResult=1;}
        }
        
        if (intDialogResult==0) {
            SumMonster.changeMode(false);
        }
        else if (intDialogResult==1) {
            YMonster.activateOptionalEffectOnField(SumMonster);
        }
        
    }
    
    // returns true, if a summoned monster can use an effect
    public boolean checkMonsterEffectOnField() {
        if (isFaceDown || isNotAbleToUseItsEffects) {
            return false;
        }
        else { // test for monster effects (for conditions see class YMonster, or look into used methods)
            int effectiveMonsterId = effectiveMonsterId();
            if (effectiveMonsterId==Mon.Obstacle.monsterId || effectiveMonsterId==Mon.God.monsterId) { // show a card effect (player has to have revealable card and CPU has to have an unknown card)
                return ( HandPlayer.numberOfCards+countOwnFaceDownMonsters(true, false)>=1 && countOwnFaceDownMonsters(false, true)>=1 );
            }
            else if (effectiveMonsterId==Mon.Burner.monsterId) {
                return lookForMook(true);
            }
            else if (effectiveMonsterId==Mon.BigBurner.monsterId) {
                return countOwnSummonedMonsters(true)>=2; // needs at least one other monster than itself as tribute
            }
            else if (effectiveMonsterId==Mon.CardGrabber.monsterId) { // it is enough to check cheaper effect of them 
                return (CardOptions.countPayableCosts(true)>=2 && DeckPlayer.numberOfCards>0);
            }
            else if (effectiveMonsterId==Mon.SkillStealer.monsterId) {
                return (HandPlayer.numberOfCards>0 && stealableEffectsExistOnField());
            }
            else if (effectiveMonsterId==Mon.CopyCat.monsterId) {
                return (HandPlayer.stealableEffectsExistOnHand());
            }
            else if (effectiveMonsterId==Mon.ModeChanger.monsterId) {
                return CardOptions.countPayableCosts(true)>=2;
            }
            else if (effectiveMonsterId==Mon.Necromancer.monsterId) {
                return canUseAnEffectOfNecromancer(true);
            }
            else if (effectiveMonsterId==Mon.BigBackBouncer.monsterId) {
                return CardOptions.countPayableCosts(true)>=2;
            }
            else if (effectiveMonsterId==Mon.BigBanisher.monsterId) { // big banisher
                return Deck.numberOfCardsInGY(false)>=1;
            }
            else if (effectiveMonsterId==Mon.MonsterStealer.monsterId) { // don't forget that one can only use its effect, if the monster has not attacked this turn yet
                return ( hasFreeMonsterZone(true) && hasStealableCards(false) && CardOptions.countPayableCosts(true)>=4 && canStillAttackThisTurn );
            }
            else if (effectiveMonsterId==Mon.Eradicator.monsterId) {
                return (CardOptions.countPayableCosts(true)>=2 && hasCardsDestructibleByEffect(false));
            }
            else if (effectiveMonsterId==Mon.Neutraliser.monsterId) { // only optional effect on field
                return canUseEffectNegateOnField(true);
            }
            else if (effectiveMonsterId==Mon.Shield.monsterId || effectiveMonsterId==Mon.Sword.monsterId || effectiveMonsterId==Mon.Lance.monsterId || effectiveMonsterId==Mon.BuggedUpgrade.monsterId) {
                if (isUsingEffectsOfOtherMonster) { // forbid equipping when copied equip effect of Lance
                    return false;
                }
                else {
                    return canUseEquipEffectFromMonster();
                }
            }
            else if (effectiveMonsterId==Mon.Demon.monsterId) { // check for both: the equipping effect, as well as the cheat change effect: for the latter, there has to be another Demon on the field
                return canUseOnFieldEffectsOfDemon();
            }
            return false;
        }
    }
    
    // --- about mode changes ---
    
    // returns true, if a summoned monster can change is mode by itself (can always be changed by effect though)
    public boolean isModeChangeable() {
        return (isExisting && isModeChangeableThisTurn);
    }
    
    // change between face up attack and defence mode
    // if monster is in face down defence mode, make flip summon (i.e. into face up attack mode)
    // also update corresponding graphics
    public void changeMode (boolean isChangedByEffect) {
        if (Game.isSwitchingOnCheatModeChangingRule && Game.isPlayersTurn) {
            int intDialogResult = YuGiOhJi.multipleChoiceDialog("Using the rule results in change between the two monsters on the card.", "Use \"Cheat Mode Changing\" rule?", new String[]{"Use rule!", "Don't use rule!"}, "Don't use rule!");
            if (intDialogResult==0) {
                cheatChangeMode(isChangedByEffect);
            }
            else {
                allowedChangeMode(isChangedByEffect);
            }
        }
        else {
            allowedChangeMode(isChangedByEffect);
        }
    }
    // alternative version used by CPU (which tells by an additional argument, if the cheat rule shall be applied, if possible)
    public void changeMode (boolean isChangedByEffect, boolean isUsingCheatChangeRuleIfPossible) {
        if (Game.isSwitchingOnCheatModeChangingRule) {
            if (isUsingCheatChangeRuleIfPossible) {
                cheatChangeMode(isChangedByEffect);
            }
            else {
                allowedChangeMode(isChangedByEffect);
            }
        }
        else {
            allowedChangeMode(isChangedByEffect);
        }
    }
    
    // makes the allowed change between Att/Def mode (according to the usual rules)
    public void allowedChangeMode (boolean isChangedByEffect) {
        if (isInAttackMode) {
            isInAttackMode=false;
            isFaceDown=false;
            if (isPlayersMonster) {
                YuGiOhJi.setMonsterButtonIcon(Monster.cardPathDef, sumMonsterNumber, isPlayersMonster, isInAttackMode, true);
            }
            else { // monsters of the CPU appear upside down
                if (isLowerMonster()) {
                    YuGiOhJi.setMonsterButtonIcon(Card.upMonster.cardPathDef, sumMonsterNumber, isPlayersMonster, isInAttackMode, true);
                }
                else {
                    YuGiOhJi.setMonsterButtonIcon(Card.lowMonster.cardPathDef, sumMonsterNumber, isPlayersMonster, isInAttackMode, true);
                }
            }
        }
        else {
            isInAttackMode=true;
            isFaceDown=false;
            if (isPlayersMonster) {
                YuGiOhJi.setMonsterButtonIcon(Monster.cardPathAtt, sumMonsterNumber, isPlayersMonster, isInAttackMode, true);
            }
            else { // monsters of the CPU appear upside down
                if (isLowerMonster()) {
                    YuGiOhJi.setMonsterButtonIcon(Card.upMonster.cardPathAtt, sumMonsterNumber, isPlayersMonster, isInAttackMode, true);
                }
                else {
                    YuGiOhJi.setMonsterButtonIcon(Card.lowMonster.cardPathAtt, sumMonsterNumber, isPlayersMonster, isInAttackMode, true);
                }
            }
        }
        updateAttDefDisplay(); // update displayed values (in case it was flipped face up, but in general to change the style of the text)
        // The idea is that if you change the mode of a monster via effect, you are not supposed to lose the one free mode change that you get each turn.
        if (!isChangedByEffect) { // if not changed by effect, then can not mode change this turn any more
            isModeChangeableThisTurn=false; // if changed by effect, then this property stays untouched
        }
    }
    
    // makes the forbidden change between monsters and Att/Def mode (according to the cheat rule)
    public void cheatChangeMode (boolean isChangedByEffect) {
        // change between monsters and update properties accordingly
        if (isLowerMonster()) {
            Monster=Card.upMonster;
            att=Card.upMonster.att;
            def=Card.upMonster.def;
        }
        else {
            Monster=Card.lowMonster;
            att=Card.lowMonster.att;
            def=Card.lowMonster.def;
        }
        isUsingEffectsOfOtherMonster=false;
        otherEffectMonsterID=0;
        reEvaluateBoostDueToEquipping(); // reevaluate possible equip cards (because could be non-negated Incorruptible now)
        allowedChangeMode(isChangedByEffect); // rest as usual
    }
    
    // applies the passive effect of an Exhausted Executioner that just attacked
    public void passiveEffectExhaustedExecutioner() {
        YuGiOhJi.informationDialog(Monster.monsterName + " changes into defence mode, because of the effect of " + Mon.ExhaustedExecutioner.monsterName + ".", "Passive effect");
        changeMode(true);
    }
    
    // --- BOOKKEEPING part1: moving (monster) cards ---
    
    // kills a summoned monster (i.e. putting it to the graveyard)
    public void killMonster() {
        sendMonsterToGYOrDD(true);
    }
    
    // banishes a summoned monster (i.e. putting it to the different dimension)
    public void banishMonster() {
        sendMonsterToGYOrDD(false);
    }
    
    // erases a summoned monster without putting it to the graveyard (useful for moving it to an equip stack, when equipping)
    public void deleteMonster() {
        resetNthSummonedMonsterPropertiesWhenDeleting(sumMonsterNumber, isPlayersMonster);
        updateAttDefDisplay();
        YuGiOhJi.setMonsterButtonIcon("/images/YuGiOhJiFacedown.png", sumMonsterNumber, isPlayersMonster, isInAttackMode, false);
        // send all of its existing equip cards to GY
        EStack.getNthStack(sumMonsterNumber, isPlayersMonster).sendAllEquipCardsToGY();
    }
    
    // sends a card of a summoned monster to the graveyard or different dimension
    public void sendMonsterToGYOrDD (boolean toGY) {
        Deck.addCardToGYOrDD(Card, isOriginallyPlayersMonster, toGY); // copy card to GY or DD
        if (isOriginallyPlayersMonster) {// update appearance of GY resp. DD
            if (toGY) {YuGiOhJi.setDeckButtonIcon(Card.upMonster.cardPathAtt, 5, true);}
            else {YuGiOhJi.setDeckButtonIcon(Card.upMonster.cardPathAtt, 4, true);}
        }
        else { // cards in opponents graveyard and DD appear upside down
            if (toGY) {YuGiOhJi.setDeckButtonIcon(Card.lowMonster.cardPathAtt, 2, true);}
            else {YuGiOhJi.setDeckButtonIcon(Card.lowMonster.cardPathAtt, 3, true);}
        }
        deleteMonster();
    }
    
    // lets a summoned monster change its owner
    // keep all its other properties
    public static void stealMonster (SummonedMonster SumMonster) {
        // assuming the effect can be applied
        EStack OldStack = EStack.getNthStack(SumMonster.sumMonsterNumber, SumMonster.isPlayersMonster);
        // copy monster
        boolean isPlayerMonster = !SumMonster.isPlayersMonster; // change owner
        int newSumMonsterNumber = determineFreeMonsterZone(isPlayerMonster).sumMonsterNumber;
        SummonedMonster StolenMonster = getNthSummonedMonster(newSumMonsterNumber, isPlayerMonster);
        StolenMonster.rememberPropertiesOfMonster(SumMonster);
        StolenMonster.sumMonsterNumber = newSumMonsterNumber;
        StolenMonster.isPlayersMonster = isPlayerMonster; // that it originally belonged to the opposing player is already copied 
        StolenMonster.isKnown = true;
        // update look of new summoned monster
        if (StolenMonster.isFaceDown) {
            YuGiOhJi.setMonsterButtonIcon("/images/YuGiOhJiFacedown.png", newSumMonsterNumber, isPlayerMonster, StolenMonster.isInAttackMode, true);
        }
        else {
            if (StolenMonster.isInAttackMode) {
                if (isPlayerMonster) {
                    YuGiOhJi.setMonsterButtonIcon(StolenMonster.Monster.cardPathAtt, newSumMonsterNumber, isPlayerMonster, StolenMonster.isInAttackMode, true);
                }
                else { // monsters of the CPU appear upside down
                    YuGiOhJi.setMonsterButtonIcon(StolenMonster.Card.lowMonster.cardPathAtt, newSumMonsterNumber, isPlayerMonster, StolenMonster.isInAttackMode, true);
                }
            }
            else {
                if (isPlayerMonster) {
                    YuGiOhJi.setMonsterButtonIcon(StolenMonster.Monster.cardPathDef, newSumMonsterNumber, isPlayerMonster, StolenMonster.isInAttackMode, true);
                }
                else { // monsters of the CPU appear upside down
                    YuGiOhJi.setMonsterButtonIcon(StolenMonster.Card.lowMonster.cardPathDef, newSumMonsterNumber, isPlayerMonster, StolenMonster.isInAttackMode, true);
                }
            }
        }
        // copy corresponding equip stack
        if (OldStack.numberOfCards>0) {
            OldStack.completeDebuff(StolenMonster); // don't forget to take away all buffs first, since they will be added later on (in order to avoid having all buffs doubled)
            EStack NewStack = EStack.getNthStack(newSumMonsterNumber, isPlayerMonster);
            EStack.copyStackWhileStealingMonster(OldStack, NewStack); // uncludes all buffs and update look of new equip stack
            // delete original stack (and update look) before deleting monster (in order to avoid sending the cards to GY)       
            OldStack.erase();
            OldStack.updateLookOfEquipStack();
        }
        StolenMonster.reEvaluateBoostDueToEquipping(); // just in case it was Incorruptible or Steep Learning Curve, make sure their buffs are correct
        StolenMonster.updateAttDefDisplay(); // update shown stats of new summoned monster
        // delete original monster (and update look)
        SumMonster.deleteMonster();
    }
    
    // lets an equip monster change its owner and special summon it to the field
    public static void stealEquipMonster (EStack Stack, int cardNumberInStack, boolean isInAttackMode) {
        // assuming the effect can be applied
        boolean isOriginallyPlayersEquipCard = Stack.getOriginalOwnerOfNthEquipCard(cardNumberInStack);
        boolean isPlayerMonster = !Stack.getControllerOfNthEquipCard(cardNumberInStack); // change owner
        boolean isNegated = Stack.getNegationStatusOfNthEquipCard(cardNumberInStack);
        SummonedMonster StolenMonster = determineFreeMonsterZone(isPlayerMonster);
        YCard Card = Stack.getNthCardOfStack(cardNumberInStack);
        StolenMonster.updateMonsterPropertiesWhenSummoning(Card, false, false, isInAttackMode);
        StolenMonster.isNotAbleToUseItsEffects=isNegated; // new summoned monster keeps its negation status
        StolenMonster.isOriginallyPlayersMonster=isOriginallyPlayersEquipCard; // important to keep track of original owner, when card moves somewhere else
        Stack.deleteNthCardInEquipStackAndRearrange(cardNumberInStack); // erase original
        // update complete appearance
        StolenMonster.updateAttDefDisplay();
        if (isPlayerMonster) {
            if (StolenMonster.isInAttackMode) {
                YuGiOhJi.setMonsterButtonIcon(Card.lowMonster.cardPathAtt, StolenMonster.sumMonsterNumber, isPlayerMonster, true, true);
            }
            else {
                YuGiOhJi.setMonsterButtonIcon(Card.lowMonster.cardPathDef, StolenMonster.sumMonsterNumber, isPlayerMonster, false, true);
            }
        }
        else { // monsters of the CPU appear upside down
            if (StolenMonster.isInAttackMode) {
                YuGiOhJi.setMonsterButtonIcon(Card.upMonster.cardPathAtt, StolenMonster.sumMonsterNumber, isPlayerMonster, true, true);
            }
            else {
                YuGiOhJi.setMonsterButtonIcon(Card.upMonster.cardPathDef, StolenMonster.sumMonsterNumber, isPlayerMonster, false, true);
            }
        }
    }
    
    // --- BOOKKEEPING part2: updating properties of monsters ---
    
    // for displaying the attack and defence values of summonend monsters correctly
    // call when summoning or equipping
    public void updateAttDefDisplay() {
        int fontsize=100;
        if (Game.isSwitchingOnRescalingFontsize) {fontsize = (int) (100*YuGiOhJi.currentFrameHeight/YuGiOhJi.initialFrameHeight);}
        String text = "<html><body>";
        if (!isExisting || !isKnownToPlayer()) {
            text = text + "<span style=\"font-size:" + fontsize + "%\">" + YuGiOhJi.standardAttDefString;
        }
        else {
            int attack;
            int defence;
            attack = att;
            defence = def;
            boolean hasIncreasedAtt = (Monster.att!=attack);
            boolean hasIncreasedDef = (Monster.def!=defence);
            if (isInAttackMode) {
                if (hasIncreasedAtt) {text = text + "<span style=\"font-size:" + fontsize*1.2 + "%;color:blue\">" + attack + "</span>";}
                else {text = text + "<span style=\"font-size:" + fontsize*1.2 + "%\">" + attack + "</span>";}
                if (hasIncreasedDef) {text = text + " / " + "<span style=\"color:blue\">" + defence + "</span>";}
                else {text = text + " / " + defence;}
            }
            else {
                if (hasIncreasedAtt) {text = text + "<span style=\"color:blue\">" + attack + "</span>";}
                else {text = text + attack;}
                if (hasIncreasedDef) {text = text + " / " + "<span style=\"font-size:" + fontsize*1.2 + "%;color:blue\">" + defence + "</span>";}
                else {text = text + " / " + "<span style=\"font-size:" + fontsize*1.2 + "%\">" + defence + "</span>";}
            }
        }
        text = text + "</span></body></html>";
        setNthAttDefDisplay(sumMonsterNumber, isPlayersMonster, text);
    }
    
    public static void setNthAttDefDisplay (int n, boolean isPlayerMonster, String textToBeDisplayed) {
        if (isPlayerMonster) {
            switch (n) {
                case 1: YuGiOhJi.displayAttDefMonster1Player.setText(textToBeDisplayed); break;
                case 2: YuGiOhJi.displayAttDefMonster2Player.setText(textToBeDisplayed); break;
                case 3: YuGiOhJi.displayAttDefMonster3Player.setText(textToBeDisplayed); break;
                case 4: YuGiOhJi.displayAttDefMonster4Player.setText(textToBeDisplayed); break;
                case 5: YuGiOhJi.displayAttDefMonster5Player.setText(textToBeDisplayed); break;
                default: YuGiOhJi.debugDialog("Error: out of bounds in SummonedMonster.setNthAttDefDisplay(...); attempted monster: " + n); break;
            }
        }
        else {
            switch (n) {
                case 1: YuGiOhJi.displayAttDefMonster1CPU.setText(textToBeDisplayed); break;
                case 2: YuGiOhJi.displayAttDefMonster2CPU.setText(textToBeDisplayed); break;
                case 3: YuGiOhJi.displayAttDefMonster3CPU.setText(textToBeDisplayed); break;
                case 4: YuGiOhJi.displayAttDefMonster4CPU.setText(textToBeDisplayed); break;
                case 5: YuGiOhJi.displayAttDefMonster5CPU.setText(textToBeDisplayed); break;
                default: YuGiOhJi.debugDialog("Error: out of bounds in SummonedMonster.setNthAttDefDisplay(...); attempted monster: " + n); break;
            }
        }
    }
    
    // when equipping a monster, this method sets its properties correctly
    public void updateMonsterPropertiesWhenEquipping (YCard Card){
        if (!isImmune()) { // only do something, if it is not immune to equip cards
            if (Card.lowMonster.equals(Mon.Shield)) {def = def + Mon.Shield.ShieldDefBoost();}
            if (Card.lowMonster.equals(Mon.Sword)) {att = att + Mon.Sword.SwordAttBoost();}
            // if Lance, do nothing (piercing effect has to be checked that during battle phase)
            // if Demon, do nothing
            if (Card.lowMonster.equals(Mon.BuggedUpgrade)) {isNotAbleToUseItsEffects=true;}
            if (isKnownToPlayer()) {
                updateAttDefDisplay();
            }
        }
    }
    
    // needed only directly after a cheat change (or when stealing a monster)
    // makes sure, that boosts due to equip cards and Steep Learning Curve effect are still kept, when doing some bookkeeping stuff
    public void reEvaluateBoostDueToEquipping() {
        // reset values
        int baseAtt;
        int baseDef;
        if (isLowerMonster()) {
            baseAtt = Card.lowMonster.att;
            baseDef = Card.lowMonster.def;
        }
        else {
            baseAtt = Card.upMonster.att;
            baseDef = Card.upMonster.def;
        }
        att = baseAtt;
        def = baseDef;
        if (!isImmune()) { // Incorruptible (and those copying its effect, ignore their equip cards) [important asking for immune property - not equippability, because could just be any monster equipped wirh 10 cards!]
            EStack Stack = EStack.getNthStack(sumMonsterNumber, isPlayersMonster);
            // if changed back to Steep Learning Curve, it should keep its attack boost due to winning
            if (isGainingExperience()) {
                att = att + (numberOfDefeatedMonsters*Mon.SteepLearningCurve.SteepLearningCurveAttBoost());
            }
            // here usual evaluation
            if (Stack.numberOfCards>0) {
                for (int index = Stack.numberOfCards; index >= 1 ; index--){
                    if (!Stack.getNegationStatusOfNthEquipCard(index)) {
                        updateMonsterPropertiesWhenEquipping(Stack.getNthCardOfStack(index));
                    }
                }
            }
        }
    }
    
    // resets the properties of summonend monster at the beginning of each turn
    public static void resetMonsterPropertiesPerTurn() {
        for (int index = 1; index <= 5; index++){
            if (getNthSummonedMonster(index, true).isExisting) {getNthSummonedMonster(index, true).updateMonsterPropertiesEachTurn();}
            if (getNthSummonedMonster(index, false).isExisting) {getNthSummonedMonster(index, false).updateMonsterPropertiesEachTurn();}
        }
    }
    
    // when summoning monster this method sets its properties correctly
    public void updateMonsterPropertiesEachTurn() {
        isModeChangeableThisTurn=true;
        canStillAttackThisTurn=true;
        canStillUseOncePerTurnEffect=true;
        isUsingEffectsOfOtherMonster=false;
        otherEffectMonsterID=0;
        reEvaluateBoostDueToEquipping(); // in order to debuff monsters with copied abilities
    }
    
    // when summoning a monster, this method sets its properties correctly
    public void updateMonsterPropertiesWhenSummoning (YCard UsedCard, boolean isConcerningUpperMonster, boolean isSet, boolean isSummonedInAttackMode)
    {
        Card=UsedCard;
        isFaceDown=isSet;
        isInAttackMode=isSummonedInAttackMode;
        isModeChangeableThisTurn=false;
        canStillAttackThisTurn=true;
        canStillUseOncePerTurnEffect=true;
        isExisting=true;
        isNotAbleToUseItsEffects=false;
        isUsingEffectsOfOtherMonster=false;
        otherEffectMonsterID=0;
        numberOfDefeatedMonsters=0;
        isKnown=!isFaceDown;
        if (isConcerningUpperMonster) {
            Monster=UsedCard.upMonster;
            att=UsedCard.upMonster.att;
            def=UsedCard.upMonster.def;
        }
        else {                     
            Monster=UsedCard.lowMonster;
            att=UsedCard.lowMonster.att;
            def=UsedCard.lowMonster.def;
        }
        
    }
    
    // when deleting monster, this method sets its properties correctly
    public static void resetNthSummonedMonsterPropertiesWhenDeleting (int n, boolean isBelongingToPlayer) {
        SummonedMonster SumMonster = getNthSummonedMonster(n, isBelongingToPlayer);
        SumMonster.resetSummonedMonsterProperties();
    }
    
    // resets the values of a given summoned monster to that of a non-existing one
    public void resetSummonedMonsterProperties() {
        isExisting=false;
        // reset the rest as well, just to be sure
        att=0;
        def=0;
        isFaceDown=false;
        isInAttackMode=true;
        isModeChangeableThisTurn=false;
        canStillAttackThisTurn=false;
        canStillUseOncePerTurnEffect=false;
        isNotAbleToUseItsEffects=false;
        isUsingEffectsOfOtherMonster=false;
        otherEffectMonsterID=0;
        numberOfDefeatedMonsters=0;
        Monster=YuGiOhJi.NoMonster;
        Card=YuGiOhJi.NoCard;
        isOriginallyPlayersMonster = isPlayersMonster;
        isKnown=false;
    }
    
    // --- BOOKKEEPING part3: check useability of optional effects on field ---
    // (all about verifying, if one can use an effect on field)
    
    // returns the effective monster ID of a summoned monster (i.e. its own ID, when not copying the effects of another monster, respectively the ID of the copied monster)
    public int effectiveMonsterId() {
        if (isUsingEffectsOfOtherMonster) {
            return otherEffectMonsterID;
        }
        else {
            return Monster.monsterId;
        }
    }
    
    // returns how many monsters a given player has on the field
    public static int countOwnSummonedMonsters (boolean isBelongingToPlayer) {
        int numberOfMonsters=0;
        for (int index = 1; index <= 5; index++){
            if (getNthSummonedMonster(index, isBelongingToPlayer).isExisting) {numberOfMonsters++;}
        }
        return numberOfMonsters;
    }
    
    // returns how many face down monsters a given player has on the field (optionally only the unkown ones among them)
    public static int countOwnFaceDownMonsters (boolean isBelongingToPlayer, boolean isUnkown) {
        int numberOfMonsters=0;
        for (int index = 1; index <= 5; index++){
            SummonedMonster SumMonster = getNthSummonedMonster(index, isBelongingToPlayer);
            if (SumMonster.isExisting && SumMonster.isFaceDown) {
                if (!isUnkown || (isUnkown && !SumMonster.isKnown)) {
                    numberOfMonsters++;
                }
            }
        }
        return numberOfMonsters;
    }
    
    // returns the position of an unknown face down monster of a given player
    // returns zero, if there is no such monster
    public static int getUnknownFaceDownMonster (boolean isBelongingToPlayer) {
        int posOfMonster=0;
        for (int index = 1; index <= 5; index++){
            SummonedMonster SumMonster = getNthSummonedMonster(index, isBelongingToPlayer);
            if (SumMonster.isExistingButUnknown()) {
                posOfMonster = index;
                break;
            }
        }
        return posOfMonster;
    }
    
    // returns true, if one possesses a MOOK anywhere (hand, summoned, equip, GY)
    // i.e. the number of stars of either the upper or lower monster is exactly one
    public static boolean lookForMook (boolean isBelongingToPlayer) {
        if (EStack.countOwnEquipMonsters(isBelongingToPlayer, false)>0) {return true;}
        int numberOfHandCards = Hand.getHand(isBelongingToPlayer).numberOfCards;
        if (numberOfHandCards>0) {
            for (int index = numberOfHandCards; index >= 1; index--){
                YCard Card = Hand.getHand(isBelongingToPlayer).getNthCardOfHand(index);
                if (Card.isContainingMook()) {return true;}
            }
        }
        for (int index = 1; index <= 5; index++){
            SummonedMonster SumMonster = getNthSummonedMonster(index, isBelongingToPlayer);
            if (SumMonster.isExisting && SumMonster.Card.isContainingMook()) {return true;}
        }
        return (Deck.getGY(isBelongingToPlayer).hasMook());
    }
    
    // returns true, if a given player can use an effect of Necromancer
    public static boolean canUseAnEffectOfNecromancer (boolean isBelongingToPlayer) {
        return ( canReviveMook(isBelongingToPlayer) || canReviveMidboss(isBelongingToPlayer) );
    }
    
    // returns true, if a given player can use the first effect of Necromancer (having a monster zone free, a mook in GY and another GY card or an equip card)
    public static boolean canReviveMook (boolean isBelongingToPlayer) {
        return ( hasFreeMonsterZone(true) && GYDeckPlayer.hasMook() && (GYDeckPlayer.numberOfCards>=2 || EStack.countOwnEquipMonsters(true, false)>=1) );
    }
    
    // returns true, if a given player can use the second effect of Necromancer
    public static boolean canReviveMidboss (boolean isBelongingToPlayer) {
        return GYDeckPlayer.hasMidboss(); // only needs a Midboss in GY because Necromancer could alwys tribute itself
    }
    
    // returns true, if a given player controls cards that are not immune to effects
    public static boolean hasStealableCards (boolean isBelongingToPlayer) {
        return ( hasNonImmuneMonsters(isBelongingToPlayer) || EStack.countOwnEquipMonsters(isBelongingToPlayer, false)>=1 );
    }
    
    // returns true, if a given player controls monsters that are not immune to effects
    public static boolean hasNonImmuneMonsters (boolean isBelongingToPlayer) {
        for (int index = 1; index <= 5; index++){
            SummonedMonster SumMonster = getNthSummonedMonster(index, isBelongingToPlayer);
            if (SumMonster.isExisting && !SumMonster.isImmune())
            {
                return true;
            }
        }
        return false;
    }
    
    // returns true, if a given player can use the effect negate on the field
    public static boolean canUseEffectNegateOnField (boolean isConcerningPlayer) { // (there has to be a non-negated, non-immune monster or equip card on the field, even after potentially tributing itself)
        int payableSemipoints=CardOptions.countPayableCosts(isConcerningPlayer);
        boolean hasValidTarget=CardOptions.thereAreNegatableCards();
        boolean hasWorkingNeutraliser = AIEffects.hasWorkingMonsterEffect(true, Mon.Neutraliser.monsterId);
        return ( hasWorkingNeutraliser && payableSemipoints>=2 && hasValidTarget );
        // in rare case, where one can only tribute Neutraliser itself, there has to be yet another target on the field (so that there still will be a valid target, after paying the cost) [update: no, don't check this! There can always be cases in which there is no valid target any more after paying costs. Then simply nothing will happen. That is way easier to check.]
    }
    
    // returns true, if there are (equip) monster with abilities one can copy
    public static boolean stealableEffectsExistOnField() {
        if (stealableEffectsExistInMonsterCardZones()) {return true;}
        return EStack.lookForCopyableEffectOfEquipCards();
    }
    
    // returns true, if there are summoned monster with abilities one can copy
    public static boolean stealableEffectsExistInMonsterCardZones() {
        SummonedMonster SumMonster;
        for (int index = 1; index <= 5; index++){
            SumMonster=SummonedMonster.getNthSummonedMonster(index, true);
            if (SumMonster.hasStealableEffect()) {return true;}
            SumMonster=SummonedMonster.getNthSummonedMonster(index, false);
            if (SumMonster.hasStealableEffect()) {return true;}
        }
        return false;
    }
    
    // returns true, if a summoned monster is a face up monster with abilities one can copy (for definition look at YMonster.hasStealableEffect())
    public boolean hasStealableEffect() {
        return (isExisting && !isFaceDown && Monster.hasStealableEffect());
    }
    
    // returns true, if an existing copyable optional removal effect exists somewhere on the field
    public static boolean hasOptionalCopyableRemovalEffect() {
        for (int index = 1; index <= 5; index++){
            SummonedMonster SumMonster = SummonedMonster.getNthSummonedMonster(index, true);
            if (SumMonster.Monster.hasOptionalRemovalEffect() && SumMonster.hasStealableEffect()) {return true;}
            SumMonster = SummonedMonster.getNthStrongestMonster(index, false);
            if (SumMonster.Monster.hasOptionalRemovalEffect() && SumMonster.hasStealableEffect()) {return true;}
        }
        return false;
    }
    
    // returns true, if one can use the equip effect of a given summoned monster
    // (in case of Bugged Upgrade there has to be a negatable monster (an effect monster that has its effects not negated yet))
    public boolean canUseEquipEffectFromMonster() {
        if (!Monster.hasEquipEffect) {
            return false;
        }
        else {
            SummonedMonster SumMonster;
            for (int index = 1; index <= 5; index++){ // check own summoned monsters
                SumMonster = SummonedMonster.getNthSummonedMonster(index, isPlayersMonster); // (No monster can equip itself. Equip monster is not same as summoned monster: that is the condition added to the usual equippability)
                if ( SumMonster.canBeEquippedBy(Monster) && !isNotAbleToUseItsEffects && !isBasicallySameMonster(SumMonster) ) {
                    return true;
                } // check summoned monsters of opponent
                SumMonster = SummonedMonster.getNthSummonedMonster(index, !isPlayersMonster);
                if ( SumMonster.canBeEquippedBy(Monster) && !isNotAbleToUseItsEffects ) {
                    return true;
                }
            }
        }
        return false;
    }
    
    // in order not to repeat oneself and to assure that always the same conditions are checked, out-source here what it means to be equippable
    public boolean isEquippable() {
        return (isExisting && !isImmune() && EStack.getNthStack(sumMonsterNumber, isPlayersMonster).numberOfCards!=10);
    }
    
    // in order not to repeat oneself and to assure that always the same conditions are checked,
    // out-source here, if a summoned to be equippable by a given equip monster
    public boolean canBeEquippedBy (YMonster EquipMonster) {
        return ( isEquippable() && !(EquipMonster.equals(Mon.BuggedUpgrade) && !canBeNegated()) );
    }
    
    // returns true, if a given monster can use at least one of the two on field effects of demon
    public boolean canUseOnFieldEffectsOfDemon() {
        if (canUseEquipEffectFromMonster()) { // check equip effect first (because way more likely)
            return true;
        }
        else { // still check cheat change effect
            return canUseCheatChangeEffectOfDemon();
        }
    }
    
    // return true, if a summoned monster which is effectively Demon, can use its cheat mode change effect
    public boolean canUseCheatChangeEffectOfDemon() {
        if (!hasCheatChangeEffect()) {
            return false;
        }
        else {
            boolean isBelongingToPlayer = isPlayersMonster;
            for (int index = 1; index <= 5; index++){
                SummonedMonster PotentialSacrifice=SummonedMonster.getNthSummonedMonster(index, isBelongingToPlayer);
                if (!isBasicallySameMonster(PotentialSacrifice) && PotentialSacrifice.Monster.monsterId==35) { // only real Demons (no copies count as possible sacrifices for this effect)
                    return true;
                }
            }
            return false;
        }
    }
    
    // returns true, if a summoned monster for whatever reason has the cheat change effect
    public boolean hasCheatChangeEffect() {
        return (isExisting && canStillUseOncePerTurnEffect && YMonster.getMonsterById(effectiveMonsterId()).isCheatChanging);
    }
    
    // --- BOOKKEEPING part4: check useability of passive effects on field ---
    // (all about verifying, if a monster uses a passive effect (in battle))
    
    // returns true, if a summoned monster for whatever reason can not be destroyed by battle
    public boolean isIndestructibleByBattle() {
        return (isExisting && !isNotAbleToUseItsEffects && YMonster.getMonsterById(effectiveMonsterId()).isIndestructibleByBattle);
    }
    
    // returns true, if a given player has a monster indestructible by battle (but reveal info about player monster only, if known to computer)
    public static boolean hasMonsterIndestructibleByBattle (boolean isConcerningPlayer) {
        for (int index = 1; index <= 5; index++){
            SummonedMonster SumMonster = SummonedMonster.getNthSummonedMonster(index, isConcerningPlayer);
            if (SumMonster.isIndestructibleByBattle() && (!isConcerningPlayer || (isConcerningPlayer && SumMonster.isExistingAndKnown()))) {
                return true;
            }
        }
        return false;
    }
    
    // returns only true, if the opponent of a given player knows exactly that there is no invincible monster awaiting (important for damage simulation)
    public static boolean hasNoInvincibleMonsterKnownToOpponentForSure (boolean isConcerningPlayer) {
        for (int index = 1; index <= 5; index++){
            SummonedMonster SumMonster = SummonedMonster.getNthSummonedMonster(index, isConcerningPlayer);
            if ((SumMonster.isIndestructibleByBattle() && SumMonster.isExistingAndKnown()) || SumMonster.isExistingButUnknown()) {
                return false;
            }
        }
        return true;
    }
    
    // returns the monster indestructible by battle of a given player that has the lowest relevant value (att if in attack mode, def if in def mode)
    // (with the option to only consider monsters in attack mode)
    // (but reveal info about player monster only, if known to computer)
    // returns a monster with position zero, if there is no such monster
    public static SummonedMonster getWeakestMonsterIndestructibleByBattle (boolean isConcerningPlayer, boolean isInAttackMode) {
        int currentlyWeakestValue=0;
        int position=0;
        SummonedMonster SumMonster;
        for (int index = 1; index <= 5; index++){
            SumMonster = SummonedMonster.getNthSummonedMonster(index, isConcerningPlayer);
            if (SumMonster.isIndestructibleByBattle() && (!isConcerningPlayer || (isConcerningPlayer && SumMonster.isExistingAndKnown())) && (!isInAttackMode || (isInAttackMode && SumMonster.isInAttackMode)) ) {
                if (position==0) {
                    position=index;
                    currentlyWeakestValue=SumMonster.relevantValue();
                }
                else {
                    if (SumMonster.relevantValue() < currentlyWeakestValue) {
                        currentlyWeakestValue=SumMonster.relevantValue();
                        position=index;
                    }
                }
            }
        }
        if (position==0) {
            SumMonster = new SummonedMonster(isConcerningPlayer, 0);
        }
        else {
            SumMonster = SummonedMonster.getNthSummonedMonster(position, isConcerningPlayer);
        }
        return SumMonster;
    }
    
    // returns true, if a summoned monster for whatever reason can be destroyed by effect (neither immune nor Diamond Sword nor copied their effect)
    public boolean canBeDestroyedByEffect() {
        return ( isExisting && !isIndestructibleByEffect() );
    }
    
    // returns true, if a given player has a summoned monster that can be targeted by Eradicator
    public static boolean hasMonstersDestructibleByEffect (boolean isConcerningPlayer) {
        for (int index = 1; index <= 5; index++){
            SummonedMonster SumMonster = getNthSummonedMonster(index, isConcerningPlayer);
            if (SumMonster.canBeDestroyedByEffect() || SumMonster.isExistingButUnknown()) // If the unknown monster is Dimaond Sword, it can not be destroyed. However, all face down cards can be targeted.
            {
                return true;
            }
        }
        return false;
    }
    
    // returns true, if a given player has a possible target for an Eradicator of the opponent
    public static boolean hasCardsDestructibleByEffect (boolean isBelongingToPlayer) {
        return ( hasMonstersDestructibleByEffect(isBelongingToPlayer) || EStack.countOwnEquipMonsters(isBelongingToPlayer, false)>=1 );
    }
    
    // returns true, if a summoned monster for whatever reason can not be destroyed by effect
    public boolean isIndestructibleByEffect() {
        return ( isExisting && !isNotAbleToUseItsEffects && (YMonster.getMonsterById(effectiveMonsterId()).isIndestructibleByEffect || isImmune()) );
    }
    
    // returns true, if a unknown (face down) Diamond Sword has to be revealed, in order to prove, that it is no valid target for Eradicator
    public boolean hasToRevealDiamondSword() {
        return ( !canBeDestroyedByEffect() && isExistingButUnknown() );
    }
    
    // returns true, if a summoned monster for whatever reason is immune to effects while it defeats its opponent (also includes simply being immune in general)
    public boolean isImmuneWhileWinning() {
        return ( isExisting && !isNotAbleToUseItsEffects && (YMonster.getMonsterById(effectiveMonsterId()).isImmuneWhileWinning || isImmune()) );
    }
    
    // returns true, if a summoned monster for whatever reason is immune to effects
    public boolean isImmune() {
        return (isExisting && !isNotAbleToUseItsEffects && !isFaceDown && YMonster.getMonsterById(effectiveMonsterId()).isImmuneToAllEffects);
    }
    
    // returns true, if a given player can attack safely with a summoned monster
    public static boolean canAttackSafely (boolean isConcerningPlayer) {
        for (int index = 1; index <= 5; index++){
            SummonedMonster SumMonster = getNthSummonedMonster(index, isConcerningPlayer);
            if (SumMonster.isImmuneWhileWinning()) {
                return true;
            }
        }
        return false;
    }
    
    // returns position of a summoned monster of a given player that can still attack safely this battle phase
    // returns zero, if there is no such monster
    public static int getMonsterThatCanStillAttackSafely (boolean isConcerningPlayer) {
        int posOfMonster=0;
        for (int index = 1; index <= 5; index++){
            SummonedMonster SumMonster = getNthSummonedMonster(index, isConcerningPlayer);
            if (SumMonster.isImmuneWhileWinning() && SumMonster.canStillAttackThisBattlePhase()) {
                posOfMonster = index;
                break;
            }
        }
        return posOfMonster;
    }
    
    // returns true, if a summoned monster for whatever reason possesses the piercing damage ability (passive effect)
    public boolean hasPiercingDamageAbility() {
        boolean isPiercingItselfOrCopying = (isExisting && !isNotAbleToUseItsEffects && YMonster.getMonsterById(effectiveMonsterId()).isPiercing);
        boolean isEquippedWithNonNegatedLance = EStack.getNthStack(sumMonsterNumber, isPlayersMonster).lookForLanceInStack(true);
        return ( !isImmune() && (isPiercingItselfOrCopying || isEquippedWithNonNegatedLance) ); // testing for immunity, because could be Incorruptible ignoring being equipped with Lance
    }
    
    // returns true, if a summoned monster for whatever reason has the effect of Steep Learning Curve
    public boolean isGainingExperience() {
        return (isExisting && !isNotAbleToUseItsEffects && YMonster.getMonsterById(effectiveMonsterId()).isGettingStronger);
    }
    
    // returns true, if a summoned monster for whatever reason has the effect of Exhausted Executioner
    public boolean isTurningIntoDef() {
        return (isExisting && !isNotAbleToUseItsEffects && YMonster.getMonsterById(effectiveMonsterId()).isGettingIntoDef);
    }
    
    // returns true, if a summoned monster for whatever reason has the effect of Exhausted Executioner
    public boolean isReturningItselfToHand() {
        return (isExisting && !isFaceDown && !isNotAbleToUseItsEffects && YMonster.getMonsterById(effectiveMonsterId()).isGettingItselfToHand);
    }
    
    // if has an useable sucidal effect, returns the monster ID of the monster of that effect
    // returns zero, otherwise
    public int relevantEffectiveSuicidalMonsterId() {
        if (!hasUseableSuicideEffect()) {
            return 0;
        }
        else {
            return effectiveMonsterId();
        }
    }
    
    // returns true, if a monster has non-negated suicide effect or copying it (i.e. effects of Napalm, Suicide Commando, Back Bouncer, Banisher)
    public boolean hasUseableSuicideEffect() {
        return ( isExisting && !isNotAbleToUseItsEffects && YMonster.getMonsterById(effectiveMonsterId()).hasSuicidalEffects );
    }
    
    // returns true, if a monster has non-negated suicide effect or copying it except the one of Napalm
    public boolean hasUseableSuicideGetRidOfEffect() {
        return ( isExisting && !isNotAbleToUseItsEffects && YMonster.getMonsterById(effectiveMonsterId()).hasPassiveRemovalEffect() );
    }
    
    // returns true, if a summoned monster for whatever reason can banish itself, when defeated opponent (ability of Banisher)
    // it also banishes the opponent, if it is not immune to the ability
    public boolean isBanishingBothMonsters (SummonedMonster OpposingMonster) {
        boolean isBanishing = (isExisting && !isNotAbleToUseItsEffects && YMonster.getMonsterById(effectiveMonsterId()).isBanishingWhenDestroyed);
        boolean opposingMonsterNotAffected = OpposingMonster.isImmune();
        return (isBanishing && !opposingMonsterNotAffected);
    }
    
    // returns true, if a summoned monster for whatever reason can banish its defeated opponent
    public boolean isBanishingOpposingMonster (SummonedMonster OpposingMonster) {
        boolean isBanishing = (isExisting && !isNotAbleToUseItsEffects && YMonster.getMonsterById(effectiveMonsterId()).isBanishingWhenDestroying);
        boolean opposingMonsterNotAffected = OpposingMonster.isImmune();
        return (isBanishing && !opposingMonsterNotAffected);
    }
    
    // --- BOOKKEEPING part5: check useability of hand traps ---
    
    // returns true, if the effects of a given monster can be negated
    // i.e. having effects at all that are not the one of Incorruptible that are not already negated
    public boolean canBeNegated() {
        return (isExisting && !isNotAbleToUseItsEffects && !isImmune());
    }
    
    // important for negation during battle phase
    public boolean hasPassiveEffectsAndCanUseThem() {
        return (Monster.hasPassEffects && !isNotAbleToUseItsEffects);
    }
    
}
