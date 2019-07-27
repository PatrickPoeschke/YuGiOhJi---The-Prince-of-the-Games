package yugiohji;

/**
 * This class creates windows for given more detailed information on YuGiOhJi-cards.
 * This includes general information about every card,
 * like basic strategies and hints on how one can use cards.
 * Summoned monsters will show more detailed information about the state they are currently in,
 * depending on the monster:
 * For some monsters it might be relevant how man monsters they have already defeated.
 * For other monsters it is more important, if they can still use their effects this turn.
 * 
 */

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import static yugiohji.YuGiOhJi.Card;
import static yugiohji.YuGiOhJi.Game;
import static yugiohji.YuGiOhJi.Mon;
import static yugiohji.YuGiOhJi.window7;

public class YCardInfoWindow implements ComponentListener {
    
    public static YCard ConsideredCard;
    
    public static int initialInfoWidth=1000;
    public static int initialInfoHeight=700;
    public static int currentInfoWidth=1000;
    public static int currentInfoHeight=700;
    
    public static JDialog cardInfoDialog;
    JScrollPane scrollPane;
    public static int initialScrollPaneWidth;
    public static int initialScrollPaneHeight;
    public static int currentScrollPaneWidth;
    public static int currentScrollPaneHeight;
    public static JPanel panel7;
    
    public static JLabel displayWholeText;
    public static JLabel bigCard;
    
    public static String additionalInfoAtTop="";
    public static String currentWorthOfCard="";
    public static String headlineSumMonsterInfo="<br><span style=\"font-size:" + 120 + "%\">Properties of the summoned monster:</span><br>";
    public static String headlineEquipCardInfo="<br><span style=\"font-size:" + 120 + "%\">Properties of the equip card:</span><br>";
    public static String paragraphSumMonsterStats="";
    public static String sumMonsterStatsBoostedInfo="";
    public static String lineBaseStats="";
    public static String lineCurrentMode="";
    public static String linePiercingAttack="<br>* has currently piercing attack<br>";
    public static String lineNegatedEffects="<br><span style=\"color:red\">* can currently not use its effects</span><br>";
    public static String lineNoMoreOncePerTurnEffect="<br><span style=\"color:red\">* can not use its effects this turn any more</span><br>";
    
    public static String headlineCardStrategiesAndHints="<br><span style=\"font-size:" + 120 + "%\">Card strategies and hints:</span><br>";
    public static String paragraphGenCardInfo=""; // has turned out to be searchable information only
    
    public static String paragraphCardStrategiesAndHints="";
    public static String searchableByInfo="";
    public static String searchableByInfoDefault="<br>* can be searched by the on field effect of " + YuGiOhJi.CardGrabber.monsterName + "<br>";
    
    public static String headlineUpperMonster="<br><br><span style=\"font-size:" + 120 + "%\">Upper monster:</span><br>";
    public static String paragraphUpperMonsterInfo="";
    public static String upperMonsterStats="";
    public static String upperMonsterSummonability="";
    public static String headlineMonsterStrategiesAndHints="<br>Monster strategies and hints:<br>";
    
    public static String headlineLowerMonster="<br><span style=\"font-size:" + 120 + "%\">Lower monster:</span><br>";
    public static String paragraphLowerMonsterInfo="";
    public static String lowerMonsterStats="";
    public static String lowerMonsterSummonability="";
    
    // constructor for info window
    public YCardInfoWindow (String windowTitle) {
        
        panel7 = new JPanel();
        panel7.setPreferredSize(new Dimension(initialScrollPaneWidth, initialScrollPaneHeight));
        panel7.setLayout(null);
        
        scrollPane = new JScrollPane(panel7);
        
        cardInfoDialog = new JDialog();
        cardInfoDialog.setTitle(windowTitle);
        cardInfoDialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // important difference to the YChooseCardWindow: This one is not modal, i.e. one can keep open the info window of a card(s) during the whole game.
        cardInfoDialog.getContentPane().addComponentListener(this);
        double scaleFactorX = (double) YuGiOhJi.currentFrameWidth/YuGiOhJi.initialFrameWidth;
        double scaleFactorY = (double) YuGiOhJi.currentFrameHeight/YuGiOhJi.initialFrameHeight;
        int posX = (int) Math.round(100*scaleFactorX);
        int posY = (int) Math.round(30*scaleFactorY);
        initialInfoWidth = (int) Math.round(1000*scaleFactorX);
        initialInfoHeight = (int) Math.round(700*scaleFactorY);
        cardInfoDialog.setBounds(posX, posY, initialInfoWidth, initialInfoHeight); // set bounds large enough that the title is completely readable
        cardInfoDialog.getContentPane().setLayout(new BorderLayout(0, 0));
        cardInfoDialog.getContentPane().add(scrollPane, BorderLayout.CENTER);
        cardInfoDialog.setVisible(true);
        
        // larger version of single card in which one can read the card text
        bigCard = new JLabel(new RescaledIcon(this.getClass().getResource(ConsideredCard.bigCardPath)));
        bigCard.setBounds(0, 0, 400, 600);
        bigCard.setVisible(true);
        panel7.add(bigCard);
        
        displayWholeText = new JLabel("");
        displayWholeText.setBounds(410, 0, 600, initialScrollPaneHeight);
        displayWholeText.setVisible(true);
        panel7.add(displayWholeText);
        
    }
    
    @Override
    public void componentHidden(ComponentEvent ce) {};
    @Override
    public void componentShown(ComponentEvent ce) {};
    @Override
    public void componentMoved(ComponentEvent ce) {};
    @Override
    public void componentResized(ComponentEvent ce) {
        currentInfoWidth = cardInfoDialog.getWidth();
        currentInfoHeight = cardInfoDialog.getHeight();
        rescaleEverything();
    };
    
    // calculates rescaling of horizontal coordinates (and widths)
    public static int rescaleX (int x) {
        int newValue = (int) Math.round(x*currentInfoWidth/initialInfoWidth);
        return newValue;
    }
    
    // calculates rescaling of vertical coordinates (and heights)
    public static int rescaleY (int y) {
        int newValue = (int) Math.round(y*currentInfoHeight/initialInfoHeight);
        return newValue;
    }
    
    // rescales a given label (uses current window size saved by global variables)
    public static void rescaleLabel(JLabel labelName, int intialPosX, int intialPosY, int intialLabelWidth, int intialLabelHeight) {
        labelName.setBounds(rescaleX(intialPosX), rescaleY(intialPosY), rescaleX(intialLabelWidth), rescaleY(intialLabelHeight));
    }
    
    // simply rescales all graphical components, i.e. all buttons and labels, one by one
    public static void rescaleEverything() {
        // rescale content
        rescaleLabel(bigCard, 0, 0, 400, 600);
        rescaleLabel(displayWholeText, 410, 0, 600, initialScrollPaneHeight);
        // rescale scroll pane
        currentScrollPaneWidth = rescaleX(initialScrollPaneWidth);
        currentScrollPaneHeight = rescaleY(initialScrollPaneHeight);
        panel7.setPreferredSize(new Dimension(currentScrollPaneWidth, currentScrollPaneHeight));
    }
    
    // defines the space of the whole content of the window
    public static void adjustScrollPaneSize (boolean isBuildingHigherWindow) {
        initialScrollPaneWidth = 1000;
        if (isBuildingHigherWindow) {
            initialScrollPaneHeight = 1600;
        }
        else {
            initialScrollPaneHeight = 2000;
        }
        // some cards need more space for their card strategies
        if (ConsideredCard.equals(Card.NeutraliserSkillStealer) || ConsideredCard.equals(Card.AttackStopperBuggedUpgrade)) {
            initialScrollPaneHeight = (int) Math.round(initialScrollPaneHeight*1.3);
        }
        else if (ConsideredCard.equals(Card.BigBackBouncerBanisher)) {
            initialScrollPaneHeight = (int) Math.round(initialScrollPaneHeight*1.1);
        }
    }
    
    // used to display information about hand cards
    public static void openInfoWindowForHandCard (YCard Card){
        ConsideredCard = Card;
        adjustScrollPaneSize(false);
        currentWorthOfCard = "<br>current worth of card: 1 card<br>";
        openAndRescaleWindow("Hand card: " + Card.cardName);
        setContent(Card);
        putCardInfoTogetherAndDisplayIt();
    }
    
    // used to display information about summoned monsters
    public static void openInfoWindowForMonster (SummonedMonster SumMonster){
        currentWorthOfCard = "<br>current worth of card: 1 card<br>";
        ConsideredCard = SumMonster.Card;
        adjustScrollPaneSize(true);
        openAndRescaleWindow("Summoned Monster: " + SumMonster.Card.cardName);
        setContent(SumMonster);
        putCardInfoTogetherAndDisplayIt();
    }
    
