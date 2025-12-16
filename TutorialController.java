import greenfoot.*;

public class TutorialController
{
    // Tutorial state
    private static boolean tutorialMode = false;

    private static SingleplayerPlaying boundWorld = null;
    
    // ===== DEBUG =====
    private static boolean DEBUG_FORCE_TUTORIAL = false;
    private static DebugStep DEBUG_STEP = DebugStep.CAPTAIN;
    public enum DebugStep { CAPTAIN, ENGINEER, FISHERMAN, BIOLOGIST }
    
    // Step flags
    private static boolean captainIntroShown = false;
    private static boolean engineerIntroShown = false;
    private static boolean fishermanIntroShown = false;
    private static boolean biologistIntroShown = false;
    
    // Arrow (optional to keep a reference)
    private static TutorialArrow arrow = null;

    // Target: Captain trigger world coords
    public static final int CAPTAIN_TARGET_X = 843;
    public static final int CAPTAIN_TARGET_Y = 564;
    
    // Target: Engineer trigger world coords
    public static final int ENGINEER_TARGET_X = 1440;
    public static final int ENGINEER_TARGET_Y = 950;
    
    // Target: Fisherman trigger world coords
    public static final int FISHERMAN_TARGET_X = 364;
    public static final int FISHERMAN_TARGET_Y = 740;
    
    // Target: Biologist trigger world coords
    public static final int BIOLOGIST_TARGET_X = 919;
    public static final int BIOLOGIST_TARGET_Y = 766;
    
    private enum Step { CAPTAIN, ENGINEER, FISHERMAN, BIOLOGIST, DONE }
    private static Step activeStep = Step.CAPTAIN;

    /** Convenience wrappers so triggers don't need to see the enum. */
    public static boolean allowCaptainTrigger()   { return !tutorialMode || activeStep == Step.CAPTAIN; }
    public static boolean allowEngineerTrigger()  { return !tutorialMode || activeStep == Step.ENGINEER; }
    public static boolean allowFishermanTrigger() { return !tutorialMode || activeStep == Step.FISHERMAN; }
    public static boolean allowBiologistTrigger() { return !tutorialMode || activeStep == Step.BIOLOGIST; }
    
    public static void enableDebugTutorial(DebugStep step)
    {
        DEBUG_FORCE_TUTORIAL = true;
        DEBUG_STEP = step;
    }
    
    public static boolean isTutorialMode()
    {
        return tutorialMode;
    }
    
    /** Returns true if THIS trigger type is allowed to open right now (during tutorial). */
    public static boolean isStepAllowed(Step step)
    {
        if (!tutorialMode) return true;        // not in tutorial => all allowed
        return activeStep == step;             // in tutorial => only active one allowed
    }
    
    private static void resetAllState()
    {
        tutorialMode = false;
        captainIntroShown = false;
        engineerIntroShown = false;
        fishermanIntroShown = false;
        biologistIntroShown = false;
        arrow = null;
    }

