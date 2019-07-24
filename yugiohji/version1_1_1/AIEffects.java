package yugiohji;

/**
 * This class contains all the YuGiOhJi-card-effects (including summoning) of the computer.
 * The computer does not open card choosing windows when deciding to use an effect.
 * It simply does the effect and asks the player to interrupt using a hand trap or on-field negate in the last moment.
 * That is why it is useful to rewrite the effects of all monsters again.
 * Again, like in the corresponding class YMonster, the methods assume the CPU can use them.
 * Thus it has to be checked, if the CPU can actually use them, before calling them.
 * Methods that check, if the CPU can use the effect can be found right below each CPU-effect-method.
 * Some methods from class file YMonster can be reused.
 * 
 * There are many methods in here that are not use at all in any strategy of the computer.
 * However, they are all potentially needed,
 * if one decided to give the computer more versatile strategies in later versions.
 * That's why they should stay here, although it's one of the biggest files in the game.
 * 
 */

import static yugiohji.AIsacrifice.worthInSemipointsOfAllCurrentSacrifices;
import static yugiohji.YuGiOhJi.Card;
import static yugiohji.YuGiOhJi.DeckCPU;
import static yugiohji.YuGiOhJi.Game;
import static yugiohji.YuGiOhJi.GYDeckCPU;
import static yugiohji.YuGiOhJi.HandCPU;
import static yugiohji.YuGiOhJi.Mon;
import static yugiohji.YMonster.getMonsterById;
import static yugiohji.PlayerInterrupts.askPlayerToNegateActivatedEffect;

public class AIEffects {
    
    // -- BOOKKEEPING: methods needed for most effects --
    
    // returns strings like "1st", "2nd", "3rd" etc. when entering the corrsponding number (useful for dialogs)
    // This method is by far most often used in this class. That's why it's here.
    public static String getNumberAsString (int number) {
        switch (number) {
            case 1: return "1st";
            case 2: return "2nd";
            case 3: return "3rd";
            case 4: return "4th";
            case 5: return "5th";
            case 6: return "6th";
            case 7: return "7th";
            case 8: return "8th";
            case 9: return "9th";
            case 10: return "10th";
            default: YuGiOhJi.debugDialog("Error: out of bounds in getNumberAsString(...); attempted number: " + number); return "0th";
        }
    }
    
    // pays the cost for an effect (i.e. discarding cards, tributing monsters, sending equip card to GY, banishing cards from GY)
    // one can sacrifice 4 cards at maximum for each effect
    // if one wants to sacrifice less, make some of the entries invalid sacrifices (they get ignored)
    // or enter simply fewer arguments (see the other versions of this method below)
    public static void cpuPaysCost (AIsacrifice Sacrifice1, AIsacrifice Sacrifice2, AIsacrifice Sacrifice3, AIsacrifice Sacrifice4) {
        // If one sacrifice is a summoned monster and some of the other ones are equip cards of the same monster, then sacrifice the equip cards first. (in order to avoid loss of sacrifices)
        if (Sacrifice1.monsterNumber!=0 || Sacrifice2.monsterNumber!=0 || Sacrifice3.monsterNumber!=0 || Sacrifice4.monsterNumber!=0) {
            if (Sacrifice1.cardIdInStack!=0 || Sacrifice2.cardIdInStack!=0 || Sacrifice3.cardIdInStack!=0 || Sacrifice4.cardIdInStack!=0) {
                int monsterNumber1=Sacrifice1.monsterNumber; int stackNumber1=0; boolean isMovingSacrifice1=false;
                int monsterNumber2=Sacrifice2.monsterNumber; int stackNumber2=0; boolean isMovingSacrifice2=false;
                int monsterNumber3=Sacrifice3.monsterNumber; int stackNumber3=0; boolean isMovingSacrifice3=false;
                int monsterNumber4=Sacrifice4.monsterNumber; int stackNumber4=0; boolean isMovingSacrifice4=false;
                if (Sacrifice1.cardIdInStack!=0 && !Sacrifice1.Stack.isBelongingToPlayer) {stackNumber1=Sacrifice1.Stack.stackNumber;}
                if (Sacrifice2.cardIdInStack!=0 && !Sacrifice2.Stack.isBelongingToPlayer) {stackNumber2=Sacrifice2.Stack.stackNumber;}
                if (Sacrifice3.cardIdInStack!=0 && !Sacrifice3.Stack.isBelongingToPlayer) {stackNumber3=Sacrifice3.Stack.stackNumber;}
                if (Sacrifice4.cardIdInStack!=0 && !Sacrifice4.Stack.isBelongingToPlayer) {stackNumber4=Sacrifice4.Stack.stackNumber;}
                if (stackNumber1!=0 || stackNumber2!=0 || stackNumber3!=0 || stackNumber4!=0) {
                    // The idea is the following: If a sacrifice is the monster of the same equip stack, sacrifice it last.
                    // There are at maximum 2 of the sacrifices that can move to the end. (in case of 2 monsters and 2 equip cards equipping them)
                    // More likely is it that there is only one monster (with 1 to 3 equip cards equipping it).
                    // Also in the cases, in which the already last ones would move to the end, the same as usual happens.
                    if (monsterNumber1==stackNumber1 || monsterNumber1==stackNumber2 || monsterNumber1==stackNumber3 || monsterNumber1==stackNumber4) {
                        isMovingSacrifice1=true; // Here sacrifice1 has to move to one of the last spots.
                    }
                    if (monsterNumber2==stackNumber1 || monsterNumber2==stackNumber2 || monsterNumber2==stackNumber3 || monsterNumber2==stackNumber4) {
                        isMovingSacrifice2=true; // Here sacrifice2 has to move to one of the last spots.
                    }
                    if (monsterNumber3==stackNumber1 || monsterNumber3==stackNumber2 || monsterNumber3==stackNumber3 || monsterNumber3==stackNumber4) {
                        isMovingSacrifice3=true; // Here sacrifice3 has to move to one of the last spots.
                    }
                    if (monsterNumber4==stackNumber1 || monsterNumber4==stackNumber2 || monsterNumber4==stackNumber3 || monsterNumber4==stackNumber4) {
                        isMovingSacrifice4=true; // Here sacrifice4 has to move to one of the last spots.
                    }
                    // standard case (no sacrifice moves) [check this one first, because it is by far the most likely one]
                    if (!isMovingSacrifice1 && !isMovingSacrifice2 && !isMovingSacrifice3 && !isMovingSacrifice4) {cpuPaysCostInOrder(Sacrifice1, Sacrifice2, Sacrifice3, Sacrifice4);}
                    // 4 cases in which only one sacrifice moves to the end (minus usual case)
                    else if (isMovingSacrifice1 && !isMovingSacrifice2 && !isMovingSacrifice3 && !isMovingSacrifice4) {cpuPaysCostInOrder(Sacrifice2, Sacrifice3, Sacrifice4, Sacrifice1);}
                    else if (!isMovingSacrifice1 && isMovingSacrifice2 && !isMovingSacrifice3 && !isMovingSacrifice4) {cpuPaysCostInOrder(Sacrifice1, Sacrifice3, Sacrifice4, Sacrifice2);}
                    else if (!isMovingSacrifice1 && !isMovingSacrifice2 && isMovingSacrifice3 && !isMovingSacrifice4) {cpuPaysCostInOrder(Sacrifice1, Sacrifice2, Sacrifice4, Sacrifice3);}
                    // 6 cases in which 2 sacrifices move (minus usual case)
                    else if (isMovingSacrifice1 && isMovingSacrifice2 && !isMovingSacrifice3 && !isMovingSacrifice4) {cpuPaysCostInOrder(Sacrifice3, Sacrifice4, Sacrifice1, Sacrifice2);}
                    else if (isMovingSacrifice1 && !isMovingSacrifice2 && isMovingSacrifice3 && !isMovingSacrifice4) {cpuPaysCostInOrder(Sacrifice2, Sacrifice4, Sacrifice1, Sacrifice3);}
                    else if (isMovingSacrifice1 && !isMovingSacrifice2 && !isMovingSacrifice3 && isMovingSacrifice4) {cpuPaysCostInOrder(Sacrifice2, Sacrifice3, Sacrifice1, Sacrifice4);}
                    else if (!isMovingSacrifice1 && isMovingSacrifice2 && isMovingSacrifice3 && !isMovingSacrifice4) {cpuPaysCostInOrder(Sacrifice1, Sacrifice4, Sacrifice2, Sacrifice3);}
                    else if (!isMovingSacrifice1 && isMovingSacrifice2 && !isMovingSacrifice3 && isMovingSacrifice4) {cpuPaysCostInOrder(Sacrifice1, Sacrifice3, Sacrifice2, Sacrifice4);}
                    else {cpuPaysCostInOrder(Sacrifice1, Sacrifice2, Sacrifice3, Sacrifice4);} // usual case
                }
                else {
                    cpuPaysCostInOrder(Sacrifice1, Sacrifice2, Sacrifice3, Sacrifice4); // usual case
                }
            }
            else {
                cpuPaysCostInOrder(Sacrifice1, Sacrifice2, Sacrifice3, Sacrifice4); // usual case
            }
        }
        else {
            cpuPaysCostInOrder(Sacrifice1, Sacrifice2, Sacrifice3, Sacrifice4); // usual case
        }
    }
    public static void cpuPaysCostInOrder (AIsacrifice Sacrifice1, AIsacrifice Sacrifice2, AIsacrifice Sacrifice3, AIsacrifice Sacrifice4) {
        Sacrifice1.sacrifice();
        Sacrifice2.sacrifice();
        Sacrifice3.sacrifice();
        Sacrifice4.sacrifice();
    }
    public static void cpuPaysCost (AIsacrifice Sacrifice1, AIsacrifice Sacrifice2, AIsacrifice Sacrifice3) {
        cpuPaysCost(Sacrifice1, Sacrifice2, Sacrifice3, new AIsacrifice());
    }
    public static void cpuPaysCost (AIsacrifice Sacrifice1, AIsacrifice Sacrifice2) {
        cpuPaysCost(Sacrifice1, Sacrifice2, new AIsacrifice(), new AIsacrifice());
    }
    public static void cpuPaysCost (AIsacrifice Sacrifice) {
        cpuPaysCost(Sacrifice, new AIsacrifice(), new AIsacrifice(), new AIsacrifice());
    }
    
    // in order not to repeat oneself, out-source here the conditions for a given monster/player hving a working monster effect
    public static boolean hasWorkingMonsterEffect (boolean isBelongingToPlayer, int monsterId) {
        for (int index = 1; index <= 5; index++){
            SummonedMonster SumMonster = SummonedMonster.getNthSummonedMonster(index, isBelongingToPlayer);
            if (hasWorkingMonsterEffect(SumMonster, monsterId)){return true;}
        }
        return false;
    }
    public static boolean hasWorkingMonsterEffect (SummonedMonster SumMonster, int monsterId) {
        boolean fulfillsUsualConditions = ( SumMonster.isExisting && !SumMonster.isFaceDown && !SumMonster.isNotAbleToUseItsEffects && (SumMonster.Monster.monsterId==monsterId || (SumMonster.isUsingEffectsOfOtherMonster && SumMonster.otherEffectMonsterID==monsterId)) && (!Game.isSwitchingOnOncePerTurnRule || (Game.isSwitchingOnOncePerTurnRule && SumMonster.canStillUseOncePerTurnEffect)) );
        if (getMonsterById(monsterId).equals(Mon.Obstacle) || getMonsterById(monsterId).equals(Mon.God) || getMonsterById(monsterId).equals(Mon.Burner) || getMonsterById(monsterId).equals(Mon.MonsterStealer)) {
            return (fulfillsUsualConditions && SumMonster.canStillAttackThisTurn);
        }// In case of the interrupting monsters (Neutraliser, (Big) Attack Stopper), one can not copy their effects, but one can still check the usual conditions. (That another monster copied their effect will simply never happen, but this method still works.)
        return fulfillsUsualConditions;
    }
    
    // returns the position of a monster with the working effect of the monster with a given monster ID
    // returns zero, if there is no such monster
    public static int getPosOfCPUMonsterWithWorkingEffectByMonsterId (int monsterId) {
        for (int index = 1; index <= 5 ; index++){
            if (hasWorkingMonsterEffect(SummonedMonster.getNthSummonedMonster(index, false), monsterId)) {
                return index;
            }
        }
        return 0;
    }
    
    // -- EFFECTS of CPU --
    
    // -- EFFECTS on the FIELD of CPU --
    