    // used to display information about equip cards
    public static void openInfoWindowForEquipCard (EquipCard ECard){
        currentWorthOfCard = "<br>current worth of card: 1/2 cards<br>";
        ConsideredCard = ECard.Card;
        adjustScrollPaneSize(true);
        openAndRescaleWindow("Equip monster: " + ECard.Card.lowMonster.monsterName);
        setContent(ECard);
        putCardInfoTogetherAndDisplayIt();
    }
    
    // used to display information about cards in the deck
    public static void openInfoWindowForDeckCard (YCard Card){
        currentWorthOfCard = "";
        ConsideredCard = Card;
        adjustScrollPaneSize(false);
        openAndRescaleWindow("Deck card: " + Card.cardName);
        setContent(Card);
        putCardInfoTogetherAndDisplayIt();
    }
    
    // used to display information about cards in the graveyard
    public static void openInfoWindowForGYCard (YCard Card){
        currentWorthOfCard = "<br>current worth of card: 1/2 cards<br>";
        ConsideredCard = Card;
        adjustScrollPaneSize(false);
        openAndRescaleWindow("Graveyard card: " + Card.cardName);
        setContent(Card);
        putCardInfoTogetherAndDisplayIt();
    }
    
    // used to display information about cards in the different dimension
    public static void openInfoWindowForBanishedCard (YCard Card){
        adjustScrollPaneSize(false);
        currentWorthOfCard = "<br>current worth of card: 0<br>";
        openAndRescaleWindow("Banished card: " + Card.cardName);
        setContent(Card);
        putCardInfoTogetherAndDisplayIt();
    }
    
    // in order not to repeat oneself, out-source here the usual commands for oping the info window
    public static void openAndRescaleWindow (String windowTitle){
        window7 = new YCardInfoWindow(windowTitle);
        YCardInfoWindow.panel7.setVisible(true);
        double scaleFactorX = (double) YuGiOhJi.currentFrameWidth/YuGiOhJi.initialFrameWidth;
        double scaleFactorY = (double) YuGiOhJi.currentFrameHeight/YuGiOhJi.initialFrameHeight;
        int windowWidth = (int) Math.round(initialInfoWidth*scaleFactorX*0.9);
        int windowHeight = (int) Math.round(initialInfoHeight*scaleFactorY*0.95);
        cardInfoDialog.setSize(windowWidth, windowHeight);
    }
    
    // sets the content of the whole text as well as the big card screen
    public static void setContent (YCard Card) {
        setStats(Card);
        additionalInfoAtTop = currentWorthOfCard;
        setMostCardInfo(Card);
    }
    public static void setContent (SummonedMonster SumMonster) {
        setStats(SumMonster);
        setSumMonsterProperties(SumMonster);
        setMostCardInfo(SumMonster.Card);
    }
    public static void setContent (EquipCard ECard) {
        setEquipCardInfo(ECard);
        setMostCardInfo(ECard.Card);
    }
    
    // in order not to repeat oneself, out-source here the setting of the information that has to be done for any kind of card
    public static void setMostCardInfo (YCard Card) {
        setSummonabilityInfoOfUpperMonster(Card);
        setSummonabilityInfoOfLowerMonster(Card);
        setSearchInfo(Card);
        setGeneralCardAndCardStrategiesInfo(Card);
        putUpperAndLowerMonsterInfoTogether(Card);
    }
    
    // sets the info about the attack and defence values of both monsters of the card
    public static void setStats (YCard Card) {
        upperMonsterStats = "<br>attack/defence: " + Card.upMonster.att + " / " + Card.upMonster.def + "<br>";
        lowerMonsterStats = "<br>attack/defence: " + Card.lowMonster.att + " / " + Card.lowMonster.def + "<br>";
    }
    
    // sets the info about the attack and defence values of both monsters of the card as well as the summoned monster
    public static void setStats (SummonedMonster SumMonster) {
        setBoostedAndBaseStatsInfo(SumMonster);
        paragraphSumMonsterStats = "<br>current attack/defence: " + SumMonster.att + " / " + SumMonster.def
                + sumMonsterStatsBoostedInfo
                + lineBaseStats
                + lineCurrentMode
                + "<br>";
        upperMonsterStats = "<br>attack/defence: " + SumMonster.Card.upMonster.att + " / " + SumMonster.Card.upMonster.def + "<br>";
        lowerMonsterStats = "<br>attack/defence: " + SumMonster.Card.lowMonster.att + " / " + SumMonster.Card.lowMonster.def + "<br>";
    }
    
    // sets the info about the basic attack and defence value, as well as, if they differ from the current value
    public static void setBoostedAndBaseStatsInfo (SummonedMonster SumMonster) {
        if (!SumMonster.isLowerMonster()) {
            lineBaseStats = "<br>base attack/defence: " + SumMonster.Card.upMonster.att + " / " + SumMonster.Card.upMonster.def;
            if (SumMonster.att!=SumMonster.Card.upMonster.att || SumMonster.def!=SumMonster.Card.upMonster.def) {
                sumMonsterStatsBoostedInfo = "<br><span style=\"color:blue\">current values increased</span>";
            }
            else {
                sumMonsterStatsBoostedInfo="";
            }
        }
        else {
            lineBaseStats = "<br>base attack/defence: " + SumMonster.Card.lowMonster.att + " / " + SumMonster.Card.lowMonster.def + "";
            if (SumMonster.att!=SumMonster.Card.lowMonster.att || SumMonster.def!=SumMonster.Card.lowMonster.def) {
                sumMonsterStatsBoostedInfo = "<br><span style=\"color:blue\">current values increased</span>";
            }
            else {
                sumMonsterStatsBoostedInfo="";
            }
        }
        if (SumMonster.isInAttackMode) {
            lineCurrentMode = "<br>currently in attack mode";
        }
        else {
            lineCurrentMode = "<br>currently in defence mode";
        }
        
    }
    
    
    public static void setSumMonsterProperties (SummonedMonster SumMonster) {
        additionalInfoAtTop = headlineSumMonsterInfo + paragraphSumMonsterStats + currentWorthOfCard;
        if (SumMonster.hasPiercingDamageAbility()) {
            additionalInfoAtTop = additionalInfoAtTop + linePiercingAttack;
        }
        if (SumMonster.isNotAbleToUseItsEffects) {
            additionalInfoAtTop = additionalInfoAtTop + lineNegatedEffects;
        }
        else {
            if (!SumMonster.canStillUseOncePerTurnEffect) {
                additionalInfoAtTop = additionalInfoAtTop + lineNoMoreOncePerTurnEffect;
            }
            if (SumMonster.isUsingEffectsOfOtherMonster) {
                YMonster CopiedMonster = YMonster.getMonsterById(SumMonster.otherEffectMonsterID);
                additionalInfoAtTop = additionalInfoAtTop + "<br>* currently uses the effects of " + CopiedMonster.monsterName + "<br>"
                        + "card text of copied effects:<br>"
                        + CopiedMonster.copyableEffect + "<br>";
            }
        }
        if (SumMonster.canStillAttackThisTurn && !Game.isVeryFirstTurn() && !Game.isMainPhase2() && Game.isPlayersTurn==SumMonster.isPlayersMonster) {
            additionalInfoAtTop = additionalInfoAtTop + "<br>* can still attack this turn<br>";
        }
        String owner;
        if (SumMonster.isPlayersMonster) {
            owner = "player";
        }
        else {
            owner = "computer";
        }
        additionalInfoAtTop = additionalInfoAtTop + "<br>monster position (counted from left): " + SumMonster.sumMonsterNumber
                + "<br>monster controlled by: " + owner;
        String originalOwner;
        if (SumMonster.isPlayersMonster!=SumMonster.isOriginallyPlayersMonster) {
            if (SumMonster.isOriginallyPlayersMonster) {
                originalOwner = "player";
            }
            else {
                originalOwner = "computer";
            }
            additionalInfoAtTop = additionalInfoAtTop + "<br>original owner of monster: " + originalOwner;
        }
        additionalInfoAtTop = additionalInfoAtTop + "<br>";
    }
    
    // sets the additional information shown at the top for equip cards
    public static void setEquipCardInfo (EquipCard ECard) {
        String cardController;
        String cardOwner;
        if (ECard.isPlayersEquipCard) {cardController = "player";}
        else {cardController = "computer";}
        if (ECard.isOriginallyPlayersEquipCard) {cardOwner = "player";}
        else {cardOwner = "computer";}
        String stackOwner;
        if (ECard.isPlayersStack) {stackOwner = "player";}
        else {stackOwner = "computer";}
        additionalInfoAtTop = headlineEquipCardInfo + currentWorthOfCard
                + "<br>equip card controlled by: " + cardController
                + "<br>original owner of equip card: " + cardOwner + "<br>"
                + "<br>number of equip stack (counted from left): " + ECard.stackNumber
                + "<br>owner of equip stack: " + stackOwner + "<br>";
        if (ECard.isNegated) {
            additionalInfoAtTop = additionalInfoAtTop + lineNegatedEffects;
        }
    }
    
