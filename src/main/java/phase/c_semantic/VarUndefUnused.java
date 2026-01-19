package phase.c_semantic;

import java.util.Collection;

import compil.util.Debug;
import compil.util.MJType;
import phase.b_syntax.ast.AstMethod;
import phase.b_syntax.ast.AstNode;
import phase.b_syntax.ast.AstVisitorDefault;
import phase.b_syntax.ast.ExprCall;
import phase.b_syntax.ast.ExprIdent;
import phase.b_syntax.ast.StmtArrayAssign;
import phase.b_syntax.ast.StmtAssign;
import phase.c_semantic.symtab.InfoKlass;
import phase.c_semantic.symtab.InfoMethod;
import phase.c_semantic.symtab.InfoVar;
import phase.c_semantic.symtab.Scope;

/**
 * Vérification de l'utilisation des identificateurs. <br>
 * Identificateurs non définis (classe, méthode, variable). <br>
 * Variables non utilisées.
 * 
 * @author Pascal Hennequin
 * @author Denis Conan
 */
public class VarUndefUnused extends AstVisitorDefault {
	/**
	 * La structure de données de l'analyse sémantique.
	 */
	protected final SemanticTree semanticTree;
	/**
	 * La liste des variables non utilisées. Construction par élimination des
	 * variables utilisées.
	 */
	protected Collection<InfoVar> unused;
	/**
	 * {@code true} si des erreurs sémantiques d'identificateurs non définis.
	 */
	protected boolean error;

	/**
	 * Construit la vérification des identificateurs.
	 * 
	 * @param semanticTree l'arbre sémantique.
	 */
	public VarUndefUnused(final SemanticTree semanticTree) {
		this.error = false;
		this.semanticTree = semanticTree;
		this.unused = semanticTree.rootScope().getAllVariables();
	}

	/**
	 * Réalise la validation des identificateurs.
	 * 
	 * @return {@code true} si des erreurs sémantiques de définition.
	 */
	public boolean execute() {
		Debug.log("= Contrôle de définitions : identificateurs non définis");
		semanticTree.axiom().accept(this);
		// Ne pas signaler 'this' comme inutilisé
		this.unused.removeIf(v -> "this".equals(v.name()));
		if (Debug.UNUSED) {
			Debug.log("= Contrôle de définitions : variables inutilisées");
			Debug.log(unused);
		}
		return this.error;
	}

	// Helpers
	/**
	 * Retourne l'attribut "Scope".
	 * 
	 * @param n le nœud de l'AST.
	 * @return la valeur de la portée du nœud.
	 */
	protected Scope getScope(final AstNode n) {
		return semanticTree.attrScope().get(n);
	}

	/**
	 * Retourne l'attribut "Type".
	 * 
	 * @param n le nœud de l'AST.
	 * @return la valeur de Type du nœud.
	 */
	protected MJType getType(final AstNode n) {
		return semanticTree.attrType().get(n);
	}

	/**
	 * Recherche une classe dans la table des symboles.
	 * 
	 * @param name le nom de la classe.
	 * @return la définition de la classe ou {@code null} si inconnue.
	 */
	protected InfoKlass lookupKlass(final String name) {
		return semanticTree.rootScope().lookupKlass(name);
	}

	/**
	 * Signale une erreur avec message et localisation dans l'AST. Erreur signalée
	 * avec traitement par exception retardé en fin d'analyse.
	 * 
	 * @param where le nœud de l'AST en faute.
	 * @param name  l'identificateur inconnu.
	 */
	protected void undefError(final AstNode where, final String name) {
		Debug.logErr(where + " Use of undefined identifier " + name);
		error = true;
	}

	// ///////////////// Visit ////////////////////
	@Override
	public void visit(final ExprIdent n) {
		final String name = n.varId().name();
		// 'this' is special: do not report as undefined and do not mark as unused
		if ("this".equals(name)) {
			final InfoVar v = getScope(n).lookupVariable("this");
			if (v != null) {
				this.unused.remove(v);
			}
			return;
		}
		final InfoVar v = getScope(n).lookupVariable(name);
		if (v == null) {
			undefError(n, name);
		} else {
			this.unused.remove(v);
		}
	}

	@Override
	public void visit(final StmtAssign n) {
		final String name = n.varId().name();
		final InfoVar v = getScope(n).lookupVariable(name);
		if (v == null) {
			undefError(n, name);
		} else {
			this.unused.remove(v);
		}
		// visiter la valeur
		n.value().accept(this);
	}

	@Override
	public void visit(final StmtArrayAssign n) {
		final String name = n.arrayId().name();
		final InfoVar v = getScope(n).lookupVariable(name);
		if (v == null) {
			undefError(n, name);
		} else {
			this.unused.remove(v);
		}
		// visiter index et valeur
		n.index().accept(this);
		n.value().accept(this);
	}

	@Override
	public void visit(final ExprCall n) {
		// On visite d'abord le receveur et les args pour détecter les usages de variables
		n.expForReceiver().accept(this);
		for (AstNode arg : n.args()) {
			arg.accept(this);
		}
		// Puis, recherche de la méthode (liaison statique) en fonction du Type du receveur
		final MJType recvType = getType(n.expForReceiver());
		if (recvType == null) {
			return;
		}
		final InfoKlass kl = lookupKlass(recvType.name());
		if (kl == null) {
			// La validité du type est normalement vérifiée au contrôle de type,
			// mais on garde une vérification défensive ici.
			undefError(n, recvType.name());
			return;
		}
		final InfoMethod m = kl.getScope().lookupMethod(n.methodId().name());
		if (m == null) {
			undefError(n.methodId(), n.methodId().name());
		}
	}

	@Override
	public void visit(final AstMethod n) {
		// Les appels de méthodes sont vérifiés au niveau de ExprCall (liaison statique).
		// Retirer 'this' de la liste des variables inutilisées pour cette méthode
		final Scope sc = getScope(n);
		if (sc != null) {
			final InfoVar thisVar = sc.lookupVariable("this");
			if (thisVar != null) {
				this.unused.remove(thisVar);
			}
		}
		// visiter le corps pour détecter les usages de variables
		n.fargs().accept(this);
		n.vars().accept(this);
		n.stmts().accept(this);
		if (n.returnExp() != null) {
			n.returnExp().accept(this);
		}
	}
}
