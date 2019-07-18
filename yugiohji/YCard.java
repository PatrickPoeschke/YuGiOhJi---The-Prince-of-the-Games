package yugiohji;

/**
 * This class creates YuGiOhJi-cards.
 * They basically consist of two monsters:
 * The upper and the lower monster
 * (both as an Object from the class YMonster)
 * as well as some other properties in order to quickly evaluate,
 * if a card can do certain things, when being in a certain part of the game.
 * Also this class has still some usefull methods
 * having to do with playing cards from the hand.
 * 
 */

import static yugiohji.YuGiOhJi.NoMonster;
import static yugiohji.YuGiOhJi.Game;

import static yugiohji.YuGiOhJi.GodBarrier;
import static yugiohji.YuGiOhJi.EradicatorObstacle;
import static yugiohji.YuGiOhJi.GodKillingSpearLance;
import static yugiohji.YuGiOhJi.MonsterStealerSteepLearningCurve;
import static yugiohji.YuGiOhJi.NeutraliserSkillStealer;
import static yugiohji.YuGiOhJi.CopyCatCardGrabber;
import static yugiohji.YuGiOhJi.AttackStopperBuggedUpgrade;
import static yugiohji.YuGiOhJi.BigAttackStopperSword;
import static yugiohji.YuGiOhJi.DiamondSwordShield;
import static yugiohji.YuGiOhJi.ModeChangerExhaustedExecutioner;
import static yugiohji.YuGiOhJi.SlickRusherRecklessRusher;
import static yugiohji.YuGiOhJi.IncorruptibleHolyLance;
import static yugiohji.YuGiOhJi.NecromancerBackBouncer;
import static yugiohji.YuGiOhJi.BigBackBouncerBanisher;
import static yugiohji.YuGiOhJi.BigBanisherBurner;
import static yugiohji.YuGiOhJi.BigBurnerSuicideCommando;
import static yugiohji.YuGiOhJi.FlakshipNapalm;
import static yugiohji.YuGiOhJi.DemonGodDemon;
import static yugiohji.YuGiOhJi.NoCard;

public class YCard {
    
    // instance variables (different for every card)
    public int cardId; // ID of the card for identifying everything about it (probably not needed)
    public String cardName; // name of the monster (just for readability) use ID for everything
        
    public boolean hasNegateEffectOnHand; // if true, then can negate an attack/effect on hand
    
    public boolean hasEffectInGY; // if true, then can special summon itself from graveyard  
    
    // options of what one can do in own turn with upper monster
    public boolean canUpMonsterNormSummon; // if true, then upper monster can in principle be normal/tribute summoned/set
    
    public boolean canUpMonsterSpecSummon; // if true, then upper monster can be special summoned
    public boolean canUpMonsterEquip; // if true, then upper monster can equip a monster
    
    public YMonster upMonster; // get the upper monster as an object for this property
    
    // analog for lower monster
    public boolean canLowMonsterNormSummon; // if true, then lower monster can in principle be normal/tribute summoned/set
    
    public boolean canLowMonsterSpecSummon; // if true, then lower monster can be special summoned
    public boolean hasEquipEffect; // if true, then lower monster can equip a monster
    
    public YMonster lowMonster; // get the lower monster as an object for this property
    public String bigCardPath; // path to the big version of the card as a string
    
    // constructor 
    public YCard (int cardId, String cardName, boolean hasNegateEffectOnHand, boolean hasEffectInGY, boolean canUpMonsterNormSummon, boolean canUpMonsterSpecSummon, boolean canUpMonsterEquip, YMonster upMonster, boolean canLowMonsterNormSummon, boolean canLowMonsterSpecSummon, boolean hasEquipEffect, YMonster lowMonster, String bigCardPath)
    {
        this.cardId = cardId;
        this.cardName = cardName;
        
        this.hasNegateEffectOnHand = hasNegateEffectOnHand;
        
        this.hasEffectInGY = hasEffectInGY;
        
        this.canUpMonsterNormSummon = canUpMonsterNormSummon;
        
        this.canUpMonsterSpecSummon = canUpMonsterSpecSummon;
        this.canUpMonsterEquip = canUpMonsterEquip;
        
        this.upMonster = upMonster;
        
        this.canLowMonsterNormSummon = canLowMonsterNormSummon;
        
        this.canLowMonsterSpecSummon = canLowMonsterSpecSummon;
        this.hasEquipEffect = hasEquipEffect;
        
        this.lowMonster = lowMonster;
        this.bigCardPath = bigCardPath;
        
    }
    
