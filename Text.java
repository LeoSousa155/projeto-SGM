import greenfoot.*;

/**
 * Text actor with optional black outline.
 */
public class Text extends Actor
{
    private String content;
    private int fontSize;
    private Color color;
    private boolean outlined;

    // === Existing constructor (NO outline) ===
    public Text(String content, int fontSize, Color color)
    {
        this(content, fontSize, color, false);
    }

    // === New constructor (with outline toggle) ===
    public Text(String content, int fontSize, Color color, boolean outlined)
    {
        this.outlined = outlined;
        updateText(content, fontSize, color);
    }

    public void updateText(String content, int fontSize, Color color)
    {
        this.content = content;
        this.fontSize = fontSize;
        this.color = color;

        setImage(createTextImage());
    }

    private GreenfootImage createTextImage()
    {
        GreenfootImage base = new GreenfootImage(
            content,
            fontSize,
            color,
            new Color(0,0,0,0)
        );

        if (!outlined)
            return base;

        // Create slightly bigger image for outline
        GreenfootImage outlinedImg =
            new GreenfootImage(base.getWidth() + 2, base.getHeight() + 2);

        // Outline color
        Color outlineColor = Color.BLACK;

        // Draw outline (8 directions)
        GreenfootImage outlineText =
            new GreenfootImage(content, fontSize, outlineColor, new Color(0,0,0,0));

        outlinedImg.drawImage(outlineText, 0, 1);
        outlinedImg.drawImage(outlineText, 2, 1);
        outlinedImg.drawImage(outlineText, 1, 0);
        outlinedImg.drawImage(outlineText, 1, 2);

        // Corners (optional but looks nicer)
        outlinedImg.drawImage(outlineText, 0, 0);
        outlinedImg.drawImage(outlineText, 2, 0);
        outlinedImg.drawImage(outlineText, 0, 2);
        outlinedImg.drawImage(outlineText, 2, 2);

        // Draw main text on top
        outlinedImg.drawImage(base, 1, 1);

        return outlinedImg;
    }
}