    // Effect ID = 1
    public static void cpuEffectShowACardLookAtCard (SummonedMonster EffectMonster, SummonedMonster UnkownFaceDownMonster, int cardNumberOnHandToShow) { // when using this method somewhere: for simplicity let the CPU always show 1st card on hand
        EffectMonster.canStillUseOncePerTurnEffect=false; // This is an "always-once-per-turn-effect" in the game.
        YMonster.revealNthHandCard(cardNumberOnHandToShow, false); // instead of paying costs, just has to reveal a card oneself
        cpuEffectLookAtCard(EffectMonster, UnkownFaceDownMonster);
    }
    public static void cpuEffectShowACardLookAtCard (SummonedMonster EffectMonster, SummonedMonster UnkownFaceDownMonster, SummonedMonster FaceDownMonsterToShow) {
        EffectMonster.canStillUseOncePerTurnEffect=false; // This is an "always-once-per-turn-effect" in the game.
        YMonster.revealFaceDownMonster(FaceDownMonsterToShow.sumMonsterNumber, FaceDownMonsterToShow.isPlayersMonster); // this should always be a monster of the CPU (instead of paying costs, just has to reveal a card oneself)
        cpuEffectLookAtCard(EffectMonster, UnkownFaceDownMonster);
    }
    public static void cpuEffectLookAtCard (SummonedMonster EffectMonster, SummonedMonster UnkownFaceDownMonster) {
        Game.isActCPUEff = true;
        YuGiOhJi.informationDialog("Computer wants to use its " + getNumberAsString(EffectMonster.sumMonsterNumber) + " monster, " + EffectMonster.Monster.monsterName + ", to look at your " + getNumberAsString(UnkownFaceDownMonster.sumMonsterNumber) + " monster, " + UnkownFaceDownMonster.Monster.monsterName + ".", "Effect attempt");
        boolean isCanceling = askPlayerToNegateActivatedEffect(EffectMonster);
        if (!isCanceling) {
            YuGiOhJi.informationDialog("Computer uses its " + getNumberAsString(EffectMonster.sumMonsterNumber) + " monster, " + EffectMonster.Monster.monsterName + ", to look at your " + getNumberAsString(UnkownFaceDownMonster.sumMonsterNumber) + " monster, " + UnkownFaceDownMonster.Monster.monsterName + ".", "Looking at face down monster");
            YMonster.revealFaceDownMonster(UnkownFaceDownMonster.sumMonsterNumber, true);
        }
        Game.deactivateCurrentCPUEffects();
    }
    // used to test, if the CPU can use the optional effect Show A Card Look At Card
    public static boolean cpuCanUseEffectShowACardLookAtCard() {
        boolean hasWorkingShowACardLookAtCardEffect = ( hasWorkingMonsterEffect(false, Mon.God.monsterId) || hasWorkingMonsterEffect(false, Mon.Obstacle.monsterId) );
        int numberOfCardsToReveal = HandCPU.numberOfCards+SummonedMonster.countOwnFaceDownMonsters(false, false);
        int numberOfValidTargets = SummonedMonster.countOwnFaceDownMonsters(true, true);
        return ( hasWorkingShowACardLookAtCardEffect && numberOfCardsToReveal>=1 && numberOfValidTargets>=1 );
    }
    public static boolean cpuCanUseEffectShowACardLookAtCard (SummonedMonster EffectMonster) {
        boolean hasWorkingShowACardLookAtCardEffect = ( hasWorkingMonsterEffect(EffectMonster, Mon.God.monsterId) || hasWorkingMonsterEffect(EffectMonster, Mon.Obstacle.monsterId) );
        int numberOfCardsToReveal = HandCPU.numberOfCards+SummonedMonster.countOwnFaceDownMonsters(false, false);
        int numberOfValidTargets = SummonedMonster.countOwnFaceDownMonsters(true, true);
        return ( hasWorkingShowACardLookAtCardEffect && numberOfCardsToReveal>=1 && numberOfValidTargets>=1 );
    }
    public static boolean cpuCanUseEffectShowACardLookAtCardOnlyByRevealingHandCard (SummonedMonster EffectMonster) {
        boolean hasWorkingShowACardLookAtCardEffect = ( hasWorkingMonsterEffect(EffectMonster, Mon.God.monsterId) || hasWorkingMonsterEffect(EffectMonster, Mon.Obstacle.monsterId) );
        int numberOfCardsToReveal = HandCPU.numberOfCards;
        int numberOfValidTargets = SummonedMonster.countOwnFaceDownMonsters(true, true);
        return ( hasWorkingShowACardLookAtCardEffect && numberOfCardsToReveal>=1 && numberOfValidTargets>=1 );
    }
    public static boolean cpuCanUseEffectShowACardLookAtCard (SummonedMonster EffectMonster, SummonedMonster UnkownFaceDownMonster) {
        boolean hasWorkingShowACardLookAtCardEffect = ( hasWorkingMonsterEffect(EffectMonster, Mon.God.monsterId) || hasWorkingMonsterEffect(EffectMonster, Mon.Obstacle.monsterId) );
        int numberOfCardsToReveal = HandCPU.numberOfCards+SummonedMonster.countOwnFaceDownMonsters(false, false);
        boolean isValidTarget = (UnkownFaceDownMonster.isPlayersMonster && UnkownFaceDownMonster.isExistingButUnknown());
        return ( hasWorkingShowACardLookAtCardEffect && numberOfCardsToReveal>=1 && isValidTarget );
    }
    
    
    // Effect ID = 2
    public static void cpuEffectBurner (SummonedMonster EffectMonster, AIsacrifice Sacrifice) {
        if (Sacrifice.isValidSacrificeAndContainsMook()) {
            Game.isActCPUEff = true;
            // Here the sacrifice is indeed the target. That's why one doesn't have to check things here.
            EffectMonster.canStillUseOncePerTurnEffect=false;
            YCard CardToBeBurned = Sacrifice.extractCardFromValidSacrifice();
            int potentialBurnDamage = CardToBeBurned.extractMookBurnDamage();
            cpuPaysCost(Sacrifice);
            YuGiOhJi.informationDialog("Computer has sacrificed its card " + CardToBeBurned.cardName + " and wants to use its " + getNumberAsString(EffectMonster.sumMonsterNumber) + " monster, " + EffectMonster.Monster.monsterName + ", to inflict " + potentialBurnDamage + " burn damage on you.", "Effect attempt");
            boolean isCanceling = askPlayerToNegateActivatedEffect(EffectMonster);
            if (!isCanceling) {
                YuGiOhJi.informationDialog("Computer has sacrificed its card " + CardToBeBurned.cardName + " and uses its " + getNumberAsString(EffectMonster.sumMonsterNumber) + " monster, " + EffectMonster.Monster.monsterName + ", to inflict " + potentialBurnDamage + " burn damage on you.", "Dealing burn damage");
                YMonster.dealBurnDamage(potentialBurnDamage, true);
            }
            Game.deactivateCurrentCPUEffects();
        }
    }
    // used to test, if the CPU can use the optional effect of Burner
    public static boolean cpuCanUseEffectBurner() {
        return ( hasWorkingMonsterEffect(false, Mon.Burner.monsterId) && SummonedMonster.lookForMook(false) ); // just needs the working effect and an own MOOK somewhere
    }
    public static boolean cpuCanUseEffectBurner (SummonedMonster EffectMonster) {
        return ( hasWorkingMonsterEffect(EffectMonster, Mon.Burner.monsterId) && SummonedMonster.lookForMook(false) );
    }
    public static boolean cpuCanUseEffectBurner (SummonedMonster EffectMonster, AIsacrifice Sacrifice) {
        return ( hasWorkingMonsterEffect(EffectMonster, Mon.Burner.monsterId) && Sacrifice.isValidSacrificeAndContainsMook() );
    }
    
    
    // Effect ID = 3
    public static void cpuEffectBigBurner (SummonedMonster EffectMonster, AIsacrifice Sacrifice) {
        // Here too the sacrifice is indeed the target. That's why one doesn't have to check that here.
        if (Sacrifice.sacrificeIsValidMonster()) {
            SummonedMonster SumMonsterToBeBurned = Sacrifice.extractSummonedMonsterFromValidSacrifice();
            if (!SumMonsterToBeBurned.isBasicallySameMonster(EffectMonster)) {
                // ... However, only after arriving here (i.e. the tribute is valid) the real effect starts.
                Game.isActCPUEff = true;
                if (Game.isSwitchingOnOncePerTurnRule) {
                    EffectMonster.canStillUseOncePerTurnEffect=false;
                }
                int potentialBurnDamage = SumMonsterToBeBurned.att;
                cpuPaysCost(Sacrifice);
                YuGiOhJi.informationDialog("Computer has sacrificed its monster " + SumMonsterToBeBurned.Monster.monsterName + " and wants to use its " + getNumberAsString(EffectMonster.sumMonsterNumber) + " monster, " + EffectMonster.Monster.monsterName + ", to inflict " + potentialBurnDamage + " burn damage on you.", "Effect attempt");
                boolean isCanceling = askPlayerToNegateActivatedEffect(EffectMonster);
                if (!isCanceling) {
                    YuGiOhJi.informationDialog("Computer has sacrificed its monster " + SumMonsterToBeBurned.Monster.monsterName + " and wants to use its " + getNumberAsString(EffectMonster.sumMonsterNumber) + " monster, " + EffectMonster.Monster.monsterName + ", to inflict " + potentialBurnDamage + " burn damage on you.", "Dealing burn damage");
                    YMonster.dealBurnDamage(potentialBurnDamage, true);
                }
                Game.deactivateCurrentCPUEffects();
            }
        }
    }
    // used to test, if the CPU can use the optional effect of Big Burner
    public static boolean cpuCanUseEffectBigBurner() {
        return (hasWorkingMonsterEffect(false, Mon.BigBurner.monsterId) && SummonedMonster.countOwnSummonedMonsters(false)>=2);
    }
    public static boolean cpuCanUseEffectBigBurner (SummonedMonster EffectMonster) {
        return (hasWorkingMonsterEffect(EffectMonster, Mon.BigBurner.monsterId) && SummonedMonster.countOwnSummonedMonsters(false)>=2);
    }
    public static boolean cpuCanUseEffectBigBurner (SummonedMonster EffectMonster, AIsacrifice Sacrifice) {
        return ( hasWorkingMonsterEffect(EffectMonster, Mon.BigBurner.monsterId) && SummonedMonster.countOwnSummonedMonsters(false)>=2 && Sacrifice.sacrificeIsValidMonster() );
    }
    
    
    // Effect ID = 4 (1st effect of Card Grabber: the search effect)
    public static void cpuEffectCardGrabberSearch (SummonedMonster EffectMonster, int cardIdInDeck, AIsacrifice Sacrifice1, AIsacrifice Sacrifice2) {
        cpuEffectCardGrabberSearch(EffectMonster, cardIdInDeck, Sacrifice1, Sacrifice2, new AIsacrifice(), new AIsacrifice());
    }
    //  same as before, but with 3 sacrifices
    public static void cpuEffectCardGrabberSearch (SummonedMonster EffectMonster, int cardIdInDeck, AIsacrifice Sacrifice1, AIsacrifice Sacrifice2, AIsacrifice Sacrifice3) {
        cpuEffectCardGrabberSearch(EffectMonster, cardIdInDeck, Sacrifice1, Sacrifice2, Sacrifice3, new AIsacrifice());
    }
    //  same as before, but with 4 sacrifices
    public static void cpuEffectCardGrabberSearch (SummonedMonster EffectMonster, int cardIdInDeck, AIsacrifice Sacrifice1, AIsacrifice Sacrifice2, AIsacrifice Sacrifice3, AIsacrifice Sacrifice4) {
        if (worthInSemipointsOfAllCurrentSacrifices()==4) {
            Game.isActCPUEff = true;
            // Since one can not sacrifice cards from the deck, here one doesn't have to check things.
            if (Game.isSwitchingOnOncePerTurnRule) {
                EffectMonster.canStillUseOncePerTurnEffect=false;
            }
            cpuPaysCost(Sacrifice1, Sacrifice2, Sacrifice3, Sacrifice4);
            YCard Card = DeckCPU.getCardInDeckByCardID(cardIdInDeck);
            YuGiOhJi.informationDialog("Computer wants to use its " + getNumberAsString(EffectMonster.sumMonsterNumber) + " monster, " + EffectMonster.Monster.monsterName + ", to get itself the card " + Card.cardName + " from the deck to the hand.", "Effect attempt");
            boolean isCanceling = askPlayerToNegateActivatedEffect(EffectMonster);
            if (!isCanceling) {
                YuGiOhJi.informationDialog("Computer uses its " + getNumberAsString(EffectMonster.sumMonsterNumber) + " monster, " + EffectMonster.Monster.monsterName + ", to get itself the card " + Card.cardName + " from the deck to the hand.", "Searching");
                int cardNumberInDeck = DeckCPU.getPositionOfCardWithCardIdInDeck(cardIdInDeck);
                if (HandCPU.numberOfCards==10) { // in case CPU has full hand, has to discard a card first
                    YCard.discardCardOfFullHand(false);
                }
                YMonster.effectCardGrabber1Execute(cardNumberInDeck, false);
            }
            Game.deactivateCurrentCPUEffects();
        }
    }
    // used to test, if the CPU can use the optional effect of Card Grabber (only considering 1st effect: the search effect)
    public static boolean cpuCanUseEffectCardGrabberSearch() { // conditions same as for cheaper effect, but with higher payable cost (and hand restriction for simplicity)
        int payableSemipoints=CardOptions.countPayableCosts(false);
        boolean cpuHasNotFullHand = HandCPU.numberOfCards<10; // for simplicity, forbid CPU to use effect, if it has full hand already
        return ( cpuCanUseEffectCardGrabberBurial() && payableSemipoints>=4 && cpuHasNotFullHand );
    }
    public static boolean cpuCanUseEffectCardGrabberSearch (SummonedMonster EffectMonster) {
        int payableSemipoints=CardOptions.countPayableCosts(false);
        boolean cpuHasNotFullHand = HandCPU.numberOfCards<10; // for simplicity, forbid CPU to use effect, if it has full hand already
        return ( cpuCanUseEffectCardGrabberBurial(EffectMonster) && payableSemipoints>=4 && cpuHasNotFullHand );
    }
    public static boolean cpuCanUseEffectCardGrabberSearch (SummonedMonster EffectMonster, int cardIdInDeck) {
        int payableSemipoints=CardOptions.countPayableCosts(false);
        boolean cpuHasNotFullHand = HandCPU.numberOfCards<10; // for simplicity, forbid CPU to use effect, if it has full hand already
        return ( cpuCanUseEffectCardGrabberBurial(EffectMonster, cardIdInDeck) && payableSemipoints>=4 && cpuHasNotFullHand );
    }
    public static boolean cpuCanUseEffectCardGrabberSearch (int cardIdInDeck) {
        boolean hasWorkingMonsterEffect = hasWorkingMonsterEffect(false, Mon.CardGrabber.monsterId);
        int payableSemipoints=CardOptions.countPayableCosts(false);
        boolean cpuHasNotFullHand = HandCPU.numberOfCards<10; // for simplicity, forbid CPU to use effect, if it has full hand already
        return ( hasWorkingMonsterEffect && payableSemipoints>=4 && cpuHasNotFullHand );
    }
    
    // Effect ID = 5 (2nd effect of Card Grabber: the "Foolish Burial" effect)
    // reuse YMonster.effectCardGrabber2Execute(int cardNumberInDeck, boolean isBelongingToPlayer)
    public static void cpuEffectCardGrabberBurial (SummonedMonster EffectMonster, int cardIdInDeck, AIsacrifice Sacrifice) {
        cpuEffectCardGrabberSearch(EffectMonster, cardIdInDeck, Sacrifice, new AIsacrifice());
    }
    // same as before, but with 2 sacrifices instead of one
    public static void cpuEffectCardGrabberBurial (SummonedMonster EffectMonster, int cardIdInDeck, AIsacrifice Sacrifice1, AIsacrifice Sacrifice2) {
        if (worthInSemipointsOfAllCurrentSacrifices()==2) {
            Game.isActCPUEff = true;
            // Since one can not sacrifice cards from the deck, here one doesn't have to check things.
            if (Game.isSwitchingOnOncePerTurnRule) {
                EffectMonster.canStillUseOncePerTurnEffect=false;
            }
            cpuPaysCost(Sacrifice1, Sacrifice2);
            YCard Card = DeckCPU.getCardInDeckByCardID(cardIdInDeck);
            YuGiOhJi.informationDialog("Computer wants to use its " + getNumberAsString(EffectMonster.sumMonsterNumber) + " monster, " + EffectMonster.Monster.monsterName + ", to get its card " + Card.cardName + " from its deck to its graveyard.", "Effect attempt");
            boolean isCanceling = askPlayerToNegateActivatedEffect(EffectMonster);
            if (!isCanceling) {
                YuGiOhJi.informationDialog("Computer uses its " + getNumberAsString(EffectMonster.sumMonsterNumber) + " monster, " + EffectMonster.Monster.monsterName + ", to get its card " + Card.cardName + " from its deck to its graveyard.", "Burying/Milling");
                int cardNumberInDeck = DeckCPU.getPositionOfCardWithCardIdInDeck(cardIdInDeck);
                YMonster.effectCardGrabber2Execute(cardNumberInDeck, false);
            }
            Game.deactivateCurrentCPUEffects();
        }
    }
    // used to test, if the CPU can use the optional effect of Card Grabber (only considering 1st effect: the search effect)
    public static boolean cpuCanUseEffectCardGrabberBurial() { // can always pay the cost for this 2nd effect, since Card Grabber can always tribute itself
        boolean hasWorkingMonsterEffect = hasWorkingMonsterEffect(false, Mon.CardGrabber.monsterId);
        int numberOfValidTargets = GYDeckCPU.numberOfCards;
        return ( hasWorkingMonsterEffect && numberOfValidTargets>=1 );
    }
    public static boolean cpuCanUseEffectCardGrabberBurial (SummonedMonster EffectMonster) {
        boolean hasWorkingMonsterEffect = hasWorkingMonsterEffect(EffectMonster, Mon.CardGrabber.monsterId);
        int numberOfValidTargets = GYDeckCPU.numberOfCards;
        return ( hasWorkingMonsterEffect && numberOfValidTargets>=1 );
    }
    public static boolean cpuCanUseEffectCardGrabberBurial (SummonedMonster EffectMonster, int cardIdInDeck) {
        boolean hasWorkingMonsterEffect = hasWorkingMonsterEffect(EffectMonster, Mon.CardGrabber.monsterId);
        int cardNumberInGY = GYDeckCPU.getPositionOfCardWithCardIdInDeck(cardIdInDeck);
        return ( hasWorkingMonsterEffect && cardNumberInGY!=0 );
    }
    
    
    // Effect ID = 6.1 (effect of Skill Stealer on summoned monster)
    // reuse YMonster.revealNthHandCard(int cardNumberOnHand, boolean isPlayersHand)
    // and YMonster.revealFaceDownMonster(int monsterNumber, boolean isPlayersMonster)
    public static void cpuEffectSkillStealerByRevealingHandCard (SummonedMonster EffectMonster, SummonedMonster TargetMonster, int cardNumberOnHandToShow) {
        if (Game.isSwitchingOnOncePerTurnRule) {
            EffectMonster.canStillUseOncePerTurnEffect=false;
        }
        YMonster.revealNthHandCard(cardNumberOnHandToShow, false);
        cpuEffectSkillStealer(EffectMonster, TargetMonster);
    }
    public static void cpuEffectSkillStealerByRevealingMonster (SummonedMonster EffectMonster, SummonedMonster TargetMonster, SummonedMonster FaceDownMonsterToShow) {
        if (Game.isSwitchingOnOncePerTurnRule) {
            EffectMonster.canStillUseOncePerTurnEffect=false;
        }
        YMonster.revealFaceDownMonster(FaceDownMonsterToShow.sumMonsterNumber, FaceDownMonsterToShow.isPlayersMonster); // this should always be a monster of the CPU
        cpuEffectSkillStealer(EffectMonster, TargetMonster);
    }
    // in order not to repeat oneself, out-source here the main part of the effect
    public static void cpuEffectSkillStealer (SummonedMonster EffectMonster, SummonedMonster TargetMonster) {
        if (TargetMonster.hasStealableEffect()) {
            Game.isActCPUEff = true;
            String pronoun;
            if (TargetMonster.isPlayersMonster) {pronoun = "your ";}
            else {pronoun = "its ";}
            YuGiOhJi.informationDialog("Computer wants to use its " + getNumberAsString(EffectMonster.sumMonsterNumber) + " monster, " + EffectMonster.Monster.monsterName + ", to copy the effects of " + pronoun + getNumberAsString(TargetMonster.sumMonsterNumber) + " monster," + TargetMonster.Monster.monsterName + ".", "Effect attempt");
            boolean isCanceling = askPlayerToNegateActivatedEffect(EffectMonster);
            if (!isCanceling) {
                YuGiOhJi.informationDialog("Computer uses its " + getNumberAsString(EffectMonster.sumMonsterNumber) + " monster, " + EffectMonster.Monster.monsterName + ", to copy the effects of " + pronoun + getNumberAsString(TargetMonster.sumMonsterNumber) + " monster," + TargetMonster.Monster.monsterName + ".", "Copying Effect");
                copyMonsterEffect(EffectMonster, TargetMonster.Monster.monsterId);
            }
            Game.deactivateCurrentCPUEffects();
        }
    }
    // used to test, if the CPU can use the optional effect of Skill Stealer
    public static boolean cpuCanUseEffectSkillStealer() { // checks is one of the 4 versions of the effect can be used
        return ( cpuCanUseEffectSkillStealerByRevealingHandCard() || cpuCanUseEffectSkillStealerByRevealingMonster() || cpuCanUseEffectSkillStealerOnEquipCardByRevealingHandCard() || cpuCanUseEffectSkillStealerOnEquipCardByRevealingMonster() );
    }
    public static boolean cpuCanUseEffectSkillStealer (SummonedMonster EffectMonster) { // checks if one of the 4 versions of the effect can be used
        return ( cpuCanUseEffectSkillStealerByRevealingHandCard(EffectMonster) || cpuCanUseEffectSkillStealerByRevealingMonster(EffectMonster) || cpuCanUseEffectSkillStealerOnEquipCardByRevealingHandCard(EffectMonster) || cpuCanUseEffectSkillStealerOnEquipCardByRevealingMonster(EffectMonster) );
    }
    public static boolean cpuCanUseEffectSkillStealerOnMonster (SummonedMonster EffectMonster, SummonedMonster TargetMonster) { // checks if one of the 2 versions targeting summoned monsters of the effect can be used
        return ( cpuCanUseEffectSkillStealerByRevealingHandCard(EffectMonster, TargetMonster) || cpuCanUseEffectSkillStealerByRevealingMonster(EffectMonster, TargetMonster) );
    }
    public static boolean cpuCanUseEffectSkillStealerOnEquipCard (SummonedMonster EffectMonster, EStack Stack, int cardNumberInStack) { // checks if one of the 2 versions targeting equip cards of the effect can be used
        return ( cpuCanUseEffectSkillStealerOnEquipCardByRevealingHandCard(EffectMonster, Stack, cardNumberInStack) || cpuCanUseEffectSkillStealerOnEquipCardByRevealingMonster(EffectMonster, Stack, cardNumberInStack) );
    }
    public static boolean cpuCanUseEffectSkillStealerByRevealingHandCard() {
        boolean hasWorkingMonsterEffect = hasWorkingMonsterEffect(false, Mon.SkillStealer.monsterId);
        boolean canPayCost = HandCPU.numberOfCards>=1;
        boolean hasValidTarget = SummonedMonster.stealableEffectsExistInMonsterCardZones();
        return ( hasWorkingMonsterEffect && canPayCost && hasValidTarget );
    }
    public static boolean cpuCanUseEffectSkillStealerByRevealingHandCard (SummonedMonster EffectMonster) {
        boolean hasWorkingMonsterEffect = hasWorkingMonsterEffect(EffectMonster, Mon.SkillStealer.monsterId);
        boolean canPayCost = HandCPU.numberOfCards>=1;
        boolean hasValidTarget = SummonedMonster.stealableEffectsExistInMonsterCardZones();
        return ( hasWorkingMonsterEffect && canPayCost && hasValidTarget );
    }
    public static boolean cpuCanUseEffectSkillStealerByRevealingHandCard (SummonedMonster EffectMonster, SummonedMonster TargetMonster) {
        boolean hasWorkingMonsterEffect = hasWorkingMonsterEffect(EffectMonster, Mon.SkillStealer.monsterId);
        boolean canPayCost = HandCPU.numberOfCards>=1;
        boolean isValidTarget = TargetMonster.hasStealableEffect();
        return ( hasWorkingMonsterEffect && canPayCost && isValidTarget );
    }
    public static boolean cpuCanUseEffectSkillStealerByRevealingMonster() {
        boolean hasWorkingMonsterEffect = hasWorkingMonsterEffect(false, Mon.SkillStealer.monsterId);
        boolean canPayCost = SummonedMonster.countOwnFaceDownMonsters(false, false)>=1;
        boolean hasValidTarget = SummonedMonster.stealableEffectsExistInMonsterCardZones();
        return ( hasWorkingMonsterEffect && canPayCost && hasValidTarget );
    }
    public static boolean cpuCanUseEffectSkillStealerByRevealingMonster (SummonedMonster EffectMonster) {
        boolean hasWorkingMonsterEffect = hasWorkingMonsterEffect(EffectMonster, Mon.SkillStealer.monsterId);
        boolean canPayCost = SummonedMonster.countOwnFaceDownMonsters(false, false)>=1;
        boolean hasValidTarget = SummonedMonster.stealableEffectsExistInMonsterCardZones();
        return ( hasWorkingMonsterEffect && canPayCost && hasValidTarget );
    }
    public static boolean cpuCanUseEffectSkillStealerByRevealingMonster (SummonedMonster EffectMonster, SummonedMonster TargetMonster) {
        boolean hasWorkingMonsterEffect = hasWorkingMonsterEffect(EffectMonster, Mon.SkillStealer.monsterId);
        boolean canPayCost = SummonedMonster.countOwnFaceDownMonsters(false, false)>=1;
        boolean isValidTarget = TargetMonster.hasStealableEffect();
        return ( hasWorkingMonsterEffect && canPayCost && isValidTarget );
    }
    
    
    // Effect ID = 6.2 (effect of Skill Stealer on equip cards)
    // additional methods just for copying the effects of the equip monster Lance
    public static void cpuEffectSkillStealerOnEquipCardByRevealingHandCard (SummonedMonster EffectMonster, EStack Stack, int cardNumberInStack, int cardNumberOnHandToShow) {
        if (Game.isSwitchingOnOncePerTurnRule) {
            EffectMonster.canStillUseOncePerTurnEffect=false;
        }
        YMonster.revealNthHandCard(cardNumberOnHandToShow, false);
        cpuEffectSkillStealerOnEquipCard(EffectMonster, Stack, cardNumberInStack);
    }
    public static void cpuEffectSkillStealerOnEquipCardByRevealingMonster (SummonedMonster EffectMonster, EStack Stack, int cardNumberInStack, SummonedMonster FaceDownMonsterToShow) {
        if (Game.isSwitchingOnOncePerTurnRule) {
            EffectMonster.canStillUseOncePerTurnEffect=false;
        }
        YMonster.revealFaceDownMonster(FaceDownMonsterToShow.sumMonsterNumber, FaceDownMonsterToShow.isPlayersMonster); // this should always be a monster of the CPU
        cpuEffectSkillStealerOnEquipCard(EffectMonster, Stack, cardNumberInStack);
    }
    // in order not to repeat oneself, out-source here the main part of the effect
    public static void cpuEffectSkillStealerOnEquipCard (SummonedMonster EffectMonster, EStack Stack, int cardNumberInStack) {
        YCard Card = Stack.getNthCardOfStack(cardNumberInStack);
        if (Stack.isCopyableEquipCard(cardNumberInStack)) {
            Game.isActCPUEff = true;
            YuGiOhJi.informationDialog("Computer wants to use its " + getNumberAsString(EffectMonster.sumMonsterNumber) + " monster, " + EffectMonster.Monster.monsterName + ", to copy the passive effects of the equip monster " + Card.lowMonster.monsterName + ".", "Effect attempt");
            boolean isCanceling = askPlayerToNegateActivatedEffect(EffectMonster);
            if (!isCanceling) {
                YuGiOhJi.informationDialog("Computer uses its " + getNumberAsString(EffectMonster.sumMonsterNumber) + " monster, " + EffectMonster.Monster.monsterName + ", to copy the passive effects of the equip monster " + Card.lowMonster.monsterName + ".", "Copying effect");
                copyMonsterEffect(EffectMonster, Card.lowMonster.monsterId);
            }
            Game.deactivateCurrentCPUEffects();
        }
    }
    // used to test, if the CPU can use the optional effect of Skill Stealer on the equip card Lance
    public static boolean cpuCanUseEffectSkillStealerOnEquipCardByRevealingHandCard() {
        boolean hasWorkingMonsterEffect = hasWorkingMonsterEffect(false, Mon.SkillStealer.monsterId);
        boolean canPayCost = HandCPU.numberOfCards>=1;
        boolean hasValidTarget = EStack.lookForCopyableEffectOfEquipCards();
        return ( hasWorkingMonsterEffect && canPayCost && hasValidTarget );
    }
    public static boolean cpuCanUseEffectSkillStealerOnEquipCardByRevealingHandCard (SummonedMonster EffectMonster) {
        boolean hasWorkingMonsterEffect = hasWorkingMonsterEffect(EffectMonster, Mon.SkillStealer.monsterId);
        boolean canPayCost = HandCPU.numberOfCards>=1;
        boolean hasValidTarget = EStack.lookForCopyableEffectOfEquipCards();
        return ( hasWorkingMonsterEffect && canPayCost && hasValidTarget );
    }
    public static boolean cpuCanUseEffectSkillStealerOnEquipCardByRevealingHandCard (SummonedMonster EffectMonster, EStack Stack, int cardNumberInStack) {
        boolean hasWorkingMonsterEffect = hasWorkingMonsterEffect(EffectMonster, Mon.SkillStealer.monsterId);
        boolean canPayCost = HandCPU.numberOfCards>=1;
        boolean isValidTarget = Stack.getNthCardOfStack(cardNumberInStack).lowMonster.isEquipMonsterAndHasCopyableEffect();
        return ( hasWorkingMonsterEffect && canPayCost && isValidTarget );
    }
    public static boolean cpuCanUseEffectSkillStealerOnEquipCardByRevealingMonster() {
        boolean hasWorkingMonsterEffect = hasWorkingMonsterEffect(false, Mon.SkillStealer.monsterId);
        boolean canPayCost = SummonedMonster.countOwnFaceDownMonsters(false, false)>=1;
        boolean hasValidTarget = EStack.lookForCopyableEffectOfEquipCards();
        return ( hasWorkingMonsterEffect && canPayCost && hasValidTarget );
    }
    public static boolean cpuCanUseEffectSkillStealerOnEquipCardByRevealingMonster (SummonedMonster EffectMonster) {
        boolean hasWorkingMonsterEffect = hasWorkingMonsterEffect(EffectMonster, Mon.SkillStealer.monsterId);
        boolean canPayCost = SummonedMonster.countOwnFaceDownMonsters(false, false)>=1;
        boolean hasValidTarget = EStack.lookForCopyableEffectOfEquipCards();
        return ( hasWorkingMonsterEffect && canPayCost && hasValidTarget );
    }
    public static boolean cpuCanUseEffectSkillStealerOnEquipCardByRevealingMonster (SummonedMonster EffectMonster, EStack Stack, int cardNumberInStack) {
        boolean hasWorkingMonsterEffect = hasWorkingMonsterEffect(EffectMonster, Mon.SkillStealer.monsterId);
        boolean canPayCost = SummonedMonster.countOwnFaceDownMonsters(false, false)>=1;
        boolean isValidTarget = Stack.isCopyableEquipCard(cardNumberInStack);
        return ( hasWorkingMonsterEffect && canPayCost && isValidTarget );
    }
    
