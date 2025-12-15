import greenfoot.*;

public class BiologistDropZone extends Actor
{
    private final int index;
    private final String text;

    public BiologistDropZone(int index, String text)
    {
        this.index = index;
        this.text = text;

        GreenfootImage img = new GreenfootImage(260, 110);
        img.setColor(new Color(0,0,0,160));
        img.fillRect(0,0,img.getWidth(), img.getHeight());

        img.setColor(Color.WHITE);
        img.drawRect(0,0,img.getWidth()-1, img.getHeight()-1);

        Text textActor = new Text(text, 24, Color.WHITE, true);
        GreenfootImage textImg = textActor.getImage();
        img.drawImage(textImg, 10, 10);

        setImage(img);
    }

    public int getIndex() { return index; }

    public boolean containsPoint(int worldX, int worldY)
    {
        int w = getImage().getWidth();
        int h = getImage().getHeight();

        return worldX >= getX() - w/2 && worldX <= getX() + w/2 &&
               worldY >= getY() - h/2 && worldY <= getY() + h/2;
    }
}