package compil.util;

import java.util.Objects;

/**
 * Classe pour les types classes.
 * 
 * @author Denis Conan
 */
public class MJTypeClass implements MJType {
	/**
	 * le nom de la classe, qui est le nom du type.
	 */
	private String name;
	
	/**
	 * construit un type classe.
	 * 
	 * @param name le nom de la classe, qui devient le nom du type.
	 */
	public MJTypeClass(final String name) {
		this.name = name;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof MJTypeClass)) {
			return false;
		}
		MJTypeClass other = (MJTypeClass) obj;
		return Objects.equals(name, other.name);
	}

	@Override
	public String toString() {
		return this.name;
	}
}
