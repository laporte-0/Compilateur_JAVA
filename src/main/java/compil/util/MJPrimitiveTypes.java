package compil.util;

/**
 * Énumération des types primitifs avec nom textuel.
 * 
 * @author Pascal Hennequin
 */
public enum MJPrimitiveTypes implements MJType {
	/**
	 * Le type booléen.
	 */
	BOOL("boolean"),
	/**
	 * Le type entier.
	 */
	INT("int"),
	/**
	 * Le type tableau d'entiers.
	 */
	INT_ARRAY("int[]"),
	/**
	 * Le type tableau de booléens.
	 */
	BOOL_ARRAY("boolean[]"),
	/**
	 * Le type tableau de chaînes de caractères.
	 */
	STRING_ARRAY("String[]"),
	/**
	 * Le type vide (inutile).
	 */
	VOID("void"),
	/**
	 * Le type inconnu (inutile).
	 */
	UNDEF("undef");

	/**
	 * Le nom de type.
	 */
	private final String name;

	/**
	 * Constructeur privé pour nommage des types.
	 * 
	 * @param name le nom JAVA du type.
	 */
	MJPrimitiveTypes(final String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}
}
