package Parser;

import java.util.*;

import FormulaSpec.*;


public class ExprAux {
	Operator operator; // the operator SUM, MULT, DIV, NEG, AND, OR, NOT, IMP, AU, EU, AX, EX,  
	ExprAux op1; // operator 1
	ExprAux op2; // operator 2
	boolean bval; // in the case it is a constant
	int ival;
	String name; // in the case it is a var
	int line; // the line where the expression was defined
	String error; // for keeping an error message 
	String owner; // in the case of variables, they have an owner
	String unqualifiedName; //the name of the var without the owner
	
	/**
	 * Basic constructor, in the case of unary operators the second operators should be set to null
	 * @param operator
	 * @param op1
	 * @param op2
	 * @param line
	 */
	public ExprAux(Operator operator, ExprAux op1, ExprAux op2, int line){
		this.operator = operator;
		this.op1 = op1;
		this.op2 = op2;
		ival = 0;
		bval = false;
		this.name = "";
		this.error = "";
		this.line = line;
		
	}
	
	/**
	 * Constructor for boolean constants
	 * @param val
	 */
	public ExprAux(boolean val, int line){
		this.operator = Operator.BCONS;
		this.op1 = null;
		this.op2 = null;
		ival = 0;
		bval = val;	
		this.name = "";
		this.error = "";
		this.line = line;
	}
	
	/**
	 * Constructor for int constants
	 * @param val
	 */
	public ExprAux(int val, int line){
		this.operator = Operator.ICONS;
		this.op1 = null;
		this.op2 = null;
		ival = val;
		bval = false;	
		this.name = "";
		this.error = "";
		this.line = line;
	}
	
	/**
	 * Constructor for vars
	 * @param val
	 */
	public ExprAux(String name, int line){
		this.operator = Operator.VAR;
		this.op1 = null;
		this.op2 = null;
		ival = 0;
		bval = false;
		this.name = name;
		this.error = "";
		this.line = line;
	}
	
		

	public int getLine(){
		return line;
	}
	
	public void setLine(int line){
		this.line = line;
	}
	
	public String getError(){
		return this.error;
	}
	
	public void setError(String error){
		this.error = error;
	}
	
	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public ExprAux getOp1() {
		return op1;
	}

	public void setOp1(ExprAux op1) {
		this.op1 = op1;
	}

	public ExprAux getOp2() {
		return op2;
	}

	public void setOp2(ExprAux op2) {
		this.op2 = op2;
	}
	
	public String getName(){
		return this.name;
	}
	
	
	public String getOwner(){
		return this.owner;
	}
	
	public void setOwner(String owner){
		this.owner = owner;
	}
	
	public void setUnqualifiedName(String name){
		this.unqualifiedName = name;
	}
	
	public String getUnqualifiedName(){
		return this.unqualifiedName;
	}
	
	public Expression getExpr(HashMap<String, Type> table){
		switch(operator){
		case VAR:if (table.get(this.name) == Type.INT){
					return new IntVar(this.name);
				 }
				 else{
					 return new BoolVar(this.name);
				 }
		case AV: 	return new Av((Var) op1.getExpr(table));
		case OWN: 	return new Own((Var) op1.getExpr(table));
		case ICONS: return new IntConstant(this.ival);
		case BCONS: return new BoolConstant(this.bval);
		case EX:	return new EX((TemporalFormula) this.op1.getExpr(table));
		case AX:	return new AX((TemporalFormula) this.op1.getExpr(table));
		case NOT:	return new Negation((Formula) this.op1.getExpr(table));
		case OR:	return new Disjunction((Formula) this.op1.getExpr(table), (Formula) this.op2.getExpr(table));
		case IMP:	return new Implication((Formula) this.op1.getExpr(table), (Formula) this.op2.getExpr(table));
		case AU:	return new AU((TemporalFormula) this.op1.getExpr(table), (TemporalFormula) this.op2.getExpr(table));
		case EU:	return new EU((TemporalFormula) this.op1.getExpr(table), (TemporalFormula) this.op2.getExpr(table));
		case AW:	return new AW((TemporalFormula) this.op1.getExpr(table), (TemporalFormula) this.op2.getExpr(table));
		case EW:	return new EW((TemporalFormula) this.op1.getExpr(table), (TemporalFormula) this.op2.getExpr(table));
		case AND:	return new Conjunction((Formula) this.op1.getExpr(table), (Formula) this.op2.getExpr(table));
		case AG:	return new AG((Formula) this.op1.getExpr(table));
		case EG:	return new EG((Formula) this.op1.getExpr(table));
		case EF:	return new EF((Formula) this.op1.getExpr(table));
		case AF: 	return new AF((Formula) this.op1.getExpr(table));
		case MINUS: return new NegExpression((AritExpression) this.op1.getExpr(table));
		case MULT:  return new MultExpression((AritExpression) this.op1.getExpr(table), (AritExpression) this.op1.getExpr(table));
		case DIV:	return new DivExpression((AritExpression) this.op1.getExpr(table), (AritExpression) this.op1.getExpr(table));
		case SUM:	return new SumExpression((AritExpression) this.op1.getExpr(table), (AritExpression) this.op1.getExpr(table));				
		case EQ:	return new EqComparison((Expression) this.op1.getExpr(table), (Expression) this.op1.getExpr(table));			
		default: return null;
		}
	}
	
