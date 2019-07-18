package yugiohji;

/**
 * This class contains information about the state of the whole YuGiOhJi card game.
 * For convenience, all parameters about what rules are currently active
 * and what turn, phase etc. it currently is, is put together in just one object called Game.
 * This allows to derive certain properties of the game.
 * One does not have to import so many single variables any more.
 * Instead one can simply ask the game, about its properties.
 * 
 * Furthermore, this class contains methods for basic game logic
 * like going to next phase, give up, start new game etc.
 * 
 */

import static yugiohji.YuGiOhJi.errorDialog;
import static yugiohji.YuGiOhJi.GYDeckCPU;
import static yugiohji.YuGiOhJi.GYDeckPlayer;
import static yugiohji.YuGiOhJi.HandPlayer;
import static yugiohji.YuGiOhJi.informationDialog;
import static yugiohji.YuGiOhJi.multipleChoiceDialog;

public class GameState {
    
    // some variables for basic game logic
    public boolean isSwitchingOnBodyAsAShieldRule=true;
    public boolean isSwitchingOnCheatModeChangingRule=false;
    public boolean isSwitchingOnOncePerTurnRule=false;
    public int intNumberOfCardsDrawnAtBeginning;
    public int initialLP;
    
    public boolean isSwitchingOnShowDrawDialog=true;
    public boolean isSwitchingOnRescalingFontsize=false;
    public boolean isSwitchingOnInfoDialogs=false;
    public boolean isSwitchingOnStrategyDialogs=false;
    
    public boolean isUsingCustomDeckForCPU;
    
    public int lifePointsPlayer;
    public int lifePointsCPU;
    
    // some numbers for the statistics/achievements
    public int LPLastTurnCPU;
    public int statisticsNumPreventiveEffectNegates;
    public int statisticsNumLastMomentEffectNegates;
    public int statisticsNumAttackNegates;
    public int statisticsNumAttDeclarations;
    public boolean trophySaveUnlocked;
    public boolean trophyNoSoFastUnlocked;
    public boolean trophyBouncerUnlocked;
    public boolean trophyFinePrintUnlocked;
    public boolean trophyHairUnlocked;
    public boolean trophyFailUnlocked;
    public boolean trophyShyUnlocked;
    public boolean trophyCloseUnlocked;
    public boolean trophyVictoryUnlocked;
    public boolean trophyOTKUnlocked;
    public boolean trophyFTKUnlocked;
    public boolean trophyRFTKUnlocked;
    public boolean trophyOneUnlocked;
    public boolean trophyParanoiaUnlocked;
    public boolean trophyDefeatUnlocked;
    public boolean trophyDuringOpponentsTurnUnlocked;
    public boolean trophyAdversaryUnlocked;
    public boolean trophyOverUnlocked;
    public boolean trophyPatienceUnlocked;
    
    public boolean hasStillNormalSummonCPU;
    public boolean hasStillNormalSummonPlayer;
    public boolean playerCurrentlyNegates;
    public boolean isPlayersTurn;
    public int phaseAsInt;
    public boolean isInEndPhase;
    public int numberOfTurns;
    
    // Add here the active effect properties as part of the whole game state! (important for running optional effects)
    public int actEffId;
    public SummonedMonster ActEffMonSource;
    public SummonedMonster ActEffMonTarget;
    public EStack ActEffStack;
    public int actEffCardNo;
    public int actEffCardId;
    public int actEffOtherNumber;
    public boolean isActAboutUpperMon;
    public boolean isAboutPlayerActEff;
    public boolean isNegatingPreventively;
    public int tributeNo1;
    public int tributeNo2;
    public int tributeNo3;
    public boolean isReadyToChooseEffTarget;
    
    public boolean isActCPUEff;
    public boolean isActDiscarding;
    
    // currently attacking and guarding monsters as well
    public SummonedMonster ActiveAttackingMonster;
    public SummonedMonster ActiveGuardingMonster;
    
    // about the CPU behavior
    public int cpuBehaviorAsInt;
    public boolean isSwitchingOnAdditionalRecklessnessPerSe;
    public boolean isSwitchingOnAdditionalRecklessness;
    public boolean isChangingCPUBehaviorOnRestart;
    public AIparameters CPUbehavior;
    public AIbattle BattleSituation;
    
