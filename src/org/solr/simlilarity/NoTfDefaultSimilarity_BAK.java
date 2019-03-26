package org.solr.simlilarity;

import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.util.SmallFloat;
import org.apache.solr.search.similarities.DefaultSimilarityFactory;



public class NoTfDefaultSimilarity_BAK extends DefaultSimilarity{

	
	@Override
	public float tf(float freq) {
		// TODO Auto-generated method stub
		//return super.tf(freq);
		return 1.0f;
	}
	
	@Override
	  public float queryNorm(float sumOfSquaredWeights) {
	    return super.queryNorm(sumOfSquaredWeights);
	  }
	@Override  
	 public float lengthNorm(FieldInvertState state) {
		return super.lengthNorm(state);
	}
	
	
	  

}
