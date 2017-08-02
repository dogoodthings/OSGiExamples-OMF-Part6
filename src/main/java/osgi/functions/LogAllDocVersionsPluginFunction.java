package osgi.functions;

import com.dscsag.plm.spi.interfaces.ECTRService;
import com.dscsag.plm.spi.interfaces.gui.PluginFunction;
import com.dscsag.plm.spi.interfaces.gui.PluginRequest;
import com.dscsag.plm.spi.interfaces.gui.PluginResponse;
import com.dscsag.plm.spi.interfaces.gui.PluginResponseFactory;
import com.dscsag.plm.spi.interfaces.objects.PlmObjectKey;
import com.dscsag.plm.spi.interfaces.rfc.RfcCall;
import com.dscsag.plm.spi.interfaces.rfc.RfcExecutor;
import com.dscsag.plm.spi.interfaces.rfc.RfcResult;
import com.dscsag.plm.spi.interfaces.rfc.RfcTable;
import com.dscsag.plm.spi.rfc.builder.RfcCallBuilder;
import com.dscsag.plm.spi.rfc.builder.RfcStructureBuilder;

/**
 * function which reads all versions from sap and logs them in debug log
 */
public class LogAllDocVersionsPluginFunction implements PluginFunction
{
  private ECTRService ectrService;

  public LogAllDocVersionsPluginFunction(ECTRService ectrService)
  {
    this.ectrService = ectrService;
  }

  @Override
  public PluginResponse actionPerformed(PluginRequest request)
  {
    PluginResponse pr = null;

    if(!request.getObjects().isEmpty())
    {
      PlmObjectKey object = request.getObjects().get(0);// get first document
      if ("DRAW".equals(object.getType()))
        handleDocument(object.getKey());
      else
        pr = PluginResponseFactory.warningResponse("only documents supported - please select one document");
    }
    else
      pr = PluginResponseFactory.warningResponse("please select one document");
    return pr;
  }

  private void handleDocument(String key)
  {
    String dokar = key.substring(0, 3);
    String doknr = key.substring(3, 28);
    String dokvr = key.substring(28, 30);
    String doktl = key.substring(30);
    
    RfcCallBuilder rfcCallBuilder = new RfcCallBuilder("/DSCSAG/DOC_VERSION_GET_ALL3");
    RfcStructureBuilder rfcStructureBuilder = new RfcStructureBuilder("DOCUMENTTYPE", "DOCUMENTNUMBER", "DOCUMENTVERSION", "DOCUMENTPART");
    rfcStructureBuilder.setValue("DOCUMENTTYPE", dokar);
    rfcStructureBuilder.setValue("DOCUMENTNUMBER", doknr);
    rfcStructureBuilder.setValue("DOCUMENTVERSION", dokvr);
    rfcStructureBuilder.setValue("DOCUMENTPART", doktl);

    rfcCallBuilder.setInputStructure("DOCUMENTKEY", rfcStructureBuilder.toRfcStructure());

    RfcCall rfcCall = rfcCallBuilder.toRfcCall();

    RfcExecutor rfcExecutor = ectrService.getRfcExecutor();
    RfcResult result = rfcExecutor.execute(rfcCall);

    RfcTable table = result.getTable("OUT_DOCUMENT");

    for (int i = 0; i < table.getRowCount(); i++)
    {
      ectrService.getPlmLogger().debug("DOC: " + table.getRow(i).getFieldValue("DOKVR") + " - " + table.getRow(i).getFieldValue("DKTXT"));
    }
  }
}