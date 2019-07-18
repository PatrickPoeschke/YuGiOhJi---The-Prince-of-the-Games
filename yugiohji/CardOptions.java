package yugiohji;

/**
 * This class checks what options you have for playing a YuGiOhJi-card on the hand.
 * If you have any, it creates a window with a button for each option.
 * If you click on a button, methods from other files are called.
 * 
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.*;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import static yugiohji.YuGiOhJi.window3;
import static yugiohji.YuGiOhJi.Game;
        
public class CardOptions extends JFrame implements ActionListener, ComponentListener {
    // modifying JDialogs is considered better than JFrames as secondary windows
    // however, this works fine as well
    
    public static boolean[] areUsable = new boolean[8];
    // array of booleans containing info if the following card options are usable:
    // card option #1: upper normal summon
    // card option #2: upper normal set
    // card option #3: upper special summon
    // card option #4: upper effect
    // card option #5: lower normal summon
    // card option #6: lower normal set
    // card option #7: lower special summon
    // card option #8: lower effect
    
    public static int initialOptionsWidth=260;
    public static int initialOptionsHeight=88;
    public static int currentOptionsWidth=260;
    public static int currentOptionsHeight=88;
    
    public JPanel panel3;
    public static JButton buttonUpNormSum;
    public static JButton buttonUpNormSet;
    public static JButton buttonUpSpecSum;
    public static JButton buttonUpEffect;
    public static JButton buttonLowNormSum;
    public static JButton buttonLowNormSet;
    public static JButton buttonLowSpecSum;
    public static JButton buttonLowEffect;
    public static JLabel dislayCardName;
    public static int playedCardNumber;
    public static YCard playedCard;
    public static boolean isInGraveyard;
    
    // constructor
    public CardOptions (YCard Card, int numberOfOptions) {
        
        this.setTitle("play card");
        double scaleFactorX = (double) YuGiOhJi.currentFrameWidth/YuGiOhJi.initialFrameWidth;
        double scaleFactorY = (double) YuGiOhJi.currentFrameHeight/YuGiOhJi.initialFrameHeight;
        int posX = (int) Math.round(710*scaleFactorX);
        int posY = (int) Math.round(400*scaleFactorY);
        this.setLocation(posX, posY);
        initialOptionsHeight=25*(numberOfOptions+1) + 38;
        currentOptionsHeight=initialOptionsHeight;
        this.setSize(initialOptionsWidth, initialOptionsHeight);
        this.setResizable(true); // just in case somehow not all the buttons might fit in the window (on some platform)
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().addComponentListener(this);
        
        panel3 = new JPanel();
        panel3.setLayout(null);
        
        dislayCardName = new JLabel(" " + Card.cardName);
        dislayCardName.setBounds(0, 0, 250, 25);
        dislayCardName.setVisible(true);
        panel3.add(dislayCardName);
        
        // define all 8 buttons below 300pixels (move them up later, if needed)
        buttonUpNormSum = new JButton("normal summon upper monster");
        buttonUpNormSum.setBounds(0, 300, 250, 25);
        buttonUpNormSum.setVisible(false);
        buttonUpNormSum.addActionListener(this);
        panel3.add(buttonUpNormSum);
        
        buttonUpNormSet = new JButton("normal set upper monster");
        buttonUpNormSet.setBounds(0, 325, 250, 25);
        buttonUpNormSet.setVisible(false);
        buttonUpNormSet.addActionListener(this);
        panel3.add(buttonUpNormSet);
        
        buttonUpSpecSum = new JButton("special summon upper monster");
        buttonUpSpecSum.setBounds(0, 350, 250, 25);
        buttonUpSpecSum.setVisible(false);
        buttonUpSpecSum.addActionListener(this);
        panel3.add(buttonUpSpecSum);
        
        buttonUpEffect = new JButton("use effect of upper monster");
        buttonUpEffect.setBounds(0, 375, 250, 25);
        buttonUpEffect.setVisible(false);
        buttonUpEffect.addActionListener(this);
        panel3.add(buttonUpEffect);
        
        buttonLowNormSum = new JButton("normal summon lower monster");
        buttonLowNormSum.setBounds(0, 400, 250, 25);
        buttonLowNormSum.setVisible(false);
        buttonLowNormSum.addActionListener(this);
        panel3.add(buttonLowNormSum);
        
        buttonLowNormSet = new JButton("normal set lower monster");
        buttonLowNormSet.setBounds(0, 425, 250, 25);
        buttonLowNormSet.setVisible(false);
        buttonLowNormSet.addActionListener(this);
        panel3.add(buttonLowNormSet);
        
        buttonLowSpecSum = new JButton("special summon lower monster");
        buttonLowSpecSum.setBounds(0, 450, 250, 25);
        buttonLowSpecSum.setVisible(false);
        buttonLowSpecSum.addActionListener(this);
        panel3.add(buttonLowSpecSum);
        
        buttonLowEffect = new JButton("use effect of lower monster");
        buttonLowEffect.setBounds(0, 475, 250, 25);
        buttonLowEffect.setVisible(false);
        buttonLowEffect.addActionListener(this);
        panel3.add(buttonLowEffect);
        
        this.add(panel3);
        
    }
    
    // -- methods concerning playing cards in the hand (including summoning of monsters) --
    
    @Override
    public void componentHidden(ComponentEvent ce) {};
    @Override
    public void componentShown(ComponentEvent ce) {};
    @Override
    public void componentMoved(ComponentEvent ce) {};
    @Override
    public void componentResized(ComponentEvent ce) {
        currentOptionsWidth = this.getWidth();
        currentOptionsHeight = this.getHeight();
        rescaleEverything();
    };
    
    // plays a card of the player, if one is allowed to do so
    public static void attemptPlayCard (YCard Card, int cardNumber, boolean isInGY){
        int numberOfOptions=checkCardOptions(Card, true, isInGY);
        if (numberOfOptions>0) {
            playedCardNumber=cardNumber;
            playedCard=Card;
            isInGraveyard = isInGY;
            window3 = new CardOptions(Card, numberOfOptions);
            shiftButtonsAndMakeThemVisible();
            rescaleEverything();
            window3.setVisible(true);
            double scaleFactorX = (double) YuGiOhJi.currentFrameWidth/YuGiOhJi.initialFrameWidth;
            double scaleFactorY = (double) YuGiOhJi.currentFrameHeight/YuGiOhJi.initialFrameHeight;
            int windowWidth = (int) Math.round(initialOptionsWidth*scaleFactorX*0.9);
            int windowHeight = (int) Math.round(initialOptionsHeight*scaleFactorY*0.95);
            window3.setSize(windowWidth, windowHeight);
        }
    }
    
    // calculates rescaling of horizontal coordinates (and widths)
    public static int rescaleX (int x) {
        int newValue = (int) Math.round(x*currentOptionsWidth/initialOptionsWidth);
        return newValue;
    }
    
    // calculates rescaling of vertical coordinates (and heights)
    public static int rescaleY (int y) {
        int newValue = (int) Math.round(y*currentOptionsHeight/initialOptionsHeight);
        return newValue;
    }
    
    // rescales a given button (uses current window size saved by global variables)
    public static void rescaleButton (JButton buttonName, int intialPosX, int intialPosY, int intialButtonWidth, int intialButtonHeight) {
        buttonName.setBounds(rescaleX(intialPosX), rescaleY(intialPosY), rescaleX(intialButtonWidth), rescaleY(intialButtonHeight));
    }
    
    // rescales a given label (uses current window size saved by global variables)
    public static void rescaleLabel(JLabel labelName, int intialPosX, int intialPosY, int intialLabelWidth, int intialLabelHeight) {
        labelName.setBounds(rescaleX(intialPosX), rescaleY(intialPosY), rescaleX(intialLabelWidth), rescaleY(intialLabelHeight));
    }
    
    // puts the buttons in the right order for the card options menu
    public static void shiftButtonsAndMakeThemVisible(){
        int buttonNumber=0;
        if (areUsable[0]) {
            buttonNumber++;
            buttonUpNormSum.setVisible(true);
            buttonUpNormSum.setBounds(0, buttonNumber*25, 250, 25);
        }
        if (areUsable[1]) {
            buttonNumber++;
            buttonUpNormSet.setVisible(true);
            buttonUpNormSet.setBounds(0, buttonNumber*25, 250, 25);
        }
        if (areUsable[2]) {
            buttonNumber++;
            buttonUpSpecSum.setVisible(true);
            buttonUpSpecSum.setBounds(0, buttonNumber*25, 250, 25);
        }
        if (areUsable[3]) {
            buttonNumber++;
            buttonUpEffect.setVisible(true);
            buttonUpEffect.setBounds(0, buttonNumber*25, 250, 25);
        }
        if (areUsable[4]) {
            buttonNumber++;
            buttonLowNormSum.setVisible(true);
            buttonLowNormSum.setBounds(0, buttonNumber*25, 250, 25);
        }
        if (areUsable[5]) {
            buttonNumber++;
            buttonLowNormSet.setVisible(true);
            buttonLowNormSet.setBounds(0, buttonNumber*25, 250, 25);
        }
        if (areUsable[6]) {
            buttonNumber++;
            buttonLowSpecSum.setVisible(true);
            buttonLowSpecSum.setBounds(0, buttonNumber*25, 250, 25);
        }
        if (areUsable[7]) {
            buttonNumber++;
            buttonLowEffect.setVisible(true);
            buttonLowEffect.setBounds(0, buttonNumber*25, 250, 25);
        }
    }
    
    // simply rescales all graphical components, i.e. all buttons and labels, one by one
    public static void rescaleEverything() {
        // rescale all labels
        rescaleLabel(dislayCardName, 0, 0, 250, 25);
        // rescale all buttons
        int buttonNumber=0;
        if (areUsable[0]) {
            buttonNumber++;
            rescaleButton(buttonUpNormSum, 0, buttonNumber*25, 250, 25);
        }
        if (areUsable[1]) {
            buttonNumber++;
            rescaleButton(buttonUpNormSet, 0, buttonNumber*25, 250, 25);
        }
        if (areUsable[2]) {
            buttonNumber++;
            rescaleButton(buttonUpSpecSum, 0, buttonNumber*25, 250, 25);
        }
        if (areUsable[3]) {
            buttonNumber++;
            rescaleButton(buttonUpEffect, 0, buttonNumber*25, 250, 25);
        }
        if (areUsable[4]) {
            buttonNumber++;
            rescaleButton(buttonLowNormSum, 0, buttonNumber*25, 250, 25);
        }
        if (areUsable[5]) {
            buttonNumber++;
            rescaleButton(buttonLowNormSet, 0, buttonNumber*25, 250, 25);
        }
        if (areUsable[6]) {
            buttonNumber++;
            rescaleButton(buttonLowSpecSum, 0, buttonNumber*25, 250, 25);
        }
        if (areUsable[7]) {
            buttonNumber++;
            rescaleButton(buttonLowEffect, 0, buttonNumber*25, 250, 25);
        }
    }
    
    // --- check usability of card effects on hand (& in GY) ---
    // (all about verifying if one can use an effect from hand)
    
    // takes a card and returns the number of options how one can use it
    public static int checkCardOptions (YCard Card, boolean isBelongingToPlayer, boolean isInGY) {
        int numberOfOptions = 0; // keep track of the number of options
        if (!Game.isMainPhase()) {
            return 0;
        }
        else if (isInGY) {
            if (Card.hasEffectInGY) {
                areUsable[0] = false; // no normal summoning from GY
                areUsable[1] = false; // no normal setting from GY
                int semipoints = countPayableCosts(isBelongingToPlayer);
                
                areUsable[2] = false;
                if (Card.canUpMonsterSpecSummon) {areUsable[2] = checkSpecialSummon(Card.upMonster, semipoints, true);}
                if (areUsable[2]) {numberOfOptions++;}
                
                areUsable[3] = false; // extend here, if adding effects in GY (other than spec. summmoning itself)
                areUsable[4] = false; // no normal summoning from GY
                areUsable[5] = false; // no normal setting from GY
                
                areUsable[6] = false;
                if (Card.canLowMonsterSpecSummon) {areUsable[6] = checkSpecialSummon(Card.lowMonster, semipoints, true);}
                if (areUsable[6]) {numberOfOptions++;}
                
                areUsable[7] = false; // extend here, if adding effects in GY (other than spec. summmoning itself)
            }
        }
        else {
            // check card options #1 & #2: normal summoning/setting of upper monster
            areUsable[0] = false;
            if (Card.canUpMonsterNormSummon) {areUsable[0] = checkNormalSummon(Card.upMonster, isBelongingToPlayer);}
            areUsable[1] = areUsable[0];
            if (areUsable[0]) {numberOfOptions=numberOfOptions+2;}
            
            int payableSemipoints = countPayableCosts(isBelongingToPlayer);
            // check card option #3: special summoning of upper monster
            areUsable[2] = false;
            if (Card.canUpMonsterSpecSummon) {areUsable[2] = checkSpecialSummon(Card.upMonster, payableSemipoints, false);}
            if (areUsable[2]) {numberOfOptions++;}
            
            // check card option #4: effect of upper monster
            areUsable[3] = false;
            if (Card.hasNegateEffectOnHand) {areUsable[3] = canUseHandTrapEffect(Card.upMonster);}
            else if (Card.cardId==11) {areUsable[3] = AIEffects.canBanishSearchSlickRusherActivate(true);}
            if (areUsable[3]) {numberOfOptions++;}
            
            // check card options #5 & #6: analog to #1 & #2 (just with lower monster)
            areUsable[4] = false;
            if (Card.canLowMonsterNormSummon){areUsable[4] = checkNormalSummon(Card.lowMonster, isBelongingToPlayer);}
            areUsable[5] = areUsable[4];
            if (areUsable[4]) {numberOfOptions=numberOfOptions+2;}
            
            // check card option #7: special summoning of lower monster
            areUsable[6] = false;
            if (Card.canLowMonsterSpecSummon) {areUsable[6] = checkSpecialSummon (Card.lowMonster, payableSemipoints, false);}
            if (areUsable[6]) {numberOfOptions++;}

            // check card option #7: effect of lower monster
            areUsable[7] = false;
            if (Card.hasEquipEffect) {areUsable[7] = canUseEquipEffectFromHand(Card);}
            else if (Card.cardId==12) {areUsable[7] = AIEffects.canBanishSearchHolyLanceActivate(true);}
            if (areUsable[7]) {numberOfOptions++;}
            
        }
        return numberOfOptions;
    }
    
    // checks if one can normal/tribute summon/set a given monster
    public static boolean hasStillNormalSummon (boolean isBelongingToPlayer) {
        if (isBelongingToPlayer) {
            return Game.hasStillNormalSummonPlayer;
        }
        else {
            return Game.hasStillNormalSummonCPU;
        }
    }
    
    // checks if one can normal/tribute summon/set a given monster
    public static boolean checkNormalSummon (YMonster Monster, boolean isBelongingToPlayer) {
        return canNormalSummonMonsterWithStars(Monster.stars, isBelongingToPlayer);
    }
    
    // returns true, if a given player can normal/tribute summon/set a given monster with a given number of stars
    public static boolean canNormalSummonMonsterWithStars (int stars, boolean isBelongingToPlayer) {
        if (!hasStillNormalSummon(isBelongingToPlayer)) {
            return false;
        }
        else {
            if (stars == 1) {
                return SummonedMonster.hasFreeMonsterZone(isBelongingToPlayer);
            }
            else {
                return (SummonedMonster.countOwnSummonedMonsters(isBelongingToPlayer) >= (stars-1));
            }
        }
    }
    
    // checks if one can special summon a given monster
    public static boolean checkSpecialSummon (YMonster Monster, int payableSemipoints, boolean isInGY) {
        if (Monster.stars == 1 || Monster.stars == 4) { // GODS and MOOKS can not summon themselves
            return false;
        }
        else { // correct worth of cards for own card
            if (isInGY) { // one could simplify this part, but one is supposed to understand how the worth adds up
                payableSemipoints--; // don't count itself if in GY
                if (Monster.stars==2) { // MIDBOSSES can not special summon themselves from graveyard
                    return false;
                }
            }
            else {
                payableSemipoints = payableSemipoints - 2; // don't count itself on hand
            }
            return ( (2*payableSemipoints) >= (Monster.stars-1) ); // the points one can pay must be at least the number of stars minus one
        }
    }
    
    // out-source evaluating the worth that a player can pay in general for effects (or special summoning)
    public static int countPayableCosts (boolean isBelongingToPlayer) {
        // cards in hand ans as summoned Monsters count as full points (=2semipoints)
        int semipoints = 2*Hand.numberOfCardsOnHand(isBelongingToPlayer);
        semipoints = semipoints + 2*SummonedMonster.countOwnSummonedMonsters(isBelongingToPlayer);
        // equip cards and cards in graveyard only card half a point each
        semipoints = semipoints + EStack.countOwnEquipMonsters(isBelongingToPlayer, false);
        semipoints = semipoints + Deck.numberOfCardsInGY(isBelongingToPlayer);
        return semipoints;
    }
    
    // checks, if there is a possible target for equip effects
    // (look into used methods for more details)
    public static boolean canUseEquipEffectFromHand (YCard Card) {
        SummonedMonster SumMonster;
        for (int index = 1; index <= 5; index++){ // check own summoned monsters
            SumMonster=SummonedMonster.getNthSummonedMonster(index, true);
            if (SumMonster.canBeEquippedBy(Card.lowMonster)) {
                return true;
            } // check summoned monsters of opponent
            SumMonster=SummonedMonster.getNthSummonedMonster(index, false);
            if (SumMonster.canBeEquippedBy(Card.lowMonster)) {
                return true;
            }
        }
        return false;
    }
    
    // checks if there is a possible target for hand trap effects
    public static boolean canUseHandTrapEffect (YMonster Monster) {
        if (Monster.monsterId==32) { // special case only for the neutraliser (look if there are effect monsters on the field, except for Incorruptible or monsters copying its effects)
            return thereAreNegatableCards();
        }
        else {
            return false;
        }
    }
    
    // returns true, if there are cards on the field that are negatable
    public static boolean thereAreNegatableCards() {
        return ( CardOptions.hasNegatableMonsters(false) || CardOptions.hasNegatableMonsters(true) || thereAreNegatableEquipCards() );
    }
    
    // returns true, if a given player controls a monster that can be negated
    public static boolean hasNegatableMonsters (boolean isConcerningPlayer) {
        SummonedMonster SumMonster;
        for (int index = 1; index <= 5; index++){ // check if there are any summoned monsters that can be negated
            SumMonster=SummonedMonster.getNthSummonedMonster(index, isConcerningPlayer);
            if (SumMonster.canBeNegated()) {return true;}
        }
        return false;
    }
    
    // returns true, if there are non-negated equip cards on the field 
    public static boolean thereAreNegatableEquipCards() {
        for (int index = 1; index <= 5; index++){
            EStack Stack = EStack.getNthStack(index, true);
            if (Stack.hasNonNegatedEquipCard()) {
                return true;
            }
        }
        return false;
    }
    
    // from here on:
    // --- reactions of the buttons ---
    
    @Override
    public void actionPerformed (ActionEvent ae){ // Fortunately, invisible buttons can not be aktivated in the first place. That's why one doesn't need to check it.
        
        if (ae.getSource() == CardOptions.buttonUpNormSum) {
            window3.dispose();
            YCard.normalSummonMonster(playedCard, playedCardNumber, true);
        }
        
        if (ae.getSource() == CardOptions.buttonUpNormSet) {
            window3.dispose();
            YCard.normalSetMonster(playedCard, playedCardNumber, true);
        }
        
        if (ae.getSource() == CardOptions.buttonUpSpecSum) {
            window3.dispose();
            YMonster.specialSummon(playedCard, playedCardNumber, playedCard.cardId, true, isInGraveyard);
        }

        if (ae.getSource() == CardOptions.buttonUpEffect) {
            window3.dispose();
            if (playedCard.cardId==11) {
                YMonster.banishSearchSlickRusherActivate(playedCardNumber);
            }
            else {
                YMonster.handTrapEffectNegateActivate();
            }
        }
        
        if (ae.getSource() == CardOptions.buttonLowNormSum) {
            window3.dispose();
            YCard.normalSummonMonster(playedCard, playedCardNumber, false);
        }
        
        if (ae.getSource() == CardOptions.buttonLowNormSet) {
            window3.dispose();
            YCard.normalSetMonster(playedCard, playedCardNumber, false);
        }
        
        if (ae.getSource() == CardOptions.buttonLowSpecSum) {
            window3.dispose();
            YMonster.specialSummon(playedCard, playedCardNumber, playedCard.cardId, false, isInGraveyard);
        }
        
        if (ae.getSource() == CardOptions.buttonLowEffect) {
            window3.dispose();
            if (playedCard.cardId==12) {
                YMonster.banishSearchHolyLanceActivate(playedCardNumber);
            }
            else {
                YMonster.equipEffectFromHandActivate(playedCardNumber);
            }
        }
        
    }
    
}
