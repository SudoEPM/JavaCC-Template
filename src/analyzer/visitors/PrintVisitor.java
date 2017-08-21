package analyzer.visitors;

import analyzer.ast.*;

import java.io.PrintWriter;

/**
 * Created: 17-08-02
 * Last Changed: 17-08-02
 * Author: Nicolas Cloutier
 *
 * Description: This visitor explore the AST
 * and "pretty" print the code.
 */

public class PrintVisitor implements ParserVisitor {

    private final PrintWriter m_writer;

    private String m_indent;

    public PrintVisitor(PrintWriter writer) {
        m_writer = writer;
        m_indent = "";
    }

    @Override
    public Object visit(SimpleNode node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTProgram node, Object data) {
        // Production: Program -> Block
        node.jjtGetChild(0).jjtAccept(this, null);
        return null;
    }

    @Override
    public Object visit(ASTBlock node, Object data) {
        // Production: Block -> Stmt*
        node.childrenAccept(this, null);
        return null;
    }

    @Override
    public Object visit(ASTStmt node, Object data) {
        // Production: Stmt -> AssignStmt ;
        if(node.jjtGetChild(0).getClass() == ASTAssignStmt.class) {
            node.jjtGetChild(0).jjtAccept(this, null);
            return null;
        }

        // Production: Stmt -> WhileStmt
        else if(node.jjtGetChild(0).getClass() == ASTWhileStmt.class) {
            node.jjtGetChild(0).jjtAccept(this, null);
            return null;
        }

        // Production: Stmt -> IfStmt
        else if(node.jjtGetChild(0).getClass() == ASTIfStmt.class) {
            node.jjtGetChild(0).jjtAccept(this, null);
            return null;
        }

        // Production: Stmt -> IOStmt ;
        else if(node.jjtGetChild(0).getClass() == ASTIOStmt.class) {
            node.jjtGetChild(0).jjtAccept(this, null);
            return null;
        }
        else {
            throw new RuntimeException("Unexpected child");
        }
    }

    @Override
    public Object visit(ASTAssignStmt node, Object data) {
        // Production: AssignStmt -> (Id AssignOp)+ Expr
        m_writer.print(m_indent);
        node.jjtGetChild(0).jjtAccept(this, null);
        for(int i = 1; i < node.jjtGetNumChildren(); i++) {
            m_writer.print(" " + node.getOps().get(i-1) + " ");
            node.jjtGetChild(i).jjtAccept(this, null);
        }
        m_writer.println(";");
        return null;
    }

    @Override
    public Object visit(ASTWhileStmt node, Object data) {
        // Production: WhileStmt -> while( Expr ) { Block }
        m_writer.print(m_indent + "while(");
        node.jjtGetChild(0).jjtAccept(this, null);
        m_writer.println(") {");

        m_indent = m_indent + "    ";
        node.jjtGetChild(1).jjtAccept(this, null);
        m_indent = m_indent.substring(0, m_indent.length() - 4);

        m_writer.println(m_indent + "}");
        return null;
    }

    @Override
    public Object visit(ASTIfStmt node, Object data) {
        // Production: IfStmt -> if( Expr ) { Block }
        if(node.jjtGetNumChildren() == 2) {
            m_writer.print(m_indent + "if(");
            node.jjtGetChild(0).jjtAccept(this, null);
            m_writer.println(") {");

            m_indent = m_indent + "    ";
            node.jjtGetChild(1).jjtAccept(this, null);
            m_indent = m_indent.substring(0, m_indent.length() - 4);

            m_writer.println(m_indent + "}");
            return null;
        }
        // Production: IfStmt -> if( Expr ) { Block } else { Block }
        else {
            m_writer.print(m_indent + "if(");
            node.jjtGetChild(0).jjtAccept(this, null);
            m_writer.println(") {");

            m_indent = m_indent + "    ";
            node.jjtGetChild(1).jjtAccept(this, null);
            m_indent = m_indent.substring(0, m_indent.length() - 4);

            m_writer.print(m_indent + "}");

            m_writer.println(" else {");

            m_indent = m_indent + "    ";
            node.jjtGetChild(2).jjtAccept(this, null);
            m_indent = m_indent.substring(0, m_indent.length() - 4);

            m_writer.println(m_indent + "}");
            return null;
        }
    }

    @Override
    public Object visit(ASTIOStmt node, Object data) {
        // Production: IOStmt -> Print( Expr )
        if(node.getOp() == "print") {
            m_writer.print(m_indent + node.getOp() + "(");
            node.jjtGetChild(0).jjtAccept(this, null);
            m_writer.println(");");
            return null;
        }
        // Production: IOStmt -> Input( Id )
        else {
            m_writer.print(m_indent + node.getOp() + "(");
            node.jjtGetChild(0).jjtAccept(this, null);
            m_writer.println(");");
            return null;
        }
    }