    // only has to be instantiated once, therefore only this constructor needed 
    public GameState() {
        // specific single rules and starting parameters/settings from here on
        this.isSwitchingOnBodyAsAShieldRule = true;
        this.isSwitchingOnCheatModeChangingRule = false;
        this.isSwitchingOnOncePerTurnRule = false;
        this.intNumberOfCardsDrawnAtBeginning=4;
        this.initialLP = 8000; // life points both players start with
        
        this.isSwitchingOnShowDrawDialog = true;
        this.isSwitchingOnRescalingFontsize = false;
        this.isSwitchingOnInfoDialogs = false;
        this.isSwitchingOnStrategyDialogs = false;
        
        this.isUsingCustomDeckForCPU = false;
        
        this.lifePointsPlayer = 0;
        this.lifePointsCPU = 0;
        
        // only used for trophy statistics
        this.LPLastTurnCPU = 8000;
        this.statisticsNumPreventiveEffectNegates = 0;
        this.statisticsNumLastMomentEffectNegates = 0;
        this.statisticsNumAttackNegates = 0;
        this.statisticsNumAttDeclarations = 0;
        this.trophySaveUnlocked = false;
        this.trophyNoSoFastUnlocked = false;
        this.trophyBouncerUnlocked = false;
        this.trophyFinePrintUnlocked = false;
        this.trophyHairUnlocked = false;
        this.trophyFailUnlocked = false;
        this.trophyShyUnlocked = false;
        this.trophyCloseUnlocked = false;
        this.trophyVictoryUnlocked = false;
        this.trophyOTKUnlocked = false;
        this.trophyFTKUnlocked = false;
        this.trophyRFTKUnlocked = false;
        this.trophyOneUnlocked = false;
        this.trophyParanoiaUnlocked = false;
        this.trophyDefeatUnlocked = false;
        this.trophyDuringOpponentsTurnUnlocked = false;
        this.trophyAdversaryUnlocked = false;
        this.trophyOverUnlocked = false;
        this.trophyPatienceUnlocked = false;
         // There are 20 achievements in this game. How many can you find? Spoiler Warning!: They are called: "Better Save than Sorry", "Not so fast!", "Pub Bouncer", "Master of Fine Print", "Hair's Breadth", "Epic Fail", "Your Noble Shyness", "That was close", "Flawless Victory", "One Turn Kill", "First Turn Kill", "Real First Turn Kill", "One Shot", "Master of Paranoia", "Admit Defeat Already!", "You didn't see that coming", "Worthy Adversary", "39 and over", and "Patience is a Virtue". (plus a hidden one)
        
        this.hasStillNormalSummonCPU = true;
        this.hasStillNormalSummonPlayer = true; // each player each turn has only one normal summoning (as main resource)
        this.playerCurrentlyNegates = false; // true, if the player just has to choose cards as cost for negating something (CPU has to be locked from playing while this holds true)
        this.isPlayersTurn = false; // true if it is the players turn
        this.phaseAsInt = 1; // 1=main phase 1, 2=battle phase, 0=main phase 2 (each time increase by 1 and modulo 3)
        this.isInEndPhase = false; // needed for returning card effects and discard cards of full hands
        this.numberOfTurns = 0; // number of turns already played: used for forbidding attacking in 1st turn
        
        this.actEffId = 0;
        this.ActEffMonSource = new SummonedMonster(false, 0); // the monster using an effect: mainly used for the CPU to know what it can negate
        this.ActEffMonTarget = new SummonedMonster(false, 0); // the target for an effect (also potentially important for the CPU to know, if the target is important enough so it is worth negating the effect)
        this.ActEffStack = new EStack(false, 0);
        this.actEffCardNo = 0;
        this.actEffCardId = 0;
        this.actEffOtherNumber = 0; // all kinds of other number, which have to be remembered for the CPU to know, like burn damage about to happen
        this.isActAboutUpperMon = false;
        this.isAboutPlayerActEff = false;
        this.isNegatingPreventively = false;
        this.tributeNo1 = 0;
        this.tributeNo2 = 0;
        this.tributeNo3 = 0;
        this.isReadyToChooseEffTarget = true; // false from activation until costs are paid, in order to avoid cheating by choosing targets before all costa are paid
        
        this.isActCPUEff = false; // to remember, if a CPU effect is active (important for negates by the player) also to remember, if the CPU still has current marked sacrifices, that need to be forgotten later
        this.isActDiscarding = false; // true if one has to discard a card during effect (hand trap or too many cards on hand) [don't reset, when deactivating effects, because discarding as a negate is a different effect]
        
        this.ActiveAttackingMonster = new SummonedMonster(false, 0); // needed for attack declaration
        this.ActiveGuardingMonster = new SummonedMonster(false, 0);
        
        this.cpuBehaviorAsInt=2; // 1 for defensive, 2 for balanced, 3 for aggressive
        this.isSwitchingOnAdditionalRecklessnessPerSe=true; // if switched on, the computer is willing to pay any card (on hand or in GY) as cost, no matter what (This is the variant, that the player can switch on)
        this.isSwitchingOnAdditionalRecklessness=false; // basically same as above (However, this is the variant, that is only sometimes switched on for AIinterrupts interactions.)
        this.isChangingCPUBehaviorOnRestart = false; // only true, when changed CPU settings until restart
        this.CPUbehavior = new AIparameters(cpuBehaviorAsInt);
        this.BattleSituation = new AIbattle();
        
    }
    
    // resets many parameters of the game, when starting a new game
    public void resetParametersWhenStartingNewGame() {
        lifePointsPlayer = initialLP;
        lifePointsCPU = initialLP;
        hasStillNormalSummonCPU = true;
        hasStillNormalSummonPlayer = true;
        playerCurrentlyNegates = false;
        isPlayersTurn = false;
        phaseAsInt = 1; // the game begins with main phase 1
        isInEndPhase = false;
        numberOfTurns = 0;
        ActiveAttackingMonster = new SummonedMonster(false, 0);
        ActiveGuardingMonster = new SummonedMonster(false, 0);
        deactivateCurrentEffects();
        deactivateCurrentCPUEffects();
        resetStatistics();
        if (isChangingCPUBehaviorOnRestart) {
            CPUbehavior = new AIparameters(cpuBehaviorAsInt);
        }
        isChangingCPUBehaviorOnRestart = false;
        CPUbehavior.forgetStrategies();
        reconsiderRecklessness();
        this.BattleSituation = new AIbattle();
    }
    
    // updates the level of recklessness of the CPU (needed when changing setup and after AIinterruptions, (in later case better used at beginning of all methods potentially using sacrifices))
    public void reconsiderRecklessness(){
        isSwitchingOnAdditionalRecklessness=isSwitchingOnAdditionalRecklessnessPerSe;
    }
    
    // resets all counters for statistical needs
    public void resetStatistics(){
        LPLastTurnCPU = initialLP;
        statisticsNumPreventiveEffectNegates=0;
        statisticsNumAttackNegates=0;
        statisticsNumAttDeclarations=0;
    }
    
    // called after an optional effect has been used
    // so that no when clicking on a monster the effect is not happening any more
    // (until reactivating it or another effect)
    public void deactivateCurrentEffects() {
        actEffId = 0;
        ActEffMonSource = new SummonedMonster(false, 0);
        ActEffMonTarget = new SummonedMonster(false, 0);
        ActEffStack = new EStack(false, 0);
        actEffCardNo = 0;
        actEffCardId = 0;
        actEffOtherNumber = 0;
        isActAboutUpperMon = false;
        isAboutPlayerActEff = false;
        isNegatingPreventively = false;
        tributeNo1 = 0;
        tributeNo2 = 0;
        tributeNo3 = 0;
        isReadyToChooseEffTarget = true;
    }
    
