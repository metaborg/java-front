package spxjava;

import org.strategoxt.imp.runtime.dynamicloading.Descriptor;
import org.strategoxt.imp.runtime.services.MetaFileLanguageValidator;

public class JavaValidator extends MetaFileLanguageValidator
{
  @Override public Descriptor getDescriptor()
  {
    return JavaParseController.getDescriptor();
  }
}