package yugiohji;

/**
 * This class creates full decks of YuGiOhJi-cards.
 * They always contain exactly 39 cards.
 * They are also being used as the cards in the graveyard and
 * banished cards (different dimension).
 * 
 * Each object just takes exactly 39 YCard objects as properties.
 * Also it has an interger telling how many actual cards there are.
 * The rest are just placeholder cards.
 * 
 * Also this class has some usefull methods
 * mostly having to do with cards in or out of decks,
 * graveyards and banishing zones.
 * 
 * This is an improved version of the file using arrays of cards
 * instead of single cards as properties. However,
 * it also contains an unused zeroth array element
 * consisting of the "NoCard"-placeholder,
 * just so that the arrays are not off by one any more.
 * 
 */

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static yugiohji.YuGiOhJi.Card; // Important!: Create bundles of cards (and monsters) before creating decks!
import static yugiohji.YuGiOhJi.DDDeckCPU;
import static yugiohji.YuGiOhJi.DDDeckPlayer;
import static yugiohji.YuGiOhJi.DeckCPU;
import static yugiohji.YuGiOhJi.DeckPlayer;
import static yugiohji.YuGiOhJi.Game;
import static yugiohji.YuGiOhJi.GYDeckCPU;
import static yugiohji.YuGiOhJi.GYDeckPlayer;
import static yugiohji.YuGiOhJi.UnshuffledDeckCPU;

public class Deck {
    
    public int numberOfCards; // a number from 1 to 39 counting how many cards there actually are in the deck piles (excluding placeholders)
    public boolean isBelongingToPlayer; // true, if it is a deck (or list of GY cards or DD cards) belongs to the player (false when belonging to CPU)
    public YCard[] Cards; // = new YCard[40]; // array of cards
    
    // constructor
    public Deck (int numberOfCards, boolean isBelongingToPlayer, YCard Card1, YCard Card2, YCard Card3, YCard Card4, YCard Card5, YCard Card6, YCard Card7, YCard Card8, YCard Card9, YCard Card10, YCard Card11, YCard Card12, YCard Card13, YCard Card14, YCard Card15, YCard Card16, YCard Card17, YCard Card18, YCard Card19, YCard Card20, YCard Card21, YCard Card22, YCard Card23, YCard Card24, YCard Card25, YCard Card26, YCard Card27, YCard Card28, YCard Card29, YCard Card30, YCard Card31, YCard Card32, YCard Card33, YCard Card34, YCard Card35, YCard Card36, YCard Card37, YCard Card38, YCard Card39)
    {
        this.numberOfCards=numberOfCards;
        this.isBelongingToPlayer=isBelongingToPlayer;
        this.Cards = new YCard[40];
        this.Cards[0]=Card.NoCard; // leave the zeroth element de facto empty and add another card at the end, such that the array is not off by one any more
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
        this.Cards[11]=Card11;
        this.Cards[12]=Card12;
        this.Cards[13]=Card13;
        this.Cards[14]=Card14;
        this.Cards[15]=Card15;
        this.Cards[16]=Card16;
        this.Cards[17]=Card17;
        this.Cards[18]=Card18;
        this.Cards[19]=Card19;
        this.Cards[20]=Card20;
        this.Cards[21]=Card21;
        this.Cards[22]=Card22;
        this.Cards[23]=Card23;
        this.Cards[24]=Card24;
        this.Cards[25]=Card25;
        this.Cards[26]=Card26;
        this.Cards[27]=Card27;
        this.Cards[28]=Card28;
        this.Cards[29]=Card29;
        this.Cards[30]=Card30;
        this.Cards[31]=Card31;
        this.Cards[32]=Card32;
        this.Cards[33]=Card33;
        this.Cards[34]=Card34;
        this.Cards[35]=Card35;
        this.Cards[36]=Card36;
        this.Cards[37]=Card37;
        this.Cards[38]=Card38;
        this.Cards[39]=Card39;
        
    }
    
    // standard constructor for empty decks
    public Deck (boolean isBelongingToPlayer) {
        this.numberOfCards=0;
        this.isBelongingToPlayer=isBelongingToPlayer;
        this.Cards = new YCard[40];
        for (int index = 0; index <= 39; index++){
            this.Cards[index]=Card.NoCard;
        }
    }
    
    // --- BOOKKEEPING part1: GAMBLING methods ---
    // (shuffling deck, choose something randomly)
    
