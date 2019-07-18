package yugiohji;

/**
 * Everything about the player interrupting the computer during the computer's turn.
 * This class contains some methods for hand trap interactions
 * and interruptive on field attack/effect negations
 * of the player during the opponent's turn
 * as well as effect negation in last moment and preventively.
 * 
 * The CPU has corresponding methods for attack declaration
 * and attack/effect negations in the AIinterrupts class file.
 * 
 * Programmer's note:
 * Although this is not the biggest file in the game,
 * nor has it the most lines of code (only a few hunderd lines),
 * this (together with the AIinterrupts file)
 * is by far the trickiest and conceptually most complicated part of the game!
 * Thus it is also the most error-prone concept of the game.
 * If you code a game, better don't allow actions during the opponent's turn.
 * 
 */

import static yugiohji.SummonedMonster.getNthSummonedMonster;
import static yugiohji.YuGiOhJi.BigAttackStopperSword;
import static yugiohji.YuGiOhJi.Game;
import static yugiohji.YuGiOhJi.HandPlayer;
import static yugiohji.YuGiOhJi.NeutraliserSkillStealer;

public class PlayerInterrupts {
    
    // --- everything about effect negation from here on --- (attack negation in the lower half of this file)
    
    // If at the end of main phase 1 and main phase 2 the player can negate effects, gets asked to do so preventively. 
    public static void askPlayerToNegateEffectsAtEndOfPhase(){
        boolean canUseOnFieldNegate = SummonedMonster.canUseEffectNegateOnField(true);
        boolean canUseHandTrapNegate = CardOptions.thereAreNegatableCards() && Hand.lookForEffectNegateOnHand(true);
        if (canUseOnFieldNegate || canUseHandTrapNegate) {
            if (canUseOnFieldNegate && !canUseHandTrapNegate) {
                int intDialogResult = YuGiOhJi.multipleChoiceDialog("Do you want to negate an effect on the field using the effect of Neutraliser on the field?", "CPU wants to end phase and you can negate effects.", new String[]{"yes", "no"}, "no");
                if (intDialogResult==0) {
                    playerUsesOnFieldEffectOfNeutaliser();
                }
                else {AIdelegate.cpuProceedsToNextPhase();}
            }
            else if (!canUseOnFieldNegate && canUseHandTrapNegate) {
                int intDialogResult = YuGiOhJi.multipleChoiceDialog("Do you want to negate an effect on the field by discarding Neutraliser?", "CPU wants to end phase and you can negate effects.", new String[]{"yes", "no"}, "no");
                if (intDialogResult==0) {YMonster.handTrapEffectNegateActivate();}
                else {AIdelegate.cpuProceedsToNextPhase();}
            }
            else if (canUseOnFieldNegate && canUseHandTrapNegate) {
                int intDialogResult = YuGiOhJi.multipleChoiceDialog("Do you want to negate an effect by either discarding Neutraliser or using its effect on the field?", "CPU wants to end phase and you can negate effects.", new String[]{"yes", "no"}, "no");
                if (intDialogResult==0) {
                    intDialogResult = YuGiOhJi.multipleChoiceDialog("How do you want to negate?", "Choose an option.", new String[]{"by using the effect on the field", "by discarding Neutraliser (default)"}, "by discarding Neutraliser (default)");
                    if (intDialogResult==0) {
                        playerUsesOnFieldEffectOfNeutaliser();
                    }
                    else {YMonster.handTrapEffectNegateActivate();}
                }
                else {AIdelegate.cpuProceedsToNextPhase();}
            }
        }
        else {AIdelegate.cpuProceedsToNextPhase();}
    }
    
