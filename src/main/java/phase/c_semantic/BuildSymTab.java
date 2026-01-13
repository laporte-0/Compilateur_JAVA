package phase.c_semantic;

import compil.util.Debug;
import compil.util.MJPrimitiveTypes;
import compil.util.MJTypeClass;
import phase.b_syntax.ast.AstMethod;
import phase.b_syntax.ast.AstNode;
import phase.b_syntax.ast.AstVisitorDefault;
import phase.b_syntax.ast.Formal;
import phase.b_syntax.ast.Klass;
import phase.b_syntax.ast.KlassMain;
import phase.b_syntax.ast.StmtBlock;
import phase.b_syntax.ast.Variable;
import phase.c_semantic.symtab.Info;
import phase.c_semantic.symtab.InfoKlass;
import phase.c_semantic.symtab.InfoMethod;
import phase.c_semantic.symtab.InfoVar;
import phase.c_semantic.symtab.Scope;

/**
 * Construction de la table des symboles.
 * 
 * @author Pascal Hennequin
 * @author Denis Conan
 */
public class BuildSymTab extends AstVisitorDefault {
	/**
	 * Le nom de la classe « Object ».
	 */
	protected static final String OBJECT = "Object";
	/**
	 * La structure de données de l'analyse sémantique.
	 */
	protected final SemanticTree semanticTree;
	/**
	 * L'attribut hérité "Scope". Entrée dans la table des symboles du nœud
	 */
	protected Scope currentScope;
	/**
	 * L'attribut hérité "Klass". Fournit le type de la variable {@code this}.
	 */
	protected InfoKlass currentKlass;
	/**
	 * {@code true} si des erreurs sémantiques détectées.
	 */
	protected boolean error;

	/**
	 * Construit la génération de la table des symboles (passe 1).
	 * 
	 * @param semanticTree l'arbre sémantique.
	 */
	public BuildSymTab(final SemanticTree semanticTree) {
		this.error = false;
		this.semanticTree = semanticTree;
		this.currentScope = semanticTree.rootScope();
		this.currentKlass = null;
	}

	/**
	 * Réalise la génération de la table des symboles (passe 1). Intègre la
	 * déclaration implicite de la classe {@code Object}, et les déclarations
	 * implicites de la variable {@code this} dans les méthodes.
	 * 
	 * @return {@code true} si des erreurs signalées de redéfinition.
	 */
	public boolean execute() {
		addObjectKlass();
		semanticTree.axiom().accept(this);
		if (Debug.SYMTAB) {
			Debug.log("= Table des symboles (passe1)");
			Debug.log(semanticTree.rootScope().toPrint());
		}
		return this.error;
	}

	/**
	 * Crée la classe {@code Object} dans la table des symboles. La classe est
	 * requise comme racine de la hiérarchie des classes. La méthode
	 * {@code Object.equals()} est ajoutée pour l'exemple.
	 */
	protected void addObjectKlass() {
		Scope sc = semanticTree.rootScope();
		final InfoKlass kl = new InfoKlass(OBJECT, null);
		sc = newKlassScope(sc, kl, null);
		final InfoMethod m = new InfoMethod(MJPrimitiveTypes.BOOL, "equals",
			new InfoVar("this", new MJTypeClass("Object")), new InfoVar("o", new MJTypeClass("Object")));
		newMethodScope(sc, m, null);
	}

	// Helpers
	/**
	 * Positionne l'attribut "Klass".
	 * 
	 * @param n     le nœud AST.
	 * @param klass la classe englobante.
	 */
	protected void setKlass(final AstNode n, final InfoKlass klass) {
		semanticTree.attrKlass().set(n, klass);
	}

	/**
	 * Retourne l'attribut "Klass".
	 * 
	 * @param n le nœud AST.
	 * @return la classe englobante.
	 */
	protected InfoKlass getKlass(final AstNode n) {
		return semanticTree.attrKlass().get(n);
	}

	/**
	 * Positionne l'attribut "Scope".
	 * 
	 * @param n  le nœud AST.
	 * @param sc la portée.
	 */
	protected void setScope(final AstNode n, final Scope sc) {
		semanticTree.attrScope().set(n, sc);
	}

	/**
	 * Retourne l'attribut "Scope".
	 * 
	 * @param n le nœud AST.
	 * @return la portée.
	 */
	protected Scope getScope(final AstNode n) {
		return semanticTree.attrScope().get(n);
	}

	/**
	 * Ajoute une déclaration de classe et crée une portée de classe.
	 * 
	 * @param sc la portée courante.
	 * @param kl la définition de classe.
	 * @return la portée pour la nouvelle classe.
	 */
	protected Scope newKlassScope(final Scope sc, final InfoKlass kl, final AstNode where) {
		if (checkRedef(sc.insertKlass(kl))) {
			if (where != null) {
				duplicateError(where, kl.getName());
			}
		}
		final Scope enfants = new Scope(sc, kl.getName());
		kl.setScope(enfants);
		return enfants;
	}

