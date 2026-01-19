package phase.c_semantic;

import compil.util.Debug;
import compil.util.MJOperators;
import compil.util.MJPrimitiveTypes;
import compil.util.MJType;
import compil.util.MJTypeClass;
import phase.b_syntax.ast.AstNode;
import phase.b_syntax.ast.AstVisitorDefault;
import phase.b_syntax.ast.AstMethod;
import phase.b_syntax.ast.ExprArrayLength;
import phase.b_syntax.ast.ExprArrayLookup;
import phase.b_syntax.ast.ExprArrayNew;
import phase.b_syntax.ast.ExprCall;
import phase.b_syntax.ast.ExprIdent;
import phase.b_syntax.ast.ExprLiteralBool;
import phase.b_syntax.ast.ExprLiteralInt;
import phase.b_syntax.ast.ExprNew;
import phase.b_syntax.ast.ExprOpBin;
import phase.b_syntax.ast.ExprOpUn;
import phase.b_syntax.ast.StmtArrayAssign;
import phase.b_syntax.ast.StmtAssign;
import phase.b_syntax.ast.StmtIf;
import phase.b_syntax.ast.StmtPrint;
import phase.b_syntax.ast.StmtWhile;
import phase.b_syntax.ast.Type;
import phase.c_semantic.symtab.InfoKlass;
import phase.c_semantic.symtab.InfoMethod;
import phase.c_semantic.symtab.InfoVar;
import phase.c_semantic.symtab.Scope;

/**
 * Contrôle de Type.
 * <ul>
 * <li>Calcule l'attribut synthétisé "Type" pour les nœuds Expressions,
 * <li>Vérifie les contraintes de typage de Minijava.
 * </ul>
 * 
 * @author Pascal Hennequin
 * @author Denis Conan
 */
public class TypeChecking extends AstVisitorDefault {
	/**
	 * La structure de données de l'analyse sémantique.
	 */
	protected final SemanticTree semanticTree;
	/**
	 * {@code true} si des erreurs de typage détectées.
	 */
	protected boolean error;

	/**
	 * Construit le contrôle de type.
	 * 
	 * @param semanticTree l'arbre sémantique.
	 */
	public TypeChecking(final SemanticTree semanticTree) {
		this.error = false;
		this.semanticTree = semanticTree;
	}

	/**
	 * Réalise le contrôle de type.
	 * 
	 * @return {@code true} si des erreurs sémantiques de typage.
	 */
	public boolean execute() {
		Debug.log("= Contrôle de typage");
		semanticTree.axiom().accept(this);
		return error;
	}

	// Helpers
	/**
	 * Retourne l'attribut Type.
	 * 
	 * @param n le nœud de l'AST.
	 * @return le type.
	 */
	protected MJType getType(final AstNode n) {
		return semanticTree.attrType().get(n);
	}

	/**
	 * Positionne l'attribut Type.
	 * 
	 * @param n    le nœud de l'AST.
	 * @param type le type.
	 */
	protected void setType(final AstNode n, final MJType type) {
		semanticTree.attrType().set(n, type);
	}

	/**
	 * Retourne l'attribut Scope.
	 * 
	 * @param n le nœud de l'AST.
	 * @return la portée courante du nœud.
	 */
	protected Scope getScope(final AstNode n) {
		return semanticTree.attrScope().get(n);
	}

	/**
	 * Recherche une classe dans la table des symboles.
	 * 
	 * @param type le type, qui correspond à la classe recherchée.
	 * @return la définition de la classe ou {@code null} si inconnue.
	 */
	protected InfoKlass lookupKlass(final MJType type) {
		return semanticTree.rootScope().lookupKlass(type.name());
	}

	/**
	 * Signale une erreur avec message et localisation dans l'AST. Erreur signalée
	 * avec traitement par exception retardé en fin d'analyse
	 * 
	 * @param where le nœud de l'AST en faute.
	 * @param msg   le message d'erreur.
	 */
	protected void erreur(final AstNode where, final String msg) {
		compil.util.Debug.logErr(where + " " + msg);
		error = true;
	}

	/**
	 * Teste le transtypage implicite entre 2 types.
	 * 
	 * @param t1 le type 1.
	 * @param t2 le type 2.
	 * @return {@code true} si t2 est sous-type de t1.
	 */
	protected boolean compareType(final MJType t1, final MJType t2) {
		if (t2 == null) {
			return false;
		}
		if (t2.equals(t1)) {
			return true;
		}
		// sinon (t1 ancêtre de t2) ? (transtypage implicite vers le haut)
		final InfoKlass kl2 = lookupKlass(t2);
		if (kl2 != null) {
			return compareType(t1, new MJTypeClass(kl2.getParent()));
		}
		return false;
	}