    // asks the player to decide on whether to discard Neutraliser in order to negate the effects of one of the two monsters involved in battle or to use the on field effect of Neutraliser
    // returns true, if player negates
    public static boolean playerMayNegateEffect (SummonedMonster AttackingMonster, SummonedMonster GuardingMonster, boolean isDiscardingNeutraliser) {
        // don't bother asking to negate already negated monsters, or those which have no effect in the first place
        boolean canNegateAttackingMonster = (AttackingMonster.hasPassiveEffectsAndCanUseThem() && AttackingMonster.canBeNegated());
        boolean canNegateGuardingMonster = (GuardingMonster.hasPassiveEffectsAndCanUseThem() && GuardingMonster.canBeNegated());
        int intDialogResult;
        if (canNegateAttackingMonster && !canNegateGuardingMonster) {
            intDialogResult = YuGiOhJi.multipleChoiceDialog("Do you want to negate the effects of the attacking monster " + Game.ActiveAttackingMonster.Monster.monsterName + "?", "You can negate the effects on the field.", new String[]{"yes", "no"}, "no");
            if (intDialogResult==0) {
                if (isDiscardingNeutraliser) {playerDiscardsNeutraliser(AttackingMonster); return true;}
                else {playerUsesOnFieldEffectOfNeutaliser(AttackingMonster); return true;}
            }
            else {return false;}
        }
        else if (!canNegateAttackingMonster && canNegateGuardingMonster) {
            intDialogResult = YuGiOhJi.multipleChoiceDialog("Do you want to negate the effects of the attacked monster " + Game.ActiveGuardingMonster.Monster.monsterName + "?", "You can negate the effects on the field.", new String[]{"yes", "no"}, "no");
            if (intDialogResult==0) {
                if (isDiscardingNeutraliser) {playerDiscardsNeutraliser(GuardingMonster); return true;}
                else {playerUsesOnFieldEffectOfNeutaliser(GuardingMonster); return true;}
            }
            else {return false;}
        }
        else if (canNegateAttackingMonster && canNegateGuardingMonster) { // thoeretically, one should be allowed to negate the effects of both monsters in one battle at once, but I hope that nobody wants to do that (the only meaningful target of one's own monsters would be Exhausted Executioner anyway)
            intDialogResult = YuGiOhJi.multipleChoiceDialog("Do you want to negate the effects of one of the monsters involved in battle?", "You can negate the effects on the field.", new String[]{"yes", "no"}, "no");
            if (intDialogResult==0) {
                intDialogResult = YuGiOhJi.multipleChoiceDialog("The effects of which monsters shall be negated?", "Choose monster", new String[]{"attacking monster", "attacked monster"}, "attacked monster");
                if (intDialogResult==0) {
                    if (isDiscardingNeutraliser) {playerDiscardsNeutraliser(AttackingMonster); return true;}
                    else {playerUsesOnFieldEffectOfNeutaliser(AttackingMonster); return true;}
                }
                else {
                    if (isDiscardingNeutraliser) {playerDiscardsNeutraliser(GuardingMonster); return true;}
                    else {playerUsesOnFieldEffectOfNeutaliser(GuardingMonster); return true;}
                }
            }
            else {return false;}
        }
        else {return false;}
    }
    // in order not to mix up methods, similar methods for the player negating (direct) attacks can be found far below in this file
    //
    // same as above method, but with only one effect monster that just wanted to use an effect
    public static boolean playerMayNegateEffect (SummonedMonster EffectMonster, boolean isDiscardingNeutraliser) {
        // don't bother asking to negate already negated monsters
        boolean canNegateEffectMonster = EffectMonster.canBeNegated();
        int intDialogResult;
        if (canNegateEffectMonster) {
            intDialogResult = YuGiOhJi.multipleChoiceDialog("Do you want to negate the activated effect of the monster " + EffectMonster.Monster.monsterName + "?", "You can negate the effects on the field.", new String[]{"yes", "no"}, "no");
            if (intDialogResult==0) {
                if (isDiscardingNeutraliser) {playerDiscardsNeutraliser(EffectMonster); return true;}
                else {playerUsesOnFieldEffectOfNeutaliser(EffectMonster); return true;}
            }
            else {return false;}
        }
        else {return false;}
    }
    public static boolean playerMayNegateEffect (EStack Stack, int cardNumberInStack, boolean isDiscardingNeutraliser, boolean isNegatingPreventively) { // like above for equip cards
        SummonedMonster SumMonster = getNthSummonedMonster(Stack.stackNumber, Stack.isBelongingToPlayer);
        String pronoun;
        if (Stack.isBelongingToPlayer) {pronoun = "your ";} else {pronoun = "its ";}
        int intDialogResult = YuGiOhJi.multipleChoiceDialog("Do you want to negate the effects of the equip monster  " + Stack.getNthCardOfStack(cardNumberInStack).lowMonster.monsterName + "?", "Computer equipped " + pronoun + AIEffects.getNumberAsString(SumMonster.sumMonsterNumber) + " monster, " + SumMonster.Monster.monsterName + ".", new String[]{"yes", "no"}, "no");
        if (intDialogResult==0) {
            if (isDiscardingNeutraliser) {playerDiscardsNeutraliser(Stack, cardNumberInStack, isNegatingPreventively); return true;}
            else {playerUsesOnFieldEffectOfNeutaliser(Stack, cardNumberInStack, isNegatingPreventively); return true;}
        }
        else {return false;}
    }
    