	/**
	 * Ajoute une déclaration de méthode et crée 2 nouvelles portées. Ajoute aussi
	 * les paramètres formels dans la portée intermédiaire
	 * 
	 * @param sc la portée courante.
	 * @param m  la définition de la méthode.
	 * @return la portée pour la nouvelle méthode.
	 */
	protected Scope newMethodScope(final Scope sc, final InfoMethod m, final AstNode where) {
		if (checkRedef(sc.insertMethod(m))) {
			if (where != null) {
				duplicateError(where, m.getName());
			}
		}
		final Scope enfants = new Scope(sc, m.getName() + "_args");
		for (InfoVar v : m.getArgs()) {
			if (checkRedef(enfants.insertVariable(v))) {
				// no AST node available for formals here
				Debug.logErr("BuildSymtab : Duplication d'identificateur " + v);
				this.error = true;
			}
		}
		final Scope pf = new Scope(enfants, m.getName());
		m.setScope(pf);
		return pf;
	}

	/**
	 * Teste les redéfinitions de symboles dans une même portée.
	 * <p>
	 * Redéfinition = retour non {@code null} lors de l'ajout d'un symbole.
	 * <p>
	 * Erreur signalée avec traitement différé.
	 * 
	 * @param info la déclaration retournée par un {@code HashMap.add()}.
	 * @return {@code true} si la déclaration est écrasée.
	 */
	protected boolean checkRedef(final Info info) {
		if (info == null) {
			return false;
		}
		Debug.logErr("BuildSymtab : Duplication d'identificateur " + info);
		error = true;
		return true;
	}

	////////////// Visit ////////////////////////

	/**
	 * Signale une erreur de redéfinition avec message et localisation dans
	 * l'AST. Erreur signalée avec traitement par exception retardé en fin
	 * d'analyse.
	 *
	 * @param where le nœud de l'AST en faute.
	 * @param name  l'identificateur redéfini.
	 */
	protected void duplicateError(final AstNode where, final String name) {
		if (where == null) {
			Debug.logErr("BuildSymtab : Identifier already defined " + name);
		} else {
			Debug.logErr(where + " Identifier already defined " + name);
		}
		this.error = true;
	}
	/**
	 * Redéfinition de la visite par défaut : Intégration de l'héritage par défaut
	 * des attributs hérités (Scope, Klass).
	 */
	@Override
	public void defaultVisit(final AstNode n) {
		setKlass(n, currentKlass);
		setScope(n, currentScope);
		for (AstNode f : n) {
			f.accept(this);
		}
		currentKlass = getKlass(n);
		currentScope = getScope(n);
	}

	// Visites Spécifiques :
	// - Création de portées : KlassMain, Klass, Method, StmtBlock
	// - Déclarations : KlassMain, Klass, Method, Variable, Formal (in Method)

	/**
	 * Déclaration de la classe avec la méthode main de Minijava. Pour l'exemple,
	 * mais inutile en pratique car on ne fera rien avec dans la suite.
	 */
	@Override
	public void visit(final KlassMain n) {
		setKlass(n, currentKlass);
		setScope(n, currentScope);
		currentKlass = new InfoKlass(n.klassId().name(), OBJECT);
		currentScope = newKlassScope(currentScope, currentKlass, n);
		n.klassId().accept(this);
		n.argId().accept(this);
		final InfoMethod m = new InfoMethod(MJPrimitiveTypes.VOID, "main",
				new InfoVar(n.argId().name(), MJPrimitiveTypes.STRING_ARRAY));
		currentScope = newMethodScope(currentScope, m, n);
		n.stmt().accept(this);
		currentKlass = getKlass(n);
		currentScope = getScope(n);
	}

	@Override
	public void visit(final Klass n) {
		setKlass(n, currentKlass);
		setScope(n, currentScope);
		currentKlass = new InfoKlass(n.klassId().name(), n.parentId().name());
		this.currentScope = newKlassScope(currentScope, currentKlass, n);
		n.klassId().accept(this);
		n.parentId().accept(this);
		n.vars().accept(this);
		n.methods().accept(this);
		currentKlass = getKlass(n);
		currentScope = getScope(n);

	}

	@Override
	public void visit(final AstMethod n) {
		setKlass(n, currentKlass);
		setScope(n, currentScope);
		// les paramètres formels explicites
		final InfoVar[] formals = new InfoVar[n.fargs().nbChildren() + 1];
		int index = 1;
		for (AstNode f : n.fargs()) {
			formals[index] = new InfoVar(((Formal) f).varId().name(), ((Formal) f).type().type());
			index++;
		}
		// le paramètre implicite "this"
		formals[0] = new InfoVar("this", new MJTypeClass(getKlass(n).getName()));
		// la définition de la méthode
		final InfoMethod m = new InfoMethod(n.returnType().type(), n.methodId().name(), formals);
		currentScope = newMethodScope(currentScope, m, n);
		n.returnType().accept(this);
		n.methodId().accept(this);
		n.fargs().accept(this); // ne fait rien !
		n.vars().accept(this);
		n.stmts().accept(this);
		n.returnExp().accept(this);
		currentKlass = getKlass(n);
		currentScope = getScope(n);
	}

	@Override
	public void visit(final Variable n) {
		setKlass(n, currentKlass);
		setScope(n, currentScope);
		final InfoVar v = new InfoVar(n.varId().name(), n.typeId().type());
		if (checkRedef(getScope(n).insertVariable(v))) {
			duplicateError(n, v.name());
		}
		currentKlass = getKlass(n);
		currentScope = getScope(n);
	}

	@Override
	public void visit(final StmtBlock n) {
		setKlass(n, currentKlass);
		setScope(n, currentScope);
		currentScope = new Scope(currentScope);
		n.vars().accept(this);
		n.stmts().accept(this);
		currentKlass = getKlass(n);
		currentScope = getScope(n);
	}
}
