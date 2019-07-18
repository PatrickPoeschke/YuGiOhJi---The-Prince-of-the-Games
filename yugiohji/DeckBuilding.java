package yugiohji;

/**
 * This class creates windows for loading and creating/saving (custom) YuGiOhJi-decks.
 * You can load one of several standard decks.
 * The cards in them and a short description of the deck are shown.
 * You can also create, save and load your own custom deck.
 * 
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import static yugiohji.YuGiOhJi.Card;
import static yugiohji.YuGiOhJi.Game;
import static yugiohji.YuGiOhJi.Mon;
import static yugiohji.YuGiOhJi.window2;

public class DeckBuilding extends JFrame implements ActionListener, MouseListener, ComponentListener {
    // modifying JDialogs is considered better than JFrames as secondary windows
    // however, this works fine as well
    
    public static int initialDeckBuildingWidth=900;
    public static int initialDeckBuildingHeight=550;
    public static int currentDeckBuildingWidth=900;
    public static int currentDeckBuildingHeight=550;
    
    public JPanel panel2;
    public static JButton buttonLoad;
    public static JButton buttonSave;
    public static JButton buttonClearDeck;
    public static JButton buttonAcceptDeck;
    
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
    
    public static JButton buttonDeck1;
    public static JButton buttonDeck2;
    public static JButton buttonDeck3;
    public static JButton buttonDeck4;
    public static JButton buttonDeck5;
    public static JButton buttonDeck6;
    public static JButton buttonDeck7;
    public static JButton buttonDeck8;
    public static JButton buttonDeck9;
    public static JButton buttonDeck10;
    
    public static JButton buttonCustomDeckCPU;
    public static JButton buttonRandomDeckCPU;
    
    String deckBuildingExplanation;
    static JLabel displayDeckDesription;
    static JLabel displaySumOfCards;
    static JLabel displayNumCard1;
    static JLabel displayNumCard2;
    static JLabel displayNumCard3;
    static JLabel displayNumCard4;
    static JLabel displayNumCard5;
    static JLabel displayNumCard6;
    static JLabel displayNumCard7;
    static JLabel displayNumCard8;
    static JLabel displayNumCard9;
    static JLabel displayNumCard10;
    static JLabel displayNumCard11;
    static JLabel displayNumCard12;
    static JLabel displayNumCard13;
    static JLabel displayNumCard14;
    static JLabel displayNumCard15;
    static JLabel displayNumCard16;
    static JLabel displayNumCard17;
    static JLabel displayNumCard18;
    
    static int numCard1=0;
    static int numCard2=0;
    static int numCard3=0;
    static int numCard4=0;
    static int numCard5=0;
    static int numCard6=0;
    static int numCard7=0;
    static int numCard8=0;
    static int numCard9=0;
    static int numCard10=0;
    static int numCard11=0;
    static int numCard12=0;
    static int numCard13=0;
    static int numCard14=0;
    static int numCard15=0;
    static int numCard16=0;
    static int numCard17=0;
    static int numCard18=0;
    static int sum;
    
    public static Integer[] customDeck = new Integer[17];
    static boolean loaded;
    
    // constructor
    public DeckBuilding() {
        
        this.setTitle("Create your own custom deck, or try one of many standard decks.");
        double scaleFactorX = (double) YuGiOhJi.currentFrameWidth/YuGiOhJi.initialFrameWidth;
        double scaleFactorY = (double) YuGiOhJi.currentFrameHeight/YuGiOhJi.initialFrameHeight;
        int posX = (int) Math.round(408*scaleFactorX);
        int posY = (int) Math.round(30*scaleFactorY);
        this.setLocation(posX, posY);
        this.setSize(initialDeckBuildingWidth, initialDeckBuildingHeight);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().addComponentListener(this);
        panel2 = new JPanel();
        panel2.setLayout(null);
        
        buttonLoad = new JButton("load custom deck");
        buttonLoad.setBounds(25, 25, 250, 25);
        buttonLoad.addActionListener(this);
        panel2.add(buttonLoad);
        
        buttonSave = new JButton("save deck");
        buttonSave.setBounds(25, 50, 150, 25);
        buttonSave.addActionListener(this);
        panel2.add(buttonSave);
        
        displaySumOfCards = new JLabel(" sum: " + 0 + " /39");
        displaySumOfCards.setBounds(175, 50, 100, 25);
        displaySumOfCards.setVisible(true);
        panel2.add(displaySumOfCards);
        
        buttonClearDeck = new JButton("clear deck");
        buttonClearDeck.setBounds(25, 75, 150, 25);
        buttonClearDeck.addActionListener(this);
        panel2.add(buttonClearDeck);
        
        buttonAcceptDeck = new JButton("take current deck and close");
        buttonAcceptDeck.setBounds(25, 100, 250, 25);
        buttonAcceptDeck.addActionListener(this);
        panel2.add(buttonAcceptDeck);
        
        buttonDeck1 = new JButton("The starter deck");
        buttonDeck1.setBounds(25, 150, 250, 25);
        buttonDeck1.addActionListener(this);
        panel2.add(buttonDeck1);
        
        buttonDeck2 = new JButton("No self-protection deck");
        buttonDeck2.setBounds(25, 175, 250, 25);
        buttonDeck2.addActionListener(this);
        panel2.add(buttonDeck2);
        
        buttonDeck3 = new JButton("No equip deck");
        buttonDeck3.setBounds(25, 200, 250, 25);
        buttonDeck3.addActionListener(this);
        panel2.add(buttonDeck3);
        
        buttonDeck4 = new JButton("No trap monsters deck");
        buttonDeck4.setBounds(25, 225, 250, 25);
        buttonDeck4.addActionListener(this);
        panel2.add(buttonDeck4);
        
        buttonDeck5 = new JButton("No removal deck");
        buttonDeck5.setBounds(25, 250, 250, 25);
        buttonDeck5.addActionListener(this);
        panel2.add(buttonDeck5);
        
        buttonDeck6 = new JButton("No Banish-Bounce-Burn deck");
        buttonDeck6.setBounds(25, 275, 250, 25);
        buttonDeck6.addActionListener(this);
        panel2.add(buttonDeck6);
        
        buttonDeck7 = new JButton("No hand traps deck");
        buttonDeck7.setBounds(25, 300, 250, 25);
        buttonDeck7.addActionListener(this);
        panel2.add(buttonDeck7);
        
        buttonDeck8 = new JButton("The complementary deck");
        buttonDeck8.setBounds(25, 325, 250, 25);
        buttonDeck8.addActionListener(this);
        panel2.add(buttonDeck8);
        
        buttonDeck9 = new JButton("The unpredictable deck");
        buttonDeck9.setBounds(25, 350, 250, 25);
        buttonDeck9.addActionListener(this);
        panel2.add(buttonDeck9);
        
        buttonDeck10 = new JButton("The trickster deck");
        buttonDeck10.setBounds(25, 425, 250, 25);
        buttonDeck10.addActionListener(this);
        panel2.add(buttonDeck10);
        
        buttonCustomDeckCPU = new JButton("give current deck to opponent");
        buttonCustomDeckCPU.setBounds(25, 450, 250, 25);
        buttonCustomDeckCPU.addActionListener(this);
        panel2.add(buttonCustomDeckCPU);
        
        buttonRandomDeckCPU = new JButton("let opponent use own random decks");
        buttonRandomDeckCPU.setBounds(25, 375, 250, 25);
        buttonRandomDeckCPU.addActionListener(this);
        panel2.add(buttonRandomDeckCPU);
        
        buttonCard1 = new JButton();
        buttonCard1.setIcon(new RescaledIcon(this.getClass().getResource(Card.GodBarrier.upMonster.cardPathAtt)));
        YuGiOhJi.setButtonProperties(buttonCard1, 300, 25, 67, 100, true);
        buttonCard1.addActionListener(this);
        buttonCard1.addMouseListener(this);
        panel2.add(buttonCard1);
        
        displayNumCard1 = new JLabel(" " + numCard1);
        displayNumCard1.setBounds(367, 25, 33, 100);
        displayNumCard1.setVisible(true);
        panel2.add(displayNumCard1);
        
        buttonCard2 = new JButton();
        buttonCard2.setIcon(new RescaledIcon(this.getClass().getResource(Card.EradicatorObstacle.upMonster.cardPathAtt)));
        YuGiOhJi.setButtonProperties(buttonCard2, 400, 25, 67, 100, true);
        buttonCard2.addActionListener(this);
        buttonCard2.addMouseListener(this);
        panel2.add(buttonCard2);
        
        displayNumCard2 = new JLabel(" " + numCard2);
        displayNumCard2.setBounds(467, 25, 33, 100);
        displayNumCard2.setVisible(true);
        panel2.add(displayNumCard2);
        
        buttonCard3 = new JButton();
        buttonCard3.setIcon(new RescaledIcon(this.getClass().getResource(Card.GodKillingSpearLance.upMonster.cardPathAtt)));
        YuGiOhJi.setButtonProperties(buttonCard3, 500, 25, 67, 100, true);
        buttonCard3.addActionListener(this);
        buttonCard3.addMouseListener(this);
        panel2.add(buttonCard3);
        
        displayNumCard3 = new JLabel(" " + numCard3);
        displayNumCard3.setBounds(567, 25, 33, 100);
        displayNumCard3.setVisible(true);
        panel2.add(displayNumCard3);
        
        buttonCard4 = new JButton();
        buttonCard4.setIcon(new RescaledIcon(this.getClass().getResource(Card.MonsterStealerSteepLearningCurve.upMonster.cardPathAtt)));
        YuGiOhJi.setButtonProperties(buttonCard4, 600, 25, 67, 100, true);
        buttonCard4.addActionListener(this);
        buttonCard4.addMouseListener(this);
        panel2.add(buttonCard4);
        
        displayNumCard4 = new JLabel(" " + numCard4);
        displayNumCard4.setBounds(667, 25, 33, 100);
        displayNumCard4.setVisible(true);
        panel2.add(displayNumCard4);
        
        buttonCard5 = new JButton();
        buttonCard5.setIcon(new RescaledIcon(this.getClass().getResource(Card.NeutraliserSkillStealer.upMonster.cardPathAtt)));
        YuGiOhJi.setButtonProperties(buttonCard5, 700, 25, 67, 100, true);
        buttonCard5.addActionListener(this);
        buttonCard5.addMouseListener(this);
        panel2.add(buttonCard5);
        
        displayNumCard5 = new JLabel(" " + numCard5);
        displayNumCard5.setBounds(767, 25, 33, 100);
        displayNumCard5.setVisible(true);
        panel2.add(displayNumCard5);
        
        buttonCard6 = new JButton();
        buttonCard6.setIcon(new RescaledIcon(this.getClass().getResource(Card.CopyCatCardGrabber.upMonster.cardPathAtt)));
        YuGiOhJi.setButtonProperties(buttonCard6, 800, 25, 67, 100, true);
        buttonCard6.addActionListener(this);
        buttonCard6.addMouseListener(this);
        panel2.add(buttonCard6);
        
        displayNumCard6 = new JLabel(" " + numCard6);
        displayNumCard6.setBounds(867, 25, 33, 100);
        displayNumCard6.setVisible(true);
        panel2.add(displayNumCard6);
        
        
        
        buttonCard7 = new JButton();
        buttonCard7.setIcon(new RescaledIcon(this.getClass().getResource(Card.AttackStopperBuggedUpgrade.upMonster.cardPathAtt)));
        YuGiOhJi.setButtonProperties(buttonCard7, 300, 125, 67, 100, true);
        buttonCard7.addActionListener(this);
        buttonCard7.addMouseListener(this);
        panel2.add(buttonCard7);
        
        displayNumCard7 = new JLabel(" " + numCard7);
        displayNumCard7.setBounds(367, 125, 33, 100);
        displayNumCard7.setVisible(true);
        panel2.add(displayNumCard7);
        
        buttonCard8 = new JButton();
        buttonCard8.setIcon(new RescaledIcon(this.getClass().getResource(Card.BigAttackStopperSword.upMonster.cardPathAtt)));
        YuGiOhJi.setButtonProperties(buttonCard8, 400, 125, 67, 100, true);
        buttonCard8.addActionListener(this);
        buttonCard8.addMouseListener(this);
        panel2.add(buttonCard8);
        
        displayNumCard8 = new JLabel(" " + numCard8);
        displayNumCard8.setBounds(467, 125, 33, 100);
        displayNumCard8.setVisible(true);
        panel2.add(displayNumCard8);
        
        buttonCard9 = new JButton();
        buttonCard9.setIcon(new RescaledIcon(this.getClass().getResource(Card.DiamondSwordShield.upMonster.cardPathAtt)));
        YuGiOhJi.setButtonProperties(buttonCard9, 500, 125, 67, 100, true);
        buttonCard9.addActionListener(this);
        buttonCard9.addMouseListener(this);
        panel2.add(buttonCard9);
        
        displayNumCard9 = new JLabel(" " + numCard9);
        displayNumCard9.setBounds(567, 125, 33, 100);
        displayNumCard9.setVisible(true);
        panel2.add(displayNumCard9);
        
        buttonCard10 = new JButton();
        buttonCard10.setIcon(new RescaledIcon(this.getClass().getResource(Card.ModeChangerExhaustedExecutioner.upMonster.cardPathAtt)));
        YuGiOhJi.setButtonProperties(buttonCard10, 600, 125, 67, 100, true);
        buttonCard10.addActionListener(this);
        buttonCard10.addMouseListener(this);
        panel2.add(buttonCard10);
        
        displayNumCard10 = new JLabel(" " + numCard10);
        displayNumCard10.setBounds(667, 125, 33, 100);
        displayNumCard10.setVisible(true);
        panel2.add(displayNumCard10);
        
        buttonCard11 = new JButton();
        buttonCard11.setIcon(new RescaledIcon(this.getClass().getResource(Card.SlickRusherRecklessRusher.upMonster.cardPathAtt)));
        YuGiOhJi.setButtonProperties(buttonCard11, 700, 125, 67, 100, true);
        buttonCard11.addActionListener(this);
        buttonCard11.addMouseListener(this);
        panel2.add(buttonCard11);
        
        displayNumCard11 = new JLabel(" " + numCard11);
        displayNumCard11.setBounds(767, 125, 33, 100);
        displayNumCard11.setVisible(true);
        panel2.add(displayNumCard11);
        
        buttonCard12 = new JButton();
        buttonCard12.setIcon(new RescaledIcon(this.getClass().getResource(Card.IncorruptibleHolyLance.upMonster.cardPathAtt)));
        YuGiOhJi.setButtonProperties(buttonCard12, 800, 125, 67, 100, true);
        buttonCard12.addActionListener(this);
        buttonCard12.addMouseListener(this);
        panel2.add(buttonCard12);
        
        displayNumCard12 = new JLabel(" " + numCard12);
        displayNumCard12.setBounds(867, 125, 33, 100);
        displayNumCard12.setVisible(true);
        panel2.add(displayNumCard12);
        
        
        
        buttonCard13 = new JButton();
        buttonCard13.setIcon(new RescaledIcon(this.getClass().getResource(Card.NecromancerBackBouncer.upMonster.cardPathAtt)));
        YuGiOhJi.setButtonProperties(buttonCard13, 300, 225, 67, 100, true);
        buttonCard13.addActionListener(this);
        buttonCard13.addMouseListener(this);
        panel2.add(buttonCard13);
        
        displayNumCard13 = new JLabel(" " + numCard13);
        displayNumCard13.setBounds(367, 225, 33, 100);
        displayNumCard13.setVisible(true);
        panel2.add(displayNumCard13);
        
        buttonCard14 = new JButton();
        buttonCard14.setIcon(new RescaledIcon(this.getClass().getResource(Card.BigBackBouncerBanisher.upMonster.cardPathAtt)));
        YuGiOhJi.setButtonProperties(buttonCard14, 400, 225, 67, 100, true);
        buttonCard14.addActionListener(this);
        buttonCard14.addMouseListener(this);
        panel2.add(buttonCard14);
        
        displayNumCard14 = new JLabel(" " + numCard14);
        displayNumCard14.setBounds(467, 225, 33, 100);
        displayNumCard14.setVisible(true);
        panel2.add(displayNumCard14);
        
        buttonCard15 = new JButton();
        buttonCard15.setIcon(new RescaledIcon(this.getClass().getResource(Card.BigBanisherBurner.upMonster.cardPathAtt)));
        YuGiOhJi.setButtonProperties(buttonCard15, 500, 225, 67, 100, true);
        buttonCard15.addActionListener(this);
        buttonCard15.addMouseListener(this);
        panel2.add(buttonCard15);
        
        displayNumCard15 = new JLabel(" " + numCard15);
        displayNumCard15.setBounds(567, 225, 33, 100);
        displayNumCard15.setVisible(true);
        panel2.add(displayNumCard15);
        
        buttonCard16 = new JButton();
        buttonCard16.setIcon(new RescaledIcon(this.getClass().getResource(Card.BigBurnerSuicideCommando.upMonster.cardPathAtt)));
        YuGiOhJi.setButtonProperties(buttonCard16, 600, 225, 67, 100, true);
        buttonCard16.addActionListener(this);
        buttonCard16.addMouseListener(this);
        panel2.add(buttonCard16);
        
        displayNumCard16 = new JLabel(" " + numCard16);
        displayNumCard16.setBounds(667, 225, 33, 100);
        displayNumCard16.setVisible(true);
        panel2.add(displayNumCard16);
        
        buttonCard17 = new JButton();
        buttonCard17.setIcon(new RescaledIcon(this.getClass().getResource(Card.FlakshipNapalm.upMonster.cardPathAtt)));
        YuGiOhJi.setButtonProperties(buttonCard17, 700, 225, 67, 100, true);
        buttonCard17.addActionListener(this);
        buttonCard17.addMouseListener(this);
        panel2.add(buttonCard17);
        
        displayNumCard17 = new JLabel(" " + numCard17);
        displayNumCard17.setBounds(767, 225, 33, 100);
        displayNumCard17.setVisible(true);
        panel2.add(displayNumCard17);
        
        buttonCard18 = new JButton();
        buttonCard18.setIcon(new RescaledIcon(this.getClass().getResource(Card.DemonGodDemon.upMonster.cardPathAtt)));
        YuGiOhJi.setButtonProperties(buttonCard18, 800, 225, 67, 100, true);
        buttonCard18.addActionListener(this);
        buttonCard18.addMouseListener(this);
        panel2.add(buttonCard18);
        
        displayNumCard18 = new JLabel(" " + numCard18);
        displayNumCard18.setBounds(867, 225, 33, 100);
        displayNumCard18.setVisible(true);
        panel2.add(displayNumCard18);
        
        
        deckBuildingExplanation=("<html>With the buttons on the left one can load standard decks. By clicking on the cards one can change how often they are in the deck. One can also start from a standard deck and then change it. Start over with the \"clear deck\" button. Finish deck building by pressing the \"take current deck and close\" button.</html>");
        displayDeckDesription = new JLabel(deckBuildingExplanation);
        displayDeckDesription.setBounds(300, 325, 500, 150);
        displayDeckDesription.setVisible(true);
        panel2.add(displayDeckDesription);
        
        this.add(panel2);
        
    }
    
    // --- methods ---
    
    public static void createDeckBuildingWindow() {
        window2 = new DeckBuilding(); // create another window for the purpose of deck building
        window2.setVisible(true);
        double scaleFactorX = (double) YuGiOhJi.currentFrameWidth/YuGiOhJi.initialFrameWidth;
        double scaleFactorY = (double) YuGiOhJi.currentFrameHeight/YuGiOhJi.initialFrameHeight;
        int windowWidth = (int) Math.round(750*scaleFactorX);
        int windowHeight = (int) Math.round(450*scaleFactorY);
        window2.setSize(windowWidth, windowHeight);
    }
    
    @Override
    public void componentHidden(ComponentEvent ce) {};
    @Override
    public void componentShown(ComponentEvent ce) {};
    @Override
    public void componentMoved(ComponentEvent ce) {};
    @Override
    public void componentResized(ComponentEvent ce) {
        currentDeckBuildingWidth = this.getWidth();
        currentDeckBuildingHeight = this.getHeight();
        rescaleEverything();
    };
    
    // calculates rescaling of horizontal coordinates (and widths)
    public static int rescaleX (int x) {
        int newValue = (int) Math.round(x*currentDeckBuildingWidth/initialDeckBuildingWidth);
        return newValue;
    }
    
    // calculates rescaling of vertical coordinates (and heights)
    public static int rescaleY (int y) {
        int newValue = (int) Math.round(y*currentDeckBuildingHeight/initialDeckBuildingHeight);
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
        rescaleLabel(displaySumOfCards, 175, 50, 100, 25);
        rescaleLabel(displayNumCard1, 367, 25, 33, 100);
        rescaleLabel(displayNumCard2, 467, 25, 33, 100);
        rescaleLabel(displayNumCard3, 567, 25, 33, 100);
        rescaleLabel(displayNumCard4, 667, 25, 33, 100);
        rescaleLabel(displayNumCard5, 767, 25, 33, 100);
        rescaleLabel(displayNumCard6, 867, 25, 33, 100);
        rescaleLabel(displayNumCard7, 367, 125, 33, 100);
        rescaleLabel(displayNumCard8, 467, 125, 33, 100);
        rescaleLabel(displayNumCard9, 567, 125, 33, 100);
        rescaleLabel(displayNumCard10, 667, 125, 33, 100);
        rescaleLabel(displayNumCard11, 767, 125, 33, 100);
        rescaleLabel(displayNumCard12, 867, 125, 33, 100);
        rescaleLabel(displayNumCard13, 367, 225, 33, 100);
        rescaleLabel(displayNumCard14, 467, 225, 33, 100);
        rescaleLabel(displayNumCard15, 567, 225, 33, 100);
        rescaleLabel(displayNumCard16, 667, 225, 33, 100);
        rescaleLabel(displayNumCard17, 767, 225, 33, 100);
        rescaleLabel(displayNumCard18, 867, 225, 33, 100);
        rescaleLabel(displayDeckDesription, 300, 325, 500, 150);
        // rescale all buttons
        rescaleButton(buttonLoad, 25, 25, 250, 25);
        rescaleButton(buttonSave, 25, 50, 150, 25);
        rescaleButton(buttonClearDeck, 25, 75, 150, 25);
        rescaleButton(buttonAcceptDeck, 25, 100, 250, 25);
        rescaleButton(buttonDeck1, 25, 150, 250, 25);
        rescaleButton(buttonDeck2, 25, 175, 250, 25);
        rescaleButton(buttonDeck3, 25, 200, 250, 25);
        rescaleButton(buttonDeck4, 25, 225, 250, 25);
        rescaleButton(buttonDeck5, 25, 250, 250, 25);
        rescaleButton(buttonDeck6, 25, 275, 250, 25);
        rescaleButton(buttonDeck7, 25, 300, 250, 25);
        rescaleButton(buttonDeck8, 25, 325, 250, 25);
        rescaleButton(buttonDeck9, 25, 350, 250, 25);
        rescaleButton(buttonDeck10, 25, 375, 250, 25);
        rescaleButton(buttonCustomDeckCPU, 25, 425, 250, 25);
        rescaleButton(buttonRandomDeckCPU, 25, 450, 250, 25);
        rescaleButton(buttonCard1, 300, 25, 67, 100);
        rescaleButton(buttonCard2, 400, 25, 67, 100);
        rescaleButton(buttonCard3, 500, 25, 67, 100);
        rescaleButton(buttonCard4, 600, 25, 67, 100);
        rescaleButton(buttonCard5, 700, 25, 67, 100);
        rescaleButton(buttonCard6, 800, 25, 67, 100);
        rescaleButton(buttonCard7, 300, 125, 67, 100);
        rescaleButton(buttonCard8, 400, 125, 67, 100);
        rescaleButton(buttonCard9, 500, 125, 67, 100);
        rescaleButton(buttonCard10, 600, 125, 67, 100);
        rescaleButton(buttonCard11, 700, 125, 67, 100);
        rescaleButton(buttonCard12, 800, 125, 67, 100);
        rescaleButton(buttonCard13, 300, 225, 67, 100);
        rescaleButton(buttonCard14, 400, 225, 67, 100);
        rescaleButton(buttonCard15, 500, 225, 67, 100);
        rescaleButton(buttonCard16, 600, 225, 67, 100);
        rescaleButton(buttonCard17, 700, 225, 67, 100);
        rescaleButton(buttonCard18, 800, 225, 67, 100);
    }
    
    // this method updates the displayed numbers of cards
    public static void updateNumbers(){
        displayNumCard1.setText(" " + numCard1);
        displayNumCard2.setText(" " + numCard2);
        displayNumCard3.setText(" " + numCard3);
        displayNumCard4.setText(" " + numCard4);
        displayNumCard5.setText(" " + numCard5);
        displayNumCard6.setText(" " + numCard6);
        displayNumCard7.setText(" " + numCard7);
        displayNumCard8.setText(" " + numCard8);
        displayNumCard9.setText(" " + numCard9);
        displayNumCard10.setText(" " + numCard10);
        displayNumCard11.setText(" " + numCard11);
        displayNumCard12.setText(" " + numCard12);
        displayNumCard13.setText(" " + numCard13);
        displayNumCard14.setText(" " + numCard14);
        displayNumCard15.setText(" " + numCard15);
        displayNumCard16.setText(" " + numCard16);
        displayNumCard17.setText(" " + numCard17);
        displayNumCard18.setText(" " + numCard18);
        sum = numCard1 + numCard2 + numCard3 + numCard4 + numCard5 + numCard6 + numCard7 + numCard8 + numCard9 + numCard10 + numCard11 + numCard12 + numCard13 + numCard14 + numCard15 + numCard16 + numCard17 + numCard18;
        displaySumOfCards.setText(" sum: " + sum + " /39");
    }
    
    // this method fills an empty deck one card at a time
    public static Deck buildDeckFromNumbers (boolean isDeckOfPlayer){
        Deck NewDeck = new Deck(isDeckOfPlayer);
        NewDeck.numberOfCards=39; // one could also steadily in crease the number, but this is less source code
        // start with an iterator, look what cards are there and proceed
        int n=1;
        if (numCard1>0) {
            if (numCard1>=1) {NewDeck.setNthCardOfDeckToCard(Card.GodBarrier, n); n++;}
            if (numCard1>=2) {NewDeck.setNthCardOfDeckToCard(Card.GodBarrier, n); n++;}
            if (numCard1==3) {NewDeck.setNthCardOfDeckToCard(Card.GodBarrier, n); n++;}
        }
        if (numCard2>0) {
            if (numCard2>=1) {NewDeck.setNthCardOfDeckToCard(Card.EradicatorObstacle, n); n++;}
            if (numCard2>=2) {NewDeck.setNthCardOfDeckToCard(Card.EradicatorObstacle, n); n++;}
            if (numCard2==3) {NewDeck.setNthCardOfDeckToCard(Card.EradicatorObstacle, n); n++;}
        }
        if (numCard3>0) {
            if (numCard3>=1) {NewDeck.setNthCardOfDeckToCard(Card.GodKillingSpearLance, n); n++;}
            if (numCard3>=2) {NewDeck.setNthCardOfDeckToCard(Card.GodKillingSpearLance, n); n++;}
            if (numCard3==3) {NewDeck.setNthCardOfDeckToCard(Card.GodKillingSpearLance, n); n++;}
        }
        if (numCard4>0) {
            if (numCard4>=1) {NewDeck.setNthCardOfDeckToCard(Card.MonsterStealerSteepLearningCurve, n); n++;}
            if (numCard4>=2) {NewDeck.setNthCardOfDeckToCard(Card.MonsterStealerSteepLearningCurve, n); n++;}
            if (numCard4==3) {NewDeck.setNthCardOfDeckToCard(Card.MonsterStealerSteepLearningCurve, n); n++;}
        }
        if (numCard5>0) {
            if (numCard5>=1) {NewDeck.setNthCardOfDeckToCard(Card.NeutraliserSkillStealer, n); n++;}
            if (numCard5>=2) {NewDeck.setNthCardOfDeckToCard(Card.NeutraliserSkillStealer, n); n++;}
            if (numCard5==3) {NewDeck.setNthCardOfDeckToCard(Card.NeutraliserSkillStealer, n); n++;}
        }
        if (numCard6>0) {
            if (numCard6>=1) {NewDeck.setNthCardOfDeckToCard(Card.CopyCatCardGrabber, n); n++;}
            if (numCard6>=2) {NewDeck.setNthCardOfDeckToCard(Card.CopyCatCardGrabber, n); n++;}
            if (numCard6==3) {NewDeck.setNthCardOfDeckToCard(Card.CopyCatCardGrabber, n); n++;}
        }
        if (numCard7>0) {
            if (numCard7>=1) {NewDeck.setNthCardOfDeckToCard(Card.AttackStopperBuggedUpgrade, n); n++;}
            if (numCard7>=2) {NewDeck.setNthCardOfDeckToCard(Card.AttackStopperBuggedUpgrade, n); n++;}
            if (numCard7==3) {NewDeck.setNthCardOfDeckToCard(Card.AttackStopperBuggedUpgrade, n); n++;}
        }
        if (numCard8>0) {
            if (numCard8>=1) {NewDeck.setNthCardOfDeckToCard(Card.BigAttackStopperSword, n); n++;}
            if (numCard8>=2) {NewDeck.setNthCardOfDeckToCard(Card.BigAttackStopperSword, n); n++;}
            if (numCard8==3) {NewDeck.setNthCardOfDeckToCard(Card.BigAttackStopperSword, n); n++;}
        }
        if (numCard9>0) {
            if (numCard9>=1) {NewDeck.setNthCardOfDeckToCard(Card.DiamondSwordShield, n); n++;}
            if (numCard9>=2) {NewDeck.setNthCardOfDeckToCard(Card.DiamondSwordShield, n); n++;}
            if (numCard9==3) {NewDeck.setNthCardOfDeckToCard(Card.DiamondSwordShield, n); n++;}
        }
        if (numCard10>0) {
            if (numCard10>=1) {NewDeck.setNthCardOfDeckToCard(Card.ModeChangerExhaustedExecutioner, n); n++;}
            if (numCard10>=2) {NewDeck.setNthCardOfDeckToCard(Card.ModeChangerExhaustedExecutioner, n); n++;}
            if (numCard10==3) {NewDeck.setNthCardOfDeckToCard(Card.ModeChangerExhaustedExecutioner, n); n++;}
        }
        if (numCard11>0) {
            if (numCard11>=1) {NewDeck.setNthCardOfDeckToCard(Card.SlickRusherRecklessRusher, n); n++;}
            if (numCard11>=2) {NewDeck.setNthCardOfDeckToCard(Card.SlickRusherRecklessRusher, n); n++;}
            if (numCard11==3) {NewDeck.setNthCardOfDeckToCard(Card.SlickRusherRecklessRusher, n); n++;}
        }
        if (numCard12>0) {
            if (numCard12>=1) {NewDeck.setNthCardOfDeckToCard(Card.IncorruptibleHolyLance, n); n++;}
            if (numCard12>=2) {NewDeck.setNthCardOfDeckToCard(Card.IncorruptibleHolyLance, n); n++;}
            if (numCard12==3) {NewDeck.setNthCardOfDeckToCard(Card.IncorruptibleHolyLance, n); n++;}
        }
        if (numCard13>0) {
            if (numCard13>=1) {NewDeck.setNthCardOfDeckToCard(Card.NecromancerBackBouncer, n); n++;}
            if (numCard13>=2) {NewDeck.setNthCardOfDeckToCard(Card.NecromancerBackBouncer, n); n++;}
            if (numCard13==3) {NewDeck.setNthCardOfDeckToCard(Card.NecromancerBackBouncer, n); n++;}
        }
        if (numCard14>0) {
            if (numCard14>=1) {NewDeck.setNthCardOfDeckToCard(Card.BigBackBouncerBanisher, n); n++;}
            if (numCard14>=2) {NewDeck.setNthCardOfDeckToCard(Card.BigBackBouncerBanisher, n); n++;}
            if (numCard14==3) {NewDeck.setNthCardOfDeckToCard(Card.BigBackBouncerBanisher, n); n++;}
        }
        if (numCard15>0) {
            if (numCard15>=1) {NewDeck.setNthCardOfDeckToCard(Card.BigBanisherBurner, n); n++;}
            if (numCard15>=2) {NewDeck.setNthCardOfDeckToCard(Card.BigBanisherBurner, n); n++;}
            if (numCard15==3) {NewDeck.setNthCardOfDeckToCard(Card.BigBanisherBurner, n); n++;}
        }
        if (numCard16>0) {
            if (numCard16>=1) {NewDeck.setNthCardOfDeckToCard(Card.BigBurnerSuicideCommando, n); n++;}
            if (numCard16>=2) {NewDeck.setNthCardOfDeckToCard(Card.BigBurnerSuicideCommando, n); n++;}
            if (numCard16==3) {NewDeck.setNthCardOfDeckToCard(Card.BigBurnerSuicideCommando, n); n++;}
        }
        if (numCard17>0) {
            if (numCard17>=1) {NewDeck.setNthCardOfDeckToCard(Card.FlakshipNapalm, n); n++;}
            if (numCard17>=2) {NewDeck.setNthCardOfDeckToCard(Card.FlakshipNapalm, n); n++;}
            if (numCard17==3) {NewDeck.setNthCardOfDeckToCard(Card.FlakshipNapalm, n); n++;}
        }
        if (numCard18>0) {
            if (numCard18>=1) {NewDeck.setNthCardOfDeckToCard(Card.DemonGodDemon, n); n++;}
            if (numCard18>=2) {NewDeck.setNthCardOfDeckToCard(Card.DemonGodDemon, n); n++;}
            if (numCard18==3) {NewDeck.setNthCardOfDeckToCard(Card.DemonGodDemon, n); n++;}
        }
        NewDeck.isBelongingToPlayer=isDeckOfPlayer;
        return NewDeck;
    }
    
    // this method puts the numbers to zero again
    public static void clearNumbers(){
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
    
    // sets the number of the nth card to a given number
    public static void setNthCardNumber (int n, int numberOfCards){
        switch (n) {
            case 1: numCard1=numberOfCards; break;
            case 2: numCard2=numberOfCards; break;
            case 3: numCard3=numberOfCards; break;
            case 4: numCard4=numberOfCards; break;
            case 5: numCard5=numberOfCards; break;
            case 6: numCard6=numberOfCards; break;
            case 7: numCard7=numberOfCards; break;
            case 8: numCard8=numberOfCards; break;
            case 9: numCard9=numberOfCards; break;
            case 10: numCard10=numberOfCards; break;
            case 11: numCard11=numberOfCards; break;
            case 12: numCard12=numberOfCards; break;
            case 13: numCard13=numberOfCards; break;
            case 14: numCard14=numberOfCards; break;
            case 15: numCard15=numberOfCards; break;
            case 16: numCard16=numberOfCards; break;
            case 17: numCard17=numberOfCards; break;
            case 18: numCard18=numberOfCards; break;
            default: YuGiOhJi.debugDialog("Error: out of bounds in DeckBuilding.setNthCardNumber(...); attempted N: " + n); break;
        }
    }
    
    // gets the number of the nth card
    public static int getNthCardNumber (int n){
        switch (n) {
            case 1: return numCard1;
            case 2: return numCard2;
            case 3: return numCard3;
            case 4: return numCard4;
            case 5: return numCard5;
            case 6: return numCard6;
            case 7: return numCard7;
            case 8: return numCard8;
            case 9: return numCard9;
            case 10: return numCard10;
            case 11: return numCard11;
            case 12: return numCard12;
            case 13: return numCard13;
            case 14: return numCard14;
            case 15: return numCard15;
            case 16: return numCard16;
            case 17: return numCard17;
            case 18: return numCard18;
            default: YuGiOhJi.debugDialog("Error: out of bounds in DeckBuilding.getNthCardNumber(...); attempted N: " + n); return 0;
        }
    }
    
    // increases the numbers by one modulo 4 (i.e. resluts in numbers 1, 2, 3, 0)
    public static void increaseNthNumberMod4 (int n){
        int newNumberOfCards = getNthCardNumber(n);
        newNumberOfCards++;
        newNumberOfCards=newNumberOfCards%4;
        setNthCardNumber(n, newNumberOfCards);
        updateNumbers();
    }
    
    // in order not to repeat oneself, out-source how one defines the standard decks here
    // one just has to enter the 18 different card numbers and the explanation text
    public static void loadStandardDeck (String text, int no1, int no2, int no3, int no4, int no5, int no6, int no7, int no8, int no9, int no10, int no11, int no12, int no13, int no14, int no15, int no16, int no17, int no18)
    {
        displayDeckDesription.setText(text);
        numCard1=no1; numCard2=no2; numCard3=no3; numCard4=no4; numCard5=no5; numCard6=no6;
        numCard7=no7; numCard8=no8; numCard9=no9; numCard10=no10; numCard11=no11; numCard12=no12;
        numCard13=no13; numCard14=no14; numCard15=no15; numCard16=no16; numCard17=no17; numCard18=no18;
        updateNumbers();
    }
    
    // in order not to repeat oneself, out-source here setting of decks of both players
    public static void finallyAcceptDeck (boolean isDeckOfPlayer) {
        if (sum==39) {
            Deck NewDeck=buildDeckFromNumbers(isDeckOfPlayer);
            if (isDeckOfPlayer) {
                YuGiOhJi.UnshuffledDeckPlayer.setDeck(NewDeck, isDeckOfPlayer);
                clearNumbers();
                YuGiOhJi.window2.dispose(); // close deck building window
            }
            else {
                Game.isUsingCustomDeckForCPU=true;
                YuGiOhJi.UnshuffledDeckCPU.setDeck(NewDeck, isDeckOfPlayer);
            }
        }
        else {
            YuGiOhJi.errorDialog("The sum of all cards in the deck must be exactly 39.", "Error.");
        }
    }
    
    // from here on:
    // --- reactions of the buttons ---
    
    @Override
    public void actionPerformed (ActionEvent ae){
        
        // save deck to file
        if (ae.getSource() == DeckBuilding.buttonSave) {
            if (sum==39) {
                customDeck = new Integer[]{numCard1, numCard2, numCard3, numCard4, numCard5, numCard6, numCard7, numCard8, numCard9, numCard10, numCard11, numCard12, numCard13, numCard14, numCard15, numCard16, numCard17, numCard18};
            
                FileFilter filter = new FileNameExtensionFilter("decks", "txt"); 
                JFileChooser chooser = new JFileChooser("c:/");
                chooser.addChoosableFileFilter(filter);
                int chooserReturn = chooser.showSaveDialog(null);
                if(chooserReturn == JFileChooser.APPROVE_OPTION){
                    try(FileWriter fw = new FileWriter(chooser.getSelectedFile()+".txt")) {
                        fw.write(Arrays.toString(customDeck));
                        fw.close();
                    } catch (Exception ex) {
                        // if data can not be saved, do nothing
                    }
                }
            
            }
            else {
                YuGiOhJi.errorDialog("The sum of all cards in the deck must be exactly 39.", "Error.");
            }
        }
        
        // load deck from file
        if (ae.getSource() == DeckBuilding.buttonLoad) {
            
            loaded=false;
            try {
                FileFilter filter = new FileNameExtensionFilter("decks", "txt"); 
                JFileChooser chooser = new JFileChooser("c:/");
                chooser.addChoosableFileFilter(filter);
                int chooserReturn = chooser.showOpenDialog(null);
                if(chooserReturn == JFileChooser.APPROVE_OPTION){
                    File selectedDeck = chooser.getSelectedFile();
                    
                    Scanner scanner = new Scanner(selectedDeck);
                    scanner.useDelimiter("\\Z"); // ^ beginninng of line, $ end of line, \b word boundary, \B non-word boundary, \A beginning of input, \Z end of input
                    String deckAsString = scanner.next();
                    
                    if (deckAsString != null && deckAsString.length() > 0){
                        deckAsString = deckAsString.substring(1, deckAsString.length() - 1); // cut-off brackets at both ends
                    }
                    String[] integerStrings = deckAsString.split(","); // trimming off spaces (if present)
                    for (int i = 0; i < integerStrings.length; i++) {
                        integerStrings[i] = integerStrings[i].trim();
                    }
                    Integer[] deckAsIntArray = new Integer[integerStrings.length];
                    for (int i = 0; i < deckAsIntArray.length; i++){
                        deckAsIntArray[i] = Integer.parseInt(integerStrings[i]); //Parses the integer for each string.
                    }
                    customDeck=deckAsIntArray;
                    int maxInt = Collections.max(Arrays.asList(customDeck));
                    if (customDeck.length==18 && maxInt < 4) {
                        loaded=true; // only accept custom decks of right length and the max. value is 3 or less (in order to avoid cheating)
                        // extract numbers out of the file
                        numCard1=customDeck[0]; // mind the usual off-set of arrays in most programming languages!
                        numCard2=customDeck[1]; numCard3=customDeck[2]; numCard4=customDeck[3]; numCard5=customDeck[4]; numCard6=customDeck[5];
                        numCard7=customDeck[6]; numCard8=customDeck[7]; numCard9=customDeck[8]; numCard10=customDeck[9]; numCard11=customDeck[10]; numCard12=customDeck[11];
                        numCard13=customDeck[12]; numCard14=customDeck[13]; numCard15=customDeck[14]; numCard16=customDeck[15]; numCard17=customDeck[16]; numCard18=customDeck[17];
                        updateNumbers();
                        
                        // show maximum integer (just to be sure)
                        //YuGiOhJi.debugDialog(550, 300, 600, 100, "max Int.: " + maxInt);
                    }
                }
                
            } catch (Exception ex) {
                Logger.getLogger(DeckBuilding.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (loaded==true) {
                YuGiOhJi.informationDialog("The deck has been successfully loaded.", "deck loaded");
            }
            else {
                YuGiOhJi.errorDialog("No valid YuGiOhJi deck!", "Error.");
            }
            
        }
        
        // finally accept deck and close window
        if (ae.getSource() == DeckBuilding.buttonAcceptDeck) {
             finallyAcceptDeck(true);
        }
        
        // define deck of opponent
        if (ae.getSource() == DeckBuilding.buttonCustomDeckCPU) {
             finallyAcceptDeck(false); // and close window
        }
        
        if (ae.getSource() == DeckBuilding.buttonRandomDeckCPU) {
             Game.isUsingCustomDeckForCPU=false;
        }
        
        // clear current deck building process and start over
        if (ae.getSource() == DeckBuilding.buttonClearDeck) {
            loadStandardDeck(deckBuildingExplanation, 0, 0, 0, 0, 0, 0,  0, 0, 0, 0, 0, 0,  0, 0, 0, 0, 0, 0);
        }
        if (ae.getSource() == DeckBuilding.buttonDeck1) {
            String explanationText = "<html>Standard deck 1: The starter deck <br>This is a very balanced and good deck with most effects. " 
                    + "It contains 3 cards of each kind, but without both Rushers, " + Mon.DiamondSword.monsterName + ", " + Mon.Incorruptible.monsterName + ", " + Card.BigBanisherBurner.cardName + ", and " + Mon.Demon.monsterName + ". "
                    + "It is recommended for beginners and is also set as the default deck when starting this game.</html>";
            loadStandardDeck(explanationText, 3, 3, 3, 3, 3, 3,  3, 3, 0, 3, 0, 0,  3, 3, 0, 3, 3, 0);
        }
        if (ae.getSource() == DeckBuilding.buttonDeck2) {
            String explanationText = "<html>Standard deck 2: No self-protection deck <br>This deck contains 3 cards of each kind, "
                    + "but without the monsters that have self-protecting effects, that means, without both Rushers, " + Mon.DiamondSword.monsterName + ", " + Mon.Incorruptible.monsterName + ", " + Card.GodBarrier.cardName + ", and " + Mon.Obstacle.monsterName + ".</html>";
            loadStandardDeck(explanationText, 0, 0, 3, 3, 3, 3,  3, 3, 0, 3, 0, 0,  3, 3, 3, 3, 3, 3);
        }
        if (ae.getSource() == DeckBuilding.buttonDeck3) {
            String explanationText = "<html>Standard deck 3: No equip deck <br>This deck contains 3 cards of each kind, "
                    + "but without the equip monsters, that means, without " + Mon.Lance.monsterName + ", " + Mon.Sword.monsterName + ", " + Mon.Shield.monsterName + ", " + Mon.BuggedUpgrade.monsterName + ", and " + Mon.Demon.monsterName + ".</html>";
            loadStandardDeck(explanationText, 3, 3, 0, 3, 3, 3,  0, 0, 0, 3, 3, 3,  3, 3, 3, 3, 3, 0);
        }
        if (ae.getSource() == DeckBuilding.buttonDeck4) {
            String explanationText = "<html>Standard deck 4: No trap monsters deck <br>This deck contains 3 cards of each kind, "
                    + "but without the monsters that have surprising effects when being destroyed by the opponent, "
                    + "that means, without " + Mon.SuicideCommando.monsterName + ", " + Mon.BackBouncer.monsterName + ", " + Mon.Banisher.monsterName + ", and " + Mon.Napalm.monsterName + ". Also both Demons are missing.</html>";
            loadStandardDeck(explanationText, 3, 3, 3, 3, 3, 3,  3, 3, 3, 3, 3, 3,  0, 0, 3, 0, 0, 0);
        }
        if (ae.getSource() == DeckBuilding.buttonDeck5) {
            String explanationText = "<html>Standard deck 5: No removal deck <br>This deck contains 3 cards of each kind, "
                    + "but without most monsters that have effects that can remove cards of the opponent, "
                    + "that means, without " + Mon.Eradicator.monsterName + ", " + Mon.MonsterStealer.monsterName + ", " + Mon.BackBouncer.monsterName + ", " + Mon.Banisher.monsterName + ", and " + Mon.BigBanisher.monsterName + ". "
                    + "This is a very challanging deck and not recommended for beginners.</html>";
            loadStandardDeck(explanationText, 3, 0, 3, 0, 3, 3,  3, 3, 3, 3, 3, 3,  0, 0, 0, 3, 3, 3);
        }
        if (ae.getSource() == DeckBuilding.buttonDeck6) {
            String explanationText = "<html>Standard deck 6: No Banish-Bounce-Burn deck <br>This deck contains 3 cards of each kind, "
                    + "but without most banish, bounce or burn effects, that means, "
                    + "without " + Mon.BigBurner.monsterName + ", " + Card.BigBanisherBurner.cardName + ", " + Card.BigBackBouncerBanisher.cardName + ", and " + Card.FlakshipNapalm.cardName + ". "
                    + "One is still supposed to be able to revive monsters using the " + Mon.Necromancer.monsterName + " though. Also both Demons are missing.</html>";
            loadStandardDeck(explanationText, 3, 3, 3, 3, 3, 3,  3, 3, 3, 3, 3, 3,  3, 0, 0, 0, 0, 0);
        }
        if (ae.getSource() == DeckBuilding.buttonDeck7) {
            String explanationText = "<html>Standard deck 7: No hand traps deck <br>This deck contains 3 cards of each kind, "
                    + "but without the cards being able to do something during the turn of the opponent, that means, "
                    + "without " + Mon.Neutraliser.monsterName + ", " + Mon.AttackStopper.monsterName + ", " + Mon.BigAttackStopper.monsterName + ", and " + Mon.Banisher.monsterName + ". Also both Demons are missing. This is a challanging deck.</html>";
            loadStandardDeck(explanationText, 3, 3, 3, 3, 0, 3,  0, 0, 3, 3, 3, 3,  3, 0, 3, 3, 3, 0);
        }
        if (ae.getSource() == DeckBuilding.buttonDeck8) {
            String explanationText = "<html>Standard deck 8: The complementary deck <br>This deck contains 3 cards of each kind, "
                    + "but without the cards that are contained in most other standard decks, that means, "
                    + "without " + Card.GodBarrier.cardName + ", " + Card.GodKillingSpearLance.cardName + ", " + Card.MonsterStealerSteepLearningCurve.cardName + ", " + Card.CopyCatCardGrabber.cardName + ", and " + Card.ModeChangerExhaustedExecutioner.cardName + ". "
                    + "This results in many other strong effects and thus demands a more indirect strategy.</html>";
            loadStandardDeck(explanationText, 0, 3, 0, 0, 3, 0,  3, 3, 3, 0, 3, 3,  3, 3, 3, 3, 3, 3);
        }
        if (ae.getSource() == DeckBuilding.buttonDeck9) {
            String explanationText = "<html>Standard deck 9: The unpredictable deck <br>This deck contains 2 cards of each kind "
                    + "and one additional copy of " + Card.EradicatorObstacle.cardName + ", " + Card.NeutraliserSkillStealer.cardName + ", and " + Card.CopyCatCardGrabber.cardName + ". "
                    + "This deck contains all cards of the game and is thus very unpredictable - for your opponent as well as for you.</html>";
            loadStandardDeck(explanationText, 2, 3, 2, 2, 3, 3,  2, 2, 2, 2, 2, 2,  2, 2, 2, 2, 2, 2);
        }
        if (ae.getSource() == DeckBuilding.buttonDeck10) {
            String explanationText = "<html>Standard deck 10: The trickster deck <br>This deck contains 3 cards of each kind, "
                    + "but without the monsters that are primarily used because of their high attack power, that means, without " + Mon.God.monsterName + ", " + Mon.GodKillingSpear.monsterName + ", " + Card.IncorruptibleHolyLance.cardName + ", " + Mon.Flakship.monsterName + ", and " + Mon.DemonGod.monsterName + ". "
                    + "This results in many other strong effects and thus demands a more indirect strategy.</html>";
            loadStandardDeck(explanationText, 0, 3, 0, 3, 3, 3,  3, 3, 3, 3, 3, 0,  3, 3, 3, 3, 0, 0);
        }
        if (ae.getSource() == DeckBuilding.buttonCard1) {increaseNthNumberMod4(1);}
        if (ae.getSource() == DeckBuilding.buttonCard2) {increaseNthNumberMod4(2);}
        if (ae.getSource() == DeckBuilding.buttonCard3) {increaseNthNumberMod4(3);}
        if (ae.getSource() == DeckBuilding.buttonCard4) {increaseNthNumberMod4(4);}
        if (ae.getSource() == DeckBuilding.buttonCard5) {increaseNthNumberMod4(5);}
        if (ae.getSource() == DeckBuilding.buttonCard6) {increaseNthNumberMod4(6);}
        if (ae.getSource() == DeckBuilding.buttonCard7) {increaseNthNumberMod4(7);}
        if (ae.getSource() == DeckBuilding.buttonCard8) {increaseNthNumberMod4(8);}
        if (ae.getSource() == DeckBuilding.buttonCard9) {increaseNthNumberMod4(9);}
        if (ae.getSource() == DeckBuilding.buttonCard10) {increaseNthNumberMod4(10);}
        if (ae.getSource() == DeckBuilding.buttonCard11) {increaseNthNumberMod4(11);}
        if (ae.getSource() == DeckBuilding.buttonCard12) {increaseNthNumberMod4(12);}
        if (ae.getSource() == DeckBuilding.buttonCard13) {increaseNthNumberMod4(13);}
        if (ae.getSource() == DeckBuilding.buttonCard14) {increaseNthNumberMod4(14);}
        if (ae.getSource() == DeckBuilding.buttonCard15) {increaseNthNumberMod4(15);}
        if (ae.getSource() == DeckBuilding.buttonCard16) {increaseNthNumberMod4(16);}
        if (ae.getSource() == DeckBuilding.buttonCard17) {increaseNthNumberMod4(17);}
        if (ae.getSource() == DeckBuilding.buttonCard18) {increaseNthNumberMod4(18);}
        
    }
    
    // what happens when one leaves any zone (just show the big version of the back side of each card)
    @Override
    public void mouseExited(MouseEvent me) {
        YuGiOhJi.preview.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
    }
    
    // what happens when one enters a certain zone with the mouse (show big preview screen depending on card)
    @Override
    public void mouseEntered(MouseEvent me) {
        // display larger (readable) version of cards (very useful for deck building to know what a card actually does)
        if (me.getSource() == DeckBuilding.buttonCard1) {YuGiOhJi.preview.setIcon(new RescaledIcon(this.getClass().getResource(Card.GodBarrier.bigCardPath)));}
        if (me.getSource() == DeckBuilding.buttonCard2) {YuGiOhJi.preview.setIcon(new RescaledIcon(this.getClass().getResource(Card.EradicatorObstacle.bigCardPath)));}
        if (me.getSource() == DeckBuilding.buttonCard3) {YuGiOhJi.preview.setIcon(new RescaledIcon(this.getClass().getResource(Card.GodKillingSpearLance.bigCardPath)));}
        if (me.getSource() == DeckBuilding.buttonCard4) {YuGiOhJi.preview.setIcon(new RescaledIcon(this.getClass().getResource(Card.MonsterStealerSteepLearningCurve.bigCardPath)));}
        if (me.getSource() == DeckBuilding.buttonCard5) {YuGiOhJi.preview.setIcon(new RescaledIcon(this.getClass().getResource(Card.NeutraliserSkillStealer.bigCardPath)));}
        if (me.getSource() == DeckBuilding.buttonCard6) {YuGiOhJi.preview.setIcon(new RescaledIcon(this.getClass().getResource(Card.CopyCatCardGrabber.bigCardPath)));}
        if (me.getSource() == DeckBuilding.buttonCard7) {YuGiOhJi.preview.setIcon(new RescaledIcon(this.getClass().getResource(Card.AttackStopperBuggedUpgrade.bigCardPath)));}
        if (me.getSource() == DeckBuilding.buttonCard8) {YuGiOhJi.preview.setIcon(new RescaledIcon(this.getClass().getResource(Card.BigAttackStopperSword.bigCardPath)));}
        if (me.getSource() == DeckBuilding.buttonCard9) {YuGiOhJi.preview.setIcon(new RescaledIcon(this.getClass().getResource(Card.DiamondSwordShield.bigCardPath)));}
        if (me.getSource() == DeckBuilding.buttonCard10) {YuGiOhJi.preview.setIcon(new RescaledIcon(this.getClass().getResource(Card.ModeChangerExhaustedExecutioner.bigCardPath)));}
        if (me.getSource() == DeckBuilding.buttonCard11) {YuGiOhJi.preview.setIcon(new RescaledIcon(this.getClass().getResource(Card.SlickRusherRecklessRusher.bigCardPath)));}
        if (me.getSource() == DeckBuilding.buttonCard12) {YuGiOhJi.preview.setIcon(new RescaledIcon(this.getClass().getResource(Card.IncorruptibleHolyLance.bigCardPath)));}
        if (me.getSource() == DeckBuilding.buttonCard13) {YuGiOhJi.preview.setIcon(new RescaledIcon(this.getClass().getResource(Card.NecromancerBackBouncer.bigCardPath)));}
        if (me.getSource() == DeckBuilding.buttonCard14) {YuGiOhJi.preview.setIcon(new RescaledIcon(this.getClass().getResource(Card.BigBackBouncerBanisher.bigCardPath)));}
        if (me.getSource() == DeckBuilding.buttonCard15) {YuGiOhJi.preview.setIcon(new RescaledIcon(this.getClass().getResource(Card.BigBanisherBurner.bigCardPath)));}
        if (me.getSource() == DeckBuilding.buttonCard16) {YuGiOhJi.preview.setIcon(new RescaledIcon(this.getClass().getResource(Card.BigBurnerSuicideCommando.bigCardPath)));}
        if (me.getSource() == DeckBuilding.buttonCard17) {YuGiOhJi.preview.setIcon(new RescaledIcon(this.getClass().getResource(Card.FlakshipNapalm.bigCardPath)));}
        if (me.getSource() == DeckBuilding.buttonCard18) {YuGiOhJi.preview.setIcon(new RescaledIcon(this.getClass().getResource(Card.DemonGodDemon.bigCardPath)));}
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

