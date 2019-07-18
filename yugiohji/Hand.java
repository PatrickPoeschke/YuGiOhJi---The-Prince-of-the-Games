package yugiohji;

/**
 * This class creates hands full of YuGiOhJi-cards.
 * For simplicity there are only 10 cards allowed on each hand.
 * If one has 10 cards at the end of a turn, one has to discard a card.
 * 
 * Each object just takes 10 YCard objects as properties.
 * Also it has an interger telling how many actual cards there are on the hand.
 * The rest are just placeholder cards.
 * 
 * Also this class has some usefull methods
 * mostly having to do with moving cards from or to the hand.
 * 
 * This is an improved version of the file using arrays of cards
 * instead of single cards as properties. However,
 * it also contains an unused zeroth array element
 * consisting of the "NoCard"-placeholder,
 * just so that the arrays are not off by one any more.
 * 
 */

import static yugiohji.YuGiOhJi.Card;
import static yugiohji.YuGiOhJi.buttonDeckCPU;
import static yugiohji.YuGiOhJi.buttonDeckPlayer;
import static yugiohji.YuGiOhJi.Game;
import static yugiohji.YuGiOhJi.HandCPU;
import static yugiohji.YuGiOhJi.HandPlayer;
import static yugiohji.YuGiOhJi.Mon;
import static yugiohji.YuGiOhJi.NoCard;

public class Hand {
    
    public int numberOfCards; // a number from 1 to 10 counting how many cards one actually has on the hand (excluding the placeholder cards)
    public boolean isBelongingToPlayer; // true if it is the hand of the player (false if it belongs to CPU)
    public YCard[] Cards;// = new YCard[11]; // first card from left as object
        
    // constructor
    public Hand (int numberOfCards, boolean isBelongingToPlayer, YCard Card1, YCard Card2, YCard Card3, YCard Card4, YCard Card5, YCard Card6, YCard Card7, YCard Card8, YCard Card9, YCard Card10)
    {
        this.numberOfCards=numberOfCards;
        this.isBelongingToPlayer=isBelongingToPlayer;
        this.Cards = new YCard[11];
        this.Cards[0]=NoCard; // leave the zeroth element de facto empty and add another card at the end, such that the array is not off by one any more
        this.Cards[1]=Card1;
        this.Cards[2]=Card2;
        this.Cards[3]=Card3;
        this.Cards[4]=Card4;
        this.Cards[5]=Card5;
        this.Cards[6]=Card6;
        this.Cards[7]=Card7;
        this.Cards[8]=Card8;
        this.Cards[9]=Card9;
        this.Cards[10]=Card10;
    }
    
    // standard constructor for empty hands
    public Hand (boolean isBelongingToPlayer) {
        this.numberOfCards=0;
        this.isBelongingToPlayer=isBelongingToPlayer;
        this.Cards = new YCard[11];
        for (int index = 0; index <= 10; index++){
            this.Cards[index]=NoCard;
        }
    }
    
    // --- BOOKKEEPING part1: general SETTER and GETTER methods ---
    
    // returns the Hand of a given player
    public static Hand getHand (boolean isBelongingToPlayer) {
        if (isBelongingToPlayer) {
            return HandPlayer;
        }
        else {
            return HandCPU;
        }
    }
    
    // returns the number of cards on the hand of a given player
    public static int numberOfCardsOnHand (boolean isBelongingToPlayer) {
        return getHand(isBelongingToPlayer).numberOfCards;
    }
    
    // returns the nth card of a given Hand as object
    public YCard getNthCardOfHand (int n){
        if (n > numberOfCards){
            YuGiOhJi.debugDialog("Error: out of bounds in getNthCardOfHand(...); max. number: " + numberOfCards + ". attempted N: " + n); return NoCard;
        }
        else if (n > 10 || n < 1) {
            YuGiOhJi.debugDialog("Error: out of bounds in getNthCardOfHand(...); attempted N: " + n); return NoCard;
        }
        else {
            return Cards[n];
        }
    }
    
