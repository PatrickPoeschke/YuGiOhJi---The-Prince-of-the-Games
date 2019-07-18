package yugiohji;

/**
 * This class creates windows for choosing YuGiOhJi-cards.
 * This includes choosing summonend monsters as tribute and
 * choosing cards to discard or banish as paying costs for an effect or summoning.
 * 
 * Programmer's note:
 * This is by far the biggest file in the whole game (about 2700 lines of code),
 * because most is saved as an own variable.
 * One could reduce the number of lines drastically by using arrays.
 * However, arrays in Java (like in most languages) are always off by one,
 * which is very error-prone, if the programmer is not used to it.
 * (That is the case, because for many years the programmer used Matlab,
 * which does not have this "feature".)
 * 
 */

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import static yugiohji.YuGiOhJi.DeckPlayer;
import static yugiohji.YuGiOhJi.Game;
import static yugiohji.YuGiOhJi.GYDeckPlayer;
import static yugiohji.YuGiOhJi.HandPlayer;
import static yugiohji.YuGiOhJi.NoCard;
import static yugiohji.YuGiOhJi.window4;

public class YChooseCardWindow implements ActionListener, MouseListener, ComponentListener {
    
    // variables about graphical elements
    public static int initialPayCostWidth=550;
    public static int initialPayCostHeight=180;
    public static int currentPayCostWidth=550;
    public static int currentPayCostHeight=180;
    
    public static JDialog cardChooserDialog;
    JScrollPane scrollPane;
    public static int initialScrollPaneWidth;
    public static int initialScrollPaneHeight;
    public static int currentScrollPaneWidth;
    public static int currentScrollPaneHeight;
    public static JPanel panel4;
    
    // the following variables remember which cards to tribute for an effect or summoning
    public static int intTributeHandCardId1=0; // one can at max. discard 2 cards on the hand
    public static int intTributeHandCardId2=0;
    
    public static int intTributeMonsterNo1=0; // one can at max. tribute 3 own monsters on the field
    public static int intTributeMonsterNo2=0;
    public static int intTributeMonsterNo3=0;
    
    public static int intTributeGYCardId1=0; // one can at max. banish 4 cards from the graveyard
    public static int intTributeGYCardId2=0;
    public static int intTributeGYCardId3=0;
    public static int intTributeGYCardId4=0;
    
    // one can at max. sacrifice 4 equip cards
    public static EStack StackOfTribute1 = new EStack(false, 1); // for each tributed card one needs to the the stack it is in, 
    public static int intTributeStackCardId1=0; // and which card number it has
    public static boolean intTributeStackCardNegationStatus1;
    
    public static EStack StackOfTribute2 = new EStack(false, 2);
    public static int intTributeStackCardId2=0;
    public static boolean intTributeStackCardNegationStatus2;
    
    public static EStack StackOfTribute3 = new EStack(false, 3);
    public static int intTributeStackCardId3=0;
    public static boolean intTributeStackCardNegationStatus3;
    
    public static EStack StackOfTribute4 = new EStack(false, 4);
    public static int intTributeStackCardId4=0;
    public static boolean intTributeStackCardNegationStatus4;
    
    public static int intTributeHandCardNo1=0; // in case one wants to tribute 22 cards with same Id from same source
    public static int intTributeHandCardNo2=0; // one also needs the position in the source (Hand, Stack, GY)
    
    public static int intTributeGYCardNo1=0;
    public static int intTributeGYCardNo2=0;
    public static int intTributeGYCardNo3=0;
    public static int intTributeGYCardNo4=0;
    
    public static int intTributeStackCardNo1=0;
    public static int intTributeStackCardNo2=0;
    public static int intTributeStackCardNo3=0;
    public static int intTributeStackCardNo4=0;
    
    public static YCard LastSacrificedCard=NoCard;
    public static SummonedMonster LastSacrificedMonster=new SummonedMonster(true, 1);
    
    public static JLabel displayHandText;
    public static JButton buttonPayCost;
    public static int selectedSemipoints;
    public static int toBePaidSemipoints;
    public static JLabel displaySumOfPaidCost;
    
    // I don't like arrays. They are always wrong by one and that drives me mad.
    // That's why I use countless single variables.
    public static JLabel displayMonsterText;
    public static JLabel displayMonsterNo1;
    public static JLabel displayMonsterNo2;
    public static JLabel displayMonsterNo3;
    public static JLabel displayMonsterNo4;
    public static JLabel displayMonsterNo5;
    public static JLabel displayGYText;
    public static JLabel displayEquipText;
    public static JLabel displayStackNo1; // if one has used all equip cards in this game, max. is 5 (3 times 5 cards able to equip)
    public static JLabel displayStackNo2;
    public static JLabel displayStackNo3;
    public static JLabel displayStackNo4;
    public static JLabel displayStackNo5;
    public static JLabel displayStackNo6;
    public static JLabel displayStackNo7;
    public static JLabel displayStackNo8;
    public static JLabel displayStackNo9;
    public static JLabel displayStackNo10;
    public static JLabel displayStackNo11;
    public static JLabel displayStackNo12;
    public static JLabel displayStackNo13;
    public static JLabel displayStackNo14;
    public static JLabel displayStackNo15;
    
    public static JButton buttonHandCard1;
    public static JButton buttonHandCard2;
    public static JButton buttonHandCard3;
    public static JButton buttonHandCard4;
    public static JButton buttonHandCard5;
    public static JButton buttonHandCard6;
    public static JButton buttonHandCard7;
    public static JButton buttonHandCard8;
    public static JButton buttonHandCard9;
    public static JButton buttonHandCard10;
    public static JButton buttonMonsterCard1;
    public static JButton buttonMonsterCard2;
    public static JButton buttonMonsterCard3;
    public static JButton buttonMonsterCard4;
    public static JButton buttonMonsterCard5;
    public static JButton buttonGYCard1;
    public static JButton buttonGYCard2;
    public static JButton buttonGYCard3;
    public static JButton buttonGYCard4;
    public static JButton buttonGYCard5;
    public static JButton buttonGYCard6;
    public static JButton buttonGYCard7;
    public static JButton buttonGYCard8;
    public static JButton buttonGYCard9;
    public static JButton buttonGYCard10;
    public static JButton buttonGYCard11;
    public static JButton buttonGYCard12;
    public static JButton buttonGYCard13;
    public static JButton buttonGYCard14;
    public static JButton buttonGYCard15;
    public static JButton buttonGYCard16;
    public static JButton buttonGYCard17;
    public static JButton buttonGYCard18;
    public static JButton buttonGYCard19;
    public static JButton buttonGYCard20;
    public static JButton buttonGYCard21;
    public static JButton buttonGYCard22;
    public static JButton buttonGYCard23;
    public static JButton buttonGYCard24;
    public static JButton buttonGYCard25;
    public static JButton buttonGYCard26;
    public static JButton buttonGYCard27;
    public static JButton buttonGYCard28;
    public static JButton buttonGYCard29;
    public static JButton buttonGYCard30;
    public static JButton buttonGYCard31;
    public static JButton buttonGYCard32;
    public static JButton buttonGYCard33;
    public static JButton buttonGYCard34;
    public static JButton buttonGYCard35;
    public static JButton buttonGYCard36;
    public static JButton buttonGYCard37;
    public static JButton buttonGYCard38;
    public static JButton buttonGYCard39;
    public static JButton buttonEquipCard1;
    public static JButton buttonEquipCard2;
    public static JButton buttonEquipCard3;
    public static JButton buttonEquipCard4;
    public static JButton buttonEquipCard5;
    public static JButton buttonEquipCard6;
    public static JButton buttonEquipCard7;
    public static JButton buttonEquipCard8;
    public static JButton buttonEquipCard9;
    public static JButton buttonEquipCard10;
    public static JButton buttonEquipCard11;
    public static JButton buttonEquipCard12;
    public static JButton buttonEquipCard13;
    public static JButton buttonEquipCard14;
    public static JButton buttonEquipCard15;
    
    public static Hand consideredHandCards = new Hand(true); // containing only the considered cards on the player's hand
    public static int numberOfConsideredHandCards;
    
    public static Deck consideredGYCards = new Deck(true); // containing only the considered cards in the player's graveyard
    public static int numberOfConsideredGYCards;
    public static int consideredHandCardId1=0; // also set these correctly to remember the original Id of the cards (not of the just considered ones)
    public static int consideredHandCardId2=0; // important for tributing the right cards in the end
    public static int consideredHandCardId3=0;
    public static int consideredHandCardId4=0;
    public static int consideredHandCardId5=0;
    public static int consideredHandCardId6=0;
    public static int consideredHandCardId7=0;
    public static int consideredHandCardId8=0;
    public static int consideredHandCardId9=0;
    public static int consideredHandCardId10=0;
    public static int consideredGYCardId1=0;
    public static int consideredGYCardId2=0;
    public static int consideredGYCardId3=0;
    public static int consideredGYCardId4=0;
    public static int consideredGYCardId5=0;
    public static int consideredGYCardId6=0;
    public static int consideredGYCardId7=0;
    public static int consideredGYCardId8=0;
    public static int consideredGYCardId9=0;
    public static int consideredGYCardId10=0;
    public static int consideredGYCardId11=0;
    public static int consideredGYCardId12=0;
    public static int consideredGYCardId13=0;
    public static int consideredGYCardId14=0;
    public static int consideredGYCardId15=0;
    public static int consideredGYCardId16=0;
    public static int consideredGYCardId17=0;
    public static int consideredGYCardId18=0;
    public static int consideredGYCardId19=0;
    public static int consideredGYCardId20=0;
    public static int consideredGYCardId21=0;
    public static int consideredGYCardId22=0;
    public static int consideredGYCardId23=0;
    public static int consideredGYCardId24=0;
    public static int consideredGYCardId25=0;
    public static int consideredGYCardId26=0;
    public static int consideredGYCardId27=0;
    public static int consideredGYCardId28=0;
    public static int consideredGYCardId29=0;
    public static int consideredGYCardId30=0;
    public static int consideredGYCardId31=0;
    public static int consideredGYCardId32=0;
    public static int consideredGYCardId33=0;
    public static int consideredGYCardId34=0;
    public static int consideredGYCardId35=0;
    public static int consideredGYCardId36=0;
    public static int consideredGYCardId37=0;
    public static int consideredGYCardId38=0;
    public static int consideredGYCardId39=0;
    
    // only considered summoned monsters
    public static SummonedMonster considSumMonster1 = new SummonedMonster(true, 1);
    public static SummonedMonster considSumMonster2 = new SummonedMonster(true, 1);
    public static SummonedMonster considSumMonster3 = new SummonedMonster(true, 1);
    public static SummonedMonster considSumMonster4 = new SummonedMonster(true, 1);
    public static SummonedMonster considSumMonster5 = new SummonedMonster(true, 1);
    public static int numberOfConsideredMonsters;
    
    // only considered equip cards
    public static int numberOfConsideredEquipCards=0;
    
    // make use of full power of properties of newly defined EquipCard class
    public static EquipCard EquipCard1 = new EquipCard(true); // public static EquipCard[] EquipCards = new EquipCard[15];
    public static EquipCard EquipCard2 = new EquipCard(true);
    public static EquipCard EquipCard3 = new EquipCard(true);
    public static EquipCard EquipCard4 = new EquipCard(true);
    public static EquipCard EquipCard5 = new EquipCard(true);
    public static EquipCard EquipCard6 = new EquipCard(true);
    public static EquipCard EquipCard7 = new EquipCard(true);
    public static EquipCard EquipCard8 = new EquipCard(true);
    public static EquipCard EquipCard9 = new EquipCard(true);
    public static EquipCard EquipCard10 = new EquipCard(true);
    public static EquipCard EquipCard11 = new EquipCard(true);
    public static EquipCard EquipCard12 = new EquipCard(true);
    public static EquipCard EquipCard13 = new EquipCard(true);
    public static EquipCard EquipCard14 = new EquipCard(true);
    public static EquipCard EquipCard15 = new EquipCard(true);
    
    public static int equipCardId1=0; // don't forget to set these correctly: original ID of all equip cards in a stack (not just considered ones)
    public static int equipCardId2=0; // important for tributing the right cards in the end
    public static int equipCardId3=0;
    public static int equipCardId4=0;
    public static int equipCardId5=0;
    public static int equipCardId6=0;
    public static int equipCardId7=0;
    public static int equipCardId8=0;
    public static int equipCardId9=0;
    public static int equipCardId10=0;
    public static int equipCardId11=0;
    public static int equipCardId12=0;
    public static int equipCardId13=0;
    public static int equipCardId14=0;
    public static int equipCardId15=0;
    
    public static int levelHand=1; // important for moving down useless buttons (and moving up useful ones)
    public static int levelMonster=2;
    public static int levelGY=3;
    public static int levelEquip=4;
    
