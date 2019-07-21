package yugiohji;

/**
 * This is the main class of the video card game YuGiOhJi - The Prince of the Games.
 * This game is an hommage to the game Yu-Gi-Oh! (the king of the games).
 * The rules are similar. That means that they are so involved that they are contained in an extra file.
 * 
 * This class file contains everything concerned with the look/graphics of the game,
 * like creating the main window and methods for changing the appearance of the cards.
 * 
 *  -- About the other CLASS FILES: --
 * The basic game logic, like setting up the game in order to start it,
 * can be found in the class file GameState.
 * This game evolves around cards with two different entities on them called monsters:
 * The classes concerning the YuGiOhJi-cards and YuGiOhJi-monsters
 * can be found in the classes YCard and YMonster.
 * The class YMonster also contains all the optional effects
 * of the monsters the player can use. (There are a lot of them.)
 * For convenience, all monsters and all cards are packed into a single objects
 * in the classes YMonsters and YCards.
 * 
 * There are derived enities.
 * For example the class SummonedMonster adds more properties to the monsters (from class YMonster).
 * The classes Hand and Deck take 10 respective 39 YCards into one object.
 * The class EStack takes up to 10 equip cards (YCards with additional properties, see class EquipCard)
 * that can lie on top of each other in a stack and are equipping a summoned monster.
 * 
 * There are several classes that open WINDOWS or DIALOGS for choosing cards and using their effects.
 * The class DeckBuilding contains all the features for allowing the player to customise the used card deck.
 * CardOptions shows the options to play a card on the hand or in the card graveyard.
 * With the SearchWindow you can search a card from the deck or the graveyard.
 * The YChooseCardWindow opens in window for choosing cards that one has to sacrifice or reveal
 * in order to use the effect of a card. (This is the biggest, i.e. most inefficiently written file)
 * The YChooseEquipCardWindow opens a window for looking (and playing)
 * a card in a stack of cards equipping a summoned monster.
 * The YCardInfoWindow contains methods for displaying windows
 * with detailed information about each single card in the game.
 * 
 * The file BattlePhase contains all the rules being applied,
 * when monsters fight against each other.
 * Many trading card games allow players to do someting during turns of other players.
 * The player makes use of that in the files PlayerInterrupts.
 * (The analog for the computer opponent can be found in AIinterrupts.)
 * 
 * All the computer "intelligence" is contained in all classes beginning with "AI".
 * If the computer is asked to make a decision, a method in the class AIdelegate is being called.
 * This one redirects the decision making to one of the classes for different computer behavior:
 * AIdefensive, AIbalanced, and AIaggressive.
 * (One can think of these different behaviors a bit as different difficulty levels.)
 * The only exception is, when the computer is asked to interact with the player during the player's turn.
 * Then the file AIinterrupts is being used.
 * The class AIEffects contains all the card effects that are rewritten for a better usage by the computer.
 * This is needed, since the effects the player can use ask the player all kinds of things,
 * whereas the computer simply has to decide everything before using an effect and then simply does stuff.
 * The class AIsacrifice contains methods for the computer to pay cards as costs for effects.
 * In the file AIbattle the computer analysis and performs the battle phase.
 * Recurring combinations of actions useful for several AI levels
 * are stored in AIstrategies. Several fine tuning parameters
 * for probabilities of certain actions defining the character traits
 * of the different AI levels are stored in AIparameters.
 * 
 * version 1.0
 * 18th Jul. 2019
 * seems to be stable, thus 1st upload
 * 
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.*;
import javax.swing.*;

// began programming this game about 19th January 2019
// finished, except maybe for a bit of debugging on 16th July 2019
// i.e. half a year development

public class YuGiOhJi extends JFrame implements ActionListener, MouseListener, ComponentListener
{
    // declare main window elements
    JPanel panel; // declaration whole screen
    static JLabel field; // declaration background field
    static JLabel preview; // declaration big preview screen
    public static YuGiOhJi windowl; // main window
    public static DeckBuilding window2;
    public static CardOptions window3; // when playing a card from hand/GY
    public static YChooseCardWindow window4; // for paying costs
    public static SearchWindow window5; // for looking at cards in deck/GY/DD
    public static YChooseEquipCardWindow window6; // for looking at cards in equip stacks
    public static YCardInfoWindow window7; // additional detailed information about a card
    
    public static int initialFrameWidth=1200;
    public static int initialFrameHeight=740;
    public static int currentFrameWidth=1200;
    public static int currentFrameHeight=740;
    
    // buttons for the "main menu"
    public static JButton buttonStart;
    public static JButton buttonDeckBuilding;
    public static JButton buttonCPUsetup;
    public static JButton buttonGiveUp;
    public static JButton buttonMods;
    public static JButton buttonGameSetup;
    public static JButton buttonInfo;
    
    // declarations of buttons for decks, graveyards, banishing zones (different dimensions)
    public static JButton buttonDeckCPU;
    public static JButton buttonGYCPU;
    public static JButton buttonDDCPU;
    public static JButton buttonDDPlayer;
    public static JButton buttonGYPlayer;
    public static JButton buttonDeckPlayer;
    
    // declarations of labels for the number of cards in each zone
    public static JLabel displayNumberCardsDeckCPU;
    public static JLabel displayNumberCardsGYCPU;
    public static JLabel displayNumberCardsDDCPU;
    public static JLabel displayNumberCardsDeckPlayer;
    public static JLabel displayNumberCardsGYPlayer;
    public static JLabel displayNumberCardsDDPlayer;
    
    // declarations of buttions for cards on the field
    public static JButton buttonEquip1CPU;
    public static JButton buttonEquip2CPU;
    public static JButton buttonEquip3CPU;
    public static JButton buttonEquip4CPU;
    public static JButton buttonEquip5CPU;
    
    public static JButton buttonMonster1CPU;
    public static JButton buttonMonster2CPU;
    public static JButton buttonMonster3CPU;
    public static JButton buttonMonster4CPU;
    public static JButton buttonMonster5CPU;
    
    public static JButton buttonMonster1Player;
    public static JButton buttonMonster2Player;
    public static JButton buttonMonster3Player;
    public static JButton buttonMonster4Player;
    public static JButton buttonMonster5Player;
    
    public static JButton buttonEquip1Player;
    public static JButton buttonEquip2Player;
    public static JButton buttonEquip3Player;
    public static JButton buttonEquip4Player;
    public static JButton buttonEquip5Player;
    
    // declarations of buttons for the cards on the player's hand (for simplicity a maximum of 10 cards on each players hand)
    public static JButton buttonCard1Player;
    public static JButton buttonCard2Player;
    public static JButton buttonCard3Player;
    public static JButton buttonCard4Player;
    public static JButton buttonCard5Player;
    public static JButton buttonCard6Player;
    public static JButton buttonCard7Player;
    public static JButton buttonCard8Player;
    public static JButton buttonCard9Player;
    public static JButton buttonCard10Player;
    
    // declarations of buttons for the cards on the CPU's hand (these buttons don't do anything)
    public static JButton buttonCard1CPU;
    public static JButton buttonCard2CPU;
    public static JButton buttonCard3CPU;
    public static JButton buttonCard4CPU;
    public static JButton buttonCard5CPU;
    public static JButton buttonCard6CPU;
    public static JButton buttonCard7CPU;
    public static JButton buttonCard8CPU;
    public static JButton buttonCard9CPU;
    public static JButton buttonCard10CPU;
    
    // declaration next phase button
    public static JButton buttonNextPhase;
    public static JButton buttonNextTurn;
    public static JLabel displayLPCPU;
    public static JLabel displayLPPlayer;
    public static JLabel displayPiercingStatement;
    public static JLabel displaySkillCopyStatement;
    public static JLabel displayNegationStatement;
    
    public static JLabel displayBodyAsShieldRule;
    public static JLabel displayCheatChangeRule;
    public static JLabel displayOncePerTurnRule;
    public static JLabel displayAntiDragonRulerRule;
    
    // declarations of labels for displaying the current phase
    // as well as the attack and defense values of the summoned monsters
    public static JLabel displayCurrentPhase;
    public static String standardAttDefString="Att. / Def.";
    public static JLabel displayAttDefMonster1CPU;
    public static JLabel displayAttDefMonster2CPU;
    public static JLabel displayAttDefMonster3CPU;
    public static JLabel displayAttDefMonster4CPU;
    public static JLabel displayAttDefMonster5CPU;
    public static JLabel displayAttDefMonster1Player;
    public static JLabel displayAttDefMonster2Player;
    public static JLabel displayAttDefMonster3Player;
    public static JLabel displayAttDefMonster4Player;
    public static JLabel displayAttDefMonster5Player;
    
    // declare all kinds of objects already
    public static YMonster NoMonster;
    
    public static YMonster Obstacle;
    public static YMonster Barrier;
    public static YMonster Napalm;
    public static YMonster ExhaustedExecutioner;
    public static YMonster RecklessRusher;
    public static YMonster SteepLearningCurve;
    public static YMonster Shield;
    public static YMonster Sword;
    public static YMonster Lance;
    public static YMonster BuggedUpgrade;
    public static YMonster SuicideCommando;
    public static YMonster BackBouncer;
    public static YMonster Banisher;
    public static YMonster CardGrabber;
    
    public static YMonster CopyCat;
    public static YMonster SkillStealer;
    public static YMonster Flakship;
    public static YMonster HolyLance;
    public static YMonster ModeChanger;
    public static YMonster Necromancer;
    public static YMonster Burner;
    public static YMonster BigBackBouncer;
    public static YMonster BigBanisher;
    public static YMonster DiamondSword;
    public static YMonster SlickRusher;
    public static YMonster Incorruptible;
    public static YMonster AttackStopper;
    
    public static YMonster MonsterStealer;
    public static YMonster BigBurner;
    public static YMonster Eradicator;
    public static YMonster BigAttackStopper;
    public static YMonster Neutraliser;
    public static YMonster GodKillingSpear;
    public static YMonster God;
    
    public static YMonster Demon;
    public static YMonster DemonGod;
    
    // copyable card effect texts
    public static String copyableEffectGodObstacle = "opt. once per turn, when face up: show 1 of your cards: look at 1 face down card";
    public static String copyableEffectBarrier = "can not be destroyed by battle";
    public static String copyableEffectNapalm = "when destroyed (by battle or effect): inflict 1000 burn damage to the player who controls the monster that destroyed this one";
    public static String copyableEffectExhaustedExecutioner = "after this monster has attacked: it turns into defence mode";
    public static String copyableEffectRecklessRusher = "when face up at the end of the turn: add this card to the hand";
    public static String copyableEffectSteepLearningCurve = "gains 500 attack for each monster it defeats in battle";
    public static String copyableEffectShield = "(no copyable effect)";
    public static String copyableEffectSword = "(no copyable effect)";
    public static String copyableEffectLance = "has piercing attack";
    public static String copyableEffectBuggedUpgrade = "(no copyable effect)";
    public static String copyableEffectSuicideCommando = "when destroyed in battle: destroy the monster that destroyed this one";
    public static String copyableEffectBackBouncer = "when destroyed in battle: If the monster that destroyed this one is still on the field, add the monster that destroyed this one to the hand.";
    public static String copyableEffectBanisher = "when destroyed in battle: banish both monsters involved in battle";
    public static String copyableEffectCardGrabber = "opt. when face up: cost: 2 cards: search out 1 card <br>opt. when face up: cost: 1 card: put 1 card from your deck to the graveyard";
    
    public static String copyableEffectCopyCat = "opt. when face up: discard 1 card: this turn the effects of this monster are replaced by the effects of 1 monster of the discarded card";
    public static String copyableEffectSkillStealer = "opt. when face up: show 1 of your cards: choose 1 face up (equip) monster: This turn the effects of this monster are replaced by the effects of the chosen one.";
    public static String copyableEffectFlakship = "(no effect)";
    public static String copyableEffectHolyLance = "has piercing attack";
    public static String copyableEffectModeChanger = "opt. when face up: cost: 1/2 or 1 card: change 1 monster to attack or defence mode";
    public static String copyableEffectNecromancer = "opt. when face up: cost: 1/2 card: spec. sum. 1 MOOK from your GY <br>opt. when face up: cost: 1 card: spec. sum. 1 MIDBOSS from your GY";
    public static String copyableEffectBurner = "opt. once per turn, when face up: cost: 1/2 or 1 MOOK card: inflict burn damage to your opponent equal to the attack of the MOOK monster";
    public static String copyableEffectBigBackBouncer = "opt. when face up: cost: 1 card: add 1 card on the field or in the graveyard back to the hand";
    public static String copyableEffectBigBanisher = "banish all monsters destroyed in battle by this one <br>opt. when face up: cost: 1/2 or 1 card: banish 1 card in your opponent's graveyard";
    public static String copyableEffectDiamondSword = "can not be destroyed by effect";
    public static String copyableEffectSlickRusher = "immune to all effects of monsters destroyed by this one";
    public static String copyableEffectIncorruptible = "when face up: immune to all effects, can not be equipped";
    public static String copyableEffectAttackStopper = "(no copyable effect)";
    
    public static String copyableEffectMonsterStealer = "opt. once per turn, when face up: cost: 2 cards: Take control over 1 opponent's (equip) monster. This card can not attack in the turn this effect has been used.";
    public static String copyableEffectBigBurner = "opt. when face up: tribute another summoned monster: inflict burn damage to your opponent equal to the attack of the tributed monster";
    public static String copyableEffectEradicator = "opt. when face up: cost: 1 card: destroy 1 card your opponent controls";
    public static String copyableEffectBigAttackStopper = "(no copyable effect)";
    public static String copyableEffectNeutraliser = "(no copyable effect)";
    public static String copyableEffectGodKillingSpear = "(no effect)";
    
    public static String copyableEffectDemon = "opt. once per turn: when face up: tribute all of your other Demons (at least 1): cheat change this card";
    public static String copyableEffectDemonGod = "(no effect)";
    
    // same with the cards, declare them already here
    public static YCard NoCard;
    
    public static YCard GodBarrier;
    public static YCard EradicatorObstacle;
    public static YCard GodKillingSpearLance;
    public static YCard MonsterStealerSteepLearningCurve;
    public static YCard NeutraliserSkillStealer;
    public static YCard CopyCatCardGrabber;
    public static YCard AttackStopperBuggedUpgrade;
    public static YCard BigAttackStopperSword;
    public static YCard DiamondSwordShield;
    public static YCard ModeChangerExhaustedExecutioner;
    public static YCard SlickRusherRecklessRusher;
    public static YCard IncorruptibleHolyLance;
    public static YCard NecromancerBackBouncer;
    public static YCard BigBackBouncerBanisher;
    public static YCard BigBanisherBurner;
    public static YCard BigBurnerSuicideCommando;
    public static YCard FlakshipNapalm;
    public static YCard DemonGodDemon;
    
    // declare summoned monsters
    //public summonedMonster NoSummonedMonster;
    public static SummonedMonster SummonedMonster1Player = new SummonedMonster(true, 1);
    public static SummonedMonster SummonedMonster2Player = new SummonedMonster(true, 2);
    public static SummonedMonster SummonedMonster3Player = new SummonedMonster(true, 3);
    public static SummonedMonster SummonedMonster4Player = new SummonedMonster(true, 4);
    public static SummonedMonster SummonedMonster5Player = new SummonedMonster(true, 5);
    public static SummonedMonster SummonedMonster1CPU = new SummonedMonster(false, 1);
    public static SummonedMonster SummonedMonster2CPU = new SummonedMonster(false, 2);
    public static SummonedMonster SummonedMonster3CPU = new SummonedMonster(false, 3);
    public static SummonedMonster SummonedMonster4CPU = new SummonedMonster(false, 4);
    public static SummonedMonster SummonedMonster5CPU = new SummonedMonster(false, 5);
    
    // decalare hands
    //public YHand EmptyHand;
    public static Hand HandCPU;
    public static Hand HandPlayer;
    
    public static EStack EquipStack1CPU;
    public static EStack EquipStack2CPU;
    public static EStack EquipStack3CPU;
    public static EStack EquipStack4CPU;
    public static EStack EquipStack5CPU;
    public static EStack EquipStack1Player;
    public static EStack EquipStack2Player;
    public static EStack EquipStack3Player;
    public static EStack EquipStack4Player;
    public static EStack EquipStack5Player;
    
    public static JLabel displayNumberOfEquipCards1CPU;
    public static JLabel displayNumberOfEquipCards2CPU;
    public static JLabel displayNumberOfEquipCards3CPU;
    public static JLabel displayNumberOfEquipCards4CPU;
    public static JLabel displayNumberOfEquipCards5CPU;
    public static JLabel displayNumberOfEquipCards1Player;
    public static JLabel displayNumberOfEquipCards2Player;
    public static JLabel displayNumberOfEquipCards3Player;
    public static JLabel displayNumberOfEquipCards4Player;
    public static JLabel displayNumberOfEquipCards5Player;
    
    // declare decks
    //public Deck EmptyDeck;
    public static Deck StandardDeck1;
    public static Deck StandardDeck2;
    public static Deck StandardDeck3;
    public static Deck StandardDeck4;
    public static Deck StandardDeck5;
    public static Deck StandardDeck6;
    public static Deck StandardDeck7;
    public static Deck StandardDeck8;
    public static Deck StandardDeck9;
    public static Deck StandardDeck10;
    
    public static Deck UnshuffledDeckCPU;
    public static Deck UnshuffledDeckPlayer;
    public static Deck DeckCPU;//=new Deck(false);
    public static Deck DeckPlayer;//=new Deck(true);
    
    public static Deck GYDeckCPU;//=new Deck(false);
    public static Deck DDDeckCPU;//=new Deck(false);
    public static Deck DDDeckPlayer;//=new Deck(true);
    public static Deck GYDeckPlayer;//=new Deck(true);
    
    public static GameState Game = new GameState();
    public static YMonsters Mon;
    public static YCards Card;
    
    // constructor for main window
    public YuGiOhJi(){
        this.setTitle("YuGiOhJi! - The Prince of the Games");
        this.setSize(initialFrameWidth, initialFrameHeight); // window for the whole game (if screen resolution is smaller than this, it automatically rescales)
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().addComponentListener(this);
        panel = new JPanel();
        panel.setLayout(null);
        
        // background picture for defining the field
        field = new JLabel(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJi-field.png")));
        field.setBounds(0, 0, 1200, 700);
        field.setVisible(true);
        panel.add(field);
        
        // big "preview" screen for single cards (larger version in which one can read the card text)
        preview = new JLabel(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        preview.setBounds(0, 0, 400, 600);
        preview.setVisible(true);
        field.add(preview);
        
        // Add here still the buttons for the "main menu"!
        // start/reset button
        buttonStart = new JButton("start/reset");
        buttonStart.setBounds(0, 600, 200, 30);
        buttonStart.addActionListener(this); // connect buttons to listeners
        field.add(buttonStart);
        
        // deck building button
        buttonDeckBuilding = new JButton("deck building");
        buttonDeckBuilding.setBounds(200, 600, 200, 30);
        buttonDeckBuilding.addActionListener(this);
        field.add(buttonDeckBuilding);
        
        // CPU setup button
        buttonCPUsetup = new JButton("CPU setup");
        buttonCPUsetup.setBounds(0, 630, 200, 30);
        buttonCPUsetup.addActionListener(this);
        field.add(buttonCPUsetup);
        
        // give up button
        buttonGiveUp = new JButton("give up / end match");
        buttonGiveUp.setBounds(200, 630, 200, 30);
        buttonGiveUp.addActionListener(this);
        field.add(buttonGiveUp);
        
        // mods button
        buttonMods = new JButton("mods");
        buttonMods.setBounds(0, 660, 200, 30);
        buttonMods.addActionListener(this);
        field.add(buttonMods);
        
        // game setup button
        buttonGameSetup = new JButton("game setup");
        buttonGameSetup.setBounds(200, 660, 100, 30);
        buttonGameSetup.addActionListener(this);
        field.add(buttonGameSetup);
        
        // info button
        buttonInfo = new JButton("info");
        buttonInfo.setBounds(300, 660, 100, 30);
        buttonInfo.addActionListener(this);
        field.add(buttonInfo);
        
        
        // buttons for decks and graveyards and so on
        buttonDeckCPU = new JButton();
        buttonDeckCPU.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        setButtonProperties(buttonDeckCPU, 400, 0, 67, 100, true);
        field.add(buttonDeckCPU);
        
        buttonGYCPU = new JButton();
        buttonGYCPU.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        setButtonProperties(buttonGYCPU, 400, 100, 67, 100, false);
        buttonGYCPU.addActionListener(this);
        buttonGYCPU.addMouseListener(this);
        field.add(buttonGYCPU);
        
        buttonDDCPU = new JButton();
        buttonDDCPU.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        setButtonProperties(buttonDDCPU, 400, 200, 67, 100, false);
        buttonDDCPU.addActionListener(this);
        buttonDDCPU.addMouseListener(this);
        field.add(buttonDDCPU);
        
        
        buttonDDPlayer = new JButton();
        buttonDDPlayer.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        setButtonProperties(buttonDDPlayer, 400, 400, 67, 100, false);
        buttonDDPlayer.addActionListener(this);
        buttonDDPlayer.addMouseListener(this);
        field.add(buttonDDPlayer);
        
        buttonGYPlayer = new JButton();
        buttonGYPlayer.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        setButtonProperties(buttonGYPlayer, 400, 500, 67, 100, false);
        buttonGYPlayer.addActionListener(this);
        buttonGYPlayer.addMouseListener(this);
        field.add(buttonGYPlayer);
        
        buttonDeckPlayer = new JButton();
        buttonDeckPlayer.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        setButtonProperties(buttonDeckPlayer, 400, 600, 67, 100, true);
        field.add(buttonDeckPlayer);
        
        
        // labels for numbers of cards in decks and graveyards and so on
        displayNumberCardsDeckCPU = new JLabel("<html><body>&nbsp; " + 0 + "</body></html>");
        displayNumberCardsDeckCPU.setBounds(467, 0, 33, 100);
        displayNumberCardsDeckCPU.setVisible(true);
        field.add(displayNumberCardsDeckCPU);
        
        displayNumberCardsGYCPU = new JLabel("<html><body>&nbsp; " + 0 + "</body></html>");
        displayNumberCardsGYCPU.setBounds(467, 100, 33, 100);
        displayNumberCardsGYCPU.setVisible(true);
        field.add(displayNumberCardsGYCPU);
        
        displayNumberCardsDDCPU = new JLabel("<html><body>&nbsp; " + 0 + "</body></html>");
        displayNumberCardsDDCPU.setBounds(467, 200, 33, 100);
        displayNumberCardsDDCPU.setVisible(true);
        field.add(displayNumberCardsDDCPU);
        
        displayCurrentPhase = new JLabel(" main phase 1");
        displayCurrentPhase.setBounds(400, 300, 100, 100);
        displayCurrentPhase.setVisible(true);
        field.add(displayCurrentPhase);
        
        displayNumberCardsDDPlayer = new JLabel("<html><body>&nbsp; " + 0 + "</body></html>");
        displayNumberCardsDDPlayer.setBounds(467, 400, 33, 100);
        displayNumberCardsDDPlayer.setVisible(true);
        field.add(displayNumberCardsDDPlayer);
    
        displayNumberCardsGYPlayer = new JLabel("<html><body>&nbsp; " + 0 + "</body></html>");
        displayNumberCardsGYPlayer.setBounds(467, 500, 33, 100);
        displayNumberCardsGYPlayer.setVisible(true);
        field.add(displayNumberCardsGYPlayer);
        
        displayNumberCardsDeckPlayer = new JLabel("<html><body>&nbsp; " + 0 + "</body></html>");
        displayNumberCardsDeckPlayer.setBounds(467, 600, 33, 100);
        displayNumberCardsDeckPlayer.setVisible(true);
        field.add(displayNumberCardsDeckPlayer);
        
        
        // labels for the attack and the defense values of summoned monsters
        displayAttDefMonster1CPU = new JLabel(standardAttDefString);
        displayAttDefMonster1CPU.setBounds(516, 300, 100, 25);
        displayAttDefMonster1CPU.setVisible(true);
        field.add(displayAttDefMonster1CPU);
        
        displayAttDefMonster2CPU = new JLabel(standardAttDefString);
        displayAttDefMonster2CPU.setBounds(616, 300, 100, 25);
        displayAttDefMonster2CPU.setVisible(true);
        field.add(displayAttDefMonster2CPU);
        
        displayAttDefMonster3CPU = new JLabel(standardAttDefString);
        displayAttDefMonster3CPU.setBounds(716, 300, 100, 25);
        displayAttDefMonster3CPU.setVisible(true);
        field.add(displayAttDefMonster3CPU);
        
        displayAttDefMonster4CPU = new JLabel(standardAttDefString);
        displayAttDefMonster4CPU.setBounds(816, 300, 100, 25);
        displayAttDefMonster4CPU.setVisible(true);
        field.add(displayAttDefMonster4CPU);
        
        displayAttDefMonster5CPU = new JLabel(standardAttDefString);
        displayAttDefMonster5CPU.setBounds(916, 300, 100, 25);
        displayAttDefMonster5CPU.setVisible(true);
        field.add(displayAttDefMonster5CPU);
        
        displayPiercingStatement = new JLabel("<html><body><span color=\"red\"> This monster has piercing attack.</span></body></html>");
        displayPiercingStatement.setBounds(500, 325, 200, 50);
        displayPiercingStatement.setVisible(false);
        field.add(displayPiercingStatement);
        
        displayAttDefMonster1Player = new JLabel(standardAttDefString);
        displayAttDefMonster1Player.setBounds(516, 375, 100, 25);
        displayAttDefMonster1Player.setVisible(true);
        field.add(displayAttDefMonster1Player);
        
        displayAttDefMonster2Player = new JLabel(standardAttDefString);
        displayAttDefMonster2Player.setBounds(616, 375, 100, 25);
        displayAttDefMonster2Player.setVisible(true);
        field.add(displayAttDefMonster2Player);
        
        displayAttDefMonster3Player = new JLabel(standardAttDefString);
        displayAttDefMonster3Player.setBounds(716, 375, 100, 25);
        displayAttDefMonster3Player.setVisible(true);
        field.add(displayAttDefMonster3Player);
        
        displayAttDefMonster4Player = new JLabel(standardAttDefString);
        displayAttDefMonster4Player.setBounds(816, 375, 100, 25);
        displayAttDefMonster4Player.setVisible(true);
        field.add(displayAttDefMonster4Player);
        
        displayAttDefMonster5Player = new JLabel(standardAttDefString);
        displayAttDefMonster5Player.setBounds(916, 375, 100, 25);
        displayAttDefMonster5Player.setVisible(true);
        field.add(displayAttDefMonster5Player);
        
        displayBodyAsShieldRule = new JLabel("<html><body><span color=\"blue\">Body As Shield rule active</span></body></html>");
        displayBodyAsShieldRule.setBounds(1010, 100, 180, 50);
        displayBodyAsShieldRule.setVisible(true);
        field.add(displayBodyAsShieldRule);
        
        displayCheatChangeRule = new JLabel("<html><body><span color=\"blue\">Cheat Change rule active</span></body></html>");
        displayCheatChangeRule.setBounds(1010, 140, 180, 50);
        displayCheatChangeRule.setVisible(false);
        field.add(displayCheatChangeRule);
        
        displayOncePerTurnRule = new JLabel("<html><body><span color=\"blue\">Once Per Turn rule active</span></body></html>");
        displayOncePerTurnRule.setBounds(1010, 180, 180, 50);
        displayOncePerTurnRule.setVisible(false);
        field.add(displayOncePerTurnRule);
        
        displayAntiDragonRulerRule = new JLabel("<html><body><span color=\"blue\">Anti Dragon Ruler rule active</span></body></html>");
        displayAntiDragonRulerRule.setBounds(1010, 220, 180, 50);
        displayAntiDragonRulerRule.setVisible(false);
        field.add(displayAntiDragonRulerRule);
        
        displayLPCPU = new JLabel("<html><body>&nbsp; LP: " + Game.lifePointsCPU + "<br>&nbsp;[________________]</body></html>");
        displayLPCPU.setBounds(1000, 250, 150, 50);
        displayLPCPU.setVisible(true);
        field.add(displayLPCPU);
        
        // next phase button
        buttonNextPhase = new JButton("next phase");
        buttonNextPhase.setBounds(1000, 325, 180, 75);
        buttonNextPhase.addActionListener(this);
        field.add(buttonNextPhase);
        
        buttonNextTurn = new JButton("next turn");
        buttonNextTurn.setBounds(1000, 300, 180, 25);
        buttonNextTurn.addActionListener(this);
        field.add(buttonNextTurn);
        
        displayLPPlayer = new JLabel("<html><body>&nbsp; LP: " + Game.lifePointsPlayer + "<br>&nbsp;[________________]</body></html>");
        displayLPPlayer.setBounds(1000, 400, 150, 50);
        displayLPPlayer.setVisible(true);
        field.add(displayLPPlayer);
        
        displaySkillCopyStatement = new JLabel("");
        displaySkillCopyStatement.setBounds(1010, 450, 150, 150);
        displaySkillCopyStatement.setVisible(false);
        field.add(displaySkillCopyStatement);
        
        displayNegationStatement = new JLabel("<html><body><span color=\"red\"> This card has its effects negated.</span></body></html>");
        displayNegationStatement.setBounds(1010, 450, 150, 100);
        displayNegationStatement.setVisible(false);
        field.add(displayNegationStatement);
        
        // Add the buttons for cards on the field here!
        // equip cards on CPU side (& number of equip cards display)
        buttonEquip1CPU = new JButton();
        buttonEquip1CPU.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        setButtonProperties(buttonEquip1CPU, 516, 100, 67, 100, false);
        buttonEquip1CPU.addActionListener(this);
        buttonEquip1CPU.addMouseListener(this);
        field.add(buttonEquip1CPU);
        
        displayNumberOfEquipCards1CPU = new JLabel(" 0");
        displayNumberOfEquipCards1CPU.setBounds(585, 100, 15, 100);
        displayNumberOfEquipCards1CPU.setVisible(false);
        field.add(displayNumberOfEquipCards1CPU);
        
        buttonEquip2CPU = new JButton();
        buttonEquip2CPU.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        setButtonProperties(buttonEquip2CPU, 616, 100, 67, 100, false);
        buttonEquip2CPU.addActionListener(this);
        buttonEquip2CPU.addMouseListener(this);
        field.add(buttonEquip2CPU);
        
        displayNumberOfEquipCards2CPU = new JLabel(" 0");
        displayNumberOfEquipCards2CPU.setBounds(685, 100, 15, 100);
        displayNumberOfEquipCards2CPU.setVisible(false);
        field.add(displayNumberOfEquipCards2CPU);
        
        buttonEquip3CPU = new JButton();
        buttonEquip3CPU.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        setButtonProperties(buttonEquip3CPU, 716, 100, 67, 100, false);
        buttonEquip3CPU.addActionListener(this);
        buttonEquip3CPU.addMouseListener(this);
        field.add(buttonEquip3CPU);
        
        displayNumberOfEquipCards3CPU = new JLabel(" 0");
        displayNumberOfEquipCards3CPU.setBounds(785, 100, 15, 100);
        displayNumberOfEquipCards3CPU.setVisible(false);
        field.add(displayNumberOfEquipCards3CPU);
        
        buttonEquip4CPU = new JButton();
        buttonEquip4CPU.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        setButtonProperties(buttonEquip4CPU, 816, 100, 67, 100, false);
        buttonEquip4CPU.addActionListener(this);
        buttonEquip4CPU.addMouseListener(this);
        field.add(buttonEquip4CPU);
        
        displayNumberOfEquipCards4CPU = new JLabel(" 0");
        displayNumberOfEquipCards4CPU.setBounds(885, 100, 15, 100);
        displayNumberOfEquipCards4CPU.setVisible(false);
        field.add(displayNumberOfEquipCards4CPU);
        
        buttonEquip5CPU = new JButton();
        buttonEquip5CPU.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        setButtonProperties(buttonEquip5CPU, 916, 100, 67, 100, false);
        buttonEquip5CPU.addActionListener(this);
        buttonEquip5CPU.addMouseListener(this);
        field.add(buttonEquip5CPU);
        
        displayNumberOfEquipCards5CPU = new JLabel(" 0");
        displayNumberOfEquipCards5CPU.setBounds(985, 100, 15, 100);
        displayNumberOfEquipCards5CPU.setVisible(false);
        field.add(displayNumberOfEquipCards5CPU);
        
        
        // summoned monsters on CPU side
        buttonMonster1CPU = new JButton();
        buttonMonster1CPU.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        setButtonProperties(buttonMonster1CPU, 516, 200, 67, 100, false);
        buttonMonster1CPU.addActionListener(this);
        buttonMonster1CPU.addMouseListener(this);
        field.add(buttonMonster1CPU);
        
        buttonMonster2CPU = new JButton();
        buttonMonster2CPU.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        setButtonProperties(buttonMonster2CPU, 616, 200, 67, 100, false);
        buttonMonster2CPU.addActionListener(this);
        buttonMonster2CPU.addMouseListener(this);
        field.add(buttonMonster2CPU);
        
        buttonMonster3CPU = new JButton();
        buttonMonster3CPU.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        setButtonProperties(buttonMonster3CPU, 716, 200, 67, 100, false);
        buttonMonster3CPU.addActionListener(this);
        buttonMonster3CPU.addMouseListener(this);
        field.add(buttonMonster3CPU);
        
        buttonMonster4CPU = new JButton();
        buttonMonster4CPU.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        setButtonProperties(buttonMonster4CPU, 816, 200, 67, 100, false);
        buttonMonster4CPU.addActionListener(this);
        buttonMonster4CPU.addMouseListener(this);
        field.add(buttonMonster4CPU);
        
        buttonMonster5CPU = new JButton();
        buttonMonster5CPU.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        setButtonProperties(buttonMonster5CPU, 916, 200, 67, 100, false);
        buttonMonster5CPU.addActionListener(this);
        buttonMonster5CPU.addMouseListener(this);
        field.add(buttonMonster5CPU);
        
        // summoned monsters on Player side
        buttonMonster1Player = new JButton();
        buttonMonster1Player.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        setButtonProperties(buttonMonster1Player, 516, 400, 67, 100, false);
        buttonMonster1Player.addActionListener(this);
        buttonMonster1Player.addMouseListener(this);
        field.add(buttonMonster1Player);
        
        buttonMonster2Player = new JButton();
        buttonMonster2Player.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        setButtonProperties(buttonMonster2Player, 616, 400, 67, 100, false);
        buttonMonster2Player.addActionListener(this);
        buttonMonster2Player.addMouseListener(this);
        field.add(buttonMonster2Player);
        
        buttonMonster3Player = new JButton();
        buttonMonster3Player.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        setButtonProperties(buttonMonster3Player, 716, 400, 67, 100, false);
        buttonMonster3Player.addActionListener(this);
        buttonMonster3Player.addMouseListener(this);
        field.add(buttonMonster3Player);
        
        buttonMonster4Player = new JButton();
        buttonMonster4Player.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        setButtonProperties(buttonMonster4Player, 816, 400, 67, 100, false);
        buttonMonster4Player.addActionListener(this);
        buttonMonster4Player.addMouseListener(this);
        field.add(buttonMonster4Player);
        
        buttonMonster5Player = new JButton();
        buttonMonster5Player.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        setButtonProperties(buttonMonster5Player, 916, 400, 67, 100, false);
        buttonMonster5Player.addActionListener(this);
        buttonMonster5Player.addMouseListener(this);
        field.add(buttonMonster5Player);
        
        
        // equip cards on Player side
        buttonEquip1Player = new JButton();
        buttonEquip1Player.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        setButtonProperties(buttonEquip1Player, 516, 500, 67, 100, false);
        buttonEquip1Player.addActionListener(this);
        buttonEquip1Player.addMouseListener(this);
        field.add(buttonEquip1Player);
        
        displayNumberOfEquipCards1Player = new JLabel(" 0");
        displayNumberOfEquipCards1Player.setBounds(585, 500, 15, 100);
        displayNumberOfEquipCards1Player.setVisible(false);
        field.add(displayNumberOfEquipCards1Player);
        
        buttonEquip2Player = new JButton();
        buttonEquip2Player.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        setButtonProperties(buttonEquip2Player, 616, 500, 67, 100, false);
        buttonEquip2Player.addActionListener(this);
        buttonEquip2Player.addMouseListener(this);
        field.add(buttonEquip2Player);
        
        displayNumberOfEquipCards2Player = new JLabel(" 0");
        displayNumberOfEquipCards2Player.setBounds(685, 500, 15, 100);
        displayNumberOfEquipCards2Player.setVisible(false);
        field.add(displayNumberOfEquipCards2Player);
        
        buttonEquip3Player = new JButton();
        buttonEquip3Player.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        setButtonProperties(buttonEquip3Player, 716, 500, 67, 100, false);
        buttonEquip3Player.addActionListener(this);
        buttonEquip3Player.addMouseListener(this);
        field.add(buttonEquip3Player);
        
        displayNumberOfEquipCards3Player = new JLabel(" 0");
        displayNumberOfEquipCards3Player.setBounds(785, 500, 15, 100);
        displayNumberOfEquipCards3Player.setVisible(false);
        field.add(displayNumberOfEquipCards3Player);
        
        buttonEquip4Player = new JButton();
        buttonEquip4Player.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        setButtonProperties(buttonEquip4Player, 816, 500, 67, 100, false);
        buttonEquip4Player.addActionListener(this);
        buttonEquip4Player.addMouseListener(this);
        field.add(buttonEquip4Player);
        
        displayNumberOfEquipCards4Player = new JLabel(" 0");
        displayNumberOfEquipCards4Player.setBounds(885, 500, 15, 100);
        displayNumberOfEquipCards4Player.setVisible(false);
        field.add(displayNumberOfEquipCards4Player);
        
        buttonEquip5Player = new JButton();
        buttonEquip5Player.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        setButtonProperties(buttonEquip5Player, 916, 500, 67, 100, false);
        buttonEquip5Player.addActionListener(this);
        buttonEquip5Player.addMouseListener(this);
        field.add(buttonEquip5Player);
        
        displayNumberOfEquipCards5Player = new JLabel(" 0");
        displayNumberOfEquipCards5Player.setBounds(985, 500, 15, 100);
        displayNumberOfEquipCards5Player.setVisible(false);
        field.add(displayNumberOfEquipCards5Player);
        
        
        // buttons for cards on the hand of the CPU
        buttonCard1CPU = new JButton();
        buttonCard1CPU.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        setButtonProperties(buttonCard1CPU, 500, 0, 67, 100, false);
        field.add(buttonCard1CPU);
        
        buttonCard2CPU = new JButton();
        buttonCard2CPU.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        setButtonProperties(buttonCard2CPU, 567, 0, 67, 100, false);
        field.add(buttonCard2CPU);
        
        buttonCard3CPU = new JButton();
        buttonCard3CPU.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        setButtonProperties(buttonCard3CPU, 634, 0, 67, 100, false);
        field.add(buttonCard3CPU);
        
        buttonCard4CPU = new JButton();
        buttonCard4CPU.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        setButtonProperties(buttonCard4CPU, 700, 0, 67, 100, false);
        field.add(buttonCard4CPU);
        
        buttonCard5CPU = new JButton();
        buttonCard5CPU.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        setButtonProperties(buttonCard5CPU, 767, 0, 67, 100, false);
        field.add(buttonCard5CPU);
        
        buttonCard6CPU = new JButton();
        buttonCard6CPU.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        setButtonProperties(buttonCard6CPU, 834, 0, 67, 100, false);
        field.add(buttonCard6CPU);
        
        buttonCard7CPU = new JButton();
        buttonCard7CPU.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        setButtonProperties(buttonCard7CPU, 900, 0, 67, 100, false);
        field.add(buttonCard7CPU);
        
        buttonCard8CPU = new JButton();
        buttonCard8CPU.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        setButtonProperties(buttonCard8CPU, 967, 0, 67, 100, false);
        field.add(buttonCard8CPU);
        
        buttonCard9CPU = new JButton();
        buttonCard9CPU.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        setButtonProperties(buttonCard9CPU, 1034, 0, 67, 100, false);
        field.add(buttonCard9CPU);
        
        buttonCard10CPU = new JButton();
        buttonCard10CPU.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        setButtonProperties(buttonCard10CPU, 1100, 0, 67, 100, false);
        field.add(buttonCard10CPU);
        
        
        // buttons for playable cards on hand
        buttonCard1Player = new JButton();
        buttonCard1Player.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        setButtonProperties(buttonCard1Player, 500, 600, 67, 100, false);
        buttonCard1Player.addActionListener(this);
        buttonCard1Player.addMouseListener(this);
        field.add(buttonCard1Player);
        
        buttonCard2Player = new JButton();
        buttonCard2Player.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        setButtonProperties(buttonCard2Player, 567, 600, 67, 100, false);
        buttonCard2Player.addActionListener(this);
        buttonCard2Player.addMouseListener(this);
        field.add(buttonCard2Player);
        
        buttonCard3Player = new JButton();
        buttonCard3Player.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        setButtonProperties(buttonCard3Player, 634, 600, 67, 100, false);
        buttonCard3Player.addActionListener(this);
        buttonCard3Player.addMouseListener(this);
        field.add(buttonCard3Player);
        
        buttonCard4Player = new JButton();
        buttonCard4Player.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        setButtonProperties(buttonCard4Player, 700, 600, 67, 100, false);
        buttonCard4Player.addActionListener(this);
        buttonCard4Player.addMouseListener(this);
        field.add(buttonCard4Player);
        
        buttonCard5Player = new JButton();
        buttonCard5Player.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        setButtonProperties(buttonCard5Player, 767, 600, 67, 100, false);
        buttonCard5Player.addActionListener(this);
        buttonCard5Player.addMouseListener(this);
        field.add(buttonCard5Player);
        
        buttonCard6Player = new JButton();
        buttonCard6Player.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        setButtonProperties(buttonCard6Player, 834, 600, 67, 100, false);
        buttonCard6Player.addActionListener(this);
        buttonCard6Player.addMouseListener(this);
        field.add(buttonCard6Player);
        
        buttonCard7Player = new JButton();
        buttonCard7Player.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        setButtonProperties(buttonCard7Player, 900, 600, 67, 100, false);
        buttonCard7Player.addActionListener(this);
        buttonCard7Player.addMouseListener(this);
        field.add(buttonCard7Player);
        
        buttonCard8Player = new JButton();
        buttonCard8Player.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        setButtonProperties(buttonCard8Player, 967, 600, 67, 100, false);
        buttonCard8Player.addActionListener(this);
        buttonCard8Player.addMouseListener(this);
        field.add(buttonCard8Player);
        
        buttonCard9Player = new JButton();
        buttonCard9Player.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        setButtonProperties(buttonCard9Player, 1034, 600, 67, 100, false);
        buttonCard9Player.addActionListener(this);
        buttonCard9Player.addMouseListener(this);
        field.add(buttonCard9Player);
        
        buttonCard10Player = new JButton();
        buttonCard10Player.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        setButtonProperties(buttonCard10Player, 1100, 600, 67, 100, false);
        buttonCard10Player.addActionListener(this);
        buttonCard10Player.addMouseListener(this);
        field.add(buttonCard10Player);
        
        // create all monsters, cards, decks and hands here
        // begin with monsters
        Obstacle = new YMonster (1, "Obstacle", 1, 0, 2000, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, 0, copyableEffectGodObstacle, "/images/ObstacleAtt.png", "/images/ObstacleDef.png");
        Barrier = new YMonster (2, "Barrier", 1, 0, 0, true, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, 0, copyableEffectBarrier, "/images/BarrierAtt.png", "/images/BarrierDef.png");
        Napalm = new YMonster (3, "Napalm", 1, 0, 0, true, true, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, 1000, copyableEffectNapalm, "/images/NapalmAtt.png", "/images/NapalmDef.png");
        ExhaustedExecutioner = new YMonster (4, "Exhausted Executioner", 1, 2000, 500, true, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, 0, copyableEffectExhaustedExecutioner, "/images/ExhaustedExecutionerAtt.png", "/images/ExhaustedExecutionerDef.png");
        RecklessRusher = new YMonster (5, "Reckless Rusher", 1, 2000, 0, true, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, 0, copyableEffectRecklessRusher, "/images/RecklessRusherAtt.png", "/images/RecklessRusherDef.png");
        SteepLearningCurve = new YMonster (6, "Steep Learning Curve", 1, 1000, 1000, true, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, 500, copyableEffectSteepLearningCurve, "/images/SteepLearningCurveAtt.png", "/images/SteepLearningCurveDef.png");
        Shield = new YMonster (7, "Shield", 1, 1500, 1000, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, true, false, false, 1000, copyableEffectShield, "/images/ShieldAtt.png", "/images/ShieldDef.png");
        Sword = new YMonster (8, "Sword", 1, 500, 1500, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, true, false, false, 500, copyableEffectSword, "/images/SwordAtt.png", "/images/SwordDef.png");
        Lance = new YMonster (9, "Lance", 1, 1500, 1500, true, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, true, true, false, false, 0, copyableEffectLance, "/images/LanceAtt.png", "/images/LanceDef.png");
        BuggedUpgrade = new YMonster (10, "Bugged Upgrade", 1, 1000, 1000, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, true, false, false, 0, copyableEffectBuggedUpgrade, "/images/BuggedUpgradeAtt.png", "/images/BuggedUpgradeDef.png");
        SuicideCommando = new YMonster (11, "Suicide Commando", 1, 500, 1500, true, true, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, 0, copyableEffectSuicideCommando, "/images/SuicideCommandoAtt.png", "/images/SuicideCommandoDef.png");
        BackBouncer = new YMonster (12, "Back Bouncer", 1, 1500, 1000, true, true, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, 0, copyableEffectBackBouncer, "/images/BackBouncerAtt.png", "/images/BackBouncerDef.png");
        Banisher = new YMonster (13, "Banisher", 1, 1500, 1000, true, true, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, 0, copyableEffectBanisher, "/images/BanisherAtt.png", "/images/BanisherDef.png");
        CardGrabber = new YMonster (14, "Card Grabber", 1, 1000, 1000, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, 0, copyableEffectCardGrabber, "/images/CardGrabberAtt.png", "/images/CardGrabberDef.png");
       
        CopyCat = new YMonster (15, "Copy Cat", 2, 2000, 1000, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, 0, copyableEffectCopyCat, "/images/CopyCatAtt.png", "/images/CopyCatDef.png");
        SkillStealer = new YMonster (16, "Skill Stealer", 2, 2000, 1500, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, 0, copyableEffectSkillStealer, "/images/SkillStealerAtt.png", "/images/SkillStealerDef.png");
        Flakship = new YMonster (17, "FLAKship", 2, 2500, 2500, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, 0, copyableEffectFlakship, "/images/FlakshipAtt.png", "/images/FlakshipDef.png");
        HolyLance = new YMonster (18, "Holy Lance", 2, 2500, 1000, true, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, 0, copyableEffectHolyLance, "/images/HolyLanceAtt.png", "/images/HolyLanceDef.png");
        ModeChanger = new YMonster (19, "Mode Changer", 2, 2500, 1000, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, 0, copyableEffectModeChanger, "/images/ModeChangerAtt.png", "/images/ModeChangerDef.png");
        Necromancer = new YMonster (20, "Necromancer", 2, 2500, 1000, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, 0, copyableEffectNecromancer, "/images/NecromancerAtt.png", "/images/NecromancerDef.png");
        Burner = new YMonster (21, "Burner", 2, 2000, 1000, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, 0, copyableEffectBurner, "/images/BurnerAtt.png", "/images/BurnerDef.png");
        BigBackBouncer = new YMonster (22, "Big Back Bouncer", 2, 2500, 1000, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, 0, copyableEffectBigBackBouncer, "/images/BigBackBouncerAtt.png", "/images/BigBackBouncerDef.png");
        BigBanisher = new YMonster (23, "Big Banisher", 2, 2500, 2000, true, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, true, false, false, 0, copyableEffectBigBanisher, "/images/BigBanisherAtt.png", "/images/BigBanisherDef.png");
        DiamondSword = new YMonster (24, "Diamond Sword", 2, 2000, 2000, true, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, 0, copyableEffectDiamondSword, "/images/DiamondSwordAtt.png", "/images/DiamondSwordDef.png");
        SlickRusher = new YMonster (25, "Slick Rusher", 2, 2500, 2000, true, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, 0, copyableEffectSlickRusher, "/images/SlickRusherAtt.png", "/images/SlickRusherDef.png");
        Incorruptible = new YMonster (26, "Incorruptible", 2, 2500, 2000, true, false, false, false, true, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, 0, copyableEffectIncorruptible, "/images/IncorruptibleAtt.png", "/images/IncorruptibleDef.png");
        AttackStopper = new YMonster (27, "Attack Stopper", 2, 2000, 3000, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, 0, copyableEffectAttackStopper, "/images/AttackStopperAtt.png", "/images/AttackStopperDef.png");
        
        MonsterStealer = new YMonster (28, "Monster Stealer", 3, 2500, 1000, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, 0, copyableEffectMonsterStealer, "/images/MonsterStealerAtt.png", "/images/MonsterStealerDef.png");
        BigBurner = new YMonster (29, "Big Burner", 3, 2000, 2000, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, 0, copyableEffectBigBurner, "/images/BigBurnerAtt.png", "/images/BigBurnerDef.png");
        Eradicator = new YMonster (30, "Eradicator", 3, 3000, 1500, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, 0, copyableEffectEradicator, "/images/EradicatorAtt.png", "/images/EradicatorDef.png");
        BigAttackStopper = new YMonster (31, "Big Attack Stopper", 3, 2500, 2000, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, 0, copyableEffectBigAttackStopper, "/images/BigAttackStopperAtt.png", "/images/BigAttackStopperDef.png");
        Neutraliser = new YMonster (32, "Neutraliser", 3, 3000, 2000, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, true, 0, copyableEffectNeutraliser, "/images/NeutraliserAtt.png", "/images/NeutraliserDef.png");
        GodKillingSpear = new YMonster (33, "God Killing Spear", 3, 4000, 0, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, 0, copyableEffectGodKillingSpear, "/images/GodKillingSpearAtt.png", "/images/GodKillingSpearDef.png");
        God = new YMonster (34, "God", 4, 4000, 4000, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, 0, copyableEffectGodObstacle, "/images/GodAtt.png", "/images/GodDef.png");
        // I came up with the following monster later on. That's why they are not in the right order
        Demon = new YMonster (35, "Demon", 1, 1000, 0, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, true, true, false, false, 0, copyableEffectDemon, "/images/DemonAtt.png", "/images/DemonDef.png");
        DemonGod = new YMonster (36, "Demon God", 4, 4000, 2000, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, 0, copyableEffectDemonGod, "/images/DemonGodAtt.png", "/images/DemonGodDef.png");
                
        // this one is just added twice to the placeholder card (the subsitute for a non-existing card)
        NoMonster = new YMonster (0, "NoMonster", 0, 0, 0, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, 0, "(no copyable effect)", "/images/YuGiOhJiFacedown.png", "/images/YuGiOhJiFacedown.png");
        
        // add cards here!
        NoCard = new YCard (0, "NoCard", false, false, false, false, false, NoMonster, false, false, false, NoMonster, "/images/YuGiOhJiFacedown.png");
        
        GodBarrier = new YCard (1, God.monsterName + " - " + Barrier.monsterName, false, false, true, false, false, God, true, false, false, Barrier, "/images/God-Barrier.png");
        EradicatorObstacle = new YCard (2, Eradicator.monsterName + " - " + Obstacle.monsterName, false, true, true, true, false, Eradicator, true, false, false, Obstacle, "/images/Eradicator-Obstacle.png");
        GodKillingSpearLance = new YCard (3, GodKillingSpear.monsterName + " - " + Lance.monsterName, false, true, true, true, false, GodKillingSpear, true, false, true, Lance, "/images/GodKillingSpear-Lance.png");
        MonsterStealerSteepLearningCurve = new YCard (4, MonsterStealer.monsterName + " - " +  SteepLearningCurve.monsterName, false, true, true, true, false, MonsterStealer, true, false, false, SteepLearningCurve, "/images/MonsterStealer-SteepLearningCurve.png");
        NeutraliserSkillStealer = new YCard (5, Neutraliser.monsterName + " - " + SkillStealer.monsterName, true, true, true, true, false, Neutraliser, true, true, false, SkillStealer, "/images/Neutraliser-SkillStealer.png");
        CopyCatCardGrabber = new YCard (6, CopyCat.monsterName + " - " + CardGrabber.monsterName, false, false, true, true, false, CopyCat, true, false, false, CardGrabber, "/images/CopyCat-CardGrabber.png");
        AttackStopperBuggedUpgrade = new YCard (7, AttackStopper.monsterName + " - " + BuggedUpgrade.monsterName, false, false, true, true, false, AttackStopper, true, false, true, BuggedUpgrade, "/images/AttackStopper-BuggedUpgrade.png");
        BigAttackStopperSword = new YCard (8, BigAttackStopper.monsterName + " - " + Sword.monsterName, true, true, true, true, false, BigAttackStopper, true, false, true, Sword, "/images/BigAttackStopper-Sword.png");
        DiamondSwordShield = new YCard (9, DiamondSword.monsterName + " - " + Shield.monsterName, false, false, true, true, false, DiamondSword, true, false, true, Shield, "/images/DiamondSword-Shield.png");
        ModeChangerExhaustedExecutioner = new YCard (10, ModeChanger.monsterName + " - " + ExhaustedExecutioner.monsterName, false, false, true, true, false, ModeChanger, true, false, false, ExhaustedExecutioner, "/images/ModeChanger-ExhaustedExecutioner.png");
        SlickRusherRecklessRusher = new YCard (11, SlickRusher.monsterName + " - " + RecklessRusher.monsterName, false, false, true, true, false, SlickRusher, true, false, false, RecklessRusher, "/images/SlickRusher-RecklessRusher.png");
        IncorruptibleHolyLance = new YCard (12, Incorruptible.monsterName + " - " + HolyLance.monsterName, false, false, true, true, false, Incorruptible, true, true, false, HolyLance, "/images/Incorruptible-HolyLance.png");
        NecromancerBackBouncer = new YCard (13, Necromancer.monsterName + " - " + BackBouncer.monsterName, false, false, true, true, false, Necromancer, true, true, false, BackBouncer, "/images/Necromancer-BackBouncer.png");
        BigBackBouncerBanisher = new YCard (14, BigBackBouncer.monsterName + " - " + Banisher.monsterName, true, false, true, true, false, BigBackBouncer, true, false, false, Banisher, "/images/BigBackBouncer-Banisher.png");
        BigBanisherBurner = new YCard (15, BigBanisher.monsterName + " - " + Burner.monsterName, false, false, true, true, false, BigBanisher, true, true, false, Burner, "/images/BigBanisher-Burner.png");
        BigBurnerSuicideCommando = new YCard (16, BigBurner.monsterName + " - " + SuicideCommando.monsterName, false, true, true, true, false,BigBurner, true, false, false, SuicideCommando, "/images/BigBurner-SuicideCommando.png");
        FlakshipNapalm = new YCard (17, Flakship.monsterName + " - " + Napalm.monsterName, false, false, true, true, false, Flakship, true, false, false, Napalm, "/images/Flakship-Napalm.png");
        DemonGodDemon = new YCard (18, DemonGod.monsterName + " - " + Demon.monsterName, false, false, true, false, false, DemonGod, true, false, true, Demon, "/images/DemonGod-Demon.png");
        
        // bundle all monsters and cards into single objects
        Mon = new YMonsters();
        Card = new YCards(); // Important!: Create bundles of cards (and monsters) before creating decks and hands! (otherwise the game won't start)
        
        // the 10 standard decks (look into deck building class for explanation)
        StandardDeck1 = new Deck (39, true, GodBarrier, GodBarrier, GodBarrier, EradicatorObstacle, EradicatorObstacle, EradicatorObstacle, GodKillingSpearLance, GodKillingSpearLance, GodKillingSpearLance, MonsterStealerSteepLearningCurve, MonsterStealerSteepLearningCurve, MonsterStealerSteepLearningCurve, NeutraliserSkillStealer, NeutraliserSkillStealer, NeutraliserSkillStealer, CopyCatCardGrabber, CopyCatCardGrabber, CopyCatCardGrabber, AttackStopperBuggedUpgrade, AttackStopperBuggedUpgrade, AttackStopperBuggedUpgrade, BigAttackStopperSword, BigAttackStopperSword, BigAttackStopperSword, ModeChangerExhaustedExecutioner, ModeChangerExhaustedExecutioner, ModeChangerExhaustedExecutioner, NecromancerBackBouncer, NecromancerBackBouncer, NecromancerBackBouncer, BigBackBouncerBanisher, BigBackBouncerBanisher, BigBackBouncerBanisher, BigBurnerSuicideCommando, BigBurnerSuicideCommando, BigBurnerSuicideCommando, FlakshipNapalm, FlakshipNapalm, FlakshipNapalm);
        UnshuffledDeckPlayer = new Deck(true); // needed to instantiate here, because NoCard already has to be defined and setting a deck right away not possible, because trying to set an undefined deck would make game crash
        UnshuffledDeckCPU = new Deck(false);
        UnshuffledDeckPlayer.setDeck(StandardDeck1, true); // player uses standard deck #1 if the settings are not changed
        UnshuffledDeckCPU.setDeck(StandardDeck1, false);
        // some of these are still used for the CPU
        StandardDeck2 = new Deck (39, true, GodKillingSpearLance, GodKillingSpearLance, GodKillingSpearLance, MonsterStealerSteepLearningCurve, MonsterStealerSteepLearningCurve, MonsterStealerSteepLearningCurve, NeutraliserSkillStealer, NeutraliserSkillStealer, NeutraliserSkillStealer, CopyCatCardGrabber, CopyCatCardGrabber, CopyCatCardGrabber, AttackStopperBuggedUpgrade, AttackStopperBuggedUpgrade, AttackStopperBuggedUpgrade, BigAttackStopperSword, BigAttackStopperSword, BigAttackStopperSword, ModeChangerExhaustedExecutioner, ModeChangerExhaustedExecutioner, ModeChangerExhaustedExecutioner, NecromancerBackBouncer, NecromancerBackBouncer, NecromancerBackBouncer, BigBackBouncerBanisher, BigBackBouncerBanisher, BigBackBouncerBanisher, BigBanisherBurner, BigBanisherBurner, BigBanisherBurner, BigBurnerSuicideCommando, BigBurnerSuicideCommando, BigBurnerSuicideCommando, FlakshipNapalm, FlakshipNapalm, FlakshipNapalm, DemonGodDemon, DemonGodDemon, DemonGodDemon);
        StandardDeck3 = new Deck (39, true, GodBarrier, GodBarrier, GodBarrier, EradicatorObstacle, EradicatorObstacle, EradicatorObstacle, MonsterStealerSteepLearningCurve, MonsterStealerSteepLearningCurve, MonsterStealerSteepLearningCurve, NeutraliserSkillStealer, NeutraliserSkillStealer, NeutraliserSkillStealer, CopyCatCardGrabber, CopyCatCardGrabber, CopyCatCardGrabber, ModeChangerExhaustedExecutioner, ModeChangerExhaustedExecutioner, ModeChangerExhaustedExecutioner, SlickRusherRecklessRusher, SlickRusherRecklessRusher, SlickRusherRecklessRusher, IncorruptibleHolyLance, IncorruptibleHolyLance, IncorruptibleHolyLance, NecromancerBackBouncer, NecromancerBackBouncer, NecromancerBackBouncer, BigBackBouncerBanisher, BigBackBouncerBanisher, BigBackBouncerBanisher, BigBanisherBurner, BigBanisherBurner, BigBanisherBurner, BigBurnerSuicideCommando, BigBurnerSuicideCommando, BigBurnerSuicideCommando, FlakshipNapalm, FlakshipNapalm, FlakshipNapalm);
        StandardDeck4 = new Deck (39, true, GodBarrier, GodBarrier, GodBarrier, EradicatorObstacle, EradicatorObstacle, EradicatorObstacle, GodKillingSpearLance, GodKillingSpearLance, GodKillingSpearLance, MonsterStealerSteepLearningCurve, MonsterStealerSteepLearningCurve, MonsterStealerSteepLearningCurve, NeutraliserSkillStealer, NeutraliserSkillStealer, NeutraliserSkillStealer, CopyCatCardGrabber, CopyCatCardGrabber, CopyCatCardGrabber, AttackStopperBuggedUpgrade, AttackStopperBuggedUpgrade, AttackStopperBuggedUpgrade, BigAttackStopperSword, BigAttackStopperSword, BigAttackStopperSword, DiamondSwordShield, DiamondSwordShield, DiamondSwordShield, ModeChangerExhaustedExecutioner, ModeChangerExhaustedExecutioner, ModeChangerExhaustedExecutioner, SlickRusherRecklessRusher, SlickRusherRecklessRusher, SlickRusherRecklessRusher, IncorruptibleHolyLance, IncorruptibleHolyLance, IncorruptibleHolyLance, BigBanisherBurner, BigBanisherBurner, BigBanisherBurner);
        StandardDeck5 = new Deck (39, true, GodBarrier, GodBarrier, GodBarrier, GodKillingSpearLance, GodKillingSpearLance, GodKillingSpearLance, NeutraliserSkillStealer, NeutraliserSkillStealer, NeutraliserSkillStealer, CopyCatCardGrabber, CopyCatCardGrabber, CopyCatCardGrabber, AttackStopperBuggedUpgrade, AttackStopperBuggedUpgrade, AttackStopperBuggedUpgrade, BigAttackStopperSword, BigAttackStopperSword, BigAttackStopperSword, DiamondSwordShield, DiamondSwordShield, DiamondSwordShield, ModeChangerExhaustedExecutioner, ModeChangerExhaustedExecutioner, ModeChangerExhaustedExecutioner, SlickRusherRecklessRusher, SlickRusherRecklessRusher, SlickRusherRecklessRusher, IncorruptibleHolyLance, IncorruptibleHolyLance, IncorruptibleHolyLance, BigBurnerSuicideCommando, BigBurnerSuicideCommando, BigBurnerSuicideCommando, FlakshipNapalm, FlakshipNapalm, FlakshipNapalm, DemonGodDemon, DemonGodDemon, DemonGodDemon);
        StandardDeck6 = new Deck (39, true, GodBarrier, GodBarrier, GodBarrier, EradicatorObstacle, EradicatorObstacle, EradicatorObstacle, GodKillingSpearLance, GodKillingSpearLance, GodKillingSpearLance, MonsterStealerSteepLearningCurve, MonsterStealerSteepLearningCurve, MonsterStealerSteepLearningCurve, NeutraliserSkillStealer, NeutraliserSkillStealer, NeutraliserSkillStealer, CopyCatCardGrabber, CopyCatCardGrabber, CopyCatCardGrabber, AttackStopperBuggedUpgrade, AttackStopperBuggedUpgrade, AttackStopperBuggedUpgrade, BigAttackStopperSword, BigAttackStopperSword, BigAttackStopperSword, DiamondSwordShield, DiamondSwordShield, DiamondSwordShield, ModeChangerExhaustedExecutioner, ModeChangerExhaustedExecutioner, ModeChangerExhaustedExecutioner, SlickRusherRecklessRusher, SlickRusherRecklessRusher, SlickRusherRecklessRusher, IncorruptibleHolyLance, IncorruptibleHolyLance, IncorruptibleHolyLance, NecromancerBackBouncer, NecromancerBackBouncer, NecromancerBackBouncer);
        StandardDeck7 = new Deck (39, true, GodBarrier, GodBarrier, GodBarrier, EradicatorObstacle, EradicatorObstacle, EradicatorObstacle, GodKillingSpearLance, GodKillingSpearLance, GodKillingSpearLance, MonsterStealerSteepLearningCurve, MonsterStealerSteepLearningCurve, MonsterStealerSteepLearningCurve, CopyCatCardGrabber, CopyCatCardGrabber, CopyCatCardGrabber, DiamondSwordShield, DiamondSwordShield, DiamondSwordShield, ModeChangerExhaustedExecutioner, ModeChangerExhaustedExecutioner, ModeChangerExhaustedExecutioner, SlickRusherRecklessRusher, SlickRusherRecklessRusher, SlickRusherRecklessRusher, IncorruptibleHolyLance, IncorruptibleHolyLance, IncorruptibleHolyLance, NecromancerBackBouncer, NecromancerBackBouncer, NecromancerBackBouncer, BigBanisherBurner, BigBanisherBurner, BigBanisherBurner, BigBurnerSuicideCommando, BigBurnerSuicideCommando, BigBurnerSuicideCommando, FlakshipNapalm, FlakshipNapalm, FlakshipNapalm);
        StandardDeck8 = new Deck (39, true, EradicatorObstacle, EradicatorObstacle, EradicatorObstacle, NeutraliserSkillStealer, NeutraliserSkillStealer, NeutraliserSkillStealer, AttackStopperBuggedUpgrade, AttackStopperBuggedUpgrade, AttackStopperBuggedUpgrade, BigAttackStopperSword, BigAttackStopperSword, BigAttackStopperSword, DiamondSwordShield, DiamondSwordShield, DiamondSwordShield, SlickRusherRecklessRusher, SlickRusherRecklessRusher, SlickRusherRecklessRusher, IncorruptibleHolyLance, IncorruptibleHolyLance, IncorruptibleHolyLance, NecromancerBackBouncer, NecromancerBackBouncer, NecromancerBackBouncer, BigBackBouncerBanisher, BigBackBouncerBanisher, BigBackBouncerBanisher, BigBanisherBurner, BigBanisherBurner, BigBanisherBurner, BigBurnerSuicideCommando, BigBurnerSuicideCommando, BigBurnerSuicideCommando, FlakshipNapalm, FlakshipNapalm, FlakshipNapalm, DemonGodDemon, DemonGodDemon, DemonGodDemon);
        StandardDeck9 = new Deck (39, true, GodBarrier, GodBarrier, EradicatorObstacle, EradicatorObstacle, EradicatorObstacle, GodKillingSpearLance, GodKillingSpearLance, MonsterStealerSteepLearningCurve, MonsterStealerSteepLearningCurve, NeutraliserSkillStealer, NeutraliserSkillStealer, NeutraliserSkillStealer, CopyCatCardGrabber, CopyCatCardGrabber, CopyCatCardGrabber, AttackStopperBuggedUpgrade, AttackStopperBuggedUpgrade, BigAttackStopperSword, BigAttackStopperSword, DiamondSwordShield, DiamondSwordShield, ModeChangerExhaustedExecutioner, ModeChangerExhaustedExecutioner, SlickRusherRecklessRusher, SlickRusherRecklessRusher, IncorruptibleHolyLance, IncorruptibleHolyLance, NecromancerBackBouncer, NecromancerBackBouncer, BigBackBouncerBanisher, BigBackBouncerBanisher, BigBanisherBurner, BigBanisherBurner, BigBurnerSuicideCommando, BigBurnerSuicideCommando, FlakshipNapalm, FlakshipNapalm, DemonGodDemon, DemonGodDemon);
        StandardDeck10 = new Deck (39, true, EradicatorObstacle, EradicatorObstacle, EradicatorObstacle, MonsterStealerSteepLearningCurve, MonsterStealerSteepLearningCurve, MonsterStealerSteepLearningCurve, NeutraliserSkillStealer, NeutraliserSkillStealer, NeutraliserSkillStealer, CopyCatCardGrabber, CopyCatCardGrabber, CopyCatCardGrabber, ModeChangerExhaustedExecutioner, ModeChangerExhaustedExecutioner, ModeChangerExhaustedExecutioner, AttackStopperBuggedUpgrade, AttackStopperBuggedUpgrade, AttackStopperBuggedUpgrade, BigAttackStopperSword, BigAttackStopperSword, BigAttackStopperSword, DiamondSwordShield, DiamondSwordShield, DiamondSwordShield, SlickRusherRecklessRusher, SlickRusherRecklessRusher, SlickRusherRecklessRusher, NecromancerBackBouncer, NecromancerBackBouncer, NecromancerBackBouncer, BigBackBouncerBanisher, BigBackBouncerBanisher, BigBackBouncerBanisher, BigBanisherBurner, BigBanisherBurner, BigBanisherBurner, BigBurnerSuicideCommando, BigBurnerSuicideCommando, BigBurnerSuicideCommando);
        
        DeckCPU = new Deck(false);
        DeckPlayer = new Deck(false);
        
        // whole panel added to this class (the frame)
        this.add(panel);
        
    }
    
    // --- methods ---
    
    public static void main(String[] args) {
        // create main window
        windowl = new YuGiOhJi();
        windowl.setVisible(true);
        windowl.setExtendedState(YuGiOhJi.MAXIMIZED_BOTH); // maximize frame
    }
    
    @Override
    public void componentHidden(ComponentEvent ce) {};
    @Override
    public void componentShown(ComponentEvent ce) {};
    @Override
    public void componentMoved(ComponentEvent ce) {};
    @Override
    public void componentResized(ComponentEvent ce) {
        currentFrameWidth = this.getWidth();
        currentFrameHeight = this.getHeight();
        rescaleEverything();
    };
    
    // calculates rescaling of horizontal coordinates (and widths)
    public static int rescaleX (int x) {
        int newValue = (int) Math.floor(x*currentFrameWidth/initialFrameWidth);
        return newValue;
    }
    
    // calculates rescaling of vertical coordinates (and heights)
    public static int rescaleY (int y) {
        int newValue = (int) Math.floor(y*currentFrameHeight/initialFrameHeight);
        return newValue;
    }
    
    // rescales a given button (uses current window size saved by global variables)
    public static void rescaleButton (JButton buttonName, int intialPosX, int intialPosY, int intialButtonWidth, int intialButtonHeight) {
        buttonName.setBounds(rescaleX(intialPosX), rescaleY(intialPosY), rescaleX(intialButtonWidth), rescaleY(intialButtonHeight));
    }
    
    // rescales a given label (uses current window size saved by global variables)
    // important: rescaling of the boundary does not change the font size!
    public static void rescaleLabel(JLabel labelName, int intialPosX, int intialPosY, int intialLabelWidth, int intialLabelHeight) {
        labelName.setBounds(rescaleX(intialPosX), rescaleY(intialPosY), rescaleX(intialLabelWidth), rescaleY(intialLabelHeight));
    }
    
    // simply rescales all graphical components, i.e. all buttons and labels, one by one
    // Text is only changes after it updated in the game.
    // Thus, ideally the player rescales/maximizes the frame at the beginning of the game and then keeps it like that.
    public static void rescaleEverything() {
        // rescale all labels
        rescaleLabel(field, 0, 0, 1200, 700);
        rescaleLabel(preview, 0, 0, 400, 600);
        rescaleLabel(displayNumberCardsDeckCPU, 467, 0, 33, 100);
        rescaleLabel(displayNumberCardsGYCPU, 467, 100, 33, 100);
        rescaleLabel(displayNumberCardsDDCPU, 467, 200, 33, 100);
        rescaleLabel(displayCurrentPhase, 400, 300, 100, 100);
        rescaleLabel(displayNumberCardsDDPlayer, 467, 400, 33, 100);
        rescaleLabel(displayNumberCardsGYPlayer, 467, 500, 33, 100);
        rescaleLabel(displayNumberCardsDeckPlayer, 467, 600, 33, 100);
        rescaleLabel(displayAttDefMonster1CPU, 516, 300, 100, 25);
        rescaleLabel(displayAttDefMonster2CPU, 616, 300, 100, 25);
        rescaleLabel(displayAttDefMonster3CPU, 716, 300, 100, 25);
        rescaleLabel(displayAttDefMonster4CPU, 816, 300, 100, 25);
        rescaleLabel(displayAttDefMonster5CPU, 916, 300, 100, 25);
        rescaleLabel(displayPiercingStatement, 500, 325, 200, 50);
        rescaleLabel(displayAttDefMonster1Player, 516, 375, 100, 25);
        rescaleLabel(displayAttDefMonster2Player, 616, 375, 100, 25);
        rescaleLabel(displayAttDefMonster3Player, 716, 375, 100, 25);
        rescaleLabel(displayAttDefMonster4Player, 816, 375, 100, 25);
        rescaleLabel(displayAttDefMonster5Player, 916, 375, 100, 25);
        rescaleLabel(displayBodyAsShieldRule, 1010, 100, 180, 50);
        rescaleLabel(displayCheatChangeRule, 1010, 140, 180, 50);
        rescaleLabel(displayOncePerTurnRule, 1010, 180, 180, 50);
        rescaleLabel(displayAntiDragonRulerRule, 1010, 220, 180, 50);
        rescaleLabel(displayLPCPU, 1000, 250, 150, 50);
        rescaleLabel(displayLPPlayer, 1000, 400, 150, 50);
        rescaleLabel(displaySkillCopyStatement, 1010, 450, 150, 150);
        rescaleLabel(displayNegationStatement, 1010, 450, 150, 100);
        rescaleLabel(displayNumberOfEquipCards1CPU, 585, 100, 15, 100);
        rescaleLabel(displayNumberOfEquipCards2CPU, 685, 100, 15, 100);
        rescaleLabel(displayNumberOfEquipCards3CPU, 785, 100, 15, 100);
        rescaleLabel(displayNumberOfEquipCards4CPU, 885, 100, 15, 100);
        rescaleLabel(displayNumberOfEquipCards5CPU, 985, 100, 15, 100);
        rescaleLabel(displayNumberOfEquipCards1Player, 585, 500, 15, 100);
        rescaleLabel(displayNumberOfEquipCards2Player, 685, 500, 15, 100);
        rescaleLabel(displayNumberOfEquipCards3Player, 785, 500, 15, 100);
        rescaleLabel(displayNumberOfEquipCards4Player, 885, 500, 15, 100);
        rescaleLabel(displayNumberOfEquipCards5Player, 985, 500, 15, 100);
        // rescale all buttons
        rescaleButton(buttonStart, 0, 600, 200, 30);
        rescaleButton(buttonDeckBuilding, 200, 600, 200, 30);
        rescaleButton(buttonCPUsetup, 0, 630, 200, 30);
        rescaleButton(buttonGiveUp, 200, 630, 200, 30);
        rescaleButton(buttonMods, 0, 660, 200, 30);
        rescaleButton(buttonGameSetup, 200, 660, 100, 30);
        rescaleButton(buttonInfo, 300, 660, 100, 30);
        rescaleButton(buttonDeckCPU, 400, 0, 67, 100);
        rescaleButton(buttonDeckCPU, 400, 0, 67, 100);
        rescaleButton(buttonGYCPU, 400, 100, 67, 100);
        rescaleButton(buttonDDCPU, 400, 200, 67, 100);
        rescaleButton(buttonDDPlayer, 400, 400, 67, 100);
        rescaleButton(buttonGYPlayer, 400, 500, 67, 100);
        rescaleButton(buttonDeckPlayer, 400, 600, 67, 100);
        rescaleButton(buttonNextPhase, 1000, 325, 180, 75);
        rescaleButton(buttonNextTurn, 1000, 300, 180, 25);
        rescaleButton(buttonEquip1CPU, 516, 100, 67, 100);
        rescaleButton(buttonEquip2CPU, 616, 100, 67, 100);
        rescaleButton(buttonEquip3CPU, 716, 100, 67, 100);
        rescaleButton(buttonEquip4CPU, 816, 100, 67, 100);
        rescaleButton(buttonEquip5CPU, 916, 100, 67, 100);
        if (SummonedMonster1CPU.isInAttackMode) {rescaleButton(buttonMonster1CPU, 516, 200, 67, 100);}
        else {rescaleButton(buttonMonster1CPU, 500, 216, 100, 67);}
        if (SummonedMonster2CPU.isInAttackMode) {rescaleButton(buttonMonster2CPU, 616, 200, 67, 100);}
        else {rescaleButton(buttonMonster2CPU, 600, 216, 100, 67);}
        if (SummonedMonster3CPU.isInAttackMode) {rescaleButton(buttonMonster3CPU, 716, 200, 67, 100);}
        else {rescaleButton(buttonMonster3CPU, 700, 216, 100, 67);}
        if (SummonedMonster4CPU.isInAttackMode) {rescaleButton(buttonMonster4CPU, 816, 200, 67, 100);}
        else {rescaleButton(buttonMonster4CPU, 800, 216, 100, 67);}
        if (SummonedMonster5CPU.isInAttackMode) {rescaleButton(buttonMonster5CPU, 916, 200, 67, 100);}
        else {rescaleButton(buttonMonster5CPU, 900, 216, 100, 67);}
        if (SummonedMonster1Player.isInAttackMode) {rescaleButton(buttonMonster1Player, 516, 400, 67, 100);}
        else {rescaleButton(buttonMonster1Player, 500, 416, 100, 67);}
        if (SummonedMonster2Player.isInAttackMode) {rescaleButton(buttonMonster2Player, 616, 400, 67, 100);}
        else {rescaleButton(buttonMonster2Player, 600, 416, 100, 67);}
        if (SummonedMonster3Player.isInAttackMode) {rescaleButton(buttonMonster3Player, 716, 400, 67, 100);}
        else {rescaleButton(buttonMonster3Player, 700, 416, 100, 67);}
        if (SummonedMonster4Player.isInAttackMode) {rescaleButton(buttonMonster4Player, 816, 400, 67, 100);}
        else {rescaleButton(buttonMonster4Player, 800, 416, 100, 67);}
        if (SummonedMonster5Player.isInAttackMode) {rescaleButton(buttonMonster5Player, 916, 400, 67, 100);}
        else {rescaleButton(buttonMonster5Player, 900, 416, 100, 67);}
        rescaleButton(buttonEquip1Player, 516, 500, 67, 100);
        rescaleButton(buttonEquip2Player, 616, 500, 67, 100);
        rescaleButton(buttonEquip3Player, 716, 500, 67, 100);
        rescaleButton(buttonEquip4Player, 816, 500, 67, 100);
        rescaleButton(buttonEquip5Player, 916, 500, 67, 100);
        rescaleButton(buttonCard1CPU, 500, 0, 67, 100);
        rescaleButton(buttonCard2CPU, 567, 0, 67, 100);
        rescaleButton(buttonCard3CPU, 634, 0, 67, 100);
        rescaleButton(buttonCard4CPU, 700, 0, 67, 100);
        rescaleButton(buttonCard5CPU, 767, 0, 67, 100);
        rescaleButton(buttonCard6CPU, 834, 0, 67, 100);
        rescaleButton(buttonCard7CPU, 900, 0, 67, 100);
        rescaleButton(buttonCard8CPU, 967, 0, 67, 100);
        rescaleButton(buttonCard9CPU, 1034, 0, 67, 100);
        rescaleButton(buttonCard10CPU, 1100, 0, 67, 100);
        rescaleButton(buttonCard1Player, 500, 600, 67, 100);
        rescaleButton(buttonCard2Player, 567, 600, 67, 100);
        rescaleButton(buttonCard3Player, 634, 600, 67, 100);
        rescaleButton(buttonCard4Player, 700, 600, 67, 100);
        rescaleButton(buttonCard5Player, 767, 600, 67, 100);
        rescaleButton(buttonCard6Player, 834, 600, 67, 100);
        rescaleButton(buttonCard7Player, 900, 600, 67, 100);
        rescaleButton(buttonCard8Player, 967, 600, 67, 100);
        rescaleButton(buttonCard9Player, 1034, 600, 67, 100);
        rescaleButton(buttonCard10Player, 1100, 600, 67, 100);
        // rescale font size of text (only if switched on)
        if (Game.isSwitchingOnRescalingFontsize) {
            updateDisplayedText();
        }
    }
    
    // resets many variables/objects for preparing a new game
    public static void resetEverything(){
        // clear hands
        HandCPU = new Hand(false);
        HandPlayer = new Hand(true);
        
        // make hands invisible
        setCardButtonIcon("/images/YuGiOhJiFacedown.png", 1, true, false);
        setCardButtonIcon("/images/YuGiOhJiFacedown.png", 2, true, false);
        setCardButtonIcon("/images/YuGiOhJiFacedown.png", 3, true, false);
        setCardButtonIcon("/images/YuGiOhJiFacedown.png", 4, true, false);
        setCardButtonIcon("/images/YuGiOhJiFacedown.png", 5, true, false);
        setCardButtonIcon("/images/YuGiOhJiFacedown.png", 6, true, false);
        setCardButtonIcon("/images/YuGiOhJiFacedown.png", 7, true, false);
        setCardButtonIcon("/images/YuGiOhJiFacedown.png", 8, true, false);
        setCardButtonIcon("/images/YuGiOhJiFacedown.png", 9, true, false);
        setCardButtonIcon("/images/YuGiOhJiFacedown.png", 10, true, false);
        setCardButtonIcon("/images/YuGiOhJiFacedown.png", 1, false, false);
        setCardButtonIcon("/images/YuGiOhJiFacedown.png", 2, false, false);
        setCardButtonIcon("/images/YuGiOhJiFacedown.png", 3, false, false);
        setCardButtonIcon("/images/YuGiOhJiFacedown.png", 4, false, false);
        setCardButtonIcon("/images/YuGiOhJiFacedown.png", 5, false, false);
        setCardButtonIcon("/images/YuGiOhJiFacedown.png", 6, false, false);
        setCardButtonIcon("/images/YuGiOhJiFacedown.png", 7, false, false);
        setCardButtonIcon("/images/YuGiOhJiFacedown.png", 8, false, false);
        setCardButtonIcon("/images/YuGiOhJiFacedown.png", 9, false, false);
        setCardButtonIcon("/images/YuGiOhJiFacedown.png", 10, false, false);
        
        Deck.resetCPUdeck(); // CPU might choose different deck => unpredictable => more fun
        
        // shuffling both decks
        DeckCPU = UnshuffledDeckCPU.shuffleDeck();
        DeckPlayer = UnshuffledDeckPlayer.shuffleDeck();
        
        // clear graveyards
        GYDeckCPU=new Deck(false);
        DDDeckCPU=new Deck(false);
        DDDeckPlayer=new Deck(true);
        GYDeckPlayer=new Deck(true);
        
        // make decks visible, graveyards invisible
        setDeckButtonIcon("/images/YuGiOhJiFacedown.png", 1, true);
        setDeckButtonIcon("/images/YuGiOhJiFacedown.png", 2, false);
        setDeckButtonIcon("/images/YuGiOhJiFacedown.png", 3, false);
        setDeckButtonIcon("/images/YuGiOhJiFacedown.png", 4, false);
        setDeckButtonIcon("/images/YuGiOhJiFacedown.png", 5, false);
        setDeckButtonIcon("/images/YuGiOhJiFacedown.png", 6, true);
        
        // clear rest of field
        SummonedMonster1CPU = new SummonedMonster(false, 1);
        SummonedMonster2CPU = new SummonedMonster(false, 2);
        SummonedMonster3CPU = new SummonedMonster(false, 3);
        SummonedMonster4CPU = new SummonedMonster(false, 4);
        SummonedMonster5CPU = new SummonedMonster(false, 5);
        SummonedMonster1Player = new SummonedMonster(true, 1);
        SummonedMonster2Player = new SummonedMonster(true, 2);
        SummonedMonster3Player = new SummonedMonster(true, 3);
        SummonedMonster4Player = new SummonedMonster(true, 4);
        SummonedMonster5Player = new SummonedMonster(true, 5);
        // make summoned monsters invisible
        setMonsterButtonIcon("/images/YuGiOhJiFacedown.png", 1, false, true, false);
        setMonsterButtonIcon("/images/YuGiOhJiFacedown.png", 2, false, true, false);
        setMonsterButtonIcon("/images/YuGiOhJiFacedown.png", 3, false, true, false);
        setMonsterButtonIcon("/images/YuGiOhJiFacedown.png", 4, false, true, false);
        setMonsterButtonIcon("/images/YuGiOhJiFacedown.png", 5, false, true, false);
        setMonsterButtonIcon("/images/YuGiOhJiFacedown.png", 1, true, true, false);
        setMonsterButtonIcon("/images/YuGiOhJiFacedown.png", 2, true, true, false);
        setMonsterButtonIcon("/images/YuGiOhJiFacedown.png", 3, true, true, false);
        setMonsterButtonIcon("/images/YuGiOhJiFacedown.png", 4, true, true, false);
        setMonsterButtonIcon("/images/YuGiOhJiFacedown.png", 5, true, true, false);
        // lists of the equipped card stacks (only 10 equip cards possible, but 10 are very unlikely, since usually max. 3x(Sword+Shield)+Lance+BuggedUprade))
        EquipStack1CPU = new EStack(false, 1);
        EquipStack2CPU = new EStack(false, 2);
        EquipStack3CPU = new EStack(false, 3);
        EquipStack4CPU = new EStack(false, 4);
        EquipStack5CPU = new EStack(false, 5);
        EquipStack1Player = new EStack(true, 1);
        EquipStack2Player = new EStack(true, 2);
        EquipStack3Player = new EStack(true, 3);
        EquipStack4Player = new EStack(true, 4);
        EquipStack5Player = new EStack(true, 5);
        
        // make equip cards invisible
        setEquipButtonIcon("/images/YuGiOhJiFacedown.png", 1, false, false);
        setEquipButtonIcon("/images/YuGiOhJiFacedown.png", 2, false, false);
        setEquipButtonIcon("/images/YuGiOhJiFacedown.png", 3, false, false);
        setEquipButtonIcon("/images/YuGiOhJiFacedown.png", 4, false, false);
        setEquipButtonIcon("/images/YuGiOhJiFacedown.png", 5, false, false);
        setEquipButtonIcon("/images/YuGiOhJiFacedown.png", 1, true, false);
        setEquipButtonIcon("/images/YuGiOhJiFacedown.png", 2, true, false);
        setEquipButtonIcon("/images/YuGiOhJiFacedown.png", 3, true, false);
        setEquipButtonIcon("/images/YuGiOhJiFacedown.png", 4, true, false);
        setEquipButtonIcon("/images/YuGiOhJiFacedown.png", 5, true, false);
        
        Game.resetParametersWhenStartingNewGame();
        Game.updateVisibilityOfRules(); // make rules visible
        updateDisplayedText(); // reset rest of the labels here
    }
    
    // updates the text displayed on various places on the screen (also rescales the text)
    public static void updateDisplayedText(){
        updateDisplayedCardNumbers();
        updateDisplayedPhase();
        updateDisplayedEquipCardNumbers();
        updateDisplayedLP();
        updateDisplayedAttDefValues();
        if (Game.isSwitchingOnRescalingFontsize) {
            rescaleButtonTexts();
        }
    }
    
    public static void updateDisplayedAttDefValues(){
        SummonedMonster1CPU.updateAttDefDisplay();
        SummonedMonster2CPU.updateAttDefDisplay();
        SummonedMonster3CPU.updateAttDefDisplay();
        SummonedMonster4CPU.updateAttDefDisplay();
        SummonedMonster5CPU.updateAttDefDisplay();
        SummonedMonster1Player.updateAttDefDisplay();
        SummonedMonster2Player.updateAttDefDisplay();
        SummonedMonster3Player.updateAttDefDisplay();
        SummonedMonster4Player.updateAttDefDisplay();
        SummonedMonster5Player.updateAttDefDisplay();
    }
    
    public static void rescaleButtonTexts(){
        int fontsize = (int) (100*currentFrameHeight/initialFrameHeight);
        buttonStart.setText("<html><body><span style=\"font-size:" + fontsize + "%\">start/reset</span></body></html>");
        buttonDeckBuilding.setText("<html><body><span style=\"font-size:" + fontsize + "%\">deck building</span></body></html>");
        buttonCPUsetup.setText("<html><body><span style=\"font-size:" + fontsize + "%\">CPU setup</span></body></html>");
        buttonGiveUp.setText("<html><body><span style=\"font-size:" + fontsize + "%\">give up / end match</span></body></html>");
        buttonMods.setText("<html><body><span style=\"font-size:" + fontsize + "%\">mods</span></body></html>");
        buttonGameSetup.setText("<html><body><span style=\"font-size:" + fontsize + "%\">game setup</span></body></html>");
        buttonInfo.setText("<html><body><span style=\"font-size:" + fontsize + "%\">info</span></body></html>");
        buttonNextPhase.setText("<html><body><span style=\"font-size:" + fontsize + "%\">next phase</span></body></html>");
        buttonNextTurn.setText("<html><body><span style=\"font-size:" + fontsize + "%\">next turn</span></body></html>");
    }
    
    // sets the displayed numbers of cards to their current values
    public static void updateDisplayedCardNumbers(){
        int fontsize=100;
        if (Game.isSwitchingOnRescalingFontsize) {fontsize = (int) (100*currentFrameHeight/initialFrameHeight);}
        displayNumberCardsDeckCPU.setText("<html><body>&nbsp;<span style=\"font-size:" + fontsize + "%\"> " + DeckCPU.numberOfCards + "</span></body></html>");
        displayNumberCardsGYCPU.setText("<html><body>&nbsp;<span style=\"font-size:" + fontsize + "%\"> " + GYDeckCPU.numberOfCards + "</span></body></html>");
        displayNumberCardsDDCPU.setText("<html><body>&nbsp;<span style=\"font-size:" + fontsize + "%\"> " + DDDeckCPU.numberOfCards + "</span></body></html>");
        displayNumberCardsDDPlayer.setText("<html><body>&nbsp;<span style=\"font-size:" + fontsize + "%\"> " + DDDeckPlayer.numberOfCards + "</span></body></html>");
        displayNumberCardsGYPlayer.setText("<html><body>&nbsp;<span style=\"font-size:" + fontsize + "%\"> " + GYDeckPlayer.numberOfCards + "</span></body></html>");
        displayNumberCardsDeckPlayer.setText("<html><body>&nbsp;<span style=\"font-size:" + fontsize + "%\"> " + DeckPlayer.numberOfCards + "</span></body></html>");
    }
    
    public static void updateDisplayedPhase(){
        int fontsize=100;
        if (Game.isSwitchingOnRescalingFontsize) {fontsize = (int) (100*currentFrameHeight/initialFrameHeight);}
        if (Game.isMainPhase1()) {
            displayCurrentPhase.setText("<html><body>&nbsp;<span style=\"font-size:" + fontsize + "%\"> main phase 1</span></body></html>");
        }
        else if (Game.isBattlePhase()) {
            displayCurrentPhase.setText("<html><body>&nbsp;<span style=\"font-size:" + fontsize + "%\"> battle phase</span></body></html>");
        }
        else if (Game.isMainPhase2()) {
            displayCurrentPhase.setText("<html><body>&nbsp;<span style=\"font-size:" + fontsize + "%\"> main phase 2</span></body></html>");
        }
    }
    
    // sets the displayed numbers of equip cards to their current values
    public static void updateDisplayedEquipCardNumbers(){
        int fontsize=100;
        if (Game.isSwitchingOnRescalingFontsize) {fontsize = (int) (100*currentFrameHeight/initialFrameHeight);}
        displayNumberOfEquipCards1CPU.setText("<html><body>&nbsp;<span style=\"font-size:" + fontsize + "%\"> " + EquipStack1CPU.numberOfCards + "</span></body></html>"); displayNumberOfEquipCards1CPU.setVisible(EquipStack1CPU.numberOfCards>=2);
        displayNumberOfEquipCards2CPU.setText("<html><body>&nbsp;<span style=\"font-size:" + fontsize + "%\"> " + EquipStack2CPU.numberOfCards + "</span></body></html>"); displayNumberOfEquipCards2CPU.setVisible(EquipStack2CPU.numberOfCards>=2);
        displayNumberOfEquipCards3CPU.setText("<html><body>&nbsp;<span style=\"font-size:" + fontsize + "%\"> " + EquipStack3CPU.numberOfCards + "</span></body></html>"); displayNumberOfEquipCards3CPU.setVisible(EquipStack3CPU.numberOfCards>=2);
        displayNumberOfEquipCards4CPU.setText("<html><body>&nbsp;<span style=\"font-size:" + fontsize + "%\"> " + EquipStack4CPU.numberOfCards + "</span></body></html>"); displayNumberOfEquipCards4CPU.setVisible(EquipStack4CPU.numberOfCards>=2);
        displayNumberOfEquipCards5CPU.setText("<html><body>&nbsp;<span style=\"font-size:" + fontsize + "%\"> " + EquipStack5CPU.numberOfCards + "</span></body></html>"); displayNumberOfEquipCards5CPU.setVisible(EquipStack5CPU.numberOfCards>=2);
        displayNumberOfEquipCards1Player.setText("<html><body>&nbsp;<span style=\"font-size:" + fontsize + "%\"> " + EquipStack1Player.numberOfCards + "</span></body></html>"); displayNumberOfEquipCards1Player.setVisible(EquipStack1Player.numberOfCards>=2);
        displayNumberOfEquipCards2Player.setText("<html><body>&nbsp;<span style=\"font-size:" + fontsize + "%\"> " + EquipStack2Player.numberOfCards + "</span></body></html>"); displayNumberOfEquipCards2Player.setVisible(EquipStack2Player.numberOfCards>=2);
        displayNumberOfEquipCards3Player.setText("<html><body>&nbsp;<span style=\"font-size:" + fontsize + "%\"> " + EquipStack3Player.numberOfCards + "</span></body></html>"); displayNumberOfEquipCards3Player.setVisible(EquipStack3Player.numberOfCards>=2);
        displayNumberOfEquipCards4Player.setText("<html><body>&nbsp;<span style=\"font-size:" + fontsize + "%\"> " + EquipStack4Player.numberOfCards + "</span></body></html>"); displayNumberOfEquipCards4Player.setVisible(EquipStack4Player.numberOfCards>=2);
        displayNumberOfEquipCards5Player.setText("<html><body>&nbsp;<span style=\"font-size:" + fontsize + "%\"> " + EquipStack5Player.numberOfCards + "</span></body></html>"); displayNumberOfEquipCards5Player.setVisible(EquipStack5Player.numberOfCards>=2);
    }
    
    // sets the displayed life points of both players to their current values (also displays the "health bar")
    public static void updateDisplayedLP(){
        int fontsize=100;
        if (Game.isSwitchingOnRescalingFontsize) {fontsize = (int) (100*currentFrameHeight/initialFrameHeight);}
        displayLPCPU.setText("<html><body>&nbsp;<span style=\"font-size:" + fontsize + "%\"> LP: <span color=\"" + lifepointColor(Game.lifePointsCPU) + "\">" + Game.lifePointsCPU + "</span><br>"
                + "&nbsp;[<span color=\"" + lifepointColor(Game.lifePointsCPU) + "\">" + healthbar(Game.lifePointsCPU, false) + "</span>]</span></body></html>");
        displayLPPlayer.setText("<html><body>&nbsp;<span style=\"font-size:" + fontsize + "%\">  LP: <span color=\"" + lifepointColor(Game.lifePointsPlayer) + "\">" + Game.lifePointsPlayer + "</span><br>" 
                + "&nbsp;[<span color=\"" + lifepointColor(Game.lifePointsPlayer) + "\">" + healthbar(Game.lifePointsPlayer, true) + "</span>]</span></body></html>");
    }
    
    // returns the color in which a given life point is displayed as a string
    public static String lifepointColor (int lifepoints){
        if (lifepoints >= 7*Game.initialLP/8) {return "green";}
        else if (lifepoints < 7*Game.initialLP/8 && lifepoints >= Game.initialLP/2) {return "#808000";} // this is some kind of brown-green
        else if (lifepoints < Game.initialLP/2 && lifepoints > Game.initialLP/8) {return "orange";}
        else {return "red";}
    }
    
    // for better visualisation of health use a string that says whose player's life points it is and let the 16 characters therein disappear (replaced by underscores)
    // since all attack and burn damage is an interger multiple of 500, one has with 8000 life points exactly 16 health units
    // (some people like numbers, some people like symbols, some people like bars: here we have all)
    public static String healthbar (int lifepoints, boolean isPlayerLP){
        int singleUnit = (int) Math.round((double) Game.initialLP/16);
        int healthunits = (int) Math.round((double) lifepoints/singleUnit);
        switch (healthunits) {
            default: return "________________";
            case 1: if (isPlayerLP) {return "H_______________";} else {return "H_______________";}
            case 2: if (isPlayerLP) {return "HE______________";} else {return "HE______________";}
            case 3: if (isPlayerLP) {return "HEA_____________";} else {return "HEA_____________";}
            case 4: if (isPlayerLP) {return "HEAL____________";} else {return "HEAL____________";}
            case 5: if (isPlayerLP) {return "HEALT___________";} else {return "HEALT___________";}
            case 6: if (isPlayerLP) {return "HEALTH__________";} else {return "HEALTH__________";}
            case 7: if (isPlayerLP) {return "HEALTHB_________";} else {return "HEALTH*_________";}
            case 8: if (isPlayerLP) {return "HEALTHBA________";} else {return "HEALTH**________";}
            case 9: if (isPlayerLP) {return "HEALTHBAR_______";} else {return "HEALTH**C_______";}
            case 10: if (isPlayerLP) {return "HEALTHBAR*______";} else {return "HEALTH**CO______";}
            case 11: if (isPlayerLP) {return "HEALTHBAR*P_____";} else {return "HEALTH**COM_____";}
            case 12: if (isPlayerLP) {return "HEALTHBAR*PL____";} else {return "HEALTH**COMP____";}
            case 13: if (isPlayerLP) {return "HEALTHBAR*PLA___";} else {return "HEALTH**COMPU___";}
            case 14: if (isPlayerLP) {return "HEALTHBAR*PLAY__";} else {return "HEALTH**COMPUT__";}
            case 15: if (isPlayerLP) {return "HEALTHBAR*PLAYE_";} else {return "HEALTH**COMPUTE_";}
            case 16: if (isPlayerLP) {return "HEALTHBAR*PLAYER";} else {return "HEALTH**COMPUTER";}
        }
    }
    
    // changes the appearance of a card in the hand of player/CPU
    public static void setCardButtonIcon (String imagePath, int n, boolean isBelongingToPlayer, boolean visibility){
        if (isBelongingToPlayer) {
            switch (n) {
                case 1: buttonCard1Player.setIcon(new RescaledIcon(YuGiOhJi.class.getResource(imagePath))); buttonCard1Player.setVisible(visibility); break;
                case 2: buttonCard2Player.setIcon(new RescaledIcon(YuGiOhJi.class.getResource(imagePath))); buttonCard2Player.setVisible(visibility); break;
                case 3: buttonCard3Player.setIcon(new RescaledIcon(YuGiOhJi.class.getResource(imagePath))); buttonCard3Player.setVisible(visibility); break;
                case 4: buttonCard4Player.setIcon(new RescaledIcon(YuGiOhJi.class.getResource(imagePath))); buttonCard4Player.setVisible(visibility); break;
                case 5: buttonCard5Player.setIcon(new RescaledIcon(YuGiOhJi.class.getResource(imagePath))); buttonCard5Player.setVisible(visibility); break;
                case 6: buttonCard6Player.setIcon(new RescaledIcon(YuGiOhJi.class.getResource(imagePath))); buttonCard6Player.setVisible(visibility); break;
                case 7: buttonCard7Player.setIcon(new RescaledIcon(YuGiOhJi.class.getResource(imagePath))); buttonCard7Player.setVisible(visibility); break;
                case 8: buttonCard8Player.setIcon(new RescaledIcon(YuGiOhJi.class.getResource(imagePath))); buttonCard8Player.setVisible(visibility); break;
                case 9: buttonCard9Player.setIcon(new RescaledIcon(YuGiOhJi.class.getResource(imagePath))); buttonCard9Player.setVisible(visibility); break;
                case 10: buttonCard10Player.setIcon(new RescaledIcon(YuGiOhJi.class.getResource(imagePath))); buttonCard10Player.setVisible(visibility); break;
                default: debugDialog("Error: out of bounds in setCardButtonIcon(...); attempted card: " + n); break;
            }
        }
        else {
            switch (n) {
                case 1: buttonCard1CPU.setIcon(new RescaledIcon(YuGiOhJi.class.getResource(imagePath))); buttonCard1CPU.setVisible(visibility); break;
                case 2: buttonCard2CPU.setIcon(new RescaledIcon(YuGiOhJi.class.getResource(imagePath))); buttonCard2CPU.setVisible(visibility); break;
                case 3: buttonCard3CPU.setIcon(new RescaledIcon(YuGiOhJi.class.getResource(imagePath))); buttonCard3CPU.setVisible(visibility); break;
                case 4: buttonCard4CPU.setIcon(new RescaledIcon(YuGiOhJi.class.getResource(imagePath))); buttonCard4CPU.setVisible(visibility); break;
                case 5: buttonCard5CPU.setIcon(new RescaledIcon(YuGiOhJi.class.getResource(imagePath))); buttonCard5CPU.setVisible(visibility); break;
                case 6: buttonCard6CPU.setIcon(new RescaledIcon(YuGiOhJi.class.getResource(imagePath))); buttonCard6CPU.setVisible(visibility); break;
                case 7: buttonCard7CPU.setIcon(new RescaledIcon(YuGiOhJi.class.getResource(imagePath))); buttonCard7CPU.setVisible(visibility); break;
                case 8: buttonCard8CPU.setIcon(new RescaledIcon(YuGiOhJi.class.getResource(imagePath))); buttonCard8CPU.setVisible(visibility); break;
                case 9: buttonCard9CPU.setIcon(new RescaledIcon(YuGiOhJi.class.getResource(imagePath))); buttonCard9CPU.setVisible(visibility); break;
                case 10: buttonCard10CPU.setIcon(new RescaledIcon(YuGiOhJi.class.getResource(imagePath))); buttonCard10CPU.setVisible(visibility); break;
                default: debugDialog("Error: out of bounds in setCardButtonIcon(...); attempted card: " + n); break;
            }
        }
        rescaleEverything();
    }
    
    // changes the appearance of graveyard, different dimension and deck button icons
    public static void setDeckButtonIcon (String imagePath, int n, boolean visibility){
        switch (n) {
            case 1: buttonDeckCPU.setIcon(new RescaledIcon(YuGiOhJi.class.getResource(imagePath))); buttonDeckCPU.setVisible(visibility); break;
            case 2: buttonGYCPU.setIcon(new RescaledIcon(YuGiOhJi.class.getResource(imagePath))); buttonGYCPU.setVisible(visibility); break;
            case 3: buttonDDCPU.setIcon(new RescaledIcon(YuGiOhJi.class.getResource(imagePath))); buttonDDCPU.setVisible(visibility); break;
            case 4: buttonDDPlayer.setIcon(new RescaledIcon(YuGiOhJi.class.getResource(imagePath))); buttonDDPlayer.setVisible(visibility); break;
            case 5: buttonGYPlayer.setIcon(new RescaledIcon(YuGiOhJi.class.getResource(imagePath))); buttonGYPlayer.setVisible(visibility); break;
            case 6: buttonDeckPlayer.setIcon(new RescaledIcon(YuGiOhJi.class.getResource(imagePath))); buttonDeckPlayer.setVisible(visibility); break;
            default: debugDialog("Error: out of bounds in setDeckButtonIcon(...); attempted button: " + n); break;
        }
        rescaleEverything();
    }
    
    // for setting button properties using fewer lines (for readability)
    public static void setButtonProperties (JButton buttonName, int x, int y, int width, int height, boolean visibility){
        // e.g. use like: setButtonProperties(buttonCard1Player, 50, 450, 100, 200);
        buttonName.setBounds(x, y, width, height);
        buttonName.setBorder(BorderFactory.createEmptyBorder());
        buttonName.setContentAreaFilled(false);
        buttonName.setFocusable(false);
        buttonName.setVisible(visibility);
    }
    
    // changes the appearance of monsters on the field
    public static void setMonsterButtonIcon (String imagePath, int n, boolean isBelongingToPlayer, boolean isInAttMode, boolean visibility){
        if (isBelongingToPlayer) {
            switch (n) {
                case 1:
                    if (isInAttMode) {setButtonProperties(buttonMonster1Player, 516, 400, 67, 100, visibility);}
                    else {setButtonProperties(buttonMonster1Player, 500, 416, 100, 67, visibility);}
                    rescaleEverything();
                    buttonMonster1Player.setIcon(new RescaledIcon(YuGiOhJi.class.getResource(imagePath)));
                    break;
                case 2:
                    if (isInAttMode) {setButtonProperties(buttonMonster2Player, 616, 400, 67, 100, visibility);}
                    else {setButtonProperties(buttonMonster2Player, 600, 416, 100, 67, visibility);}
                    rescaleEverything();
                    buttonMonster2Player.setIcon(new RescaledIcon(YuGiOhJi.class.getResource(imagePath)));
                    break;
                case 3:
                    if (isInAttMode) {setButtonProperties(buttonMonster3Player, 716, 400, 67, 100, visibility);}
                    else {setButtonProperties(buttonMonster3Player, 700, 416, 100, 67, visibility);}
                    rescaleEverything();
                    buttonMonster3Player.setIcon(new RescaledIcon(YuGiOhJi.class.getResource(imagePath)));
                    break;
                case 4:
                    if (isInAttMode) {setButtonProperties(buttonMonster4Player, 816, 400, 67, 100, visibility);}
                    else {setButtonProperties(buttonMonster4Player, 800, 416, 100, 67, visibility);}
                    rescaleEverything();
                    buttonMonster4Player.setIcon(new RescaledIcon(YuGiOhJi.class.getResource(imagePath)));
                    break;
                case 5:
                    if (isInAttMode) {setButtonProperties(buttonMonster5Player, 916, 400, 67, 100, visibility);}
                    else {setButtonProperties(buttonMonster5Player, 900, 416, 100, 67, visibility);}
                    rescaleEverything();
                    buttonMonster5Player.setIcon(new RescaledIcon(YuGiOhJi.class.getResource(imagePath)));
                    break;
                default: debugDialog("Error: out of bounds in setMonsterButtonIcon(...); attempted button: " + n); break;
            }
        }
        else {
            switch (n) {
                case 1:
                    if (isInAttMode) {setButtonProperties(buttonMonster1CPU, 516, 200, 67, 100, visibility);}
                    else {setButtonProperties(buttonMonster1CPU, 500, 216, 100, 67, visibility);}
                    rescaleEverything();
                    buttonMonster1CPU.setIcon(new RescaledIcon(YuGiOhJi.class.getResource(imagePath)));
                    break;
                case 2:
                    if (isInAttMode) {setButtonProperties(buttonMonster2CPU, 616, 200, 67, 100, visibility);}
                    else {setButtonProperties(buttonMonster2CPU, 600, 216, 100, 67, visibility);}
                    rescaleEverything();
                    buttonMonster2CPU.setIcon(new RescaledIcon(YuGiOhJi.class.getResource(imagePath)));
                    break;
                case 3:
                    if (isInAttMode) {setButtonProperties(buttonMonster3CPU, 716, 200, 67, 100, visibility);}
                    else {setButtonProperties(buttonMonster3CPU, 700, 216, 100, 67, visibility);}
                    rescaleEverything();
                    buttonMonster3CPU.setIcon(new RescaledIcon(YuGiOhJi.class.getResource(imagePath)));
                    break;
                case 4:
                    if (isInAttMode) {setButtonProperties(buttonMonster4CPU, 816, 200, 67, 100, visibility);}
                    else {setButtonProperties(buttonMonster4CPU, 800, 216, 100, 67, visibility);}
                    rescaleEverything();
                    buttonMonster4CPU.setIcon(new RescaledIcon(YuGiOhJi.class.getResource(imagePath)));
                    break;
                case 5:
                    if (isInAttMode) {setButtonProperties(buttonMonster5CPU, 916, 200, 67, 100, visibility);}
                    else {setButtonProperties(buttonMonster5CPU, 900, 216, 100, 67, visibility);}
                    rescaleEverything();
                    buttonMonster5CPU.setIcon(new RescaledIcon(YuGiOhJi.class.getResource(imagePath)));
                    break;
                default: debugDialog("Error: out of bounds in setMonsterButtonIcon(...); attempted button: " + n); break;
            }
        }
    }
    
    // changes the appearance of an equip card on the field
    public static void setEquipButtonIcon (String imagePath, int n, boolean isBelongingToPlayer, boolean visibility){
        if (isBelongingToPlayer) {
            switch (n) {
                case 1: buttonEquip1Player.setIcon(new RescaledIcon(YuGiOhJi.class.getResource(imagePath))); buttonEquip1Player.setVisible(visibility); break;
                case 2: buttonEquip2Player.setIcon(new RescaledIcon(YuGiOhJi.class.getResource(imagePath))); buttonEquip2Player.setVisible(visibility); break;
                case 3: buttonEquip3Player.setIcon(new RescaledIcon(YuGiOhJi.class.getResource(imagePath))); buttonEquip3Player.setVisible(visibility); break;
                case 4: buttonEquip4Player.setIcon(new RescaledIcon(YuGiOhJi.class.getResource(imagePath))); buttonEquip4Player.setVisible(visibility); break;
                case 5: buttonEquip5Player.setIcon(new RescaledIcon(YuGiOhJi.class.getResource(imagePath))); buttonEquip5Player.setVisible(visibility); break;
                default: debugDialog("Error: out of bounds in setEquipButtonIcon(...); attempted button: " + n); break;
            }
        }
        else {
            switch (n) {
                case 1: buttonEquip1CPU.setIcon(new RescaledIcon(YuGiOhJi.class.getResource(imagePath))); buttonEquip1CPU.setVisible(visibility); break;
                case 2: buttonEquip2CPU.setIcon(new RescaledIcon(YuGiOhJi.class.getResource(imagePath))); buttonEquip2CPU.setVisible(visibility); break;
                case 3: buttonEquip3CPU.setIcon(new RescaledIcon(YuGiOhJi.class.getResource(imagePath))); buttonEquip3CPU.setVisible(visibility); break;
                case 4: buttonEquip4CPU.setIcon(new RescaledIcon(YuGiOhJi.class.getResource(imagePath))); buttonEquip4CPU.setVisible(visibility); break;
                case 5: buttonEquip5CPU.setIcon(new RescaledIcon(YuGiOhJi.class.getResource(imagePath))); buttonEquip5CPU.setVisible(visibility); break;
                default: debugDialog("Error: out of bounds in setEquipButtonIcon(...); attempted button: " + n); break;
            }
        }
        rescaleEverything();
    }
    
    // used for making dialogs that tell the current strategy of cpu
    // (for debugging) to be able to write dialog commands as single lines
    public static void debugDialog (String dialogText){
        JDialog decideJDialog = new JDialog();
        int x = (int) Math.floor(currentFrameWidth/2);
        int width = (int) Math.floor(x*0.8);
        int height = (int) Math.floor(currentFrameHeight/7);
        int y = (int) Math.floor((currentFrameHeight-height)/2);
        decideJDialog.setBounds(x, y, width, height);
        decideJDialog.add(new JLabel(" " + dialogText));
        decideJDialog.setTitle("\"thoughts\" of the computer:");
        decideJDialog.setModal(true);
        decideJDialog.setVisible(true);
    }
    
    // like information message, but with a warning sign instead of an information symbol
    public static void errorDialog (String content, String title) {
        JOptionPane.showOptionDialog(null, content, title, JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, new String[]{"ok"}, "ok");
    }
    
    // simple shows a dialog with a certain information and an OK and a close button
    public static void informationDialog (String content, String title) {
        JOptionPane.showOptionDialog(null, content, title, JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new String[]{"ok"}, "ok");
    }
    
    // shows a dialog with a number of buttons defined by the entered String array and a question mark symbol
    // It returns the number of the entered button minus one.
    // It returns -1, in case one clicked on the close button.
    // The default option is an exact copy of one of the options (elements in the string array), but it can also be just empty inverted commas "", if there is no default.
    public static int multipleChoiceDialog (String content, String title, String[] options, String defaultOption) {
        return JOptionPane.showOptionDialog(null, content, title, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, defaultOption);
    }
    
    // from here on:
    // --- reactions of the buttons ---
    // what happens, if one clicks on a card etc.
    
    @Override
    public void actionPerformed (ActionEvent ae){
        
        if (ae.getSource() == YuGiOhJi.buttonNextPhase) {Game.nextPhase();}
        if (ae.getSource() == YuGiOhJi.buttonNextTurn) {Game.nextTurn();}
        if (ae.getSource() == YuGiOhJi.buttonStart) {Game.startNewGame();}
        if (ae.getSource() == YuGiOhJi.buttonDeckBuilding) {Game.openDeckBuildingWindow();}
        if (ae.getSource() == YuGiOhJi.buttonCPUsetup) {Game.openCPUSetupDialog();}
        if (ae.getSource() == YuGiOhJi.buttonGiveUp) {Game.giveUpButton();}
        if (ae.getSource() == YuGiOhJi.buttonMods) {Game.openModsDialog();}
        if (ae.getSource() == YuGiOhJi.buttonGameSetup) {Game.openGameSetupDialog();}
        if (ae.getSource() == YuGiOhJi.buttonInfo) { // one can change the code of this button to any method call in order to test any method
            informationDialog("<html><body>This game has been programmed by Patrick P&ouml;schke.<br>Look for the latest version or other games on:<br>https://patrick-poeschke.itch.io<body></html>", "This is version 1.1");
        }
        
        // clicking on cards in the hand of the player
        if (ae.getSource() == YuGiOhJi.buttonCard1Player) {Game.chooseCardOnHand(1);}
        if (ae.getSource() == YuGiOhJi.buttonCard2Player) {Game.chooseCardOnHand(2);}
        if (ae.getSource() == YuGiOhJi.buttonCard3Player) {Game.chooseCardOnHand(3);}
        if (ae.getSource() == YuGiOhJi.buttonCard4Player) {Game.chooseCardOnHand(4);}
        if (ae.getSource() == YuGiOhJi.buttonCard5Player) {Game.chooseCardOnHand(5);}
        if (ae.getSource() == YuGiOhJi.buttonCard6Player) {Game.chooseCardOnHand(6);}
        if (ae.getSource() == YuGiOhJi.buttonCard7Player) {Game.chooseCardOnHand(7);}
        if (ae.getSource() == YuGiOhJi.buttonCard8Player) {Game.chooseCardOnHand(8);}
        if (ae.getSource() == YuGiOhJi.buttonCard9Player) {Game.chooseCardOnHand(9);}
        if (ae.getSource() == YuGiOhJi.buttonCard10Player) {Game.chooseCardOnHand(10);}
        // clicking on summonend monsters of the player
        if (ae.getSource() == YuGiOhJi.buttonMonster1Player) {Game.chooseMonster(1, true);}
        if (ae.getSource() == YuGiOhJi.buttonMonster2Player) {Game.chooseMonster(2, true);}
        if (ae.getSource() == YuGiOhJi.buttonMonster3Player) {Game.chooseMonster(3, true);}
        if (ae.getSource() == YuGiOhJi.buttonMonster4Player) {Game.chooseMonster(4, true);}
        if (ae.getSource() == YuGiOhJi.buttonMonster5Player) {Game.chooseMonster(5, true);}
        // clicking on summonend monsters of the CPU
        if (ae.getSource() == YuGiOhJi.buttonMonster1CPU) {Game.chooseMonster(1, false);}
        if (ae.getSource() == YuGiOhJi.buttonMonster2CPU) {Game.chooseMonster(2, false);}
        if (ae.getSource() == YuGiOhJi.buttonMonster3CPU) {Game.chooseMonster(3, false);}
        if (ae.getSource() == YuGiOhJi.buttonMonster4CPU) {Game.chooseMonster(4, false);}
        if (ae.getSource() == YuGiOhJi.buttonMonster5CPU) {Game.chooseMonster(5, false);}
        // clicking on an equip stack of player
        if (ae.getSource() == YuGiOhJi.buttonEquip1Player) {Game.chooseEquipStack(1, true);}
        if (ae.getSource() == YuGiOhJi.buttonEquip2Player) {Game.chooseEquipStack(2, true);}
        if (ae.getSource() == YuGiOhJi.buttonEquip3Player) {Game.chooseEquipStack(3, true);}
        if (ae.getSource() == YuGiOhJi.buttonEquip4Player) {Game.chooseEquipStack(4, true);}
        if (ae.getSource() == YuGiOhJi.buttonEquip5Player) {Game.chooseEquipStack(5, true);}
        // clicking on an equip stack of CPU
        if (ae.getSource() == YuGiOhJi.buttonEquip1CPU) {Game.chooseEquipStack(1, false);}
        if (ae.getSource() == YuGiOhJi.buttonEquip2CPU) {Game.chooseEquipStack(2, false);}
        if (ae.getSource() == YuGiOhJi.buttonEquip3CPU) {Game.chooseEquipStack(3, false);}
        if (ae.getSource() == YuGiOhJi.buttonEquip4CPU) {Game.chooseEquipStack(4, false);}
        if (ae.getSource() == YuGiOhJi.buttonEquip5CPU) {Game.chooseEquipStack(5, false);}
        // clicking on the graveyards
        if (ae.getSource() == YuGiOhJi.buttonGYPlayer) {Game.chooseGY(true);}
        if (ae.getSource() == YuGiOhJi.buttonGYCPU) {Game.chooseGY(false);}
        // clicking on the banishing zones (different dimensions) with more than one card in it
        // just to see what cards have already been banished
        if (ae.getSource() == YuGiOhJi.buttonDDPlayer) {Game.lookAtDifferentDimension(true);}
        if (ae.getSource() == YuGiOhJi.buttonDDCPU) {Game.lookAtDifferentDimension(false);}
        
    }
    
    // in order not to repeat oneself, out-source here what happens when one hovers with the mouse over a hand card
    public static void hoverOnHandCard (int cardNumber){
        preview.setIcon(new RescaledIcon(YuGiOhJi.class.getResource(HandPlayer.getNthCardOfHand(cardNumber).bigCardPath)));
    }
    
    // in order not to repeat oneself, out-source here what happens when one hovers with the mouse over an equip stack
    public static void hoverOnEquipButton (int stackNumber, boolean isBelongingToPlayer){
        EStack Stack = EStack.getNthStack(stackNumber, isBelongingToPlayer);
        preview.setIcon(new RescaledIcon(YuGiOhJi.class.getResource(Stack.getNthCardOfStack(Stack.numberOfCards).bigCardPath)));
        if (Stack.getNegationStatusOfNthEquipCard(Stack.numberOfCards)) {displayNegationStatement.setVisible(true);}
    }
    
    // in order not to repeat oneself, out-source here what happens when one hovers with the mouse over a summoned monster
    public static void hoverOnMonster (int monsterNumber, boolean isBelongingToPlayer){
        int fontsize=100;
        if (Game.isSwitchingOnRescalingFontsize) {fontsize = (int) (100*currentFrameHeight/initialFrameHeight);}
        SummonedMonster SumMonster = SummonedMonster.getNthSummonedMonster(monsterNumber, isBelongingToPlayer);
        if (SumMonster.isKnownToPlayer()) {
            preview.setIcon(new RescaledIcon(YuGiOhJi.class.getResource(SumMonster.Card.bigCardPath)));
        }
        if (SumMonster.hasPiercingDamageAbility()) {
            displayPiercingStatement.setText("<html><body><span style=\"font-size:" + fontsize + "%\";color=\"red\">This monster has piercing attack.</span></body></html>");
            displayPiercingStatement.setVisible(true);
        }
        if (SumMonster.isNotAbleToUseItsEffects) {
            displayNegationStatement.setText("<html><body><span style=\"font-size:" + fontsize + "%\";color=\"red\"> This card has its effects negated.</span></body></html>");
            displayNegationStatement.setVisible(true);
        }
        else {
            if (SumMonster.isUsingEffectsOfOtherMonster) {
                displaySkillCopyStatement.setText("<html><body><span style=\"font-size:" + fontsize + "%\";color=\"blue\">This monster uses the effects of " + YMonster.getMonsterById(SumMonster.otherEffectMonsterID).monsterName + ".</span></body></html>");
                displaySkillCopyStatement.setVisible(true);
            }
        }
    }
    
    // what happens when one enters a certain zone with the mouse (show big preview screen depending on card)
    @Override
    public void mouseEntered(MouseEvent me) {
        // display larger (readable) version of cards in player's hand
        if (me.getSource() == YuGiOhJi.buttonCard1Player && HandPlayer.numberOfCards>=1) {hoverOnHandCard(1);}
        if (me.getSource() == YuGiOhJi.buttonCard2Player && HandPlayer.numberOfCards>=2) {hoverOnHandCard(2);}
        if (me.getSource() == YuGiOhJi.buttonCard3Player && HandPlayer.numberOfCards>=3) {hoverOnHandCard(3);}
        if (me.getSource() == YuGiOhJi.buttonCard4Player && HandPlayer.numberOfCards>=4) {hoverOnHandCard(4);}
        if (me.getSource() == YuGiOhJi.buttonCard5Player && HandPlayer.numberOfCards>=5) {hoverOnHandCard(5);}
        if (me.getSource() == YuGiOhJi.buttonCard6Player && HandPlayer.numberOfCards>=6) {hoverOnHandCard(6);}
        if (me.getSource() == YuGiOhJi.buttonCard7Player && HandPlayer.numberOfCards>=7) {hoverOnHandCard(7);}
        if (me.getSource() == YuGiOhJi.buttonCard8Player && HandPlayer.numberOfCards>=8) {hoverOnHandCard(8);}
        if (me.getSource() == YuGiOhJi.buttonCard9Player && HandPlayer.numberOfCards>=9) {hoverOnHandCard(9);}
        if (me.getSource() == YuGiOhJi.buttonCard10Player && HandPlayer.numberOfCards>=10) {hoverOnHandCard(10);}
        // display larger (readable) version of upper most cards in graveyards on the field
        if (me.getSource() == YuGiOhJi.buttonGYCPU && GYDeckCPU.numberOfCards>=1) {preview.setIcon(new RescaledIcon(this.getClass().getResource(GYDeckCPU.getLastCardOfDeck().bigCardPath)));}
        if (me.getSource() == YuGiOhJi.buttonDDCPU && DDDeckCPU.numberOfCards>=1) {preview.setIcon(new RescaledIcon(this.getClass().getResource(DDDeckCPU.getLastCardOfDeck().bigCardPath)));}
        if (me.getSource() == YuGiOhJi.buttonDDPlayer && DDDeckPlayer.numberOfCards>=1) {preview.setIcon(new RescaledIcon(this.getClass().getResource(DDDeckPlayer.getLastCardOfDeck().bigCardPath)));}
        if (me.getSource() == YuGiOhJi.buttonGYPlayer && GYDeckPlayer.numberOfCards>=1) {preview.setIcon(new RescaledIcon(this.getClass().getResource(GYDeckPlayer.getLastCardOfDeck().bigCardPath)));}
        // display larger (readable) version of monster cards on the field (but only when they are face up)
        if (me.getSource() == YuGiOhJi.buttonMonster1CPU && SummonedMonster1CPU.isExisting) {hoverOnMonster(1, false);}
        if (me.getSource() == YuGiOhJi.buttonMonster2CPU && SummonedMonster2CPU.isExisting) {hoverOnMonster(2, false);}
        if (me.getSource() == YuGiOhJi.buttonMonster3CPU && SummonedMonster3CPU.isExisting) {hoverOnMonster(3, false);}
        if (me.getSource() == YuGiOhJi.buttonMonster4CPU && SummonedMonster4CPU.isExisting) {hoverOnMonster(4, false);}
        if (me.getSource() == YuGiOhJi.buttonMonster5CPU && SummonedMonster5CPU.isExisting) {hoverOnMonster(5, false);}
        if (me.getSource() == YuGiOhJi.buttonMonster1Player && SummonedMonster1Player.isExisting) {hoverOnMonster(1, true);}
        if (me.getSource() == YuGiOhJi.buttonMonster2Player && SummonedMonster2Player.isExisting) {hoverOnMonster(2, true);}
        if (me.getSource() == YuGiOhJi.buttonMonster3Player && SummonedMonster3Player.isExisting) {hoverOnMonster(3, true);}
        if (me.getSource() == YuGiOhJi.buttonMonster4Player && SummonedMonster4Player.isExisting) {hoverOnMonster(4, true);}
        if (me.getSource() == YuGiOhJi.buttonMonster5Player && SummonedMonster5Player.isExisting) {hoverOnMonster(5, true);}
        // display larger (readable) version of upper most equip monster cards on the field
        if (me.getSource() == YuGiOhJi.buttonEquip1CPU && EquipStack1CPU.numberOfCards>0) {hoverOnEquipButton(1, false);}
        if (me.getSource() == YuGiOhJi.buttonEquip2CPU && EquipStack2CPU.numberOfCards>0) {hoverOnEquipButton(2, false);}
        if (me.getSource() == YuGiOhJi.buttonEquip3CPU && EquipStack3CPU.numberOfCards>0) {hoverOnEquipButton(3, false);}
        if (me.getSource() == YuGiOhJi.buttonEquip4CPU && EquipStack4CPU.numberOfCards>0) {hoverOnEquipButton(4, false);}
        if (me.getSource() == YuGiOhJi.buttonEquip5CPU && EquipStack5CPU.numberOfCards>0) {hoverOnEquipButton(5, false);}
        if (me.getSource() == YuGiOhJi.buttonEquip1Player && EquipStack1Player.numberOfCards>0) {hoverOnEquipButton(1, true);}
        if (me.getSource() == YuGiOhJi.buttonEquip2Player && EquipStack2Player.numberOfCards>0) {hoverOnEquipButton(2, true);}
        if (me.getSource() == YuGiOhJi.buttonEquip3Player && EquipStack3Player.numberOfCards>0) {hoverOnEquipButton(3, true);}
        if (me.getSource() == YuGiOhJi.buttonEquip4Player && EquipStack4Player.numberOfCards>0) {hoverOnEquipButton(4, true);}
        if (me.getSource() == YuGiOhJi.buttonEquip5Player && EquipStack5Player.numberOfCards>0) {hoverOnEquipButton(5, true);}
        // maybe even include larger version of card of CPU (in rare case that the CPU is showing a card?): no, just make modal dialog (small version has to suffice)
    }
    
    // what happens when one leaves any zone (just show the big version of the back side of each card)
    @Override
    public void mouseExited(MouseEvent me) {
        preview.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        displayPiercingStatement.setVisible(false);
        displaySkillCopyStatement.setVisible(false);
        displayNegationStatement.setVisible(false);
    }
    
    // These overridden functions need to be here in order for the program to work. However, simply ignore them.
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
