package go.sptc.sinf;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import go.sptc.sinf.services.Logger;



public class AppTest 
{
    
    @Test
    public void shouldAnswerWithTrue()
    {
        Logger logger = new Logger("C:/temp/test2.log");
        try{
            logger.start();
            logger.write("Testando mensagem do log");
        }finally{
            logger.close();
        }
    
    }
}