    // constructor for standard tribute window
    public YChooseCardWindow (String windowTitle) {
        
        panel4 = new JPanel();
        adjustScrollPaneSize(); // this sets scrollPaneWidth and scrollPaneHeight
        panel4.setPreferredSize(new Dimension(initialScrollPaneWidth, initialScrollPaneHeight)); // max. size (if GY full) should be around 4000x525
        panel4.setLayout(null);
        
        scrollPane = new JScrollPane(panel4);
        
        cardChooserDialog = new JDialog();
        cardChooserDialog.setTitle(windowTitle);
        cardChooserDialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        cardChooserDialog.getContentPane().addComponentListener(this);
        double scaleFactorX = (double) YuGiOhJi.currentFrameWidth/YuGiOhJi.initialFrameWidth;
        double scaleFactorY = (double) YuGiOhJi.currentFrameHeight/YuGiOhJi.initialFrameHeight;
        int posX = (int) Math.round(408*scaleFactorX);
        int posY = (int) Math.round(30*scaleFactorY);
        initialPayCostWidth = (int) Math.round(550*scaleFactorX);
        initialPayCostHeight = (int) Math.round(180*scaleFactorY);
        cardChooserDialog.setBounds(posX, posY, initialPayCostWidth, initialPayCostHeight); // set bounds large enough that the title is completely readable
        cardChooserDialog.getContentPane().setLayout(new BorderLayout(0, 0));
        cardChooserDialog.getContentPane().add(scrollPane, BorderLayout.CENTER);
        cardChooserDialog.setVisible(true);
        
        displayHandText = new JLabel("<html> cards on hand: worth 1 card</html>");
        displayHandText.setBounds(10, 0, 100, 40);
        displayHandText.setVisible(true);
        panel4.add(displayHandText);
        
        buttonPayCost = new JButton("pay cost");
        buttonPayCost.setBounds(10, 50, 90, 25);
        buttonPayCost.addActionListener(this);
        buttonPayCost.setVisible(true);
        panel4.add(buttonPayCost);
        
        displaySumOfPaidCost = new JLabel("sum worth: " + 0);
        displaySumOfPaidCost.setBounds(10, 75, 90, 25);
        displaySumOfPaidCost.setVisible(true);
        panel4.add(displaySumOfPaidCost);
        
        displayMonsterText = new JLabel("<html> monster: pos.: worth 1 card</html>");
        displayMonsterText.setBounds(10, 125, 90, 40);
        displayMonsterText.setVisible(true);
        panel4.add(displayMonsterText);
        
        displayGYText = new JLabel("<html> cards in GY: worth 1/2 card</html>");
        displayGYText.setBounds(10, 250, 90, 40);
        displayGYText.setVisible(true);
        panel4.add(displayGYText);
        
        displayEquipText = new JLabel("<html> equip stack no: worth 1/2 card</html>");
        displayEquipText.setBounds(10, 375, 90, 40);
        displayEquipText.setVisible(true);
        panel4.add(displayEquipText);
        
        
        buttonHandCard1 = new JButton();
        buttonHandCard1.setIcon(new RescaledIcon(this.getClass().getResource(consideredHandCards.Cards[1].upMonster.cardPathAtt)));
        YuGiOhJi.setButtonProperties(buttonHandCard1, 116, 25, 67, 100, numberOfConsideredHandCards>=1);
        buttonHandCard1.addActionListener(this);
        buttonHandCard1.addMouseListener(this);
        panel4.add(buttonHandCard1);
        
        buttonHandCard2 = new JButton();
        buttonHandCard2.setIcon(new RescaledIcon(this.getClass().getResource(consideredHandCards.Cards[2].upMonster.cardPathAtt)));
        YuGiOhJi.setButtonProperties(buttonHandCard2, 216, 25, 67, 100, numberOfConsideredHandCards>=2);
        buttonHandCard2.addActionListener(this);
        buttonHandCard2.addMouseListener(this);
        panel4.add(buttonHandCard2);
        
        buttonHandCard3 = new JButton();
        buttonHandCard3.setIcon(new RescaledIcon(this.getClass().getResource(consideredHandCards.Cards[3].upMonster.cardPathAtt)));
        YuGiOhJi.setButtonProperties(buttonHandCard3, 316, 25, 67, 100, numberOfConsideredHandCards>=3);
        buttonHandCard3.addActionListener(this);
        buttonHandCard3.addMouseListener(this);
        panel4.add(buttonHandCard3);
        
        buttonHandCard4 = new JButton();
        buttonHandCard4.setIcon(new RescaledIcon(this.getClass().getResource(consideredHandCards.Cards[4].upMonster.cardPathAtt)));
        YuGiOhJi.setButtonProperties(buttonHandCard4, 416, 25, 67, 100, numberOfConsideredHandCards>=4);
        buttonHandCard4.addActionListener(this);
        buttonHandCard4.addMouseListener(this);
        panel4.add(buttonHandCard4);
        
        buttonHandCard5 = new JButton();
        buttonHandCard5.setIcon(new RescaledIcon(this.getClass().getResource(consideredHandCards.Cards[5].upMonster.cardPathAtt)));
        YuGiOhJi.setButtonProperties(buttonHandCard5, 516, 25, 67, 100, numberOfConsideredHandCards>=5);
        buttonHandCard5.addActionListener(this);
        buttonHandCard5.addMouseListener(this);
        panel4.add(buttonHandCard5);
        
        buttonHandCard6 = new JButton();
        buttonHandCard6.setIcon(new RescaledIcon(this.getClass().getResource(consideredHandCards.Cards[6].upMonster.cardPathAtt)));
        YuGiOhJi.setButtonProperties(buttonHandCard6, 616, 25, 67, 100, numberOfConsideredHandCards>=6);
        buttonHandCard6.addActionListener(this);
        buttonHandCard6.addMouseListener(this);
        panel4.add(buttonHandCard6);
        
        buttonHandCard7 = new JButton();
        buttonHandCard7.setIcon(new RescaledIcon(this.getClass().getResource(consideredHandCards.Cards[7].upMonster.cardPathAtt)));
        YuGiOhJi.setButtonProperties(buttonHandCard7, 716, 25, 67, 100, numberOfConsideredHandCards>=7);
        buttonHandCard7.addActionListener(this);
        buttonHandCard7.addMouseListener(this);
        panel4.add(buttonHandCard7);
        
        buttonHandCard8 = new JButton();
        buttonHandCard8.setIcon(new RescaledIcon(this.getClass().getResource(consideredHandCards.Cards[8].upMonster.cardPathAtt)));
        YuGiOhJi.setButtonProperties(buttonHandCard8, 816, 25, 67, 100, numberOfConsideredHandCards>=8);
        buttonHandCard8.addActionListener(this);
        buttonHandCard8.addMouseListener(this);
        panel4.add(buttonHandCard8);
        
        buttonHandCard9 = new JButton();
        buttonHandCard9.setIcon(new RescaledIcon(this.getClass().getResource(consideredHandCards.Cards[9].upMonster.cardPathAtt)));
        YuGiOhJi.setButtonProperties(buttonHandCard9, 916, 25, 67, 100, numberOfConsideredHandCards>=9);
        buttonHandCard9.addActionListener(this);
        buttonHandCard9.addMouseListener(this);
        panel4.add(buttonHandCard9);
        
        buttonHandCard10 = new JButton();
        buttonHandCard10.setIcon(new RescaledIcon(this.getClass().getResource(consideredHandCards.Cards[10].upMonster.cardPathAtt)));
        YuGiOhJi.setButtonProperties(buttonHandCard10, 1016, 25, 67, 100, numberOfConsideredHandCards>=10);
        buttonHandCard10.addActionListener(this);
        buttonHandCard10.addMouseListener(this);
        panel4.add(buttonHandCard10);
        
        
        displayMonsterNo1 = new JLabel("" + getNthConsideredMonster(1).sumMonsterNumber);
        displayMonsterNo1.setBounds(150, 125, 50, 25);
        displayMonsterNo1.setVisible(numberOfConsideredMonsters>=1);
        panel4.add(displayMonsterNo1);
        
        displayMonsterNo2 = new JLabel("" + getNthConsideredMonster(2).sumMonsterNumber);
        displayMonsterNo2.setBounds(250, 125, 50, 25);
        displayMonsterNo2.setVisible(numberOfConsideredMonsters>=2);
        panel4.add(displayMonsterNo2);
        
        displayMonsterNo3 = new JLabel("" + getNthConsideredMonster(3).sumMonsterNumber);
        displayMonsterNo3.setBounds(350, 125, 50, 25);
        displayMonsterNo3.setVisible(numberOfConsideredMonsters>=3);
        panel4.add(displayMonsterNo3);
        
        displayMonsterNo4 = new JLabel("" + getNthConsideredMonster(4).sumMonsterNumber);
        displayMonsterNo4.setBounds(450, 125, 50, 25);
        displayMonsterNo4.setVisible(numberOfConsideredMonsters>=4);
        panel4.add(displayMonsterNo4);
        
        displayMonsterNo5 = new JLabel("" + getNthConsideredMonster(5).sumMonsterNumber);
        displayMonsterNo5.setBounds(550, 125, 50, 25);
        displayMonsterNo5.setVisible(numberOfConsideredMonsters>=5);
        panel4.add(displayMonsterNo5);
        
        buttonMonsterCard1 = new JButton();
        if (considSumMonster1.isFaceDown) {
            buttonMonsterCard1.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
            YuGiOhJi.setButtonProperties(buttonMonsterCard1, 100, 166, 100, 67, numberOfConsideredMonsters>=1);
        }
        else {
            if (considSumMonster1.isInAttackMode) {
                buttonMonsterCard1.setIcon(new RescaledIcon(this.getClass().getResource(considSumMonster1.Monster.cardPathAtt)));
                YuGiOhJi.setButtonProperties(buttonMonsterCard1, 116, 150, 67, 100, numberOfConsideredMonsters>=1);
            }
            else {
                buttonMonsterCard1.setIcon(new RescaledIcon(this.getClass().getResource(considSumMonster1.Monster.cardPathDef)));
                YuGiOhJi.setButtonProperties(buttonMonsterCard1, 100, 166, 100, 67, numberOfConsideredMonsters>=1);
            }
        }
        buttonMonsterCard1.addActionListener(this);
        buttonMonsterCard1.addMouseListener(this);
        panel4.add(buttonMonsterCard1);
        
        buttonMonsterCard2 = new JButton();
        if (considSumMonster2.isFaceDown) {
            buttonMonsterCard2.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
            YuGiOhJi.setButtonProperties(buttonMonsterCard2, 200, 166, 100, 67, numberOfConsideredMonsters>=2);
        }
        else {
            if (considSumMonster2.isInAttackMode) {
                buttonMonsterCard2.setIcon(new RescaledIcon(this.getClass().getResource(considSumMonster2.Monster.cardPathAtt)));
                YuGiOhJi.setButtonProperties(buttonMonsterCard2, 216, 150, 67, 100, numberOfConsideredMonsters>=2);
            }
            else {
                buttonMonsterCard2.setIcon(new RescaledIcon(this.getClass().getResource(considSumMonster2.Monster.cardPathDef)));
                YuGiOhJi.setButtonProperties(buttonMonsterCard2, 200, 166, 100, 67, numberOfConsideredMonsters>=2);
            }
        }
        buttonMonsterCard2.addActionListener(this);
        buttonMonsterCard2.addMouseListener(this);
        panel4.add(buttonMonsterCard2);
        
        buttonMonsterCard3 = new JButton();
        if (considSumMonster3.isFaceDown) {
            buttonMonsterCard3.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
            YuGiOhJi.setButtonProperties(buttonMonsterCard3, 300, 166, 100, 67, numberOfConsideredMonsters>=3);
        }
        else {
            if (considSumMonster3.isInAttackMode) {
                buttonMonsterCard3.setIcon(new RescaledIcon(this.getClass().getResource(considSumMonster3.Monster.cardPathAtt)));
                YuGiOhJi.setButtonProperties(buttonMonsterCard3, 316, 150, 67, 100, numberOfConsideredMonsters>=3);
            }
            else {
                buttonMonsterCard3.setIcon(new RescaledIcon(this.getClass().getResource(considSumMonster3.Monster.cardPathDef)));
                YuGiOhJi.setButtonProperties(buttonMonsterCard3, 300, 166, 100, 67, numberOfConsideredMonsters>=3);
            }
        }
        buttonMonsterCard3.addActionListener(this);
        buttonMonsterCard3.addMouseListener(this);
        panel4.add(buttonMonsterCard3);
        
        buttonMonsterCard4 = new JButton();
        if (considSumMonster4.isFaceDown) {
            buttonMonsterCard4.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
            YuGiOhJi.setButtonProperties(buttonMonsterCard4, 400, 166, 100, 67, numberOfConsideredMonsters>=4);
        }
        else {
            if (considSumMonster4.isInAttackMode) {
                buttonMonsterCard4.setIcon(new RescaledIcon(this.getClass().getResource(considSumMonster4.Monster.cardPathAtt)));
                YuGiOhJi.setButtonProperties(buttonMonsterCard4, 416, 150, 67, 100, numberOfConsideredMonsters>=4);
            }
            else {
                buttonMonsterCard4.setIcon(new RescaledIcon(this.getClass().getResource(considSumMonster4.Monster.cardPathDef)));
                YuGiOhJi.setButtonProperties(buttonMonsterCard4, 400, 166, 100, 67, numberOfConsideredMonsters>=4);
            }
        }
        buttonMonsterCard4.addActionListener(this);
        buttonMonsterCard4.addMouseListener(this);
        panel4.add(buttonMonsterCard4);
        
        buttonMonsterCard5 = new JButton();
        if (considSumMonster5.isFaceDown) {
            buttonMonsterCard5.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
            YuGiOhJi.setButtonProperties(buttonMonsterCard5, 500, 166, 100, 67, numberOfConsideredMonsters>=5);
        }
        else {
            if (considSumMonster5.isInAttackMode) {
                buttonMonsterCard5.setIcon(new RescaledIcon(this.getClass().getResource(considSumMonster5.Monster.cardPathAtt)));
                YuGiOhJi.setButtonProperties(buttonMonsterCard5, 516, 150, 67, 100, numberOfConsideredMonsters>=5);
            }
            else {
                buttonMonsterCard5.setIcon(new RescaledIcon(this.getClass().getResource(considSumMonster5.Monster.cardPathDef)));
                YuGiOhJi.setButtonProperties(buttonMonsterCard5, 500, 166, 100, 67, numberOfConsideredMonsters>=5);
            }
        }
        buttonMonsterCard5.addActionListener(this);
        buttonMonsterCard5.addMouseListener(this);
        panel4.add(buttonMonsterCard5);
        
        
        // --- GY from here on ---
        
        
        buttonGYCard1 = new JButton();
        if (numberOfConsideredGYCards>=1) {
            buttonGYCard1.setIcon(new RescaledIcon(this.getClass().getResource(consideredGYCards.getNthCardOfDeck(1).upMonster.cardPathAtt)));
        }
        YuGiOhJi.setButtonProperties(buttonGYCard1, 116, 275, 67, 100, numberOfConsideredGYCards>=1);
        buttonGYCard1.addActionListener(this);
        buttonGYCard1.addMouseListener(this);
        panel4.add(buttonGYCard1);
        
        buttonGYCard2 = new JButton();
        if (numberOfConsideredGYCards>=2) {
            buttonGYCard2.setIcon(new RescaledIcon(this.getClass().getResource(consideredGYCards.getNthCardOfDeck(2).upMonster.cardPathAtt)));
        }
        YuGiOhJi.setButtonProperties(buttonGYCard2, 216, 275, 67, 100, numberOfConsideredGYCards>=2);
        buttonGYCard2.addActionListener(this);
        buttonGYCard2.addMouseListener(this);
        panel4.add(buttonGYCard2);
        
        buttonGYCard3 = new JButton();
        if (numberOfConsideredGYCards>=3) {
            buttonGYCard3.setIcon(new RescaledIcon(this.getClass().getResource(consideredGYCards.getNthCardOfDeck(3).upMonster.cardPathAtt)));
        }
        YuGiOhJi.setButtonProperties(buttonGYCard3, 316, 275, 67, 100, numberOfConsideredGYCards>=3);
        buttonGYCard3.addActionListener(this);
        buttonGYCard3.addMouseListener(this);
        panel4.add(buttonGYCard3);
        
        buttonGYCard4 = new JButton();
        if (numberOfConsideredGYCards>=4) {
            buttonGYCard4.setIcon(new RescaledIcon(this.getClass().getResource(consideredGYCards.getNthCardOfDeck(4).upMonster.cardPathAtt)));
        }
        YuGiOhJi.setButtonProperties(buttonGYCard4, 416, 275, 67, 100, numberOfConsideredGYCards>=4);
        buttonGYCard4.addActionListener(this);
        buttonGYCard4.addMouseListener(this);
        panel4.add(buttonGYCard4);
        
        buttonGYCard5 = new JButton();
        if (numberOfConsideredGYCards>=5) {
            buttonGYCard5.setIcon(new RescaledIcon(this.getClass().getResource(consideredGYCards.getNthCardOfDeck(5).upMonster.cardPathAtt)));
        }
        YuGiOhJi.setButtonProperties(buttonGYCard5, 516, 275, 67, 100, numberOfConsideredGYCards>=5);
        buttonGYCard5.addActionListener(this);
        buttonGYCard5.addMouseListener(this);
        panel4.add(buttonGYCard5);
        
        buttonGYCard6 = new JButton();
        if (numberOfConsideredGYCards>=6) {
            buttonGYCard6.setIcon(new RescaledIcon(this.getClass().getResource(consideredGYCards.getNthCardOfDeck(6).upMonster.cardPathAtt)));
        }
        YuGiOhJi.setButtonProperties(buttonGYCard6, 616, 275, 67, 100, numberOfConsideredGYCards>=6);
        buttonGYCard6.addActionListener(this);
        buttonGYCard6.addMouseListener(this);
        panel4.add(buttonGYCard6);
        
        buttonGYCard7 = new JButton();
        if (numberOfConsideredGYCards>=7) {
            buttonGYCard7.setIcon(new RescaledIcon(this.getClass().getResource(consideredGYCards.getNthCardOfDeck(7).upMonster.cardPathAtt)));
        }
        YuGiOhJi.setButtonProperties(buttonGYCard7, 716, 275, 67, 100, numberOfConsideredGYCards>=7);
        buttonGYCard7.addActionListener(this);
        buttonGYCard7.addMouseListener(this);
        panel4.add(buttonGYCard7);
        
        buttonGYCard8 = new JButton();
        if (numberOfConsideredGYCards>=8) {
            buttonGYCard8.setIcon(new RescaledIcon(this.getClass().getResource(consideredGYCards.getNthCardOfDeck(8).upMonster.cardPathAtt)));
        }
        YuGiOhJi.setButtonProperties(buttonGYCard8, 816, 275, 67, 100, numberOfConsideredGYCards>=8);
        buttonGYCard8.addActionListener(this);
        buttonGYCard8.addMouseListener(this);
        panel4.add(buttonGYCard8);
        
        buttonGYCard9 = new JButton();
        if (numberOfConsideredGYCards>=9) {
            buttonGYCard9.setIcon(new RescaledIcon(this.getClass().getResource(consideredGYCards.getNthCardOfDeck(9).upMonster.cardPathAtt)));
        }
        YuGiOhJi.setButtonProperties(buttonGYCard9, 916, 275, 67, 100, numberOfConsideredGYCards>=9);
        buttonGYCard9.addActionListener(this);
        buttonGYCard9.addMouseListener(this);
        panel4.add(buttonGYCard9);
        
        buttonGYCard10 = new JButton();
        if (numberOfConsideredGYCards>=10) {
            buttonGYCard10.setIcon(new RescaledIcon(this.getClass().getResource(consideredGYCards.getNthCardOfDeck(10).upMonster.cardPathAtt)));
        }
        YuGiOhJi.setButtonProperties(buttonGYCard10, 1016, 275, 67, 100, numberOfConsideredGYCards>=10);
        buttonGYCard10.addActionListener(this);
        buttonGYCard10.addMouseListener(this);
        panel4.add(buttonGYCard10);
        
        buttonGYCard11 = new JButton();
        if (numberOfConsideredGYCards>=11) {
            buttonGYCard11.setIcon(new RescaledIcon(this.getClass().getResource(consideredGYCards.getNthCardOfDeck(11).upMonster.cardPathAtt)));
        }
        YuGiOhJi.setButtonProperties(buttonGYCard11, 1116, 275, 67, 100, numberOfConsideredGYCards>=11);
        buttonGYCard11.addActionListener(this);
        buttonGYCard11.addMouseListener(this);
        panel4.add(buttonGYCard11);
        
        buttonGYCard12 = new JButton();
        if (numberOfConsideredGYCards>=12) {
            buttonGYCard12.setIcon(new RescaledIcon(this.getClass().getResource(consideredGYCards.getNthCardOfDeck(12).upMonster.cardPathAtt)));
        }
        YuGiOhJi.setButtonProperties(buttonGYCard12, 1216, 275, 67, 100, numberOfConsideredGYCards>=12);
        buttonGYCard12.addActionListener(this);
        buttonGYCard12.addMouseListener(this);
        panel4.add(buttonGYCard12);
        
        buttonGYCard13 = new JButton();
        if (numberOfConsideredGYCards>=13) {
            buttonGYCard13.setIcon(new RescaledIcon(this.getClass().getResource(consideredGYCards.getNthCardOfDeck(13).upMonster.cardPathAtt)));
        }
        YuGiOhJi.setButtonProperties(buttonGYCard13, 1316, 275, 67, 100, numberOfConsideredGYCards>=13);
        buttonGYCard13.addActionListener(this);
        buttonGYCard13.addMouseListener(this);
        panel4.add(buttonGYCard13);
        
        buttonGYCard14 = new JButton();
        if (numberOfConsideredGYCards>=14) {
            buttonGYCard14.setIcon(new RescaledIcon(this.getClass().getResource(consideredGYCards.getNthCardOfDeck(14).upMonster.cardPathAtt)));
        }
        YuGiOhJi.setButtonProperties(buttonGYCard14, 1416, 275, 67, 100, numberOfConsideredGYCards>=14);
        buttonGYCard14.addActionListener(this);
        buttonGYCard14.addMouseListener(this);
        panel4.add(buttonGYCard14);
        
        buttonGYCard15 = new JButton();
        if (numberOfConsideredGYCards>=15) {
            buttonGYCard15.setIcon(new RescaledIcon(this.getClass().getResource(consideredGYCards.getNthCardOfDeck(15).upMonster.cardPathAtt)));
        }
        YuGiOhJi.setButtonProperties(buttonGYCard15, 1516, 275, 67, 100, numberOfConsideredGYCards>=15);
        buttonGYCard15.addActionListener(this);
        buttonGYCard15.addMouseListener(this);
        panel4.add(buttonGYCard15);
        
        buttonGYCard16 = new JButton();
        if (numberOfConsideredGYCards>=16) {
            buttonGYCard16.setIcon(new RescaledIcon(this.getClass().getResource(consideredGYCards.getNthCardOfDeck(16).upMonster.cardPathAtt)));
        }
        YuGiOhJi.setButtonProperties(buttonGYCard16, 1616, 275, 67, 100, numberOfConsideredGYCards>=16);
        buttonGYCard16.addActionListener(this);
        buttonGYCard16.addMouseListener(this);
        panel4.add(buttonGYCard16);
        
        buttonGYCard17 = new JButton();
        if (numberOfConsideredGYCards>=17) {
            buttonGYCard17.setIcon(new RescaledIcon(this.getClass().getResource(consideredGYCards.getNthCardOfDeck(17).upMonster.cardPathAtt)));
        }
        YuGiOhJi.setButtonProperties(buttonGYCard17, 1716, 275, 67, 100, numberOfConsideredGYCards>=17);
        buttonGYCard17.addActionListener(this);
        buttonGYCard17.addMouseListener(this);
        panel4.add(buttonGYCard17);
        
        buttonGYCard18 = new JButton();
        if (numberOfConsideredGYCards>=18) {
            buttonGYCard18.setIcon(new RescaledIcon(this.getClass().getResource(consideredGYCards.getNthCardOfDeck(18).upMonster.cardPathAtt)));
        }
        YuGiOhJi.setButtonProperties(buttonGYCard18, 1816, 275, 67, 100, numberOfConsideredGYCards>=18);
        buttonGYCard18.addActionListener(this);
        buttonGYCard18.addMouseListener(this);
        panel4.add(buttonGYCard18);
        
        buttonGYCard19 = new JButton();
        if (numberOfConsideredGYCards>=19) {
            buttonGYCard19.setIcon(new RescaledIcon(this.getClass().getResource(consideredGYCards.getNthCardOfDeck(19).upMonster.cardPathAtt)));
        }
        YuGiOhJi.setButtonProperties(buttonGYCard19, 1916, 275, 67, 100, numberOfConsideredGYCards>=19);
        buttonGYCard19.addActionListener(this);
        buttonGYCard19.addMouseListener(this);
        panel4.add(buttonGYCard19);
        
        buttonGYCard20 = new JButton();
        if (numberOfConsideredGYCards>=20) {
            buttonGYCard20.setIcon(new RescaledIcon(this.getClass().getResource(consideredGYCards.getNthCardOfDeck(20).upMonster.cardPathAtt)));
        }
        YuGiOhJi.setButtonProperties(buttonGYCard20, 2016, 275, 67, 100, numberOfConsideredGYCards>=20);
        buttonGYCard20.addActionListener(this);
        buttonGYCard20.addMouseListener(this);
        panel4.add(buttonGYCard20);
        
        buttonGYCard21 = new JButton();
        if (numberOfConsideredGYCards>=21) {
            buttonGYCard21.setIcon(new RescaledIcon(this.getClass().getResource(consideredGYCards.getNthCardOfDeck(21).upMonster.cardPathAtt)));
        }
        YuGiOhJi.setButtonProperties(buttonGYCard21, 2116, 275, 67, 100, numberOfConsideredGYCards>=21);
        buttonGYCard21.addActionListener(this);
        buttonGYCard21.addMouseListener(this);
        panel4.add(buttonGYCard21);
        
        buttonGYCard22 = new JButton();
        if (numberOfConsideredGYCards>=22) {
            buttonGYCard22.setIcon(new RescaledIcon(this.getClass().getResource(consideredGYCards.getNthCardOfDeck(22).upMonster.cardPathAtt)));
        }
        YuGiOhJi.setButtonProperties(buttonGYCard22, 2216, 275, 67, 100, numberOfConsideredGYCards>=22);
        buttonGYCard22.addActionListener(this);
        buttonGYCard22.addMouseListener(this);
        panel4.add(buttonGYCard22);
        
        buttonGYCard23 = new JButton();
        if (numberOfConsideredGYCards>=23) {
            buttonGYCard23.setIcon(new RescaledIcon(this.getClass().getResource(consideredGYCards.getNthCardOfDeck(23).upMonster.cardPathAtt)));
        }
        YuGiOhJi.setButtonProperties(buttonGYCard23, 2316, 275, 67, 100, numberOfConsideredGYCards>=23);
        buttonGYCard23.addActionListener(this);
        buttonGYCard23.addMouseListener(this);
        panel4.add(buttonGYCard23);
        
        buttonGYCard24 = new JButton();
        if (numberOfConsideredGYCards>=24) {
            buttonGYCard24.setIcon(new RescaledIcon(this.getClass().getResource(consideredGYCards.getNthCardOfDeck(24).upMonster.cardPathAtt)));
        }
        YuGiOhJi.setButtonProperties(buttonGYCard24, 2416, 275, 67, 100, numberOfConsideredGYCards>=24);
        buttonGYCard24.addActionListener(this);
        buttonGYCard24.addMouseListener(this);
        panel4.add(buttonGYCard24);
        
        buttonGYCard25 = new JButton();
        if (numberOfConsideredGYCards>=25) {
            buttonGYCard25.setIcon(new RescaledIcon(this.getClass().getResource(consideredGYCards.getNthCardOfDeck(25).upMonster.cardPathAtt)));
        }
        YuGiOhJi.setButtonProperties(buttonGYCard25, 2516, 275, 67, 100, numberOfConsideredGYCards>=25);
        buttonGYCard25.addActionListener(this);
        buttonGYCard25.addMouseListener(this);
        panel4.add(buttonGYCard25);
        
        buttonGYCard26 = new JButton();
        if (numberOfConsideredGYCards>=26) {
            buttonGYCard26.setIcon(new RescaledIcon(this.getClass().getResource(consideredGYCards.getNthCardOfDeck(26).upMonster.cardPathAtt)));
        }
        YuGiOhJi.setButtonProperties(buttonGYCard26, 2616, 275, 67, 100, numberOfConsideredGYCards>=26);
        buttonGYCard26.addActionListener(this);
        buttonGYCard26.addMouseListener(this);
        panel4.add(buttonGYCard26);
        
        buttonGYCard27 = new JButton();
        if (numberOfConsideredGYCards>=27) {
            buttonGYCard27.setIcon(new RescaledIcon(this.getClass().getResource(consideredGYCards.getNthCardOfDeck(27).upMonster.cardPathAtt)));
        }
        YuGiOhJi.setButtonProperties(buttonGYCard27, 2716, 275, 67, 100, numberOfConsideredGYCards>=27);
        buttonGYCard27.addActionListener(this);
        buttonGYCard27.addMouseListener(this);
        panel4.add(buttonGYCard27);
        
        buttonGYCard28 = new JButton();
        if (numberOfConsideredGYCards>=28) {
            buttonGYCard28.setIcon(new RescaledIcon(this.getClass().getResource(consideredGYCards.getNthCardOfDeck(28).upMonster.cardPathAtt)));
        }
        YuGiOhJi.setButtonProperties(buttonGYCard28, 2816, 275, 67, 100, numberOfConsideredGYCards>=28);
        buttonGYCard28.addActionListener(this);
        buttonGYCard28.addMouseListener(this);
        panel4.add(buttonGYCard28);
        
        buttonGYCard29 = new JButton();
        if (numberOfConsideredGYCards>=29) {
            buttonGYCard29.setIcon(new RescaledIcon(this.getClass().getResource(consideredGYCards.getNthCardOfDeck(29).upMonster.cardPathAtt)));
        }
        YuGiOhJi.setButtonProperties(buttonGYCard29, 2916, 275, 67, 100, numberOfConsideredGYCards>=29);
        buttonGYCard29.addActionListener(this);
        buttonGYCard29.addMouseListener(this);
        panel4.add(buttonGYCard29);
        
        buttonGYCard30 = new JButton();
        if (numberOfConsideredGYCards>=30) {
            buttonGYCard30.setIcon(new RescaledIcon(this.getClass().getResource(consideredGYCards.getNthCardOfDeck(30).upMonster.cardPathAtt)));
        }
        YuGiOhJi.setButtonProperties(buttonGYCard30, 3016, 275, 67, 100, numberOfConsideredGYCards>=30);
        buttonGYCard30.addActionListener(this);
        buttonGYCard30.addMouseListener(this);
        panel4.add(buttonGYCard30);
        
        buttonGYCard31 = new JButton();
        if (numberOfConsideredGYCards>=31) {
            buttonGYCard31.setIcon(new RescaledIcon(this.getClass().getResource(consideredGYCards.getNthCardOfDeck(31).upMonster.cardPathAtt)));
        }
        YuGiOhJi.setButtonProperties(buttonGYCard31, 3116, 275, 67, 100, numberOfConsideredGYCards>=31);
        buttonGYCard31.addActionListener(this);
        buttonGYCard31.addMouseListener(this);
        panel4.add(buttonGYCard31);
        
        buttonGYCard32 = new JButton();
        if (numberOfConsideredGYCards>=32) {
            buttonGYCard32.setIcon(new RescaledIcon(this.getClass().getResource(consideredGYCards.getNthCardOfDeck(32).upMonster.cardPathAtt)));
        }
        YuGiOhJi.setButtonProperties(buttonGYCard32, 3216, 275, 67, 100, numberOfConsideredGYCards>=32);
        buttonGYCard32.addActionListener(this);
        buttonGYCard32.addMouseListener(this);
        panel4.add(buttonGYCard32);
        
        buttonGYCard33 = new JButton();
        if (numberOfConsideredGYCards>=33) {
            buttonGYCard33.setIcon(new RescaledIcon(this.getClass().getResource(consideredGYCards.getNthCardOfDeck(33).upMonster.cardPathAtt)));
        }
        YuGiOhJi.setButtonProperties(buttonGYCard33, 3316, 275, 67, 100, numberOfConsideredGYCards>=33);
        buttonGYCard33.addActionListener(this);
        buttonGYCard33.addMouseListener(this);
        panel4.add(buttonGYCard33);
        
        buttonGYCard34 = new JButton();
        if (numberOfConsideredGYCards>=34) {
            buttonGYCard34.setIcon(new RescaledIcon(this.getClass().getResource(consideredGYCards.getNthCardOfDeck(34).upMonster.cardPathAtt)));
        }
        YuGiOhJi.setButtonProperties(buttonGYCard34, 3416, 275, 67, 100, numberOfConsideredGYCards>=34);
        buttonGYCard34.addActionListener(this);
        buttonGYCard34.addMouseListener(this);
        panel4.add(buttonGYCard34);
        
        buttonGYCard35 = new JButton();
        if (numberOfConsideredGYCards>=35) {
            buttonGYCard35.setIcon(new RescaledIcon(this.getClass().getResource(consideredGYCards.getNthCardOfDeck(35).upMonster.cardPathAtt)));
        }
        YuGiOhJi.setButtonProperties(buttonGYCard35, 3516, 275, 67, 100, numberOfConsideredGYCards>=35);
        buttonGYCard35.addActionListener(this);
        buttonGYCard35.addMouseListener(this);
        panel4.add(buttonGYCard35);
        
        buttonGYCard36 = new JButton();
        if (numberOfConsideredGYCards>=36) {
            buttonGYCard36.setIcon(new RescaledIcon(this.getClass().getResource(consideredGYCards.getNthCardOfDeck(36).upMonster.cardPathAtt)));
        }
        YuGiOhJi.setButtonProperties(buttonGYCard36, 3616, 275, 67, 100, numberOfConsideredGYCards>=36);
        buttonGYCard36.addActionListener(this);
        buttonGYCard36.addMouseListener(this);
        panel4.add(buttonGYCard36);
        
        buttonGYCard37 = new JButton();
        if (numberOfConsideredGYCards>=37) {
            buttonGYCard37.setIcon(new RescaledIcon(this.getClass().getResource(consideredGYCards.getNthCardOfDeck(37).upMonster.cardPathAtt)));
        }
        YuGiOhJi.setButtonProperties(buttonGYCard37, 3716, 275, 67, 100, numberOfConsideredGYCards>=37);
        buttonGYCard37.addActionListener(this);
        buttonGYCard37.addMouseListener(this);
        panel4.add(buttonGYCard37);
        
        buttonGYCard38 = new JButton();
        if (numberOfConsideredGYCards>=38) {
            buttonGYCard38.setIcon(new RescaledIcon(this.getClass().getResource(consideredGYCards.getNthCardOfDeck(38).upMonster.cardPathAtt)));
        }
        YuGiOhJi.setButtonProperties(buttonGYCard38, 3816, 275, 67, 100, numberOfConsideredGYCards>=38);
        buttonGYCard38.addActionListener(this);
        buttonGYCard38.addMouseListener(this);
        panel4.add(buttonGYCard38);
        
        buttonGYCard39 = new JButton();
        if (numberOfConsideredGYCards>=39) {
            buttonGYCard39.setIcon(new RescaledIcon(this.getClass().getResource(consideredGYCards.getNthCardOfDeck(39).upMonster.cardPathAtt)));
        }
        YuGiOhJi.setButtonProperties(buttonGYCard39, 3916, 275, 67, 100, numberOfConsideredGYCards>=39);
        buttonGYCard39.addActionListener(this);
        buttonGYCard39.addMouseListener(this);
        panel4.add(buttonGYCard39);
        
        
        // --- equip cards from here on ---
        
        
        displayStackNo1 = new JLabel("" + getNthEquipCard(1).stackNumber);
        displayStackNo1.setBounds(150, 375, 50, 25);
        displayStackNo1.setVisible(numberOfConsideredEquipCards>=1);
        panel4.add(displayStackNo1);
        
        displayStackNo2 = new JLabel("" + getNthEquipCard(2).stackNumber);
        displayStackNo2.setBounds(250, 375, 50, 25);
        displayStackNo2.setVisible(numberOfConsideredEquipCards>=2);
        panel4.add(displayStackNo2);
        
        displayStackNo3 = new JLabel("" + getNthEquipCard(3).stackNumber);
        displayStackNo3.setBounds(350, 375, 50, 25);
        displayStackNo3.setVisible(numberOfConsideredEquipCards>=3);
        panel4.add(displayStackNo3);
        
        displayStackNo4 = new JLabel("" + getNthEquipCard(4).stackNumber);
        displayStackNo4.setBounds(450, 375, 50, 25);
        displayStackNo4.setVisible(numberOfConsideredEquipCards>=4);
        panel4.add(displayStackNo4);
        
        displayStackNo5 = new JLabel("" + getNthEquipCard(5).stackNumber);
        displayStackNo5.setBounds(550, 375, 50, 25);
        displayStackNo5.setVisible(numberOfConsideredEquipCards>=5);
        panel4.add(displayStackNo5);
        
        displayStackNo6 = new JLabel("" + getNthEquipCard(6).stackNumber);
        displayStackNo6.setBounds(650, 375, 50, 25);
        displayStackNo6.setVisible(numberOfConsideredEquipCards>=6);
        panel4.add(displayStackNo6);
        
        displayStackNo7 = new JLabel("" + getNthEquipCard(7).stackNumber);
        displayStackNo7.setBounds(750, 375, 50, 25);
        displayStackNo7.setVisible(numberOfConsideredEquipCards>=7);
        panel4.add(displayStackNo7);
        
        displayStackNo8 = new JLabel("" + getNthEquipCard(8).stackNumber);
        displayStackNo8.setBounds(850, 375, 50, 25);
        displayStackNo8.setVisible(numberOfConsideredEquipCards>=8);
        panel4.add(displayStackNo8);
        
        displayStackNo9 = new JLabel("" + getNthEquipCard(9).stackNumber);
        displayStackNo9.setBounds(950, 375, 50, 25);
        displayStackNo9.setVisible(numberOfConsideredEquipCards>=9);
        panel4.add(displayStackNo9);
        
        displayStackNo10 = new JLabel("" + getNthEquipCard(10).stackNumber);
        displayStackNo10.setBounds(1050, 375, 50, 25);
        displayStackNo10.setVisible(numberOfConsideredEquipCards>=10);
        panel4.add(displayStackNo10);
        
        displayStackNo11 = new JLabel("" + getNthEquipCard(11).stackNumber);
        displayStackNo11.setBounds(1150, 375, 50, 25);
        displayStackNo11.setVisible(numberOfConsideredEquipCards>=11);
        panel4.add(displayStackNo11);
        
        displayStackNo12 = new JLabel("" + getNthEquipCard(12).stackNumber);
        displayStackNo12.setBounds(1250, 375, 50, 25);
        displayStackNo12.setVisible(numberOfConsideredEquipCards>=12);
        panel4.add(displayStackNo12);
        
        displayStackNo13 = new JLabel("" + getNthEquipCard(13).stackNumber);
        displayStackNo13.setBounds(1350, 375, 50, 25);
        displayStackNo13.setVisible(numberOfConsideredEquipCards>=13);
        panel4.add(displayStackNo13);
        
        displayStackNo14 = new JLabel("" + getNthEquipCard(14).stackNumber);
        displayStackNo14.setBounds(1450, 375, 50, 25);
        displayStackNo14.setVisible(numberOfConsideredEquipCards>=14);
        panel4.add(displayStackNo14);
        
        displayStackNo15 = new JLabel("" + getNthEquipCard(15).stackNumber);
        displayStackNo15.setBounds(1550, 375, 50, 25);
        displayStackNo15.setVisible(numberOfConsideredEquipCards>=15);
        panel4.add(displayStackNo15);
        
        buttonEquipCard1 = new JButton();
        if (getNthEquipCard(1).isNegated) {
            buttonEquipCard1.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        }
        else {
            buttonEquipCard1.setIcon(new RescaledIcon(this.getClass().getResource(getNthEquipCard(1).Card.lowMonster.cardPathAtt)));
        }
        YuGiOhJi.setButtonProperties(buttonEquipCard1, 116, 400, 67, 100, numberOfConsideredEquipCards>=1);
        buttonEquipCard1.addActionListener(this);
        buttonEquipCard1.addMouseListener(this);
        panel4.add(buttonEquipCard1);
        
        buttonEquipCard2 = new JButton();
        if (getNthEquipCard(2).isNegated) {
            buttonEquipCard2.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        }
        else {
            buttonEquipCard2.setIcon(new RescaledIcon(this.getClass().getResource(getNthEquipCard(2).Card.lowMonster.cardPathAtt)));
        }
        YuGiOhJi.setButtonProperties(buttonEquipCard2, 216, 400, 67, 100, numberOfConsideredEquipCards>=2);
        buttonEquipCard2.addActionListener(this);
        buttonEquipCard2.addMouseListener(this);
        panel4.add(buttonEquipCard2);
        
        buttonEquipCard3 = new JButton();
        if (getNthEquipCard(3).isNegated) {
            buttonEquipCard3.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        }
        else {
            buttonEquipCard3.setIcon(new RescaledIcon(this.getClass().getResource(getNthEquipCard(3).Card.lowMonster.cardPathAtt)));
        }
        YuGiOhJi.setButtonProperties(buttonEquipCard3, 316, 400, 67, 100, numberOfConsideredEquipCards>=3);
        buttonEquipCard3.addActionListener(this);
        buttonEquipCard3.addMouseListener(this);
        panel4.add(buttonEquipCard3);
        
        buttonEquipCard4 = new JButton();
        if (getNthEquipCard(4).isNegated) {
            buttonEquipCard4.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        }
        else {
            buttonEquipCard4.setIcon(new RescaledIcon(this.getClass().getResource(getNthEquipCard(4).Card.lowMonster.cardPathAtt)));
        }
        YuGiOhJi.setButtonProperties(buttonEquipCard4, 416, 400, 67, 100, numberOfConsideredEquipCards>=4);
        buttonEquipCard4.addActionListener(this);
        buttonEquipCard4.addMouseListener(this);
        panel4.add(buttonEquipCard4);
        
        buttonEquipCard5 = new JButton();
        if (getNthEquipCard(5).isNegated) {
            buttonEquipCard5.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        }
        else {
            buttonEquipCard5.setIcon(new RescaledIcon(this.getClass().getResource(getNthEquipCard(5).Card.lowMonster.cardPathAtt)));
        }
        YuGiOhJi.setButtonProperties(buttonEquipCard5, 516, 400, 67, 100, numberOfConsideredEquipCards>=5);
        buttonEquipCard5.addActionListener(this);
        buttonEquipCard5.addMouseListener(this);
        panel4.add(buttonEquipCard5);
        
        buttonEquipCard6 = new JButton();
        if (getNthEquipCard(6).isNegated) {
            buttonEquipCard6.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        }
        else {
            buttonEquipCard6.setIcon(new RescaledIcon(this.getClass().getResource(getNthEquipCard(6).Card.lowMonster.cardPathAtt)));
        }
        YuGiOhJi.setButtonProperties(buttonEquipCard6, 616, 400, 67, 100, numberOfConsideredEquipCards>=6);
        buttonEquipCard6.addActionListener(this);
        buttonEquipCard6.addMouseListener(this);
        panel4.add(buttonEquipCard6);
        
        buttonEquipCard7 = new JButton();
        if (getNthEquipCard(7).isNegated) {
            buttonEquipCard7.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        }
        else {
            buttonEquipCard7.setIcon(new RescaledIcon(this.getClass().getResource(getNthEquipCard(7).Card.lowMonster.cardPathAtt)));
        }
        YuGiOhJi.setButtonProperties(buttonEquipCard7, 716, 400, 67, 100, numberOfConsideredEquipCards>=7);
        buttonEquipCard7.addActionListener(this);
        buttonEquipCard7.addMouseListener(this);
        panel4.add(buttonEquipCard7);
        
        buttonEquipCard8 = new JButton();
        if (getNthEquipCard(8).isNegated) {
            buttonEquipCard8.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        }
        else {
            buttonEquipCard8.setIcon(new RescaledIcon(this.getClass().getResource(getNthEquipCard(8).Card.lowMonster.cardPathAtt)));
        }
        YuGiOhJi.setButtonProperties(buttonEquipCard8, 816, 400, 67, 100, numberOfConsideredEquipCards>=8);
        buttonEquipCard8.addActionListener(this);
        buttonEquipCard8.addMouseListener(this);
        panel4.add(buttonEquipCard8);
        
        buttonEquipCard9 = new JButton();
        if (getNthEquipCard(9).isNegated) {
            buttonEquipCard9.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        }
        else {
            buttonEquipCard9.setIcon(new RescaledIcon(this.getClass().getResource(getNthEquipCard(9).Card.lowMonster.cardPathAtt)));
        }
        YuGiOhJi.setButtonProperties(buttonEquipCard9, 916, 400, 67, 100, numberOfConsideredEquipCards>=9);
        buttonEquipCard9.addActionListener(this);
        buttonEquipCard9.addMouseListener(this);
        panel4.add(buttonEquipCard9);
        
        buttonEquipCard10 = new JButton();
        if (getNthEquipCard(10).isNegated) {
            buttonEquipCard10.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        }
        else {
            buttonEquipCard10.setIcon(new RescaledIcon(this.getClass().getResource(getNthEquipCard(10).Card.lowMonster.cardPathAtt)));
        }
        YuGiOhJi.setButtonProperties(buttonEquipCard10, 1016, 400, 67, 100, numberOfConsideredEquipCards>=10);
        buttonEquipCard10.addActionListener(this);
        buttonEquipCard10.addMouseListener(this);
        panel4.add(buttonEquipCard10);
        
        buttonEquipCard11 = new JButton();
        if (getNthEquipCard(11).isNegated) {
            buttonEquipCard11.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        }
        else {
            buttonEquipCard11.setIcon(new RescaledIcon(this.getClass().getResource(getNthEquipCard(11).Card.lowMonster.cardPathAtt)));
        }
        YuGiOhJi.setButtonProperties(buttonEquipCard11, 1116, 400, 67, 100, numberOfConsideredEquipCards>=11);
        buttonEquipCard11.addActionListener(this);
        buttonEquipCard11.addMouseListener(this);
        panel4.add(buttonEquipCard11);
        
        buttonEquipCard12 = new JButton();
        if (getNthEquipCard(12).isNegated) {
            buttonEquipCard12.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        }
        else {
            buttonEquipCard12.setIcon(new RescaledIcon(this.getClass().getResource(getNthEquipCard(12).Card.lowMonster.cardPathAtt)));
        }
        YuGiOhJi.setButtonProperties(buttonEquipCard12, 1216, 400, 67, 100, numberOfConsideredEquipCards>=12);
        buttonEquipCard12.addActionListener(this);
        buttonEquipCard12.addMouseListener(this);
        panel4.add(buttonEquipCard12);
        
        buttonEquipCard13 = new JButton();
        if (getNthEquipCard(13).isNegated) {
            buttonEquipCard13.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        }
        else {
            buttonEquipCard13.setIcon(new RescaledIcon(this.getClass().getResource(getNthEquipCard(13).Card.lowMonster.cardPathAtt)));
        }
        YuGiOhJi.setButtonProperties(buttonEquipCard13, 1316, 400, 67, 100, numberOfConsideredEquipCards>=13);
        buttonEquipCard13.addActionListener(this);
        buttonEquipCard13.addMouseListener(this);
        panel4.add(buttonEquipCard13);
        
        buttonEquipCard14 = new JButton();
        if (getNthEquipCard(14).isNegated) {
            buttonEquipCard14.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        }
        else {
            buttonEquipCard14.setIcon(new RescaledIcon(this.getClass().getResource(getNthEquipCard(14).Card.lowMonster.cardPathAtt)));
        }
        YuGiOhJi.setButtonProperties(buttonEquipCard14, 1416, 400, 67, 100, numberOfConsideredEquipCards>=14);
        buttonEquipCard14.addActionListener(this);
        buttonEquipCard14.addMouseListener(this);
        panel4.add(buttonEquipCard14);
        
        buttonEquipCard15 = new JButton();
        if (getNthEquipCard(15).isNegated) {
            buttonEquipCard15.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        }
        else {
            buttonEquipCard15.setIcon(new RescaledIcon(this.getClass().getResource(getNthEquipCard(15).Card.lowMonster.cardPathAtt)));
        }
        YuGiOhJi.setButtonProperties(buttonEquipCard15, 1516, 400, 67, 100, numberOfConsideredEquipCards>=15);
        buttonEquipCard15.addActionListener(this);
        buttonEquipCard15.addMouseListener(this);
        panel4.add(buttonEquipCard15);
        
    }
    
