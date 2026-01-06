package phase.d_intermediate.ir;

import compil.util.MJOperators;

/**
 * Copie : {@code result = arg1}.
 * 
 * @author Pascal Hennequin
 * @author Denis Conan
 * 
 * @param arg1   la source.
 * @param result la destination.
 */
public record QCopy(IRVariable arg1, IRVariable result) implements IRQuadruple {
	@Override
	public MJOperators op() {
		return null;
	}

	@Override
	public IRVariable arg2() {
		return null;
	}

	@Override
	public void accept(final IRvisitor v) {
		v.visit(this);
	}

	@Override
	public String toString() {
		return format(this.result.name() + " = " + this.arg1.name());
	}
}
