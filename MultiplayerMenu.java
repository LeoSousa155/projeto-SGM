import greenfoot.*;

public class MultiplayerMenu extends World
{
    private static GreenfootImage bg = new GreenfootImage("background.png");

    static {
        bg.scale(800, 600);
    }

    // Added CREATE_ROOM
    private enum PanelType { NONE, JOIN_ROOM, CREATE_ROOM }
    private PanelType currentPanelType = PanelType.NONE;

    private PanelBoard currentPanel = null;
    private PanelBoard joinRoomBoard;
    private PanelBoard createRoomBoard;

    @Override
    public void started()
    {
        MusicManager.enableMenuMusic();
        MusicManager.onScenarioStarted();
    }

    @Override
    public void stopped()
    {
        MusicManager.onScenarioStopped();
    }

    public MultiplayerMenu()
    {
        super(bg.getWidth(), bg.getHeight(), 1);
        setBackground(bg);

        Button joinRoom = new Button(
            "Join a Room",
            "button2.png",
            () -> showJoinRoomPanel()
        );
        addObject(joinRoom, 180, 340);

        Button createRoom = new Button(
            "Create a Room",
            "button2.png",
            () -> showCreateRoomPanel()
        );
        addObject(createRoom, 180, 400);

        Button back = new Button(
            "Back",
            "button2.png",
            () -> Greenfoot.setWorld(new MainMenu())
        );
        addObject(back, 180, 460);

        setPaintOrder(CodeBox.class, Button.class, Text.class, PanelBoard.class);
    }

    // ───────────────── PANEL HELPERS ─────────────────

    private void openPanel(PanelBoard newPanel, int x, int y)
    {
        if (currentPanel != null && currentPanel.getWorld() != null) {
            currentPanel.destroy();
        }
        addObject(newPanel, x, y);
        currentPanel = newPanel;
    }

    // ───────────────── JOIN ROOM PANEL ─────────────────