    // --- methods ---
    
    // opens the window for choosing cards to tribute/discard/banish for paying costs
    public static void payCost (int costInSemipoints, String windowTitle){
        unselectEverything();
        toBePaidSemipoints=costInSemipoints;
        unconsiderCards();
        determineConsideredCards();
        window4 = new YChooseCardWindow(windowTitle);
        YChooseCardWindow.panel4.setVisible(true);
        moveButtons();
        double scaleFactorX = (double) YuGiOhJi.currentFrameWidth/YuGiOhJi.initialFrameWidth;
        double scaleFactorY = (double) YuGiOhJi.currentFrameHeight/YuGiOhJi.initialFrameHeight;
        int windowWidth = (int) Math.round(initialPayCostWidth*scaleFactorX*0.9);
        int windowHeight = (int) Math.round(initialPayCostHeight*scaleFactorY*0.95);
        cardChooserDialog.setSize(windowWidth, windowHeight);
        
    }
    
    // as cost to be paid: discard cards from the hand (if selected),
    // tributes summoned monsters (if selected), puts equip cards to the graveyard (if selected)
    // and banishes cards from the graveyard (if selected)
    public static void discardTributeBanishCardsAsCost(){
        cardChooserDialog.dispose();
        if (intTributeHandCardId1!=0) {
            int handCardNo = Hand.getHand(true).getPositionOfCardWithCardId(intTributeHandCardId1);
            LastSacrificedCard=Hand.getHand(true).getNthCardOfHand(handCardNo);
            YuGiOhJi.informationDialog("discarding " + LastSacrificedCard.cardName, "");
            Hand.discardCard(handCardNo, true);
        }
        if (intTributeHandCardId2!=0) {
            int handCardNo = Hand.getHand(true).getPositionOfCardWithCardId(intTributeHandCardId2);
            LastSacrificedCard=Hand.getHand(true).getNthCardOfHand(handCardNo);
            YuGiOhJi.informationDialog("discarding " + LastSacrificedCard.cardName, "");
            Hand.discardCard(handCardNo, true);
        }
        if (intTributeMonsterNo1!=0){
            LastSacrificedMonster=SummonedMonster.getNthSummonedMonster(intTributeMonsterNo1, true);
            LastSacrificedCard=LastSacrificedMonster.Card;
            YuGiOhJi.informationDialog("sending " + LastSacrificedMonster.Monster.monsterName + " to GY", "");
            SummonedMonster.getNthSummonedMonster(intTributeMonsterNo1, true).killMonster();
        }
        if (intTributeMonsterNo2!=0){
            LastSacrificedMonster=SummonedMonster.getNthSummonedMonster(intTributeMonsterNo2, true);
            LastSacrificedCard=LastSacrificedMonster.Card;
            YuGiOhJi.informationDialog("sending " + LastSacrificedMonster.Monster.monsterName + " to GY", "");
            SummonedMonster.getNthSummonedMonster(intTributeMonsterNo2, true).killMonster();
        }
        if (intTributeMonsterNo3!=0){
            LastSacrificedMonster=SummonedMonster.getNthSummonedMonster(intTributeMonsterNo3, true);
            LastSacrificedCard=LastSacrificedMonster.Card;
            YuGiOhJi.informationDialog("sending " + LastSacrificedMonster.Monster.monsterName + " to GY", "");
            SummonedMonster.getNthSummonedMonster(intTributeMonsterNo3, true).killMonster();
        }
        if (intTributeGYCardId1!=0) {
            int cardNoInGY = GYDeckPlayer.getPositionOfCardWithCardIdInDeck(intTributeGYCardId1);
            LastSacrificedCard=GYDeckPlayer.getNthCardOfDeck(cardNoInGY);
            YuGiOhJi.informationDialog("banishing " + LastSacrificedCard.cardName + " from GY", "");
            Deck.banishNthCardFromGY(cardNoInGY, true);
        }
        if (intTributeGYCardId2!=0) {
            int cardNoInGY = GYDeckPlayer.getPositionOfCardWithCardIdInDeck(intTributeGYCardId2);
            LastSacrificedCard=GYDeckPlayer.getNthCardOfDeck(cardNoInGY);
            YuGiOhJi.informationDialog("banishing " + LastSacrificedCard.cardName + " from GY", "");
            Deck.banishNthCardFromGY(cardNoInGY, true);
        }
        if (intTributeGYCardId3!=0) {
            int cardNoInGY = GYDeckPlayer.getPositionOfCardWithCardIdInDeck(intTributeGYCardId3);
            LastSacrificedCard=GYDeckPlayer.getNthCardOfDeck(cardNoInGY);
            YuGiOhJi.informationDialog("banishing " + LastSacrificedCard.cardName + " from GY", "");
            Deck.banishNthCardFromGY(cardNoInGY, true);
        }
        if (intTributeGYCardId4!=0) {
            int cardNoInGY = GYDeckPlayer.getPositionOfCardWithCardIdInDeck(intTributeGYCardId4);
            LastSacrificedCard=GYDeckPlayer.getNthCardOfDeck(cardNoInGY);
            YuGiOhJi.informationDialog("banishing " + LastSacrificedCard.cardName + " from GY", "");
            Deck.banishNthCardFromGY(cardNoInGY, true);
        }
        if (intTributeStackCardId1!=0) {
            int equipCardNo = StackOfTribute1.getPositionOfEquipCardByCardID(intTributeStackCardId1, true, intTributeStackCardNegationStatus1);
            LastSacrificedCard=StackOfTribute1.getNthCardOfStack(equipCardNo);
            YuGiOhJi.informationDialog("sending equip card " + LastSacrificedCard.cardName + " to GY", "");
            StackOfTribute1.sendNthEquipCardToGY(equipCardNo);
        }
        if (intTributeStackCardId2!=0) {
            int equipCardNo = StackOfTribute2.getPositionOfEquipCardByCardID(intTributeStackCardId2, true, intTributeStackCardNegationStatus2);
            LastSacrificedCard=StackOfTribute2.getNthCardOfStack(equipCardNo);
            YuGiOhJi.informationDialog("sending equip card " + LastSacrificedCard.cardName + " to GY", "");
            StackOfTribute2.sendNthEquipCardToGY(equipCardNo);
        }
        if (intTributeStackCardId3!=0) {
            int equipCardNo = StackOfTribute3.getPositionOfEquipCardByCardID(intTributeStackCardId3, true, intTributeStackCardNegationStatus3);
            LastSacrificedCard=StackOfTribute3.getNthCardOfStack(equipCardNo);
            YuGiOhJi.informationDialog("sending equip card " + LastSacrificedCard.cardName + " to GY", "");
            StackOfTribute3.sendNthEquipCardToGY(equipCardNo);
        }
        if (intTributeStackCardId4!=0) {
            int equipCardNo = StackOfTribute4.getPositionOfEquipCardByCardID(intTributeStackCardId4, true, intTributeStackCardNegationStatus4);
            LastSacrificedCard=StackOfTribute4.getNthCardOfStack(equipCardNo);
            YuGiOhJi.informationDialog("sending equip card " + LastSacrificedCard.cardName + " to GY", "");
            StackOfTribute4.sendNthEquipCardToGY(equipCardNo);
        }
        // finally reset every memory regarding the choose card window
        unselectEverything();
        unconsiderCards();
        possiblyInterceptEffect();
    }
    
