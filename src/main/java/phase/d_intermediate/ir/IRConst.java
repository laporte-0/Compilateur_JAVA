package phase.d_intermediate.ir;

import compil.util.MJType;

/**
 * Variable IR : Constante.
 * 
 * @author Pascal Hennequin
 * @author Denis Conan
 * 
 * @param value La valeur de la constante.
 */
public record IRConst(Integer value) implements IRVariable {

	@Override
	public String name() {
		return value().toString();
	}

	@Override
	public MJType type() {
		return IRFakeEnumType.IR_CONST;
	}

	@Override
	public String toString() {
		return "c_" + name();
	}
}
