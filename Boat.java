import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * A classe Boat representa o barco do jogo, combinando as visões interna e externa.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Boat extends Actor
{
    public Boat()
    {
        // 1. Carrega a imagem do interior como base.
        GreenfootImage finalImage = new GreenfootImage("boatinterior1.png");
        
        // 2. Carrega a imagem do exterior para sobrepor.
        GreenfootImage exteriorImage = new GreenfootImage("boatexterior2.png");
        
        // 3. Desenha a imagem exterior sobre a interior.
        //    As imagens devem ter o mesmo tamanho para um alinhamento correto.
        finalImage.drawImage(exteriorImage, 0, 0);
        
        // 4. Redimensiona a imagem combinada para um tamanho adequado à tela (ex: 800x600).
        //    Ajuste a largura (800) e altura (600) conforme necessário.
        finalImage.scale(800, 600);
        
        // 5. Define a imagem final para o ator.
        setImage(finalImage);
    }
}
