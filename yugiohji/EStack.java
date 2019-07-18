package yugiohji;

/**
 * This class creates stacks of YuGiOhJi-equip-cards.
 * For simplicity there are only 10 cards allowed on each stack.
 * 
 * Each object just takes 10 YCard objects as properties.
 * Also it has an interger telling how many actual cards there are in it.
 * The rest are just placeholder cards.
 * 
 * Also here are some methods for managing equip cards.
 * 
 * This is an improved version of the file using arrays of cards
 * instead of single cards as properties. However,
 * it also contains an unused zeroth array element
 * consisting of the "NoCard"-placeholder,
 * just so that the arrays are not off by one any more.
 * 
 */

import static yugiohji.YuGiOhJi.EquipStack1CPU;
import static yugiohji.YuGiOhJi.EquipStack2CPU;
import static yugiohji.YuGiOhJi.EquipStack3CPU;
import static yugiohji.YuGiOhJi.EquipStack4CPU;
import static yugiohji.YuGiOhJi.EquipStack5CPU;
import static yugiohji.YuGiOhJi.EquipStack2Player;
import static yugiohji.YuGiOhJi.EquipStack1Player;
import static yugiohji.YuGiOhJi.EquipStack3Player;
import static yugiohji.YuGiOhJi.EquipStack4Player;
import static yugiohji.YuGiOhJi.EquipStack5Player;
import static yugiohji.YuGiOhJi.Game;
import static yugiohji.YuGiOhJi.Mon;

public class EStack {
    
    public int numberOfCards; // a number from 1 to 10 counting how many cards one actually has in a stack (excluding the placeholder cards)
    public boolean isBelongingToPlayer; // true, if it is an equip stack of the player (false if it belongs to CPU)
    public int stackNumber; // a number to identify the pile of cards (together with isBelongingToPlayer)
    public EquipCard[] EquipCards;// = new EquipCard[11]; // array of 10 equip cards (+1 placeholder such that the array is not off by one any more)

    // constructor
    public EStack (int numberOfCards, boolean isBelongingToPlayer, int stackNumber, EquipCard EquipCard1, EquipCard EquipCard2, EquipCard EquipCard3, EquipCard EquipCard4, EquipCard EquipCard5, EquipCard EquipCard6, EquipCard EquipCard7, EquipCard EquipCard8, EquipCard EquipCard9, EquipCard EquipCard10)
    {
        this.numberOfCards=numberOfCards;
        this.isBelongingToPlayer=isBelongingToPlayer;
        this.stackNumber=stackNumber;
        this.EquipCards = new EquipCard[11];
        this.EquipCards[0]=new EquipCard(isBelongingToPlayer); // leave the zeroth element de facto empty and add another card at the end, such that the array is not off by one any more
        this.EquipCards[1]=EquipCard1;
        this.EquipCards[2]=EquipCard2;
        this.EquipCards[3]=EquipCard3;
        this.EquipCards[4]=EquipCard4;
        this.EquipCards[5]=EquipCard5;
        this.EquipCards[6]=EquipCard6;
        this.EquipCards[7]=EquipCard7;
        this.EquipCards[8]=EquipCard8;
        this.EquipCards[9]=EquipCard9;
        this.EquipCards[10]=EquipCard10;
    }

    // standard constructor for empty stacks
    public EStack (boolean isBelongingToPlayer, int stackNumber)
    {
        this.numberOfCards=0;
        this.isBelongingToPlayer=isBelongingToPlayer;
        this.stackNumber=stackNumber;
        this.EquipCards = new EquipCard[11];
        for (int index = 0; index <= 10; index++){
            EquipCards[index]=new EquipCard(isBelongingToPlayer, stackNumber);
        }
    }
    
    // -- methods for moving cards in hands and equip stacks --
    
    // --- BOOKKEEPING part1: general SETTER and GETTER methods ---
    