    // called, after the CPU just activated an effect and paid the costs for it
    // here the player is asked, if or how one wants to negate the just activated effect
    // (The monsters having an immunity to negation, have no optional effects they could actvate. That's why it's safe not to check for immunity here.)
    public static boolean askPlayerToNegateActivatedEffect (SummonedMonster EffectMonster) {
        boolean isCanceling = false;
        boolean hasEffectNegateHandTrap = Hand.lookForEffectNegateOnHand(true);
        boolean hasEffectNegateOnField = Hand.lookForEffectNegateOnField(true);
        if (hasEffectNegateHandTrap || hasEffectNegateOnField) {
            if (hasEffectNegateHandTrap && !hasEffectNegateOnField) {
                isCanceling = playerMayNegateEffect(EffectMonster, true);
            }
            else if (!hasEffectNegateHandTrap && hasEffectNegateOnField) {
                isCanceling = playerMayNegateEffect(EffectMonster, false);
            }
            else {
                int intDialogResult = YuGiOhJi.multipleChoiceDialog("Do you want to negate the activated effect?", "You can negate the effects on the field.", new String[]{"yes, by discarding Neutraliser", "yes, by paying the cost worth 1 card", "no (default)"}, "no (default)");
                if (intDialogResult==0) {
                    isCanceling = playerMayNegateEffect(EffectMonster, true);
                }
                else if (intDialogResult==1) {
                    isCanceling = playerMayNegateEffect(EffectMonster, false);
                }
            }
        }
        return isCanceling;
    }
    // like above for equip cards (asks the player to negate a just used equip card of CPU)
    // Here it can happen though, that one wants to negate an equip card preventively after it has equipped a monster.
    public static boolean askPlayerToNegateActivatedEffect (EStack Stack, int cardNumberInStack, boolean isNegatingPreventively) {
        boolean isCanceling = false;
        boolean hasEffectNegateHandTrap = Hand.lookForEffectNegateOnHand(true);
        boolean hasEffectNegateOnField = Hand.lookForEffectNegateOnField(true);
        if (hasEffectNegateHandTrap || hasEffectNegateOnField) {
            if (hasEffectNegateHandTrap && !hasEffectNegateOnField) {
                isCanceling = playerMayNegateEffect(Stack, cardNumberInStack, true, isNegatingPreventively);
            }
            else if (!hasEffectNegateHandTrap && hasEffectNegateOnField) {
                isCanceling = playerMayNegateEffect(Stack, cardNumberInStack, false, isNegatingPreventively);
            }
            else {
                int intDialogResult = YuGiOhJi.multipleChoiceDialog("Do you want to negate the effects of the equip card?", "You can negate the effects on the field.", new String[]{"yes, by discarding Neutraliser", "yes, by paying the cost worth 1 card", "no (default)"}, "no (default)");
                if (intDialogResult==0) {
                    isCanceling = playerMayNegateEffect(Stack, cardNumberInStack, true, isNegatingPreventively);
                }
                else if (intDialogResult==1) {
                    isCanceling = playerMayNegateEffect(Stack, cardNumberInStack, false, isNegatingPreventively);
                }
            }
        }
        return isCanceling;
    }
    