    /** Call once when the playing world is created. */
    public static void maybeShowTutorial(SingleplayerPlaying world)
    {
        // ===== DEBUG =====
        if (DEBUG_FORCE_TUTORIAL)
        {
            tutorialMode = true;
        
            switch (DEBUG_STEP)
            {
                case CAPTAIN:
                    captainIntroShown = false;
                    showCaptainIntro(world, null);
                    break;
        
                case ENGINEER:
                    engineerIntroShown = false;
                    showEngineerIntro(world, null);
                    break;
        
                case FISHERMAN:
                    fishermanIntroShown = false;
                    showFishermanIntro(world, null);
                    break;
        
                case BIOLOGIST:
                    biologistIntroShown = false;
                    showBiologistIntro(world, null);
                    break;
            }
        
            return;
        }
        
        if (world == null) return;
    
        if (boundWorld != world)
        {
            resetAllState();
            boundWorld = world;
        }
        
        // Show only once per SingleplayerPlaying instance
        if (world.isTutorialPromptShown()) return;
        world.setTutorialPromptShown(true);
    
        if (!MinigameLock.tryLock())
            return;
    
        PanelBoard board = new PanelBoard("panelboard.png", 700, 500);
        world.addObject(board, world.getWidth() / 2, world.getHeight() / 2);
    
        MinigameLock.registerForceClose(() -> {
            if (board.getWorld() != null) board.destroy();
            MinigameLock.setLocked(false);
        });
    
        Text title = new Text("Tutorial", 50, Color.WHITE, true);
        board.addContent(title, 0, -200);
        
        Text title2 = new Text("Story:", 35, Color.WHITE, true);
        board.addContent(title2, 0, -150);
    
        Text story = new Text(
            "After hearing about the numerous opportunities in the sea,\n" +
            "you decided to buy a boat and try your luck in the seas.\n" +
            "However, since you didn’t have enough money, you went and got\n" +
            "a bank loan to pay it off! Now your objective is to get the money\n" +
            "to pay the loan back, so you can enjoy the\n" +
            "sea life and finally make some money off it.",
            24,
            Color.WHITE,
            true
        );
        board.addContent(story, 0, -60);
        
        Text objective1 = new Text(
            "Reach 1000$ to win.",
            26,
            Color.GREEN,
            true
        );
        board.addContent(objective1, -120, 35);
        
        Text objective2 = new Text(
            "If you somehow reach -1000$, you fail!",
            26,
            Color.RED,
            true
        );
        board.addContent(objective2, -120, 60);
        
        Text proceed = new Text(
            "Start by going into the Captain's Cabin by\n" +
            "following the yellow arrow and interact (E)\n" +
            "with the ship's wheel.",
            24, 
            Color.YELLOW,
            true
        );
        board.addContent(proceed, -120, 120);

        PanelImage arrowImg = new PanelImage("yellow-arrow.png", 128, 80);
        board.addContent(arrowImg, -250, 170);

        int posX = 200;
        int posY = 80;
        
        PanelImage pic = new PanelImage("W.png", 50, 50);
        board.addContent(pic, posX, posY);
        
        PanelImage pic2 = new PanelImage("S.png", 50, 50);
        board.addContent(pic2, posX, posY + 55);
        
        PanelImage pic3 = new PanelImage("A.png", 50, 50);
        board.addContent(pic3, posX - 55, posY + 55);
        
        PanelImage pic4 = new PanelImage("D.png", 50, 50);
        board.addContent(pic4, posX + 55, posY + 55);
        
        PanelImage pic5 = new PanelImage("E.png", 50, 50);
        board.addContent(pic5, posX + 55, posY);
        
        Text instr = new Text(
            "Up",
            22,
            Color.WHITE,
            true
        );
        board.addContent(instr, posX, posY - 45);
        
        Text instr2 = new Text(
            "Down",
            22,
            Color.WHITE,
            true
        );
        board.addContent(instr2, posX, posY + 95);
        
        Text instr3 = new Text(
            "Left",
            22,
            Color.WHITE,
            true
        );
        board.addContent(instr3, posX - 55, posY + 95);
        
        Text instr4 = new Text(
            "Right",
            22,
            Color.WHITE,
            true
        );
        board.addContent(instr4, posX + 55, posY + 95);
        
        Text instr5 = new Text(
            "Interact",
            22,
            Color.WHITE,
            true
        );
        board.addContent(instr5, posX + 55, posY - 45);
    
        Runnable closePanelUnlock = () -> {
            if (board.getWorld() != null) board.destroy();
            MinigameLock.setLocked(false);
        };
    
        Button continueBtn = new Button(
            "Continue", 28, "button1.png", 220, 60,
            () -> {
                tutorialMode = true;
                activeStep = Step.CAPTAIN;
                closePanelUnlock.run();
    
                if (arrow == null || arrow.getWorld() == null)
                {
                    arrow = new TutorialArrow("yellow-arrow.png", CAPTAIN_TARGET_X, CAPTAIN_TARGET_Y);
                    world.addObject(arrow, 0, 0);
                }
                else
                {
                    arrow.setTarget(CAPTAIN_TARGET_X, CAPTAIN_TARGET_Y);
                }
            }
        );
    
        int continueOffsetY =
            board.getHalfHeight() - continueBtn.getImage().getHeight() / 2 - 10;
        board.addContent(continueBtn, 0, continueOffsetY);
    
        Button skipBtn = new Button(
            "Skip Tutorial", 20, "button1.png", 200, 45,
            () -> {
                tutorialMode = false;
                activeStep = Step.DONE;
                closePanelUnlock.run();
    
                if (arrow != null && arrow.getWorld() != null)
                    arrow.getWorld().removeObject(arrow);
                arrow = null;
            }
        );
    
        int skipOffsetX =
            -board.getHalfWidth() + skipBtn.getImage().getWidth() / 2 + 10;
        int skipOffsetY =
            -board.getHalfHeight() + skipBtn.getImage().getHeight() / 2 + 10;
        board.addContent(skipBtn, skipOffsetX, skipOffsetY);
    }