    // returns the position of first card with a given ID on a given hand as integer
    // (Since cards with the same ID are identical, it doesn't matter which one)
    // Here, one has to go from left to right in order to prevent the computer
    // from sacrificing a hand card it just searched out by another strategy!
    // If such a card does not exist, returns zero.
    public int getPositionOfCardWithCardId (int cardId) {
        return getPositionOfNthCopyOfCardWithCardId(cardId, 1);
    }
    public int getPositionOfNthCopyOfCardWithCardId(int cardId, int n) {
        int counter=0;
        if (numberOfCards>=1) {
            for (int index = 1; index <= numberOfCards; index++){
                if (getNthCardOfHand(index).cardId==cardId) {
                    counter++;
                    if (counter==n) {return index;}
                }
            }
        }
        return 0;
    }
    
    // combined method that returns a card on a given hand with a given card ID
    public YCard getCardOnHandByCardID(int cardId) {
        return getNthCardOfHand(getPositionOfCardWithCardId(cardId));
    }
    
    // sets the nth card of a given hand equal to a specific card
    public void setNthCardOfHandToCard (YCard ConsCard, int n){
        if (n > 10 || n < 1) {
            YuGiOhJi.debugDialog("Error: out of bounds in setNthCardOfHandToCard(...); attempted N: " + n);
        }
        else {
            Cards[n]=ConsCard;
        }
    }
    
    // --- BOOKKEEPING part2: MOVING CARDS FROM OR TO HAND ---
    
    // sends nth card from the hand to the graveyard
    public static void discardCard (int n, boolean isBelongingToPlayer) {
        discardOrBanishNthHandCard(n, isBelongingToPlayer, true);
    }
    
    // sends nth card from the hand to the different dimension
    public static void banishHandCard (int n, boolean isBelongingToPlayer) {
        discardOrBanishNthHandCard(n, isBelongingToPlayer, false);
    }
    
    // in order not to repeat oneself, combine here the source code of two ver similar methods
    public static void discardOrBanishNthHandCard (int n, boolean isBelongingToPlayer, boolean isDiscarding) {
        Deck.addCardToGYOrDD(getHand(isBelongingToPlayer).getNthCardOfHand(n), isBelongingToPlayer, isDiscarding);
        getHand(isBelongingToPlayer).deleteNthCardOnHandAndRearrange(n, isBelongingToPlayer);
    }
    
    // adds a given card to the hand of a player and updates the appearance of the hand
    public static void addCardToHand (YCard ConsCard, boolean isBelongingToPlayer) {
        Hand FullHand = getHand(isBelongingToPlayer);
        FullHand.numberOfCards++;
        FullHand.setNthCardOfHandToCard(ConsCard, FullHand.numberOfCards);
        if (isBelongingToPlayer) {
            YuGiOhJi.setCardButtonIcon(FullHand.getNthCardOfHand(FullHand.numberOfCards).upMonster.cardPathAtt, FullHand.numberOfCards, isBelongingToPlayer, true);
        }
        else {
            YuGiOhJi.setCardButtonIcon("/images/YuGiOhJiFacedown.png", FullHand.numberOfCards, isBelongingToPlayer, true);
        }
    }
    