    // The player discards the card Neutraliser - Skill Stealer in order to negate the effects of a summoned monster
    // in battle or right after it activated an effect. Either way the game simply continues after this effect.
    // The corresponding method in the file YMonster is not called in order to keep the flow of the program more smooth. (This way no method has to be interrupted and then started again.)
    public static void playerDiscardsNeutraliser (SummonedMonster SumMonster) {
        YuGiOhJi.informationDialog("discarding " + NeutraliserSkillStealer.cardName + ", negating effects of " + SumMonster.Monster.monsterName, "");
        Hand.discardCard(HandPlayer.getPositionOfCardWithCardId(NeutraliserSkillStealer.cardId), true);
        getNthSummonedMonster(SumMonster.sumMonsterNumber, SumMonster.isPlayersMonster).isNotAbleToUseItsEffects=true;
        if (Game.ActiveAttackingMonster.isExisting) { // if negated monster effects at beginning of attack
            BattlePhase.makeDamageCalculation(Game.ActiveAttackingMonster, Game.ActiveGuardingMonster); // continue attack
        }
    }
    public static void playerDiscardsNeutraliser (EStack Stack, int cardNumberInStack, boolean isNegatingPreventively) { // same used on equip card
        YCard Card = Stack.getNthCardOfStack(cardNumberInStack);
        YuGiOhJi.informationDialog("discarding " + NeutraliserSkillStealer.cardName + ", negating effects of equip monster" + Card.lowMonster.monsterName, "");
        Hand.discardCard(HandPlayer.getPositionOfCardWithCardId(NeutraliserSkillStealer.cardId), true);
        YMonster.handTrapEffectNegateExecuteOnEquipCard(Stack, cardNumberInStack, isNegatingPreventively); // here the program simply goes on after negating
    }
    
    // here the player gets asked right after an effect has been activated or during the battle phase (either way this is a reactive negation)
    // (if one tributes the monster involved in battle, the attack has been stopped, but with the attacking monster is still keeping its one attack per turn)
    public static void playerUsesOnFieldEffectOfNeutaliser (SummonedMonster SumMonster) {
        YMonster.onFieldEffectNeutraliserDuringOpponentsTurnOrBattleActivate(SumMonster);
    }
    // if player decided to use the on-field effect of Neutraliser at the end of a main phase of the opponent, here you have to decide which one you use the effect on,
    // because this is always a preventive negation and thus the opponent has to know which monster you use in order to negate it, if opponent decides to do so
    public static void playerUsesOnFieldEffectOfNeutaliser() {
        boolean[] isValidOption = new boolean[5];
        for (int index = 1; index <= 5; index++){
            isValidOption[index-1] = Hand.checkEffectNegateOfNthMonster(index, true);
        }
        int posOfChosenMonster = showChooseNegatingMonsterDialog(isValidOption);
        SummonedMonster NegatingMonster = getNthSummonedMonster(posOfChosenMonster, true);
        if (Game.isSwitchingOnOncePerTurnRule) {
            NegatingMonster.canStillUseOncePerTurnEffect=false;
        }
        YMonster.effectNeutraliserOptionalActivate(getNthSummonedMonster(posOfChosenMonster, true));
    }
    public static void playerUsesOnFieldEffectOfNeutaliser (EStack Stack, int cardNumberInStack, boolean isNegatingPreventively) {
        if (Game.isSwitchingOnOncePerTurnRule || isNegatingPreventively) { // make the player choose which monster uses the negate effect, in case the once-per-turn-rule is active, because then it makes a difference (also needed, when preventively negating, since CPU might negate negation)
            boolean[] isValidOption = new boolean[5];
            for (int index = 1; index <= 5; index++){
                isValidOption[index-1] = Hand.checkEffectNegateOfNthMonster(index, true);
            }
            int posOfChosenMonster = showChooseNegatingMonsterDialog(isValidOption);
            SummonedMonster NegatingMonster = getNthSummonedMonster(posOfChosenMonster, true);
            if (isNegatingPreventively) { // CPU needs to know which monster it may or may not negate, if you negate an equip card preventively
                Game.ActEffMonSource = NegatingMonster;
            }
            if (Game.isSwitchingOnOncePerTurnRule) {
                NegatingMonster.canStillUseOncePerTurnEffect=false;
            }
        }
        // negate here
        YMonster.onFieldEffectNeutraliserDuringOpponentsTurnOnEquipCardActivate(Stack, cardNumberInStack, isNegatingPreventively);
    }
    
