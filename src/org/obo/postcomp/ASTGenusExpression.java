/* Generated By:JJTree: Do not edit this line. ASTGenusExpression.java */

package org.obo.postcomp;

public class ASTGenusExpression extends SimpleNode {
  public ASTGenusExpression(int id) {
    super(id);
  }

  public ASTGenusExpression(OBOPostcomp p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(OBOPostcompVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}