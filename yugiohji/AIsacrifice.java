package yugiohji;

/**
 * For convenience summarise all possible sacrifices (cards from hand/monster/equip/GY) in one object, called sacrifice.
 * Whenever the computer wants to use an effect and has to pay for it,
 * it passes one or more sacrifices as arguments to the effect methods (in class file AIEffects).
 * This class also contains many methods for changing between different formats of cards
 * (marking/remembering as a sacrifice, extracting a card from a sacrifice)
 * and for saving and getting stored sacrifices.
 * 
 */

import static yugiohji.YuGiOhJi.GYDeckCPU;
import static yugiohji.YuGiOhJi.Game;
import static yugiohji.YuGiOhJi.HandCPU;

public class AIsacrifice {
    
    public static AIsacrifice CurrentCPUSacrifice1 = new AIsacrifice(); // needed for the CPU paying costs
    public static AIsacrifice CurrentCPUSacrifice2 = new AIsacrifice();
    public static AIsacrifice CurrentCPUSacrifice3 = new AIsacrifice();
    public static AIsacrifice CurrentCPUSacrifice4 = new AIsacrifice();
    
    public static int maxBurnDamage; // needed to decide which card to sacrifice for Burner (I out-sourced some part of the source code into another method, so that I need this static variable here.)
    public static int currentBurnDamage;
    
    public int cardIdOnHand; // if CPU wants to discard a card as sacrifice, this is its card ID (if not, it's zero)
    
    public int monsterNumber; // if CPU wants to tribute one of its own monsters, this is its number (if not, it's zero)
    
    public int cardIdInGY; // if CPU wants to banish a card from its graveyard, this is its card ID (if not, it's zero)
    
    public EStack Stack; // if CPU wants to tribute an own equip card, this is the equip stack it is in (if not, it is not an existing stack)
    public int cardIdInStack; // if CPU wants to tribute an own equip card, this is its card ID (if not, it's zero)
    public boolean negationStatusOfEquipCard; // if CPU wants to tribute an own equip card, also consider the right negation status (true, if negated)
    
    // to summarise: when making a sacrifice, the 4 integers get checked first
    // if they are ok (only one of them not zero), it is considered a valid sacrifice (if cardIdInStack==0 the non-integer types get ignored)
    
    // constructor
    public AIsacrifice (int cardIdOnHand, int monsterNumber, int cardIdInGY, EStack Stack, int cardIdInStack, boolean negationStatusOfEquipCard)
    {
        this.cardIdOnHand=cardIdOnHand;
        this.monsterNumber=monsterNumber;
        this.cardIdInGY=cardIdInGY;
        
        this.Stack=Stack;
        this.cardIdInStack=cardIdInStack;
        this.negationStatusOfEquipCard=negationStatusOfEquipCard;
    }
    
    // standard constructor for empty sacrifices
    public AIsacrifice (){
        this.cardIdOnHand=0;
        this.monsterNumber=0;
        this.cardIdInGY=0;
        
        this.Stack = new EStack(false, 0);
        this.cardIdInStack=0;
        this.negationStatusOfEquipCard=false;
    }
    
    // --- methods ---
    
    // returns true, if two sacrifices are essentially the same
    // This is extremely important for e.g. not tributing very specific cards, like the ones one want to use/summon.
    // Then one decodes the card as sacrifice and looks, if it is the same or not.
    // (Note, that the way the cards are encoded, this method is a very conservative and will return true,
    // if they are already the same kind of card in the same part of the game.
    // But this is ok. It prevents errors very safely.)
    // This method has the nice feature, that it automatically returns false, when the sacrifice in the argument is invalid.
    // This has the great advantage that methods, that check, if certain cards are not to be sacrifficed can still be used,
    // when one can simply sacrifice anything. Then one simply passes a new empty sacrifice (no card not to be sacrificed) in the argument. 
    public boolean isSameKind (AIsacrifice Sacrifice) {
        if (sacrificeIsValidHandCard()) {
            return (cardIdOnHand == Sacrifice.cardIdOnHand);
        }
        else if (sacrificeIsValidMonster()) {
            return (monsterNumber == Sacrifice.monsterNumber);
        }
        else if (sacrificeIsValidGYCard()) {
            return (cardIdInGY == Sacrifice.cardIdInGY);
        }
        else if (sacrificeIsValidEquipCard()) {
            return (Stack == Sacrifice.Stack && cardIdInStack == Sacrifice.cardIdInStack && negationStatusOfEquipCard == Sacrifice.negationStatusOfEquipCard);
        }
        return false;
    }
    
    // --- BOOKKEEPING part1: converting between sacrifices and types it consists of ---
    
    // returns true, if an attempted new sacrifice is definitely new (i.e. has not the same card ID and is in the same hand/GY/stack/zone)
    public boolean isDefinitelyNewSacrifice() {
        if (isValidSacrifice()) {
            if (CurrentCPUSacrifice1.isValidSacrifice() && isSameKind(CurrentCPUSacrifice1)) {
                return false;
            }
            else if (CurrentCPUSacrifice2.isValidSacrifice() && isSameKind(CurrentCPUSacrifice2)) {
                return false;
            }
            else if (CurrentCPUSacrifice3.isValidSacrifice() && isSameKind(CurrentCPUSacrifice3)) {
                return false;
            }
            else if (CurrentCPUSacrifice4.isValidSacrifice() && isSameKind(CurrentCPUSacrifice4)) {
                return false;
            }
            return true;
        }
        return false;
    }
    
    // used to save the sacrifice in the memory
    // (the other methods below are there for declaring the decision on which card to sacrifice)
    // use like : rememberSacrifice(markAsSacrifice(int cardId, boolean isInGY));
    public static void rememberSacrifice (AIsacrifice Sacrifice) {
        if (Sacrifice.isValidSacrifice()) {
            if (!CurrentCPUSacrifice1.isValidSacrifice()) {
                CurrentCPUSacrifice1 = Sacrifice;
            }
            else if (!CurrentCPUSacrifice2.isValidSacrifice()) {
                CurrentCPUSacrifice2 = Sacrifice;
            }
            else if (!CurrentCPUSacrifice3.isValidSacrifice()) {
                CurrentCPUSacrifice3 = Sacrifice;
            }
            else if (!CurrentCPUSacrifice4.isValidSacrifice()) {
                CurrentCPUSacrifice4 = Sacrifice;
            }
            else { // This case is not supposed to happen! Here there are already 4 defined sacrifices, but the game assumes that the last remembered sacrifices have simply not been deleted yet and overwrites them.
                YuGiOhJi.debugDialog("Error in rememberSacrifice(...); previous remembered sacrifices of the CPU have not been forgotten yet.");
                forgetAllSacrifices();
                CurrentCPUSacrifice1 = Sacrifice;
            }
        }
        else {
            YuGiOhJi.debugDialog("Error in rememberSacrifice(...); CPU chose an invalid target as sacrifice.");
        }
    }
    
