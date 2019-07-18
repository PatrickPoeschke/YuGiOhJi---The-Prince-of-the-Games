package yugiohji;

/**
 * Similarly to the SearchWindow class this class creates windows
 * for choosing an equip card in a stack of several YuGiOhJi-equip-cards.
 * Most of the source code has been copied from the YChooseCardWindow class,
 * however this class is infinitely more simple.
 * 
 */

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import static yugiohji.YuGiOhJi.Game;
import static yugiohji.YuGiOhJi.window6;

public class YChooseEquipCardWindow implements ActionListener, MouseListener, ComponentListener {
    
    public static int initialStackWindowWidth=300;
    public static int initialStackWindowHeight=190;
    public static int currentStackWindowWidth=300;
    public static int currentStackWindowHeight=190;
    
    public static JDialog cardChooserDialog;
    JScrollPane scrollPane;
    public static int scrollPaneWidth;
    public static int scrollPaneHeight;
    public JPanel panel6;
    
    public static EStack Stack;
    
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
    
    // constructor
    public YChooseEquipCardWindow(EStack Stack, String windowTitle) {
        
        double scaleFactorX = (double) YuGiOhJi.currentFrameWidth/YuGiOhJi.initialFrameWidth;
        double scaleFactorY = (double) YuGiOhJi.currentFrameHeight/YuGiOhJi.initialFrameHeight;
        
        panel6 = new JPanel();
        
        scrollPaneWidth = (int) Math.round((10+100*Stack.numberOfCards)*scaleFactorX);
        scrollPaneHeight = (int) Math.round(125*scaleFactorY);
        
        panel6.setPreferredSize(new Dimension(scrollPaneWidth, scrollPaneHeight));
        panel6.setLayout(null);
        
        scrollPane = new JScrollPane(panel6);
        
        cardChooserDialog = new JDialog();
        cardChooserDialog.setTitle(windowTitle);
        int posX = (int) Math.round(408*scaleFactorX);
        int posY = (int) Math.round(230*scaleFactorY);
        cardChooserDialog.setBounds(posX, posY, initialStackWindowWidth, initialStackWindowHeight); // set bounds large enough that the title is completely readable
        cardChooserDialog.getContentPane().setLayout(new BorderLayout(0, 0));
        cardChooserDialog.getContentPane().add(scrollPane, BorderLayout.CENTER);
        cardChooserDialog.setVisible(true);
        cardChooserDialog.getContentPane().addComponentListener(this);
        
        buttonEquipCard1 = new JButton();
        if (Stack.EquipCards[1].isNegated) {
            buttonEquipCard1.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        }
        else if (Stack.EquipCards[1].isPlayersEquipCard) {
            buttonEquipCard1.setIcon(new RescaledIcon(this.getClass().getResource(Stack.EquipCards[1].Card.lowMonster.cardPathAtt)));
        }
        else {
            buttonEquipCard1.setIcon(new RescaledIcon(this.getClass().getResource(Stack.EquipCards[1].Card.upMonster.cardPathAtt)));
        }
        YuGiOhJi.setButtonProperties(buttonEquipCard1, 33, 10, 67, 100, Stack.numberOfCards>=1);
        buttonEquipCard1.addActionListener(this);
        buttonEquipCard1.addMouseListener(this);
        panel6.add(buttonEquipCard1);
        
        buttonEquipCard2 = new JButton();
        if (Stack.EquipCards[2].isNegated) {
            buttonEquipCard2.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        }
        else if (Stack.EquipCards[2].isPlayersEquipCard) {
            buttonEquipCard2.setIcon(new RescaledIcon(this.getClass().getResource(Stack.EquipCards[2].Card.lowMonster.cardPathAtt)));
        }
        else {
            buttonEquipCard2.setIcon(new RescaledIcon(this.getClass().getResource(Stack.EquipCards[2].Card.upMonster.cardPathAtt)));
        }
        YuGiOhJi.setButtonProperties(buttonEquipCard2, 133, 10, 67, 100, Stack.numberOfCards>=2);
        buttonEquipCard2.addActionListener(this);
        buttonEquipCard2.addMouseListener(this);
        panel6.add(buttonEquipCard2);
        
        buttonEquipCard3 = new JButton();
        if (Stack.EquipCards[3].isNegated) {
            buttonEquipCard3.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        }
        else if (Stack.EquipCards[3].isPlayersEquipCard) {
            buttonEquipCard3.setIcon(new RescaledIcon(this.getClass().getResource(Stack.EquipCards[3].Card.lowMonster.cardPathAtt)));
        }
        else {
            buttonEquipCard3.setIcon(new RescaledIcon(this.getClass().getResource(Stack.EquipCards[3].Card.upMonster.cardPathAtt)));
        }
        YuGiOhJi.setButtonProperties(buttonEquipCard3, 233, 10, 67, 100, Stack.numberOfCards>=3);
        buttonEquipCard3.addActionListener(this);
        buttonEquipCard3.addMouseListener(this);
        panel6.add(buttonEquipCard3);
        
        buttonEquipCard4 = new JButton();
        if (Stack.EquipCards[4].isNegated) {
            buttonEquipCard4.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        }
        else if (Stack.EquipCards[4].isPlayersEquipCard) {
            buttonEquipCard4.setIcon(new RescaledIcon(this.getClass().getResource(Stack.EquipCards[4].Card.lowMonster.cardPathAtt)));
        }
        else {
            buttonEquipCard4.setIcon(new RescaledIcon(this.getClass().getResource(Stack.EquipCards[4].Card.upMonster.cardPathAtt)));
        }
        YuGiOhJi.setButtonProperties(buttonEquipCard4, 333, 10, 67, 100, Stack.numberOfCards>=4);
        buttonEquipCard4.addActionListener(this);
        buttonEquipCard4.addMouseListener(this);
        panel6.add(buttonEquipCard4);
        
        buttonEquipCard5 = new JButton();
        if (Stack.EquipCards[5].isNegated) {
            buttonEquipCard5.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        }
        else if (Stack.EquipCards[5].isPlayersEquipCard) {
            buttonEquipCard5.setIcon(new RescaledIcon(this.getClass().getResource(Stack.EquipCards[5].Card.lowMonster.cardPathAtt)));
        }
        else {
            buttonEquipCard5.setIcon(new RescaledIcon(this.getClass().getResource(Stack.EquipCards[5].Card.upMonster.cardPathAtt)));
        }
        YuGiOhJi.setButtonProperties(buttonEquipCard5, 433, 10, 67, 100, Stack.numberOfCards>=5);
        buttonEquipCard5.addActionListener(this);
        buttonEquipCard5.addMouseListener(this);
        panel6.add(buttonEquipCard5);
        
        buttonEquipCard6 = new JButton();
        if (Stack.EquipCards[6].isNegated) {
            buttonEquipCard6.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        }
        else if (Stack.EquipCards[6].isPlayersEquipCard) {
            buttonEquipCard6.setIcon(new RescaledIcon(this.getClass().getResource(Stack.EquipCards[6].Card.lowMonster.cardPathAtt)));
        }
        else {
            buttonEquipCard6.setIcon(new RescaledIcon(this.getClass().getResource(Stack.EquipCards[6].Card.upMonster.cardPathAtt)));
        }
        YuGiOhJi.setButtonProperties(buttonEquipCard6, 533, 10, 67, 100, Stack.numberOfCards>=6);
        buttonEquipCard6.addActionListener(this);
        buttonEquipCard6.addMouseListener(this);
        panel6.add(buttonEquipCard6);
        
        buttonEquipCard7 = new JButton();
        if (Stack.EquipCards[7].isNegated) {
            buttonEquipCard7.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        }
        else if (Stack.EquipCards[7].isPlayersEquipCard) {
            buttonEquipCard7.setIcon(new RescaledIcon(this.getClass().getResource(Stack.EquipCards[7].Card.lowMonster.cardPathAtt)));
        }
        else {
            buttonEquipCard7.setIcon(new RescaledIcon(this.getClass().getResource(Stack.EquipCards[7].Card.upMonster.cardPathAtt)));
        }
        YuGiOhJi.setButtonProperties(buttonEquipCard7, 633, 10, 67, 100, Stack.numberOfCards>=7);
        buttonEquipCard7.addActionListener(this);
        buttonEquipCard7.addMouseListener(this);
        panel6.add(buttonEquipCard7);
        
        buttonEquipCard8 = new JButton();
        if (Stack.EquipCards[8].isNegated) {
            buttonEquipCard8.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        }
        else if (Stack.EquipCards[8].isPlayersEquipCard) {
            buttonEquipCard8.setIcon(new RescaledIcon(this.getClass().getResource(Stack.EquipCards[8].Card.lowMonster.cardPathAtt)));
        }
        else {
            buttonEquipCard8.setIcon(new RescaledIcon(this.getClass().getResource(Stack.EquipCards[8].Card.upMonster.cardPathAtt)));
        }
        YuGiOhJi.setButtonProperties(buttonEquipCard8, 733, 10, 67, 100, Stack.numberOfCards>=8);
        buttonEquipCard8.addActionListener(this);
        buttonEquipCard8.addMouseListener(this);
        panel6.add(buttonEquipCard8);
        
        buttonEquipCard9 = new JButton();
        if (Stack.EquipCards[9].isNegated) {
            buttonEquipCard9.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        }
        else if (Stack.EquipCards[9].isPlayersEquipCard) {
            buttonEquipCard9.setIcon(new RescaledIcon(this.getClass().getResource(Stack.EquipCards[9].Card.lowMonster.cardPathAtt)));
        }
        else {
            buttonEquipCard9.setIcon(new RescaledIcon(this.getClass().getResource(Stack.EquipCards[9].Card.upMonster.cardPathAtt)));
        }
        YuGiOhJi.setButtonProperties(buttonEquipCard9, 833, 10, 67, 100, Stack.numberOfCards>=9);
        buttonEquipCard9.addActionListener(this);
        buttonEquipCard9.addMouseListener(this);
        panel6.add(buttonEquipCard9);
        
        buttonEquipCard10 = new JButton();
        if (Stack.EquipCards[10].isNegated) {
            buttonEquipCard10.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));
        }
        else if (Stack.EquipCards[10].isPlayersEquipCard) {
            buttonEquipCard10.setIcon(new RescaledIcon(this.getClass().getResource(Stack.EquipCards[10].Card.lowMonster.cardPathAtt)));
        }
        else {
            buttonEquipCard10.setIcon(new RescaledIcon(this.getClass().getResource(Stack.EquipCards[10].Card.upMonster.cardPathAtt)));
        }
        YuGiOhJi.setButtonProperties(buttonEquipCard10, 933, 10, 67, 100, Stack.numberOfCards>=10);
        buttonEquipCard10.addActionListener(this);
        buttonEquipCard10.addMouseListener(this);
        panel6.add(buttonEquipCard10);
        
    }
    
    // --- methods ---
    
    // opens the window in which one can choose a card from an equip stack
    public static void openEquipStackWindow (EStack chosenStack, String windowTitle){
        Stack=chosenStack;
        window6 = new YChooseEquipCardWindow(chosenStack, windowTitle);
        window6.panel6.setVisible(true);
        double scaleFactorX = (double) YuGiOhJi.currentFrameWidth/YuGiOhJi.initialFrameWidth;
        double scaleFactorY = (double) YuGiOhJi.currentFrameHeight/YuGiOhJi.initialFrameHeight;
        int windowWidth = (int) Math.round(initialStackWindowWidth*scaleFactorX*0.9);
        int windowHeight = (int) Math.round(initialStackWindowHeight*scaleFactorY*0.95);
        cardChooserDialog.setSize(windowWidth, windowHeight);
    }
    
    @Override
    public void componentHidden(ComponentEvent ce) {};
    @Override
    public void componentShown(ComponentEvent ce) {};
    @Override
    public void componentMoved(ComponentEvent ce) {};
    @Override
    public void componentResized(ComponentEvent ce) {
        currentStackWindowWidth = cardChooserDialog.getWidth();
        currentStackWindowHeight = cardChooserDialog.getHeight();
        rescaleEverything();
    };
    
    // calculates rescaling of horizontal coordinates (and widths)
    public static int rescaleX (int x) {
        int newValue = (int) Math.round(x*currentStackWindowWidth/initialStackWindowWidth);
        return newValue;
    }
    
    // calculates rescaling of vertical coordinates (and heights)
    public static int rescaleY (int y) {
        int newValue = (int) Math.round(y*currentStackWindowHeight/initialStackWindowHeight);
        return newValue;
    }
    
    // rescales a given button (uses current window size saved by global variables)
    public static void rescaleButton (JButton buttonName, int intialPosX, int intialPosY, int intialButtonWidth, int intialButtonHeight) {
        buttonName.setBounds(rescaleX(intialPosX), rescaleY(intialPosY), rescaleX(intialButtonWidth), rescaleY(intialButtonHeight));
    }
    
    // simply rescales all graphical components, i.e. all buttons, one by one
    public static void rescaleEverything() {
        rescaleButton(buttonEquipCard1, 33, 10, 67, 100);
        rescaleButton(buttonEquipCard2, 133, 10, 67, 100);
        rescaleButton(buttonEquipCard3, 233, 10, 67, 100);
        rescaleButton(buttonEquipCard4, 333, 10, 67, 100);
        rescaleButton(buttonEquipCard5, 433, 10, 67, 100);
        rescaleButton(buttonEquipCard6, 533, 10, 67, 100);
        rescaleButton(buttonEquipCard7, 633, 10, 67, 100);
        rescaleButton(buttonEquipCard8, 733, 10, 67, 100);
        rescaleButton(buttonEquipCard9, 833, 10, 67, 100);
        rescaleButton(buttonEquipCard10, 933, 10, 67, 100);
        // rescale fontsize
        
    }
    
    // for not repeating oneself, out-source what happens (if something happens), when choosing a card in an equip stack
    public static void chooseEquipCard (int equipCardNo){
        if (Game.isSwitchingOnInfoDialogs) {
            int intDialogResult = YuGiOhJi.multipleChoiceDialog("What do you want to do?", "", new String[]{"play card", "look at card info"}, "");
            if (intDialogResult==0) {
                playEquipCard(equipCardNo);
            }
            else if (intDialogResult==1) {
                cardChooserDialog.dispose();
                YCardInfoWindow.openInfoWindowForEquipCard(Stack.getNthEquipCardOfStack(equipCardNo));
            }
        }
        else {
                playEquipCard(equipCardNo);
        }
    }
    
    // for not repeating oneself, out-source what happens (if something happens), when choosing one wants to play an equip stack
    public static void playEquipCard (int equipCardNo){
        if (Game.isPlaying()) {
            if (Game.isActiveSomeEffect()) {
                if (Game.isReadyToChooseEffTarget) {
                    cardChooserDialog.dispose();
                    YMonster.executeOptionalEffectToEquipCard(Stack, equipCardNo);
                }
            }
            else{
                if (Game.isPlayersTurn && Game.isMainPhase()) { // during main phase 1 or main phase 2
                    cardChooserDialog.dispose();
                    EStack.attemptPlayEquipCard(Stack, equipCardNo);
                }
            }
        }
    }
    
    // from here on:
    // --- reactions of the buttons ---
    
    @Override
    public void actionPerformed (ActionEvent ae){
        if (ae.getSource() == YChooseEquipCardWindow.buttonEquipCard1) {chooseEquipCard(1);}
        if (ae.getSource() == YChooseEquipCardWindow.buttonEquipCard2) {chooseEquipCard(2);}
        if (ae.getSource() == YChooseEquipCardWindow.buttonEquipCard3) {chooseEquipCard(3);}
        if (ae.getSource() == YChooseEquipCardWindow.buttonEquipCard4) {chooseEquipCard(4);}
        if (ae.getSource() == YChooseEquipCardWindow.buttonEquipCard5) {chooseEquipCard(5);}
        if (ae.getSource() == YChooseEquipCardWindow.buttonEquipCard6) {chooseEquipCard(6);}
        if (ae.getSource() == YChooseEquipCardWindow.buttonEquipCard7) {chooseEquipCard(7);}
        if (ae.getSource() == YChooseEquipCardWindow.buttonEquipCard8) {chooseEquipCard(8);}
        if (ae.getSource() == YChooseEquipCardWindow.buttonEquipCard9) {chooseEquipCard(9);}
        if (ae.getSource() == YChooseEquipCardWindow.buttonEquipCard10) {chooseEquipCard(10);}
        
    }
    
    // in order not to repeat oneself, out-source here what happens when one hovers with the mouse over an equip card
    public static void hoverOnNthEquipButton (int cardNumber){
        YuGiOhJi.preview.setIcon(new RescaledIcon(YuGiOhJi.class.getResource(Stack.getNthCardOfStack(cardNumber).bigCardPath)));
        if (Stack.getNegationStatusOfNthEquipCard(cardNumber)) {YuGiOhJi.displayNegationStatement.setVisible(true);}
    }
    
    // what happens when one enters a certain zone with the mouse (show big preview screen depending on card)
    @Override
    public void mouseEntered(MouseEvent me) {
        // display larger version of equip cards
        if (me.getSource() == YChooseEquipCardWindow.buttonEquipCard1) {hoverOnNthEquipButton(1);}
        if (me.getSource() == YChooseEquipCardWindow.buttonEquipCard2) {hoverOnNthEquipButton(2);}
        if (me.getSource() == YChooseEquipCardWindow.buttonEquipCard3) {hoverOnNthEquipButton(3);}
        if (me.getSource() == YChooseEquipCardWindow.buttonEquipCard4) {hoverOnNthEquipButton(4);}
        if (me.getSource() == YChooseEquipCardWindow.buttonEquipCard5) {hoverOnNthEquipButton(5);}
        if (me.getSource() == YChooseEquipCardWindow.buttonEquipCard6) {hoverOnNthEquipButton(6);}
        if (me.getSource() == YChooseEquipCardWindow.buttonEquipCard7) {hoverOnNthEquipButton(7);}
        if (me.getSource() == YChooseEquipCardWindow.buttonEquipCard8) {hoverOnNthEquipButton(8);}
        if (me.getSource() == YChooseEquipCardWindow.buttonEquipCard9) {hoverOnNthEquipButton(9);}
        if (me.getSource() == YChooseEquipCardWindow.buttonEquipCard10) {hoverOnNthEquipButton(10);}
    }
    
    // what happens when one leaves any zone (just show the big version of the back side of each card)
    @Override
    public void mouseExited(MouseEvent me) {
        YuGiOhJi.preview.setIcon(new RescaledIcon(this.getClass().getResource("/images/YuGiOhJiFacedown.png")));   
        YuGiOhJi.displayNegationStatement.setVisible(false);
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