    // a player draws a card
    public static void drawCard (boolean playerIsDrawing){
        if (playerIsDrawing) { // player draws a card
            if (Game.isSwitchingOnShowDrawDialog || !Game.isVeryFirstTurn()) {
                YuGiOhJi.informationDialog("draw a card", "you");
            }
            if (Deck.getDeck(true).numberOfCards==0) { // player loses from not being able to draw any more
                YuGiOhJi.informationDialog("You lose, because you can not draw a card any more!", "YOU LOSE!");
                // display number of hidden achievements here, if unlocked
                if (!Game.trophyOverUnlocked) {YuGiOhJi.informationDialog("Hidden achievement found: \"39 and over\". You lost by running out of cards.", ""); Game.unlockTrophy(Game.trophyOverUnlocked);}
                // end of listing of achievements
                Game.over(false);
            }
            else {
                Deck.addNthCardFromDeckToHand(Deck.getDeck(true).numberOfCards, true);
                // after last card of deck has been taken, a card there must not be visible any more 
                if (Deck.getDeck(true).numberOfCards==0) {
                    buttonDeckPlayer.setIcon(null);
                }
            }
        }
        else { // CPU draws a card (completely analog to player (term 'player' in objectnames replaced by 'CPU'))
            if (Game.isSwitchingOnShowDrawDialog || !Game.isVeryFirstTurn()) {
                YuGiOhJi.informationDialog("computer draws a card", "");
            }
            if (Deck.getDeck(false).numberOfCards==0) { // player loses from not being able to draw any more
                YuGiOhJi.informationDialog("The computer loses, because it can not draw a card any more!", "YOU WIN!");
                // display number of hidden achievements here, if unlocked
                if (!Game.trophyPatienceUnlocked) {YuGiOhJi.informationDialog("Hidden achievement found: \"Patience is a Virtue\". You have won by making the computer run out of cards.", ""); Game.unlockTrophy(Game.trophyPatienceUnlocked);}
                // end of listing of achievements
                Game.over(true);
            }
            else {
                Deck.addNthCardFromDeckToHand(Deck.getDeck(false).numberOfCards, false);
                // after last card of deck has been taken, a card there must not be visible any more 
                if (Deck.getDeck(false).numberOfCards==0) {
                    buttonDeckCPU.setIcon(null);
                }
            }
        }
        
    }
    
    // deletes the nth card of a hand, and if it was not the last card (which is highly likely)
    // it takes the last one and puts it at the empty place
    // also updates the look of the hand
    public void deleteNthCardOnHandAndRearrange (int n, boolean isBelongingToPlayer) {
        setNthCardOfHandToCard(NoCard, n); // delete nth card of hand
        if (n < numberOfCards) { // if it was not the last card, rearrange
            // take last card and put it to empty place
            setNthCardOfHandToCard(getNthCardOfHand(numberOfCards), n);
            setNthCardOfHandToCard(NoCard, numberOfCards); // delete last card
            if (isBelongingToPlayer) { // update appearance of cards: change appearance of hand after rearranging, also let the last card on hand disappear
                YuGiOhJi.setCardButtonIcon(getNthCardOfHand(n).upMonster.cardPathAtt, n, isBelongingToPlayer, true);
            } // CPU has just "face down cards" on hand anyway
        }
        YuGiOhJi.setCardButtonIcon("/images/YuGiOhJiFacedown.png", numberOfCards, isBelongingToPlayer, false); // let last card disappear
        numberOfCards--;
    }
    
    // --- BOOKKEEPING part3: LOOKING FOR certain CARDS on hand ---
    
    // returns the card number of the strongest ENDBOSS on a given hand
    // returns zero, if there is no ENDBOSS on the hand
    public int getCardNoOfStrongestEndbossOnHand() {
        int cardNoInGY;
        cardNoInGY = getPositionOfCardWithCardId(Card.GodKillingSpearLance.cardId);
        if (cardNoInGY==0) {getPositionOfCardWithCardId(Card.NeutraliserSkillStealer.cardId);}
        if (cardNoInGY==0) {getPositionOfCardWithCardId(Card.EradicatorObstacle.cardId);}
        if (cardNoInGY==0) {getPositionOfCardWithCardId(Card.BigAttackStopperSword.cardId);}
        if (cardNoInGY==0) {getPositionOfCardWithCardId(Card.MonsterStealerSteepLearningCurve.cardId);}
        if (cardNoInGY==0) {getPositionOfCardWithCardId(Card.BigBurnerSuicideCommando.cardId);}
        return cardNoInGY;
    }
    