    // used to delete all memory of sacrifices (must be used at the beginning of any new attempt to use an effect)
    public static void forgetAllSacrifices() {
        CurrentCPUSacrifice1 = new AIsacrifice();
        CurrentCPUSacrifice2 = new AIsacrifice();
        CurrentCPUSacrifice3 = new AIsacrifice();
        CurrentCPUSacrifice4 = new AIsacrifice();
    }
    
    // used so that one does not have to import the stored sacrifices in other files
    public static AIsacrifice getNthSacrifice (int n) {
        switch(n) {
            case 1: return CurrentCPUSacrifice1;
            case 2: return CurrentCPUSacrifice2;
            case 3: return CurrentCPUSacrifice3;
            case 4: return CurrentCPUSacrifice4;
            default: YuGiOhJi.debugDialog("Error: out of bounds in getNthSacrifice(...); attempted N: " + n); return new AIsacrifice();
        }
    }
    
    // a series of methods with same for defining sacrifices: Always try to use these methods!
    // This first method allows to define cards on the hand and in the GY of the CPU as sacrifices:
    // Enter the card ID and if it is in the GY (if not, it is on the hand).
    public static AIsacrifice markAsSacrifice (int cardId, boolean isInGY) {
        if (isInGY) {return markGYCardAsSacrificeByCardId(cardId);}
        else {return markHandCardAsSacrificeByCardId(cardId);}
    }
    // allows to define a monster of CPU as a tribute
    public static AIsacrifice markAsSacrifice (SummonedMonster SumMonster) {
        return markMonsterAsSacrifice(SumMonster);
    }
    // allows to define an equip card of CPU as sacrifice:
    // Enter the Stack it is in and its cardID.
    // The method will try to mark a fitting negated card first. If that doesn't exist, it marks a fitting non-negated card.
    public static AIsacrifice markAsSacrifice (EStack Stack, int cardId) {
        int position = Stack.getPositionOfEquipCardByCardID(cardId, false, true);
        if (position!=0) {return markEquipCardAsSacrificeByStackIdAndNegationStatus(Stack, cardId, true);}
        else {return markEquipCardAsSacrificeByStackIdAndNegationStatus(Stack, cardId, false);}
    }
    
    // returns a sacrifice consisting of a hand card by entering the card number on the computer's hand
    public static AIsacrifice markHandCardAsSacrificeByCardNumber (int cardNumberOnHand) {
        int cardIdOnHand = HandCPU.getNthCardOfHand(cardNumberOnHand).cardId;
        AIsacrifice Sacrifice = new AIsacrifice(cardIdOnHand, 0, 0, new EStack(false, 0), 0, false);
        return Sacrifice;
    }
    
    // returns a sacrifice consisting of a computer's hand card by entering the card ID
    public static AIsacrifice markHandCardAsSacrificeByCardId (int cardIdOnHand) {
        return markHandCardAsSacrificeByCardNumber(HandCPU.getPositionOfCardWithCardId(cardIdOnHand));
    }
    
    // returns a sacrifice consisting of a computer's summoned monster by entering the summoned monster number
    public static AIsacrifice markMonsterAsSacrificeByMonsterNumber (int monsterNumber) {
        AIsacrifice Sacrifice = new AIsacrifice(0, monsterNumber, 0, new EStack(false, 0), 0, false);
        return Sacrifice;
    }
    
    // returns a sacrifice consisting of a computer's summoned monster by entering the summoned monster
    public static AIsacrifice markMonsterAsSacrifice (SummonedMonster SumMonster) {
        AIsacrifice Sacrifice = new AIsacrifice(0, SumMonster.sumMonsterNumber, 0, new EStack(false, 0), 0, false);
        return Sacrifice;
    }
    
    // returns a sacrifice consisting of a graveyard card by entering the card number in the computer's graveyard
    public static AIsacrifice markGYCardAsSacrificeByCardNumber (int cardNumberInGY) {
        int cardIdInGY = GYDeckCPU.getNthCardOfDeck(cardNumberInGY).cardId;
        AIsacrifice Sacrifice = new AIsacrifice(0, 0, cardIdInGY, new EStack(false, 0), 0, false);
        return Sacrifice;
    }
    
    // returns a sacrifice consisting of a computer's graveyard card by entering the card ID
    public static AIsacrifice markGYCardAsSacrificeByCardId (int cardIdInGY) {
        return markGYCardAsSacrificeByCardNumber(GYDeckCPU.getPositionOfCardWithCardIdInDeck(cardIdInGY));
    }
    
    // returns a sacrifice consisting of an equip card by entering the stack it is in and its card number
    public static AIsacrifice markEquipCardAsSacrificeByCardNumber (EStack Stack, int cardNumberInStack) {
        int cardIdInStack = Stack.getNthCardOfStack(cardNumberInStack).cardId;
        boolean negationStatusOfEquipCard = Stack.getNegationStatusOfNthEquipCard(cardNumberInStack);
        AIsacrifice Sacrifice = new AIsacrifice(0, 0, 0, Stack, cardIdInStack, negationStatusOfEquipCard);
        return Sacrifice;
    }
    
    // returns a sacrifice consisting of an equip card by entering the stack it is in, its card ID and if it is negated
    public static AIsacrifice markEquipCardAsSacrificeByStackIdAndNegationStatus (EStack Stack, int cardId, boolean isNegated) {
        AIsacrifice Sacrifice = new AIsacrifice(0, 0, 0, Stack, cardId, isNegated);
        return Sacrifice;
    }
    