    // asks the CPU to decide whether to negate the effect (if CPU has the corresponding hand trap)
    // otherwise one is told to choose the traget of the monster (if needed)
    public static void possiblyInterceptEffect() {
        // cancelling of effect 1 in YMonster class
        if (Game.isActEffBurner()) {
            Game.actEffOtherNumber = LastSacrificedCard.extractMookBurnDamage();
            boolean isCanceling = AIinterrupts.cpuIsUsingEffectNegate();
            if (!isCanceling) {YMonster.effectBurnerExecute(LastSacrificedCard);}
            else {Game.deactivateCurrentEffects();}
            LastSacrificedCard=NoCard;
        } // effect 3 needs to click on cards (doesn't call this window)
        // cancelling of effects 4 & 5 in SearchWindow class
        if (Game.isActEffSearch()) {SearchWindow.searchDeck(DeckPlayer, false, false, false, false, false);}
        if (Game.isActEffMilling()) {SearchWindow.searchDeck(DeckPlayer, true, false, false, false, false);}
        // cancelling of effect 6 in YMonster class
        if (Game.isActEffCopyCat()) {
            boolean isCanceling = AIinterrupts.cpuIsUsingEffectNegate();
            if (!isCanceling) {YMonster.effectCopyCatExecute(LastSacrificedCard);}
            else {Game.deactivateCurrentEffects();}
            LastSacrificedCard=NoCard;
        }
        if (Game.isActEffModeChanger()) { // cancelling of effect 8 in YMonster class
            Game.isReadyToChooseEffTarget=true;
            YuGiOhJi.informationDialog("Click on the monster you want to target.", "");
        } // cancelling of effects 9 & 10 (Necromancer) in YMonster class
        if (Game.isActEffReviveMook()) {
            YMonster.effectReviveMook(true);
        }
        if (Game.isActEffReviveMidboss()) {
            YMonster.effectReviveMidboss(true);
        } // cancelling of effect 11 in YMonster class
        if (Game.isActEffBigBackBouncer()) {
            Game.isReadyToChooseEffTarget=true;
            YuGiOhJi.informationDialog("Click on the card you want to target.", "");
        }
        if (Game.isActEffBigBanisher()) {
            Game.isReadyToChooseEffTarget=true;
            YuGiOhJi.informationDialog("Click on the card you want to target.", "");
        }
        if (Game.isActEffMonsterStealer()) {
            if (!SummonedMonster.hasStealableCards(false)) { // in case one tributed such, that the last valid target, was an equip card that has been put to the GY, becaus it equipped a tributed monster
                YuGiOhJi.informationDialog("no more valid targets", "Nothing happens.");
                Game.deactivateCurrentEffects();
            }
            else {
                Game.isReadyToChooseEffTarget=true;
                YuGiOhJi.informationDialog("Click on the card you want to target.", "");
            }
        }
        if (Game.isActEffEradicator()) { // in case one tributed such, that the last valid target, was an equip card that has been put to the GY, becaus it equipped a tributed monster
            if (!SummonedMonster.hasCardsDestructibleByEffect(false)) {
                YuGiOhJi.informationDialog("no more valid targets", "Nothing happens.");
                Game.deactivateCurrentEffects();
            }
            else {
                Game.isReadyToChooseEffTarget=true;
                YuGiOhJi.informationDialog("Click on the card you want to target.", "");
            }
        }
        if (Game.isActEffOptNeutraliser()) {
            if (!CardOptions.thereAreNegatableCards()) { // in case one tributed Neutraliser itself and there is no valid target any more
                YuGiOhJi.informationDialog("no more valid targets", "Nothing happens.");
                Game.deactivateCurrentEffects();
            }
            else {
                Game.isReadyToChooseEffTarget=true;
                YuGiOhJi.informationDialog("Click on the card you want to target.", "");
            }
        } // effects 16 through 18 are equip effects and cancelled in YMonster class or not at all (depending if equipping from field or hand)
        if (Game.isActSumEndbossHand()) {
            // special summoning itself from hand can not be negated
            for (int index = 1; index <= Hand.getHand(true).numberOfCards; index++){
                if (Hand.getHand(true).getNthCardOfHand(index).cardId==Game.actEffCardId) {
                    YCard.specialSummonMonster(Hand.getHand(true).getNthCardOfHand(index), Game.isActAboutUpperMon, false);
                    break;
                }
            }
            Game.deactivateCurrentEffects();
        }
        if (Game.isActSumEndbossGY()) { // special summoning itself from GY
            boolean isCanceling = AIinterrupts.cpuIsUsingBanisherHandTrapNegate();
            if (!isCanceling) {
                for (int index = 1; index <= GYDeckPlayer.numberOfCards; index++){
                    if (GYDeckPlayer.getNthCardOfDeck(index).cardId==Game.actEffCardId) {
                        YCard.specialSummonMonster(GYDeckPlayer.getNthCardOfDeck(index), Game.isActAboutUpperMon, true);
                        break;
                    }
                }
            }
            Game.deactivateCurrentEffects();
        } // effects 21 to 26 are tribute summonings (they can not be negated, because happen from hand)
        // 27 hand trap Neutraliser during own turn (doesn't open this window)
        // 28 passive effect Back Bouncer (doesn't open this window)
        if (Game.isActEffNeutraliserOpponentsTurn()) { // since this is an on field effect, one should in principle be able to negate it, but I leave that out for simplicity
            boolean isTributingAFightingMonster = false;
            if (LastSacrificedMonster.isBasicallySameMonster(Game.ActiveAttackingMonster) || LastSacrificedMonster.isBasicallySameMonster(Game.ActiveGuardingMonster)) {
                isTributingAFightingMonster = true;
            }
            YMonster.onFieldEffectNeutraliserDuringOpponentsTurnOrBattleExecute(isTributingAFightingMonster);
        }
        if (Game.isActEffNeutraliserOpponentsTurnEquip()) {YMonster.onFieldEffectNeutraliserDuringOpponentsTurnOnEquipCardExecute();}  // since this is an on field effect, one should in principle be able to negate it, but I leave that out for simplicity
         //about 31, 32 theoretically, one should be allowed to negate attack stopping effects with effect negates, but for simplicity I simply forbid that
        if (Game.isActEffAttackStopper() || Game.isActEffBigAttackStopper()) {
            YMonster.negateAttack(Game.ActiveAttackingMonster);
        } // all effects after these don't use this window
    }
    
    @Override
    public void componentHidden(ComponentEvent ce) {};
    @Override
    public void componentShown(ComponentEvent ce) {};
    @Override
    public void componentMoved(ComponentEvent ce) {};
    @Override
    public void componentResized(ComponentEvent ce) {
        currentPayCostWidth = cardChooserDialog.getWidth();
        currentPayCostHeight = cardChooserDialog.getHeight();
        rescaleEverything();
    };
    
    // calculates rescaling of horizontal coordinates (and widths)
    public static int rescaleX (int x) {
        int newValue = (int) Math.round(x*currentPayCostWidth/initialPayCostWidth);
        return newValue;
    }
    
    // calculates rescaling of vertical coordinates (and heights)
    public static int rescaleY (int y) {
        int newValue = (int) Math.round(y*currentPayCostHeight/initialPayCostHeight);
        return newValue;
    }
    
    // rescales a given button (uses current window size saved by global variables)
    public static void rescaleButton (JButton buttonName, int intialPosX, int intialPosY, int intialButtonWidth, int intialButtonHeight, boolean visibility) {
        buttonName.setBounds(rescaleX(intialPosX), rescaleY(intialPosY), rescaleX(intialButtonWidth), rescaleY(intialButtonHeight));
        buttonName.setVisible(visibility);
    }
    
    // rescales a given label (uses current window size saved by global variables)
    public static void rescaleLabel(JLabel labelName, int intialPosX, int intialPosY, int intialLabelWidth, int intialLabelHeight, boolean visibility) {
        labelName.setBounds(rescaleX(intialPosX), rescaleY(intialPosY), rescaleX(intialLabelWidth), rescaleY(intialLabelHeight));
        labelName.setVisible(visibility);
    }
    
