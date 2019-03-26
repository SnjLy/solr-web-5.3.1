package org.solr.function;

import org.apache.http.ParseException;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.search.FunctionQParser;
import org.apache.solr.search.ValueSourceParser;

/**
 * solrconfig.xml<config>下配置
 *   <valueSourceParser name="myfunc" class="org.solr.function.MyValueSourceParser" />
 * @author dell
 *
 */
public class MyValueSourceParser extends ValueSourceParser {  
	  public void init(NamedList namedList) {  
	  }  
	  
	  public ValueSource parse(FunctionQParser fqp) throws ParseException {  
	    return new MyValueSource("artisan_level");  
	  } 

}
