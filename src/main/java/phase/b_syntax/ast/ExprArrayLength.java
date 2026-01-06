package phase.b_syntax.ast;

import java.util.Arrays;
import java.util.List;

import compil.util.AstLocations;

/**
 * Taille d'un tableau.
 * <ul>
 * <li>{@link #expForArray}
 * </ul>
 * 
 * @author Pascal Hennequin
 * @author Denis Conan
 * 
 * @param label     le label du nœud.
 * @param enfants   les enfants du nœud.
 * @param locations les positions de début et de fin du nœud dans le fichier
 *                  source.
 * @param expForArray     l'expression tableau.
 */
public record ExprArrayLength(String label, List<AstNode> enfants, AstLocations locations,
		Expr expForArray) implements Expr {
    @Override
    public void accept(final AstVisitor v) {
        v.visit(this);
    }

    @Override
    public String toString() {
        return print();
    }

    /**
     * construit un enregistrement avec des valeurs par défaut pour les premiers
     * attributs (label, etc.). Cette méthode est utilisée dans CUP.
     * 
     * @param expForArray l'expression tableau.
     * @return le nouveau nœud.
     */
    public static ExprArrayLength build(final Expr expForArray) {
        return new ExprArrayLength(ExprArrayLength.class.getSimpleName(), Arrays.asList(expForArray), new AstLocations(),
                expForArray);
    }
}