    // for coin flips, rolling dice, making a random choice etc.
    public static int chooseRandomOption (int numberOfOptions){
        if (numberOfOptions<1) {return 1;} // just in case, one has entered zero or so (assuming that there is at least one option, choose it)
        // creates an array with the first n intergers
        // shuffles it
        // and returns the first element of that array
        Integer[] options = new Integer[numberOfOptions];
        for (int index = 1; index <= numberOfOptions; index++){
            options[index-1]=index;
        }
        List<Integer> optionsaslist = Arrays.asList(options);
        Collections.shuffle(optionsaslist);
        optionsaslist.toArray(options);
        return options[0];
    }
    
    // for shuffling the whole deck
    public Deck shuffleDeck (){
        // creates an array of the first 39 integers
        // shuffles it
        // and uses it to shuffle a whole deck
        int numberOfCardsInDeck=39;
        Integer[] array39 = new Integer[numberOfCardsInDeck];
        for (int index = 1; index <= numberOfCardsInDeck; index++){
            array39[index-1]=index;
        }
        List<Integer> array39aslist = Arrays.asList(array39);
        Collections.shuffle(array39aslist);
        array39aslist.toArray(array39);
        
        Deck ShuffledDeck = new Deck(isBelongingToPlayer);
        ShuffledDeck.numberOfCards=39;
        for (int index = 1; index <= numberOfCards; index++){
            ShuffledDeck.Cards[index] = Cards[array39[index-1]];
        }
        return ShuffledDeck;
    }
    
    // --- BOOKKEEPING part2: general SETTER and GETTER methods ---
    
    // returns the nth standard deck
    public static Deck getNthStandardDeck (int n) {
        switch (n) {
            case 1: return YuGiOhJi.StandardDeck1;
            case 2: return YuGiOhJi.StandardDeck2;
            case 3: return YuGiOhJi.StandardDeck3;
            case 4: return YuGiOhJi.StandardDeck4;
            case 5: return YuGiOhJi.StandardDeck5;
            case 6: return YuGiOhJi.StandardDeck6;
            case 7: return YuGiOhJi.StandardDeck7;
            case 8: return YuGiOhJi.StandardDeck8;
            case 9: return YuGiOhJi.StandardDeck9;
            case 10: return YuGiOhJi.StandardDeck10;
            default: YuGiOhJi.debugDialog("Error: out of bounds in getNthStandardDeck(...); attempted N: " + n); return YuGiOhJi.StandardDeck1;
        }
    }
    
    // at the beginning of each game the deck of the CPU is chosen randomly from several deck fitting its strategy
    // this will make the CPU less predictable and more challanging
    public static void resetCPUdeck(){
        int intDiceRollResult = chooseRandomOption(Game.CPUbehavior.numberOfDecks); // choose a random deck
        int deckNumber = Game.CPUbehavior.standardDecks[intDiceRollResult]; // look up the nth deck the CPU wants to use
        UnshuffledDeckCPU.setDeck(getNthStandardDeck(deckNumber), false); // finally set the deck
    }
    
    // sets a deck to a given deck 
    public void setDeck (Deck SourceDeck, boolean isPlayersDeck){
        numberOfCards=SourceDeck.numberOfCards;
        isBelongingToPlayer=isPlayersDeck;
        Cards[0]=Card.NoCard;
        for (int index = 1; index <= numberOfCards; index++){
            Cards[index] = SourceDeck.Cards[index];
        }
    }
    
    // returns the graveyard of a given player
    public static Deck getGY (boolean isBelongingToPlayer) {
        if (isBelongingToPlayer) {
            return GYDeckPlayer;
        }
        else {
            return GYDeckCPU;
        }
    }
    
    // returns the number of cards in the graveyard of a given player
    public static int numberOfCardsInGY (boolean isBelongingToPlayer) {
        return getGY(isBelongingToPlayer).numberOfCards;
    }
    
    // returns the deck of a given player
    public static Deck getDeck (boolean isBelongingToPlayer) {
        if (isBelongingToPlayer) {
            return DeckPlayer;
        }
        else {
            return DeckCPU;
        }
    }
    
    // returns the different dimension (aka banishing zone) of a given player
    public static Deck getDD (boolean isBelongingToPlayer) {
        if (isBelongingToPlayer) {
            return DDDeckPlayer;
        }
        else {
            return DDDeckCPU;
        }
    }
    