    // returns true, if a card contains a monster with a copyable effect 
    public boolean hasStealableEffect() {
        return (upMonster.hasStealableEffect() || lowMonster.hasStealableEffect());
    }
    
    // standard constructor for empty cards
    public YCard () {
        this.cardId = 0;
        this.cardName = "NoCard";
        this.hasNegateEffectOnHand = false;
        this.hasEffectInGY = false;
        this.canUpMonsterNormSummon = false;
        this.canUpMonsterSpecSummon = false;
        this.canUpMonsterEquip = false;
        this.upMonster = NoMonster;
        this.canLowMonsterNormSummon = false;
        this.canLowMonsterSpecSummon = false;
        this.hasEquipEffect = false;
        this.lowMonster = NoMonster;
        this.bigCardPath = "/images/YuGiOhJiFacedown.png";
    }
    
    // -- methods for card effects on hand (including summoning) --
    // the monster effects on the field however should be put into the class SummnedMonster
    
    // I consider two cards equal, if they have the same card ID.
    // (for more readable code, because card names used, and more efficiency, because no string comparison)
    public boolean equals (YCard Card) {
        return (cardId==Card.cardId);
    }
    
    // --- all about SUMMONING in general ---
    
    // sets the nth card of a given hand (or GY) to a summoned monster
    public static void setCardToNthSumMonster (YCard Card, boolean isConcerningUpperMonster, boolean isFaceDown, boolean isInAttackMode, int zoneNumber, boolean isInGY, boolean isBelongingToPlayer)
    {
        if (zoneNumber>=1 && zoneNumber<=5) {
            SummonedMonster.getNthSummonedMonster(zoneNumber, isBelongingToPlayer).updateMonsterPropertiesWhenSummoning(Card, isConcerningUpperMonster, isFaceDown, isInAttackMode);
            // change appearance of summoned monster accordingly
            if (isFaceDown) {
                if (isInAttackMode) {
                    YuGiOhJi.setMonsterButtonIcon("/images/YuGiOhJiFacedown.png", zoneNumber, isBelongingToPlayer, true, true);
                }
                else {
                    YuGiOhJi.setMonsterButtonIcon("/images/YuGiOhJiFacedown.png", zoneNumber, isBelongingToPlayer, false, true);
                }
            }
            else { // upper monster of player and lower monster of CPU appear correctly (else upside down)
                if ((isBelongingToPlayer && isConcerningUpperMonster) || (!isBelongingToPlayer && !isConcerningUpperMonster)) {
                    if (isInAttackMode) {
                        YuGiOhJi.setMonsterButtonIcon(Card.upMonster.cardPathAtt, zoneNumber, isBelongingToPlayer, true, true);
                    }
                    else {
                        YuGiOhJi.setMonsterButtonIcon(Card.upMonster.cardPathDef, zoneNumber, isBelongingToPlayer, false, true);
                    }
                }
                else {
                    if (isInAttackMode) {
                        YuGiOhJi.setMonsterButtonIcon(Card.lowMonster.cardPathAtt, zoneNumber, isBelongingToPlayer, true, true);
                    }
                    else {
                        YuGiOhJi.setMonsterButtonIcon(Card.lowMonster.cardPathDef, zoneNumber, isBelongingToPlayer, false, true);
                    }
                }
                // since face up, make changed attack and defence values visible
                SummonedMonster.getNthSummonedMonster(zoneNumber, isBelongingToPlayer).updateAttDefDisplay();
            }
            if (isInGY) {
                Deck.getGY(isBelongingToPlayer).deleteNthCardOfDeckAndRearrange(Deck.getGY(isBelongingToPlayer).getPositionOfCardWithCardIdInDeck(Card.cardId), true);
            }
            else {
                Hand.getHand(isBelongingToPlayer).deleteNthCardOnHandAndRearrange(Hand.getHand(isBelongingToPlayer).getPositionOfCardWithCardId(Card.cardId), isBelongingToPlayer);
            }
            YuGiOhJi.updateDisplayedAttDefValues();
            YuGiOhJi.rescaleEverything(); // update all graphical elements (needed, because empty monster card zones are invisible and thus are not rescaled yet (thanks to Java))
        }
    }
    
    
    // --- all about NORMAL SUMMONING ---
    
