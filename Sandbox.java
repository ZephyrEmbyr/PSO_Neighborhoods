import java.lang.*;
public class Sandbox
{
    public static void main(String args[])
    {
        Best temp = new Best();
        try
        {
            Thread.sleep(20);
        }
        catch (Exception e)
        {
            //do nothing
        }
        System.out.println(temp.getTimeElapsed());
    }
}