    // in order not to repeat oneself, out-source here the part, where the copying of a monster effect happens
    // (kind of analog to method with same name in YMonster class file)
    public static void copyMonsterEffect (SummonedMonster EffectMonster, int monsterId) {
        YuGiOhJi.informationDialog("copied effect of " + YMonster.getMonsterById(monsterId).monsterName, "");
        EffectMonster.isUsingEffectsOfOtherMonster=true;
        EffectMonster.otherEffectMonsterID=monsterId;
    }
    
    
    // Effect ID = 7
    public static void cpuEffectCopyCat (SummonedMonster EffectMonster, int monsterIdOnHand) {
        int cardIdOnHand = YCard.getCardIdByMonsterId(monsterIdOnHand);
        int cardNumberOnHand = HandCPU.getPositionOfCardWithCardId(cardIdOnHand);
        YMonster Monster = YMonster.getMonsterById(monsterIdOnHand);
        if (Monster.hasStealableEffect() && cardNumberOnHand!=0) { // only do something is effect is copyable and, if CPU has the corresponding card on the hand
            Game.isActCPUEff = true;
            if (Game.isSwitchingOnOncePerTurnRule) {
                EffectMonster.canStillUseOncePerTurnEffect=false;
            }
            Hand.discardCard(cardNumberOnHand, false);
            YuGiOhJi.informationDialog("CPU discards card " + HandCPU.getNthCardOfHand(cardNumberOnHand).cardName, "");
            YuGiOhJi.informationDialog("Computer wants to use its " + getNumberAsString(EffectMonster.sumMonsterNumber) + " monster, " + EffectMonster.Monster.monsterName + ", to copy the effects of its discarded monster " + YMonster.getMonsterById(monsterIdOnHand).monsterName + ".", "Effect attempt");
            boolean isCanceling = askPlayerToNegateActivatedEffect(EffectMonster);
            if (!isCanceling) {
                YuGiOhJi.informationDialog("Computer uses its " + getNumberAsString(EffectMonster.sumMonsterNumber) + " monster, " + EffectMonster.Monster.monsterName + ", to copy the effects of its discarded monster " + YMonster.getMonsterById(monsterIdOnHand).monsterName + ".", "Copying effect");
                copyMonsterEffect(EffectMonster, monsterIdOnHand);
            }
            Game.deactivateCurrentCPUEffects();
        }
    }
    // used to test, if the CPU can use the optional effect of Copy Cat
    public static boolean cpuCanUseEffectCopyCat() {
        boolean hasWorkingMonsterEffect = hasWorkingMonsterEffect(false, Mon.CopyCat.monsterId);
        return ( hasWorkingMonsterEffect && HandCPU.stealableEffectsExistOnHand() );
    }
    public static boolean cpuCanUseEffectCopyCat (SummonedMonster EffectMonster) {
        boolean hasWorkingMonsterEffect = hasWorkingMonsterEffect(EffectMonster, Mon.CopyCat.monsterId);
        return ( hasWorkingMonsterEffect && HandCPU.stealableEffectsExistOnHand() );
    }
    public static boolean cpuCanUseEffectCopyCat (SummonedMonster EffectMonster, int monsterId) { // can not copy attack stopping effects, equip effects and no effects
        YMonster Monster = YMonster.getMonsterById(monsterId);
        if (!Monster.hasStealableEffect()) {
            return false;
        }
        else {
            boolean hasWorkingMonsterEffect = hasWorkingMonsterEffect(EffectMonster, Mon.CopyCat.monsterId);
            int cardNumberOnHand = HandCPU.getPositionOfCardWithCardId(YCard.getCardIdByMonsterId(monsterId));
            return ( hasWorkingMonsterEffect && cardNumberOnHand!=0 );
        }
    }
    
    
    // Effect ID = 8
    // reuse method SummonedMonster.changeMode(SummonedMonster SumMonster, boolean isChangedByEffect, boolean isUsingCheatChangeRuleIfPossible)
    public static void cpuEffectModeChanger (SummonedMonster EffectMonster, SummonedMonster TargetMonster, boolean isUsingCheatChange, AIsacrifice Sacrifice) {
        int toBePaidSemipoints = worthInSemipointsOfAllCurrentSacrifices();
        if (AIsacrifice.sacrificeIsNotTargetMonster(Sacrifice, TargetMonster) && (toBePaidSemipoints==1 || toBePaidSemipoints==2)) { // proceed only, if target is not tributed away
            Game.isActCPUEff = true;
            if (Game.isSwitchingOnOncePerTurnRule) {
                EffectMonster.canStillUseOncePerTurnEffect=false;
            }
            cpuPaysCost(Sacrifice);
            String pronoun;
            String monsterName="";
            if (TargetMonster.isPlayersMonster) {pronoun = "your ";}
            else {
                pronoun = "its ";
                if (TargetMonster.isKnownToPlayer()) {monsterName = ", " + TargetMonster.Monster.monsterName;}
            }
            String cheat;
            if (Game.isSwitchingOnCheatModeChangingRule && isUsingCheatChange) {cheat = " cheat ";}
            else {cheat = "";}
            YuGiOhJi.informationDialog("Computer wants to use its " + getNumberAsString(EffectMonster.sumMonsterNumber) + " monster, " + EffectMonster.Monster.monsterName + ", to" + cheat + " change the mode of " + pronoun + getNumberAsString(TargetMonster.sumMonsterNumber) + " monster" + monsterName + ".", "Effect attempt");
            boolean isCanceling = askPlayerToNegateActivatedEffect(EffectMonster);
            if (!isCanceling) {
                YuGiOhJi.informationDialog("Computer uses its " + getNumberAsString(EffectMonster.sumMonsterNumber) + " monster, " + EffectMonster.Monster.monsterName + ", to" + cheat + " change the mode of " + pronoun + getNumberAsString(TargetMonster.sumMonsterNumber) + " monster" + monsterName + ".", "Changing mode");
                TargetMonster.changeMode(true, Game.isSwitchingOnCheatModeChangingRule && isUsingCheatChange);
            }
            Game.deactivateCurrentCPUEffects();
        }
    }
    // used to test, if the CPU can use the optional effect of Mode Changer
    public static boolean cpuCanUseEffectModeChanger() {
        return hasWorkingMonsterEffect(false, Mon.ModeChanger.monsterId); // apart from working effect always usable, because Mode Changer can always tribute itself and can always use effect on itself
    }
    public static boolean cpuCanUseEffectModeChanger (SummonedMonster EffectMonster) { // returns true, if a given player controls a monster, that can has a non-negated Mode Changer effect
        return hasWorkingMonsterEffect(EffectMonster, Mon.ModeChanger.monsterId); // apart from working effect always usable, because Mode Changer can always tribute itself and can always use effect on itself
    }
    public static boolean cpuCanUseEffectModeChanger (SummonedMonster EffectMonster, SummonedMonster TargetMonster) {
        return ( cpuCanUseEffectModeChanger(EffectMonster) && !TargetMonster.isImmune() );
    }
    
    // ORDINARY MODE CHANGE (not an effect, but it fits best here)
    // The CPU has the right to change the mode of its nth monster once per turn, if it has not been summoned this turn.
    public static void cpuChangeModeOfNthMonster (int n) {
        cpuChangeModeOfNthMonster(n, false);
    }
    // same as above, but using the cheat change rule to switch between monsters
    public static void cpuChangeModeOfNthMonster (int n, boolean isUsingCheatChange) {
        SummonedMonster SumMonster = SummonedMonster.getNthSummonedMonster(n, false);
        if (SumMonster.isModeChangeable()) {
            String cheat="";
            if (!Game.isSwitchingOnCheatModeChangingRule) {
                isUsingCheatChange=false;
            }
            else {
                if (isUsingCheatChange) {cheat = " cheat";}
            }
            String monsterName="";
            if (SumMonster.isKnownToPlayer()) {monsterName = ", " + SumMonster.Monster.monsterName;}
            YuGiOhJi.informationDialog("Computer" + cheat + " changes the mode of its " + getNumberAsString(SumMonster.sumMonsterNumber) + " monster" + monsterName + ".", "Changing mode");
            // Don't let player interfere here! The once-per-turn-mode-change of a monster can not be negated! (not even the cheat change variant)
            SumMonster.changeMode(false, isUsingCheatChange);
        }
    }
    // same as above, just with more readable name instead of another argument
    public static void cpuCheatChangeModeOfNthMonster (int n) {
        cpuChangeModeOfNthMonster(n, true);
    }
    // used to test, if the CPU can use the ordinary mode change
    public static boolean cpuCanChangeModeOfNthMonster (int n) {
        return SummonedMonster.getNthSummonedMonster(n, false).isModeChangeable();
    }
    public static boolean cpuCanCheatChangeModeOfNthMonster (int n) {
        return (cpuCanChangeModeOfNthMonster(n) && Game.isSwitchingOnCheatModeChangingRule);
    }
    
    
    // Effect ID = 9 (effect of Necromancer reviving a MOOK in the GY of the CPU)
    public static void cpuEffectNecromancerRevivesMook (SummonedMonster EffectMonster, int mookMonsterIdInGY, boolean isInAttackMode, AIsacrifice Sacrifice) {
        int cardId = YCard.getCardIdByMonsterId(mookMonsterIdInGY);
        if (AIsacrifice.sacrificeIsNotTargetedGYCard(Sacrifice, cardId) && worthInSemipointsOfAllCurrentSacrifices()==1) { // proceed only, if target is not tributed away
            if (YMonster.getMonsterById(mookMonsterIdInGY).stars==1) {
                Game.isActCPUEff = true;
                if (Game.isSwitchingOnOncePerTurnRule) {
                    EffectMonster.canStillUseOncePerTurnEffect=false;
                }
                cpuPaysCost(Sacrifice);
                String mode;
                if (isInAttackMode) {mode = "attack mode (" + YMonster.getMonsterById(mookMonsterIdInGY).att + " attack)";}
                else {mode = "defence mode (" + YMonster.getMonsterById(mookMonsterIdInGY).def + " defence)";}
                YuGiOhJi.informationDialog("Computer wants to use its " + getNumberAsString(EffectMonster.sumMonsterNumber) + " monster, " + EffectMonster.Monster.monsterName + ", to revive its MOOK, " + YMonster.getMonsterById(mookMonsterIdInGY).monsterName + ", in " + mode + ".", "Effect attempt");
                boolean isCancelingViaBanisher = YMonster.askPlayerToNegateSpecSumFromGY(GYDeckCPU.getPositionOfCardWithCardIdInDeck(mookMonsterIdInGY)); // ask first form specific negation using Banisher, before asking for  general effect negation, because easier this way
                boolean isCanceling = askPlayerToNegateActivatedEffect(EffectMonster);
                if (!isCanceling && !isCancelingViaBanisher) {
                    YuGiOhJi.informationDialog("Computer uses its " + getNumberAsString(EffectMonster.sumMonsterNumber) + " monster, " + EffectMonster.Monster.monsterName + ", to revive its MOOK, " + YMonster.getMonsterById(mookMonsterIdInGY).monsterName + ", in " + mode +  ".", "Summoning");
                    cpuRevivesMonster(mookMonsterIdInGY, isInAttackMode);
                }
                Game.deactivateCurrentCPUEffects();
            }
        }
    }
    // used to test, if the CPU can use the optional effect of Necromancer (only considering 1st effect: reviving MOOKS)
    public static boolean cpuCanUseEffectNecromancerRevivesMook() {
        boolean hasWorkingNecromancerEffect = hasWorkingMonsterEffect(false, Mon.Necromancer.monsterId);
        boolean hasValidTarget = GYDeckCPU.hasMook();
        int numberOfEquipCards = EStack.countOwnEquipMonsters(false, false);
        int numberOfCardsInGY = GYDeckCPU.numberOfCards;
        return ( hasWorkingNecromancerEffect && SummonedMonster.hasFreeMonsterZone(false) && hasValidTarget && numberOfEquipCards+numberOfCardsInGY>=2 ); // CPU needs at least two half cards: one to revive, one to pay cost
    }
    public static boolean cpuCanUseEffectNecromancerRevivesMook (SummonedMonster EffectMonster) {
        boolean hasWorkingNecromancerEffect = hasWorkingMonsterEffect(EffectMonster, Mon.Necromancer.monsterId);
        boolean hasValidTarget = GYDeckCPU.hasMook();
        int numberOfEquipCards = EStack.countOwnEquipMonsters(false, false);
        int numberOfCardsInGY = GYDeckCPU.numberOfCards;
        return ( hasWorkingNecromancerEffect && SummonedMonster.hasFreeMonsterZone(false) && hasValidTarget && numberOfEquipCards+numberOfCardsInGY>=2 ); // CPU needs at least two half cards: one to revive, one to pay cost
    }
    public static boolean cpuCanUseEffectNecromancerRevivesMook (SummonedMonster EffectMonster, int mookMonsterIdInGY) {
        boolean hasSpecificMookInGY = GYDeckCPU.getPositionOfCardWithCardIdInDeck(mookMonsterIdInGY)!=0;
        return ( YMonster.getMonsterById(mookMonsterIdInGY).stars==1 && hasSpecificMookInGY && cpuCanUseEffectNecromancerRevivesMook(EffectMonster) );
    }
    