    // sets the info about ways to summon the upper monster on the card
    public static void setSummonabilityInfoOfUpperMonster (YCard Card) {
        int stars = Card.upMonster.stars;
        if (stars==4) {
            upperMonsterSummonability="<br>* can normal summon (in face up attack mode) or set (in face down defence mode) itself from hand by tributing 3 summoned monsters you control<br>";
        }
        else if (stars==3) {
            upperMonsterSummonability="<br>* can normal summon (in face up attack mode) or set (in face down defence mode) itself from hand by tributing 2 summoned monsters you control<br>"
                    + "* can special summon itself (in face up attack or defence mode) from hand or from graveyard for the cost of 2 cards<br>";
        }
        else if (stars==2) {
            upperMonsterSummonability="<br>* can normal summon (in face up attack mode) or set (in face down defence mode) itself from hand by tributing 1 summoned monster you control<br>"
                    + "* can special summon itself (in face up attack or defence mode) from hand for the cost of 1 card<br>"
                    + "* can be special summoned (in face up attack or defence mode) from graveyard by the effect of " + YuGiOhJi.Necromancer.monsterName + " for the cost of 1 card<br>";
        }
        else {
            upperMonsterSummonability="<br>* can normal summon (in face up attack mode) or set (in face down defence mode) itself from hand, if you have one monster card zone free<br>"
                    + "* can be special summoned (in face up attack or defence mode) from graveyard by the effect of " + YuGiOhJi.Necromancer.monsterName + " for the cost of 1/2 cards<br>";
        }
    }
    
    // sets the info about ways to summon the lower monster on the card
    public static void setSummonabilityInfoOfLowerMonster (YCard Card) {
        int stars = Card.lowMonster.stars;
        if (stars==1) {
            lowerMonsterSummonability="<br>* can normal summon (in face up attack mode) or set (in face down defence mode) itself from hand, if you have one monster card zone free<br>"
                    + "* can be special summoned (in face up attack or defence mode) from graveyard by the effect of " + YuGiOhJi.Necromancer.monsterName + " for the cost of 1/2 cards<br>";
            if (Card.lowMonster.isCheatChanging) { // effects of Demon
                lowerMonsterSummonability = lowerMonsterSummonability + "* can special summon itself (in face up attack or defence mode) while equipping a monster";
            }
        }
        else if (stars==2) {
            lowerMonsterSummonability="<br>* can normal summon (in face up attack mode) or set (in face down defence mode) itself from hand by tributing 1 summoned monster you control<br>"
                    + "* can special summon itself (in face up attack or defence mode) from hand for the cost of 1 card<br>"
                    + "* can be special summoned (in face up attack or defence mode) from graveyard by the effect of " + YuGiOhJi.Necromancer.monsterName + " for the cost of 1 card<br>";
        }
        else if (stars==3) {
            lowerMonsterSummonability="<br>* can normal summon (in face up attack mode) or set (in face down defence mode) itself from hand by tributing 2 summoned monsters you control<br>"
                    + "* can special summon itself (in face up attack or defence mode) from hand or from graveyard for the cost of 2 cards<br>";
        }
        else {
            lowerMonsterSummonability="<br>* can normal summon (in face up attack mode) or set (in face down defence mode) itself from hand by tributing 3 summoned monsters you control<br>";
        }
    }
    
    // sets the information about how a card can be searched
    public static void setSearchInfo (YCard ConsCard) {
        searchableByInfo = "<br>* can be searched by the on field effect of " + Mon.CardGrabber.monsterName;
        if (ConsCard.equals(Card.GodBarrier) || ConsCard.equals(Card.DemonGodDemon)) {
            searchableByInfo = searchableByInfo + "<br>* can be searched by the on hand effect of " + Card.IncorruptibleHolyLance.cardName;
        }
        if (ConsCard.equals(Card.NecromancerBackBouncer) || ConsCard.equals(Card.BigBackBouncerBanisher) || ConsCard.equals(Card.BigBanisherBurner) || ConsCard.equals(Card.BigBurnerSuicideCommando)) {
            searchableByInfo = searchableByInfo + "<br>* can be searched by the on hand effect of " + Card.SlickRusherRecklessRusher.cardName;
        }
        searchableByInfo = searchableByInfo + "<br>";
    }
    