    // returns the position of last card with a given ID in a given deck as integer
    // (Since cards with the same ID are identical, it doesn't matter which one.
    // However, if it is the last one that is supposed to move somewhere else,
    // one does not need to rearrange, thus saving computation.)
    // If such a card does not exist, returns zero.
    public int getPositionOfCardWithCardIdInDeck (int cardId) {
        return getPositionOfNthCopyOfCardWithCardIdInDeck(cardId, 1);
    }
    // generalisation of previous method: used for checking, if there are multiple copies of a card in the deck
    public int getPositionOfNthCopyOfCardWithCardIdInDeck (int cardId, int n) {
        int counter=0;
        if (numberOfCards>0) {
            for (int index = numberOfCards; index >= 1; index--){
                if (Cards[index].cardId==cardId) {
                    counter++;
                    if (counter==n) {return index;}
                }
            }
        }
        return 0;
    }
    
    // returns how often a card with a certain ID appears in a Deck/GY/DD (useful for testing applicability of GY effects)
    public int numberOfCardsWithCardIdInDeck (int cardIdInDeck) {
        int counter=0;
        if (numberOfCards>0) {
            for (int index = numberOfCards; index >= 1; index--){
                if (Cards[index].cardId==cardIdInDeck) {counter++;}
            }
        }
        return counter;
    }
    
    // combined method that returns a card in a given deck with a given card ID
    public YCard getCardInDeckByCardID (int cardId) {
        return Cards[getPositionOfCardWithCardIdInDeck(cardId)];
    }
    
    // returns the nth card of a given deck as object
    public YCard getNthCardOfDeck (int n){
        if (n > numberOfCards){
            YuGiOhJi.debugDialog("Error: out of bounds in getNthCardOfDeck(...); max. number: " + numberOfCards + ". attempted N: " + n); return Card.NoCard;
        }
        else if (n > 39 || n < 1) {
            YuGiOhJi.debugDialog("Error: out of bounds in getNthCardOfDeck(...); attempted N: " + n); return Card.NoCard;
        }
        else {
            return Cards[n];
        }
    }
    
    // returns the last (uppermost) card of a given deck as object (in order to less repeat oneself)
    public YCard getLastCardOfDeck(){
        return Cards[numberOfCards];
    }
    
    // sets the nth card of a given deck equal to a specific card
    public void setNthCardOfDeckToCard (YCard ConsCard, int n){
        if (n > 39 || n < 1) {
            YuGiOhJi.debugDialog("Error: out of bounds in setNthCardOfDeckToCard(...); attempted N: " + n);
        }
        else {
            Cards[n]=ConsCard;
        }
    }
    
    // --- BOOKKEEPING part3: MOVING CARDS FROM OR TO DECK/GY/DD ---
    
    // banishes nth card of graveyard of a given player
    // (i.e. copy card to different dimension, then deletes nth card)
    public static void banishNthCardFromGY (int n, boolean isBelongingToPlayer) {
        addCardToGYOrDD(getGY(isBelongingToPlayer).Cards[n], isBelongingToPlayer, false);
        getGY(isBelongingToPlayer).deleteNthCardOfDeckAndRearrange(n, true);
    }
    
    // moves the nth card from a given deck to the hand of a given player (and updates look of both: hand and deck)
    public static void addNthCardFromDeckToHand (int n, boolean isBelongingToPlayer) { // case of full hand already taken care of elsewhere
            Hand.addCardToHand(getDeck(isBelongingToPlayer).Cards[n], isBelongingToPlayer);
            getDeck(isBelongingToPlayer).deleteNthCardOfDeckAndRearrange(n, false);
    }
    
    // moves the nth card from a given deck to the GY of a given player (milling a card, Foolish Burial effect)
    public static void addNthCardFromDeckToGY (int n, boolean isBelongingToPlayer) {
        addCardToGYOrDD(getDeck(isBelongingToPlayer).Cards[n], isBelongingToPlayer, true);
        getDeck(isBelongingToPlayer).deleteNthCardOfDeckAndRearrange(n, false);
    }
    
    // moves the nth card from a given graveyard to the hand of a given player 
    public static void addNthCardFromGYToHand (int n, boolean isBelongingToPlayer) {
        Hand.addCardToHand(getGY(isBelongingToPlayer).Cards[n], isBelongingToPlayer);
        getGY(isBelongingToPlayer).deleteNthCardOfDeckAndRearrange(n, true);
    }
    