    // called after an effect has been executed or has been negated
    public void deactivateCurrentCPUEffects() {
        AIsacrifice.forgetAllSacrifices();
        isActCPUEff = false;
    }
    
    // -- shortcuts for decoding active effects --
    
    // returns true, if the effect of God/Obstacle (#1) is currently active
    public boolean isActiveSomeEffect() {
        return (actEffId!=0);
    }
    
    // returns true, if the effect of God/Obstacle (#1) is currently active
    public boolean isActEffRevealCard() {
        return (actEffId==1);
    }
    
    // returns true, if the effect of Burner (#2) is currently active
    public boolean isActEffBurner() {
        return (actEffId==2);
    }
    
    // returns true, if the effect of Big Burner (#3) is currently active
    public boolean isActEffBigBurner() {
        return (actEffId==3);
    }
    
    // returns true, if the search effect of Card Grabber (#4) is currently active
    public boolean isActEffSearch() {
        return (actEffId==4);
    }
    
    // returns true, if the milling effect of Card Grabber (#5) is currently active
    public boolean isActEffMilling() {
        return (actEffId==5);
    }
    
    // returns true, if the effect of SkillStealer (#6) is currently active
    public boolean isActEffSkillStealer() {
        return (actEffId==6);
    }
    
    // returns true, if the effect of Copy Cat (#7) is currently active
    public boolean isActEffCopyCat() {
        return (actEffId==7);
    }
    
    // returns true, if the effect of Mode Changer (#8) is currently active
    public boolean isActEffModeChanger() {
        return (actEffId==8);
    }
    
    // returns true, if the MOOK revival effect of Necromancer (#9) is currently active
    public boolean isActEffReviveMook() {
        return (actEffId==9);
    }
    
    // returns true, if the MIDBOSS revival effect of Necromancer (#10) is currently active
    public boolean isActEffReviveMidboss() {
        return (actEffId==10);
    }
    
    // returns true, if the effect of Big Back Bouncer (#11) is currently active
    public boolean isActEffBigBackBouncer() {
        return (actEffId==11);
    }
    
    // returns true, if the effect of Big Banisher (#12) is currently active
    public boolean isActEffBigBanisher() {
        return (actEffId==12);
    }
    
    // returns true, if the effect of Monster Stealer (#13) is currently active
    public boolean isActEffMonsterStealer() {
        return (actEffId==13);
    }
    
    // returns true, if the effect of Eradicator (#14) is currently active
    public boolean isActEffEradicator() {
        return (actEffId==14);
    }
    
    // returns true, if the optional on field effect of Neutraliser (#15) is currently active
    public boolean isActEffOptNeutraliser() {
        return (actEffId==15);
    }
    
    // returns true, if the equip effect of a summoned monster (#16) is currently active
    public boolean isActEffEquipFromMonster() {
        return (actEffId==16);
    }
    
    // returns true, if the equip effect of an equipping monster (#17) is currently active
    public boolean isActEffEquipFromStack() {
        return (actEffId==17);
    }
    
    // returns true, if the equip effect of an equip card (#18) is currently active
    public boolean isActEffEquipFromHand() {
        return (actEffId==18);
    }
    
    // returns true, if the special summoning of an ENDBOSS from hand (#19) is currently active
    public boolean isActSumEndbossHand() {
        return (actEffId==19);
    }
    
    // returns true, if the special summoning of an ENDBOSS from graveyard (#20) is currently active
    public boolean isActSumEndbossGY() {
        return (actEffId==20);
    }
    
    // returns true, if the tribute summoning of a MIDBOSS (#21) is currently active
    public boolean isActTribSumMidboss() {
        return (actEffId==21);
    }
    
    // returns true, if the tribute setting of a MIDBOSS (#22) is currently active
    public boolean isActTribSetMidboss() {
        return (actEffId==22);
    }
    
    // returns true, if the tribute summoning of an ENDBOSS (#23) is currently active
    public boolean isActTribSumEndboss() {
        return (actEffId==23);
    }
    
    // returns true, if the tribute setting of an ENDBOSS (#24) is currently active
    public boolean isActTribSetEndboss() {
        return (actEffId==24);
    }
    
    // returns true, if the tribute summoning of a GOD (#24) is currently active
    public boolean isActTribSumGod() {
        return (actEffId==25);
    }
    
    // returns true, if the tribute setting of a GOD (#26) is currently active
    public boolean isActTribSetGod() {
        return (actEffId==26);
    }
    
    // returns true, if the on hand effect of Neutraliser (#27) is currently active
    public boolean isActEffHandTrapNeutraliser() {
        return (actEffId==27);
    }
    
    // returns true, if the effect of Back Bouncer (#28) is currently active
    public boolean isActEffBackBouncer() {
        return (actEffId==28);
    }
    
    // returns true, if the on field effect of Neutraliser duriing the opponent's Turn (#29) is currently active
    public boolean isActEffNeutraliserOpponentsTurn() {
        return (actEffId==29);
    }
    
    // returns true, if the on field effect of Neutraliser duriing the opponent's Turn on an equip card (#30) is currently active
    public boolean isActEffNeutraliserOpponentsTurnEquip() {
        return (actEffId==30);
    }
    
    // returns true, if the effect of Attack Stopper (#31) is currently active
    public boolean isActEffAttackStopper() {
        return (actEffId==31);
    }
    
    // returns true, if the effect of Big Attack Stopper (#32) is currently active
    public boolean isActEffBigAttackStopper() {
        return (actEffId==32);
    }
    
    // some effects allow paying 1/2 or 1 whole card
    public boolean isAllowingDifferentCostValuesForEffect() {
        return (isActEffBurner() || isActEffModeChanger() || isActEffBigBanisher() || isActEffBigAttackStopper());
    }
    
    // some effects force one to reveal a card as cost: they are the effects of God/Obstacle (#1), and SkillStealer (#6)
    public boolean hasToRevealAsCostForEffect() {
        return (isActEffRevealCard() || isActEffSkillStealer());
    }
    
    // -- other derived properties and related methods --
    
