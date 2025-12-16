import greenfoot.*;

public class PanelImage extends Actor
{
    public PanelImage(String fileName, int w, int h)
    {
        GreenfootImage img = new GreenfootImage(fileName);
        if (w > 0 && h > 0) img.scale(w, h);
        setImage(img);
    }
}