    // returns the nth equip stack of a given player
    public static EStack getNthStack (int n, boolean isBelongingToPlayer) {
        if (isBelongingToPlayer) {
            switch (n) {
                case 1: return EquipStack1Player;
                case 2: return EquipStack2Player;
                case 3: return EquipStack3Player;
                case 4: return EquipStack4Player;
                case 5: return EquipStack5Player;
                default: YuGiOhJi.debugDialog("Error: out of bounds in getNthStack(...); attempted N: " + n); return null;
            }
        }
        else {
            switch (n) {
            case 1: return EquipStack1CPU;
            case 2: return EquipStack2CPU;
            case 3: return EquipStack3CPU;
            case 4: return EquipStack4CPU;
            case 5: return EquipStack5CPU;
            default: YuGiOhJi.debugDialog("Error: out of bounds in getNthStack(...); attempted N: " + n); return null;
            }
        }
        
    }
    
    // deletes all cards within an equip stack
    public void erase() {
        numberOfCards=0;
        for (int index = 0; index <= 10; index++){
            EquipCards[index]=new EquipCard(isBelongingToPlayer, stackNumber);
        }
    }
    
    // If one adds an equip card to a stack and forgets to set these two unimportant properties, then use this method. (seemingly not needed though)
    public void updateEquipCardStackNumberAndOwner() {
        if (numberOfCards>=1) {
            for (int index = numberOfCards; index >= 1; index--){
                EquipCards[index].stackNumber=stackNumber;
                EquipCards[index].isPlayersStack=isBelongingToPlayer;
            }
        }
    }
    
    // returns the position of last card with a given ID in a given equip stack as integer
    // (Since cards with the same ID are identical, it doesn't matter which one.
    // However, if it is the last one that is supposed to move somewhere else,
    // one does not need to rearrange, thus saving computation.)
    // Unfortunately, we also have to check, if the card is belonging to the right player
    // and if it is negated, because one can only sacrifice own cards,
    // and it makes a difference, if one wants to sacrifice a useless negated card or a still useful equip card.
    public int getPositionOfEquipCardByCardID (int cardId, boolean isPlayerCard, boolean isNegated) {
        if (numberOfCards >= 1) {
            for (int index = numberOfCards; index >= 1; index--){
                EquipCard ECard = EquipCards[index];
                if (ECard.Card.cardId==cardId && ECard.isPlayersEquipCard==isPlayerCard && ECard.isNegated==isNegated) {
                    return index;
                }
            }
        }
        return 0; //  this case should not occur, since one should only ask for a card, if one knows it will definitely be there
    }
    
    // combined method that returns a card in a given stack with a given card ID, owner and negation status
    public YCard getCardInStackByCardID (int cardId, boolean isPlayerCard, boolean isNegated) {
        return EquipCards[getPositionOfEquipCardByCardID(cardId, isPlayerCard, isNegated)].Card;
    }
    // returns the nth card of a given equip stack as object
    public EquipCard getNthEquipCardOfStack (int n){
        if (n > numberOfCards){
            YuGiOhJi.debugDialog("Error: out of bounds in getNthEquipCardOfStack(...); max. number: " + numberOfCards + ". attempted N: " + n); return (new EquipCard(isBelongingToPlayer));
        }
        else if (n > 10 || n < 1) {
            YuGiOhJi.debugDialog("Error: out of bounds in getNthEquipCardOfStack(...); attempted N: " + n); return (new EquipCard(isBelongingToPlayer));
        }
        else {
            return EquipCards[n];
        }
    }
    
    // returns the nth card of a given equip stack as object
    public YCard getNthCardOfStack (int n){
        return EquipCards[n].Card;
    }
    
    // sets the nth equip card of a given stack equal to a specific equip card
    public void setNthEquipCardOfStackToEquipCard (EquipCard ECard, int n){
        if (n > 10 || n < 1) {
            YuGiOhJi.debugDialog("Error: out of bounds in setNthEquipCardOfStackToEquipCard(...); attempted N: " + n);
        }
        else {
            EquipCards[n]=ECard;
        }
    }
    
    // returns true, if the nth card of a given equip stack is controlled by the player
    public boolean getControllerOfNthEquipCard (int n){
        return EquipCards[n].isPlayersEquipCard;
    }
    
    // sets the original owner of an equip stack (usually the one who controls the equip card, but because of monster stealer, that doesn't have to be true)
    public void setOriginalOwnerOfNthEquipCard (int n, boolean isBelongingToPlayer){
        EquipCards[n].isOriginallyPlayersEquipCard=isBelongingToPlayer;
    }
    