    // returns true, if the CPU is currenty set to defensive behavior
    public boolean isDefensiveCPU() {
        return (cpuBehaviorAsInt==1);
    }
    
    // returns true, if the CPU is currenty set to balanced behavior
    public boolean isBalancedCPU() {
        return (cpuBehaviorAsInt==2);
    }
    
    // returns true, if the CPU is currenty set to aggressive behavior
    public boolean isAggressiveCPU() {
        return (cpuBehaviorAsInt==3);
    }
    
    
    // returns true, if the player is currently in a game
    public boolean isPlaying() {
        return (lifePointsPlayer!=0 && lifePointsCPU!=0);
    }
    
    // player gives up means life turns to zero (so that it is implied, that the player is not playing any more)
    public void giveUp() {
        lifePointsPlayer=0;
    }
    
    // this happens when the give up / end match button is pressed
    public void giveUpButton() {
        if (isPlaying()) { // one can only give up, if one is playing in the first place
            int intDialogResult = multipleChoiceDialog("Are you sure you want to give up?", "", new String[]{"yes", "no"}, "no");
            if (intDialogResult==0) { // only procced, if player clicked "yes"
                giveUp(); // player is not playing anymore
                informationDialog("Press the \"start/reset\" button to start a new game.", "You gave up.");
            }
        }
    }
    
    // returns true, if the CPU is locked from playing (thus not allowed to do anything)
    public boolean isLockingCPUFromPlaying() {
        return (playerCurrentlyNegates || !isPlaying());
    }
    
    // returns true, if the CPU is not locked from playing and it's the also its turn 
    public boolean isAllowingCPUToPlay() {
        return (!isPlayersTurn && !isLockingCPUFromPlaying());
    }
    
    // -- about phases and turns --
    
    // returns true, if the current phase is main phase 1
    public boolean isMainPhase1() {
        return (phaseAsInt==1);
    }
    
    // returns true, if the current phase is the battle phase
    public boolean isBattlePhase() {
        return (phaseAsInt==2);
    }
    
    // returns true, if the current phase is main phase 2
    public boolean isMainPhase2() {
        return (phaseAsInt==0);
    }
    
    // returns true, if the current phase is a main phase (thus most effects can be used)
    public boolean isMainPhase() {
        return (isMainPhase1() || isMainPhase2());
    }
    
    // this happens, when one clicks on next phase button
    public void nextPhase () {
        if (isPlayersTurn && isPlaying()) {// the player can only go to the next phase, if it's the plyer's turn
            proceedToNextPhase();
        }
    }
    
    // this happens, when one clicks on next turn button
    public void nextTurn () {
        if (isPlayersTurn && isPlaying()) {
            if (isMainPhase1()) {
                proceedToNextPhase(); proceedToNextPhase(); proceedToNextPhase();
            }
            else if (isBattlePhase()) {
                proceedToNextPhase(); proceedToNextPhase();
            }
            else if (isMainPhase2()) {
                proceedToNextPhase();
            }
        }
    }
    
    // returns true, if it is the very first turn of the game (important for allowing attack declaration and achievements)
    public boolean isVeryFirstTurn(){
        return (numberOfTurns==0);
    }
    
    // only needed for FTK trophy
    public boolean isFirstOwnTurnOrEarlier(){
        return (numberOfTurns<=1);
    }
    
    // exactly what it says on the tin: go to next phase within a turn
    public void proceedToNextPhase(){
        deactivateCurrentEffects(); // in case one went to next turn with an effect still being active
        if (phaseAsInt==0) {
            isInEndPhase=true;
            endPhase();
        }
        else {
            progressPhaseCounter();
        }
    }
    
    // increase phase counter modulo 3 and display new corresponding phase
    public void progressPhaseCounter() {
        phaseAsInt++; // next phase
        phaseAsInt=phaseAsInt%3; // there are only 3 phases
        YuGiOhJi.updateDisplayedPhase();
        // info for the player, if needed
        if (isBattlePhase() && !isPlayersTurn) {
            informationDialog("Computer announces battle phase.", "");
        }
        if (isMainPhase2()) {
            if (!isPlayersTurn) {
                informationDialog("Computer announces main phase 2.", "");
            }
            if (ActiveAttackingMonster.isExisting) {
                BattlePhase.endAttack(false); // just to prevent maybe some possible bugs, end any possibly still ongoing attack
            }
        }
    }
    
    // I noticed that I need an end phase for spirit effects and discarding cards at the end of the turn
    // without having to rename the phases as integers.
    public void endPhase(){
        if (isInEndPhase) {
            YCard.checkForSpritEffectsAndFullHand();
        }
        else {
            if (isPlayersTurn) { // the next phase is being called, if all spirit effects have been resolved and one has less then 10 cards on the hand
                informationDialog("It's the computer's turn.", "You end your turn.");
            }
            else {
                informationDialog("It's your turn.", "Computer ends turn.");
            }
            newTurn();
        }
    }
    
    // things happening at the beginning of a new turn
    public void newTurn(){
        progressPhaseCounter();
        numberOfTurns++;
        isPlayersTurn=!isPlayersTurn; // change turns between player & CPU
        // bookkeeping (update resources)
        SummonedMonster.resetMonsterPropertiesPerTurn();
        hasStillNormalSummonPlayer=true; // new turn grants new normal summon (the main resource of this game (next to the cards in general))
        hasStillNormalSummonCPU=true;
        
        LPLastTurnCPU=lifePointsCPU; // only used for OTK trophy
        
        Hand.drawCard(isPlayersTurn); // at the beginning of each turn, the turn player draws one card
        if (!isPlayersTurn) {
            AIdelegate.cpuPlaysTurn(); // ask CPU to play a turn
        }
    }
    
    // -- behavior of the remaining buttons --
    
