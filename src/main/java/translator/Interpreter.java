package translator;

import representation2.GeneratorData;

/**
 * Created by user on 1/5/17.
 */
public class Interpreter {
    private GeneratorData generatorData;
    Parser parser;

    public Interpreter(Parser parser) {
        this.parser = parser;
        this.generatorData = new GeneratorData();
    }

    public GeneratorData getGeneratorData() {
        return generatorData;
    }

    public void setGeneratorData(GeneratorData generatorData) {
        this.generatorData = generatorData;
    }

    public String visit(AST node) {
        if (node instanceof ASTExpression) {
            return visitExpression((ASTExpression) node);
        } else if (node instanceof ASTNonermDef) {
            return visitNonermDef((ASTNonermDef) node);
        } else if (node instanceof ASTNonterm) {
            return visitNonterm((ASTNonterm) node);
        } else if (node instanceof ASTOr) {
            return visitOr((ASTOr) node);
        } else if (node instanceof ASTProgram) {
            return visitProgram((ASTProgram) node);
        } else if (node instanceof ASTQuoted) {
            return visitQuoted((ASTQuoted) node);
        } else if (node instanceof ASTRepeat) {
            return visitRepeat((ASTRepeat) node);
        } else if (node instanceof ASTTerm) {
            return visitTerm((ASTTerm) node);
        } else if (node instanceof ASTTermDef) {
            return visitTermDef((ASTTermDef) node);
        } else {
            return "";
        }
    }

    public String visitExpression(ASTExpression node) {
        String result =  "ASTExpression:\n";
        for (AST child : node.childs) {
            result += "            " + visit(child);
        }
        return result;
    }

    public String visitNonermDef(ASTNonermDef node) {
        generatorData.getNonterminals().add(node.name);
        String result = "ASTNontermDef:\n        " + visit(node.expr);
        return result;
    }

    public String visitNonterm(ASTNonterm node) {
        return "ASTNonterm\n";
    }

    public String visitOr(ASTOr node) {
        return "ASTOr\n";
    }

    public String visitProgram(ASTProgram node) {
        String result = "ASTProgram:\n";
        for (AST child : node.childs) {
            result += "    " + visit(child);
        }
        return result;
    }

    public String visitQuoted(ASTQuoted node) {
        return "ASTQuoted\n";
    }

    public String visitRepeat(ASTRepeat node) {
        return "ASTRepeat\n";
    }

    public String visitTerm(ASTTerm node) {
        return "ASTTerm\n";
    }

    public String visitTermDef(ASTTermDef node) {
        generatorData.getTerminals().add(node.head);
        String result = "ASTTermDef:\n        " + visit(node.expr);
        return result;
    }

    public String interpret() throws Exception {
        AST tree = parser.parse();
        if (tree == null) {
            return "";
        }
        return visit(tree);
    }
}
