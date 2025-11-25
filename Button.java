import greenfoot.*;

public class Button extends Actor
{
    private String label;
    private int fontSize;
    private Runnable action;

    private GreenfootImage image;      
    private GreenfootImage hoverImage; 

    private boolean isMouseOver = false;

    private boolean leftAligned = false;
    private boolean hoverEnabled = true;

    // ───────────────────── CONSTRUCTORS ─────────────────────

    public Button(String label, int fontSize, String imageFile, int width, int height, Runnable action) {
        this(label, fontSize, imageFile, width, height, action, false, true);
    }

    public Button(String label, int fontSize, String imageFile,
                  int width, int height, Runnable action, boolean leftAligned) {
        this(label, fontSize, imageFile, width, height, action, leftAligned, true);
    }

    // Master constructor
    public Button(String label, int fontSize, String imageFile,
                  int width, int height, Runnable action,
                  boolean leftAligned, boolean hoverEnabled)
    {
        this.label = label;
        this.fontSize = fontSize;   // ⭐ THIS WAS MISSING
        this.action = action;
        this.leftAligned = leftAligned;
        this.hoverEnabled = hoverEnabled;

        // 1. Setup Main Image
        image = new GreenfootImage(imageFile);
        if (width > 0 && height > 0) {
            image.scale(width, height);
        }

        // 2. Prepare hover image
        hoverImage = new GreenfootImage(image.getWidth(), image.getHeight());

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color color = image.getColorAt(x, y);

                if (color.getAlpha() > 0) {
                    int darken = 60;

                    int r = Math.max(0, color.getRed() - darken);
                    int g = Math.max(0, color.getGreen() - darken);
                    int b = Math.max(0, color.getBlue() - darken);

                    hoverImage.setColorAt(x, y, new Color(r, g, b, color.getAlpha()));
                }
            }
        }

        updateImage(false);
    }

    public Button(String label, String imageFile, Runnable action) {
        this(label, 28, imageFile, -1, -1, action);
    }

    public Button(String label, String imageFile, int width, int height, Runnable action) {
        this(label, 28, imageFile, width, height, action);
    }

    // Convenience: leftAligned + hoverDisabled option
    public Button(String label, int fontSize, String imageFile, int width, int height,
                  boolean leftAligned, boolean hoverEnabled, Runnable action) {
        this(label, fontSize, imageFile, width, height, action, leftAligned, hoverEnabled);
    }

    // ─────────────────── IMAGE + TEXT RENDERING ───────────────────

    private void updateImage(boolean hovered) {
        GreenfootImage base = (hovered && hoverEnabled) ? hoverImage : image;
        GreenfootImage img = new GreenfootImage(base);

        String[] lines = label.split("\n");
        int y = (img.getHeight() - (lines.length * fontSize)) / 2;

        for (String line : lines) {
            GreenfootImage text = new GreenfootImage(line, fontSize, Color.WHITE, new Color(0,0,0,0));

            int x;
            if (leftAligned) {
                x = 10;
            } else {
                x = (img.getWidth() - text.getWidth()) / 2;
            }

            img.drawImage(text, x, y);
            y += fontSize;
        }

        setImage(img);
    }

    // ───────────────────────── ACT LOOP ─────────────────────────

    public void act() {
        MouseInfo mouse = Greenfoot.getMouseInfo();
        if (mouse == null) return;

        int mx = mouse.getX();
        int my = mouse.getY();
        int bx = getX();
        int by = getY();
        int w = getImage().getWidth();
        int h = getImage().getHeight();

        boolean currentlyOver =
            (mx > bx - w/2 && mx < bx + w/2 &&
             my > by - h/2 && my < by + h/2);

        // Hover only if enabled
        if (hoverEnabled) {
            if (currentlyOver && !isMouseOver) {
                isMouseOver = true;
                updateImage(true);
            } else if (!currentlyOver && isMouseOver) {
                isMouseOver = false;
                updateImage(false);
            }
        }

        // Clicking still works
        if (currentlyOver && Greenfoot.mouseClicked(null)) {
            if (action != null) action.run();
        }
    }
}
