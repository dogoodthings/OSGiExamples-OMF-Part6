package osgi;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.dscsag.plm.spi.interfaces.ECTRService;
import com.dscsag.plm.spi.interfaces.gui.PluginFunction;
import com.dscsag.plm.spi.interfaces.gui.PluginFunctionService;

import osgi.functions.LogAllDocVersionsPluginFunction;

/**
 * function manager which maps the function code to "real" plugin function
 *
 */
class PluginFunctionManager implements PluginFunctionService
{
  private final Map<String, Supplier<PluginFunction>> map;

  protected PluginFunctionManager(ECTRService service)
  {
    map = new HashMap<>();
    map.put("fnc.com.dscsag.log.all.doc.versions", () -> new LogAllDocVersionsPluginFunction(service));
  }

  @Override
  public PluginFunction getPluginFunction(String functionName)
  {
    Supplier<PluginFunction> supplier = map.get(functionName);
    if (supplier != null)
      return supplier.get();
    return null;
  }
  
}