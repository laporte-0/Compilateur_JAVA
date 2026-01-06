package compil.util;

/**
 * Interface pour les types.
 * 
 * <p>
 * N.B. : le nom de la classe est préfixé avec « MJ » (pour MiniJAVA) afin
 * d'éviter les collisions avec l'interface {@link java.lang.reflect.Type}.
 * </p>
 * 
 * @author Denis Conan
 */
public interface MJType {
	/**
	 * le nom du type.
	 * 
	 * @return le nom du type.
	 */
	String name();
}