    // returns true, if the nth card of a given equip stack originally belonged to the player
    public boolean getOriginalOwnerOfNthEquipCard (int n){
        return EquipCards[n].isOriginallyPlayersEquipCard;
    }
    
    // returns true, if the nth card of a given equip stack has its effect negated
    public boolean getNegationStatusOfNthEquipCard (int n){
        return EquipCards[n].isNegated;
    }
    
    // sets, if the nth card of a given equip stack has its effect negated
    public void setNegationStatusOfNthEquipCard (int n, boolean isNegated){
        EquipCards[n].isNegated=isNegated;
    }
    
    // --- BOOKKEEPING part2: adding and erasing equip cards or whole stacks ---
    
    // moves an entire equip stack accordingly, when a summonend monster changes its owner (the cards inside it still keep their controllers and original owners)
    // one still has to erase the original equip stack outside of this method
    public static void copyStackWhileStealingMonster (EStack OldStack, EStack NewStack) {
        if (OldStack.numberOfCards>0) {
            for (int index = 1; index <= OldStack.numberOfCards; index++){
                copyNthEquipCardWhileStealingMonster(OldStack, NewStack, index);
            }
            NewStack.updateLookOfEquipStack(); // update the appearance of new equip stack
        }
    }
    //  in order not to repeat oneself, out-source some part of the code for stealing a stack here
    public static void copyNthEquipCardWhileStealingMonster (EStack OldStack, EStack NewStack, int n) {
        EquipCard ECard = OldStack.EquipCards[n];
        NewStack.addEquipCardToStack(ECard);
    }
    
    // equips a summoned monster with a card
    public static void equipMonster (SummonedMonster SumMonster, YCard Card, boolean isPlayersEquipCard, boolean isOriginallyPlayersEquipCard) {
        if (Card.lowMonster.equals(Mon.BuggedUpgrade) && isPlayersEquipCard) {YMonster.negatedPreventively();} // just for statistics (if one used Bugged Upgrade to negate opponent)
        getNthStack(SumMonster.sumMonsterNumber, SumMonster.isPlayersMonster).addCardToStack(Card, false, isPlayersEquipCard, isOriginallyPlayersEquipCard);
    }
    
    // adds a card to an equip stack (useful either from hand or from field)
    public void addCardToStack (YCard Card, boolean isNegated, boolean isPlayersEquipCard, boolean isOriginallyPlayersEquipCard) {
        EquipCard ECard = new EquipCard(Card, isPlayersEquipCard, isOriginallyPlayersEquipCard, isNegated, stackNumber, isBelongingToPlayer);
        addEquipCardToStack(ECard);
    }
    
    // adds an equip card to an equip stack
    public void addEquipCardToStack (EquipCard ECard) {
        numberOfCards++;
        EquipCards[numberOfCards]=ECard;
        if (!EquipCards[numberOfCards].isNegated) { // This branch is needed for copying an entire equip stack, when stealing a monster. Then one needs to "add" a negated equip card to the new stack.
            SummonedMonster.getNthSummonedMonster(stackNumber, isBelongingToPlayer).updateMonsterPropertiesWhenEquipping(EquipCards[numberOfCards].Card); // update properties and abilities
        }
        updateLookOfEquipStack();// update appearance of stack
    }
    
    // moves a card from a given stack with given card ID and given negation status to the graveyard of its owner
    public void sendEquipCardToGY (int cardIdInStack, boolean isNegated, boolean isPlayersCard) {
        if (numberOfCards>=1) {
            for (int index = numberOfCards; index >= 1; index--){
                EquipCard ECard = EquipCards[index];
                if (ECard.Card.cardId==cardIdInStack && ECard.isNegated==isNegated && ECard.isPlayersEquipCard==isPlayersCard) {
                    Deck.addCardToGYOrDD(ECard.Card, ECard.isOriginallyPlayersEquipCard, true);
                    deleteNthCardInEquipStackAndRearrange(index);
                    break;
                }
            }
        }
    }
    
