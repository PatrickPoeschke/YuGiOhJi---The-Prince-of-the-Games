package yugiohji;

/**
 * This class creates a window for searching a YuGiOhJi-card out of the deck.
 * That means this class is used for the effect of the monster "Card Grabber".
 * It is also used, when looking at all the cards in the different dimension
 * (the banishing zone) or a graveyard and selecting a card to use its effect
 * (only special summoning itself from the graveyard).
 * 
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.*;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import static yugiohji.YuGiOhJi.Card;
import static yugiohji.YuGiOhJi.DDDeckCPU;
import static yugiohji.YuGiOhJi.DDDeckPlayer;
import static yugiohji.YuGiOhJi.DeckPlayer;
import static yugiohji.YuGiOhJi.Game;
import static yugiohji.YuGiOhJi.GYDeckCPU;
import static yugiohji.YuGiOhJi.GYDeckPlayer;
import static yugiohji.YuGiOhJi.window5;

public class SearchWindow implements ActionListener, MouseListener, ComponentListener {
    
    public static int initialSearchWidth=675;
    public static int initialSearchHeight=425;
    public static int currentSearchWidth=675;
    public static int currentSearchHeight=425;
    
    public JPanel panel5;
    JFrame frame;
    
    public static int intDeckCardId=0; // Id of card one wants to get from the deck
    public static Deck ConsideredDeck;
    public static boolean isGettingCardFromDeck;
    public static boolean isGettingCardFromGY;
    public static boolean isGettingCardFromGYCPU;
    public static boolean isSendingToGY; // for Foolish Burial effect
    public static boolean isOnlyAllowingMooks;
    public static boolean isOnlyAllowingMidbosses;
    public static boolean isLookingAtBanishingZone;
    public static boolean isOnlyAllowingGods;
    public static boolean isOnlyAllowingBanishBounceBurn;
    
    //String instructionText;
    public static JLabel displayInstruction;
    
    public static JButton buttonOK;
    public static JButton buttonCard1;
    public static JButton buttonCard2;
    public static JButton buttonCard3;
    public static JButton buttonCard4;
    public static JButton buttonCard5;
    public static JButton buttonCard6;
    public static JButton buttonCard7;
    public static JButton buttonCard8;
    public static JButton buttonCard9;
    public static JButton buttonCard10;
    public static JButton buttonCard11;
    public static JButton buttonCard12;
    public static JButton buttonCard13;
    public static JButton buttonCard14;
    public static JButton buttonCard15;
    public static JButton buttonCard16;
    public static JButton buttonCard17;
    public static JButton buttonCard18;
    
    public static JLabel displayCardName;
    public static JLabel displayNumCard1;
    public static JLabel displayNumCard2;
    public static JLabel displayNumCard3;
    public static JLabel displayNumCard4;
    public static JLabel displayNumCard5;
    public static JLabel displayNumCard6;
    public static JLabel displayNumCard7;
    public static JLabel displayNumCard8;
    public static JLabel displayNumCard9;
    public static JLabel displayNumCard10;
    public static JLabel displayNumCard11;
    public static JLabel displayNumCard12;
    public static JLabel displayNumCard13;
    public static JLabel displayNumCard14;
    public static JLabel displayNumCard15;
    public static JLabel displayNumCard16;
    public static JLabel displayNumCard17;
    public static JLabel displayNumCard18;
    
    public static int numCard1=0;
    public static int numCard2=0;
    public static int numCard3=0;
    public static int numCard4=0;
    public static int numCard5=0;
    public static int numCard6=0;
    public static int numCard7=0;
    public static int numCard8=0;
    public static int numCard9=0;
    public static int numCard10=0;
    public static int numCard11=0;
    public static int numCard12=0;
    public static int numCard13=0;
    public static int numCard14=0;
    public static int numCard15=0;
    public static int numCard16=0;
    public static int numCard17=0;
    public static int numCard18=0;
    
    // constructor for deck searching
    public SearchWindow (Deck SearchedDeck) {
        
        frame = new JFrame();
        if (isGettingCardFromDeck) {
            frame.setTitle("Choose a card in your deck.");
        }
        else if (isGettingCardFromGY) {
            frame.setTitle("Choose a card in your GY.");
        }
        else if (isGettingCardFromGYCPU) {
            frame.setTitle("Choose a card in your opponent's GY.");
        }
        else if (isLookingAtBanishingZone) {
            frame.setTitle("Look at cards in banishing zone.");
        }
        else { // This case should never happen.
            frame.setTitle("Choose a card in deck or GY.");
        }
        
        // rescale this whole thing according to the size of the main window
        double scaleFactorX = (double) YuGiOhJi.currentFrameWidth/YuGiOhJi.initialFrameWidth;
        double scaleFactorY = (double) YuGiOhJi.currentFrameHeight/YuGiOhJi.initialFrameHeight;
        int posX = (int) Math.round(408*scaleFactorX);
        int posY = (int) Math.round(30*scaleFactorY);
        int windowWidth = (int) Math.round(675*scaleFactorX);
        int windowHeight = (int) Math.round(425*scaleFactorY);
        frame.setLocation(posX, posY);
        frame.setSize(windowWidth, windowHeight);
        
        if (Game.isActiveSomeEffect()) {
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        }
        // Concerning active card effects:
        // If a player has already paid the cost for an active effect,
        // one is not allowed to close the window. (The close button will simply be ignored.)
        // One can not abort the effect, since one will never get back the paid cost.
        // If one closed the window, one could only get cards into GY. That is not allowed.
        
        frame.getContentPane().addComponentListener(this);
        
        panel5 = new JPanel();
        panel5.setLayout(null);
        
        countCardNumbersInDeck(SearchedDeck);
        
        buttonCard1 = new JButton();
        buttonCard1.setIcon(new RescaledIcon(this.getClass().getResource(Card.GodBarrier.upMonster.cardPathAtt)));
        YuGiOhJi.setButtonProperties(buttonCard1, 25, 25, 67, 100, numCard1>0);
        buttonCard1.addActionListener(this);
        buttonCard1.addMouseListener(this);
        panel5.add(buttonCard1);
        
        displayNumCard1 = new JLabel(" " + numCard1);
        displayNumCard1.setBounds(92, 25, 33, 100);
        displayNumCard1.setVisible(true);
        panel5.add(displayNumCard1);
        
        buttonCard2 = new JButton();
        buttonCard2.setIcon(new RescaledIcon(this.getClass().getResource(Card.EradicatorObstacle.upMonster.cardPathAtt)));
        YuGiOhJi.setButtonProperties(buttonCard2, 125, 25, 67, 100, numCard2>0);
        buttonCard2.addActionListener(this);
        buttonCard2.addMouseListener(this);
        panel5.add(buttonCard2);
        
        displayNumCard2 = new JLabel(" " + numCard2);
        displayNumCard2.setBounds(192, 25, 33, 100);
        displayNumCard2.setVisible(true);
        panel5.add(displayNumCard2);
        
        buttonCard3 = new JButton();
        buttonCard3.setIcon(new RescaledIcon(this.getClass().getResource(Card.GodKillingSpearLance.upMonster.cardPathAtt)));
        YuGiOhJi.setButtonProperties(buttonCard3, 225, 25, 67, 100, numCard3>0);
        buttonCard3.addActionListener(this);
        buttonCard3.addMouseListener(this);
        panel5.add(buttonCard3);
        
        displayNumCard3 = new JLabel(" " + numCard3);
        displayNumCard3.setBounds(292, 25, 33, 100);
        displayNumCard3.setVisible(true);
        panel5.add(displayNumCard3);
        
        buttonCard4 = new JButton();
        buttonCard4.setIcon(new RescaledIcon(this.getClass().getResource(Card.MonsterStealerSteepLearningCurve.upMonster.cardPathAtt)));
        YuGiOhJi.setButtonProperties(buttonCard4, 325, 25, 67, 100, numCard4>0);
        buttonCard4.addActionListener(this);
        buttonCard4.addMouseListener(this);
        panel5.add(buttonCard4);
        
        displayNumCard4 = new JLabel(" " + numCard4);
        displayNumCard4.setBounds(392, 25, 33, 100);
        displayNumCard4.setVisible(true);
        panel5.add(displayNumCard4);
        
        buttonCard5 = new JButton();
        buttonCard5.setIcon(new RescaledIcon(this.getClass().getResource(Card.NeutraliserSkillStealer.upMonster.cardPathAtt)));
        YuGiOhJi.setButtonProperties(buttonCard5, 425, 25, 67, 100, numCard5>0);
        buttonCard5.addActionListener(this);
        buttonCard5.addMouseListener(this);
        panel5.add(buttonCard5);
        
        displayNumCard5 = new JLabel(" " + numCard5);
        displayNumCard5.setBounds(497, 25, 33, 100);
        displayNumCard5.setVisible(true);
        panel5.add(displayNumCard5);
        
        buttonCard6 = new JButton();
        buttonCard6.setIcon(new RescaledIcon(this.getClass().getResource(Card.CopyCatCardGrabber.upMonster.cardPathAtt)));
        YuGiOhJi.setButtonProperties(buttonCard6, 525, 25, 67, 100, numCard6>0);
        buttonCard6.addActionListener(this);
        buttonCard6.addMouseListener(this);
        panel5.add(buttonCard6);
        
        displayNumCard6 = new JLabel(" " + numCard6);
        displayNumCard6.setBounds(592, 25, 33, 100);
        displayNumCard6.setVisible(true);
        panel5.add(displayNumCard6);
        
        
        
        buttonCard7 = new JButton();
        buttonCard7.setIcon(new RescaledIcon(this.getClass().getResource(Card.AttackStopperBuggedUpgrade.upMonster.cardPathAtt)));
        YuGiOhJi.setButtonProperties(buttonCard7, 25, 125, 67, 100, numCard7>0);
        buttonCard7.addActionListener(this);
        buttonCard7.addMouseListener(this);
        panel5.add(buttonCard7);
        
        displayNumCard7 = new JLabel(" " + numCard7);
        displayNumCard7.setBounds(92, 125, 33, 100);
        displayNumCard7.setVisible(true);
        panel5.add(displayNumCard7);
        
        buttonCard8 = new JButton();
        buttonCard8.setIcon(new RescaledIcon(this.getClass().getResource(Card.BigAttackStopperSword.upMonster.cardPathAtt)));
        YuGiOhJi.setButtonProperties(buttonCard8, 125, 125, 67, 100, numCard8>0);
        buttonCard8.addActionListener(this);
        buttonCard8.addMouseListener(this);
        panel5.add(buttonCard8);
        
        displayNumCard8 = new JLabel(" " + numCard8);
        displayNumCard8.setBounds(192, 125, 33, 100);
        displayNumCard8.setVisible(true);
        panel5.add(displayNumCard8);
        
        buttonCard9 = new JButton();
        buttonCard9.setIcon(new RescaledIcon(this.getClass().getResource(Card.DiamondSwordShield.upMonster.cardPathAtt)));
        YuGiOhJi.setButtonProperties(buttonCard9, 225, 125, 67, 100, numCard9>0);
        buttonCard9.addActionListener(this);
        buttonCard9.addMouseListener(this);
        panel5.add(buttonCard9);
        
        displayNumCard9 = new JLabel(" " + numCard9);
        displayNumCard9.setBounds(292, 125, 33, 100);
        displayNumCard9.setVisible(true);
        panel5.add(displayNumCard9);
        
        buttonCard10 = new JButton();
        buttonCard10.setIcon(new RescaledIcon(this.getClass().getResource(Card.ModeChangerExhaustedExecutioner.upMonster.cardPathAtt)));
        YuGiOhJi.setButtonProperties(buttonCard10, 325, 125, 67, 100, numCard10>0);
        buttonCard10.addActionListener(this);
        buttonCard10.addMouseListener(this);
        panel5.add(buttonCard10);
        
        displayNumCard10 = new JLabel(" " + numCard10);
        displayNumCard10.setBounds(392, 125, 33, 100);
        displayNumCard10.setVisible(true);
        panel5.add(displayNumCard10);
        
        buttonCard11 = new JButton();
        buttonCard11.setIcon(new RescaledIcon(this.getClass().getResource(Card.SlickRusherRecklessRusher.upMonster.cardPathAtt)));
        YuGiOhJi.setButtonProperties(buttonCard11, 425, 125, 67, 100, numCard11>0);
        buttonCard11.addActionListener(this);
        buttonCard11.addMouseListener(this);
        panel5.add(buttonCard11);
        
        displayNumCard11 = new JLabel(" " + numCard11);
        displayNumCard11.setBounds(492, 125, 33, 100);
        displayNumCard11.setVisible(true);
        panel5.add(displayNumCard11);
        
        buttonCard12 = new JButton();
        buttonCard12.setIcon(new RescaledIcon(this.getClass().getResource(Card.IncorruptibleHolyLance.upMonster.cardPathAtt)));
        YuGiOhJi.setButtonProperties(buttonCard12, 525, 125, 67, 100, numCard12>0);
        buttonCard12.addActionListener(this);
        buttonCard12.addMouseListener(this);
        panel5.add(buttonCard12);
        
        displayNumCard12 = new JLabel(" " + numCard12);
        displayNumCard12.setBounds(592, 125, 33, 100);
        displayNumCard12.setVisible(true);
        panel5.add(displayNumCard12);
        
        
        
        buttonCard13 = new JButton();
        buttonCard13.setIcon(new RescaledIcon(this.getClass().getResource(Card.NecromancerBackBouncer.upMonster.cardPathAtt)));
        YuGiOhJi.setButtonProperties(buttonCard13, 25, 225, 67, 100, numCard13>0);
        buttonCard13.addActionListener(this);
        buttonCard13.addMouseListener(this);
        panel5.add(buttonCard13);
        
        displayNumCard13 = new JLabel(" " + numCard13);
        displayNumCard13.setBounds(92, 225, 33, 100);
        displayNumCard13.setVisible(true);
        panel5.add(displayNumCard13);
        
        buttonCard14 = new JButton();
        buttonCard14.setIcon(new RescaledIcon(this.getClass().getResource(Card.BigBackBouncerBanisher.upMonster.cardPathAtt)));
        YuGiOhJi.setButtonProperties(buttonCard14, 125, 225, 67, 100, numCard14>0);
        buttonCard14.addActionListener(this);
        buttonCard14.addMouseListener(this);
        panel5.add(buttonCard14);
        
        displayNumCard14 = new JLabel(" " + numCard14);
        displayNumCard14.setBounds(192, 225, 33, 100);
        displayNumCard14.setVisible(true);
        panel5.add(displayNumCard14);
        
        buttonCard15 = new JButton();
        buttonCard15.setIcon(new RescaledIcon(this.getClass().getResource(Card.BigBanisherBurner.upMonster.cardPathAtt)));
        YuGiOhJi.setButtonProperties(buttonCard15, 225, 225, 67, 100, numCard15>0);
        buttonCard15.addActionListener(this);
        buttonCard15.addMouseListener(this);
        panel5.add(buttonCard15);
        
        displayNumCard15 = new JLabel(" " + numCard15);
        displayNumCard15.setBounds(292, 225, 33, 100);
        displayNumCard15.setVisible(true);
        panel5.add(displayNumCard15);
        
        buttonCard16 = new JButton();
        buttonCard16.setIcon(new RescaledIcon(this.getClass().getResource(Card.BigBurnerSuicideCommando.upMonster.cardPathAtt)));
        YuGiOhJi.setButtonProperties(buttonCard16, 325, 225, 67, 100, numCard16>0);
        buttonCard16.addActionListener(this);
        buttonCard16.addMouseListener(this);
        panel5.add(buttonCard16);
        
        displayNumCard16 = new JLabel(" " + numCard16);
        displayNumCard16.setBounds(392, 225, 33, 100);
        displayNumCard16.setVisible(true);
        panel5.add(displayNumCard16);
        
        buttonCard17 = new JButton();
        buttonCard17.setIcon(new RescaledIcon(this.getClass().getResource(Card.FlakshipNapalm.upMonster.cardPathAtt)));
        YuGiOhJi.setButtonProperties(buttonCard17, 425, 225, 67, 100, numCard17>0);
        buttonCard17.addActionListener(this);
        buttonCard17.addMouseListener(this);
        panel5.add(buttonCard17);
        
        displayNumCard17 = new JLabel(" " + numCard17);
        displayNumCard17.setBounds(492, 225, 33, 100);
        displayNumCard17.setVisible(true);
        panel5.add(displayNumCard17);
        
        buttonCard18 = new JButton();
        buttonCard18.setIcon(new RescaledIcon(this.getClass().getResource(Card.DemonGodDemon.upMonster.cardPathAtt)));
        YuGiOhJi.setButtonProperties(buttonCard18, 525, 225, 67, 100, numCard18>0);
        buttonCard18.addActionListener(this);
        buttonCard18.addMouseListener(this);
        panel5.add(buttonCard18);
        
        displayNumCard18 = new JLabel(" " + numCard18);
        displayNumCard18.setBounds(592, 225, 33, 100);
        displayNumCard18.setVisible(true);
        panel5.add(displayNumCard18);
        
        buttonOK = new JButton("OK");
        buttonOK.setBounds(25, 350, 150, 25);
        buttonOK.addActionListener(this);
        buttonOK.setVisible(false); // becomes visible when clicking on a card
        panel5.add(buttonOK);
        
        frame.add(panel5);
        
    }
    
    // --- methods ---
    
    @Override
    public void componentHidden(ComponentEvent ce) {};
    @Override
    public void componentShown(ComponentEvent ce) {};
    @Override
    public void componentMoved(ComponentEvent ce) {};
    @Override
    public void componentResized(ComponentEvent ce) {
        currentSearchWidth = frame.getWidth();
        currentSearchHeight = frame.getHeight();
        rescaleEverything();
    };
    
    // opens the window for choosing a card to search from the deck or GY
    public static void searchDeck (Deck SearchedDeck, boolean isMovingToGY, boolean isRestrictingToMooks, boolean isRestrictingToMidbosses, boolean isRestrictingToGods, boolean isRestrictingToBanishBounceBurn){
        ConsideredDeck=SearchedDeck;
        if (ConsideredDeck==DeckPlayer) {
            isGettingCardFromDeck=true;
            isGettingCardFromGY=false;
            isGettingCardFromGYCPU=false;
            isLookingAtBanishingZone=false;
        }
        else if (ConsideredDeck==GYDeckPlayer) {
            isGettingCardFromDeck=false;
            isGettingCardFromGY=true;
            isGettingCardFromGYCPU=false;
            isLookingAtBanishingZone=false;
        }
        else if (ConsideredDeck==GYDeckCPU) {
            isGettingCardFromDeck=false;
            isGettingCardFromGY=false;
            isGettingCardFromGYCPU=true;
            isLookingAtBanishingZone=false;
        }
        else if (ConsideredDeck==DDDeckPlayer || ConsideredDeck==DDDeckCPU) {
            isGettingCardFromDeck=false;
            isGettingCardFromGY=false;
            isGettingCardFromGYCPU=false;
            isLookingAtBanishingZone=true;
        }
        isSendingToGY=isMovingToGY; // for Foolish Burial effect
        isOnlyAllowingMooks=isRestrictingToMooks;
        isOnlyAllowingMidbosses=isRestrictingToMidbosses;
        isOnlyAllowingGods=isRestrictingToGods;
        isOnlyAllowingBanishBounceBurn=isRestrictingToBanishBounceBurn;
        window5 = new SearchWindow(SearchedDeck);
        window5.frame.setVisible(true);
        double scaleFactorX = (double) YuGiOhJi.currentFrameWidth/YuGiOhJi.initialFrameWidth;
        double scaleFactorY = (double) YuGiOhJi.currentFrameHeight/YuGiOhJi.initialFrameHeight;
        int windowWidth = (int) Math.round(initialSearchWidth*scaleFactorX*0.9);
        int windowHeight = (int) Math.round(initialSearchHeight*scaleFactorY*0.95);
        window5.frame.setSize(windowWidth, windowHeight);
    }
    
    // calculates rescaling of horizontal coordinates (and widths)
    public static int rescaleX (int x) {
        int newValue = (int) Math.round(x*currentSearchWidth/initialSearchWidth);
        return newValue;
    }
    
    // calculates rescaling of vertical coordinates (and heights)
    public static int rescaleY (int y) {
        int newValue = (int) Math.round(y*currentSearchHeight/initialSearchHeight);
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
    
    // simply rescales all graphical components, i.e. all buttons and labels, one by one
    public static void rescaleEverything() {
        // rescale all labels
        rescaleLabel(displayNumCard1, 92, 25, 33, 100);
        rescaleLabel(displayNumCard2, 192, 25, 33, 100);
        rescaleLabel(displayNumCard3, 292, 25, 33, 100);
        rescaleLabel(displayNumCard4, 392, 25, 33, 100);
        rescaleLabel(displayNumCard5, 497, 25, 33, 100);
        rescaleLabel(displayNumCard6, 592, 25, 33, 100);
        rescaleLabel(displayNumCard7, 92, 125, 33, 100);
        rescaleLabel(displayNumCard8, 192, 125, 33, 100);
        rescaleLabel(displayNumCard9, 292, 125, 33, 100);
        rescaleLabel(displayNumCard10, 392, 125, 33, 100);
        rescaleLabel(displayNumCard11, 492, 125, 33, 100);
        rescaleLabel(displayNumCard12, 592, 125, 33, 100);
        rescaleLabel(displayNumCard13, 92, 225, 33, 100);
        rescaleLabel(displayNumCard14, 192, 225, 33, 100);
        rescaleLabel(displayNumCard15, 292, 225, 33, 100);
        rescaleLabel(displayNumCard16, 392, 225, 33, 100);
        rescaleLabel(displayNumCard17, 492, 225, 33, 100);
        rescaleLabel(displayNumCard18, 592, 225, 33, 100);
        // rescale all buttons
        rescaleButton(buttonCard1, 25, 25, 67, 100);
        rescaleButton(buttonCard2, 125, 25, 67, 100);
        rescaleButton(buttonCard3, 225, 25, 67, 100);
        rescaleButton(buttonCard4, 325, 25, 67, 100);
        rescaleButton(buttonCard5, 425, 25, 67, 100);
        rescaleButton(buttonCard6, 525, 25, 67, 100);
        rescaleButton(buttonCard7, 25, 125, 67, 100);
        rescaleButton(buttonCard8, 125, 125, 67, 100);
        rescaleButton(buttonCard9, 225, 125, 67, 100);
        rescaleButton(buttonCard10, 325, 125, 67, 100);
        rescaleButton(buttonCard11, 425, 125, 67, 100);
        rescaleButton(buttonCard12, 525, 125, 67, 100);
        rescaleButton(buttonCard13, 25, 225, 67, 100);
        rescaleButton(buttonCard14, 125, 225, 67, 100);
        rescaleButton(buttonCard15, 225, 225, 67, 100);
        rescaleButton(buttonCard16, 325, 225, 67, 100);
        rescaleButton(buttonCard17, 425, 225, 67, 100);
        rescaleButton(buttonCard18, 525, 225, 67, 100);
        rescaleButton(buttonOK, 25, 350, 150, 25);
    }
    
    // counts how many cards of which kind are in the rest of the deck (for displaying these numbers)
    public static void countCardNumbersInDeck (Deck SearchedDeck){
        clearNumbers();
        for (int index = SearchedDeck.numberOfCards; index >= 1; index--){
            int cardId=SearchedDeck.getNthCardOfDeck(index).cardId;
            switch (cardId) {
                case 1: numCard1++; break;
                case 2: numCard2++; break;
                case 3: numCard3++; break;
                case 4: numCard4++; break;
                case 5: numCard5++; break;
                case 6: numCard6++; break;
                case 7: numCard7++; break;
                case 8: numCard8++; break;
                case 9: numCard9++; break;
                case 10: numCard10++; break;
                case 11: numCard11++; break;
                case 12: numCard12++; break;
                case 13: numCard13++; break;
                case 14: numCard14++; break;
                case 15: numCard15++; break;
                case 16: numCard16++; break;
                case 17: numCard17++; break;
                case 18: numCard18++; break;
                default: YuGiOhJi.debugDialog("Error: out of bounds in SearchWindow.countCardNumbersInDeck(...); attempted cardId: " + cardId); break;
            }
        }
    }
    
    // puts the numbers to zero again
    public static void clearNumbers(){
        intDeckCardId=0;
        numCard1=0;
        numCard2=0;
        numCard3=0;
        numCard4=0;
        numCard5=0;
        numCard6=0;
        numCard7=0;
        numCard8=0;
        numCard9=0;
        numCard10=0;
        numCard11=0;
        numCard12=0;
        numCard13=0;
        numCard14=0;
        numCard15=0;
        numCard16=0;
        numCard17=0;
        numCard18=0;
    }
    
    // shows an error message, if one wants to choose a card not containing a MOOK
    public static void errorNoMook(){
        YuGiOhJi.errorDialog("You have to choose a card containing a MOOK.", "Error: No valid target.");
    }
    
    // shows an error message, if one wants to choose a card not containing a MIDBOSS
    public static void errorNoMidboss(){
        YuGiOhJi.errorDialog("You have to choose a card containing a MIDBOSS.", "Error: No valid target.");
    }
    
    // shows an error message, if one wants to choose a card not containing a GOD
    public static void errorNoGod(){
        YuGiOhJi.errorDialog("You have to choose a card containing a GOD.", "Error: No valid target.");
    }
    
    // shows an error message, if one wants to choose a card not containing a GOD
    public static void errorNoBanishBounceBurn(){
        YuGiOhJi.errorDialog("You have to choose a card with \"Banish\", \"Bounce\" or \"Burn\" in its name.", "Error: No valid target.");
    }
    
    // in order to less repeat oneself, out-source here what happens, when one selects a card in deck/GY/DD
    public static void chooseCard (int cardID){
        if (Game.isSwitchingOnInfoDialogs) {
            int intDialogResult = YuGiOhJi.multipleChoiceDialog("What do you want to do?", "", new String[]{"play card", "look at card info"}, "");
            if (intDialogResult==0) {
                selectCard(cardID);
            }
            else if (intDialogResult==1) {
                openInfoWindow(cardID);
            }
        }
        else {
            selectCard(cardID);
        }
    }
    
    // in order not to repeat oneself, out-source here opening of info window
    public static void openInfoWindow (int cardID){
        if (isGettingCardFromDeck) {
            YCardInfoWindow.openInfoWindowForDeckCard(YCard.getCardByCardId(cardID));
        }
        else if (isGettingCardFromGY || isGettingCardFromGYCPU) {
            YCardInfoWindow.openInfoWindowForGYCard(YCard.getCardByCardId(cardID));
        }
        else if (isLookingAtBanishingZone) {
            YCardInfoWindow.openInfoWindowForBanishedCard(YCard.getCardByCardId(cardID));
        }
    }
    
    // in order not to repeat oneself, out-source here what happens if one finally decided on a card
    public static void selectCard (int cardID){
        Game.actEffCardId = cardID; // important info for helping CPU deciding if wants to negate
        YCard Card = YCard.getCardByCardId(cardID);
        if (isOnlyAllowingMooks && !Card.isContainingMook()) {
            errorNoMook();
        }
        else if (isOnlyAllowingMidbosses && !Card.isContainingMidboss()) {
            errorNoMidboss();
        }
        else if (isOnlyAllowingGods && !Card.isContainingGod()) {
            errorNoGod();
        }
        else if (isOnlyAllowingBanishBounceBurn && !Card.isBanishBounceOrBurnCard()) {
            errorNoBanishBounceBurn();
        }
        else if (!isLookingAtBanishingZone) { // one can only select cards, if one is not looking at banishing zone (different dimenion)
            intDeckCardId=cardID; buttonOK.setVisible(true);
        }
    }
    
    // from here on:
    // --- reactions of the buttons ---
    
    @Override
    public void actionPerformed (ActionEvent ae){
        
        // confirm for getting the chosen card from deck
        if (ae.getSource() == SearchWindow.buttonOK && isGettingCardFromDeck) {
            YuGiOhJi.window5.frame.dispose();
            if (!isSendingToGY) {
                boolean isCanceling = AIinterrupts.cpuIsUsingEffectNegate();
                if (!isCanceling) {
                    for (int index = DeckPlayer.numberOfCards; index >= 1; index--){
                        if (DeckPlayer.getNthCardOfDeck(index).cardId==intDeckCardId) {
                            if (Game.isActEffSearch()) {
                                YMonster.effectCardGrabber1Execute(index, true);
                            }
                            else {
                                YMonster.banishSearchExecute(intDeckCardId, true);
                            }
                            clearNumbers();
                            break;
                        }
                    }
                }
                else {Game.deactivateCurrentEffects();}
            }
            else { // Foolish Burial effect of Card Grabber
                boolean isCanceling = AIinterrupts.cpuIsUsingEffectNegate();
                if (!isCanceling) {
                    for (int index = DeckPlayer.numberOfCards; index >= 1; index--){
                        if (DeckPlayer.getNthCardOfDeck(index).cardId==intDeckCardId) {
                            clearNumbers();
                            YMonster.effectCardGrabber2Execute(index, true);
                            break;
                        }
                    }
                }
                else {Game.deactivateCurrentEffects();}
            }
        }
        
        // choose a card as target from graveyard
        if (ae.getSource() == SearchWindow.buttonOK && isGettingCardFromGY) {
            YuGiOhJi.window5.frame.dispose();
            for (int index = GYDeckPlayer.numberOfCards; index >= 1; index--){
                if (GYDeckPlayer.getNthCardOfDeck(index).cardId==intDeckCardId) {
                    if (!Game.isActiveSomeEffect()) {
                        CardOptions.attemptPlayCard(GYDeckPlayer.getNthCardOfDeck(index), index, true);
                    }
                    else {
                        if (Game.isReadyToChooseEffTarget) {
                            YMonster.executeOptionalEffectToGYCard(true, index);
                        }
                    }
                    break;
                }
            }
        }
        
        // choose a card as target from computer's graveyard
        if (ae.getSource() == SearchWindow.buttonOK && isGettingCardFromGYCPU) {
            YuGiOhJi.window5.frame.dispose();
            for (int index = GYDeckCPU.numberOfCards; index >= 1; index--){
                if (GYDeckCPU.getNthCardOfDeck(index).cardId==intDeckCardId) {
                    if (Game.isActiveSomeEffect() && Game.isReadyToChooseEffTarget) {
                        YMonster.executeOptionalEffectToGYCard(false, index);
                        // for targetting a card with effect of Big Back Bouncer or Necromancer
                    }
                    break;
                }
            }
        }
        
        if (ae.getSource()==SearchWindow.buttonCard1) {chooseCard(1);}
        if (ae.getSource()==SearchWindow.buttonCard2) {chooseCard(2);}
        if (ae.getSource()==SearchWindow.buttonCard3) {chooseCard(3);}
        if (ae.getSource()==SearchWindow.buttonCard4) {chooseCard(4);}
        if (ae.getSource()==SearchWindow.buttonCard5) {chooseCard(5);}
        if (ae.getSource()==SearchWindow.buttonCard6) {chooseCard(6);}
        if (ae.getSource()==SearchWindow.buttonCard7) {chooseCard(7);}
        if (ae.getSource()==SearchWindow.buttonCard8) {chooseCard(8);}
        if (ae.getSource()==SearchWindow.buttonCard9) {chooseCard(9);}
        if (ae.getSource()==SearchWindow.buttonCard10) {chooseCard(10);}
        if (ae.getSource()==SearchWindow.buttonCard11) {chooseCard(11);}
        if (ae.getSource()==SearchWindow.buttonCard12) {chooseCard(12);}
        if (ae.getSource()==SearchWindow.buttonCard13) {chooseCard(13);}
        if (ae.getSource()==SearchWindow.buttonCard14) {chooseCard(14);}
        if (ae.getSource()==SearchWindow.buttonCard15) {chooseCard(15);}
        if (ae.getSource()==SearchWindow.buttonCard16) {chooseCard(16);}
        if (ae.getSource()==SearchWindow.buttonCard17) {chooseCard(17);}
        if (ae.getSource()==SearchWindow.buttonCard18) {chooseCard(18);}
        
    }
    
    // in order not to repeat oneself, out-source here what happens when one hovers with the mouse over a card
    public static void hoverOnCard (String imagePath){
        YuGiOhJi.preview.setIcon(new RescaledIcon(YuGiOhJi.class.getResource(imagePath)));
    }
    
    // what happens when one enters a certain zone with the mouse (show big preview screen depending on card)
    @Override
    public void mouseEntered(MouseEvent me) {
        // display larger (readable) version of cards in deck searching window
        if (me.getSource() == SearchWindow.buttonCard1) {hoverOnCard(Card.GodBarrier.bigCardPath);}
        if (me.getSource() == SearchWindow.buttonCard2) {hoverOnCard(Card.EradicatorObstacle.bigCardPath);}
        if (me.getSource() == SearchWindow.buttonCard3) {hoverOnCard(Card.GodKillingSpearLance.bigCardPath);}
        if (me.getSource() == SearchWindow.buttonCard4) {hoverOnCard(Card.MonsterStealerSteepLearningCurve.bigCardPath);}
        if (me.getSource() == SearchWindow.buttonCard5) {hoverOnCard(Card.NeutraliserSkillStealer.bigCardPath);}
        if (me.getSource() == SearchWindow.buttonCard6) {hoverOnCard(Card.CopyCatCardGrabber.bigCardPath);}
        if (me.getSource() == SearchWindow.buttonCard7) {hoverOnCard(Card.AttackStopperBuggedUpgrade.bigCardPath);}
        if (me.getSource() == SearchWindow.buttonCard8) {hoverOnCard(Card.BigAttackStopperSword.bigCardPath);}
        if (me.getSource() == SearchWindow.buttonCard9) {hoverOnCard(Card.DiamondSwordShield.bigCardPath);}
        if (me.getSource() == SearchWindow.buttonCard10) {hoverOnCard(Card.ModeChangerExhaustedExecutioner.bigCardPath);}
        if (me.getSource() == SearchWindow.buttonCard11) {hoverOnCard(Card.SlickRusherRecklessRusher.bigCardPath);}
        if (me.getSource() == SearchWindow.buttonCard12) {hoverOnCard(Card.IncorruptibleHolyLance.bigCardPath);}
        if (me.getSource() == SearchWindow.buttonCard13) {hoverOnCard(Card.NecromancerBackBouncer.bigCardPath);}
        if (me.getSource() == SearchWindow.buttonCard14) {hoverOnCard(Card.BigBackBouncerBanisher.bigCardPath);}
        if (me.getSource() == SearchWindow.buttonCard15) {hoverOnCard(Card.BigBanisherBurner.bigCardPath);}
        if (me.getSource() == SearchWindow.buttonCard16) {hoverOnCard(Card.BigBurnerSuicideCommando.bigCardPath);}
        if (me.getSource() == SearchWindow.buttonCard17) {hoverOnCard(Card.FlakshipNapalm.bigCardPath);}
        if (me.getSource() == SearchWindow.buttonCard18) {hoverOnCard(Card.DemonGodDemon.bigCardPath);}
    
    }
    
    // what happens when one leaves any zone (just show the big version of the back side of each card)
    @Override
    public void mouseExited(MouseEvent me) {
        YuGiOhJi.preview.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));   
    }
    
    // these overridden functions need to be here in order for the program to work
    @Override
    public void mouseClicked(MouseEvent me) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mousePressed(MouseEvent me) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