    // returns the strongest monster on the hand, if it has a certain minimum attack
    // returns placeholder NoMonster otherwise
    public YMonster getStrongestMonsterOnHand (int atLeastAtt) {
        int currentAtt;
        int maxAtt=0;
        int cardNoOnHand=0;
        int currentStars;
        int lowestStars=4; // if two monsters have the same amount of attack, prefer the one with fewer stars (because esier to summon)
        YMonster Monster = Mon.NoMonster;
        for (int index = numberOfCards; index >= 1; index--){
            YCard HandCard = getNthCardOfHand(index);
            Monster = HandCard.upMonster;
            currentStars = Monster.stars;
            currentAtt = Monster.att;
            if (currentAtt > maxAtt) {maxAtt=currentAtt; cardNoOnHand=index;}
            else if (currentAtt == maxAtt && currentStars < lowestStars) {
                cardNoOnHand=index; lowestStars=currentStars;
            }
            Monster = HandCard.lowMonster;
            currentStars = Monster.stars;
            currentAtt = Monster.att;
            if (currentAtt > maxAtt) {maxAtt=currentAtt; cardNoOnHand=index;}
            else if (currentAtt == maxAtt && currentStars < lowestStars) {
                cardNoOnHand=index; lowestStars=currentStars;
            }
        }
        if (maxAtt < atLeastAtt) {cardNoOnHand=0;}
        if (cardNoOnHand!=0) {
            return Monster;
        }
        else {return Mon.NoMonster;}
    }
    
    // returns the card ID of the cards of the monster with the strongest Attack, except the card with strongest monster
    // returns zero, if there is only one card on the hand (or if there are only zero attack monsters otherwise)
    public int cardIdOf2ndStrongestMonsterOnHandBeingAMidboss (AIsacrifice StrongestMonsterOnHand) {
        int currentAtt;
        int maxAtt=0;
        int cardNoOnHand=0;
        for (int index = numberOfCards; index >= 1; index--){
            YCard HandCard = getNthCardOfHand(index);
            AIsacrifice HandCardAsSacrifice = AIsacrifice.markHandCardAsSacrificeByCardId(HandCard.cardId);
            if (!HandCardAsSacrifice.isSameKind(StrongestMonsterOnHand)) {
                YMonster Monster = HandCard.upMonster;
                currentAtt = Monster.att;
                if (currentAtt > maxAtt && Monster.stars==2) {maxAtt=currentAtt; cardNoOnHand=index;}
                Monster = HandCard.lowMonster;
                currentAtt = Monster.att;
                if (currentAtt > maxAtt && Monster.stars==2) {maxAtt=currentAtt; cardNoOnHand=index;}
            }
        }
        if (cardNoOnHand!=0) {
            return getNthCardOfHand(cardNoOnHand).cardId;
        }
        else {return 0;}
    }
    
    // returns true, if a given hand, has card with an optional removal effect (useful for effect of CopyCat)
    public boolean hasOptionalRemovalEffectOnHand() {
        for (int index = numberOfCards; index >= 1; index--){
            YCard ConsCard = getNthCardOfHand(index);
            if (ConsCard.upMonster.hasOptionalRemovalEffect()) {
                return true;
            }
        }
        return false;
    }
    
    // returns true, if a given player has a specific card on hand
    public static boolean hasCardWithCardIdOnHand (boolean isConcerningPlayer, int cardId) {
        int positionOnHand = getHand(isConcerningPlayer).getPositionOfCardWithCardId(cardId);
        return positionOnHand!=0;
    }
    
    // returns true, if a given hand has a stealable effect in it
    public boolean stealableEffectsExistOnHand() {
        if (numberOfCards>=1) {
            for (int index = numberOfCards; index >= 1; index--){
                if (Cards[index].hasStealableEffect()) {return true;}
            }
        }
        return false;
    }
    
    // --- BOOKKEEPING part4: LOOKING FOR possible NEGATION ---
    // (because has to do a bit with cards on the hand ... well not really any more after rewritng, but keep them here, since the other files are already pretty big)
    
    // returns true, if a given player can negate an effect (i.e. has a effect negate on hand or field)
    public static boolean lookForEffectNegate (boolean isConcerningPlayer) {
        return ( lookForEffectNegateOnHand(isConcerningPlayer) || lookForEffectNegateOnField(isConcerningPlayer) );
    }
    