    // sends all equipping cards of an equip stack to the graveyard
    public void sendAllEquipCardsToGY() {
        if (numberOfCards>0) {
            YuGiOhJi.informationDialog("sending equip card(s) to GY", "");
            for (int index = numberOfCards; index >= 1 ; index--){
                sendNthEquipCardToGY(index);
            }
        }
    }
    
    // moves the nth card from a given equip stack to the GY
    public void sendNthEquipCardToGY (int n) {
        if (numberOfCards>=n) {
            EquipCard ECard = EquipCards[n];
            Deck.addCardToGYOrDD(ECard.Card, ECard.isOriginallyPlayersEquipCard, true);
            deleteNthCardInEquipStackAndRearrange(n);
        }
    }
    
    // out-source some card managing operations to this method
    // takes the nth card in stack and copies it to the mth position
    // also carries all information about the card with it
    public void rearrangeEquipStack (int m, int n) {
        EquipCards[n]=EquipCards[m];
    }
    
    // sets the properies of the nth card in a given equip stack to standard values
    // i.e. its effects not negated and belonging to the player who controls the stack
    // (useful for setting the properties of a deleted card in a stack)
    public void resetNthEquipCardProperties (int n) {
        setNthEquipCardOfStackToEquipCard(new EquipCard(isBelongingToPlayer, stackNumber), n);
    }
    
    // deletes a card in an equip stack,
    // also puts the last card at the now empty slot if needed
    // also changes the appearance of the stack if needed
    public void deleteNthCardInEquipStackAndRearrange (int n) {
        downgradeUnequippedMonster(n); // consider possible debuffs, when unequipping
        resetNthEquipCardProperties(n); // delete nth card in stack
        if (n < numberOfCards) { // if it was not the last card, rearrange
            rearrangeEquipStack(numberOfCards, n); // copies last card and put it to empty place
            resetNthEquipCardProperties(numberOfCards); // delete last card
            numberOfCards--;
            updateLookOfEquipStack(); // update appearance of stack
        }
        else { // if it was uppermost card in stack
            if (n==1) { // let equip button disappear
                numberOfCards--;
                if (isBelongingToPlayer) {
                    switch (stackNumber) {
                        case 1: YuGiOhJi.buttonEquip1Player.setIcon(null); break;
                        case 2: YuGiOhJi.buttonEquip2Player.setIcon(null); break;
                        case 3: YuGiOhJi.buttonEquip3Player.setIcon(null); break;
                        case 4: YuGiOhJi.buttonEquip4Player.setIcon(null); break;
                        case 5: YuGiOhJi.buttonEquip5Player.setIcon(null); break;
                        default: YuGiOhJi.debugDialog("Error: out of bounds in deleteNthCardInEquipStackAndRearrange(...); attempted equip stack: " + stackNumber); break;
                    }
                }
                else {
                    switch (stackNumber) {
                        case 1: YuGiOhJi.buttonEquip1CPU.setIcon(null); break;
                        case 2: YuGiOhJi.buttonEquip2CPU.setIcon(null); break;
                        case 3: YuGiOhJi.buttonEquip3CPU.setIcon(null); break;
                        case 4: YuGiOhJi.buttonEquip4CPU.setIcon(null); break;
                        case 5: YuGiOhJi.buttonEquip5CPU.setIcon(null); break;
                        default: YuGiOhJi.debugDialog("Error: out of bounds in deleteNthCardInEquipStackAndRearrange(...); attempted equip stack: " + stackNumber); break;
                    }
                }
            }
            else {
                numberOfCards--;
                updateLookOfEquipStack(); // update appearance of stack
            }
        }
    }
    
    // takes away possible buffs, when an equip card from an equip stack is deleted/negated
    public void downgradeUnequippedMonster (int n) {
        SummonedMonster SumMonster = SummonedMonster.getNthSummonedMonster(stackNumber, isBelongingToPlayer);
        downgradeUnequippedMonster(n, SumMonster);
    }
    