    // for normal summoning a monster
    public static void normalSummonMonster (YCard Card, int cardNumberOnHand, boolean isConcerningUpperMonster) {
        int stars;
        if (isConcerningUpperMonster) {
            stars = Card.upMonster.stars;
        }
        else {
            stars = Card.lowMonster.stars;
        }
        if (stars==1) {
            SummonedMonster TargetMonster = SummonedMonster.determineFreeMonsterZone(true);
            if (TargetMonster.sumMonsterNumber > 0 ) {
                setCardToNthSumMonster(Card, isConcerningUpperMonster, false, true, TargetMonster.sumMonsterNumber, false, true);
                Game.hasStillNormalSummonPlayer=false;
            }
        } // tribute summon monster
        else if (stars==2) { // one does not lose the normal summon yet, because one can still make up one's mind and opt-out
            YMonster.tributeSummonMidbossActivate(cardNumberOnHand, isConcerningUpperMonster);
        }
        else if (stars==3) {
            YMonster.tributeSummonEndbossActivate(cardNumberOnHand, isConcerningUpperMonster);
        }
        else if (stars==4) {
            YMonster.tributeSummonGodActivate(cardNumberOnHand, isConcerningUpperMonster);
        }
        
    }
    
    // for setting a monster
    public static void normalSetMonster (YCard Card, int cardNumberOnHand, boolean isConcerningUpperMonster) {
        int stars;
        if (isConcerningUpperMonster) {
            stars = Card.upMonster.stars;
        }
        else {
            stars = Card.lowMonster.stars;
        }
        if (stars==1) {
            SummonedMonster TargetMonster = SummonedMonster.determineFreeMonsterZone(true);
            if (TargetMonster.sumMonsterNumber > 0 ) {
                setCardToNthSumMonster(Card, isConcerningUpperMonster, true, false, TargetMonster.sumMonsterNumber, false, true);
                Game.hasStillNormalSummonPlayer=false;
            }
        } // tribute summon monster
        else if (stars==2) {
            Game.hasStillNormalSummonPlayer=false;
            YMonster.tributeSetMidbossActivate(cardNumberOnHand, isConcerningUpperMonster);
        }
        else if (stars==3) {
            Game.hasStillNormalSummonPlayer=false;
            YMonster.tributeSetEndbossActivate(cardNumberOnHand, isConcerningUpperMonster);
        }
        else if (stars==4) {
            Game.hasStillNormalSummonPlayer=false;
            YMonster.tributeSetGodActivate(cardNumberOnHand, isConcerningUpperMonster);
        }
        
    }
    
    // --- all about SPECIAL SUMMONING ---
    
    // for special summoning a monster
    // supposed to be called, after one has paid the cost for special summoning
    public static void specialSummonMonster (YCard Card, boolean isConcerningUpperMonster, boolean isInGY) {
        SummonedMonster TargetMonster = SummonedMonster.determineFreeMonsterZone(true);
        if (TargetMonster.sumMonsterNumber > 0 ) {
            int intDialogResult = YuGiOhJi.multipleChoiceDialog("Which mode shall the monster be summoned in?", "Choose mode", new String[]{"face up attack mode", "face up defence mode"}, "face up attack mode");
            if (intDialogResult==0) {
                setCardToNthSumMonster(Card, isConcerningUpperMonster, false, true, TargetMonster.sumMonsterNumber, isInGY, true);
            }
            else {
                setCardToNthSumMonster(Card, isConcerningUpperMonster, false, false, TargetMonster.sumMonsterNumber, isInGY, true);
            }
        }
    }
    
    // --- about card management at the END OF TURN ---
    