    // this happens, when one clicks on the (re)start button
    public void startNewGame() {
        int intDialogResult = multipleChoiceDialog("Start/restart a new game with the current settings?", "", new String[]{"yes", "no"}, "");
        if (intDialogResult==0) { // only procced, if player clicked "yes"
            YuGiOhJi.resetEverything();

            int intCoinFlipResult=Deck.chooseRandomOption(2);
            informationDialog("Coin flip", "Shuffling decks");
            if (intCoinFlipResult==1) {
                intDialogResult = multipleChoiceDialog("Do you want to take the first turn or do you let the computer begin?", "You have won the coin flip.", new String[]{"I begin.", "Computer shall begin."}, "Computer shall begin.");
                isPlayersTurn = (intDialogResult==0);
            }
            else { // defensive CPUs want to begin in order to set a monster face down, all other ones want to attack first, thus letting the player have the first turn
                if (isDefensiveCPU()) {
                    isPlayersTurn=false;
                    informationDialog("Computer wants to begin.", "You lost the coin flip.");
                }
                else {
                    isPlayersTurn=true;
                    informationDialog("Computer lets you begin.", "You lost the coin flip.");
                }
            }
            for (int index = intNumberOfCardsDrawnAtBeginning; index >= 1 ; index--){ // both players start with 4+1 cards (default) in order to avoid Big Burner First Turn Kill
                Hand.drawCard(isPlayersTurn);
                Hand.drawCard(!isPlayersTurn);
            }
            Hand.drawCard(isPlayersTurn); // draw for turn (whoever it is)
            if (!isPlayersTurn) {
                AIdelegate.cpuPlaysTurn(); // ask CPU to play a turn
            } // if it's the player's turn, nothing happens, because the player is supposed to click on something 
        }
    }
    
    // this happens, when one presses the deck building button
    public void openDeckBuildingWindow() {
        if (!isPlaying()) {
            int intDialogResult = multipleChoiceDialog("Here you can load standard decks as well as create, save and load custom decks. Continue?", "Load/Create deck?", new String[]{"yes", "no"}, "");
            if (intDialogResult==0) {
                DeckBuilding.createDeckBuildingWindow();
            }
        }
        else {errorDialog("You have to finish or give up the current game before changing settings.", "Error.");}
    }
    
    // this happens, when one presses the CPU setup button
    public void openCPUSetupDialog() {
        if (!isPlaying()) {
            int intDialogResult = multipleChoiceDialog("The computer will use random decks befitting its strategy.", "Choose CPU behavior", new String[]{"defensive", "balanced (default)", "aggressive"}, "balanced (default)");
            switch (intDialogResult) {
                case 0: cpuBehaviorAsInt=1; informationDialog("computer set to \"defensive\"", ""); break;
                default: cpuBehaviorAsInt=2; informationDialog("computer set to \"balanced\"", ""); break;
                case 2: cpuBehaviorAsInt=3; informationDialog("computer set to \"aggressive\"", ""); break;
            }
            isChangingCPUBehaviorOnRestart = true;
            intDialogResult = multipleChoiceDialog("If switched on, the computer will more likely try to use effects \"at all costs\".", "Switch on additional recklessness?", new String[]{"switch on recklessness (default)", "switch off recklessness"}, "switch on recklessness (default)");
            switch (intDialogResult) {
                default: isSwitchingOnAdditionalRecklessnessPerSe=true; informationDialog("computer plays with additional recklessness", ""); break;
                case 1: isSwitchingOnAdditionalRecklessnessPerSe=false; informationDialog("computer plays without additional recklessness", ""); break;
            }
        }
        else {errorDialog("You have to finish or give up the current game before changing settings.", "Error.");}
    }
    
    // this happens, when one presses the mods button
    public void openModsDialog() { // can only change maximum LP while not playing (otherwise would collide somewhere)
        if (!isPlaying()) {
            int intDialogResult = multipleChoiceDialog("Which rule do you want to change?", "Modify Game?", new String[]{"\"My Body As A Shield\" rule", "\"Cheat Mode Changing\" rule", "\"Once Per Turn\" rule", "number of cards drawn at beginning", "number of life points at beginning"}, "");
            switch (intDialogResult) {
                case 0:
                    intDialogResult = multipleChoiceDialog("If switched on, a player can take an attack as a direct hit in order to protect a monster.", "Switch on \"My Body As A Shield\" rule?", new String[]{"Play with rule (default)!", "Play without rule!"}, "Play with rule (default)!");
                    switch (intDialogResult) {
                        default: isSwitchingOnBodyAsAShieldRule=true; informationDialog("switching rule on", ""); break;
                        case 1: isSwitchingOnBodyAsAShieldRule=false; informationDialog("switching rule off", ""); break;
                    }
                    updateVisibilityOfRules();
                    break;
                case 1:
                    intDialogResult = multipleChoiceDialog("If switched on, a player can cheat by switching monsters on the card, when changing between Att/Def mode.", "Switch on \"Cheat Mode Changing\" rule?", new String[]{"Play with rule!", "Play without rule (default)!"}, "Play without rule (default)!");
                    switch (intDialogResult) {
                        case 0: isSwitchingOnCheatModeChangingRule=true; informationDialog("switching rule on", ""); break;
                        default: isSwitchingOnCheatModeChangingRule=false; informationDialog("switching rule off", ""); break;
                    }
                    updateVisibilityOfRules();
                    break;
                case 2:
                    intDialogResult = multipleChoiceDialog("If switched on, a player can use the optional effects on field of any summoned monster only once per turn.", "Switch on \"Once Per Turn\" rule?", new String[]{"Play with rule!", "Play without rule (default)!"}, "Play without rule (default)!");
                    switch (intDialogResult) {
                        case 0: isSwitchingOnOncePerTurnRule=true; informationDialog("switching rule on", ""); break;
                        default: isSwitchingOnOncePerTurnRule=false; informationDialog("switching rule off", ""); break;
                    }
                    updateVisibilityOfRules();
                    break;
                case 3:
                    intDialogResult = multipleChoiceDialog("Choose the number of cards a player draws before the first turn. On the first turn one draws another card.", "Set number of cards drawn before first turn?", new String[]{"0", "1", "2", "3", "4 (default)", "5", "6", "7", "8", "9"}, "4 (default)");
                    if (intDialogResult==-1) {
                        intNumberOfCardsDrawnAtBeginning=4;
                    }
                    else {
                        intNumberOfCardsDrawnAtBeginning=intDialogResult;
                    }
                    informationDialog("playing with " + intNumberOfCardsDrawnAtBeginning + " cards at beginning", "");
                    break;
                case 4:
                    intDialogResult = multipleChoiceDialog("Choose the number of life points both players start with.", "Set number of LP?", new String[]{"4000", "8000 (default)", "16000"}, "8000 (default)");
                    switch (intDialogResult) {
                        case 0: initialLP=4000; break;
                        default: initialLP=8000; break;
                        case 2: initialLP=16000; break;
                    }
                    informationDialog("playing with " + initialLP + " life points", "");
                    break;
                default:
                    break;
            }
        }
        else {errorDialog("You have to finish or give up the current game before changing settings.", "Error.");}
    }
    