    // returns the card that is supposed to be sacrificed (better only insert valid sacrifices here!) useful for burn effects
    public YCard extractCardFromValidSacrifice() {
        if (sacrificeIsValidHandCard()) {
            return HandCPU.getCardOnHandByCardID(cardIdOnHand);
        }
        else if (sacrificeIsValidMonster()) {
            return SummonedMonster.getNthSummonedMonster(monsterNumber, false).Card;
        }
        else if (sacrificeIsValidGYCard()) {
            return GYDeckCPU.getCardInDeckByCardID(cardIdInGY);
        }
        else if (sacrificeIsValidEquipCard()) {
            return Stack.getCardInStackByCardID(cardIdInStack, false, negationStatusOfEquipCard);
        }
        else {
            return YuGiOhJi.NoCard; // in case it is not even a valid sacrifice, returns the placeholder for a non-existing card (hm, better show an error message?)
        }
    }
    
    // returns the SummonedMonster that is supposed to be sacrificed useful for burn effects
    public SummonedMonster extractSummonedMonsterFromValidSacrifice() {
        if (sacrificeIsValidMonster()) {
            return SummonedMonster.getNthSummonedMonster(monsterNumber, false);
        }
        else { // in case it is not even a valid sacrifice, returns the placeholder for a non-existing summoned monster (hm, better show an error message?)
            SummonedMonster SumMonster = new SummonedMonster(true, 0);
            SumMonster.resetSummonedMonsterProperties();
            return SumMonster;
        }
    }
    
    // -- from here on: try finding certain kinds of cards and mark and remember them as sacrifices  --
    
    // Looks for an hand card of CPU that is not a hand trap and marks and remembers it as sacrifice and returns true.
    // If there is no such card, returns false.
    public static boolean tryMarkingNonHandTrapHandCardAsSacrifice() {
        if (HandCPU.numberOfCards>0) {
            for (int index = HandCPU.numberOfCards; index >= 1 ; index--){
                YCard Card = HandCPU.getNthCardOfHand(index);
                if (!Card.hasNegateEffectOnHand) {
                    rememberSacrifice(markHandCardAsSacrificeByCardNumber(index));
                    return true;
                }
            }
        }
        return false;
    }
    
    // Looks for a monster of the CPU that is not the nth monster and marks and remembers it as sacrifice and returns true.
    // If there is no such card, returns false.
    public static boolean tryMarkingMonsterExceptNthAsSacrifice (int n) {
        for (int index = 1; index <= 5 ; index++){
            SummonedMonster SumMonster = SummonedMonster.getNthSummonedMonster(index, false);
            if (SumMonster.isExisting && index!=n) {
                rememberSacrifice(markMonsterAsSacrifice(SumMonster));
                return true;
            }
        }
        return false;
    }
    
    // Looks for a card in the GY of the CPU that has not a given card ID and marks and remembers it as sacrifice and returns true.
    // If there is no such card, returns false.
    public static boolean tryMarkingGYCardExceptWithcardIdAsSacrifice (int cardId) {
        if (GYDeckCPU.numberOfCards>0) {
            for (int index = GYDeckCPU.numberOfCards; index >= 1 ; index--){
                int currentCardId = GYDeckCPU.getNthCardOfDeck(index).cardId;
                if (currentCardId!=cardId) {
                    rememberSacrifice(markGYCardAsSacrificeByCardId(currentCardId));
                    return true;
                }
            }
        }
        return false;
    }
    
    // Looks, if there are n equip cards on the side of the CPU that belong to the CPU and marks
    // and remembers as many of them as possible as sacrifice until the needed value has been reached,
    // then stops and returns the total number of these sacrifices.
    public static int tryMarkingNEquipCardsOnOwnSideAsSacrifice (int n) {
        int counter=0;
        for (int index = 1; index <= 5 ; index++){
            if (SummonedMonster.getNthSummonedMonster(index, false).isExisting) {
                EStack Stack = EStack.getNthStack(index, false);
                if (Stack.numberOfCards>0) {
                    for (int m = Stack.numberOfCards; m >= 1 ; m--){
                        if (!Stack.getControllerOfNthEquipCard(m)) {
                            AIsacrifice NewProposedSacrifice = markEquipCardAsSacrificeByCardNumber(Stack, m);
                            if (NewProposedSacrifice.isDefinitelyNewSacrifice()) {
                                rememberSacrifice(NewProposedSacrifice);
                                counter++;
                                if (counter==n) {return counter;}
                            }
                        }
                    }
                }
            }
        }
        return counter;
    }
    
    // returns true, if summoned monster is expendable depending on CPU behavior (thus can be used to pay for effects)
    public static boolean isExpendableCardOnField (SummonedMonster SumMonster) {
        switch (Game.cpuBehaviorAsInt) {
            case 1: return AIdefensive.isExpendableCardOnField(SumMonster);
            case 2: return AIbalanced.isExpendableCardOnField(SumMonster);
            case 3: return AIaggressive.isExpendableCardOnField(SumMonster);
            default: return false; // this shouldn't happen
        }
    }
    
    // returns true, if a card with a given card ID is expendable when on hand depending on CPU behavior (thus can be used to pay for effects)
    public static boolean isExpendableCardOnHand (YCard Card) {
        switch (Game.cpuBehaviorAsInt) {
            case 1: return AIdefensive.isExpendableCardOnHand(Card);
            case 2: return AIbalanced.isExpendableCardOnHand(Card);
            case 3: return AIaggressive.isExpendableCardOnHand(Card);
            default: return false; // this shouldn't happen
        }
    }
    
    // returns true, if a card with a given card ID is expendable when in GY depending on CPU behavior (thus can be used to pay for effects)
    public static boolean isExpendableCardInGY (YCard Card) {
        switch (Game.cpuBehaviorAsInt) {
            case 1: return AIdefensive.isExpendableCardInGY(Card);
            case 2: return AIbalanced.isExpendableCardInGY(Card);
            case 3: return AIaggressive.isExpendableCardInGY(Card);
            default: return false; // this shouldn't happen
        }
    }
    
    public static int countUselessCardsOnHand() {
        int counter=0;
        int numberOfHandcards = HandCPU.numberOfCards;
        if (numberOfHandcards>0) {
            for (int index = numberOfHandcards; index >= 1; index--){
                if (isExpendableCardOnHand(HandCPU.getNthCardOfHand(index))) {
                    counter++;
                }
            }
        }
        return counter;
    }
    