    // takes a card and adds it to the graveyard or different dimension
    // and updates the appearance of the GY resp. DD accordingly
    public static void addCardToGYOrDD (YCard ConsCard, boolean isBelongingToPlayer, boolean toGY) {
        if (isBelongingToPlayer) {
            if (toGY) { // to player's GY
                GYDeckPlayer.numberOfCards++;
                GYDeckPlayer.Cards[GYDeckPlayer.numberOfCards]=ConsCard;
                YuGiOhJi.setDeckButtonIcon(GYDeckPlayer.Cards[GYDeckPlayer.numberOfCards].upMonster.cardPathAtt, 5, true);
            }
            else { // send to player's different dimension
                DDDeckPlayer.numberOfCards++;
                DDDeckPlayer.Cards[DDDeckPlayer.numberOfCards]=ConsCard;
                YuGiOhJi.setDeckButtonIcon(DDDeckPlayer.Cards[DDDeckPlayer.numberOfCards].upMonster.cardPathAtt, 4, true);
            }
        }
        else { // card in graveyard and DD of the CPU appear upside down
            if (toGY) { // to CPU's GY
                GYDeckCPU.numberOfCards++;
                GYDeckCPU.Cards[GYDeckCPU.numberOfCards]=ConsCard;
                YuGiOhJi.setDeckButtonIcon(GYDeckCPU.Cards[GYDeckCPU.numberOfCards].lowMonster.cardPathAtt, 2, true);
            }
            else { // send to CPU's different dimension
                DDDeckCPU.numberOfCards++;
                DDDeckCPU.Cards[DDDeckCPU.numberOfCards]=ConsCard;
                YuGiOhJi.setDeckButtonIcon(DDDeckCPU.Cards[DDDeckCPU.numberOfCards].lowMonster.cardPathAtt, 3, true);
            }
        }
        YuGiOhJi.updateDisplayedCardNumbers();
    }
    
    // deletes the nth card of a deck, and if it was not the last card (which is highly likely)
    // it takes the last one and puts it at the empty place
    // (this can happen due to the search effect of "Card Grabber" or by banishing monster from GY, or by getting a card from GY)
    public void deleteNthCardOfDeckAndRearrange (int n, boolean isInGY) {
        setNthCardOfDeckToCard(Card.NoCard, n); // delete nth card of deck
        if (n<numberOfCards) { // if it was not the last card, rearrange
            Cards[n]=Cards[numberOfCards];
        }
        numberOfCards--;// decrease number of card cards on hand by one
        if (isInGY) { // update appearance of GY
            if (isBelongingToPlayer) {
                if (numberOfCards==0) { // if no more card in GY, then all cards there disappear
                    YuGiOhJi.buttonGYPlayer.setIcon(null);
                }
                else { // set look of GY to upper most card in GY
                    YuGiOhJi.setDeckButtonIcon(Cards[numberOfCards].upMonster.cardPathAtt, 5, true);
                }
            }
            else {
                if (numberOfCards==0) { // if no more card in GY, then all cards there disappear
                    YuGiOhJi.buttonGYCPU.setIcon(null);
                }
                else { // set look of GY to upper most card in GY
                    YuGiOhJi.setDeckButtonIcon(Cards[numberOfCards].lowMonster.cardPathAtt, 2, true);
                }
            }
        }
        else {
            if (numberOfCards==0) { // if it was the last card (unlikely, but possible), make deck disappear
                if (isBelongingToPlayer) {
                    YuGiOhJi.buttonDeckPlayer.setIcon(null);
                }
                else {
                    YuGiOhJi.buttonDeckCPU.setIcon(null);
                }
            }
        }
        YuGiOhJi.updateDisplayedCardNumbers();
    }
    
    // --- BOOKKEEPING part4: LOOKING UP certain CARDS IN DECK/GY/DD ---
    
    // returns true, if a given deck/GY/DD contains a MOOK
    public boolean hasMook() {
        if (numberOfCards>=1) {
            for (int index = numberOfCards; index >= 1; index--){
                if (Cards[index].isContainingMook()) {return true;}
            }
        }
        return false;
    }
    
    // returns true, if a given deck/GY/DD contains a MIDBOSS
    public boolean hasMidboss() {
        if (numberOfCards>=1) {
            for (int index = numberOfCards; index >= 1; index--){
                if (Cards[index].isContainingMidboss()) {return true;}
            }
        }
        return false;
    }
    