    // this version is needed to debuff a monster while stealing it (because then the summoned monster has a different stack than usual)
    public void downgradeUnequippedMonster (int n, SummonedMonster SumMonster) {
        if (numberOfCards>=n && n>0) {
            EquipCard ECard = EquipCards[n];
            if (!ECard.isNegated || SumMonster.isImmune()){ // only do something, if the equip card was not negated or monster is not immune (otherwise it had no effect anyway)
                if (ECard.Card.lowMonster.equals(Mon.Shield)) {
                    SumMonster.def = SumMonster.def - Mon.Shield.ShieldDefBoost();
                }
                else if (ECard.Card.lowMonster.equals(Mon.Sword)) {
                    SumMonster.att = SumMonster.att - Mon.Sword.SwordAttBoost();
                } // for Lance doesn't has to be checked, because that happens during battle phase anyway (for Demon neither, because doesn't do anything anyway)
                else if (ECard.Card.lowMonster.equals(Mon.BuggedUpgrade)) { // look, if there is another Bugged Upgrade without negated effect in the stack (if yes, deleting this one doesn't change anything) [actually there should only be one non-negated Bugged Upgrade anyway, but keep this part just to be safe]
                    boolean isHavingOtherWorkingBuggedUpgrade=false;
                    for (int index = numberOfCards; index >= 1; index--){
                        ECard = EquipCards[index];
                        if (index!=n && ECard.Card.lowMonster.equals(Mon.BuggedUpgrade) && !ECard.isNegated) {
                            isHavingOtherWorkingBuggedUpgrade=true; break;
                        }
                    }
                    if (!isHavingOtherWorkingBuggedUpgrade) {SumMonster.isNotAbleToUseItsEffects=false;}
                }
                if (SumMonster.isKnownToPlayer()) { // update appearing stats, if needed
                    SumMonster.updateAttDefDisplay();
                }
            }
        }
    }
    
    // takes away the buffs of all equip monsters a given monster is equipped with
    public void completeDebuff (SummonedMonster SumMonster) {
        if (numberOfCards>=1) {
            for (int index = numberOfCards; index >= 1; index--){
                downgradeUnequippedMonster(index, SumMonster);
            }
        }
    }
    
    // changes the appearance of the equip stack
    // simply by looking whom the uppermost card belongs to (and if its effects are negated or not)
    public void updateLookOfEquipStack() {
        EquipCard UppermostEquipCard = EquipCards[numberOfCards];
        if (numberOfCards==0) { // make non-existing stack disappear
            YuGiOhJi.setEquipButtonIcon("/images/YuGiOhJiFacedown.png", stackNumber, isBelongingToPlayer, false);
        }
        else if (UppermostEquipCard.isNegated) {
            YuGiOhJi.setEquipButtonIcon("/images/YuGiOhJiFacedown.png", stackNumber, isBelongingToPlayer, true);
        }
        else {
            if (UppermostEquipCard.isPlayersEquipCard) { // consider that, in case of player the monster card is upside down
                YuGiOhJi.setEquipButtonIcon(UppermostEquipCard.Card.lowMonster.cardPathAtt, stackNumber, isBelongingToPlayer, true);
            }
            else {
                YuGiOhJi.setEquipButtonIcon(UppermostEquipCard.Card.upMonster.cardPathAtt, stackNumber, isBelongingToPlayer, true);
            }
        }
        YuGiOhJi.updateDisplayedEquipCardNumbers();
        YuGiOhJi.rescaleEverything(); // also update the number displayed right next to the stack (also belongs to the looks), also rescaling needed, since potential empty equip card zones are invisible and thus not rescaled yet (thanks to Java)
    }
    
    // --- BOOKKEEPING part3: CHECKING usability, COUNTING and LOOKING for certain cards ---
    
    // returns how many equip monsters cards one controls
    // (keep in mind, that one can also equip monsters of the opponent
    // thus cards one controls might be in the equip stack of the opponent) 
    // with an additional boolean one can count only the the equip cards that have not been negated 
    public static int countOwnEquipMonsters (boolean isBelongingToPlayer, boolean isNotNegated) {
        int numberOfCards=0;
        for (int index = 1; index <= 5; index++){
            numberOfCards = numberOfCards + getNthStack(index, isBelongingToPlayer).countOwnCardsInStack(isBelongingToPlayer, isNotNegated);
            numberOfCards = numberOfCards + getNthStack(index, !isBelongingToPlayer).countOwnCardsInStack(isBelongingToPlayer, isNotNegated);
        }
        return numberOfCards;
    }
    