    public static int countUselessCardsInGY() {
        int counter=0;
        int numberOfGYCards = GYDeckCPU.numberOfCards;
        if (numberOfGYCards>0) {
            for (int index = numberOfGYCards; index >= 1; index--){
                if (isExpendableCardInGY(GYDeckCPU.getNthCardOfDeck(index))) {
                    counter++;
                }
            }
        }
        return counter;
    }
    
    public static int countPayableSemipointsWithHandAndGYAlone() {
        int payableSemipoints = countUselessCardsInGY();
        payableSemipoints = payableSemipoints + 2*countUselessCardsOnHand();
        return payableSemipoints;
    }
    
    public static int getPositionOfNthUselessCardOnHand (int n, AIsacrifice CardNotToBeSacrificed) {
        int counter=0;
        int numberOfHandcards = HandCPU.numberOfCards;
        if (numberOfHandcards>0) {
            for (int index = numberOfHandcards; index >= 1; index--){
                YCard HandCard = HandCPU.getNthCardOfHand(index);
                AIsacrifice HandCardPotentialSacrifice = AIsacrifice.markHandCardAsSacrificeByCardId(HandCard.cardId);
                if (isExpendableCardOnHand(HandCard) && !HandCardPotentialSacrifice.isSameKind(CardNotToBeSacrificed)) {
                    counter++;
                    if (counter==n) {return index;}
                }
            }
        }
        return 0;
    }
    
    public static int getPositionOfNthUselessCardInGY (int n, AIsacrifice CardNotToBeSacrificed) {
        int counter=0;
        int numberOfGYCards = GYDeckCPU.numberOfCards;
        if (numberOfGYCards>0) {
            for (int index = numberOfGYCards; index >= 1; index--){
                YCard GYCard = GYDeckCPU.getNthCardOfDeck(index);
                AIsacrifice GYCardPotentialSacrifice = AIsacrifice.markGYCardAsSacrificeByCardId(GYCard.cardId);
                if (isExpendableCardInGY(GYCard) && !GYCardPotentialSacrifice.isSameKind(CardNotToBeSacrificed)) {
                    counter++;
                    if (counter==n) {return index;}
                }
            }
        }
        return 0;
    }
    
    // tries to set up sacrifices worth 1/2 card, by remembering GY/equip cards otherwise useless
    // returns true, if worked
    // if successful, one can afterwards just use the effect with the 1st remembered sacrifice
    public static boolean tryPreparingSacrificeWorthOneHalfCard (AIsacrifice CardNotToBeSacrificed) {
        if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try to decide on sacrifice worth 1/2 card");}
        forgetAllSacrifices();
        Game.reconsiderRecklessness();
        int cardNo = getPositionOfNthUselessCardInGY(1, CardNotToBeSacrificed);
        if (cardNo!=0) {
            rememberSacrifice(markGYCardAsSacrificeByCardNumber(cardNo));
            return true;
        }
        else {
            return tryMarkingNEquipCardsOnOwnSideAsSacrifice(1)==1;
        }
    }
    
    // tries to set up sacrifices worth 1/2 or one card, by remembering hand/GY/equip cards otherwise useless
    // returns true, if worked
    // if successful, one can afterwards just use the effect with the 1st remembered sacrifice
    public static boolean tryPreparingAsSacrificeAnyCardExceptMonsters (AIsacrifice CardNotToBeSacrificed) {
        if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try to decide on any card except summoned monsters as sacrifice");}
        forgetAllSacrifices();
        Game.reconsiderRecklessness();
        int cardNo = getPositionOfNthUselessCardInGY(1, CardNotToBeSacrificed);
        if (cardNo!=0) {
            rememberSacrifice(markGYCardAsSacrificeByCardNumber(cardNo));
            return true;
        }
        else {
            boolean hasWorked = tryMarkingNEquipCardsOnOwnSideAsSacrifice(1)==1;
            if (hasWorked) {
                return true;
            }
            else {
                cardNo = getPositionOfNthUselessCardOnHand(1, CardNotToBeSacrificed);
                if (cardNo!=0) {
                    rememberSacrifice(markHandCardAsSacrificeByCardNumber(cardNo));
                    return true;
                }
            }
        }
        return false;
    }
    
    // tries to set up sacrifices worth one card, by remembering summoned monsters, which are considered weak enough
    // returns true, if worked
    // if successful, one can afterwards just use the effect with the 1st n remembered sacrifices
    public static boolean tryPreparingAsSacrificesNWeakMonsters (int n, AIsacrifice CardNotToBeSacrificed) {
        if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try to decide on " + n + " summoned monsters as sacrifice");}
        Game.CPUbehavior.reevaluateStrategies(); // Here one needs to this, because one has to (re)consider the threat levels of the CPU monsters. (Needed for determining, what is a useless monster.)
        forgetAllSacrifices();
        Game.reconsiderRecklessness();
        for (int index = 1; index <= 5; index++){
            SummonedMonster SumMonster = SummonedMonster.getNthSummonedMonster(index, false);
            if (isExpendableCardOnField(SumMonster)) {
                AIsacrifice NewProposedSacrifice = markMonsterAsSacrifice(SumMonster);
                if (NewProposedSacrifice.isDefinitelyNewSacrifice() && !NewProposedSacrifice.isSameKind(CardNotToBeSacrificed)) { // for summoned monsters the "same kind" is actually the exact same monster
                    rememberSacrifice(NewProposedSacrifice);
                    if (worthInSemipointsOfAllCurrentSacrifices() == 2*n) {return true;}
                }
            }
        }
        return false;
    }
    
