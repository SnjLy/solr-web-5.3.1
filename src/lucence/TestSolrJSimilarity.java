package lucence;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.search.FilteredQuery;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.handler.component.QueryComponent;
import org.apache.solr.handler.component.SearchHandler;

public class TestSolrJSimilarity {
	private static Log log=LogFactory.getLog(TestSolrJSimilarity.class);
	private QueryComponent qc;
	private SearchHandler sh;
	private FilteredQuery filter;
	//private SortedIntDocSetTopFilter sdt;
	public static void main(String[] args) {
		DefaultSimilarity ds=new DefaultSimilarity();
		String url = "http://172.16.254.28:8080/solr/item";
		  /*
		    HttpSolrServer is thread-safe and if you are using the following constructor,
		    you *MUST* re-use the same instance for all requests.  If instances are created on
		    the fly, it can cause a connection leak. The recommended practice is to keep a
		    static instance of HttpSolrServer per solr server url and share it for all requests.
		    See https://issues.apache.org/jira/browse/SOLR-861 for more details
		  */
		
		
		SolrClient server = new HttpSolrClient(url);
		
		
		
		/*SolrInputDocument sid=new SolrInputDocument();
		sid.addField("id", "1");
		List<String> name1=new ArrayList<String>();
		name1.add("中国");
		name1.add("zhong guo");
		name1.add("z g");
		name1.add("zh g");
		sid.addField("name",name1);		
		

		SolrInputDocument sid2=new SolrInputDocument();
		sid2.addField("id", "2");
		List<String> name2=new ArrayList<String>();
		name2.add("jone");
		name2.add("jone");
		sid2.addField("name", name2);		
		
		
		SolrInputDocument sid3=new SolrInputDocument();
		sid3.addField("id", "3");
		List<String> name3=new ArrayList<String>();
		name3.add("在国外");
		name3.add("zaiguowai");
		sid3.addField("name", name3);*/
		//updateSolrWithSetOrNo(server);		

		try {
			//UpdateResponse  rsp=server.deleteByQuery("id:\"01701c38a36140a09d5a268c1d622207\"");			
			/*server.add(sid);
			server.add(sid2);
			server.add(sid3);*/
			//server.commit();
			//System.out.println(getSuggestions(server,"中"));
	
			SolrQuery sq=new SolrQuery();
		
			sq.set("q", "name:\"杨\"");
			sq.set("fl", "name");
			sq.set("debugQuery",true);
			sq.setRows(1);
			QueryResponse qr=server.query(sq);
			System.out.println(qr.getExplainMap());
				
				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

}