	/**
	 * @param context	the context where the expression is, it could be global or the name of a process
	 * @return	the type of the expression or an error type in any other case
	 */
	public Type getType(HashMap<String, Type> table, SpecAux mySpec, String context){
		switch(operator){
			case VAR:	if (context.equals("global")){ // if the var is invoqued in a global context
							if (this.owner.equals("this")){ // cannot use this in main
								this.error = "Incorrect use of keyword this, line: " + Integer.toString(line);
								return Type.ERROR;
							}
							if (this.owner.equals("global")){ // if it is qualified with "global"
								Type resultType = mySpec.getTypeVar(this.unqualifiedName, context); // look for a global var
								if (resultType != Type.ERROR){
									return resultType; // if no error, returns the type
								}
								else{
									this.error = "Undeclared global variable, line: " + Integer.toString(line);
									return Type.ERROR; // if there is an error returns the error
								}
							}
							else{ // otherwise is owned by an instance
								if (!mySpec.checkVarBelongInstance(this.unqualifiedName, this.owner)){ // check if the var is declared in the instance
									this.error = "Undeclared local variable, line: " + Integer.toString(line);
									return Type.ERROR;
								}
								else{
									return mySpec.getTypeVarFromInstance(this.unqualifiedName, this.owner); // if found return the type
								}
							}				
						}
						else{ // if the context is not global (that is it is in a process)
							if (this.owner.equals("global")){ // if it is qualified with "global"
								Type resultType = mySpec.getTypeVar(this.unqualifiedName, "global"); // look for a global var
								if (resultType != Type.ERROR){
									return resultType; // if no error type then return the type
								}
								else{
									this.error = "Undeclared global variable, line: " + Integer.toString(line);
									return Type.ERROR; // otherwise return the error
								}
							}
							if (this.owner.equals("this")){ // if the qualified name if this
								if (!mySpec.checkVarDeclaredInProcess(this.unqualifiedName, context)){ // check if the var is declared in the context
									this.error = "Undeclared local variable, line: " + Integer.toString(line);
									return Type.ERROR;
								}
								else{
									return mySpec.getTypeVar(this.unqualifiedName, context); // if declared, returns the correct type
								}
							}
							if (this.owner.equals("par")){ // if it is a parameter
								if (!mySpec.checkParDeclaredInProcess(this.unqualifiedName, context)){ // check if the var is declared in the context
									this.error = "Undeclared local variable, line: " + Integer.toString(line);
									return Type.ERROR;
								}
								else{
									return mySpec.getTypePar(this.unqualifiedName, context); // if declared, returns the correct type
								}
							}
							else{ // other variables cannot be called in a local setting
								this.error = "extern variables cannot be referenced from local context, line: " + Integer.toString(line);
								return Type.ERROR;
							}					
						}						
			case ICONS: return Type.INT;
			case BCONS: return Type.BOOL;
			case AV:
			case OWN: 	
						if(op1.getOperator() == Operator.VAR){
							// If it is a global variable, then true
							if (mySpec.isLock(op1.getUnqualifiedName())){						
								return Type.BOOL;
							}
							if (mySpec.isParameter(op1.getUnqualifiedName(),op1.getOwner())){			
								return Type.BOOL;
							}
							if (mySpec.getProcessByName(context).containsPar(op1.getUnqualifiedName())){
								return Type.BOOL;
							}
							else{
								this.error = "Lock over a local variable, line: " + Integer.toString(line);
								return Type.ERROR;
							}
						}
						else{ 
							this.error = "Lock over a local variable, line: " + Integer.toString(line);
							return Type.ERROR;
						}
			case EX:
			case AX:
			case AG:
			case EG:
			case EF:
			case AF:
			case NOT:	if (op1 == null){
							this.error = "Type Error in Boolean Expression, line: " + Integer.toString(line);
							return Type.ERROR;
					  	}
						else{
							if (op1.getType(table, mySpec, context) == Type.BOOL){
								return Type.BOOL;
							}
							else{
								if (op1.getType(table, mySpec, context) == Type.ERROR){
									this.error = op1.getError();
									return Type.ERROR;
								}	
								this.error = "Type Error in Boolean Expression, line: " + Integer.toString(line); 
								return Type.ERROR;
							}
						}
			case OR:
			case IMP:
			case AU:
			case EU:
			case AW:
			case EW:
			case AND:	if (op1 == null || op2 == null){
							this.error = "Type Error in Boolean Expression, line: " + Integer.toString(line);
							return Type.ERROR;
						}
						else{
							if ((op1.getType(table, mySpec, context) == Type.BOOL) && (op2.getType(table, mySpec, context) == Type.BOOL)){
								return Type.BOOL;
							}
							else{
								if (op1.getType(table, mySpec, context) == Type.ERROR){
									this.error = op1.getError();
									return Type.ERROR;
								}
								if (op2.getType(table, mySpec, context) == Type.ERROR){
									this.error = op2.getError();
									return Type.ERROR;
								}
								this.error = "Type Error in Boolean Expression, line: " + Integer.toString(line);
								return Type.ERROR;
							}
						}
			case MINUS:
			case MULT:
			case DIV:
			case SUM:	
						if (op1 == null || op2 == null){
							this.error = "Type Error in Aritmethic Expression, line: " + Integer.toString(line);
							return Type.ERROR;
						}
						else{
							if ((op1.getType(table, mySpec, context) == Type.INT) && (op2.getType(table, mySpec, context) == Type.INT)){
								return Type.INT;
							}
							else{
								this.error = "Type Error in Aritmethic Expression, line: " + Integer.toString(line);
								return Type.ERROR;
							}
						}
			case EQ:
					if (op1 != null && op2 !=null){
						Type t1 = op1.getType(table, mySpec, context);
						Type t2 = op2.getType(table, mySpec, context);
						if (t1 == Type.ERROR || t2 == Type.ERROR ){
							this.error = "Type Error in Equation, line: " + Integer.toString(line);
							return Type.ERROR;
						}
						else{
							if (t1 == t2){
								return Type.BOOL;				
							}
							else{
								this.error = "Type Error in Equation, line: " + Integer.toString(line);
								return Type.ERROR;
							}
						}
					}
					else{
						this.error = "Type Error in Equation, line: " + Integer.toString(line);
						return Type.ERROR;
					}
			default: return Type.ERROR;
		}
	}
	