    // -- in between, some dialogs being used for both: attack and effect negation --
    
    // shows a dialog to choose a monster with an effect negate on the field, in case there is more than one and the once-per-turn-rule is active 
    public static int showChooseNegatingMonsterDialog (boolean[] isValidOption) {
        int noOfOptions = countNumberOfTruthsInBooleanArray(isValidOption);
        int posOfChosenMonster;
        if (noOfOptions==1) {posOfChosenMonster=1;}
        else {
            String DialogTitle = "Which Monster?";
            String DialogContent = "Which monster shall use the negate effect?";
            String[] ButtonTexts = new String[5];
            for (int index = 1; index <= 5; index++){
                ButtonTexts[index-1] = AIEffects.getNumberAsString(index) + " monster (" + getNthSummonedMonster(index, true).Monster.monsterName + ")";
            }
            posOfChosenMonster = constructMultipleChoiceDialogFromArrays(isValidOption, ButtonTexts, true, DialogContent, DialogTitle);
            posOfChosenMonster++; // Arrays are alwys off by one.
        }
        return posOfChosenMonster;
    }
    
    // --- everything about attack negation from here on ---
    
    // if player decided to use the on-field effect of Attack Stopper or Big Attack Stopper, here you have to decide which one you use the effect on, if once-per-turn-rule is active,
    // but otherwise just goes on and calls a method that makes the negation
    // (if one tributes the monster involved in battle, the attack has been stopped, but with the attacking monster is still keeping its one attack per turn)
    public static void playerUsesOnFieldEffectOfSmallOrBigAttackStopper (SummonedMonster SumMonster, boolean isUsingBigAttackStopper) {
        if (Game.isSwitchingOnOncePerTurnRule) { // make the player choose which monster uses the negate effect, in case the once-per-turn-rule is active, because then it makes a difference 
            boolean[] isValidOption = new boolean[5];
            for (int index = 1; index <= 5; index++){
                if (isUsingBigAttackStopper) { // here looking for the Big Attack Stopper
                    isValidOption[index-1] = Hand.checkIfNthMonsterIsUsableBigAttackStopper(index, true);
                }
                else { // otherwise just the orginary Attack Stopper
                    isValidOption[index-1] = Hand.checkIfNthMonsterIsUsableAttackStopper(index, true);
                }
            }
            int posOfChosenMonster = showChooseNegatingMonsterDialog(isValidOption);
            SummonedMonster NegatingMonster = getNthSummonedMonster(posOfChosenMonster, true);
            NegatingMonster.canStillUseOncePerTurnEffect = false;
        }
        // negate here
        if (isUsingBigAttackStopper) {YMonster.onFieldEffectBigAttackStopperActivate(SumMonster);}
        else {YMonster.onFieldEffectAttackStopperActivate(SumMonster);}
    }
    