    // maybe move all hand related methods to the corresponding file about hands?
    // also maybe move all methods related to equip cards to corresponding file about equip cards?
    // No, keep all methods about returning cards back to the hand here.
    
    // is being called at the end of a turn
    // and checks for "spirit monster effects" (i.e. monsters returning to the hand at end of turn)
    // i.e. Reckless Rusher or a monster that has copied its effect
    public static void checkForSpritEffectsAndFullHand() {
        boolean isReadyForTurnChange=true;
        if (Hand.getHand(Game.isPlayersTurn).numberOfCards==10) {
            isReadyForTurnChange=false;
            discardCardAtEndOfTurn(Game.isPlayersTurn);
        }
        else if (Hand.getHand(!Game.isPlayersTurn).numberOfCards==10) {
            isReadyForTurnChange=false;
            discardCardAtEndOfTurn(!Game.isPlayersTurn);
        }
        else {
            for (int index = 1; index <= 5; index++){
                SummonedMonster SumMonster = SummonedMonster.getNthSummonedMonster(index, Game.isPlayersTurn);
                if (SumMonster.isReturningItselfToHand())
                {
                    boolean isBelongingToPlayer = SumMonster.isOriginallyPlayersMonster;
                    if (Hand.getHand(isBelongingToPlayer).numberOfCards==10) {
                        discardCardAtEndOfTurn(isBelongingToPlayer);
                        isReadyForTurnChange=false;
                        break;
                    }
                     else {
                        returnCardBackToHand(SummonedMonster.getNthSummonedMonster(index, Game.isPlayersTurn));
                        isReadyForTurnChange=false;
                        Game.endPhase();
                        break;
                    }
                }
                SumMonster = SummonedMonster.getNthSummonedMonster(index, !Game.isPlayersTurn);
                if (SumMonster.isReturningItselfToHand())
                {
                    boolean isBelongingToPlayer = SumMonster.isOriginallyPlayersMonster;
                    if (Hand.getHand(isBelongingToPlayer).numberOfCards==10) {
                        isReadyForTurnChange=false;
                        discardCardAtEndOfTurn(isBelongingToPlayer);
                        break;
                    }
                    else {
                        returnCardBackToHand(SummonedMonster.getNthSummonedMonster(index, !Game.isPlayersTurn));
                        isReadyForTurnChange=false;
                        Game.endPhase();
                        break;
                    }
                }
            }
        }
        if (isReadyForTurnChange) {
            Game.isInEndPhase=false;
            Game.endPhase();
        }
    }
    
    // called, if one has 10 cards on the hand, at the end of a turn: is asking to discard a card
    // also called, if one has 10 cards on the hand and another is added, because of an effect
    public static void discardCardAtEndOfTurn (boolean isConcerningPlayer) {
        if (isConcerningPlayer) {
            YuGiOhJi.informationDialog("You have to discard a card.", "too many cards on hand");
            discardCardOfFullHand(true);
        }
        else {
            int cardNumber = AIdelegate.cpuChooseCardToDiscard();
            discardACardExecute(cardNumber, false);
            Game.endPhase();
        }
    }
    
    // puts a summoned monster card on the field back to the hand of its original owner
    public static void returnCardBackToHand (SummonedMonster SumMonster) {
        YuGiOhJi.informationDialog("adding card " + SumMonster.Card.cardName + " to hand", "");
        Hand.addCardToHand(SumMonster.Card, SumMonster.isOriginallyPlayersMonster);
        SumMonster.deleteMonster();
    }
    
    // forces a player to discard a card in the middle of a turn, because one has a full hand and one has gets a card bounced back
    public static void discardCardOfFullHand (boolean isConcerningPlayer) {
        Game.isActDiscarding = true;
        if (isConcerningPlayer) {
            YuGiOhJi.informationDialog("Discard a card. After confirming click on the card you want to discard.", "too many cards on hand");
        }
        else {
            int cardNumber = AIdelegate.cpuChooseCardToDiscard();
            attemptToDiscardACard(cardNumber, false);
        }
    }
    