    // simply rescales all graphical components, i.e. all buttons and labels, one by one
    public static void rescaleEverything() {
        // rescale all labels
        rescaleLabel(displayHandText, 10, (levelHand-1)*125, 100, 40, levelHand<5);
        rescaleLabel(displaySumOfPaidCost, 10, 75, 90, 25, true);
        rescaleLabel(displayMonsterText, 10, (levelMonster-1)*125, 90, 40, levelMonster<5);
        rescaleLabel(displayGYText, 10, (levelGY-1)*125, 90, 40, levelGY<5);
        rescaleLabel(displayEquipText, 10, (levelEquip-1)*125, 90, 40, levelEquip<5);
        rescaleLabel(displayMonsterNo1, 150, (levelMonster-1)*125, 50, 25, numberOfConsideredMonsters>=1);
        rescaleLabel(displayMonsterNo2, 250, (levelMonster-1)*125, 50, 25, numberOfConsideredMonsters>=2);
        rescaleLabel(displayMonsterNo3, 350, (levelMonster-1)*125, 50, 25, numberOfConsideredMonsters>=3);
        rescaleLabel(displayMonsterNo4, 450, (levelMonster-1)*125, 50, 25, numberOfConsideredMonsters>=4);
        rescaleLabel(displayMonsterNo5, 550, (levelMonster-1)*125, 50, 25, numberOfConsideredMonsters>=5);
        rescaleLabel(displayStackNo1, 150, (levelEquip-1)*125, 50, 25, numberOfConsideredEquipCards>=1);
        rescaleLabel(displayStackNo2, 250, (levelEquip-1)*125, 50, 25, numberOfConsideredEquipCards>=2);
        rescaleLabel(displayStackNo3, 350, (levelEquip-1)*125, 50, 25, numberOfConsideredEquipCards>=3);
        rescaleLabel(displayStackNo4, 450, (levelEquip-1)*125, 50, 25, numberOfConsideredEquipCards>=4);
        rescaleLabel(displayStackNo5, 550, (levelEquip-1)*125, 50, 25, numberOfConsideredEquipCards>=5);
        rescaleLabel(displayStackNo6, 650, (levelEquip-1)*125, 50, 25, numberOfConsideredEquipCards>=6);
        rescaleLabel(displayStackNo7, 750, (levelEquip-1)*125, 50, 25, numberOfConsideredEquipCards>=7);
        rescaleLabel(displayStackNo8, 850, (levelEquip-1)*125, 50, 25, numberOfConsideredEquipCards>=8);
        rescaleLabel(displayStackNo9, 950, (levelEquip-1)*125, 50, 25, numberOfConsideredEquipCards>=9);
        rescaleLabel(displayStackNo10, 1050, (levelEquip-1)*125, 50, 25, numberOfConsideredEquipCards>=10);
        rescaleLabel(displayStackNo11, 1150, (levelEquip-1)*125, 50, 25, numberOfConsideredEquipCards>=11);
        rescaleLabel(displayStackNo12, 1250, (levelEquip-1)*125, 50, 25, numberOfConsideredEquipCards>=12);
        rescaleLabel(displayStackNo13, 1350, (levelEquip-1)*125, 50, 25, numberOfConsideredEquipCards>=13);
        rescaleLabel(displayStackNo14, 1450, (levelEquip-1)*125, 50, 25, numberOfConsideredEquipCards>=14);
        rescaleLabel(displayStackNo15, 1550, (levelEquip-1)*125, 50, 25, numberOfConsideredEquipCards>=15);
        // rescale all buttons
        rescaleButtons();
        // rescale scroll pane
        currentScrollPaneWidth = rescaleX(initialScrollPaneWidth);
        currentScrollPaneHeight = rescaleY(initialScrollPaneHeight);
        panel4.setPreferredSize(new Dimension(currentScrollPaneWidth, currentScrollPaneHeight));
        // rescale fontsize
        
    }
    
    public static void rescaleButtons() {
        rescaleButton(buttonPayCost, 10, 50, 90, 25, true);
        for (int index = 1; index <= 10; index++){
            if (intTributeHandCardNo1==index || intTributeHandCardNo2==index) {
                rescaleButton(getNthHandButton(index), index*100, 25+16+(levelHand-1)*125, 100, 67, numberOfConsideredHandCards>=index);
            }
            else {
                rescaleButton(getNthHandButton(index), index*100+16, 25+(levelHand-1)*125, 67, 100, numberOfConsideredHandCards>=index);
            }
        }
        for (int index = 1; index <= 5; index++){
            if (nthMonsterAppearsInDef(index)) {
                rescaleButton(getNthMonsterButton(index), index*100, 25+16+(levelMonster-1)*125, 100, 67, numberOfConsideredMonsters>=index);
            }
            else {
                rescaleButton(getNthMonsterButton(index), index*100+16, 25+(levelMonster-1)*125, 67, 100, numberOfConsideredMonsters>=index);
            }
        }
        for (int index = 1; index <= 39; index++){
            if (intTributeGYCardNo1==index || intTributeGYCardNo2==index || intTributeGYCardNo3==index || intTributeGYCardNo4==index) {
                rescaleButton(getNthGYButton(index), index*100, 25+16+(levelGY-1)*125, 100, 67, numberOfConsideredGYCards>=index);
            }
            else {
                rescaleButton(getNthGYButton(index), index*100+16, 25+(levelGY-1)*125, 67, 100, numberOfConsideredGYCards>=index);
            }
        }
        for (int index = 1; index <= 15; index++){
            if (intTributeStackCardNo1==index || intTributeStackCardNo2==index || intTributeStackCardNo3==index || intTributeStackCardNo4==index) {
                rescaleButton(getNthEquipButton(index), index*100, 25+16+(levelEquip-1)*125, 100, 67, numberOfConsideredEquipCards>=index);
            }
            else {
                rescaleButton(getNthEquipButton(index), index*100+16, 25+(levelEquip-1)*125, 67, 100, numberOfConsideredEquipCards>=index);
            }
        }
    }
    
    public static JButton getNthHandButton (int n){
        switch (n) {
            case 1: return buttonHandCard1;
            case 2: return buttonHandCard2;
            case 3: return buttonHandCard3;
            case 4: return buttonHandCard4;
            case 5: return buttonHandCard5;
            case 6: return buttonHandCard6;
            case 7: return buttonHandCard7;
            case 8: return buttonHandCard8;
            case 9: return buttonHandCard9;
            case 10: return buttonHandCard10;
            default: YuGiOhJi.debugDialog("Error: out of bounds in YChooseCardWindow.getNthHandButton(...); attempted N: " + n); return null;
        }
    }
    
    public static JButton getNthMonsterButton (int n){
        switch (n) {
            case 1: return buttonMonsterCard1;
            case 2: return buttonMonsterCard2;
            case 3: return buttonMonsterCard3;
            case 4: return buttonMonsterCard4;
            case 5: return buttonMonsterCard5;
            default: YuGiOhJi.debugDialog("Error: out of bounds in YChooseCardWindow.getNthMonsterButton(...); attempted N: " + n); return null;
        }
    }
    
    public static boolean nthMonsterAppearsInDef (int n) {
        boolean isInAttMode;
        switch (n) {
            case 1: isInAttMode=considSumMonster1.isInAttackMode; break;
            case 2: isInAttMode=considSumMonster2.isInAttackMode; break;
            case 3: isInAttMode=considSumMonster3.isInAttackMode; break;
            case 4: isInAttMode=considSumMonster4.isInAttackMode; break;
            case 5: isInAttMode=considSumMonster5.isInAttackMode; break;
            default: YuGiOhJi.debugDialog("Error: out of bounds in YChooseCardWindow.nthMonsterAppearsInDef(...); attempted N: " + n); isInAttMode=false; break;
        }
        boolean isSelected = (intTributeMonsterNo1==getNthConsideredMonster(n).sumMonsterNumber || intTributeMonsterNo2==getNthConsideredMonster(n).sumMonsterNumber || intTributeMonsterNo3==getNthConsideredMonster(n).sumMonsterNumber);
        return ( (!isInAttMode && !isSelected) || (isInAttMode && isSelected) );
    }
    
    public static JButton getNthGYButton (int n){
        switch (n) {
            case 1: return buttonGYCard1;
            case 2: return buttonGYCard2;
            case 3: return buttonGYCard3;
            case 4: return buttonGYCard4;
            case 5: return buttonGYCard5;
            case 6: return buttonGYCard6;
            case 7: return buttonGYCard7;
            case 8: return buttonGYCard8;
            case 9: return buttonGYCard9;
            case 10: return buttonGYCard10;
            case 11: return buttonGYCard11;
            case 12: return buttonGYCard12;
            case 13: return buttonGYCard13;
            case 14: return buttonGYCard14;
            case 15: return buttonGYCard15;
            case 16: return buttonGYCard16;
            case 17: return buttonGYCard17;
            case 18: return buttonGYCard18;
            case 19: return buttonGYCard19;
            case 20: return buttonGYCard20;
            case 21: return buttonGYCard21;
            case 22: return buttonGYCard22;
            case 23: return buttonGYCard23;
            case 24: return buttonGYCard24;
            case 25: return buttonGYCard25;
            case 26: return buttonGYCard26;
            case 27: return buttonGYCard27;
            case 28: return buttonGYCard28;
            case 29: return buttonGYCard29;
            case 30: return buttonGYCard30;
            case 31: return buttonGYCard31;
            case 32: return buttonGYCard32;
            case 33: return buttonGYCard33;
            case 34: return buttonGYCard34;
            case 35: return buttonGYCard35;
            case 36: return buttonGYCard36;
            case 37: return buttonGYCard37;
            case 38: return buttonGYCard38;
            case 39: return buttonGYCard39;
            default: YuGiOhJi.debugDialog("Error: out of bounds in YChooseCardWindow.getNthGYButton(...); attempted N: " + n); return null;
        }
    }
    
    public static JButton getNthEquipButton (int n){
        switch (n) {
            case 1: return buttonEquipCard1;
            case 2: return buttonEquipCard2;
            case 3: return buttonEquipCard3;
            case 4: return buttonEquipCard4;
            case 5: return buttonEquipCard5;
            case 6: return buttonEquipCard6;
            case 7: return buttonEquipCard7;
            case 8: return buttonEquipCard8;
            case 9: return buttonEquipCard9;
            case 10: return buttonEquipCard10;
            case 11: return buttonEquipCard11;
            case 12: return buttonEquipCard12;
            case 13: return buttonEquipCard13;
            case 14: return buttonEquipCard14;
            case 15: return buttonEquipCard15;
            default: YuGiOhJi.debugDialog("Error: out of bounds in YChooseCardWindow.getNthGYButton(...); attempted N: " + n); return null;
        }
    }
    
    // reserves enough space to display all cards the player possesses in all kinds of parts of the game
    public static void adjustScrollPaneSize() {
        initialScrollPaneWidth = 110+100*Math.max(Math.max(numberOfConsideredHandCards,numberOfConsideredMonsters), Math.max(numberOfConsideredEquipCards,numberOfConsideredGYCards));
        int numberOfRowsToBeDisplayed=4;
        if (numberOfConsideredHandCards==0) {numberOfRowsToBeDisplayed--;}
        if (numberOfConsideredMonsters==0) {numberOfRowsToBeDisplayed--;}
        if (numberOfConsideredGYCards==0) {numberOfRowsToBeDisplayed--;}
        if (numberOfConsideredEquipCards==0) {numberOfRowsToBeDisplayed--;}
        initialScrollPaneHeight=numberOfRowsToBeDisplayed*125;
    }
    
    // shifts the position of card choose buttons upwards, depending on which are relevant or not
    // Yeah, this is some nice mathematical task:
    // Every source of cards that is not needed, moves downwards (position 5) out of the visible range
    // and every resource below moves one level upwards (--).
    // However, the lowest resource (equip cards) does not need to move downwards any more.
    public static void moveButtons() {
        // the case that all four resources are zero, should not exist
        // in case that all four resources exist, do nothing, because default arrangement from constructer used
        if (numberOfConsideredHandCards==0) {
            levelHand=5; levelMonster--; levelGY--; levelEquip--;
            if (numberOfConsideredMonsters==0) {
                levelMonster=5; levelGY--; levelEquip--;
                if (numberOfConsideredGYCards==0) {
                    levelGY=5; levelEquip--;
                }
            }
            else if (numberOfConsideredGYCards==0) {
                levelGY=5; levelEquip--;
            }
        }
        else if (numberOfConsideredMonsters==0) {
            levelMonster=5; levelGY--; levelEquip--;
            if (numberOfConsideredGYCards==0) {
                levelGY=5; levelEquip--;
            }
        }
        else if (numberOfConsideredGYCards==0) {
            levelGY=5; levelEquip--;
        }
        updateButtonPositions();
    }
    
    // shifts the buttons according to the already new set levels
    public static void updateButtonPositions() {
        moveHandCardsToVerticalLevel(levelHand);
        moveMonsterCardsToVerticalLevel(levelMonster);
        moveGYCardsToVerticalLevel(levelGY);
        moveEquipCardsToVerticalLevel(levelEquip);
    }
    
    // puts the cards on the hand in the card chooser window to
    // 1st row (level=1) on top to last row (level=4) at bottom
    public static void moveHandCardsToVerticalLevel (int level) {
        moveLabelToVerticalLevel(displayHandText, level);
        for (int index = 1; index <= 10; index++){
            moveButtonToVerticalLevel(getNthHandButton(index), level, false);
        }
    }
    
    // puts the cards for the summonend monsters in the card chooser window to
    // 1st row (level=1) on top to last row (level=4) at bottom
    public static void moveMonsterCardsToVerticalLevel (int level) {
        moveLabelToVerticalLevel(displayMonsterText, level);
        
        moveLabelToVerticalLevel(displayMonsterNo1, level);
        moveLabelToVerticalLevel(displayMonsterNo2, level);
        moveLabelToVerticalLevel(displayMonsterNo3, level);
        moveLabelToVerticalLevel(displayMonsterNo4, level);
        moveLabelToVerticalLevel(displayMonsterNo5, level);
        
        moveButtonToVerticalLevel(buttonMonsterCard1, level, !considSumMonster1.isInAttackMode);
        moveButtonToVerticalLevel(buttonMonsterCard2, level, !considSumMonster2.isInAttackMode);
        moveButtonToVerticalLevel(buttonMonsterCard3, level, !considSumMonster3.isInAttackMode);
        moveButtonToVerticalLevel(buttonMonsterCard4, level, !considSumMonster4.isInAttackMode);
        moveButtonToVerticalLevel(buttonMonsterCard5, level, !considSumMonster5.isInAttackMode);
    }
    
    // puts the cards in graveyard in the card chooser window to
    // 1st row (level=1) on top to last row (level=4) at bottom
    public static void moveGYCardsToVerticalLevel (int level) {
        moveLabelToVerticalLevel(displayGYText, level);
        for (int index = 1; index <= 39; index++){
            moveButtonToVerticalLevel(getNthGYButton(index), level, false);
        }
    }
    
    // puts the equip cards in the card chooser window to
    // 1st row (level=1) on top to last row (level=4) at bottom
    public static void moveEquipCardsToVerticalLevel (int level) {
        moveLabelToVerticalLevel(displayEquipText, level);
        
        moveLabelToVerticalLevel(displayStackNo1, level);
        moveLabelToVerticalLevel(displayStackNo2, level);
        moveLabelToVerticalLevel(displayStackNo3, level);
        moveLabelToVerticalLevel(displayStackNo4, level);
        moveLabelToVerticalLevel(displayStackNo5, level);
        moveLabelToVerticalLevel(displayStackNo6, level);
        moveLabelToVerticalLevel(displayStackNo7, level);
        moveLabelToVerticalLevel(displayStackNo8, level);
        moveLabelToVerticalLevel(displayStackNo9, level);
        moveLabelToVerticalLevel(displayStackNo10, level);
        moveLabelToVerticalLevel(displayStackNo11, level);
        moveLabelToVerticalLevel(displayStackNo12, level);
        moveLabelToVerticalLevel(displayStackNo13, level);
        moveLabelToVerticalLevel(displayStackNo14, level);
        moveLabelToVerticalLevel(displayStackNo15, level);
        for (int index = 1; index <= 15; index++){
            moveButtonToVerticalLevel(getNthEquipButton(index), level, false);
        }
    }
    
    // moves a label to a certain vertical level in the card chooser window
    public static void moveLabelToVerticalLevel (JLabel label, int lv) {
        rescaleLabel(label, (int) label.getLocation().getX(), (lv-1)*125, (int) label.getWidth(), (int) label.getHeight(), label.isVisible());
        //label.setBounds((int) label.getLocation().getX(), (lv-1)*125, (int) label.getWidth(), (int) label.getHeight());
    }
    
    // moves a button to a certain vertical level in the card chooser window
    public static void moveButtonToVerticalLevel (JButton button, int lv, boolean isInDefMode) {
        int deltaY=0; if (isInDefMode) {deltaY=16;}
        rescaleButton(button, (int) button.getLocation().getX(), deltaY+25+(lv-1)*125, (int) button.getWidth(), (int) button.getHeight(), button.isVisible());
        //button.setBounds((int) button.getLocation().getX(), deltaY+25+(lv-1)*125, (int) button.getWidth(), (int) button.getHeight());
    }
    
    // makes a selected card stand out by turning it into "defence mode"
    // (also turns it back, if unselected)
    // if an equip card has its effects negated, it is presented as face down
    public static void makeSelectedCardStandOut (JButton button, String imagePath) {
        if (button.getHeight()>button.getWidth()) { // if the height>width, it appears as if in attack mode: turn it into defence mode
            //button.setBounds((int) button.getLocation().getX() - 16, (int) button.getLocation().getY() + 16, 100, 67);
            button.setIcon(new RescaledIcon(YuGiOhJi.class.getResource(imagePath)));
        }
        else {
            //button.setBounds((int) button.getLocation().getX() + 16, (int) button.getLocation().getY() - 16, 67, 100);
            button.setIcon(new RescaledIcon(YuGiOhJi.class.getResource(imagePath)));
        }
    } // the exact bounds are changed in the rescaling method called when selecting
    
    // (un)selects (un)selected card on hand as tribute for an effect or a summoning
    public static void selectedUnselectHandCard (JButton button, int consideredCardNo, int originalCardId) {
        if (intTributeHandCardNo1==0 && intTributeHandCardNo2!=consideredCardNo) { // select as 1st tribute
            makeSelectedCardStandOut(button, consideredHandCards.getNthCardOfHand(consideredCardNo).upMonster.cardPathDef);
            intTributeHandCardNo1=consideredCardNo;
            intTributeHandCardId1=originalCardId; updateSumOfPaidCost(2);
        }
        else if (intTributeHandCardNo1!=consideredCardNo && intTributeHandCardNo2==0) { // select as 2nd tribute
            makeSelectedCardStandOut(button, consideredHandCards.getNthCardOfHand(consideredCardNo).upMonster.cardPathDef);
            intTributeHandCardNo2=consideredCardNo;
            intTributeHandCardId2=originalCardId; updateSumOfPaidCost(2);
        }
        else if (intTributeHandCardNo1==consideredCardNo) { // unselect
            makeSelectedCardStandOut(button, consideredHandCards.getNthCardOfHand(consideredCardNo).upMonster.cardPathAtt);
            intTributeHandCardNo1=0; intTributeHandCardId1=0; updateSumOfPaidCost(-2);
        }
        else if (intTributeHandCardNo2==consideredCardNo) { // unselect
            makeSelectedCardStandOut(button, consideredHandCards.getNthCardOfHand(consideredCardNo).upMonster.cardPathAtt);
            intTributeHandCardNo2=0; intTributeHandCardId2=0; updateSumOfPaidCost(-2);
        }
        rescaleButtons();
    }
    
    // (un)selects (un)selected summoned monster card as tribute for an effect or a summoning
    public static void selectedUnselectMonster (JButton button, SummonedMonster SumMonster, int originalCardNo) {
        if (intTributeMonsterNo1==0 && intTributeMonsterNo2!=originalCardNo && intTributeMonsterNo3!=originalCardNo) { // select as 1st tribute
            makeSelectedMonsterCardStandOut(button, SumMonster, true);
            intTributeMonsterNo1=originalCardNo; updateSumOfPaidCost(2);
        }
        else if (intTributeMonsterNo1!=originalCardNo && intTributeMonsterNo2==0 && intTributeMonsterNo3!=originalCardNo) { // select as 2nd tribute
            makeSelectedMonsterCardStandOut(button, SumMonster, true);
            intTributeMonsterNo2=originalCardNo; updateSumOfPaidCost(2);
        }
        else if (intTributeMonsterNo1!=originalCardNo && intTributeMonsterNo2!=originalCardNo && intTributeMonsterNo3==0) { // select as 3rd tribute
            makeSelectedMonsterCardStandOut(button, SumMonster, true);
            intTributeMonsterNo3=originalCardNo; updateSumOfPaidCost(2);
        }
        else if (intTributeMonsterNo1==originalCardNo) { // unselect
            makeSelectedMonsterCardStandOut(button, SumMonster, false);
            intTributeMonsterNo1=0; updateSumOfPaidCost(-2);
        }
        else if (intTributeMonsterNo2==originalCardNo) { // unselect
            makeSelectedMonsterCardStandOut(button, SumMonster, false);
            intTributeMonsterNo2=0; updateSumOfPaidCost(-2);
        }
        else if (intTributeMonsterNo3==originalCardNo) { // unselect
            makeSelectedMonsterCardStandOut(button, SumMonster, false);
            intTributeMonsterNo3=0; updateSumOfPaidCost(-2);
        }
        rescaleButtons();
    }
    