    /** True if we should show the "before Captain minigame" tutorial panel. */
    public static boolean shouldShowCaptainIntro()
    {
        return tutorialMode && !captainIntroShown;
    }

    /**
     * Shows the Captain intro tutorial panel.
     * On Continue: closes panel, unlocks, marks step done, then runs onContinue (which should start minigame).
     */
    public static void showCaptainIntro(World w, Runnable onContinue)
    {
        if (!(w instanceof SingleplayerPlaying)) return;
        SingleplayerPlaying world = (SingleplayerPlaying) w;

        // Lock movement / interaction like a minigame
        if (!MinigameLock.tryLock())
            return;

        PanelBoard board = new PanelBoard("panelboard.png", 700, 500);
        world.addObject(board, world.getWidth() / 2, world.getHeight() / 2);

        MinigameLock.registerForceClose(() -> {
            if (board.getWorld() != null) board.destroy();
            MinigameLock.setLocked(false);
        });

        // ---- Content ----
        Text title = new Text("Captain Minigame – Steering the Ship", 40, Color.WHITE, true);
        board.addContent(title, 0, -200);

        Text body = new Text(
            "Navigate the boat safely to the next fishing spot to get paid.\n" +
            "Hitting rocks will damage the boat costing you money.",
            24,
            Color.YELLOW,
            true
        );
        board.addContent(body, 0, -140);
        
        PanelImage img = new PanelImage("capgame_tuto.png", 400, 285);
        board.addContent(img, -100, 35);
        
        int posX = 200;
        int posY = 0;
        
        PanelImage pic = new PanelImage("W.png", 50, 50);
        board.addContent(pic, posX, posY);
        
        PanelImage pic2 = new PanelImage("S.png", 50, 50);
        board.addContent(pic2, posX, posY + 55);
        
        PanelImage pic3 = new PanelImage("A.png", 50, 50);
        board.addContent(pic3, posX - 55, posY + 55);
        
        PanelImage pic4 = new PanelImage("D.png", 50, 50);
        board.addContent(pic4, posX + 55, posY + 55);
        
        Text instr = new Text(
            "Up",
            22,
            Color.WHITE,
            true
        );
        board.addContent(instr, posX, posY - 45);
        
        Text instr2 = new Text(
            "Down",
            22,
            Color.WHITE,
            true
        );
        board.addContent(instr2, posX, posY + 95);
        
        Text instr3 = new Text(
            "Left",
            22,
            Color.WHITE,
            true
        );
        board.addContent(instr3, posX - 55, posY + 95);
        
        Text instr4 = new Text(
            "Right",
            22,
            Color.WHITE,
            true
        );
        board.addContent(instr4, posX + 55, posY + 95);
        
        Button continueBtn = new Button(
            "Continue", 28, "button1.png", 220, 60,
            () -> {
                captainIntroShown = true;

                if (board.getWorld() != null) board.destroy();
                MinigameLock.setLocked(false); // IMPORTANT: release before real minigame locks

                if (onContinue != null) onContinue.run();
            }
        );

        int continueOffsetY =
            board.getHalfHeight() - continueBtn.getImage().getHeight() / 2 - 10;
        board.addContent(continueBtn, 0, continueOffsetY);
    }
    
    /** Called when Captain minigame ends (success OR closed). */
    public static void onCaptainMinigameClosed()
    {
        if (!tutorialMode) return;
        activeStep = Step.ENGINEER;
    
        if (arrow != null && arrow.getWorld() != null)
            arrow.setTarget(ENGINEER_TARGET_X, ENGINEER_TARGET_Y);
    }
    
    public static boolean shouldShowEngineerIntro()
    {
        return tutorialMode && !engineerIntroShown;
    }
    