	public boolean isDNF(HashMap<String,Type> table, SpecAux mySpec, String context){	
		if (this.getType(table, mySpec, context) != Type.BOOL){
			return false;
		}
		if (this.isClause(table, mySpec, context))
			return true;
		if (op1 != null && op2 != null)
			return (op1.isDNF(table, mySpec, context) && (operator == Operator.OR) && op2.isDNF(table, mySpec, context));
		else
			return false;
	}
	
	public boolean isClause(HashMap<String, Type> table, SpecAux mySpec, String context){
		if (this.getType(table, mySpec, context) != Type.BOOL){
			return false;
		}
		if (this.isElementary(table, mySpec, context))
			return true;
		if (op1 != null && op2 != null){
			return (op1.isClause(table, mySpec, context) && (operator == Operator.AND) && op2.isClause(table, mySpec, context));			
		}
		else
			return false;
	}
	
	public boolean isElementary(HashMap<String, Type> table, SpecAux mySpec, String context){
		if (this.getType(table, mySpec, context) != Type.BOOL){
			return false;
		}
		if (operator == Operator.VAR){
			return (mySpec.getTypeVar(this.unqualifiedName, context) == Type.BOOL);	
		}
		if (operator == Operator.BCONS || operator == Operator.AV || operator == Operator.OWN)
			return true;
		if (operator == Operator.NOT){
			return (op1.getOperator() == Operator.VAR) || (op1.getOperator() == Operator.EQ) || (op1.getOperator() == Operator.OWN) || (op1.getOperator() == Operator.AV);
		}
		if (operator == Operator.EQ){
			return true;
		}
		return false;	
	}
	