    // counts how many cards one controls in a given equip stack
    // with an additional boolean one can count only the the equip cards that have not been negated 
    public int countOwnCardsInStack (boolean isBelongingToPlayer, boolean isNotNegated) {
        int counter=0;
        if (numberOfCards>0) {
            for (int index = numberOfCards; index >= 1; index--){
                EquipCard ECard = EquipCards[index];
                if (isNotNegated) {
                    if (ECard.isPlayersEquipCard==isBelongingToPlayer && !ECard.isNegated) {
                        counter++;
                    }
                }
                else {
                    if (ECard.isPlayersEquipCard==isBelongingToPlayer) {counter++;}
                }

            }
        }
        return counter;
    }
    
    // returns true, if a given equip stack contains a non-negated card
    public boolean hasNonNegatedEquipCard() {
        if (numberOfCards>0) {
            for (int n = numberOfCards; n >= 1; n--){
                if (!EquipCards[n].isNegated) {
                    return true;
                }
            }
        }
        return false;
    }
    
    // looks everywhere on the field for equip monsters with effects one can copy (returns true, if existing)
    public static boolean lookForCopyableEffectOfEquipCards() {
        for (int index = 1; index <= 5; index++){
            if (getNthStack(index, true).lookForCopyableEffectInStack()) {return true;}
            if (getNthStack(index, false).lookForCopyableEffectInStack()) {return true;}
        }
        return false;
    }
    
    // looks in a stack for equip monsters with effects one can copy (returns true, if existing)
    // Since Skill Stealer needs face up cards, only count the non-negated ones. (negated equip cards are face down)
    public boolean lookForCopyableEffectInStack() {
        if (numberOfCards>=1) {
            for (int index = numberOfCards; index >= 1; index--){
                if (isCopyableEquipCard(index)); {return true;}
            }
        }
        return false;
    }
    
    // returns true, if the nth card in a stack is an equip monster with a copyable effect
    public boolean isCopyableEquipCard (int cardNumberInStack) {
        EquipCard ECard = EquipCards[cardNumberInStack];
        return (ECard.Card.lowMonster.isEquipMonsterAndHasCopyableEffect() && !ECard.isNegated);
    }
    
    // return true, if "Lance" exists in a given stack
    public boolean lookForLanceInStack (boolean isNotNegated) {
        return lookForCardWithMonsterIdInStack(Mon.Lance.monsterId, isNotNegated);
    }
    
    // returns true if "Bugged Upgrade" exists somewhere in an equip stack
    public static boolean lookForBuggedUpgrade (boolean isNotNegated) {
        return lookForCardWithMonsterId(Mon.BuggedUpgrade.monsterId, isNotNegated);
    }
    
    // return true, if "Bugged Upgrade" exists in a given stack
    public boolean lookForBuggedUpgradeInStack (boolean isNotNegated) {
        return lookForCardWithMonsterIdInStack(Mon.BuggedUpgrade.monsterId, isNotNegated);
    }
    
    // returns true if "Shield" exists somewhere in an equip stack
    public static boolean lookForShield (boolean isNotNegated) {
        return lookForCardWithMonsterId(Mon.Shield.monsterId, isNotNegated);
    }
    
    // returns true if a card with a given monster Id as lower monster exists somewhere in an equip stack
    public static boolean lookForCardWithMonsterId (int monsterId, boolean isNotNegated) {
        for (int index = 1; index <= 5; index++){
            if (getNthStack(index, true).lookForCardWithMonsterIdInStack(monsterId, isNotNegated)) {return true;}
            if (getNthStack(index, false).lookForCardWithMonsterIdInStack(monsterId, isNotNegated)) {return true;}
        }
        return false;
    }
    