	/**
	 * Valide une condition de typage et signale l'erreur éventuelle.
	 * 
	 * @param t1    le type attendu.
	 * @param t2    le type testé.
	 * @param msg   le message d'erreur si t2 ne cast pas en t1.
	 * @param where le nœud de l'AST en faute.
	 */
	protected void checkType(final MJType t1, final MJType t2, final String msg, final AstNode where) {
		if (!compareType(t1, t2)) {
			erreur(where, "Wrong Type : " + t2 + "->" + t1 + ";  " + msg);
		}
	}

	/**
	 * Valide un nom de type et signale l'erreur éventuelle.
	 * 
	 * @param type  le type testé.
	 * @param where le nœud de l'AST en faute.
	 */
	protected void checkTypeName(final MJType type, final AstNode where) {
		if (type.equals(MJPrimitiveTypes.BOOL) || type.equals(MJPrimitiveTypes.INT)) {
			return;
		}
		if (type.equals(MJPrimitiveTypes.BOOL_ARRAY) || type.equals(MJPrimitiveTypes.INT_ARRAY)) {
			return;
		}
		if (type.equals(MJPrimitiveTypes.VOID)) {
			return;
		}
		if (lookupKlass(type) != null) {
			return;
		}
		erreur(where, "Unknown Type : " + type);
	}

	/**
	 * Recherche le type d'une variable dans la table des symboles.
	 * 
	 * @param n    le nœud de l'AST (pour obtenir la portée courante).
	 * @param name le nom de la variable.
	 * @return le type, {@code VOID} pour type inconnu.
	 */
	protected MJType lookupVarType(final AstNode n, final String name) {
		final InfoVar v = getScope(n).lookupVariable(name);
		if (v == null) {
			return MJPrimitiveTypes.VOID;
		}
		return v.type();
	}

	/////////////////// Visit ////////////////////
	// Visites spécifiques :
	// - Expr* : Positionner l'attribut Type
	// - Stmt* + Expr* (sauf exceptions) : Compatibilité des Types
	// - Type : Validité des noms de type dans Variable, Method, Formal
	// - Method : returnType compatible avec Type(returnExpr)

	@Override
	public void visit(final Type n) {
		// Valide les noms de types (primitifs, tableaux, classes).
		checkTypeName(n.type(), n);
		// pas d'attribut Type à poser ici (Type n'est pas une Expr).
	}

	@Override
	public void visit(final ExprLiteralInt n) {
		setType(n, MJPrimitiveTypes.INT);
	}

	@Override
	public void visit(final ExprLiteralBool n) {
		setType(n, MJPrimitiveTypes.BOOL);
	}

	@Override
	public void visit(final ExprIdent n) {
		final MJType t = lookupVarType(n, n.varId().name());
		setType(n, t);
	}

	@Override
	public void visit(final ExprNew n) {
		final MJType t = new MJTypeClass(n.klassId().name());
		checkTypeName(t, n);
		setType(n, t);
	}

	@Override
	public void visit(final ExprArrayNew n) {
		// visit size expression first
		defaultVisit(n);
		checkTypeName(n.type(), n);
		checkType(MJPrimitiveTypes.INT, getType(n.expForSize()), "Array size must be int", n);
		setType(n, n.type());
	}

	@Override
	public void visit(final ExprArrayLength n) {
		defaultVisit(n);
		final MJType t = getType(n.expForArray());
		if (!(MJPrimitiveTypes.INT_ARRAY.equals(t) || MJPrimitiveTypes.BOOL_ARRAY.equals(t))) {
			erreur(n, "Array length applied to non-array expression");
		}
		setType(n, MJPrimitiveTypes.INT);
	}

	@Override
	public void visit(final ExprArrayLookup n) {
		defaultVisit(n);
		checkType(MJPrimitiveTypes.INT, getType(n.expForIndex()), "Array index must be int", n);
		final MJType tArr = getType(n.expForArray());
		if (MJPrimitiveTypes.INT_ARRAY.equals(tArr)) {
			setType(n, MJPrimitiveTypes.INT);
			return;
		}
		if (MJPrimitiveTypes.BOOL_ARRAY.equals(tArr)) {
			setType(n, MJPrimitiveTypes.BOOL);
			return;
		}
		erreur(n, "Array lookup applied to non-array expression");
		setType(n, MJPrimitiveTypes.VOID);
	}

	@Override
	public void visit(final ExprOpUn n) {
		defaultVisit(n);
		if (n.op() == MJOperators.NOT) {
			checkType(MJPrimitiveTypes.BOOL, getType(n.expr()), "Unary ! expects boolean", n);
			setType(n, MJPrimitiveTypes.BOOL);
			return;
		}
		erreur(n, "Unknown unary operator " + n.op());
		setType(n, MJPrimitiveTypes.VOID);
	}