    // for better readability and not repeating oneself out-source how a summoned monster changes its mode in chosse window 
    public static void makeSelectedMonsterCardStandOut (JButton button, SummonedMonster SumMonster, boolean isSelecting) {
        if (SumMonster.isFaceDown) { // if face down, then only back of the card
            makeSelectedCardStandOut(button, "/images/YuGiOhJiFacedown.png");
        }
        else {
            if ( (SumMonster.isInAttackMode && isSelecting) || (!SumMonster.isInAttackMode && !isSelecting) ) {
                makeSelectedCardStandOut(button, SumMonster.Monster.cardPathDef);
            }
            else {
                makeSelectedCardStandOut(button, SumMonster.Monster.cardPathAtt);
            }
        }
    }
    
    // (un)selects (un)selected card in graveyard as tribute for an effect or a summoning
    public static void selectedUnselectGYCard (JButton button, int consideredCardNo, int originalCardId) {
        if (intTributeGYCardNo1==0 && intTributeGYCardNo2!=consideredCardNo && intTributeGYCardNo3!=consideredCardNo && intTributeGYCardNo4!=consideredCardNo) { // select as 1st tribute
            makeSelectedCardStandOut(button, consideredGYCards.getNthCardOfDeck(consideredCardNo).upMonster.cardPathDef);
            intTributeGYCardNo1=consideredCardNo;
            intTributeGYCardId1=originalCardId; updateSumOfPaidCost(1);
        }
        else if (intTributeGYCardNo1!=consideredCardNo && intTributeGYCardNo2==0 && intTributeGYCardNo3!=consideredCardNo && intTributeGYCardNo4!=consideredCardNo) { // select as 2nd tribute
            makeSelectedCardStandOut(button, consideredGYCards.getNthCardOfDeck(consideredCardNo).upMonster.cardPathDef);
            intTributeGYCardNo2=consideredCardNo;
            intTributeGYCardId2=originalCardId; updateSumOfPaidCost(1);
        }
        else if (intTributeGYCardNo1!=consideredCardNo && intTributeGYCardNo2!=consideredCardNo && intTributeGYCardNo3==0 && intTributeGYCardNo4!=consideredCardNo) { // select as 3rd tribute
            makeSelectedCardStandOut(button, consideredGYCards.getNthCardOfDeck(consideredCardNo).upMonster.cardPathDef);
            intTributeGYCardNo3=consideredCardNo;
            intTributeGYCardId3=originalCardId; updateSumOfPaidCost(1);
        }
        else if (intTributeGYCardNo1!=consideredCardNo && intTributeGYCardNo2!=consideredCardNo && intTributeGYCardNo3!=consideredCardNo && intTributeGYCardNo4==0) { // select as 4th tribute
            makeSelectedCardStandOut(button, consideredGYCards.getNthCardOfDeck(consideredCardNo).upMonster.cardPathDef);
            intTributeGYCardNo4=consideredCardNo;
            intTributeGYCardId4=originalCardId; updateSumOfPaidCost(1);
        }
        else if (intTributeGYCardNo1==consideredCardNo) { // unselect
            makeSelectedCardStandOut(button, consideredGYCards.getNthCardOfDeck(consideredCardNo).upMonster.cardPathAtt);
            intTributeGYCardNo1=0; intTributeGYCardId1=0; updateSumOfPaidCost(-1);
        }
        else if (intTributeGYCardNo2==consideredCardNo) { // unselect
            makeSelectedCardStandOut(button, consideredGYCards.getNthCardOfDeck(consideredCardNo).upMonster.cardPathAtt);
            intTributeGYCardNo2=0; intTributeGYCardId2=0; updateSumOfPaidCost(-1);
        }
        else if (intTributeGYCardNo3==consideredCardNo) { // unselect
            makeSelectedCardStandOut(button, consideredGYCards.getNthCardOfDeck(consideredCardNo).upMonster.cardPathAtt);
            intTributeGYCardNo3=0; intTributeGYCardId3=0; updateSumOfPaidCost(-1);
        }
        else if (intTributeGYCardNo4==consideredCardNo) { // unselect
            makeSelectedCardStandOut(button, consideredGYCards.getNthCardOfDeck(consideredCardNo).upMonster.cardPathAtt);
            intTributeGYCardNo4=0; intTributeGYCardId4=0; updateSumOfPaidCost(-1);
        }
        rescaleButtons();
    }
    
    // (un)selects (un)selected equip card as tribute for an effect or a summoning
    public static void selectUnselectEquipCard (JButton button, int consideredCardNo) {
    //public static void selectUnselectEquipCard (JButton button, int stackNo, boolean isPlayersStack, YCard EquipCard, boolean isNegated, int consideredCardNo, int cardId) {
        EquipCard NthEquipCard = getNthEquipCard(consideredCardNo);
        int stackNo = NthEquipCard.stackNumber;
        boolean isPlayersStack = NthEquipCard.isPlayersStack;
        if (intTributeStackCardNo1==0 && intTributeStackCardNo2!=consideredCardNo && intTributeStackCardNo3!=consideredCardNo && intTributeStackCardNo4!=consideredCardNo) { // select as 1st tribute
            if (NthEquipCard.isNegated) {
                makeSelectedCardStandOut(button, "/images/YuGiOhJiFacedown.png");
            }
            else {
                makeSelectedCardStandOut(button, NthEquipCard.Card.lowMonster.cardPathDef);
            }
            intTributeStackCardNo1=consideredCardNo;
            intTributeStackCardId1=NthEquipCard.Card.cardId;
            intTributeStackCardNegationStatus1=NthEquipCard.isNegated;
            StackOfTribute1=EStack.getNthStack(stackNo, isPlayersStack);
            updateSumOfPaidCost(1);
        }
        else if (intTributeStackCardNo1!=consideredCardNo && intTributeStackCardNo2==0 && intTributeStackCardNo3!=consideredCardNo && intTributeStackCardNo4!=consideredCardNo) { // select as 2nd tribute
            if (NthEquipCard.isNegated) {
                makeSelectedCardStandOut(button, "/images/YuGiOhJiFacedown.png");
            }
            else {
                makeSelectedCardStandOut(button, NthEquipCard.Card.lowMonster.cardPathDef);
            }
            intTributeStackCardNo2=consideredCardNo;
            intTributeStackCardId2=NthEquipCard.Card.cardId;
            intTributeStackCardNegationStatus2=NthEquipCard.isNegated;
            StackOfTribute2=EStack.getNthStack(stackNo, isPlayersStack);
            updateSumOfPaidCost(1);
        }
        else if (intTributeStackCardNo1!=consideredCardNo && intTributeStackCardNo2!=consideredCardNo && intTributeStackCardNo3==0 && intTributeStackCardNo4!=consideredCardNo) { // select as 3rd tribute
            if (NthEquipCard.isNegated) {
                makeSelectedCardStandOut(button, "/images/YuGiOhJiFacedown.png");
            }
            else {
                makeSelectedCardStandOut(button, NthEquipCard.Card.lowMonster.cardPathDef);
            }
            intTributeStackCardNo3=consideredCardNo;
            intTributeStackCardId3=NthEquipCard.Card.cardId;
            intTributeStackCardNegationStatus3=NthEquipCard.isNegated;
            StackOfTribute3=EStack.getNthStack(stackNo, isPlayersStack);
            updateSumOfPaidCost(1);
        }
        else if (intTributeStackCardNo1!=consideredCardNo && intTributeStackCardNo2!=consideredCardNo && intTributeStackCardNo3!=consideredCardNo && intTributeStackCardNo4==0) { // select as 4th tribute
            if (NthEquipCard.isNegated) {
                makeSelectedCardStandOut(button, "/images/YuGiOhJiFacedown.png");
            }
            else {
                makeSelectedCardStandOut(button, NthEquipCard.Card.lowMonster.cardPathDef);
            }
            intTributeStackCardNo4=consideredCardNo;
            intTributeStackCardId4=NthEquipCard.Card.cardId;
            intTributeStackCardNegationStatus4=NthEquipCard.isNegated;
            StackOfTribute4=EStack.getNthStack(stackNo, isPlayersStack);
            updateSumOfPaidCost(1);
        }
        else if (intTributeStackCardNo1==consideredCardNo) { // unselect
            if (NthEquipCard.isNegated) {
                makeSelectedCardStandOut(button, "/images/YuGiOhJiFacedown.png");
            }
            else {
                makeSelectedCardStandOut(button, NthEquipCard.Card.lowMonster.cardPathAtt);
            }
            intTributeStackCardNo1=0;
            intTributeStackCardId1=0; intTributeStackCardNegationStatus1=false; StackOfTribute1=new EStack(false, 1); updateSumOfPaidCost(-1);
        }
        else if (intTributeStackCardNo2==consideredCardNo) { // unselect
            if (NthEquipCard.isNegated) {
                makeSelectedCardStandOut(button, "/images/YuGiOhJiFacedown.png");
            }
            else {
                makeSelectedCardStandOut(button, NthEquipCard.Card.lowMonster.cardPathAtt);
            }
            intTributeStackCardNo2=0;
            intTributeStackCardId2=0; intTributeStackCardNegationStatus2=false; StackOfTribute2=new EStack(false, 1); updateSumOfPaidCost(-1);
        }
        else if (intTributeStackCardNo3==consideredCardNo) { // unselect
            if (NthEquipCard.isNegated) {
                makeSelectedCardStandOut(button, "/images/YuGiOhJiFacedown.png");
            }
            else {
                makeSelectedCardStandOut(button, NthEquipCard.Card.lowMonster.cardPathAtt);
            }
            intTributeStackCardNo3=0;
            intTributeStackCardId3=0; intTributeStackCardNegationStatus3=false; StackOfTribute3=new EStack(false, 1); updateSumOfPaidCost(-1);
        }
        else if (intTributeStackCardNo4==consideredCardNo) { // unselect
            if (NthEquipCard.isNegated) {
                makeSelectedCardStandOut(button, "/images/YuGiOhJiFacedown.png");
            }
            else {
                makeSelectedCardStandOut(button, NthEquipCard.Card.lowMonster.cardPathAtt);
            }
            intTributeStackCardNo4=0;
            intTributeStackCardId4=0; intTributeStackCardNegationStatus4=false; StackOfTribute4=new EStack(false, 1); updateSumOfPaidCost(-1);
        }
        rescaleButtons();
    }
    
    // recalculates the current sum of cost about to pay and dispays it correctly
    // the argument is the difference, the change of the sum
    public static void updateSumOfPaidCost (int semipointsDiff) {
        selectedSemipoints = selectedSemipoints + semipointsDiff;
        displaySumOfPaidCost.setText("sum worth: " + (double) selectedSemipoints/2);
    }
    
    // unselects everything by putting many variables zeros
    public static void unselectEverything(){
        selectedSemipoints=0;
        
        intTributeHandCardId1=0;
        intTributeHandCardId2=0;
        intTributeMonsterNo1=0;
        intTributeMonsterNo2=0;
        intTributeMonsterNo3=0;
        intTributeGYCardId1=0;
        intTributeGYCardId2=0;
        intTributeGYCardId3=0;
        intTributeGYCardId4=0;
        StackOfTribute1 = new EStack(true, 1);
        intTributeStackCardId1=0;
        intTributeStackCardNegationStatus1=false;
        StackOfTribute2 = new EStack(true, 2);
        intTributeStackCardId2=0;
        intTributeStackCardNegationStatus2=false;
        StackOfTribute3 = new EStack(true, 3);
        intTributeStackCardId3=0;
        intTributeStackCardNegationStatus3=false;
        StackOfTribute4 = new EStack(true, 4);
        intTributeStackCardId4=0;
        intTributeStackCardNegationStatus4=false;
        
        intTributeHandCardNo1=0;
        intTributeHandCardNo2=0;
        intTributeGYCardNo1=0;
        intTributeGYCardNo2=0;
        intTributeGYCardNo3=0;
        intTributeGYCardNo4=0;
        intTributeStackCardNo1=0;
        intTributeStackCardNo2=0;
        intTributeStackCardNo3=0;
        intTributeStackCardNo4=0;
    }
    
    // sets all variables for considered cards to zero
    public static void unconsiderCards(){
        consideredHandCards = new Hand(true);
        for (int index = 1; index <= 10; index++){
            setNthCardId(index, 0);
        }
        numberOfConsideredHandCards=0; numberOfConsideredGYCards=0;
        consideredGYCards = new Deck(true);
        for (int index = 1; index <= 39; index++){
            setNthGYId(index, 0);
        }
        for (int index = 1; index <= 5; index++){
            considerNthSummonedMonster(new SummonedMonster(true, 1), index);
        }
        numberOfConsideredMonsters=0; numberOfConsideredEquipCards=0;
        for (int index = 1; index <= 15; index++){
            setNthEquipCardId(index, 0);
            setNthEquipCard(index, new EquipCard(true));
        }
        levelHand=1;
        levelMonster=2;
        levelGY=3;
        levelEquip=4;
        
    }
    
    // sets nth considered monster to given monster
    public static void considerNthSummonedMonster (SummonedMonster SumMonster, int n) {
        switch (n) {
            case 1: considSumMonster1=SumMonster; break;
            case 2: considSumMonster2=SumMonster; break;
            case 3: considSumMonster3=SumMonster; break;
            case 4: considSumMonster4=SumMonster; break;
            case 5: considSumMonster5=SumMonster; break;
            default: YuGiOhJi.debugDialog("Error: out of bounds in YChooseCardWindow.considerNthSummonedMonster(...); attempted N: " + n); break;
        }
    }
    
    // gets nth considered summoned monster
    public static SummonedMonster getNthConsideredMonster (int n) {
        switch (n) {
            case 1: return considSumMonster1;
            case 2: return considSumMonster2;
            case 3: return considSumMonster3;
            case 4: return considSumMonster4;
            case 5: return considSumMonster5;
            default: YuGiOhJi.debugDialog("Error: out of bounds in YChooseCardWindow.getNthconsideredMonster(...); attempted N: " + n); return null;
        }
    }
    
    // sets the remembered original hand card Id correctly
    public static void setNthCardId (int consideredCardId, int originalCardId){
        switch (consideredCardId) {
            case 1: consideredHandCardId1=originalCardId; break;
            case 2: consideredHandCardId2=originalCardId; break;
            case 3: consideredHandCardId3=originalCardId; break;
            case 4: consideredHandCardId4=originalCardId; break;
            case 5: consideredHandCardId5=originalCardId; break;
            case 6: consideredHandCardId6=originalCardId; break;
            case 7: consideredHandCardId7=originalCardId; break;
            case 8: consideredHandCardId8=originalCardId; break;
            case 9: consideredHandCardId9=originalCardId; break;
            case 10: consideredHandCardId10=originalCardId; break;
            default: YuGiOhJi.debugDialog("Error: out of bounds in YChooseCardWindow.setNthCardId(...); attempted N: " + originalCardId); break;
        }
    }
    
    // gets the remembered original hand card Id
    public static int getNthCardId (int n){
        switch (n) {
            case 1: return consideredHandCardId1;
            case 2: return consideredHandCardId2;
            case 3: return consideredHandCardId3;
            case 4: return consideredHandCardId4;
            case 5: return consideredHandCardId5;
            case 6: return consideredHandCardId6;
            case 7: return consideredHandCardId7;
            case 8: return consideredHandCardId8;
            case 9: return consideredHandCardId9;
            case 10: return consideredHandCardId10;
            default: YuGiOhJi.debugDialog("Error: out of bounds in YChooseCardWindow.getNthCardId(...); attempted N: " + n); return 0;
        }
    }
    
    // sets the remembered original graveyard card Id correctly
    public static void setNthGYId (int consideredCardNo, int originalCardId){
        switch (consideredCardNo) {
            case 1: consideredGYCardId1=originalCardId; break;
            case 2: consideredGYCardId2=originalCardId; break;
            case 3: consideredGYCardId3=originalCardId; break;
            case 4: consideredGYCardId4=originalCardId; break;
            case 5: consideredGYCardId5=originalCardId; break;
            case 6: consideredGYCardId6=originalCardId; break;
            case 7: consideredGYCardId7=originalCardId; break;
            case 8: consideredGYCardId8=originalCardId; break;
            case 9: consideredGYCardId9=originalCardId; break;
            case 10: consideredGYCardId10=originalCardId; break;
            case 11: consideredGYCardId11=originalCardId; break;
            case 12: consideredGYCardId12=originalCardId; break;
            case 13: consideredGYCardId13=originalCardId; break;
            case 14: consideredGYCardId14=originalCardId; break;
            case 15: consideredGYCardId15=originalCardId; break;
            case 16: consideredGYCardId16=originalCardId; break;
            case 17: consideredGYCardId17=originalCardId; break;
            case 18: consideredGYCardId18=originalCardId; break;
            case 19: consideredGYCardId19=originalCardId; break;
            case 20: consideredGYCardId20=originalCardId; break;
            case 21: consideredGYCardId21=originalCardId; break;
            case 22: consideredGYCardId22=originalCardId; break;
            case 23: consideredGYCardId23=originalCardId; break;
            case 24: consideredGYCardId24=originalCardId; break;
            case 25: consideredGYCardId25=originalCardId; break;
            case 26: consideredGYCardId26=originalCardId; break;
            case 27: consideredGYCardId27=originalCardId; break;
            case 28: consideredGYCardId28=originalCardId; break;
            case 29: consideredGYCardId29=originalCardId; break;
            case 30: consideredGYCardId30=originalCardId; break;
            case 31: consideredGYCardId31=originalCardId; break;
            case 32: consideredGYCardId32=originalCardId; break;
            case 33: consideredGYCardId33=originalCardId; break;
            case 34: consideredGYCardId34=originalCardId; break;
            case 35: consideredGYCardId35=originalCardId; break;
            case 36: consideredGYCardId36=originalCardId; break;
            case 37: consideredGYCardId37=originalCardId; break;
            case 38: consideredGYCardId38=originalCardId; break;
            case 39: consideredGYCardId39=originalCardId; break;
            default: YuGiOhJi.debugDialog("Error: out of bounds in YChooseCardWindow.setNthGYId(...); attempted N: " + originalCardId); break;
        }
    }
    
    // sets the considered hand, summoned monsters, equip cards and GY to the actual cards that one possesses and that are mooks
    // i.e. the number of stars of either the upper or lower monster is exactly one
    public static void considerAllMooks(){
        // consider all mooks on hands
        int numberOfHandCards = Hand.getHand(true).numberOfCards;
        if (numberOfHandCards>0) {
            for (int index = 1; index <= numberOfHandCards; index++){
                YCard Card = Hand.getHand(true).getNthCardOfHand(index);
                if (Card.lowMonster.stars==1 || Card.upMonster.stars==1) {
                    numberOfConsideredHandCards++;
                    consideredHandCards.setNthCardOfHandToCard(Card, numberOfConsideredHandCards);
                    setNthCardId(numberOfConsideredHandCards, Card.cardId);
                }
            }
        }
        consideredHandCards.numberOfCards=numberOfConsideredHandCards;
        numberOfConsideredGYCards=0; // analog with GY
        int numberOfGYCards = GYDeckPlayer.numberOfCards;
        if (numberOfGYCards>0) {
            for (int index = numberOfGYCards; index >= 1; index--){
                YCard Card = GYDeckPlayer.getNthCardOfDeck(index);
                if (Card.lowMonster.stars==1 || Card.upMonster.stars==1) {
                    numberOfConsideredGYCards++;
                    consideredGYCards.setNthCardOfDeckToCard(Card, numberOfConsideredGYCards);
                    setNthGYId(numberOfConsideredGYCards, Card.cardId);
                }
            }
        }
        consideredGYCards.numberOfCards=numberOfConsideredGYCards;
        // analog for summoned monster
        numberOfConsideredMonsters=0;
        for (int index = 1; index <= 5; index++){
            SummonedMonster SumMonster = SummonedMonster.getNthSummonedMonster(index, true);
            if (SumMonster.Card.lowMonster.stars==1 || SumMonster.Card.upMonster.stars==1) { // non-existing monsters have zero stars (thus not needed to check if existing)
                numberOfConsideredMonsters++;
                considerNthSummonedMonster(SumMonster, numberOfConsideredMonsters);
            }
        }
        considerAllEquipCards(); // equip cards always have a mook
    }
    