    // shows/hides if certain rules are active
    // (when a game last a while, or if one keeps changing them frequently, it is nice to know what the actual rules are)
    public void updateVisibilityOfRules() {
        YuGiOhJi.displayBodyAsShieldRule.setVisible(isSwitchingOnBodyAsAShieldRule);
        YuGiOhJi.displayCheatChangeRule.setVisible(isSwitchingOnCheatModeChangingRule);
        YuGiOhJi.displayOncePerTurnRule.setVisible(isSwitchingOnOncePerTurnRule);
    }
    
    // this happens, when one presses the game setup button
    public void openGameSetupDialog() {
        if (!isPlaying()) { // For some weird reason, when I allow switching the info dialogs on/off during a game, one can sometimes attack more often than what would be allowed. Since I haven't found the bug yet, I simply forbid changing the setting during the actual play.
            int intDialogResult = multipleChoiceDialog("What do you want to change?", "Change Settings?", new String[]{"Show draw dialog", "Rescale fontsize", "Show card info", "Show CPU strategies"}, "");
            //int intDialogResult = multipleChoiceDialog("What do you want to change?", "Change Settings?", new String[]{"Show draw dialog", "Rescale fontsize", "Show card info"}, ""); // use this line instead of previous one to hide option of showing debug dialogs of CPU strategies
            switch (intDialogResult) {
                case 0:
                    intDialogResult = multipleChoiceDialog("Do you want the game to stop each time a player draws a card at the beginning of the game?", "Show draw dialog?", new String[]{"yes (default)", "no"}, "yes (default)");
                    switch (intDialogResult) {
                        default: isSwitchingOnShowDrawDialog=true; informationDialog("draw dialogs on", ""); break;
                        case 1: isSwitchingOnShowDrawDialog=false; informationDialog("draw dialogs off", ""); break;
                    }
                    break;
                case 1:
                    intDialogResult = multipleChoiceDialog("If switched on, the fontsize in the main window will rescale proportional to the frame height.", "Rescale fontsize?", new String[]{"yes", "no (default)"}, "no (default)");
                    switch (intDialogResult) {
                        case 0: isSwitchingOnRescalingFontsize=true; informationDialog("rescaling on", ""); break;
                        default: isSwitchingOnRescalingFontsize=false; informationDialog("rescaling off", ""); break;
                    }
                    break;
                case 2:
                    intDialogResult = multipleChoiceDialog("If switched on, you can get more detailed information about the cards during the game.", "Show card info?", new String[]{"yes", "no (default)"}, "no (default)");
                    switch (intDialogResult) {
                        case 0: isSwitchingOnInfoDialogs=true; informationDialog("info dialogs on", ""); break;
                        default: isSwitchingOnInfoDialogs=false; informationDialog("info dialogs off", ""); break;
                    }
                    break;
                case 3:
                    intDialogResult = multipleChoiceDialog("If switched on, the computer will tell its attempted strategies.", "Show CPU strategies?", new String[]{"yes", "no (default)"}, "no (default)");
                    switch (intDialogResult) {
                        case 0: isSwitchingOnStrategyDialogs=true; informationDialog("strategy dialogs on", ""); break;
                        default: isSwitchingOnStrategyDialogs=false; informationDialog("strategy dialogs off", ""); break;
                    }
                    break;
                default:
                    break;
            }
        }
        else {errorDialog("You have to finish or give up the current game before changing settings.", "Error.");}
    }
    
    // -- methods for playing all kinds of cards --
    
    // in order not to repeat oneself, out-source here what happens (if something happen), when one clicks on a card of the player
    public void chooseCardOnHand (int cardNumber){
        if (isSwitchingOnInfoDialogs) {
            int intDialogResult = multipleChoiceDialog("What do you want to do?", "", new String[]{"play card", "look at card info"}, "");
            if (intDialogResult==0) {
                playCardOnHand(cardNumber);
            }
            else if (intDialogResult==1) {
                YCardInfoWindow.openInfoWindowForHandCard(HandPlayer.getNthCardOfHand(cardNumber));
            }
        }
        else {
            playCardOnHand(cardNumber);
        }
    }
    
    // in order not to repeat oneself, out-source here what happens (if something happen), when one wants to play a card of the player
    public void playCardOnHand (int cardNumber){
        if (isActDiscarding && isPlaying()) {
            YCard.attemptToDiscardACard(cardNumber, true);
        }
        else {
            if (HandPlayer.numberOfCards >= cardNumber && isPlayersTurn && isPlaying() && isReadyToChooseEffTarget) {
                CardOptions.attemptPlayCard(HandPlayer.getNthCardOfHand(cardNumber), cardNumber, false);
            }
        }
    }
    
