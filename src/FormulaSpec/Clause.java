package FormulaSpec;
import java.util.*;
/**
 * A clause is a collection of elementary formula of negation o them
 * @author Pablo
 *
 */
public class Clause implements Formula {
	private LinkedList<ElemFormula> pos;
	private LinkedList<ElemFormula> neg;
	
	public Clause(){
		this.pos = new LinkedList<ElemFormula>();
		this.neg = new LinkedList<ElemFormula>();
	}
	
	public void addPosElem(ElemFormula e){
		pos.add(e);
	}
	
	public void addNegElem(ElemFormula e){
		neg.add(e);
	}
	
	public void addAllPosElem(LinkedList<ElemFormula> list){
		pos.addAll(list);
	}
	
	public void addAllNegElem(LinkedList<ElemFormula> list){
		neg.addAll(list);
	}
	
	public LinkedList<ElemFormula> getPosElem(){
		return pos;
	}
	
	public LinkedList<ElemFormula> getNegElem(){
		return neg;
	}
	
	public void addClause(Clause c){
		pos.addAll(c.getPosElem());
		neg.addAll(c.getNegElem());
	}
	
	
	@Override	
	public void accept(FormulaVisitor visitor){
		 visitor.visit(this);		 
	}
	
	public String toAlloy(String metaName, String state){
		String result = "";
		for (int i = 0; i < pos.size(); i++){
			if (i==0)
				result = result + pos.get(i).toAlloy(metaName, state);
			else 
				result = result + " and (" + pos.get(i).toAlloy(metaName, state) + ")"; 
		}
		for (int i=0; i<neg.size(); i++){
			if (pos.size() > 0 || i > 0)
				result = result +" and (" + neg.get(i).toAlloy(metaName, state) + ")";
			else
				result = result +" (" + neg.get(i).toAlloy(metaName, state) + ")";
		}
		return result;
	}
	
	public String toString(){
		String result = "";
		for (int i=0; i< pos.size(); i++){
			if (i==0)
				result += pos.get(i).toString();
			else
				result += "&&" + pos.get(i).toString();
		}
		for (int i=0; i< neg.size(); i++){
			if (i==0 && result.equals(""))
				result += neg.get(i).toString();
			else
				result += "&&" + neg.get(i).toString();
		}
		return result;	
	}
}
