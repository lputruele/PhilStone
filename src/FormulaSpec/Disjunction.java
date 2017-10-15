package FormulaSpec;

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
	
	public String toString(){
		return this.getExpr1().toString() + "||" + this.getExpr2().toString(); 
	}

}