    // returns true, if there is a hand trap with an effect negate (i.e. Neutraliser) on a given hand
    public static boolean lookForEffectNegateOnHand (boolean isConcerningPlayer) {
        return hasCardWithCardIdOnHand(isConcerningPlayer, Card.NeutraliserSkillStealer.cardId);
    }
    
    // returns true, if a given player has the non-negated effect of Neutraliser on the field (always able to pay cost, since Neutraliser can always tribute itself)
    public static boolean lookForEffectNegateOnField (boolean isConcerningPlayer) {
        return AIEffects.hasWorkingMonsterEffect(isConcerningPlayer, Mon.Neutraliser.monsterId);
    }
    
    // returns true, if the nth summoned monster of a given player has an effect negate on the field
    public static boolean checkEffectNegateOfNthMonster (int n, boolean isBelongingToPlayer) {
        return AIEffects.hasWorkingMonsterEffect(SummonedMonster.getNthSummonedMonster(n, isBelongingToPlayer), Mon.Neutraliser.monsterId);
    }
    
    
    // returns true, if a given player can negate an attack (i.e. has an attack negate on hand or field)
    public static boolean lookForAttackNegate (boolean isConcerningPlayer) {
        return ( lookForAttackNegateOnHand(isConcerningPlayer) || lookForAttackNegateOnField(isConcerningPlayer) );
    }
    
    // returns true, if there is a hand trap with an attack negate (i.e. Big Attack Stopper) on hand of a given player
    public static boolean lookForAttackNegateOnHand (boolean isConcerningPlayer) {
        return Hand.hasCardWithCardIdOnHand(isConcerningPlayer, Card.BigAttackStopperSword.cardId);
    }
    
    // returns true, if a given player has the non-negated effect of Attack Stopper or Big Attack Stopper on the field and enough cards of the right kind in order to use the effect
    public static boolean lookForAttackNegateOnField (boolean isConcerningPlayer) {
        return ( lookForUsableAttackStopper(isConcerningPlayer) || lookForUsableBigAttackStopper(isConcerningPlayer) );
    }
    
    // returns true, if a given player has non-negated Attack Stopper and an equip card on the field
    public static boolean lookForUsableAttackStopper (boolean isConcerningPlayer) {
        boolean isAbleToPayCost = EStack.countOwnEquipMonsters(isConcerningPlayer, false)>=1;
        boolean hasWorkingAttackStopper = AIEffects.hasWorkingMonsterEffect(isConcerningPlayer, Mon.AttackStopper.monsterId);
        return (isAbleToPayCost && hasWorkingAttackStopper);
    }
    
    // returns true, if the nth summoned monster of a given player is Attack Stopper and can use its effect
    public static boolean checkIfNthMonsterIsUsableAttackStopper (int n, boolean isBelongingToPlayer) {
        boolean isAbleToPayCost = EStack.countOwnEquipMonsters(isBelongingToPlayer, false)>=1;
        boolean hasWorkingAttackStopper = AIEffects.hasWorkingMonsterEffect(SummonedMonster.getNthSummonedMonster(n, isBelongingToPlayer), Mon.AttackStopper.monsterId);
        return (isAbleToPayCost && hasWorkingAttackStopper);
    }
    
    // returns true, if a given player has non-negated Big Attack Stopper on the field (always able to pay any card, because can always tribute itself)
    public static boolean lookForUsableBigAttackStopper (boolean isConcerningPlayer) {
        return AIEffects.hasWorkingMonsterEffect(isConcerningPlayer, Mon.BigAttackStopper.monsterId);
    }
    
    // returns true, if the nth summoned monster of a given player is Big Attack Stopper and can use its effect
    public static boolean checkIfNthMonsterIsUsableBigAttackStopper (int n, boolean isBelongingToPlayer) {
        return AIEffects.hasWorkingMonsterEffect(SummonedMonster.getNthSummonedMonster(n, isBelongingToPlayer), Mon.BigAttackStopper.monsterId);
    }
    
    
}
