package FormulaSpec;

import java.util.LinkedList;

public class Disjunction extends TemporalFormula {
	
	public Disjunction(Formula e1, Formula e2){
		super(e1,e2);
	}
	
	@Override
	public void accept(FormulaVisitor v){
		v.visit(this);		
	}
	
	public String toAlloy(String metaName, String state){
		String result = "("+this.getExpr1().toAlloy(metaName, state)+")" + " or " + "("+this.getExpr2().toAlloy(metaName, state)+")";
		return result;
	}
	
	public boolean usesVar(String name){
		return (this.getExpr1().usesVar(name) || this.getExpr2().usesVar(name));
	}
	
	public String getAuxPred(String modelName){
		String result = "";
		return result;
	}
	
	public String toString(){
		//System.out.println(this.getExpr1());
		//System.out.println(this.getExpr2());
		return this.getExpr1().toString() + " | " + this.getExpr2().toString(); 
	}
	
	public LinkedList<String> generatePreds(String modelName){
    	LinkedList<String> result = new LinkedList<String>();
    	if (this.getExpr1() instanceof TemporalFormula)
			result.addAll(((TemporalFormula)this.getExpr1()).generatePreds(modelName));
		if (this.getExpr2() instanceof TemporalFormula)
			result.addAll(((TemporalFormula)this.getExpr2()).generatePreds(modelName));
    	return result;
    }
	
	public Formula removeVarOwnedBy(LinkedList<String> instances){
		return new Disjunction(this.getExpr1().removeVarOwnedBy(instances), this.getExpr2().removeVarOwnedBy(instances));
	}
	
	public boolean containsVarOwnedBy(LinkedList<String> instances){
		return this.getExpr1().containsVarOwnedBy(instances) || this.getExpr2().containsVarOwnedBy(instances);
	}

}