    // in order not to repeat oneself, out-source here what happens (if something happen), when one clicks on a monster of the player
    public void chooseMonster (int monsterNumber, boolean isMonsterOfPlayer){
        SummonedMonster SumMonster = SummonedMonster.getNthSummonedMonster(monsterNumber, isMonsterOfPlayer);
        if (isPlaying() && SumMonster.isExisting) {
            if (isSwitchingOnInfoDialogs && SumMonster.isKnownToPlayer()) {
                int intDialogResult = multipleChoiceDialog("What do you want to do?", "", new String[]{"play card", "look at card info"}, "");
                if (intDialogResult==0) {
                    if (isMonsterOfPlayer) {playMonsterOfPlayer(monsterNumber);}
                    else {playMonsterOfCPU(monsterNumber);}
                }
                else if (intDialogResult==1) {
                    YCardInfoWindow.openInfoWindowForMonster(SumMonster);
                }
            }
            else {
                if (isMonsterOfPlayer) {playMonsterOfPlayer(monsterNumber);}
                else {playMonsterOfCPU(monsterNumber);}
            }
        }
    }
    
    // in order not to repeat oneself, out-source here what happens (if something happen), when one wants to play a monster of the player
    public void playMonsterOfPlayer (int monsterNumber){
        SummonedMonster SumMonster = SummonedMonster.getNthSummonedMonster(monsterNumber, true);
        if (isPlaying() && SumMonster.isExisting) {
            if (isActiveSomeEffect()) {
                if (isReadyToChooseEffTarget) {
                    YMonster.executeOptionalEffectOnField(SumMonster);
                }
            }
            else {
                if (isPlayersTurn && isMainPhase()) {
                    SummonedMonster.attemptPlayMonster(SumMonster);
                }
                else if (isPlayersTurn && isBattlePhase()) {
                    BattlePhase.attemptDeclareAttack(SumMonster);
                }
            }
        }
    }
    
    // in order not to repeat oneself, out-source here what happens (if something happen), when one wants to play a monster of the CPU
    public void playMonsterOfCPU (int monsterNumber){
        SummonedMonster SumMonster = SummonedMonster.getNthSummonedMonster(monsterNumber, false);
        if (isPlaying() && SumMonster.isExisting) {
            if (isBattlePhase() && isPlayersTurn) {
                BattlePhase.attemptFinishDeclareAttack(SumMonster);
            }
            if (isActiveSomeEffect() && isReadyToChooseEffTarget) {
                YMonster.executeOptionalEffectOnField(SumMonster);
            }
        }
    }
    
    // in order not to repeat oneself, out-source here what happens (if something happen), when one clicks on an equip stack
    public void chooseEquipStack (int stackNumber, boolean isBelongingToPlayer){
        if (isPlaying()) {
            if (EStack.getNthStack(stackNumber, isBelongingToPlayer).numberOfCards==1){
                if (isSwitchingOnInfoDialogs) {
                    int intDialogResult = multipleChoiceDialog("What do you want to do?", "", new String[]{"play card", "look at card info"}, "");
                    if (intDialogResult==0) {
                        playEquipStack(stackNumber, isBelongingToPlayer);
                    }
                    else if (intDialogResult==1) {
                        YCardInfoWindow.openInfoWindowForEquipCard(EStack.getNthStack(stackNumber, isBelongingToPlayer).getNthEquipCardOfStack(1));
                    }
                }
                else {
                    playEquipStack(stackNumber, isBelongingToPlayer);
                }
            }
            else if (EStack.getNthStack(stackNumber, isBelongingToPlayer).numberOfCards>1){
                String playerName;
                if (isBelongingToPlayer) {
                    playerName="player";
                } else {
                    playerName="CPU";
                }
                YChooseEquipCardWindow.openEquipStackWindow(EStack.getNthStack(stackNumber, isBelongingToPlayer), AIEffects.getNumberAsString(stackNumber) + " equip stack of " + playerName);
            }
        }
    }
    
    // in order not to repeat oneself, out-source here what happens (if something happen), when one wants to play an equip stack
    public void playEquipStack (int stackNumber, boolean isBelongingToPlayer){
        if (isActiveSomeEffect()) {
            if (isReadyToChooseEffTarget) {
                YMonster.executeOptionalEffectToEquipCard(EStack.getNthStack(stackNumber, isBelongingToPlayer), 1);
            }
        }
        else {
            if (isPlayersTurn && isMainPhase() ) {
                EStack.attemptPlayEquipCard(EStack.getNthStack(stackNumber, isBelongingToPlayer), 1);
            }
        }
    }
    
    // in order not to repeat oneself, out-source here what happens (if something happen), when one clicks on a graveyard
    public void chooseGY (boolean isBelongingToPlayer){
        if (isPlayersTurn && isPlaying()) {
            if (isBelongingToPlayer) {
                if (GYDeckPlayer.numberOfCards>1) {
                    SearchWindow.searchDeck(GYDeckPlayer, false, false, false, false, false);
                }
                else if (GYDeckPlayer.numberOfCards==1) {
                    chooseOnlyGYCard(true);
                }
            }
            else {
                if (GYDeckCPU.numberOfCards>1) {
                    SearchWindow.searchDeck(GYDeckCPU, false, false, false, false, false);
                }
                else if (GYDeckCPU.numberOfCards==1) {
                    chooseOnlyGYCard(false);
                }
            }
        }     
    }
    
    // in order not to repeat oneself, out-source here what happens (if something happen), when one chooses the only card in a graveyard
    public void chooseOnlyGYCard (boolean isBelongingToPlayer){
        if (isSwitchingOnInfoDialogs) {
            int intDialogResult = multipleChoiceDialog("What do you want to do?", "", new String[]{"play card", "look at card info"}, "");
            if (intDialogResult==0) {
                playOnlyGYCard(isBelongingToPlayer);
            }
            else if (intDialogResult==1) {
                if (isBelongingToPlayer) {
                    YCardInfoWindow.openInfoWindowForGYCard(GYDeckPlayer.getNthCardOfDeck(1));
                }
                else {
                    YCardInfoWindow.openInfoWindowForGYCard(GYDeckCPU.getNthCardOfDeck(1));
                }
            }
        }
        else {
            playOnlyGYCard(isBelongingToPlayer);
        }
    }
    