    // sets infos about the basic idea and philosophy of a card and several interesting combos one can do with it
    public static void setGeneralCardAndCardStrategiesInfo (YCard ConsCard) {
        // write philosophie idea about the card here (like: "With this card you can summon a strong monster with this an that effect, a weak monster with this and that effect, of use this and that hand trap effect")
        // write all nice strategies about a card in here
        if (ConsCard.equals(Card.GodBarrier)) {
            paragraphGenCardInfo = searchableByInfo + "<br>This card can be used to tribute summon the by far strongest monster in the whole game. However, this uses up many resources and is thus not always the best idea or even possible. The whole card is rarely ever dead on the hand, since one may simply normal set it without any tributes and thus stall for a while.";
            paragraphCardStrategiesAndHints = "<br><br>* summarising: can summon the strongest monster in the game, when having enough cards to pay for, or just stalls a while by itself";
        }
        else if (ConsCard.equals(Card.EradicatorObstacle)) {
            paragraphGenCardInfo = searchableByInfo + "<br>This card can be used in four different ways:"
                    + "<br>* can be used to summon one of the two strongest ENDBOSSES"
                    + "<br>* same monster can destroy monsters and equip cards by effect"
                    + "<br>* can reveal monsters after normal summoning the MOOK"
                    + "<br>* can try to stall a bit by normal setting a MOOK with " + Mon.Obstacle.def + " defence";
            paragraphCardStrategiesAndHints = "<br><br>This is one of the best offensive cards in general. Either one defeats one of the strongest monsters in battle and destroy what's left by effect, or one tries to destroy monsters by effect first and if the opponent negated the effect, can still attack very well.";
        }
        else if (ConsCard.equals(Card.GodKillingSpearLance)) {
            paragraphGenCardInfo = searchableByInfo + "<br>This card can be used to summon one of the three monsters with the most attack, but can also give a monster piercing damage, allowing to deal damage even if the attacked monsters are in defence mode. Two copies of this card can very well be used together in order summon a monster with lots of attack being able to deal a lot damage.";
            paragraphCardStrategiesAndHints = "<br><br>This card is the cheaper alternative to the GOD monstes, because it has the same attack, but needs a card less to summon. Its vanishing defence value makes it risky though.";
        }
        else if (ConsCard.equals(Card.MonsterStealerSteepLearningCurve)) {
            paragraphGenCardInfo = searchableByInfo + "<br>This card lets you either train a weak monster until it rises above all in terms of pure attack power or, probably better, lets you summon an ENDBOSS with decent attack, being able to steal the best monster of the opponent, use its effect and attack its second strongest monster with it.";
            paragraphCardStrategiesAndHints = "<br><br>Stealing a monster is kind of expensive. However, even without the effect the monster is still relatively strong.";
        }
        else if (ConsCard.equals(Card.NeutraliserSkillStealer)) {
            paragraphGenCardInfo = searchableByInfo + "<br>This might very well be the by far best card in the whole game. Use it wisely. When wasted carelessly, it may cost you the victory.";
            paragraphCardStrategiesAndHints = "<br><br>There is a bit of a mind game involved here. You have to watch out for other " + Mon.Neutraliser.monsterName + "s of the opponent. There are two things you can do with a summoned " + Mon.Neutraliser.monsterName + ":"
                    + "<br>* You can negate an effect actively/preventively during your own turn or during the ends of main phases of the opponent or right after an equip card has been used."
                    + "<br>* You can react in the last possible moment to negate an effect after the opponent already paid the costs for the effect."
                    + "<br>When reacting in the last moment, your negation can not be negated. However, if you actively/preventively choose to negate an effect out of pure paranoia, the opponent might negate your " + Mon.Neutraliser.monsterName + "! That is also possible, if the opponent has " + Mon.Neutraliser.monsterName + " on the hand. Furthermore, this negation used by the opponent (no matter, if on field or on hand) can not be negated! To summarise: If you have " + Mon.Neutraliser.monsterName + " on the field, it is recommended to always wait until the last moment to negate an effect."
                    + "<br>Furthermore, watch out especially for the hand trap effect of " + Mon.Banisher.monsterName + ". It is very tempting to discard " + Mon.Neutraliser.monsterName + " in order to get an effect negation basically for free, since you plan on reviving it by itself anyway. (Like all on hand effects, this negation can not be negated!) This revival can not be negated by another " + Mon.Neutraliser.monsterName + " of the opponent. However, if the opponent has a " + Mon.Banisher.monsterName + " on hand, then your " + Mon.Neutraliser.monsterName + " will almost surely be banished, since that banishing can not be negated. Then that copy of this valuable card will be lost for the rest of the game."
                    + "<br>One of the weak spots of " + Mon.Neutraliser.monsterName + " is the following: It can be negated by getting equipped with " + Mon.BuggedUpgrade.monsterName + " by the opponent. You can not negate that equipping. Use " + Mon.Eradicator.monsterName + " or " + Mon.BigBackBouncer.monsterName + " to get rid of that equip card, or use another " + Mon.Neutraliser.monsterName + " to negate it."
                    + "<br><br>Furthermore, this card can also be used to copy effects much cheaper than with the effect of " + Mon.CopyCat.monsterName + ". However, one typically has to keep a card on the hand. Also oneself or the opponent has to have the effect already on the field."
                    + "<br>Keep in mind that one can never copy negate effects or equip effects.";
        }
        else if (ConsCard.equals(Card.CopyCatCardGrabber)) {
            paragraphGenCardInfo = searchableByInfo + "<br>This card can be used mainly in three different ways:"
                    + "<br>* can be used to summon a MIDBOSS being able to use the same effect as another monster for one turn (except for equip and negate effects)"
                    + "<br>* can be normal summoned and search out any card from the deck for the high cost of two cards (\"search effect\")"
                    + "<br>* can be normal summoned and get any card from your deck to your GY for the cost of one card (\"milling effect\")";
            paragraphCardStrategiesAndHints = "<br><br>The search and milling effect make this card potentially one of the key cards in this game. However, especially the search is very expensive. When the opponent negates it one will have a disadvantage, when it comes to the number of accessible cards.";
        }
        else if (ConsCard.equals(Card.AttackStopperBuggedUpgrade)) {
            paragraphGenCardInfo = searchableByInfo + "<br>This card can be used mainly in three different ways:"
                    + "<br>* can be used to summon or set the MIDBOSS with the by far highest defence"
                    + "<br>* in less likely case one can spare an equip card, same monster can negate an attack"
                    + "<br>* can equip any monster to negate their effects as long as it is equipping and not negated itself";
            paragraphCardStrategiesAndHints = "<br><br>The strength of the card lies in the fact that one can always re-equip other monsters and temporarily negate all their effects. This re-equipping can be used as often as one wants during a turn. This temporary negation is very versatile. One can get rid of unwanted effects of own monsters. One can make opponent's monsters easier targets. One can just keep it on a strong monster that does not need its effect, pass it on and eventually tribute it for another effect. This is a good card for a defensive and a very good card for a tricky playing style.";
        }
        else if (ConsCard.equals(Card.BigAttackStopperSword)) {
            paragraphGenCardInfo = searchableByInfo + "<br>This card can be used in three different ways:"
                    + "<br>* can be used to summon an ENDBOSS with decent attack being able to negate an attack by tributing any own card from anywhere"
                    + "<br>* can be discarded to end a battle phase of the opponent entirely, thus dodging potentially many game deciding attacks"
                    + "<br>* can equip a monster to give it a small but sometimes crucial attack boost";
            paragraphCardStrategiesAndHints = "<br><br>* all in all a versatile card for offensive as well as defensive playing styles - probably useful in any deck";
        }
        else if (ConsCard.equals(Card.DiamondSwordShield)) {
            paragraphGenCardInfo = searchableByInfo + "<br>This card can be used in three different ways:"
                    + "<br>* can be used to summon a monster indestructible by effect (not by battle though)"
                    + "<br>* can be used to summon a relatively strong MOOK"
                    + "<br>* can equip a monster to increase its defence drastically";
            paragraphCardStrategiesAndHints = "<br><br>* all in all a good card for stalling";
        }
        else if (ConsCard.equals(Card.ModeChangerExhaustedExecutioner)) {
            paragraphGenCardInfo = searchableByInfo + "<br>This card can be used in two ways:"
                    + "<br>* can be used to summon a relatively strong monster being able to change the mode of monsters, turning them into a weaker or stronger battle state"
                    + "<br>* can be used to summon one of the two strongest MOOKS, turning itself into defence mode after attacking";
            paragraphCardStrategiesAndHints = "<br><br>* using two copies of this card, the MOOK can attack and afterwards can be turned back into attack mode";
        }
        else if (ConsCard.equals(Card.SlickRusherRecklessRusher)) {
            paragraphGenCardInfo = searchableByInfo + "<br>This card can be used in three different ways:"
                    + "<br>* can be used to summon a relatively strong monster being immune to everything (except negation), in the moment it wins against other monsters"
                    + "<br>* can be used to summon one of the two strongest MOOKS, returning itself to hand at the end of the turn"
                    + "<br>* can be used once to search out: " + Card.NecromancerBackBouncer.cardName + ", " + Card.BigBackBouncerBanisher.cardName + ", " + Card.BigBanisherBurner.cardName + ", or " + Card.BigBurnerSuicideCommando.cardName + " from the deck";
            paragraphCardStrategiesAndHints = "<br><br>* all in all a card for dodging effects and simply attack well while also being a versatile searcher";
        }
        else if (ConsCard.equals(Card.IncorruptibleHolyLance)) {
            paragraphGenCardInfo = searchableByInfo + "<br>This card can be used in three different ways:"
                    + "<br>* can be used to summon a relatively strong monster being immune to everything"
                    + "<br>* can be used to summon a relatively strong monster having piercing attack, allowing to win without having to defeat all monsters of the opponent first"
                    + "<br>* can be used once to search out a " + Card.GodBarrier.cardName + " or " + Card.DemonGodDemon.cardName + " from the deck";
            paragraphCardStrategiesAndHints = "<br><br>* all in all a good card against opponents, who want to stall using weak MOOKS";
        }
        else if (ConsCard.equals(Card.NecromancerBackBouncer)) {
            paragraphGenCardInfo = searchableByInfo + "<br>This card can be used in two ways:"
                    + "<br>* can be used to summon a relatively strong monster having the ability to revive weak monsters"
                    + "<br>* can be used to normal set a monster with a suicidal effect, allowing to get rid of attacking monsters for a turn";
            paragraphCardStrategiesAndHints = "<br><br>* a useful card for most decks to recycle resources and many effects";
        }
        else if (ConsCard.equals(Card.BigBackBouncerBanisher)) {
            paragraphGenCardInfo = searchableByInfo + "<br>This card can be used in three different ways:"
                    + "<br>* can be used to summon a relatively strong monster having an active effect, allowing to get rid of unwanted cards of the opponent (at least temporarily)"
                    + "<br>* can be used to normal set a monster with the best suicidal effect, allowing to get rid of attacking monsters for good"
                    + "<br>* can be kept on hand to negate an opponent's special summoning from graveyard"
                    + "<br>The last of these three uses is arguably the best. Since this card can hand trap negate any special summoning from GY (either of MOOKS and MIDBOSSES by " + Mon.Necromancer.monsterName + " or of ENDBOSSES by themselves), might be one of the best cards in the whole game.";
            paragraphCardStrategiesAndHints = "<br><br>Use the effects of " + Mon.BigBackBouncer.monsterName + " very carefully. Returning cards of the opponent back to the hand, will get rid of them for one turn. However, the opponent will most likely use them again next turn. Even more: If negated cards (or the monsters being the weaker lower monster on their card) are returned, then you are probably helping the opponent. Don't carelessly return cards to the hand that will then become hand traps! This concerns " + Card.BigAttackStopperSword.cardName + ", " + Card.BigBackBouncerBanisher.cardName + ", and especially " + Card.NeutraliserSkillStealer.cardName + ". You have to keep in mind that, when returning summoned monster, you return the ignored other half of the card as well. This is an additional twist not known to other card games."
                    + "<br>When using the effect of " + Mon.BigBackBouncer.monsterName + " to return used up hand traps and other useful cards in your own GY to the hand though, you can not do much wrong.";
        }
        else if (ConsCard.equals(Card.BigBanisherBurner)) {
            paragraphGenCardInfo = searchableByInfo + "<br>There are three major things you can do with this card:"
                    + "<br>* can be used to summon a relatively strong monster defeating monsters in battle for good, such that they can not be revived"
                    + "<br>* same monster can preventively get rid of monsters the opponent might revive later"
                    + "<br>* can be used to summon a monster having the ability to help you winning the game without attacking at all";
            paragraphCardStrategiesAndHints = "<br><br>All in all, this card is a good support for slower and indirect playing styles. However, its attack and defence values are not too bad either.";
        }
        else if (ConsCard.equals(Card.BigBurnerSuicideCommando)) {
            paragraphGenCardInfo = searchableByInfo + "<br>This card can be used in two ways:"
                    + "<br>* can be used to summon a monster having the ability to win you the game without attacking at all, as long you can summon enough monsters"
                    + "<br>* can be used to normal set a monster with a suicidal effect, allowing to get rid of almost any attacking monster";
            paragraphCardStrategiesAndHints = "<br><br>* might very likely be the crucial card at the end of a game to finish of your opponent";
        }
        else if (ConsCard.equals(Card.FlakshipNapalm)) {
            paragraphGenCardInfo = searchableByInfo + "<br>This card can be used in two ways:"
                    + "<br>* can be used to summon a monster with a lot of attack and defence for its relatively small costs"
                    + "<br>* can be used to normal set a monster with a suicidal effect, allowing to deal a bit burn damage";
            paragraphCardStrategiesAndHints = "<br><br>* very suitable for a simple offensive playing style";
        }
        else if (ConsCard.equals(Card.DemonGodDemon)) {
            paragraphGenCardInfo = searchableByInfo + "<br>This card can be used to summon a monster with a lot of attack, using up many tributes. When used only this way, the card " + Card.GodBarrier.cardName + " should better be used instead. However, this card can also be used to effectively summon a GOD in a quicker, but more risky way. Furthermore, multiple copies of this searchable card can be used as a support for summoning GOD monsters in general.";
            paragraphCardStrategiesAndHints = "<br><br>The main combo of this card involves two copies of it and can easily summon two of its lower monsters in one turn, then can tribute one of them to turn the other one into the upper monster of this card. Because of effect restrictions, this upper monster will be in its weaker defence mode. That makes it more risky. The challange of this card is to use the effects of other monsters in order to get around these restrictions:"
                    + "<br>* the effect of " + Mon.ModeChanger.monsterName + "can be used to turn " + Mon.DemonGod.monsterName + " into attack mode and attack right away"
                    + "<br>* after getting a copy of this card into the graveyard (e.g. by paying the cost for an effect or summoning), the effect of " + Mon.Necromancer.monsterName + " can be used to get around the restrictions, by special summoning " + Mon.Demon.monsterName + " from GY into defence mode and use its cheat change effect, by tributing another " + Mon.Demon.monsterName + " to get " + Mon.DemonGod.monsterName + " in attack mode";
        }
    }
    
