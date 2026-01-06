package phase.d_intermediate.ir;

import compil.util.MJType;

/**
 * Énumération des faux types qui sont utilisés pour mettre des informations
 * utiles pour la représentation intermédiaire : par exemple, dans des instances
 * de {@link IRVariable}, pour dire p.ex. « c'est une constante », ou « c'est un
 * label », ou encore « c'est une variable temporaire ».
 * 
 * @author Denis Conan
 */
public enum IRFakeEnumType implements MJType {
	/**
	 * Le type dans {@link IRConst}.
	 */
	IR_CONST("IRConst"),
	/**
	 * Le type dans {@link IRLabel}.
	 */
	IR_LABEL("IRLabel"),
	/**
	 * Le type dans {@link IRTempVar}.
	 */
	IR_TEMPVAR("IRTemp");

	/**
	 * Le nom de type.
	 */
	private final String name;

	/**
	 * Constructeur privé pour nommage des types.
	 * 
	 * @param name le nom JAVA du type.
	 */
	IRFakeEnumType(final String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}
}