    // in order not to repeat oneself, out-source here what happens, when the CPU revives a monster with a given ID from its GY
    public static void cpuRevivesMonster (int monsterIdInGY, boolean isInAttackMode) {
        SummonedMonster TargetMonster = SummonedMonster.determineFreeMonsterZone(false);
        if (GYDeckCPU.numberOfCards>0) {
            for (int index = GYDeckCPU.numberOfCards; index >= 1; index--){
                if (GYDeckCPU.getNthCardOfDeck(index).lowMonster.monsterId==monsterIdInGY) {
                    YCard Card = GYDeckCPU.getNthCardOfDeck(index);
                    YCard.setCardToNthSumMonster(Card, false, false, isInAttackMode, TargetMonster.sumMonsterNumber, true, false);
                    break;
                }
                else if (GYDeckCPU.getNthCardOfDeck(index).upMonster.monsterId==monsterIdInGY) {
                    YCard Card = GYDeckCPU.getNthCardOfDeck(index);
                    YCard.setCardToNthSumMonster(Card, true, false, isInAttackMode, TargetMonster.sumMonsterNumber, true, false);
                    break;
                }
            }
        }
    }
    
    
    // Effect ID = 10 (effect of Necromancer reviving a MIDBOSS in the GY of the CPU)
    public static void cpuEffectNecromancerRevivesMidboss (SummonedMonster EffectMonster, int midbossMonsterIdInGY, boolean isInAttackMode, AIsacrifice Sacrifice) {
        cpuEffectNecromancerRevivesMidboss(EffectMonster, midbossMonsterIdInGY, isInAttackMode, Sacrifice, new AIsacrifice());
    }
    // same as before, but with 2 sacrifices instead of one
    public static void cpuEffectNecromancerRevivesMidboss (SummonedMonster EffectMonster, int midbossMonsterIdInGY, boolean isInAttackMode, AIsacrifice Sacrifice1, AIsacrifice Sacrifice2) {
        int cardId = YCard.getCardIdByMonsterId(midbossMonsterIdInGY);
        if (AIsacrifice.sacrificeIsNotTargetedGYCard(Sacrifice1, Sacrifice2, cardId) && worthInSemipointsOfAllCurrentSacrifices()==2) { // proceed only, if target is not tributed away
            if (YMonster.getMonsterById(midbossMonsterIdInGY).stars==2) {
                Game.isActCPUEff = true;
                if (Game.isSwitchingOnOncePerTurnRule) {
                    EffectMonster.canStillUseOncePerTurnEffect=false;
                }
                cpuPaysCost(Sacrifice1, Sacrifice2);
                String mode;
                if (isInAttackMode) {mode = "attack mode (" + YMonster.getMonsterById(midbossMonsterIdInGY).att + " attack)";}
                else {mode = "defence mode (" + YMonster.getMonsterById(midbossMonsterIdInGY).def + " defence)";}
                YuGiOhJi.informationDialog("Computer wants to use its " + getNumberAsString(EffectMonster.sumMonsterNumber) + " monster, " + EffectMonster.Monster.monsterName + ", to revive its MIDBOSS, " + YMonster.getMonsterById(midbossMonsterIdInGY).monsterName + ", in " + mode + ".", "Effect attempt");
                boolean isCancelingViaBanisher = YMonster.askPlayerToNegateSpecSumFromGY(GYDeckCPU.getPositionOfCardWithCardIdInDeck(midbossMonsterIdInGY)); // ask first form specific negation using Banisher, before asking for  general effect negation, because easier this way
                boolean isCanceling = askPlayerToNegateActivatedEffect(EffectMonster);
                if (!isCanceling && !isCancelingViaBanisher) {
                    YuGiOhJi.informationDialog("Computer uses its " + getNumberAsString(EffectMonster.sumMonsterNumber) + " monster, " + EffectMonster.Monster.monsterName + ", to revive its MIDBOSS, " + YMonster.getMonsterById(midbossMonsterIdInGY).monsterName + ", in " + mode +  ".", "Summoning");
                    cpuRevivesMonster(midbossMonsterIdInGY, isInAttackMode);
                }
                Game.deactivateCurrentCPUEffects();
            }
        }
    }
    // used to test, if the CPU can use the optional effect of Necromancer (only considering 2nd effect: reviving MIDBOSSES)
    public static boolean cpuCanUseEffectNecromancerRevivesMidboss() {
        boolean hasWorkingNecromancerEffect = hasWorkingMonsterEffect(false, Mon.Necromancer.monsterId);
        boolean hasValidTarget = GYDeckCPU.hasMidboss();
        return ( hasWorkingNecromancerEffect && SummonedMonster.hasFreeMonsterZone(false) && hasValidTarget ); // CPU is always able to pay cost, since Necromancer can always tribute itself
    } // for simplicity, I demand here that the computer has one free monster zone, just so that it can not tribute in a wrong way
    public static boolean cpuCanUseEffectNecromancerRevivesMidboss (SummonedMonster EffectMonster) {
        boolean hasWorkingNecromancerEffect = hasWorkingMonsterEffect(EffectMonster, Mon.Necromancer.monsterId);
        boolean hasValidTarget = GYDeckCPU.hasMidboss();
        return ( hasWorkingNecromancerEffect && SummonedMonster.hasFreeMonsterZone(false) && hasValidTarget ); // CPU is always able to pay cost, since Necromancer can always tribute itself
    }
    public static boolean cpuCanUseEffectNecromancerRevivesMidboss (SummonedMonster EffectMonster, int midbossMonsterIdInGY) {
        boolean hasSpecificMidbossInGY = GYDeckCPU.getPositionOfCardWithCardIdInDeck(midbossMonsterIdInGY)!=0;
        return ( YMonster.getMonsterById(midbossMonsterIdInGY).stars==2 && hasSpecificMidbossInGY && cpuCanUseEffectNecromancerRevivesMidboss(EffectMonster) );
    }
    
    
    // Effect ID = 11.1 (effect of Big Back Bouncer on summoned monster)
    public static void cpuEffectBigBackBouncerOnMonster (SummonedMonster EffectMonster, SummonedMonster TargetMonster, AIsacrifice Sacrifice) {
        cpuEffectBigBackBouncerOnMonster(EffectMonster, TargetMonster, Sacrifice, new AIsacrifice());
    }
    // same as before, but with 2 sacrifices instead of one
    public static void cpuEffectBigBackBouncerOnMonster (SummonedMonster EffectMonster, SummonedMonster TargetMonster, AIsacrifice Sacrifice1, AIsacrifice Sacrifice2) {
        if (AIsacrifice.sacrificeIsNotTargetMonster(Sacrifice1, Sacrifice2, TargetMonster) && worthInSemipointsOfAllCurrentSacrifices()==2) { // proceed only, if target is not tributed away
            Game.isActCPUEff = true;
            if (Game.isSwitchingOnOncePerTurnRule) {
                EffectMonster.canStillUseOncePerTurnEffect=false;
            }
            cpuPaysCost(Sacrifice1, Sacrifice2);
            String pronoun;
            String monsterName="";
            if (TargetMonster.isPlayersMonster) {pronoun = "your ";}
            else {
                pronoun = "its ";
                if (TargetMonster.isKnownToPlayer()) {monsterName = ", " + TargetMonster.Monster.monsterName + ",";}
            }
            YuGiOhJi.informationDialog("Computer wants to use its " + getNumberAsString(EffectMonster.sumMonsterNumber) + " monster, " + EffectMonster.Monster.monsterName + ", to bounce " + pronoun + getNumberAsString(TargetMonster.sumMonsterNumber) + " monster" + monsterName + " back to the hand.", "Effect attempt");
            boolean isCanceling = askPlayerToNegateActivatedEffect(EffectMonster);
            if (!isCanceling) {
                YuGiOhJi.informationDialog("Computer uses its " + getNumberAsString(EffectMonster.sumMonsterNumber) + " monster, " + EffectMonster.Monster.monsterName + ", to bounce " + pronoun + getNumberAsString(TargetMonster.sumMonsterNumber) + " monster" + monsterName + " back to the hand.", "Returning card");
                Game.deactivateCurrentCPUEffects();
                YCard.returnCardBackToHand(TargetMonster); // whom the monster originally belonged to is already checked in that method
            }
            Game.deactivateCurrentCPUEffects();
        }
    }
    // used to test, if the CPU can use the optional effect of Big Back Bouncer
    public static boolean cpuCanUseEffectBigBackBouncer() {
        boolean hasWorkingBigBackBouncerEffect = hasWorkingMonsterEffect(false, Mon.BigBackBouncer.monsterId);
        boolean cpuHasNotFullHand = HandCPU.numberOfCards<10; // for simplicity, forbid CPU to use effect, if it has full hand already
        boolean playerHasNotFullHand = Hand.numberOfCardsOnHand(true)<10; // for simplicity, forbid CPU to use effect, if player has full hand already
        int numberOfOwnValidTargets = SummonedMonster.countOwnSummonedMonsters(false)+EStack.countOwnEquipMonsters(false, false)+GYDeckCPU.numberOfCards;
        int numberOfPlayersValidTargets = SummonedMonster.countOwnSummonedMonsters(true)+EStack.countOwnEquipMonsters(true, false)+Deck.numberOfCardsInGY(true);
        return ( hasWorkingBigBackBouncerEffect && cpuHasNotFullHand && playerHasNotFullHand && numberOfOwnValidTargets+numberOfPlayersValidTargets>=2 ); // for simplicity, allow CPU to use the effect only if there are at least 2 valid targets (one to tribute itself and another target)
    }
    public static boolean cpuCanUseEffectBigBackBouncer (SummonedMonster EffectMonster) {
        boolean hasWorkingBigBackBouncerEffect = hasWorkingMonsterEffect(EffectMonster, Mon.BigBackBouncer.monsterId);
        boolean cpuHasNotFullHand = HandCPU.numberOfCards<10; // for simplicity, forbid CPU to use effect, if it has full hand already
        boolean playerHasNotFullHand = Hand.numberOfCardsOnHand(true)<10; // for simplicity, forbid CPU to use effect, if player has full hand already
        int numberOfOwnValidTargets = SummonedMonster.countOwnSummonedMonsters(false)+EStack.countOwnEquipMonsters(false, false)+GYDeckCPU.numberOfCards;
        int numberOfPlayersValidTargets = SummonedMonster.countOwnSummonedMonsters(true)+EStack.countOwnEquipMonsters(true, false)+Deck.numberOfCardsInGY(true);
        return ( hasWorkingBigBackBouncerEffect && cpuHasNotFullHand && playerHasNotFullHand && numberOfOwnValidTargets+numberOfPlayersValidTargets>=2 );
    }
    public static boolean cpuCanUseEffectBigBackBouncerOnMonster (SummonedMonster EffectMonster, SummonedMonster TargetMonster) {
        return ( cpuCanUseEffectBigBackBouncer(EffectMonster) && !TargetMonster.isImmune() );
    }
    // there are no further method to check the effect of Big Back Bouncer, because always usuable, if target it not an immune monster
    
    
    // Effect ID = 11.2 (effect of Big Back Bouncer on equip card)
    public static void cpuEffectBigBackBouncerOnEquipCard (SummonedMonster EffectMonster, EStack Stack, int cardNumberInStack, AIsacrifice Sacrifice) {
        cpuEffectBigBackBouncerOnEquipCard(EffectMonster, Stack, cardNumberInStack, Sacrifice, new AIsacrifice());
    }
    // same as before, but with 2 sacrifices instead of one
    public static void cpuEffectBigBackBouncerOnEquipCard (SummonedMonster EffectMonster, EStack Stack, int cardNumberInStack, AIsacrifice Sacrifice1, AIsacrifice Sacrifice2) {
        if (AIsacrifice.sacrificeIsNotTargetedEquipCard(Sacrifice1, Sacrifice2, Stack, cardNumberInStack) && AIsacrifice.sacrificeIsNotEquippedWithTarget(Sacrifice1, Sacrifice2, Stack) && worthInSemipointsOfAllCurrentSacrifices()==2) { // proceed only, if target is not tributed away
            Game.isActCPUEff = true;
            if (Game.isSwitchingOnOncePerTurnRule) {
                EffectMonster.canStillUseOncePerTurnEffect=false;
            }
            // One has to get a new cardNumberInStack before CPU pays cost, because one could have tributed from the same stack, thus changing the order of the cards.
            int cardIdInStack = Stack.getNthCardOfStack(cardNumberInStack).cardId;
            boolean isPlayersEquipCard = Stack.getControllerOfNthEquipCard(cardNumberInStack);
            boolean isNegated = Stack.getNegationStatusOfNthEquipCard(cardNumberInStack);
            cpuPaysCost(Sacrifice1, Sacrifice2);
            String pronounEquipCard;
            if (isPlayersEquipCard) {pronounEquipCard = "your";}
            else {pronounEquipCard = "its";}
            String possiblyNegated; // In general, it is important for the player to know, if the equip card is negated or not, because there might be the case that there are two cards with same IDs but different negation status. 
            if (Stack.getNegationStatusOfNthEquipCard(cardNumberInStack)) {possiblyNegated = " negated ";}
            else {possiblyNegated = " ";}
            String pronounMonster;
            if (Stack.isBelongingToPlayer) {pronounMonster = "your ";}
            else {pronounMonster = "its ";}
            YuGiOhJi.informationDialog("Computer wants to use its " + getNumberAsString(EffectMonster.sumMonsterNumber) + " monster, " + EffectMonster.Monster.monsterName + ", to bounce " + pronounEquipCard + possiblyNegated + "equip card (" + Stack.getNthCardOfStack(cardNumberInStack).lowMonster.monsterName + ", in equip stack of " + pronounMonster + getNumberAsString(Stack.stackNumber) + " monster) back to the hand.", "Effect attempt");
            boolean isCanceling = askPlayerToNegateActivatedEffect(EffectMonster);
            if (!isCanceling) {
                YuGiOhJi.informationDialog("Computer uses its " + getNumberAsString(EffectMonster.sumMonsterNumber) + " monster, " + EffectMonster.Monster.monsterName + ", to bounce " + pronounEquipCard + possiblyNegated + "equip card (" + Stack.getNthCardOfStack(cardNumberInStack).lowMonster.monsterName + ", in equip stack of " + pronounMonster + getNumberAsString(Stack.stackNumber) + " monster) back to the hand.", "Returning card");
                Game.deactivateCurrentCPUEffects();
                int newCardNumberInStack = Stack.getPositionOfEquipCardByCardID(cardIdInStack, isPlayersEquipCard, isNegated);
                YCard.returnEquipCardBackToHand(Stack, newCardNumberInStack);
            }
            Game.deactivateCurrentCPUEffects();
        }
    }
    
    
    // Effect ID = 11.3 (effect of Big Back Bouncer on own GY card)
    // no method needed to bounce back players GY card back to hand (CPU should never want to do that)
    public static void cpuEffectBigBackBouncerOnGYCard (SummonedMonster EffectMonster, int cardIdInGY, AIsacrifice Sacrifice) {
        cpuEffectBigBackBouncerOnGYCard(EffectMonster, cardIdInGY, Sacrifice, new AIsacrifice());
    }
    // same as before, but with 2 sacrifices instead of one
    public static void cpuEffectBigBackBouncerOnGYCard (SummonedMonster EffectMonster, int cardIdInGY, AIsacrifice Sacrifice1, AIsacrifice Sacrifice2) {
        if (AIsacrifice.sacrificeIsNotTargetedGYCard(Sacrifice1, Sacrifice2, cardIdInGY) && worthInSemipointsOfAllCurrentSacrifices()==2) { // proceed only, if target is not tributed away
            Game.isActCPUEff = true;
            if (Game.isSwitchingOnOncePerTurnRule) {
                EffectMonster.canStillUseOncePerTurnEffect=false;
            }
            cpuPaysCost(Sacrifice1, Sacrifice2);
            int cardNumberInGY = GYDeckCPU.getPositionOfCardWithCardIdInDeck(cardIdInGY);
            YuGiOhJi.informationDialog("Computer wants to use its " + getNumberAsString(EffectMonster.sumMonsterNumber) + " monster, " + EffectMonster.Monster.monsterName + ", to bounce its GY card (" + GYDeckCPU.getNthCardOfDeck(cardNumberInGY).cardName + ") back to the hand.", "Effect attempt");
            boolean isCanceling = askPlayerToNegateActivatedEffect(EffectMonster);
            if (!isCanceling) {
                YuGiOhJi.informationDialog("Computer uses its " + getNumberAsString(EffectMonster.sumMonsterNumber) + " monster, " + EffectMonster.Monster.monsterName + ", to bounce its GY card (" + GYDeckCPU.getNthCardOfDeck(cardNumberInGY).cardName + ") back to the hand.", "Returning card");
                Game.deactivateCurrentCPUEffects();
                Deck.addNthCardFromGYToHand(cardNumberInGY, false);
            }
            Game.deactivateCurrentCPUEffects();
        }
    }
    
    
    // Effect ID = 12
    public static void cpuEffectBigBanisher (SummonedMonster EffectMonster, int cardNumberInGY, AIsacrifice Sacrifice) {
        int toBePaidSeminpoints = worthInSemipointsOfAllCurrentSacrifices();
        if (toBePaidSeminpoints==1 || toBePaidSeminpoints==2) {
            // Here the sacrifice is always a card of the CPU and the target always a card of the player. That's why one doesn't have to check further things here.
            Game.isActCPUEff = true;
            if (Game.isSwitchingOnOncePerTurnRule) {
                EffectMonster.canStillUseOncePerTurnEffect=false;
            }
            cpuPaysCost(Sacrifice);
            YuGiOhJi.informationDialog("Computer wants to use its " + getNumberAsString(EffectMonster.sumMonsterNumber) + " monster, " + EffectMonster.Monster.monsterName + ", to banish the card " + Deck.getGY(true).getNthCardOfDeck(cardNumberInGY).cardName + " from your graveyard.", "Effect attempt");
            boolean isCanceling = askPlayerToNegateActivatedEffect(EffectMonster);
            if (!isCanceling) {
                YuGiOhJi.informationDialog("Computer uses its " + getNumberAsString(EffectMonster.sumMonsterNumber) + " monster, " + EffectMonster.Monster.monsterName + ", to banish the card " + Deck.getGY(true).getNthCardOfDeck(cardNumberInGY).cardName + " from your graveyard.", "Banishing");
                Deck.banishNthCardFromGY(cardNumberInGY, true);
            }
            Game.deactivateCurrentCPUEffects();
        }
    }
    // used to test, if the CPU can use the optional effect of Big Banisher
    public static boolean cpuCanUseEffectBigBanisher() {
        boolean hasWorkingBigBanisherEffect = hasWorkingMonsterEffect(false, Mon.BigBanisher.monsterId); // if CPU has Big Banisher effect, it can pay the cost, since Big Banisher can tribute itself
        int numberOfValidTargets = Deck.numberOfCardsInGY(true);
        return (hasWorkingBigBanisherEffect && numberOfValidTargets>=1);
    }
    public static boolean cpuCanUseEffectBigBanisher (SummonedMonster EffectMonster) {
        boolean hasWorkingBigBanisherEffect = hasWorkingMonsterEffect(EffectMonster, Mon.BigBanisher.monsterId); // if CPU has Big Banisher effect, it can pay the cost, since Big Banisher can tribute itself
        int numberOfValidTargets = Deck.numberOfCardsInGY(true);
        return (hasWorkingBigBanisherEffect && numberOfValidTargets>=1);
    }
    public static boolean cpuCanUseEffectBigBanisher (SummonedMonster EffectMonster, int cardNumberInGY) {
        return (cpuCanUseEffectBigBanisher(EffectMonster) && Deck.numberOfCardsInGY(true) >= cardNumberInGY);
    }
    
    
    // Effect ID = 13.1 (effect of Monster Stealer on summoned monster)
    public static void cpuEffectMonsterStealerOnMonster (SummonedMonster EffectMonster, SummonedMonster TargetMonster, AIsacrifice Sacrifice1, AIsacrifice Sacrifice2) {
        cpuEffectMonsterStealerOnMonster (EffectMonster, TargetMonster, Sacrifice1, Sacrifice2, new AIsacrifice(), new AIsacrifice());
    }
    //  same as before, but with 3 sacrifices
    public static void cpuEffectMonsterStealerOnMonster (SummonedMonster EffectMonster, SummonedMonster TargetMonster, AIsacrifice Sacrifice1, AIsacrifice Sacrifice2, AIsacrifice Sacrifice3) {
        cpuEffectMonsterStealerOnMonster (EffectMonster, TargetMonster, Sacrifice1, Sacrifice2, Sacrifice3, new AIsacrifice());
    }
    //  same as before, but with 4 sacrifices
    public static void cpuEffectMonsterStealerOnMonster (SummonedMonster EffectMonster, SummonedMonster TargetMonster, AIsacrifice Sacrifice1, AIsacrifice Sacrifice2, AIsacrifice Sacrifice3, AIsacrifice Sacrifice4) {
        if (worthInSemipointsOfAllCurrentSacrifices()==4) {
            // Here the sacrifice is always a card of the CPU and the target always a card of the player. That's why one doesn't have to check further things here.
            Game.isActCPUEff = true;
            if (Game.isSwitchingOnOncePerTurnRule) {
                EffectMonster.canStillUseOncePerTurnEffect=false;
            }
            cpuPaysCost(Sacrifice1, Sacrifice2, Sacrifice3, Sacrifice4);
            YuGiOhJi.informationDialog("Computer wants to use its " + getNumberAsString(EffectMonster.sumMonsterNumber) + " monster, " + EffectMonster.Monster.monsterName + ", to steal your " + getNumberAsString(TargetMonster.sumMonsterNumber) + " monster, " + TargetMonster.Monster.monsterName + ".", "Effect attempt");
            boolean isCanceling = askPlayerToNegateActivatedEffect(EffectMonster);
            if (!isCanceling) {
                YuGiOhJi.informationDialog("Computer uses its " + getNumberAsString(EffectMonster.sumMonsterNumber) + " monster, " + EffectMonster.Monster.monsterName + ", to steal your " + getNumberAsString(TargetMonster.sumMonsterNumber) + " monster, " + TargetMonster.Monster.monsterName + ".", "Stealing Monster");
                SummonedMonster.stealMonster(TargetMonster);
                EffectMonster.canStillAttackThisTurn=false;
            }
            Game.deactivateCurrentCPUEffects();
        }
    }
    // used to test, if the CPU can use the optional effect of Monster Stealer on (a specific) summoned monster
    public static boolean cpuCanUseEffectMonsterStealerOnMonster() {
        int payableSemipoints=CardOptions.countPayableCosts(false);
        boolean hasFreeMonsterZone = SummonedMonster.determineFreeMonsterZone(false).sumMonsterNumber>0;
        boolean hasWorkingMonsterStealEffect = hasWorkingMonsterEffect(false, Mon.MonsterStealer.monsterId);
        boolean hasValidTarget = SummonedMonster.hasNonImmuneMonsters(true);
        return ( payableSemipoints>=4 && hasFreeMonsterZone && hasWorkingMonsterStealEffect && hasValidTarget );
    }
    public static boolean cpuCanUseEffectMonsterStealerOnMonster (SummonedMonster EffectMonster) {
        int payableSemipoints=CardOptions.countPayableCosts(false);
        boolean hasFreeMonsterZone = SummonedMonster.determineFreeMonsterZone(false).sumMonsterNumber>0;
        boolean hasWorkingMonsterStealEffect = hasWorkingMonsterEffect(EffectMonster, Mon.MonsterStealer.monsterId);
        boolean hasValidTarget = SummonedMonster.hasNonImmuneMonsters(true);
        return ( payableSemipoints>=4 && hasFreeMonsterZone && hasWorkingMonsterStealEffect && hasValidTarget );
    }
    public static boolean cpuCanUseEffectMonsterStealerOnMonster (SummonedMonster EffectMonster, SummonedMonster TargetMonster) {
        int payableSemipoints=CardOptions.countPayableCosts(false);
        boolean hasFreeMonsterZone = SummonedMonster.determineFreeMonsterZone(false).sumMonsterNumber>0;
        boolean hasWorkingMonsterStealEffect = hasWorkingMonsterEffect(EffectMonster, Mon.MonsterStealer.monsterId);
        return ( payableSemipoints>=4 && hasFreeMonsterZone && hasWorkingMonsterStealEffect && !TargetMonster.isImmune() );
    }
    
    
    // Effect ID = 13.2 (effect of Monster Stealer on equip card)
    public static void cpuEffectMonsterStealerExecuteOnEquipCard (SummonedMonster EffectMonster, EStack Stack, int cardNumberInStack, boolean isInAttackMode, AIsacrifice Sacrifice1, AIsacrifice Sacrifice2) {
        cpuEffectMonsterStealerExecuteOnEquipCard(EffectMonster, Stack, cardNumberInStack, isInAttackMode, Sacrifice1, Sacrifice2, new AIsacrifice(), new AIsacrifice());
    }
    //  same as before, but with 3 sacrifices
    public static void cpuEffectMonsterStealerExecuteOnEquipCard (SummonedMonster EffectMonster, EStack Stack, int cardNumberInStack, boolean isInAttackMode, AIsacrifice Sacrifice1, AIsacrifice Sacrifice2, AIsacrifice Sacrifice3) {
        cpuEffectMonsterStealerExecuteOnEquipCard(EffectMonster, Stack, cardNumberInStack, isInAttackMode, Sacrifice1, Sacrifice2, Sacrifice3, new AIsacrifice());
    }
    //  same as before, but with 4 sacrifices
    public static void cpuEffectMonsterStealerExecuteOnEquipCard (SummonedMonster EffectMonster, EStack Stack, int cardNumberInStack, boolean isInAttackMode, AIsacrifice Sacrifice1, AIsacrifice Sacrifice2, AIsacrifice Sacrifice3, AIsacrifice Sacrifice4) {
        if (worthInSemipointsOfAllCurrentSacrifices()==4) {
            // Here the sacrifice is always a card of the CPU and the target always a card of the player. That's why one doesn't have to check further things here. (except for rare case the CPU tributes the monster equipped with the only valid target)
            Game.isActCPUEff = true;
            if (Game.isSwitchingOnOncePerTurnRule) {
                EffectMonster.canStillUseOncePerTurnEffect=false;
            }
            // One has to get a new cardNumberInStack before CPU pays cost, because one could have tributed from the same stack, thus changing the order of the cards.
            int cardIdInStack = Stack.getNthCardOfStack(cardNumberInStack).cardId;
            boolean isPlayersEquipCard = Stack.getControllerOfNthEquipCard(cardNumberInStack);
            boolean isNegated = Stack.getNegationStatusOfNthEquipCard(cardNumberInStack);
            cpuPaysCost(Sacrifice1, Sacrifice2, Sacrifice3, Sacrifice4);
            if (!SummonedMonster.hasStealableCards(true)) { // in case one tributed such, that the last valid target, was an equip card that has been put to the GY, because the it equipped a tributed monster
                YuGiOhJi.informationDialog("no more stealable cards", "Nothing happens.");
            }
            else {
                String possiblyNegated; // In general, it is important for the player to know, if the equip card is negated or not, because there might be the case that there are two cards with same IDs but different negation status. 
                if (Stack.getNegationStatusOfNthEquipCard(cardNumberInStack)) {possiblyNegated = " negated ";}
                else {possiblyNegated = " ";}
                String mode;
                if (isInAttackMode) {mode = "attack mode";}
                else {mode = "defence mode";}
                String pronoun;
                if (Stack.isBelongingToPlayer) {pronoun = "your ";}
                else {pronoun = "its ";}
                YuGiOhJi.informationDialog("Computer wants to use its " + getNumberAsString(EffectMonster.sumMonsterNumber) + " monster, " + EffectMonster.Monster.monsterName + ", to steal your" + possiblyNegated + "equip monster (" + Stack.getNthCardOfStack(cardNumberInStack).lowMonster.monsterName + ", " + pronoun + getNumberAsString(Stack.stackNumber) + " stack) and summon it in " + mode + ".", "Effect attempt");
                boolean isCanceling = askPlayerToNegateActivatedEffect(EffectMonster);
                if (!isCanceling) {
                    YuGiOhJi.informationDialog("Computer uses its " + getNumberAsString(EffectMonster.sumMonsterNumber) + " monster, " + EffectMonster.Monster.monsterName + ", to steal your" + possiblyNegated + "equip monster (" + Stack.getNthCardOfStack(cardNumberInStack).lowMonster.monsterName + ", " + pronoun + getNumberAsString(Stack.stackNumber) + " stack) and summon it in " + mode + ".", "Stealing Equip Monster");
                    int newCardNumberInStack = Stack.getPositionOfEquipCardByCardID(cardIdInStack, isPlayersEquipCard, isNegated);
                    SummonedMonster.stealEquipMonster(Stack, newCardNumberInStack, isInAttackMode);
                    EffectMonster.canStillAttackThisTurn=false;
                }
            }
            Game.deactivateCurrentCPUEffects();
        }
    }
    // used to test, if the CPU can use the optional effect of Monster Stealer on (a specific) equip monster
    public static boolean cpuCanUseEffectMonsterStealerOnEquipCard() {
        int payableSemipoints=CardOptions.countPayableCosts(false);
        boolean hasFreeMonsterZone = SummonedMonster.determineFreeMonsterZone(false).sumMonsterNumber>0;
        boolean hasWorkingMonsterStealEffect =  hasWorkingMonsterEffect(false, Mon.MonsterStealer.monsterId);
        int numberOfValidTargets = EStack.countOwnEquipMonsters(true, false);
        return ( payableSemipoints>=4 && hasFreeMonsterZone && hasWorkingMonsterStealEffect && numberOfValidTargets>=1 );
    }
    public static boolean cpuCanUseEffectMonsterStealerOnEquipCard (SummonedMonster EffectMonster) {
        int payableSemipoints=CardOptions.countPayableCosts(false);
        boolean hasFreeMonsterZone = SummonedMonster.determineFreeMonsterZone(false).sumMonsterNumber>0;
        boolean hasWorkingMonsterStealEffect = hasWorkingMonsterEffect(EffectMonster, Mon.MonsterStealer.monsterId);
        int numberOfValidTargets = EStack.countOwnEquipMonsters(true, false);
        return ( payableSemipoints>=4 && hasFreeMonsterZone && hasWorkingMonsterStealEffect && numberOfValidTargets>=1 );
    }
    public static boolean cpuCanUseEffectMonsterStealerOnEquipCard (SummonedMonster EffectMonster, EStack Stack, int cardNumberInStack) {
        int payableSemipoints=CardOptions.countPayableCosts(false);
        boolean hasFreeMonsterZone = SummonedMonster.determineFreeMonsterZone(false).sumMonsterNumber>0;
        boolean hasWorkingMonsterStealEffect = hasWorkingMonsterEffect(EffectMonster, Mon.MonsterStealer.monsterId);
        boolean isPlayersEquipCard = Stack.getControllerOfNthEquipCard(cardNumberInStack);
        return ( payableSemipoints>=4 && hasFreeMonsterZone && hasWorkingMonsterStealEffect && isPlayersEquipCard );
    }
    
    
    // Effect ID = 14.1 (effect of Eradicator on summoned monster)
    public static void cpuEffectEradicatorOnMonster (SummonedMonster EffectMonster, SummonedMonster TargetMonster, AIsacrifice Sacrifice) {
        cpuEffectEradicatorOnMonster(EffectMonster, TargetMonster, Sacrifice, new AIsacrifice());
    }
    // same as before, but with 2 sacrifices instead of one
    public static void cpuEffectEradicatorOnMonster (SummonedMonster EffectMonster, SummonedMonster TargetMonster, AIsacrifice Sacrifice1, AIsacrifice Sacrifice2) {
        if (AIsacrifice.sacrificeIsNotTargetMonster(Sacrifice1, Sacrifice2, TargetMonster) && TargetMonster.isPlayersMonster && worthInSemipointsOfAllCurrentSacrifices()==2) { // proceed only, if target is not tributed away and if target is player's monster
            Game.isActCPUEff = true;
            if (Game.isSwitchingOnOncePerTurnRule) {
                EffectMonster.canStillUseOncePerTurnEffect=false;
            }
            cpuPaysCost(Sacrifice1, Sacrifice2);
            YuGiOhJi.informationDialog("Computer wants to use its " + getNumberAsString(EffectMonster.sumMonsterNumber) + " monster, " + EffectMonster.Monster.monsterName + ", to destroy your " + getNumberAsString(TargetMonster.sumMonsterNumber) + " monster, " + TargetMonster.Monster.monsterName + ".", "Effect attempt");
            boolean isCanceling = askPlayerToNegateActivatedEffect(EffectMonster);
            if (!isCanceling) {
                YuGiOhJi.informationDialog("Computer uses its " + getNumberAsString(EffectMonster.sumMonsterNumber) + " monster, " + EffectMonster.Monster.monsterName + ", to destroy your " + getNumberAsString(TargetMonster.sumMonsterNumber) + " monster, " + TargetMonster.Monster.monsterName + ".", "Destruction");
                if (TargetMonster.hasToRevealDiamondSword()) { // here player has to reveal that it is Diamond Sword, in order to prove, that the monster is not destroyed
                    YMonster.revealDiamondSword(TargetMonster); // effect basically ends here in this case
                }
                else {
                    TargetMonster.killMonster();
                    if (YMonster.getMonsterById(TargetMonster.effectiveMonsterId()).isBurning) { // in case Eradicator killed Napalm, inflict burn damage to the owner of Eradicator (not the original owner, also not the owner of the destroyed monster)
                        YMonster.dealBurnDamage(Mon.Napalm.NapalmBurnDamage(), EffectMonster.isPlayersMonster);
                    }
                }
            }
            Game.deactivateCurrentCPUEffects();
        }
    }
    // used to test, if the CPU can use the optional effect of Eradicator on (a specific) summoned monster
    public static boolean cpuCanUseEffectEradicatorOnMonster() {
        int payableSemipoints=CardOptions.countPayableCosts(false);
        boolean hasWorkingEradicatorEffect = hasWorkingMonsterEffect(false, Mon.Eradicator.monsterId);
        boolean hasValidTarget = SummonedMonster.hasCardsDestructibleByEffect(true);
        return ( payableSemipoints>=2 && hasWorkingEradicatorEffect && hasValidTarget );
    }
    public static boolean cpuCanUseEffectEradicatorOnMonster (SummonedMonster EffectMonster) {
        int payableSemipoints=CardOptions.countPayableCosts(false);
        boolean hasWorkingEradicatorEffect = hasWorkingMonsterEffect(EffectMonster, Mon.Eradicator.monsterId);
        boolean hasValidTarget = SummonedMonster.hasCardsDestructibleByEffect(true);
        return ( payableSemipoints>=2 && hasWorkingEradicatorEffect && hasValidTarget );
    }
    public static boolean cpuCanUseEffectEradicatorOnMonster (SummonedMonster EffectMonster, SummonedMonster TargetMonster) {
        int payableSemipoints=CardOptions.countPayableCosts(false);
        boolean hasWorkingEradicatorEffect = hasWorkingMonsterEffect(EffectMonster, Mon.Eradicator.monsterId);
        return ( payableSemipoints>=2 && hasWorkingEradicatorEffect && TargetMonster.canBeDestroyedByEffect() );
    }
    
    
    // Effect ID = 14.2 (effect of Eradicator on equip card)
    public static void cpuEffectEradicatorOnEquipCard (SummonedMonster EffectMonster, EStack Stack, int cardNumberInStack, AIsacrifice Sacrifice) {
        cpuEffectEradicatorOnEquipCard(EffectMonster, Stack, cardNumberInStack, Sacrifice, new AIsacrifice());
    }
    // same as before, but with 2 sacrifices instead of one
    public static void cpuEffectEradicatorOnEquipCard (SummonedMonster EffectMonster, EStack Stack, int cardNumberInStack, AIsacrifice Sacrifice1, AIsacrifice Sacrifice2) {
        if (AIsacrifice.sacrificeIsNotTargetedEquipCard(Sacrifice1, Sacrifice2, Stack, cardNumberInStack) && AIsacrifice.sacrificeIsNotEquippedWithTarget(Sacrifice1, Sacrifice2, Stack) && worthInSemipointsOfAllCurrentSacrifices()==2) { // proceed only, if target is not tributed away
            Game.isActCPUEff = true;
            if (Game.isSwitchingOnOncePerTurnRule) {
                EffectMonster.canStillUseOncePerTurnEffect=false;
            }
            // One has to get a new cardNumberInStack before CPU pays cost, because one could have tributed from the same stack, thus changing the order of the cards.
            int cardIdInStack = Stack.getNthCardOfStack(cardNumberInStack).cardId;
            boolean isPlayersEquipCard = Stack.getControllerOfNthEquipCard(cardNumberInStack);
            boolean isNegated = Stack.getNegationStatusOfNthEquipCard(cardNumberInStack);
            cpuPaysCost(Sacrifice1, Sacrifice2);
            if (!SummonedMonster.hasCardsDestructibleByEffect(true)) { // in case one tributed such, that the last valid target, was an equip card that has been put to the GY, because the it equipped a tributed monster
                YuGiOhJi.informationDialog("no more destructible cards", "Nothing happens.");
            }
            else {
                String pronounEquipCard;
                if (isPlayersEquipCard) {pronounEquipCard = "your";}
                else {pronounEquipCard = "its";}
                String possiblyNegated; // In general, it is important for the player to know, if the equip card is negated or not, because there might be the case that there are two cards with same IDs but different negation status. 
                if (Stack.getNegationStatusOfNthEquipCard(cardNumberInStack)) {possiblyNegated = " negated ";}
                else {possiblyNegated = " ";}
                String pronounMonster;
                if (Stack.isBelongingToPlayer) {pronounMonster = "your ";}
                else {pronounMonster = "its ";}
                YuGiOhJi.informationDialog("Computer wants to use its " + getNumberAsString(EffectMonster.sumMonsterNumber) + " monster, " + EffectMonster.Monster.monsterName + ", to destroy " + pronounEquipCard + possiblyNegated + "equip card (" + Stack.getNthCardOfStack(cardNumberInStack).lowMonster.monsterName + ", in equip stack of " + pronounMonster + getNumberAsString(Stack.stackNumber) + " monster).", "Effect attempt");
                boolean isCanceling = askPlayerToNegateActivatedEffect(EffectMonster);
                if (!isCanceling) {
                    YuGiOhJi.informationDialog("Computer uses its " + getNumberAsString(EffectMonster.sumMonsterNumber) + " monster, " + EffectMonster.Monster.monsterName + ", to destroy " + pronounEquipCard + possiblyNegated + "equip card (" + Stack.getNthCardOfStack(cardNumberInStack).lowMonster.monsterName + ", in equip stack of " + pronounMonster + getNumberAsString(Stack.stackNumber) + " monster).", "Destruction");
                    int newCardNumberInStack = Stack.getPositionOfEquipCardByCardID(cardIdInStack, isPlayersEquipCard, isNegated);
                    Stack.sendNthEquipCardToGY(newCardNumberInStack);
                }
            }
            Game.deactivateCurrentCPUEffects();
        }
    }
    // used to test, if the CPU can use the optional effect of Eradicator on an equip card
    // (the analog for a specific equip card not needed, because always destructible, if can use effect of Eradicator at all)
    public static boolean cpuCanUseEffectEradicatorOnEquipCard() {
        int payableSemipoints=CardOptions.countPayableCosts(false);
        boolean hasWorkingEradicatorEffect = hasWorkingMonsterEffect(false, Mon.Eradicator.monsterId);
        int numberOfValidTargets = EStack.countOwnEquipMonsters(true, false);
        return ( payableSemipoints>=2 && hasWorkingEradicatorEffect && numberOfValidTargets>=1 );
    }
    public static boolean cpuCanUseEffectEradicatorOnEquipCard (SummonedMonster EffectMonster) {
        int payableSemipoints=CardOptions.countPayableCosts(false);
        boolean hasWorkingEradicatorEffect = hasWorkingMonsterEffect(EffectMonster, Mon.Eradicator.monsterId);
        int numberOfValidTargets = EStack.countOwnEquipMonsters(true, false);
        return ( payableSemipoints>=2 && hasWorkingEradicatorEffect && numberOfValidTargets>=1 );
    }
    
    
    // Effect ID = 15.1 (optionl on field effect of Neutraliser negating a summoned monster)
    // with boolean argument set to false, one can also use this method for interrupting effects of CPU during players turn
    public static void cpuEffectNeutraliserOptionalOnMonster (boolean isAskingPlayerToInterfere, SummonedMonster NegatingMonster, SummonedMonster TargetMonster, AIsacrifice Sacrifice) {
        cpuEffectNeutraliserOptionalOnMonster(isAskingPlayerToInterfere, NegatingMonster, TargetMonster, Sacrifice, new AIsacrifice());
    }
    // same as before, but with 2 sacrifices instead of one
    public static void cpuEffectNeutraliserOptionalOnMonster (boolean isAskingPlayerToInterfere, SummonedMonster NegatingMonster, SummonedMonster TargetMonster, AIsacrifice Sacrifice1, AIsacrifice Sacrifice2) {
        if (AIsacrifice.sacrificeIsNotTargetMonster(Sacrifice1, Sacrifice2, TargetMonster) && worthInSemipointsOfAllCurrentSacrifices()==2) { // proceed only, if target is not tributed away
            Game.isActCPUEff = true;
            if (Game.isSwitchingOnOncePerTurnRule) {
                NegatingMonster.canStillUseOncePerTurnEffect=false;
            }
            cpuPaysCost(Sacrifice1, Sacrifice2);
            if (!CardOptions.thereAreNegatableCards()) { // in case one tributed all valid targets
                YuGiOhJi.informationDialog("no more negatable cards", "Nothing happens.");
            }
            else {
                String pronoun;
                if (TargetMonster.isPlayersMonster) {pronoun = "your ";}
                else {pronoun = "its ";}
                YuGiOhJi.informationDialog("Computer wants to use its " + getNumberAsString(NegatingMonster.sumMonsterNumber) + " monster, " + NegatingMonster.Monster.monsterName + ", to negate " + pronoun + getNumberAsString(TargetMonster.sumMonsterNumber) + " monster, " + TargetMonster.Monster.monsterName + ".", "Effect attempt");
                if (isAskingPlayerToInterfere) {
                    boolean isCanceling = askPlayerToNegateActivatedEffect(NegatingMonster);
                    if (!isCanceling) {
                        YuGiOhJi.informationDialog("Computer uses its " + getNumberAsString(NegatingMonster.sumMonsterNumber) + " monster, " + NegatingMonster.Monster.monsterName + ", to negate " + pronoun + getNumberAsString(TargetMonster.sumMonsterNumber) + " monster, " + TargetMonster.Monster.monsterName + ".", "Effect negation");
                        TargetMonster.isNotAbleToUseItsEffects=true;
                    }
                }
                else {
                    YuGiOhJi.informationDialog("Computer uses its " + getNumberAsString(NegatingMonster.sumMonsterNumber) + " monster, " + NegatingMonster.Monster.monsterName + ", to negate " + pronoun + getNumberAsString(TargetMonster.sumMonsterNumber) + " monster, " + TargetMonster.Monster.monsterName + ".", "Effect negation");
                    TargetMonster.isNotAbleToUseItsEffects=true;
                }
            }
            Game.deactivateCurrentCPUEffects();
        }
    }
    // used to test, if the CPU can use the optional effect of Neutraliser on summoned monster
    public static boolean cpuCanUseEffectNeutraliserOptionalOnMonster() {
        return SummonedMonster.canUseEffectNegateOnField(false);
    }
    
    
    // Effect ID = 15.2 (optionl on field effect of Neutraliser negating an equip card)
    // with boolean argument set to false, one can also use this method for interrupting effects of CPU during players turn
    public static void cpuEffectNeutraliserOptionalOnEquipCard (boolean isAskingPlayerToInterfere, SummonedMonster NegatingMonster, EStack Stack, int cardNumberInStack, AIsacrifice Sacrifice) {
        cpuEffectNeutraliserOptionalOnEquipCard(isAskingPlayerToInterfere, NegatingMonster, Stack, cardNumberInStack, Sacrifice, new AIsacrifice());
    }
    // same as before, but with 2 sacrifices instead of one
    public static void cpuEffectNeutraliserOptionalOnEquipCard (boolean isAskingPlayerToInterfere, SummonedMonster NegatingMonster, EStack Stack, int cardNumberInStack, AIsacrifice Sacrifice1, AIsacrifice Sacrifice2) {
        if (AIsacrifice.sacrificeIsNotTargetedEquipCard(Sacrifice1, Sacrifice2, Stack, cardNumberInStack) && AIsacrifice.sacrificeIsNotEquippedWithTarget(Sacrifice1, Sacrifice2, Stack) && worthInSemipointsOfAllCurrentSacrifices()==2) { // proceed only, if target is not tributed away
            Game.isActCPUEff = true;
            if (Game.isSwitchingOnOncePerTurnRule) {
                NegatingMonster.canStillUseOncePerTurnEffect=false;
            }
            // One has to get a new cardNumberInStack before CPU pays cost, because one could have tributed from the same stack, thus changing the order of the cards.
            int cardIdInStack = Stack.getNthCardOfStack(cardNumberInStack).cardId;
            boolean isPlayersEquipCard = Stack.getControllerOfNthEquipCard(cardNumberInStack);
            cpuPaysCost(Sacrifice1, Sacrifice2);
            if (!CardOptions.thereAreNegatableCards()) { // in case one tributed all valid targets
                YuGiOhJi.informationDialog("no more negatable cards", "Nothing happens.");
            }
            else {
                String pronoun;
                if (Stack.isBelongingToPlayer) {pronoun = "your ";}
                else {pronoun = "its ";}
                YuGiOhJi.informationDialog("Computer wants to use its " + getNumberAsString(NegatingMonster.sumMonsterNumber) + " monster, " + NegatingMonster.Monster.monsterName + ", to negate the equip card " + Stack.getNthCardOfStack(cardNumberInStack).lowMonster.monsterName + ", in the equip stack of " + pronoun + getNumberAsString(Stack.stackNumber) + " monster.", "Effect attempt");
                if (isAskingPlayerToInterfere) {
                    boolean isCanceling = askPlayerToNegateActivatedEffect(NegatingMonster);
                    if (!isCanceling) {
                        YuGiOhJi.informationDialog("Computer uses its " + getNumberAsString(NegatingMonster.sumMonsterNumber) + " monster, " + NegatingMonster.Monster.monsterName + ", to negate the equip card " + Stack.getNthCardOfStack(cardNumberInStack).lowMonster.monsterName + ", in the equip stack of " + pronoun + getNumberAsString(Stack.stackNumber) + " monster.", "Effect negation");
                        int newCardNumberInStack = Stack.getPositionOfEquipCardByCardID(cardIdInStack, isPlayersEquipCard, false);
                        YMonster.negateEquipCard(Stack, newCardNumberInStack, false, false);
                    }
                }
                else {
                    YuGiOhJi.informationDialog("Computer uses its " + getNumberAsString(NegatingMonster.sumMonsterNumber) + " monster, " + NegatingMonster.Monster.monsterName + ", to negate the equip card " + Stack.getNthCardOfStack(cardNumberInStack).lowMonster.monsterName + ", in the equip stack of " + pronoun + getNumberAsString(Stack.stackNumber) + " monster.", "Effect negation");
                    int newCardNumberInStack = Stack.getPositionOfEquipCardByCardID(cardIdInStack, isPlayersEquipCard, false);
                    YMonster.negateEquipCard(Stack, newCardNumberInStack, false, false);
                }
            }
            Game.deactivateCurrentCPUEffects();
        }
    }
    // used to test, if the CPU can use the optional effect of Neutraliser on equip cards
    public static boolean cpuCanUseEffectNeutraliserOptionalOnEquipCard() {
        int payableSemipoints=CardOptions.countPayableCosts(false);
        int numberOfValidTargets=EStack.countOwnEquipMonsters(false, true)+EStack.countOwnEquipMonsters(true, true);
        return ( hasWorkingMonsterEffect(false, 5) && payableSemipoints>=2 && numberOfValidTargets>=1 );
    }
    public static boolean cpuCanUseEffectNeutraliserOptionalOnEquipCard (SummonedMonster EffectMonster) {
        int payableSemipoints=CardOptions.countPayableCosts(false);
        int numberOfValidTargets=EStack.countOwnEquipMonsters(false, true)+EStack.countOwnEquipMonsters(true, true);
        return ( hasWorkingMonsterEffect(EffectMonster, 5) && payableSemipoints>=2 && numberOfValidTargets>=1 );
    }
    
