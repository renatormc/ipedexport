package go.sptc.sinf;

import static org.junit.Assert.assertTrue;

import go.sptc.sinf.services.IpedIndexService;
import org.junit.Test;

import go.sptc.sinf.services.Logger;
import go.sptc.sinf.services.ResourceManager;

import java.util.ArrayList;
import java.util.HashMap;


public class AppTest
{

    @Test
    public void testar() throws Exception {
        Logger copyLogger = new Logger("copy.log");
        IpedIndexService ipedService = new IpedIndexService("F:\\ipedExport_teste\\procesamento", copyLogger);

        ipedService.setHitsPerPage(50);


        try {
            ArrayList<HashMap<String, Object>> data = ipedService.query("categoria:text* ", "*");
            System.out.println(data.size());
            System.out.println(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