    // sets the considered hand, summoned monsters, equip cards and GY to the actual cards that one possesses
    // (that means one considers all of the own cards for selecting tributes for paying costs)
    public static void considerAllCards(){
        considerAllHandCards();
        considerEntireGY();
        considerAllSummonedMonsters();
        considerAllEquipCards();
    }
    
    // For special summoning from hand one has to be able to tribute everything
    // except the card on the one wants to summon.
    // This method collects all these cards.
    public static void considerAllCardsExceptChosenOneOnHand(){
        considerAllSummonedMonsters();
        considerEntireGY();
        considerAllEquipCards();
        for (int index = 1; index <= Hand.getHand(true).numberOfCards; index++){
            if (index!=Game.actEffCardNo) {
                numberOfConsideredHandCards++;
                YCard Card = Hand.getHand(true).getNthCardOfHand(index);
                consideredHandCards.setNthCardOfHandToCard(Card, numberOfConsideredHandCards);
                setNthCardId(numberOfConsideredHandCards, Card.cardId);
            }
        }
        consideredHandCards.numberOfCards=numberOfConsideredHandCards;
    }
    
    // For negating the effects of a monster one has to be able to tribute everything
    // except the monster, if it is the monster of the player.
    // This method collects all these cards.
    public static void considerAllCardsExceptChosenMonster() {
        considerAllHandCards();
        considerAllSummonedMonstersExceptChosenOne();
        considerEntireGY();
        considerAllEquipCards();
    }
    
    // sets all considered summoned monsters to the actual cards that one possesses
    public static void considerAllSummonedMonsters(){
        for (int index = 1; index <= 5 ; index++){
            SummonedMonster SumMonster  = SummonedMonster.getNthSummonedMonster(index, true);
            if (SumMonster.isExisting) {
                numberOfConsideredMonsters++;
                considerNthSummonedMonster(SumMonster, numberOfConsideredMonsters);
            }
        }
    }
    
    
    // sets all considered summoned monsters to the actual cards that one possesses
    public static void considerAllSummonedMonstersExceptChosenOne(){
        for (int index = 1; index <= 5 ; index++){
            SummonedMonster SumMonster  = SummonedMonster.getNthSummonedMonster(index, true);
            if (SumMonster.isExisting && !SumMonster.isBasicallySameMonster(Game.ActEffMonSource)) {
                numberOfConsideredMonsters++;
                considerNthSummonedMonster(SumMonster, numberOfConsideredMonsters);
            }
        }
    }
    
    // For special summoning from graveyard one has to be able to tribute everything
    // except the card on the one wants to summon.
    // This method collects all these cards.
    public static void considerAllCardsExceptChosenOneInGY(){
        considerAllHandCards();
        considerAllSummonedMonsters();
        considerEntireGYExceptChosenCard();
        considerAllEquipCards();
    }
    
    // For reviving a monster from GY one is not allowed to consider paying that card as a cost.
    public static void considerEntireGYExceptChosenCard(){
        for (int index = 1; index <= GYDeckPlayer.numberOfCards; index++){
            if (index!=Game.actEffCardNo) {
                numberOfConsideredGYCards++;
                YCard Card = GYDeckPlayer.getNthCardOfDeck(index);
                consideredGYCards.setNthCardOfDeckToCard(Card, numberOfConsideredGYCards);
                setNthGYId(numberOfConsideredGYCards, Card.cardId);
            }
        }
        consideredGYCards.numberOfCards=numberOfConsideredGYCards;
    }
    
    // considers all cards that a worth half a card (i.e. equip and GY cards) for tributing for an effect
    public static void considerAllHalfCardsExceptChosenOneInGY(){
        considerEntireGYExceptChosenCard();
        considerAllEquipCards();
    }
    
    // sets all considered hand cards to the actual cards that one possesses
    public static void considerAllHandCards(){
        consideredHandCards=Hand.getHand(true);
        numberOfConsideredHandCards=consideredHandCards.numberOfCards;
        if (numberOfConsideredHandCards>0) {
            for (int index = 1; index <= numberOfConsideredHandCards; index++){
                setNthCardId(index, Hand.getHand(true).getNthCardOfHand(index).cardId);
            }
        }
    }
    
    // sets all considered hand cards to all copies of a specific hand trap
    public static void considerAllFittingHandTraps(){
        for (int index = 1; index <= Hand.getHand(true).numberOfCards; index++){
            YCard Card = Hand.getHand(true).getNthCardOfHand(index);
            if (Card.cardId==Game.actEffCardId) {
                numberOfConsideredHandCards++;
                setNthCardId(numberOfConsideredHandCards, Card.cardId);
                consideredHandCards.setNthCardOfHandToCard(Card, numberOfConsideredHandCards);
            }
        }
        consideredHandCards.numberOfCards=numberOfConsideredHandCards;
    }
    
    // sets all considered cards in graveyard to the actual cards that one possesses
    public static void considerEntireGY(){
        consideredGYCards=GYDeckPlayer;
        numberOfConsideredGYCards=consideredGYCards.numberOfCards;
        if (numberOfConsideredGYCards>0) {
            for (int index = numberOfConsideredGYCards; index >= 1; index--){
                setNthGYId(index, consideredGYCards.getNthCardOfDeck(index).cardId);
            }
        }
    }
    
    // sets all considered equip cards to the actual cards that one possesses
    public static void considerAllEquipCards(){
        // go through all 10 equip stacks, look what card belongs to the player, copy their properties
        // first go through the 5 equip stacks of the player
        searchEquipStackForOwnCard(EStack.getNthStack(1, true));
        searchEquipStackForOwnCard(EStack.getNthStack(2, true));
        searchEquipStackForOwnCard(EStack.getNthStack(3, true));
        searchEquipStackForOwnCard(EStack.getNthStack(4, true));
        searchEquipStackForOwnCard(EStack.getNthStack(5, true));
        // now go through the 5 equip stacks of the CPU
        searchEquipStackForOwnCard(EStack.getNthStack(1, false));
        searchEquipStackForOwnCard(EStack.getNthStack(2, false));
        searchEquipStackForOwnCard(EStack.getNthStack(3, false));
        searchEquipStackForOwnCard(EStack.getNthStack(4, false));
        searchEquipStackForOwnCard(EStack.getNthStack(5, false));
    }
    
    // look what card in a given equip stack belongs to the player and considers these cards
    // (remembers all their properties)
    public static void searchEquipStackForOwnCard (EStack Stack) {
        if (Stack.numberOfCards>0) {
            for (int index = Stack.numberOfCards; index >= 1; index--){
                if (Stack.EquipCards[index].isPlayersEquipCard) {
                    numberOfConsideredEquipCards++;
                    setNthEquipCardId(numberOfConsideredEquipCards, Stack.getNthCardOfStack(index).cardId);
                    setNthEquipCard(numberOfConsideredEquipCards, Stack.EquipCards[index]);
                }
            }
        }
    }
    
    // sets the Id of the considered equip card in the stack correctly
    public static void setNthEquipCardId (int n, int equipCardId){
        switch (n) {
            case 1: equipCardId1=equipCardId; break;
            case 2: equipCardId2=equipCardId; break;
            case 3: equipCardId3=equipCardId; break;
            case 4: equipCardId4=equipCardId; break;
            case 5: equipCardId5=equipCardId; break;
            case 6: equipCardId6=equipCardId; break;
            case 7: equipCardId7=equipCardId; break;
            case 8: equipCardId8=equipCardId; break;
            case 9: equipCardId9=equipCardId; break;
            case 10: equipCardId10=equipCardId; break;
            case 11: equipCardId11=equipCardId; break;
            case 12: equipCardId12=equipCardId; break;
            case 13: equipCardId13=equipCardId; break;
            case 14: equipCardId14=equipCardId; break;
            case 15: equipCardId15=equipCardId; break;
            default: YuGiOhJi.debugDialog("Error: out of bounds in YChooseCardWindow.setNthEquipCardNumber(...); attempted N: " + n); break;
        }
    }
    
    // sets the nth considered equip card in the stack correctly
    public static void setNthEquipCard (int n, EquipCard ConsideredEquipCard){
        switch (n) {
            case 1: EquipCard1 = ConsideredEquipCard; break;
            case 2: EquipCard2 = ConsideredEquipCard; break;
            case 3: EquipCard3 = ConsideredEquipCard; break;
            case 4: EquipCard4 = ConsideredEquipCard; break;
            case 5: EquipCard5 = ConsideredEquipCard; break;
            case 6: EquipCard6 = ConsideredEquipCard; break;
            case 7: EquipCard7 = ConsideredEquipCard; break;
            case 8: EquipCard8 = ConsideredEquipCard; break;
            case 9: EquipCard9 = ConsideredEquipCard; break;
            case 10: EquipCard10 = ConsideredEquipCard; break;
            case 11: EquipCard11 = ConsideredEquipCard; break;
            case 12: EquipCard12 = ConsideredEquipCard; break;
            case 13: EquipCard13 = ConsideredEquipCard; break;
            case 14: EquipCard14 = ConsideredEquipCard; break;
            case 15: EquipCard15 = ConsideredEquipCard; break;
            default: YuGiOhJi.debugDialog("Error: out of bounds in YChooseCardWindow.setNthEquipCard(...); attempted N: " + n); break;
        }
    }
    
    // gets the nth considered equip card
    public static EquipCard getNthEquipCard (int n){
        switch (n) {
            case 1: return EquipCard1;
            case 2: return EquipCard2;
            case 3: return EquipCard3;
            case 4: return EquipCard4;
            case 5: return EquipCard5;
            case 6: return EquipCard6;
            case 7: return EquipCard7;
            case 8: return EquipCard8;
            case 9: return EquipCard9;
            case 10: return EquipCard10;
            case 11: return EquipCard11;
            case 12: return EquipCard12;
            case 13: return EquipCard13;
            case 14: return EquipCard14;
            case 15: return EquipCard15;
            default: YuGiOhJi.debugDialog("Error: out of bounds in YChooseCardWindow.setNthEquipCard(...); attempted N: " + n); return (new EquipCard(true));
        }
    }
    
    // remebers all cards the player can show (and thus reveal), like hand cards and face down monsters
    public static void considerShowableCards() {
        considerAllHandCards();
        // consider all existing facedown own monsters (no matter, if they are already known or not, for simplicity)
        for (int index = 1; index <= 5; index++){
            considerFaceDownMonster(index);
        }
    }
    
    // out-source code for setting the nth considered monster to an existing facedown monster
    // for better readability
    public static void considerFaceDownMonster (int n){
        SummonedMonster SumMonster = SummonedMonster.getNthSummonedMonster(n, true);
        if (SumMonster.isExisting && SumMonster.isFaceDown) {
            numberOfConsideredMonsters++;
            considerNthSummonedMonster(SumMonster, numberOfConsideredMonsters);
        }
    }

    // determines what cards one is allowed to pay as cost for a given effect
    // also remembers these cards
    public static void determineConsideredCards() {
        if (Game.isActEffRevealCard()) {considerShowableCards(); Game.isReadyToChooseEffTarget=false;} // show a card
        if (Game.isActEffBurner()) {considerAllMooks();}
        if (Game.isActEffBigBurner()) {considerAllSummonedMonstersExceptChosenOne();}
        if (Game.isActEffSearch()) {considerAllCards();} // 1st paying costs for card grabber (2 cards)
        if (Game.isActEffMilling()) {considerAllCards();} // 1st paying costs for card grabber (1 card)
        if (Game.isActEffSkillStealer()) {considerShowableCards(); Game.isReadyToChooseEffTarget=false;} // again show a card (same as effectId==1)
        if (Game.isActEffCopyCat()) {considerAllHandCards();}
        if (Game.isActEffModeChanger()) {considerAllCards(); Game.isReadyToChooseEffTarget=false;}
        if (Game.isActEffReviveMook()) {considerAllHalfCardsExceptChosenOneInGY();}
        if (Game.isActEffReviveMidboss()) {considerAllCardsExceptChosenOneInGY();}
        if (Game.isActEffBigBackBouncer()) {considerAllCards(); Game.isReadyToChooseEffTarget=false;}
        if (Game.isActEffBigBanisher()) {considerAllCards(); Game.isReadyToChooseEffTarget=false;}
        if (Game.isActEffMonsterStealer()) {considerAllCards(); Game.isReadyToChooseEffTarget=false;}
        if (Game.isActEffEradicator()) {considerAllCards(); Game.isReadyToChooseEffTarget=false;}
        if (Game.isActEffOptNeutraliser()) {considerAllCards(); Game.isReadyToChooseEffTarget=false;}
        // effects 16-18 are equip effects and don't need a tribute (thus this window is not called)
        if (Game.isActSumEndbossHand()) {considerAllCardsExceptChosenOneOnHand();}
        if (Game.isActSumEndbossGY()) {considerAllCardsExceptChosenOneInGY();}
        // 21-26 "effects" for tribute summoning (they don't open this window)
        // 27 hand trap Neutraliser during own turn (doesn't open this window)
        // 28 passive effect Back Bouncer (doesn't open this window)
        if (Game.isActEffNeutraliserOpponentsTurn()) { // on-field effect of Neutraliser during opponents turn of battle
            if (Game.ActEffMonTarget.isPlayersMonster) { // forbid tributing monster one wants to negate
                considerAllCardsExceptChosenMonster();
            }
            else {
                considerAllCards();
            }
        }
        if (Game.isActEffNeutraliserOpponentsTurnEquip()) {considerAllCards();}
        if (Game.isActEffAttackStopper()) {considerAllEquipCards();} // effect of Attack Stopper
        if (Game.isActEffBigAttackStopper()) {considerAllCards();}
    }
    