    // puts an equip card on the field back to the hand
    public static void returnEquipCardBackToHand (EStack Stack, int numberInStack) {
        boolean isBelongingToPlayer = Stack.getOriginalOwnerOfNthEquipCard(numberInStack);
        if (Hand.getHand(isBelongingToPlayer).numberOfCards==10) {
            discardCardOfFullHand(isBelongingToPlayer);
        }
        else {
            YuGiOhJi.informationDialog("adding card " + Game.ActEffStack.getNthCardOfStack(Game.actEffCardNo).cardName + " to hand", "");
            Hand.addCardToHand(Stack.getNthCardOfStack(numberInStack), isBelongingToPlayer);
            Stack.deleteNthCardInEquipStackAndRearrange(numberInStack);
        }
    }
    
    // discards the attempted card (card number on hand), if one is allowed to do so
    // calls whatever function is needed to let the game go on
    public static void attemptToDiscardACard (int cardNumber, boolean isConcerningPlayer) {
        if (Game.isActEffBigBackBouncer()) {
            if (Game.ActEffMonTarget.sumMonsterNumber!=0) { // bouncing back a monster
                discardACardExecute(cardNumber, isConcerningPlayer);
                YCard.returnCardBackToHand(Game.ActEffMonTarget); // whom the monster originally belonged to is already checked in that method
                Game.deactivateCurrentEffects();
            }
            else if (Game.ActEffStack.numberOfCards!=0) {
                discardACardExecute(cardNumber, isConcerningPlayer);
                YCard.returnEquipCardBackToHand(Game.ActEffStack, Game.actEffCardNo); // whom the equip card originally belonged to is already checked in that method
                Game.deactivateCurrentEffects();
            }
            else {
                discardACardExecute(cardNumber, isConcerningPlayer);
                YuGiOhJi.informationDialog("adding card " + Deck.getGY(Game.isAboutPlayerActEff).getNthCardOfDeck(Game.actEffCardNo).cardName + " to hand", "");
                Deck.addNthCardFromGYToHand(Game.actEffCardNo, Game.isAboutPlayerActEff);
                Game.deactivateCurrentEffects();
            }        
        }
        else if (Game.isActEffBackBouncer()) {
            discardACardExecute(cardNumber, isConcerningPlayer);
            YMonster.effectBackBouncerExecute(); // the deactivation of the effect happens at the end of executing the effect
        }
        else if (Game.actEffCardNo!=0) { // generic search from deck
            discardACardExecute(cardNumber, isConcerningPlayer);
            Deck.addNthCardFromDeckToHand(Game.actEffCardNo, Game.isAboutPlayerActEff);
            Game.deactivateCurrentEffects();
        }
        // don't add specific hand traps here (they are mostly in the interrupts classes)
        // same for discarding any card as cost for a hand trap effect on field
        else if (Game.isInEndPhase) { // discarding at the end of the turn (because one has too many cards on the hand, no matter if spirit effect or not)
            discardACardExecute(cardNumber, isConcerningPlayer);
            Game.endPhase();
        }
        // in case it is the turn of the CPU, ask CPU to continue playing
        if (!Game.isPlayersTurn) {
            AIdelegate.cpuContinuesPlayingTurn();
        }
        // in case it is the turn of the player, nothing happens (the player can simply continue playing)
    }
    
    // in order not to repeat oneself, out-source here the code of the actual discarding, because of too many cards on the hand
    public static void discardACardExecute (int cardNumber, boolean isConcerningPlayer) {
        if (!isConcerningPlayer) {
            YMonster.revealNthHandCard(cardNumber, isConcerningPlayer);
            YuGiOhJi.informationDialog("computer discards card " + Hand.getHand(false).getNthCardOfHand(cardNumber).cardName, "");
        }
        Hand.discardCard(cardNumber, isConcerningPlayer);
        Game.isActDiscarding=false; // end process of discarding a card
    }
    
    // --- about checking card properties ---
    
    // returns true, if either the upper monster of a given card, or lower monster (or both) is a MOOK
    public boolean isContainingMook() {
        return (lowMonster.stars==1 || upMonster.stars==1);
    }
    