    // -- EQUIP EFFECTS --
    
    // Effect ID = 16
    public static void cpuEquipEffectFromMonster (SummonedMonster EquipMonster, SummonedMonster TargetMonster) {
        if (!EquipMonster.isBasicallySameMonster(TargetMonster)) { // only proceed, if the target is another monster than the equip monster
            Game.isActCPUEff = true; // needed here for effect negation by player
            if (Game.isSwitchingOnOncePerTurnRule) {
                TargetMonster.canStillUseOncePerTurnEffect=false;
            }
            String pronoun;
            String monsterName="";
            if (TargetMonster.isPlayersMonster) {pronoun = "your ";}
            else {
                pronoun = "its ";
                if (TargetMonster.isKnownToPlayer()) {monsterName = ", " + TargetMonster.Monster.monsterName + ",";}
            }
            YuGiOhJi.informationDialog("Computer wants to equip " + pronoun + getNumberAsString(TargetMonster.sumMonsterNumber) + " monster" + monsterName + " with its " + getNumberAsString(EquipMonster.sumMonsterNumber) + " monster, " + EquipMonster.Monster.monsterName + ".", "Effect attempt");
            boolean isCanceling = askPlayerToNegateActivatedEffect(EquipMonster);
            if (!isCanceling) {
                YuGiOhJi.informationDialog("Computer equips " + pronoun + getNumberAsString(TargetMonster.sumMonsterNumber) + " monster" + monsterName + " with its " + getNumberAsString(EquipMonster.sumMonsterNumber) + " monster, " + EquipMonster.Monster.monsterName + ".", "Equipping");
                EStack.equipMonster(TargetMonster, EquipMonster.Card, false, EquipMonster.isOriginallyPlayersMonster);
                SummonedMonster.getNthSummonedMonster(EquipMonster.sumMonsterNumber, EquipMonster.isPlayersMonster).deleteMonster();
                EStack Stack = EStack.getNthStack(TargetMonster.sumMonsterNumber, TargetMonster.isPlayersMonster);
                // ask player to negate the equip card
                askPlayerToNegateActivatedEffect(Stack, Stack.numberOfCards, true);
            }
            Game.deactivateCurrentCPUEffects();
        }
    }
    // used to test, if the CPU can use a very specific equip effect from monster
    public static boolean cpuCanUseEquipEffectFromMonster() {
        SummonedMonster SumMonster;
        for (int index = 1; index <= 5; index++){ // check own monsters
            SumMonster=SummonedMonster.getNthSummonedMonster(index, false);
            if (cpuCanUseEquipEffectFromMonster(SumMonster)) {
                return true;
            }
        }
        return false;
    }
    public static boolean cpuCanUseEquipEffectFromMonster (SummonedMonster EquipMonster) {
        return EquipMonster.canUseEquipEffectFromMonster();
    }
    public static boolean cpuCanUseEquipEffectFromMonster (SummonedMonster EquipMonster, SummonedMonster TargetMonster) {
        boolean canUseEquipEffect = EquipMonster.canUseEquipEffectFromMonster();
        boolean isValidTarget = TargetMonster.canBeEquippedBy(EquipMonster.Monster);
        return (canUseEquipEffect && isValidTarget);
    }
    