    // tries to set up sacrifices worth one card, by remembering cards otherwise useless
    // returns true, if worked
    // if successful, one can afterwards just use the effect with the 1st two remembered sacrifices
    // (it doesn't matter, if only the first one of them is just a valid one, since it then will be worth enough and other sacrifice gets ignored)
    public static boolean tryPreparingSacrificesWorthOneCard (AIsacrifice CardNotToBeSacrificed, boolean isNotUsingHandCards) {
        if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try to decide on sacrifices worth one card");}
        forgetAllSacrifices();
        Game.reconsiderRecklessness();
        int cardNo;
        if (!isNotUsingHandCards) {
            cardNo = getPositionOfNthUselessCardOnHand(1, CardNotToBeSacrificed);
            if (cardNo!=0) {
                rememberSacrifice(markHandCardAsSacrificeByCardNumber(cardNo));
                return true;
            }
        }
        cardNo = getPositionOfNthUselessCardInGY(1, CardNotToBeSacrificed);
        if (cardNo!=0) {
            rememberSacrifice(markGYCardAsSacrificeByCardNumber(cardNo));
            for (int index = 1; index <= countUselessCardsInGY(); index++){
                cardNo = getPositionOfNthUselessCardInGY(index, CardNotToBeSacrificed);
                if (cardNo!=0) {
                    AIsacrifice NewProposedSacrifice = markGYCardAsSacrificeByCardNumber(cardNo);
                    if (NewProposedSacrifice.isDefinitelyNewSacrifice()) {
                        rememberSacrifice(NewProposedSacrifice);
                        if (worthInSemipointsOfAllCurrentSacrifices()==2) {return true;}
                    }
                }
            }
        }
        int currentSemipoints = worthInSemipointsOfAllCurrentSacrifices();
        if (currentSemipoints==2) {
            return true;
        }
        else {
            int neededSemipoints = 2-currentSemipoints;
            if (tryMarkingNEquipCardsOnOwnSideAsSacrifice(neededSemipoints)==neededSemipoints) {
                return true;
            }
        }
        currentSemipoints = worthInSemipointsOfAllCurrentSacrifices();
        if (currentSemipoints==2) {
            return true;
        }
        else {
            int neededSemipoints = 2-currentSemipoints;
            return tryPreparingAsSacrificesNWeakMonsters(2*neededSemipoints, CardNotToBeSacrificed);
        }
    }
    
    // tries to set up sacrifices worth two cards, by remembering cards otherwise useless
    // returns true, if worked
    // if successful, one can afterwards just use the effect with all four remembered sacrifices
    // (it doesn't matter, if only the first few of them are just a valid ones, since it then will be worth enough and other sacrifice(s) get ignored)
    public static boolean tryPreparingSacrificesWorthTwoCards (AIsacrifice CardNotToBeSacrificed) {
        if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try to decide on sacrifices worth two cards");}
        forgetAllSacrifices();
        Game.reconsiderRecklessness();
        int cardNo = getPositionOfNthUselessCardOnHand(1, CardNotToBeSacrificed);
        if (cardNo!=0) {
            rememberSacrifice(markHandCardAsSacrificeByCardNumber(cardNo));
            for (int index = 1; index <= countUselessCardsOnHand(); index++){
                cardNo = getPositionOfNthUselessCardOnHand(index, CardNotToBeSacrificed);
                if (cardNo!=0) {
                    AIsacrifice NewProposedSacrifice = markHandCardAsSacrificeByCardNumber(cardNo);
                    if (NewProposedSacrifice.isDefinitelyNewSacrifice()) {
                        rememberSacrifice(NewProposedSacrifice);
                        return true;
                    }
                }
            }
        }
        if (worthInSemipointsOfAllCurrentSacrifices()==4) {return true;}
        cardNo = getPositionOfNthUselessCardInGY(1, CardNotToBeSacrificed);
        if (cardNo!=0) {
            rememberSacrifice(markGYCardAsSacrificeByCardNumber(cardNo));
            for (int index = 1; index <= countUselessCardsInGY(); index++){
                cardNo = getPositionOfNthUselessCardInGY(index, CardNotToBeSacrificed);
                if (cardNo!=0) {
                    AIsacrifice NewProposedSacrifice = markGYCardAsSacrificeByCardNumber(cardNo);
                    if (NewProposedSacrifice.isDefinitelyNewSacrifice()) {
                        rememberSacrifice(NewProposedSacrifice);
                        if (worthInSemipointsOfAllCurrentSacrifices()==4) {return true;}
                    }
                }
            }
        }
        int currentSemipoints = worthInSemipointsOfAllCurrentSacrifices();
        if (currentSemipoints==4) {
            return true;
        }
        else {
            int neededSemipoints = 4-currentSemipoints;
            if (tryMarkingNEquipCardsOnOwnSideAsSacrifice(neededSemipoints)==neededSemipoints) {
                return true;
            }
        }
        currentSemipoints = worthInSemipointsOfAllCurrentSacrifices();
        if (currentSemipoints==4) {
            return true;
        }
        else {
            int neededSemipoints = 4-currentSemipoints;
            return tryPreparingAsSacrificesNWeakMonsters(2*neededSemipoints, CardNotToBeSacrificed);
        }
    }
    
    // returns the best sacrifice for Burner
    // if there is no sacrifice for Burner (i.e. no MOOK with attack > 0), returns the standard invalid/empty sacrifice
    public static AIsacrifice prepareSacrificeForBurner () {
        if (Game.isSwitchingOnStrategyDialogs) {YuGiOhJi.debugDialog("try to decide on sacrifice for maximum burn damage");}
        forgetAllSacrifices();
        Game.reconsiderRecklessness();
        AIsacrifice ConsCardAsSacrifice = new AIsacrifice();
        maxBurnDamage=0;
        currentBurnDamage=0;
        for (int index = GYDeckCPU.numberOfCards; index >= 1; index--){
            ConsCardAsSacrifice = markGYCardAsSacrificeByCardNumber(index);
            maxBurnDamage = updateMaxBurnDamage(ConsCardAsSacrifice);
        }
        for (int index = HandCPU.numberOfCards; index >= 1; index--){
            ConsCardAsSacrifice = markHandCardAsSacrificeByCardNumber(index);
            maxBurnDamage = updateMaxBurnDamage(ConsCardAsSacrifice);
        }
        for (int index = 1; index <= 5; index++){
            SummonedMonster SumMonster = SummonedMonster.getNthSummonedMonster(index, false);
            if (SumMonster.isExisting) {
                EStack Stack = EStack.getNthStack(index, false);
                for (int n = Stack.numberOfCards; n >= 1; n--){
                    if (!Stack.EquipCards[n].isPlayersEquipCard) { // equip cards always contain MOOKs and they always have some attack value
                         // Actually, it would be wiser to prefer negated equip cards as sacrifice,
                         // but this method is already very compilcated and this burn strategy happens rarely enough so that the effort is not worth it.
                        ConsCardAsSacrifice = AIsacrifice.markEquipCardAsSacrificeByCardNumber(Stack, n);
                        maxBurnDamage = updateMaxBurnDamage(ConsCardAsSacrifice);
                    }
                }
            }
            SumMonster = SummonedMonster.getNthSummonedMonster(index, true);
            if (SumMonster.isExisting) {
                EStack Stack = EStack.getNthStack(index, true);
                for (int n = Stack.numberOfCards; n >= 1; n--){
                    if (!Stack.EquipCards[n].isPlayersEquipCard) {
                        ConsCardAsSacrifice = AIsacrifice.markEquipCardAsSacrificeByCardNumber(Stack, n);
                        maxBurnDamage = updateMaxBurnDamage(ConsCardAsSacrifice);
                    }
                }
            }
        }
        for (int index = 1; index <= 5; index++){
            SummonedMonster SumMonster = SummonedMonster.getNthSummonedMonster(index, false);
            if (SumMonster.isExisting && SumMonster.Card.isContainingMook()) {
                ConsCardAsSacrifice = AIsacrifice.markMonsterAsSacrificeByMonsterNumber(index);
                maxBurnDamage = updateMaxBurnDamage(ConsCardAsSacrifice);
            }
        }
        if (ConsCardAsSacrifice.isValidSacrifice()) {
            return ConsCardAsSacrifice;
        }
        else {
            return (new AIsacrifice());
        }
    }
    