    private void showJoinRoomPanel()
    {
        if (currentPanelType == PanelType.JOIN_ROOM) {
            return; // already open
        }
        currentPanelType = PanelType.JOIN_ROOM;

        joinRoomBoard = new PanelBoard("panelboard.png");
        openPanel(joinRoomBoard, getWidth()/2 + 150, getHeight()/2);

        Button code = new Button(
            "   CODE:",
            25,
            "button1.png",
            220, 70,
            true,
            false,
            () -> {}
        );
        addObject(code, 530, 85);
        joinRoomBoard.addContent(code);

        // Code input box
        final CodeBox codeBox = new CodeBox(90, 30, 6); // width, height, max length
        addObject(codeBox, 560, 80);
        joinRoomBoard.addContent(codeBox);

        Button joinCode = new Button(
            "Join",
            25,
            "button1.png",
            100, 40,
            false,   // leftAligned
            true,    // hoverEnabled
            () -> {}
        );
        addObject(joinCode, 710, 90);
        joinRoomBoard.addContent(joinCode);

        // Base positions
        int baseX = 470;
        int baseY = 170;
        int rowSpacing = 60;
        int buttonWidth = 250;
        int buttonHeight = 40;

        Button join1 = new Button(
            "Join",
            25,
            "button1.png",
            100, buttonHeight,
            false,
            true,
            () -> Greenfoot.setWorld(new MultiplayerLobby())
        );
        addObject(join1, baseX + 240, baseY);
        joinRoomBoard.addContent(join1);

        Button join2 = new Button(
            "Join",
            25,
            "button1.png",
            100, buttonHeight,
            false,
            true,
            () -> Greenfoot.setWorld(new MultiplayerLobby())
        );
        addObject(join2, baseX + 240, baseY + rowSpacing);
        joinRoomBoard.addContent(join2);

        Button join3 = new Button(
            "Join",
            25,
            "button1.png",
            100, buttonHeight,
            false,
            true,
            () -> Greenfoot.setWorld(new MultiplayerLobby())
        );
        addObject(join3, baseX + 240, baseY + 2*rowSpacing);
        joinRoomBoard.addContent(join3);

        Button join4 = new Button(
            "Join",
            25,
            "button1.png",
            100, buttonHeight,
            false,
            true,
            () -> Greenfoot.setWorld(new MultiplayerLobby())
        );
        addObject(join4, baseX + 240, baseY + 3*rowSpacing);
        joinRoomBoard.addContent(join4);

        // Room 1
        Button room1 = new Button(
            "friends lol",
            20,
            "select.png",
            buttonWidth, buttonHeight,
            true,
            false,
            () -> {}
        );
        addObject(room1, baseX, baseY);
        joinRoomBoard.addContent(room1);

        Button room1Count = new Button(
            "1/4",
            25,
            "select.png",
            50, buttonHeight,
            false,
            false,
            () -> {}
        );
        addObject(room1Count, baseX+160, baseY);
        joinRoomBoard.addContent(room1Count);

        // Room 2
        Button room2 = new Button(
            "real cool hangout",
            20,
            "select.png",
            buttonWidth, buttonHeight,
            true,
            false,
            () -> {}
        );
        addObject(room2, baseX, baseY + rowSpacing);
        joinRoomBoard.addContent(room2);

        Button room2Count = new Button(
            "3/4",
            25,
            "select.png",
            50, buttonHeight,
            false,
            false,
            () -> {}
        );
        addObject(room2Count, baseX+160, baseY + rowSpacing);
        joinRoomBoard.addContent(room2Count);

        // Room 3
        Button room3 = new Button(
            "bruh",
            20,
            "select.png",
            buttonWidth, buttonHeight,
            true,
            false,
            () -> {}
        );
        addObject(room3, baseX, baseY + 2*rowSpacing);
        joinRoomBoard.addContent(room3);

        Button room3Count = new Button(
            "2/4",
            25,
            "select.png",
            50, buttonHeight,
            false,
            false,
            () -> {}
        );
        addObject(room3Count, baseX+160, baseY + 2*rowSpacing);
        joinRoomBoard.addContent(room3Count);

        // Room 4
        Button room4 = new Button(
            "i really like fishing",
            20,
            "select.png",
            buttonWidth, buttonHeight,
            true,
            false,
            () -> {}
        );
        addObject(room4, baseX, baseY + 3*rowSpacing);
        joinRoomBoard.addContent(room4);

        Button room4Count = new Button(
            "4/4",
            25,
            "select.png",
            50, buttonHeight,
            false,
            false,
            () -> {}
        );
        addObject(room4Count, baseX+160, baseY + 3*rowSpacing);
        joinRoomBoard.addContent(room4Count);
    }

    // ──────────────── CREATE ROOM PANEL ────────────────

    private void showCreateRoomPanel()
    {
        if (currentPanelType == PanelType.CREATE_ROOM) {
            return; // already open
        }
        currentPanelType = PanelType.CREATE_ROOM;

        createRoomBoard = new PanelBoard("panelboard.png");
        openPanel(createRoomBoard, getWidth()/2 + 150, getHeight()/2);

        int labelX = 420;
        int nameY  = 150;
        int passY  = 250;

        // "Name of Server:"
        Text nameLabel = new Text("Name of Server:", 25, Color.WHITE);
        addObject(nameLabel, labelX, nameY);
        createRoomBoard.addContent(nameLabel);

        // Name input box
        final CodeBox nameBox = new CodeBox(380, 30, 30, true); // width, height, max length, allowSpace
        addObject(nameBox, labelX + 120, nameY + 40);
        createRoomBoard.addContent(nameBox);

        // "Password:"
        Text passLabel = new Text("Password:", 25, Color.WHITE);
        addObject(passLabel, labelX - 20, passY);
        createRoomBoard.addContent(passLabel);

        // Password input box
        final CodeBox passBox = new CodeBox(260, 30, 10);
        addObject(passBox, labelX + 60, passY + 40);
        createRoomBoard.addContent(passBox);

        Button createBtn = new Button(
            "Create",
            25,
            "button1.png",
            160, 50,
            false,  // left aligned? no, center text
            true,   // hover enabled
            () -> Greenfoot.setWorld(new MultiplayerLobby())
        );
        // Position roughly like in your screenshot
        addObject(createBtn, 550, 400);
        createRoomBoard.addContent(createBtn);
    }

    private void createRoom(String name, String password)
    {
        // TODO: implement your actual create-room logic here
        System.out.println("Creating room: \"" + name + "\" with password: \"" + password + "\"");
    }
}