	public boolean isPropositional(){
		boolean isVar = (operator == Operator.VAR);
		boolean isCons =  (operator == Operator.BCONS);
		boolean isLock = (operator == Operator.AV) || (operator == Operator.OWN);
		boolean isOr = (operator == Operator.OR && (op1.isPropositional() && op2.isPropositional()));
		boolean isAnd = (operator == Operator.AND && (op1.isPropositional() && op2.isPropositional()));
		boolean isImp = (operator == Operator.IMP && (op1.isPropositional() && op2.isPropositional()));
		boolean isNot = (operator == Operator.NOT) && (op1.isPropositional());
		boolean isEQ = (operator == Operator.EQ);
		return (isVar || isCons || isOr || isAnd || isImp || isNot || isEQ || isLock);	
	}
	
	/**
	 * The expression must be in DNF
	 * @return the collection of clauses
	 */
	public LinkedList<Clause> getClauses(HashMap<String, Type> table, SpecAux mySpec, String context){
		LinkedList<Clause> result = new LinkedList<Clause>();
		if (!this.isDNF(table, mySpec, context)){
			throw new NotDNFException();
		}
		if ((operator == Operator.OR) && op1.isDNF(table, mySpec, context) && op2.isDNF(table, mySpec, context)){
			result.addAll(op1.getClauses(table, mySpec, context));
			result.addAll(op2.getClauses(table, mySpec, context));
		}
		// otherwise the expression is a clause
		if (operator != Operator.OR){
			Clause c = new Clause();
			c.addAllPosElem(this.getClausePos(table, mySpec, context)); // change for op1 for this
			c.addAllNegElem(this.getClauseNeg(table, mySpec, context));
			result.add(c);
		}
		//if ((operator != Operator.OR) && op2 != null){
		//	Clause c = new Clause();
		//	c.addAllPosElem(this.getClausePos(table, mySpec, context));
		//	c.addAllNegElem(op2.getClauseNeg(table, mySpec, context));
		//	result.add(c);
		//}
		return result;
	}
	
	public LinkedList<ElemFormula> getClausePos(HashMap<String, Type> table, SpecAux mySpec, String context){
		LinkedList<ElemFormula> result = new LinkedList<ElemFormula>();
		if (!this.isClause(table, mySpec, context)){
			throw new NotDNFException();
		}
		if (operator == Operator.AND){
			result.addAll(op1.getClausePos(table, mySpec, context));
			result.addAll(op2.getClausePos(table, mySpec, context));
		}
		if (operator == Operator.VAR || operator == Operator.EQ || operator == Operator.AV || operator == Operator.OWN){
			result.add((ElemFormula) this.getExpr(table));
		}
		// otherwise it is a negated formula
		return result;
	}
	