    @Override
    public Object visit(ASTExpr node, Object data) {
        // Production: Expr -> AddExpr
        node.jjtGetChild(0).jjtAccept(this, null);
        return null;
    }

    @Override
    public Object visit(ASTAddExpr node, Object data) {
        // Production: AddExpr -> MultExpr ( Op MultExpr )*
        node.jjtGetChild(0).jjtAccept(this, null);
        for(int i = 1; i < node.jjtGetNumChildren(); i++) {
            m_writer.print(" " + node.getOps().get(i - 1) + " ");
            node.jjtGetChild(i).jjtAccept(this, null);
        }
        return null;
    }

    @Override
    public Object visit(ASTMultExpr node, Object data) {
        // Production: MultExpr -> PowExpr ( Op PowExpr )*
        node.jjtGetChild(0).jjtAccept(this, null);
        for(int i = 1; i < node.jjtGetNumChildren(); i++) {
            m_writer.print(" " + node.getOps().get(i - 1) + " ");
            node.jjtGetChild(i).jjtAccept(this, null);
        }
        return null;
    }

    @Override
    public Object visit(ASTPowExpr node, Object data) {
        // Production: PowExpr -> NegExpr ( Op NegExpr )*
        node.jjtGetChild(0).jjtAccept(this, null);
        for(int i = 1; i < node.jjtGetNumChildren(); i++) {
            m_writer.print(" ^ ");
            node.jjtGetChild(i).jjtAccept(this, null);
        }
        return null;
    }

    @Override
    public Object visit(ASTNegExpr node, Object data) {
        // Production: NegExpr -> - BasicExpr
        if(node.isNeg()) {
            m_writer.print("-");
            node.jjtGetChild(0).jjtAccept(this, null);
            return null;
        }
        // Production: NegExpr -> BasicExpr
        else {
            node.jjtGetChild(0).jjtAccept(this, null);
            return null;
        }
    }

    @Override
    public Object visit(ASTBasicExpr node, Object data) {
        // Production: BasicExpr -> FctExpr
        if(node.jjtGetChild(0).getClass() == ASTFctExpr.class) {
            node.jjtGetChild(0).jjtAccept(this, null);
            return null;
        }
        // Production: BasicExpr -> Id
        else if(node.jjtGetChild(0).getClass() == ASTIdentifier.class) {
            node.jjtGetChild(0).jjtAccept(this, null);
            return null;
        }
        // Production: BasicExpr -> Int
        else if(node.jjtGetChild(0).getClass() == ASTIntValue.class) {
            node.jjtGetChild(0).jjtAccept(this, null);
            return null;
        }
        // Production: BasicExpr -> Real
        else if(node.jjtGetChild(0).getClass() == ASTRealValue.class) {
            node.jjtGetChild(0).jjtAccept(this, null);
            return null;
        }
        // Production: BasicExpr -> ( Expr )
        else if(node.jjtGetChild(0).getClass() == ASTExpr.class) {
            m_writer.print("(");
            node.jjtGetChild(0).jjtAccept(this, null);
            m_writer.print(")");
            return null;
        }
        else {
            throw new RuntimeException("Unexpected child");
        }
    }

    @Override
    public Object visit(ASTFctExpr node, Object data) {
        // Production: FctExpt -> Id ( Expr )
        if(node.jjtGetNumChildren() == 1) {
            m_writer.print(node.getValue() + "(");
            node.jjtGetChild(0).jjtAccept(this, null);
            m_writer.print(")");
            return null;
        }
        // Production: FctExpt -> Id ( Expr, Expr )
        else {
            m_writer.print(node.getValue() + "(");
            node.jjtGetChild(0).jjtAccept(this, null);
            m_writer.print(", ");
            node.jjtGetChild(1).jjtAccept(this, null);
            m_writer.print(")");
            return null;
        }
    }

    @Override
    public Object visit(ASTIdentifier node, Object data) {
        // Production: Id -> letter ( letter | digit )*
        m_writer.print(node.getValue());
        return null;
    }

    @Override
    public Object visit(ASTIntValue node, Object data) {
        // Production: Int -> integer
        m_writer.print(node.getValue());
        return null;
    }

    @Override
    public Object visit(ASTRealValue node, Object data) {
        // Production: Real -> double
        m_writer.print(node.getValue());
        return null;
    }
}
