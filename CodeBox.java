import greenfoot.*;

public class CodeBox extends Actor
{
    private String text = "";
    private int width;
    private int height;
    private int maxLength;

    private boolean focused = false;
    private boolean showCursor = false;
    private int cursorTimer = 0;

    // NEW: per-box settings
    private boolean allowSpace = false;
    private boolean readOnly = false;

    // Keys we care about
    private String[] keys;
    private boolean[] wasDown;
    private int[] holdTicks;

    // Key repeat timing
    private final int initialDelay = 12;  // frames before auto-repeat starts
    private final int repeatSpeed  = 3;   // frames between repeats after start

    // DEFAULT → no space, editable
    public CodeBox(int width, int height, int maxLength)
    {
        this(width, height, maxLength, false, false);
    }

    // Allow spaces toggle
    public CodeBox(int width, int height, int maxLength, boolean allowSpace)
    {
        this(width, height, maxLength, allowSpace, false);
    }

    // FULL CONTROL → allowSpace + readOnly
    public CodeBox(int width, int height, int maxLength, boolean allowSpace, boolean readOnly)
    {
        this.width = width;
        this.height = height;
        this.maxLength = maxLength;
        this.allowSpace = allowSpace;
        this.readOnly = readOnly;

        initKeyArrays();
        updateImage();
    }

    // ──────────────────────────────────────────────
    // KEY SETUP
    // ──────────────────────────────────────────────
    private void initKeyArrays()
    {
        String letters = "abcdefghijklmnopqrstuvwxyz";
        keys = new String[26 + 10 + 2];

        int idx = 0;

        for (int i = 0; i < letters.length(); i++)
            keys[idx++] = "" + letters.charAt(i);

        for (int d = 0; d <= 9; d++)
            keys[idx++] = "" + d;

        keys[idx++] = "space";
        keys[idx++] = "backspace";

        wasDown = new boolean[keys.length];
        holdTicks = new int[keys.length];
    }

    // ──────────────────────────────────────────────
    // ACT LOOP
    // ──────────────────────────────────────────────
    public void act()
    {
        if (!readOnly) {
            handleMouse();

            if (focused) {
                pollKeysForTyping();
                blinkCursor();
            }
        }
    }

    // ──────────────────────────────────────────────
    // MOUSE FOCUS HANDLING
    // ──────────────────────────────────────────────
    private void handleMouse()
    {
        if (readOnly) return;

        // Click → focus
        if (Greenfoot.mouseClicked(this)) {
            focused = true;
            showCursor = true;
            cursorTimer = 0;
            updateImage();
            return;
        }

        // Click outside → unfocus
        if (Greenfoot.mouseClicked(null)) {
            MouseInfo m = Greenfoot.getMouseInfo();
            if (m == null) return;

            int mx = m.getX(), my = m.getY();
            int bx = getX(), by = getY();
            int w = getImage().getWidth(), h = getImage().getHeight();

            boolean inside =
                mx > bx - w/2 && mx < bx + w/2 &&
                my > by - h/2 && my < by + h/2;

            if (!inside && focused) {
                focused = false;
                showCursor = false;
                updateImage();
            }
        }
    }

    // ──────────────────────────────────────────────
    // KEY POLLING + HOLD-DOWN REPEAT
    // ──────────────────────────────────────────────
    private void pollKeysForTyping()
    {
        for (int i = 0; i < keys.length; i++) {

            String key = keys[i];
            boolean down = Greenfoot.isKeyDown(key);

            if (down) {

                if (!wasDown[i]) {
                    // Just pressed
                    applyKey(key);
                    wasDown[i] = true;
                    holdTicks[i] = 0;
                } else {
                    // Held down
                    holdTicks[i]++;
                    if (holdTicks[i] > initialDelay &&
                        (holdTicks[i] - initialDelay) % repeatSpeed == 0)
                    {
                        applyKey(key);
                    }
                }

            } else {
                wasDown[i] = false;
                holdTicks[i] = 0;
            }
        }
    }

    // ──────────────────────────────────────────────
    // APPLY TYPED KEY
    // ──────────────────────────────────────────────
    private void applyKey(String key)
    {
        boolean changed = false;

        if (key.equals("backspace")) {
            if (text.length() > 0) {
                text = text.substring(0, text.length() - 1);
                changed = true;
            }
        }
        else if (key.equals("space")) {
            if (allowSpace && text.length() < maxLength) {
                text += " ";
                changed = true;
            }
        }
        else if (key.length() == 1) {
            char c = key.charAt(0);
            if (Character.isLetterOrDigit(c) && text.length() < maxLength) {
                text += c;
                changed = true;
            }
        }

        if (changed) updateImage();
    }

    // ──────────────────────────────────────────────
    // CURSOR BLINK
    // ──────────────────────────────────────────────
    private void blinkCursor()
    {
        cursorTimer++;
        if (cursorTimer % 25 == 0) {
            showCursor = !showCursor;
            updateImage();
        }
    }

    // ──────────────────────────────────────────────
    // DRAW IMAGE
    // ──────────────────────────────────────────────
    private void updateImage()
    {
        GreenfootImage img = new GreenfootImage(width, height);

        img.setColor(Color.WHITE);
        img.fill();

        // Black border unless focused (and editable)
        if (readOnly)
            img.setColor(Color.BLACK);
        else
            img.setColor(focused ? Color.BLUE : Color.BLACK);

        img.drawRect(0, 0, width - 1, height - 1);

        String display = text;
        if (!readOnly && focused && showCursor)
            display += "|";

        GreenfootImage txt = new GreenfootImage(display, 20, Color.BLACK, new Color(0,0,0,0));
        img.drawImage(txt, 5, (height - txt.getHeight()) / 2);

        setImage(img);
    }

    // ──────────────────────────────────────────────
    // PUBLIC API
    // ──────────────────────────────────────────────
    public String getText()
    {
        return text;
    }

    public void setText(String t)
    {
        if (t == null) t = "";
        if (t.length() > maxLength) t = t.substring(0, maxLength);
        text = t;
        updateImage();
    }
}