	public LinkedList<ElemFormula> getClauseNeg(HashMap<String, Type> table, SpecAux mySpec, String context){
		LinkedList<ElemFormula> result = new LinkedList<ElemFormula>();
		if (!this.isClause(table, mySpec, context)){
			throw new NotDNFException();
		}
		if (operator == Operator.AND){
			result.addAll(op1.getClauseNeg(table, mySpec, context));
			result.addAll(op2.getClauseNeg(table, mySpec, context));
		}
		if (operator == Operator.NOT){
			ElemNegation neg = new ElemNegation((ElemFormula) op1.getExpr(table));
			result.add(neg);
		}
		// otherwise it is a positive formula
		return result;
	}
	
	
	public String toString(){
		if (op1!=null && op2!=null)
			return operator.toString()+"("+ op1.toString() +")"+"("+ op2.toString() +")";
		if (op1!=null && op2==null)
			return operator.toString()+"("+op1.toString()+")";
		if (op1==null && op2==null)
			return operator.toString();
		return "";
	}
	
	/**
	 * 
	 * @return	true when the expression contains locks
	 */
	public boolean containsLock(){
		switch(operator){
		case VAR:	return false;
		case AV: 	
		case OWN:	return true;				
		case ICONS: 
		case BCONS: return false;
		case EX:	
		case AX:	
		case NOT:	return op1.containsLock();
		case OR:	
		case AU:	
		case EU:	
		case AW:	
		case EW:	
		case AND:	
		case MINUS: 
		case MULT:  
		case DIV:	
		case SUM:					
		case EQ: return (op1.containsLock() || op2.containsLock());				
		default: return false;
}
	}
	
	/**
	 * This function checks whether the accesses to variables are ok from a given process.
	 * @return	returns false when the woner are worng, the errors are saved in this.errors
	 */
	/*public boolean checkOwnersOK(ProcessAux process){
		switch(operator){
				case VAR:	if (this.owner.equals("this") || this.owner.equals("global")){
								return true;
							}
							else{
								this.error = this.error + "incorrect access to variable: "+ this.owner + this.name + ", line"+ this.line + "\n";
								return false;
							}
				case AV: 	
				case OWN:	if (op1.getOwner().equals("global")){
								return true;
							}
							else{
								this.error = this.error + "av and own can only applied to global vars, line"+ this.line;
								return false;
							}
							
				case ICONS: 
				case BCONS: return true;
				case EX:	
				case AX:	
				case NOT:	if (op1.checkOwnersOK(process)){
								return true;
							}
							else{
								this.error = this.error + op1.getError() + "\n";
								return false;
							}
				case OR:	
				case AU:	
				case EU:	
				case AW:	
				case EW:	
				case AND:	
				case MINUS: 
				case MULT:  
				case DIV:	
				case SUM:					
				case EQ:	if (op1.checkOwnersOK(process) && op2.checkOwnersOK(process)){
								return true;
							}
							else{
								this.error = this.error + op1.getError() + op2.getError() + "\n";
								return false;
							}			
				default: return true;
		}
	}
	
	public boolean checkOwnersOK(SpecAux spec){
		switch(operator){
				case VAR:	if (this.owner.equals("this")){
								this.error = this.error + "incorrect use of keyword this in global property, line"+ this.line + "\n";
								return false;
							}
							if (this.owner.equals("global")){
								return true;
							}
							if (this.owner.equals())
							else{
								this.error = this.error + "incorrect access to variable: "+ this.owner + this.name + ", line"+ this.line + "\n";
								return false;
							}
				case AV: 	
				case OWN:	if (op1.getOwner().equals("global")){
								return true;
							}
							else{
								this.error = this.error + "av and own can only applied to global vars, line"+ this.line;
								return false;
							}
							
				case ICONS: 
				case BCONS: return true;
				case EX:	
				case AX:	
				case NOT:	if (op1.checkOwnersOK(process)){
								return true;
							}
							else{
								this.error = this.error + op1.getError() + "\n";
								return false;
							}
				case OR:	
				case AU:	
				case EU:	
				case AW:	
				case EW:	
				case AND:	
				case MINUS: 
				case MULT:  
				case DIV:	
				case SUM:					
				case EQ:	if (op1.checkOwnersOK(spec) && op2.checkOwnersOK(spec)){
								return true;
							}
							else{
								this.error = this.error + op1.getError() + op2.getError() + "\n";
								return false;
							}			
				default: return true;
		}
	}*/
	
}