    // in order not to repeat oneself, out-source here the part about updating what card makes the highest potential burn damage, when sacrificed to Burner
    public static int updateMaxBurnDamage (AIsacrifice ConsCardAsSacrifice) {
        YCard CardToBeBurned = ConsCardAsSacrifice.extractCardFromValidSacrifice();
        currentBurnDamage = CardToBeBurned.extractMookBurnDamage();
        if (currentBurnDamage > maxBurnDamage) {maxBurnDamage=currentBurnDamage;}
        return maxBurnDamage;
    }
    
    // returns true, if the CPU has either one card on the hand, or two cards in the GY that are not needed for other strategies
    public static boolean cpuCanPayOneUselessCard() {
        int payableSemiPonts = 2*countUselessCardsOnHand();
        payableSemiPonts = payableSemiPonts + countUselessCardsInGY();
        payableSemiPonts = payableSemiPonts + EStack.countOwnEquipMonsters(false, false);
        return (payableSemiPonts>=2);
    }
    
    // returns true, if the CPU can pay cards from hand or GY that are not needed for other strategies and are worth 2 cards in total
    public static boolean cpuCanPayTwoUselessCards() {
        int payableSemiPonts = 2*countUselessCardsOnHand();
        payableSemiPonts = payableSemiPonts + countUselessCardsInGY();
        payableSemiPonts = payableSemiPonts + EStack.countOwnEquipMonsters(false, false);
        return (payableSemiPonts>=4);
    }
    
    // -- from here on: about sacrificing in all kinds of ways --
    
    // sacrifices a sacrifice, if it is valid (otherwise does nothing)
    public void sacrifice() {
        if (sacrificeIsValidHandCard()) {discardSacrifice();}
        else if (sacrificeIsValidMonster()) {tributeSacrifice();}
        else if (sacrificeIsValidGYCard()) {banishesSacrifice() ;}
        else if (sacrificeIsValidEquipCard()) {sendSacrificeToGY();}
        // if it is an invalid Sacrifice, it is ignored (Hm, one could add an error message in that case. However, many effects use the convenience of ignoring empty redundant sacrifices.)
    }
    
    // assuming sacriice is a valid hand card, it discards it (also tells player by displaying dialog)
    public void discardSacrifice() {
        if (HandCPU.numberOfCards>0) {
            for (int index = HandCPU.numberOfCards; index >= 1; index--){
                YCard Card = HandCPU.getNthCardOfHand(index);
                if (Card.cardId==cardIdOnHand) {
                    YuGiOhJi.informationDialog("Computer discards card " + Card.cardName + ".", "");
                    Hand.discardCard(index, false);
                    break;
                }
            }
        }
    }
    
    // assuming sacriice is a valid summoned monster card, it tributes it (also tells player by displaying dialog)
    public void tributeSacrifice() {
        SummonedMonster SumMonster = SummonedMonster.getNthSummonedMonster(monsterNumber, false);
        YuGiOhJi.informationDialog("Computer tributes its " + AIEffects.getNumberAsString(monsterNumber) + " monster " + "(" + SumMonster.Monster.monsterName + ").", "");
        SumMonster.killMonster();
    }
    
    // assuming sacrifice is a valid graveyard card, it banishes it from the graveyard (also tells player by displaying dialog)
    public void banishesSacrifice() {
        if (GYDeckCPU.numberOfCards>0) {
            for (int index = GYDeckCPU.numberOfCards; index >= 1; index--){
                YCard Card = GYDeckCPU.getNthCardOfDeck(index);
                if (Card.cardId==cardIdInGY) {
                    YuGiOhJi.informationDialog("Computer banishes its card " + Card.cardName + " from graveyard.", "");
                    Deck.banishNthCardFromGY(index, false);
                    break;
                }
            }
        }
    }
    
    // assuming sacriice is a valid equip card, it sends it to the graveyard (also tells player by displaying dialog)
    // rare case of non-static method use like: Sacrifice.sendSacrificeToGY();
    public void sendSacrificeToGY () {
        if (Stack.numberOfCards>0) {
            for (int index = Stack.numberOfCards; index >= 1; index--){
                YCard Card = Stack.getNthCardOfStack(index);
                // the card ID has to be same, it shall not belong to the player (thus to the CPU) and it has to have the right negation status
                if (Card.cardId==cardIdInStack && !Stack.getControllerOfNthEquipCard(index) && Stack.getNegationStatusOfNthEquipCard(index)==negationStatusOfEquipCard) {
                    YuGiOhJi.informationDialog("Computer sends equip card " + Card.cardName + " to gaveyard.", "");
                    Stack.sendEquipCardToGY(cardIdInStack, negationStatusOfEquipCard, false);
                    break;
                }
            }
        }
    }
    
    // --- BOOKKEEPING part2: checking if sacrifice is valid or empty ---
    