    public static void showEngineerIntro(World w, Runnable onContinue)
    {
        if (!(w instanceof SingleplayerPlaying)) return;
        SingleplayerPlaying world = (SingleplayerPlaying) w;
    
        if (!MinigameLock.tryLock())
            return;
    
        PanelBoard board = new PanelBoard("panelboard.png", 700, 500);
        world.addObject(board, world.getWidth() / 2, world.getHeight() / 2);
    
        MinigameLock.registerForceClose(() -> {
            if (board.getWorld() != null) board.destroy();
            MinigameLock.setLocked(false);
        });
    
        // ---- Template content ----
        Text title = new Text("Engineer Minigame - Connect the Wires", 40, Color.WHITE, true);
        board.addContent(title, 0, -200);
    
        Text body = new Text( 
            "Reconnect the loose wires by dragging them to the correct pegs.\n" +
            "Fixing the engine keeps the ship running and earns you money.\n" + 
            "Wrong matches will damage the ship even more, costing you money!",
            24,
            Color.YELLOW,
            true
        );
        board.addContent(body, 0, -130);
        
        int posX = 200;
        int posY = 50;
        
        PanelImage pic = new PanelImage("Mouse.png", 70, 70);
        board.addContent(pic, posX, posY);
        
        Text instr = new Text(
            "Click-Hold & Drag",
            22,
            Color.WHITE,
            true
        );
        board.addContent(instr, posX, posY - 55);
        
        PanelImage img = new PanelImage("enggame_tuto.png", 375, 267);
        board.addContent(img, -100, 45);
    
        Button continueBtn = new Button(
            "Continue", 28, "button1.png", 220, 60,
            () -> {
                engineerIntroShown = true;
    
                if (board.getWorld() != null) board.destroy();
                MinigameLock.setLocked(false); // release before real minigame locks
    
                if (onContinue != null) onContinue.run();
            }
        );
    
        int continueOffsetY =
            board.getHalfHeight() - continueBtn.getImage().getHeight() / 2 - 10;
        board.addContent(continueBtn, 0, continueOffsetY);
    }
    
    /** Called when Engineer minigame ends (success OR closed). */
    public static void onEngineerMinigameClosed()
    {
        if (!tutorialMode) return;
        activeStep = Step.FISHERMAN;
    
        if (arrow != null && arrow.getWorld() != null)
            arrow.setTarget(FISHERMAN_TARGET_X, FISHERMAN_TARGET_Y);
    }
    
    public static boolean shouldShowFishermanIntro()
    {
        return tutorialMode && !fishermanIntroShown;
    }
    
    public static void showFishermanIntro(World w, Runnable onContinue)
    {
        if (!(w instanceof SingleplayerPlaying)) return;
        SingleplayerPlaying world = (SingleplayerPlaying) w;
    
        if (!MinigameLock.tryLock())
            return;
    
        PanelBoard board = new PanelBoard("panelboard.png", 700, 500);
        world.addObject(board, world.getWidth() / 2, world.getHeight() / 2);
    
        MinigameLock.registerForceClose(() -> {
            if (board.getWorld() != null) board.destroy();
            MinigameLock.setLocked(false);
        });
    
        // ---- Template content ----
        Text title = new Text("Fisherman Minigame - Fishing Timing", 40, Color.WHITE, true);
        board.addContent(title, 0, -200);
    
        Text body = new Text(
            "Click the Interact Key when the cursor is inside the Green Zone.\n" + 
            "Time your presses carefully to catch the fish or risk losing it and\n" + 
            "damage your fishing rod, costing you money!\n",
            24,
            Color.YELLOW,
            true
        );
        board.addContent(body, 0, -130);
        
        int posX = 230;
        int posY = 50;
        
        PanelImage pic = new PanelImage("E.png", 60, 60);
        board.addContent(pic, posX, posY);
        
        Text instr = new Text(
            "Interact",
            22,
            Color.WHITE,
            true
        );
        board.addContent(instr, posX, posY - 55);
        
        PanelImage img = new PanelImage("fishgame_tuto.png", 160, 150);
        board.addContent(img, -250, -10);
        
        PanelImage img2 = new PanelImage("fishgame_tuto2.png", 160, 150);
        board.addContent(img2, -100, 40);
        
        PanelImage img3 = new PanelImage("fishgame_tuto3.png", 160, 150);
        board.addContent(img3, 50, 90);
    
        Button continueBtn = new Button(
            "Continue", 28, "button1.png", 220, 60,
            () -> {
                fishermanIntroShown = true;
    
                if (board.getWorld() != null) board.destroy();
                MinigameLock.setLocked(false); // release before real minigame locks
    
                if (onContinue != null) onContinue.run();
            }
        );
    
        int continueOffsetY =
            board.getHalfHeight() - continueBtn.getImage().getHeight() / 2 - 10;
        board.addContent(continueBtn, 0, continueOffsetY);
    }
    
    /** Called when Fisherman minigame ends (success OR closed). */
    public static void onFishermanMinigameClosed()
    {
        if (!tutorialMode) return;
        activeStep = Step.BIOLOGIST;
    
        if (arrow != null && arrow.getWorld() != null)
            arrow.setTarget(BIOLOGIST_TARGET_X, BIOLOGIST_TARGET_Y);
    }
    
