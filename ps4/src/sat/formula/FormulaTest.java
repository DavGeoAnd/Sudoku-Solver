package sat.formula;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.Test;

import sat.env.Variable;

public class FormulaTest {    
    Literal a = PosLiteral.make("a");
    Literal b = PosLiteral.make("b");
    Literal c = PosLiteral.make("c");
    Literal d = PosLiteral.make("d");
    Literal nd = d.getNegation();
    Literal e = PosLiteral.make("e");
    Literal f = PosLiteral.make("f");
    Literal g = PosLiteral.make("g");
    Literal p = PosLiteral.make("p");
    Literal q = PosLiteral.make("q");
    Literal r = PosLiteral.make("r");
    Literal na = a.getNegation();
    Literal nb = b.getNegation();
    Literal nc = c.getNegation();

    // make sure assertions are turned on!  
    // we don't want to run test cases without assertions too.
    // see the handout to find out how to turn them on.
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false;
    }
    @Test
    public void testNot() {
    	//(a | b) & c ==> !((a | b) & c) 
    	//            ==> (!a & !b) | !c 
    	//            ==> (!a | !c) & (!b | !c)
    	Clause clause1 = make(b,a);
    	Clause clause2 = make(c);
    	Formula form1 = new Formula(clause1);
    	form1 = form1.addClause(clause2);
    	Formula not = form1.not();
    	assertEquals(not.toString(),"Problem[\nClause[~c, ~b]\nClause[~c, ~a]]");
    	
    	// (a | b) & (c | !d | e)  & (f |  g) ==> !((a | b) & (c | !d | e)  & (f |  g))
    	// => [ (!a | !c| ! f) & (!a| !c | !g) &  (!b | !c | !f) &  (!b | !c | !g) & (!a| d|!f) & (!a| d|!g) & 
    	//    (!b | d| !f) & (!b | d| !g) & (!a | !e|!f) & (!a | !e|!g) & (!b | !e| !f) & (!b|!e| !g) ]
    	Clause c1 = make(a,b);
    	Clause c2 = make(c,nd,e);
    	Clause c3 = make(f,g);
    	Formula form2 = new Formula(c1);
    	form2 = form2.addClause(c2);
    	form2 = form2.addClause(c3);
    	Formula not2 = form2.not();
    	
    	assertEquals(not2.toString(),"Problem[\n"+
		"Clause[~f, ~e, ~b]\n"+
		"Clause[~g, ~e, ~b]\n"+
		"Clause[~f, d, ~b]\n"+
		"Clause[~g, d, ~b]\n"+
		"Clause[~f, ~c, ~b]\n"+
		"Clause[~g, ~c, ~b]\n"+
		"Clause[~f, ~e, ~a]\n"+
		"Clause[~g, ~e, ~a]\n"+
		"Clause[~f, d, ~a]\n"+
		"Clause[~g, d, ~a]\n"+
		"Clause[~f, ~c, ~a]\n"+
		"Clause[~g, ~c, ~a]]");
    }
    
    @Test
    public void testOr() {
    	//(a & b) or (c & d) ==> (a | c) & (a | d) & (b | c) & (b | d)
    	Clause clause1 = make(b);
    	Clause clause2 = make(a);
    	Clause clause3 = make(d);
    	Clause clause4 = make(c);
    	Formula form = new Formula(clause2);
    	form = form.addClause(clause1);
    	
    	Formula form2 = new Formula(clause4);
    	form2 = form2.addClause(clause3);
    	
    	Formula form3 = form.or(form2);
    	assertEquals(form3.toString(),"Problem[\nClause[c, a]\nClause[d, a]\nClause[c, b]\nClause[d, b]]");
    	
    }
    @Test
    public void testAnd() {
    	Clause clause1 = make(c,b,a);
    	Clause clause2 = make(nc,nb,na);
    	Clause clause3 = make(r,q,p);
    	Formula form1 = new Formula(clause1);
    	Formula form2 = new Formula(clause2);
    	Formula form3 = new Formula(clause3);
    	form1 = form1.and(form2);
    	assertEquals(form1.getSize(),2);
    	assertEquals(form1.toString(),"Problem[\nClause[~a, ~b, ~c]\nClause[a, b, c]]");
    	form1 = form1.and(form3);
    	assertEquals(form1.getSize(),3);
    	assertEquals(form1.toString(),"Problem[\nClause[p, q, r]\nClause[~a, ~b, ~c]\nClause[a, b, c]]");
    }
    // TODO: put your test cases here
    @Test
    public void testGetSize() {
    	Clause clause1 = new Clause(a);
    	Clause clause2 = new Clause(b);
    	Clause clause3 = new Clause(c);
    	Formula emptyform = new Formula();
    	assertEquals(emptyform.getSize(),0);
    	
    	Formula form = new Formula(clause1);
    	form = form.addClause(clause2);
    	form = form.addClause(clause3);
    	assertEquals(form.getSize(),3);    	
    }
    @Test
    public void testIterator() {
    	Clause clause1 = new Clause(a);
    	Clause clause2 = new Clause(b);
    	Clause clause3 = new Clause(c);
    	Clause clause4 = make(c,b,a);
    	Clause clause5 = make(nc,nb,na);
    	Formula form = new Formula(clause1);
    	form = form.addClause(clause2);
    	form = form.addClause(clause3);
    	form = form.addClause(clause4);
    	form = form.addClause(clause5);
    	Iterator<Clause> iter = form.iterator();
    	String[] array = 
    	{"Clause[~a, ~b, ~c]",
    	 "Clause[a, b, c]",
    	 "Clause[c]",
    	 "Clause[b]",
    	 "Clause[a]"};
    	
    	int i = 0;
    	while(iter.hasNext()) {
  		  Clause c = iter.next();
  		  if(form.getClauses().contains(c)) {
  			 assertEquals(c.toString(),array[i]);
  			 i++;
  		  }
  		  else {
  			  fail();
  		  }
    	}
    }
    @Test
    public void testGetClauses() {
    	Clause clause1 = new Clause(c);
    	Clause clause2 = new Clause(b);
    	Clause clause3 = new Clause(a);
        Clause clause4 = make(c,b,a);
    	Formula form = new Formula(clause1);
    	form = form.addClause(clause2);
    	form = form.addClause(clause3);
    	form = form.addClause(clause4);
    	assertEquals(form.getClauses().toString(),"[Clause[a, b, c], Clause[a], Clause[b], Clause[c]]");
    }
    @Test
    public void testAddClause() {
    	Clause clause1 = new Clause(c);
    	Clause clause2 = new Clause(b);
    	Clause clause3 = new Clause(a);
        
    	Formula form = new Formula(clause1);
    	assertEquals(form.getSize(),1);
    	assertEquals(form.getClauses().toString(),"[Clause[c]]");
    	
    	form = form.addClause(clause2);
    	assertEquals(form.getSize(),2);	
    	assertEquals(form.getClauses().toString(),"[Clause[b], Clause[c]]");
    	
    	form = form.addClause(clause3);
    	assertEquals(form.getSize(),3);    	
    	assertEquals(form.getClauses().toString(),"[Clause[a], Clause[b], Clause[c]]");
        
    }
    //testFormula includes tests for 3 Formula constructors:
    //Formula()
    //Formula(Variable l)
    //Formula(Clause c)
    @Test
    public void testFormula() {
    	Formula form = new Formula();
    	assertEquals(form.getSize(),0);
    	assertEquals(form.toString(),"Problem[]");
    	
    	Variable v = new Variable("q");
    	Formula form2 = new Formula(v);
    	assertEquals(form2.getSize(),1);
    	assertEquals(form2.toString(),"Problem[\nClause[q]]");
    	
    	Clause clause = make(c,b,a);
    	Formula form3 = new Formula(clause);
    	assertEquals(form3.getSize(),1);
    	//System.out.println(form3.toString());
    	assertEquals(form3.toString(),"Problem[\nClause[a, b, c]]");
    	assertEquals(form3.getClauses().toString(),"[Clause[a, b, c]]");
    }
    
    // Helper function for constructing a clause.  Takes
    // a variable number of arguments, e.g.
    //  clause(a, b, c) will make the clause (a or b or c)
    // @param e,...   literals in the clause
    // @return clause containing e,...
    private Clause make(Literal... e) {
        Clause c = new Clause();
        for (int i = 0; i < e.length; ++i) {
            c = c.add(e[i]);
        }
        return c;
    }
}