    // in order not to repeat oneself, out-source here what happens (if something happen), when one wants to play the only card in a graveyard
    public void playOnlyGYCard (boolean isBelongingToPlayer){
        if (isBelongingToPlayer) {
            if (!isActiveSomeEffect()) {
                CardOptions.attemptPlayCard(GYDeckPlayer.getNthCardOfDeck(1), 1, true);
            }
            else {
                if (isReadyToChooseEffTarget) {
                    YMonster.executeOptionalEffectToGYCard(true, 1);
                }
            }
        }
        else {
            if (isActiveSomeEffect()) {
                if (isReadyToChooseEffTarget) {
                    YMonster.executeOptionalEffectToGYCard(false, 1);
                }
            }
        }
    }
    
    // in order not to repeat oneself, out-source here what happens (if something happen), when one clicks on a banishing zone
    public void lookAtDifferentDimension (boolean isBelongingToPlayer){
        if (isPlayersTurn && isPlaying()) {
            if (Deck.getDD(isBelongingToPlayer).numberOfCards==1) {
                chooseOnlyBanishedCard(isBelongingToPlayer);
            }
            else if (Deck.getDD(isBelongingToPlayer).numberOfCards>1) {
                SearchWindow.searchDeck(Deck.getDD(isBelongingToPlayer), false, false, false, false, false);
            }
        }
    }
    
    // in order not to repeat oneself, out-source here what happens (if something happen), when one wants to look at the only card in a graveyard
    public void chooseOnlyBanishedCard (boolean isBelongingToPlayer){
        if (isSwitchingOnInfoDialogs) {
            int intDialogResult = multipleChoiceDialog("Look at card info?", "", new String[]{"yes", "no"}, "no");
            if (intDialogResult==0) {
                YCardInfoWindow.openInfoWindowForBanishedCard(Deck.getDD(isBelongingToPlayer).getLastCardOfDeck());
            }
        }
    }
    
    // -- GAMEOVER methods: use like Game.over() --
    
    // in order not to repeat oneself, out-source here what happen, if one player has won or lost
    public void over (boolean playerHasWon) {
        displayMostTrophiesAtGameOver (playerHasWon);
        over();
    }
    
    // in order not to repeat oneself, out-source here what happen, if the game is over
    public void over () {
        giveUp(); // This effectively lets the player only click on Start, Deck Building, CPU setup, Mods, Game Setup, Info, and the popup telling who has won.
         // Furthermore, this effectively stops the computer from doing anything. (can not use any more strategies)
        phaseAsInt=10; // needed for stopping CPU from attacking any further
        informationDialog("Press the \"start/reset\" button to start a new game.", "Game Over");
    } // (Hovering on all kinds of cards and thus displaying larger versions of cards still works though, once one has closed the dialog telling who has won.)
    
    // in order to repeat oneself less, out-source here the display of most trophies
    public void displayMostTrophiesAtGameOver (boolean playerHasWon) {
        // display some hidden achievements here, if unlocked
        if (playerHasWon) {
            if (lifePointsPlayer<=500) {informationDialog("Hidden achievement found: \"That was close\". You have won with almost no health left.", ""); unlockTrophy(trophyCloseUnlocked);}
            if (lifePointsPlayer==initialLP) {informationDialog("Hidden achievement found: \"Flawless Victory\". You have won with full health.", ""); unlockTrophy(trophyVictoryUnlocked);}
            if (LPLastTurnCPU==initialLP) {informationDialog("Hidden achievement found: \"One Turn Kill\". You have won and decreased your opponent's life points completely during one single turn.", ""); unlockTrophy(trophyOTKUnlocked);}
            if (isFirstOwnTurnOrEarlier()) {informationDialog("Hidden achievement found: \"First Turn Kill\". You have won during your own first turn.", ""); unlockTrophy(trophyFTKUnlocked);}
            if (isVeryFirstTurn()) {informationDialog("Hidden achievement found: \"Real First Turn Kill\". You have won during the very first turn of the game.", ""); unlockTrophy(trophyRFTKUnlocked);}
            if (!isVeryFirstTurn() && statisticsNumAttDeclarations==0) {informationDialog("Hidden achievement found: \"Master of Paranoia\". You have won without having declared a single attack in the whole game.", ""); unlockTrophy(trophyParanoiaUnlocked);}
            if (statisticsNumAttDeclarations>=20) {informationDialog("Hidden achievement found: \"Admit Defeat Already!\". You have won and declared 20 or more attacks in one game.", ""); unlockTrophy(trophyDefeatUnlocked);}
            if (!isPlayersTurn) {informationDialog("Hidden achievement found: \"You didn't see that coming\". You have won during the opponent's turn.", ""); unlockTrophy(trophyDuringOpponentsTurnUnlocked);}
        }
        else {
            if (lifePointsCPU<=500) {informationDialog("Hidden achievement found: \"Hair\'s Breadth\". You lost to the computer having almost no health.", ""); unlockTrophy(trophyHairUnlocked);}
            if (lifePointsCPU==initialLP) {informationDialog("Hidden achievement found: \"Epic Fail\". You lost to the computer having its full health.", ""); unlockTrophy(trophyFailUnlocked);}
            if (statisticsNumAttDeclarations==0) {informationDialog("Hidden achievement found: \"Your Noble Shyness\". You have lost without having declared a single attack in the whole game.", ""); unlockTrophy(trophyShyUnlocked);}
        }
        // end of listing of achievements
    }
    
    public void unlockTrophy (boolean trophy){
        trophy=true;
        if (trophySaveUnlocked && trophyBouncerUnlocked && trophyFinePrintUnlocked && trophyHairUnlocked && trophyFailUnlocked && trophyShyUnlocked && trophyCloseUnlocked && trophyVictoryUnlocked && trophyOTKUnlocked && trophyFTKUnlocked && trophyRFTKUnlocked && trophyOneUnlocked && trophyParanoiaUnlocked && trophyDefeatUnlocked && trophyDuringOpponentsTurnUnlocked && trophyAdversaryUnlocked && trophyOverUnlocked && trophyPatienceUnlocked) {
            informationDialog("Platinum Trophy unlocked: Congratulation! You found all achievements in one gaming session!", "");
        }
    }
    
    
}