    // returns true, if the sacrifice is correctly formatted and if the CPU actually possesses a card with such properties
    public boolean isValidSacrifice() {
        return (sacrificeIsValidHandCard() || sacrificeIsValidMonster() || sacrificeIsValidGYCard() || sacrificeIsValidEquipCard());
    }
    
    // returns true, if a given sacrifice is a well formatted hand card that exists on the hand of the CPU
    public boolean sacrificeIsValidHandCard() {
        if (monsterNumber!=0 || cardIdInGY!=0 || cardIdInStack!=0) {
            return false; // not well enough formatted
        }
        else if (cardIdOnHand==0) {
            return false; // empty/new sacrifice
        }
        else { // check if CPU actually possesses such a card
            if (HandCPU.numberOfCards>0) {
                for (int index = HandCPU.numberOfCards; index >= 1; index--){
                    if (HandCPU.getNthCardOfHand(index).cardId==cardIdOnHand) {
                        return true;
                    }
                }
            }
            return false;
        }
    }
    
    // returns true, if a given sacrifice is a well formatted monster that is controlled by the CPU
    public boolean sacrificeIsValidMonster() {
        if (cardIdOnHand!=0 || cardIdInGY!=0 || cardIdInStack!=0) {
            return false; // not well enough formatted
        }
        else if (monsterNumber==0) {
            return false; // empty/new sacrifice
        }
        else { // check if CPU actually possesses such a card
            return (SummonedMonster.getNthSummonedMonster(monsterNumber, false).isExisting);
        }
        
    }
    
    // returns true, if a given sacrifice is a well formatted card in the computer's graveyard
    public boolean sacrificeIsValidGYCard() {
        if (cardIdOnHand!=0 || monsterNumber!=0 || cardIdInStack!=0) {
            return false; // not well enough formatted
        }
        else if (cardIdInGY==0) {
            return false; // empty/new sacrifice
        }
        else { // check if CPU actually possesses such a card
            if (GYDeckCPU.numberOfCards>0) {
                for (int index = GYDeckCPU.numberOfCards; index >= 1; index--){
                    if (GYDeckCPU.getNthCardOfDeck(index).cardId==cardIdInGY) {
                        return true;
                    }
                }
            }
            return false;
        }
        
    }
    
    // returns true, if a given sacrifice is a well formatted equip card belonging to CPU with the right given negation status
    public boolean sacrificeIsValidEquipCard() {
        if (cardIdOnHand!=0 || monsterNumber!=0 || cardIdInGY!=0) {
            return false; // not well enough formatted
        }
        else if (cardIdInStack==0 && Stack.numberOfCards==0) {
            return false; // empty/new sacrifice
        }
        else { // check if CPU actually possesses such a card
            if (Stack.numberOfCards>0) {
                for (int index = Stack.numberOfCards; index >= 1; index--){
                    YCard Card = Stack.getNthCardOfStack(index);
                    if (Card.cardId==cardIdInStack && !Stack.getControllerOfNthEquipCard(index) && Stack.getNegationStatusOfNthEquipCard(index)==negationStatusOfEquipCard) {
                        return true;
                    }
                }
            }
            return false;
        }
        
    }
    
    // --- BOOKKEEPING part3: checking certain properties for monster effects ---
    
    // returns true, if the sacrifice is correctly formatted, CPU actually possesses such a card and at least one of the two monsters on the card is a MOOK
    public boolean isValidSacrificeAndContainsMook() {
        if (sacrificeIsValidHandCard() || sacrificeIsValidMonster() || sacrificeIsValidGYCard()) {
            return extractCardFromValidSacrifice().isContainingMook();
        }
        else {
            return sacrificeIsValidEquipCard(); // all equip cards contain at least one MOOK
        } // in case it is not even a valid sacrifice, returns false
    }
    
    // The following methods test, if a sacrifice is not the target, in order to prevent the CPU from tributing away the selected target.
    // There are different versions for different kinds of targets.
    
    // returns true, if a given sacrifice is not the same as a targeted monster
    public static boolean sacrificeIsNotTargetMonster (AIsacrifice Sacrifice1, AIsacrifice Sacrifice2, SummonedMonster TargetMonster) {
        return (sacrificeIsNotTargetMonster(Sacrifice1, TargetMonster) && sacrificeIsNotTargetMonster(Sacrifice2, TargetMonster));
    }
    public static boolean sacrificeIsNotTargetMonster (AIsacrifice Sacrifice, SummonedMonster TargetMonster) {
        if (!Sacrifice.sacrificeIsValidMonster()) {
            return true;
        }
        else { // if the target belongs to CPU and has the same monster number, it is the same monster
            return !(!TargetMonster.isPlayersMonster && Sacrifice.monsterNumber==TargetMonster.sumMonsterNumber);
        }
    }
    
    // returns true, if a given sacrifice is not the same as a targeted card in the graveyard
    // (useful for checking, if Necromancer can revive a monster or the Big Back Bouncer can get back a card)
    // IMPORTANT!: Since one can tribute a card with the same card ID away, if there is at least one more copy of that card,
    // check this property always with all sacrifices at the same time!
    // also keep in mind, that the CPU uses effects on GY cards only on cards in ITS OWN GY (that's why one doesn't have to specify which GY)
    public static boolean sacrificeIsNotTargetedGYCard (AIsacrifice Sacrifice, int cardIdInGY) {
        return sacrificeIsNotTargetedGYCard(Sacrifice, new AIsacrifice(), new AIsacrifice(), new AIsacrifice(), cardIdInGY);
    }
    public static boolean sacrificeIsNotTargetedGYCard (AIsacrifice Sacrifice1, AIsacrifice Sacrifice2, int cardIdInGY) {
        return sacrificeIsNotTargetedGYCard(Sacrifice1, Sacrifice2, new AIsacrifice(), new AIsacrifice(), cardIdInGY);
    }
    public static boolean sacrificeIsNotTargetedGYCard (AIsacrifice Sacrifice1, AIsacrifice Sacrifice2, AIsacrifice Sacrifice3, int cardIdInGY) {
        return sacrificeIsNotTargetedGYCard(Sacrifice1, Sacrifice2, Sacrifice3, new AIsacrifice(), cardIdInGY);
    }
    public static boolean sacrificeIsNotTargetedGYCard (AIsacrifice Sacrifice1, AIsacrifice Sacrifice2, AIsacrifice Sacrifice3, AIsacrifice Sacrifice4, int cardIdInGY) {
        int numberOfSacrificedCardsWithId=0;
        if (Sacrifice1.cardIdInGY==cardIdInGY) {numberOfSacrificedCardsWithId++;}
        if (Sacrifice2.cardIdInGY==cardIdInGY) {numberOfSacrificedCardsWithId++;}
        if (Sacrifice3.cardIdInGY==cardIdInGY) {numberOfSacrificedCardsWithId++;}
        if (Sacrifice4.cardIdInGY==cardIdInGY) {numberOfSacrificedCardsWithId++;}
        // the number of the valid targets must be at least one more than the total number of sacrifices sacrificing that card
        int numberOfCardsWithIdInGY = GYDeckCPU.numberOfCardsWithCardIdInDeck(cardIdInGY);
        return (numberOfCardsWithIdInGY > numberOfSacrificedCardsWithId);
    }
    