    // the player discards the card Big Attack Stopper - Sword in order to negate an attack
    public static void playerDiscardsBigAttackStopper() {
        Game.playerCurrentlyNegates=true;
        //YMonster.revealHandCard(BigAttackStopperSword.cardId, true); // player using an on hand effect (hand traps and searches) should reveal it first (for dramatic effect and stuff)
        YuGiOhJi.informationDialog("discarding " + BigAttackStopperSword.cardName + " and ending battle phase", "");
        Hand.discardCard(HandPlayer.getPositionOfCardWithCardId(BigAttackStopperSword.cardId), true);
        YMonster.updateAttackNegateStatistic(); // just for statistics/achievements
        BattlePhase.cancelAttackAndEndBattlePhase(true);
    }
    
    // called, when the player uses the "My Body As A Shield"-rule
    public static void playerUsesBodyAsShield (SummonedMonster AttackingMonster) {
        YuGiOhJi.informationDialog("You invoke the \"My Body As A Shield\" rule.", "");
        boolean isContinuingGame = BattlePhase.isDealingBattleDamageAndContinuingGame(false, AttackingMonster.att, true);
        if (isContinuingGame) { // The My Body As A Shield rule is not really an attack negation. It just redirects the attack into a direct attack. That mean the Exhausted Executioner still turns into defence mode after that.
            if (AttackingMonster.isTurningIntoDef()) {
                AttackingMonster.passiveEffectExhaustedExecutioner();
            }
            BattlePhase.endAttack(false); // swich off booleans remembering what monster are currently in battle
        }
    }
    
    // player negates an attack on a monster (and may has to decide how)
    public static void playerNegatesAttack (SummonedMonster AttackingMonster, boolean isDirectAttack) {
        boolean canNegateByBigAttackStopperOnHand = Hand.lookForAttackNegateOnHand(true);
        boolean canNegateByAttackStopper = Hand.lookForUsableAttackStopper(true);
        boolean canNegateByBigAttackStopperOnField = Hand.lookForUsableBigAttackStopper(true);
        boolean canNegateByMyBodyAsAShieldRule = (!isDirectAttack && Game.isSwitchingOnBodyAsAShieldRule && AttackingMonster.att < Game.lifePointsPlayer);
        // The attacks of monsters being immune to all effects (i.e. being effectively Incorruptible) can not be negated.
        // However, one can still "negate" the attack by using the own body as a shield and redirect the attack.
        if (AttackingMonster.isImmune()) { // take away all negation options except for My Body As A Shield
            canNegateByBigAttackStopperOnHand = false;
            canNegateByAttackStopper = false;
            canNegateByBigAttackStopperOnField = false;
        }
        boolean[] isValidOption = new boolean[4];
        isValidOption[0] = canNegateByBigAttackStopperOnHand;
        isValidOption[1] = canNegateByAttackStopper;
        isValidOption[2] = canNegateByBigAttackStopperOnField;
        isValidOption[3] = canNegateByMyBodyAsAShieldRule;
        int noOfOptions = countNumberOfTruthsInBooleanArray(isValidOption);
        String DialogContent = "How do you want to stop the attack?";
        String[] ButtonTexts = new String[4];
        ButtonTexts[0] = "by discarding Big Attack Stopper";
        ButtonTexts[1] = "by effect of Attack Stopper";
        ButtonTexts[2] = "by effect of Big Attack Stopper on field";
        ButtonTexts[3] = "by \"My Body As A Shield\" rule";
        // below here rewrite using arrays
        if (noOfOptions==0) {
            // if no option to negate, nothing happens
        }
        if (noOfOptions==1) {
            if (isValidOption[0]) {
                playerDiscardsBigAttackStopper();
            }
            else if (isValidOption[1]) {
                playerUsesOnFieldEffectOfSmallOrBigAttackStopper(AttackingMonster, false);
            }
            else if (isValidOption[2]) {
                playerUsesOnFieldEffectOfSmallOrBigAttackStopper(AttackingMonster, true);
            }
            else if (isValidOption[3]) {
                playerUsesBodyAsShield(AttackingMonster);
            }
        }
        else {
            int intDialogResult = constructMultipleChoiceDialogFromArrays(isValidOption, ButtonTexts, true, DialogContent);
            playerUsesNthAttackNegation(intDialogResult, AttackingMonster);
        }
    }
    