    // Effect ID = 17
    public static void cpuEquipEffectFromStack (EStack Stack, int cardIdInStack, SummonedMonster TargetMonster) {
        int cardNumberInStack = Stack.getPositionOfEquipCardByCardID(cardIdInStack, false, false);
        if (cardNumberInStack!=0 && !Stack.stackIsBelongingToMonster(TargetMonster)) {
            Game.isActCPUEff = true; // needed here for effect negation by player
            YCard Card = Stack.getNthCardOfStack(cardNumberInStack);
            String pronounTarget;
            String monsterName="";
            if (TargetMonster.isPlayersMonster) {pronounTarget = "your ";}
            else {
                pronounTarget = "its ";
                if (TargetMonster.isKnownToPlayer()) {monsterName = ", " + TargetMonster.Monster.monsterName + ",";}
            }
            String pronounStack;
            if (Stack.isBelongingToPlayer) {pronounStack = "your ";}
            else {pronounStack = "its ";}
            YuGiOhJi.informationDialog("Computer wants to equip " + pronounTarget + getNumberAsString(TargetMonster.sumMonsterNumber) + " monster" + monsterName + " with its equip monster " + Card.lowMonster.monsterName + " from " + pronounStack + getNumberAsString(Stack.stackNumber) + " equip stack.", "Effect attempt");
            boolean isCanceling = askPlayerToNegateActivatedEffect(Stack, cardNumberInStack, false); // ask player to negate the equip card before equipping
            if (!isCanceling) {
                YuGiOhJi.informationDialog("Computer equips " + pronounTarget + getNumberAsString(TargetMonster.sumMonsterNumber) + " monster" + monsterName + " with its equip monster " + Card.lowMonster.monsterName + " from " + pronounStack + getNumberAsString(Stack.stackNumber) + " equip stack.", "Equipping");
                EStack.equipMonster(TargetMonster, Stack.getNthCardOfStack(cardNumberInStack), false, Stack.getOriginalOwnerOfNthEquipCard(cardNumberInStack));
                EStack.getNthStack(Stack.stackNumber, Stack.isBelongingToPlayer).deleteNthCardInEquipStackAndRearrange(cardNumberInStack);
                EStack NewStack = EStack.getNthStack(TargetMonster.sumMonsterNumber, TargetMonster.isPlayersMonster);
                askPlayerToNegateActivatedEffect(NewStack, NewStack.numberOfCards, true); // ask player to negate the equip card after equipping
            }
            Game.deactivateCurrentCPUEffects();
        }
    }
    // used to test, if the CPU can use a very specific equip effect from stack,
    // or if CPU can use equip effects from hand in general, when nothing entered
    // (in case of Bugged Upgrade the monster has to be negatable (an effect monster that has its effects not negated yet))
    public static boolean cpuCanUseEquipEffectFromStack (EStack Stack, int cardId, SummonedMonster TargetMonster) { // 
        return (cpuCanUseEquipEffectFromStack(Stack, cardId) && TargetMonster.isEquippable() && !Stack.stackIsBelongingToMonster(TargetMonster) && !(YuGiOhJi.AttackStopperBuggedUpgrade.cardId==cardId && !TargetMonster.canBeNegated()) );
    }
    public static boolean cpuCanUseEquipEffectFromStack (EStack Stack, int cardId) {
        int cardNumberInStack = Stack.getPositionOfEquipCardByCardID(cardId, false, false);
        boolean hasValidTarget = false;
        if (cardNumberInStack!=0) {
            hasValidTarget = Stack.canUseEquipEffectFromStack(cardNumberInStack);
        }
        return (hasValidTarget);
    }
    public static boolean cpuCanUseEquipEffectFromStack() {
        SummonedMonster SumMonster;
        for (int index = 1; index <= 5; index++){ // check own monsters
            SumMonster=SummonedMonster.getNthSummonedMonster(index, false);
            if (SumMonster.isExisting) {
                EStack Stack = EStack.getNthStack(index, false);
                if (Stack.numberOfCards>0) {
                    for (int n = Stack.numberOfCards; n >= 1; n--){
                        if (Stack.canUseEquipEffectFromStack(n)) {
                            return true;
                        }
                    }
                }
            } // check summoned monsters of opponent
            SumMonster=SummonedMonster.getNthSummonedMonster(index, true);
            if (SumMonster.isExisting) {
                EStack Stack = EStack.getNthStack(index, true);
                if (Stack.numberOfCards>0) {
                    for (int n = Stack.numberOfCards; n >= 1; n--){
                        if (Stack.canUseEquipEffectFromStack(n)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    // Effect ID = 18
    public static void cpuEquipEffectFromHand (SummonedMonster TargetMonster, int cardId) {
        // effects from hand can not be negated ...
        int cardNumber = HandCPU.getPositionOfCardWithCardId(cardId);
        YCard Card = HandCPU.getNthCardOfHand(cardNumber);
        String pronoun;
        String monsterName="";
        if (TargetMonster.isPlayersMonster) {pronoun = "your ";}
        else {
            pronoun = "its ";
            if (TargetMonster.isKnownToPlayer()) {monsterName = ", " + TargetMonster.Monster.monsterName + ",";}
        }
        YuGiOhJi.informationDialog("Computer equips " + pronoun + getNumberAsString(TargetMonster.sumMonsterNumber) + " monster" + monsterName + " with its equip monster " + Card.lowMonster.monsterName + " from the hand.", "Equipping");
        EStack.equipMonster(TargetMonster, Card, false, false);
        HandCPU.deleteNthCardOnHandAndRearrange(cardNumber, false);
        EStack Stack = EStack.getNthStack(TargetMonster.sumMonsterNumber, TargetMonster.isPlayersMonster);
        // ... however, as soon as the equip card equips a monster, the equip card can be negated
        Game.isActCPUEff = true; // needed here for effect negation by player
        askPlayerToNegateActivatedEffect(Stack, Stack.numberOfCards, true);
        Game.deactivateCurrentCPUEffects();
    }
    // used to test, if the CPU can use a very specific equip effect from hand,
    // or if CPU can use equip effects from hand in general, when nothing entered
    // (in case of Bugged Upgrade the monster has to be negatable (an effect monster that has its effects not negated yet))
    public static boolean cpuCanUseEquipEffectFromHand (SummonedMonster TargetMonster, int cardId) { // 
        return (cpuCanUseEquipEffectFromHand(cardId) && TargetMonster.isEquippable() && !(YuGiOhJi.AttackStopperBuggedUpgrade.cardId==cardId && !TargetMonster.canBeNegated()) );
    }
    public static boolean cpuCanUseEquipEffectFromHand (int cardId) {
        if (Hand.hasCardWithCardIdOnHand(false, cardId)) {
            YCard EquipCard = HandCPU.getCardOnHandByCardID(cardId);
            boolean isEquipCard = EquipCard.hasEquipEffect;
            boolean hasValidTarget = CardOptions.canUseEquipEffectFromHand(EquipCard);
            return (isEquipCard && hasValidTarget);
        }
        return false;
    }
    public static boolean cpuCanUseEquipEffectFromHand() {
        if (HandCPU.numberOfCards>0) {
            for (int index = HandCPU.numberOfCards; index >= 1; index--){
                YCard EquipCard = HandCPU.getNthCardOfHand(index);
                if (EquipCard.hasEquipEffect && CardOptions.canUseEquipEffectFromHand(EquipCard)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    // --- SUMMONING EFFECTS ---
    // they can not be negated (that means one only needs one method: no activation of effects, just do it)
    
    // -- SPECIAL SUMMONING EFFECTS of CPU --
    
    // Effect ID = 19.1
    public static void cpuSpecialSummonsMidbossFromHand (boolean isInAttackMode, int cardId, boolean isConcerningUpperMonster, AIsacrifice Sacrifice) {
        cpuSpecialSummonsMidbossFromHand(isInAttackMode, cardId, isConcerningUpperMonster, Sacrifice, new AIsacrifice());
    }
    // same as before, but with 2 sacrifices instead of one
    public static void cpuSpecialSummonsMidbossFromHand (boolean isInAttackMode, int cardId, boolean isConcerningUpperMonster, AIsacrifice Sacrifice1, AIsacrifice Sacrifice2) {
        if (worthInSemipointsOfAllCurrentSacrifices()==2) {
            cpuPaysCost(Sacrifice1, Sacrifice2);
            SummonedMonster TargetMonster = SummonedMonster.determineFreeMonsterZone(false);
            YCard Card = HandCPU.getNthCardOfHand(HandCPU.getPositionOfCardWithCardId(cardId));
            if (isConcerningUpperMonster) {YuGiOhJi.informationDialog("Computer special summons the MIDBOSS " + Card.upMonster.monsterName + " from hand.", "Summoning");}
            else {YuGiOhJi.informationDialog("Computer special summons the MIDBOSS " + Card.lowMonster.monsterName + " from hand.", "Summoning");}
            YCard.setCardToNthSumMonster(Card, isConcerningUpperMonster, false, isInAttackMode, TargetMonster.sumMonsterNumber, false, false);
            Game.deactivateCurrentCPUEffects();
        }
    }
    public static boolean cpuCanSpecialSummonMidbossFromHand() { // looks if cpu could special summon a MIDBOSS from hand, if there was one
        return (CardOptions.countPayableCosts(false)>=4); // has to be able to pay 2 semi-points + the MIDBOSS (on hand) one wants to summon
    }
    
    // Effect ID = 19.2
    public static void cpuSpecialSummonsEndbossFromHand (boolean isInAttackMode, int cardId, boolean isConcerningUpperMonster, AIsacrifice Sacrifice1, AIsacrifice Sacrifice2) {
        cpuSpecialSummonsEndbossFromHand(isInAttackMode, cardId, isConcerningUpperMonster, Sacrifice1, Sacrifice2, new AIsacrifice(), new AIsacrifice());
    }
    // same as before, but with 3 sacrifices instead of 2
    public static void cpuSpecialSummonsEndbossFromHand (boolean isInAttackMode, int cardId, boolean isConcerningUpperMonster, AIsacrifice Sacrifice1, AIsacrifice Sacrifice2, AIsacrifice Sacrifice3) {
        cpuSpecialSummonsEndbossFromHand(isInAttackMode, cardId, isConcerningUpperMonster, Sacrifice1, Sacrifice2, Sacrifice3, new AIsacrifice());
    }
    // same as before, but with 4 sacrifices
    public static void cpuSpecialSummonsEndbossFromHand (boolean isInAttackMode, int cardId, boolean isConcerningUpperMonster, AIsacrifice Sacrifice1, AIsacrifice Sacrifice2, AIsacrifice Sacrifice3, AIsacrifice Sacrifice4) {
        if (worthInSemipointsOfAllCurrentSacrifices()==4) {
            cpuPaysCost(Sacrifice1, Sacrifice2, Sacrifice3, Sacrifice4);
            SummonedMonster TargetMonster = SummonedMonster.determineFreeMonsterZone(false);
            YCard Card = HandCPU.getNthCardOfHand(HandCPU.getPositionOfCardWithCardId(cardId));
            YuGiOhJi.informationDialog("Computer special summons the ENDBOSS " + Card.upMonster.monsterName + " from hand.", "Summoning");
            YCard.setCardToNthSumMonster(Card, isConcerningUpperMonster, false, isInAttackMode, TargetMonster.sumMonsterNumber, false, false);
            Game.deactivateCurrentCPUEffects();
        }
    }
    public static boolean cpuCanSpecialSummonEndbossFromHand() { // looks if cpu could special summon an ENDBOSS from hand, if there was one
        return (CardOptions.countPayableCosts(false)>=6); // has to be able to pay 4 semi-points + the ENDBOSS (on hand) one wants to summon
    }
    
    // Effect ID = 20 (from GY only endbosses)
    public static void cpuSpecialSummonsFromGY (boolean isInAttackMode, int cardId, AIsacrifice Sacrifice1, AIsacrifice Sacrifice2) {
        cpuSpecialSummonsFromGY(isInAttackMode, cardId, Sacrifice1, Sacrifice2, new AIsacrifice(), new AIsacrifice());
    }
    // same as before, but with 3 sacrifices instead of 2
    public static void cpuSpecialSummonsFromGY (boolean isInAttackMode, int cardId, AIsacrifice Sacrifice1, AIsacrifice Sacrifice2, AIsacrifice Sacrifice3) {
        cpuSpecialSummonsFromGY(isInAttackMode, cardId, Sacrifice1, Sacrifice2, Sacrifice3, new AIsacrifice());
    }
    // same as before, but with 4 sacrifices
    public static void cpuSpecialSummonsFromGY (boolean isInAttackMode, int cardId, AIsacrifice Sacrifice1, AIsacrifice Sacrifice2, AIsacrifice Sacrifice3, AIsacrifice Sacrifice4) {
        if (worthInSemipointsOfAllCurrentSacrifices()==4) {
            Game.isActCPUEff = true;
            cpuPaysCost(Sacrifice1, Sacrifice2, Sacrifice3, Sacrifice4);
            int cardNumberInGY = GYDeckCPU.getPositionOfCardWithCardIdInDeck(cardId);
            YCard Card = GYDeckCPU.getNthCardOfDeck(cardNumberInGY);
            YuGiOhJi.informationDialog("Computer wants to special summon the ENDBOSS " + Card.upMonster.monsterName + " from GY by its own effect.", "Effect attempt");
            boolean isCanceling = YMonster.askPlayerToNegateSpecSumFromGY(cardNumberInGY);
            if (!isCanceling) {
                SummonedMonster TargetMonster = SummonedMonster.determineFreeMonsterZone(false);
                YuGiOhJi.informationDialog("Computer special summons the ENDBOSS " + Card.upMonster.monsterName + " from GY by its own effect.", "Summoning");
                YCard.setCardToNthSumMonster(Card, true, false, isInAttackMode, TargetMonster.sumMonsterNumber, true, false);
            }
            Game.deactivateCurrentCPUEffects();
        }
    }
    public static boolean cpuCanSpecialSummonEndbossFromGY() { // looks if cpu could special summon a ENDBOSS from GY, if there was one
        return (CardOptions.countPayableCosts(false)>=5 && !Game.isSwitchingOnAntiDragonRulerRule); // has to be able to pay 4 semi-points + the MIDBOSS (in GY) one wants to revive in the GY
    }
    
    // -- TRIBUTE SUMMONING EFFECTS of CPU --
    // for completeness also use simple methods for normal summoning/setting MOOKS
    // only summoned monsters can be sacrificed here as cost
    
    public static boolean cpuCanNormalSummonMonsterWithStars (int stars) { // looks if cpu could normal/tribute summon/set a monster with a given number of stars
        switch (stars) {
            case 1: return cpuCanNormalSummonMook();
            case 2: return cpuCanNormalSummonMidboss();
            case 3: return cpuCanNormalSummonEndboss();
            case 4: return cpuCanNormalSummonGod();
            default: YuGiOhJi.debugDialog("Error: out of bounds in cpuCanNormalSummonMonsterWithStars(...); attempted number of stars: " + stars); return false;
        }
    }
    
    public static void cpuNormalSummonsMookWithCardId (int cardId) {
        SummonedMonster TargetMonster = SummonedMonster.determineFreeMonsterZone(false);
        if (TargetMonster.sumMonsterNumber!=0) {
            YCard Card = HandCPU.getNthCardOfHand(HandCPU.getPositionOfCardWithCardId(cardId));
            YuGiOhJi.informationDialog("Computer normal summons " + Card.lowMonster.monsterName + ".", "Summoning");
            YCard.setCardToNthSumMonster(Card, false, false, true, TargetMonster.sumMonsterNumber, false, false);
            Game.hasStillNormalSummonCPU=false;
        }
    }
    public static void cpuNormalSummonsMookWithCardNo (int cardNo) {
        SummonedMonster TargetMonster = SummonedMonster.determineFreeMonsterZone(false);
        if (TargetMonster.sumMonsterNumber!=0) {
            YCard Card = HandCPU.getNthCardOfHand(cardNo);
            YuGiOhJi.informationDialog("Computer normal summons " + Card.lowMonster.monsterName + ".", "Summoning");
            YCard.setCardToNthSumMonster(Card, false, false, true, TargetMonster.sumMonsterNumber, false, false);
            Game.hasStillNormalSummonCPU=false;
        }
    }
    // looks if cpu could normal summon/set a MOOK, if it had one on the hand
    public static boolean cpuCanNormalSummonMook() {
        return CardOptions.canNormalSummonMonsterWithStars(1, false);
    }
    
    public static void cpuNormalSetsMookWithCardId (int cardId) {
        SummonedMonster TargetMonster = SummonedMonster.determineFreeMonsterZone(false);
        if (TargetMonster.sumMonsterNumber!=0) {
            YCard Card = HandCPU.getNthCardOfHand(HandCPU.getPositionOfCardWithCardId(cardId));
            YuGiOhJi.informationDialog("Computer normal sets a MOOK.", "Summoning");
            YCard.setCardToNthSumMonster(Card, false, true, false, TargetMonster.sumMonsterNumber, false, false);
            Game.hasStillNormalSummonCPU=false;
        }
    }
    public static void cpuNormalSetsMookWithCardNo (int cardNo) {
        SummonedMonster TargetMonster = SummonedMonster.determineFreeMonsterZone(false);
        if (TargetMonster.sumMonsterNumber!=0) {
            YCard Card = HandCPU.getNthCardOfHand(cardNo);
            YuGiOhJi.informationDialog("Computer normal sets a MOOK.", "Summoning");
            YCard.setCardToNthSumMonster(Card, false, true, false, TargetMonster.sumMonsterNumber, false, false);
            Game.hasStillNormalSummonCPU=false;
        }
    }
    
    // Effect ID = 21
    public static void cpuTributeSummonsMidboss (int cardId, boolean isConcerningUpperMonster, AIsacrifice Sacrifice){
        if (Sacrifice.sacrificeIsValidMonster() && worthInSemipointsOfAllCurrentSacrifices()==2) {
            cpuPaysCost(Sacrifice);
            SummonedMonster TargetMonster = SummonedMonster.determineFreeMonsterZone(false);
            YCard Card = HandCPU.getNthCardOfHand(HandCPU.getPositionOfCardWithCardId(cardId));
            if (isConcerningUpperMonster) {YuGiOhJi.informationDialog("Computer tribute summons the MIDBOSS " + Card.upMonster.monsterName + ".", "Summoning");}
            else {YuGiOhJi.informationDialog("Computer tribute summons the MIDBOSS " + Card.lowMonster.monsterName + ".", "Summoning");}
            YCard.setCardToNthSumMonster(Card, isConcerningUpperMonster, false, true, TargetMonster.sumMonsterNumber, false, false);
            Game.hasStillNormalSummonCPU=false;
            Game.deactivateCurrentCPUEffects();
        }
    }
    public static boolean cpuCanNormalSummonMidboss() { // looks if cpu could normal summon/set a MIDBOSS, if it had one on the hand
        return CardOptions.canNormalSummonMonsterWithStars(2, false);
    }
    
    // Effect ID = 22
    public static void cpuTributeSetsMidboss (int cardId, boolean isConcerningUpperMonster, AIsacrifice Sacrifice){
        if (Sacrifice.sacrificeIsValidMonster() && worthInSemipointsOfAllCurrentSacrifices()==2) {
            cpuPaysCost(Sacrifice);
            SummonedMonster TargetMonster = SummonedMonster.determineFreeMonsterZone(false);
            YCard Card = HandCPU.getNthCardOfHand(HandCPU.getPositionOfCardWithCardId(cardId));
            YuGiOhJi.informationDialog("Computer tribute sets a MIDBOSS.", "Summoning");
            YCard.setCardToNthSumMonster(Card, isConcerningUpperMonster, true, false, TargetMonster.sumMonsterNumber, false, false);
            Game.hasStillNormalSummonCPU=false;
            Game.deactivateCurrentCPUEffects();
        }
    }
    
    // return true, if CPU has a monster wit a certain monster ID on the hand
    public static boolean cpuHasMonsterWithIdOnHand (int monsterId) {
        return Hand.hasCardWithCardIdOnHand(false, YCard.getCardIdByMonsterId(monsterId));
    }
    
    // Effect ID = 23
    public static void cpuTributeSummonsEndboss (int cardId, AIsacrifice Sacrifice1, AIsacrifice Sacrifice2){
        if (Sacrifice1.sacrificeIsValidMonster() && Sacrifice2.sacrificeIsValidMonster() && worthInSemipointsOfAllCurrentSacrifices()==4) {
            cpuPaysCost(Sacrifice1, Sacrifice2);
            SummonedMonster TargetMonster = SummonedMonster.determineFreeMonsterZone(false);
            YCard Card = HandCPU.getNthCardOfHand(HandCPU.getPositionOfCardWithCardId(cardId));
            YuGiOhJi.informationDialog("Computer tribute summons the ENDBOSS " + Card.upMonster.monsterName + ".", "Summoning");
            YCard.setCardToNthSumMonster(Card, true, false, true, TargetMonster.sumMonsterNumber, false, false);
            Game.hasStillNormalSummonCPU=false;
            Game.deactivateCurrentCPUEffects();
        }
    }
    public static boolean cpuCanNormalSummonEndboss() { // looks if cpu could normal summon/set a ENDBOSS, if it had one on the hand
        return CardOptions.canNormalSummonMonsterWithStars(3, false);
    }
    
    // Effect ID = 24
    public static void cpuTributeSetsEndboss (int cardId, AIsacrifice Sacrifice1, AIsacrifice Sacrifice2){
        if (Sacrifice1.sacrificeIsValidMonster() && Sacrifice2.sacrificeIsValidMonster() && worthInSemipointsOfAllCurrentSacrifices()==4) {
            cpuPaysCost(Sacrifice1, Sacrifice2);
            SummonedMonster TargetMonster = SummonedMonster.determineFreeMonsterZone(false);
            YCard Card = HandCPU.getNthCardOfHand(HandCPU.getPositionOfCardWithCardId(cardId));
            YuGiOhJi.informationDialog("Computer tribute sets an ENDBOSS.", "Summoning");
            YCard.setCardToNthSumMonster(Card, true, true, false, TargetMonster.sumMonsterNumber, false, false);
            Game.hasStillNormalSummonCPU=false;
            Game.deactivateCurrentCPUEffects();
        }
    }
    
    // Effect ID = 25
    public static void cpuTributeSummonsGod (int cardId, AIsacrifice Sacrifice1, AIsacrifice Sacrifice2, AIsacrifice Sacrifice3){
        if (Sacrifice1.sacrificeIsValidMonster() && Sacrifice2.sacrificeIsValidMonster() && Sacrifice3.sacrificeIsValidMonster() && worthInSemipointsOfAllCurrentSacrifices()==6) {
            cpuPaysCost(Sacrifice1, Sacrifice2, Sacrifice3);
            SummonedMonster TargetMonster = SummonedMonster.determineFreeMonsterZone(false);
            YCard Card = HandCPU.getNthCardOfHand(HandCPU.getPositionOfCardWithCardId(cardId));
            YuGiOhJi.informationDialog("Computer tribute summons the GOD \"" + Card.upMonster.monsterName + "\".", "Summoning");
            YCard.setCardToNthSumMonster(Card, true, false, true, TargetMonster.sumMonsterNumber, false, false);
            Game.hasStillNormalSummonCPU=false;
            Game.deactivateCurrentCPUEffects();
        }
    }
    public static boolean cpuCanNormalSummonGod() { // looks if cpu could normal summon/set a GOD, if it had one on the hand
        return CardOptions.canNormalSummonMonsterWithStars(4, false);
    }
    
    // Effect ID = 26
    public static void cpuTributeSetsGod (int cardId, AIsacrifice Sacrifice1, AIsacrifice Sacrifice2, AIsacrifice Sacrifice3){
        if (Sacrifice1.sacrificeIsValidMonster() && Sacrifice2.sacrificeIsValidMonster() && Sacrifice3.sacrificeIsValidMonster() && worthInSemipointsOfAllCurrentSacrifices()==6) {
            cpuPaysCost(Sacrifice1, Sacrifice2, Sacrifice3);
            SummonedMonster TargetMonster = SummonedMonster.determineFreeMonsterZone(false);
            YCard Card = HandCPU.getNthCardOfHand(HandCPU.getPositionOfCardWithCardId(cardId));
            YuGiOhJi.informationDialog("Computer tribute sets a GOD.", "Summoning");
            YCard.setCardToNthSumMonster(Card, true, true, false, TargetMonster.sumMonsterNumber, false, false);
            Game.hasStillNormalSummonCPU=false;
            Game.deactivateCurrentCPUEffects();
        }
    }
    
    // -- INTERRUPTING EFFECTS of CPU --
    // (can not be interrupted by player)
    
    // Effect ID = 27.1 (hand trap effect of Neutraliser used on an effect monster)
    public static void cpuHandTrapEffectNegateOnMonster (SummonedMonster TargetMonster) {
        // Hand traps can not be negated. That's why the player is not asked to interefere here.
        if (cpuCanUseHandTrapEffectNegate()) {
            YMonster.revealHandCard(Card.NeutraliserSkillStealer.cardId, false); // player using an on hand effect (hand traps and searches) should reveal it first (for dramatic effect and stuff)
            String pronoun;
            if (TargetMonster.isPlayersMonster) {pronoun = "your ";}
            else {pronoun = "its ";}
            YuGiOhJi.informationDialog("Computer discards a Neutraliser in order to negate the effects of " + pronoun + getNumberAsString(TargetMonster.sumMonsterNumber) + " monster, " + TargetMonster.Monster.monsterName + ".", "Effect negation");
            Hand.discardCard(HandCPU.getPositionOfCardWithCardId(Card.NeutraliserSkillStealer.cardId), false);
            TargetMonster.isNotAbleToUseItsEffects=true;
        }
    }
    // used to test, if the CPU can use the hand trap effect of Neutraliser
    public static boolean cpuCanUseHandTrapEffectNegate() {
        return Hand.hasCardWithCardIdOnHand(false, Card.NeutraliserSkillStealer.cardId);
    }
    // used to test, if the CPU can use the hand trap effect of Neutraliser on an equip card
    public static boolean cpuCanUseHandTrapEffectNegateOnMonster (SummonedMonster TargetMonster) {
        boolean hasNeutraliserOnHand = cpuCanUseHandTrapEffectNegate();
        return (hasNeutraliserOnHand && TargetMonster.canBeNegated());
    }
    
    
    // Effect ID = 27.2 (hand trap effect of Neutraliser used on an equip card)
    // reuse method YMonster.negateEquipCard(EquipStack Stack, int cardNumberInStack)
    public static void cpuHandTrapEffectNegateOnEquipCard (EStack Stack, int cardNumberInStack) {
        // Hand traps can not be negated. That's why the player is not asked to interefere here.
        if (cpuCanUseHandTrapEffectNegate()) {
            YMonster.revealHandCard(Card.NeutraliserSkillStealer.cardId, false); // player using an on hand effect (hand traps and searches) should reveal it first (for dramatic effect and stuff)
            String pronoun;
            if (Stack.isBelongingToPlayer) {pronoun = "your ";}
            else {pronoun = "its ";}
            YuGiOhJi.informationDialog("Computer discards " + Card.NeutraliserSkillStealer.cardName + " in order to negate the equip card " + Stack.getNthCardOfStack(cardNumberInStack).lowMonster.monsterName + ", in the equip stack of " + pronoun + getNumberAsString(Stack.stackNumber) + " monster.", "Effect negation");
            Hand.discardCard(HandCPU.getPositionOfCardWithCardId(5), false);
            YMonster.negateEquipCard(Stack, cardNumberInStack, false, false);
        }
    }
    // a negate can always be used on an equip card, if the CPU has the nagate (that's why there is no method needed to check, if CPU can negate it)
    
    // Effect ID = 28 (no CPU-version of effect needed)
    // CPU simply also uses the methods YMonster.effectBackBouncerActivate(...) and YMonster.effectBackBouncerExecute()
    
    // Effect ID = 29 and 30
    // look into AIinterrupts class file
    
    // Effect ID = 31
    // All the interrupting effects of the CPU are called from one place in the AI files (in AIdelegate class file).
    // don't use method YMonster.negateAttack(SummonedMonster AttackingMonster)
    // since this is an on field effect, one should in principle be able to negate it, but I leave that out for simplicity (so an attack has to count as an effect)
    public static void cpuOnFieldEffectAttackStopper (SummonedMonster NegatingMonster, AIsacrifice Sacrifice) {
        if (Game.isSwitchingOnOncePerTurnRule) {
            NegatingMonster.canStillUseOncePerTurnEffect=false;
        }
        cpuPaysCost(Sacrifice);
        YuGiOhJi.informationDialog("Computer uses the effect of its " + getNumberAsString(NegatingMonster.sumMonsterNumber) + " monster, " + NegatingMonster.Monster.monsterName + ", in order to negate the attack.", "Attack negation");
        BattlePhase.endAttack(false);
    }
    // used to test, if the CPU can use the on field effect of Attack Stopper
    public static boolean cpuCanUseOnFieldEffectAttackStopper() {
        boolean isAbleToPayCost = EStack.countOwnEquipMonsters(false, false)>=1;
        boolean hasWorkingAttackStopper = AIEffects.hasWorkingMonsterEffect(false, Mon.AttackStopper.monsterId);
        return (isAbleToPayCost && hasWorkingAttackStopper);
    }
    public static boolean cpuCanUseOnFieldEffectAttackStopper (SummonedMonster NegatingMonster) {
        boolean isAbleToPayCost = EStack.countOwnEquipMonsters(false, false)>=1;
        boolean hasWorkingAttackStopper = AIEffects.hasWorkingMonsterEffect(NegatingMonster, Mon.AttackStopper.monsterId);
        return (isAbleToPayCost && hasWorkingAttackStopper);
    }
    
    
    // Effect ID = 32
    // All the interrupting effects of the CPU are called from one place in the AI files (in AIdelegate class file).
    // don't use method YMonster.negateAttack(SummonedMonster AttackingMonster)
    // since this is an on field effect, one should in principle be able to negate it, but I leave that out for simplicity (so an attack has to count as an effect)
    public static void cpuOnFieldEffectBigAttackStopper (SummonedMonster NegatingMonster, AIsacrifice Sacrifice) {
        if (Game.isSwitchingOnOncePerTurnRule) {
            NegatingMonster.canStillUseOncePerTurnEffect=false;
        }
        cpuPaysCost(Sacrifice);
        YuGiOhJi.informationDialog("Computer uses the effect of its " + getNumberAsString(NegatingMonster.sumMonsterNumber) + " monster, " + NegatingMonster.Monster.monsterName + ", in order to negate the attack.", "Attack negation");
        BattlePhase.endAttack(false);
    }
    // used to test, if the CPU can use the on field effect of Big Attack Stopper (always able to pay cost, since it can always tribute itself)
    public static boolean cpuCanUseOnFieldEffectBigAttackStopper() {
        return AIEffects.hasWorkingMonsterEffect(false, Mon.BigAttackStopper.monsterId);
    }
    public static boolean cpuCanUseOnFieldEffectBigAttackStopper (SummonedMonster NegatingMonster) {
        return AIEffects.hasWorkingMonsterEffect(NegatingMonster, Mon.BigAttackStopper.monsterId);
    }
    // btw, for the hand trap effect of the Big Attack Stopper, see AIinterrupts.cpuDiscardsBigAttackStopper();
    
    
    // Effect ID = 33
    // has become redundant and has been deleted (effect #16 used instead)
    
    
    // Effect ID = 34
    public static void cpuSpecialSummonDemonFromStack (EStack Stack, boolean isInAttMode) {
        int cardNumberInStack=0;
        if (Stack.numberOfCards>0) {
            for (int n = Stack.numberOfCards; n >= 1; n--){
                if (Stack.getNthCardOfStack(n).lowMonster.equals(Mon.Demon) && !Stack.getControllerOfNthEquipCard(n) && Stack.getNegationStatusOfNthEquipCard(n)==false) {
                    cardNumberInStack=n;
                    break;
                }
            }
            boolean isBelongingToPlayer = false;
            int zoneNumber = SummonedMonster.determineFreeMonsterZone(isBelongingToPlayer).sumMonsterNumber;
            if (cardNumberInStack!=0 && zoneNumber>0) { // proceed only, if CPU has usable Demon in equip stack
                Game.isActCPUEff = true;
                String pronounStack;
                if (Stack.isBelongingToPlayer) {pronounStack = "your ";}
                else {pronounStack = "its ";}
                YuGiOhJi.informationDialog("Computer wants to special summon its equip monster Demon from " + pronounStack + getNumberAsString(Stack.stackNumber) + " equip stack.", "Effect attempt");
                boolean isCanceling = askPlayerToNegateActivatedEffect(Stack, cardNumberInStack, false);
                if (!isCanceling) {
                    YuGiOhJi.informationDialog("Computer special summons its equip monster Demon from " + pronounStack + getNumberAsString(Stack.stackNumber) + " equip stack.", "Summoning");
                    // since method setCardToNthSumMonster needs either summoning from hand or GY, can not use it here, thus do it manually
                    YCard Card = Stack.getNthCardOfStack(cardNumberInStack);
                    SummonedMonster.getNthSummonedMonster(zoneNumber, isBelongingToPlayer).updateMonsterPropertiesWhenSummoning(Card, false, false, isInAttMode);
                    SummonedMonster.getNthSummonedMonster(zoneNumber, isBelongingToPlayer).canStillUseOncePerTurnEffect=false;
                    if (isInAttMode) {
                        YuGiOhJi.setMonsterButtonIcon(Card.upMonster.cardPathAtt, zoneNumber, isBelongingToPlayer, isInAttMode, true);
                    }
                    else {
                        YuGiOhJi.setMonsterButtonIcon(Card.upMonster.cardPathDef, zoneNumber, isBelongingToPlayer, isInAttMode, true);
                    }
                    SummonedMonster.getNthSummonedMonster(zoneNumber, isBelongingToPlayer).updateAttDefDisplay();
                    Stack.deleteNthCardInEquipStackAndRearrange(cardNumberInStack);
                    YuGiOhJi.rescaleEverything();
                }
                Game.deactivateCurrentCPUEffects();
            }
        }
    }
    // used to test, if the CPU can use the special summoning effect of Demon
    public static boolean cpuCanSpecialSummonDemonFromStack() {
        for (int index = 1; index <= 5; index++){
            if (cpuCanSpecialSummonDemonFromStack(EStack.getNthStack(index, false))) {return true;}
            if (cpuCanSpecialSummonDemonFromStack(EStack.getNthStack(index, true))) {return true;}
        }
        return false;
    }
    public static boolean cpuCanSpecialSummonDemonFromStack (EStack Stack) {
        int cardNumberInStack=0;
        if (Stack.numberOfCards>0) {
            for (int n = Stack.numberOfCards; n >= 1; n--){
                if (Stack.getNthCardOfStack(n).lowMonster.equals(Mon.Demon) && !Stack.getControllerOfNthEquipCard(n) && !Stack.getNegationStatusOfNthEquipCard(n)) {
                    cardNumberInStack=n;
                    break;
                }
            }
        }
        SummonedMonster TargetMonster = SummonedMonster.determineFreeMonsterZone(false);
        return (cardNumberInStack!=0 && TargetMonster.sumMonsterNumber>0);
    }

    
    // Effect ID = 35
    public static void cheatChangeEffectDemon (SummonedMonster EffectMonster) {
        boolean hasTributedADemon = false;
        for (int index = 1; index <= 5; index++){
            SummonedMonster PotentialSacrifice=SummonedMonster.getNthSummonedMonster(index, false);
            if (!EffectMonster.isBasicallySameMonster(PotentialSacrifice) && PotentialSacrifice.Monster.equals(Mon.Demon)) { // Only real Demons (no copies) count as possible sacrifices for this effect. Fun fact: The word "tribute" imlies that only summoned monsters (no equipping monsters) are considered. Maybe mention that in the rules?
                YuGiOhJi.informationDialog("tributing " + PotentialSacrifice.Monster.monsterName, "");
                PotentialSacrifice.killMonster();
                hasTributedADemon = true;
            }
        }
        if (hasTributedADemon) {
            Game.isActCPUEff = true;
            boolean isCanceling = askPlayerToNegateActivatedEffect(EffectMonster);
            if (!isCanceling) {
                YuGiOhJi.informationDialog("applying cheat mode change", "Effect");
                EffectMonster.cheatChangeMode(true);
            }
            Game.deactivateCurrentCPUEffects();
        }
    }
    // used to test, if the CPU can use the cheat change effect of Demon
    public static boolean cpuCanCheatChangeDemon() {
        for (int index = 1; index <= 5; index++){
            SummonedMonster SumMonster = SummonedMonster.getNthSummonedMonster(index, false);
            if (cpuCanCheatChangeDemon(SumMonster)) {return true;}
        }
        return false;
    }
    public static boolean cpuCanCheatChangeDemon (SummonedMonster SumMonster) {
        return SumMonster.canUseCheatChangeEffectOfDemon();
    }
    
    
    // Effect ID = 36
    public static void cpuBanishSearchHolyLanceActivate (int targetCardId) {
        if (canBanishSearchHolyLanceActivate(targetCardId, false)) {
            if (banishCardWithCardIdFromHand(Card.IncorruptibleHolyLance.cardId)) {
                YMonster.banishSearchExecute(targetCardId, false);
            }
        }
    }
    // used to test, if the CPU can use the banish search of Holy Lance (make them also usable to the player)
    public static boolean canBanishSearchHolyLanceActivate (int targetCardId, boolean isConcerningPlayer) {
        boolean hasValidTargets = (canBanishSearchHolyLanceActivate(isConcerningPlayer) && Deck.getDeck(isConcerningPlayer).getPositionOfCardWithCardIdInDeck(targetCardId)!=0);
        boolean isValidTarget = (Card.GodBarrier.cardId==targetCardId || Card.DemonGodDemon.cardId==targetCardId);
        return (hasValidTargets && isValidTarget);
    }
    public static boolean canBanishSearchHolyLanceActivate (boolean isConcerningPlayer) {
        boolean hasHolyLanceOnHand = Hand.hasCardWithCardIdOnHand(isConcerningPlayer, Card.IncorruptibleHolyLance.cardId);
        Deck ConsDeck = Deck.getDeck(isConcerningPlayer);
        boolean hasValidTarget = (ConsDeck.getPositionOfCardWithCardIdInDeck(Card.GodBarrier.cardId)!=0 || ConsDeck.getPositionOfCardWithCardIdInDeck(Card.DemonGodDemon.cardId)!=0);
        return (hasHolyLanceOnHand && hasValidTarget);
    }
    
    // in order not to repeat oneself, out-source here the banishing of a card from the hand for a banish search
    // returns true, if banishing the card was successful
    public static boolean banishCardWithCardIdFromHand (int cardId) {
        int cardNumOnHand = HandCPU.getPositionOfCardWithCardId(cardId);
        if (cardNumOnHand!=0) {
            YMonster.revealHandCard(cardId, false); // player using an on hand effect (hand traps and searches) should reveal it first (for dramatic effect and stuff)
            YuGiOhJi.informationDialog("Computer banishes card " + HandCPU.getNthCardOfHand(cardNumOnHand).cardName, "Search effect");
            Hand.banishHandCard(cardNumOnHand, false);
            return true;
        }
        return false;
    }
    
    
    // Effect ID = 37
    public static void banishSearchSlickRusherActivate (int targetCardId) {
        if (canBanishSearchSlickRusherActivate(targetCardId, false)) {
            if (banishCardWithCardIdFromHand(Card.SlickRusherRecklessRusher.cardId)) {
                YMonster.banishSearchExecute(targetCardId, false);
            }
        }
    }
    // used to test, if the CPU can use the banish search of Slick Rusher (make them also usable to the player)
    public static boolean canBanishSearchSlickRusherActivate (int targetCardId, boolean isConcerningPlayer) {
        boolean hasValidTargets = (canBanishSearchSlickRusherActivate(isConcerningPlayer) && Deck.getDeck(isConcerningPlayer).getPositionOfCardWithCardIdInDeck(targetCardId)!=0);
        boolean isValidTarget = (Card.NecromancerBackBouncer.cardId==targetCardId || Card.BigBackBouncerBanisher.cardId==targetCardId || Card.BigBanisherBurner.cardId==targetCardId || Card.BigBurnerSuicideCommando.cardId==targetCardId);
        return (hasValidTargets && isValidTarget);
    }
    public static boolean canBanishSearchSlickRusherActivate (boolean isConcerningPlayer) {
        boolean hasSlickRusherOnHand = Hand.hasCardWithCardIdOnHand(isConcerningPlayer, Card.SlickRusherRecklessRusher.cardId);
        Deck ConsDeck = Deck.getDeck(isConcerningPlayer);
        boolean hasValidTarget = (ConsDeck.getPositionOfCardWithCardIdInDeck(Card.NecromancerBackBouncer.cardId)!=0 || ConsDeck.getPositionOfCardWithCardIdInDeck(Card.BigBackBouncerBanisher.cardId)!=0 || ConsDeck.getPositionOfCardWithCardIdInDeck(Card.BigBanisherBurner.cardId)!=0 || ConsDeck.getPositionOfCardWithCardIdInDeck(Card.BigBurnerSuicideCommando.cardId)!=0);
        return (hasSlickRusherOnHand && hasValidTarget);
    }
    
    // Effect ID = 38
    public static void cpuBanisherHandTrapEffectExecute (int cardNumberInGY) {
        YMonster.banisherHandTrapEffectExecute(cardNumberInGY, false);
    }
    public static boolean cpuCanUseBanisherHandTrapEffect() {
        return Hand.hasCardWithCardIdOnHand(false, Card.BigBackBouncerBanisher.cardId);
    }
    // fun fact: If the CPU has 3 Banishers and 3 Slick Rushers in the deck, when starting with 5 cards (standard settings), it will have this hand trap in 59% of the games on the starting hand or can at least search it. With 10 starting hand cards it's even 85%.
    // fun fact: The computer has each "unsearchable" hand hand trap (Neutraliser, Big Attack Stopper) on the starting hand in every 3rd game with standard settings, when having them 3 times in the deck with. With 10 starting hand cards it's 60%.
    // fun fact: The computer has at least any hand trap (Neutraliser, Big Attack Stopper or Banisher) in 86% of the games on starting hand, when having them + searchers all in the deck. With 10 hand cards its basically always the case (99%).
    // If you don't like these statistics (or hand traps in general), go into deck building and give the computer the 7th standard deck (the "No hand traps deck"). Then the computer won't hand trap you. You could also just set the CPU on aggressive. Then the computer will summon a lot of monsters and will (most likely?) not hand trap you. Or play against the balanced CPU. That one will usually only use optional removal effects on the field against you.
    
    
}
