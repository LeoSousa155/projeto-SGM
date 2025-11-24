import greenfoot.*;

public class Button extends Actor
{
    private String label;
    private Runnable action;
    private GreenfootImage baseImage;

    public Button(String label, String imageFile, int width, int height, Runnable action) {
        this.label = label;
        this.action = action;
    
        baseImage = new GreenfootImage(imageFile);
    
        if (width > 0 && height > 0) {
            baseImage.scale(width, height);
        }
    
        updateImage();
    }
    
    public Button(String label, String imageFile, Runnable action) {
        this(label, imageFile, -1, -1, action);
    }

    private void updateImage() {
        GreenfootImage img = new GreenfootImage(baseImage);
    
        // Split label into lines
        String[] lines = label.split("\n");
    
        // Draw each line centered
        int y = (img.getHeight() - lines.length * 28) / 2;  // 28 = font height
    
        for (String line : lines) {
            GreenfootImage text = new GreenfootImage(line, 28, Color.WHITE, new Color(0,0,0,0));
            int x = (img.getWidth() - text.getWidth()) / 2;
            img.drawImage(text, x, y);
            y += 28;
        }
    
        setImage(img);
    }

    public void act() {
        if (Greenfoot.mouseClicked(this) && action != null) {
            action.run();
        }
    }
}