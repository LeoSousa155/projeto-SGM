import greenfoot.*;

public class Button extends Actor
{
    private String label;
    private Runnable action;
    
    private GreenfootImage image;      // The normal image
    private GreenfootImage hoverImage; // The darkened image
    
    private boolean isMouseOver = false;

    public Button(String label, String imageFile, int width, int height, Runnable action) {
        this.label = label;
        this.action = action;
    
        // 1. Setup Main Image
        image = new GreenfootImage(imageFile);
        if (width > 0 && height > 0) {
            image.scale(width, height);
        }

        // 2. Create Hover Image (Pixel-Perfect Darkening)
        // Instead of drawing a rectangle, we process every pixel.
        hoverImage = new GreenfootImage(image.getWidth(), image.getHeight());
        
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color color = image.getColorAt(x, y);
                
                // Only darken if the pixel is not transparent
                if (color.getAlpha() > 0) {
                    int darkenFactor = 60; // Amount to subtract (0-255)
                    
                    int r = Math.max(0, color.getRed() - darkenFactor);
                    int g = Math.max(0, color.getGreen() - darkenFactor);
                    int b = Math.max(0, color.getBlue() - darkenFactor);
                    
                    // Set the darkened color, keeping original transparency
                    hoverImage.setColorAt(x, y, new Color(r, g, b, color.getAlpha()));
                }
            }
        }
        
        // 3. Set Initial State
        updateImage(false);
    }
    
    public Button(String label, String imageFile, Runnable action) {
        this(label, imageFile, -1, -1, action);
    }

    private void updateImage(boolean hovered) {
        // Use the pre-calculated dark image or the normal one
        GreenfootImage base = hovered ? hoverImage : image;
        
        // Create a fresh canvas to combine background + text
        GreenfootImage img = new GreenfootImage(base);

        // Draw Text
        String[] lines = label.split("\n");
        int fontSize = 28;
        int y = (img.getHeight() - (lines.length * fontSize)) / 2;
    
        for (String line : lines) {
            GreenfootImage text = new GreenfootImage(line, fontSize, Color.WHITE, new Color(0,0,0,0));
            int x = (img.getWidth() - text.getWidth()) / 2;
            img.drawImage(text, x, y);
            y += fontSize;
        }
    
        setImage(img);
    }

    public void act() {
        MouseInfo mouse = Greenfoot.getMouseInfo();
        
        // ----------------------------------------------------------
        // STABILITY FIX: Bounding Box Check
        // Instead of asking "is the mouse touching the actor?", we ask
        // "is the mouse coordinates inside the box of this button?"
        // This works even if the mouse stops moving.
        // ----------------------------------------------------------
        if (mouse != null) {
            int mx = mouse.getX();
            int my = mouse.getY();
            int bx = getX();
            int by = getY();
            int w = getImage().getWidth();
            int h = getImage().getHeight();

            // Check if mouse is inside the button area
            boolean currentlyOver = (mx > bx - w / 2 && mx < bx + w / 2 && 
                                     my > by - h / 2 && my < by + h / 2);

            if (currentlyOver && !isMouseOver) {
                isMouseOver = true;
                updateImage(true);
            } else if (!currentlyOver && isMouseOver) {
                isMouseOver = false;
                updateImage(false);
            }
            
            // Click handling
            if (currentlyOver && Greenfoot.mouseClicked(null)) {
                if (action != null) action.run();
            }
        }
    }
}