    // this method is supposed to be directly applied after getting the result from the multiple choice dialog, of how one wants to negate,
    // turning the corresponding number of the negate option into action
    public static void playerUsesNthAttackNegation (int n, SummonedMonster AttackingMonster) {
        switch (n) {
            case 0: playerDiscardsBigAttackStopper(); break;
            case 1: playerUsesOnFieldEffectOfSmallOrBigAttackStopper(AttackingMonster, false); break;
            case 2: playerUsesOnFieldEffectOfSmallOrBigAttackStopper(AttackingMonster, true); break;
            case 3: playerUsesBodyAsShield(AttackingMonster); break;
            default: YuGiOhJi.debugDialog("Error: out of bounds in playerUsesNthAttackNegation(...); attempted N: " + n);
        } 
    }
    
    // returns how many elements in a boolean array are "true"
     public static int countNumberOfTruthsInBooleanArray (boolean[] isValidOption) {
        int noOfValidOptions = 0;
        for (int index = 0; index <= isValidOption.length-1; index++){ // arrays always go from zero to length-1
            if (isValidOption[index]) {
                noOfValidOptions++;
            }
        }
        return noOfValidOptions;
     }
    
    // This method takes an array of strings (containing the texts of buttons)
    // and an equally long array of booleans telling which option has to be considered,
    // creates a dialog without the invalid buttons and returns the transformed answer of what would have been the original dialog with all options
    // such that one can simply take the result and plug it into a method that executes the (N+1)th of all original options.
    // So basically it shows a multiple choice dialog, but leaves out unwanted options.
    public static int constructMultipleChoiceDialogFromArrays (boolean[] isValidOption, String[] ButtonTexts, boolean isModal) {
        return constructMultipleChoiceDialogFromArrays(isValidOption, ButtonTexts, isModal, "", "");
    }
    public static int constructMultipleChoiceDialogFromArrays (boolean[] isValidOption, String[] ButtonTexts, boolean isModal, String DialogContent) {
        return constructMultipleChoiceDialogFromArrays(isValidOption, ButtonTexts, isModal, DialogContent, "");
    }
    public static int constructMultipleChoiceDialogFromArrays (boolean[] isValidOption, String[] ButtonTexts, boolean isModal, String DialogContent, String DialogTitle) {
        // If the boolean array is too long, simply ignore the rest.
        // If the boolean array is too short, assume that the corresponding options are invalid and thus omitted.
        // Also if the dialog is considered modal, always consider the 1st option as default, such that, if oone simpy closed the dialog, the first option is automatically chosen.
        int noOfOptions = countNumberOfTruthsInBooleanArray(isValidOption);
        String[] NewButtonTexts = new String[noOfOptions]; // per construction NewButtonTexts.length <= ButtonTexts.length
        Integer[] CorrespondingOriginalOptionNumber = new Integer[noOfOptions];
        noOfOptions = 0;
        for (int index = 0; index <= ButtonTexts.length-1; index++){
            if (isValidOption[index]) {
                NewButtonTexts[noOfOptions] = ButtonTexts[index];
                CorrespondingOriginalOptionNumber[noOfOptions] = index;
                noOfOptions++; // Here the increase has to happen AFTER the reading of array, because of the arrays always being off by one thing.
            }
        }
        int intDialogResult;
        String DefaultText = "";
        if (isModal) {
            DefaultText = NewButtonTexts[0]; // 1st element always standard for modal dialogs
        }
        intDialogResult = YuGiOhJi.multipleChoiceDialog(DialogContent, DialogTitle, NewButtonTexts, DefaultText);
        if (intDialogResult==-1 && isModal) {
            intDialogResult = 0; // answers of modal dialogs always assume first possible option
        }
        if (intDialogResult==-1) {
            return -1;
        }
        else {
            return CorrespondingOriginalOptionNumber[intDialogResult];
        }
    }
    
    
}
