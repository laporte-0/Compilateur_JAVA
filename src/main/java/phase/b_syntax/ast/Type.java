package phase.b_syntax.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import compil.util.AstLocations;
import compil.util.MJPrimitiveTypes;
import compil.util.MJType;
import compil.util.MJTypeClass;

/**
 * Identificateur de type.
 * <ul>
 * <li>{@link #name}
 * </ul>
 * 
 * <p>
 * N.B. : le nom de la classe {@code MJType} est préfixé avec « MJ » (pour
 * MiniJAVA) afin d'éviter les collisions avec l'interface
 * {@link java.lang.reflect.Type}.
 * </p>
 * 
 * @author Pascal Hennequin
 * @author Denis Conan
 * 
 * @param label     le label du nœud.
 * @param enfants   les enfants du nœud.
 * @param locations les positions de début et de fin du nœud dans le fichier
 *                  source.
 * @param type      le type.
 */
public record Type(String label, List<AstNode> enfants, AstLocations locations, MJType type) implements AstNode {

	@Override
	public String toString() {
		return print() + " " + this.type.name();
	}

	@Override
	public void accept(final AstVisitor v) {
		v.visit(this);
	}

	/**
	 * construit un enregistrement avec des valeurs par défaut pour les premiers
	 * attributs (label, etc.). Cette méthode est utilisée dans CUP.
	 * 
	 * @param type le type.
	 * @return le nouveau nœud.
	 */
	public static Type build(final MJType type) {
		return new Type(Type.class.getSimpleName(), new ArrayList<>(), new AstLocations(), type);
	}

	/**
	 * construit un enregistrement avec des valeurs par défaut pour les premiers
	 * attributs (label, etc.). Cette méthode est utilisée dans CUP.
	 * 
	 * @param typeName le nom du type.
	 * @return le nouveau nœud.
	 */
	public static Type build(final String typeName) {
		MJType t = null;
		Optional<MJPrimitiveTypes> et = Arrays.asList(MJPrimitiveTypes.values()).stream()
				.filter(e -> e.name().equals(typeName)).findFirst();
		if (et.isPresent()) {
			t = et.get();
		} else {
			t = new MJTypeClass(typeName);
		}
		return new Type(Type.class.getSimpleName(), new ArrayList<>(), new AstLocations(), t);
	}
}