    // return true, if a card with a given monster Id as lower monster exists in a given stack (and if it is not negatd, if one wishes)
    public boolean lookForCardWithMonsterIdInStack (int monsterId, boolean isNotNegated)
    {
        if (numberOfCards>=1) {
            if (isNotNegated) {
                for (int index = numberOfCards; index >= 1; index--){
                    EquipCard ECard = EquipCards[index];
                    if (ECard.Card.lowMonster.monsterId==monsterId && !ECard.isNegated) {return true;}
                }
            }
            else {
                for (int index = numberOfCards; index >= 1; index--){
                    EquipCard ECard = EquipCards[index];
                    if (ECard.Card.lowMonster.monsterId==monsterId) {return true;}
                }
            }
        }
        return false;
    }
    
    // player plays an equip card, if one is allowed to do so
    public static void attemptPlayEquipCard (EStack Stack, int cardNumberInStack)
    {// if it is card of right player, there are suitable targets, and the card itself has its effects not negated
        EquipCard ECard = Stack.EquipCards[cardNumberInStack];
        if (ECard.isPlayersEquipCard && !ECard.isNegated) {
            if (ECard.Card.lowMonster.equals(Mon.Demon)) { // Demon (has equip effect and special summon effect)
                boolean canUseEquipEffect = Stack.canUseEquipEffectFromStack(cardNumberInStack);
                boolean canUseSpecialSummon = SummonedMonster.determineFreeMonsterZone(true).sumMonsterNumber>0;
                if (canUseEquipEffect && canUseSpecialSummon) {
                    int intDialogResult = YuGiOhJi.multipleChoiceDialog("Which effect do you want to use?", "Choose effect", new String[]{"equip a monster", "special summon this monster"}, "");
                    if (intDialogResult==0) {
                        YMonster.equipEffectFromStackActivate(Stack, cardNumberInStack);
                    }
                    else if (intDialogResult==1) {
                        YMonster.specialSummonDemonFromStack(Stack, cardNumberInStack);
                    }
                }
                else if (canUseEquipEffect && !canUseSpecialSummon) {
                    YMonster.equipEffectFromStackActivate(Stack, cardNumberInStack);
                }
                else if (!canUseEquipEffect && canUseSpecialSummon) {
                    YMonster.specialSummonDemonFromStack(Stack, cardNumberInStack);
                }
            }
            else {
                if (Stack.canUseEquipEffectFromStack(cardNumberInStack)) {
                    YMonster.equipEffectFromStackActivate(Stack, cardNumberInStack);
                }
            }
        }
    }
    
    // checks, if one is able to play an equip card from an equip stack
    // (in case of Bugged Upgrade there has to be a negatable monster (an effect monster that has its effects not negated yet))
    public boolean canUseEquipEffectFromStack (int n)
    {
        if (numberOfCards>0) {
            EquipCard ECard = EquipCards[n];
            boolean isPlayersEquipCard = ECard.isPlayersEquipCard;
            if (isPlayersEquipCard==Game.isPlayersTurn) { // only do something if one wants to play own card
                int numberOfMonsters=SummonedMonster.countOwnSummonedMonsters(true)+SummonedMonster.countOwnSummonedMonsters(false);
                if (numberOfMonsters>=2) { // there has to be at least one other summoned monster than the card it is already equipping (stack number&owner not same as summoned monster number&owner: that is the condition added to the usual equippability)
                    SummonedMonster SumMonster;
                    for (int index = 1; index <= 5; index++){ // check own summoned monsters
                        SumMonster = SummonedMonster.getNthSummonedMonster(index, isPlayersEquipCard);
                        if ( SumMonster.canBeEquippedBy(ECard.Card.lowMonster) && !stackIsBelongingToMonster(SumMonster) ) {
                            return true;
                        } // check summoned monsters of opponent
                        SumMonster = SummonedMonster.getNthSummonedMonster(index, !isPlayersEquipCard);
                        if ( SumMonster.canBeEquippedBy(ECard.Card.lowMonster) && !stackIsBelongingToMonster(SumMonster) ) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    // returns true, if a given stack is right behind a given summoned monster (true, if owners and positions coincide)
    public boolean stackIsBelongingToMonster (SummonedMonster SumMonster) {
        return (SumMonster.isPlayersMonster==isBelongingToPlayer && SumMonster.sumMonsterNumber==stackNumber);
    }
    
    
}
