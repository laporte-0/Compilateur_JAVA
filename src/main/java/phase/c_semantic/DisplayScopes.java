package phase.c_semantic;

import compil.util.Debug;
import compil.util.IndentWriter;
import phase.b_syntax.ast.AstMethod;
import phase.b_syntax.ast.AstVisitorDefault;
import phase.b_syntax.ast.Klass;
import phase.b_syntax.ast.StmtBlock;

/**
 * Deuxième exemple de Visiteur : détection des portées (scopes).
 * 
 * @author Denis Conan
 */
public class DisplayScopes extends AstVisitorDefault {
    /**
     * Le Writer pour impression.
     */
    protected final IndentWriter out = new IndentWriter();

    /**
     * Constructeur.
     * 
     * @param semTree l'arbre sémantique.
     */
    public DisplayScopes(final SemanticTree semTree) {
        out.print("= Affichage des portées : ");
        semTree.axiom().accept(this);
        Debug.log(out);
   }

    /////////////////// Visit ////////////////////
    @Override
    public void visit(final Klass n) {
        out.print(n.klassId().name() + "{");
        defaultVisit(n);
        out.print("}");
    }

    @Override
    public void visit(final AstMethod n) {
        out.print(n.methodId().name() + "{");
        defaultVisit(n);
        out.print("}");
    }

    @Override
    public void visit(final StmtBlock n) {
        out.print("{");
        defaultVisit(n);
        out.print("}");
    }
}
