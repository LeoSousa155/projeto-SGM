import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Text here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Text extends Actor
{
    private String content;
    
    public Text(String content, int fontSize, Color color) {
        updateText(content, fontSize, color);
    }
    
    public void updateText(String content, int fontSize, Color color)
    {
        GreenfootImage img = new GreenfootImage(content, fontSize, color, new Color(0,0,0,0));
        setImage(img);
    }
}
