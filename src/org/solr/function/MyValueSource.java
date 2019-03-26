package org.solr.function;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.lucene.index.DocValues;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LeafReader;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.index.ReaderUtil;
import org.apache.lucene.index.SlowCompositeReaderWrapper;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.docvalues.IntDocValues;
import org.apache.lucene.search.SortedSetSelector;
import org.apache.lucene.util.mutable.MutableValue;
import org.apache.lucene.util.mutable.MutableValueInt;
import org.apache.solr.schema.SchemaField;
import org.apache.solr.search.Insanity;
import org.apache.solr.search.SolrIndexSearcher;


public class MyValueSource extends ValueSource {
	  protected final String field;

	  public MyValueSource(String field) {
	    this.field = field;
	  }

	  @Override
	  public String description() {
	    return "ord(" + field + ')';
	  }


	  @Override
	  public FunctionValues getValues(Map context, LeafReaderContext readerContext) throws IOException {
	    final int off = readerContext.docBase;
	    final LeafReader r;
	    Object o = context.get("searcher");
	    if (o instanceof SolrIndexSearcher) {
	      SolrIndexSearcher is = (SolrIndexSearcher) o;
	      SchemaField sf = is.getSchema().getFieldOrNull(field);
	      if (sf != null && sf.hasDocValues() == false && sf.multiValued() == false && sf.getType().getNumericType() != null) {
	        // it's a single-valued numeric field: we must currently create insanity :(
	        List<LeafReaderContext> leaves = is.getIndexReader().leaves();
	        LeafReader insaneLeaves[] = new LeafReader[leaves.size()];
	        int upto = 0;
	        for (LeafReaderContext raw : leaves) {
	          insaneLeaves[upto++] = Insanity.wrapInsanity(raw.reader(), field);
	        }
	        r = SlowCompositeReaderWrapper.wrap(new MultiReader(insaneLeaves));
	      } else {
	        // reuse ordinalmap
	        r = ((SolrIndexSearcher)o).getLeafReader();
	      }
	    } else {
	      IndexReader topReader = ReaderUtil.getTopLevelContext(readerContext).reader();
	      r = SlowCompositeReaderWrapper.wrap(topReader);
	    }
	    // if it's e.g. tokenized/multivalued, emulate old behavior of single-valued fc
	    final SortedDocValues sindex = SortedSetSelector.wrap(DocValues.getSortedSet(r, field), SortedSetSelector.Type.MIN);
	    return new IntDocValues(this) {
	      protected String toTerm(String readableValue) {
	        return readableValue;
	      }
	      @Override
	      public int intVal(int doc) {
	        return sindex.getOrd(doc+off);
	      }
	      @Override
	      public int ordVal(int doc) {
	        return sindex.getOrd(doc+off);
	      }
	      @Override
	      public int numOrd() {
	        return sindex.getValueCount();
	      }

	      @Override
	      public boolean exists(int doc) {
	        return sindex.getOrd(doc+off) != 0;
	      }

	      @Override
	      public ValueFiller getValueFiller() {
	        return new ValueFiller() {
	          private final MutableValueInt mval = new MutableValueInt();

	          @Override
	          public MutableValue getValue() {
	            return mval;
	          }

	          @Override
	          public void fillValue(int doc) {
	            mval.value = sindex.getOrd(doc);
	            mval.exists = mval.value!=0;
	          }
	        };
	      }
	    };
	  }

	  @Override
	  public boolean equals(Object o) {
	    return o != null && o.getClass() == MyValueSource.class && this.field.equals(((MyValueSource)o).field);
	  }

	  private static final int hcode = MyValueSource.class.hashCode();
	  @Override
	  public int hashCode() {
	    return hcode + field.hashCode();
	  }

}
