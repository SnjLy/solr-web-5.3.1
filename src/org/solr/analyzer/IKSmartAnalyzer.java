package org.solr.analyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;
import org.wltea.analyzer.lucene.IKTokenizer;

public class IKSmartAnalyzer extends Analyzer{
	
	private boolean useSmart;
	
	public boolean useSmart() {
		return useSmart;
	}

	public void setUseSmart(boolean useSmart) {
		this.useSmart = useSmart;
	}

	/**
	 * IK分词器Lucene  Analyzer接口实现类
	 * 
	 * 默认细粒度切分算法
	 */
	public IKSmartAnalyzer(){
		this(true);
	}
	
	/**
	 * IK分词器Lucene Analyzer接口实现类
	 * 
	 * @param useSmart 当为true时，分词器进行智能切分
	 */
	public IKSmartAnalyzer(boolean useSmart){
		super();
		this.useSmart = useSmart;
	}

	/**
	 * 重载Analyzer接口，构造分词组件
	 */
	@Override
	protected TokenStreamComponents createComponents(String fieldName) {
		Tokenizer _IKTokenizer = new IKTokenizer(this.useSmart());
		return new TokenStreamComponents(_IKTokenizer);
	}
}