    // asks for a final confirmation before tributing (or discarding or banishing) the selected cards
    public static void confirmTribute() {
        int intDialogResult = JOptionPane.showOptionDialog(null, "Sacrifice selected cards?", "Are you sure?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[]{"yes", "no"}, "no");
        if (intDialogResult==0){discardTributeBanishCardsAsCost();}
    }
    
    // in order not to repeat oneself, out-source here what happens when one wants to choose the nth considered card
    public static void chooseNthHandCard (JButton button, int n) {
        if (Game.hasToRevealAsCostForEffect()) {
            int intDialogResult = JOptionPane.showOptionDialog(null, "Do you want to reveal this card?", "Are you sure?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[]{"yes", "no"}, "no");
            if (intDialogResult==0) {
                cardChooserDialog.dispose();
                unselectEverything();
                YMonster.revealHandCard(getNthCardId(n), true);
                Game.isReadyToChooseEffTarget=true;
                YuGiOhJi.informationDialog("Click on the card you want to target.", "");
            }
        }
        else {
            selectedUnselectHandCard(button, n, getNthCardId(n));
        }
    }
    
    // in order not to repeat oneself, out-source here what happens when one wants to choose the nth considered monster card
    public static void chooseNthMonsterCard (JButton button, int n) {
        if (Game.hasToRevealAsCostForEffect()) {
            int intDialogResult = JOptionPane.showOptionDialog(null, "Do you want to reveal this card?", "Are you sure?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[]{"yes", "no"}, "no");
            if (intDialogResult==0) {
                cardChooserDialog.dispose();
                unselectEverything();
                YMonster.revealFaceDownMonster(getNthConsideredMonster(n).sumMonsterNumber, true);
                Game.isReadyToChooseEffTarget=true;
                YuGiOhJi.informationDialog("Click on the card you want to target.", "");
            }
        }
        else {
            selectedUnselectMonster(button, getNthConsideredMonster(n), getNthConsideredMonster(n).sumMonsterNumber);
        }
    }
    
    // from here on:
    // --- reactions of the buttons ---
    
    @Override
    public void actionPerformed (ActionEvent ae){
        if (ae.getSource() == YChooseCardWindow.buttonPayCost) {
            if (Game.isAllowingDifferentCostValuesForEffect()) {
                if (selectedSemipoints==1 || selectedSemipoints==2) {
                    confirmTribute(); // special case for some effects that allow paying either 1/2 card or 1 card
                }
                else {
                    YuGiOhJi.errorDialog("Click on a card to sacrifice it for an effect or summoning.", "Select right amount of cards!");
                }
            }
            else {
                if (toBePaidSemipoints!=selectedSemipoints) {
                    YuGiOhJi.errorDialog("Click on a card to sacrifice it for an effect or summoning.", "Select right amount of cards!");
                }
                else {
                    if (Game.isActEffSearch() && HandPlayer.numberOfCards==10) {
                        if (intTributeHandCardId1==0 && intTributeHandCardId2==0) {
                            YuGiOhJi.errorDialog("Since you have 10 cards on the hand, you have to select one card from the hand for discarding.", "Too many cards on hand!");
                        }
                        else {
                            confirmTribute();
                        }
                    }
                    if (Game.isActEffCopyCat()) {
                        if ((intTributeHandCardId1!=0 && !Hand.getHand(true).getCardOnHandByCardID(intTributeHandCardId1).hasStealableEffect()) || (intTributeHandCardId2!=0 && !Hand.getHand(true).getCardOnHandByCardID(intTributeHandCardId2).hasStealableEffect())) {
                            YuGiOhJi.errorDialog("You can neither copy the effect of the upper monster nor the lower monster.", "Choose a different card!");
                        }
                        else {
                            confirmTribute();
                        }
                    }
                    else if ( (Game.isActSumEndbossHand() || Game.isActSumEndbossGY() || Game.isActEffReviveMidboss()) && !SummonedMonster.hasFreeMonsterZone(true) ) {
                        if (intTributeMonsterNo1==0 && intTributeMonsterNo2==0 && intTributeMonsterNo3==0) {
                            YuGiOhJi.errorDialog("Since you have the maximum number of summoned monsters on the field, you have to tribute at least one monster.", "No free monster card zone!");
                        }
                        else {
                            confirmTribute();
                        }
                    }
                    else { // here we end up usually, when using most effects
                        confirmTribute();
                    }
                }
            }
            
        }
        // (un)select cards on hand
        if (ae.getSource() == YChooseCardWindow.buttonHandCard1) {chooseNthHandCard(buttonHandCard1, 1);}
        if (ae.getSource() == YChooseCardWindow.buttonHandCard2) {chooseNthHandCard(buttonHandCard2, 2);}
        if (ae.getSource() == YChooseCardWindow.buttonHandCard3) {chooseNthHandCard(buttonHandCard3, 3);}
        if (ae.getSource() == YChooseCardWindow.buttonHandCard4) {chooseNthHandCard(buttonHandCard4, 4);}
        if (ae.getSource() == YChooseCardWindow.buttonHandCard5) {chooseNthHandCard(buttonHandCard5, 5);}
        if (ae.getSource() == YChooseCardWindow.buttonHandCard6) {chooseNthHandCard(buttonHandCard6, 6);}
        if (ae.getSource() == YChooseCardWindow.buttonHandCard7) {chooseNthHandCard(buttonHandCard7, 7);}
        if (ae.getSource() == YChooseCardWindow.buttonHandCard8) {chooseNthHandCard(buttonHandCard8, 8);}
        if (ae.getSource() == YChooseCardWindow.buttonHandCard9) {chooseNthHandCard(buttonHandCard9, 9);}
        if (ae.getSource() == YChooseCardWindow.buttonHandCard10) {chooseNthHandCard(buttonHandCard10, 10);}
        // (un)select monster cards
        if (ae.getSource() == YChooseCardWindow.buttonMonsterCard1) {chooseNthMonsterCard(buttonMonsterCard1, 1);}
        if (ae.getSource() == YChooseCardWindow.buttonMonsterCard2) {chooseNthMonsterCard(buttonMonsterCard2, 2);}
        if (ae.getSource() == YChooseCardWindow.buttonMonsterCard3) {chooseNthMonsterCard(buttonMonsterCard3, 3);}
        if (ae.getSource() == YChooseCardWindow.buttonMonsterCard4) {chooseNthMonsterCard(buttonMonsterCard4, 4);}
        if (ae.getSource() == YChooseCardWindow.buttonMonsterCard5) {chooseNthMonsterCard(buttonMonsterCard5, 5);}
        // (un)select cards in GY
        if (ae.getSource() == YChooseCardWindow.buttonGYCard1) {selectedUnselectGYCard(buttonGYCard1, 1, consideredGYCardId1);}
        if (ae.getSource() == YChooseCardWindow.buttonGYCard2) {selectedUnselectGYCard(buttonGYCard2, 2, consideredGYCardId2);}
        if (ae.getSource() == YChooseCardWindow.buttonGYCard3) {selectedUnselectGYCard(buttonGYCard3, 3, consideredGYCardId3);}
        if (ae.getSource() == YChooseCardWindow.buttonGYCard4) {selectedUnselectGYCard(buttonGYCard4, 4, consideredGYCardId4);}
        if (ae.getSource() == YChooseCardWindow.buttonGYCard5) {selectedUnselectGYCard(buttonGYCard5, 5, consideredGYCardId5);}
        if (ae.getSource() == YChooseCardWindow.buttonGYCard6) {selectedUnselectGYCard(buttonGYCard6, 6, consideredGYCardId6);}
        if (ae.getSource() == YChooseCardWindow.buttonGYCard7) {selectedUnselectGYCard(buttonGYCard7, 7, consideredGYCardId7);}
        if (ae.getSource() == YChooseCardWindow.buttonGYCard8) {selectedUnselectGYCard(buttonGYCard8, 8, consideredGYCardId8);}
        if (ae.getSource() == YChooseCardWindow.buttonGYCard9) {selectedUnselectGYCard(buttonGYCard9, 9, consideredGYCardId9);}
        if (ae.getSource() == YChooseCardWindow.buttonGYCard10) {selectedUnselectGYCard(buttonGYCard10, 10, consideredGYCardId10);}
        if (ae.getSource() == YChooseCardWindow.buttonGYCard11) {selectedUnselectGYCard(buttonGYCard10, 11, consideredGYCardId11);}
        if (ae.getSource() == YChooseCardWindow.buttonGYCard12) {selectedUnselectGYCard(buttonGYCard12, 12, consideredGYCardId12);}
        if (ae.getSource() == YChooseCardWindow.buttonGYCard13) {selectedUnselectGYCard(buttonGYCard13, 13, consideredGYCardId13);}
        if (ae.getSource() == YChooseCardWindow.buttonGYCard14) {selectedUnselectGYCard(buttonGYCard14, 14, consideredGYCardId14);}
        if (ae.getSource() == YChooseCardWindow.buttonGYCard15) {selectedUnselectGYCard(buttonGYCard15, 15, consideredGYCardId15);}
        if (ae.getSource() == YChooseCardWindow.buttonGYCard16) {selectedUnselectGYCard(buttonGYCard16, 16, consideredGYCardId16);}
        if (ae.getSource() == YChooseCardWindow.buttonGYCard17) {selectedUnselectGYCard(buttonGYCard17, 17, consideredGYCardId17);}
        if (ae.getSource() == YChooseCardWindow.buttonGYCard18) {selectedUnselectGYCard(buttonGYCard18, 18, consideredGYCardId18);}
        if (ae.getSource() == YChooseCardWindow.buttonGYCard19) {selectedUnselectGYCard(buttonGYCard19, 19, consideredGYCardId19);}
        if (ae.getSource() == YChooseCardWindow.buttonGYCard20) {selectedUnselectGYCard(buttonGYCard20, 20, consideredGYCardId20);}
        if (ae.getSource() == YChooseCardWindow.buttonGYCard21) {selectedUnselectGYCard(buttonGYCard21, 21, consideredGYCardId21);}
        if (ae.getSource() == YChooseCardWindow.buttonGYCard22) {selectedUnselectGYCard(buttonGYCard22, 22, consideredGYCardId22);}
        if (ae.getSource() == YChooseCardWindow.buttonGYCard23) {selectedUnselectGYCard(buttonGYCard23, 23, consideredGYCardId23);}
        if (ae.getSource() == YChooseCardWindow.buttonGYCard24) {selectedUnselectGYCard(buttonGYCard24, 24, consideredGYCardId24);}
        if (ae.getSource() == YChooseCardWindow.buttonGYCard25) {selectedUnselectGYCard(buttonGYCard25, 25, consideredGYCardId25);}
        if (ae.getSource() == YChooseCardWindow.buttonGYCard26) {selectedUnselectGYCard(buttonGYCard26, 26, consideredGYCardId26);}
        if (ae.getSource() == YChooseCardWindow.buttonGYCard27) {selectedUnselectGYCard(buttonGYCard27, 27, consideredGYCardId27);}
        if (ae.getSource() == YChooseCardWindow.buttonGYCard28) {selectedUnselectGYCard(buttonGYCard28, 28, consideredGYCardId28);}
        if (ae.getSource() == YChooseCardWindow.buttonGYCard29) {selectedUnselectGYCard(buttonGYCard29, 29, consideredGYCardId29);}
        if (ae.getSource() == YChooseCardWindow.buttonGYCard30) {selectedUnselectGYCard(buttonGYCard30, 30, consideredGYCardId30);}
        if (ae.getSource() == YChooseCardWindow.buttonGYCard31) {selectedUnselectGYCard(buttonGYCard31, 31, consideredGYCardId31);}
        if (ae.getSource() == YChooseCardWindow.buttonGYCard32) {selectedUnselectGYCard(buttonGYCard32, 32, consideredGYCardId32);}
        if (ae.getSource() == YChooseCardWindow.buttonGYCard33) {selectedUnselectGYCard(buttonGYCard33, 33, consideredGYCardId33);}
        if (ae.getSource() == YChooseCardWindow.buttonGYCard34) {selectedUnselectGYCard(buttonGYCard34, 34, consideredGYCardId34);}
        if (ae.getSource() == YChooseCardWindow.buttonGYCard35) {selectedUnselectGYCard(buttonGYCard35, 35, consideredGYCardId35);}
        if (ae.getSource() == YChooseCardWindow.buttonGYCard36) {selectedUnselectGYCard(buttonGYCard36, 36, consideredGYCardId36);}
        if (ae.getSource() == YChooseCardWindow.buttonGYCard37) {selectedUnselectGYCard(buttonGYCard37, 37, consideredGYCardId37);}
        if (ae.getSource() == YChooseCardWindow.buttonGYCard38) {selectedUnselectGYCard(buttonGYCard38, 38, consideredGYCardId38);}
        if (ae.getSource() == YChooseCardWindow.buttonGYCard39) {selectedUnselectGYCard(buttonGYCard39, 39, consideredGYCardId39);}
        // (un)select equip cards
        if (ae.getSource() == YChooseCardWindow.buttonEquipCard1) {selectUnselectEquipCard(buttonEquipCard1, 1);}
        if (ae.getSource() == YChooseCardWindow.buttonEquipCard2) {selectUnselectEquipCard(buttonEquipCard2, 2);}
        if (ae.getSource() == YChooseCardWindow.buttonEquipCard3) {selectUnselectEquipCard(buttonEquipCard3, 3);}
        if (ae.getSource() == YChooseCardWindow.buttonEquipCard4) {selectUnselectEquipCard(buttonEquipCard4, 4);}
        if (ae.getSource() == YChooseCardWindow.buttonEquipCard5) {selectUnselectEquipCard(buttonEquipCard5, 5);}
        if (ae.getSource() == YChooseCardWindow.buttonEquipCard6) {selectUnselectEquipCard(buttonEquipCard6, 6);}
        if (ae.getSource() == YChooseCardWindow.buttonEquipCard7) {selectUnselectEquipCard(buttonEquipCard7, 7);}
        if (ae.getSource() == YChooseCardWindow.buttonEquipCard8) {selectUnselectEquipCard(buttonEquipCard8, 8);}
        if (ae.getSource() == YChooseCardWindow.buttonEquipCard9) {selectUnselectEquipCard(buttonEquipCard9, 9);}
        if (ae.getSource() == YChooseCardWindow.buttonEquipCard10) {selectUnselectEquipCard(buttonEquipCard10, 10);}
        if (ae.getSource() == YChooseCardWindow.buttonEquipCard11) {selectUnselectEquipCard(buttonEquipCard11, 11);}
        if (ae.getSource() == YChooseCardWindow.buttonEquipCard12) {selectUnselectEquipCard(buttonEquipCard12, 12);}
        if (ae.getSource() == YChooseCardWindow.buttonEquipCard13) {selectUnselectEquipCard(buttonEquipCard13, 13);}
        if (ae.getSource() == YChooseCardWindow.buttonEquipCard14) {selectUnselectEquipCard(buttonEquipCard14, 14);}
        if (ae.getSource() == YChooseCardWindow.buttonEquipCard15) {selectUnselectEquipCard(buttonEquipCard15, 15);}
        
    }
    
    // in order not to repeat oneself, out-source here what happens when one hovers with the mouse over a card
    public static void hoverOnCard (String imagePath){
        YuGiOhJi.preview.setIcon(new RescaledIcon(YuGiOhJi.class.getResource(imagePath)));
    }
    
    // in order not to repeat oneself, out-source here what happens when one hovers with the mouse over a GY card
    public static void hoverOnGYCard (int cardNo){
        YuGiOhJi.preview.setIcon(new RescaledIcon(YuGiOhJi.class.getResource(consideredGYCards.getNthCardOfDeck(cardNo).bigCardPath)));
    }
    
    // what happens when one leaves any zone (just show the big version of the back side of each card)
    @Override
    public void mouseExited(MouseEvent me) {
        YuGiOhJi.preview.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
    }
    
    // what happens when one enters a certain zone with the mouse (show big preview screen depending on card)
    @Override
    public void mouseEntered(MouseEvent me) {
        // display larger (readable) version of cards in standard tribute window
        // display larger version of considered cards on hand
        if (me.getSource() == YChooseCardWindow.buttonHandCard1) {hoverOnCard(consideredHandCards.Cards[1].bigCardPath);}
        if (me.getSource() == YChooseCardWindow.buttonHandCard2) {hoverOnCard(consideredHandCards.Cards[2].bigCardPath);}
        if (me.getSource() == YChooseCardWindow.buttonHandCard3) {hoverOnCard(consideredHandCards.Cards[3].bigCardPath);}
        if (me.getSource() == YChooseCardWindow.buttonHandCard4) {hoverOnCard(consideredHandCards.Cards[4].bigCardPath);}
        if (me.getSource() == YChooseCardWindow.buttonHandCard5) {hoverOnCard(consideredHandCards.Cards[5].bigCardPath);}
        if (me.getSource() == YChooseCardWindow.buttonHandCard6) {hoverOnCard(consideredHandCards.Cards[6].bigCardPath);}
        if (me.getSource() == YChooseCardWindow.buttonHandCard7) {hoverOnCard(consideredHandCards.Cards[7].bigCardPath);}
        if (me.getSource() == YChooseCardWindow.buttonHandCard8) {hoverOnCard(consideredHandCards.Cards[8].bigCardPath);}
        if (me.getSource() == YChooseCardWindow.buttonHandCard9) {hoverOnCard(consideredHandCards.Cards[9].bigCardPath);}
        if (me.getSource() == YChooseCardWindow.buttonHandCard10) {hoverOnCard(consideredHandCards.Cards[10].bigCardPath);}
        // display larger version of considered summoned monsters
        if (me.getSource() == YChooseCardWindow.buttonMonsterCard1) {hoverOnCard(considSumMonster1.Card.bigCardPath);}
        if (me.getSource() == YChooseCardWindow.buttonMonsterCard2) {hoverOnCard(considSumMonster2.Card.bigCardPath);}
        if (me.getSource() == YChooseCardWindow.buttonMonsterCard3) {hoverOnCard(considSumMonster3.Card.bigCardPath);}
        if (me.getSource() == YChooseCardWindow.buttonMonsterCard4) {hoverOnCard(considSumMonster4.Card.bigCardPath);}
        if (me.getSource() == YChooseCardWindow.buttonMonsterCard5) {hoverOnCard(considSumMonster5.Card.bigCardPath);}
        // display larger version of considered cards in GY
        if (me.getSource() == YChooseCardWindow.buttonGYCard1) {hoverOnGYCard(1);}
        if (me.getSource() == YChooseCardWindow.buttonGYCard2) {hoverOnGYCard(2);}
        if (me.getSource() == YChooseCardWindow.buttonGYCard3) {hoverOnGYCard(3);}
        if (me.getSource() == YChooseCardWindow.buttonGYCard4) {hoverOnGYCard(4);}
        if (me.getSource() == YChooseCardWindow.buttonGYCard5) {hoverOnGYCard(5);}
        if (me.getSource() == YChooseCardWindow.buttonGYCard6) {hoverOnGYCard(6);}
        if (me.getSource() == YChooseCardWindow.buttonGYCard7) {hoverOnGYCard(7);}
        if (me.getSource() == YChooseCardWindow.buttonGYCard8) {hoverOnGYCard(8);}
        if (me.getSource() == YChooseCardWindow.buttonGYCard9) {hoverOnGYCard(9);}
        if (me.getSource() == YChooseCardWindow.buttonGYCard10) {hoverOnGYCard(10);}
        if (me.getSource() == YChooseCardWindow.buttonGYCard11) {hoverOnGYCard(11);}
        if (me.getSource() == YChooseCardWindow.buttonGYCard12) {hoverOnGYCard(12);}
        if (me.getSource() == YChooseCardWindow.buttonGYCard13) {hoverOnGYCard(13);}
        if (me.getSource() == YChooseCardWindow.buttonGYCard14) {hoverOnGYCard(14);}
        if (me.getSource() == YChooseCardWindow.buttonGYCard15) {hoverOnGYCard(15);}
        if (me.getSource() == YChooseCardWindow.buttonGYCard16) {hoverOnGYCard(16);}
        if (me.getSource() == YChooseCardWindow.buttonGYCard17) {hoverOnGYCard(17);}
        if (me.getSource() == YChooseCardWindow.buttonGYCard18) {hoverOnGYCard(18);}
        if (me.getSource() == YChooseCardWindow.buttonGYCard19) {hoverOnGYCard(19);}
        if (me.getSource() == YChooseCardWindow.buttonGYCard20) {hoverOnGYCard(20);}
        if (me.getSource() == YChooseCardWindow.buttonGYCard21) {hoverOnGYCard(21);}
        if (me.getSource() == YChooseCardWindow.buttonGYCard22) {hoverOnGYCard(22);}
        if (me.getSource() == YChooseCardWindow.buttonGYCard23) {hoverOnGYCard(23);}
        if (me.getSource() == YChooseCardWindow.buttonGYCard24) {hoverOnGYCard(24);}
        if (me.getSource() == YChooseCardWindow.buttonGYCard25) {hoverOnGYCard(25);}
        if (me.getSource() == YChooseCardWindow.buttonGYCard26) {hoverOnGYCard(26);}
        if (me.getSource() == YChooseCardWindow.buttonGYCard27) {hoverOnGYCard(27);}
        if (me.getSource() == YChooseCardWindow.buttonGYCard28) {hoverOnGYCard(28);}
        if (me.getSource() == YChooseCardWindow.buttonGYCard29) {hoverOnGYCard(29);}
        if (me.getSource() == YChooseCardWindow.buttonGYCard30) {hoverOnGYCard(30);}
        if (me.getSource() == YChooseCardWindow.buttonGYCard31) {hoverOnGYCard(31);}
        if (me.getSource() == YChooseCardWindow.buttonGYCard32) {hoverOnGYCard(32);}
        if (me.getSource() == YChooseCardWindow.buttonGYCard33) {hoverOnGYCard(33);}
        if (me.getSource() == YChooseCardWindow.buttonGYCard34) {hoverOnGYCard(34);}
        if (me.getSource() == YChooseCardWindow.buttonGYCard35) {hoverOnGYCard(35);}
        if (me.getSource() == YChooseCardWindow.buttonGYCard36) {hoverOnGYCard(36);}
        if (me.getSource() == YChooseCardWindow.buttonGYCard37) {hoverOnGYCard(37);}
        if (me.getSource() == YChooseCardWindow.buttonGYCard38) {hoverOnGYCard(38);}
        if (me.getSource() == YChooseCardWindow.buttonGYCard39) {hoverOnGYCard(39);}
        // display larger version of considered equip cards
        if (me.getSource() == YChooseCardWindow.buttonEquipCard1) {hoverOnCard(getNthEquipCard(1).Card.bigCardPath);}
        if (me.getSource() == YChooseCardWindow.buttonEquipCard2) {hoverOnCard(getNthEquipCard(2).Card.bigCardPath);}
        if (me.getSource() == YChooseCardWindow.buttonEquipCard3) {hoverOnCard(getNthEquipCard(3).Card.bigCardPath);}
        if (me.getSource() == YChooseCardWindow.buttonEquipCard4) {hoverOnCard(getNthEquipCard(4).Card.bigCardPath);}
        if (me.getSource() == YChooseCardWindow.buttonEquipCard5) {hoverOnCard(getNthEquipCard(5).Card.bigCardPath);}
        if (me.getSource() == YChooseCardWindow.buttonEquipCard6) {hoverOnCard(getNthEquipCard(6).Card.bigCardPath);}
        if (me.getSource() == YChooseCardWindow.buttonEquipCard7) {hoverOnCard(getNthEquipCard(7).Card.bigCardPath);}
        if (me.getSource() == YChooseCardWindow.buttonEquipCard8) {hoverOnCard(getNthEquipCard(8).Card.bigCardPath);}
        if (me.getSource() == YChooseCardWindow.buttonEquipCard9) {hoverOnCard(getNthEquipCard(9).Card.bigCardPath);}
        if (me.getSource() == YChooseCardWindow.buttonEquipCard10) {hoverOnCard(getNthEquipCard(10).Card.bigCardPath);}
        if (me.getSource() == YChooseCardWindow.buttonEquipCard11) {hoverOnCard(getNthEquipCard(11).Card.bigCardPath);}
        if (me.getSource() == YChooseCardWindow.buttonEquipCard12) {hoverOnCard(getNthEquipCard(12).Card.bigCardPath);}
        if (me.getSource() == YChooseCardWindow.buttonEquipCard13) {hoverOnCard(getNthEquipCard(13).Card.bigCardPath);}
        if (me.getSource() == YChooseCardWindow.buttonEquipCard14) {hoverOnCard(getNthEquipCard(14).Card.bigCardPath);}
        if (me.getSource() == YChooseCardWindow.buttonEquipCard15) {hoverOnCard(getNthEquipCard(15).Card.bigCardPath);}
        
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
