package yugiohji;

/**
 * This class creates YuGiOhJi-equip-cards.
 * They are YuGiOhJi-cards with some more properties.
 * Equip cards are supposed to know, whom they are controlled by,
 * whom they belong to and if they are negated.
 * They don't have to know where inside a stack they are
 * or where that stack is on the field.
 * This is something the stack has to know.
 * 
 */

import static yugiohji.YuGiOhJi.NoCard;

// One could maybe extend YCards and just add the properties.
// But it is enough to have the card one of several properties of the equip card,
// like cards and monsters are just properties of summoned monsters.

public class EquipCard {
    
    public YCard Card; // the YuGiOhJi card itself
    public boolean isPlayersEquipCard; // owner of the equip card
    public boolean isNegated; // true, if the equip card has been negated and thus has been put face down
    
    public boolean isOriginallyPlayersEquipCard; // needed solely because of the effects of Monster Stealer
    
    // It would be very useful for the YChooseCardWindow, if every equip card "knows" what Stack it is in (the owner and the number of the Stack).
    // Use EStack.updateEquipCardStackNumberAndOwner() to set these two values correctly to all equip cards inside a given stack.
    public int stackNumber;
    public boolean isPlayersStack;
    
    // interestingly, it does not remember where exactly inside a stack the card is positioned, because that might change anyway
            
    // constructor
    public EquipCard (YCard Card, boolean isPlayersEquipCard, boolean isOriginallyPlayersEquipCard, boolean isNegated, int stackNumber, boolean isPlayersStack)
    {
        this.Card=Card;
        this.isPlayersEquipCard=isPlayersEquipCard;
        this.isNegated=isNegated;
        
        this.isOriginallyPlayersEquipCard=isOriginallyPlayersEquipCard;
        
        this.stackNumber=stackNumber;
        this.isPlayersStack=isPlayersStack;
    }
    
    // standard constructor for non-existing equip card
    public EquipCard (boolean isBelongingToPlayer, int stackNumber)
    {
        this.Card=NoCard;
        this.isPlayersEquipCard=isBelongingToPlayer;
        this.isNegated=false;
        
        this.isOriginallyPlayersEquipCard=isBelongingToPlayer;
        
        this.stackNumber=stackNumber;
        this.isPlayersStack=isBelongingToPlayer;
    }
    
    // another constructor for non-existing equip card
    public EquipCard (boolean isBelongingToPlayer)
    {
        this.Card=NoCard;
        this.isPlayersEquipCard=isBelongingToPlayer;
        this.isNegated=false;
        
        this.isOriginallyPlayersEquipCard=isBelongingToPlayer;
        
        this.stackNumber=0;
        this.isPlayersStack=isBelongingToPlayer;
    }
    
}