    // returns the amount of burn damage, if the card was tributed for the effect of Burner
    public int extractMookBurnDamage() {
        if (!isContainingMook()) {
            return 0;
        }
        else {
            int attValue = 0;
            if (lowMonster.stars==1) {
                attValue=lowMonster.att;
            }
            if (upMonster.stars==1 && upMonster.att > attValue) {
                attValue=upMonster.att;
            }
            return attValue;
        }
    }
    
    // returns true, if either the upper monster of a given card, or lower monster (or both) is a MIDBOSS
    public boolean isContainingMidboss() {
        return (upMonster.stars==2 || lowMonster.stars==2);
    }
    
    // returns true, if either the upper monster of a given card, or lower monster (or both) is a GOD
    public boolean isContainingGod() {
        return (upMonster.stars==4 || lowMonster.stars==4);
    }
    
    // returns true, if the card can be searched by the card Slick Rusher - Reckless Rusher
    public boolean isBanishBounceOrBurnCard() {
        return (equals(NecromancerBackBouncer) || equals(BigBackBouncerBanisher) || equals(BigBanisherBurner) || equals(BigBurnerSuicideCommando));
    }
    
    // returns the card ID of the card a given monster (with given monster ID) beongs to
    // for the inverse: (card to monster) see YMonster.getMonsterById(monsterId).upMonster.monsterId
    // and YMonster.getMonsterById(monsterId).lowMonster.monsterId
    public static int getCardIdByMonsterId (int monsterId) {
        switch (monsterId) {
            case 0: return 0; // i.e. NoCard: in case one needs some card properties of a non-existing monster
            case 1: return 2;
            case 2: return 1;
            case 3: return 17;
            case 4: return 10;
            case 5: return 11;
            case 6: return 4;
            case 7: return 9;
            case 8: return 8;
            case 9: return 3;
            case 10: return 7;
            case 11: return 16;
            case 12: return 13;
            case 13: return 14;
            case 14: return 6;
            case 15: return 6;
            case 16: return 5;
            case 17: return 17;
            case 18: return 12;
            case 19: return 10;
            case 20: return 13;
            case 21: return 15;
            case 22: return 14;
            case 23: return 15;
            case 24: return 9;
            case 25: return 11;
            case 26: return 12;
            case 27: return 7;
            case 28: return 4;
            case 29: return 16;
            case 30: return 2;
            case 31: return 8;
            case 32: return 5;
            case 33: return 3;
            case 34: return 1;
            case 35: return 18;
            case 36: return 18;
            default: YuGiOhJi.debugDialog("Error: out of bounds in YCard.getCardIdByMonsterId(...); attempted Id: " + monsterId); return 0;
        }
    }
    
    // returns true, if the entered monster ID belongs to a card on which the corresonding monster is the upper monster
    public static boolean monsterIdBelongsToUpperMonster (int monsterId) {
        YCard Card = YCard.getCardByCardId(YCard.getCardIdByMonsterId(monsterId));
        return (Card.upMonster.monsterId==monsterId);
    }
    
    // returns the card belonging to a given card ID
    public static YCard getCardByCardId (int cardId) {
        switch (cardId) {
            case 1: return GodBarrier;
            case 2: return EradicatorObstacle;
            case 3: return GodKillingSpearLance;
            case 4: return MonsterStealerSteepLearningCurve;
            case 5: return NeutraliserSkillStealer;
            case 6: return CopyCatCardGrabber;
            case 7: return AttackStopperBuggedUpgrade;
            case 8: return BigAttackStopperSword;
            case 9: return DiamondSwordShield;
            case 10: return ModeChangerExhaustedExecutioner;
            case 11: return SlickRusherRecklessRusher;
            case 12: return IncorruptibleHolyLance;
            case 13: return NecromancerBackBouncer;
            case 14: return BigBackBouncerBanisher;
            case 15: return BigBanisherBurner;
            case 16: return BigBurnerSuicideCommando;
            case 17: return FlakshipNapalm;
            case 18: return DemonGodDemon;
            default: YuGiOhJi.debugDialog("Error: out of bounds in YCard.getCardByCardId(...); attempted Id: " + cardId); return NoCard;
        }
        
    }
    
    
}
