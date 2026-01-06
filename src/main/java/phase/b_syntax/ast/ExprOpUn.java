package phase.b_syntax.ast;

import java.util.Arrays;
import java.util.List;

import compil.util.AstLocations;
import compil.util.MJOperators;

/**
 * Opérateur Unaire.
 * <ul>
 * <li>{@link #op}
 * <li>{@link #expr}
 * </ul>
 * 
 * @author Pascal Hennequin
 * @author Denis Conan
 * 
 * @param label     le label du nœud.
 * @param enfants   les enfants du nœud.
 * @param locations les positions de début et de fin du nœud dans le fichier
 *                  source.
 * @param op        l'opérateur.
 * @param expr      l'opérande.
 */
public record ExprOpUn(String label, List<AstNode> enfants, AstLocations locations, MJOperators op, Expr expr)
        implements Expr {
    @Override
    public String toString() {
        return print() + " " + this.op.name();
    }

    @Override
    public void accept(final AstVisitor v) {
        v.visit(this);
    }

    /**
     * construit un enregistrement avec des valeurs par défaut pour les premiers
     * attributs (label, etc.). Cette méthode est utilisée dans CUP.
     * 
     * @param op   l'opérateur.
     * @param expr l'opérande.
     * @return le nouveau nœud.
     */
    public static ExprOpUn build(final MJOperators op, final Expr expr) {
        return new ExprOpUn(ExprOpUn.class.getSimpleName(), Arrays.asList(expr), new AstLocations(), op, expr);
    }
}