    public static boolean shouldShowBiologistIntro()
    {
        return tutorialMode && !biologistIntroShown;
    }
    
    public static void showBiologistIntro(World w, Runnable onContinue)
    {
        if (!(w instanceof SingleplayerPlaying)) return;
        SingleplayerPlaying world = (SingleplayerPlaying) w;
    
        if (!MinigameLock.tryLock())
            return;
    
        PanelBoard board = new PanelBoard("panelboard.png", 700, 500);
        world.addObject(board, world.getWidth() / 2, world.getHeight() / 2);
    
        MinigameLock.registerForceClose(() -> {
            if (board.getWorld() != null) board.destroy();
            MinigameLock.setLocked(false);
        });
    
        Text title = new Text("Biologist Minigame - Judge, Jury, Executioner", 35, Color.WHITE, true);
        board.addContent(title, 0, -200);
    
        Text body = new Text(
            "Drag your fish onto the description that matches it to earn half of the\n" + 
            "fish's value as money!\n" +
            "Afterwards, decide whether to keep the fish (and money) or\n" +
            "or release it (and lose money). Illegal fish (endangered or too small)\n" +
            "must be released. Keeping illegal fish results in heavy penalties!",
            22,
            Color.YELLOW,
            true
        );
        board.addContent(body, 0, -125);
        
        PanelImage img = new PanelImage("biogame_tuto.png", 350, 220);
        board.addContent(img, -165, 45);

        PanelImage img2 = new PanelImage("biogame_tuto2.png", 350, 220);
        board.addContent(img2, 165, 45);
        
        PanelImage pic = new PanelImage("Mouse.png", 70, 70);
        board.addContent(pic, 0, 45);
        
        Text instr = new Text(
            "Click-Hold & Drag",
            22,
            Color.WHITE,
            true
        );
        board.addContent(instr, 0, 0);
        
        Button continueBtn = new Button(
            "Continue", 28, "button1.png", 220, 60,
            () -> {
                biologistIntroShown = true;
    
                if (board.getWorld() != null) board.destroy();
                MinigameLock.setLocked(false); // release before real minigame locks
    
                if (onContinue != null) onContinue.run();
            }
        );
    
        int continueOffsetY =
            board.getHalfHeight() - continueBtn.getImage().getHeight() / 2 - 10;
        board.addContent(continueBtn, 0, continueOffsetY);
    }
    
    public static void onBiologistMinigameClosed(World w)
    {
        if (!tutorialMode) return;
        if (!(w instanceof SingleplayerPlaying)) return;
        SingleplayerPlaying world = (SingleplayerPlaying) w;
        activeStep = Step.DONE;
    
        // 1) Remove arrow
        if (arrow != null && arrow.getWorld() != null)
            arrow.getWorld().removeObject(arrow);
        arrow = null;
    
        // 2) Show final tutorial panel
        showTutorialComplete(world);
    }
    
    private static void showTutorialComplete(SingleplayerPlaying world)
    {
        if (!MinigameLock.tryLock())
            return;
    
        PanelBoard board = new PanelBoard("panelboard.png", 700, 500);
        world.addObject(board, world.getWidth() / 2, world.getHeight() / 2);
    
        MinigameLock.registerForceClose(() -> {
            if (board.getWorld() != null) board.destroy();
            MinigameLock.setLocked(false);
        });
    
        // Template content (same style as initial)
        Text title = new Text("Tutorial Complete", 50, Color.WHITE, true);
        board.addContent(title, 0, -200);
    
        Text body = new Text(
            "You've learned all minigames, now the real game can begin!\n" +
            "Remember! Look out for rocks, carefully fix wires,\n" +
            "fish with good timing and respect the fishing laws.\n" + 
            "Now go pay that bank loan so that you can travel\n" +
            "these vast seas!",
            26,
            Color.YELLOW,
            true);
        board.addContent(body, 0, -50);
    
        Button continueBtn = new Button(
            "Begin", 28, "button1.png", 220, 60,
            () -> {
                // Close panel
                if (board.getWorld() != null) board.destroy();
                MinigameLock.setLocked(false);
    
                // 3) Disable tutorial mode
                endTutorialMode();
            }
        );
    
        int continueOffsetY =
            board.getHalfHeight() - continueBtn.getImage().getHeight() / 2 - 10;
        board.addContent(continueBtn, 0, continueOffsetY);
    }
    
    private static void endTutorialMode()
    {
        tutorialMode = false;
        activeStep = Step.DONE;
    }

}