	@Override
	public void visit(final ExprOpBin n) {
		defaultVisit(n);
		final MJType t1 = getType(n.expr1());
		final MJType t2 = getType(n.expr2());
		switch (n.op()) {
		case PLUS, MINUS, TIMES -> {
			checkType(MJPrimitiveTypes.INT, t1, "Arithmetic operator expects int", n);
			checkType(MJPrimitiveTypes.INT, t2, "Arithmetic operator expects int", n);
			setType(n, MJPrimitiveTypes.INT);
		}
		case AND -> {
			checkType(MJPrimitiveTypes.BOOL, t1, "Logical && expects boolean", n);
			checkType(MJPrimitiveTypes.BOOL, t2, "Logical && expects boolean", n);
			setType(n, MJPrimitiveTypes.BOOL);
		}
		case LESS -> {
			checkType(MJPrimitiveTypes.INT, t1, "Comparison < expects int", n);
			checkType(MJPrimitiveTypes.INT, t2, "Comparison < expects int", n);
			setType(n, MJPrimitiveTypes.BOOL);
		}
		default -> {
			erreur(n, "Unknown binary operator " + n.op());
			setType(n, MJPrimitiveTypes.VOID);
		}
		}
	}

	@Override
	public void visit(final ExprCall n) {
		// Pseudo-code style: visit receiver, resolve method from receiver *type*
		n.expForReceiver().accept(this);
		final MJType recvType = getType(n.expForReceiver());
		if (!(recvType instanceof MJTypeClass)) {
			erreur(n, "Call on non-object receiver of type " + recvType);
			setType(n, MJPrimitiveTypes.VOID);
			return;
		}
		final InfoKlass kl = lookupKlass(recvType);
		if (kl == null) {
			erreur(n, "Call on unknown class " + recvType);
			setType(n, MJPrimitiveTypes.VOID);
			return;
		}
		n.methodId().accept(this);
		final InfoMethod m = kl.getScope().lookupMethod(n.methodId().name());
		if (m == null) {
			erreur(n, "Unknown method " + n.methodId().name() + " for receiver type " + recvType);
			setType(n, MJPrimitiveTypes.VOID);
			return;
		}
		// attention : this est en arg0
		if (m.getArgs().length != n.args().nbChildren() + 1) {
			erreur(n, "Call of method " + m + " does not match the number of args");
			setType(n, MJPrimitiveTypes.VOID);
			return;
		}
		int i = 1;
		for (AstNode arg : n.args()) {
			arg.accept(this);
			checkType(m.getArgs()[i++].type(), getType(arg), "Call of method does not match the signature :" + m, n);
		}
		setType(n, m.getReturnType());
	}

	@Override
	public void visit(final StmtAssign n) {
		// visit RHS first to compute its type
		n.value().accept(this);
		final MJType varType = lookupVarType(n, n.varId().name());
		checkType(varType, getType(n.value()), "Assignment type mismatch", n);
	}

	@Override
	public void visit(final StmtArrayAssign n) {
		// visit index/value first
		n.index().accept(this);
		n.value().accept(this);
		checkType(MJPrimitiveTypes.INT, getType(n.index()), "Array index must be int", n);
		final MJType arrayType = lookupVarType(n, n.arrayId().name());
		final MJType elementType;
		if (MJPrimitiveTypes.INT_ARRAY.equals(arrayType)) {
			elementType = MJPrimitiveTypes.INT;
		} else if (MJPrimitiveTypes.BOOL_ARRAY.equals(arrayType)) {
			elementType = MJPrimitiveTypes.BOOL;
		} else {
			erreur(n, "Array assignment on non-array variable of type " + arrayType);
			elementType = MJPrimitiveTypes.VOID;
		}
		checkType(elementType, getType(n.value()), "Array element assignment type mismatch", n);
	}

	@Override
	public void visit(final StmtIf n) {
		defaultVisit(n);
		checkType(MJPrimitiveTypes.BOOL, getType(n.test()), "If condition must be boolean", n);
	}

	@Override
	public void visit(final StmtWhile n) {
		defaultVisit(n);
		checkType(MJPrimitiveTypes.BOOL, getType(n.test()), "While condition must be boolean", n);
	}

	@Override
	public void visit(final StmtPrint n) {
		defaultVisit(n);
		checkType(MJPrimitiveTypes.INT, getType(n.expr()), "println expects int", n);
	}

	@Override
	public void visit(final AstMethod n) {
		// Visit children to compute return expression type and validate nested types.
		defaultVisit(n);
		final MJType declaredReturn = n.returnType().type();
		final MJType actualReturn = getType(n.returnExp());
		checkType(declaredReturn, actualReturn, "Return expression type mismatch", n);
	}
}