    // in order to repeat oneself less, use one method twice to extract most information about monsters on the card
    public static void putUpperAndLowerMonsterInfoTogether (YCard Card) {
        paragraphUpperMonsterInfo = upperMonsterStats + upperMonsterSummonability + headlineMonsterStrategiesAndHints + putMonsterInfoTogether(Card.upMonster);
        paragraphLowerMonsterInfo = lowerMonsterStats + lowerMonsterSummonability + headlineMonsterStrategiesAndHints + putMonsterInfoTogether(Card.lowMonster);
    }
    public static String putMonsterInfoTogether (YMonster Monster) {
        if (Monster.equals(Mon.Obstacle)) {
            return "<br>* can be normal set, in order to stall using a face down monster with " + Mon.Obstacle.def + " defence"
                    + "<br>* Don't underestimate the psychological effect! When setting, the opponent might very likely assume it is one of many MOOKS possessing a devastating suicidal effect triggered when destroyed (in battle) and thus maybe not attack it."
                    + "<br>* can be normal summoned and then turned into defence mode by the effect of " + Mon.ModeChanger.monsterName
                    + "<br>* can be normal summoned, used to reveal a face down monster and then tributed away for an effect or summoning, in order not to have a weak monster in attack mode any more"
                    + "<br>* after that, " + Mon.Eradicator.monsterName + " may summon itself from GY"
                    + "<br><br>copyable monster effect: " + Monster.copyableEffect + "<br>";
        }
        else if (Monster.equals(Mon.Barrier)) {
            return "<br>* a good monster to stall when normal set, since indestructible by battle"
                    + "<br>* Don't underestimate the psychological effect! When setting, the opponent might very likely assume it is one of many MOOKS possessing a devastating suicidal effect triggered when destroyed (in battle) and thus maybe not attack it."
                    + "<br>* watch out for piercing damage"
                    + "<br>* The most fatal flaw of this monster however, is the effect of " + Mon.ModeChanger.monsterName + ". If that effect is used against " + Mon.Barrier.monsterName + " and you can not negate the effect or the following attacks, you have probably lost the game, since then, all monsters will attack with full damage."
                    + "<br><br>copyable monster effect: " + Monster.copyableEffect + "<br>";
        }
        else if (Monster.equals(Mon.Napalm)) {
            return "<br>* can be normal set, in order to become a trap for the opponent to run into and deal burn damage"
                    + "<br><br>copyable monster effect: " + Monster.copyableEffect + "<br>";
        }
        else if (Monster.equals(Mon.ExhaustedExecutioner)) {
            return "<br>* as long as the opponent doesn't have piercing attack, a good option for attacking when running low on life points, since contrary to all other monsters, " + Mon.ExhaustedExecutioner.monsterName + " protects your life points after it attacked"
                    + "<br>* when equipped with " + Mon.Sword.monsterName + ", might be able to take out most MIDBOSSES in battle"
                    + "<br>* its often unwanted effect can be cancelled out by equipping it with " + Mon.BuggedUpgrade.monsterName + " or using the effects of " + Mon.Neutraliser.monsterName + " on it"
                    + "<br>* when cancelling its effect using " + Mon.BuggedUpgrade.monsterName + ", the latter can actually be reused after the battle phase to negate another effect (e.g. an own " + Mon.RecklessRusher.monsterName + ")"
                    + "<br>* is one of the two best targets for the effect of " + Mon.Burner.monsterName + " in order to inflict " + Mon.ExhaustedExecutioner.att + " burn damage"
                    + "<br>* If you have another copy of this card, you can use the effect of " + Mon.ModeChanger.monsterName + " on " + Mon.ExhaustedExecutioner.monsterName + " after it attacked. Then in each turn after that you can attack with " + Mon.ExhaustedExecutioner.monsterName + " and afterwards in main phase 2 you can use your one ordinary mode change per turn to change " + Mon.ExhaustedExecutioner.monsterName + " back into attack mode."
                    + "<br><br>copyable monster effect: " + Monster.copyableEffect + "<br>";
        }
        else if (Monster.equals(Mon.RecklessRusher)) {
            return "<br>* when equipped with " + Mon.Sword.monsterName + ", might be able to take out most MIDBOSSES in battle"
                    + "<br>* its often unwanted effect can be cancelled out by equipping it with " + Mon.BuggedUpgrade.monsterName + " or using the effects of " + Mon.Neutraliser.monsterName + " on it"
                    + "<br>* can use its effect to return itself to the hand and let its card be used in another way in the next turn (either for a search, or to summon a stronger monster)"
                    + "<br>* reusing cards this way can also be done with the monsters " + Mon.CopyCat.monsterName + " and " + Mon.SkillStealer.monsterName + " by copying the effects of this card"
                    + "<br>* is one of the two best targets for the effect of " + Mon.Burner.monsterName + " in order to inflict " + Mon.RecklessRusher.att + " burn damage"
                    + "<br><br>copyable monster effect: " + Monster.copyableEffect + "<br>";
        }
        else if (Monster.equals(Mon.SteepLearningCurve)) {
            return "<br>* can be equipped with " + Mon.Sword.monsterName + "(s), in order to make its first wins easier"
                    + "<br>* has the potential to become by far the most powerful monster in the whole game, because other monsters have limits to what their attack values can rise to"
                    + "<br><br>copyable monster effect: " + Monster.copyableEffect + "<br>";
        }
        else if (Monster.equals(Mon.Shield)) {
            return "<br>* can equip a monster to increase its defence by " + Mon.Shield.ShieldDefBoost()
                    + "<br>* when equipped itself with " + Mon.Sword.monsterName + " might be able to take out most MOOKS in battle"
                    + "<br>* can be used as a tribute for the effect of " + Mon.AttackStopper.monsterName
                    + "<br><br>copyable monster effect: " + Monster.copyableEffect + "<br>";
        }
        else if (Monster.equals(Mon.Sword)) {
            return "<br>* can equip a monster to increase its attack by " + Mon.Sword.SwordAttBoost()
                    + "<br>* when equipped itself with another " + Mon.Sword.monsterName + " might be able to take out most MOOKS in battle"
                    + "<br><br>copyable monster effect: " + Monster.copyableEffect + "<br>";
        }
        else if (Monster.equals(Mon.Lance)) {
            return "<br>* can equip a monster to enable piercing attacks"
                    + "<br>* recommended to be used on monsters with 3000 or more attack, in order to make full use of the effect"
                    + "<br>* when equipped with " + Mon.Sword.monsterName + " might be able to take out most MOOKS in battle"
                    + "<br><br>copyable monster effect: " + Monster.copyableEffect + "<br>";
        }
        else if (Monster.equals(Mon.BuggedUpgrade)) {
            return "<br>* can be used to temporarily get rid of unwanted effects of the opponent"
                    + "<br>* \"temporarily\", because the opponent might negate this card by the multiple effects of the card " + Card.NeutraliserSkillStealer.cardName + " (so watch out!)"
                    + "<br>* \"temporarily\" also, because you can make up your mind about what is the most unwanted effect and negate that one instead by re-equipping"
                    + "<br>* can even be used to negate effects of face down monsters preventively"
                    + "<br>* If you tribute a monster and save this equip card, by equipping another one of your own monsters, you can still use the effects of all of your monsters. You just have to re-equip your monsters in between."
                    + "<br>* If you are afraid the opponent might negate an effect that you can only use during your own turn anyway, you might equip it with " + Mon.BuggedUpgrade.monsterName + ". Until you maybe re-equip another monster with it in the next turn, the opponent can not the negate the effect, since it is already negated. (The opponent might negate the equip card and the monster one after another though.)"
                    + "<br>* can also be used to cancel the unwanted effects of your own monsters, " + Mon.ExhaustedExecutioner.monsterName + " and " + Mon.RecklessRusher.monsterName
                    + "<br>* while doing that, can be used to pay the cost for the effect of " + Mon.AttackStopper.monsterName + " (not being equipped with a working " + Mon.BuggedUpgrade.monsterName + " itself, of course)"
                    + "<br><br>copyable monster effect: " + Monster.copyableEffect + "<br>";
        }
        else if (Monster.equals(Mon.SuicideCommando)) {
            return "<br>* can get rid of the monster attacking this one by using its suicidal effect (triggered, when defeated in battle)"
                    + "<br>* thus recommoned for normal setting it"
                    + "<br>* can be discarded as cost for the effect of " + Mon.Burner.monsterName + " (then " + Mon.BigBurner.monsterName + " can revive itself from GY)"
                    + "<br><br>copyable monster effect: " + Monster.copyableEffect + "<br>";
        }
        else if (Monster.equals(Mon.BackBouncer)) {
            return "<br>* can get rid of the monster attacking this one by using its suicidal effect (triggered, when defeated in battle)"
                    + "<br>* this suicidal effect affects even " + Mon.DiamondSword.monsterName + " (indestructible by effect)"
                    + "<br>* thus recommoned for normal setting it"
                    + "<br>* when equipped with " + Mon.Sword.monsterName + " might be able to take out most MOOKS in battle"
                    + "<br><br>copyable monster effect: " + Monster.copyableEffect + "<br>";
        }
        else if (Monster.equals(Mon.Banisher)) {
            return "<br>* can get rid of the monster attacking this one by using its suicidal effect (triggered, when defeated in battle)"
                    + "<br>* this suicidal effect affects even " + Mon.DiamondSword.monsterName + " (indestructible by effect)"
                    + "<br>* this suicidal effects gets rid of cards of the opponent for the rest of the game"
                    + "<br>* thus recommended for normal setting it"
                    + "<br>* when equipped with " + Mon.Sword.monsterName + " might be able to take out most MOOKS in battle"
                    + "<br>* instead of summoning " + Mon.Banisher.monsterName + ", it is even more recommended keeping it on the hand as a hand trap being able to negate summonings from GY"
                    + "<br>* " + Mon.BigBanisher.monsterName + " on field and " + Mon.Banisher.monsterName + " on hand are a good combination. The first can preventively banish monsters from the opponent's graveyard. The latter can do it in the last moment, when they are about to be summoned. But neither has the effects of the other."
                    + "<br>* When purely thinking about card advantage, one should most often negate special summonings of ENDBOSSES from GY, since the opponent has to pay two cards for that and does not get the summoning while oneself only loses one card."
                    + "<br><br>copyable monster effect: " + Monster.copyableEffect + "<br>";
        }
        else if (Monster.equals(Mon.CardGrabber)) {
            return "<br>* The milling effect of this card can be used basically for free, when discarding an ENDBOSS one wanted to revive by its own effect."
                    + "<br>* Theoretically, one can even get the expensive search effect for free, when discarding two ENDBOSSES when planing on reviving them later by their own effects. One probably just runs out of cards to be able to pay for that. Furthermore, keep in mind that a revival can be negated by the opponent. This makes this monster kind of risky to use."
                    + "<br><br>copyable monster effect: " + Monster.copyableEffect + "<br>";
        }
        else if (Monster.equals(Mon.CopyCat)) {
            return "<br>* can return itself to the hand and be reused, by copying the effects of " + Mon.RecklessRusher.monsterName + " or " + Mon.BigBackBouncer.monsterName
                    + "<br>* can turn into " + Mon.CardGrabber.monsterName + " by copying the effects of " + Mon.Demon.monsterName + " and then tributing another " + Mon.Demon.monsterName
                    + "<br>* can copy the protection of " + Mon.Incorruptible.monsterName + " (ignores its equip cards, while doing so)"
                    + "<br>* by first discarding a card with a copyable effect for the effect of " + Mon.CopyCat.monsterName + ", then reviving the discarded monster (by " + Mon.Necromancer.monsterName + " or by itself), and then coyping its effect using " + Mon.SkillStealer.monsterName + ", you have the effect of that monster three times on the field"
                    + "<br>* Look up the \"copyable monster effect\" part of the card informations in order to see the part of the effect text that can be copied by " + Mon.CopyCat.monsterName + " and " + Mon.SkillStealer.monsterName + "."
                    + "<br>* can only copy the original effect printed on the card, not necessarily the current effect of the monster"
                    + "<br><br>copyable monster effect: " + Monster.copyableEffect + "<br>";
        }
        else if (Monster.equals(Mon.SkillStealer)) {
            return "<br>* can return itself to the hand and be reused, by copying the effects of " + Mon.RecklessRusher.monsterName + " or " + Mon.BigBackBouncer.monsterName
                    + "<br>* can turn into " + Mon.Neutraliser.monsterName + " by copying the effects of " + Mon.Demon.monsterName + " and then tributing copied " + Mon.Demon.monsterName
                    + "<br>* can copy the protection of " + Mon.Incorruptible.monsterName + " (ignores its equip cards, while doing so)"
                    + "<br>* by first discarding a card with a copyable effect for the effect of " + Mon.CopyCat.monsterName + ", then reviving the discarded monster (by " + Mon.Necromancer.monsterName + " or by itself), and then coyping its effect using " + Mon.SkillStealer.monsterName + ", you have the effect of that monster three times on the field"
                    + "<br>* Look up the \"copyable monster effect\" part of the card informations in order to see the part of the effect text that can be copied by " + Mon.CopyCat.monsterName + " and " + Mon.SkillStealer.monsterName + "."
                    + "<br>* can only copy the original effect printed on the card, not necessarily the current effect of the monster"
                    + "<br><br>copyable monster effect: " + Monster.copyableEffect + "<br>";
        }
        else if (Monster.equals(Mon.Flakship)) {
            return "<br>* has the best attack and defence values of all MIDBOSSES"
                    + "<br>* because of high defence and no effect, recommended for setting face down"
                    + "<br>* does not have to worry about getting its effects negated :-)"
                    + "<br><br>copyable monster effect: " + Monster.copyableEffect + "<br>";
        }
        else if (Monster.equals(Mon.HolyLance)) {
            return "<br>* can be used to attack an unequipped MOOK to deal battle damage for sure"
                    + "<br>* recommended to negate or at least reveal any face down MOOK, before doing that"
                    + "<br>* " + Mon.CopyCat.monsterName + "s and " + Mon.SkillStealer.monsterName + "s may copy its effect to attack a phalanx of face down MOOKS, if the opponent has few life points left, in order to try to win in a risky way."
                    + "<br>* even without its effect, can take out most MIDBOSSES (with them in attack mode most of them in mutual annihilation though)"
                    + "<br>* instead of summoning (or after returning it with use of " + Mon.BigBackBouncer.monsterName + "), can be used to search out a GOD card"
                    + "<br><br>copyable monster effect: " + Monster.copyableEffect + "<br>";
        }
        else if (Monster.equals(Mon.ModeChanger)) {
            return "<br>* can be used to turn monsters of your opponent with high attack into defence mode, so that they can be taken out in battle more easily"
                    + "<br>* can be used to turn your own monsters with low defence into attack mode, so that they can not be taken out in battle so easily"
                    + "<br>* natural targets for that: almost all monsters"
                    + "<br><br>copyable monster effect: " + Monster.copyableEffect + "<br>";
        }
        else if (Monster.equals(Mon.Necromancer)) {
            return "<br>* can revive monsters serving as damage dealers later on in the battle phase"
                    + "<br>* can be used to special summon monsters serving as tributes for a tribute summon"
                    + "<br>* can be used to summon monsters serving as tributes for the effect of " + Mon.BigBurner.monsterName
                    + "<br>* can use its effect, to revive another copy of itself"
                    + "<br>* Always keep in mind, that you never get a revival effect fully for free. You can not discard/tribute/unequip the exact same monster you want to revive! You have to have a valid target for the effect already in your GY before paying the costs for the effect. That is why you need multiple copies of cards for the loops described further below."
                    + "<br>* Fun fact: There is a loop: If you have two copies of the same equip monster (one equipping, one in GY), then you can pay, revive, equip, and again pay, revive ... that equip monster."
                    + "<br>* Fun fact: There is another loop: If you have two copies of the same MIDBOSS (one summoned or on hand, one in the GY), then you can tribute/discard, revive, again tribute, revive ... that monster for free."
                    + "<br>* Loops can be used to reset monster properties, like negated effects. When equipped with " + Mon.BuggedUpgrade.monsterName + " by the opponent, the latter will lose that equip card when tributing the equipped monster."
                    + "<br>* can be used to revive a " + Mon.Demon.monsterName + " in face up defence mode to get around a restriction in the " + Card.DemonGodDemon.cardName + "-combo in order to effectively summon " + Mon.DemonGod.monsterName + " in attack mode in one turn"
                    + "<br><br>copyable monster effect: " + Monster.copyableEffect + "<br>";
        }
        else if (Monster.equals(Mon.Burner)) {
            return "<br>* can use any own card (hand/summoned/equipping/GY) as tribute for its effect, as long as the card contains a MOOK with non-zero attack"
                    + "<br>* can use targets twice: once by discarding/tributing and then again by banishing from graveyard"
                    + "<br>* can banish the used up targets for the effect of " + Mon.BigBurner.monsterName + " to inflict even more burn damage"
                    + "<br>* the two best targets for the effect of " + Mon.Burner.monsterName + ": " + Mon.ExhaustedExecutioner.monsterName + ", " + Mon.RecklessRusher.monsterName
                    + "<br><br>copyable monster effect: " + Monster.copyableEffect + "<br>";
        }
        else if (Monster.equals(Mon.BigBackBouncer)) {
            return "<br>* can be used to get rid of powerful monsters of the opponent"
                    + "<br>* can be used to get rid of unknown face down monsters of the opponent, in order to avoid running into a trap"
                    + "<br>* can be used to return own monster cards or equip cards back to the hand to reuse them in another way"
                    + "<br>* can be used to recycle monsters from graveyard, especially when one can not revive them otherwise"
                    + "<br>* The last strategy might even be very useful, when used on " + Mon.Neutraliser.monsterName + ". The latter can revive itself, however might get hand trapped by a " + Mon.Banisher.monsterName + " on the hand of the opponent. (This effect may get negated by Neutraliser, but at least one doesn't lose the own Neutraliser from the GY.)"
                    + "<br>* can use its effect to return itself back to the hand becoming a valuable hand trap"
                    + "<br>* might technically also be used to return a card in the opponent's GY back to the opponent's hand, although probably not very useful"
                    + "<br><br>copyable monster effect: " + Monster.copyableEffect + "<br>";
        }
        else if (Monster.equals(Mon.BigBanisher)) {
            return "<br>* running over opposing monsters with this one, lets you gain card advantage by banishing the defeated monsters making the cards inaccessible to the opponent for the rest of the game"
                    + "<br>* when being equipped with two " + Mon.Sword.monsterName + "s, it might be able to defeat all ENDBOSSES, thus banishing them right away without having to pay for its optional effect"
                    + "<br>* can use any own card (hand/summoned/equipping/GY) as cost to preventively banish a monster in the GY of your opponent"
                    + "<br>* Since all ENDBOSSES are arguably the best monsters in this game (in respect to effects and attack values) and can also revive themselves from GY, it is very recommended to use the effect of " + Mon.BigBanisher.monsterName + " to preventively banish them from GY."
                    + "<br>* " + Mon.BigBanisher.monsterName + " on field and " + Mon.Banisher.monsterName + " on hand are a good combination. The first can preventively banish monsters from the opponent's graveyard. The latter can do it in the last moment, when they are about to be summoned. But neither has the effects of the other."
                    + "<br><br>copyable monster effect: " + Monster.copyableEffect + "<br>";
        }
        else if (Monster.equals(Mon.DiamondSword)) {
            return "<br>* does not have to fear the destruction effects of " + Mon.Eradicator.monsterName + " and " + Mon.SuicideCommando.monsterName
                    + "<br>* The similar monster " + Mon.Incorruptible.monsterName + " is a lot better protected, but can not get its attack and defence values increased."
                    + "<br>* The similar monster " + Mon.SlickRusher.monsterName + " is as well protected as " + Mon.Incorruptible.monsterName + ", at least while it is defeating a monster."
                    + "<br>* In many offensive playing styles one may want to attack with all three monsters (" + Mon.Incorruptible.monsterName + ", " + Mon.DiamondSword.monsterName + ", and " + Mon.SlickRusher.monsterName + "), if one fears possible suicidal effects of face down monsters."
                    + "<br>* better equip with " + Mon.Sword.monsterName + " or " + Mon.Shield.monsterName + ", since can still be taken out in battle"
                    + "<br><br>copyable monster effect: " + Monster.copyableEffect + "<br>";
        }
        else if (Monster.equals(Mon.SlickRusher)) {
            return "<br>* can run over any monster without having to fear any effects that trigger during or after battle"
                    + "<br>* The similar monster " + Mon.Incorruptible.monsterName + " has this ultimate protection effect even as long as it is face up, but can not get its attack and defence values increased."
                    + "<br>* The similar monster " + Mon.DiamondSword.monsterName + " can be boosted, but is way less protected."
                    + "<br>* In many offensive playing styles one may want to attack with all three monsters (" + Mon.Incorruptible.monsterName + ", " + Mon.DiamondSword.monsterName + ", and " + Mon.SlickRusher.monsterName + "), if one fears possible suicidal effects of face down monsters."
                    + "<br><br>copyable monster effect: " + Monster.copyableEffect + "<br>";
        }
        else if (Monster.equals(Mon.Incorruptible)) {
            return "<br>* has the best protection effect in the whole game: immune to all effects, when face up (even immune to negation)"
                    + "<br>* should never be set, because its effects can only be negated, while being face down"
                    + "<br>* unfortunately, its values can not be increased by equip monsters"
                    + "<br>* The similar monster " + Mon.DiamondSword.monsterName + " can be boosted, but is way less protected."
                    + "<br>* The similar monster " + Mon.SlickRusher.monsterName + " can be boosted, and is almost as well protected as " + Mon.Incorruptible.monsterName + ", at least while it is defeating a monster."
                    + "<br>* In many offensive playing styles one may want to attack with all three monsters (" + Mon.Incorruptible.monsterName + ", " + Mon.DiamondSword.monsterName + ", and " + Mon.SlickRusher.monsterName + "), if one fears possible suicidal effects of face down monsters."
                    + "<br>* Not being able to be equipped means that you can not equip it with an additional monster while it still has its effects (face up, not negated). However, it stays equipped with all its current equip cards. It doesn't get boosts by these equip cards, if it has its effects. The only things that you can choose it for, while having its effects, is copying its effects as well as tributing it for paying costs."
                    + "<br>* Its attacks can not be negated!"
                    + "<br>* can not be pierced through, if in face up defence mode"
                    + "<br>* is versatile in the sense that, if you don't need protection, but attack power, can be set, then equipped with " + Mon.BuggedUpgrade.monsterName + " as well as other equip monsters, boosting its values, like " + Mon.Sword.monsterName + ", can then be turned into attack mode, by mode changer"
                    + "<br>* even more versatile: instead of summoning (or after returning it with use of " + Mon.BigBackBouncer.monsterName + "), can be used to search out a GOD card"
                    + "<br><br>copyable monster effect: " + Monster.copyableEffect + "<br>";
        }
        else if (Monster.equals(Mon.AttackStopper)) {
            return "<br>* even negated equip cards, although seemingly useless, can be used as tribute for the effect of " + Mon.AttackStopper.monsterName
                    + "<br>* your equip cards equipping other monsters can be used as tribute as well"
                    + "<br>* natural candidates for tributes for this effect: " + Mon.Shield.monsterName + " equipping " + Mon.AttackStopper.monsterName + ", your negated equip cards, " + Mon.Demon.monsterName + ", " + Mon.BuggedUpgrade.monsterName + " equipping a " + Mon.RecklessRusher.monsterName
                    + "<br>* Even without you controlling any equip cards, " + Mon.AttackStopper.monsterName + " has still one of the highest base defence values in the whole game and thus should be summoned in defence mode."
                    + "<br>* If you don't have or want to use any equip cards, you may as well tribute set this monster. Then you can not use its effect, as long as it is face down, but without equip cards, you can not use it anyway. However, if the opponent attacks it, you will likely reflect a lot of battle damage, because of the high defence value."
                    + "<br>* This strategy can even be improved by equipping the face down " + Mon.AttackStopper.monsterName + " with a " + Mon.Shield.monsterName + ". Then it can still not use its effects, but the reflected battle damage will be even higher."
                    + "<br>* Attack negations can not be negated in the last moment. However, the effects of a summoned " + Mon.AttackStopper.monsterName + " can be negated preventively."
                    + "<br><br>copyable monster effect: " + Monster.copyableEffect + "<br>";
        }
        else if (Monster.equals(Mon.MonsterStealer)) {
            return "<br>* can steal the strongest monster of the opponent and then attack the second strongest one with it"
                    + "<br>* can steal a monster of the opponent having a very useful effect to you at that moment"
                    + "<br>* even without its effect, or not using its effect every turn, it can still deal a lot of damage"
                    + "<br>* If you don't have access to a reveal effect and can afford the high cost, you can also steal an unknown face down monster of the opponent, such that you can not run into a trap or a high defence. This is not recommended though."
                    + "<br>* You can only use the effect (and only once) or attack that turn - not both. Consider that, when planning the whole turn, especially the battle phase, ahead."
                    + "<br>* Sometimes it might be better just to steal an equip card of the opponent, e.g. if it has a good effect or high enough attack. The equip monster gets special summoned to your side of the field."
                    + "<br>* In general it is best to steal the whole summoned monster instead of just an equip card. When stealing a summoned monster, its entire equip stack follows. That means, its properties stay the same. Keep in mind, that you don't control the new equip cards. However, you can tribute the stolen monster. Then itself and all its equip cards move to the GY of their original owner. Similarly, if you return any of these cards to the hand, they move to the hand of their original owner."
                    + "<br><br>copyable monster effect: " + Monster.copyableEffect + "<br>";
        }
        else if (Monster.equals(Mon.BigBurner)) {
            return "<br>* the effect of " + Mon.Necromancer.monsterName + " can be used to summon possible tributes for the effect of " + Mon.BigBurner.monsterName
                    + "<br>* when controlling many strong monsters, you may be able to win by attacking with all of them, and then, after the battle phase, tributing them for the effect of " + Mon.BigBurner.monsterName
                    + "<br>* However, this is a risky strategy. If you almost win after having tributed most monsters and then the opponent negates " + Mon.BigBurner.monsterName + ", then you are left with hardly anything and might lose next turn."
                    + "<br>* That's why it is recommended to tribute your weak monsters first for the effect of " + Mon.BigBurner.monsterName
                    + "<br><br>copyable monster effect: " + Monster.copyableEffect + "<br>";
        }
        else if (Monster.equals(Mon.Eradicator)) {
            return "<br>* can be used to destroy a monster or an equip card of the opponent"
                    + "<br>* monsters not affected by this effect: " + Mon.DiamondSword.monsterName + ", face up " + Mon.Incorruptible.monsterName + " as well as monsters copying their effects"
                    + "<br>* when used on an unknown face down " + Mon.DiamondSword.monsterName + ", it will be revealed, but not destroyed"
                    + "<br>* even without effect still a very powerful monster being able to take out most monsters in battle."
                    + "<br>* in general, one of the best offensive monsters in the game"
                    + "<br><br>copyable monster effect: " + Monster.copyableEffect + "<br>";
        }
        else if (Monster.equals(Mon.BigAttackStopper)) {
            return "<br>* better and more expensive version of " + Mon.AttackStopper.monsterName
                    + "<br>* \"better\" in the sense that it is more flexible in how to pay the costs for negating an attack (any card instead of equip cards only)"
                    + "<br>* You can keep this card on hand and discard it, when the opponent wants to attack with all its powerful monsters in order to win the game. This way you can survive one more turn."
                    + "<br>* Attack negations can not be negated in the last moment. However, the effects of a summoned " + Mon.BigAttackStopper.monsterName + " can be negated preventively."
                    + "<br>* If it has to be, then " + Mon.BigAttackStopper.monsterName + " can even tribute itself to negate an attack."
                    + "<br><br>copyable monster effect: " + Monster.copyableEffect + "<br>";
        }
        else if (Monster.equals(Mon.Neutraliser)) {
            return "<br>* can be used to negate an effect actively/preventively of any card on the field (even face down ones) during your own turn or during the ends of phases of the opponent or after an equip effect (this negation can be negated though)"
                    + "<br>* can be used to negate an effect in the last possible moment after the opponent already paid the costs for the effect (this negation can not be negated)"
                    + "<br>* can also be used to negate the passive (i.e. non-optional) effects (typically the suicidal effects) of monsters during battle (this negation counts as a reaction and can thus not be negated)"
                    + "<br>* Whenever an effect of a card is negated by " + Mon.Neutraliser.monsterName + " and is still on the field, all effects of the negated card are negated as long as it exists on the field."
                    + "<br>* When a summoned monster is equipped with cards and then gets negated, it can not use any of its own effects. However, it still gains all possible boosts and additional effects of all non-negated equip cards, it is equipped with. So watch out. That's why, sometimes it might be better to negate equip cards instead of the summoned monsters."
                    + "<br>* Even if the opponent negated the effects of this monster forever, it is still a very powerful monster and can take out most monsters in battle."
                    + "<br>* can also be used to cancel the unwanted effects of your own monsters, " + Mon.ExhaustedExecutioner.monsterName + " and " + Mon.RecklessRusher.monsterName
                    + "<br>* If it has to be, then " + Mon.Neutraliser.monsterName + " can even tribute itself to negate an effect."
                    + "<br>* When purely thinking about card advantage, one should always negate in the last moment, when the opponent wants to use one of the most expensive effects (the one of " + Mon.MonsterStealer.monsterName + " or the search effect of " + Mon.CardGrabber.monsterName + " or the special summoning of an ENDBOSS), because the opponent pays two cards and does not get the effect while oneself pays only one card."
                    + "<br>* Look at the general strategies about how to use the whole card, for more details on " + Mon.Neutraliser.monsterName
                    + "<br><br>copyable monster effect: " + Monster.copyableEffect + "<br>";
        }
        else if (Monster.equals(Mon.GodKillingSpear)) {
            return "<br>* has enough attack to take out a GOD monster in battle"
                    + "<br>* when being equipped with " + Mon.Sword.monsterName + " while doing this, it can even survive that battle"
                    + "<br>* does not have to worry about getting its effects negated :-)"
                    + "<br>* using two copies of this card, you can give " + Mon.GodKillingSpear.monsterName + " the ability of piercing damage and thus very likely deal a lot of battle damage"
                    + "<br><br>copyable monster effect: " + Monster.copyableEffect + "<br>";
        }
        else if (Monster.equals(Mon.God)) {
            return "<br>* This is by far the strongest monster in the whole game, when it comes to attack and defence values. It does not have active protection against effects, however can reveal other monsters, in order to avoid running into traps."
                    + "<br>* before attacking an unknown MOOK, be sure to use the effect to reveal it first"
                    + "<br>* watch out for the effects of strong boss monsters being able to get rid of other monsters, especially " + Mon.MonsterStealer.monsterName
                    + "<br>* If this monster takes to much resources for you to summon it, you can use the same effect of " + Mon.Obstacle.monsterName + ". However, in order to use the effect of the latter, one has to summon a monster with " + Mon.Obstacle.att + " attack in attack mode."
                    + "<br><br>copyable monster effect: " + Monster.copyableEffect + "<br>";
        }
        else if (Monster.equals(Mon.Demon)) {
            return "<br>* If you have not normal summoned yet this turn, two copies of this card can be used for a combo to effectively get one " + Mon.DemonGod.monsterName + " on the field in defence mode. " + Mon.ModeChanger.monsterName + " might then be used to turn it into attack mode."
                    + "<br>* This effect of " + Mon.Demon.monsterName + " can be copied by other monsters, in order to turn into other monsters. However, one still has to tribute at least one original " + Mon.Demon.monsterName + " as cost for the effect."
                    + "<br>* can be used to equip a monster with it, then special summon it, and can then serve as one of three tributes for the summoning/setting of " + Mon.God.monsterName + " or " + Mon.DemonGod.monsterName
                    + "<br>* can be used as a tribute for the effect of " + Mon.AttackStopper.monsterName
                    + "<br>* can be used as a tribute for the effects of " + Mon.BigBurner.monsterName + " and " + Mon.Burner.monsterName + " (both also right after another) " + "to inflict " + Mon.Demon.att + " burn damage each time"
                    + "<br><br>copyable monster effect: " + Monster.copyableEffect + "<br>";
        }
        else if (Monster.equals(Mon.DemonGod)) {
            return "<br>* does not have to worry about getting its effects negated :-)"
                    + "<br>* Using the effects of two " + Mon.Demon.monsterName + "s and using up one normal summon, one can turn a " + Mon.Demon.monsterName + " into " + Mon.DemonGod.monsterName + ". This " + Card.DemonGodDemon.cardName + "-combo does technically not count as a summoning."
                    + "<br>* when effectively \"summoned\" this way in defence mode by the effects of " + Mon.Demon.monsterName + ", can be equipped with " + Mon.Shield.monsterName + " to make it less vulnerable"
                    + "<br>* the effect of " + Mon.ModeChanger.monsterName + " can also be used to finally turn " + Mon.DemonGod.monsterName + "into attack mode"
                    + "<br><br>copyable monster effect: " + Monster.copyableEffect + "<br>";
        }
        else {
            return ""; // shouldn't happen (only the Card "NoCard" would end up here)
        }
    }
    
    // finally put all the pieces of text together and display it
    public static void putCardInfoTogetherAndDisplayIt() {
        String wholeText = "<html><body>"
                + additionalInfoAtTop
                + headlineCardStrategiesAndHints
                + paragraphGenCardInfo
                + paragraphCardStrategiesAndHints
                + headlineUpperMonster
                + paragraphUpperMonsterInfo
                + headlineLowerMonster
                + paragraphLowerMonsterInfo
                + "</body></html>";
        displayWholeText.setText(wholeText);
        displayWholeText.setVerticalAlignment(SwingConstants.TOP);
    }
    
}