    // returns true, if a given sacrifice is not the same as a targeted equip card (given by stack and card number in stack)
    public static boolean sacrificeIsNotTargetedEquipCard (AIsacrifice Sacrifice1, AIsacrifice Sacrifice2, AIsacrifice Sacrifice3, AIsacrifice Sacrifice4, EStack Stack, int cardNumberInStack) {
        return (sacrificeIsNotTargetedEquipCard(Sacrifice1, Sacrifice2, Stack, cardNumberInStack) && sacrificeIsNotTargetedEquipCard(Sacrifice3, Sacrifice4, Stack, cardNumberInStack));
    }
    public static boolean sacrificeIsNotTargetedEquipCard (AIsacrifice Sacrifice1, AIsacrifice Sacrifice2, EStack Stack, int cardNumberInStack) {
        return (sacrificeIsNotTargetedEquipCard(Sacrifice1, Stack, cardNumberInStack) && sacrificeIsNotTargetedEquipCard(Sacrifice2, Stack, cardNumberInStack));
    }
    public static boolean sacrificeIsNotTargetedEquipCard (AIsacrifice Sacrifice, EStack Stack, int cardNumberInStack) {
        boolean isNegated = Stack.getNegationStatusOfNthEquipCard(cardNumberInStack);
        boolean isPlayerCard = Stack.getControllerOfNthEquipCard(cardNumberInStack);
        int cardId = Stack.getNthCardOfStack(cardNumberInStack).cardId;
        return sacrificeIsNotTargetedEquipCard(Sacrifice, Stack, cardId, isNegated, isPlayerCard);
    }
    // returns true, if a given sacrifice is not the same as a targeted equip card (given by stack, card ID and if it is negated)
    public static boolean sacrificeIsNotTargetedEquipCard (AIsacrifice Sacrifice, EStack Stack, int cardIdInStack, boolean negationStatusOfEquipCard, boolean isEquipCardOfPlayer) {
        if (isEquipCardOfPlayer) {
            return true; // if target belongs to player, it can not be same as the sacrifice (because sacrifices always belong to CPU, which is tested when sacrificing)
        }
        else {
            if (Sacrifice.cardIdInStack!=cardIdInStack) {
                return true; // can not be the same, because different card ID
            }
            else {
                if (Sacrifice.negationStatusOfEquipCard!=negationStatusOfEquipCard) {
                    return true; // can not be the same, because one is nagated and the other is not
                }
                else { // Since the stack of a valid equip card sacrifice does not have to belong to CPU (only the sacrificed card) one also has to check the owner of the stack.
                    return ( Sacrifice.Stack.stackNumber!=Stack.stackNumber && Sacrifice.Stack.isBelongingToPlayer!=Stack.isBelongingToPlayer );
                }
            }
        }
    }
    
    // checks, if a sacrifice is not equipped with the target! (because, if tributed away, the target moves to GY and can not be targeted any more)
    public static boolean sacrificeIsNotEquippedWithTarget (AIsacrifice Sacrifice1, AIsacrifice Sacrifice2, AIsacrifice Sacrifice3, AIsacrifice Sacrifice4, EStack Stack) {
        return (sacrificeIsNotEquippedWithTarget(Sacrifice1, Sacrifice2, Stack) && sacrificeIsNotEquippedWithTarget(Sacrifice3, Sacrifice4, Stack));
    }
    public static boolean sacrificeIsNotEquippedWithTarget (AIsacrifice Sacrifice1, AIsacrifice Sacrifice2, EStack Stack) {
        return (sacrificeIsNotEquippedWithTarget(Sacrifice1, Stack) && sacrificeIsNotEquippedWithTarget(Sacrifice2, Stack));
    }
    public static boolean sacrificeIsNotEquippedWithTarget (AIsacrifice Sacrifice, EStack Stack) {
        if (!Sacrifice.sacrificeIsValidMonster()) {
            return true; // if the sacrifice is not a summoned monster, it can not be equipped with the target for the effect
        }
        else { // if the stack of the target belongs to CPU and has the same monster number, it is equipping the sacrifice
            return !(!Stack.isBelongingToPlayer && Sacrifice.monsterNumber==Stack.stackNumber);
        }
    }
    
    // --- BOOKKEEPING part4: calculating the worth of a sacrifice for an effect ---
    
    // returns the worth of a given sacrifice measured in semiponts
    public int worthInSemipointsOfSacrifice() {
        if (sacrificeIsValidHandCard() || sacrificeIsValidMonster()) {
            return 2;
        }
        else if (sacrificeIsValidGYCard() || sacrificeIsValidEquipCard()) {
            return 1;
        }
        else {
            return 0;
        }
    }
    
    // returns the worth all currently remembered sacrifices measured in semiponts
    public static int worthInSemipointsOfAllCurrentSacrifices() {
        int semipoints=0;
        semipoints = semipoints + CurrentCPUSacrifice1.worthInSemipointsOfSacrifice();
        semipoints = semipoints + CurrentCPUSacrifice2.worthInSemipointsOfSacrifice();
        semipoints = semipoints + CurrentCPUSacrifice3.worthInSemipointsOfSacrifice();
        semipoints = semipoints + CurrentCPUSacrifice4.worthInSemipointsOfSacrifice();
        return semipoints;
    }
    
    
}