    // returns the card number of the strongest MOOK in a given GY
    // returns zero, if there is no MOOK in the GY
    public int getCardNoOfStrongestMookInGY() {
        int cardNoInGY;
        cardNoInGY = getPositionOfCardWithCardIdInDeck(Card.ModeChangerExhaustedExecutioner.cardId);
        if (cardNoInGY==0) {getPositionOfCardWithCardIdInDeck(Card.SlickRusherRecklessRusher.cardId);}
        if (cardNoInGY==0) {getPositionOfCardWithCardIdInDeck(Card.BigBackBouncerBanisher.cardId);}
        if (cardNoInGY==0) {getPositionOfCardWithCardIdInDeck(Card.GodKillingSpearLance.cardId);}
        if (cardNoInGY==0) {getPositionOfCardWithCardIdInDeck(Card.NecromancerBackBouncer.cardId);}
        if (cardNoInGY==0) {getPositionOfCardWithCardIdInDeck(Card.DiamondSwordShield.cardId);}
        if (cardNoInGY==0) {getPositionOfCardWithCardIdInDeck(Card.AttackStopperBuggedUpgrade.cardId);}
        if (cardNoInGY==0) {getPositionOfCardWithCardIdInDeck(Card.CopyCatCardGrabber.cardId);}
        if (cardNoInGY==0) {getPositionOfCardWithCardIdInDeck(Card.MonsterStealerSteepLearningCurve.cardId);}
        if (cardNoInGY==0) {getPositionOfCardWithCardIdInDeck(Card.DemonGodDemon.cardId);}
        return cardNoInGY;
    }
    
    // returns the card number of the strongest MIDBOSS in a given GY
    // returns zero, if there is no MIDBOSS in the GY
    public int getCardNoOfStrongestMidbossInGY() {
        int cardNoInGY;
        cardNoInGY = getPositionOfCardWithCardIdInDeck(Card.IncorruptibleHolyLance.cardId);
        if (cardNoInGY==0) {getPositionOfCardWithCardIdInDeck(Card.BigBackBouncerBanisher.cardId);}
        if (cardNoInGY==0) {getPositionOfCardWithCardIdInDeck(Card.FlakshipNapalm.cardId);}
        if (cardNoInGY==0) {getPositionOfCardWithCardIdInDeck(Card.NecromancerBackBouncer.cardId);}
        if (cardNoInGY==0) {getPositionOfCardWithCardIdInDeck(Card.ModeChangerExhaustedExecutioner.cardId);}
        if (cardNoInGY==0) {getPositionOfCardWithCardIdInDeck(Card.SlickRusherRecklessRusher.cardId);}
        if (cardNoInGY==0) {getPositionOfCardWithCardIdInDeck(Card.BigBanisherBurner.cardId);}
        if (cardNoInGY==0) {getPositionOfCardWithCardIdInDeck(Card.AttackStopperBuggedUpgrade.cardId);}
        if (cardNoInGY==0) {getPositionOfCardWithCardIdInDeck(Card.DiamondSwordShield.cardId);}
        if (cardNoInGY==0) {getPositionOfCardWithCardIdInDeck(Card.NeutraliserSkillStealer.cardId);}
        if (cardNoInGY==0) {getPositionOfCardWithCardIdInDeck(Card.CopyCatCardGrabber.cardId);}
        return cardNoInGY;
    }
    
    // returns the card number of the strongest ENDBOSS in a given GY
    // returns zero, if there is no ENDBOSS in the GY
    public int getCardNoOfStrongestEndbossInGY() {
        int cardNoInGY;
        cardNoInGY = getPositionOfCardWithCardIdInDeck(Card.GodKillingSpearLance.cardId);
        if (cardNoInGY==0) {getPositionOfCardWithCardIdInDeck(Card.NeutraliserSkillStealer.cardId);}
        if (cardNoInGY==0) {getPositionOfCardWithCardIdInDeck(Card.EradicatorObstacle.cardId);}
        if (cardNoInGY==0) {getPositionOfCardWithCardIdInDeck(Card.BigAttackStopperSword.cardId);}
        if (cardNoInGY==0) {getPositionOfCardWithCardIdInDeck(Card.MonsterStealerSteepLearningCurve.cardId);}
        if (cardNoInGY==0) {getPositionOfCardWithCardIdInDeck(Card.BigBurnerSuicideCommando.cardId);}
        return cardNoInGY;
    }
    
    
    
    
}
