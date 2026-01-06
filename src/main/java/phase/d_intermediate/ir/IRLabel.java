package phase.d_intermediate.ir;

import compil.util.MJType;

/**
 * Variable IR : Label.
 * 
 * @author Pascal Hennequin
 * @author Denis Conan
 * 
 * @param name Le nom du label.
 */
public record IRLabel(String name) implements IRVariable {
	/**
	 * La numérotation des labels.
	 */
	private static int index;

	/**
	 * Construit un label avec nom unique auto-généré.
	 */
	public IRLabel() {
		this("L_" + index++);
	}

	@Override
	public MJType type() {
		return IRFakeEnumType.IR_LABEL;
	}

	@Override
	public String toString() {
		return this.name;
	}
}
